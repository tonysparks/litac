/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;

import litac.compiler.c.CTranspiler;
import litac.util.OS.OsType;

/**
 * @author Tony
 *
 */
public class BackendOptions {

    public static enum BackendType {
        C,        
    }
    
    public BackendType backendType;
    
    public File srcDir;
    public File outputDir;        
    public String outputFileName;
    public OsType targetOS;
    
    public CTranspiler.COptions cOptions;
    
    public BackendOptions(BackendType type) {
        this.backendType = type;
        
        this.outputDir = new File(System.getProperty("user.dir"), "output");
        this.outputFileName = "a";
        
        this.targetOS = OsType.WINDOWS;
        
        this.cOptions = type == BackendType.C    ? new CTranspiler.COptions(this) : null;
    }
}
