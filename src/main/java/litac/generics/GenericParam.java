/*
 * see license.txt
 */
package litac.generics;

import litac.ast.TypeSpec;

/**
 * @author Tony
 *
 */
public class GenericParam {

    public String name;
    public TypeSpec type;
    
    public GenericParam(String name, TypeSpec type) {
        this.name = name;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
