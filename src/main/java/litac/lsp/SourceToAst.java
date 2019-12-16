/*
 * see license.txt
 */
package litac.lsp;

import java.io.File;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node.SrcPos;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo;
import litac.checker.TypeResolver.Operand;
import litac.compiler.*;
import litac.lsp.JsonRpc.*;

/**
 * @author antho
 *
 */
public class SourceToAst implements NodeVisitor {

    private Position pos;
    private Program program;
    private Location location;
    
    public SourceToAst(Program program, Position pos) {
        this.program = program;
        this.pos = pos;
    }
    
    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    private boolean isNodeAtPos(Node node) {
        if(node == null) {
            return false;
        }
        
        SrcPos srcPos = node.getSrcPos();
        return isNodeAtPos(srcPos);
    }
    
    private boolean isNodeAtPos(TypeSpec typeSpec) {
        if(typeSpec == null) {
            return false;
        }
        
        SrcPos srcPos = typeSpec.pos;
        return isNodeAtPos(srcPos);
    }
    
    private boolean isNodeAtPos(SrcPos srcPos) {  
        if(srcPos.token == null) {
            return false;
        }
        
        int fromIndex = srcPos.position;
        int toIndex = fromIndex + srcPos.token.getText().length();
        if((pos.line+1) == srcPos.lineNumber) {
            if(pos.character >= fromIndex && pos.character <= toIndex) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean findFromTypeSpec(TypeSpec typeSpec) {
        TypeInfo type = program.getResolvedTypeMap().get(typeSpec);
        return findFromTypeInfo(type);
    }
    
    private boolean findFromExpr(Expr expr) {
        Operand op = expr.getResolvedType();
        if(op == null) {
            return false;
        }
        
        TypeInfo type = op.type;
        return findFromTypeInfo(type);
    }
    
    private boolean findFromTypeInfo(TypeInfo type) {        
        if(type == null) {
            return false;
        }
        type = TypeInfo.getBase(type);
        
        SrcPos srcPos = type.sym.decl.getSrcPos();
        if(srcPos.sourceFile == null) {
            return false;
        }
        
        this.location = new Location();
        this.location.uri = new File(srcPos.sourceFile).toURI().toString();
        this.location.range = LspUtil.fromSrcPosToken(srcPos);        
        return true;
    }
    
    @Override
    public void visit(ModuleStmt stmt) {
        for(Decl d: stmt.declarations) {
            d.visit(this);
        }
    }

    @Override
    public void visit(ImportStmt stmt) {
    }

    @Override
    public void visit(NoteStmt stmt) {
    }

    @Override
    public void visit(VarFieldStmt stmt) {
        if(isNodeAtPos(stmt)) {
            findFromTypeSpec(stmt.type);
        }        
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
    public void visit(IfStmt stmt) {
        stmt.condExpr.visit(this);
        stmt.thenStmt.visit(this);
        if(stmt.elseStmt != null) {
            stmt.elseStmt.visit(this);
        }
    }

    @Override
    public void visit(WhileStmt stmt) {
        stmt.condExpr.visit(this);
        stmt.bodyStmt.visit(this);
    }

    @Override
    public void visit(DoWhileStmt stmt) {
        stmt.condExpr.visit(this);
        stmt.bodyStmt.visit(this);
    }

    @Override
    public void visit(ForStmt stmt) {
        if(stmt.initStmt != null) {
            stmt.initStmt.visit(this);
        }
        if(stmt.condExpr != null) {
            stmt.condExpr.visit(this);
        }
        if(stmt.postStmt != null) {
            stmt.postStmt.visit(this);
        }
        
        stmt.bodyStmt.visit(this);
    }

    @Override
    public void visit(SwitchCaseStmt stmt) {
        stmt.cond.visit(this);
        stmt.stmt.visit(this);
    }

    @Override
    public void visit(SwitchStmt stmt) {
        stmt.cond.visit(this);
        if(stmt.defaultStmt != null) {
            stmt.defaultStmt.visit(this);
        }
        
        for(Stmt s : stmt.stmts) {
            s.visit(this);
        }
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
        for(Stmt s: stmt.stmts) {
            s.visit(this);
        }
    }

    @Override
    public void visit(DeferStmt stmt) {
        stmt.stmt.visit(this);
    }

    @Override
    public void visit(EmptyStmt stmt) {
    }

    @Override
    public void visit(ParametersStmt stmt) {
        for(Stmt s: stmt.params) {
            s.visit(this);
        }
    }

    @Override
    public void visit(VarDeclsStmt stmt) {
        for(Decl d : stmt.vars) {
            d.visit(this);
        }
    }

    @Override
    public void visit(ConstDeclsStmt stmt) {
        for(Decl d : stmt.consts) {
            d.visit(this);
        }
    }

    @Override
    public void visit(GotoStmt stmt) {
    }

    @Override
    public void visit(LabelStmt stmt) {
    }

    @Override
    public void visit(ConstDecl d) {
        if(d.expr != null) {
            d.expr.visit(this);
        }
        
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }
    }

    @Override
    public void visit(EnumDecl d) {
    }

    @Override
    public void visit(FuncDecl d) {
        if(d.bodyStmt != null) {
            d.bodyStmt.visit(this);
        }
        d.params.visit(this);
        
        if(isNodeAtPos(d.returnType)) {
            findFromTypeSpec(d.returnType);
        }
    }

    @Override
    public void visit(StructDecl d) {
        for(Stmt s: d.fields) {
            s.visit(this);
        }
    }

    @Override
    public void visit(TypedefDecl d) {
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }
    }

    @Override
    public void visit(UnionDecl d) {
        for(Stmt s: d.fields) {
            s.visit(this);
        }
    }

    @Override
    public void visit(VarDecl d) {
        if(d.expr != null) {
            d.expr.visit(this);
        }
        
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }        
    }

    @Override
    public void visit(ParameterDecl d) {
        if(d.defaultValue != null) {
            d.defaultValue.visit(this);
        }
        
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }
    }

    @Override
    public void visit(CastExpr expr) {
        expr.expr.visit(this);
        
        if(isNodeAtPos(expr.castTo)) {
            findFromTypeSpec(expr.castTo);
        }
    }

    @Override
    public void visit(SizeOfExpr expr) {
        expr.expr.visit(this);
    }

    @Override
    public void visit(TypeOfExpr expr) {
        if(expr.expr != null) {
            expr.expr.visit(this);
        }
        
        if(isNodeAtPos(expr.type)) {
            findFromTypeSpec(expr.type);
        }
    }

    @Override
    public void visit(InitArgExpr expr) {
        if(expr.value != null) {
            expr.value.visit(this);
        }
        
        if(isNodeAtPos(expr) || isNodeAtPos(expr.value)) {
            // TODO: Navigate to parent type?
            //findFromTypeSpec(expr.getParentNode().)
        }
    }

    @Override
    public void visit(InitExpr expr) {
        
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
        expr.expr.visit(this);
    }

    @Override
    public void visit(FuncCallExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        
        expr.object.visit(this);
        for(Expr e : expr.arguments) {
            e.visit(this);
        }
    }

    @Override
    public void visit(IdentifierExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromTypeSpec(expr.type);
        }        
    }

    @Override
    public void visit(TypeIdentifierExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }        
    }

    @Override
    public void visit(GetExpr expr) {
        // TODO
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
    }

    @Override
    public void visit(SetExpr expr) {
        // TODO Auto-generated method stub
        
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
    public void visit(TernaryExpr expr) {
        expr.cond.visit(this);
        expr.then.visit(this);
        expr.other.visit(this);
    }

    @Override
    public void visit(ArrayInitExpr expr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ArrayDesignationExpr expr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(SubscriptGetExpr expr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(SubscriptSetExpr expr) {
        // TODO Auto-generated method stub
        
    }

}
