/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;

import litac.ast.Stmt;
import litac.checker.*;
import litac.compiler.c.CTranspiler;

/**
 * @author Tony
 *
 */
public class Compiler {

    private BackendOptions options;
    
    /**
     * 
     */
    public Compiler(BackendOptions options) {
        this.options = options;
    }
    
    public static CompileException error(Stmt stmt, String message, Object ...args) {
        return new CompileException(String.format(message, args) + 
                String.format(" at line %d in '%s'", stmt.getLineNumber(), stmt.getSourceFile()));
    }
    
    public PhaseResult compile(File rootModule) throws Exception {
        PhaseResult result = new PhaseResult();
        
        CompilationUnit unit = CompilationUnit.modules(this.options, rootModule);
        Module main = typeCheck(options, result, unit);
        
        if(!result.hasErrors()) {
            compile(options, result, unit, main);
        }
                
        return result;
    }
    
    private Module typeCheck(BackendOptions options, PhaseResult result, CompilationUnit unit) {
        TypeResolver resolver = new TypeResolver(result, unit);        
        TypeChecker checker = new TypeChecker(result);
                
        Module main = resolver.resolveTypes();
        if(!result.hasErrors()) {
            checker.typeCheck(main);
        }
        
        return main;
    }
    
    private static void compile(BackendOptions options, 
                                PhaseResult checkerResult, 
                                CompilationUnit unit,
                                Module main) throws Exception {
        switch(options.backendType) {            
            case C: {
                CTranspiler.transpile(checkerResult, unit, main, options);
                break;
            }
            default: {
                throw error(null, "unsupported backend type '%s'", options.backendType.toString());
            }
        }        
    }

}
