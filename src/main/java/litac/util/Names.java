/*
 * see license.txt
 */
package litac.util;

import litac.checker.TypeInfo;

/**
 * @author Tony
 *
 */
public class Names {

    public static String getModuleName(String fileName) {
        int startIndex = fileName.lastIndexOf("/");
        int endIndex = fileName.lastIndexOf(".");
        
        if(startIndex < 0) {
            startIndex = 0;
        }
        
        if(endIndex < 0) {
            endIndex = fileName.length();
        }
        
        return fileName.substring(startIndex, endIndex);
    }
    
    public static final String identifierFrom(String name) {
        if(name.contains(":")) {
            return name.substring(name.lastIndexOf(":") + 1);
        }
        return name;
    }
    
    public static final String backendName(String module, String name) {        
        if(module != null && !module.isEmpty()) {
            return module + "__" + name;
        }
        return name;
    }
    
    public static final String backendName(String name) {        
        return name.replace("::", "__");
    }
    
    public static final String litaName(String module, String name) {
        if(module != null && !module.isEmpty()) {
            return module + "::" + name;
        }
        return name;
    }
    
    public static String moduleFrom(String name) {
        if(name.contains(":")) {
            return name.substring(0, name.indexOf(":"));
        }
        return "";
    }
    
    public static String moduleFromBackendName(String name) {
        if(name.contains("__")) {
            return name.substring(0, name.indexOf("__"));
        }
        return "";
    }
    
    public static String escapeName(TypeInfo type) {
        if(TypeInfo.isAggregate(type) || TypeInfo.isFunc(type)) {
            return escapeName(type.getName());
        }
        
        return type.getName();
    }
    
    public static String escapeName(String name) {
        return name
                .replace("::", "__")
                .replace("*", "_ptr_")
                .replace("<", "_cb_")
                .replace(">", "_ce_")
                .replace("[", "_bb_")
                .replace("]", "_be_")
                .replace("(", "_pb_")
                .replace(")", "_pe_")
                .replace(":", "_r_")
                .replace(",", "_c_")
                .replace(" ", "_");
    }
}
