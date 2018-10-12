/*
 * see license.txt
 */
package litac.checker;

import litac.ast.AbstractNodeVisitor;
import litac.ast.Expr;
import litac.ast.Expr.BinaryExpr;
import litac.ast.Expr.BooleanExpr;
import litac.ast.Expr.DotExpr;
import litac.ast.Expr.FuncCallExpr;
import litac.ast.Expr.GetExpr;
import litac.ast.Expr.GroupExpr;
import litac.ast.Expr.IdentifierExpr;
import litac.ast.Expr.InitExpr;
import litac.ast.Expr.NullExpr;
import litac.ast.Expr.UnaryExpr;
import litac.ast.TypeInfo.FuncTypeInfo;
import litac.ast.TypeInfo.PtrTypeInfo;
import litac.ast.TypeInfo.TypeKind;
import litac.ast.TypeInfo;

/**
 * @author Tony
 *
 */
public class TypeInferencer {
   
    public static TypeInfo inferType(Expr expr) {
        if(expr.isResolved()) {
            return expr.getResolvedType();
        }
        
        TypeInferenceVisitor visitor = new TypeInferenceVisitor();
        expr.visit(visitor);
        
        return expr.getResolvedType();
    }
    
    private static class TypeInferenceVisitor extends AbstractNodeVisitor {
    
        @Override
        public void visit(BinaryExpr expr) {
            expr.left.visit(this);
            expr.right.visit(this);
            
            if(expr.left.isResolved() && expr.right.isResolved()) {
                TypeInfo leftType = expr.left.getResolvedType();
                TypeInfo rightType = expr.right.getResolvedType();
                
                if(leftType.strictEquals(rightType)) {
                    expr.resolveTo(leftType);
                }
                else if(leftType.isGreater(rightType)) {
                    expr.resolveTo(leftType);
                }
                else {
                    expr.resolveTo(rightType);
                }
            }
        }
                
        @Override
        public void visit(DotExpr expr) {
            expr.field.visit(this);
            expr.resolveTo(expr.field.getResolvedType());
        }
        
        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);      
            if(expr.object.isResolved()) {
                TypeInfo type = expr.object.getResolvedType();
                if(type.isKind(TypeKind.Func)) {
                    FuncTypeInfo funcInfo = type.as();
                    expr.resolveTo(funcInfo.returnType);
                }
            }
        }
        
        
        @Override
        public void visit(GroupExpr expr) {
            expr.expr.visit(this);
            expr.resolveTo(expr.expr.getResolvedType());
        }
                
        @Override
        public void visit(UnaryExpr expr) {
            expr.expr.visit(this);
            
            if(expr.expr.isResolved()) {
                switch(expr.operator) {
                    case BAND: {
                        PtrTypeInfo ptrInfo = new PtrTypeInfo("ptr", expr.expr.getResolvedType());
                        expr.resolveTo(ptrInfo);
                        break;
                    }
                    case STAR: {
                        TypeInfo ptrType = expr.expr.getResolvedType();
                        if(ptrType.isKind(TypeKind.Ptr)) {
                            PtrTypeInfo ptrInfo = ptrType.as();
                            expr.resolveTo(ptrInfo.ptrOf);
                        }
                        break;
                    }
                    default: {
                        expr.resolveTo(expr.expr.getResolvedType());
                    }
                }
            }
        }
    
    }    
    
}
