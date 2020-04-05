/*
 * see license.txt
 */
package litac.generics;

import litac.ast.*;
import litac.checker.TypeInfo;

/**
 * @author Tony
 *
 */
public class ResolvedGenericArg {

    public Expr expr;
    public TypeInfo type;
    
    public ResolvedGenericArg(Expr expr, TypeInfo type) {
        this.expr = expr;
        this.type = type;
    }    
}
