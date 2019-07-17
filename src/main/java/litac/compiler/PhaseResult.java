/*
 * see license.txt
 */
package litac.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import litac.ast.Stmt;

/**
 * Compilation phase result.
 * 
 * @author Tony
 *
 */
public class PhaseResult {

    /**
     * Type of error
     * 
     * @author Tony
     *
     */
    public static enum ErrorType {
        WARN,
        ERROR,
    };
    
    public static class PhaseError {
        public final ErrorType type;
        public final String message;
        public final Stmt stmt;
        
        public PhaseError(ErrorType type, String message, Stmt stmt) {
            this.type = type;
            this.message = message;
            this.stmt = stmt;
        }
        
        
    }
    
    private List<PhaseError> errors;
    
    public PhaseResult() {
        this.errors = new ArrayList<>();
    }
    
    /**
     * Merge the errors/warnings
     * 
     * @param result
     */
    public void merge(PhaseResult result) {
        this.errors.addAll(result.errors);
    }
    
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }
    
    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }
    
    public void addError(PhaseError error) {
        this.errors.add(error);
    }
    
    public void addErrors(List<PhaseError> errors) {
        this.errors.addAll(errors);
    }
    
    public void addError(Stmt stmt, String message, Object ... args) {
        addError(new PhaseError(ErrorType.ERROR, String.format(message, args), stmt));
    }
    
    public void addWarn(Stmt stmt, String message, Object ... args) {
        addError(new PhaseError(ErrorType.WARN, String.format(message, args), stmt));
    }
    
    public List<PhaseError> getErrors() {
        return errors.stream().filter(c -> c.type.equals(ErrorType.ERROR)).collect(Collectors.toList());
    }
    
    public List<PhaseError> getWarnings() {
        return errors.stream().filter(c -> c.type.equals(ErrorType.WARN)).collect(Collectors.toList());
    }
}
