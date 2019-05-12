/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;

import litac.Errors;
import litac.ast.Stmt;
import litac.checker.GenericsResolver;
import litac.checker.Module;
import litac.checker.PhaseResult;
import litac.checker.PhaseResult.PhaseError;
import litac.checker.TypeChecker;
import litac.checker.TypeResolver;
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
    
    public void compile(File rootModule) throws Exception {
        PhaseResult result = new PhaseResult();
        
        CompilationUnit unit = CompilationUnit.modules(this.options, rootModule);
        Module main = typeCheck(options, result, unit);
        
        if(!result.hasErrors()) {
            compile(options, result, unit, main);
        }
    }
    
    private Module typeCheck(BackendOptions options, PhaseResult result, CompilationUnit unit) {
        GenericsResolver generics = new GenericsResolver(unit);        
        TypeResolver resolver = new TypeResolver(result, unit);        
        TypeChecker checker = new TypeChecker(result);
                
        Module main = resolver.resolveTypes();
        if(!result.hasErrors()) {
            generics.applyGenerics(resolver, main);
        }
        
        if(!result.hasErrors()) {
            checker.typeCheck(main);
        }
        
        if(result.hasErrors()) {
            for(PhaseError error : result.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            }            
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
