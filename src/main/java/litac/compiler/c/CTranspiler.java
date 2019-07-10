/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import litac.Errors;
import litac.checker.*;
import litac.checker.PhaseResult.PhaseError;
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
        public BackendOptions options;
        
        
        public COptions(BackendOptions options) {
            this.options = options;
            this.useTabs = false;
            this.indentWidth = 4;
            this.symbolPrefix = "litaC__";
            
            this.compileCmd = "clang.exe -o \"%output%\" \"%input%\" -D_CRT_SECURE_NO_WARNINGS";
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
                                 Program program,
                                 BackendOptions options) throws Exception {
        
        Buf buf = toC(unit, program, options.cOptions);        
        File cOutput = writeCFile(buf, options);

        // if there are any type checker errors, we want to fail at this point,
        // this allows me to debug the output C
        if(checkerResult.hasErrors()) {
            for(PhaseError error : checkerResult.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            } 
            System.exit(23);
        }
        
        compileC(cOutput, options);        
        runProgram(options);
    }
    
    private static Buf toC(CompilationUnit unit, Program program, COptions options) throws Exception {
        Buf buf = new Buf(options.indentWidth, options.useTabs);        
        
        CGenNodeVisitor cWriter = new CGenNodeVisitor(unit, program, options, buf);                
        cWriter.write();
        
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
