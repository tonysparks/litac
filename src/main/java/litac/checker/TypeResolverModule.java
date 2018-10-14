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
public class TypeResolverModule {

    public String name;        
    public Map<String, TypeResolverModule> imports;
       
    public TypeResolverScope currentScope;
    
    public TypeResolverModule(TypeResolverResult result, String name) {
        this.name = name;
        
        this.imports = new HashMap<>();                
        this.currentScope = new TypeResolverScope(result);
    }
    
    
    public TypeResolverScope pushScope() {
        this.currentScope = this.currentScope.pushScope();
        return this.currentScope;
    }
    
    public TypeResolverScope popScope() {
        this.currentScope = this.currentScope.getParent();
        return this.currentScope;
    }
}
