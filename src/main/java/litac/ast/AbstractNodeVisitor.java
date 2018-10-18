/*
 * see license.txt
 */
package litac.ast;

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
import litac.ast.Expr.FuncIdentifierExpr;
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
import litac.ast.Stmt.DeferStmt;
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

/**
 * @author Tony
 *
 */
public abstract class AbstractNodeVisitor implements NodeVisitor {

    @Override
    public void visit(ProgramStmt stmt) {
        for(ImportStmt i : stmt.imports) {
            i.visit(this);
        }
        
        for(Decl d : stmt.declarations) {
            d.visit(this);
        }        
    }

    @Override
    public void visit(ModuleStmt stmt) {
        for(ImportStmt i : stmt.imports) {
            i.visit(this);
        }
        
        for(Decl d : stmt.declarations) {
            d.visit(this);
        }
    }

    @Override
    public void visit(ImportStmt stmt) {
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
    public void visit(GetExpr expr) {
    }


    @Override
    public void visit(UnaryExpr expr) {
    }


    @Override
    public void visit(BinaryExpr expr) {
    }


    @Override
    public void visit(DotExpr expr) {
    }

    @Override
    public void visit(ArrayInitExpr expr) {
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
