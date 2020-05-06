/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import litac.*;
import litac.ast.Stmt.NoteStmt;
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
            
            this.compileCmd = "clang -o \"%output%\" %input% -D_CRT_SECURE_NO_WARNINGS";
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
        
        public String getCompileCmd(File[] cOutputFiles) {
            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for(File outputFile : cOutputFiles) {
                if(!isFirst) sb.append(" ");
                sb.append("\"").append(outputFile.getAbsolutePath()).append("\"");
                isFirst = false;
            }
            String binaryFileOutput = getBinaryOutputFile();            
            return this.compileCmd.replace("%output%", binaryFileOutput)
                                  .replace("%input%", sb.toString());
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
        
        // find all C compilation units
        List<Module> modules = program.getModules()
                                        .stream()
                                        .filter(module -> module.isCompilationUnit() || program.isMainModule(module))
                                        .collect(Collectors.toList());
        
        File[] cOutputFiles = new File[modules.size()];
        for(int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            String compilationUnitName = null;
            CompilationUnit compilationUnit = null;
            if(program.isMainModule(module)) {
                compilationUnit = unit;
                compilationUnitName = options.outputFileName;
            }
            else {
                compilationUnit = new CompilationUnit(unit.getBuiltin(), module.getModuleStmt());
                NoteStmt moduleNote = module.getModuleStmt().getCompilationUnitNote();                
                compilationUnitName = moduleNote.getAttr(0, module.simpleName());
                
                for(Module importModule : module.getImports()) {
                    compilationUnit.getImports().put(importModule.getId(), importModule.getModuleStmt());
                }
            }
            
            Buf buf = toC(compilationUnit, program, options);        
            File cOutput = writeCFile(buf, options, compilationUnitName);
            cOutputFiles[i] = cOutput;
        }

        // if there are any type checker errors, we want to fail at this point,
        // this allows me to debug the output C
        if(checkerResult.hasErrors()) {
            for(PhaseError error : checkerResult.getErrors()) {
                Errors.typeCheckError(error.pos, error.message);
            } 
            System.exit(23);
        }
        
        if(!options.cOnly) {
            compileC(cOutputFiles, options);   
            
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
    
    
    private static File writeCFile(Buf buf, LitaOptions options, String compilationUnitName) throws Exception {
        try(Segment s = Profiler.startSegment("C Write")) {
            options.outputDir.mkdirs();
            
            File cOutput = new File(options.outputDir, compilationUnitName + ".c");
            Files.write(cOutput.toPath(), buf.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            return cOutput;
        }
    }
    
    private static void compileC(File[] cOutputFiles, LitaOptions options) throws Exception {
        try(Segment s = Profiler.startSegment("C Compile")) {
            String compileCmd = options.cOptions.getCompileCmd(cOutputFiles);
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
