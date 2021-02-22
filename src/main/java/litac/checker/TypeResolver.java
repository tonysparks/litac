/*
 * see license.txt
 */
package litac.checker;

import java.math.BigDecimal;
import java.util.*;

import litac.LitaOptions;
import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node.SrcPos;
import litac.ast.Node.Identifier;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo.*;
import litac.compiler.*;
import litac.compiler.FieldPath.FieldPathNode;
import litac.compiler.Symbol.*;
import litac.generics.*;
import litac.parser.tokens.TokenType;
import litac.util.*;
import litac.util.Stack;
import litac.compiler.Module;

/**
 * @author antho
 *
 */
public class TypeResolver {

    private class TypeCheckException extends RuntimeException {        
        private static final long serialVersionUID = 4636780657825316494L;
        public SrcPos pos;
        public TypeCheckException(SrcPos pos, String message, Object ... args) {
            super(String.format(message, args));
            this.pos = pos;
        }
    }
    
    public static class Operand {
        public TypeInfo type;
        public boolean isLeftValue;
        public boolean isConst;
        public Object val;
        
        public static Operand op(Operand other) {
            Operand op = new Operand();
            op.type = other.type;
            op.isConst = other.isConst;
            
            return op;
        }
        
        public static Operand op(TypeInfo type) {
            Operand op = new Operand();
            op.type = type;
            return op;
        }
                
        public static Operand opConst(TypeInfo type, Object val) {
            Operand op = new Operand();
            op.type = type;            
            op.isConst = true;
            op.val = val;
            return op;
        }
        
        public static Operand opRight(TypeInfo type, Object val) {
            Operand op = new Operand();
            op.type = type;            
            op.isLeftValue = false;
            op.val = val;
            return op;
        }
        
        public static Operand opLeft(TypeInfo type, Object val) {
            Operand op = new Operand();
            op.type = type;            
            op.isLeftValue = true;
            op.val = val;
            return op;
        }
    }
    
    private CompilationUnit unit;
    private PhaseResult result;
    private Map<ModuleId, Module> resolvedModules;
    private Map<String, Boolean> labels;
    
    private List<Symbol> moduleSymbols;
    private List<Symbol> moduleFuncs;
    private Stack<Tuple<Map<TypeSpec, Module>, Module>> currentModule;
    private Module root;
    
    // Types pending to be completed
    private List<Symbol> programSymbols;
    private List<Symbol> pendingTypes;
    private List<Symbol> pendingValues;
    private Map<String, Symbol> genericTypes;
    
    private Map<TypeSpec, TypeInfo> resolvedTypeMap;
    private NodeVisitor stmtVisitor;
    
    private Stack<List<GenericParam>> genericStack;
        
    private FuncTypeInfo currentFunc;
    
    private Preprocessor preprocessor;
    
    public TypeResolver(LitaOptions options,
                        PhaseResult result, 
                        CompilationUnit unit) {
        
        this.preprocessor = options.preprocessor();
        this.result = result;
        this.unit = unit;
        
        this.programSymbols = new ArrayList<>();
        this.moduleSymbols = new ArrayList<>();
        this.moduleFuncs = new ArrayList<>();
        this.pendingTypes = new ArrayList<>();
        this.pendingValues = new ArrayList<>();
        this.genericTypes = new HashMap<>();
        
        this.resolvedModules = new HashMap<>();
        this.resolvedTypeMap = new IdentityHashMap<>();
        
        this.labels = new HashMap<>();
        
        this.genericStack = new Stack<>();
        this.currentModule = new Stack<>();
        
        this.stmtVisitor = new StmtVisitor();
    }

    public Program resolveTypes() {
        Module module = resolveMainModule(this.unit.getMain());                
        return new Program(module, 
                           resolvedModules, 
                           module.getSymbols(), 
                           this.resolvedTypeMap);
    }
    
    public void resolveModuleTypes(Module module) {
        resolveModule(module);
        finishResolveModule(module);
    }
        
    private void error(Stmt stmt, String message, Object ... args) {        
        throw new TypeCheckException(stmt != null ? stmt.getSrcPos() : null, message, args);        
    }
    
    private void error(SrcPos pos, String message, Object ... args) {        
        throw new TypeCheckException(pos, message, args);
    }
    
    private void addError(Stmt stmt, String message, Object ... args) {
        this.result.addError(stmt != null ? stmt.getSrcPos() : null, message, args);
    }
        
    private Module resolveMainModule(ModuleStmt moduleStmt) {
        this.root = createModule(moduleStmt);
        return finishResolveModule(this.root);
    }
    
    private Module finishResolveModule(Module module) {                
        enterModule(module);
        
        List<Symbol> syms = null;
        do {
            syms = new ArrayList<>(this.moduleSymbols);
            this.moduleSymbols.clear();
            
            for(Symbol sym : syms) {
                tryResolveSym(sym);
            }
                        
            for(Symbol sym : syms) {
                tryFinishResolveSym(sym);
            }
        }
        while(!syms.isEmpty());

        // populate function declaration types
        for(Symbol sym : this.moduleFuncs) {                
            tryResolveSym(sym);
        }
        
        for(Symbol sym : this.pendingValues) {
            tryResolveValue(sym);
        }
        
        
        for(Symbol sym : this.moduleFuncs) {
            if(sym.isGenericTemplate()) {
                continue;
            }
            
            tryResolveFunc(sym);
        }
        
        Set<Symbol> processedSyms = new HashSet<>();
        
        // add all of the generic types
        do {
            Collection<Symbol> gens = new ArrayList<>(this.genericTypes.values());
            gens.removeAll(processedSyms);
            processedSyms.addAll(this.genericTypes.values());                
            
            if(gens.isEmpty()) {
                break;
            }
            
            for(Symbol sym : gens) {
                if(sym.isGenericTemplate()) {
                    continue;
                }
                
                if(sym.kind == SymbolKind.FUNC) {
                    tryResolveFunc(sym);
                }
                else {
                    tryFinishResolveSym(sym);
                }
                
                module.getModuleStmt().declarations.add(sym.decl);
            }
            
        } 
        while(true);
                
        return module;
    }
    
    private void tryResolveFunc(Symbol sym) {
        try {
            resolveFunc(sym);
        }
        catch(TypeCheckException e) {
            this.result.addError(e.pos, e.getMessage());
        }
    }
    
    private void resolveFunc(Symbol sym) {
        if(sym.decl.kind != DeclKind.FUNC) {
            return;
        }
        
        resolveSym(sym);
        
        enterModuleFor(sym);
        try {
            FuncDecl funcDecl = (FuncDecl) sym.decl;
            if(funcDecl.hasGenericParams()) {
                return;
            }
            
            sym.markAsComplete();
            
            funcDecl.visit(this.stmtVisitor);
        }
        finally {
            leaveModule();
        }
    }
    
    private Module getModuleFor(NameTypeSpec typeSpec) {
        Symbol sym = current().getType(typeSpec.name);
        if(sym != null) {
            return current();
        }
        
        Map<TypeSpec, Module> genericMap = this.currentModule.peek().getFirst();
        if(genericMap == null) {
            return null;
        }
        
        Module module = genericMap.get(typeSpec);
        if(module != null) {
            return module;
        }
        
        return null;
    }
    
    private void enterModuleFor(Symbol sym) {        
        this.currentModule.add(new Tuple<>(sym.genericMap, sym.getDeclaredModule()));        
    }
    
    private void enterModule(Module module) {        
        this.currentModule.add(new Tuple<>(null, module));
    }
    
    private void leaveModule() {
        this.currentModule.pop();
    }
    
    private Module current() {
        return this.currentModule.peek().getSecond();
    }
    
    private Symbol getType(NameTypeSpec typeSpec) {
        Symbol sym = current().getType(typeSpec.name);
        if(sym != null) {
            return sym;
        }
        
        Map<TypeSpec, Module> genericMap = this.currentModule.peek().getFirst();
        if(genericMap == null) {
            return null;
        }
        
        Module module = genericMap.get(typeSpec);
        if(module != null) {
            return module.getType(typeSpec.name);
        }
        
        return null;
    }
    
    private void addBuiltins(Module module) {
        module.addBuiltin(TypeInfo.BOOL_TYPE);
        module.addBuiltin(TypeInfo.CHAR_TYPE);
        module.addBuiltin(TypeInfo.F32_TYPE);
        module.addBuiltin(TypeInfo.F64_TYPE);
        module.addBuiltin(TypeInfo.I8_TYPE);
        module.addBuiltin(TypeInfo.I16_TYPE);
        module.addBuiltin(TypeInfo.I32_TYPE);
        module.addBuiltin(TypeInfo.I64_TYPE);
        module.addBuiltin(TypeInfo.U8_TYPE);
        module.addBuiltin(TypeInfo.U16_TYPE);
        module.addBuiltin(TypeInfo.U32_TYPE);
        module.addBuiltin(TypeInfo.U64_TYPE);
        module.addBuiltin(TypeInfo.USIZE_TYPE);
        module.addBuiltin(TypeInfo.NULL_TYPE);
        module.addBuiltin(TypeInfo.VOID_TYPE);
    }
    
    private Module createModule(ModuleStmt moduleStmt) {
        ModuleId moduleId = moduleStmt.id;
        
        if(resolvedModules.containsKey(moduleId)) {
            return resolvedModules.get(moduleId);
        }
        
        Module module = new Module(this.root, 
                                   this.programSymbols, 
                                   this.genericTypes, 
                                   this.result, 
                                   moduleStmt);
        addBuiltins(module);
        
        resolvedModules.put(moduleId, module);
        resolveModule(module);
        
        return module;
    }
    
    private void resolveModule(Module module) {
        ModuleStmt moduleStmt = module.getModuleStmt();        
        enterModule(module);
        
        List<Decl> nonGenericDecls = new ArrayList<>();
        
        for(Decl decl : moduleStmt.declarations) {
            tryResolveDeclaration(decl, module, nonGenericDecls);
        }      
        
        // we don't want the upstream systems to deal with the Generic types
        // they are resolved with proper substituted types
        moduleStmt.declarations = nonGenericDecls;        
        
        for(ImportStmt imp : moduleStmt.imports) {            
            tryResolveImport(module, imp);            
        }   
        
        leaveModule();
    }
    
    private void tryResolveDeclaration(Decl decl, Module module, List<Decl> nonGenericDecls) {
        try {
            resolveDeclaration(decl, module, nonGenericDecls);
        }
        catch(TypeCheckException e) {
            this.result.addError(e.pos, e.getMessage());
        }
    }
    
    private void resolveDeclaration(Decl decl, Module module, List<Decl> nonGenericDecls) {
        switch(decl.kind) {
            case CONST:  {
                Symbol sym = module.addIncomplete(decl);
                if(!checkResolveValue(sym)) {
                    this.pendingValues.add(sym);
                }
                nonGenericDecls.add(decl);
                break;
            }
            case VAR: {
                Symbol sym = module.addIncomplete(decl);
                this.pendingValues.add(sym);
                nonGenericDecls.add(decl);
                break;
            }
            case FUNC: {
                FuncDecl funcDecl = (FuncDecl) decl;
                String funcName = funcDecl.name;
                if(funcDecl.isMethod()) {
                    funcName = Names.methodName(funcDecl.params.params.get(0).type, funcName);
                }
                Symbol sym = module.declareFunc(funcDecl, funcName);
                sym.maskAsIncomplete();
                
                sym.type = new FuncTypeInfo(funcName, null, new ArrayList<>(), funcDecl.flags, funcDecl.genericParams);
                sym.type.sym = sym;
                
                this.moduleFuncs.add(sym);
                
                if(!funcDecl.hasGenericParams()) {
                    nonGenericDecls.add(funcDecl);
                }
                else {
                    sym.markAsGenericTemplate();
                }
                
                break;
            }                
            case ENUM: {
                EnumDecl enumDecl = (EnumDecl) decl;
                Symbol sym = module.declareEnum(enumDecl, enumDecl.name);
                sym.maskAsIncomplete();
                
                this.moduleSymbols.add(sym);
                
                Symbol enumAsStrSym = createEnumAsStrFunc(enumDecl, sym);
                if(enumAsStrSym != null) {
                    this.moduleSymbols.add(enumAsStrSym);
                    nonGenericDecls.add(enumAsStrSym.decl);
                }
                
                nonGenericDecls.add(enumDecl);
                
                break;
            }
            case STRUCT: {
                StructDecl structDecl = (StructDecl) decl;
                Symbol sym = module.declareStruct(structDecl, structDecl.name);
                sym.maskAsIncomplete();
                
                this.moduleSymbols.add(sym);
                
                if(!structDecl.hasGenericParams()) {
                    nonGenericDecls.add(structDecl);
                }
                else {
                    sym.markAsGenericTemplate();
                }
                
                break;
            }
            case UNION: {
                UnionDecl unionDecl = (UnionDecl) decl;
                Symbol sym = module.declareUnion(unionDecl, unionDecl.name);
                sym.maskAsIncomplete();
                
                this.moduleSymbols.add(sym);
                
                if(!unionDecl.hasGenericParams()) {
                    nonGenericDecls.add(unionDecl);
                }
                else {
                    sym.markAsGenericTemplate();
                }
                
                break;
            }
            case TYPEDEF: {
                TypedefDecl typeDecl = (TypedefDecl) decl;
                Symbol sym = module.declareTypedef(typeDecl, typeDecl.alias);
                
                this.moduleSymbols.add(sym);
                nonGenericDecls.add(typeDecl);
                break;
            }
            default:
                break;
        }        
    }
    
    private void tryResolveImport(Module parentModule, ImportStmt importStmt) {
        try {
            resolveImport(parentModule, importStmt);
        }
        catch(TypeCheckException e) {
            this.result.addError(e.pos, e.getMessage());
        }
    }
    
    private void resolveImport(Module parentModule, ImportStmt importStmt) {
        
        String importName = importStmt.getImportName();
        if(parentModule.getModule(importName) != null) {
            error(importStmt, "duplicate import of module '%s'", importName); 
            return;
        }
        
        ModuleStmt moduleStmt = unit.getModule(importStmt.moduleId);
        if(moduleStmt == null) {
            error(importStmt.getSrcPos(), "Could not find module name: '%s'", importStmt.moduleId.id);
        }
        
        Module module = createModule(moduleStmt);
        parentModule.importModule(importStmt, module, importStmt.alias);
    }
    
    private void tryResolveSym(Symbol sym) {
        try {
            resolveSym(sym);
        }
        catch(TypeCheckException e) {
            this.result.addError(e.pos, e.getMessage());
        }
    }
    
    private void resolveSym(Symbol sym) {
        if(sym.state == ResolveState.RESOLVED) {
            return;
        }
        
        if(sym.state == ResolveState.RESOLVING) {
            error(sym.decl.getSrcPos(), "Cyclic dependency with '%s'", sym.name);
            return;
        }
        
        sym.state = ResolveState.RESOLVING;
        enterModuleFor(sym);
        try {
            switch(sym.kind) {
                case TYPE:
                    switch(sym.decl.kind) {
                        case ENUM:
                            EnumDecl enumDecl = (EnumDecl)sym.decl;
                            sym.type = enumTypeInfo(enumDecl);     
                            sym.type.sym = sym;
                            // required to avoid cyclic
                            sym.state = ResolveState.RESOLVED; 
                            
                            // Done during declaration resolution (so that public types can
                            // be properly imported to other modules.
                            // However, we need to check and see if we need to do this again
                            // for enum declared in structs/union because those are NOT
                            // part of module declarations.
                            Symbol enumAsStrSym = createEnumAsStrFunc(enumDecl, sym);
                            if(enumAsStrSym != null) {
                                resolveSym(enumAsStrSym);
                            }
                            break;
                        case TYPEDEF:
                            TypedefDecl typedefDecl = (TypedefDecl) sym.decl;
                            if(typedefDecl.hasGenericParams()) {
                                this.genericStack.add(typedefDecl.genericParams);
                            }
                            
                            TypeInfo aliasedType = resolveTypeSpec(typedefDecl.type);
                            
                            // Allow foreign types to be aliased and referenced
                            // in our type system                            
                            if(typedefDecl.attributes.isForeign() && aliasedType.isKind(TypeKind.Void)) {            
                                aliasedType = TypeInfo.newForeignPrimitive(typedefDecl.alias);
                                aliasedType.sym = sym;
                            }
                            
                            sym.type = aliasedType;
                            
                            if(typedefDecl.hasGenericParams()) {
                                this.genericStack.pop();
                            }
                            break;
                        case STRUCT:                        
                            StructDecl structDecl = (StructDecl) sym.decl;                        
                            sym.type = new StructTypeInfo(sym.decl.name, structDecl.genericParams, new ArrayList<>(), structDecl.flags);
                            sym.type.sym = sym;
                            this.pendingTypes.add(sym);
                            break;
                        case UNION:
                            UnionDecl unionDecl = (UnionDecl) sym.decl;
                            sym.type = new UnionTypeInfo(sym.decl.name, unionDecl.genericParams, new ArrayList<>(), unionDecl.flags);
                            sym.type.sym = sym;
                            this.pendingTypes.add(sym);
                            break;
                        default:                        
                    }
                    break;
                case FUNC: {
                    FuncDecl funcDecl = (FuncDecl) sym.decl;
                    sym.type = funcTypeInfo(funcDecl, sym);
                    sym.type.sym = sym;
                    break;
                }
                case CONST:
                case VAR: {         
                    if(!sym.isLocal()) {
                        this.pendingValues.add(sym);
                        return;
                    }
                    break;
                }
                default:
                    break;
            
            }        
            sym.state = ResolveState.RESOLVED;
        }
        finally {
            leaveModule();
        }
    }        
    
    private Symbol createEnumAsStrFunc(EnumDecl enumDecl, Symbol enumSym) {
        NoteStmt asStr = enumDecl.attributes.getNote("asStr");
        if(asStr == null) {
            return null;
        }
        
        String funcName = asStr.getAttr(0, enumDecl.name + "AsStr");
        FuncTypeInfo asStrFuncInfo = new FuncTypeInfo(funcName, 
                                                      new PtrTypeInfo(new ConstTypeInfo(TypeInfo.CHAR_TYPE)), 
                                                      new ArrayList<>(), 
                                                      0, 
                                                      Collections.emptyList());
        
        ParameterDecl param = new ParameterDecl(new NameTypeSpec(enumDecl.getSrcPos(), enumDecl.name), new Identifier("e"), null, 0);
        FuncDecl funcDecl = new FuncDecl(new Identifier(asStrFuncInfo.name), 
                                         new ParametersStmt(Arrays.asList(param), false),   
                                         new EmptyStmt(),
                                         asStrFuncInfo.returnType.asTypeSpec(), 
                                         Collections.emptyList(), 0).
                                         setSrcPos(asStr.getSrcPos(), asStr.getEndSrcPos());
        
        // Name must match CGen.visit(EnumDecl)
        funcDecl.attributes.addNote(new NoteStmt("foreign", Arrays.asList("__" + current().simpleName() + "_" + enumDecl.name + "_AsStr")));
        funcDecl.attributes.isGlobal = enumSym.decl.attributes.isGlobal;
        funcDecl.attributes.isPublic = enumSym.decl.attributes.isPublic;
        Module module = current();
        
        // this was already created via a declaration
        if(module.getFuncType(asStrFuncInfo.name) != null && !enumSym.isLocal()) {
            return null;
        }
        
        Symbol sym = current().declareFunc(funcDecl, asStrFuncInfo.name);
        sym.type = asStrFuncInfo;
        sym.type.sym = sym;
        //sym.state = ResolveState.RESOLVED;
                
        return sym;       
    }
    
    /*
    private Symbol createEnumAsStrFunc(EnumDecl enumDecl, Symbol enumSym) {
        NoteStmt asStr = enumDecl.attributes.getNote("asStr");
        if(asStr == null) {
            return null;
        }
        
        String funcName = asStr.getAttr(0, enumDecl.name + "AsStr");
        FuncTypeInfo asStrFuncInfo = new FuncTypeInfo(funcName, 
                                                      new PtrTypeInfo(new ConstTypeInfo(TypeInfo.CHAR_TYPE)), 
                                                      Arrays.asList(new ParamInfo(enumSym.type, "e", null, new Attributes())), 
                                                      0, 
                                                      Collections.emptyList());
        
        ParameterDecl param = new ParameterDecl(enumSym.type.asTypeSpec(), "e", null, 0);
        FuncDecl funcDecl = new FuncDecl(asStrFuncInfo.name, 
                                         new ParametersStmt(Arrays.asList(param), false),   
                                         new EmptyStmt(),
                                         asStrFuncInfo.returnType.asTypeSpec(), 
                                         Collections.emptyList(), 0);
        
        // Name must match CGen.visit(EnumDecl)
        funcDecl.attributes.addNote(new NoteStmt("foreign", Arrays.asList("__" + current().name() + "_" + enumDecl.name + "_AsStr")));
        funcDecl.attributes.isGlobal = enumSym.decl.attributes.isGlobal;
        funcDecl.attributes.isPublic = enumSym.decl.attributes.isPublic;
        
        Symbol sym = current().declareFunc(funcDecl, asStrFuncInfo.name);
        sym.type = asStrFuncInfo;
        sym.type.sym = sym;
        sym.state = ResolveState.RESOLVED;
                
        return sym;       
    }*/
    
    private void tryTypeCheck(SrcPos pos, TypeInfo a, TypeInfo b) {
        try {
            typeCheck(pos, a, b);
        }
        catch(TypeCheckException e) {
            this.result.addError(pos, e.getMessage());
        }
    }
    
    private void typeCheck(SrcPos pos, TypeInfo a, TypeInfo b) {
        typeCheck(pos, a, b, false);
    }
    
    private void typeCheck(SrcPos pos, TypeInfo a, TypeInfo b, boolean isCasted) {
        if(isCasted) {
            if((!TypeInfo.isPtrLike(a) || !TypeInfo.isPtrLike(b)) && (!TypeInfo.isFunc(a) || !TypeInfo.isFunc(b))) {
                if(!a.canCastTo(b) && !b.canCastTo(a)) {
                    error(pos, "'%s' can't be casted to '%s'", b, a);
                }
            }
        }
        else if(!a.canCastTo(b)) {
            error(pos, "'%s' is not of type '%s'", b, a);
        }
    }
    
    private boolean checkResolveValue(Symbol sym) {
        try {
            resolveValue(sym);
            return true;
        }
        catch(TypeCheckException | NullPointerException e ) {
            return false;
        }
    }
    
    private void tryResolveValue(Symbol sym) {
        try {
            resolveValue(sym);
        }
        catch(TypeCheckException e) {
            this.result.addError(e.pos, e.getMessage());
        }
    }
    
    private void resolveValue(Symbol sym) {
        if(sym.kind != SymbolKind.CONST && sym.kind != SymbolKind.VAR) {            
            return;
        }
        
        enterModuleFor(sym);
        try {
            Decl decl = sym.decl;
            switch(sym.kind) {
                case CONST: {
                    ConstDecl constDecl = (ConstDecl) decl;
                    sym.type = resolveValueDecl(decl, constDecl.type, constDecl.expr, !sym.isForeign());
                    sym.state = ResolveState.RESOLVED;
                    sym.markAsComplete();
                    break;
                }
                case VAR: {
                    VarDecl varDecl = (VarDecl) decl;
                    sym.type = resolveValueDecl(decl, varDecl.type, varDecl.expr, false);
                    sym.state = ResolveState.RESOLVED;
                    sym.markAsComplete();
                    break;
                }
                default:
            }    
        }
        finally {
            leaveModule();
        }
    }
    
    private TypeInfo resolveValueDecl(Decl decl, TypeSpec typeSpec, Expr expr, boolean isConstant) {
        if(expr == null && isConstant) {
            error(decl.getSrcPos(), "const declaration must have an assignment (unless it is '@foreign')");
            return null;
        }
        
        TypeInfo declaredType = null;
        if(typeSpec != null) {
            declaredType = resolveTypeSpec(typeSpec);
        }

        TypeInfo inferredType = null;
        if(expr != null) {
            Operand op = resolveExpr(expr);
            if(op != null) {
                if(expr.getKind() == ExprKind.ARRAY_INIT ||
                   expr.getKind() == ExprKind.INIT) {
                    inferredType = op.type;
                }
                else if(op.type.isKind(TypeKind.Null) && declaredType == null) {
                    error(decl.getSrcPos(), "invalid variable declaration, can't infer type from 'null' for '%s' variable", decl.name);
                }
                else {
                    inferredType = decayType(op.type);    
                }                
            }
                        
            expr.expectedType = declaredType != null ? declaredType : inferredType;
        }
        
        if(declaredType != null && inferredType != null) {            
            typeCheck(expr.getSrcPos(), inferredType, declaredType);
        }
                
        return declaredType != null ? declaredType : inferredType;
        
    }
    
    /**
     * Types when assigned must decay to their assigned type,
     * such as arrays to pointers and functions to function pointers
     * 
     * @param type
     * @return the assignable type info
     */
    private TypeInfo decayType(TypeInfo type) {        
        switch(type.kind) {
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();
                PtrTypeInfo ptrInfo = new PtrTypeInfo(decayType(arrayInfo.arrayOf));
                return ptrInfo;
            }
            case Func: {
                FuncTypeInfo funcInfo = type.as();
                return funcInfo.asPtr();
            }
            default: {
                return type;
            }
        }
    }
    
    private void tryFinishResolveSym(Symbol sym) {
        try {
            finishResolveSym(sym);
        }
        catch(TypeCheckException e) {
            this.result.addError(e.pos, e.getMessage());
        }
    }
    
    private void finishResolveSym(Symbol sym) {
        if(sym.isComplete()) {                    
            return;
        }
        
        enterModuleFor(sym);
        switch(sym.decl.kind) {
            case STRUCT:
            case UNION: {
                AggregateDecl aggDecl = (AggregateDecl) sym.decl;
                if(aggDecl.hasGenericParams()) {
                    this.genericStack.add(aggDecl.genericParams);
                }
                
                AggregateTypeInfo aggInfo = sym.type.as();        
                for(FieldStmt field : aggDecl.fields) {
                    FieldInfo info = resolveFieldInfo(sym, field);
                    aggInfo.addField(info);
                }        
                
                sym.decl.visit(stmtVisitor);
                
                if(aggDecl.hasGenericParams()) {
                    this.genericStack.pop();
                }
                
                sym.markAsComplete();                
                break;
            }
            case ENUM: {
                sym.decl.visit(stmtVisitor);
                sym.markAsComplete();
                break;
            }
            case TYPEDEF: {
                sym.decl.visit(stmtVisitor);
                sym.markAsComplete();
                break;
            }
            default:
                /* funcs/vars/const must be handled after other declarations 
                 */
        }
        leaveModule();
    }
    
    private NameTypeSpec asTypeSpec(AggregateDecl decl, 
                                    List<TypeInfo> genericArgs,
                                    List<GenericParam> parentGenericParams) {
        List<TypeSpec> args = new ArrayList<>();
        for(GenericParam p : decl.genericParams) {
            for(int i = 0; i < parentGenericParams.size(); i++) {
                GenericParam parentParam = parentGenericParams.get(i);
                if(parentParam.name.equals(p.name)) {
                    args.add(genericArgs.get(i).asTypeSpec());
                    break;
                }
            }
        }
        return new NameTypeSpec(decl.getSrcPos(), decl.name, args);
    }
    
    private FieldInfo resolveFieldInfo(Symbol parentSym, FieldStmt stmt) {
        Module module = parentSym.getDeclaredModule();
        
        if (stmt instanceof VarFieldStmt) {
            VarFieldStmt var = (VarFieldStmt) stmt;
            TypeInfo type = resolveTypeSpec(var.type);
            
            if(var.defaultExpr != null) {
                Operand op = resolveExpr(var.defaultExpr);
                typeCheck(var.defaultExpr.getSrcPos(), op.type, type);
            }
            
            return new FieldInfo(type, var.fieldName.identifier, var.attributes, null);
        }
        
        if (stmt instanceof StructFieldStmt) {
            TypeInfo type = null;
            
            StructFieldStmt struct = (StructFieldStmt) stmt;
            if(parentSym.isFromGenericTemplate()) {
                Symbol sym = module.getType(struct.decl.name);
                type = createTypeFromGenericTemplate(sym, asTypeSpec(struct.decl, parentSym.genericArgs, parentSym.genericParams));
                struct.decl.sym = type.sym;
            }
            else {
                Symbol sym = module.declareStruct(struct.decl, struct.decl.name);
                sym.maskAsIncomplete();                       
                this.moduleSymbols.add(sym);
                
                resolveSym(sym);
                finishResolveSym(sym);
    
                type = sym.type;
            }
            
            return new FieldInfo(type, struct.decl.name, struct.decl.attributes, null);
        }
        
        if (stmt instanceof UnionFieldStmt) {
            TypeInfo type = null;
            
            UnionFieldStmt union = (UnionFieldStmt) stmt;
            if(parentSym.isFromGenericTemplate()) {
                Symbol sym = module.getType(union.decl.name);
                type = createTypeFromGenericTemplate(sym, asTypeSpec(union.decl, parentSym.genericArgs, parentSym.genericParams));
                union.decl.sym = type.sym;
            }
            else {            
                Symbol sym = module.declareUnion(union.decl, union.decl.name);
                sym.maskAsIncomplete();                       
                this.moduleSymbols.add(sym);
                
                resolveSym(sym);
                finishResolveSym(sym);
    
                type = sym.type;
            }
            
            return new FieldInfo(type, union.decl.name, union.decl.attributes, null);
        }
        
        if (stmt instanceof EnumFieldStmt) {
            EnumFieldStmt enm = (EnumFieldStmt) stmt;
            Symbol sym = module.declareEnum(enm.decl, enm.decl.name);
            this.moduleSymbols.add(sym);
            
            resolveSym(sym);
            finishResolveSym(sym);
            return new FieldInfo(sym.type, enm.decl.name, enm.decl.attributes, null);
        }
        
        error(stmt.getSrcPos(), "Unknown field item declaration");
        return null;
    }
    
    private boolean hasGenericParam(List<TypeSpec> genArgs) {
        for(TypeSpec type : genArgs) {
            NameTypeSpec name = TypeSpec.getBaseType(type);
            if(name != null) {
                if(isGenericParam(name) != null) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private TypeInfo createTypeFromGenericTemplate(Symbol sym, NameTypeSpec nameSpec) {
        List<TypeSpec> genArgs = nameSpec.genericArgs;                
        String genericsName = nameSpec.toGenericsName();
        
        Symbol genericSym = this.genericTypes.get(genericsName);                
        if(genericSym == null) {
            // Duplicate the Decl with the substituted generic arguments
            switch(sym.decl.kind) {
                case FUNC: {
                    FuncDecl newDecl = Generics.createFuncDecl((FuncDecl)sym.decl, genericsName, genArgs);
                    genericSym = this.root.declareFunc(newDecl, genericsName);
                    break;
                }
                case STRUCT: {
                    StructDecl newDecl = Generics.createStructDecl((StructDecl)sym.decl, genericsName, genArgs);
                    genericSym = this.root.declareStruct(newDecl, genericsName);
                    break;
                }
                case UNION: {
                    UnionDecl newDecl = Generics.createUnionDecl((UnionDecl)sym.decl, genericsName, genArgs);
                    genericSym = this.root.declareUnion(newDecl, genericsName);
                    break;
                }
                case TYPEDEF: {
                    TypedefDecl newDecl = Generics.createTypedefDecl((TypedefDecl)sym.decl, genericsName, genArgs);
                    genericSym = this.root.declareTypedef(newDecl, genericsName);
                    break;
                }
                default: {
                    error(nameSpec.pos, "'%s' can't contain a generic type", nameSpec.name);
                    return null;
                }
            }
            
            GenericDecl orgDecl = (GenericDecl)sym.decl;
            genericSym.callsiteModule = current();
            genericSym.genericDeclaration = sym.getDeclaredModule();
            genericSym.genericParams = new ArrayList<>(orgDecl.genericParams);
            genericSym.markFromGenericTemplate();
            genericSym.maskAsIncomplete();
            
            this.genericTypes.put(genericsName, genericSym);   
            
            if(hasGenericParam(genArgs)) {
                genericSym.markAsGenericTemplate();  
                
                // TODO: Move this logic into Generics.createDecl, we don't want to remove
                // the generic params if we still have them; this hack adds them back in
                GenericDecl genDecl = (GenericDecl)genericSym.decl;
                
                genDecl.genericParams = new ArrayList<>(orgDecl.genericParams);
            }
            //else 
            {                
                for(int i = 0; i < genArgs.size(); i++) {
                    TypeSpec arg = genArgs.get(i);
                    
                    TypeInfo type = resolveTypeSpec(arg);
                    NameTypeSpec typeName = TypeSpec.getBaseType(arg);
                    Module module = getModuleFor(typeName);
                    Symbol typedefSym = null;
                    if(module != null) {
                        typedefSym = module.getAliasedType(typeName.name);
                    }
                    genericSym.addModuleForType(arg, type, typedefSym); 
                    genericSym.genericArgs.add(type);
                }
            }
        }
        
        // create the TypeInfo's for this symbol
        resolveSym(genericSym);
        
        // resolve any embedded fields for aggregates 
        finishResolveSym(genericSym);
        
        this.resolvedTypeMap.put(nameSpec, genericSym.type);
        return genericSym.type;
    }
    
    private TypeInfo resolveTypeSpec(TypeSpec typeSpec) {
        if(typeSpec == null) {
            return TypeInfo.VOID_TYPE;
        }
        
        switch(typeSpec.kind) {
            case NAME: {
                NameTypeSpec nameSpec = typeSpec.as();
                GenericParam genParam = isGenericParam(nameSpec);
                if(genParam != null) {
                    return new GenericParamTypeInfo(genParam.name);
                }
                
                // This will check the current module AND if we are in a generic structure
                // we must also see if this type is mapped to a particular call site module
                Symbol sym = getType(nameSpec);                
                if(sym == null) {
                    // This may be a func pointer
                    sym = current().currentScope().getSymbol(nameSpec.name);

                    if(sym != null && sym.type != null && TypeInfo.isFunc(sym.type)) {                        
                        return sym.type;
                    }
                    
                    if(sym == null) {
                        error(typeSpec.pos, "Unknown type '%s'", nameSpec.name);
                        return null;
                    }
                    
                }
                
                if(!sym.isType()) {
                    error(typeSpec.pos, "'%s' must be a type", nameSpec.name);
                    return null;
                }
                
                resolveSym(sym);
                
                if(nameSpec.hasGenericArgs()) {
                    return createTypeFromGenericTemplate(sym, nameSpec);
                }
                                
                this.resolvedTypeMap.put(typeSpec, sym.type);
                return sym.type;
            }
            case ARRAY: {
                ArrayTypeSpec arraySpec = typeSpec.as();
                TypeInfo arrayOf = resolveTypeSpec(arraySpec.base);
                long length = -1;
                if(arraySpec.numElements != null) {
                    Operand operand = resolveConstExpr(arraySpec.numElements);
                    if(!TypeInfo.isInteger(operand.type)) {
                        error(arraySpec.numElements.getSrcPos(), "Array size expression must be an integer type");
                        return null;    
                    }
                    if(operand.val instanceof Number) {
                        length = ((BigDecimal)operand.val).longValue();
                    }
                    
                }
                TypeInfo type = new ArrayTypeInfo(arrayOf, length, arraySpec.numElements);
                this.resolvedTypeMap.put(typeSpec, type);
                return type;
            }
            case CONST: {
                ConstTypeSpec constSpec = typeSpec.as();
                TypeInfo constOf = resolveTypeSpec(constSpec.base);
                TypeInfo type = new ConstTypeInfo(constOf);
                this.resolvedTypeMap.put(typeSpec, type);
                return type;
            }
            case PTR: {
                PtrTypeSpec ptrSpec = typeSpec.as();
                TypeInfo ptrOf = resolveTypeSpec(ptrSpec.base);
                TypeInfo type = new PtrTypeInfo(ptrOf);
                this.resolvedTypeMap.put(typeSpec, type);
                return type;
            }
            case FUNC_PTR: {
                FuncPtrTypeSpec funcSpec = typeSpec.as();
                List<TypeInfo> args = new ArrayList<>();
                for(TypeSpec argSpec : funcSpec.args) {
                    TypeInfo arg = resolveTypeSpec(argSpec);
                    args.add(arg);
                }
                
                TypeInfo ret = resolveTypeSpec(funcSpec.ret);
                TypeInfo type = new FuncPtrTypeInfo(ret, args, funcSpec.hasVarargs, funcSpec.genericParams);
                this.resolvedTypeMap.put(typeSpec, type);
                return type;
            }
            default: {
                error(typeSpec.pos, "Unknown type specification");
                return null;
            }
        
        }
    }
    
    private TypeInfo funcTypeInfo(FuncDecl funcDecl, Symbol sym) {
        if(funcDecl.hasGenericParams()) {
            this.genericStack.add(funcDecl.genericParams);
        }
        
        enterModuleFor(sym);
        try {
            ParametersStmt params = funcDecl.params;
            
            List<ParamInfo> args = new ArrayList<>();
            for(ParameterDecl param : params.params) {            
                TypeInfo arg = resolveTypeSpec(param.type);                        
                args.add(new ParamInfo(arg, param.name, param.defaultValue, param.attributes));
            }
                    
            TypeInfo ret = resolveTypeSpec(funcDecl.returnType);
            ret = decayType(ret);        
            
            if(funcDecl.hasGenericParams()) {
                this.genericStack.pop();        
            }
        
            TypeInfo type = sym.type;
            if(type != null) {
                FuncTypeInfo funcInfo = type.as();
                funcInfo.name = funcDecl.name;
                funcInfo.returnType = ret;
                funcInfo.parameterDecls = args;
                funcInfo.flags = funcDecl.flags;
                funcInfo.genericParams = funcDecl.genericParams;
                
                return funcInfo;
            }
            
            return new FuncTypeInfo(funcDecl.name, ret, args, funcDecl.flags, funcDecl.genericParams);
        }
        finally {
            leaveModule();
        }
    }
            
    private TypeInfo enumTypeInfo(EnumDecl enumDecl) {
        List<EnumFieldInfo> enumFields = new ArrayList<>(enumDecl.fields.size());
        for(EnumFieldEntryStmt field : enumDecl.fields) {
            enumFields.add(new EnumFieldInfo(field.fieldName.identifier, field.value, field.attributes));
        }
        EnumTypeInfo enumInfo = new EnumTypeInfo(enumDecl.name, enumFields);
        return enumInfo;
    }
    
    private Operand resolveExpr(Expr expr) {
        switch(expr.getKind()) {
        case FUNC_IDENTIFIER: {
            FuncIdentifierExpr id = expr.as();
            return resolveFuncIdentifier(id);
        }
        case IDENTIFER: {
            IdentifierExpr id =  expr.as();
            return resolveIdentifier(id);
        }
        case TYPE_IDENTIFIER: {
            TypeIdentifierExpr id = expr.as();
            return resolveTypeIdentifier(id);
        }
        case BOOLEAN: {
            BooleanExpr b = expr.as();
            return b.getResolvedType();
        }
        case CHAR: {
            CharExpr c = expr.as();
            return c.getResolvedType();
        }
        case NULL: {
            NullExpr n = expr.as();
            return n.getResolvedType();
        }
        case NUMBER: {
            NumberExpr n = expr.as();
            return n.getResolvedType();
        }
        case STRING: {
            StringExpr s = expr.as();
            return s.getResolvedType();
        }
        case ARRAY_DESIGNATION: {
            ArrayDesignationExpr a = expr.as();
            return resolveArrayDesExpr(a);
        }            
        case ARRAY_INIT: {
            ArrayInitExpr a = expr.as();
            return resolveArrayInitExpr(a);
        }
        case BINARY: {
            BinaryExpr b = expr.as();
            return resolveBinaryExpr(b);
        }
        case CAST: {
            CastExpr c = expr.as();
            return resolveCastExpr(c);
        }
        case FUNC_CALL: {
            FuncCallExpr f = expr.as();
            return resolveFuncCallExpr(f);
        }
        case GET: {
            GetExpr g = expr.as();
            return resolveGetExpr(g);            
        }
        case GROUP: {
            GroupExpr g = expr.as();
            return resolveGroupExpr(g);
        }
        case INIT: {
            InitExpr i = expr.as();
            return resolveInitExpr(i);
        }
        case INIT_ARG: {
            InitArgExpr i = expr.as();
            return resolveInitArgExpr(i);
        }
        case SET: {
            SetExpr s = expr.as();
            return resolveSetExpr(s);
        }
        case SIZE_OF: {
            SizeOfExpr s = expr.as();
            return resolveSizeOfExpr(s);
        }
        case OFFSET_OF: {
            OffsetOfExpr s = expr.as();
            return resolveOffsetOfExpr(s);
        }
        case SUBSCRIPT_GET: {
            SubscriptGetExpr s = expr.as();
            return resolveSubGetExpr(s);
        }
        case SUBSCRIPT_SET: {
            SubscriptSetExpr s = expr.as();
            return resolveSubSetExpr(s);
        }
        case TERNARY: {
            TernaryExpr t = expr.as();
            return resolveTernaryExpr(t);
        }
        case TYPE_OF: {
            TypeOfExpr t = expr.as();
            return resolveTypeOfExpr(t);
        }
        case UNARY: {
            UnaryExpr u = expr.as();
            return resolveUnaryExpr(u);
        }
        default:
            break;
        
        }
        return null;
    }
    
    private Operand resolveFuncIdentifier(FuncIdentifierExpr expr) { 
        TypeInfo type = resolveTypeSpec(expr.type);
        if(type == null) {
            error(expr, "unknown function '%s'", expr.type.name);
            return null;
        }
        
        Symbol sym = type.sym;
        if(type.isKind(TypeKind.FuncPtr)) {
            Symbol funcSym = current().currentScope().getSymbol(expr.type.name);
            if(funcSym != null && funcSym.type != null && funcSym.type.isKind(TypeKind.FuncPtr)) {
                sym = funcSym;
            }
        }
        
        if(sym == null) {
            error(expr, "unknown function '%s'", expr.type.name);
            return null;
        }
        
        Operand op = Operand.op(sym.type);
        expr.resolveTo(op);
        expr.sym = sym;
        
        return op;
    }
    
    private Operand resolveIdentifier(IdentifierExpr expr) {              
        Symbol sym = current().currentScope().getSymbol(expr.type.name);
        if(sym == null) {
            error(expr, "unknown variable '%s'", expr.type.name);
            return null;
        }
        
        TypeInfo type = sym.type;
        if(sym.isGenericTemplate() && expr.type.hasGenericArgs()) {
            type = resolveTypeSpec(expr.type);
            if(type == null) {
                error(expr, "unknown type '%s'", expr.type);
                return null;
            }
            sym = type.sym;
        }
        
        Operand op = Operand.op(type);
        op.isConst = sym.isConstant();
//        if(op.isConst) {
//            ConstDecl
//        }
        
        expr.resolveTo(op);
        expr.sym = sym;
        
        return op;
    }
    
    private Operand resolveTypeIdentifier(TypeIdentifierExpr expr) {        
        TypeInfo type = resolveTypeSpec(expr.type);
        if(type == null) {
            error(expr, "unknown type '%s'", expr.type);
            return null;
        }
        
        Symbol sym = type.sym;
        Operand op = Operand.op(type);
        if(sym != null) {
            op.isConst = sym.isConstant();
        }
//        if(op.isConst) {
//            ConstDecl
//        }
        
        expr.resolveTo(op);
        expr.sym = sym;
        
        return op;
    }
    
    private Operand resolveUnaryExpr(UnaryExpr expr) {
        Operand op = resolveExpr(expr.expr);
        
        Operand operand = null;
        switch(expr.operator) {
            case STAR: {
                TypeInfo type = op.type;     
                
                if(type.isKind(TypeKind.Ptr)) {
                    PtrTypeInfo ptrInfo = type.as();
                    if(ptrInfo.ptrOf.isKind(TypeKind.Const)) {
                        ConstTypeInfo constInfo = ptrInfo.ptrOf.as();
                        operand = Operand.op(constInfo.constOf);                        
                    }
                    else {                    
                        operand = Operand.op(ptrInfo.ptrOf);
                    }
                    break;
                }
                else if(type.isKind(TypeKind.Str)) {
                    operand = Operand.op(TypeInfo.CHAR_TYPE);
                    break;
                }
                else if(type.isKind(TypeKind.Array)) {
                    ArrayTypeInfo arrayInfo = type.as();
                    if(arrayInfo.arrayOf.isKind(TypeKind.Const)) {
                        ConstTypeInfo constInfo = arrayInfo.arrayOf.as();                        
                        operand = Operand.op(constInfo.constOf);
                    }
                    else {
                        operand = Operand.op(arrayInfo.arrayOf);
                    }
                    break;
                }
                if(type.isKind(TypeKind.Const)) {
                    ConstTypeInfo constInfo = type.as();
                    if(TypeInfo.isPtrLike(constInfo.constOf)) {
                        operand = Operand.op(constInfo.constOf);
                        break;
                    }
                }
                
                error(expr, "'%s' is not a pointer type", type);
                break;
            }
            case BAND: {
                operand = op.type.isKind(TypeKind.Func) 
                            ? Operand.op(((FuncTypeInfo)op.type.as()).asPtr()) 
                            : Operand.op(new PtrTypeInfo(op.type));
                break;
            }
            case NOT: {
                operand = Operand.op(TypeInfo.BOOL_TYPE);
                break;
            }
            default: {                
                operand = Operand.op(op.type);
                operand.isConst = op.isConst;
            }
        }
        
        expr.resolveTo(operand);
        return operand;
    }
    
    private Operand resolveTernaryExpr(TernaryExpr c) {
        Operand cond = resolveExpr(c.cond);
        if(!TypeInfo.isBooleanable(cond.type)) {
            error(c.cond, "must be a boolean expression");
            return null;
        }
        
        Operand t = resolveExpr(c.then);
        Operand o = resolveExpr(c.other);
        
        typeCheck(c.getSrcPos(), t.type, o.type);
        
        Operand result = Operand.op(t.type);
        c.resolveTo(result);
        return result;
    }
    
    private Operand resolveSubSetExpr(SubscriptSetExpr c) {
        Operand objOp = resolveExpr(c.object);
        Operand indexOp = resolveExpr(c.index);
        Operand valOp = resolveExpr(c.value);
        
        
        TypeInfo objectInfo = objOp.type;
        TypeKind objectKind = objectInfo.getKind();
        switch(objectKind) {
            case Str: {
                objectInfo = TypeInfo.CHAR_TYPE;
                break;
            }
            case Array: {
                ArrayTypeInfo arrayInfo = objectInfo.as();
                objectInfo = arrayInfo.arrayOf;
                break;
            }
            case Ptr: {
                PtrTypeInfo ptrInfo = objectInfo.as();
                objectInfo = ptrInfo.ptrOf;
                break;
            }
            default: {
                error(c.object, "invalid index into '%s'", objectKind.name());
                return null;
            }
        }
        
        TypeKind indexKind = indexOp.type.getKind();
        switch(indexKind) {
            case i8:
            case u8:
            case i16:
            case u16:
            case i32:
            case u32:
            case i64:
            case u64:
            case Enum:
            case usize:
                break;
            default: {
                error(c.index, "'%s' invalid index value", indexKind.name());
                return null;
            }
        }
        
        typeCheck(c.getSrcPos(), valOp.type, objectInfo);
                
        Operand result = Operand.op(valOp.type);
        c.resolveTo(result);
        
        return result;
    }
    
    private Operand resolveSubGetExpr(SubscriptGetExpr c) {
        Operand objOp = resolveExpr(c.object);
        Operand indexOp = resolveExpr(c.index);
        
        TypeInfo baseObj = null;
        TypeKind objectKind = objOp.type.getKind();
        switch(objectKind) {
            case Str: {
                baseObj = TypeInfo.CHAR_TYPE;
                break;
            }
            case Array: {
                ArrayTypeInfo arrayInfo = objOp.type.as();
                baseObj = arrayInfo.arrayOf;
                break;
            }
            case Ptr: {
                PtrTypeInfo ptrInfo = objOp.type.as();
                baseObj = ptrInfo.ptrOf;
                break;
            }
            default: {
                error(c, "invalid index into '%s'", objectKind.name());
                return null;
            }
        }
        
        TypeKind indexKind = indexOp.type.getKind();
        switch(indexKind) {
            case Char:
            case i8:
            case u8:
            case i16:
            case u16:
            case i32:
            case u32:
            case i64:
            case u64:
            case Enum:
            case usize:
                break;
            default: {
                error(c, "'%s' invalid index value", indexKind.name());
                return null;
            }
        }
        
        
        Operand result = Operand.op(baseObj);
        c.resolveTo(result);
        
        return result;
    }
    
    private TypeInfo getAggregateField(SrcPos pos, AggregateTypeInfo aggInfo, NameTypeSpec name, boolean allowMethods, boolean error) {
        
        // Simple aggregate field access
        FieldInfo field = aggInfo.getFieldWithAnonymous(name.name);
        if(field != null) {
            return field.type;
        }
        
        
        // Field access with aggregates with using fields
        FieldPath path = aggInfo.getFieldPath(name.name);
        if(path.hasPath()) {
            TypeInfo usingField = path.getTargetField().type;    
            return usingField;
        }
        
        
        // Method field access
        Symbol sym = current().getMethodType(aggInfo, name.name);
        if(sym == null || !allowMethods) {
            if(error) {
                error(pos, "'%s' does not have field '%s'", aggInfo.name, name);
            }
            return null;                
        }
                
        FuncTypeInfo funcInfo = sym.type.as();
        if(funcInfo.parameterDecls.isEmpty()) {     
            if(error) {
                error(pos, "'%s' does not have a parameter of '%s'", funcInfo.getName(), aggInfo.name);
            }
            return null;
        }
        
        if(funcInfo.hasGenerics()) {
            NameTypeSpec methodName = new NameTypeSpec(name.pos, sym.name, name.genericArgs);
            TypeInfo type = resolveTypeSpec(methodName);
            if(!type.isKind(TypeKind.Func)) {
                if(error) {
                    this.result.addError(pos, "'%s' does not have field '%s'", aggInfo.name, name);
                }
                return null;    
            }
            
            funcInfo = type.as();
        }
        
        ParamInfo objectParam = funcInfo.parameterDecls.get(0);
                    
        TypeInfo baseType = objectParam.type;
        if(TypeInfo.isPtrAggregate(baseType)) {
            baseType = ((PtrTypeInfo) baseType.as()).getBaseType();
        }
        
        if(baseType.strictEquals(aggInfo)) {                                
            return funcInfo;
        }
        
        if(allowMethods && aggInfo.isUsingType(baseType)) {
            return funcInfo;
        }
        
        if(error) {
            error(pos, "'%s' does not have field '%s'", aggInfo.name, name);
        }
        
        return null;
    }
    
    private Operand resolveTypeOfExpr(TypeOfExpr c) {
        Operand op = null;
        if(c.expr != null) {
            Operand exprOp = resolveExpr(c.expr);
            op = Operand.opConst(TypeInfo.I64_TYPE, exprOp.type.typeId);
        }
        else {
            TypeInfo type = resolveTypeSpec(c.type);
            op = Operand.opConst(TypeInfo.I64_TYPE, type.typeId);
        }
        
        c.resolveTo(op);
        return op;
    }
    
    private Operand resolveOffsetOfExpr(OffsetOfExpr c) {
        TypeInfo type = resolveTypeSpec(c.type);
        if(!TypeInfo.isAggregate(type)) {
            error(c.getSrcPos(), "%s must be an aggreate type", c.type.toString());
        }
        AggregateTypeInfo aggInfo = type.as();
        FieldPath path = aggInfo.getFieldPath(c.field);
        if(path == null || !path.hasPath()) {
            error(c.getSrcPos(), "%s does not have field '%s'", c.type.toString(), c.field);
        }
        
        Operand op = Operand.op(TypeInfo.I64_TYPE);        
        c.resolveTo(op);
        
        return op;
    }
    
    private Operand resolveSizeOfExpr(SizeOfExpr c) {
        /*if(c.expr != null) {
            resolveExpr(c.expr);
        }
        else {
            TypeInfo type = resolveTypeSpec(c.type);
            return Operand.opConst(type, 0);
        }
        
        // TODO: calculate real size
        return Operand.opConst(TypeInfo.U64_TYPE, 0);
        */
        
        resolveExpr(c.expr);
        return Operand.opConst(TypeInfo.U64_TYPE, 0);
    }
    
    private Operand resolveGroupExpr(GroupExpr c) {
        Operand op = resolveExpr(c.expr);
        Operand result = Operand.op(op.type);
        result.isConst = op.isConst;
        c.resolveTo(result);
        return result;
    }
    
    private Operand resolveSetExpr(SetExpr c) {
        Operand objOp = resolveExpr(c.object);
        //Operand fieldOp = resolveExpr(c.field);
        Operand valOp = resolveExpr(c.value);
        
        if(objOp.type.isKind(TypeKind.Enum)) {
            error(c.object, "can't reassign enum '%s'", objOp.type.name);
            return null;
        }
        
        if(!TypeInfo.isFieldAccessible(objOp.type)) {
            error(c.field, "'%s' can't be accessed with field '%s'", objOp.type, c.field.type.name);
            return null;
        }
        
        AggregateTypeInfo aggInfo = null;
        if(TypeInfo.isPtrAggregate(objOp.type)) {
            PtrTypeInfo ptrInfo = objOp.type.as();
            aggInfo = ptrInfo.getBaseType().as();
        }
        else {
            aggInfo = objOp.type.as();
        }
        
        TypeInfo field = getAggregateField(c.getSrcPos(), aggInfo, c.field.type, false, true);
        if(field != null) {
            c.field.resolveTo(Operand.op(field));
        }
        
        typeCheck(c.getSrcPos(), valOp.type, field);
                
        Operand result = Operand.op(valOp.type);
        c.resolveTo(result);
        
        return result;
    }
    
    private Operand resolveGetExpr(GetExpr c) {
        Operand objOp = resolveExpr(c.object);
        //Operand fieldOp = resolveExpr(c.field);
            
        Operand fieldOp = null; 
        if(objOp.type.isKind(TypeKind.Enum)) {
            EnumTypeInfo enumInfo = objOp.type.as();
            EnumFieldInfo field = enumInfo.getField(c.field.type.name);
            if(field == null) {
                error(c.field, "'%s' does not have field '%s'", objOp.type, c.field.type.name);
                return null;
            }
            
            // TODO: Calculate proper enum value            
            Object val = null;
            if(field.value != null) {
                val = resolveConstExpr(field.value).val;
            }
            
            fieldOp = Operand.opConst(enumInfo.getFieldType(), val);
        }
        else {
            if(!TypeInfo.isFieldAccessible(objOp.type)) {
                error(c.field, "'%s' can't be accessed with field '%s'", objOp.type, c.field.type.name);
                return null;
            }
            
            AggregateTypeInfo aggInfo = null;
            if(TypeInfo.isPtrAggregate(objOp.type)) {
                PtrTypeInfo ptrInfo = objOp.type.as();
                aggInfo = ptrInfo.getBaseType().as();
            }
            else {
                aggInfo = objOp.type.as();
            }
            
            TypeInfo field = getAggregateField(c.getSrcPos(), aggInfo, c.field.type, true, true);
            if(field != null) {
                fieldOp = Operand.op(field);
                c.field.resolveTo(fieldOp);
            }
        }
        
        if(fieldOp == null) {
            return null;
        }
        
        Operand result = Operand.op(fieldOp);        
        c.resolveTo(result);
        
        return result;
    }
    
    
    /**
     * This will determine if the function argument needs to be modified to fit the actual expected
     * parameter type for structs/unions having "using" elements.  It will also appropriately take
     * the address of or dereference a parameter
     * 
     * @param funcPtr
     * @param suppliedArguments
     */
    private void checkForUsingArguments(FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments) {
        int argIndex = 0;
        for(int i = 0; i < funcPtr.params.size() && i < suppliedArguments.size(); i++) {
            Expr argExpr = suppliedArguments.get(i);
            resolveExpr(argExpr);
            
            SrcPos pos = argExpr.getSrcPos();
            SrcPos endPos = argExpr.getEndSrcPos();
            TypeInfo paramInfo = funcPtr.params.get(argIndex);                    
            TypeInfo argInfo = argExpr.getResolvedType().type;
            
            if(!argInfo.isKind(TypeKind.Array)) {
                // Determine if we need to get the "using" field of this method call
                if(TypeInfo.isAggregate(TypeInfo.getBase(argInfo))) {
                    AggregateTypeInfo aggInfo = TypeInfo.getBase(argInfo).as();
                    TypeInfo pInfo = TypeInfo.getBase(paramInfo);
                    FieldPath path = aggInfo.getFieldPathUsingType(pInfo);
                    
                    if(path.hasPath()) {
                        for(FieldPathNode node : path.getPath()) {
                            NameTypeSpec nameSpec = new NameTypeSpec(pos, node.field.name);
                            argExpr = new GetExpr(argExpr, new IdentifierExpr(nameSpec).setSrcPos(pos, endPos))
                                                    .setSrcPos(pos, endPos);
                        }
                        
                        //argInfo = resolveExpr(argExpr).type;
                        resolveExpr(argExpr);
                        argInfo = argExpr.getResolvedType().type;
                    }
                }
                
                // Determine if we need to promote the object to a
                // pointer depending on what the method is expecting as an
                // argument
                if(TypeInfo.isPtrAggregate(paramInfo) && (!TypeInfo.isPtrAggregate(argInfo) && !argInfo.isKind(TypeKind.Null))) {
                    // Can't take the address of an R-Value; TODO: this should be expr.operand.isRvalue
                    // But the isLeftValue isn't working correctly atm
                    //if(!argExpr.getResolvedType().isLeftValue) {
                    if(argExpr instanceof FuncCallExpr) {
                        error(argExpr, 
                                "cannot take the return value address of an R-Value");
                    }
                                        
                    argExpr = new UnaryExpr(TokenType.BAND, new GroupExpr(argExpr).setSrcPos(pos, endPos))
                                            .setSrcPos(pos, endPos);
                    
                    resolveExpr(argExpr);
                }
                // See if we need to dereference the pointer
                else if(TypeInfo.isAggregate(paramInfo) && TypeInfo.isPtrAggregate(argInfo)) {
                    argExpr = new UnaryExpr(TokenType.STAR, new GroupExpr(argExpr).setSrcPos(pos, endPos))
                            .setSrcPos(pos, endPos);
    
                    resolveExpr(argExpr);
                }
            }
            
            suppliedArguments.set(argIndex, argExpr);
            argIndex++;
        }
    }
    
    private Operand resolveFuncCallExpr(FuncCallExpr expr) {
        Operand op = resolveExpr(expr.object);        
        if(!op.type.isKind(TypeKind.Func) && !op.type.isKind(TypeKind.FuncPtr)) {
            error(expr.object, "'%s' is not a function", op.type.sym.name);
            return null;
        }
        
        boolean isMethod = false;
        int numberOfDefaultArgs = 0;
        List<Expr> suppliedArguments = new ArrayList<>(expr.arguments); 
        
        FuncPtrTypeInfo funcPtr = null;
        if(op.type.isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = op.type.as();
            
            isMethod = funcInfo.isMethod();            
            funcPtr = funcInfo.asPtr();
            
            for(ParamInfo p : funcInfo.parameterDecls) {
                if(p.defaultValue != null) {
                    numberOfDefaultArgs++;
                }
            }            
        }
        else {
            funcPtr = op.type.as();
        }

        // see if this is method call syntax
        boolean isMethodCall = isMethod && isMethodSyntax(expr, funcPtr, suppliedArguments);
        checkNumberOfArgs(expr, op.type.name, funcPtr, suppliedArguments.size(), numberOfDefaultArgs);
        
        checkForUsingArguments(funcPtr, suppliedArguments);
                
        // type inference for generic functions 
        if(funcPtr.hasGenerics() && expr.genericArgs.isEmpty()) {            
            funcPtr = inferFuncCallExpr(expr, funcPtr, suppliedArguments, isMethodCall);            
        }
               
        
        int i = 0;
        for(; i < funcPtr.params.size(); i++) {
            TypeInfo p = funcPtr.params.get(i);
            
            if(i < suppliedArguments.size()) {
                Expr arg = suppliedArguments.get(i);                
                resolveExpr(arg);
                
                typeCheck(arg.getSrcPos(), arg.getResolvedType().type, p);
                arg.expectedType = p;
            }
        }
        
        if(funcPtr.isVararg) {
            for(; i < suppliedArguments.size(); i++) {
                Expr arg = suppliedArguments.get(i);
                resolveExpr(arg);
            }                
        }

        expr.replaceArguments(suppliedArguments);
        
        Operand retOp = Operand.op(funcPtr.returnType);
        expr.resolveTo(retOp);
        
        return retOp;
    }
    
    private Operand resolveCastExpr(CastExpr c) {
        TypeInfo type = resolveTypeSpec(c.castTo);
        Operand operand = Operand.op(type);
        c.resolveTo(operand);
        
        resolveExpr(c.expr);
        
        typeCheck(c.getSrcPos(), c.expr.getResolvedType().type, type, true);
        
        return operand;
    }
    
    private Operand resolveBinaryExpr(BinaryExpr expr) {
        Operand left = resolveExpr(expr.left);
        Operand right = resolveExpr(expr.right);
        
        TypeInfo leftType = left.type;
        TypeInfo rightType = right.type;
        
        TypeInfo targetType = leftType;
        
        switch(expr.operator) {
            case EQUALS: {                    
                checkConstant(expr.left);
                break;
            }
        
            case BAND_EQ:
            case BNOT_EQ:
            case BOR_EQ:
            case XOR_EQ:
            case LSHIFT_EQ:
            case RSHIFT_EQ: 
                checkConstant(expr.left);
                // fallthru
            case BAND:
            case BNOT:
            case BOR:
            case XOR:
            case LSHIFT:
            case RSHIFT: {
                if(!TypeInfo.isInteger(leftType)) {
                    error(expr.left, "illegal, left operand has type '%s'", leftType.getName());
                }
                
                if(!TypeInfo.isInteger(rightType)) {
                    error(expr.right, "illegal, right operand has type '%s'", rightType.getName());
                }
                break;
            }
                
            case AND:
            case OR:
                targetType = TypeInfo.BOOL_TYPE;
                break;
            
            case EQUALS_EQUALS:
            case NOT_EQUALS:
                targetType = TypeInfo.BOOL_TYPE;
                break;
                
            case GREATER_EQUALS:
            case GREATER_THAN:
            case LESS_EQUALS:
            case LESS_THAN:
                if(!TypeInfo.isNumber(leftType)) {
                    error(expr.left, "illegal, left operand has type '%s'", leftType.getName());
                }
                
                if(!TypeInfo.isNumber(rightType)) {
                    error(expr.right, "illegal, right operand has type '%s'", rightType.getName());
                }
                targetType = TypeInfo.BOOL_TYPE;
                break;
            
            case MINUS_EQ:
            case PLUS_EQ:
            case MOD_EQ:
            case MUL_EQ:
            case DIV_EQ:
                checkConstant(expr.left);
                // fallthru
            case MINUS:
            case PLUS:
            case MOD:
            case STAR:
            case SLASH:
                
                if(!TypeInfo.isNumber(leftType) && !leftType.isKind(TypeKind.Ptr) && !leftType.isKind(TypeKind.Str)) {
                    error(expr.left, "illegal, left operand has type '%s'", leftType.getName());
                }
                
                if(!TypeInfo.isNumber(rightType) && !rightType.isKind(TypeKind.Ptr) && !rightType.isKind(TypeKind.Str)) {
                    error(expr.right, "illegal, right operand has type '%s'", rightType.getName());
                }
                
                break;
        default:
            break;
        
        }

        // TODO: Cast to appropriate type
        Operand op = Operand.op(targetType);        
        op.isConst = left.isConst && right.isConst;
        
        
        expr.resolveTo(op);
        
        return op;
    }
    
    private Operand resolveArrayInitExpr(ArrayInitExpr expr) {
        TypeInfo type = resolveTypeSpec(expr.type);
        Operand operand = Operand.op(type);
        expr.resolveTo(operand);
        
        ArrayTypeInfo arrayInfo = null;
        TypeInfo arrayOf = null;
        
        if(TypeInfo.isArray(type)) {
            arrayInfo = type.as();
            arrayOf = arrayInfo.arrayOf;
        }
        
        
        if(expr.values != null) {
            for(Expr val : expr.values) {
                resolveExpr(val);
                val.expectedType = arrayOf;
            }
            
            if(arrayInfo != null) {                
                if(arrayInfo.length < 0 && arrayInfo.lengthExpr == null) {
                    arrayInfo.length = expr.values.size();
                }
            }
        }
        
        
        // TODO: Array init size
        
        return operand;
    }
    
    private Operand resolveArrayDesExpr(ArrayDesignationExpr expr) {
        resolveExpr(expr.index);
        resolveExpr(expr.value);
        // should probably not be an expr??
        
        if(expr.index instanceof IdentifierExpr) {
            IdentifierExpr id = expr.index.as();
            Symbol sym = id.sym;
            if(sym != null && !sym.isConstant()) {
                error(expr.index.getSrcPos(), "'%s' must be a constant", id.type);
            }
        }
        
        typeCheck(expr.index.getSrcPos(), expr.index.getResolvedType().type, TypeInfo.USIZE_TYPE);
        
        return null;
    }
    
    private Operand resolveInitArgExpr(InitArgExpr expr) {
        Operand op = Operand.op(TypeInfo.NULL_TYPE);
        if(expr.value != null) {
            op = resolveExpr(expr.value);            
            expr.resolveTo(op);            
        }
                
        return op;
    }
    
    private Operand resolveInitExpr(InitExpr expr) {
        TypeInfo type = resolveTypeSpec(expr.type);
        if(!TypeInfo.isAggregate(type) && !type.isKind(TypeKind.Array)) {
            error(expr, "only struct, union or array can use initialization syntax");
        }
        
        addDefaultArguments(type, expr);
        
        for(InitArgExpr arg : expr.arguments) {
            resolveInitArgExpr(arg);            
        } 

        if(TypeInfo.isAggregate(type)) {
            AggregateTypeInfo aggInfo = type.as();
            if(type.hasGenerics() && expr.genericArgs.isEmpty()) {
                type = inferInitExpr(expr, aggInfo);
            }
        }
                
        Operand operand = Operand.op(type);        
        expr.resolveTo(operand);
        
        checkInitArguments(expr);
        
        return operand;
    }
    
    private void addDefaultArguments(TypeInfo type, InitExpr expr) {
        if(!TypeInfo.isAggregate(type)) {
            return;
        }
        
        AggregateDecl decl = (AggregateDecl)type.sym.decl;
        
        List<InitArgExpr> defaultArgs = new ArrayList<>();
        
        for(int position = 0; position < decl.fields.size(); position++) {
            FieldStmt fieldStmt = decl.fields.get(position);
            
            if(!(fieldStmt instanceof VarFieldStmt)) {
                continue;
            }
            
            VarFieldStmt var = (VarFieldStmt)fieldStmt;            
            if(var.defaultExpr == null) {
                continue;
            }
            
            boolean isArgDefinedByName = false;
            boolean isArgDefinedByPosition = false;
            boolean hasNamedArgs = false;
            
            for(InitArgExpr argExpr : expr.arguments) {
                if(argExpr.fieldName != null) {
                    hasNamedArgs = true;
                    
                    if(argExpr.fieldName.equals(var.fieldName.identifier)) {
                        isArgDefinedByName = true;
                        break;
                    }
                }
                else {
                    if(argExpr.argPosition == position) {
                        isArgDefinedByPosition = true;                        
                    }
                }
            }
            
            // the argument wasn't included
            if(!isArgDefinedByName && !(!hasNamedArgs && isArgDefinedByPosition)) {
                defaultArgs.add(new InitArgExpr(var.fieldName.identifier, position, var.defaultExpr));
            }
        }
        
        for(InitArgExpr defaultArg : defaultArgs) {
            expr.addArgument(defaultArg);
        }
    }
    
    private Operand resolveConstExpr(Expr expr) {
        Operand op = resolveExpr(expr);
        if(!op.isConst) {
            error(expr, "expected a constant expression");
        }
        
        return op;
    }
    
    private void checkConstant(Expr expr) {
        if(expr instanceof IdentifierExpr) {
            IdentifierExpr id = expr.as();
            if(id.sym != null) {
                if(id.sym.isType()) {
                    error(expr, "can't reassign type '%s'", id.type);
                    return;
                }
                if(id.sym.isConstant()) {
                    error(expr, "can't reassign constant variable '%s'", id.type);
                    return;
                }
            }
        }
        
        Operand op = expr.getResolvedType();
        if(op == null || op.type == null) {
            return;
        }
        
        if(op.type.sym != null && (op.type.sym.isConstant() || op.isConst)) {
            error(expr, "can't reassign constant variable '%s'", op.type.sym.name);            
        }
    }
    
    private void checkNumberOfArgs(FuncCallExpr expr, String funcName, FuncPtrTypeInfo funcInfo, int numberOfSuppliedArgs, int numberOfDefaultArgs) {
        int maxNumOfArgs = funcInfo.params.size();
        if(maxNumOfArgs == numberOfSuppliedArgs) {
            return;
        }
        
        int minNumOfArgs = maxNumOfArgs - numberOfDefaultArgs;
        
        if(numberOfSuppliedArgs == minNumOfArgs) {
            return;
        }
        
        if(minNumOfArgs > numberOfSuppliedArgs) {
            error(expr, "'%s' called with too few arguments", funcName);
            return;
        }
        
        if(funcInfo.isVararg) {
            return;
        }
        
        if(maxNumOfArgs < numberOfSuppliedArgs) {            
            error(expr, "'%s' called with too many arguments", funcName);
        }
    }
    
    private void checkDuplicateFields(Stmt stmt, AggregateTypeInfo aggInfo, Map<String, Tuple<AggregateTypeInfo, FieldInfo>> definedFields) {
        for(FieldInfo field : aggInfo.fieldInfos) {
            if(definedFields.containsKey(field.name)) {
                Tuple<AggregateTypeInfo, FieldInfo> tuple = definedFields.get(field.name);
                boolean isSame = aggInfo.name.equals(tuple.getFirst().name);
                
                if(!isSame) {
                    this.result.addError(stmt, "duplicate member '%s' in '%s' and '%s'", 
                            field.name, aggInfo.name, tuple.getFirst().name);
                }
                else {
                    this.result.addError(stmt, "duplicate member '%s' in '%s'", 
                            field.name, aggInfo.name);
                }
            }
            definedFields.put(field.name, new Tuple<>(aggInfo, field));
            
            if(field.attributes.isUsing()) {
                if(field.type.isKind(TypeKind.Struct) || field.type.isKind(TypeKind.Union)) {
                    checkDuplicateFields(stmt, field.type.as(), definedFields);
                }                
            }
        }
    }
    
    private void checkInitArguments(InitExpr expr) {
        TypeInfo type = expr.getResolvedType().type;
        
        if(TypeInfo.isAggregate(type)) { 
            AggregateTypeInfo aggInfo = expr.getResolvedType().type.as();
            
            for(InitArgExpr arg : expr.arguments) {
                if(arg.fieldName != null) {
                    TypeInfo fieldType = getAggregateField(arg.getSrcPos(), aggInfo, new NameTypeSpec(arg.getSrcPos(), arg.fieldName), false, true);
                    typeCheck(arg.getSrcPos(), arg.value.getResolvedType().type, fieldType);
                    arg.expectedType = fieldType;
                    arg.value.expectedType = fieldType;
                }
                else {
                    FieldInfo field = aggInfo.getFieldByPosition(arg.argPosition);
                    if(field == null) {
                        error(arg.getSrcPos(), "'%s' does not have a field at index '%d'", aggInfo.name, arg.argPosition);
                    }
                    
                    TypeInfo fieldType = field.type;
                    typeCheck(arg.getSrcPos(), arg.value.getResolvedType().type, fieldType);
                    arg.expectedType = fieldType;
                    arg.value.expectedType = fieldType;
                }
            }
        }
        else if(type.isKind(TypeKind.Array)) {
            ArrayTypeInfo arrayInfo = type.as();
            TypeInfo baseType = arrayInfo.getBaseType();
            
            for(InitArgExpr arg : expr.arguments) {                
                typeCheck(arg.getSrcPos(), arg.value.getResolvedType().type, baseType);
                arg.expectedType = baseType;
                arg.value.expectedType = baseType;
            }
        }
    }
    
    private void addTypeToScope(Decl p, Scope scope, TypeInfo rootType, TypeInfo currentType) {        
        AggregateTypeInfo aggInfo = (currentType.isKind(TypeKind.Ptr)) 
                                        ? ((PtrTypeInfo)currentType).ptrOf.as()
                                        : currentType.as();
                                        
        for(FieldInfo field : aggInfo.fieldInfos) {
            Symbol sym = scope.addSymbol(current(), new VarDecl(new Identifier(p.name), field.type.asTypeSpec()), field.name, Symbol.IS_USING);
            sym.type = field.type;
            sym.usingParent = rootType;
            
            tryResolveSym(sym);            
        }
        
        if(aggInfo.hasUsingFields()) {
            for(FieldInfo field : aggInfo.usingInfos) {
                addTypeToScope(p, scope, rootType, field.type);
            }
        }
    }
    
    private Symbol tryAddSymbol(Decl d, TypeSpec typeSpec) {
        try {
            return addSymbol(d, typeSpec);
        }
        catch(TypeCheckException e) {
            result.addError(e.pos, e.getMessage());
        }
        
        return null;
    }
    
    private Symbol addSymbol(Decl d, TypeSpec typeSpec) {
        Scope scope = current().currentScope();
        Symbol sym = scope.addSymbol(current(), d, d.name);
        
        TypeInfo type = resolveTypeSpec(typeSpec);
        sym.type = type;
        
        resolveSym(sym);
        return sym;
    }
    
    private Symbol addSymbol(Decl d, TypeInfo type) {
        Scope scope = current().currentScope();
        Symbol sym = scope.addSymbol(current(), d, d.name, d instanceof ConstDecl);        
        sym.type = type;
        
        resolveSym(sym);
        return sym;
    }
    
    private GenericParam isGenericParam(NameTypeSpec name) {
        for(List<GenericParam> gens : this.genericStack) {
            for(GenericParam p : gens) {
                if(p.name.equals(name.name)) {
                    return p;
                }
            }
        }
        
        return null;
    }
    
    private TypeInfo inferredType(String genericName, TypeInfo paramType, TypeInfo argumentType) {
        if(paramType.getName().equals(genericName)) {
            return argumentType;
        }
        
        if(!paramType.getKind().equals(argumentType.getKind())) {
            return null;
        }
        
        // TODO
        int index = 0;
        //if(paramType.hasGenericArgs()) {
        if(paramType.sym != null && paramType.sym.isFromGenericTemplate()) {
            List<TypeInfo> genericArgs = paramType.sym.genericArgs;
            for(;index < genericArgs.size(); index++) {
                if(genericArgs.get(index).name.equals(genericName)) {
                    break;
                }
            }
        }
        else if(paramType.isKind(TypeKind.FuncPtr)) {
            // TODO: If we forgot to put generic types on Typedef of func pointer with
            // generics
            //this.result.addError(null, "", args);
        }
        
        switch(paramType.getKind()) {
            case Array: {
                ArrayTypeInfo arrayInfo = paramType.as();
                ArrayTypeInfo argumentArrayInfo = argumentType.as();
                return inferredType(genericName, arrayInfo.arrayOf, argumentArrayInfo.arrayOf);
            }
            case Const: {
                ConstTypeInfo constInfo = paramType.as();
                ConstTypeInfo argumentConstInfo = argumentType.as();
                return inferredType(genericName, constInfo.constOf, argumentConstInfo.constOf);
            }
            case FuncPtr: {
                FuncPtrTypeInfo funcInfo = paramType.as();
                if(!funcInfo.hasGenerics() || funcInfo.genericParams.size() <= index) {
                    break;
                }
                
                genericName = funcInfo.genericParams.get(index).name;
                FuncPtrTypeInfo argumentFuncInfo = argumentType.as();
                
                TypeInfo retType = inferredType(genericName, funcInfo.returnType, argumentFuncInfo.returnType);
                if(retType != null) {
                    return retType;
                }
                
                
                for(int i = 0; i < funcInfo.params.size(); i++) {
                    if(i < argumentFuncInfo.params.size()) {
                        TypeInfo pType = inferredType(genericName, funcInfo.params.get(i), argumentFuncInfo.params.get(i));
                        if(pType != null) {
                            return pType;
                        }
                    }
                }
                
                break;
            }
            case Ptr: {
                PtrTypeInfo ptrInfo = paramType.as();
                PtrTypeInfo argumentPtrInfo = argumentType.as();
                return inferredType(genericName, ptrInfo.ptrOf, argumentPtrInfo.ptrOf);
            }
            case Union:
            case Struct: {
                AggregateTypeInfo aggInfo = paramType.as();
                if(!aggInfo.hasGenerics() || aggInfo.genericParams.size() <= index) {
                    break;
                }
                
                genericName = aggInfo.genericParams.get(index).name;                    
                AggregateTypeInfo argumentAggInfo = argumentType.as();
                
                if(argumentAggInfo.getBaseName().equals(aggInfo.getBaseName())) {
                    Symbol sym = argumentAggInfo.sym;
                    if(sym.genericParams != null) {
                        for(int i = 0; i < sym.genericParams.size(); i++) {
                            GenericParam p = sym.genericParams.get(i);
                            if(p.name.equals(genericName)) {
                                return sym.genericArgs.get(i);
                            }
                        }
                    }
                }
                
                for(FieldInfo field : aggInfo.fieldInfos) {
                    FieldInfo argumentField = argumentAggInfo.getFieldWithAnonymous(field.name);
                    if(argumentField == null) {
                        continue;
                    }
                    
                    TypeInfo fieldType = inferredType(genericName, field.type, argumentField.type);
                    if(fieldType != null) {
                        return fieldType;
                    }
                }
                break;
            }
            default:
                break;            
        }
        
        return null;
        
    }
    
    
    private FuncPtrTypeInfo inferFuncCallExpr(FuncCallExpr expr, FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments, boolean isMethodCall) {
        for(Expr arg : expr.arguments) {
            resolveExpr(arg);
        }
        
        Expr objectExpr = expr.object;
        if(isMethodCall) {
            GetExpr getExpr = (GetExpr) expr.object;
            objectExpr = getExpr.field;
        }
        
        if(!(objectExpr instanceof FuncIdentifierExpr)) {
            return funcPtr;            
        }
        
        FuncIdentifierExpr funcExpr = (FuncIdentifierExpr)objectExpr;
        NameTypeSpec nameSpec = funcExpr.type;
        nameSpec.genericArgs = new ArrayList<>();
        
        expr.genericArgs = new ArrayList<>(funcPtr.genericParams.size());
        
        for(GenericParam p : funcPtr.genericParams) {
            for(int j = 0; j < funcPtr.params.size(); j++) {
                TypeInfo paramType = funcPtr.params.get(j);
                
                if(j >= suppliedArguments.size()) {
                    break;
                }
                
                TypeInfo argType = suppliedArguments.get(j).getResolvedType().type;
                TypeInfo inferredType = inferredType(p.name, paramType, argType);
                if(inferredType != null) {
                    nameSpec.genericArgs.add(inferredType.asTypeSpec());
                    break;
                }
            }
        }
        
        Operand operand = resolveExpr(expr.object);
        
        // unable to infer types
        if(!expr.object.isResolved()) {
            for(int i = expr.genericArgs.size(); i < funcPtr.genericParams.size(); i++) {
                error(expr, "unable to infer generic parameter '%s'", funcPtr.genericParams.get(i));
            }
            return funcPtr;
        }
        
        TypeInfo type = operand.type;
        
        Symbol sym = type.sym;
        if(sym != null && sym.isGenericTemplate()) {
            GenericDecl decl = (GenericDecl)sym.decl;
            error(funcExpr, "'%s' is missing generic arguments %s", nameSpec.name, decl.genericParams);
            return null;
        }
        
        if(type.isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = type.as();
            return funcInfo.asPtr();
        }
        
        return type.as();
    }
    
    
    private TypeInfo inferInitExpr(InitExpr expr, AggregateTypeInfo aggInfo) {
        expr.genericArgs = new ArrayList<>(aggInfo.genericParams.size());
        
        List<TypeInfo> suppliedArguments = new ArrayList<>();
        for(Expr arg : expr.arguments) {
            suppliedArguments.add(arg.getResolvedType().type);
        }
        
        NameTypeSpec nameSpec = expr.type;
        if(nameSpec == null) {
            return aggInfo;
        }
        
        nameSpec.genericArgs = new ArrayList<>();
        
        for(GenericParam p : aggInfo.genericParams) {                
            for(int j = 0; j < aggInfo.fieldInfos.size(); j++) {
                TypeInfo paramType = aggInfo.fieldInfos.get(j).type;
                
                if(j >= suppliedArguments.size()) {
                    break;
                }
                
                TypeInfo inferredType = inferredType(p.name, paramType, suppliedArguments.get(j));
                if(inferredType != null) {
                    nameSpec.genericArgs.add(inferredType.asTypeSpec());
                    break;
                }
            }
        }
        
        TypeInfo type = resolveTypeSpec(expr.type);
        
        Symbol sym = type.sym;
        if(sym != null && sym.isGenericTemplate()) {
            GenericDecl decl = (GenericDecl)sym.decl;
            error(expr, "'%s' is missing generic arguments %s", nameSpec.name, decl.genericParams);
            return null;
        }
        
        return type;
    }
    
    private boolean isMethodSyntax(FuncCallExpr expr, FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments) {
        if(!(expr.object instanceof GetExpr)) {
            return false;
        }
        
        GetExpr getExpr = (GetExpr) expr.object;
        if(!getExpr.field.getResolvedType().type.isKind(TypeKind.Func)) {
            return false;
        }
        
        // we've already set this as a method call
        if(getExpr.isMethodCall) {
            return true;
        }
        
        getExpr.isMethodCall = true;
        suppliedArguments.add(0, getExpr.object); 
        
        return true;
    }
    
    /* --------------------------------------------------------------
                              Statements
       -------------------------------------------------------------- */
    
    private class StmtVisitor implements NodeVisitor {

        @Override
        public void visit(ModuleStmt stmt) {
        }

        @Override
        public void visit(ImportStmt stmt) {
        }

        @Override
        public void visit(NoteStmt stmt) {
        }

        @Override
        public void visit(VarFieldStmt stmt) {
            try {
                resolveTypeSpec(stmt.type);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(StructFieldStmt stmt) {
        }

        @Override
        public void visit(UnionFieldStmt stmt) {
        }

        @Override
        public void visit(EnumFieldStmt stmt) {
        }
        
        @Override
        public void visit(EnumFieldEntryStmt stmt) {
        }

        @Override
        public void visit(IfStmt stmt) {
            stmt.condExpr.visit(this);
            
            current().pushScope();
            stmt.thenStmt.visit(this);
            current().popScope();
            
            if(stmt.elseStmt != null) {
                current().pushScope();
                stmt.elseStmt.visit(this);
                current().popScope();
            }
        }

        @Override
        public void visit(WhileStmt stmt) {
            stmt.condExpr.visit(this);
            
            current().pushScope();
            stmt.bodyStmt.visit(this);
            current().popScope();
        }

        @Override
        public void visit(DoWhileStmt stmt) {
            current().pushScope();
            stmt.bodyStmt.visit(this);
            current().popScope();
            
            stmt.condExpr.visit(this);
        }

        @Override
        public void visit(ForStmt stmt) {
            current().pushScope();
            if(stmt.initStmt != null) stmt.initStmt.visit(this);
            if(stmt.condExpr != null) stmt.condExpr.visit(this);
            if(stmt.postStmt != null) stmt.postStmt.visit(this);
            
            stmt.bodyStmt.visit(this);
            current().popScope();
        }

        @Override
        public void visit(SwitchCaseStmt stmt) {
            stmt.cond.visit(this);
            stmt.stmt.visit(this);
        }

        @Override
        public void visit(SwitchStmt stmt) {
            stmt.cond.visit(this);
            
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
            
            if(stmt.defaultStmt != null) {
                stmt.defaultStmt.visit(this);
            }
        }

        @Override
        public void visit(BreakStmt stmt) {
        }

        @Override
        public void visit(ContinueStmt stmt) {
        }

        @Override
        public void visit(ReturnStmt stmt) {
            if(stmt.returnExpr != null) {
                stmt.returnExpr.visit(this);
                tryTypeCheck(stmt.getSrcPos(), stmt.returnExpr.getResolvedType().type, currentFunc.returnType);
                stmt.returnExpr.expectedType = currentFunc.returnType;
            }
            else {
                tryTypeCheck(stmt.getSrcPos(), TypeInfo.VOID_TYPE, currentFunc.returnType);
            }
        }

        @Override
        public void visit(BlockStmt stmt) {
            current().pushScope();
            for(Stmt s : stmt.stmts) {                
                s.visit(this);                
            }
            current().popScope();
        }
        
        @Override
        public void visit(FuncBodyStmt stmt) {            
            for(Stmt s : stmt.stmts) {                
                s.visit(this);                
            }            
        }

        @Override
        public void visit(DeferStmt stmt) {
            stmt.stmt.visit(this);
        }

        @Override
        public void visit(EmptyStmt stmt) {
        }

        @Override
        public void visit(ParametersStmt stmt) {
            boolean hasDefault = false;
            for(ParameterDecl p : stmt.params) {
                p.visit(this);
                
                if(p.defaultValue != null) {
                    hasDefault = true;
                }
                else if(hasDefault) {
                    Decl decl = (Decl)stmt.getParentNode();
                    addError(stmt, "'%s' must have default arguments defined last", decl.name);
                }
            }
        }

        @Override
        public void visit(CompStmt stmt) {
            preprocessor.putContext("module", current());
            preprocessor.putContext("scope", current().currentScope());
            Stmt s = stmt.evaluateForBody(preprocessor);
            if(s != null) {
                s.visit(this);
            }            
        }
        
        @Override
        public void visit(VarDeclsStmt stmt) {
            for(Stmt s : stmt.vars) {
                s.visit(this);
            }
        }

        @Override
        public void visit(ConstDeclsStmt stmt) {
            for(Stmt s : stmt.consts) {
                s.visit(this);
            }
        }

        @Override
        public void visit(GotoStmt stmt) {
            labels.putIfAbsent(stmt.label, false);
        }

        @Override
        public void visit(LabelStmt stmt) {
            labels.put(stmt.label, true);
        }
        
        @Override
        public void visit(StructDecl d) {
            for(FieldStmt field : d.fields) {
                field.visit(this);
            }
            
            Map<String, Tuple<AggregateTypeInfo, FieldInfo>> definedFields = new HashMap<>();
            AggregateTypeInfo aggInfo = d.sym.type.as();
            checkDuplicateFields(d, aggInfo, definedFields);
        }
        
        @Override
        public void visit(TypedefDecl d) {
        }
        
        @Override
        public void visit(UnionDecl d) {
            for(FieldStmt field : d.fields) {
                field.visit(this);
            }
            
            Map<String, Tuple<AggregateTypeInfo, FieldInfo>> definedFields = new HashMap<>();
            AggregateTypeInfo aggInfo = d.sym.type.as();
            checkDuplicateFields(d, aggInfo, definedFields);
        }

        @Override
        public void visit(EnumDecl d) {
            Map<String, EnumFieldEntryStmt> definedFields = new HashMap<>();
            for(EnumFieldEntryStmt field : d.fields) {
                if(field.value != null) {
                    field.value.visit(this);
                    Operand op = field.value.getResolvedType();
                    tryTypeCheck(field.value.getSrcPos(), op.type, TypeInfo.I32_TYPE);
                }
                
                if(definedFields.containsKey(field.fieldName.identifier)) {
                    result.addError(d, "duplicate member '%s'", field.fieldName.identifier);
                    continue;
                }
                
                definedFields.put(field.fieldName.identifier, field);
            }
        }
        
        @Override
        public void visit(ConstDecl d) {  
            try {
                TypeInfo type = resolveValueDecl(d, d.type, d.expr, !d.attributes.isForeign());            
                addSymbol(d, type);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(VarDecl d) {
            try {
                TypeInfo type = resolveValueDecl(d, d.type, d.expr, false);            
                addSymbol(d, type);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
            
        }

        @Override
        public void visit(FuncDecl d) {  
            Symbol sym = d.sym;
            enterModuleFor(sym);       
            current().pushScope();
            try {                            
                d.params.visit(this);
                
                if(d.bodyStmt != null) {
                    labels.clear();
                    currentFunc = sym.type.as();
                    d.bodyStmt.visit(this);
                    
                    labels.forEach((label, isDefined) -> {
                        if(!isDefined) {
                            result.addError(d.bodyStmt, "'%s' label not found", label);
                        }
                    });
                }
            }
            finally {
                current().popScope();
                leaveModule();
            }
        }



        @Override
        public void visit(ParameterDecl d) {
            if(d.defaultValue != null) {
                d.defaultValue.visit(this);
            }
            
            d.sym = tryAddSymbol(d, d.type);

            if(d.attributes.isUsing()) {
                TypeInfo type = d.sym.type;
                if (!TypeInfo.isAggregate(type) && 
                    !TypeInfo.isPtrAggregate(type)) {
                    addError(d, "'%s' is not an aggregate type (or pointer to an aggregate), can't use 'using'", d.name);
                }
                else {                    
                    addTypeToScope(d, current().currentScope(), type, type);
                }
            }
        }

        @Override
        public void visit(CastExpr expr) {
            try {
                resolveCastExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(SizeOfExpr expr) {
            try {
                resolveSizeOfExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(TypeOfExpr expr) {
            try {
                resolveTypeOfExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }
        
        @Override
        public void visit(OffsetOfExpr expr) {
            try {
                resolveOffsetOfExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(InitArgExpr expr) {
            try {
                resolveInitArgExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(InitExpr expr) {
            try {
                resolveInitExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(NullExpr expr) {
        }

        @Override
        public void visit(BooleanExpr expr) {
        }

        @Override
        public void visit(NumberExpr expr) {
        }

        @Override
        public void visit(StringExpr expr) {
        }

        @Override
        public void visit(CharExpr expr) {
        }

        @Override
        public void visit(GroupExpr expr) {
            try {
                resolveGroupExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(FuncCallExpr expr) {
            try {
                resolveFuncCallExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(IdentifierExpr expr) {
            try {
                resolveIdentifier(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(FuncIdentifierExpr expr) {
            try {
                resolveFuncIdentifier(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(TypeIdentifierExpr expr) {
            // TODO Auto-generated method stub
        }

        @Override
        public void visit(GetExpr expr) {
            try {
                resolveGetExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(SetExpr expr) {
            try {
                resolveSetExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(UnaryExpr expr) {
            try {
                resolveUnaryExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(BinaryExpr expr) {
            try {
                resolveBinaryExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(TernaryExpr expr) {
            try {
                resolveTernaryExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(ArrayInitExpr expr) {
            try {
                resolveArrayInitExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(ArrayDesignationExpr expr) {
            try {
                resolveArrayDesExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(SubscriptGetExpr expr) {
            try {
                resolveSubGetExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }

        @Override
        public void visit(SubscriptSetExpr expr) {
            try {
                resolveSubSetExpr(expr);
            }
            catch(TypeCheckException e) {
                result.addError(e.pos, e.getMessage());
            }
        }
    }
}
