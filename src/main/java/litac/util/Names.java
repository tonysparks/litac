/*
 * see license.txt
 */
package litac.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import litac.LitaOptions;
import litac.ast.TypeSpec;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;

/**
 * @author Tony
 *
 */
public class Names {

    public static String getModuleName(String fileName) {
        int startIndex = fileName.lastIndexOf("/") + 1;
        int endIndex = fileName.lastIndexOf(".");
        
        if(startIndex < 0) {
            startIndex = 0;
        }
        
        if(endIndex < 0) {
            endIndex = fileName.length();
        }
        
        return fileName.substring(startIndex, endIndex);
    }
    
    public static String getPackagePath(File libDir, File srcDir, File moduleFile) {        
        Path path = (moduleFile.isFile() ? moduleFile.getParentFile().toPath() : moduleFile.toPath()).normalize();
        
        String packages = "";
        if(path.startsWith(libDir.toPath())) {
            packages = libDir.toPath().relativize(path).toString();
        }
        else if(srcDir != null && path.startsWith(srcDir.toPath())) {
            packages = srcDir.toPath().relativize(path).toString();
        }
        
        if(packages.length() > 0) {
            packages += "/";
        }
        
        return packages;
    }
    
    public static File getModuleFile(File parentModuleFile, LitaOptions options, String moduleName) {
        // Find the full module path based on the module importing this module
        File parentDir = OS.canonicalize(parentModuleFile.getParentFile());        
        File moduleFile = options.findModule(parentDir, moduleName + ".lita");
        
        return moduleFile;
    }
    
    public static String getRelativeModulePath(File parentModuleFile, LitaOptions options, File srcDir, String moduleName) {        
        File moduleFile = getModuleFile(parentModuleFile, options, moduleName);
        
        // now that we have the full module path, we can make it relative to either the root source
        // directory or it is a standard library module
        return getRelativeModulePath(options.libDir, srcDir, moduleFile);
    }
    
    public static String getRelativeModulePath(File libDir, File srcDir, File moduleFile) {                
        // now that we have the full module path, we can make it relative to either the root source
        // directory or it is a standard library module
        String packages = getPackagePath(libDir, srcDir, moduleFile);
        return packages + Names.getModuleName(moduleFile.getName());
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
        if(TypeInfo.isAggregate(type)) {
            return escapeName(type.getName());
            //return type.getName() + "_" + type.getTypeId();
        }
        
        if(TypeInfo.isFunc(type)) {
            if(type.isKind(TypeKind.Func)) {
                FuncTypeInfo funcInfo = type.as();
                return escapeName(funcInfo.getMethodName());
                //return funcInfo.getMethodName() + "_" + type.getTypeId();
            }
            
            return escapeName(type.getName());
            //return type.getName() + "_" + type.getTypeId();
        }
        
        return type.getName();
    }
    
    public static String baseTypeName(String name) {
        name = stripModule(name);
        name = stripGenerics(name);
        return name;
    }
    
    private static String stripGenerics(String name) {
        int index = name.indexOf('<');
        if(index < 0) {
            return name;
        }
        
        return name.substring(0, index);
    }
    
    private static String stripModule(String name) {
        int index = name.indexOf("::");
        if(index < 0) {
            return name;
        }
        
        return name.substring(index + 2, name.length());
    }
    
    public static String escapeName(String name) {
        StringBuilder sb = new StringBuilder(name);
        for(int i = 0; i < sb.length();) {
            char c = sb.charAt(i);
            switch(c) {
                case '*': sb.replace(i, i+1, "_ptr_");i+=6; break;
                case '<': sb.replace(i, i+1, "_cb_"); i+=4; break;
                case '>': sb.replace(i, i+1, "_ce_"); i+=4; break;
                case '[': sb.replace(i, i+1, "_bb_"); i+=4; break;
                case ']': sb.replace(i, i+1, "_be_"); i+=4; break;
                case '(': sb.replace(i, i+1, "_pb_"); i+=4; break;
                case ')': sb.replace(i, i+1, "_pe_"); i+=4; break;
                case ',': sb.replace(i, i+1, "_c_");  i+=3; break;
                case ' ': sb.replace(i, i+1, "_");    i+=1; break;
                case ':': {
                    int n = i + 1;
                    if(n < sb.length()) {
                        char nc = sb.charAt(n);
                        if(nc == ':') {
                            sb.replace(i, i+1, "__");
                            i+=2;
                            break;        
                        }
                    }
                    
                    sb.replace(i, i+1, "_r_");
                    i+=3;
                    break;
                }
                default: {
                    i++;
                    break;
                }
            }
        }
        return sb.toString();
        /*return name
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
                .replace(" ", "_");*/
        /*
        name = replace(name, "::", "__");
        name = replace(name, "*", "_ptr_");
        name = replace(name, "<", "_cb_");
        name = replace(name, ">", "_ce_");
        name = replace(name, "[", "_bb_");
        name = replace(name, "]", "_be_");
        name = replace(name, "(", "_pb_");
        name = replace(name, ")", "_pe_");
        name = replace(name, ":", "_r_");
        name = replace(name, ",", "_c_");
        name = replace(name, " ", "_");
        return name;*/
    }
    
    public static String replace(String source, String os, String ns) {
        if (source == null) {
            return null;
        }
        int i = 0;
        if ((i = source.indexOf(os, i)) >= 0) {
            char[] sourceArray = source.toCharArray();
            char[] nsArray = ns.toCharArray();
            int oLength = os.length();
            StringBuilder buf = new StringBuilder(sourceArray.length);
            buf.append(sourceArray, 0, i).append(nsArray);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = source.indexOf(os, i)) > 0) {
                buf.append(sourceArray, j, i - j).append(nsArray);
                i += oLength;
                j = i;
            }
            buf.append(sourceArray, j, sourceArray.length - j);
            source = buf.toString();
            buf.setLength(0);
        }
        return source;
    }
    
    public static String methodName(TypeInfo recvInfo, String funcName) {
        if(recvInfo == null) {
            return funcName;
        }
        
        String recvName = null;
        
        //recvInfo = recvInfo.getResolvedType();
        switch(recvInfo.getKind()) {
            case Ptr: {
                PtrTypeInfo ptrInfo = recvInfo.as();
                recvName = ptrInfo.getBaseType().getName();
                break;
            }
            case Array: {
                ArrayTypeInfo arrayInfo = recvInfo.as();
                recvName = arrayInfo.getBaseType().getName();
                break;
            }
            case Str: {
                recvName = "String";                    
                break;
            }
            case Const: {
                ConstTypeInfo constInfo = recvInfo.as();
                recvName = constInfo.getBaseType().getName();
                break;
            }
            default: {
                recvName = recvInfo.getName();
                break;
            }
        }
        
        recvName = baseTypeName(recvName);
        return String.format("%s_%s", recvName, funcName);
    }
    
    public static String getBaseName(TypeSpec typeSpec) {
        switch(typeSpec.kind) {
            case PTR: {
                PtrTypeSpec ptrInfo = typeSpec.as();
                return getBaseName(ptrInfo.base);
            }
            case ARRAY: {
                ArrayTypeSpec arrayInfo = typeSpec.as();
                return getBaseName(arrayInfo.base);
            }
            case CONST: {
                ConstTypeSpec constInfo = typeSpec.as();
                return getBaseName(constInfo.base);
            }
            default: {
                return typeSpec.toString();
            }
        }
    }
    
    public static String methodName(TypeSpec recvInfo, String funcName) {
        if(recvInfo == null) {
            return funcName;
        }
        
        String recvName = getBaseName(recvInfo);        
        recvName = baseTypeName(recvName);
        return String.format("%s_%s", recvName, funcName);
    }
    
    public static String genericsName(TypeSpec type) {        
        if(type.kind != TypeSpecKind.NAME) {
            return type.toString();
        }
        
        NameTypeSpec name = type.as();
        StringBuilder newName = new StringBuilder(name.name);
        
        List<TypeSpec> genericArgs = name.hasGenericArgs() ? name.genericArgs : Collections.emptyList();
        
        if(!genericArgs.isEmpty()) {
            newName.append("<");
        }
        for(int i = 0; i < genericArgs.size(); i++) {
            if(i > 0) newName.append(",");
            
            TypeSpec argInfo = genericArgs.get(i);            
            newName.append(genericsName(argInfo));
        }
        
        if(!genericArgs.isEmpty()) {
            newName.append(">");
        }
        
        return newName.toString();
    }
}
