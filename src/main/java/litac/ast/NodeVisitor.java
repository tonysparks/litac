/*
 * see license.txt
 */
package litac.ast;

import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;

/**
 * @author Tony
 *
 */
public interface NodeVisitor {

    void visit(ModuleStmt stmt);
    void visit(ImportStmt stmt);
    void visit(NoteStmt stmt);
    void visit(VarFieldStmt stmt);
    void visit(StructFieldStmt stmt);
    void visit(UnionFieldStmt stmt);
    void visit(EnumFieldStmt stmt);
    void visit(IfStmt stmt);
    void visit(WhileStmt stmt);
    void visit(DoWhileStmt stmt);
    void visit(ForStmt stmt);
    void visit(BreakStmt stmt);
    void visit(ContinueStmt stmt);
    void visit(ReturnStmt stmt);
    void visit(BlockStmt stmt);
    void visit(DeferStmt stmt);
    void visit(EmptyStmt stmt);
    void visit(ParametersStmt stmt);
    void visit(VarDeclsStmt stmt);
    void visit(ConstDeclsStmt stmt);
    
    void visit(ConstDecl d);
    void visit(EnumDecl d);
    void visit(FuncDecl d);
    void visit(StructDecl d);
    void visit(TypedefDecl d);
    void visit(UnionDecl d);
    void visit(VarDecl d);
    void visit(ParameterDecl d);
    
    
    void visit(CastExpr expr);
    void visit(SizeOfExpr expr);
    void visit(InitArgExpr expr);    
    void visit(InitExpr expr);    
    void visit(NullExpr expr);
    void visit(BooleanExpr expr);
    void visit(NumberExpr expr);
    void visit(StringExpr expr);
    void visit(CharExpr expr);
    void visit(GroupExpr expr);
    void visit(FuncCallExpr expr);
    void visit(IdentifierExpr expr);
    void visit(FuncIdentifierExpr expr);
    void visit(SizeOfIdentifierExpr expr);
    void visit(GetExpr expr);
    void visit(SetExpr expr);
    void visit(UnaryExpr expr);
    void visit(BinaryExpr expr);
    void visit(ArrayInitExpr expr);
    void visit(ArrayDesignationExpr expr);
    void visit(SubscriptGetExpr expr);
    void visit(SubscriptSetExpr expr);
    
    
    public static abstract class AbstractNodeVisitor implements NodeVisitor {

        @Override
        public void visit(ModuleStmt stmt) {
        }

        @Override
        public void visit(ImportStmt stmt) {
        }
        
        @Override
        public void visit(InitArgExpr expr) {
        }
        
        @Override
        public void visit(InitExpr expr) {
        }
        
        @Override
        public void visit(StructFieldStmt stmt) {
        }
        
        @Override
        public void visit(UnionFieldStmt stmt) {
        }
        
        @Override
        public void visit(EnumFieldStmt stmt) {
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
        }
        

        @Override
        public void visit(IfStmt stmt) {
        }

        @Override
        public void visit(WhileStmt stmt) {
        }


        @Override
        public void visit(DoWhileStmt stmt) {
        }


        @Override
        public void visit(ForStmt stmt) {
        }

        @Override
        public void visit(BreakStmt stmt) {
        }

        @Override
        public void visit(ContinueStmt stmt) {
        }


        @Override
        public void visit(ReturnStmt stmt) {
        }

        @Override
        public void visit(BlockStmt stmt) {
        }
        
        @Override
        public void visit(DeferStmt stmt) {
        }
        
        @Override
        public void visit(EmptyStmt stmt) {
        }
        
        @Override
        public void visit(ParametersStmt stmt) {
        }

        @Override
        public void visit(NoteStmt stmt) {
        }
        
        @Override
        public void visit(VarDeclsStmt stmt) {
        }
        
        @Override
        public void visit(ConstDeclsStmt stmt) {
        }
        

        @Override
        public void visit(ConstDecl d) {
        }

        @Override
        public void visit(EnumDecl d) {
        }


        @Override
        public void visit(FuncDecl d) {
        }


        @Override
        public void visit(StructDecl d) {
        }


        @Override
        public void visit(TypedefDecl d) {
        }

        @Override
        public void visit(UnionDecl d) {
        }


        @Override
        public void visit(VarDecl d) {
        }
        
        @Override
        public void visit(ParameterDecl d) {
        }

        @Override
        public void visit(CastExpr expr) {
        }
        
        @Override
        public void visit(SizeOfExpr expr) {
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
        public void visit(CharExpr expr) {
        }


        @Override
        public void visit(GroupExpr expr) {
        }

        @Override
        public void visit(FuncCallExpr expr) {
        }


        @Override
        public void visit(IdentifierExpr expr) {
        }

        @Override
        public void visit(FuncIdentifierExpr expr) {
        }
        
        @Override
        public void visit(SizeOfIdentifierExpr expr) {
        }

        @Override
        public void visit(GetExpr expr) {
        }


        @Override
        public void visit(UnaryExpr expr) {
        }


        @Override
        public void visit(BinaryExpr expr) {
        }

        @Override
        public void visit(ArrayInitExpr expr) {
        }
        
        @Override
        public void visit(ArrayDesignationExpr expr) {
        }
        
        @Override
        public void visit(SetExpr expr) {
        }
        
        @Override
        public void visit(SubscriptGetExpr expr) {
        }
        
        @Override
        public void visit(SubscriptSetExpr expr) {
        }
        
    }

}
