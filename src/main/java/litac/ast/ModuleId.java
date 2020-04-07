/*
 * see license.txt
 */
package litac.ast;

import java.io.File;

import litac.util.Names;

/**
 * A way to uniquely identify a module
 * 
 * @author Tony
 *
 */
public class ModuleId {

    public static String getPackageName(String fullModuleName) {
        if(fullModuleName == null) {
            return "";
        }
        
        int index = fullModuleName.lastIndexOf('/');
        if(index < 0) {
            return "";
        }
        
        return fullModuleName.substring(0, index);
    }
    
    public static String getSimpleName(String fullModuleName) {
        if(fullModuleName == null) {
            return null;
        }
        
        if(fullModuleName.length() == 0) {
            return fullModuleName;
        }
        
        int index = fullModuleName.lastIndexOf('/') + 1;        
        return fullModuleName.substring(index);
    }
    
    /**
     * Builds a {@link ModuleId} based off of the supplied dir, everything is relative off
     * of it
     * @param dir the directory to be relative off of -- this should be either the source directory or library directory
     * @param fullModuleName
     * @return the {@link ModuleId}
     */
    public static ModuleId fromDirectory(File dir, String fullModuleName) {
        File moduleFile = new File(dir, fullModuleName + ".lita");
        String packageName = getPackageName(fullModuleName);
        String simpleName = getSimpleName(fullModuleName);
        return new ModuleId(moduleFile, packageName, simpleName, fullModuleName);
    }
    
    /**
     * Builds a {@link ModuleId} based off of the supplied libDir and srcDir, everything is relative off
     * of it - it will determine which directory the file is from.
     * 
     * @param libDir
     * @param srcDir
     * @param moduleFile
     * @return the {@link ModuleId}
     */
    public static ModuleId from(File libDir, File srcDir, File moduleFile) {        
        String fullModuleName = Names.getRelativeModulePath(libDir, srcDir, moduleFile);
        String packageName = getPackageName(fullModuleName);
        String simpleName = getSimpleName(fullModuleName);
        return new ModuleId(moduleFile, packageName, simpleName, fullModuleName);
    }
        
    /**
     * The File representation to this module
     */
    public final File moduleFile;
    
    /**
     * The package name of this module.  The path relative to the src or lib
     * directory to the module
     */
    public final String packageName;
    
    /**
     * Just the module name with no package (i.e., the file name without the file extension)
     */
    public final String simpleName;
    
    /**
     * The full module name including the package
     */
    public final String fullModuleName;
    
    /**
     * The unique id of this module
     */
    public final String id;

    /**
     * @param moduleFile
     * @param packageName
     * @param simpleName
     * @param fullModuleName
     */
    public ModuleId(File moduleFile, String packageName, String simpleName, String fullModuleName) {
        this.moduleFile = moduleFile;
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.fullModuleName = fullModuleName;
        
        this.id = moduleFile.toPath().normalize().toString().toLowerCase();
    }

    
    
    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullModuleName == null) ? 0 : fullModuleName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModuleId other = (ModuleId) obj;
        if (fullModuleName == null) {
            if (other.fullModuleName != null)
                return false;
        }
        else if (!fullModuleName.equals(other.fullModuleName))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
}
