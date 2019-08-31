/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node.SrcPos;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo.*;
import litac.compiler.*;
import litac.compiler.Symbol.*;
import litac.generics.*;
import litac.parser.tokens.TokenType;
import litac.util.Names;
import litac.util.Stack;

/**
 * @author antho
 *
 */
public class Resolver {

    public static class Operand {
        public TypeInfo type;
        public boolean isLeftValue;
        public boolean isConst;
        public Object val;
        
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
    private Map<String, Module> resolvedModules;
   // private Map<String, Tuple<Module,Decl>> genericTypes;
    
    private List<Symbol> moduleSymbols;
    private List<Symbol> moduleFuncs;
    private Module current;
    
    // Types pending to be completed
    private List<Symbol> pendingTypes;
    private List<Symbol> pendingValues;
    private Map<String, Symbol> genericTypes;
    
    private Map<TypeSpec, TypeInfo> resolvedTypeMap;
    private NodeVisitor stmtVisitor;
    
    private Stack<List<GenericParam>> genericStack;
    
    public Resolver(PhaseResult result, CompilationUnit unit) {
        this.result = result;
        this.unit = unit;
        
        this.moduleSymbols = new ArrayList<>();
        this.moduleFuncs = new ArrayList<>();
        this.pendingTypes = new ArrayList<>();
        this.pendingValues = new ArrayList<>();
        this.genericTypes = new HashMap<>();
        
        this.resolvedModules = new HashMap<>();
        this.resolvedTypeMap = new IdentityHashMap<>();
        
        this.genericStack = new Stack<>();
        
        this.stmtVisitor = new StmtVisitor();
    }

    public Program resolveTypes() {
        Module module = resolveModule(this.unit.getMain());                
        return new Program(module, 
                           resolvedModules, 
                           module.getSymbols(), 
                           this.resolvedTypeMap);
    }
        
    private Module resolveModule(ModuleStmt moduleStmt) {
        Module module = createModule(moduleStmt);
        addBuiltins(module);
        
        enterModule(module);
        
        for(ImportStmt imp : moduleStmt.imports) {
            resolveImport(module, imp);
        }
        
        List<Symbol> syms = null;
        do {
            syms = new ArrayList<>(this.moduleSymbols);
            this.moduleSymbols.clear();
            
            for(Symbol sym : syms) {
                resolveSym(sym);
            }
            
            for(Symbol sym : syms) {
                finishResolveSym(sym);
            }
            
        }
        while(!syms.isEmpty());
        
        for(Symbol sym : this.pendingValues) {
            resolveValue(sym);
        }
        
        for(Symbol sym : this.moduleFuncs) {            
            resolveFunc(sym);
        }
        
        // add all of the generic types
        for(Symbol sym : this.genericTypes.values()) {
            if(sym.isGenericTemplate()) {
                continue;
            }
            
            if(sym.kind == SymbolKind.FUNC) {
                resolveFunc(sym);
            }
            
            moduleStmt.declarations.add(sym.decl);
        }
        
        return module;
    }
    
    private void resolveFunc(Symbol sym) {
        if(sym.decl.kind != DeclKind.FUNC) {
            return;
        }
        
        resolveSym(sym);
        
        FuncDecl funcDecl = (FuncDecl) sym.decl;
        if(funcDecl.hasGenericParams()) {
            return;
        }
        
        funcDecl.visit(this.stmtVisitor);
    }
    
    private Module enterModule(Module module) {
        Module oldModule = this.current;
        this.current = module;
        return oldModule;
    }
    
    private void leaveModule(Module oldModule) {
        this.current = oldModule;
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
        module.addBuiltin(TypeInfo.NULL_TYPE);
        module.addBuiltin(TypeInfo.VOID_TYPE);
    }
    
    private Module createModule(ModuleStmt moduleStmt) {
        String moduleName = moduleStmt.name;
        if(resolvedModules.containsKey(moduleName)) {
            return resolvedModules.get(moduleName);
        }
        
        Module module = new Module(null, null, result, moduleStmt, moduleName);
        resolvedModules.put(moduleName, module);
        
        List<Decl> nonGenericDecls = new ArrayList<>();
        
        for(Decl decl : moduleStmt.declarations) {
            switch(decl.kind) {
                case CONST:                                        
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
                    this.moduleSymbols.add(sym);
                    break;
                }
                case STRUCT: {
                    StructDecl structDecl = (StructDecl) decl;
                    Symbol sym = module.declareStruct(structDecl, structDecl.name);
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
        
        // we don't want the upstream systems to deal with the Generic types
        // they are resolved with proper substituted types
        moduleStmt.declarations = nonGenericDecls;        
        
        return module;
    }
    
    private void resolveImport(Module parentModule, ImportStmt importStmt) {
        String moduleName = importStmt.alias != null ? importStmt.alias : importStmt.moduleName;
        
        if(parentModule.getModule(moduleName) != null) {
            result.addError(importStmt, "duplicate import of module '%s'", moduleName);
            return;
        }
        
        ModuleStmt moduleStmt = unit.getImports().get(importStmt.moduleName);
        
        Module module = createModule(moduleStmt);
        parentModule.importModule(importStmt, module, importStmt.alias);
    }
    
    private void resolveSym(Symbol sym) {
        if(sym.state == ResolveState.RESOLVED) {
            return;
        }
        
        if(sym.state == ResolveState.RESOLVING) {
            this.result.addError(sym.decl.getSrcPos(), "Cyclic dependency with '%s'", sym.name);
            return;
        }
        
        sym.state = ResolveState.RESOLVING;
        Module oldModule = enterModule(sym.declared);
        switch(sym.kind) {
            case TYPE:
                switch(sym.decl.kind) {
                    case ENUM:
                        EnumDecl enumDecl = (EnumDecl)sym.decl;
                        sym.type = enumTypeInfo(enumDecl);     
                        sym.type.sym = sym;
                        break;
                    case TYPEDEF:
                        TypedefDecl typedefDecl = (TypedefDecl) sym.decl;
                        sym.type = resolveTypeSpec(typedefDecl.type);
                        sym.type.sym = sym;
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
            case FUNC:
                FuncDecl funcDecl = (FuncDecl) sym.decl;
                sym.type = funcTypeInfo(funcDecl);
                sym.type.sym = sym;
                break;
                
            case CONST:
            case VAR:         
                if(!sym.isLocal()) {
                    this.pendingValues.add(sym);
                    return;
                }
                break;
            default:
                break;
        
        }        
        sym.state = ResolveState.RESOLVED;
        leaveModule(oldModule);
    }        
    
    private boolean typeCheck(SrcPos pos, TypeInfo a, TypeInfo b) {
        return typeCheck(pos, a, b, false);
    }
    
    private boolean typeCheck(SrcPos pos, TypeInfo a, TypeInfo b, boolean isCasted) {
        if(isCasted) {
            if(!TypeInfo.isPtrLike(a) || !TypeInfo.isPtrLike(b)) {
                if(!a.canCastTo(b) && !b.canCastTo(a)) {
                    result.addError(pos,
                            "'%s' can't be casted to '%s'", b, a);
                    return false;
                }
            }
        }
        else if(!a.canCastTo(b)) {
            result.addError(pos,
                    "'%s' is not of type '%s'", b, a);
            return false;
        }
        
        return true;
    }
    
    private void resolveValue(Symbol sym) {
        if(sym.kind != SymbolKind.CONST && sym.kind != SymbolKind.VAR) {            
            return;
        }
        
        Decl decl = sym.decl;
        switch(sym.kind) {
            case CONST: {
                ConstDecl constDecl = (ConstDecl) decl;
                sym.type = resolveValueDecl(decl, constDecl.type, constDecl.expr, !sym.isForeign());
                sym.state = ResolveState.RESOLVED;
                break;
            }
            case VAR: {
                VarDecl varDecl = (VarDecl) decl;
                sym.type = resolveValueDecl(decl, varDecl.type, varDecl.expr, false);
                sym.state = ResolveState.RESOLVED;
                break;
            }
            default:
        }
    }
    
    private TypeInfo resolveValueDecl(Decl decl, TypeSpec typeSpec, Expr expr, boolean isConstant) {
        if(expr == null && isConstant) {
            this.result.addError(decl.getSrcPos(), "const declaration must have an assignment (unless it is '@foreign')");
            return null;
        }
        
        TypeInfo declaredType = null;
        if(typeSpec != null) {
            declaredType = resolveTypeSpec(typeSpec);
        }


        TypeInfo inferredType = null;
        if(expr != null) {
            Operand op = resolveExpr(expr);
            inferredType = op.type;
        }
        
        if(declaredType != null && inferredType != null) {            
            if(!typeCheck(expr.getSrcPos(), declaredType, inferredType)) {
                return null;
            }
        }
        
        // TODO: Decay array types
        
        return inferredType != null ? inferredType : declaredType;
    }
    
    private void finishResolveSym(Symbol sym) {
        if(sym.decl.kind != DeclKind.STRUCT && sym.decl.kind != DeclKind.UNION) {
            //this.result.addError(sym.decl.getSrcPos(), "Attempting to finish resolving a declaration of type: '%s'", sym.decl.kind.name());
            return;
        }
        
        AggregateDecl aggDecl = (AggregateDecl) sym.decl;
        if(aggDecl.hasGenericParams()) {
            this.genericStack.add(aggDecl.genericParams);
        }
        
        AggregateTypeInfo aggInfo = sym.type.as();        
        for(FieldStmt field : aggDecl.fields) {
            FieldInfo info = resolveFieldInfo(sym, field);
            aggInfo.fieldInfos.add(info);
        }        
        
        if(aggDecl.hasGenericParams()) {
            this.genericStack.pop();
        }
    }
    
    
    
    private FieldInfo resolveFieldInfo(Symbol parentSym, FieldStmt stmt) {
        Module module = parentSym.declared;
        
        if (stmt instanceof VarFieldStmt) {
            VarFieldStmt var = (VarFieldStmt) stmt;
            TypeInfo type = resolveTypeSpec(var.type);
            return new FieldInfo(type, var.name, var.attributes, null);
        }
        
        if (stmt instanceof StructFieldStmt) {
            StructFieldStmt struct = (StructFieldStmt) stmt;
            Symbol sym = module.declareStruct(struct.decl, struct.decl.name);
            this.moduleSymbols.add(sym);
            
            resolveSym(sym);            
            return new FieldInfo(sym.type, struct.decl.name, struct.decl.attributes, null);
        }
        
        if (stmt instanceof UnionFieldStmt) {
            UnionFieldStmt union = (UnionFieldStmt) stmt;
            Symbol sym = module.declareUnion(union.decl, union.decl.name);
            this.moduleSymbols.add(sym);
            
            resolveSym(sym);
            return new FieldInfo(sym.type, union.decl.name, union.decl.attributes, null);
        }
        
        if (stmt instanceof EnumFieldStmt) {
            EnumFieldStmt enm = (EnumFieldStmt) stmt;
            Symbol sym = module.declareEnum(enm.decl, enm.decl.name);
            this.moduleSymbols.add(sym);
            
            resolveSym(sym);
            return new FieldInfo(sym.type, enm.decl.name, enm.decl.attributes, null);
        }
        
        this.result.addError(stmt.getSrcPos(), "Unknown field item declaration");
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
                
                Symbol sym = this.current.getType(nameSpec.name);
                if(sym == null) {
                    this.result.addError(typeSpec.pos, "Unresolved type name '%s'", nameSpec.name);
                    return null;
                }
                
                if(!sym.isType()) {
                    this.result.addError(typeSpec.pos, "'%s' must  be a type", nameSpec.name);
                    return null;
                }
                
                resolveSym(sym);
                
                if(!nameSpec.hasGenericArgs()) {
                    this.resolvedTypeMap.put(typeSpec, sym.type);
                    return sym.type;
                }
                
                List<TypeSpec> genArgs = nameSpec.genericArgs;                
                String genericsName = nameSpec.toGenericsName();
                
                Symbol genericSym = this.genericTypes.get(genericsName);                
                if(genericSym == null) {
                    // Duplicate the Decl with the substituted generic arguments
                    switch(sym.decl.kind) {
                        case FUNC: {
                            FuncDecl newDecl = Generics2.createFuncDecl((FuncDecl)sym.decl, genericsName, genArgs);
                            genericSym = this.current.declareFunc(newDecl, genericsName);
                            break;
                        }
                        case STRUCT: {
                            StructDecl newDecl = Generics2.createStructDecl((StructDecl)sym.decl, genericsName, genArgs);
                            genericSym = this.current.declareStruct(newDecl, genericsName);                            
                            break;
                        }
                        case UNION:
                            UnionDecl newDecl = Generics2.createUnionDecl((UnionDecl)sym.decl, genericsName, genArgs);
                            genericSym = this.current.declareUnion(newDecl, genericsName);
                            break;
                        default: {
                            this.result.addError(typeSpec.pos, "'%s' can't get a generic type", nameSpec.name);
                            return null;
                        }
                    }
                    this.genericTypes.put(genericsName, genericSym);   
                    
                    if(hasGenericParam(genArgs)) {
                        genericSym.markAsGenericTemplate();
                    }
                }
                
                resolveSym(genericSym);
                finishResolveSym(genericSym);
                
                this.resolvedTypeMap.put(typeSpec, genericSym.type);
                return genericSym.type;
            }
            case ARRAY: {
                ArrayTypeSpec arraySpec = typeSpec.as();
                TypeInfo arrayOf = resolveTypeSpec(arraySpec.base);
                long length = -1;
                if(arraySpec.numElements != null) {
                    Operand operand = resolveConstExpr(arraySpec.numElements);
                    if(!TypeInfo.isInteger(operand.type)) {
                        this.result.addError(arraySpec.numElements.getSrcPos(), "Array size expression must be an integer type");
                        return null;    
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
                this.result.addError(typeSpec.pos, "Unknown type specification");
                return null;
            }
        
        }
    }
    
    private TypeInfo funcTypeInfo(FuncDecl funcDecl) {
        if(funcDecl.hasGenericParams()) {
            this.genericStack.add(funcDecl.genericParams);
        }
        
        ParametersStmt params = funcDecl.params;
        
        List<ParamInfo> args = new ArrayList<>();
        for(ParameterDecl param : params.params) {
            TypeInfo arg = resolveTypeSpec(param.type);
            args.add(new ParamInfo(arg, param.name, param.defaultValue, param.attributes));
        }
        
        TypeInfo ret = resolveTypeSpec(funcDecl.returnType);
        
        if(funcDecl.hasGenericParams()) {
            this.genericStack.pop();
        }
        
        return new FuncTypeInfo(funcDecl.name, ret, args, funcDecl.flags, funcDecl.genericParams);
    }
            
    private TypeInfo enumTypeInfo(EnumDecl enumDecl) {
        EnumTypeInfo enumInfo = new EnumTypeInfo(enumDecl.name, enumDecl.fields);
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
        case TYPE_IDENTIFIER:
            break;
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
        //Symbol sym = this.current.getFuncType(expr.type.name);
        Symbol sym = (type != null) ? type.sym : null;
        if(sym == null) {
            this.result.addError(expr, "unknown function '%s'", expr.type.name);
            return null;
        }
        
        Operand op = Operand.op(sym.type);
        expr.resolveTo(op);
        expr.sym = sym;
        
        return op;
    }
    
    private Operand resolveIdentifier(IdentifierExpr expr) {        
        Symbol sym = this.current.currentScope().getSymbol(expr.type.name);
        if(sym == null) {
            this.result.addError(expr, "unknown variable '%s'", expr.type.name);
            return null;
        }
        
        Operand op = Operand.op(sym.type);
        expr.resolveTo(op);
        expr.sym = sym;
        
        return op;
    }
    
    private Operand resolveUnaryExpr(UnaryExpr expr) {
        Operand op = resolveExpr(expr.expr);
        switch(expr.operator) {
            case STAR: {
                TypeInfo type = op.type;
                if(!TypeInfo.isPtrLike(type)) {                            
                    this.result.addError(expr, "'%s' is not a pointer type", type);
                    return null;
                }
                
                break;
            }
            default:
        }
        
        Operand operand = Operand.op(new PtrTypeInfo(op.type));
        expr.resolveTo(operand);
        return operand;
    }
    
    private Operand resolveTernaryExpr(TernaryExpr c) {
        Operand cond = resolveExpr(c.cond);
        if(!TypeInfo.isBooleanable(cond.type)) {
            this.result.addError(c.cond, "must be a boolean expression");
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
                this.result.addError(c.object, "invalid index into '%s'", objectKind.name());
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
                break;
            default: {
                this.result.addError(c.index, "'%s' invalid index value", indexKind.name());
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
                this.result.addError(c, "invalid index into '%s'", objectKind.name());
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
                break;
            default: {
                this.result.addError(c, "'%s' invalid index value", indexKind.name());
                return null;
            }
        }
        
        
        Operand result = Operand.op(baseObj);
        c.resolveTo(result);
        
        return result;
    }
    
    private TypeInfo getAggregateField(SrcPos pos, AggregateTypeInfo aggInfo, NameTypeSpec name, boolean allowMethods, boolean error) {
        
        // Simple aggregate field access
        FieldInfo field = aggInfo.getField(name.name);
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
        Symbol sym = this.current.getMethodType(aggInfo, name.name);
        if(sym == null || !allowMethods) {
            if(error) {
                this.result.addError(pos, "'%s' does not have field '%s'", aggInfo.name, name);
            }
            return null;                
        }
                
        FuncTypeInfo funcInfo = sym.type.as();
        if(funcInfo.parameterDecls.isEmpty()) {     
            if(error) {
                this.result.addError(pos, "'%s' does not have a parameter of '%s'", funcInfo.getName(), aggInfo.name);
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
        
        if(error) {
            this.result.addError(pos, "'%s' does not have field '%s'", aggInfo.name, name);
        }
        
        return null;
    }
    
    private Operand resolveTypeOfExpr(TypeOfExpr c) {
        return null;
    }
    
    private Operand resolveSizeOfExpr(SizeOfExpr c) {
        return null;
    }
    
    private Operand resolveGroupExpr(GroupExpr c) {
        Operand op = resolveExpr(c.expr);
        Operand result = Operand.op(op.type);
        c.resolveTo(result);
        return result;
    }
    
    private Operand resolveSetExpr(SetExpr c) {
        Operand objOp = resolveExpr(c.object);
        //Operand fieldOp = resolveExpr(c.field);
        Operand valOp = resolveExpr(c.value);
        
        if(objOp.type.isKind(TypeKind.Enum)) {
            this.result.addError(c.object, "can't reassign enum '%s'", objOp.type.name);
            return null;
        }
        
        AggregateTypeInfo aggInfo = objOp.type.as();
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
                this.result.addError(c.field, "'%s' does not have field '%s'", objOp.type, c.field.type.name);
                return null;
            }
            
            // TODO: Calculate proper enum value
            Operand val = resolveConstExpr(field.value);
            fieldOp = Operand.opConst(enumInfo.getFieldType(), val.val);
        }
        else {
            if(!TypeInfo.isFieldAccessible(objOp.type)) {
                this.result.addError(c.field, "'%s' can't be accessed with field '%s'", objOp.type, c.field.type.name);
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
        
        Operand result = Operand.op(fieldOp.type);
        c.resolveTo(result);
        
        return result;
    }
    
    private Operand resolveFuncCallExpr(FuncCallExpr c) {
        Operand op = resolveExpr(c.object);
        if(!op.type.isKind(TypeKind.Func) && !op.type.isKind(TypeKind.FuncPtr)) {
            this.result.addError(c.object, "'%s' is not a function", op.type.sym.name);
            return null;
        }
        
        boolean isMethod = false;
        int numberOfDefaultArgs = 0;
        List<Expr> suppliedArguments = new ArrayList<>(c.arguments);
        
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

        boolean isMethodCall = isMethod && isMethodSyntax(c, funcPtr, suppliedArguments);
                
        checkNumberOfArgs(c, op.type.name, funcPtr, suppliedArguments.size(), numberOfDefaultArgs);
        
        
        // see if this is method call syntax
        
        // type inference for generic functions 
        /*if(funcPtr.hasGenerics() && c.genericArgs.isEmpty()) {            
            funcPtr = inferFuncCallExpr(c, funcPtr, suppliedArguments, isMethodCall);            
        }*/
               
        
        int i = 0;
        for(; i < funcPtr.params.size(); i++) {
            TypeInfo p = funcPtr.params.get(i);
            
            if(i < suppliedArguments.size()) {
                Expr arg = suppliedArguments.get(i);
                resolveExpr(arg);
                
                typeCheck(arg.getSrcPos(), arg.getResolvedType().type, p);
            }
        }
        
        if(funcPtr.isVararg) {
            for(; i < suppliedArguments.size(); i++) {
                Expr arg = suppliedArguments.get(i);
                resolveExpr(arg);
            }                
        }
                
        FuncTypeInfo funcInfo = op.type.as();
        Operand retOp = Operand.op(funcInfo.returnType);
        c.resolveTo(retOp);
        
        return retOp;
    }
    
    private Operand resolveCastExpr(CastExpr c) {
        TypeInfo type = resolveTypeSpec(c.castTo);
        Operand operand = Operand.op(type);
        
        resolveExpr(c.expr);
        
        return operand;
    }
    
    private Operand resolveBinaryExpr(BinaryExpr expr) {
        Operand left = resolveExpr(expr.left);
        Operand right = resolveExpr(expr.right);
        
        typeCheck(expr.getSrcPos(), left.type, right.type);
        typeCheck(expr.getSrcPos(), right.type, left.type);
        
        TypeInfo leftType = left.type;
        TypeInfo rightType = right.type;
        
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
                    this.result.addError(expr.left, "illegal, left operand has type '%s'", leftType.getName());
                }
                
                if(!TypeInfo.isInteger(rightType)) {
                    this.result.addError(expr.right, "illegal, right operand has type '%s'", rightType.getName());
                }
                break;
            }
                
            case AND:
            case OR:
                break;
            
            case EQUALS_EQUALS:
            case NOT_EQUALS:
                break;
                
            case GREATER_EQUALS:
            case GREATER_THAN:
            case LESS_EQUALS:
            case LESS_THAN:
                if(!TypeInfo.isNumber(leftType)) {
                    this.result.addError(expr.left, "illegal, left operand has type '%s'", leftType.getName());
                }
                
                if(!TypeInfo.isNumber(rightType)) {
                    this.result.addError(expr.right, "illegal, right operand has type '%s'", rightType.getName());
                }
                
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
                    this.result.addError(expr.left, "illegal, left operand has type '%s'", leftType.getResolvedType().getName());
                }
                
                if(!TypeInfo.isNumber(rightType) && !rightType.isKind(TypeKind.Ptr) && !rightType.isKind(TypeKind.Str)) {
                    this.result.addError(expr.right, "illegal, right operand has type '%s'", rightType.getResolvedType().getName());
                }
                
                break;
        default:
            break;
        
        }

        // TODO: Cast to appropriate type
        Operand op = Operand.op(leftType);
        expr.resolveTo(op);
        
        return op;
    }
    
    private Operand resolveArrayInitExpr(ArrayInitExpr expr) {
        TypeInfo type = resolveTypeSpec(expr.type);
        Operand operand = Operand.op(type);
        expr.resolveTo(operand);
        
        if(expr.values != null) {
            for(Expr val : expr.values) {
                resolveExpr(val);
            }
        }
        
        return operand;
    }
    
    private Operand resolveArrayDesExpr(ArrayDesignationExpr expr) {
        resolveExpr(expr.index);
        resolveExpr(expr.value);
        // should probably not be an expr??
        return null;
    }
    
    private Operand resolveInitArgExpr(InitArgExpr expr) {
        Operand op = null;
        if(expr.value != null) {
            op = resolveExpr(expr.value);            
            expr.value.resolveTo(op);            
        }
                
        return op;
    }
    
    private Operand resolveInitExpr(InitExpr expr) {
        TypeInfo type = resolveTypeSpec(expr.type);
        Operand operand = Operand.op(type);        
        expr.resolveTo(operand);
                
        for(InitArgExpr arg : expr.arguments) {
            resolveInitArgExpr(arg);            
        }
        
        return operand;
    }
    
    private Operand resolveConstExpr(Expr expr) {
        Operand op = resolveExpr(expr);
        if(op != null && op.isConst) {
            return op;
        }
        
        return null;
    }
    
    private void checkConstant(Expr expr) {
        Operand op = expr.getResolvedType();
        if(op == null || op.type == null) {
            return;
        }
        
        if(op.type.sym.isConstant()) {
            this.result.addError(expr, "can't reassign constant variable '%s'", op.type.sym.name);            
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
            this.result.addError(expr, "'%s' called with too few arguments", funcName);
            return;
        }
        
        if(funcInfo.isVararg) {
            return;
        }
        
        if(maxNumOfArgs < numberOfSuppliedArgs) {            
            this.result.addError(expr, "'%s' called with too many arguments", funcName);
        }
    }
    
    private void addTypeToScope(Decl p, Scope scope, TypeInfo currentType) {        
        AggregateTypeInfo aggInfo = (currentType.isKind(TypeKind.Ptr)) 
                                        ? ((PtrTypeInfo)currentType).ptrOf.as()
                                        : currentType.as();
                                        
        for(FieldInfo field : aggInfo.fieldInfos) {
            Symbol sym = scope.addSymbol(this.current, p, field.name, Symbol.IS_USING);
            sym.type = field.type;
        }
        
        if(aggInfo.hasUsingFields()) {
            for(FieldInfo field : aggInfo.usingInfos) {
                addTypeToScope(p, scope, field.type);
            }
        }
    }
    
    private Symbol addSymbol(Decl d, TypeSpec typeSpec) {
        Scope scope = current.currentScope();
        Symbol sym = scope.addSymbol(current, d, d.name);
        
        TypeInfo type = resolveTypeSpec(typeSpec);
        sym.type = type;
        
        resolveSym(sym);
        return sym;
    }
    
    private Symbol addSymbol(Decl d, TypeInfo type) {
        Scope scope = current.currentScope();
        Symbol sym = scope.addSymbol(current, d, d.name);        
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
            return argumentType.getResolvedType();
        }
        
        if(!paramType.getKind().equals(argumentType.getKind())) {
            return null;
        }
        
        int index = 0;
        if(paramType.hasGenericArgs()) {
            List<TypeInfo> genericArgs = paramType.getGenericArgs();
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
                for(FieldInfo field : aggInfo.fieldInfos) {
                    TypeInfo fieldType = inferredType(genericName, field.type, argumentAggInfo.getField(field.name).type);
                    if(fieldType != null) {
                        return fieldType;
                    }
                }
                break;
            }
            case Identifier:
                break;
            default:
                break;            
        }
        
        return null;
        
    }
    
    /*
    private FuncPtrTypeInfo inferFuncCallExpr(FuncCallExpr expr, FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments, boolean isMethodCall) {
        for(Expr arg : expr.arguments) {
            resolveExpr(arg);
        }
                    
        expr.genericArgs = new ArrayList<>(funcPtr.genericParams.size());
        
        for(GenericParam p : funcPtr.genericParams) {
            for(int j = 0; j < funcPtr.params.size(); j++) {
                TypeInfo paramType = funcPtr.params.get(j);
                
                if(j >= suppliedArguments.size()) {
                    break;
                }
                
                TypeInfo inferredType = inferredType(p.name, paramType, suppliedArguments.get(j).getResolvedType().type);
                if(inferredType != null) {
                    expr.genericArgs.add(inferredType);
                    break;
                }
            }
        }
        
        Expr objectExpr = expr.object;
        if(isMethodCall) {
            GetExpr getExpr = (GetExpr) expr.object;
            objectExpr = getExpr.field;
        }
        
        objectExpr.unresolve();
        if(objectExpr instanceof IdentifierExpr) {
            IdentifierExpr idExpr = (IdentifierExpr)objectExpr;
            idExpr.setGenericArgs(expr.genericArgs);
        }
        
        expr.object.visit(this);
        
        // unable to infer types
        if(!expr.object.isResolved()) {
            for(int i = expr.genericArgs.size(); i < funcPtr.genericParams.size(); i++) {
                this.result.addError(expr, "unable to infer generic parameter '%s'", funcPtr.genericParams.get(i));
            }
            return funcPtr;
        }
        
        TypeInfo type = expr.object.getResolvedType();            
        if(type.isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = type.as();
            return funcInfo.asPtr();
        }
        
        return type.as();
    }*/
    
    private boolean isMethodSyntax(FuncCallExpr expr, FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments) {
        if(!(expr.object instanceof GetExpr)) {
            return false;
        }
        
        GetExpr getExpr = (GetExpr) expr.object;
        if(!getExpr.field.getResolvedType().type.isKind(TypeKind.Func)) {
            return false;
        }
        
        getExpr.isMethodCall = true;
        if(!funcPtr.params.isEmpty()) {
            TypeInfo paramInfo = funcPtr.params.get(0);                    
            TypeInfo argInfo = getExpr.object.getResolvedType().type;
            
            
            // Determine if we need to promote the object to a
            // pointer depending on what the method is expecting as an
            // argument
            if(TypeInfo.isPtrAggregate(paramInfo)) {
                if(!TypeInfo.isPtrAggregate(argInfo)) {
                    
                    // Can't take the address of an R-Value 
                    if(getExpr.object instanceof FuncCallExpr) {
                        this.result.addError(getExpr.object, 
                                "cannot take the return value address of '%s' as it's an R-Value", getExpr.field.type.name);
                    }
                    
                    getExpr.object = new UnaryExpr(TokenType.BAND, new GroupExpr(getExpr.object));
                    resolveExpr(getExpr.object);
                }
            }
        }
        
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
        public void visit(IfStmt stmt) {
            stmt.condExpr.visit(this);
            
            current.pushScope();
            stmt.thenStmt.visit(this);
            current.popScope();
            
            if(stmt.elseStmt != null) {
                current.pushScope();
                stmt.elseStmt.visit(this);
                current.popScope();
            }
        }

        @Override
        public void visit(WhileStmt stmt) {
            stmt.condExpr.visit(this);
            
            current.pushScope();
            stmt.bodyStmt.visit(this);
            current.popScope();
        }

        @Override
        public void visit(DoWhileStmt stmt) {
            current.pushScope();
            stmt.bodyStmt.visit(this);
            current.popScope();
            
            stmt.condExpr.visit(this);
        }

        @Override
        public void visit(ForStmt stmt) {
            current.pushScope();
            if(stmt.initStmt != null) stmt.initStmt.visit(this);
            if(stmt.condExpr != null) stmt.condExpr.visit(this);
            if(stmt.postStmt != null) stmt.postStmt.visit(this);
            
            stmt.bodyStmt.visit(this);
            current.popScope();
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
            }
        }

        @Override
        public void visit(BlockStmt stmt) {
            current.pushScope();
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
            current.popScope();
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
            for(ParameterDecl p : stmt.params) {
                p.visit(this);
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
        }

        @Override
        public void visit(LabelStmt stmt) {
        }
        
        @Override
        public void visit(StructDecl d) {
        }
        
        @Override
        public void visit(TypedefDecl d) {
        }
        
        @Override
        public void visit(UnionDecl d) {
        }

        @Override
        public void visit(EnumDecl d) {            
        }
        
        @Override
        public void visit(ConstDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
                addSymbol(d, d.expr.getResolvedType().type);
            }
            else {
                addSymbol(d, d.type);
            }
        }

        @Override
        public void visit(VarDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
                addSymbol(d, d.expr.getResolvedType().type);
            }
            else {
                addSymbol(d, d.type);
            }
        }

        @Override
        public void visit(FuncDecl d) {  
            Symbol sym = d.sym;
            Module oldModule = enterModule(sym.declared);       
            current.pushScope();            
            for(ParameterDecl p : d.params.params) {
                p.visit(this);
            }
            
            if(d.bodyStmt != null) {
                d.bodyStmt.visit(this);
            }
            
            current.popScope();
            leaveModule(oldModule);
        }



        @Override
        public void visit(ParameterDecl d) {
            if(d.defaultValue != null) {
                d.defaultValue.visit(this);
            }
            
            d.sym = addSymbol(d, d.type);

            if(d.attributes.isUsing()) {
                TypeInfo type = d.sym.type;
                if (!TypeInfo.isAggregate(type) && 
                    !TypeInfo.isPtrAggregate(type)) {
                    result.addError(d, "'%s' is not an aggregate type (or pointer to an aggregate), can't use 'using'", d.name);
                }
                else {                    
                    addTypeToScope(d, current.currentScope(), type);
                }
            }
        }

        @Override
        public void visit(CastExpr expr) {
            resolveCastExpr(expr);            
        }

        @Override
        public void visit(SizeOfExpr expr) {
            resolveSizeOfExpr(expr);
        }

        @Override
        public void visit(TypeOfExpr expr) {
            resolveTypeOfExpr(expr);
        }

        @Override
        public void visit(InitArgExpr expr) {
            resolveInitArgExpr(expr);
        }

        @Override
        public void visit(InitExpr expr) {
            resolveInitExpr(expr);
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
            resolveGroupExpr(expr);
        }

        @Override
        public void visit(FuncCallExpr expr) {
            resolveFuncCallExpr(expr);
        }

        @Override
        public void visit(IdentifierExpr expr) {
            resolveIdentifier(expr);
        }

        @Override
        public void visit(FuncIdentifierExpr expr) {
            resolveFuncIdentifier(expr);
        }

        /* (non-Javadoc)
         * @see litac.ast.NodeVisitor#visit(litac.ast.Expr.TypeIdentifierExpr)
         */
        @Override
        public void visit(TypeIdentifierExpr expr) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void visit(GetExpr expr) {
            resolveGetExpr(expr);
        }

        @Override
        public void visit(SetExpr expr) {
            resolveSetExpr(expr);
        }

        @Override
        public void visit(UnaryExpr expr) {
            resolveUnaryExpr(expr);
        }

        @Override
        public void visit(BinaryExpr expr) {
            resolveBinaryExpr(expr);
        }

        @Override
        public void visit(TernaryExpr expr) {
            resolveTernaryExpr(expr);
        }

        @Override
        public void visit(ArrayInitExpr expr) {
            resolveArrayInitExpr(expr);
        }

        @Override
        public void visit(ArrayDesignationExpr expr) {
            resolveArrayDesExpr(expr);
        }

        @Override
        public void visit(SubscriptGetExpr expr) {
            resolveSubGetExpr(expr);
        }

        @Override
        public void visit(SubscriptSetExpr expr) {
            resolveSubSetExpr(expr);
        }
    }
}
