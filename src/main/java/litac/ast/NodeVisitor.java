/*
 * see license.txt
 */
package litac.ast;

import litac.ast.Stmt.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;

/**
 * @author Tony
 *
 */
public interface NodeVisitor {

    void visit(ProgramStmt stmt);
    void visit(ModuleStmt stmt);
    void visit(ImportStmt stmt);
    void visit(VarFieldStmt stmt);
    void visit(StructFieldStmt stmt);
    void visit(UnionFieldStmt stmt);
    void visit(IfStmt stmt);
    void visit(WhileStmt stmt);
    void visit(DoWhileStmt stmt);
    void visit(ForStmt stmt);
    void visit(BreakStmt stmt);
    void visit(ContinueStmt stmt);
    void visit(ReturnStmt stmt);
    void visit(BlockStmt stmt);
    
    void visit(ConstDecl d);
    void visit(EnumDecl d);
    void visit(FuncDecl d);
    void visit(StructDecl d);
    void visit(TypedefDecl d);
    void visit(UnionDecl d);
    void visit(VarDecl d);
    
    
    void visit(InitExpr expr);
    void visit(NullExpr expr);
    void visit(BooleanExpr expr);
    void visit(NumberExpr expr);
    void visit(StringExpr expr);
    void visit(GroupExpr expr);
    void visit(FuncCallExpr expr);
    void visit(IdentifierExpr expr);
    void visit(GetExpr expr);
    void visit(UnaryExpr expr);
    void visit(BinaryExpr expr);
    void visit(DotExpr expr);
}
