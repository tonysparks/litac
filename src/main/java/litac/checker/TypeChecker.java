/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import litac.ast.AbstractNodeVisitor;
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
import litac.ast.Expr.StringExpr;
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
import litac.ast.TypeInfo.EnumField;
import litac.ast.TypeInfo.Parameter;
import litac.ast.TypeInfo.StructTypeInfo;
import litac.ast.TypeInfo.TypeKind;
import litac.util.Stack;

/**
 * @author Tony
 *
 */
public class TypeChecker {

    public static class TypeCheckerOptions {
        
    }
    
    
    public static TypeCheckResult typeCheck(TypeCheckerOptions options, ProgramStmt stmt) {
        TypeCheckerNodeVisitor checker = new TypeCheckerNodeVisitor(options);
        stmt.visit(checker);
        
        return checker.result;
    }
    
    private static class TypeCheckerNodeVisitor extends AbstractNodeVisitor {

        private TypeCheckResult result;
        private TypeCheckerOptions options;
        
        private Map<String, Module> modules;
        
        private Stack<Module> activeModules; 
                
        public TypeCheckerNodeVisitor(TypeCheckerOptions options) {
            this.options = options;
            this.result = new TypeCheckResult();
            
            this.activeModules = new Stack<>();   
            this.modules = new HashMap<>();
        }
        
        private void visitModule(ModuleStmt stmt) {
            String moduleName = stmt.name;
            if(!modules.containsKey(moduleName)) {                
                modules.put(moduleName, new Module(result, moduleName));
            }
            
            Module module = modules.get(moduleName);
            activeModules.push(module);
            
            enterScope();
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            } 
            
            exitScope();
        }
        
        private void enterScope() {
            activeModules.peek().pushScope();
            
        }
        
        private void exitScope() {
            Scope scope = peekScope();
            scope.checkTypes();
            
            activeModules.peek().popScope();
        }
        
        private Scope peekScope() {
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
                modules.put(stmt.moduleName, new Module(result, stmt.moduleName));
            }
            
            activeModules.peek().imports.put(stmt.alias, modules.get(stmt.moduleName));
        }

        @Override
        public void visit(VarFieldStmt stmt) {
            //activeModules.peek().scope.addVariable(stmt);
            //stmt.
            // TODO: Figure out struct/union type definitions..
            //stmt.
        }

        @Override
        public void visit(StructFieldStmt stmt) {
            stmt.decl.visit(this);            
        }

        @Override
        public void visit(UnionFieldStmt stmt) {
            stmt.decl.visit(this);
           // stmt.decl.name
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
                FuncDecl func = peekScope().peekFuncDecl();
                peekScope().addTypeCheck(stmt.returnExpr, func.returnType);
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
        public void visit(VarDecl d) {
            d.expr.visit(this);
            
            peekScope().addVariable(d, d.name, d.type);
            peekScope().addTypeCheck(d.expr, d.type);
        }
  
        @Override
        public void visit(ConstDecl d) {
            d.expr.visit(this);
            
            peekScope().addVariable(d, d.name, d.type);
            peekScope().addTypeCheck(d.expr, d.type);
        }

        @Override
        public void visit(EnumDecl d) {            
            for(EnumField f : d.fields) {
                f.value.visit(this);
            }
            
            peekScope().addType(d, d.type);            
        }


        @Override
        public void visit(FuncDecl d) {
            peekScope().addType(d, d.type);
            
            enterScope();
            {
                peekScope().pushFuncDecl(d);
                
                peekScope().addType(d, d.returnType);
                for(Parameter p : d.parameters) {
                    peekScope().addType(d, p.type);
                    peekScope().addVariable(d, p.name, p.type);
                }
                
                d.bodyStmt.visit(this);
                
                peekScope().popsFuncDecl();
            }
            exitScope();
        }


        @Override
        public void visit(StructDecl d) {
            peekScope().addType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }

        @Override
        public void visit(UnionDecl d) {
            peekScope().addType(d, d.type);
            
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
            peekScope().addType(d, d.type);
        }

        @Override
        public void visit(InitExpr expr) {
            for(Expr e : expr.arguments) {
                e.visit(this);
            }
            
            if(expr.type.isKind(TypeKind.Struct)) {
                StructTypeInfo structInfo = expr.type.as();
                
                if(structInfo.fields.size() != expr.arguments.size()) {
                    // TODO should this be allowed??
                    this.result.addError(expr, String.format("incorrect number of arguments"));
                }
                
                for(int i = 0; i < structInfo.fields.size(); i++) {
                    if(i < expr.arguments.size()) {                       
                        peekScope().addTypeCheck(expr.arguments.get(i), structInfo.fields.get(i).type);
                    }
                }
            }
            
        }

        @Override
        public void visit(FuncCallExpr expr) {
            // TODO
            expr.object.visit(this);            
            for(Expr arg : expr.arguments) {
                arg.visit(this);
            }
        }


        @Override
        public void visit(IdentifierExpr expr) {
            peekScope().pushTypeInfo(expr.type);
        }


        @Override
        public void visit(GetExpr expr) {
            expr.object.visit(this);            
            peekScope().pushTypeInfo(expr.field);
        }
        
        @Override
        public void visit(DotExpr expr) {
            expr.field.visit(this);                
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




    }
    
}
