/*
 * see license.txt
 */
package litac.compiler.c;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tony
 *
 */
public class Scope {

    public enum ScopeType {
        MODULE,
        LOCAL
    }
    
    private Scope parent;
    private ScopeType type;
    private String name;
    private Map<String, ScopeType> variables;
    
    /**
     * 
     */
    public Scope(Scope parent, String name, ScopeType type) {
        this.parent = parent;
        this.name = name;
        this.type = type;
        this.variables = new HashMap<>();
    }

    /**
     * @return the type
     */
    public ScopeType getType() {
        return type;
    }
    
    public boolean isModuleScope() {
        return this.type.equals(ScopeType.MODULE);
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    public void add(String identifier) {
        this.variables.put(identifier, getType());
    }
    
    public ScopeType lookup(String identifier) {
        if(this.variables.containsKey(identifier)) {
            return this.variables.get(identifier);
        }
        
        if(this.parent == null) {
            return null;
        }
        
        return this.parent.lookup(identifier);
    }
    
    public Scope pushLocalScope() {
        return new Scope(this, this.name, ScopeType.LOCAL);
    }
    
    public Scope pushScope(String name, ScopeType type) {
        return new Scope(this, name, type);
    }
    
    public Scope popScope() {
        return this.parent;
    }
}
