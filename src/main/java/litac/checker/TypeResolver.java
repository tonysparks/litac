/*
 * see license.txt
 */
package litac.checker;


import java.util.*;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo.*;
import litac.compiler.CompilationUnit;
import litac.util.Tuple;

/**
 * Responsible for ensuring each Expr can be resolved down to a {@link TypeInfo}.
 * 
 * @author Tony
 *
 */
public class TypeResolver {
    
    private CompilationUnit unit;
    private PhaseResult result;
    private Map<String, Tuple<Module,Decl>> genericTypes;
    private Set<String> resolvedGenericTypes;
    private Module root;
    
    public TypeResolver(PhaseResult result, CompilationUnit unit) {
        this.result = result;
        this.unit = unit;
        
        this.genericTypes = new HashMap<>();
        this.resolvedGenericTypes = new HashSet<>();
    }

    public Module resolveTypes() {
        Module module = resolveModule(this.unit.getMain());
                
        // add the generic types to the root declaration list        
        module.getModuleStmt().declarations
               .addAll(module.getGenericTypes()
                             .stream()
                             .map(e -> e.getSecond())                    
                             .collect(Collectors.toList()));
        
        return module;
    }
    
    private Module resolveModule(ModuleStmt moduleStmt) {
        // First build up an inventory of all declarations
        DeclNodeVisitor declVisitor = new DeclNodeVisitor();
        declVisitor.visit(moduleStmt);
        
        Module module = declVisitor.module;
        
        // Now resolve the types        
        TypeResolverNodeVisitor checker = new TypeResolverNodeVisitor(result, resolvedGenericTypes, module);
        moduleStmt.visit(checker);
        
        return module;
    }
    
   
    private class DeclNodeVisitor extends AbstractNodeVisitor {
        Module module;
        
        @Override
        public void visit(ModuleStmt stmt) {
            String moduleName = stmt.name;
            
            this.module = new Module(root, genericTypes, result, stmt, moduleName);
            if(root == null) {
                root = this.module;
            }
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            }
        }
        
        @Override
        public void visit(ImportStmt stmt) {                                
            String moduleName = stmt.alias != null ? stmt.alias : stmt.moduleName;
            
            if(this.module.getModule(moduleName) != null) {
                result.addError(stmt, "duplicate import of module '%s'", moduleName);
                return;
            }
            
            ModuleStmt moduleStmt = unit.getImports().get(stmt.moduleName);
            
            Module module = resolveModule(moduleStmt);
            this.module.importModule(stmt, module, stmt.alias);
        }
        
        @Override
        public void visit(StructFieldStmt stmt) {
            Node parent = stmt.getParentNode();
            if(parent instanceof StructDecl) {
                StructDecl decl = (StructDecl)parent;
                stmt.decl.type.name = String.format("%s:%s", decl.name, stmt.decl.type.name); 
            }
            
            stmt.decl.visit(this);      
        }

        @Override
        public void visit(UnionFieldStmt stmt) {
            Node parent = stmt.getParentNode();
            if(parent instanceof UnionDecl) {
                UnionDecl decl = (UnionDecl)parent;
                stmt.decl.type.name = String.format("%s:%s", decl.name, stmt.decl.type.name); 
            }
            
            stmt.decl.visit(this);
        }
        
        @Override
        public void visit(EnumFieldStmt stmt) {
            Node parent = stmt.getParentNode();
            if(parent instanceof EnumDecl) {
                EnumDecl decl = (EnumDecl)parent;
                stmt.decl.type.name = String.format("%s:%s", decl.name, stmt.decl.type.name); 
            }
            
            stmt.decl.visit(this);
        }
        
        @Override
        public void visit(EnumDecl d) {            
            this.module.declareEnum(d, d.name, (EnumTypeInfo)d.type);    
            
            for(EnumFieldInfo f : d.fields) {                
                if(f.value != null && !f.value.isResolved()) {
                    f.value.resolveTo(TypeInfo.I32_TYPE);
                }
            }
        }


        @Override
        public void visit(FuncDecl d) {
            this.module.declareFunc(d, d.name, (FuncTypeInfo)d.type);
        }


        @Override
        public void visit(StructDecl d) {
            this.module.declareStruct(d, d.name, (StructTypeInfo)d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }

        @Override
        public void visit(UnionDecl d) {
            this.module.declareUnion(d, d.name, (UnionTypeInfo)d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
            this.module.declareTypedef(d, d.alias, d.type);
        }

    }
    
    
    private static class TypeResolverNodeVisitor extends AbstractNodeVisitor {
        
        private Module module;
        private PhaseResult result;
        private FuncTypeInfo currentFuncInfo;
        private Set<String> resolvedGenericTypes;
        
        
        public TypeResolverNodeVisitor(PhaseResult result, 
                                       Set<String> resolvedGenericTypes,
                                       Module module) {
            this.result = result;
            this.module = module;
            this.resolvedGenericTypes = resolvedGenericTypes;
        }
        
        private boolean includeGenerics() {
            return this.currentFuncInfo == null ||
                  !this.currentFuncInfo.hasGenerics();
        }
        
        private void enterScope() {
            this.module.pushScope();
            
        }
        
        private void exitScope() {            
            module.popScope();
        }
        
        private Scope peekScope() {
            return module.currentScope();
        }
        
        private void validateArrayDimension(Stmt stmt, TypeInfo type) {
            switch(type.getKind()) {
                case i128:
                case i16:
                case i32:
                case i64:
                case i8:
                case u128:
                case u16:
                case u32:
                case u64:
                case u8:
                    break;
                default:                    
                    this.result.addError(stmt, "'%s' invalid array length type", type.getName());
            }
        }
                
        private TypeInfo getType(Stmt stmt, List<TypeInfo> genericArgs, List<GenericParam> genericParams, TypeInfo expectedType) {
            for(int i = 0; i < genericParams.size(); i++) {
                GenericParam p = genericParams.get(i);
                if(p.name.equals(expectedType.getName())) {
                    if(i >= genericArgs.size()) {
                        this.result.addError(stmt, "'%s' invalid generic argument type", expectedType.getName());
                        break;
                    }
                    
                    return genericArgs.get(i);
                }
            }
            
            return expectedType;
        }
        
        private void resolveType(Stmt stmt, TypeInfo type) {
            resolveType(stmt, type, null);
        }
        
        private void resolveType(Stmt stmt, TypeInfo type, TypeInfo resolvedType) {
            if(type == null) {
                return;
            }
            
            if(!type.isResolved()) {  
                if(resolvedType == null) {
                    resolvedType = module.getType(type.getName());
                    if(resolvedType == null && includeGenerics()) {
                        this.result.addError(stmt, "'%s' is an unknown type", type.getName());
                        return;
                    }
                }
                
                IdentifierTypeInfo idType = type.as();
                idType.resolve(this.module, resolvedType, includeGenerics());
            }
            else if(type.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = type.as();
                resolveType(stmt, ptrInfo.ptrOf, resolvedType);
            }
            else if(type.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = type.as();
                resolveType(stmt, arrayInfo.arrayOf, resolvedType);
                
                if(arrayInfo.lengthExpr != null) {                    
                    arrayInfo.lengthExpr.visit(this);
                    
                    if(arrayInfo.lengthExpr instanceof NumberExpr) {
                          NumberExpr nExpr = (NumberExpr) arrayInfo.lengthExpr;                   
                          validateArrayDimension(arrayInfo.lengthExpr, nExpr.type);
                          
                          arrayInfo.length = ((Number)nExpr.number.getValue()).intValue();
                          
                      }
                      else if(arrayInfo.lengthExpr instanceof IdentifierExpr) {
                          IdentifierExpr iExpr = (IdentifierExpr)arrayInfo.lengthExpr;
                          if(iExpr.sym.decl instanceof ConstDecl) {
                              validateArrayDimension(arrayInfo.lengthExpr, iExpr.sym.decl.type);
                              
                              ConstDecl cExpr = (ConstDecl)iExpr.sym.decl;
                              NumberExpr nExpr = (NumberExpr)cExpr.expr;
                              
                              arrayInfo.length = ((Number)nExpr.number.getValue()).intValue();
                          }
                          else {
                              this.result.addError(arrayInfo.lengthExpr, "'%s' invalid array length expression", type.getName());
                          }
                      }
                }
                
            }
        }
        
        
        @Override
        public void visit(ModuleStmt stmt) {
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            } 
            
            Module current = this.module;
            
            List<Tuple<Module,Decl>> newTypes = new ArrayList<>();
            do {                
                newTypes.addAll(this.module.getGenericTypes());
                newTypes.removeIf(decl -> resolvedGenericTypes.contains(decl.getSecond().name));
                
                for(Tuple<Module,Decl> d : newTypes) {
                    Decl decl = d.getSecond();
                    if(resolvedGenericTypes.contains(decl.name)) {
                        continue;
                    }
                    
                    resolvedGenericTypes.add(decl.name);
                    
                    this.module = d.getFirst();
                    decl.visit(this);
                }
            }
            while(!newTypes.isEmpty());
            
            this.module = current;
        }
        
        

        @Override
        public void visit(ImportStmt stmt) {
            // TODO
        }

        @Override
        public void visit(IfStmt stmt) {
            stmt.condExpr.visit(this);
            
            enterScope();
            stmt.thenStmt.visit(this);
            exitScope();
            
            if(stmt.elseStmt != null) {
                enterScope();
                stmt.elseStmt.visit(this);
                exitScope();    
            }
        }

        @Override
        public void visit(WhileStmt stmt) {
            stmt.condExpr.visit(this);
            
            enterScope();
            stmt.bodyStmt.visit(this);
            exitScope();
        }


        @Override
        public void visit(DoWhileStmt stmt) {
            enterScope();
            stmt.bodyStmt.visit(this);
            exitScope();
            
            stmt.condExpr.visit(this);
        }

 
        @Override
        public void visit(ForStmt stmt) {
            enterScope();
            stmt.initStmt.visit(this);
            stmt.condExpr.visit(this);
            stmt.postStmt.visit(this);
            stmt.bodyStmt.visit(this);
            exitScope();
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
            enterScope();
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
            exitScope();

        }
        
        @Override
        public void visit(DeferStmt stmt) {
            stmt.stmt.visit(this);            
        }
        
        @Override
        public void visit(EmptyStmt stmt) {
        }
        
        @Override
        public void visit(VarDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
            }
            
            // infer the type from the expression
            if(d.type == null) {
                d.type = d.expr.getResolvedType();
            }
            
            // if we can't infer the type, some
            // type hasn't been resolved correctly
            if(d.type == null) {
                return;
            }
            
            resolveType(d, d.type);
            
            peekScope().addSymbol(this.module, d, d.name, d.type);            
        }
        
  
        @Override
        public void visit(ConstDecl d) {
            d.expr.visit(this);

            // infer the type from the expression
            if(d.type == null) {
                d.type = d.expr.getResolvedType();
            }
            
            resolveType(d, d.type);
            
            peekScope().addSymbol(this.module, d, d.name, d.type, true);
        }

        @Override
        public void visit(EnumDecl d) {    
            for(EnumFieldInfo f : d.fields) {
                if(f.value != null) {
                    f.value.visit(this);
                }
            }
        }

        @Override
        public void visit(FuncDecl d) {
            enterScope();
            {
                FuncTypeInfo funcInfo = d.type.as();
                // must resolve types, even for generic functions
                // otherwise the Generic.replacer will not work
                this.currentFuncInfo = funcInfo;
                
                resolveType(d, d.returnType);
                for(ParameterDecl p : d.params.params) {
                    resolveType(p, p.type);
                    if(p.defaultValue != null) {
                        p.defaultValue.visit(this);
                    }
                    
                    peekScope().addSymbol(this.module, p, p.name, p.type);
                }
                
                d.bodyStmt.visit(this);
                
                this.currentFuncInfo = null;
            }
            exitScope();
        }


        @Override
        public void visit(StructDecl d) {
            StructTypeInfo structInfo = d.type.as();
            if(structInfo.hasGenerics()) {
                return;
            }
            
            resolveType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
            resolveType(stmt, stmt.type);
            if(stmt.type.isKind(TypeKind.FuncPtr)) {
                FuncPtrTypeInfo funcPtr = stmt.type.as();
                funcPtr.name = stmt.name;
            }
        }

        @Override
        public void visit(UnionDecl d) {
            UnionTypeInfo unionInfo = d.type.as();
            if(unionInfo.hasGenerics()) {
                return;
            }
            
            resolveType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
            // TODO
            resolveType(d, d.type);
            //module.gett
        }

        private TypeInfo getAggregateFieldTypeInfo(InitExpr expr) {
            TypeInfo type = null;
            if(expr.type != null) {
                type = this.module.getType(expr.type.getName());               
            }
            // anonymous aggregate
            else {
                Node parent = expr.getParentNode();
                if(parent instanceof InitExpr) {
                    InitExpr parentExpr = (InitExpr)parent;
                    
                    TypeInfo aggInfo;
                    if(!parentExpr.isResolved()) {
                        aggInfo = getAggregateFieldTypeInfo(parentExpr);
                    }
                    else {
                        aggInfo = parentExpr.getResolvedType();
                    }
                    
                    
                    int index = 0;
                    for(Expr e : parentExpr.arguments) {
                        if(e == expr) {
                            break;
                        }
                        
                        index++;
                    }
                                   
                    
                    if(aggInfo.isKind(TypeKind.Struct)) {                        
                        StructTypeInfo structInfo = aggInfo.as();
                        if(index >= structInfo.fieldInfos.size()) {
                            this.result.addError(expr, "invalid struct initialize index");
                        }
                        else {
                            FieldInfo field = structInfo.fieldInfos.get(index);
                            type = field.type;
                            expr.resolveTo(type);
                        }
                    }
                    else if(aggInfo.isKind(TypeKind.Union)) {
                        UnionTypeInfo unionInfo = aggInfo.as();
                        if(index >= unionInfo.fieldInfos.size()) {
                            this.result.addError(expr, "invalid union initialize index");
                        }
                        else {
                            FieldInfo field = unionInfo.fieldInfos.get(index);
                            type = field.type;
                            expr.resolveTo(type);
                        }
                    }
                    
                }
            }
            
            return type;
        }
        
        @Override
        public void visit(CastExpr expr) {
            resolveType(expr, expr.castTo);
            expr.expr.visit(this);
        }
        
        @Override
        public void visit(SizeOfExpr expr) {
            expr.expr.visit(this);
        }
        
        @Override
        public void visit(InitArgExpr expr) {
            expr.value.visit(this);
            expr.resolveTo(expr.value.getResolvedType());
        }
                
        @Override
        public void visit(InitExpr expr) {
            for(InitArgExpr e : expr.arguments) {
                e.visit(this);
            }
            
            // TODO: no type was specified, must infer from
            // parent node
            if(expr.type == null) {
                Node parent = expr.getParentNode();
                if(!(parent instanceof Expr)) {
                    this.result.addError(expr, "'%s' is an unknown type", expr.type);
                    return;
                }
                
                Expr parentExpr = (Expr)parent;
                if(parentExpr instanceof InitArgExpr) {
                  InitArgExpr arg = (InitArgExpr)parentExpr;
                  
                }
                //else if(parentExpr instanceof Pa)
                
                expr.type = parentExpr.getResolvedType();
            }
            
            if(!expr.type.isResolved()) {
                TypeInfo type = getAggregateFieldTypeInfo(expr);          
                if(type == null) {
                    this.result.addError(expr, "'%s' is an unknown type", expr.type);
                    return;
                }
            
                IdentifierTypeInfo idInfo = expr.type.as();
                idInfo.resolve(this.module, type, includeGenerics());
            }
        }

        @Override
        public void visit(FuncCallExpr expr) {
            for(TypeInfo arg : expr.genericArgs) {
                resolveType(expr, arg);
            }
            
            expr.object.visit(this);            
            
            TypeInfo type = expr.object.getResolvedType();
            if(!type.isKind(TypeKind.Func) && !type.isKind(TypeKind.FuncPtr)) {
                this.result.addError(expr, "'%s' is not a function", type.getName());
                return;
            }
            
            FuncPtrTypeInfo funcPtr = null;
            if(type.isKind(TypeKind.Func)) {
                FuncTypeInfo funcInfo = type.as();
                funcPtr = funcInfo.asPtr();
            }
            else {
                funcPtr = type.as();
            }
            
            expr.resolveTo(getType(expr, expr.genericArgs, funcPtr.genericParams, funcPtr.returnType));
            
            int i = 0;
            for(; i < funcPtr.params.size(); i++) {
                TypeInfo paramInfo = funcPtr.params.get(i);
                resolveType(expr, getType(expr, expr.genericArgs, funcPtr.genericParams, paramInfo));
                
                if(i < expr.arguments.size()) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                    
                    resolveType(arg, getType(expr, expr.genericArgs, funcPtr.genericParams, arg.getResolvedType()));
                }
            }
            
            if(funcPtr.isVararg) {
                for(; i < expr.arguments.size(); i++) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                    
                    resolveType(arg, getType(expr, expr.genericArgs, funcPtr.genericParams, arg.getResolvedType()));
                }                
            }
        }

        @Override
        public void visit(IdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                Symbol sym = peekScope().getSymbol(expr.variable); 
                
                if(sym == null) {
                    this.result.addError(expr, "unknown variable '%s'", expr.variable);
                    return;
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(this.module, sym.type, includeGenerics());
                
//                TypeInfo newType = type.getResolvedType();
//                if(newType.sym != null) {
//                    expr.sym = newType.sym;   
//                }
                expr.sym = sym;
                expr.resolveTo(expr.type); // TODO: remove type from IdExpr
            }
        }
        
        @Override
        public void visit(FuncIdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                TypeInfo resolvedType = this.module.getFuncType(expr.variable); 
                
                if(resolvedType == null) {
                    Symbol sym = peekScope().getSymbol(expr.variable); 
                    
                    if(sym == null || !sym.type.isKind(TypeKind.FuncPtr)) {
                        this.result.addError(expr, "unknown function '%s'", expr.variable);
                        return;
                    }
                    
                    resolvedType = sym.type;
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(this.module, resolvedType, includeGenerics());

                TypeInfo newType = type.getResolvedType();
                if(newType.sym != null) {
                    expr.sym = newType.sym;
                }
                expr.resolveTo(expr.type); // TODO: remove type from IdExpr
            }
        }
        
        @Override
        public void visit(SizeOfIdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                Symbol sym = peekScope().getSymbol(expr.variable); 
                
                // Might be a generic type not defined
                if(sym == null) {                    
                    return;
                }
                else {
                    IdentifierTypeInfo type = expr.type.as();
                    type.resolve(this.module, sym.type, includeGenerics());
                    
                    TypeInfo newType = type.getResolvedType();
                    if(newType.sym != null) {
                        expr.sym = newType.sym;
                    }
                    expr.resolveTo(expr.type); // TODO: remove type from IdExpr
                }
            }
        }
        
        private boolean resolveAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value) { 
            if(type == null) {
                this.result.addError(expr, "unknown type");
                return false;
            }
            
            switch(type.getKind()) {
                case Ptr: {
                    PtrTypeInfo ptrInfo = type.as();
                    return resolveAggregate(ptrInfo.ptrOf, field, expr, value);                    
                }
                case Struct: {
                    StructTypeInfo structInfo = type.as();
                    for(FieldInfo fieldInfo : structInfo.fieldInfos) {  
                        if(fieldInfo.type.isAnonymous()) {
                            if(resolveAggregate(fieldInfo.type, field, expr, value)) {
                                return true;
                            }
                        }
                        else if(fieldInfo.name.equals(field.getName())) {
                            if(!field.isResolved()) {
                                resolveType(expr, field, fieldInfo.type);
                            }
                            
                            expr.resolveTo(fieldInfo.type);                            
                            return true;
                        }
                    }
                    this.result.addError(expr, "'%s' does not have field '%s'", structInfo.name, field.name);
                    break;
                }                
                case Union: {
                    UnionTypeInfo unionInfo = type.as();
                    for(FieldInfo fieldInfo : unionInfo.fieldInfos) {
                        if(fieldInfo.type.isAnonymous()) {
                            if(resolveAggregate(fieldInfo.type, field, expr, value)) {
                                return true;
                            }
                        }
                        else if(fieldInfo.name.equals(field.getName())) {
                            if(!field.isResolved()) {
                                resolveType(expr, field, fieldInfo.type);
                            }
                            
                            expr.resolveTo(fieldInfo.type);                            
                            return true;
                        }
                    }
                    this.result.addError(expr, "'%s' does not have field '%s'", unionInfo.name, field.name);
                    break;
                }
                case Enum: {
                    if(value != null) {
                        this.result.addError(expr, "'%s.%s' can not be reassigned", type.name, field.name);
                    }
                    else {
                        EnumTypeInfo enumInfo = type.as();
                        for(EnumFieldInfo fieldInfo : enumInfo.fields) {
                            if(!field.isResolved()) {
                                if(fieldInfo.value != null) {
                                    resolveType(expr, field, fieldInfo.value.getResolvedType());
                                }
                                else {
                                    resolveType(expr, field, TypeInfo.I32_TYPE);
                                }
                            }
                            
                            expr.resolveTo(enumInfo);
                            return true;
                        }
                        this.result.addError(expr, "'%s' does not have field '%s'", enumInfo.name, field.name);
                    }
                    break;
                }
                default: {
                    this.result.addError(expr, "'%s' is an invalid type for aggregate access", type.getName());
                }
            }
            
            return false;
        }
        
        @Override
        public void visit(GetExpr expr) {
            expr.object.visit(this);            
            
            if(!expr.field.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                resolveAggregate(type, expr.field, expr, null);
            }
            
            resolveType(expr, expr.field);
        }
        
        @Override
        public void visit(SetExpr expr) {
            expr.object.visit(this);            
            expr.value.visit(this);
            
            if(!expr.field.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                resolveAggregate(type, expr.field, expr, expr.value);
            }
            
            resolveType(expr, expr.field);
        }

        @Override
        public void visit(UnaryExpr expr) {
            expr.expr.visit(this);
            
            switch(expr.operator) {
                case STAR: {
                    TypeInfo type = expr.expr.getResolvedType().getResolvedType();
                    if(type.isKind(TypeKind.Ptr)) {
                        PtrTypeInfo ptrInfo = type.as();
                        expr.resolveTo(ptrInfo.ptrOf.getResolvedType());
                    }
                    else if(type.isKind(TypeKind.Str)) {
                        expr.resolveTo(type);
                    }
                    else {
                        this.result.addError(expr, "'%s' is not a pointer type", type);
                    }
                    
                    break;
                }
                case BAND: {
                    TypeInfo type = expr.expr.getResolvedType().getResolvedType();
                    if(type.isKind(TypeKind.Func)) {
                        FuncTypeInfo funcType = type.as();
                        expr.resolveTo(funcType.asPtr());
                    }
                    else {
                        PtrTypeInfo ptrInfo = new PtrTypeInfo(type);
                        expr.resolveTo(ptrInfo);
                    }
                    break;
                }
                case NOT: {
                    expr.resolveTo(TypeInfo.BOOL_TYPE);
                    break;
                }
                default: {
                    expr.resolveTo(expr.expr.getResolvedType());
                }
            }
        }

        @Override
        public void visit(GroupExpr expr) {
            expr.expr.visit(this);
            expr.resolveTo(expr.expr.getResolvedType());
        }

        @Override
        public void visit(BinaryExpr expr) {
            expr.left.visit(this);
            expr.right.visit(this);
            
            switch(expr.operator) {
                case AND:
                case OR:
                case GREATER_THAN:
                case GREATER_EQUALS:
                case LESS_THAN:
                case LESS_EQUALS:
                case EQUALS_EQUALS:
                case NOT_EQUALS:
                    expr.resolveTo(TypeInfo.BOOL_TYPE);
                    break;
                default:
                    if(expr.left.isResolved() && expr.right.isResolved()) {
                        TypeInfo leftType = expr.left.getResolvedType();
                        TypeInfo rightType = expr.right.getResolvedType();
                        
                        if(leftType.strictEquals(rightType)) {
                            expr.resolveTo(leftType);
                        }
                        else if(leftType.isGreater(rightType)) {
                            expr.resolveTo(leftType);
                        }
                        else {
                            expr.resolveTo(rightType);
                        }
                    }
                    break;
            }            
        }


        @Override
        public void visit(ArrayInitExpr expr) {
            ArrayTypeInfo arrayInfo = expr.getResolvedType().as();
            
            int n = expr.values.size();
            for(Expr v : expr.values) {
                v.visit(this);
            }
            
            // TODO -- validate sizes?
            if(arrayInfo.length < 0) {
                arrayInfo.length = n;
            }
            
            resolveType(expr, arrayInfo);
            
            if(arrayInfo.length < n) {
                this.result.addError(expr, "defined array dimension '%d' is smaller than number of elements '%d'", arrayInfo.length, n);
            }
            
        }
        
        @Override
        public void visit(SubscriptGetExpr expr) {
            expr.object.visit(this);
            expr.index.visit(this);
            
            TypeKind objectKind = expr.object.getResolvedType().getKind();
            switch(objectKind) {
                case Str:
                    expr.resolveTo(TypeInfo.CHAR_TYPE);
                    break;
                case Array:
                    ArrayTypeInfo arrayInfo = expr.object.getResolvedType().as();
                    expr.resolveTo(arrayInfo.arrayOf.getResolvedType());
                    break;
                case Ptr:
                    PtrTypeInfo ptrInfo = expr.object.getResolvedType().as();
                    expr.resolveTo(ptrInfo.ptrOf.getResolvedType());
                    break;
                default: {
                    this.result.addError(expr, "invalid index into '%s'", objectKind.name());
                    return;
                }
            }
        }

        @Override
        public void visit(SubscriptSetExpr expr) {
            expr.object.visit(this);
            expr.index.visit(this);
            expr.value.visit(this);
            
            expr.resolveTo(expr.value.getResolvedType());
        }
        
       
        
    }
    
}
