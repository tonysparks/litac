/*
 * see license.txt
 */
package litac.generics;

/**
 * @author Tony
 *
 */
public class GenericParam {

    public String name;
    
    public GenericParam(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
