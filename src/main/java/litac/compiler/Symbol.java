/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.TypeKind;
import litac.generics.GenericParam;
import litac.compiler.Module;

/**
 * @author Tony
 *
 */
public class Symbol {

    public static enum SymbolKind {
        TYPE,
        VAR,
        CONST,
        FUNC,
    }
    
    public static enum ResolveState {
        UNRESOLVED,
        RESOLVING,
        RESOLVED,
    }
    
    public static final int IS_LOCAL                = (1<<1);
    public static final int IS_FOREIGN              = (1<<2);
    public static final int IS_CONSTANT             = (1<<3);
    public static final int IS_USING                = (1<<4);
    public static final int IS_TYPE                 = (1<<5);
    public static final int IS_INCOMPLETE           = (1<<6);
    public static final int IS_GENERIC_TEMPLATE     = (1<<7);
    public static final int IS_BUILTIN              = (1<<8);
    public static final int IS_FROM_GENERIC_TEMPLATE  = (1<<9);
    
    public SymbolKind kind;
    public ResolveState state;
    public Decl decl;
    public final String name;
    
    /** the module in which this symbol is defined in 
     *
     *  Generics are declared in the root module
     */
    public Module declared;
    
    /** if this symbol is from a generic type, this module is where the generic
     *  type is defined
     */
    public Module genericDeclaration;
    
    /**
     * if this symbol is from a generic type, the module in which it is used
     * and therefore generated from
     */
    public Module callsiteModule;
    
    /**
     * Map for knowing which types should be resolved
     * in the genericDeclaration module or the call site module 
     */
    public Map<TypeSpec, Module> genericMap;
    
    /**
     * Map for knowing the generic arguments for this symbol
     */
    public List<TypeInfo> genericArgs;
    
    /**
     * Original declaration parameter names
     */
    public List<GenericParam> genericParams;
    
    /**
     * The type associated with this symbol
     */
    public TypeInfo type;
    
    /**
     * If this symbol is defined by a using block, this is the 
     * parent aggregate type
     */
    public TypeInfo usingParent;
    
    private      int flags;
    
    public Symbol(SymbolKind kind, 
                  Decl decl, 
                  String name,
                  Module declared, 
                  int flags) {
        
        this.kind = kind;
        this.decl = decl;
        this.name = name;
        this.declared = declared;
        this.flags = flags;
        this.state = ResolveState.UNRESOLVED;
        
        this.genericMap = new HashMap<>();
        this.genericArgs = new ArrayList<>();
    }
    
    public boolean isTypeKind(TypeKind kind) {
        if(this.type == null) {
            return false;
        }
        
        return this.type.isKind(kind);
    }
    
    public TypeKind getTypeKind() {
        if(this.type == null) {
            return TypeKind.Void;
        }
        
        return this.type.getKind();
    }
    
    /**
     * @return the type
     */
    public TypeInfo getType() {
        return type;
    }
    
    /**
     * @return true if this symbol is a generic template
     */
    public boolean isGenericTemplate() {
        return (this.flags & IS_GENERIC_TEMPLATE) > 0;
    }
    
    /**
     * @return true if this symbol is a local symbol (not module scoped)
     */
    public boolean isLocal() {
        return (this.flags & IS_LOCAL) > 0;
    }
    
    /**
     * @return true if this symbol is a foreign symbol (native C type)
     */
    public boolean isForeign() {
        return (this.flags & IS_FOREIGN) > 0;
    }
    
    /**
     * @return true if this symbol was defined as a constant
     */
    public boolean isConstant() {
        return (this.flags & IS_CONSTANT) > 0;
    }
    
    /**
     * @return true if this symbol was defined from a using modifier
     */
    public boolean isUsing() {
        return (this.flags & IS_USING) > 0;
    }
    
    /**
     * @return if this symbol represents a type
     */
    public boolean isType() {
        return (this.flags & IS_TYPE) > 0;
    }
    
    public boolean isBuiltin() {
        return (this.flags & IS_BUILTIN) > 0;
    }
    
    public boolean isFromGenericTemplate() {
        return (this.flags & IS_FROM_GENERIC_TEMPLATE) > 0;
    }
    
    /**
     * @return if this symbol is incomplete in its definition
     */
    public boolean isIncomplete() {
        return (this.flags & IS_INCOMPLETE) > 0;
    }
    
    public boolean isComplete() {
        return !isIncomplete();
    }
    
    /**
     * Removes the foreign designation
     */
    public void removeForeign() {
        this.flags &= ~IS_FOREIGN;
    }
    
    public void markAsGenericTemplate() {
        this.flags |= IS_GENERIC_TEMPLATE;
    }
    
    public void markAsBuiltin() {
        this.flags |= IS_BUILTIN;
    }
    
    public void markFromGenericTemplate() {
        this.flags |= IS_FROM_GENERIC_TEMPLATE;
    }
    
    /**
     * Marks the symbol as completed by definition (incomplete types are
     * defined as globals that need to be eventually resolved)
     */
    public void markAsComplete() {        
        this.flags &= ~IS_INCOMPLETE;        
    }
    
    public void maskAsIncomplete() {
        this.flags |= IS_INCOMPLETE;
    }
    
    public Module getDeclaredModule() {
        /*if(this.callsiteModule != null && !isGenericTemplate() && isFromGenericTemplate()) {
            if(this.decl.kind == DeclKind.TYPEDEF) {
                return this.callsiteModule;
            }
        }*/
        
        if(this.genericDeclaration != null) {
            return this.genericDeclaration;
        }
        
        return this.declared;
    }
    
    public void addModuleForType(TypeSpec typeSpec, TypeInfo type, Symbol typedefSym) {        
        type = TypeInfo.getBase(type);
        
        Module module = null;
        if(typedefSym != null) {
            /*System.out.println("TypedefSym Decl: " + typedefSym.name + "(" +TypeSpec.getBaseType(typeSpec)+")" +
                " type: " + typedefSym.decl.kind.name() + 
                " declared: " + typedefSym.declared + 
                " getDeclared: " + typedefSym.getDeclaredModule() + 
                " callsite: " + typedefSym.callsiteModule);*/
            module = typedefSym.getDeclaredModule();
        }
        
        if(module == null && type != null && type.sym != null) {
            /*System.out.println("Sym Decl: " + type.sym.name + "(" +TypeSpec.getBaseType(typeSpec)+")" +
                               " type: " + type.sym.decl.kind.name() + 
                               " declared: " + type.sym.declared + 
                               " getDeclared: " + type.sym.getDeclaredModule() + 
                               " callsite: " + type.sym.callsiteModule);*/            
            module = type.sym.getDeclaredModule();
        }
        
        if(module != null) {
            this.genericMap.put(TypeSpec.getBaseType(typeSpec), module);
        }
    }
    
    public Module getModuleForType(TypeSpec type) {
        return this.genericMap.getOrDefault(TypeSpec.getBaseType(type), this.getDeclaredModule());
    }
        
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name:").append(this.name).append("\n")
          .append("Type:").append(this.type).append("\n")
          .append("isLocal:").append(isLocal()).append("\n")
          .append("isForeign:").append(isForeign()).append("\n")
          .append("isConstant:").append(isConstant()).append("\n")
          .append("isUsing:").append(isUsing()).append("\n")
          .append("isType:").append(isType()).append("\n")
          .append("isIncomplete:").append(isIncomplete()).append("\n")
          .append("isGenericTemplate:").append(isGenericTemplate()).append("\n")
          .append("isBuiltin:").append(isBuiltin()).append("\n")
          .append("isFromGenericTemplate:").append(isFromGenericTemplate()).append("\n")
          ;
        return sb.toString();
    }
}
