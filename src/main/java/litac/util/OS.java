/*
 * see license.txt
 */
package litac.util;

/**
 * @author Tony
 *
 */
public class OS {

    public static enum OsType {
        WINDOWS(".exe", ".lib", ".dll"),
        LINUX("", ".a", "so"),
        MACOS("", ".a", ".dylib"),
        OTHER("", "", "")
        ;
        
        private String executableExt;
        private String staticLibExt;
        private String dynamicLibExt;
        
        /**
         * @param executableExt
         * @param staticLibExt
         * @param dynamicLibExt
         */
        private OsType(String executableExt, String staticLibExt, String dynamicLibExt) {
            this.executableExt = executableExt;
            this.staticLibExt = staticLibExt;
            this.dynamicLibExt = dynamicLibExt;
        }
        
        /**
         * @return the executableExt
         */
        public String getExecutableExt() {
            return executableExt;
        }
        
        /**
         * @return the staticLibExt
         */
        public String getStaticLibExt() {
            return staticLibExt;
        }
        
        /**
         * @return the dynamicLibExt
         */
        public String getDynamicLibExt() {
            return dynamicLibExt;
        }
        
    }
    
    public static String getOperatingSystemName() {
        return System.getProperty("os.name", "unknown").toLowerCase();
    }
    
    public static boolean isWindows() {
        return getOperatingSystemName().contains("windows");
    }
    
    public static boolean isMacOS() {
        return getOperatingSystemName().contains("mac") || 
               getOperatingSystemName().contains("darwin");
    }
        
    public static boolean isLinux() {
        return getOperatingSystemName().contains("nux");
    }
    
    public static boolean isUnix() {
        return getOperatingSystemName().contains("unix") ||
               getOperatingSystemName().contains("solaris") || 
               getOperatingSystemName().contains("sunos");
    }
    
    public static OsType getOS() {
        if(isWindows()) return OsType.WINDOWS;
        if(isLinux())   return OsType.LINUX;
        if(isMacOS())   return OsType.MACOS;
        
        return OsType.OTHER;
    }
    
}
