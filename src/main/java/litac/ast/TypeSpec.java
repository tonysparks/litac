/*
 * see license.txt
 */
package litac.ast;

import java.util.*;

import litac.ast.Node.SrcPos;
import litac.generics.GenericParam;

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
    
    public static enum TypeSpecKind {
        ARRAY,
        PTR,
        CONST,
        NAME,    
        FUNC,
    }
    
    
    public final SrcPos pos;
    public final TypeSpecKind kind;
    public TypeSpec base;
        
    TypeSpec(TypeSpecKind kind, SrcPos pos, TypeSpec base) {
        this.kind = kind;        
        this.pos = pos;
        this.base = base;
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
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public static class ArrayTypeSpec extends TypeSpec {
        public long length;
        public Expr numElements;
        
        public ArrayTypeSpec(SrcPos pos, TypeSpec base, long length, Expr numElements) {
            super(TypeSpecKind.ARRAY, pos, base);
            this.length = length;
            this.numElements = numElements;
        }
    }
    
    public static class PtrTypeSpec extends TypeSpec {     
        public PtrTypeSpec(SrcPos pos, TypeSpec base) {
            super(TypeSpecKind.PTR, pos, base);
        }
    }
    
    public static class ConstTypeSpec extends TypeSpec {     
        public ConstTypeSpec(SrcPos pos, TypeSpec base) {
            super(TypeSpecKind.CONST, pos, base);
        }
    }
    
    public static class FuncPtrTypeSpec extends TypeSpec {     
        public List<TypeSpec> args;
        public TypeSpec ret;
        public boolean hasVarargs;
        public List<GenericParam> genericParam;
        
        public FuncPtrTypeSpec(SrcPos pos, List<TypeSpec> args, TypeSpec ret, boolean hasVarargs, List<GenericParam> genericParam) {
            super(TypeSpecKind.FUNC, pos, null);
            this.args = args;
            this.ret = ret;
            this.hasVarargs = hasVarargs;
            this.genericParam = genericParam;
        }
    }
    
}
