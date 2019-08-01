/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import litac.compiler.c.CTranspiler;
import litac.util.OS;
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
    
    public BackendType backendType;
    
    public File srcDir;
    public File libDir;
    public File buildFile;
    public File outputDir;        
    public String outputFileName;
    public OsType targetOS;
    public boolean run;
    public boolean checkerOnly;
    public boolean cOnly;
    public boolean typeInfo;
    public boolean profile;
    public boolean disableLines;
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
        this.run = false;
        this.typeInfo = true;
        this.checkerOnly = false;
        this.cOnly = false;
        this.profile = false;
        this.disableLines = false;
        
        this.cOptions = type == BackendType.C ? new CTranspiler.COptions(this) : null;
    }
    
    public Preprocessor preprocessor() {
        if(this.preprocessor == null) {
            this.preprocessor = new Preprocessor() {
                
                // TODO: This is bringing a missle to a fist fight. We
                // don't need a full on scripting engine for the preprocessor.
                // Eventually clean this up to be a simple expr evaluator
                Leola runtime = Leola.builder()
                            .setBarebones(true)
                            .setSandboxed(true)
                            .setAllowThreadLocals(false)
                            .newRuntime();
                
                {
                    runtime.put("options", BackendOptions.this);
                    runtime.put("OS", OS.getOS().name());
                }
                
                @Override
                public boolean execute(String stmt) {
                    try {
                        return LeoObject.isTrue(this.runtime.eval("return " + stmt));
                    }
                    catch (Exception e) {
                        throw new CompileException(e.getMessage());
                    }
                }
            };
        }
        
        return this.preprocessor;
    }
}
