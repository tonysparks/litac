/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;

import litac.Errors;
import litac.ast.Stmt;
import litac.checker.TypeCheckResult;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.checker.TypeChecker;
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
        CompilationUnit unit = CompilationUnit.modules(this.options, rootModule);
        TypeCheckResult result = typeCheck(options, unit);
        
        if(!result.hasErrors()) {
            compile(options, result, unit);
        }
    }
    
    private TypeCheckResult typeCheck(BackendOptions options, CompilationUnit unit) {
        TypeChecker checker = new TypeChecker(options.checkerOptions, unit);
        
        TypeCheckResult checkerResult = checker.typeCheck();
        if(checkerResult.hasErrors()) {
            for(TypeCheckError error : checkerResult.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            }            
        }
        
        return checkerResult;
    }
    
    private static void compile(BackendOptions options, TypeCheckResult checkerResult, CompilationUnit unit) throws Exception {
        switch(options.backendType) {
            case LLVM: {                
//                options.llvmOptions.checkerOptions.srcDir = moduleFile.getParentFile();
//                
//                LLVMTranspiler.transpile(program, options.llvmOptions);
                break;
            }
            case C: {
                CTranspiler.transpile(checkerResult, unit, options);
                break;
            }
        }        
    }

}
