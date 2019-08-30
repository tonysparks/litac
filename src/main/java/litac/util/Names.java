/*
 * see license.txt
 */
package litac.util;

import java.util.Collections;
import java.util.List;

import litac.ast.TypeSpec;
import litac.ast.TypeSpec.NameTypeSpec;
import litac.ast.TypeSpec.TypeSpecKind;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;

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
        if(TypeInfo.isAggregate(type)) {
            return escapeName(type.getName());
        }
        
        if(TypeInfo.isFunc(type)) {
            if(type.isKind(TypeKind.Func)) {
                FuncTypeInfo funcInfo = type.as();
                return escapeName(funcInfo.getMethodName());    
            }
            
            return escapeName(type.getName());
        }
        
        return type.getName();
    }
    
    public static String baseTypeName(String name) {
        int index = name.indexOf('<');
        if(index < 0) {
            return name;
        }
        
        return name.substring(0, index);
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
