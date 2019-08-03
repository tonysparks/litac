/*
 * see license.txt
 */
package litac.generics;

import java.util.*;

import litac.ast.Decl.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.*;

/**
 * Handles creating new {@link TypeInfo}s based on generic types with generic arguments.
 * As an example:
 * 
 *   Array<T> is represented by IdentityTypeInfo with genericParam 'T', when used
 *   in an expression, such as Array<i32>, the IdentityTypeInfo gets converted to a 
 *   StructTypeInfo named Arrayi32 with i32 type replaced where T was.
 * 
 * @author Tony
 *
 */
public class Generics {
    
    private static boolean isGenericParam(TypeInfo type, List<TypeInfo> genericArgs) {
        if(!(type instanceof GenericTypeInfo)) {
            return false;
        }
        
        GenericTypeInfo genInfo = type.as();
        
        for(TypeInfo arg: genericArgs) {
            if(genInfo.genericParams.stream().anyMatch(p -> p.name.equals(arg.getName()))) {                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Creates a resolved {@link TypeInfo} if one doesn't exist and if the supplied type has generics
     * 
     * @param module
     * @param type
     * @param genericArgs
     * @return the new {@link TypeInfo}
     */
    public static TypeInfo createFromGenericTypeInfo(Module module, TypeInfo type, List<TypeInfo> genericArgs) {
        if(genericArgs.isEmpty() || type.isPrimitive() || !type.hasGenerics() || isGenericParam(type, genericArgs)) {
            return type;
        }
                
        type = normalizeType(module, type);
        
        // All generics are defined in the root module
        // so we want to ensure we are comparing types from the root module
        Module root = module.getRoot();
        
        
        String genericImplTypeName = newDeclGenericName(type, genericArgs);
        TypeInfo resultType = root.getType(genericImplTypeName);
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
    
    public static TypeInfo createGenericTypeInfo(Module module, TypeInfo type, List<GenericParam> genericParams, List<TypeInfo> genericArgs) {
        
        type = normalizeType(module, type);        
        
        switch(type.getKind()) {
            case Func: 
            case Struct:
            case Union: {
                
                // Only use the Generic Args that are needed
                List<TypeInfo> narrowedGenericArgs = genericArgs;
                if(type.hasGenericArgs()) {
                    narrowedGenericArgs = new ArrayList<>();
                    for(int i = 0; i < genericParams.size(); i++) {
                        GenericParam parent = genericParams.get(i);
                        
                        // First check and see if there are Generic Parameters that should be substituted with
                        // the parents generic arguments
                        if(type.getGenericArgs().stream().anyMatch(p -> parent.name.equals(p.getName()))) {
                            if(i < genericArgs.size()) {
                                narrowedGenericArgs.add(genericArgs.get(i));
                            }
                        }
                        else {
                            
                            // Otherwise, this type might already have generic definitions itself; if 
                            // so, populate it with its supplied generic arguments
                            if(type.isResolved() && type.hasGenerics()) {
                                GenericTypeInfo genInfo = type.as();
                                for(int j = 0; j < genInfo.genericParams.size(); j++) {
                                    GenericParam genParam = genInfo.genericParams.get(j);
                                    if(genParam.name.equals(parent.name)) {
                                        narrowedGenericArgs.add(type.getGenericArgs().get(j));
                                    }                                    
                                }
                            }
                        }
                    }                
                }
                
                return createFromGenericTypeInfo(module, type, narrowedGenericArgs);                
            }                
            case Ptr: {
                PtrTypeInfo ptrInfo = (type = type.copy()).as();
                ptrInfo.ptrOf = createGenericTypeInfo(module, ptrInfo.ptrOf, genericParams, genericArgs);
                return type;                
            }
            case Const: {
                ConstTypeInfo constInfo = (type = type.copy()).as();
                constInfo.constOf = createGenericTypeInfo(module, constInfo.constOf, genericParams, genericArgs);
                return type;                
            }
            case Array: {
                ArrayTypeInfo arrayInfo = (type = type.copy()).as();
                arrayInfo.arrayOf = createGenericTypeInfo(module, arrayInfo.arrayOf, genericParams, genericArgs);
                return type;
            }
            case FuncPtr: {
                FuncPtrTypeInfo funcPtr = (type = type.copy()).as();
                funcPtr.returnType = createGenericTypeInfo(module, funcPtr.returnType, genericParams, genericArgs);
                for(int i = 0; i < funcPtr.params.size(); i++) {
                    TypeInfo p = funcPtr.params.get(i);
                    funcPtr.params.set(i, createGenericTypeInfo(module, p, genericParams, genericArgs));
                }
                funcPtr.genericParams.clear();
                type.clearGenericArgs();
                return type;
            }
            case Identifier: {
                IdentifierTypeInfo idTypeInfo = (type = type.copy()).as();
                
                // Check and see if the identifier is another generic type with generic arguments,
                // if it has them, swap them out with the corresponding generics on the parent type
                if(!idTypeInfo.isResolved() && idTypeInfo.hasGenericArgs()) {
                    
                    for(int index = 0; index < idTypeInfo.genericArgs.size(); index++)  {
                        TypeInfo genericArg = idTypeInfo.genericArgs.get(index);
                        for(int i = 0; i < genericParams.size(); i++) {
                            GenericParam p = genericParams.get(i);
                            if(p.name.equals(genericArg.getName())) {
                                TypeInfo newArg = createGenericTypeInfo(module, genericArgs.get(i), genericParams, genericArgs);
                                idTypeInfo.genericArgs.set(index, newArg);
                            }
                        }
                    }
                    
                    TypeInfo resolvedType = module.getType(idTypeInfo.getName());
                    idTypeInfo.resolve(module, resolvedType, true);
                }
                else {
                    for(int i = 0; i < genericParams.size(); i++) {
                        GenericParam p = genericParams.get(i);
                        if(p.name.equals(type.getName())) {
                            if(i >= genericArgs.size()) {
                                break;
                            }
                            
                            TypeInfo genericArg = genericArgs.get(i);
                            if(genericArg.isGenericParam() || genericArg.getName().equals(p.name)) {
                                continue;
                            }
                            
                            // Don't get caught in a recursive loop, ensure the target type is resolved
                            // (it could be unresolved if it's a Generic type itself)
//                            if(!genericArg.isResolved() && !genericArg.isGenericParam()) {
//                                // TODO: Get the stmt where this was defined!!
//                                module.getPhaseResult().addError(null, "'%s' is an unknown type", genericArg.getName());
//                                return type;
//                            }
                            
                            TypeInfo genType = createGenericTypeInfo(module, genericArg, genericParams, genericArgs);
                            idTypeInfo.resolve(module, genType, true);
                            return idTypeInfo.getResolvedType();
                        }
                    } 
                }
                
                return type;
            }
            default: {
                for(int i = 0; i < genericParams.size(); i++) {
                    GenericParam p = genericParams.get(i);
                    if(p.name.equals(type.getName())) {
                        return createGenericTypeInfo(module, genericArgs.get(i), genericParams, genericArgs);
                    }
                }
            }
        }
        
        return type;
    }
    
    private static TypeInfo normalizeType(Module module, TypeInfo type) {
        // If this type is aliasing the real type, make sure we use
        // the real type and not the aliased for generic creation.  
        // This helps with the type checker as it doesn't have to look
        // up aliased types
        
        // Function Pointers are type declarations themselves, so
        // it doesn't make sense to get the root function pointer declaration
        if(type.isResolved() && type.isKind(TypeKind.FuncPtr)) {
            return type;
        }
        
        TypeInfo realType = module.getAliasedType(type.getName());
        if(realType != null) {
            if(realType instanceof IdentifierTypeInfo &&
               type instanceof IdentifierTypeInfo) { 
                realType = realType.copy();
                
                IdentifierTypeInfo realIdInfo = (IdentifierTypeInfo)realType;
                IdentifierTypeInfo typeIdInfo = (IdentifierTypeInfo)type;
                
                realIdInfo.genericArgs = new ArrayList<>(typeIdInfo.genericArgs);
            }
            
            type = realType;
        }
        
        return type;
    }
    
    private static List<FieldInfo> createNewFieldInfos(Module module, AggregateTypeInfo aggInfo, List<TypeInfo> genericArgs) {
        List<FieldInfo> newFieldInfos = new ArrayList<>(aggInfo.fieldInfos.size());
        for(FieldInfo field : aggInfo.fieldInfos) {            
            TypeInfo newType = createGenericTypeInfo(module, field.type, aggInfo.genericParams, genericArgs);
            FieldInfo newField = new FieldInfo(newType, field.name, field.attributes, field.genericArg);            
            newFieldInfos.add(newField);
        }
        
        return newFieldInfos;
    }
    
    private static TypeInfo createStructTypeInfo(Module module, String newStructName, StructTypeInfo structInfo, List<TypeInfo> genericArgs) {
        
        List<FieldInfo> newFieldInfos = createNewFieldInfos(module, structInfo, genericArgs);        
        StructTypeInfo newStructInfo = new StructTypeInfo(newStructName,
                                                          Collections.emptyList(),
                                                          newFieldInfos, 
                                                          structInfo.flags);
        
        newStructInfo.sym = structInfo.sym;
        
        StructDecl decl = createNewStructDecl(module, 
                                              newStructInfo,
                                              structInfo.genericParams,
                                              genericArgs);

        Module root = module.getRoot();
        root.declareStruct(decl, newStructName, newStructInfo);
        root.addGenericType(structInfo.sym.declared, module, decl);
        
        GenericsNodeVisitor replacer = new GenericsNodeVisitor(newStructInfo.fieldInfos, module, structInfo.genericParams, genericArgs);
        decl.visit(replacer);
        
        return newStructInfo;
    }
    
    private static TypeInfo createUnionTypeInfo(Module module, String newUnionName, UnionTypeInfo unionInfo, List<TypeInfo> genericArgs) {        
        
        List<FieldInfo> newFieldInfos = createNewFieldInfos(module, unionInfo, genericArgs);        
        UnionTypeInfo newUnionInfo = new UnionTypeInfo(newUnionName,
                                                       Collections.emptyList(),
                                                       newFieldInfos, 
                                                       unionInfo.flags);
        
        newUnionInfo.sym = unionInfo.sym;
        
        UnionDecl decl = createNewUnionDecl(module, 
                                            newUnionInfo,
                                            unionInfo.genericParams,
                                            genericArgs);

        Module root = module.getRoot();        
        root.declareUnion(decl, newUnionName, newUnionInfo);
        root.addGenericType(unionInfo.sym.declared, module, decl);
        
        GenericsNodeVisitor replacer = new GenericsNodeVisitor(newUnionInfo.fieldInfos, module, unionInfo.genericParams, genericArgs);
        decl.visit(replacer);
        
        return newUnionInfo;
    }
    
    
    private static TypeInfo createFuncTypeInfo(Module module, String newFuncName, FuncTypeInfo funcInfo, List<TypeInfo> genericArgs) {
        
        List<ParameterDecl> newFuncParams = new ArrayList<>();
        
        for(ParameterDecl paramDecl : funcInfo.parameterDecls) {            
            TypeInfo argInfo = createGenericTypeInfo(module, paramDecl.type, funcInfo.genericParams, genericArgs);
            ParameterDecl newParamDecl = new ParameterDecl(argInfo, paramDecl.name, paramDecl.defaultValue, paramDecl.attributes.modifiers);
            newParamDecl.sym = paramDecl.sym;
            
            newFuncParams.add(newParamDecl);
        }
         
        TypeInfo newReturnType = createGenericTypeInfo(module, funcInfo.returnType, funcInfo.genericParams, genericArgs);
        FuncTypeInfo newFuncInfo = new FuncTypeInfo(newFuncName, 
                                                    newReturnType, 
                                                    newFuncParams, 
                                                    funcInfo.flags, 
                                                    Collections.emptyList());
        newFuncInfo.sym = funcInfo.sym;
        FuncDecl decl = createNewFuncDecl(module, 
                                          newFuncInfo,
                                          funcInfo.genericParams,
                                          genericArgs);

        Module root = module.getRoot();
        root.declareFunc(decl, newFuncName, newFuncInfo);
        root.addGenericType(funcInfo.sym.declared, module, decl);
        
        GenericsNodeVisitor replacer = new GenericsNodeVisitor(Collections.emptyList(), module, funcInfo.genericParams, genericArgs);
        decl.visit(replacer);
        
        return newFuncInfo;
    }
    
    private static StructDecl createNewStructDecl(Module module, StructTypeInfo structInfo, List<GenericParam> genericParams, List<TypeInfo> genericArgs) { 
        StructDecl decl = structInfo.sym.decl.copy();
        decl.name = structInfo.name;
        decl.type = structInfo;
        decl.attributes = structInfo.sym.decl.attributes;
        
        return decl;
    }
    
    private static UnionDecl createNewUnionDecl(Module module, UnionTypeInfo unionInfo, List<GenericParam> genericParams, List<TypeInfo> genericArgs) {
        UnionDecl decl = unionInfo.sym.decl.copy();
        decl.name = unionInfo.name;
        decl.type = unionInfo;
        decl.attributes = unionInfo.sym.decl.attributes;
                
        return decl;
    }
    
    private static FuncDecl createNewFuncDecl(Module module, FuncTypeInfo funcInfo, List<GenericParam> genericParams, List<TypeInfo> genericArgs) {
        FuncDecl decl = funcInfo.sym.decl.copy();
        decl.name = funcInfo.name;
        decl.type = funcInfo;
        decl.returnType = funcInfo.returnType;
        decl.params.params = funcInfo.parameterDecls;
        decl.attributes = funcInfo.sym.decl.attributes;
        
        return decl;
    }

    
    private static String newDeclGenericName(TypeInfo type, List<TypeInfo> genericArgs) {
        String baseType = type.getName();
        if(type.isResolved() && !type.hasGenericArgs()) {
            baseType = type.getResolvedType().getName();
        }
        
        if(type.isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = type.as();
            if(funcInfo.isMethod()) {
                baseType = funcInfo.getMethodName();
            }
        }
        
        StringBuilder newName = new StringBuilder(baseType);
        
        if(!genericArgs.isEmpty()) {
            newName.append("<");
        }
        for(int i = 0; i < genericArgs.size(); i++) {
            if(i > 0) newName.append(",");
            
            TypeInfo argInfo = genericArgs.get(i);
            
            newName.append(newDeclGenericName(argInfo, argInfo.getGenericArgs()));
        }
        
        if(!genericArgs.isEmpty()) {
            newName.append(">");
        }
        
        return newName.toString();
    }
    
}
