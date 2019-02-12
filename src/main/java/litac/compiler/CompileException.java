/*
 * see license.txt
 */
package litac.compiler;

/**
 * @author Tony
 *
 */
public class CompileException extends RuntimeException {

    /**
     * SUID
     */
    private static final long serialVersionUID = 3037682680869765854L;

    public CompileException(String message) {
        super(message);
    }
    
    public CompileException(String message, Throwable t) {
        super(message, t);
    }
    
    public CompileException(Throwable t) {
        super(t);
    }
}
