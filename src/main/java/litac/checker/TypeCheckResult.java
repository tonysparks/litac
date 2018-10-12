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
public class TypeCheckResult {

    public static class TypeCheckError {
        public String message;
        public Stmt stmt;
        
        public TypeCheckError(String message, Stmt stmt) {
            this.message = message;
            this.stmt = stmt;
        }
        
        
    }
    
    private List<TypeCheckError> errors;
    
    public TypeCheckResult() {
        this.errors = new ArrayList<>();
    }
    
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
    
    public void addError(TypeCheckError error) {
        this.errors.add(error);
    }
    
    public void addErrors(List<TypeCheckError> errors) {
        this.errors.addAll(errors);
    }
    
    public void addError(Stmt stmt, String message) {
        addError(new TypeCheckError(message, stmt));
    }
    
    public List<TypeCheckError> getErrors() {
        return errors;
    }
}
