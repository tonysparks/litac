/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tony
 *
 */
public class Module {

    public String name;        
    public Map<String, Module> imports;
       
    public Scope currentScope;
    
    public Module(TypeCheckResult result, String name) {
        this.name = name;
        
        this.imports = new HashMap<>();                
        this.currentScope = new Scope(result);
    }
    
    
    public Scope pushScope() {
        this.currentScope = this.currentScope.pushScope();
        return this.currentScope;
    }
    
    public Scope popScope() {
        this.currentScope = this.currentScope.getParent();
        return this.currentScope;
    }
}
