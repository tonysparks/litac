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
    public File libDir;
    public File buildFile;
    public File outputDir;        
    public String outputFileName;
    public OsType targetOS;
    public boolean run;
    
    public CTranspiler.COptions cOptions;
    
    public BackendOptions() {
        this(BackendType.C);
    }
    
    public BackendOptions(BackendType type) {
        this.backendType = type;
        
        File wd = new File(System.getProperty("user.dir"));
        
        this.outputDir = new File(wd, "output");
        this.outputFileName = "a";
        
        this.srcDir = wd;
        this.libDir = new File("./lib");
        
        this.targetOS = OsType.WINDOWS;
        this.run = false;
        
        this.cOptions = type == BackendType.C ? new CTranspiler.COptions(this) : null;
    }
}
