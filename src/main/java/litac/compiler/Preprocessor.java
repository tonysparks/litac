/*
 * see license.txt
 */
package litac.compiler;

/**
 * A preprocessor for compile time execution
 * 
 * @author Tony
 *
 */
public interface Preprocessor {

    public void putContext(String name, Object context);
    public boolean execute(String stmt);
}
