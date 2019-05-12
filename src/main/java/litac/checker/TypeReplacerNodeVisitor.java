package litac.checker;

import java.util.List;

import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.EnumDecl;
import litac.ast.Decl.FuncDecl;
import litac.ast.Decl.ParameterDecl;
import litac.ast.Decl.StructDecl;
import litac.ast.Decl.TypedefDecl;
import litac.ast.Decl.UnionDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.Expr;
import litac.ast.Expr.ArrayInitExpr;
import litac.ast.Expr.BinaryExpr;
import litac.ast.Expr.BooleanExpr;
import litac.ast.Expr.CastExpr;
import litac.ast.Expr.FuncCallExpr;
import litac.ast.Expr.FuncIdentifierExpr;
import litac.ast.Expr.GetExpr;
import litac.ast.Expr.GroupExpr;
import litac.ast.Expr.IdentifierExpr;
import litac.ast.Expr.InitArgExpr;
import litac.ast.Expr.InitExpr;
import litac.ast.Expr.NullExpr;
import litac.ast.Expr.NumberExpr;
import litac.ast.Expr.SetExpr;
import litac.ast.Expr.SizeOfExpr;
import litac.ast.Expr.StringExpr;
import litac.ast.Expr.SubscriptGetExpr;
import litac.ast.Expr.SubscriptSetExpr;
import litac.ast.Expr.UnaryExpr;
import litac.ast.NodeVisitor;
import litac.ast.Stmt;
import litac.ast.Stmt.BlockStmt;
import litac.ast.Stmt.BreakStmt;
import litac.ast.Stmt.ContinueStmt;
import litac.ast.Stmt.DeferStmt;
import litac.ast.Stmt.DoWhileStmt;
import litac.ast.Stmt.EmptyStmt;
import litac.ast.Stmt.ForStmt;
import litac.ast.Stmt.IfStmt;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.ast.Stmt.NoteStmt;
import litac.ast.Stmt.ParametersStmt;
import litac.ast.Stmt.ReturnStmt;
import litac.ast.Stmt.StructFieldStmt;
import litac.ast.Stmt.UnionFieldStmt;
import litac.ast.Stmt.VarFieldStmt;
import litac.ast.Stmt.WhileStmt;
import litac.checker.TypeInfo.TypeKind;
import litac.util.Tuple;


/**
 * Replaces a type with a different type based on type name. This is used
 * for generics - copying the AST tree of the function or aggregate and replacing
 * the generic type with an actual type.
 * 
 * @author Tony
 *
 */
public class TypeReplacerNodeVisitor implements NodeVisitor {

    private List<Tuple<String, TypeInfo>> replacements;
    
    
    public TypeReplacerNodeVisitor(List<Tuple<String, TypeInfo>> replacements) {
        this.replacements = replacements;
    }
    
    private Stmt replaceType(Stmt stmt) {
        if(stmt instanceof Expr) {
            replaceType((Expr)stmt);
        }
        else if(stmt instanceof Decl) {
            replaceType((Decl)stmt);
        }
        return stmt;
    }
    
    

    private void replaceType(List<Expr> exprs) {
        for(Expr expr : exprs) {
            replaceType(expr);
        }
    }
    
    private Expr replaceType(Expr expr) {
        TypeInfo oldType = expr.getResolvedType();
        if(oldType != null) {
            if(oldType.isKind(TypeKind.Any) && this.replacements.stream().anyMatch(t -> t.getFirst().equals(oldType.getName()))) {                 
                expr.resolveTo(this.replacements.stream().filter(t -> t.getFirst().equals(oldType.getName())).findFirst().get().getSecond());
            }
        }
        
        return expr;
    }
    
    private void replaceType(Decl decl) {
        decl.type = replaceType(decl.type);
    }
    
    private TypeInfo replaceType(TypeInfo oldType) {
        if(oldType.isKind(TypeKind.Any) && this.replacements.stream().anyMatch(t -> t.getFirst().equals(oldType.getName()))) {                 
            return this.replacements.stream().filter(t -> t.getFirst().equals(oldType.getName())).findFirst().get().getSecond();
        }
        
        return oldType;
    }
    

    @Override
    public void visit(ModuleStmt stmt) {
    }

    @Override
    public void visit(ImportStmt stmt) {
    }

    @Override
    public void visit(NoteStmt stmt) {
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
        replaceType(stmt.condExpr).visit(this);
        replaceType(stmt.thenStmt).visit(this);
        if(stmt.elseStmt != null) {
            replaceType(stmt.elseStmt).visit(this);
        }
    }

    @Override
    public void visit(WhileStmt stmt) {
        replaceType(stmt.condExpr).visit(this);
        replaceType(stmt.bodyStmt).visit(this);
    }

    @Override
    public void visit(DoWhileStmt stmt) {
        replaceType(stmt.bodyStmt).visit(this);
        replaceType(stmt.condExpr).visit(this);
    }

    @Override
    public void visit(ForStmt stmt) {
        replaceType(stmt.initStmt).visit(this);
        replaceType(stmt.condExpr).visit(this);
        replaceType(stmt.bodyStmt).visit(this);
        replaceType(stmt.postStmt).visit(this);
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
            replaceType(stmt.returnExpr).visit(this);            
        }        
    }

    @Override
    public void visit(BlockStmt stmt) {
        for(Stmt s : stmt.stmts) {
            replaceType(s).visit(this);
        }
    }

    @Override
    public void visit(DeferStmt stmt) {
        replaceType(stmt.stmt).visit(this);
    }

    @Override
    public void visit(EmptyStmt stmt) {
    }

    @Override
    public void visit(ParametersStmt stmt) {
        for(ParameterDecl d : stmt.params) {
            replaceType(d);
        }
    }

    @Override
    public void visit(ConstDecl d) {
        replaceType(d.expr);
    }

    @Override
    public void visit(EnumDecl d) {
    }

    @Override
    public void visit(FuncDecl d) {
        d.params.visit(this);
        d.bodyStmt.visit(this);
        
        replaceType(d.bodyStmt);
        d.returnType = replaceType(d.returnType);
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
    public void visit(ParameterDecl d) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CastExpr expr) {
        replaceType(expr.expr);
        replaceType(expr);
    }

    @Override
    public void visit(SizeOfExpr expr) {
        replaceType(expr.expr);
    }

    @Override
    public void visit(InitArgExpr expr) {
        replaceType(expr);
        replaceType(expr.value);
    }

    @Override
    public void visit(InitExpr expr) {
        replaceType(expr);        
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
        replaceType(expr.expr);
        replaceType(expr);
    }

    @Override
    public void visit(FuncCallExpr expr) {
        replaceType(expr);
        replaceType(expr.object);
        replaceType(expr.arguments);
    }

    @Override
    public void visit(IdentifierExpr expr) {
        replaceType(expr);        
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        replaceType(expr);
    }

    @Override
    public void visit(GetExpr expr) {
        replaceType(expr);
        replaceType(expr.object);        
    }

    @Override
    public void visit(SetExpr expr) {
        replaceType(expr);
        replaceType(expr.object);
        replaceType(expr.value);
    }

    @Override
    public void visit(UnaryExpr expr) {
        replaceType(expr);
        replaceType(expr.expr);
    }

    @Override
    public void visit(BinaryExpr expr) {
        replaceType(expr);
        replaceType(expr.left);
        replaceType(expr.right);
    }

    @Override
    public void visit(ArrayInitExpr expr) {
        replaceType(expr);
        replaceType(expr.values);
    }

    @Override
    public void visit(SubscriptGetExpr expr) {
        replaceType(expr);
        replaceType(expr.index);
        replaceType(expr.object);

    }

    @Override
    public void visit(SubscriptSetExpr expr) {
        replaceType(expr);
        replaceType(expr.index);
        replaceType(expr.object);
        replaceType(expr.value);
    }

}
