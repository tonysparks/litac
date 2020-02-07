/*
 * see license.txt
 */
package litac.compiler;

import java.io.*;

import litac.ast.Node.SrcPos;
import litac.ast.Stmt;
import litac.checker.*;
import litac.compiler.c.CTranspiler;
import litac.util.Profiler;
import litac.util.Profiler.Segment;

/**
 * @author Tony
 *
 */
public class Compiler {

    private BackendOptions options;
    
    public Compiler(BackendOptions options) {
        this.options = options;
    }
    
    public static CompileException error(Stmt stmt, String message, Object ...args) {
        return new CompileException(String.format(message, args) + 
                String.format(" at line %d in '%s'", stmt.getLineNumber(), stmt.getSourceFile()));
    }
    
    public PhaseResult compile(File rootModule) throws Exception {
        PhaseResult result = new PhaseResult();
        
        try {
            CompilationUnit unit = parse(this.options, rootModule, result);
            Program program = typeCheck(options, result, unit);
            
            if(!result.hasErrors() && !options.checkerOnly) {
                compile(options, result, unit, program);
            }
        }
        catch(Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            result.addError((SrcPos)null, "internal compiler error: %s", writer.toString());
        }
                
        return result;
    }
    
    private CompilationUnit parse(BackendOptions options, File rootModule, PhaseResult result) throws IOException {
        try(Segment s = Profiler.startSegment("Lexing/Parsing")) {
            CompilationUnit unit = CompilationUnit.modules(this.options, rootModule, result);
            return unit;
        }
    }
    
    private Program typeCheck(BackendOptions options, PhaseResult result, CompilationUnit unit) {
        try(Segment s = Profiler.startSegment("Type Checker")) {
            TypeResolver resolver = new TypeResolver(options.preprocessor(), result, unit);
                    
            Program program = resolver.resolveTypes();
            return program;
        }
    }
    
    private static void compile(BackendOptions options, 
                                PhaseResult checkerResult, 
                                CompilationUnit unit,
                                Program program) throws Exception {
        
        switch(options.backendType) {            
            case C: {
                CTranspiler.transpile(checkerResult, unit, program, options);
                break;
            }
            default: {
                throw error(null, "unsupported backend type '%s'", options.backendType.toString());
            }
        }      
        
    }

}
