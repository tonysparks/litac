/*
 * see license.txt
 */
package litac.generics;

import litac.ast.*;

/**
 * @author Tony
 *
 */
public class GenericArg {

    public Expr expr;
    public TypeSpec type;
    
    public GenericArg(Expr expr) {
        this(expr, null);
    }
    
    public GenericArg(TypeSpec type) {
        this(null, type);
    }
    
    public GenericArg(Expr expr, TypeSpec type) {
        this.expr = expr;
        this.type = type;
    }
    
    public GenericArg copy() {
        return new GenericArg(this.expr != null ? expr.copy() : null, TypeSpec.copy(this.type));
    }
}
