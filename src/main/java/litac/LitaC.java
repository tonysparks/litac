/*
 * see license.txt
 */
package litac;

import java.io.File;

import litac.compiler.BackendOptions;
import litac.compiler.BackendOptions.BackendType;
import litac.compiler.BackendOptions.OutputType;
import litac.compiler.PhaseResult.PhaseError;
import litac.util.Profiler;
import litac.util.Profiler.Segment;
import litac.compiler.Compiler;
import litac.compiler.PhaseResult;

/**
 * @author Tony
 *
 */
public class LitaC {

    public static final String VERSION = "v0.2.0";
    
    private static void checkArg(String[] args, int index, String paramName) {
        if(index + 1 >= args.length) {
            System.err.printf("The '%s' option must include an argument\n", paramName);
            System.exit(1);
        }
    }
    
    private static void printHelp() {
        System.out.println("<usage> litac [options] [source file to compile]");
        System.out.println("OPTIONS:");
        System.out.println("  -lib <arg>           The LitaC library path");
        System.out.println("  -cPrefix <arg>       The symbol prefix to use on the generated C code output");
        System.out.println("  -run                 Runs the program after a successful compile");
        System.out.println("  -checkerOnly         Only runs the type checker, does not compile");
        System.out.println("  -cOnly               Only creates the C output file, does not compile the generated C code");
        System.out.println("  -profile             Reports profile metrics of the compiler");
        System.out.println("  -o, -output <arg>    The name of the compiled binary");
        System.out.println("  -outpuDir <arg>      The directory in which the C output files are stored");
        System.out.println("  -v, -version         Displays the LitaC version");
        System.out.println("  -h, -help            Displays this help");
        System.out.println("  -t, -types           Does not include TypeInfo for reflection");
        System.out.println("  -test <arg>          Runs functions annotated with @test.  'arg' is a regex of which tests should be run");
        System.out.println("  -buildCmd            The underlying C compiler build and compile command.  Variables will ");
        System.out.println("                       be substituted if found: ");
        System.out.println("                          %output%         The executable name ");
        System.out.println("                          %input%          The C file(s) generated ");
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
        
        BackendOptions options = new BackendOptions(BackendType.C);
        
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch(arg) {
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
                    options.typeInfo = false;
                    break;
                }
                case "-test": {
                    checkArg(args, i, "-test");
                    options.outputType = OutputType.Test;
                    options.testRegex = args[++i];
                    break;
                }
                default:                    
                    options.buildFile = new File(args[i]);
                    break;
            }
        }
        
        if(options.buildFile == null) {
            System.err.println("No input file supplied");
            System.exit(1);
        }
        
//        try {
            PhaseResult result = compile(options);
            
            if(result.hasErrors()) {
                for(PhaseError error : result.getErrors()) {
                    Errors.typeCheckError(error.stmt, error.message);
                }            
            }  
            
            if(options.profile) {
                printProfileResults();
            }
//        }
//        catch(Exception e) {
//            System.err.println(e.getMessage());
//        }
    }

    public static PhaseResult compile(BackendOptions options) throws Exception {
        File moduleFile = options.buildFile;
        options.srcDir = moduleFile.getParentFile();
                
        Compiler compiler = new Compiler(options);
        return compiler.compile(moduleFile);        
    }

    
}
