/*
 * see license.txt
 */
package litac.c;

import java.io.File;

import litac.ast.Decl.*;
import litac.ast.Expr;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.ast.NodeVisitor;

import litac.Errors;

import litac.checker.TypeCheckResult;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.checker.TypeChecker;
import litac.checker.TypeChecker.TypeCheckerOptions;
import litac.util.OS.OsType;

/**
 * @author Tony
 *
 */
public class Transpiler {

    public static class TranspilerOptions {
        public File outputDir;        
        public String outputFileName;
        public TypeCheckerOptions checkerOptions;
        public OsType targetOS;
        
        public TranspilerOptions() {
            this.checkerOptions = new TypeCheckerOptions();
            this.outputDir = new File(System.getProperty("user.dir"), "output");
            this.outputFileName = "a";
            
            this.targetOS = OsType.WINDOWS;
        }
    }

    public static void transpile(ProgramStmt program, TranspilerOptions options) {
        TypeCheckResult checkerResult = TypeChecker.typeCheck(options.checkerOptions, program);
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
        public void visit(DeferStmt stmt) {
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
        public void visit(FuncIdentifierExpr expr) {
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
        public void visit(SetExpr expr) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void visit(SubscriptSetExpr expr) {
            // TODO Auto-generated method stub
            
        }
    }
}
