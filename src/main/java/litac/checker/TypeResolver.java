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
import litac.compiler.*;
import litac.generics.GenericParam;
import litac.parser.tokens.TokenType;
import litac.util.Stack;
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
    private List<Decl> pendingGlobals;
    
    private Module root;
    
    public TypeResolver(PhaseResult result, CompilationUnit unit) {
        this.result = result;
        this.unit = unit;
        
        this.genericTypes = new HashMap<>();
        this.resolvedGenericTypes = new HashSet<>();
        this.resolvedModules = new HashMap<>();
        this.pendingGlobals = new ArrayList<>();
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
        
        // First build up an inventory of all declarations
        // Now that we have all of the type declarations, we can now resolve
        // any global declarations (we know their types).
        // Finally, we are able to traverse thru functions and resolve the sub
        // expressions
        
        DeclNodeVisitor declVisitor = new DeclNodeVisitor(moduleStmt);
        declVisitor.visit(moduleStmt);
        
        
        Set<String> resolvedModules = new HashSet<>();
        Module module = declVisitor.module;
        
        resolveGlobals(resolvedModules);
        
        // Second phase, resolve all types, visiting function/type bodies
        TypeResolverNodeVisitor checker = new TypeResolverNodeVisitor(result,
                                                                      unit,
                                                                      resolvedGenericTypes,
                                                                      resolvedModules,
                                                                      module);
        
        moduleStmt.visit(checker);
        
        return module;
    }
    
    private Module resolveDeclModule(ModuleStmt moduleStmt) {
        String moduleName = moduleStmt.name;
        if(resolvedModules.containsKey(moduleName)) {
            return resolvedModules.get(moduleName);
        }
        
        DeclNodeVisitor declVisitor = new DeclNodeVisitor(moduleStmt);
        declVisitor.visit(moduleStmt);
                
        return declVisitor.module;
    }
    
    private void resolveGlobals(Set<String> resolvedModules) {
        for(Decl d : pendingGlobals) {
            TypeResolverNodeVisitor checker = new TypeResolverNodeVisitor(result,
                    unit,
                    resolvedGenericTypes,
                    resolvedModules,
                    d.sym.declared);
            
            d.visit(checker);
        }
    }
   
    private class DeclNodeVisitor extends AbstractNodeVisitor {
        Module module;
        
        public DeclNodeVisitor(ModuleStmt stmt) {
            String moduleName = stmt.name;
            
            this.module = new Module(root, genericTypes, result, stmt, moduleName);
        }
        
        @Override
        public void visit(ModuleStmt stmt) {            
            if(root == null) {
                root = this.module;
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            }
                        
            resolvedModules.put(module.name(), module);
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
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
            
            Module module = resolveDeclModule(moduleStmt);
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
            
            NoteStmt asStr = d.attributes.getNote("asStr");
            if(asStr != null) {
                String funcName = asStr.getAttr(0, d.name + "AsStr");
                FuncTypeInfo asStrFuncInfo = new FuncTypeInfo(funcName, 
                                                              new PtrTypeInfo(new ConstTypeInfo(TypeInfo.CHAR_TYPE)), 
                                                              Arrays.asList(new ParameterDecl(d.type, "e", null, 0)), 
                                                              0, 
                                                              Collections.emptyList());
                FuncDecl funcDecl = new FuncDecl(asStrFuncInfo.name, 
                                                 asStrFuncInfo, 
                                                 new ParametersStmt(asStrFuncInfo.parameterDecls, false), 
                                                 new EmptyStmt(), 
                                                 asStrFuncInfo.returnType);
                
                // Name must match CGen.visit(EnumDecl)
                funcDecl.attributes.addNote(new NoteStmt("foreign", Arrays.asList("__" + this.module.name() + "_" + d.name + "_AsStr")));
                funcDecl.attributes.isGlobal = true;
                funcDecl.attributes.isPublic = true;
                
                this.module.declareFunc(funcDecl, asStrFuncInfo.name, asStrFuncInfo);
            }
        }


        @Override
        public void visit(FuncDecl d) {
            FuncTypeInfo funcInfo = d.type.as();
            String funcName = d.name;
            if(funcInfo.isMethod()) {
                funcName = funcInfo.getMethodName();
            }
            
            this.module.declareFunc(d, funcName, funcInfo);
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

        @Override
        public void visit(ConstDecl d) {            
            if(this.module.addIncomplete(d)) {
                pendingGlobals.add(d);
            }
        }
        
        @Override
        public void visit(VarDecl d) {
            if(this.module.addIncomplete(d)) {
                pendingGlobals.add(d);
            }
        }
        
    }
    
    
    private static class TypeResolverNodeVisitor extends AbstractNodeVisitor {
        
        private Module module;
        private PhaseResult result;
        private CompilationUnit unit;
        private Stack<GenericTypeInfo> currentGenericType;
        
        private Set<String> resolvedGenericTypes;
        private Set<String> resolvedModules;
        
        // The type the generic module is defined in        
        private Module genericModule;
        
        public TypeResolverNodeVisitor(PhaseResult result, 
                                       CompilationUnit unit,
                                       Set<String> resolvedGenericTypes,
                                       Set<String> resolvedModules,
                                       Module module) {
            this.result = result;
            this.unit = unit;
            this.module = module;
            this.resolvedGenericTypes = resolvedGenericTypes;
            this.currentGenericType = new Stack<>();
            this.resolvedModules = resolvedModules;
        }
        
        private TypeInfo getType(String typeName) {
            // This type could actually be a generic parameter, if so
            // make sure not to attempt to get an actual defined type, 
            // as the generic parameter takes precedence
            if(!this.currentGenericType.isEmpty()) {
                for(GenericTypeInfo genType: this.currentGenericType) {
                    for(GenericParam p : genType.genericParams) {
                        if(p.name.equals(typeName)) {
                            return null;
                        }
                    }
                }
            }
            
            TypeInfo type = this.module.getType(typeName);
            if(type == null && this.genericModule != null) {
                return this.genericModule.getType(typeName);
            }
            
            return type;
        }
        
        private boolean isResolvingGenericDecl() {
            return !this.currentGenericType.isEmpty();
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
            if(expectedType == null && isResolvingGenericDecl()) {
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
            
            if(type.hasGenericArgs() && !type.hasGenerics()) {
                List<TypeInfo> args = type.getGenericArgs();
                for(TypeInfo t : args) {
                    resolveType(stmt, t);
                }
            }
            
            
            if(type.isKind(TypeKind.Ptr)) {
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
            else if(!type.isResolved()) {
                if(resolvedType == null) {
                    resolvedType = getType(type.getName());
                    if(resolvedType == null) {                        
                        if(isResolvingGenericDecl()) {
                            if(this.currentGenericType.peek().isGenericParam(type.getName())) {
                                IdentifierTypeInfo idType = type.as();
                                idType.makeGenericParam();
                            }
                            return;
                        }
                        
                        if(!type.isGenericParam()) {
                            this.result.addError(stmt, "'%s' is an unknown type", type.getName());
                        }
                        return;                        
                    }
                }
                else if(!resolvedType.isResolved()) {
                    resolveType(stmt, resolvedType);
                }
                
                IdentifierTypeInfo idType = type.as();
                idType.resolve(this.module, resolvedType, !isResolvingGenericDecl());
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
            Module importModule = module.getModule(stmt.alias != null ? stmt.alias : stmt.moduleName);
            if(this.resolvedModules.contains(importModule.name())) {
                return;
            }
            
            this.resolvedModules.add(importModule.name());
            
            TypeResolverNodeVisitor checker = new TypeResolverNodeVisitor(result, 
                                                                          unit,
                                                                          resolvedGenericTypes, 
                                                                          resolvedModules,
                                                                          importModule);
            
            importModule.getModuleStmt().visit(checker);
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
        public void visit(SwitchCaseStmt stmt) {
            stmt.cond.visit(this);
            stmt.stmt.visit(this);
        }
        
        @Override
        public void visit(SwitchStmt stmt) {
            stmt.cond.visit(this);
            
            for(SwitchCaseStmt s : stmt.stmts) {
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
        public void visit(GotoStmt stmt) {
        }
        
        @Override
        public void visit(LabelStmt stmt) {
        }
        
        @Override
        public void visit(EmptyStmt stmt) {
        }
        
        @Override
        public void visit(VarDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
    
                // infer the type from the expression
                if(d.type == null) {
                    d.type = d.expr.getResolvedType();
                    
                    if(d.type.isKind(TypeKind.Null)) {
                        this.result.addError(d, "unable to infer type from null");
                        return;
                    }
                    
                    if(d.type == null) {
                        return;
                    }
                    
                    // we can't assign an array literal, so we must
                    // infer as a pointer
                    if(d.type.isKind(TypeKind.Array)) {
                        if(!(d.expr instanceof ArrayInitExpr)) {
                            ArrayTypeInfo arrayInfo = d.type.as();
                            d.type = new PtrTypeInfo(arrayInfo.arrayOf);
                        }
                    }
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
            
            // if we can't infer the type, some
            // type hasn't been resolved correctly
            if(d.type == null) {
                return;
            }
            
            resolveType(d, d.type);
            
            if(d.attributes.isGlobal && d.attributes.isPublic) {
                Symbol sym = peekScope().getSymbol(d.name);
                sym.markComplete();
            }
            else {
                peekScope().addSymbol(this.module, d, d.name, d.type);
            }
        }
        
  
        @Override
        public void visit(ConstDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
    
                // infer the type from the expression
                if(d.type == null) {
                    d.type = d.expr.getResolvedType();
                    
                    if(d.type.isKind(TypeKind.Null)) {
                        this.result.addError(d, "unable to infer type from null");
                        return;
                    }
                    
                    if(d.type == null) {
                        return;
                    }
                    
                    // we can't assign an array literal, so we must
                    // infer as a pointer
                    if(d.type.isKind(TypeKind.Array)) {
                        if(!(d.expr instanceof ArrayInitExpr)) {
                            ArrayTypeInfo arrayInfo = d.type.as();
                            d.type = new PtrTypeInfo(arrayInfo.arrayOf);
                        }
                    }
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
            
            if(d.attributes.isGlobal && d.attributes.isPublic) {
                Symbol sym = peekScope().getSymbol(d.name);
                sym.markComplete();
            }
            else {
                peekScope().addSymbol(this.module, d, d.name, d.type, true);
            }
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
                if(funcInfo.hasGenerics()) {
                    this.currentGenericType.add(funcInfo);
                }
                
                resolveType(d, d.returnType);
                for(ParameterDecl p : d.params.params) {
                    resolveType(p, p.type);
                    if(p.defaultValue != null) {
                        p.defaultValue.visit(this);
                    }
                    
                    Scope scope = peekScope();
                    scope.addSymbol(this.module, p, p.name, p.type);
                    
                    if(p.attributes.isUsing()) {
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
                
                if(funcInfo.hasGenerics()) {
                    this.currentGenericType.pop();
                }
            }
            exitScope();
        }


        @Override
        public void visit(StructDecl d) {
            StructTypeInfo structInfo = d.type.as();
            if(structInfo.hasGenerics()) {
                this.currentGenericType.add(structInfo);
            }
            
            resolveType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
            
            if(structInfo.hasGenerics()) {
                this.currentGenericType.pop();
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
                this.currentGenericType.add(unionInfo);
            }
            
            resolveType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
            
            if(unionInfo.hasGenerics()) {
                this.currentGenericType.pop();
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
            if(d.type.hasGenericArgs()) {                
                for(TypeInfo type: d.type.getGenericArgs()) {
                    resolveType(d, type);        
                }
            }
            
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
                idInfo.resolve(this.module, type, !isResolvingGenericDecl());
            }
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
        
        private FuncPtrTypeInfo inferFuncCallExpr(FuncCallExpr expr, FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments, boolean isMethodCall) {
            for(Expr arg : expr.arguments) {
                arg.visit(this);
            }
            
            expr.genericArgs = new ArrayList<>(funcPtr.genericParams.size());
            
            for(GenericParam p : funcPtr.genericParams) {
                for(int j = 0; j < funcPtr.params.size(); j++) {
                    TypeInfo paramType = funcPtr.params.get(j);
                    
                    if(j >= suppliedArguments.size()) {
                        break;
                    }
                    
                    TypeInfo inferredType = inferredType(p.name, paramType, suppliedArguments.get(j).getResolvedType());
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
        }
        
        private boolean isMethodSyntax(FuncCallExpr expr, FuncPtrTypeInfo funcPtr, List<Expr> suppliedArguments) {
            if(!(expr.object instanceof GetExpr)) {
                return false;
            }
            
            GetExpr getExpr = (GetExpr) expr.object;
            if(!getExpr.field.getResolvedType().isKind(TypeKind.Func)) {
                return false;
            }
            
            getExpr.isMethodCall = true;
            if(!funcPtr.params.isEmpty()) {
                TypeInfo paramInfo = funcPtr.params.get(0);                    
                TypeInfo argInfo = getExpr.object.getResolvedType();
                
                
                // Determine if we need to premote the object to a
                // pointer depending on what the method is expecting as an
                // argument
                if(TypeInfo.isPtrAggregate(paramInfo)) {
                    if(!TypeInfo.isPtrAggregate(argInfo)) {
                        
                        // Can't take the address of an R-Value 
                        if(getExpr.object instanceof FuncCallExpr) {
                            this.result.addError(getExpr.object, 
                                    "cannot take the return value address of '%s' as it's an R-Value", getExpr.field.variable);
                        }
                        
                        getExpr.object = new UnaryExpr(TokenType.BAND, new GroupExpr(getExpr.object));
                        getExpr.object.resolveTo(new PtrTypeInfo(argInfo));
                    }
                }
            }
            
            suppliedArguments.add(0, getExpr.object);            
            return true;
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
            
            boolean isMethod = false;
                        
            FuncPtrTypeInfo funcPtr = null;
            if(type.isKind(TypeKind.Func)) {
                FuncTypeInfo funcInfo = type.as();
                isMethod = funcInfo.isMethod();
                
                funcPtr = funcInfo.asPtr();
            }
            else {
                funcPtr = type.as();
            }
            
            
            List<Expr> suppliedArguments = new ArrayList<>(expr.arguments);
            
            // see if this is method call syntax
            boolean isMethodCall = isMethod && isMethodSyntax(expr, funcPtr, suppliedArguments);
            
            // type inference for generic functions 
            if(funcPtr.hasGenerics() && expr.genericArgs.isEmpty()) {            
                funcPtr = inferFuncCallExpr(expr, funcPtr, suppliedArguments, isMethodCall);            
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
                
                // Might be a generic type not defined
                if(sym == null && isResolvingGenericDecl()) {                    
                    return;
                }
                
                if(sym == null) {
                    this.result.addError(expr, "unknown variable '%s'", expr.variable);
                    return;
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(this.module, sym.getType(), !isResolvingGenericDecl());
                
                // If this is a type, we will use the resolved type from the 
                // symbol (as it might have generic types); otherwise it is
                // a variable name
                TypeInfo newType = type.getResolvedType();
                if(newType.sym != null && sym.isType()) {
                    expr.sym = newType.sym;
                }
                else {
                    expr.sym = sym;
                }
                expr.resolveTo(expr.type); // TODO: remove type from IdExpr
            }
        }
        
        @Override
        public void visit(FuncIdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                TypeInfo resolvedType = this.module.getFuncType(expr.variable); 
                
                if(resolvedType == null) {
                    Symbol sym = peekScope().getSymbol(expr.variable); 
                    
                    if(sym == null || !sym.getType().isKind(TypeKind.FuncPtr)) {
                        if(sym == null && isResolvingGenericDecl()) {                    
                            return;
                        }
                        
                        this.result.addError(expr, "unknown function '%s'", expr.variable);
                        return;                        
                    }
                    
                    resolvedType = sym.getType();
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(this.module, resolvedType, !isResolvingGenericDecl());

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
                    if(isResolvingGenericDecl()) {
                        return;
                    }
                    
                    TypeInfo type = getType(expr.type.getName());
                    if(type == null) {
                        resolveType(expr, expr.type);
                        
                        if(!expr.type.isResolved()) {
                            this.result.addError(expr, "unknown type '%s'", expr.variable);
                            return;   
                        }
                    }
                    else {
                        sym = type.sym;
                    }
                }
                
                if(expr.type instanceof IdentifierTypeInfo) {
                    IdentifierTypeInfo type = expr.type.as();
                    type.resolve(this.module, sym.getType(), !isResolvingGenericDecl());
                    
                    // If this is a type, we will use the resolved type from the 
                    // symbol (as it might have generic types); otherwise it is
                    // a variable name
                    TypeInfo newType = type.getResolvedType();
                    if(newType.sym != null && sym.isType()) {
                        expr.sym = newType.sym;
                    }
                    else {
                        expr.sym = sym;
                    }
                    expr.resolveTo(expr.type); // TODO: remove type from IdExpr
                }
                else {
                    expr.resolveTo(expr.type);
                }
            }
        }
        
        private boolean resolveAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value) {
            return resolveAggregate(type, field, expr, value, true);
        }
        
        private boolean resolveAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value, boolean error) { 
            if(type == null) {
                this.result.addError(expr, "unknown type");
                return false;
            }
            
            resolveType(expr, type);
            
            switch(type.getKind()) {
                case Ptr: {
                    PtrTypeInfo ptrInfo = type.as();
                    return resolveAggregate(ptrInfo.ptrOf, field, expr, value, error);                    
                }
                case Const: {
                    ConstTypeInfo constInfo = type.as();
                    return resolveAggregate(constInfo.constOf, field, expr, value, error);
                }
                case Union:
                case Struct: {
                    AggregateTypeInfo structInfo = type.as();
                    for(FieldInfo fieldInfo : structInfo.fieldInfos) {  
                        if(fieldInfo.type.isAnonymous()) {
                            if(resolveAggregate(fieldInfo.type, field, expr, value, false)) {
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
                    
                    FuncTypeInfo funcInfo = this.module.getMethodType(type, field.getName());
                    if(funcInfo != null) {                        
                        if(funcInfo.parameterDecls.isEmpty()) {
                            if(error) {
                                this.result.addError(expr, "'%s' does not have a parameter of '%s'", funcInfo.getName(), structInfo.name);
                            }
                            break;
                        }              
                        
                        ParameterDecl objectParam = funcInfo.parameterDecls.get(0);
                        resolveType(objectParam, objectParam.type);
                        
                        TypeInfo baseType = objectParam.type.getResolvedType();
                        if(TypeInfo.isPtrAggregate(baseType)) {
                            baseType = ((PtrTypeInfo) baseType.as()).getBaseType().getResolvedType();
                        }
                        
                        if(baseType.strictEquals(structInfo)) {
                            if(!field.isResolved()) {
                                resolveType(expr, field, funcInfo);
                            }
                            
                            expr.resolveTo(field.getResolvedType());
                            return true;
                        }
                    }
                    
                    if(error) {
                        this.result.addError(expr, "'%s' does not have field '%s'", structInfo.name, field.name);
                    }
                    break;
                }
                case Enum: {
                    if(value != null) {
                        if(error) {
                            this.result.addError(expr, "'%s.%s' can not be reassigned", type.name, field.name);
                        }
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
                        if(error) {
                            this.result.addError(expr, "'%s' does not have field '%s'", enumInfo.name, field.name);
                        }
                    }
                    break;
                }
                case Identifier: {
                    // if we are parsing a Generic structure, ignore that we
                    // can't fully resolve this type (will get resolved when defined with concrete type)
                    if(isResolvingGenericDecl()) {
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
                if(!expr.field.isResolved()) {
                    expr.field.resolveTo(expr.field.type);
                }
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
                if(!expr.field.isResolved()) {
                    expr.field.resolveTo(expr.field.type);
                }
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
                    else if(type.isKind(TypeKind.Array)) {
                        ArrayTypeInfo arrayInfo = type.as();
                        expr.resolveTo(arrayInfo.arrayOf);
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
        public void visit(TernaryExpr expr) {
            expr.cond.visit(this);
            expr.then.visit(this);
            expr.other.visit(this);
            
            expr.resolveTo(expr.then.getResolvedType());
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
                    expr.resolveTo(constInfo.getBaseType().getResolvedType());
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
