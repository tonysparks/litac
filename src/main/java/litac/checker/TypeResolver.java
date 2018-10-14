/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import litac.ast.Decl;
import litac.ast.Expr;
import litac.ast.NodeVisitor;
import litac.ast.Stmt;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.EnumDecl;
import litac.ast.Decl.FuncDecl;
import litac.ast.Decl.StructDecl;
import litac.ast.Decl.TypedefDecl;
import litac.ast.Decl.UnionDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.Expr.ArrayInitExpr;
import litac.ast.Expr.BinaryExpr;
import litac.ast.Expr.BooleanExpr;
import litac.ast.Expr.DotExpr;
import litac.ast.Expr.FuncCallExpr;
import litac.ast.Expr.GetExpr;
import litac.ast.Expr.GroupExpr;
import litac.ast.Expr.IdentifierExpr;
import litac.ast.Expr.InitExpr;
import litac.ast.Expr.NullExpr;
import litac.ast.Expr.NumberExpr;
import litac.ast.Expr.SetExpr;
import litac.ast.Expr.StringExpr;
import litac.ast.Expr.SubscriptGetExpr;
import litac.ast.Expr.SubscriptSetExpr;
import litac.ast.Expr.UnaryExpr;
import litac.ast.Stmt.BlockStmt;
import litac.ast.Stmt.BreakStmt;
import litac.ast.Stmt.ContinueStmt;
import litac.ast.Stmt.DoWhileStmt;
import litac.ast.Stmt.FieldStmt;
import litac.ast.Stmt.ForStmt;
import litac.ast.Stmt.IfStmt;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.ast.Stmt.ProgramStmt;
import litac.ast.Stmt.ReturnStmt;
import litac.ast.Stmt.StructFieldStmt;
import litac.ast.Stmt.UnionFieldStmt;
import litac.ast.Stmt.VarFieldStmt;
import litac.ast.Stmt.WhileStmt;
import litac.ast.TypeInfo;
import litac.ast.TypeInfo.EnumFieldInfo;
import litac.ast.TypeInfo.IdentifierTypeInfo;
import litac.ast.TypeInfo.ParameterInfo;
import litac.ast.TypeInfo.TypeKind;
import litac.util.Stack;

/**
 * @author Tony
 *
 */
public class TypeResolver {
   
    
    public static TypeResolverResult resolveTypes(ProgramStmt stmt) {
        TypeResolverNodeVisitor resolver = new TypeResolverNodeVisitor();
        stmt.visit(resolver);
                
        return resolver.result;
    }
    
    private static class TypeResolverNodeVisitor implements NodeVisitor {

        private TypeResolverResult result;
        
        
        private Map<String, TypeResolverModule> modules;
        
        private Stack<TypeResolverModule> activeModules; 
                
        public TypeResolverNodeVisitor() {            
            this.result = new TypeResolverResult();
            
            this.modules = new HashMap<>();
            this.activeModules = new Stack<>();            
        }
        
        private void visitModule(ModuleStmt stmt) {
            String moduleName = stmt.name;
            if(!modules.containsKey(moduleName)) {                
                modules.put(moduleName, new TypeResolverModule(result, moduleName));
            }
            
            TypeResolverModule module = modules.get(moduleName);
            activeModules.push(module);
            
            pushScope();
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            } 
            
            popScope();
        }
        
        private void pushScope() {
            activeModules.peek().pushScope();
        }
        
        private void popScope() {
            peekScope().resolveTypes();
            activeModules.peek().popScope();
        }
        
        private TypeResolverScope peekScope() {
            return activeModules.peek().currentScope;
        }
        
        @Override
        public void visit(ProgramStmt stmt) {
            visitModule(stmt);
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
            visitModule(stmt);
        }
        
        

        @Override
        public void visit(ImportStmt stmt) {
            if(!modules.containsKey(stmt.moduleName)) {
                modules.put(stmt.moduleName, new TypeResolverModule(result, stmt.moduleName));
            }
            
            activeModules.peek().imports.put(stmt.alias, modules.get(stmt.moduleName));
        }

        @Override
        public void visit(VarFieldStmt stmt) {
            activeModules.peek().currentScope.addUnresolvedType(stmt.type);            
        }

        @Override
        public void visit(StructFieldStmt stmt) {
            stmt.decl.visit(this);   
            activeModules.peek().currentScope.addUnresolvedType(stmt.decl.type);            
            for(FieldStmt s : stmt.decl.fields) {
                s.visit(this);
            }
        }

        @Override
        public void visit(UnionFieldStmt stmt) {
            stmt.decl.visit(this);
            activeModules.peek().currentScope.addUnresolvedType(stmt.decl.type);            
            for(FieldStmt s : stmt.decl.fields) {
                s.visit(this);
            }
        }

        @Override
        public void visit(IfStmt stmt) {
            stmt.condExpr.visit(this);
            
            pushScope();
            stmt.thenStmt.visit(this);
            popScope();
            
            if(stmt.elseStmt != null) {
                pushScope();
                stmt.elseStmt.visit(this);
                popScope();    
            }
        }

        @Override
        public void visit(WhileStmt stmt) {
            stmt.condExpr.visit(this);
            
            pushScope();
            stmt.bodyStmt.visit(this);
            popScope();
        }


        @Override
        public void visit(DoWhileStmt stmt) {
            pushScope();
            stmt.bodyStmt.visit(this);
            popScope();
            
            stmt.condExpr.visit(this);
        }

 
        @Override
        public void visit(ForStmt stmt) {
            pushScope();
            stmt.initStmt.visit(this);
            stmt.condExpr.visit(this);
            stmt.postStmt.visit(this);
            stmt.bodyStmt.visit(this);
            popScope();
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
            pushScope();
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
            popScope();

        }

  
        @Override
        public void visit(ConstDecl d) {
            d.expr.visit(this);
            peekScope().addTypeDeclaration(d);
        }

        @Override
        public void visit(EnumDecl d) {            
            for(EnumFieldInfo f : d.fields) {
                f.value.visit(this);                
            }
            
            peekScope().addTypeDeclaration(d);
        }


        @Override
        public void visit(FuncDecl d) {
            peekScope().addTypeDeclaration(d);
            pushScope();
            
            for(ParameterInfo p : d.parameterInfos) {
                peekScope().addUnresolvedType(p.type);
            }
            d.bodyStmt.visit(this);
            peekScope().addUnresolvedType(d.returnType);            
            popScope();
        }


        @Override
        public void visit(StructDecl d) {
            peekScope().addTypeDeclaration(d);
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }


        @Override
        public void visit(TypedefDecl d) {
            peekScope().addTypeDeclaration(d);
            // TODO

        }

        @Override
        public void visit(UnionDecl d) {
            peekScope().addTypeDeclaration(d);
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }


        @Override
        public void visit(VarDecl d) {
            d.expr.visit(this);
            
            TypeInfo inferredType = TypeInferencer.inferType(d.expr);            
            if(inferredType != null && d.type.isKind(TypeKind.Identifier) ) {
                IdentifierTypeInfo idInfo = d.type.as();
                idInfo.resolve(inferredType);
            }
            
            peekScope().addTypeDeclaration(d);
            peekScope().addUnresolvedType(d.type);
        }

        @Override
        public void visit(InitExpr expr) {
            peekScope().addUnresolvedType(expr.type);
            for(Expr e : expr.arguments) {
                e.visit(this);
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
        public void visit(GroupExpr expr) {
            expr.expr.visit(this);

        }

        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);            
            for(Expr arg : expr.arguments) {
                arg.visit(this);
            }            
        }


        @Override
        public void visit(IdentifierExpr expr) {
            peekScope().addUnresolvedType(expr.type);
        }


        @Override
        public void visit(GetExpr expr) {
            expr.object.visit(this);         
            TypeInfo inferedType = TypeInferencer.inferType(expr.object);
            peekScope().addUnresolvedType(expr, inferedType, expr.field);
        }


        @Override
        public void visit(UnaryExpr expr) {
            expr.expr.visit(this);
        }


        @Override
        public void visit(BinaryExpr expr) {
            expr.left.visit(this);
            expr.right.visit(this);
        }


        @Override
        public void visit(DotExpr expr) {
            expr.field.visit(this);  
        }
        
        @Override
        public void visit(ArrayInitExpr expr) {
            for(Expr d : expr.dimensions) {
                d.visit(this);
            }
            
            // TODO
            
        }
        
        @Override
        public void visit(SubscriptGetExpr expr) {
            // TODO Auto-generated method stub
            expr.object.visit(this);
            expr.index.visit(this);
        }

        @Override
        public void visit(SubscriptSetExpr expr) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void visit(SetExpr expr) {
            // TODO Auto-generated method stub
            
        }
    }
    
}
