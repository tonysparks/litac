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
import litac.compiler.Symbol.ResolveState;
import litac.compiler.Symbol.SymbolKind;

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
        
        for(Symbol sym : this.moduleFuncs) {
            resolveSym(sym);
        }

        for(Symbol sym : this.pendingValues) {
            resolveValue(sym);
        }
        
        return module;
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
        
        for(Decl decl : moduleStmt.declarations) {
            switch(decl.kind) {
                case CONST:                                        
                case VAR: {
                    Symbol sym = module.addIncomplete(decl);
                    this.pendingValues.add(sym);
                    break;
                }
                case FUNC: {
                    FuncDecl funcDecl = (FuncDecl) decl;
                    Symbol sym = module.declareFunc(funcDecl, funcDecl.name);
                    //this.moduleSymbols.add(sym);
                    this.moduleFuncs.add(sym);
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
                    break;
                }
                case TYPEDEF: {
                    TypedefDecl typeDecl = (TypedefDecl) decl;
                    Symbol sym = module.declareTypedef(typeDecl, typeDecl.alias);
                    this.moduleSymbols.add(sym);
                    break;
                }
                case UNION: {
                    UnionDecl unionDecl = (UnionDecl) decl;
                    Symbol sym = module.declareUnion(unionDecl, unionDecl.name);
                    this.moduleSymbols.add(sym);
                    break;
                }
                default:
                    break;
            }
        }      
        
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
                sym.state = ResolveState.RESOLVED;
                break;
            case FUNC:
                FuncDecl funcDecl = (FuncDecl) sym.decl;
                sym.type = funcTypeInfo(funcDecl);
                sym.type.sym = sym;
                
                sym.state = ResolveState.RESOLVED;
                break;
                
            case CONST:
            case VAR:                
                this.pendingValues.add(sym);
                break;
            default:
                break;
        
        }        
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
        if(sym.kind != SymbolKind.CONST || sym.kind != SymbolKind.VAR) {            
            return;
        }
        
        Decl decl = sym.decl;
        switch(sym.kind) {
            case CONST: {
                ConstDecl constDecl = (ConstDecl) decl;
                sym.type = resolveValueDecl(decl, constDecl.type, constDecl.expr, !sym.isForeign());
                break;
            }
            case VAR: {
                VarDecl varDecl = (VarDecl) decl;
                sym.type = resolveValueDecl(decl, varDecl.type, varDecl.expr, false);
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
            this.result.addError(sym.decl.getSrcPos(), "Attempting to finish resolving a declaration of type: '%s'", sym.decl.kind.name());
            return;
        }
        
        AggregateDecl aggDecl = (AggregateDecl) sym.decl;
        AggregateTypeInfo aggInfo = sym.type.as();        
        for(FieldStmt field : aggDecl.fields) {
            FieldInfo info = resolveFieldInfo(sym, field);
            aggInfo.fieldInfos.add(info);
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
    
    private TypeInfo resolveTypeSpec(TypeSpec typeSpec) {
        if(typeSpec == null) {
            return TypeInfo.VOID_TYPE;
        }
        
        switch(typeSpec.kind) {
            case NAME: {
                NameTypeSpec nameSpec = typeSpec.as();
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
                
                String genericsName = nameSpec.toGenericsName();
                Symbol genericSym = this.genericTypes.get(genericsName);
                if(genericSym == null) {
                    switch(sym.decl.kind) {
                        case FUNC: {                                
                            genericSym = this.current.declareFunc((FuncDecl)sym.decl, genericsName);
                            break;
                        }
                        case STRUCT: {
                            genericSym = this.current.declareStruct((StructDecl)sym.decl, genericsName);
                            break;
                        }
                        case UNION:
                            genericSym = this.current.declareUnion((UnionDecl)sym.decl, genericsName);
                            break;
                        default: {
                            this.result.addError(typeSpec.pos, "'%s' can't get a generic type", nameSpec.name);
                            return null;
                        }
                    }
                    this.genericTypes.put(genericsName, genericSym);                        
                }
                
                resolveSym(genericSym);
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
        ParametersStmt params = funcDecl.params;
        
        List<ParamInfo> args = new ArrayList<>();
        for(ParameterDecl param : params.params) {
            TypeInfo arg = resolveTypeSpec(param.type);
            args.add(new ParamInfo(arg, param.name, param.defaultValue, param.attributes));
        }
        
        TypeInfo ret = resolveTypeSpec(funcDecl.returnType);        
        return new FuncTypeInfo(funcDecl.name, ret, args, funcDecl.flags, funcDecl.genericParams);
    }
            
    private TypeInfo enumTypeInfo(EnumDecl enumDecl) {
        EnumTypeInfo enumInfo = new EnumTypeInfo(enumDecl.name, enumDecl.fields);
        return enumInfo;
    }
    
    private Operand resolveExpr(Expr expr) {
        switch(expr.getKind()) {
        case FUNC_IDENTIFIER:
            break;
        case IDENTIFER:
            break;
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
    
    private FieldInfo getAggregateField(SrcPos pos, AggregateTypeInfo aggInfo, String name) {
        FieldInfo field = aggInfo.getField(name);
        if(field == null) {
            this.result.addError(pos, "'%s' does not have field name '%s'", aggInfo.name, name);
            return null;
        }
        
        return field;
    }
    
    private Operand resolveTypeOfExpr(TypeOfExpr c) {
        return null;
    }
    
    private Operand resolveSizeOfExpr(SizeOfExpr c) {
        return null;
    }
    
    private Operand resolveGroupExpr(GroupExpr c) {
        return null;
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
        FieldInfo field = getAggregateField(c.getSrcPos(), aggInfo, c.field.variable);
        if(field != null) {
            c.field.resolveTo(Operand.op(field.type));
        }
        
        typeCheck(c.getSrcPos(), valOp.type, field.type);
        
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
            EnumFieldInfo field = enumInfo.getField(c.field.variable);
            if(field == null) {
                this.result.addError(c.field, "'%s' does not have filed '%s'", objOp.type, c.field.variable);
                return null;
            }
            
            // TODO: Calculate proper enum value
            Operand val = resolveConstExpr(field.value);
            fieldOp = Operand.opConst(enumInfo.getFieldType(), val.val);
        }
        else {
            AggregateTypeInfo aggInfo = objOp.type.as();
            FieldInfo field = getAggregateField(c.getSrcPos(), aggInfo, c.field.variable);
            if(field != null) {
                fieldOp = Operand.op(field.type);
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
        if(!op.type.isKind(TypeKind.Func)) {
            this.result.addError(c.object, "'%s' is not a function", op.type.sym.name);
            return null;
        }
        
        for(Expr arg : c.arguments) {
            resolveExpr(arg);
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
}
