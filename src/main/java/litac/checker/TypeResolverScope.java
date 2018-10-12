/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import litac.ast.TypeInfo;
import litac.ast.TypeInfo.IdentifierTypeInfo;
import litac.ast.TypeInfo.TypeKind;
import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.Stmt.VarFieldStmt;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.checker.TypeResolverResult.TypeResolverError;

/**
 * @author Tony
 *
 */
public class TypeResolverScope {
    
    private Map<String, TypeInfo> resolvedTypes;
    // Key=>TypeName, Value=>List of TypeInfo's that need to be resolved
    private Map<String, List<TypeInfo>> unresolvedTypes;
    
    private TypeResolverScope parent;
    
    public TypeResolverScope() {
        this(null);
    }
    
    public TypeResolverScope(TypeResolverScope parent) {
        this.parent = parent;
        
        this.resolvedTypes = new HashMap<>();
        this.unresolvedTypes = new HashMap<>();
    }
    
    public TypeResolverScope getParent() {
        return this.parent;
    }
    
    public void addUnresolvedType(TypeInfo type) {
        // this is not a type that needs resolution
        if(type.kind != TypeKind.Identifier) {
            return;
        }

        // determine if the type has already been resolved
        if(this.resolvedTypes.containsKey(type.name)) {
            if(type instanceof IdentifierTypeInfo) {                
                IdentifierTypeInfo idType = (IdentifierTypeInfo) type;
                if(!idType.isResolved()) {
                    idType.resolve(this.resolvedTypes.get(type.name));
                }
            }
            
            return;
        }
        
        // we'll need to resolve this type at some point
        if(!this.unresolvedTypes.containsKey(type.name)) {
            this.unresolvedTypes.put(type.name, new ArrayList<>());
        }
        
        this.unresolvedTypes.get(type.name).add(type);
    }
   
    public TypeResolverError resolveType(Decl decl) {
        if(this.resolvedTypes.containsKey(decl.name)) {
            return new TypeResolverError(String.format("type '%s' already defined", decl.name), decl);
        }
        
        this.resolvedTypes.put(decl.name, decl.type);
        
        
        List<TypeInfo> unresolved = this.unresolvedTypes.get(decl.name);
        if(unresolved == null) {
            return null;
        }
        
        for(TypeInfo type : unresolved) {
            if(type instanceof IdentifierTypeInfo) {
                IdentifierTypeInfo idType = (IdentifierTypeInfo) type;
                idType.resolve(decl.type);
            }
        }
        
        return null;
    }
        
    public TypeResolverScope pushScope() {
        return new TypeResolverScope(this);
    }
}
