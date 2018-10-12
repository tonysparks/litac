/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.List;

import litac.ast.Stmt;

/**
 * @author Tony
 *
 */
public class TypeResolverResult {

    public static class TypeResolverError {
        public String message;
        public Stmt stmt;
        
        public TypeResolverError(String message, Stmt stmt) {
            this.message = message;
            this.stmt = stmt;
        }
        
        
    }
    
    private List<TypeResolverError> errors;
    
    public TypeResolverResult() {
        this.errors = new ArrayList<>();
    }
    
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
    
    public void addError(TypeResolverError error) {
        this.errors.add(error);
    }
    
    public void addError(Stmt stmt, String message) {
        addError(new TypeResolverError(message, stmt));
    }
    
    public List<TypeResolverError> getErrors() {
        return errors;
    }
}
