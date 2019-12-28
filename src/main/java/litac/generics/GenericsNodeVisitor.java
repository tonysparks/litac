package litac.generics;

import java.util.List;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;


/**
 * Replaces a type with a different type based on type name. This is used
 * for generics - copying the AST tree of the function or aggregate and replacing
 * the generic type with an actual type.
 * 
 * @author Tony
 *
 */
public class GenericsNodeVisitor implements NodeVisitor {

    private List<GenericParam> genericParams; 
    private List<TypeSpec> genericArgs;
    
    public GenericsNodeVisitor(List<GenericParam> genericParams, 
                               List<TypeSpec> genericArgs) {
        this.genericParams = genericParams;
        this.genericArgs = genericArgs;
    }
    
    private TypeSpec getReplacedTypeSpec(NameTypeSpec nameSpec) {
        for(int i = 0; i < this.genericParams.size(); i++) {
            GenericParam p = this.genericParams.get(i);
            if(p.name.equals(nameSpec.name)) {            
                if(i < this.genericArgs.size()) {
                    return this.genericArgs.get(i);
                }
            }
        }
        
        return nameSpec;
    }
    
    private List<TypeSpec> replaceType(List<TypeSpec> types) {
        if(types == null) {
            return null;
        }
        
        for(int i = 0; i < types.size(); i++) {
            types.set(i, replaceType(types.get(i)));
        }
        return types;
    }
    
    private NameTypeSpec replaceType(NameTypeSpec nameSpec) {
        if(nameSpec == null) {
            return null;
        }
        
        TypeSpec type = replaceType((TypeSpec)nameSpec);
        return type.as();
    }
    
    private TypeSpec replaceType(TypeSpec type) {
        if(type == null) {
            return null;
        }
        
        switch(type.kind) {
            case ARRAY: {
                ArrayTypeSpec arraySpec = type.as();
                arraySpec.base = replaceType(arraySpec.base);
                return arraySpec;
            }
            case CONST: {
                ConstTypeSpec constSpec = type.as();
                constSpec.base = replaceType(constSpec.base);
                return constSpec;
            }
            case PTR: {
                PtrTypeSpec ptrSpec = type.as();
                ptrSpec.base = replaceType(ptrSpec.base);
                return ptrSpec;
            }
            case FUNC_PTR: {
                FuncPtrTypeSpec funcSpec = type.as();
                
                List<TypeSpec> args = funcSpec.args;
                for(int i = 0; i < args.size(); i++) {
                    args.set(i, replaceType(args.get(i)));
                }
                
                funcSpec.ret = replaceType(funcSpec.ret);
                
                for(int i = 0; i < funcSpec.genericParams.size();) {
                    boolean isRemoved = false;
                    for(GenericParam subParam : this.genericParams) {
                        if(funcSpec.genericParams.get(i).name.equals(subParam.name)) {
                            funcSpec.genericParams.remove(i);
                            isRemoved = true;
                            break;
                        }
                    }
                    
                    if(!isRemoved) {
                        i++;           
                    }
                }
                
                return funcSpec;
            }
            case NAME: {
                NameTypeSpec nameSpec = type.as();
                List<TypeSpec> genArgs = nameSpec.genericArgs;
                for(int i = 0; i < genArgs.size(); i++) {
                    genArgs.set(i, replaceType(genArgs.get(i)));
                }
                // TODO: Embedded generic types...
                type = getReplacedTypeSpec(nameSpec);    
//                if(nameSpec != type && nameSpec.genericArgs.size() > 0) {
//                    System.out.println("");
//                }
                
                return type;
            }
            default:
                return null;
        
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
        stmt.decl.visit(this);
    }

    @Override
    public void visit(UnionFieldStmt stmt) {
        stmt.decl.visit(this);
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
        stmt.bodyStmt.visit(this);
        stmt.condExpr.visit(this);
    }

    @Override
    public void visit(ForStmt stmt) {
        if(stmt.initStmt != null) {
            stmt.initStmt.visit(this);
        }
        
        if(stmt.condExpr != null) {
            stmt.condExpr.visit(this);
        }
        
        stmt.bodyStmt.visit(this);
        
        if(stmt.postStmt != null) {
            stmt.postStmt.visit(this);
        }
    }
    
    @Override
    public void visit(SwitchCaseStmt stmt) {
        stmt.cond.visit(this);
        stmt.stmt.visit(this);
    }
    
    @Override
    public void visit(SwitchStmt stmt) {
        stmt.cond.visit(this);
        
        for(SwitchCaseStmt s : stmt.stmts) {
            s.visit(this);
        }
        
        if(stmt.defaultStmt != null) {
            stmt.defaultStmt.visit(this);
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
            //replaceType(stmt.returnExpr).visit(this);
            stmt.returnExpr.visit(this);
        }        
    }

    @Override
    public void visit(BlockStmt stmt) {
        for(Stmt s : stmt.stmts) {
//            replaceType(s).visit(this);
            s.visit(this);
        }
    }

    @Override
    public void visit(DeferStmt stmt) {
        stmt.stmt.visit(this);
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
    public void visit(CompStmt stmt) {
        if(stmt.body != null) {
            for(Stmt s : stmt.body) {
                s.visit(this);
            }
        }
        
        if(stmt.end != null) {
            stmt.end.visit(this);
        }
    }

    @Override
    public void visit(EnumDecl d) {
    }

    @Override
    public void visit(FuncDecl d) {
        d.params.visit(this);
        d.bodyStmt.visit(this);
        
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
        d.type = replaceType(d.type);
    }

    @Override
    public void visit(UnionDecl d) {
        for(FieldStmt f : d.fields) {
            f.visit(this);
        }
    }
    
    @Override
    public void visit(ConstDecl d) {
        d.type = replaceType(d.type);
        if(d.expr != null) {
            d.expr.visit(this);
        }
    }

    @Override
    public void visit(VarDecl d) {
        d.type = replaceType(d.type);
        if(d.expr != null) {
            d.expr.visit(this);
        }
    }

    @Override
    public void visit(ParameterDecl d) {        
        d.type = replaceType(d.type);
        if(d.defaultValue != null) {
            d.defaultValue.visit(this);
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
    public void visit(CastExpr expr) {
        expr.expr.visit(this);
        expr.castTo = replaceType(expr.castTo);
    }

    @Override
    public void visit(SizeOfExpr expr) {
        /*if(expr.expr != null) {
            expr.expr.visit(this);
        }
        expr.type = replaceType(expr.type);*/
        
        expr.expr.visit(this);
    }
    
    @Override
    public void visit(TypeOfExpr expr) {
        if(expr.expr != null) {
            expr.expr.visit(this);
        }
        expr.type = replaceType(expr.type);
    }

    @Override
    public void visit(InitArgExpr expr) {        
        expr.value.visit(this);
    }

    @Override
    public void visit(InitExpr expr) {
        expr.type = replaceType(expr.type);
        expr.genericArgs = replaceType(expr.genericArgs);
        for(Expr arg : expr.arguments) {
            arg.visit(this);
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
        expr.expr.visit(this);        
    }

    @Override
    public void visit(FuncCallExpr expr) {
        expr.genericArgs = replaceType(expr.genericArgs);
        expr.object.visit(this);
        for(Expr arg : expr.arguments) {
            arg.visit(this);
        }
    }

    @Override
    public void visit(IdentifierExpr expr) {
        expr.type = replaceType(expr.type);
        expr.genericArgs = replaceType(expr.genericArgs);        
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        visit((IdentifierExpr)expr);
    }
    
    @Override
    public void visit(TypeIdentifierExpr expr) {
        expr.type = replaceType(expr.type);
        expr.genericArgs = replaceType(expr.genericArgs);
    }

    @Override
    public void visit(GetExpr expr) {
        expr.object.visit(this);
        expr.field.visit(this);        
    }

    @Override
    public void visit(SetExpr expr) {
        expr.object.visit(this);
        expr.field.visit(this);
        expr.value.visit(this);        
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
        expr.type = replaceType(expr.type);
        if(expr.values != null) {
            for(Expr e : expr.values) {
                e.visit(this);
            }
        }
    }
    
    @Override
    public void visit(ArrayDesignationExpr expr) {
        expr.index.visit(this);
        expr.value.visit(this);
    }

    @Override
    public void visit(SubscriptGetExpr expr) {
        expr.index.visit(this);
        expr.object.visit(this);
    }

    @Override
    public void visit(SubscriptSetExpr expr) {
        expr.index.visit(this);
        expr.object.visit(this);
        expr.value.visit(this);        
    }

}
