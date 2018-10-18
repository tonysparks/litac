/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;

import litac.ast.Stmt;
import litac.ast.TypeInfo;

/**
 * @author Tony
 *
 */
public class Scope {
    
    private Scope parent;
    private TypeCheckResult result;
    
    private Map<String, TypeInfo> variables;    
    
    
    public Scope(TypeCheckResult result) {
        this(result, null);
    }
    
    public Scope(TypeCheckResult result, Scope parent) {
        this.result = result;
        this.parent = parent;
        
        this.variables = new HashMap<>();
    }
    
    public Scope getParent() {
        return this.parent;
    }
    
    public void addVariable(Stmt stmt, String variableName, TypeInfo type) {
        if(this.variables.containsKey(variableName)) {
            this.result.addError(stmt, "variable '%s' already defined", variableName);
        }
                
        this.variables.put(variableName, type);
    }
        

    
    public TypeInfo getVariable(String varName) {
        if(this.variables.containsKey(varName)) {
            return this.variables.get(varName);
        }
        
        if(this.parent != null) {
            return this.parent.getVariable(varName);
        }
        
        return null;
    }
    
    
    /**
     * Updates an already defined variable, ensures the type match.
     * 
     * @param stmt
     * @param varName
     * @param type
     */
    public void updateVariable(Stmt stmt, String varName, TypeInfo type) {
        TypeInfo definedType = getVariable(varName);
        if(definedType == null) {
            this.result.addError(stmt, "'%s' has not been declared", varName);
            return;
        }
        
        if(!definedType.canCastTo(type)) {
            this.result.addError(stmt, "'%s' of type '%s' can't be assigned to type '%s'", varName, definedType, type);
            return;
        }
    }
        
    public Scope pushScope() {
        return new Scope(this.result, this);
    }
      
}
