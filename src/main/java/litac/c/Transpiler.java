/*
 * see license.txt
 */
package litac.c;

import java.io.File;

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
import litac.Errors;
import litac.ast.Expr;
import litac.ast.NodeVisitor;
import litac.ast.Stmt.BlockStmt;
import litac.ast.Stmt.BreakStmt;
import litac.ast.Stmt.ContinueStmt;
import litac.ast.Stmt.DoWhileStmt;
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
import litac.checker.TypeCheckResult;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.checker.TypeChecker;
import litac.checker.TypeChecker.TypeCheckerOptions;
import litac.checker.TypeResolver;
import litac.checker.TypeResolverResult;
import litac.checker.TypeResolverResult.TypeResolverError;

/**
 * @author Tony
 *
 */
public class Transpiler {

    public static class TranspilerOptions {
        public File outputDir;
    }

    public static void transpile(ProgramStmt program, TranspilerOptions options) {
        TypeResolverResult resolverResult = TypeResolver.resolveTypes(program);
        if(resolverResult.hasErrors()) {
            for(TypeResolverError error : resolverResult.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            }
        }
        
        TypeCheckResult checkerResult = TypeChecker.typeCheck(new TypeCheckerOptions(), program);
        if(checkerResult.hasErrors()) {
            for(TypeCheckError error : checkerResult.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            }
        }
        
        CWriterNodeVisitor visitor = new CWriterNodeVisitor();
        program.visit(visitor);
        
        System.out.println(visitor.sb);
    }

    private static class CWriterNodeVisitor implements NodeVisitor {

        private StringBuilder sb;
        private int indent;

        /**
         * 
         */
        public CWriterNodeVisitor() {
            this.sb = new StringBuilder();
            this.indent = 0;
        }
        
        @Override
        public void visit(ProgramStmt stmt) {
           // stmt.
        }

        @Override
        public void visit(ModuleStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(ImportStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(VarFieldStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(StructFieldStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(UnionFieldStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(IfStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(WhileStmt stmt) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(DoWhileStmt stmt) {
            // TODO Auto-generated method stub

        }

 
        @Override
        public void visit(ForStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(BreakStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(ContinueStmt stmt) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(ReturnStmt stmt) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(BlockStmt stmt) {
            // TODO Auto-generated method stub

        }

  
        @Override
        public void visit(ConstDecl d) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(EnumDecl d) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(FuncDecl d) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(StructDecl d) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(TypedefDecl d) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(UnionDecl d) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(VarDecl d) {
            // TODO Auto-generated method stub

        }
        
        @Override
        public void visit(InitExpr expr) {            
        }


        @Override
        public void visit(NullExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(BooleanExpr expr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(NumberExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(StringExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(GroupExpr expr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(FuncCallExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(IdentifierExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(GetExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(UnaryExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(BinaryExpr expr) {
            // TODO Auto-generated method stub

        }


        @Override
        public void visit(DotExpr expr) {
            // TODO Auto-generated method stub

        }

    }
}
