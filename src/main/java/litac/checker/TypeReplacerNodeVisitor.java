package litac.checker;

import java.util.List;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo.*;


/**
 * Replaces a type with a different type based on type name. This is used
 * for generics - copying the AST tree of the function or aggregate and replacing
 * the generic type with an actual type.
 * 
 * @author Tony
 *
 */
public class TypeReplacerNodeVisitor implements NodeVisitor {

    private List<FieldInfo> fieldInfos;
    
    private Module module;
    private List<GenericParam> genericParams; 
    private List<TypeInfo> genericArgs;
    
    public TypeReplacerNodeVisitor(List<FieldInfo> fieldInfos, 
                                   Module module,                       
                                   List<GenericParam> genericParams, 
                                   List<TypeInfo> genericArgs) {
        this.fieldInfos = fieldInfos;
        this.module = module;
        this.genericParams = genericParams;
        this.genericArgs = genericArgs;
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
            replaceType(expr).visit(this);
        }
    }
    
    private Expr replaceType(Expr expr) {
        TypeInfo oldType = expr.getResolvedType();
        if(oldType != null) {            
            expr.resolveTo(replaceType(oldType));
        }
        
        return expr;
    }
    
    private void replaceType(Decl decl) {
        decl.type = replaceType(decl.type);
    }
    
    private TypeInfo replaceType(TypeInfo oldType) {
        return Generics.createGenericTypeInfo(module, oldType, genericParams, genericArgs);
    }
    
    private void replaceGenericArgs(List<TypeInfo> genericArgs) {
        for(int i = 0; i < genericArgs.size(); i++) {
            genericArgs.set(i, replaceType(genericArgs.get(i)));
        }
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
        stmt.type = replaceType(stmt.type);
    }

    @Override
    public void visit(StructFieldStmt stmt) {
        for(FieldInfo f : this.fieldInfos) {
            if(f.name.equals(stmt.decl.name)) {
                stmt.decl = (StructDecl)f.type.sym.decl;
                stmt.decl.name = f.name;
                return;
            }
        }
        
        stmt.decl.type = replaceType(stmt.decl.type);
        stmt.decl.visit(this);
    }

    @Override
    public void visit(UnionFieldStmt stmt) {
        for(FieldInfo f : this.fieldInfos) {
            if(f.name.equals(stmt.decl.name)) {
                stmt.decl = (UnionDecl)f.type.sym.decl;
                stmt.decl.name = f.name;
                return;
            }
        }
        
        stmt.decl.type = replaceType(stmt.decl.type);
        stmt.decl.visit(this);
    }
    
    @Override
    public void visit(EnumFieldStmt stmt) {
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
        if(stmt.initStmt != null) {
            replaceType(stmt.initStmt).visit(this);
        }
        
        if(stmt.condExpr != null) {
            replaceType(stmt.condExpr).visit(this);
        }
        
        replaceType(stmt.bodyStmt).visit(this);
        
        if(stmt.postStmt != null) {
            replaceType(stmt.postStmt).visit(this);
        }
    }
    
    @Override
    public void visit(SwitchCaseStmt stmt) {
        replaceType(stmt.cond).visit(this);
        replaceType(stmt.stmt).visit(this);
    }
    
    @Override
    public void visit(SwitchStmt stmt) {
        replaceType(stmt.cond).visit(this);
        
        for(SwitchCaseStmt s : stmt.stmts) {
            replaceType(s).visit(this);
        }
        
        if(stmt.defaultStmt != null) {
            replaceType(stmt.defaultStmt).visit(this);
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
    public void visit(GotoStmt stmt) {
    }
    
    @Override
    public void visit(LabelStmt stmt) {
    }

    @Override
    public void visit(EmptyStmt stmt) {
    }

    @Override
    public void visit(ParametersStmt stmt) {
        for(ParameterDecl d : stmt.params) {            
            d.visit(this);
        }
    }

    @Override
    public void visit(ConstDecl d) {
        replaceType(d);
        if(d.expr != null) {
            replaceType(d.expr).visit(this);
        }
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
        for(FieldStmt s : d.fields) {
            s.visit(this);
        }
    }

    @Override
    public void visit(TypedefDecl d) {
        // TODO Auto-generated method stub
        System.out.println("here!" + d.alias);
    }

    @Override
    public void visit(UnionDecl d) {
        for(FieldStmt f : d.fields) {
            f.visit(this);
        }
    }

    @Override
    public void visit(VarDecl d) {
        replaceType(d);
        if(d.expr != null) {
            replaceType(d.expr).visit(this);
        }
    }

    @Override
    public void visit(ParameterDecl d) {
        replaceType(d);
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
    public void visit(CastExpr expr) {
        expr.castTo = replaceType(expr.castTo);
        replaceType(expr.expr).visit(this);
        replaceType(expr);
    }

    @Override
    public void visit(SizeOfExpr expr) {
        replaceType(expr.expr).visit(this);
    }
    
    @Override
    public void visit(TypeOfExpr expr) {
        replaceType(expr.expr).visit(this);
    }

    @Override
    public void visit(InitArgExpr expr) {
        replaceType(expr);
        replaceType(expr.value).visit(this);
    }

    @Override
    public void visit(InitExpr expr) {
        replaceGenericArgs(expr.genericArgs);
        replaceType(expr);        
        for(Expr arg : expr.arguments) {
            replaceType(arg).visit(this);
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
    public void visit(CharExpr expr) {
    }

    @Override
    public void visit(GroupExpr expr) {
        replaceType(expr.expr).visit(this);
        replaceType(expr);
    }

    @Override
    public void visit(FuncCallExpr expr) {
        replaceGenericArgs(expr.genericArgs);
        replaceType(expr);
        replaceType(expr.object).visit(this);
        replaceType(expr.arguments);
    }

    @Override
    public void visit(IdentifierExpr expr) {
        replaceType(expr);
        
        if(expr.sym != null && expr.sym.isType()) {
            TypeInfo newType = replaceType(expr.type).getResolvedType();
            expr.type = newType;
            expr.resolveTo(newType);
            expr.sym = newType.sym;
        }
        else {
            IdentifierTypeInfo idInfo = (IdentifierTypeInfo)expr.type;
            if(idInfo.resolvedType != null) {
                idInfo.resolvedType = replaceType(idInfo.resolvedType);
                expr.resolveTo(idInfo);
            }
            else {
                // Don't get the resolved type, because that will bypass the Identifier 
                // information
                TypeInfo newType = replaceType(expr.type);//.getResolvedType();
                expr.type = newType;
                expr.resolveTo(newType);
                expr.sym = newType.sym;
            }
        }
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        visit((IdentifierExpr)expr);
    }
    
    @Override
    public void visit(TypeIdentifierExpr expr) {
        visit((IdentifierExpr)expr);
    }

    @Override
    public void visit(GetExpr expr) {
        replaceType(expr);
        replaceType(expr.object).visit(this);
        replaceType(expr.field).visit(this);
    }

    @Override
    public void visit(SetExpr expr) {
        replaceType(expr);
        replaceType(expr.object).visit(this);
        replaceType(expr.field).visit(this);
        replaceType(expr.value).visit(this);
        //expr.field = replaceType(expr.field);
    }

    @Override
    public void visit(UnaryExpr expr) {
        replaceType(expr);
        replaceType(expr.expr).visit(this);
    }

    @Override
    public void visit(BinaryExpr expr) {
        replaceType(expr);
        replaceType(expr.left).visit(this);
        replaceType(expr.right).visit(this);
    }
    
    @Override
    public void visit(TernaryExpr expr) {
        replaceType(expr);
        replaceType(expr.cond).visit(this);
        replaceType(expr.then).visit(this);
        replaceType(expr.other).visit(this);
    }

    @Override
    public void visit(ArrayInitExpr expr) {
        replaceType(expr);
        replaceType(expr.values);
    }
    
    @Override
    public void visit(ArrayDesignationExpr expr) {
        replaceType(expr);
        replaceType(expr.index).visit(this);
        replaceType(expr.value).visit(this);
    }

    @Override
    public void visit(SubscriptGetExpr expr) {
        replaceType(expr);
        replaceType(expr.index).visit(this);
        replaceType(expr.object).visit(this);

    }

    @Override
    public void visit(SubscriptSetExpr expr) {
        replaceType(expr);
        replaceType(expr.index).visit(this);
        replaceType(expr.object).visit(this);
        replaceType(expr.value).visit(this);
    }

}
