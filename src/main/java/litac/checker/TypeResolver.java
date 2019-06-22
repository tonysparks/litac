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
    private Map<String, Module> resolvedModules;
    private Module root;
    
    public TypeResolver(PhaseResult result, CompilationUnit unit) {
        this.result = result;
        this.unit = unit;
        
        this.genericTypes = new HashMap<>();
        this.resolvedGenericTypes = new HashSet<>();
        this.resolvedModules = new HashMap<>();
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
        String moduleName = moduleStmt.name;
        if(resolvedModules.containsKey(moduleName)) {
            return resolvedModules.get(moduleName);
        }
        
        // First build up an inventory of all declarations
        DeclNodeVisitor declVisitor = new DeclNodeVisitor();
        declVisitor.visit(moduleStmt);
        
        Module module = declVisitor.module;
        resolvedModules.put(moduleName, module);
        
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
        
        // The type the generic module is defined in        
        private Module genericModule;
        
        public TypeResolverNodeVisitor(PhaseResult result, 
                                       Set<String> resolvedGenericTypes,
                                       Module module) {
            this.result = result;
            this.module = module;
            this.resolvedGenericTypes = resolvedGenericTypes;
        }
        
        private TypeInfo getType(String typeName) {
            TypeInfo type = this.module.getType(typeName);
            if(type == null && this.genericModule != null) {
                return this.genericModule.getType(typeName);
            }
            
            return type;
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
        
        private TypeInfo getType(Stmt stmt, List<TypeInfo> genericArgs, List<GenericParam> genericParams, TypeInfo expectedType) {
            if(expectedType == null && !includeGenerics()) {
                // we're processing a generic function, we can only resolve so much
                // with these unknown types
                return null;
            }
            
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
                    resolvedType = getType(type.getName());
                    if(resolvedType == null) {
                        if(!includeGenerics()) {
                            //resolvedType = type;
                            // todo
                            return;
                        }
                        else {
                            this.result.addError(stmt, "'%s' is an unknown type", type.getName());
                            return;
                        }
                    }
                }
                else if(!resolvedType.isResolved()) {
                    resolveType(stmt, resolvedType);
                }
                
                IdentifierTypeInfo idType = type.as();
                idType.resolve(this.module, resolvedType, includeGenerics());
            }
            else if(type.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = type.as();
                resolveType(stmt, ptrInfo.ptrOf, resolvedType);
            }
            else if(type.isKind(TypeKind.Const)) {
                ConstTypeInfo constInfo = type.as();
                resolveType(stmt, constInfo.constOf, resolvedType);
            }
            else if(type.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = type.as();
                resolveType(stmt, arrayInfo.arrayOf, resolvedType);
                
                if(arrayInfo.lengthExpr != null) {                    
                    arrayInfo.lengthExpr.visit(this);
                    
                    Integer len = asInt(arrayInfo.lengthExpr);
                    if(len == null) {
                        this.result.addError(arrayInfo.lengthExpr, "'%s' invalid array length expression", type.getName());
                        return;
                    }
                    
                    arrayInfo.length = len;
                }
            }
            else if(type.isKind(TypeKind.FuncPtr)) {
                FuncPtrTypeInfo funcInfo = type.as();
                if(funcInfo.hasGenerics()) {
                    return;
                }
                
                resolveType(stmt, funcInfo.returnType);
                for(TypeInfo p : funcInfo.params) {
                    resolveType(stmt, p);
                }
            }
        }
        
        private Integer asInt(Expr expr) {
            if(expr instanceof NumberExpr) {
                NumberExpr nExpr = (NumberExpr) expr;                   
                return nExpr.asInt();
            }
            else if(expr instanceof IdentifierExpr) {
                IdentifierExpr iExpr = (IdentifierExpr)expr;
                if(iExpr.sym != null && iExpr.sym.decl instanceof ConstDecl) {
                    ConstDecl cExpr = (ConstDecl)iExpr.sym.decl;
                    if(cExpr.expr instanceof NumberExpr) {
                        NumberExpr nExpr = (NumberExpr)cExpr.expr;
                        
                        return nExpr.asInt();
                    }
                    else if(iExpr.sym.isForeign()) {
                        return -1;
                    }
                }
            }
            else if(expr instanceof GetExpr) {
                GetExpr getExpr = (GetExpr)expr;
                TypeInfo objectInfo = getExpr.object.getResolvedType();
                if(objectInfo.isKind(TypeKind.Enum)) {
                    EnumTypeInfo enumInfo = objectInfo.as();
                    EnumFieldInfo field = enumInfo.getField(getExpr.field.variable);
                    if(field != null) {
                        if(field.value != null) {
                            return asInt(field.value);
                        }
                        
                        int index = enumInfo.indexOf(field.name);
                        if(index > -1) {
                            return index;
                        }
                    }    
                }                
            }
            
            return null;
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
                    
                    this.genericModule = current;
                    this.module = d.getFirst();
                    
                    decl.visit(this);
                }
            }
            while(!newTypes.isEmpty());
            
            this.module = current;
            this.genericModule = null;
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
            if(stmt.initStmt != null) {
                stmt.initStmt.visit(this);
            }
            
            if(stmt.condExpr != null) {
                stmt.condExpr.visit(this);
            }
            
            if(stmt.postStmt != null) {
                stmt.postStmt.visit(this);
            }
            
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
            else {
                // infer the array length, if this is an array
                if(d.type.isKind(TypeKind.Array)) {
                    ArrayTypeInfo arrayInfo = d.type.as();
                    if(arrayInfo.length < 0) {
                        TypeInfo exprInfo = d.expr.getResolvedType();
                        if(exprInfo.isKind(TypeKind.Array)) {
                            ArrayTypeInfo exprArrayInfo = exprInfo.as();
                            arrayInfo.length = exprArrayInfo.length;
                        }
                    }
                }
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
            if(d.expr != null) {
                d.expr.visit(this);
    
                // infer the type from the expression
                if(d.type == null) {
                    d.type = d.expr.getResolvedType();
                }
                else {
                    // infer the array length, if this is an array
                    if(d.type.isKind(TypeKind.Array)) {
                        ArrayTypeInfo arrayInfo = d.type.as();
                        if(arrayInfo.length < 0) {
                            TypeInfo exprInfo = d.expr.getResolvedType();
                            if(exprInfo.isKind(TypeKind.Array)) {
                                ArrayTypeInfo exprArrayInfo = exprInfo.as();
                                arrayInfo.length = exprArrayInfo.length;
                            }
                        }
                    }
                }
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

        private void addTypeToScope(Decl p, Scope scope, TypeInfo currentType) {
            if(!currentType.isResolved()) {
                resolveType(p, currentType);
            }
            
            AggregateTypeInfo aggInfo = (currentType.isKind(TypeKind.Ptr)) 
                                            ? ((PtrTypeInfo)currentType).ptrOf.as()
                                            : currentType.as();
                                            
            for(FieldInfo field : aggInfo.fieldInfos) {
                scope.addSymbol(this.module, p, field.name, field.type, Symbol.IS_USING);
            }
            
            if(aggInfo.hasUsingFields()) {
                for(FieldInfo field : aggInfo.usingInfos) {
                    addTypeToScope(p, scope, field.type);
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
                    
                    Scope scope = peekScope();
                    scope.addSymbol(this.module, p, p.name, p.type);
                    
                    if((p.attributes.modifiers & Attributes.USING_MODIFIER) > 0) {
                        if(!TypeInfo.isAggregate(p.type) &&
                           !TypeInfo.isPtrAggregate(p.type)) {
                            this.result.addError(d, "'%s' is not an aggregate type (or pointer to an aggregate), can't use 'using'", p.name);
                        }
                        else {
                            addTypeToScope(p, scope, p.type);
                        }
                    }
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
        public void visit(UnionFieldStmt stmt) {
            stmt.decl.visit(this);
        }
        
        @Override
        public void visit(StructFieldStmt stmt) {
            stmt.decl.visit(this);
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
            resolveType(d, d.type);
        }

        private TypeInfo getAggregateFieldTypeInfo(InitExpr expr) {
            TypeInfo type = null;
            if(expr.type != null) {
                type = getType(expr.type.getName());               
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
                                   
                    
                    if(TypeInfo.isAggregate(aggInfo)) {                        
                        AggregateTypeInfo info = aggInfo.as();
                        if(index >= info.fieldInfos.size()) {
                            this.result.addError(expr, "invalid %s initialize index", info.getKind().name());
                        }
                        else {
                            FieldInfo field = info.fieldInfos.get(index);
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
        public void visit(TypeOfExpr expr) {
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
            
            // type inference for generic functions 
            if(funcPtr.hasGenerics() && expr.genericArgs.isEmpty()) {
                expr.genericArgs = new ArrayList<>(funcPtr.genericParams.size());
                
                for(GenericParam p : funcPtr.genericParams) {
                    for(int j = 0; j < funcPtr.params.size(); j++) {
                        if(funcPtr.params.get(j).getName().equals(p.name)) {
                            expr.genericArgs.add(expr.arguments.get(j).getResolvedType());
                        }
                    }
                }
                // TODO, reresolve the function now with populated genericArgs
                expr.object.visit(this);
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
        public void visit(TypeIdentifierExpr expr) {
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
                case Const: {
                    ConstTypeInfo constInfo = type.as();
                    return resolveAggregate(constInfo.constOf, field, expr, value);
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
                    
                    FieldPath path = structInfo.getFieldPath(field.getName());
                    if(path.hasPath()) {
                        TypeInfo usingField = path.getTargetField().type; 
                        if(!field.isResolved()) {
                            resolveType(expr, field, usingField);
                        }
                        
                        expr.resolveTo(usingField);
                        return true;
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
                case Identifier: {
                    // if we are parsing a Generic structure, ignore that we
                    // can't fully resolve this type (will get resolved when defined with concrete type)
                    if(!includeGenerics()) {
                        expr.resolveTo(type);
                        break;
                    }
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
            //expr.field.visit(this);
            
            if(!expr.field.type.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                resolveAggregate(type, expr.field.type, expr, null);
            }
            
            resolveType(expr, expr.field.type);
        }
        
        @Override
        public void visit(SetExpr expr) {
            expr.object.visit(this);         
            //expr.field.visit(this);
            expr.value.visit(this);
            
            if(!expr.field.type.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                resolveAggregate(type, expr.field.type, expr, expr.value);
            }
            
            resolveType(expr, expr.field.type);
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
        public void visit(ArrayDesignationExpr expr) {
            expr.index.visit(this);
            expr.value.visit(this);
            expr.resolveTo(expr.value.getResolvedType());
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
                case Const:
                    ConstTypeInfo constInfo = expr.object.getResolvedType().as();
                    expr.resolveTo(constInfo.baseOf().getResolvedType());
                    break;
                case Enum: {
                    expr.resolveTo(TypeInfo.I32_TYPE);
                    break;
                }
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
