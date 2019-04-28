/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;

import litac.checker.TypeChecker.TypeCheckerOptions;
import litac.compiler.c.CTranspiler;
import litac.compiler.llvm.LLVMTranspiler;
import litac.util.OS.OsType;

/**
 * @author Tony
 *
 */
public class BackendOptions {

    public static enum BackendType {
        LLVM,
        C,        
    }
    
    public BackendType backendType;
    public TypeCheckerOptions checkerOptions;
    
    public File outputDir;        
    public String outputFileName;
    public OsType targetOS;
    
    public LLVMTranspiler.LLVMOptions llvmOptions;
    public CTranspiler.COptions cOptions;
    
    public BackendOptions(BackendType type) {
        this.backendType = type;
        this.checkerOptions = new TypeCheckerOptions();
        
        this.outputDir = new File(System.getProperty("user.dir"), "output");
        this.outputFileName = "a";
        
        this.targetOS = OsType.WINDOWS;
        
        this.llvmOptions = type == BackendType.LLVM ? new LLVMTranspiler.LLVMOptions() : null;
        this.cOptions = type == BackendType.C    ? new CTranspiler.COptions(this) : null;
    }
}
