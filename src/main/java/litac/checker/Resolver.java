/*
 * see license.txt
 */
package litac.checker;

import java.util.*;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.ConstExpr;
import litac.ast.Node.SrcPos;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo.*;
import litac.compiler.*;
import litac.compiler.Symbol.ResolveState;
import litac.util.Tuple;

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
    }
    
    private CompilationUnit unit;
    private PhaseResult result;
    private Map<String, Module> resolvedModules;
    private Map<String, Tuple<Module,Decl>> genericTypes;
    
    private List<Symbol> moduleSymbols;
    private List<Symbol> moduleFuncs;
    private Module current;
    
    // Types pending to be completed
    private List<Symbol> pendingTypes;
    private List<Symbol> pendingValues;
    
    public Resolver(PhaseResult result, CompilationUnit unit) {
        this.result = result;
        this.unit = unit;
        
        this.moduleSymbols = new ArrayList<>();
        this.moduleFuncs = new ArrayList<>();
        this.pendingTypes = new ArrayList<>();
        this.pendingValues = new ArrayList<>();
    }

    public Program resolveTypes() {
        Module module = resolveModule(this.unit.getMain());
                
        // add the generic types to the root declaration list        
        module.getModuleStmt().declarations
               .addAll(module.getGenericTypes()
                             .stream()
                             .map(e -> e.getSecond())                    
                             .collect(Collectors.toList()));
        
        return new Program(module, resolvedModules, module.getSymbols());
    }
    
    private Module resolveModule(ModuleStmt moduleStmt) {
        Module module = createModule(moduleStmt);
                
        List<Symbol> syms = null;
        do {
            syms = new ArrayList<>(this.moduleSymbols);
            this.moduleSymbols.clear();
            
            for(Symbol sym : this.moduleSymbols) {
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
    
    private Module createModule(ModuleStmt moduleStmt) {
        String moduleName = moduleStmt.name;
        if(resolvedModules.containsKey(moduleName)) {
            return resolvedModules.get(moduleName);
        }
        
        Module module = new Module(null, genericTypes, result, moduleStmt, moduleName);
        for(Decl decl : moduleStmt.declarations) {
            switch(decl.kind) {
                case CONST:                                        
                case VAR: {
                    Symbol sym = module.addIncomplete(decl);
                    this.pendingValues.add(sym);
                    break;
                }
                case ENUM: {
                    EnumDecl enumDecl = (EnumDecl) decl;
                    Symbol sym = module.declareEnum(enumDecl, enumDecl.name);
                    this.moduleSymbols.add(sym);
                    break;
                }
                case FUNC: {
                    FuncDecl funcDecl = (FuncDecl) decl;
                    Symbol sym = module.declareFunc(funcDecl, funcDecl.name);
                    this.moduleSymbols.add(sym);
                    this.moduleFuncs.add(sym);
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
                        break;
                    case TYPEDEF:
                        TypedefDecl typedefDecl = (TypedefDecl) sym.decl;
                        sym.type = resolveTypeSpec(typedefDecl.type);
                        break;
                    case STRUCT:                        
                        StructDecl structDecl = (StructDecl) sym.decl;                        
                        sym.type = new StructTypeInfo(sym.decl.name, structDecl.genericParams, new ArrayList<>(), structDecl.flags);
                        this.pendingTypes.add(sym);
                        break;
                    case UNION:
                        UnionDecl unionDecl = (UnionDecl) sym.decl;
                        sym.type = new UnionTypeInfo(sym.decl.name, unionDecl.genericParams, new ArrayList<>(), unionDecl.flags);
                        this.pendingTypes.add(sym);
                        break;
                    default:                        
                }
                break;
            case FUNC:
                FuncDecl funcDecl = (FuncDecl) sym.decl;
                sym.type = funcTypeInfo(funcDecl);
                break;
                
            case CONST:
                ConstDecl constDecl = (ConstDecl) sym.decl;
                if(constDecl.type != null) {
                    
                }
                break;
            case VAR:
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
        if(sym.decl.kind != DeclKind.STRUCT || sym.decl.kind != DeclKind.UNION) {
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
            case NAME:
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
                return sym.type;
            case ARRAY:
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
                return new ArrayTypeInfo(arrayOf, length, arraySpec.numElements);
            case CONST:
                ConstTypeSpec constSpec = typeSpec.as();
                TypeInfo constOf = resolveTypeSpec(constSpec.base);
                return new ConstTypeInfo(constOf);
            case PTR:
                PtrTypeSpec ptrSpec = typeSpec.as();
                TypeInfo ptrOf = resolveTypeSpec(ptrSpec.base);
                return new PtrTypeInfo(ptrOf);                
            case FUNC_PTR:
                FuncPtrTypeSpec funcSpec = typeSpec.as();
                List<TypeInfo> args = new ArrayList<>();
                for(TypeSpec argSpec : funcSpec.args) {
                    TypeInfo arg = resolveTypeSpec(argSpec);
                    args.add(arg);
                }
                
                TypeInfo ret = resolveTypeSpec(funcSpec.ret);
                return new FuncPtrTypeInfo(ret, args, funcSpec.hasVarargs, funcSpec.genericParam);
            default:
                this.result.addError(typeSpec.pos, "Unknown type specification");
                return null;
        
        }
    }
    
    private TypeInfo funcTypeInfo(FuncDecl funcDecl) {
        ParametersStmt params = funcDecl.params;
        
        List<ParamInfo> args = new ArrayList<>();
        for(ParameterDecl param : params.params) {
            TypeInfo arg = resolveTypeSpec(param.type);
            args.add(new ParamInfo(arg, param.name));
        }
        
        TypeInfo ret = resolveTypeSpec(funcDecl.returnType);        
        return new FuncTypeInfo(funcDecl.name, ret, args, funcDecl.flags, funcDecl.genericParams);
    }
            
    private TypeInfo enumTypeInfo(EnumDecl enumDecl) {
        EnumTypeInfo enumInfo = new EnumTypeInfo(enumDecl.name, enumDecl.fields);
        return enumInfo;
    }
    
    private Operand resolveExpr(Expr expr) {
        return null;
    }
    
    private Operand resolveInitExpr(Expr expr) {
        return null;
    }
    
    private Operand resolveConstExpr(Expr expr) {
        return null;
    }
}
