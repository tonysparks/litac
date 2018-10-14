/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import litac.ast.Decl;
import litac.ast.Expr.IdentifierExpr;
import litac.ast.Stmt;
import litac.ast.TypeInfo;
import litac.ast.TypeInfo.FieldInfo;
import litac.ast.TypeInfo.IdentifierTypeInfo;
import litac.ast.TypeInfo.StructTypeInfo;
import litac.ast.TypeInfo.TypeKind;
import litac.checker.TypeResolverResult.TypeResolverError;

/**
 * @author Tony
 *
 */
public class TypeResolverScope {
    
    public static class Pending {
        public Stmt stmt;
        public TypeInfo objectField;
        public TypeInfo accessField;
        
        public Pending(Stmt stmt, TypeInfo objectField, TypeInfo accessField) {
            this.stmt = stmt;
            this.objectField = objectField;
            this.accessField = accessField;
        }
    }
    
    private Map<String, TypeInfo> resolvedTypes;
    // Key=>TypeName, Value=>List of TypeInfo's that need to be resolved
    private Map<String, List<TypeInfo>> unresolvedTypes;
    
    private TypeResolverScope parent;
    private TypeResolverResult result;
    private List<Pending> pendingResolvedTypes;
    
    public TypeResolverScope(TypeResolverResult result) {
        this(result, null);
    }
    
    public TypeResolverScope(TypeResolverResult result, TypeResolverScope parent) {
        this.result = result;
        this.parent = parent;
        
        this.resolvedTypes = new HashMap<>();
        this.unresolvedTypes = new HashMap<>();
        
        this.pendingResolvedTypes = new ArrayList<>();
    }
    
    public TypeResolverScope getParent() {
        return this.parent;
    }
    
    public void addPendingField(Stmt stmt, TypeInfo objectField, TypeInfo accessField) {
        this.pendingResolvedTypes.add(new Pending(stmt, objectField, accessField));
    }
    
    public void addUnresolvedType(TypeInfo type) {
        // this is not a type that needs resolution
        if(type.kind != TypeKind.Identifier) {
            return;
        }

        // determine if the type has already been resolved
        if(resolveTypeInfo(type)) {
            return;
        }
        
        // we'll need to resolve this type at some point
        if(!this.unresolvedTypes.containsKey(type.name)) {
            this.unresolvedTypes.put(type.name, new ArrayList<>());
        }
        
        this.unresolvedTypes.get(type.name).add(type);
    }
    
    private boolean resolveTypeInfo(TypeInfo type) {
        if(!isResolvedType(type.name)) {
            return false;
        }
        
        if(!type.isResolved()) {                
            IdentifierTypeInfo idType = type.as();
            idType.resolve(getResolvedType(type.name));  
            
            // resolved all unresolved??
            return true;
        }
        
        if(this.parent != null) {
            return this.parent.resolveTypeInfo(type);
        }
        
        return false;
    }
    
    private boolean isResolvedType(String typeName) {
        if(this.resolvedTypes.containsKey(typeName)) {
            return true;
        }
        
        if(this.parent != null) {
            return this.parent.isResolvedType(typeName);
        }
        
        return false;
    }
    
    private TypeInfo getResolvedType(String typeName) {
        TypeInfo type = this.resolvedTypes.get(typeName);
        if(type == null && this.parent != null) {
            return this.parent.getResolvedType(typeName);
        }
        
        return type;
    }
    
    public void addUnresolvedType(Stmt stmt, TypeInfo object, TypeInfo field) {
        if(object.isPrimitive()) {
            return;
        }
        
        addUnresolvedType(object);
        // addUnresolvedType(field);
        
        resolveFieldType(stmt, object, field);        
    }
    
    private void resolveFieldType(Stmt stmt, TypeInfo object, TypeInfo field) {
        switch(object.getKind()) {
            case Array:
                break;
            case Enum:
                break;
            case Func:
                break;
            case Ptr:
                break;
            case Str:
                break;
            case Struct: {
                StructTypeInfo structInfo = object.as();
                for(FieldInfo f : structInfo.fieldInfos) {
                    addUnresolvedType(f.type);
                    
                    if(f.name.equals(field.name)) {
                        addPendingField(stmt, f.type, field);
                        break;
                    }
                }
                
                // TODO: Report Error??
                //result.addError(stmt, message);
                break;
            }
            case Union:
                break;
            case Identifier: {
                IdentifierTypeInfo idType = object.as();
                if(idType.isResolved()) {
                    resolveFieldType(stmt, idType.resolvedType, field);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
   
    public void addTypeDeclaration(Decl decl) {
        if(this.resolvedTypes.containsKey(decl.name)) {
            this.result.addError(new TypeResolverError(String.format("type '%s' already defined", decl.name), decl));
        }
        
        this.resolvedTypes.put(decl.name, decl.type);
        
        
        List<TypeInfo> unresolved = this.unresolvedTypes.remove(decl.name);
        if(unresolved == null) {
            return;
        }
        
        for(TypeInfo type : unresolved) {            
            if(!type.isResolved()) {
                IdentifierTypeInfo idType = type.as();
                idType.resolve(decl.type);
            }
        }
    }
    
    public void resolveTypes() {
        for(Pending pending : this.pendingResolvedTypes) {
            resolveTypeInfo(pending.objectField);
            
            if(!pending.accessField.isResolved()) {
                IdentifierTypeInfo idInfo = pending.accessField.as();
                idInfo.resolve(pending.objectField);
            }
        }
    }
        
    public TypeResolverScope pushScope() {
        return new TypeResolverScope(this.result, this);
    }
}
