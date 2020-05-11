/*
 * see license.txt
 */
package litac;

import java.io.File;

import litac.compiler.*;
import litac.compiler.c.CTranspiler;
import litac.util.OS;
import litac.util.OS.OsType;

/**
 * Options for the LitaC compiler
 * 
 * @author Tony
 *
 */
public class LitaOptions {

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
        
        public String value = name();
        
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
    
    private File srcDir;
    public File libDir;
    public File buildFile;
    public File outputDir;        
    public File outputDocDir;
    
    public String outputFileName;
    public OsType targetOS;
    public TypeInfoOption typeInfo;
    public boolean run;
    public boolean checkerOnly;
    public boolean cOnly;
    public boolean profile;
    public boolean disableLines;
    public boolean debugMode;
    public boolean isVerbose;
    public boolean generateDocs;
    public boolean docsAll;
    
    public OutputType outputType;
    public String testRegex;
    public boolean testFile;
    
    public CTranspiler.COptions cOptions;
    
    private boolean hasCustomSrcDir;
    private Preprocessor preprocessor;
    
    private final String LITAC_HOME;
    private final String LITAC_LIB;
    
    public LitaOptions() {
        this(BackendType.C);
    }
    
    public LitaOptions(BackendType type) {
        this.backendType = type;
        this.outputType = OutputType.Executable;
        this.testRegex = ".*";
        
        File wd = new File(System.getProperty("user.dir"));
        
        this.outputDir = new File(wd, "output");
        this.outputFileName = "a";
        
        this.srcDir = wd;
        this.libDir = new File("./lib");
        this.LITAC_HOME = System.getenv("LITAC_HOME");
        if(this.LITAC_HOME != null) {
            this.LITAC_LIB = this.LITAC_HOME + "/lib";
            this.libDir = new File(this.LITAC_HOME, "lib");
        }
        else {
            this.LITAC_LIB = "";
        }
        
        this.targetOS = OS.getOS();
        this.typeInfo = TypeInfoOption.None;
        
        this.run = false;
        this.checkerOnly = false;
        this.cOnly = false;
        this.profile = false;
        this.disableLines = false;
        this.debugMode = false;
        
        this.generateDocs = false;
        this.outputDocDir = new File(wd, "output");
        
        this.cOptions = type == BackendType.C ? new CTranspiler.COptions(this) : null;
        this.preprocessor = new LeolaPreprocessor(this);
    }
    
    public Preprocessor preprocessor() {        
        return this.preprocessor;
    }
    
    public boolean reflectionEnabled() {
        return this.typeInfo != null && !this.typeInfo.equals(TypeInfoOption.None);
    }
    
    public File findModule(String fileName) {        
        return findModule(getSrcDir(), fileName);
    }
    
    public File findModule(File srcDir, String fileName) {
        File importFile = new File(srcDir, fileName);
        if(!importFile.exists()) {
            importFile = new File(this.libDir, fileName);
            if(!importFile.exists()) {                               
                if(this.LITAC_HOME != null) {
                    importFile = new File(this.LITAC_LIB, fileName);                
                }
            }
        }
        
        return importFile;
    }
    
    public void setSrcDir(File srcDir) {
        this.hasCustomSrcDir = true;
        this.srcDir = srcDir;
    }
    
    public File getSrcDir() {
        if(this.hasCustomSrcDir) {
            return this.srcDir;
        }
        
        File moduleFile = this.buildFile;
        this.srcDir = moduleFile != null ? moduleFile.getParentFile() : this.srcDir;
        if(this.srcDir == null) {
            this.srcDir = new File(System.getProperty("user.dir"));
        }
        
        return this.srcDir;
    }
}
