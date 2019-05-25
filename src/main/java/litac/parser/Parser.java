package litac.parser;


import static litac.parser.tokens.TokenType.*;

import java.util.*;

import litac.Errors;
import litac.ast.Decl;
import litac.ast.Decl.*;
import litac.ast.Expr;
import litac.ast.Expr.*;
import litac.ast.Node;
import litac.ast.Stmt;
import litac.ast.Stmt.*;
import litac.checker.Attributes;
import litac.checker.GenericParam;
import litac.checker.Note;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.parser.tokens.NumberToken;
import litac.parser.tokens.Token;
import litac.parser.tokens.TokenType;


/**
 * A {@link Parser} for the LitaC programming language.
 * 
 * @author Tony
 *
 */
public class Parser {   
    private int anonStructId;
    private int anonUnionId;
    private int loopLevel;
    private int aggregateLevel;
    
    private final Scanner scanner;
    private final List<Token> tokens;
    private int current;
        
    private Token startToken;
    
    private final static Set<TokenType> GENERICS_AMBIGUITY = new HashSet<>();
    static {
        // ( ) ] { } : ; , . ? == != | ^ *
        GENERICS_AMBIGUITY.add(TokenType.LEFT_PAREN);   // (
        GENERICS_AMBIGUITY.add(TokenType.RIGHT_PAREN);  // )
       // GENERICS_AMBIGUITY.add(TokenType.LEFT_BRACKET); // [
        GENERICS_AMBIGUITY.add(TokenType.RIGHT_BRACKET);// ]
        GENERICS_AMBIGUITY.add(TokenType.LEFT_BRACE);   // }
        GENERICS_AMBIGUITY.add(TokenType.RIGHT_BRACE);  // }
        GENERICS_AMBIGUITY.add(TokenType.COLON);        // :
        GENERICS_AMBIGUITY.add(TokenType.SEMICOLON);    // ;
        GENERICS_AMBIGUITY.add(TokenType.COMMA);        // ,
        GENERICS_AMBIGUITY.add(TokenType.DOT);          // .
        GENERICS_AMBIGUITY.add(TokenType.QUESTION_MARK);// ?
        GENERICS_AMBIGUITY.add(TokenType.EQUALS_EQUALS);// ==
        GENERICS_AMBIGUITY.add(TokenType.NOT_EQUALS);   // !=
        GENERICS_AMBIGUITY.add(TokenType.OR);           // |
        //GENERICS_AMBIGUITY.add(TokenType.AND);        // &
        GENERICS_AMBIGUITY.add(TokenType.XOR);          // ^
        GENERICS_AMBIGUITY.add(TokenType.STAR);         // *
    }
    
    /**
     * @param scanner
     *            the scanner to be used with this parser.
     */
    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.tokens = scanner.getTokens();
        
        this.current = 0;    
    }



    /**
     * Parses a module
     * 
     * @return the {@link ModuleStmt}
     */
    public ModuleStmt parseModule() {
                
        List<ImportStmt> imports = new ArrayList<>();
        List<NoteStmt> moduleNotes = new ArrayList<>();
        List<Decl> declarations = new ArrayList<>();
        
        String moduleName = ""; // default to global module
        if(match(MODULE))  {
            moduleName = moduleDeclaration();
        }
        
        while(!isAtEnd()) {
            if(match(IMPORT)) {
                imports.add(importDeclaration());
            }
            else {
                List<NoteStmt> notes = notes();
                boolean isPublic = match(PUBLIC);
                
                if(match(VAR))            declarations.add(varDeclaration());
                else if(match(CONST))     declarations.add(constDeclaration());
                else if(match(FUNC))      declarations.add(funcDeclaration());
                else if(match(STRUCT))    declarations.add(structDeclaration());
                else if(match(UNION))     declarations.add(unionDeclaration());
                else if(match(ENUM))      declarations.add(enumDeclaration());
                else if(match(TYPEDEF))   declarations.add(typedefDeclaration());            
                else if(match(SEMICOLON)) {
                    if(notes != null) {
                        for(NoteStmt note : notes) {
                            moduleNotes.add(note);
                        }
                    }
                    continue;
                }
                else throw error(peek(), ErrorCode.UNEXPECTED_TOKEN);
                
                Attributes attrs = declarations.get(declarations.size() - 1).attributes; 
                attrs.isPublic = isPublic;
                attrs.isGlobal = true;
                attrs.notes = notes;
            }
        }
        
        return node(new ModuleStmt(moduleName, imports, moduleNotes, declarations));
    }
    
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                      Declaration parsing
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    private String moduleDeclaration() {
        source();
        
        Token moduleName = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        return moduleName.getText();
    }
    
    private ImportStmt importDeclaration() {
        source();
        
        String aliasName = null;
        String moduleName = null;
        
        Token library = consume(STRING, ErrorCode.MISSING_IDENTIFIER);
        if(match(AS)) {
            Token alias = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            aliasName = alias.getText();
        }
        
        // removes the string quotes
        String libTxt = library.getText();
        moduleName = libTxt.substring(1, libTxt.length() - 1);
        
        return node(new ImportStmt(moduleName, aliasName));
    }
        
    private VarDecl varDeclaration() {
        source();
        
        TypeInfo type = null;
        Expr expr = null;
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(match(COLON)) {
            type = type(false);
            
            // Equals is optional if the type is defined
            if(match(EQUALS)) {
                expr = expression();            
            }
        }
        // If no type is defined, we must infer the type and thus
        // require an assignement
        else {
            consume(EQUALS, ErrorCode.MISSING_EQUALS);
            expr = expression();
            
            type = null;
        }
        
        
                
        return node(new VarDecl(identifier.getText(), type, expr));
    }
    
    private ConstDecl constDeclaration() {
        source();
        
        TypeInfo type = null;
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(match(COLON)) {
            type = type(false);
        }
        
        consume(TokenType.EQUALS, ErrorCode.MISSING_EQUALS);
        
        Expr expr = constExpression();            
        return node(new ConstDecl(identifier.getText(), type, expr));
    }
    
    
    
    private FuncDecl funcDeclaration() {
        source();

        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        List<GenericParam> genericParams = Collections.emptyList();
        if(match(LESS_THAN)) {
            genericParams = genericParameters();
        }
        
        ParametersStmt parameters = parametersStmt();
        
        TypeInfo returnType = TypeInfo.VOID_TYPE;
        if(match(COLON)) {
            returnType = type(false);
        }
        
        TypeInfo type = new FuncTypeInfo(identifier.getText(), 
                                         returnType, 
                                         parameters.params, 
                                         parameters.isVararg,
                                         genericParams);
        
        Stmt body;
        if(match(SEMICOLON)) {
            body = node(new EmptyStmt());
        }
        else {        
            body = statement();
        }
        
        return node(new FuncDecl(identifier.getText(), type, parameters, body, returnType));
    }
    
    private StructDecl structDeclaration() {
        source();
        
        Token start = peek();
        int flags = 0;
        if(this.aggregateLevel > 0) {
            flags |= AggregateTypeInfo.IS_EMBEDDED;
        }
        
        String structName = null;
        if(check(IDENTIFIER)) {
            Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            structName = identifier.getText();
        }
        else {
            structName = String.format("<anonymous-struct-%d>", anonStructId++);
            flags |= AggregateTypeInfo.IS_ANONYMOUS;
        }

        List<GenericParam> genericParams = Collections.emptyList();
        if(match(LESS_THAN)) {
            genericParams = genericParameters();
        }
        
        List<FieldStmt> fields = new ArrayList<>();
        this.aggregateLevel++;
        
        if(!match(SEMICOLON)) {        
            consume(LEFT_BRACE, ErrorCode.MISSING_LEFT_BRACE);
            
            do {
                if(check(RIGHT_BRACE)) {
                    break;
                }
                
                FieldStmt field = fieldStatement();
                fields.add(field);
                
                eatSemicolon();
            }
            while(!isAtEnd());
            consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        }
        this.aggregateLevel--;
        
        List<FieldInfo> typeFields = Stmt.fromFieldStmt(start, fields);
        TypeInfo type = new StructTypeInfo(structName, genericParams, typeFields, flags);
        
        return node(new StructDecl(structName, type, fields));
    }
    
    private UnionDecl unionDeclaration() {
        source();
        
        Token start = peek();
        int flags = 0;
        if(this.aggregateLevel > 0) {
            flags |= AggregateTypeInfo.IS_EMBEDDED;
        }
        
        String unionName = null;
        if(check(IDENTIFIER)) {
            Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            unionName = identifier.getText();
        }
        else {
            unionName = String.format("<anonymous-union-%d>", anonUnionId++);
            flags |= AggregateTypeInfo.IS_ANONYMOUS;
        }

        List<GenericParam> genericParams = Collections.emptyList();
        if(match(LESS_THAN)) {
            genericParams = genericParameters();
        }
        
        List<FieldStmt> fields = new ArrayList<>();
        this.aggregateLevel++;
        
        if(!match(SEMICOLON)) {        
            consume(LEFT_BRACE, ErrorCode.MISSING_LEFT_BRACE);
            
            do {
                if(check(RIGHT_BRACE)) {
                    break;
                }
                
                FieldStmt field = fieldStatement();
                fields.add(field);
                
                eatSemicolon();
            }
            while(!isAtEnd());
            consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        }
        this.aggregateLevel--;
        
        List<FieldInfo> typeFields = Stmt.fromFieldStmt(start, fields);
        TypeInfo type = new UnionTypeInfo(unionName, genericParams, typeFields, flags);
        
        
        return node(new UnionDecl(unionName, type, fields));
    }
    
    private EnumDecl enumDeclaration() {
        source();

        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        String enumName = identifier.getText();
        
        consume(LEFT_BRACE, ErrorCode.MISSING_LEFT_BRACE);
        
        List<EnumFieldInfo> fields = new ArrayList<>();
        
        do {
            if(check(RIGHT_BRACE)) {
                break;
            }
            
            EnumFieldInfo field = enumFieldStatement();
            fields.add(field);
        }
        while(match(COMMA));
                
        TypeInfo type = new EnumTypeInfo(enumName, fields);
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return node(new EnumDecl(enumName, type, fields));
    }

    private TypedefDecl typedefDeclaration() {
        source();
        
        TypeInfo aliasedType = type(false);        
        match(AS);
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        String name = identifier.getText();
        
        return node(new TypedefDecl(name, aliasedType, name));
    }
    

    private List<NoteStmt> notes() {
        List<NoteStmt> notes = null;
        if(check(AT)) {
            notes = new ArrayList<>();
            
            while(match(AT)) {
                Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
                List<String> attributes = new ArrayList<>();
                if(match(LEFT_PAREN)) {                    
                    do {
                        if(match(STRING)) {                            
                            attributes.add(previous().getValue().toString());
                        }
                    }
                    while(match(COMMA));
                    
                    consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
                }     
                
                notes.add(node(new NoteStmt(new Note(identifier.getText(), attributes))));
            }
        }
        
        return notes;
    }
    
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                      Statement parsing
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    private Stmt statement() {    
        source();
        
        if(match(LEFT_BRACE))   return blockStatement();        
        if(match(VAR))          return varDeclaration();        
        if(match(CONST))        return constDeclaration();
        if(match(IF))           return ifStmt();
        if(match(WHILE))        return whileStmt();
        if(match(DO))           return doWhileStmt();
        if(match(FOR))          return forStmt();
        if(match(BREAK))        return breakStmt();
        if(match(CONTINUE))     return continueStmt();
        if(match(RETURN))       return returnStmt();
        if(match(DEFER))        return deferStmt();
                        
        return expression();
    }
    
    private BlockStmt blockStatement() {
        List<Stmt> stmts = new ArrayList<>();
        
        do {
            if(check(RIGHT_BRACE)) {
                break;
            }
            
            Stmt stmt = statement();
            stmts.add(stmt);
            
            eatSemicolon();
        }
        while(!isAtEnd());
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return node(new BlockStmt(stmts));
    }
    
    private FieldStmt fieldStatement() {
        switch(peek().getType()) {
            case IDENTIFIER: {
                Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
                consume(COLON, ErrorCode.MISSING_COLON);
                TypeInfo type = type(false);
                
                return node(new VarFieldStmt(identifier.getText(), type));
            }                
            case STRUCT: {
                advance();
                
                StructDecl struct = structDeclaration();
                return node(new StructFieldStmt(struct));
            }
            case UNION: {
                advance();
                
                UnionDecl union = unionDeclaration();
                return node(new UnionFieldStmt(union));                
            }
            case ENUM: {
                advance();
                
                EnumDecl enm = enumDeclaration();
                return node(new EnumFieldStmt(enm));
            }
            default:
                throw error(peek(), ErrorCode.INVALID_FIELD);
        }
    }
    
    private EnumFieldInfo enumFieldStatement() {
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        
        Expr expr = null;
        if(match(EQUALS)) {
            expr = constExpression();
        }
        
        return new EnumFieldInfo(identifier.getText(), expr);
    }
    
    private IfStmt ifStmt() {     
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Expr condExpr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        Stmt thenStmt = statement();
        Stmt elseStmt = null;
        if (match(ELSE)) {
            elseStmt = statement();
        }
        
        return node(new IfStmt(condExpr, thenStmt, elseStmt));
    }
    
    private WhileStmt whileStmt() {       
        this.loopLevel++;
        
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Expr condExpr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        Stmt bodyStmt = statement();
        this.loopLevel--;
        
        return node(new WhileStmt(condExpr, bodyStmt));
    }
    
    private DoWhileStmt doWhileStmt() {
        this.loopLevel++;
        Stmt bodyStmt = statement();
        
        consume(WHILE, ErrorCode.MISSING_COLON);
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Expr condExpr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        this.loopLevel--;
        
        return node(new DoWhileStmt(condExpr, bodyStmt));
    }
    
    private ForStmt forStmt() {        
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Stmt initStmt = statement();    
        consume(SEMICOLON, ErrorCode.MISSING_SEMICOLON);
        Expr condExpr = expression();
        consume(SEMICOLON, ErrorCode.MISSING_SEMICOLON);
        Stmt postStmt = statement();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        this.loopLevel++;
        Stmt bodyStmt = statement();
        this.loopLevel--;
        
        return node(new ForStmt(initStmt, condExpr, postStmt, bodyStmt));
    }
    
    
    private BreakStmt breakStmt() {     
        if(this.loopLevel < 1) {
            throw error(previous(), ErrorCode.INVALID_BREAK);
        }
        return node(new BreakStmt());
    }
    
    private ContinueStmt continueStmt() {
        if(this.loopLevel < 1) {
            throw error(previous(), ErrorCode.INVALID_CONTINUE);
        }
        return node(new ContinueStmt());
    }
    
    private ReturnStmt returnStmt() {
        Expr returnExpr = null;
        if(!check(SEMICOLON)) {
            returnExpr = expression();
        }
        
        return node(new ReturnStmt(returnExpr));
    }
    
    private DeferStmt deferStmt() {
        return node(new DeferStmt(statement()));
    }
    
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                      Expression parsing
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    
    private Expr expression() {
        source();
        return assignment();
    }
    
    private void checkConstExpr(Token token, Expr expr) {
        if(!(expr instanceof ConstExpr)) {
            if(expr instanceof GroupExpr) {
                GroupExpr groupExpr = (GroupExpr)expr;
                checkConstExpr(token, groupExpr.expr);
            }
            else if(expr instanceof BinaryExpr) {
                BinaryExpr binExpr = (BinaryExpr)expr;
                checkConstExpr(token, binExpr.left);
                checkConstExpr(token, binExpr.right);
            }
            else if(expr instanceof UnaryExpr) {
                UnaryExpr uExpr = (UnaryExpr)expr;
                checkConstExpr(token, uExpr.expr);
            }
            else {
                throw error(token, ErrorCode.INVALID_CONST_EXPR);
            }
        }
    }
    
    private Expr constExpression() {
        source();
        
        Token start = peek();
        
        Expr expr = assignment();
        checkConstExpr(start, expr);
        
        return expr;
    }
    
    private InitExpr aggregateInitExpr() {        
        // TODO: Figure out how to infer which type this is
        // based off of the parameter index
        List<InitArgExpr> arguments = structArguments();
        return node(new InitExpr(null, arguments));
    }
    
    private ArrayInitExpr arrayInitExpr() {
        TypeInfo array = type(false);
        if(!array.isKind(TypeKind.Array)) {
            throw error(previous(), ErrorCode.MISSING_LEFT_BRACE);
        }
        
        List<Expr> values = Collections.emptyList();
        if(match(LEFT_BRACE)) {            
            values = arrayArguments();            
        }
        
        return node(new ArrayInitExpr(array, values));
    }
        
    private Expr assignment() {
        Expr expr = or();
        
        while(match(EQUALS,
                    PLUS_EQ, MINUS_EQ, DIV_EQ, MUL_EQ,MOD_EQ,
                    LSHIFT_EQ, RSHIFT_EQ, BNOT_EQ, XOR_EQ, BAND_EQ, BOR_EQ)) {
            TokenType operator = previous().getType();
            Expr right = or();
            
            if(expr instanceof GetExpr) {
                GetExpr getExpr = (GetExpr)expr;
                expr = node(new SetExpr(getExpr.object, getExpr.field, operator, right));
            }
            else if(expr instanceof SubscriptGetExpr) {
                SubscriptGetExpr getExpr = (SubscriptGetExpr)expr;
                expr = node(new SubscriptSetExpr(getExpr.object, getExpr.index, operator, right));
            }
            else {
                expr = node(new BinaryExpr(expr, operator, right));
            }
        }
        
        return expr;
    }
        
    private Expr or() {
        Expr expr = and();
        
        while(match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr and() {
        Expr expr = bitOr();
        
        while(match(AND)) {
            Token operator = previous();
            Expr right = bitOr();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr bitOr() {
        Expr expr = bitXor();
        
        while(match(BOR)) {
            Token operator = previous();
            Expr right = bitXor();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }

    private Expr bitXor() {
        Expr expr = bitAnd();
        
        while(match(BAND)) {
            Token operator = previous();
            Expr right = bitAnd();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    
    private Expr bitAnd() {
        Expr expr = equality();
        
        while(match(BAND)) {
            Token operator = previous();
            Expr right = equality();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr equality() {
        Expr expr = comparison();
        
        while(match(NOT_EQUALS, 
                    EQUALS_EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr comparison() {
        Expr expr = bitShift();
        
        while(match(GREATER_THAN, GREATER_EQUALS, LESS_THAN, LESS_EQUALS)) {
            Token operator = previous();
            Expr right = bitShift();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr bitShift() {
        Expr expr = term();
        
        Expr bitExpr = tryBitShiftRight(expr);
        if(bitExpr != null) {
            return bitExpr;
        }
        
        while(match(LSHIFT, RSHIFT)) {
            Token operator = previous();
            Expr right = term();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr term() {
        Expr expr = factor();
        
        while(match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr factor() {
        Expr expr = unary();
        
        while(match(SLASH, STAR, MOD)) {
            Token operator = previous();
            Expr right = unary();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
        }
        
        return expr;
    }
    
    private Expr unary() {
        if(match(NOT, MINUS, PLUS, STAR, BAND, BNOT)) {
            Token operator = previous();
            Expr right = unary();
            return node(new UnaryExpr(operator.getType(), right)); 
        }
        else if(match(SIZEOF)) {
            return sizeof();
        }
        
        return functionCall();
    }
    
    private Expr cast(Expr expr) {
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        TypeInfo castTo = type(false);
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return node(new CastExpr(castTo, expr));
    }
    
    private Expr sizeof() {
        boolean hasParen = match(TokenType.LEFT_PAREN);
        
        Expr expr = null;
        
        Token t = peek();        
        if(t.getType().isPrimitiveToken() || t.getType().equals(LEFT_BRACKET)) {            
            TypeInfo type = type();
            expr = new IdentifierExpr(type.getName(), type);
        }
        else {
            expr = expression();
        }
                
        if(hasParen) {
            consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        }
        
        return node(new SizeOfExpr(expr));
    }
    
    private Expr functionCall() {
        Expr expr = primary();
        while(true) {
            if(match(LEFT_PAREN)) {                
                expr = finishFunctionCall(expr);
            }
            else if(check(LEFT_BRACE)) {
                if(!(expr instanceof IdentifierExpr)) {
                    return expr;
                }
                
                advance(); // eat the {
                
                IdentifierExpr idExpr = (IdentifierExpr)expr;
                List<InitArgExpr> arguments = structArguments();
                expr = node(new InitExpr(idExpr.type, arguments));
            }
            else if(match(LEFT_BRACKET)) {
                Expr index = expression();
                consume(RIGHT_BRACKET, ErrorCode.MISSING_RIGHT_BRACKET);
                
                expr = node(new SubscriptGetExpr(expr, index));
            }
            else if(match(DOT)) {
                Token name = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);                
                expr = node(new GetExpr(expr, new IdentifierTypeInfo(name.getText(), Collections.emptyList())));
            }
            else if(match(AS)) {
                expr = cast(expr);
            }
            else {
                break;
            }
        }
        
        return expr;
    }
    
    private Expr primary() {
        source();
        
        if(match(TRUE))  return node(new BooleanExpr(true));
        if(match(FALSE)) return node(new BooleanExpr(false));
        if(match(NULL))  return node(new NullExpr());
        
        if(match(NUMBER))  return node(new NumberExpr((NumberToken)previous()));        
        if(match(STRING))  return node(new StringExpr(previous().getValue().toString()));
        if(match(CHAR))    return node(new CharExpr(previous().getValue().toString()));
                
        if(match(LEFT_PAREN))   return groupExpr();
        if(check(LEFT_BRACKET)) return arrayInitExpr();
        if(match(LEFT_BRACE))   return aggregateInitExpr();                
        
        if(check(IDENTIFIER)) {
            IdentifierTypeInfo identifier = identifierType(true);
            return node(new IdentifierExpr(identifier.identifier, identifier));
        }
                
        throw error(peek(), ErrorCode.UNEXPECTED_TOKEN);
    }     
    
    private Expr finishFunctionCall(Expr callee) {
        List<TypeInfo> genericArgs = Collections.emptyList();
        
        List<Expr> arguments = arguments();
        if(callee instanceof IdentifierExpr) {
            IdentifierExpr idExpr = (IdentifierExpr)callee;
            if(idExpr.type instanceof IdentifierTypeInfo) {
                genericArgs = ((IdentifierTypeInfo)idExpr.type).genericArgs;
            }
            callee = node(new FuncIdentifierExpr(idExpr.variable, idExpr.type));
        }
        
        return node(new FuncCallExpr(callee, arguments, genericArgs));
    }
    
    private Expr groupExpr() {
        Expr expr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return new GroupExpr(expr);
    }
    
    private void eatSemicolon() {
        match(SEMICOLON);
    }
    
    private List<TypeInfo> tryGenericArguments(boolean disambiguiate) {
        int backtrack = this.current;
        
        advance(); // eat <
        
        List<TypeInfo> arguments = null;
        try {
            arguments = genericArguments();
            
            if(disambiguiate) {
                Token token = peek();
                if(!GENERICS_AMBIGUITY.contains(token.getType())) {
                    arguments = null;
                    this.current = backtrack;
                }
            }
        }
        catch(ParseException e) {
            this.current = backtrack;
        }
        
        return arguments;
    }
    
    private Expr tryBitShiftRight(Expr expr) {
        
        // Due to generics, we must see if we have a bit shift right operator
        // if we are in an expression, there must be a space between a generic
        // start and a greater than operator, otherwise this will think its a bit shift
        // right.
        if(check(GREATER_THAN)) {
            Token prevToken = advance();
            if(check(GREATER_THAN)) {
                Token nextToken = advance();
                if(nextToken.getPosition() - prevToken.getPosition() == 1) {
                    Expr right = term();
                    return node(new BinaryExpr(expr, TokenType.RSHIFT, right));
                }
                else {
                    rewind();
                    rewind();
                }
            }
            else {
                rewind();
            }
        }
        
        return null;
    }
    
    private TypeInfo chainableType(TypeInfo type) {
        do {
            advance();
        
            Token n = peek();
            if(n.getType().equals(STAR)) {
                type = new PtrTypeInfo(type);
            }
            else if(n.getType().equals(LEFT_BRACKET)) {
                type = arrayType(type);
            }
            else {
                break;
            }
        } while(!isAtEnd());
        
        return type;
    }
        
    private ArrayTypeInfo arrayType(TypeInfo type) {
        TypeInfo arrayOf = type;
        int length = -1;
    
        advance(); // eat [
        
        Expr lengthExpr = null;
        if(!peek().getType().equals(RIGHT_BRACKET)) {
            Expr expr = expression();
            
            if(expr instanceof NumberExpr) {
                lengthExpr = expr;
            }
            else if(expr instanceof IdentifierExpr) {
                lengthExpr = expr;
            }
            else {
                throw error(peek(), ErrorCode.INVALID_ARRAY_DIMENSION_EXPR);
            }            
        }
        
        return new ArrayTypeInfo(arrayOf, length, lengthExpr);                    
    }
    
    private TypeInfo type() {
        return type(true);
    }
    
    private TypeInfo type(boolean disambiguiate) {
        Token t = peek();
        switch(t.getType()) {
            case BOOL: {
                TypeInfo type = TypeInfo.BOOL_TYPE;
                return chainableType(type);
            }
            case CHAR: {
                TypeInfo type = TypeInfo.CHAR_TYPE;
                return chainableType(type);
            }
            case I8: {
                TypeInfo type = TypeInfo.I8_TYPE;
                return chainableType(type);
            }
            case U8: {
                TypeInfo type = TypeInfo.U8_TYPE;
                return chainableType(type);
            }
            case I16: {
                TypeInfo type = TypeInfo.I16_TYPE;
                return chainableType(type);
            }
            case U16: {
                TypeInfo type = TypeInfo.U16_TYPE;
                return chainableType(type);
            }
            case I32: {
                TypeInfo type = TypeInfo.I32_TYPE;
                return chainableType(type);
            }
            case U32: {
                TypeInfo type = TypeInfo.U32_TYPE;
                return chainableType(type);
            }
            case I64: {
                TypeInfo type = TypeInfo.I64_TYPE;
                return chainableType(type);
            }
            case U64: {
                TypeInfo type = TypeInfo.U64_TYPE;
                return chainableType(type);
            }
            case I128: {
                TypeInfo type = TypeInfo.I128_TYPE;
                return chainableType(type);
            }
            case U128: {
                TypeInfo type = TypeInfo.U128_TYPE;
                return chainableType(type);
            }
            case F32: {
                TypeInfo type = TypeInfo.F32_TYPE;
                return chainableType(type);
            }
            case F64: {
                TypeInfo type = TypeInfo.F64_TYPE;
                return chainableType(type);
            }
            case VOID: {
                TypeInfo type = TypeInfo.VOID_TYPE;
                return chainableType(type);
            }
            case IDENTIFIER: {
                TypeInfo type = identifierType(disambiguiate);

                // identifier() consumes multiple tokens, 
                // account for advance in ptr check
                rewind();
                return chainableType(type);
            }
            case STRING: {
                TypeInfo type = new StrTypeInfo(t.getText());
                return chainableType(type);
            }
            case LEFT_BRACKET: {                
                ArrayTypeInfo type = arrayType(null);
                advance();
                
                type.arrayOf = type(disambiguiate);
                return type;
            }            
            case FUNC: {
                advance();
                
                List<GenericParam> genericParams = Collections.emptyList();
                if(match(LESS_THAN)) {
                    genericParams = genericParameters();
                }
                
                consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
                List<TypeInfo> params = new ArrayList<>();
                boolean isVarargs = false;
                
                if(!check(RIGHT_PAREN)) {
                    do {
                        if(match(VAR_ARGS)) {
                            isVarargs = true;
                            if(!check(RIGHT_PAREN)) {
                                throw error(peek(), ErrorCode.INVALID_VARARG_POSITION);
                            }
                        }
                        else {
                            params.add(type(false));
                        }
                    }
                    while(match(COMMA));
                }
                
                consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
                
                consume(COLON, ErrorCode.MISSING_COLON);
                
                TypeInfo returnType = type(false);
                
                return new FuncPtrTypeInfo(returnType, params, isVarargs, genericParams);
            }
            default:
                throw error(t, ErrorCode.UNEXPECTED_TOKEN);
        }
    }
    
    private IdentifierTypeInfo identifierType(boolean disambiguate) {
        Token token = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        String identifier = token.getText();
        
        // imported module type
        if(match(COLON_COLON)) {            
            Token name = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            identifier = String.format("%s::%s", identifier, name.getText());
        }
        
        List<TypeInfo> arguments = null;
        if(check(LESS_THAN)) {
            arguments = tryGenericArguments(disambiguate);
        }
        
        if(arguments == null) {
            arguments = Collections.emptyList();
        }
        
        return new IdentifierTypeInfo(identifier, arguments);
    }
    
    /**
     * Parses parameter declarations:
     * 
     * func test(x:i32,y:f32,z:bool) : void {
     * }
     * 
     * The (x:i32,y:f32,z:bool) part
     * 
     * @return the parsed {@link ParameterList}
     */
    private ParametersStmt parametersStmt() {
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        
        List<ParameterDecl> parameterInfos = new ArrayList<>();
        boolean isVarargs = false;
        
        if(!check(RIGHT_PAREN)) {
            do {                
                if(match(VAR_ARGS)) {
                    isVarargs = true;
                    if(!check(RIGHT_PAREN)) {
                        throw error(peek(), ErrorCode.INVALID_VARARG_POSITION);
                    }
                }
                else {
                    Token param = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
                    String parameterName = param.getText();
                    
                    consume(COLON, ErrorCode.MISSING_COLON);
                    TypeInfo type = type(false);
                    
                    parameterInfos.add(new ParameterDecl(type, parameterName));
                }
            }
            while(match(COMMA));
        }
        
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return node(new ParametersStmt(parameterInfos, isVarargs));
    }
    
    /**
     * Parses arguments:
     * 
     * someFunction( 1.0, x );
     * 
     * Parses the ( 1.0, x ) into a {@link List} of {@link Expr}
     * 
     * @return the {@link List} of {@link Expr}
     */
    private List<Expr> arguments() {
        List<Expr> arguments = new ArrayList<>();
        if(!check(RIGHT_PAREN)) {        
            do {
                arguments.add(expression());
            } 
            while(match(COMMA));            
        }
        
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        return arguments;
    }
    
    private List<GenericParam> genericParameters() {
        List<GenericParam> arguments = new ArrayList<>();
        if(!check(GREATER_THAN)) {        
            do {
                String typeName = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER).getText();
                arguments.add(new GenericParam(typeName));
            } 
            while(match(COMMA));            
        }
        
        consume(GREATER_THAN, ErrorCode.MISSING_GENERIC_END);
        
        return arguments;
    }
    
    private List<TypeInfo> genericArguments() {
        List<TypeInfo> arguments = new ArrayList<>();
        if(!check(GREATER_THAN)) {        
            do {
                arguments.add(type());
            } 
            while(match(COMMA));            
        }
        
        consume(GREATER_THAN, ErrorCode.MISSING_GENERIC_END);
        
        return arguments;
    }

    private List<Expr> arrayArguments() {
        List<Expr> arguments = new ArrayList<>();
        if(!check(RIGHT_BRACE)) {        
            do {
                arguments.add(expression());
            } 
            while(match(COMMA));            
        }
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return arguments;
    }
    
    private List<InitArgExpr> structArguments() {
        List<InitArgExpr> arguments = new ArrayList<>();
        int argPosition = 0;
        do {
            if(check(RIGHT_BRACE)) {
                break;
            }
            
            String fieldName = null;
            if(match(DOT)) {
                fieldName = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER).getText();
                consume(COLON, ErrorCode.MISSING_COLON);
            }
            
            Expr value = expression();
            InitArgExpr argExpr = new InitArgExpr(fieldName, argPosition++, value);
            arguments.add(node(argExpr));
             
        }
        while(match(COMMA));            
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return arguments;
    }

    
    /**
     * Mark the start of parsing a statement
     * so that we can properly mark the AST node
     * source line and number information
     */
    private void source() {
        this.startToken = peek();
    }
    
    /**
     * Updates the AST node parsing information
     * 
     * @param node
     * @return the supplied node
     */
    private <T extends Node> T node(T node) {
        if(this.startToken != null) {
            node.setSourceFile(this.scanner.getSourceFile());
            node.setSourceLine(this.scanner.getSourceLine(this.startToken.getLineNumber()));
            node.setLineNumber(this.startToken.getLineNumber());
        }
        return node;
    }
    
    /**
     * Determines if the supplied {@link TokenType} is
     * the current {@link Token}, if it is it will advance
     * over it.
     * 
     * @param type
     * @return true if we've advanced (i.e., the supplied token type was
     * the current one).
     */
    private boolean match(TokenType type) {        
        if(check(type)) {
            advance();
            return true;
        }        
        
        return false;
    }

    /**
     * Determines if any of the supplied {@link TokenType}'s are
     * the current {@link Token}, if it is it will advance.
     * 
     * @param type
     * @return true if we've advanced (i.e., the supplied token type was
     * the current one).
     */
    private boolean match(TokenType ...types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Ensures the supplied {@link TokenType} is the current one and 
     * advances.  If the {@link TokenType} does not match, this will
     * throw a {@link ParseException}
     * 
     * @param type
     * @param errorCode
     * @return the skipped {@link Token}
     */
    private Token consume(TokenType type, ErrorCode errorCode) {
        if(check(type)) {
            return advance();
        }
        
        throw error(peek(), errorCode);
    }
    
    
    /**
     * Checks to see if the current {@link Token} is of the supplied
     * {@link TokenType}
     * 
     * @param type
     * @return true if it is
     */
    private boolean check(TokenType type) {
        if(isAtEnd()) {
            return false;
        }
        
        return peek().getType() == type;
    }
  
    /**
     * Advances to the next Token.  If we've reached
     * the END_OF_FILE token, this stop advancing.
     * 
     * @return the previous token.
     */
    private Token advance() {
        if(!isAtEnd()) {
            this.current++;
        }
        return previous();
    }
    
    
    /**
     * The previous token
     * @return The previous token
     */
    private Token previous() {
        return this.tokens.get(current - 1);
    }
        
    /**
     * The current token
     * @return The current token
     */
    private Token peek() {
        return this.tokens.get(current);
    }
    
    private void rewind() {
        this.current--;
        if(this.current < 0) {
            this.current = 0;
        }
    }
        
    /**
     * If we've reached the end of the file
     * 
     * @return true if we're at the end
     */
    private boolean isAtEnd() {
        return peek().getType() == END_OF_FILE;
    }
    
    
    /**
     * Constructs an error message into a {@link ParseException}
     * 
     * @param token
     * @param errorCode
     * @return the {@link ParseException} to be thrown
     */
    private ParseException error(Token token, ErrorCode errorCode) {
        return Errors.parseError(this.scanner, token, errorCode);
    }
}
