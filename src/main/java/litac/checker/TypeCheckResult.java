/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import litac.ast.Stmt;

/**
 * @author Tony
 *
 */
public class TypeCheckResult {

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
    
    public static class TypeCheckError {
        public final ErrorType type;
        public final String message;
        public final Stmt stmt;
        
        public TypeCheckError(ErrorType type, String message, Stmt stmt) {
            this.type = type;
            this.message = message;
            this.stmt = stmt;
        }
        
        
    }
    
    private List<TypeCheckError> errors;
    private Module module;
    
    public TypeCheckResult() {        
        this.errors = new ArrayList<>();
    }
    
    /**
     * @param module the module to set
     */
    void setModule(Module module) {
        this.module = module;
    }
    
    /**
     * @return the module
     */
    public Module getModule() {
        return module;
    }
    
    /**
     * Merge the errors/warnings
     * 
     * @param result
     */
    public void merge(TypeCheckResult result) {
        this.errors.addAll(result.errors);
    }
    
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }
    
    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }
    
    public void addError(TypeCheckError error) {
        this.errors.add(error);
    }
    
    public void addErrors(List<TypeCheckError> errors) {
        this.errors.addAll(errors);
    }
    
    public void addError(Stmt stmt, String message, Object ... args) {
        addError(new TypeCheckError(ErrorType.ERROR, String.format(message, args), stmt));
    }
    
    public void addWarn(Stmt stmt, String message, Object ... args) {
        addError(new TypeCheckError(ErrorType.WARN, String.format(message, args), stmt));
    }
    
    public List<TypeCheckError> getErrors() {
        return errors.stream().filter(c -> c.type.equals(ErrorType.ERROR)).collect(Collectors.toList());
    }
    
    public List<TypeCheckError> getWarnings() {
        return errors.stream().filter(c -> c.type.equals(ErrorType.WARN)).collect(Collectors.toList());
    }
}
