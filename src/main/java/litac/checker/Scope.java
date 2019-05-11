/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;

import litac.ast.Decl;
import litac.ast.Stmt;

/**
 * @author Tony
 *
 */
public class Scope {
    
    public static class Variable {
        public Decl decl;
        public TypeInfo type;
        
        Variable(Decl decl, TypeInfo type) {
            this.decl = decl;
            this.type = type;
        }
    }
    
    private Scope parent;
    private PhaseResult result;
    
    private Map<String, Variable> variables;    
    
    
    public Scope(PhaseResult result) {
        this(result, null);
    }
    
    public Scope(PhaseResult result, Scope parent) {
        this.result = result;
        this.parent = parent;
        
        this.variables = new HashMap<>();
    }
    
    public Scope getParent() {
        return this.parent;
    }
    
    public void addVariable(Decl decl, String variableName, TypeInfo type) {
        if(this.variables.containsKey(variableName)) {
            this.result.addError(decl, "variable '%s' already defined", variableName);
        }
                
        this.variables.put(variableName, new Variable(decl, type));
    }
        

    
    public Variable getVariable(String varName) {
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
        Variable definedType = getVariable(varName);
        if(definedType == null) {
            this.result.addError(stmt, "'%s' has not been declared", varName);
            return;
        }
        
        if(!definedType.type.canCastTo(type)) {
            this.result.addError(stmt, "'%s' of type '%s' can't be assigned to type '%s'", varName, definedType, type);
            return;
        }
    }
        
    public Scope pushScope() {
        return new Scope(this.result, this);
    }
      
}
