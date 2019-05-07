/*
 * see license.txt
 */
package litac.compiler.c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import litac.checker.TypeInfo;
import litac.compiler.CompilationUnit;
import litac.util.Names;

/**
 * @author Tony
 *
 */
public class NameCache {

    // Name => BackendName
    private Map<String, String> backendNames;
    
    // Name => Lita Name 
//    private Map<String, String> litaNames;
    
    // Module Alias => Actual Module name
//    private Map<String, String> moduleAliases;
    
    // CurrentModule => import alias => actual module name
    private Map<String, Map<String, String>> lookup;
    
    private Set<String> foreignIdentifiers;
    
    // Key: CurrentModule, Value: { AliasName => RealName }
    // Example:
    // CurrentModule         Alias
    //    default     =>   std => core
//    private Map<String, Map<String, String>> aliases;
    
    // BackendName => TypeInfo
    private Map<String, TypeInfo> types;
    
    public NameCache() {
        this.backendNames = new HashMap<>();
//        this.litaNames = new HashMap<>();
//        this.moduleAliases = new HashMap<>();
        
        this.lookup = new HashMap<>();
        this.foreignIdentifiers = new HashSet<>();
        this.types = new HashMap<>();
        
        addPrimitive(TypeInfo.I8_TYPE);
        addPrimitive(TypeInfo.I16_TYPE);
        addPrimitive(TypeInfo.I32_TYPE);
        addPrimitive(TypeInfo.I64_TYPE);
        addPrimitive(TypeInfo.I128_TYPE);
        
        addPrimitive(TypeInfo.U8_TYPE);
        addPrimitive(TypeInfo.U16_TYPE);
        addPrimitive(TypeInfo.U32_TYPE);
        addPrimitive(TypeInfo.U64_TYPE);
        addPrimitive(TypeInfo.U128_TYPE);
        
        addPrimitive(TypeInfo.F32_TYPE);
        addPrimitive(TypeInfo.F64_TYPE);
        
        addPrimitive(TypeInfo.BOOL_TYPE);
        
        addPrimitive(TypeInfo.CHAR_TYPE);
        
        addPrimitive(TypeInfo.NULL_TYPE);
        
        addPrimitive(TypeInfo.VOID_TYPE);
    }

//    public String getLitaName(String normalName) {
//        return this.litaNames.get(normalName);
//    }
    
//    public String getBackendName(String litaName) {
//        String moduleAlias = Names.moduleFrom(litaName);
//        String normalName  = Names.identifierFrom(litaName);
//        String moduleName = this.moduleAliases.get(moduleAlias);
//        
//        String normalizedLitaName = Names.litaName(moduleName, normalName);
//        return this.backendNames.get(normalizedLitaName);
//    }
    
    public String getBackendName(String currentModule, String litaName) {                   
        Map<String, String> aliases = this.lookup.get(currentModule);
        if(aliases == null) {
            //throw new NullPointerException("Invalid module '" + currentModule + "'");
//            return null;
            String moduleAlias = Names.moduleFrom(litaName);
            if(moduleAlias == null || moduleAlias.equals("")) {
                String thisModuleName = Names.backendName(currentModule, litaName);
                if(this.backendNames.containsKey(thisModuleName)) {
                    return this.backendNames.get(thisModuleName);
                }
                
                return this.backendNames.get(litaName);
            }
        }

        String moduleAlias = Names.moduleFrom(litaName);        
        String actualModuleName = aliases.get(moduleAlias);
        
        String normalName  = Names.identifierFrom(litaName);
        
        String backendName = Names.backendName(actualModuleName, normalName);
        return this.backendNames.get(backendName);
        
//        String normalizedLitaName = Names.litaName(actualModuleName, normalName);
//        return this.backendNames.get(Names.backendName(normalizedLitaName)); 
    }
    
    private void addPrimitive(TypeInfo type) {
        add(new NameCoord("", "", ""), type.getName(), false, true, type);
    }
    
    public String getActualModuleName(String currentModule, String moduleAlias) {
        Map<String, String> aliases = this.lookup.get(currentModule);
        if(aliases == null) {
            //throw new NullPointerException("Invalid module '" + currentModule + "'");
            return null;
        }
        
        return aliases.get(moduleAlias);
    }
    
    public void add(NameCoord coord, String normalName, boolean isForeign, boolean isType, TypeInfo type) {
        if(!this.lookup.containsKey(coord.parentModuleName)) {
            this.lookup.put(coord.parentModuleName, new HashMap<>());
        }
         
        Map<String, String> aliases = this.lookup.get(coord.parentModuleName);
        aliases.put(coord.moduleAlias, coord.moduleName);
        aliases.put(coord.moduleName, coord.moduleName);
        
//        this.moduleAliases.put(coord.moduleAlias, coord.moduleName);
        
//        String litaName = Names.litaName(coord.moduleName, normalName);
//        this.litaNames.put(normalName, litaName);
        
        String backendName = Names.backendName(coord.moduleName, normalName);
        this.backendNames.put(backendName, backendName);
        
        if(isForeign) {
            this.foreignIdentifiers.add(backendName);            
        }
        
        if(isType) {
            this.types.put(backendName, type);
        }
    }
    
//    public void add(NameCoord coord, String normalName, boolean isForeign, boolean isType, TypeInfo type) {
//        if(!this.lookup.containsKey(coord.parentModuleName)) {
//            this.lookup.put(coord.parentModuleName, new HashMap<>());
//        }
//        
//        Map<String, String> aliases = this.lookup.get(coord.parentModuleName);
//        aliases.put(coord.moduleAlias, coord.moduleName);
//        
//        this.moduleAliases.put(coord.moduleAlias, coord.moduleName);
//        
//        String litaName = Names.litaName(coord.moduleName, normalName);
//        this.litaNames.put(normalName, litaName);
//        
//        String backendName = Names.backendName(litaName);
//        this.backendNames.put(normalName, backendName);
//        
//        if(isForeign) {
//            this.foreignIdentifiers.add(backendName);            
//        }
//        
//        if(isType) {
//            this.types.put(backendName, type);
//        }
//    }
    
    public boolean isForeign(String backendName) {
        return this.foreignIdentifiers.contains(backendName);
    }
    
    public boolean isForeign(String currentModule, String litaName) {
        String moduleAlias = Names.moduleFrom(litaName);
        String name = Names.identifierFrom(litaName);
        
        String actualModuleName = getActualModuleName(currentModule, moduleAlias);
        
        litaName = Names.litaName(actualModuleName, name);
        return this.foreignIdentifiers.contains(Names.backendName(litaName));
    }
    
    public Map<String, TypeInfo> getTypes() {
        return types;
    }
        
    public static NameCache build(CompilationUnit unit) {
        NameCache names = new NameCache();
        NameCacheNodeVisitor visitor = new NameCacheNodeVisitor(names, unit);
        unit.getMain().visit(visitor);
        
        return names;
    }
}
