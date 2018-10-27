/*
 * see license.txt
 */
package litac.checker;


import java.io.File;
import java.io.FileReader;
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
import litac.checker.TypeInfo.*;
import litac.parser.Parser;
import litac.parser.Scanner;
import litac.parser.Source;

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
    
    
    public static TypeCheckResult typeCheck(TypeCheckerOptions options, ModuleStmt stmt) {
        TypeCheckResult result = new TypeCheckResult();
        
        // First build up an inventory of all declarations
        DeclNodeVisitor declVisitor = new DeclNodeVisitor(result, options);
        declVisitor.visit(stmt);
        
        // Now validate types        
        TypeCheckerNodeVisitor checker = new TypeCheckerNodeVisitor(options, result, declVisitor.module);
        stmt.visit(checker);
        
        checker.checkTypes();
        
        result.setModule(declVisitor.module);
        
        return result;
    }
    
    private static class DeclNodeVisitor extends AbstractNodeVisitor {
        Module module;
        TypeCheckResult result;
        TypeCheckerOptions options;
        
        public DeclNodeVisitor(TypeCheckResult result, TypeCheckerOptions options) {
            this.result = result;
            this.options = options;
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
            String moduleName = stmt.name;
            
            this.module = new Module(result, moduleName);
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            }
        }
        
        @Override
        public void visit(ImportStmt stmt) {        
            File importFile = new File(this.options.srcDir.getAbsolutePath(), stmt.moduleName + ".lita");
            if(!importFile.exists()) {
                this.result.addError(stmt, "could not find module '%s' at '%s'", stmt.moduleName, importFile.getAbsolutePath());
            }
            
            String moduleName = stmt.alias != null ? stmt.alias : stmt.moduleName;
            
            if(this.module.getModule(moduleName) != null) {
                this.result.addError(stmt, "duplicate import of module '%s'", moduleName);
                return;
            }
            
            
            Source source = null;
            try {
                source = new Source(importFile.getName(), new FileReader(importFile));
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
                        
            Parser parser = new Parser(new Scanner(source));
            ModuleStmt program = parser.parseModule();
            
            TypeCheckResult moduleResult = typeCheck(this.options, program);
            this.result.merge(moduleResult);
            
            
            this.module.importModule(stmt, moduleResult.getModule(), moduleName);
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {            
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
            this.module.currentScope.addVariable(d, d.name, d.type);
            
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
    
    private static class TypeCheckerNodeVisitor extends AbstractNodeVisitor {

        static class TypeCheck {
            public Stmt stmt;
            public TypeInfo type;
            public TypeInfo otherType;
            
            public TypeCheck(Stmt stmt, 
                             TypeInfo type,
                             TypeInfo otherType) {
                this.stmt = stmt;
                this.type = type;            
                this.otherType = otherType;
            }
        }
        
        private TypeCheckResult result;
        private TypeCheckerOptions options;
        private Module module;
        
        private List<TypeCheck> pendingChecks;
        
        public TypeCheckerNodeVisitor(TypeCheckerOptions options, TypeCheckResult result, Module module) {
            this.options = options;
            this.result = result;
            this.module = module;
            
            this.pendingChecks = new ArrayList<>();     
        }
        
        private void addTypeCheck(Expr expr, TypeInfo type) {
            this.pendingChecks.add(new TypeCheck(expr, type, null));
        }
        
        private void addTypeCheck(Stmt stmt, TypeInfo type, TypeInfo otherType) {
            this.pendingChecks.add(new TypeCheck(stmt, type, otherType));
        }
        
        
        /**
         * Does type checks for the full module, type checks are delayed to the end of walking the AST tree as certain
         * types may not have been resolved at time of processing the AST node.  
         */
        private void checkTypes() {        
            for(TypeCheck check : this.pendingChecks) {
                if(check.otherType != null) {
                    if(!check.type.getResolvedType().canCastTo(check.otherType.getResolvedType())) {
                        result.addError(check.stmt,
                                "'%s' is not of type '%s'", check.otherType, check.type);
                    }
                }
                else {
                    Expr expr = (Expr)check.stmt;
                    
                    if(!expr.isResolved()) {
                        result.addError(check.stmt,
                                "unresolved type expression", check.stmt);
                    }
                    
                    if(!check.type.getResolvedType().canCastTo(expr.getResolvedType().getResolvedType())) {
                        result.addError(check.stmt,
                                "'%s' is not of type '%s'", expr.getResolvedType(), check.type);
                    }
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
            return module.currentScope;
        }
        

        private void resolveType(TypeInfo type) {
            if(type == null) {
                return;
            }
            
            if(!type.isResolved()) {
                TypeInfo resolvedType = module.getType(type.getName());
                IdentifierTypeInfo idType = type.as();
                idType.resolve(resolvedType);
            }
            else if(type.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = type.as();
                resolveType(ptrInfo.ptrOf);
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
        public void visit(VarDecl d) {
            d.expr.visit(this);
            
            // infer the type from the expression
            if(d.type == null) {
                d.type = d.expr.getResolvedType();
            }
            
            // if we can't infer the type, some
            // type hasn't been resolved correctly
            if(d.type == null) {
                return;
            }
            
            resolveType(d.type);
            
            peekScope().addVariable(d, d.name, d.type);
            addTypeCheck(d.expr, d.type);
        }
        
  
        @Override
        public void visit(ConstDecl d) {
            d.expr.visit(this);

            // infer the type from the expression
            if(d.type == null) {
                d.type = d.expr.getResolvedType();
            }
            
            resolveType(d.type);
            
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
                 
                resolveType(d.returnType);
                for(ParameterInfo p : d.parameterInfos) {
                    resolveType(p.type);
                    peekScope().addVariable(d, p.name, p.type);
                }
                
                d.bodyStmt.visit(this);
            }
            exitScope();
        }


        @Override
        public void visit(StructDecl d) {
            resolveType(d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
            resolveType(stmt.type);
        }

        @Override
        public void visit(UnionDecl d) {
            resolveType(d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
        }

        @Override
        public void visit(InitExpr expr) {
            for(Expr e : expr.arguments) {
                e.visit(this);
            }
            
            TypeInfo type = this.module.getType(expr.type.getName());            
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
                    
                    if(structInfo.fieldInfos.size() != expr.arguments.size()) {
                        // TODO should this be allowed??
                        this.result.addError(expr, "incorrect number of arguments");
                    }
                    
                    for(int i = 0; i < structInfo.fieldInfos.size(); i++) {
                        if(i < expr.arguments.size()) {                       
                            addTypeCheck(expr.arguments.get(i), structInfo.fieldInfos.get(i).type);
                        }
                    }
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
            
            if(funcInfo.parameterInfos.size() != expr.arguments.size()) {
                this.result.addError(expr, "'%s' called with incorrect number of arguments", type.getName());
            }
            
            for(int i = 0; i < funcInfo.parameterInfos.size(); i++) {
                TypeInfo paramInfo = funcInfo.parameterInfos.get(i).type;
                resolveType(paramInfo);
                
                if(i < expr.arguments.size()) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                    
                    resolveType(arg.getResolvedType());
                    addTypeCheck(arg, arg.getResolvedType(), paramInfo);
                }
            }
        }


        @Override
        public void visit(IdentifierExpr expr) {
            if(!expr.type.isResolved()) {
                TypeInfo resolvedType = peekScope().getVariable(expr.variable); 
                
                if(resolvedType == null) {
                    this.result.addError(expr, "unknown variable '%s'", expr.type.getName());
                    return;
                }
                
                IdentifierTypeInfo type = expr.type.as();
                type.resolve(resolvedType);
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


        private void resolveAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value) {            
            switch(type.getKind()) {
                case Ptr: {
                    PtrTypeInfo ptrInfo = type.as();
                    resolveAggregate(ptrInfo.ptrOf, field, expr, value);
                    break;
                }
                case Struct: {
                    StructTypeInfo structInfo = type.as();
                    for(FieldInfo fieldInfo : structInfo.fieldInfos) {                                                        
                        if(fieldInfo.name.equals(field.getName())) {
                            if(!field.isResolved()) {
                                resolveType(field, fieldInfo.type);
                            }
                            
                            expr.resolveTo(fieldInfo.type);
                            if(value != null) {
                                addTypeCheck(expr, value.getResolvedType(), fieldInfo.type);
                            }
                            
                            return;
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
                            
                            return;
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
                                return;
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
        }
        
        @Override
        public void visit(GetExpr expr) {
            expr.object.visit(this);            
            
            resolveType(expr.field);
            
            if(!expr.field.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                resolveAggregate(type, expr.field, expr, null);
            }
        }
        
        @Override
        public void visit(SetExpr expr) {
            expr.object.visit(this);            
            expr.value.visit(this);
            
            resolveType(expr.field);
            
            if(!expr.field.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                resolveAggregate(type, expr.field, expr, expr.value);
            }
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
            resolveType(arrayInfo.arrayOf);
            
            for(Expr d : expr.dimensions) {
                d.visit(this);
            }
            
            for(Expr v : expr.values) {
                v.visit(this);
                
                addTypeCheck(v, v.getResolvedType(), arrayInfo.arrayOf.getResolvedType());
            }
            
            // TODO -- validate sizes?
            
            
        }
        
        @Override
        public void visit(SubscriptGetExpr expr) {
            expr.object.visit(this);
            expr.index.visit(this);
            
            TypeKind objectKind = expr.object.getResolvedType().getKind();
            switch(objectKind) {
                case Str:
                    expr.resolveTo(TypeInfo.I8_TYPE);
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
