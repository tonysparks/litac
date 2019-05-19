/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.Decl.*;
import litac.checker.TypeInfo.*;
import litac.util.Tuple;

/**
 * @author Tony
 *
 */
public class Generics {
    
    /**
     * Creates a resolved {@link TypeInfo} if one doesn't exist and if the supplied type has generics
     * 
     * @param module
     * @param type
     * @param genericArgs
     * @return the new {@link TypeInfo}
     */
    public static TypeInfo createFromGenericTypeInfo(Module module, TypeInfo type, List<TypeInfo> genericArgs) {
        if(genericArgs.isEmpty() || type.isPrimitive()) {
            return type;
        }
        
        String genericImplTypeName = newDeclGenericName(type, genericArgs);
        TypeInfo resultType = module.getType(genericImplTypeName);
        if(resultType != null) {
            return resultType;
        }
                
        switch(type.getKind()) {
            case Struct: {
                StructTypeInfo structInfo = type.as();
                if(!structInfo.hasGenerics()) {
                    return type;
                }
                
                return createStructTypeInfo(module, genericImplTypeName, structInfo, genericArgs);
            }
            case Union: {
                UnionTypeInfo unionInfo = type.as();
                if(!unionInfo.hasGenerics()) {
                    return type;
                }
                
                return createUnionTypeInfo(module, genericImplTypeName, unionInfo, genericArgs);
            }
            case Func: {
                FuncTypeInfo funcInfo = type.as();
                if(!funcInfo.hasGenerics()) {
                    return type;
                }
                
                return createFuncTypeInfo(module, genericImplTypeName, funcInfo, genericArgs);
            }
            default:
                return type;
        }        
    }
    
    private static TypeInfo createStructTypeInfo(Module module, String newStructName, StructTypeInfo structInfo, List<TypeInfo> genericArgs) {
        
        List<FieldInfo> newFieldInfos = new ArrayList<>(structInfo.fieldInfos.size());
        for(FieldInfo field : structInfo.fieldInfos) {
            switch(field.type.getKind()) {
                case Struct:
                case Union: {
                    field.type = createFromGenericTypeInfo(module, field.type, genericArgs);
                    break;
                }
                default: {
                    for(int i = 0; i < structInfo.genericParams.size(); i++) {
                        GenericParam p = structInfo.genericParams.get(i);
                        if(p.name.equals(field.type.getName())) {
                            TypeInfo argInfo = createFromGenericTypeInfo(module, genericArgs.get(i), genericArgs);
                            field = new FieldInfo(argInfo, field.name, p.name);
                            break;
                        }
                    }
                }
            }
            newFieldInfos.add(field);
        }
        
        StructTypeInfo newStructInfo = new StructTypeInfo(newStructName,
                                                          Collections.emptyList(),
                                                          newFieldInfos, 
                                                          structInfo.flags);
        
        newStructInfo.sym = structInfo.sym;
        
        StructDecl decl = createNewStructDecl(module, 
                                              newStructInfo, 
                                              buildReplacements(module, structInfo, genericArgs));
        module.declareStruct(decl, newStructName, newStructInfo);
        module.addGenericType(decl);
        
        return newStructInfo;
    }
    
    private static TypeInfo createUnionTypeInfo(Module module, String newUnionName, UnionTypeInfo unionInfo, List<TypeInfo> genericArgs) {        
        List<FieldInfo> newFieldInfos = new ArrayList<>(unionInfo.fieldInfos.size());
        for(FieldInfo field : unionInfo.fieldInfos) {
            switch(field.type.getKind()) {
                case Struct:
                case Union: {
                    field.type = createFromGenericTypeInfo(module, field.type, genericArgs);
                    break;
                }
                default: {
                    for(int i = 0; i < unionInfo.genericParams.size(); i++) {
                        GenericParam p = unionInfo.genericParams.get(i);
                        if(p.name.equals(field.type.getName())) {
                            TypeInfo argInfo = createFromGenericTypeInfo(module, genericArgs.get(i), genericArgs);
                            field = new FieldInfo(argInfo, field.name, p.name);
                            break;
                        }
                    } 
                }
            }
            newFieldInfos.add(field);
        }
        
        UnionTypeInfo newUnionInfo = new UnionTypeInfo(newUnionName,
                                                       Collections.emptyList(),
                                                       newFieldInfos, 
                                                       unionInfo.flags);
        
        newUnionInfo.sym = unionInfo.sym;
        
        UnionDecl decl = createNewUnionDecl(module, 
                                            newUnionInfo, 
                                            buildReplacements(module, unionInfo, genericArgs));
        module.declareUnion(decl, newUnionName, newUnionInfo);
        module.addGenericType(decl);
        
        return newUnionInfo;
    }
    
    private static StructDecl createNewStructDecl(Module module, StructTypeInfo structInfo, List<Tuple<String, TypeInfo>> replacements) {
        StructDecl decl = structInfo.sym.decl.copy();
        decl.name = structInfo.name;
        decl.type = structInfo;
        
        TypeReplacerNodeVisitor replacer = new TypeReplacerNodeVisitor(structInfo.fieldInfos, replacements);
        decl.visit(replacer);
        
        return decl;
    }
    
    private static UnionDecl createNewUnionDecl(Module module, UnionTypeInfo unionInfo, List<Tuple<String, TypeInfo>> replacements) {
        UnionDecl decl = unionInfo.sym.decl.copy();
        decl.name = unionInfo.name;
        decl.type = unionInfo;
        
        TypeReplacerNodeVisitor replacer = new TypeReplacerNodeVisitor(unionInfo.fieldInfos, replacements);
        decl.visit(replacer);
        
        return decl;
    }
    
    private static FuncDecl createNewFuncDecl(Module module, FuncTypeInfo funcInfo, List<Tuple<String, TypeInfo>> replacements) {
        FuncDecl decl = funcInfo.sym.decl.copy();
        decl.name = funcInfo.name;
        decl.type = funcInfo;
        decl.returnType = funcInfo.returnType;
        decl.params.params = funcInfo.parameterDecls;
        
        TypeReplacerNodeVisitor replacer = new TypeReplacerNodeVisitor(Collections.emptyList(), replacements);
        decl.visit(replacer);
        
        return decl;
    }
    
    private static TypeInfo createFuncTypeInfo(Module module, String newFuncName, FuncTypeInfo funcInfo, List<TypeInfo> genericArgs) {
        
        List<ParameterDecl> newFuncParams = new ArrayList<>();
        
        for(ParameterDecl paramDecl : funcInfo.parameterDecls) {
            for(int i = 0; i < funcInfo.genericParams.size(); i++) {
                GenericParam p = funcInfo.genericParams.get(i);
                if(p.name.equals(paramDecl.type.getName())) {
                    // TODO: verify correct number of generic args/params
                    TypeInfo argInfo = genericArgs.get(i);
                    paramDecl = new ParameterDecl(argInfo, paramDecl.name);
                    break;
                }
            }    
            
            newFuncParams.add(paramDecl);
        }
        
        TypeInfo newReturnType = funcInfo.returnType;
        for(int i = 0; i < funcInfo.genericParams.size(); i++) {
            GenericParam p = funcInfo.genericParams.get(i);
            if(p.name.equals(funcInfo.returnType.getName())) {
                // TODO: verify correct number of generic args/params
                TypeInfo argInfo = genericArgs.get(i);
                newReturnType = argInfo;
                break;
            }
        }
         
        FuncTypeInfo newFuncInfo = new FuncTypeInfo(newFuncName, 
                                                    newReturnType, 
                                                    newFuncParams, 
                                                    funcInfo.isVararg, 
                                                    Collections.emptyList());
        newFuncInfo.sym = funcInfo.sym;
        FuncDecl decl = createNewFuncDecl(module, 
                                          newFuncInfo, 
                                          buildReplacements(module, funcInfo, genericArgs));
        module.declareFunc(decl, newFuncName, newFuncInfo);
        module.addGenericType(decl);
        
        return newFuncInfo;
    }
    
    private static String newDeclGenericName(TypeInfo type, List<TypeInfo> genericArgs) {
        StringBuilder newName = new StringBuilder(type.getName());
        
        for(int i = 0; i < genericArgs.size(); i++) {
            TypeInfo argInfo = genericArgs.get(i);
            newName.append("").append(argInfo.getName().replace("::", ""));
        }
        
        return newName.toString();
    }
    
    private static List<Tuple<String, TypeInfo>> buildReplacements(Module module, GenericTypeInfo type, List<TypeInfo> genericArgs) {
        List<Tuple<String, TypeInfo>> replacements = new ArrayList<>();
        for(int i = 0; i < genericArgs.size(); i++) {
            TypeInfo argInfo = createFromGenericTypeInfo(module, genericArgs.get(i), genericArgs);
            replacements.add(new Tuple<>(type.genericParams.get(i).name, argInfo));
        }
        
        return replacements;
    }
}
