/*
 * see license.txt
 */
package litac.checker;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Decl;
import litac.ast.Expr;
import litac.ast.Stmt;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node;
import litac.ast.Stmt.*;
import litac.checker.Scope.Variable;
import litac.checker.TypeInfo.*;
import litac.compiler.CompilationUnit;
import litac.compiler.CompileException;

/**
 * @author Tony
 *
 */
public class TypeChecker {

    public static class TypeCheckerOptions {
        public File srcDir;
        
        public TypeCheckerOptions() {
            this.srcDir = new File(System.getProperty("user.dir"));
        }
    }
    
    private TypeCheckerOptions options;
    private CompilationUnit unit;
    
    public TypeChecker(TypeCheckerOptions options, CompilationUnit unit) {
        this.options = options;
        this.unit = unit;
    }

    public TypeCheckResult typeCheck() {
        return typeCheckModule(this.unit.getMain());
    }
    
    
    private TypeCheckResult typeCheckModule(ModuleStmt module) {
        TypeCheckResult result = new TypeCheckResult();
        
        // First build up an inventory of all declarations
        DeclNodeVisitor declVisitor = new DeclNodeVisitor(result);
        declVisitor.visit(module);
        
        // Now validate types        
        TypeCheckerNodeVisitor checker = new TypeCheckerNodeVisitor(result, declVisitor.module);
        module.visit(checker);
        
        checker.checkTypes();
        
        result.setModule(declVisitor.module);
        
        return result;
    }
    
    private class DeclNodeVisitor extends AbstractNodeVisitor {
        Module module;
        TypeCheckResult result;
        
        public DeclNodeVisitor(TypeCheckResult result) {
            this.result = result;
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
            String moduleName = stmt.name;
            
            this.module = new Module(result, stmt, moduleName);
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            }
            
//            for(NoteStmt n : stmt.notes) {
//                this.module.declareNote(n);
//            }
        }
        
        @Override
        public void visit(ImportStmt stmt) {                                
            String moduleName = stmt.alias != null ? stmt.alias : stmt.moduleName;
            
            if(this.module.getModule(moduleName) != null) {
                this.result.addError(stmt, "duplicate import of module '%s'", moduleName);
                return;
            }
            
            ModuleStmt module = unit.getImports().get(stmt.moduleName);
            
            TypeCheckResult moduleResult = typeCheckModule(module);
            this.result.merge(moduleResult);
            
            
            this.module.importModule(stmt, moduleResult.getModule(), moduleName);
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {            
        }
        
        @Override
        public void visit(ParameterDecl d) {
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
        public void visit(EnumDecl d) {            
            this.module.declareEnum(d, d.name, (EnumTypeInfo)d.type);    
            this.module.currentScope().addVariable(d, d.name, d.type);
            
            for(EnumFieldInfo f : d.fields) {
                if(!f.value.isResolved()) {
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
            // TODO
        }

    }
    
    private class TypeCheckerNodeVisitor extends AbstractNodeVisitor {

        class TypeCheck {
            public Stmt stmt;
            public TypeInfo type;
            public TypeInfo otherType;
            public boolean isCasted;
            
            public TypeCheck(Stmt stmt, 
                             TypeInfo type,
                             TypeInfo otherType,
                             boolean isCasted) {
                this.stmt = stmt;
                this.type = type;            
                this.otherType = otherType;
                this.isCasted = isCasted;
            }
        }
        
        private Module module;
        private TypeCheckResult result;
        
        private List<TypeCheck> pendingChecks;
        
        public TypeCheckerNodeVisitor(TypeCheckResult result, Module module) {
            this.result = result;
            this.module = module;
            
            this.pendingChecks = new ArrayList<>();     
        }
        
        private void addTypeCheck(Expr expr, TypeInfo type) {
            addTypeCheck(expr, type, null);
        }
        
        private void addTypeCheck(Stmt stmt, TypeInfo type, TypeInfo otherType) {
            addTypeCheck(stmt, type, otherType, false);
        }
        
        private void addTypeCheck(Stmt stmt, TypeInfo type, TypeInfo otherType, boolean isCasted) {
            this.pendingChecks.add(new TypeCheck(stmt, type, otherType, isCasted));
        }
        
        private void checkType(Stmt stmt, TypeInfo a, TypeInfo b, boolean isCasted) {
            if(isCasted) {
                if(!a.isKind(TypeKind.Ptr) || !b.isKind(TypeKind.Ptr)) {
                    if(!a.canCastTo(b) && !b.canCastTo(a)) {
                        result.addError(stmt,
                                "'%s' can't be casted to '%s'", b, a);    
                    }
                }
            }
            else if(!a.canCastTo(b)) {
                result.addError(stmt,
                        "'%s' is not of type '%s'", b, a);
            }
        }
        
        /**
         * Does type checks for the full module, type checks are delayed to the end of walking the AST tree as certain
         * types may not have been resolved at time of processing the AST node.  
         */
        private void checkTypes() {     
            if(result.hasErrors()) {
                return;
            }
            
            for(TypeCheck check : this.pendingChecks) {
                if(check.type == null) {
                    result.addError(check.stmt,
                            "unresolved type expression");
                    return;
                }
                
                if(check.otherType != null) {
                    if(!check.type.isResolved()) {
                        result.addError(check.stmt,
                                "unresolved type expression", check.type);
                        return;
                    }
                    
                    if(!check.otherType.isResolved()) {
                        result.addError(check.stmt,
                                "unresolved type expression", check.otherType);
                        return;
                    }
                    
                    checkType(check.stmt, check.type.getResolvedType(), check.otherType.getResolvedType(), check.isCasted);                    
                }
                else {
                    Expr expr = (Expr)check.stmt;
                    
                    if(!expr.isResolved()) {
                        result.addError(check.stmt,
                                "unresolved type expression", check.stmt);
                        return;
                    }
                    
                    if(!check.type.isResolved()) {
                        result.addError(check.stmt,
                                "unresolved type expression", check.stmt);
                        return;
                    }
                    
                    checkType(check.stmt, check.type.getResolvedType(), expr.getResolvedType().getResolvedType(), check.isCasted);                                        
                }
            }
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

        private void resolveType(Stmt stmt, TypeInfo type) {
            if(type == null) {
                return;
            }
            
            if(!type.isResolved()) {
                TypeInfo resolvedType = module.getType(type.getName());
                if(resolvedType == null) {
                    this.result.addError(stmt, "'%s' is an unknown type", type.getName());
                }
                else {
                    IdentifierTypeInfo idType = type.as();
                    idType.resolve(resolvedType);
                }
            }
            else if(type.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = type.as();
                resolveType(stmt, ptrInfo.ptrOf);
            }
            else if(type.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = type.as();
                resolveType(stmt, arrayInfo.arrayOf);
                
                if(arrayInfo.length < 0) {
                    if(arrayInfo.lengthExpr == null) {
                        this.result.addError(stmt, "'%s' has an unknown array length", type.getName());    
                    }
                    else {
                        arrayInfo.lengthExpr.visit(this);
                        
                        if(arrayInfo.lengthExpr instanceof NumberExpr) {
                              NumberExpr nExpr = (NumberExpr) arrayInfo.lengthExpr;                   
                              validateArrayDimension(arrayInfo.lengthExpr, nExpr.type);
                              
                              arrayInfo.length = ((Number)nExpr.number.getValue()).intValue();
                              
                          }
                          else if(arrayInfo.lengthExpr instanceof IdentifierExpr) {
                              IdentifierExpr iExpr = (IdentifierExpr)arrayInfo.lengthExpr;
                              if(iExpr.declType instanceof ConstDecl) {
                                  validateArrayDimension(arrayInfo.lengthExpr, iExpr.declType.type);
                                  
                                  ConstDecl cExpr = (ConstDecl)iExpr.declType;
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
        }
        
        private void resolveType(TypeInfo type, TypeInfo resolvedType) {
            if(type == null) {
                return;
            }
            
            if(!type.isResolved()) {                
                IdentifierTypeInfo idType = type.as();
                idType.resolve(resolvedType);
            }
            else if(type.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = type.as();
                resolveType(ptrInfo.ptrOf, resolvedType);
            }
            else if(type.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = type.as();
                resolveType(arrayInfo.arrayOf, resolvedType);
                
                if(arrayInfo.length < 0) {
                    if(arrayInfo.lengthExpr != null) {
                        arrayInfo.lengthExpr.visit(this);
                        
                        if(arrayInfo.lengthExpr instanceof NumberExpr) {
                              NumberExpr nExpr = (NumberExpr) arrayInfo.lengthExpr;                   
                              validateArrayDimension(arrayInfo.lengthExpr, nExpr.type);
                              
                              arrayInfo.length = ((Number)nExpr.number.getValue()).intValue();
                              
                          }
                          else if(arrayInfo.lengthExpr instanceof IdentifierExpr) {
                              IdentifierExpr iExpr = (IdentifierExpr)arrayInfo.lengthExpr;
                              if(iExpr.declType instanceof ConstDecl) {
                                  validateArrayDimension(arrayInfo.lengthExpr, iExpr.declType.type);
                                  
                                  ConstDecl cExpr = (ConstDecl)iExpr.declType;
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
        }
        
        
        @Override
        public void visit(ModuleStmt stmt) {
            enterScope();
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            } 
            
            exitScope();
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
            FuncDecl funcDecl = null;
            
            Node parent = stmt.getParentNode();
            while(parent != null) {
                if(parent instanceof FuncDecl) {
                    funcDecl = (FuncDecl)parent;
                    break;
                }
                
                parent = parent.getParentNode();
            }
            
            if(stmt.returnExpr != null) {
                stmt.returnExpr.visit(this);                                                
                addTypeCheck(stmt.returnExpr, funcDecl.returnType);
            }
            else {
                addTypeCheck(stmt, TypeInfo.VOID_TYPE, funcDecl.returnType);
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
            
            peekScope().addVariable(d, d.name, d.type);
            
            if(d.expr != null) {
                addTypeCheck(d.expr, d.type);
            }
        }
        
  
        @Override
        public void visit(ConstDecl d) {
            d.expr.visit(this);

            // infer the type from the expression
            if(d.type == null) {
                d.type = d.expr.getResolvedType();
            }
            
            resolveType(d, d.type);
            
            peekScope().addVariable(d, d.name, d.type);
            addTypeCheck(d.expr, d.type);
        }

        @Override
        public void visit(EnumDecl d) {            
            for(EnumFieldInfo f : d.fields) {
                f.value.visit(this);
                addTypeCheck(f.value, TypeInfo.I32_TYPE);
            }
        }


        @Override
        public void visit(FuncDecl d) {
            enterScope();
            {
                 
                resolveType(d, d.returnType);
                for(ParameterDecl p : d.params.params) {
                    resolveType(p, p.type);
                    peekScope().addVariable(p, p.name, p.type);
                }
                
                d.bodyStmt.visit(this);
            }
            exitScope();
        }


        @Override
        public void visit(StructDecl d) {
            resolveType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
            resolveType(stmt, stmt.type);
        }

        @Override
        public void visit(UnionDecl d) {
            resolveType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
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
                        // TODO:
                        throw new CompileException("Not implemented yet");
                    }
                    
                }
            }
            
            return type;
        }
        
        @Override
        public void visit(CastExpr expr) {
            resolveType(expr, expr.castTo);
            expr.expr.visit(this);
            
            addTypeCheck(expr, expr.expr.getResolvedType(), expr.castTo, true);
        }
        
        @Override
        public void visit(SizeOfExpr expr) {
            //expr.expr.visit(this);
        }
        
        @Override
        public void visit(InitArgExpr expr) {
            expr.value.visit(this);
            expr.resolveTo(expr.value.getResolvedType());
        }
                
        private void checkAggregateInitFields(InitExpr expr, TypeInfo aggInfo, List<FieldInfo> fieldInfos, List<InitArgExpr> arguments) {
            if(fieldInfos.size() != arguments.size()) {
                // TODO should this be allowed??
                this.result.addError(expr, "incorrect number of arguments");
            }
            
            
            // Validate Named fields
            for(int index = 0; index < arguments.size(); index++) {
                InitArgExpr arg = arguments.get(index);
                if(arg.fieldName == null) {                    
                    if(arg.argPosition >= fieldInfos.size()) {
                        this.result.addError(arg, "No field defined at position '%d' for '%s'", arg.argPosition, aggInfo.getName());
                    }
                    else {
                        FieldInfo fieldInfo = fieldInfos.get(arg.argPosition);
                        addTypeCheck(arg, fieldInfo.type);
                    }
                }
                else {
                    boolean matchedField = false;
                    for(int i = 0; i < fieldInfos.size(); i++) {
                        FieldInfo fieldInfo = fieldInfos.get(i);
                        
                        if(fieldInfo.name.equals(arg.fieldName)) {
                            addTypeCheck(arg, fieldInfo.type);
                                       
                            matchedField = true;
                            break;
                        }
                    }
                    
                    if(!matchedField) {
                        this.result.addError(arg, "'%s' is not defined in '%s'", arg.fieldName, aggInfo.getName());
                    }
                }
            }
            
        }
        
        @Override
        public void visit(InitExpr expr) {
            for(InitArgExpr e : expr.arguments) {
                e.visit(this);
            }
            
            TypeInfo type = getAggregateFieldTypeInfo(expr);          
            if(type == null) {
                this.result.addError(expr, "'%s' is an unknown type", expr.type);
                return;
            }
            
            if(!expr.type.isResolved()) {
                IdentifierTypeInfo idInfo = expr.type.as();
                idInfo.resolve(type);
            }
            
            switch(type.getKind()) {
                case Struct: {
                    StructTypeInfo structInfo = expr.type.as();
                    checkAggregateInitFields(expr, structInfo, structInfo.fieldInfos, expr.arguments);
                    
                    break;
                }
                case Union: {
                    UnionTypeInfo unionInfo = expr.type.as();
                    // TODO, think about how these types are initialized
                    
//                    if(unionInfo.fieldInfos.size() != expr.arguments.size()) {
//                        // TODO should this be allowed??
//                        this.result.addError(expr, "incorrect number of arguments");
//                    }
//                    
//                    for(int i = 0; i < unionInfo.fieldInfos.size(); i++) {
//                        if(i < expr.arguments.size()) {                       
//                            addTypeCheck(expr.arguments.get(i), unionInfo.fieldInfos.get(i).type);
//                        }
//                    }
                    break;
                }
                default: {
                    this.result.addError(expr, "'%s' is an invalid type for initialization", type.getName());
                }
            }
            
        }

        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);            
            
            
            TypeInfo type = expr.object.getResolvedType();
            if(!type.isKind(TypeKind.Func)) {
                this.result.addError(expr, "'%s' is not a function", type.getName());
                return;
            }
            
            FuncTypeInfo funcInfo = type.as();
            expr.resolveTo(funcInfo.returnType);
            
            if(funcInfo.parameterDecls.size() != expr.arguments.size()) {
                if(funcInfo.parameterDecls.size() > expr.arguments.size() || !funcInfo.isVararg) {                    
                    this.result.addError(expr, "'%s' called with incorrect number of arguments", type.getName());
                }
            }
            
            int i = 0;
            for(; i < funcInfo.parameterDecls.size(); i++) {
                TypeInfo paramInfo = funcInfo.parameterDecls.get(i).type;
                resolveType(expr, paramInfo);
                
                if(i < expr.arguments.size()) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                    
                    resolveType(arg, arg.getResolvedType());
                    addTypeCheck(arg, arg.getResolvedType(), paramInfo);
                }
            }
            
            if(funcInfo.isVararg) {
                for(; i < expr.arguments.size(); i++) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                    
                    resolveType(arg, arg.getResolvedType());
                }                
            }
        }


        @Override
        public void visit(IdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                Variable resolvedType = peekScope().getVariable(expr.variable); 
                
                if(resolvedType == null) {
                    this.result.addError(expr, "unknown variable '%s'", expr.type.getName());
                    return;
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(resolvedType.type);
                expr.declType = resolvedType.decl;
            }
        }
        
        @Override
        public void visit(FuncIdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                TypeInfo resolvedType = this.module.getFuncType(expr.variable); 
                
                if(resolvedType == null) {
                    this.result.addError(expr, "unknown function '%s'", expr.variable);
                    return;
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(resolvedType);
            }
        }
        
        private boolean resolveAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value) {            
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
                                resolveType(field, fieldInfo.type);
                            }
                            
                            expr.resolveTo(fieldInfo.type);
                            if(value != null) {
                                addTypeCheck(expr, value.getResolvedType(), fieldInfo.type);
                            }
                            
                            return true;
                        }
                    }
                    this.result.addError(expr, "'%s' does not have field '%s'", structInfo.name, field.name);
                    break;
                }                
                case Union: {
                    UnionTypeInfo unionInfo = type.as();
                    for(FieldInfo fieldInfo : unionInfo.fieldInfos) {
                        if(fieldInfo.name.equals(field.getName())) {
                            if(!field.isResolved()) {
                                resolveType(field, fieldInfo.type);
                            }
                            
                            expr.resolveTo(fieldInfo.type);
                            if(value != null) {
                                addTypeCheck(expr, value.getResolvedType(), fieldInfo.type);
                            }
                            
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
                            if(fieldInfo.name.equals(field.getName())) {                                
                                //expr.resolveTo(fieldInfo.value.getResolvedType());
                                expr.resolveTo(enumInfo);
                                return true;
                            }
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
                    TypeInfo type = expr.expr.getResolvedType();
                    if(!type.isKind(TypeKind.Ptr)) {
                        this.result.addError(expr, "'%s' is not a pointer type", type);
                        return;
                    }
                    
                    PtrTypeInfo ptrInfo = type.as();
                    expr.resolveTo(ptrInfo.ptrOf.getResolvedType());
                    break;
                }
                case BAND: {
                    TypeInfo type = expr.expr.getResolvedType();
                    PtrTypeInfo ptrInfo = new PtrTypeInfo(type);
                    expr.resolveTo(ptrInfo);
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
            
            addTypeCheck(expr.left, expr.right.getResolvedType());
            addTypeCheck(expr.right, expr.left.getResolvedType());
        }


        @Override
        public void visit(ArrayInitExpr expr) {
            ArrayTypeInfo arrayInfo = expr.getResolvedType().as();
            resolveType(expr, arrayInfo);
            
            for(Expr v : expr.values) {
                v.visit(this);
                
                addTypeCheck(v, v.getResolvedType(), arrayInfo.arrayOf.getResolvedType());
            }
            
            // TODO -- validate sizes?
            if(arrayInfo.length < 0) {
                //if(expr.
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
            
            TypeKind indexKind = expr.index.getResolvedType().getKind();
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
                case i128:
                case u128:
                    break;
                default: {
                    this.result.addError(expr, "'%s' invalid index value", indexKind.name());
                    return;
                }
            }
        }

        @Override
        public void visit(SubscriptSetExpr expr) {
            expr.object.visit(this);
            expr.index.visit(this);
            expr.value.visit(this);
            
            TypeInfo objectInfo = expr.object.getResolvedType();
            TypeKind objectKind = objectInfo.getKind();
            switch(objectKind) {
                case Str:
                case Array:
                case Ptr:
                    break;
                default: {
                    this.result.addError(expr, "invalid index into '%s'", objectKind.name());
                    return;
                }
            }
            
            TypeKind indexKind = expr.index.getResolvedType().getKind();
            switch(indexKind) {
                case i8:
                case u8:
                case i16:
                case u16:
                case i32:
                case u32:
                case i64:
                case u64:
                case i128:
                case u128:
                    break;
                default: {
                    this.result.addError(expr, "'%s' invalid index value", indexKind.name());
                    return;
                }
            }
            
            addTypeCheck(expr.object, expr.value.getResolvedType(), objectInfo);
        }
        
       
        
    }
    
}
