/*
 * see license.txt
 */
package litac.doc;

import litac.compiler.Module;

/**
 * @author Tony
 *
 */
public interface DocWriter {

    public void start();
    public void writeModule(Module module);
    public void end();

}
