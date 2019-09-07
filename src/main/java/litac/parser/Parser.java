package litac.parser;


import static litac.parser.tokens.TokenType.*;

import java.util.*;

import leola.ast.ParameterList;
import litac.Errors;
import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node.SrcPos;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.AggregateTypeInfo;
import litac.checker.TypeInfo.EnumFieldInfo;
import litac.compiler.Preprocessor;
import litac.generics.GenericParam;
import litac.parser.tokens.*;
import litac.util.Names;


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
    private int switchLevel;
    private int breakLevel;
    private int aggregateLevel;
    private int funcLevel;
    
    private final Scanner scanner;
    private final List<Token> tokens;
    private int current;
        
    private Token startToken;
    private Preprocessor pp;
    
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
    public Parser(Preprocessor pp, Scanner scanner) {
        this.scanner = scanner;
        this.tokens = scanner.getTokens();
        
        this.current = 0;    
        this.pp = pp;
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
        
        String moduleName = Names.getModuleName(this.scanner.getSourceFile());
        while(!isAtEnd()) {
            if(match(IMPORT)) {
                imports.add(importDeclaration());
            }
            else if(match(HASH)) {
                CompStmt compStmt = compStmt();
                executeCompStmt(compStmt, imports, moduleNotes, declarations);
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
        
    private ImportStmt importDeclaration() {
        source();
        
        String aliasName = null;
        String moduleName = null;
        boolean isUsing = false;
        
        if(match(USING)) {
            isUsing = true;
        }
        
        Token library = consume(STRING, ErrorCode.MISSING_IDENTIFIER);
        if(match(AS)) {
            Token alias = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            aliasName = alias.getText();
        }
        
        // removes the string quotes
        String libTxt = library.getText();
        moduleName = libTxt.substring(1, libTxt.length() - 1);
        
        return node(new ImportStmt(moduleName, aliasName, isUsing));
    }
        
    private VarDecl varDeclaration() {
        source();
        
        TypeSpec type = null;
        Expr expr = null;
        int modifiers = 0;
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(match(COLON)) {
            modifiers = modifiers();
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
                
        return node(new VarDecl(identifier.getText(), type, expr, modifiers));
    }
    
    private ConstDecl constDeclaration() {
        source();
        
        TypeSpec type = null;
        int modifiers = 0;
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(match(COLON)) {
            modifiers = modifiers();
            type = type(false);
        }
        
        Expr expr = null;
        if(match(EQUALS)) {
        // TODO: Create module initializer functions, so
        // that module level variables can be initialized via functions
        // and NOT just constExpressions
            expr = // constExpression(); 
                expression();
        }
        
        return node(new ConstDecl(identifier.getText(), type, expr, modifiers));
    }
    
    
    
    private FuncDecl funcDeclaration() {
        source();
        this.funcLevel++;
        
        ParameterDecl objectParam = null;
        if(match(LEFT_PAREN)) {
            objectParam = parameterDecl(false);
            consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        }
        
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        
        
        List<GenericParam> genericParams = Collections.emptyList();
        if(match(LESS_THAN)) {
            genericParams = genericParameters();
        }
        
        ParametersStmt parameters = parametersStmt();
        if(objectParam != null) {
            parameters.params.add(0, objectParam);
        }
        
        TypeSpec returnType = TypeSpec.newVoid(peek().getPos());
        if(match(COLON)) {
            returnType = type(false);
        }
        
        int flags = 0;
        if(parameters.isVararg) {
            flags |= TypeInfo.FUNC_ISVARARG_FLAG;
        }
        
        if(objectParam != null) {
            flags |= TypeInfo.FUNC_ISMETHOD_FLAG;
        }
                
        Stmt body;
        if(match(SEMICOLON)) {
            body = emptyStmt();
        }
        else {        
            body = statement();
        }
        
        this.funcLevel--;
        
        return node(new FuncDecl(identifier.getText(), parameters, body, returnType, genericParams, flags));
    }
    
    private StructDecl structDeclaration() {
        source();
        
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
                                       
        return node(new StructDecl(structName, fields, genericParams, flags));
    }
    
    private UnionDecl unionDeclaration() {
        source();
        
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
        
        return node(new UnionDecl(unionName, fields, genericParams, flags));
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
                        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return node(new EnumDecl(enumName, fields));
    }

    private TypedefDecl typedefDeclaration() {
        source();
        
        TypeSpec aliasedType = type(false);        
        match(AS);
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        String name = identifier.getText();
        
        List<GenericParam> genericParams = Collections.emptyList();
        if(match(LESS_THAN)) {
            genericParams = genericParameters();
        }
        
        return node(new TypedefDecl(name, aliasedType, name, genericParams));
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
                
                notes.add(node(new NoteStmt(identifier.getText(), attributes)));
            }
        }
        
        return notes;
    }
    
    private Stmt compileTimeStmt() {
        if(match(IMPORT)) {
            return importDeclaration();
        }
        else {
            List<NoteStmt> notes = notes();
            boolean isPublic = match(PUBLIC);
            
            Decl decl = null;
            if(match(VAR))            decl = varDeclaration();
            else if(match(CONST))     decl = constDeclaration();
            else if(match(FUNC))      decl = funcDeclaration();
            else if(match(STRUCT))    decl = structDeclaration();
            else if(match(UNION))     decl = unionDeclaration();
            else if(match(ENUM))      decl = enumDeclaration();
            else if(match(TYPEDEF))   decl = typedefDeclaration();            
            else if(match(SEMICOLON)) {
                if(notes != null) {
                    return new BlockStmt(new ArrayList<>(notes));
                }
                return null;
            }
            else {
                return statement();
            }
            
            Attributes attrs = decl.attributes; 
            attrs.isPublic = isPublic;
            attrs.isGlobal = true;
            attrs.notes = notes;
            
            return decl;
        }
    }
    
    private void executeCompStmt(CompStmt compStmt, List<ImportStmt> imports, List<NoteStmt> moduleNotes, List<Decl> declarations) {
        if(compStmt.type.equals("if") || compStmt.type.equals("elseif") || compStmt.type.equals("else")) {
            if(compStmt.type.equals("else") || this.pp.execute(compStmt.expr)) {
                List<Stmt> stmts = compStmt.body;
                for(Stmt s : stmts) {
                    if(s instanceof Decl) {
                        declarations.add((Decl)s);
                    }
                    else if(s instanceof ImportStmt) {
                        imports.add((ImportStmt)s);
                    }
                    else if(s instanceof NoteStmt) {
                        moduleNotes.add((NoteStmt)s);
                    }
                    else if(s instanceof BlockStmt) {
                        BlockStmt b = (BlockStmt)s;
                        for(Stmt n : b.stmts) {
                            if(n instanceof NoteStmt) {
                                moduleNotes.add((NoteStmt)n);
                            }
                        }
                    }
                    else {
                        throw error(peek(), ErrorCode.INVALID_COMP_STMT);
                    }
                }
            }
            else {
                if(compStmt.end != null) {
                    executeCompStmt(compStmt.end, imports, moduleNotes, declarations);
                }
            }
        }
    }
    
    private CompStmt compStmt() {
        String type = "";
        
        if(match(IF)) {
            type = "if";
        }
        else if(match(ELSE)) {
            type = "else";
        }
        else {
            Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
            type = identifier.getText().toLowerCase();
        }
        
        switch(type) {
            case "if": 
            case "elseif": {
                consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
                StringBuilder sb = new StringBuilder();
                while(!isAtEnd()) {
                    if(check(RIGHT_PAREN)) {
                        break;
                    }
                    
                    sb.append(" ").append(advance().getText());                    
                }
                consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
               
                List<Stmt> body = new ArrayList<>();
                while(!isAtEnd()) {
                    if(check(HASH)) {
                        break;
                    }
                    
                    Stmt stmt = compileTimeStmt();
                    if(stmt != null) {
                        body.add(stmt);
                    }
                }
                
                consume(HASH, ErrorCode.MISSING_COMP_STMT_END);
                
                CompStmt end = compStmt();
                return new CompStmt(type, sb.toString(), body, end);
            }
            case "else": {
                List<Stmt> body = new ArrayList<>();
                while(!isAtEnd()) {
                    if(check(HASH)) {
                        break;
                    }
                    
                    Stmt stmt = compileTimeStmt();
                    if(stmt != null) {
                        body.add(stmt);
                    }
                }
                
                consume(HASH, ErrorCode.MISSING_COMP_STMT_END);
                
                CompStmt end = compStmt();
                return new CompStmt(type, "", body, end);
            }
            case "end": {
                return new CompStmt(type, "", Collections.emptyList(), null);
            }
            default: {
                throw error(peek(), ErrorCode.INVALID_COMP_STMT);
            }
        }
    }
    
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                      Statement parsing
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    private Stmt statement() {    
        source();
        
        // check for notes on declarations
        if(check(AT)) {
            List<NoteStmt> notes = notes();
        
            Decl decl = null;
            if(match(VAR)) {
                decl = varDeclaration();
            }
            else if(match(CONST)) {
                decl = constDeclaration();
            }
            else {
                throw error(peek(), ErrorCode.INVALID_NOTE_DECL);
            }
            
            decl.attributes.notes = notes;
            return decl;
        }
        
        if(match(LEFT_BRACE))   return blockStatement();        
        if(match(VAR))          return varDeclaration();        
        if(match(CONST))        return constDeclaration();
        if(match(IF))           return ifStmt();
        if(match(WHILE))        return whileStmt();
        if(match(DO))           return doWhileStmt();
        if(match(FOR))          return forStmt();
        if(match(SWITCH))       return switchStmt();
        if(match(BREAK))        return breakStmt();
        if(match(CONTINUE))     return continueStmt();
        if(match(RETURN))       return returnStmt();
        if(match(DEFER))        return deferStmt();
        if(match(GOTO))         return gotoStmt();
        //if(match(SEMICOLON))    return emptyStmt();
        
        if(check(IDENTIFIER)) {
            Stmt stmt = tryLabelStmt();
            if(stmt != null) {
                return stmt;
            }
        }
        
        return expression();
    }
    
    private EmptyStmt emptyStmt() {
        return node(new EmptyStmt());
    }
    
    private BlockStmt blockStatement() {
        List<Stmt> stmts = new ArrayList<>();
        
        if(this.breakLevel > 0) {
            this.breakLevel--;
        }
        
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
        List<NoteStmt> notes = notes();
        switch(peek().getType()) {
            case IDENTIFIER: {
                Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
                consume(COLON, ErrorCode.MISSING_COLON);
                int modifiers = modifiers();
                TypeSpec type = type(false);
                
                return node(new VarFieldStmt(identifier.getText(), type, modifiers).addNotes(notes));
            }                
            case STRUCT: {
                advance();
                
                StructDecl struct = structDeclaration();
                struct.attributes.addNotes(notes);
                return node(new StructFieldStmt(struct));
            }
            case UNION: {
                advance();
                
                UnionDecl union = unionDeclaration();
                union.attributes.addNotes(notes);
                return node(new UnionFieldStmt(union));                
            }
            case ENUM: {
                advance();
                
                EnumDecl enm = enumDeclaration();
                enm.attributes.addNotes(notes);
                return node(new EnumFieldStmt(enm));
            }
            default:
                throw error(peek(), ErrorCode.INVALID_FIELD);
        }
    }
    
    private EnumFieldInfo enumFieldStatement() {
        List<NoteStmt> notes = notes();
        Token identifier = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        
        Expr expr = null;
        if(match(EQUALS)) {
            expr = constExpression();
        }
        
        Attributes attrs = new Attributes();
        attrs.notes = notes;
        
        return new EnumFieldInfo(identifier.getText(), expr, attrs);
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
        Stmt initStmt = !check(SEMICOLON) ? statement() : null;    
        consume(SEMICOLON, ErrorCode.MISSING_SEMICOLON);
        Expr condExpr = !check(SEMICOLON) ? expression() : null;
        consume(SEMICOLON, ErrorCode.MISSING_SEMICOLON);
        Stmt postStmt = !check(RIGHT_PAREN) ? statement() : null;
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        this.loopLevel++;
        Stmt bodyStmt = statement();
        this.loopLevel--;
        
        return node(new ForStmt(initStmt, condExpr, postStmt, bodyStmt));
    }
    
    private SwitchCaseStmt switchCaseStmt() {
        Expr cond =  constExpression();
        consume(COLON, ErrorCode.MISSING_COLON);
        
        List<Stmt> stmts = new ArrayList<>();
        
//        int breakCount = this.breakLevel;
        while(!isAtEnd()) {
            if(check(RIGHT_BRACE) ||
               check(CASE) ||
               check(DEFAULT)) {
                break;
            }
            
            Stmt stmt = statement();
            match(SEMICOLON);
            
            stmts.add(stmt);
                       
//            if(breakCount != this.breakLevel) {
//                this.breakLevel--;
//                break;
//            }
        }
        
        return node(new SwitchCaseStmt(cond, stmts.isEmpty() 
                            ? new EmptyStmt() : new BlockStmt(stmts)));
    }
    
    private Stmt defaultStmt() {
        List<Stmt> stmts = new ArrayList<>();
        
        int breakCount = this.breakLevel;
        while(!isAtEnd()) {
            if(check(RIGHT_BRACE) ||
               check(CASE)) {
                break;
            }
            
            Stmt stmt = statement();
            match(SEMICOLON);
            
            stmts.add(stmt);
                       
            if(breakCount != this.breakLevel) {
                this.breakLevel--;
                break;
            }
        }
        
        return node(new BlockStmt(stmts));
    }
    
    private SwitchStmt switchStmt() {
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        Expr cond = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        
        boolean startBrace = match(LEFT_BRACE);
        
        Stmt defaultStmt = null;
        List<SwitchCaseStmt> caseStmts = new ArrayList<>();
        
        this.switchLevel++;
        
        while(!isAtEnd()) {
            if(match(CASE)) {
                caseStmts.add(switchCaseStmt());
                
                match(SEMICOLON);
            }
            else if(match(DEFAULT)) {
                consume(COLON, ErrorCode.MISSING_COLON);
                defaultStmt = defaultStmt();
                
                match(SEMICOLON);
            }
            else {
                break;
            }
        }
        
        this.switchLevel--;
        
        if(startBrace) {
            consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        }
        
        return node(new SwitchStmt(cond, caseStmts, defaultStmt));
    }
    
    
    private BreakStmt breakStmt() {     
        if(this.loopLevel < 1 && this.switchLevel < 1) {
            throw error(previous(), ErrorCode.INVALID_BREAK);
        }
        
        if(this.switchLevel > 0 && this.loopLevel < 1) {
//            if(this.breakLevel > 0) {
//                throw error(previous(), ErrorCode.INVALID_BREAK);
//            }
            
            this.breakLevel++;
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
    
    private GotoStmt gotoStmt() {
        Token label = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        return node(new GotoStmt(label.getText()));
    }
    
    private LabelStmt tryLabelStmt() {
        int backtrack = this.current;
        
        Token label = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        if(!match(COLON)) {
            this.current = backtrack;
            return null;
        }
        
        if(this.funcLevel < 0) {
            throw error(peek(), ErrorCode.INVALID_ARRAY_DIMENSION_EXPR);
        }
        
        return node(new LabelStmt(label.getText()));
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
            else if(expr instanceof IdentifierExpr || expr instanceof GetExpr) {
                // this is allowed, will be checked for
                // constant in type checker
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
        TypeSpec array = type(false);
        if(array.kind != TypeSpecKind.ARRAY) {
            throw error(previous(), ErrorCode.MISSING_LEFT_BRACE);
        }
        
        List<Expr> values = Collections.emptyList();
        if(match(LEFT_BRACE)) {            
            values = arrayArguments();            
        }
        
        return node(new ArrayInitExpr(array, values));
    }
        
    private Expr assignment() {
        Expr expr = ternary();
        
        while(match(EQUALS,
                    PLUS_EQ, MINUS_EQ, DIV_EQ, MUL_EQ,MOD_EQ,
                    LSHIFT_EQ, RSHIFT_EQ, BNOT_EQ, XOR_EQ, BAND_EQ, BOR_EQ)) {
            TokenType operator = previous().getType();
            Expr right = ternary();
            
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
    
    private Expr ternary() {
        Expr expr = or();
        
        if(match(QUESTION_MARK)) {
            Expr then = expression();
            consume(COLON, ErrorCode.MISSING_COLON);
            Expr other = expression();
            expr = node(new TernaryExpr(expr, then, other));
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
        
        while(match(XOR)) {
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
        
//        Expr expr = null;        
//        if(match(SIZEOF)) {
//            expr = sizeofExpr();
//        }
//        if(match(TYPEOF)) {
//            expr = typeofExpr();
//        }
//        else if(expr == null) {
//            expr = functionCall();    
//        }
//        
//        if(match(AS)) {
//            expr = cast(expr);
//        }
//        
//        return expr;
        return functionCall();
    }
    
    private Expr cast(Expr expr) {
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        TypeSpec castTo = type(false);
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return node(new CastExpr(castTo, expr));
    }
    
    private Expr sizeofExpr() {
        boolean hasParen = match(TokenType.LEFT_PAREN);
        
        Expr expr = null;
        TypeSpec type = null;
        
        int backtrack = this.current;
        
        if(hasParen) {            
            type = type();
            
            // check to see if this is an enum type (or a field expression)
            if(check(DOT)) {
                this.current = backtrack;
                expr = unary();
            }
            else {
                expr = new TypeIdentifierExpr(type.as());
            }
        }
        else {
            expr = unary();
        }
                
        if(hasParen) {
            consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        }
        

        return node(new SizeOfExpr(expr));        
    }
    
    private Expr typeofExpr() {
        boolean hasParen = match(TokenType.LEFT_PAREN);
        
        Expr expr = null;
        TypeSpec type = null;
        
        Token t = peek();        
        if(t.getType().isPrimitiveToken() || t.getType().equals(LEFT_BRACKET)) {            
            type = type();            
        }
        else {
            expr = expression();
        }
                
        if(hasParen) {
            consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        }
        
        if(expr != null) {
            return node(new TypeOfExpr(expr));
        }
        
        return node(new TypeOfExpr(type));
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
                NameTypeSpec identifier = identifierType(true);
                expr = node(new GetExpr(expr, node(new IdentifierExpr(identifier))));
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
        if(match(SIZEOF))       return sizeofExpr();
        if(match(TYPEOF))       return typeofExpr();
        
        if(check(IDENTIFIER)) {
            NameTypeSpec name = identifierType(true);
            return node(new IdentifierExpr(name));
        }
                
        throw error(peek(), ErrorCode.UNEXPECTED_TOKEN);
    }     
    
    private Expr finishFunctionCall(Expr callee) {
        List<TypeSpec> genericArgs = Collections.emptyList();
        
        List<Expr> arguments = arguments();
        
        // Convert the IdentifierExpr to a FuncIndentiferExpr
        if(callee instanceof IdentifierExpr) {
            IdentifierExpr idExpr = (IdentifierExpr)callee;            
            callee = node(new FuncIdentifierExpr(idExpr.type));
        }
        else if(callee instanceof GetExpr) {
            GetExpr getExpr = (GetExpr)callee;
            if(getExpr.field instanceof IdentifierExpr) {
                IdentifierExpr idExpr = (IdentifierExpr)getExpr.field;
                genericArgs = idExpr.genericArgs;    
            }
            
            IdentifierExpr newId = node(new FuncIdentifierExpr(getExpr.field.type));
            getExpr.setField(newId);
        }
            
        
        return node(new FuncCallExpr(callee, arguments, genericArgs));
    }
    
    private Expr groupExpr() {
        Expr expr = expression();
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return node(new GroupExpr(expr));
    }
    
    private void eatSemicolon() {
        match(SEMICOLON);
    }
    
    private List<TypeSpec> tryGenericArguments(boolean disambiguiate) {
        int backtrack = this.current;
        
        advance(); // eat <
        
        List<TypeSpec> arguments = null;
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
    
    private TypeSpec chainableType(TypeSpec type) {
        do {
            advance();
        
            Token n = peek();
            if(n.getType().equals(STAR)) {
                type = new PtrTypeSpec(pos(), type);
            }
            else if(n.getType().equals(LEFT_BRACKET)) {
                type = arrayType(type);
            }
            else if(n.getType().equals(CONST)) {
                if(type.kind == TypeSpecKind.CONST) {
                    throw error(n, ErrorCode.INVALID_CONST_EXPR);
                }
                
                type = new ConstTypeSpec(pos(), type);
            }
            else {
                break;
            }
        } while(!isAtEnd());
        
        return type;
    }
        
    private ArrayTypeSpec arrayType(TypeSpec type) {
        TypeSpec arrayOf = type;
        advance(); // eat [
        
        Expr lengthExpr = null;
        if(!peek().getType().equals(RIGHT_BRACKET)) {
            Expr expr = expression();
            
            if(expr instanceof NumberExpr) {
                lengthExpr = expr;
            }
            else if(expr instanceof CharExpr) {
                lengthExpr = expr;
            }
            else if(expr instanceof IdentifierExpr) {
                lengthExpr = expr;
            }
            else if(expr instanceof GetExpr) {
                lengthExpr = expr;
            }
            else {
                throw error(peek(), ErrorCode.INVALID_ARRAY_DIMENSION_EXPR);
            }            
        }
        
        return new ArrayTypeSpec(pos(), arrayOf, lengthExpr);                    
    }
    
    private int modifiers() {
        int modifiers = 0;
        do {
            if(match(USING)) {
                modifiers |= Attributes.USING_MODIFIER;
            }
//            else if(match(CONST)) {
//                modifiers |= Attributes.CONST_MODIFIER;
//            }
            else {
                break;
            }
        }
        while(isAtEnd());
        
        return modifiers;
    }
    
    private TypeSpec type() {
        return type(true);
    }
        
    private TypeSpec type(boolean disambiguiate) {
        Token t = peek();
        switch(t.getType()) {
            case BOOL: 
            case CHAR: 
            case I8: 
            case U8: 
            case I16: 
            case U16: 
            case I32: 
            case U32: 
            case I64: 
            case U64: 
            case F32: 
            case F64: 
            case VOID: {
                TypeSpec type = new NameTypeSpec(t.getPos(), t.getText());
                return chainableType(type);
            }
            case IDENTIFIER: {
                TypeSpec type = identifierType(disambiguiate);

                // identifier() consumes multiple tokens, 
                // account for advance in ptr check
                rewind();
                return chainableType(type);
            }
            case LEFT_BRACKET: {                
                ArrayTypeSpec type = arrayType(null);
                advance();
                
                type.base = type(disambiguiate);
                return type;
            }            
            case FUNC: {
                advance();                
                return funcPtrType();
            }
            default:
                throw error(t, ErrorCode.UNEXPECTED_TOKEN);
        }
    }
    
    private FuncPtrTypeSpec funcPtrType() {
        Token token = peek();
        
        List<GenericParam> genericParams = Collections.emptyList();
        if(match(LESS_THAN)) {
            genericParams = genericParameters();
        }
        
        consume(LEFT_PAREN, ErrorCode.MISSING_LEFT_PAREN);
        List<TypeSpec> params = new ArrayList<>();
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
        
        TypeSpec returnType = type(false);
        
        return new FuncPtrTypeSpec(token.getPos(), params, returnType, isVarargs, genericParams);
    }
    
    private NameTypeSpec identifierType(boolean disambiguate) {
        Token token = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        String identifier = token.getText();
        
        // imported module type
        if(match(COLON_COLON)) {            
            Token name = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);            
            identifier = String.format("%s::%s", identifier, name.getText());
        }
        
        List<TypeSpec> arguments = null;
        if(check(LESS_THAN)) {
            arguments = tryGenericArguments(disambiguate);
        }
        
        if(arguments == null) {
            arguments = Collections.emptyList();
        }
        
        return new NameTypeSpec(token.getPos(), identifier, arguments);
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
                    ParameterDecl param = parameterDecl(true);
                    parameterInfos.add(param);
                }
            }
            while(match(COMMA));
        }
        
        consume(RIGHT_PAREN, ErrorCode.MISSING_RIGHT_PAREN);
        return node(new ParametersStmt(parameterInfos, isVarargs));
    }
    
    private ParameterDecl parameterDecl(boolean allowEquals) {
        Token param = consume(IDENTIFIER, ErrorCode.MISSING_IDENTIFIER);
        String parameterName = param.getText();
        
        consume(COLON, ErrorCode.MISSING_COLON);
        int modifiers = modifiers();
        TypeSpec type = type(false);
        
        Expr defaultValue = null;
        if(match(EQUALS)) {
            defaultValue = constExpression();
        }
        
        return (node(new ParameterDecl(type, parameterName, defaultValue, modifiers)));
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
    
    private List<TypeSpec> genericArguments() {
        List<TypeSpec> arguments = new ArrayList<>();
        if(!check(GREATER_THAN)) {        
            do {
                arguments.add(type(false));
            } 
            while(match(COMMA));            
        }
        
        consume(GREATER_THAN, ErrorCode.MISSING_GENERIC_END);
        
        return arguments;
    }

    private List<Expr> arrayArguments() {
        List<Expr> arguments = new ArrayList<>();
        do {
            if(check(RIGHT_BRACE)) {   
                break;
            }
            Expr expr = null;
            if(check(LEFT_BRACKET)) {
                expr = tryArrayDesignationExpr();
            }
            
            if(expr == null) {
                expr = expression();
            }
            arguments.add(expr);
        } 
        while(match(COMMA));            
        
        consume(RIGHT_BRACE, ErrorCode.MISSING_RIGHT_BRACE);
        
        return arguments;
    }
    
    private Expr tryArrayDesignationExpr() {
        int backtrack = this.current;
        
        Expr designator = null;
        try {
            if(match(LEFT_BRACKET)) {
                Expr index = expression();
                consume(RIGHT_BRACKET, ErrorCode.MISSING_RIGHT_BRACKET);
                consume(EQUALS, ErrorCode.MISSING_EQUALS);
                Expr value = expression();
                designator = node(new ArrayDesignationExpr(index, value));
            }
            
        }
        catch(ParseException e) {
            this.current = backtrack;
        }
        
        return designator;
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
                if(check(COLON)) {
                    consume(COLON, ErrorCode.MISSING_COLON);
                }
                else if(check(EQUALS)) {
                    consume(EQUALS, ErrorCode.MISSING_EQUALS);
                }
                else {
                    throw error(peek(), ErrorCode.MISSING_COLON);
                }
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
    
    private SrcPos pos() {
        if(this.startToken != null) {
            source();
        }
        
        return this.startToken.getPos();
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
