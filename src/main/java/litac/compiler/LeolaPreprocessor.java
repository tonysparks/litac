/*
 * see license.txt
 */
package litac.compiler;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import litac.LitaOptions;
import litac.checker.TypeInfo;
import litac.util.OS;

/**
 * TODO: This is bringing a missile to a fist fight. We
 * don't need a full on scripting engine for the preprocessor.
 * Eventually clean this up to be a simple expr evaluator
 */
public class LeolaPreprocessor implements Preprocessor {
    private Leola runtime;
    
    public LeolaPreprocessor(LitaOptions options) {
        this.runtime = Leola.builder()
                .setBarebones(true)
                .setSandboxed(true)
                .setAllowThreadLocals(false)
                .newRuntime();
        
        this.runtime.put("options", options);
        this.runtime.put("OS", OS.getOS().name());
        this.runtime.loadStatics(TypeInfo.class);
    }
    
    @Override
    public void putContext(String name, Object context) {
        this.runtime.put(name, context);
    }
    
    @Override
    public boolean execute(String stmt) {
        try {
            return LeoObject.isTrue(this.runtime.eval("return " + stmt));
        }
        catch (Exception e) {
            throw new CompileException(e.getMessage());
        }
    }

}
