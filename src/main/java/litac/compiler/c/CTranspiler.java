/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import litac.*;
import litac.compiler.*;
import litac.compiler.PhaseResult.PhaseError;
import litac.util.Exec;
import litac.util.Profiler;
import litac.util.Profiler.Segment;

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
        public LitaOptions options;
        
        
        public COptions(LitaOptions options) {
            this.options = options;
            this.useTabs = false;
            this.indentWidth = 4;
            this.symbolPrefix = "litaC__";
            
            this.compileCmd = "clang -o \"%output%\" \"%input%\" -D_CRT_SECURE_NO_WARNINGS";
        }
        
        public String getBinaryOutputFile() {
            String fileExt = options.targetOS.getExecutableExt();
            if(options.outputFileName.contains(".")) {
                fileExt = ""; // it is defined already..maybe :S
            }
            return String.format("%s/%s%s", options.outputDir.getAbsolutePath(), 
                                            options.outputFileName, 
                                            fileExt);
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
                                 LitaOptions options) throws Exception {
        
        Buf buf = toC(unit, program, options);        
        File cOutput = writeCFile(buf, options);

        // if there are any type checker errors, we want to fail at this point,
        // this allows me to debug the output C
        if(checkerResult.hasErrors()) {
            for(PhaseError error : checkerResult.getErrors()) {
                Errors.typeCheckError(error.pos, error.message);
            } 
            System.exit(23);
        }
        
        if(!options.cOnly) {
            compileC(cOutput, options);   
            
            if(options.run) {
                runProgram(options);
            }
        }
    }
    
    private static Buf toC(CompilationUnit unit, Program program, LitaOptions options) throws Exception {
        try(Segment s = Profiler.startSegment("C Genaration")) {
            COptions cOptions = options.cOptions;
            Buf buf = new Buf(cOptions.indentWidth, cOptions.useTabs);        
            
            CGen cWriter = new CGen(options.preprocessor(), unit, program, cOptions, buf);                
            cWriter.write();
            
            return buf;
        }
    }
    
    
    private static File writeCFile(Buf buf, LitaOptions options) throws Exception {
        try(Segment s = Profiler.startSegment("C Write")) {
            options.outputDir.mkdirs();
            
            File cOutput = new File(options.outputDir, options.outputFileName + ".c");
            Files.write(cOutput.toPath(), buf.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            return cOutput;
        }
    }
    
    private static void compileC(File cOutput, LitaOptions options) throws Exception {
        try(Segment s = Profiler.startSegment("C Compile")) {
            String compileCmd = options.cOptions.getCompileCmd(cOutput);
            int status = Exec.run(options.outputDir, compileCmd);
            if(status != 0) {
                System.exit(status);
            }
        }
    }
    
    private static void runProgram(LitaOptions options) throws Exception {
        int status = Exec.run(options.outputDir, options.cOptions.getBinaryOutputFile());
        if(status != 0) {
            System.exit(status);
        }
    }
}
