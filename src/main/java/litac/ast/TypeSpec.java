/*
 * see license.txt
 */
package litac.ast;

import java.util.Collections;
import java.util.List;

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
        // TODO: Do we need to copy??
        return spec;
    }
    
    public static List<TypeSpec> copy(List<TypeSpec> specs) {
        // TODO: Do we need to copy??
        return specs;
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
    }
    
    public static class PtrTypeSpec extends TypeSpec {     
        public PtrTypeSpec(SrcPos pos, TypeSpec base) {
            super(TypeSpecKind.PTR, pos, base);
        }
        
        @Override
        public String toString() {        
            return this.base.toString() + "*";
        }
    }
    
    public static class ConstTypeSpec extends TypeSpec {     
        public ConstTypeSpec(SrcPos pos, TypeSpec base) {
            super(TypeSpecKind.CONST, pos, base);
        }
        
        @Override
        public String toString() {        
            return this.base.toString() + "const";
        }
    }
    
    public static class FuncPtrTypeSpec extends TypeSpec {     
        public List<TypeSpec> args;
        public TypeSpec ret;
        public boolean hasVarargs;
        public List<GenericParam> genericParams;
        
        public FuncPtrTypeSpec(SrcPos pos, List<TypeSpec> args, TypeSpec ret, boolean hasVarargs, List<GenericParam> genericParams) {
            super(TypeSpecKind.FUNC_PTR, pos, null);
            this.args = args;
            this.ret = ret;
            this.hasVarargs = hasVarargs;
            this.genericParams = genericParams;
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
