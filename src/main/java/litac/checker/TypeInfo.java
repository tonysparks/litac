/*
 * see license.txt
 */
package litac.checker;

import java.util.List;

import litac.ast.Decl.ParameterDecl;
import litac.ast.Expr;


/**
 * @author Tony
 *
 */
public abstract class TypeInfo {
    
    public static final TypeInfo BOOL_TYPE = new PrimitiveTypeInfo("bool", TypeKind.bool);
    public static final TypeInfo CHAR_TYPE = new PrimitiveTypeInfo("char", TypeKind.Char);
    public static final TypeInfo I8_TYPE   = new PrimitiveTypeInfo("i8", TypeKind.i8);
    public static final TypeInfo U8_TYPE   = new PrimitiveTypeInfo("u8", TypeKind.u8);
    public static final TypeInfo I16_TYPE  = new PrimitiveTypeInfo("i16", TypeKind.i16);
    public static final TypeInfo U16_TYPE  = new PrimitiveTypeInfo("u16", TypeKind.u16);
    public static final TypeInfo I32_TYPE  = new PrimitiveTypeInfo("i32", TypeKind.i32);
    public static final TypeInfo U32_TYPE  = new PrimitiveTypeInfo("u32", TypeKind.u32);
    public static final TypeInfo I64_TYPE  = new PrimitiveTypeInfo("i64", TypeKind.i64);
    public static final TypeInfo U64_TYPE  = new PrimitiveTypeInfo("u64", TypeKind.u64);
    public static final TypeInfo I128_TYPE = new PrimitiveTypeInfo("i128", TypeKind.i128);
    public static final TypeInfo U128_TYPE = new PrimitiveTypeInfo("u128", TypeKind.u128);
    public static final TypeInfo F32_TYPE  = new PrimitiveTypeInfo("f32", TypeKind.f32);
    public static final TypeInfo F64_TYPE  = new PrimitiveTypeInfo("f64", TypeKind.f64);
    public static final TypeInfo NULL_TYPE = new NullTypeInfo();
    public static final TypeInfo VOID_TYPE = new VoidTypeInfo();
    
    
    public static enum TypeKind {
        bool,
        Char,
        i8,  u8,
        i16, u16,
        i32, u32,
        i64, u64,
        i128,u128,
        f32, f64,
        
        Str,
        Array,
        Ptr,
        Null,
        
        Struct,
        Func,
        Enum,
        Union,
        
        Void,
        
        Identifier,
        ;
        
        public static TypeKind fromString(String str) {
            for(TypeKind kind : values()) {
                if(kind.name().equals(str)) {
                    return kind;
                }
            }
            
            return null;
        }
    }
    
    protected TypeKind kind;
    public String name;
    
    TypeInfo(TypeKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }
    
    @Override
    public String toString() {    
        return this.kind.name().toLowerCase();
    }
    
    public boolean isAnonymous() {
        return false;
    }
    
    public boolean isKind(TypeKind kind) {
        return this.kind == kind;
    }
    
    public TypeKind getKind() {
        return this.kind;
    }
    
    public String getName() {
        return this.name;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends TypeInfo> T as() {
        return (T) this;
    }
    
    public boolean isPrimitive() {
        return false;
    }

    public boolean isResolved() {
        return true;
    }
    
    public TypeInfo getResolvedType() {
        return this;
    }
    
    public boolean isGreater(TypeInfo type) {
        if(this == type) {
            return false;
        }
        
        TypeInfo me = this;
        if(this.kind == TypeKind.Identifier) {
            IdentifierTypeInfo idInfo = (IdentifierTypeInfo)this;
            if(!idInfo.isResolved()) {
                return false;
            }
            
            me = idInfo.resolvedType;            
        }
        
        TypeInfo them = type;
        if(type.kind == TypeKind.Identifier) {
            IdentifierTypeInfo idInfo = (IdentifierTypeInfo)type;
            if(!idInfo.isResolved()) {
                return false;
            }
            
            them = idInfo.resolvedType;
        }
        
        return me.kind.ordinal() > them.kind.ordinal();
    }
        
    public boolean strictEquals(TypeInfo other) {
        if(other == this) {
            return true;
        }
        
        if(other.isKind(this.getKind())) {
            return other.getResolvedType().getName().equals(this.getResolvedType().getName());
        }
        
        return false;
    }
    
    public abstract boolean canCastTo(TypeInfo target);

    
    public static class FieldInfo {
        public TypeInfo type;
        public String name;
        
        
        /**
         * @param type
         * @param name
         */
        public FieldInfo(TypeInfo type, String name) {
            this.type = type;
            this.name = name;
        }
    }
    
    public static class EnumFieldInfo {        
        public String name;
        public Expr value;
        
        
        /**
         * @param name
         * @param value
         */
        public EnumFieldInfo(String name, Expr value) {            
            this.name = name;
            this.value = value;
        }
    }
    
    
    public static class StructTypeInfo extends TypeInfo {
        public List<FieldInfo> fieldInfos;
        public boolean isAnonymous;

        /**
         * @param name
         * @param fieldInfos
         */
        public StructTypeInfo(String name, List<FieldInfo> fieldInfos, boolean isAnon) {
            super(TypeKind.Struct, name);
            this.fieldInfos = fieldInfos;
            this.isAnonymous = isAnon;
        }
        
        @Override
        public boolean isAnonymous() {
            return isAnonymous;
        }
        
        @Override
        public String toString() {        
            return "struct " + this.name;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Struct) {
                StructTypeInfo targetStruct = (StructTypeInfo)target;
                
                if(this.fieldInfos.size() < targetStruct.fieldInfos.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.fieldInfos.size(); i++) {
                    FieldInfo targetField = targetStruct.fieldInfos.get(i);
                    FieldInfo thisField   = this.fieldInfos.get(i);
                    
                    if(!targetField.type.strictEquals(thisField.type)) {
                        return false;
                    }
                }
            }
            return false;
        }
    }
    
    public static class UnionTypeInfo extends TypeInfo {
        public List<FieldInfo> fieldInfos;
        public boolean isAnonymous;
        
        /**
         * @param name
         * @param fieldInfos
         */
        public UnionTypeInfo(String name, List<FieldInfo> fieldInfos, boolean isAnon) {
            super(TypeKind.Union, name);
            this.fieldInfos = fieldInfos;
            this.isAnonymous = isAnon;
        }
        
        @Override
        public boolean isAnonymous() {
            return isAnonymous;
        }
        
        @Override
        public String toString() {        
            return "union " + this.name;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Union) {
                UnionTypeInfo targetUnion = (UnionTypeInfo)target;
                
                if(this.fieldInfos.size() < targetUnion.fieldInfos.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.fieldInfos.size(); i++) {
                    FieldInfo targetField = targetUnion.fieldInfos.get(i);
                    FieldInfo thisField   = this.fieldInfos.get(i);
                    
                    if(!targetField.type.strictEquals(thisField.type)) {
                        return false;
                    }
                }
            }
            return false;
        }
    }
    
    public static class EnumTypeInfo extends TypeInfo {
        public List<EnumFieldInfo> fields;

        /**
         * @param name
         * @param fieldInfos
         */
        public EnumTypeInfo(String name, List<EnumFieldInfo> fields) {
            super(TypeKind.Enum, name);
            this.fields = fields;
        }
        
        @Override
        public String toString() {        
            return "enum " + this.name;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Enum) {
                return this.name.equals(target.name);
            }
            
            return false;
        }
    }
    
    public static class FuncTypeInfo extends TypeInfo {
        public TypeInfo returnType;
        public List<ParameterDecl> parameterDecls;
        public boolean isVararg;
        
        /**
         * @param name
         * @param returnType
         * @param parameterDecls
         */
        public FuncTypeInfo(String name, TypeInfo returnType, List<ParameterDecl> parameterDecls, boolean isVararg) {
            super(TypeKind.Func, name);
            this.returnType = returnType;
            this.parameterDecls = parameterDecls;
            this.isVararg = isVararg;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Func) {
                FuncTypeInfo funcType = (FuncTypeInfo) target;
                if(this.parameterDecls.size() != funcType.parameterDecls.size()) {
                    return false;
                }
                
                if(this.isVararg != funcType.isVararg) {
                    return false;
                }
                
                for(int i = 0; i < this.parameterDecls.size(); i++) {
                    if(!this.parameterDecls.get(i).type.strictEquals(funcType.parameterDecls.get(i).type)) {
                        return false;
                    }
                }
                
                return this.returnType.strictEquals(funcType.returnType);
                       
            }
            
            return false;
        }
        
        @Override
        public String toString() {    
            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for(ParameterDecl p : this.parameterDecls) {
                if(!isFirst) sb.append(", ");
                sb.append(p);
                isFirst = false;
            }
            
            if(this.isVararg) {
                if(!isFirst) {
                    sb.append(",");
                }
                sb.append("...");
            }
            
            return String.format("func %s(%s) : %s", this.name, sb, this.returnType);
        }
    }
    
    public static class StrTypeInfo extends TypeInfo {
        public String str;

        public StrTypeInfo(String str) {
            super(TypeKind.Str, "String");
            this.str = str;
        }
        
        @Override
        public String toString() {    
            return "string";
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Str) {
                return true;
            }
            
            // Account for c style strings
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                if(ptrInfo.ptrOf.isKind(TypeKind.Char) ||
                   ptrInfo.ptrOf.isKind(TypeKind.u8) ||
                   ptrInfo.ptrOf.isKind(TypeKind.i8)) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    public static class PtrTypeInfo extends TypeInfo {
        public TypeInfo ptrOf;

        /**
         * @param moduleName
         * @param ptrOf
         */
        public PtrTypeInfo(TypeInfo ptrOf) {
            super(TypeKind.Ptr, "ptr");
            this.ptrOf = ptrOf;
        }
                
        @Override
        public String getName() {
            return ptrOf.getName() + "*";
        }
        
        @Override
        public String toString() {    
            return ptrOf.toString() + "*";
        }
                
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Identifier)) {
                IdentifierTypeInfo idInfo = target.as();
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.getResolvedType());
                }
            }
            
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                return this.ptrOf.getResolvedType().canCastTo(ptrInfo.ptrOf.getResolvedType());
            }
            
            return false;
        }
    }
    
    public static class ArrayTypeInfo extends TypeInfo {
        public TypeInfo arrayOf;
        public List<Integer> dimensions;

        /**
         * @param moduleName
         * @param arrayOf
         */
        public ArrayTypeInfo(TypeInfo arrayOf, List<Integer> dimensions) {
            super(TypeKind.Array, "Array");
            this.arrayOf = arrayOf;
            this.dimensions = dimensions;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Array) {
                ArrayTypeInfo arrayInfo = (ArrayTypeInfo)target;
                if(!this.arrayOf.strictEquals(arrayInfo.arrayOf)) {
                    return false;
                }
                                
                if(this.dimensions.size() != arrayInfo.dimensions.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.dimensions.size(); i++) {
                    if(this.dimensions.get(i) != arrayInfo.dimensions.get(i)) {
                        return false;
                    }
                }
                
                return true;
            }
            
            return false;
        }
    }
    
    public static class PrimitiveTypeInfo extends TypeInfo {        
        
        private PrimitiveTypeInfo(String name, TypeKind kind) {
            super(kind, name);            
        }
        
        @Override
        public boolean isPrimitive() {
            return true;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            // Warnings, allow everything to be casted?
            if(target.isPrimitive()) {
                if(target.isGreater(this)) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    public static class VoidTypeInfo extends TypeInfo {
        private VoidTypeInfo() {
            super(TypeKind.Void, "void");            
        }
        
        @Override
        public String toString() {    
            return "void";
        }
        

        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == this.kind) {
                return true;
            }
            
            return true;
        }
    }
    
    public static class NullTypeInfo extends TypeInfo {
        private NullTypeInfo() {
            super(TypeKind.Null, "null");            
        }
        
        @Override
        public String toString() {    
            return "null";
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == this.kind) {
                return true;
            }
            
            return true;
        }
    }
    
    public static class IdentifierTypeInfo extends TypeInfo {
        public TypeInfo resolvedType;
        public String identifier;
        
        public IdentifierTypeInfo(String identifier) {
            super(TypeKind.Identifier, identifier);
            this.identifier = identifier;
            this.resolvedType = null;
        }
        
        @Override
        public String toString() {   
            if(isResolved()) {
                return this.resolvedType.toString();
            }
            return "[unresolved] : " + this.identifier;
        }
        
        @Override
        public boolean isResolved() {
            return this.resolvedType != null;
        }
        
        @Override
        public TypeInfo getResolvedType() {
            return resolvedType;
        }
        
        public void resolve(TypeInfo resolvedTo) {
            this.resolvedType = resolvedTo;
        }
        
        @Override
        public boolean isAnonymous() {
            if(isResolved()) {
                return this.resolvedType.isAnonymous();
            }
            
            return super.isAnonymous();
        }
        
        @Override
        public boolean isKind(TypeKind kind) {
            if(isResolved()) {
                return this.resolvedType.isKind(kind);
            }
            
            return super.isKind(kind);
        }
        
        @Override
        public TypeKind getKind() {
            if(isResolved()) {
                return this.resolvedType.getKind();
            }
            
            return super.getKind();
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T extends TypeInfo> T as() {
            if(isResolved()) {
                return (T) this.resolvedType.as();
            }
            
            return (T) this;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
                        
            if(target.isKind(this.kind)) {
                IdentifierTypeInfo idInfo = target.as();
                if(isResolved() && idInfo.isResolved()) {
                    return this.resolvedType.canCastTo(idInfo.getResolvedType());
                }
            }
            else if(isResolved()) {
                return this.resolvedType.canCastTo(target);
            }
                
            return false;
        }
    }
}
