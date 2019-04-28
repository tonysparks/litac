/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import litac.ast.Stmt.*;
import litac.Errors;
import litac.checker.Module;
import litac.checker.TypeCheckResult;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.checker.TypeChecker;
import litac.checker.TypeChecker.TypeCheckerOptions;
import litac.compiler.BackendOptions;
import litac.compiler.Buf;
import litac.compiler.CompilationUnit;
import litac.util.Exec;
import litac.util.OS.OsType;

/**
 * @author Tony
 *
 */
public class CTranspiler {

    public static class COptions {
        public boolean useTabs;
        public int indentWidth;
        public String compileCmd;
        public boolean run;
        String binaryFileOutput;
        
        
        public COptions(BackendOptions options) {            
            this.useTabs = false;
            this.indentWidth = 4;
            this.run = false;
            
            this.binaryFileOutput = String.format("%s/%s%s", options.outputDir.getAbsolutePath(), 
                                                             options.outputFileName, 
                                                             options.targetOS.getExecutableExt());
            
            this.compileCmd = String.format("clang.exe -o \"%s\" ", this.binaryFileOutput);
        }
    }

    /**
     * Transpile the LitaC to C99 and compile it
     *  
     * @param program
     * @param options
     * @throws Exception
     */
    public static void transpile(TypeCheckResult checkerResult, CompilationUnit unit, BackendOptions options) throws Exception {        
        Buf buf = toC(unit, options.cOptions);        
        File cOutput = writeCFile(buf, options);

        // if there are any type checker errors, we want to fail at this point,
        // this allows me to debug the output C
        if(checkerResult.hasErrors()) {
            System.exit(23);
        }
        
        compileC(cOutput, options);        
        runProgram(options);
    }
    
    private static Buf toC(CompilationUnit unit, COptions options) throws Exception {
        Buf buf = new Buf(options.indentWidth, options.useTabs);        
        
        NameCache names = NameCache.build(unit);
                
        CWriterNodeVisitor visitor = new CWriterNodeVisitor(unit, names, options, buf);                
        unit.getMain().visit(visitor);
        
        return buf;
    }
    
    
    private static File writeCFile(Buf buf, BackendOptions options) throws Exception {
        options.outputDir.mkdirs();
        
        File cOutput = new File(options.outputDir, options.outputFileName + ".c");
        Files.write(cOutput.toPath(), buf.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        return cOutput;
    }
    
    private static void compileC(File cOutput, BackendOptions options) throws Exception {
        String compileCmd = options.cOptions.compileCmd + "\"" + cOutput.getAbsolutePath() + "\"";
        int status = Exec.run(options.outputDir, compileCmd);
        if(status != 0) {
            System.exit(status);
        }
    }
    
    private static void runProgram(BackendOptions options) throws Exception {
        if(options.cOptions.run) {
            int status = Exec.run(options.outputDir, options.cOptions.binaryFileOutput);
            if(status != 0) {
                System.exit(status);
            }
        }
    }
}
