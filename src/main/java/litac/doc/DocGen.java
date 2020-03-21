/*
 * see license.txt
 */
package litac.doc;

import litac.compiler.Module;
import litac.LitaOptions;
import litac.compiler.*;

/**
 * @author Tony
 *
 */
public class DocGen {

    private LitaOptions options;
        
    public DocGen(LitaOptions options) {
        this.options = options;
    }

    public void generate(Program program) {
        DocWriter writer = new MarkdownDocWriter(this.options);
        
        writer.start();
        for(Module module : program.getModules()) {
            writer.writeModule(module);
        }
        writer.end();
    }
}
