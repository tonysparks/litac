/*
 * see license.txt
 */
package litac;

import java.io.File;

import litac.checker.PhaseResult;
import litac.checker.PhaseResult.PhaseError;
import litac.compiler.BackendOptions;
import litac.compiler.BackendOptions.BackendType;
import litac.compiler.Compiler;

/**
 * @author Tony
 *
 */
public class LitaC {

    public static final String VERSION = "v0.1.0";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.out.println("<usage> litac.exe [source files]");
            //System.out.println("   -backend=C|LLVM   Options are either C or LLVM, C is default");
            return;
        }
        
        File moduleFile = new File(args[0]);
        PhaseResult result = compile(moduleFile);
        
        if(result.hasErrors()) {
            for(PhaseError error : result.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            }            
        }  
    }

    public static PhaseResult compile(File moduleFile) throws Exception {
        BackendOptions options = new BackendOptions(BackendType.C);
        options.srcDir = moduleFile.getParentFile();
        options.cOptions.run = true;
        
        Compiler compiler = new Compiler(options);
        return compiler.compile(moduleFile);        
    }
    
}
