package litac.parser;


import static litac.parser.tokens.TokenType.*;

import java.util.ArrayList;
import java.util.List;

import litac.Errors;
import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.ast.TypeInfo.*;
import litac.parser.tokens.NumberToken;
import litac.parser.tokens.Token;
import litac.parser.tokens.TokenType;


/**
 * A {@link Parser} for the JSLT programming language.
 * 
 * @author Tony
 *
 */
public class Parser {   
    private final Scanner scanner;
    private final List<Token> tokens;
    private int current;
        
    private Token startToken;
    
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
        List<Decl> declarations = new ArrayList<>();
        
        String moduleName = ""; // default to global module
        if(match(MODULE))  moduleName = moduleDeclaration();
        
        while(!isAtEnd()) {
            if(match(IMPORT))       imports.add(importDeclaration());
            else if(match(VAR))     declarations.add(varDeclaration());
            else if(match(CONST))   declarations.add(constDeclaration());
            else if(match(FUNC))    declarations.add(funcDeclaration());
            else if(match(STRUCT))  declarations.add(structDeclaration());
            else if(match(UNION))   declarations.add(unionDeclaration());
            else if(match(ENUM))    declarations.add(enumDeclaration());
            else if(match(TYPEDEF)) declarations.add(typedefDeclaration());
            else if(match(SEMICOLON));
            else throw error(peek(), ErrorCode.UNEXPECTED_TOKEN);
        }
                
        return node(new ModuleStmt(moduleName, imports, declarations));
    }

    

    /**
     * Parses the program
     * 
     * @return the {@link ProgramStmt}
     */
    public ProgramStmt parseProgram() {
        
        List<ImportStmt> imports = new ArrayList<>();
        List<Decl> declarations = new ArrayList<>();
        
        String moduleName = ""; // default to global module
        if(match(MODULE))  moduleName = moduleDeclaration();
        
        while(!isAtEnd()) {
            if(match(IMPORT))       imports.add(importDeclaration());
            else if(match(VAR))     declarations.add(varDeclaration());
            else if(match(CONST))   declarations.add(constDeclaration());
            else if(match(FUNC))    declarations.add(funcDeclaration());
            else if(match(STRUCT))  declarations.add(structDeclaration());
            else if(match(UNION))   declarations.add(unionDeclaration());
            else if(match(ENUM))    declarations.add(enumDeclaration());
            else if(match(TYPEDEF)) declarations.add(typedefDeclaration());            
            else if(match(SEMICOLON));
            else throw error(peek(), ErrorCode.UNEXPECTED_TOKEN);
        }
        
        return node(new ProgramStmt(moduleName, imports, declarations));
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
        
        Token library = consume(STRING, ErrorCode.MISSING_IDENTIFIER);
        if(match(AS)) {
            Token alias = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            aliasName = alias.getText();
        }
        
        return node(new ImportStmt(library.getText(), aliasName));
    }
        
    private VarDecl varDeclaration() {
        source();
        
        TypeInfo type = null;
        Expr expr = null;
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(match(COLON)) {
            type = type();
            
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
            
            type = new IdentifierTypeInfo(identifier.getText());
        }
        
        
                
        return node(new VarDecl(identifier.getText(), type, expr));
    }
    
    private ConstDecl constDeclaration() {
        source();
        
        TypeInfo type = null;
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(match(COLON)) {
            type = type();
        }
        
        consume(TokenType.EQUALS, ErrorCode.MISSING_EQUALS);
        
        
        Expr expr = constExpression();            
        
                
        return node(new ConstDecl(identifier.getText(), type, expr));
    }
    
    
    
    private FuncDecl funcDeclaration() {
        source();
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);        
        List<Parameter> parameters = parameters();
        
        consume(COLON, ErrorCode.MISSING_COLON);
        
        TypeInfo returnType = type();
        TypeInfo type = new FuncTypeInfo(identifier.getText(), returnType, parameters);
        
        Stmt body = statement();
        return node(new FuncDecl(identifier.getText(), type, parameters, body, returnType));
    }
    
    private StructDecl structDeclaration() {
        source();
        
        Token start = peek();
        
        String structName = null;
        if(check(IDENTIFIER)) {
            Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            structName = identifier.getText();
        }
        
        consume(LEFT_BRACE, ErrorCode.MISSING_LEFT_BRACE);
        
        List<FieldStmt> fields = new ArrayList<>();
        
        do {
            if(check(RIGHT_BRACE)) {
                break;
            }
            
            FieldStmt field = fieldStatement();
            fields.add(field);
            
            eatSemicolon();
        }
        while(!isAtEnd());
        
        List<Field> typeFields = Stmt.fromFieldStmt(start, fields);
        TypeInfo type = new StructTypeInfo(structName, typeFields);
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return node(new StructDecl(structName, type, fields));
    }
    
    private UnionDecl unionDeclaration() {
        source();
        
        Token start = peek();
        
        String unionName = null;
        if(check(IDENTIFIER)) {
            Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            unionName = identifier.getText();
        }
        
        consume(LEFT_BRACE, ErrorCode.MISSING_LEFT_BRACE);
        
        List<FieldStmt> fields = new ArrayList<>();
        
        do {
            if(check(RIGHT_BRACE)) {
                break;
            }
            
            FieldStmt field = fieldStatement();
            fields.add(field);
            
            eatSemicolon();
        }
        while(!isAtEnd());
        
        List<Field> typeFields = Stmt.fromFieldStmt(start, fields);
        TypeInfo type = new UnionTypeInfo(unionName, typeFields);
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return node(new UnionDecl(unionName, type, fields));
    }
    
    private EnumDecl enumDeclaration() {
        source();
        
        String enumName = null;
        if(check(IDENTIFIER)) {
            Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            enumName = identifier.getText();
        }
        
        consume(LEFT_BRACE, ErrorCode.MISSING_LEFT_BRACE);
        
        List<EnumField> fields = new ArrayList<>();
        
        do {
            if(check(RIGHT_BRACE)) {
                break;
            }
            
            EnumField field = enumFieldStatement();
            fields.add(field);
        }
        while(match(COMMA));
                
        TypeInfo type = new EnumTypeInfo(enumName, fields);
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return node(new EnumDecl(enumName, type, fields));
    }

    private TypedefDecl typedefDeclaration() {
        source();
        return null;
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
                TypeInfo type = type();
                
                return new VarFieldStmt(identifier.getText(), type);
            }                
            case STRUCT: {
                advance();
                
                StructDecl struct = structDeclaration();
                return new StructFieldStmt(struct);                
            }
            case UNION: {
                advance();
                
                UnionDecl union = unionDeclaration();
                return new UnionFieldStmt(union);                
            }
            default:
                throw error(peek(), ErrorCode.INVALID_FIELD);
        }
    }
    
    private EnumField enumFieldStatement() {
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        
        Expr expr = null;
        if(match(EQUALS)) {
            expr = constExpression();
        }
        
        return new EnumField(identifier.getText(), expr);
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
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Expr condExpr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        
        Stmt bodyStmt = statement();
        
        return node(new WhileStmt(condExpr, bodyStmt));
    }
    
    private DoWhileStmt doWhileStmt() {        
        Stmt bodyStmt = statement();
        
        consume(WHILE, ErrorCode.MISSING_COLON);
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Expr condExpr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
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
        
        Stmt bodyStmt = statement();
        
        return node(new ForStmt(initStmt, condExpr, postStmt, bodyStmt));
    }
    
    
    private BreakStmt breakStmt() {                        
        return node(new BreakStmt());
    }
    
    private ContinueStmt continueStmt() {                        
        return node(new ContinueStmt());
    }
    
    private ReturnStmt returnStmt() {
        Expr returnExpr = null;
        if(!check(SEMICOLON)) {
            returnExpr = expression();
        }
        
        return node(new ReturnStmt(returnExpr));
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
            
    private DotExpr dotExpr() {
        Expr field = null;
        if(match(IDENTIFIER))  field = node(new IdentifierExpr(previous().getText(), new IdentifierTypeInfo(previous().getText())));
        else if(match(STRING)) field = node(new StringExpr(previous().getText()));
        
        return node(new DotExpr(field));
    }
    
    private Expr assignment() {
        Expr expr = or();
        
        while(match(EQUALS,
                    PLUS_EQ, MINUS_EQ, DIV_EQ, MUL_EQ,MOD_EQ,
                    LSHIFT_EQ, RSHIFT_EQ, BNOT_EQ, XOR_EQ, BAND_EQ, BOR_EQ)) {
            Token operator = previous();
            Expr right = or();
            expr = node(new BinaryExpr(expr, operator.getType(), right));
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
        if(match(NOT, MINUS, PLUS, STAR, BAND, BNOT /*sizeof, cast*/)) {
            Token operator = previous();
            Expr right = unary();
            return node(new UnaryExpr(operator.getType(), right)); 
        }
        return functionCall();
    }
    
    private Expr functionCall() {
        Expr expr = primary();
        while(true) {
            if(check(LEFT_PAREN)) {                
                expr = finishFunctionCall(expr);
            }
            else if(check(LEFT_BRACE)) {
                if(!(expr instanceof IdentifierExpr)) {
                    throw error(peek(), ErrorCode.INVALID_ASSIGNMENT);
                }
                
                advance(); // eat the {
                IdentifierExpr idExpr = (IdentifierExpr)expr;
                List<Expr> arguments = structArguments();
                expr = new InitExpr(idExpr.type, arguments);
            }
            else if(check(LEFT_BRACKET)) {
                // SubscriptGetExpr
            }
            else if(match(DOT)) {
                Token name = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);                
                expr = node(new GetExpr(expr, new IdentifierTypeInfo(name.getText())));
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
        
        if(match(IDENTIFIER)) return node(new IdentifierExpr(previous().getText(), new IdentifierTypeInfo(previous().getText())));
                
        if(match(LEFT_PAREN)) return groupExpr();
        
//        if(match(LEFT_BRACKET)) return array();
//        if(match(LEFT_BRACE))   return object();
//        
//        if(match(STAR))      return matchExpr();
//        if(match(AMPERSAND)) return matchExpr();
        if(match(DOT))  return dotExpr();
                        
        throw error(peek(), ErrorCode.UNEXPECTED_TOKEN);
    }     
    
    private Expr finishFunctionCall(Expr callee) {
        List<Expr> arguments = arguments();        
        return node(new FuncCallExpr(callee, arguments));
    }
    
    private Expr groupExpr() {
        Expr expr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return new GroupExpr(expr);
    }
    
    private void eatSemicolon() {
        match(SEMICOLON);
    }
    
    private TypeInfo ptrType(TypeInfo type) {
        do {
            advance();
        
            Token n = peek();
            if(!n.getType().equals(STAR)) {
                break;
            }
            
            type = new PtrTypeInfo("Ptr", type);                    
        } while(!isAtEnd());
        
        return type;
    }
    
    private TypeInfo type() {
        Token t = peek();
        switch(t.getType()) {
            case BOOL: {
                TypeInfo type = new PrimitiveTypeInfo("bool", TypeKind.bool);
                return ptrType(type);
            }
            case I8: {
                TypeInfo type = new PrimitiveTypeInfo("i8", TypeKind.i8);
                return ptrType(type);
            }
            case U8: {
                TypeInfo type = new PrimitiveTypeInfo("u8", TypeKind.u8);
                return ptrType(type);
            }
            case I16: {
                TypeInfo type = new PrimitiveTypeInfo("i16", TypeKind.i16);
                return ptrType(type);
            }
            case U16: {
                TypeInfo type = new PrimitiveTypeInfo("u16", TypeKind.u16);
                return ptrType(type);
            }
            case I32: {
                TypeInfo type = new PrimitiveTypeInfo("i32", TypeKind.i32);
                return ptrType(type);
            }
            case U32: {
                TypeInfo type = new PrimitiveTypeInfo("u32", TypeKind.u32);
                return ptrType(type);
            }
            case I64: {
                TypeInfo type = new PrimitiveTypeInfo("i64", TypeKind.i64);
                return ptrType(type);
            }
            case U64: {
                TypeInfo type = new PrimitiveTypeInfo("u64", TypeKind.u64);
                return ptrType(type);
            }
            case I128: {
                TypeInfo type = new PrimitiveTypeInfo("i128", TypeKind.i128);
                return ptrType(type);
            }
            case U128: {
                TypeInfo type = new PrimitiveTypeInfo("u128", TypeKind.u128);
                return ptrType(type);
            }
            case F32: {
                TypeInfo type = new PrimitiveTypeInfo("f32", TypeKind.f32);
                return ptrType(type);
            }
            case F64: {
                TypeInfo type = new PrimitiveTypeInfo("f64", TypeKind.f64);
                return ptrType(type);
            }
            case IDENTIFIER: {
                TypeInfo type = new IdentifierTypeInfo(t.getText());
                return ptrType(type);
            }
            case VOID: {
                TypeInfo type = new VoidTypeInfo();
                return ptrType(type);
            }
            case STRING: {
                TypeInfo type = new StrTypeInfo(null);
                return ptrType(type);
            }
            // case FUNC: TODO
            default:
                throw error(t, ErrorCode.UNEXPECTED_TOKEN);
        }
    }
    
    /**
     * Parses parameters:
     * 
     * func test(x:i32,y:f32,z:bool) : void {
     * }
     * 
     * The (x:i32,y:f32,z:bool) part
     * 
     * @return the parsed {@link ParameterList}
     */
    private List<Parameter> parameters() {
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        
        List<Parameter> parameters = new ArrayList<>();        
        if(!check(RIGHT_PAREN)) {
            do {                
                Token param = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
                String parameterName = param.getText();
                
                consume(COLON, ErrorCode.MISSING_COLON);
                TypeInfo type = type();
                
                parameters.add(new Parameter(type, parameterName));
            }
            while(match(COMMA));
        }
        
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return parameters;
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

    private List<Expr> structArguments() {
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
