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

    public boolean execute(String stmt);
}
