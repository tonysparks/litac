/*
 * see license.txt
 */
package litac.ast;

import java.util.*;

import litac.ast.Node.SrcPos;
import litac.generics.GenericParam;
import litac.util.Names;

/**
 * Describes a Type Specification.
 * 
 * @author Tony
 *
 */
public abstract class TypeSpec {

    public static TypeSpec copy(TypeSpec spec) {
        if(spec == null) {
            return null;
        }
        
        return spec.copy();
    }
    
    public static List<TypeSpec> copy(List<TypeSpec> specs) {
        if(specs == null) {
            return null;
        }
        
        List<TypeSpec> result = new ArrayList<>(specs.size());
        for(TypeSpec s : specs) {
            result.add(TypeSpec.copy(s));
        }
        
        return result;
    }
    
    public static TypeSpec newVoid(SrcPos pos) {
        return new NameTypeSpec(pos, "void");
    }
    
    public static NameTypeSpec getBaseType(TypeSpec spec) {
        if(spec == null) {
            return null;
        }
        
        switch(spec.kind) {
            case ARRAY:
                return getBaseType(spec.base);
            case CONST:
                return getBaseType(spec.base);
            case PTR:
                return getBaseType(spec.base);
            case FUNC_PTR:
                return null;
            case NAME:
                return spec.as();
            default:
                return null;        
        }
    }
    
    public static enum TypeSpecKind {
        ARRAY,
        PTR,
        CONST,
        NAME,    
        FUNC_PTR,
    }
    
    
    public final SrcPos pos;
    public final TypeSpecKind kind;
    public TypeSpec base;
        
    TypeSpec(TypeSpecKind kind, SrcPos pos, TypeSpec base) {
        this.kind = kind;        
        this.pos = pos;
        this.base = base;
    }

    @SuppressWarnings("unchecked")
    public <T extends TypeSpec> T as() {
        return (T) this;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {    
        if(obj == this) {
            return true;
        }
        
        if(!(obj instanceof TypeSpec)) {
            return false;
        }
        
        return this.toString().equals(obj.toString());
    }
    
    public abstract TypeSpec copy();
    
    public static class NameTypeSpec extends TypeSpec {         
        public String name;
        public List<TypeSpec> genericArgs;
        
        public NameTypeSpec(SrcPos pos, String name) {
            this(pos, null, name, Collections.emptyList());
        }
        
        public NameTypeSpec(SrcPos pos, String name, List<TypeSpec> genericArgs) {
            this(pos, null, name, genericArgs);
        }
        
        public NameTypeSpec(SrcPos pos, TypeSpec base, String name, List<TypeSpec> genericArgs) {
            super(TypeSpecKind.NAME, pos, base);
            this.name = name;
            this.genericArgs = genericArgs;
        }
        
        @Override
        public String toString() {
            return Names.genericsName(this);
        }
        
        public String toGenericsName() {
            return Names.genericsName(this);
        }
        
        public boolean hasGenericArgs() {
            return this.genericArgs != null && !this.genericArgs.isEmpty();
        }        
        
        @Override
        public TypeSpec copy() {
            return new NameTypeSpec(pos, this.name, TypeSpec.copy(this.genericArgs));
        }
    }
    
    public static class ArrayTypeSpec extends TypeSpec {
        public Expr numElements;
        
        public ArrayTypeSpec(SrcPos pos, TypeSpec base, Expr numElements) {
            super(TypeSpecKind.ARRAY, pos, base);            
            this.numElements = numElements;
        }
        
        @Override
        public String toString() {        
            return this.base.toString() + "[]";
        }
        
        @Override
        public TypeSpec copy() {
            return new ArrayTypeSpec(this.pos, 
                                     TypeSpec.copy(this.base), 
                                     (this.numElements != null) ? this.numElements.copy() : null);
        }
    }
    
    public static class PtrTypeSpec extends TypeSpec {     
        public PtrTypeSpec(SrcPos pos, TypeSpec base) {
            super(TypeSpecKind.PTR, pos, base);
        }
        
        @Override
        public String toString() {        
            return this.base.toString() + "*";
        }
        
        @Override
        public TypeSpec copy() {
            return new PtrTypeSpec(this.pos, TypeSpec.copy(base));
        }
    }
    
    public static class ConstTypeSpec extends TypeSpec {     
        public ConstTypeSpec(SrcPos pos, TypeSpec base) {
            super(TypeSpecKind.CONST, pos, base);
        }
        
        @Override
        public String toString() {        
            return this.base.toString() + " const";
        }
        
        @Override
        public TypeSpec copy() {
            return new ConstTypeSpec(this.pos, TypeSpec.copy(base));
        }
    }
    
    public static class FuncPtrTypeSpec extends TypeSpec {     
        public List<TypeSpec> args;
        public TypeSpec ret;
        public boolean hasVarargs;
        public List<GenericParam> genericParams;
        
        public FuncPtrTypeSpec(SrcPos pos, 
                               List<TypeSpec> args, 
                               TypeSpec ret, 
                               boolean hasVarargs, 
                               List<GenericParam> genericParams) {
            super(TypeSpecKind.FUNC_PTR, pos, null);
            this.args = args;
            this.ret = ret;
            this.hasVarargs = hasVarargs;
            this.genericParams = genericParams;
        }
        
        @Override
        public TypeSpec copy() {
            return new FuncPtrTypeSpec(this.pos, 
                                       TypeSpec.copy(this.args), 
                                       TypeSpec.copy(this.ret),
                                       this.hasVarargs,
                                       new ArrayList<>(this.genericParams)); 
        }
        
        @Override
        public String toString() {
            StringBuilder genParams = new StringBuilder();
            
            boolean isFirst = true;
            if(!this.genericParams.isEmpty()) {
                genParams.append("<");
                for(GenericParam p: this.genericParams) {
                    if(!isFirst) genParams.append(", ");
                    genParams.append(p.name);
                    isFirst = false;
                }
                genParams.append(">");
            }
            
            
            isFirst = true;
            StringBuilder params = new StringBuilder();
            for(TypeSpec p : this.args) {
                if(!isFirst) params.append(", ");
                params.append(p);
                isFirst = false;
            }
            
            if(this.hasVarargs) {
                if(!isFirst) {
                    params.append(",");
                }
                params.append("...");
            }
            
            return String.format("func%s(%s) : %s", genParams, params, this.ret);
        }
    }
    
}
