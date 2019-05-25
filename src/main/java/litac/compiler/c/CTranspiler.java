/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import litac.checker.Module;
import litac.checker.PhaseResult;
import litac.compiler.BackendOptions;
import litac.compiler.Buf;
import litac.compiler.CompilationUnit;
import litac.util.Exec;

/**
 * @author Tony
 *
 */
public class CTranspiler {

    public static class COptions {
        public boolean useTabs;
        public int indentWidth;
        public String compileCmd;
        public String symbolPrefix;        
        private BackendOptions options;
        
        
        public COptions(BackendOptions options) {
            this.options = options;
            this.useTabs = false;
            this.indentWidth = 4;
            this.symbolPrefix = "litaC__";
            
            
            
            this.compileCmd = "clang.exe -o \"%output%\" \"%input%\"";
        }
        
        public String getBinaryOutputFile() {
            return String.format("%s/%s%s", options.outputDir.getAbsolutePath(), 
                                            options.outputFileName, 
                                            options.targetOS.getExecutableExt());
        }
        
        public String getCompileCmd(File cOutput) {
            String binaryFileOutput = getBinaryOutputFile();            
            return this.compileCmd.replace("%output%", binaryFileOutput)
                                  .replace("%input%", cOutput.getAbsolutePath());
        }
    }

    /**
     * Transpile the LitaC to C99 and compile it
     *  
     * @param program
     * @param options
     * @throws Exception
     */
    public static void transpile(PhaseResult checkerResult, 
                                 CompilationUnit unit, 
                                 Module main,
                                 BackendOptions options) throws Exception {
        
        Buf buf = toC(unit, main, options.cOptions);        
        File cOutput = writeCFile(buf, options);

        // if there are any type checker errors, we want to fail at this point,
        // this allows me to debug the output C
        if(checkerResult.hasErrors()) {
            System.exit(23);
        }
        
        compileC(cOutput, options);        
        runProgram(options);
    }
    
    private static Buf toC(CompilationUnit unit, Module main, COptions options) throws Exception {
        Buf buf = new Buf(options.indentWidth, options.useTabs);        
        
        CWriterNodeVisitor cWriter = new CWriterNodeVisitor(unit, main, options, buf);                
        unit.getMain().visit(cWriter);
        
        return buf;
    }
    
    
    private static File writeCFile(Buf buf, BackendOptions options) throws Exception {
        options.outputDir.mkdirs();
        
        File cOutput = new File(options.outputDir, options.outputFileName + ".c");
        Files.write(cOutput.toPath(), buf.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        return cOutput;
    }
    
    private static void compileC(File cOutput, BackendOptions options) throws Exception {
        String compileCmd = options.cOptions.getCompileCmd(cOutput);
        int status = Exec.run(options.outputDir, compileCmd);
        if(status != 0) {
            System.exit(status);
        }
    }
    
    private static void runProgram(BackendOptions options) throws Exception {
        if(options.run) {
            int status = Exec.run(options.outputDir, options.cOptions.getBinaryOutputFile());
            if(status != 0) {
                System.exit(status);
            }
        }
    }
}
