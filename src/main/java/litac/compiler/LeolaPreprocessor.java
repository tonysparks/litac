/*
 * see license.txt
 */
package litac.compiler;

import leola.vm.Leola;
import leola.vm.types.*;
import litac.LitaOptions;
import litac.LitaOptions.TypeInfoOption;
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
        this.runtime.put("DEBUG", options.debugMode);
        this.runtime.put("REFLECTION", !options.typeInfo.equals(TypeInfoOption.None));
        
        //this.runtime.loadStatics(TypeInfo.class);
        this.runtime.put("getTypeKind", new LeoUserFunction() {
            
            @Override
            public LeoObject call(LeoObject[] args) {
                if(args.length < 1) {
                    return new LeoError("invalid empty symbol name", 0);
                }
                String element = args[0].toString();
                LeoObject scopeObj = runtime.get("scope");
                if(scopeObj == null) {
                    return new LeoError("no active scope defined", 0);
                }
                
                Object scope = scopeObj.getValue(Scope.class);
                if(!(scope instanceof Scope)) {
                    return new LeoError("no active scope defined", 0);
                }
                
                Scope currentScope = (Scope)scope;
                Symbol sym = currentScope.getSymbol(element);
                if(sym == null) {
                    return new LeoError("no symbol found for '" + element + "'", 0);
                }
                
                return LeoString.valueOf(sym.type.getKind().name().toUpperCase());
            }
        });        
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
