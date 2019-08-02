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
    
    public static enum OutputType {
        Test,
        Executable,
        StaticLib,
        DynamicLib
    }
    
    public static enum TypeInfoOption {
        None,
        All,
        Tagged,
        ;
        
        public static TypeInfoOption fromString(String option) {
            if(option == null) {
                return None;
            }
            
            for(TypeInfoOption op : values()) {
                if(op.name().equalsIgnoreCase(option)) {
                    return op;
                }
            }
            
            return None;
        }
    }
    
    public BackendType backendType;
    
    public File srcDir;
    public File libDir;
    public File buildFile;
    public File outputDir;        
    public String outputFileName;
    public OsType targetOS;
    public TypeInfoOption typeInfo;
    public boolean run;
    public boolean checkerOnly;
    public boolean cOnly;
    public boolean profile;
    public boolean disableLines;
    public boolean debugMode;
    public OutputType outputType;
    public String testRegex;
    
    public CTranspiler.COptions cOptions;
    
    private Preprocessor preprocessor;
    
    public BackendOptions() {
        this(BackendType.C);
    }
    
    public BackendOptions(BackendType type) {
        this.backendType = type;
        this.outputType = OutputType.Executable;
        this.testRegex = ".*";
        
        File wd = new File(System.getProperty("user.dir"));
        
        this.outputDir = new File(wd, "output");
        this.outputFileName = "a";
        
        this.srcDir = wd;
        this.libDir = new File("./lib");
        
        this.targetOS = OsType.WINDOWS;
        this.typeInfo = TypeInfoOption.None;
        
        this.run = false;
        this.checkerOnly = false;
        this.cOnly = false;
        this.profile = false;
        this.disableLines = false;
        this.debugMode = false;
        
        this.cOptions = type == BackendType.C ? new CTranspiler.COptions(this) : null;
    }
    
    public Preprocessor preprocessor() {
        if(this.preprocessor == null) {
            this.preprocessor = new LeolaPreprocessor(this);
        }
        
        return this.preprocessor;
    }
}
