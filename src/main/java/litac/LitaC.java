/*
 * see license.txt
 */
package litac;

import java.io.File;

import litac.compiler.PhaseResult.PhaseError;
import litac.lsp.LitaCLanguageServer;
import litac.util.Profiler;
import litac.util.Profiler.Segment;
import litac.LitaOptions.*;
import litac.compiler.Compiler;
import litac.compiler.PhaseResult;

/**
 * @author Tony
 *
 */
public class LitaC {

    public static final String VERSION = "v0.4.0";
    
    private static void checkArg(String[] args, int index, String paramName) {
        if(index + 1 >= args.length) {
            System.err.printf("The '%s' option must include an argument\n", paramName);
            System.exit(1);
        }
    }
    
    private static void printHelp() {
        System.out.println("<usage> litac [options] [source file to compile]");
        System.out.println("OPTIONS:");
        System.out.println("  -languageServer      Start the LitaC language server");
        System.out.println("  -lib <arg>           The LitaC library path");
        System.out.println("  -cPrefix <arg>       The symbol prefix to use on the generated C code output");
        System.out.println("  -run                 Runs the program after a successful compile");
        System.out.println("  -checkerOnly         Only runs the type checker, does not compile");
        System.out.println("  -cOnly               Only creates the C output file, does not compile the generated C code");
        System.out.println("  -profile             Reports profile metrics of the compiler");
        System.out.println("  -disableLine         Disables #line directive in C output");
        System.out.println("  -debug               Enables debug mode");
        System.out.println("  -verbose             Enables verbose output");
        System.out.println("  -srcDir              Specifies the source code directory, defaults to the parent folder of the supplied source file");
        System.out.println("  -doc                 Generates document output");
        System.out.println("  -docDir <arg>        Directory where the generated documents are written to, defaults to './output'");
        System.out.println("  -docAll              Includes non-public types in the documentation generation");
        System.out.println("  -o, -output <arg>    The name of the compiled binary");
        System.out.println("  -outpuDir <arg>      The directory in which the C output files are stored");
        System.out.println("  -v, -version         Displays the LitaC version");
        System.out.println("  -h, -help            Displays this help");
        System.out.println("  -t, -types <arg>     Includes TypeInfo for reflection");
        System.out.println("                       <arg> can be:");
        System.out.println("                         all         Means all types will have reflection values");
        System.out.println("                         tagged      Means only basic types and types annoted with @typeinfo will have reflection values");        
        System.out.println("  -test <arg>          Runs functions annotated with @test.  <arg> is a regex of which tests should be run");
        System.out.println("  -testFile            Runs functions annotated with @test in the supplied source file only");
        System.out.println("  -buildCmd            The underlying C compiler build and compile command.  Variables will ");
        System.out.println("                       be substituted if found: ");
        System.out.println("                          %output%         The executable name ");
        System.out.println("                          %input%          The C file(s) generated ");
        System.out.println("  -lineInfo            Enables line information in C source file");
    }
    
    
    private static void printProfileResults() {
        long totalTime = 0;
        for(Segment s : Profiler.profiledSegments()) {
            totalTime += s.getDeltaTimeNSec();                                     
        }
        
        System.out.printf("\n");
        System.out.printf("%-20s %-20s %10s\n", "Segment", "Time (NanoSec)", "% of Total");
        System.out.printf("======================================================\n");
        for(Segment s : Profiler.profiledSegments()) {
            long delta = s.getDeltaTimeNSec();
            int percentage = 0;
            if(totalTime > 0) {
                percentage = (int) (((double)delta / (double)totalTime) * 100);
            }
            
            System.out.printf("%-20s %15d %10d%%\n", s.name, delta, percentage);
            
        }
        
        System.out.printf("%20s %15d ns (%d ms)\n", "Total Time:", totalTime, totalTime / 1_000_000);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            printHelp();
            return;
        }
        
        LitaOptions options = new LitaOptions(BackendType.C);
        boolean isLanguageServer = false;
        
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch(arg) {
                case "-languageServer": {
                    isLanguageServer = true;
                    break;
                }
                case "-h":
                case "-help": {
                    printHelp();
                    break;
                }
                case "-v":
                case "-version": {
                    System.out.printf("%s\n", VERSION);
                    break;
                }
                case "-verbose": {
                    options.isVerbose = true;
                    break;
                }
                case "-srcDir": {
                    checkArg(args, i, "-srcDir");
                    options.setSrcDir(new File(args[++i]));
                    break;
                }
                case "-doc": {
                    options.generateDocs = true;
                    break;
                }
                case "-docAll": {
                    options.docsAll = true;
                    break;
                }
                case "-docDir": {
                    checkArg(args, i, "-docDir");
                    options.outputDocDir = new File(args[++i]);
                    break;
                }
                case "-profile": {
                    options.profile = true;
                    break;
                }
                case "-buildCmd": {
                    checkArg(args, i, "-buildCmd");
                    options.cOptions.compileCmd = args[++i];
                    break;
                }
                case "-lib": {
                    checkArg(args, i, "-lib");                    
                    options.libDir = new File(args[++i]);
                    break;
                }
                case "-cPrefix": {
                    checkArg(args, i, "-cPrefix");                    
                    options.cOptions.symbolPrefix = args[++i];
                    break;
                }
                case "-run": {
                    options.run = true;
                    break;
                }
                case "-checkerOnly": {
                    options.checkerOnly = true;
                    break;
                }
                case "-cOnly": {
                    options.cOnly = true;
                    break;
                }
                case "-debug": {
                    options.debugMode = true;
                    break;
                }
                case "-o": 
                case "-output": {
                    checkArg(args, i, "-output");
                    options.outputFileName = args[++i];
                    break;
                }
                case "-outputDir": {
                    checkArg(args, i, "-outputDir");
                    options.outputDir = new File(args[++i]);
                    break;
                }
                case "-t":
                case "-types": {
                    checkArg(args, i, "-types");
                    options.typeInfo = TypeInfoOption.fromString(args[++i]);
                    break;
                }
                case "-test": {
                    checkArg(args, i, "-test");
                    options.outputType = OutputType.Test;
                    options.testRegex = args[++i];
                    break;
                }
                case "-testFile": {
                    options.outputType = OutputType.Test;
                    options.testFile = true;
                    break;
                }
                case "-lineInfo": {
                    options.disableLines = false;
                    break;
                }
                default:                    
                    options.buildFile = new File(args[i]);
                    break;
            }
        }
        
        if(isLanguageServer) {
            LitaCLanguageServer server = new LitaCLanguageServer(options);
            server.start();
        }
        else {
            if(options.buildFile == null) {
                System.err.println("No input file supplied");
                System.exit(1);
            }
            
            try {
                PhaseResult result = compile(options);
                
                if(result.hasErrors()) {
                    for(PhaseError error : result.getErrors()) {
                        Errors.typeCheckError(error.pos, error.message);
                    }            
                }  
                
                if(options.profile) {
                    printProfileResults();
                }
            }
            catch(Exception e) {
                if(options.isVerbose) {
                    throw e;
                }
                
                System.err.println(e.getMessage());
            }
        }
    }

    public static PhaseResult compile(LitaOptions options) throws Exception {
        File moduleFile = options.buildFile;
        Compiler compiler = new Compiler(options);
        return compiler.compile(moduleFile);        
    }

    
}
