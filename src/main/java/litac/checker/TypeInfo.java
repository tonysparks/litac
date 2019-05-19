/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
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
    
    public static TypeInfo copy(TypeInfo type) {
        if(type != null) {
            return type.copy();
        }
        
        return null;
    }
    
    public static List<TypeInfo> copy(List<TypeInfo> types) {
        List<TypeInfo> copies = new ArrayList<>(types.size());
        for(TypeInfo t : types) {
            copies.add(t.copy());
        }
        
        return copies;
    }
    
    public static TypeInfo fromString(String typeName) {
        switch(typeName) {
            case "bool": return BOOL_TYPE;
            case "char": return CHAR_TYPE;
            case "i8":   return I8_TYPE;
            case "u8":   return U8_TYPE;
            case "i16":  return I16_TYPE;
            case "u16":  return U16_TYPE;
            case "i32":  return I32_TYPE;
            case "u32":  return U32_TYPE;
            case "i64":  return I64_TYPE;
            case "u64":  return U64_TYPE;
            case "i128": return I128_TYPE;
            case "u128": return U128_TYPE;
            case "f32":  return F32_TYPE;
            case "f64":  return F64_TYPE;
            case "null":   return NULL_TYPE;
            case "void":   return VOID_TYPE;
            default: return null;
        }
    }

    public static boolean isFloat(TypeInfo type) {
        switch(type.getKind()) {
            case Identifier: {
                IdentifierTypeInfo idType = type.as();
                return isFloat(idType.getResolvedType());
            }
            case Any:
            case f32:
            case f64:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isUnsignedInteger(TypeInfo type) {
        switch(type.getKind()) {
            case Identifier: {
                IdentifierTypeInfo idType = type.as();
                return isUnsignedInteger(idType.getResolvedType());
            }
            case Any:
            case bool:
            case u128:
            case u16:
            case u32:
            case u64:
            case u8:
                return true;
            default:
                return false;
        }
    }

    
    public static boolean isSignedInteger(TypeInfo type) {
        switch(type.getKind()) {
            case Identifier: {
                IdentifierTypeInfo idType = type.as();
                return isSignedInteger(idType.getResolvedType());
            }
            case Any:
            case bool:
            case Char:
            case Enum:
            case i128:
            case i16:
            case i32:
            case i64:
            case i8:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isInteger(TypeInfo type) {
        switch(type.getKind()) {
            case Identifier: {
                IdentifierTypeInfo idType = type.as();
                return isInteger(idType.getResolvedType());
            }
            case Any:
            case bool:
            case Char:
            case Enum:
            case i128:
            case i16:
            case i32:
            case i64:
            case i8:

            case u128:
            case u16:
            case u32:
            case u64:
            case u8:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isNumber(TypeInfo type) {
        switch(type.getKind()) {
            case Identifier: {
                IdentifierTypeInfo idType = type.as();
                return isNumber(idType.getResolvedType());
            }
            case Any:
            case bool:
            case Char:
            case Enum:
            case i128:
            case i16:
            case i32:
            case i64:
            case i8:

            case u128:
            case u16:
            case u32:
            case u64:
            case u8:
                
            case f32:
            case f64:
                return true;
            default:
                return false;
        }
    }
    
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
        Any
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
    public Symbol sym;
    
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
    public abstract TypeInfo copy();
    
    public static class FieldInfo {
        public TypeInfo type;
        public String name;
        public String genericArg;
        
        /**
         * @param type
         * @param name
         */
        public FieldInfo(TypeInfo type, String name, String genericArg) {
            this.type = type;
            this.name = name;
            this.genericArg = genericArg;
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
    
    public static abstract class GenericTypeInfo extends TypeInfo {
        public List<GenericParam> genericParams;
        
        public GenericTypeInfo(TypeKind kind, String name, List<GenericParam> genericParams) {
            super(kind, name);
            this.genericParams = genericParams;
        }
        
        public boolean hasGenerics() {
            return !this.genericParams.isEmpty();
        }
    }
    
    public static class AnyTypeInfo extends TypeInfo {
        public AnyTypeInfo(String name) {
            super(TypeKind.Any, name);
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            return true;
        }
        
        @Override
        public String toString() {        
            return this.name;
        }
        
        @Override
        public TypeInfo copy() {        
            return new AnyTypeInfo(this.name);
        }
    }
    
    public static class StructTypeInfo extends GenericTypeInfo {
        public List<FieldInfo> fieldInfos;
        public boolean isAnonymous;

        /**
         * @param name
         * @param fieldInfos
         */
        public StructTypeInfo(String name, List<GenericParam> genericParams, List<FieldInfo> fieldInfos, boolean isAnon) {
            super(TypeKind.Struct, name, genericParams);
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
        public TypeInfo copy() {
            return new StructTypeInfo(this.name, genericParams, fieldInfos, isAnonymous);
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
            
            if(target.isKind(TypeKind.Any)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class UnionTypeInfo extends GenericTypeInfo {
        public List<FieldInfo> fieldInfos;
        public boolean isAnonymous;
        
        /**
         * @param name
         * @param fieldInfos
         */
        public UnionTypeInfo(String name, List<GenericParam> genericParams, List<FieldInfo> fieldInfos, boolean isAnon) {
            super(TypeKind.Union, name, genericParams);
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
        public TypeInfo copy() {
            return new UnionTypeInfo(this.name, this.genericParams, this.fieldInfos, this.isAnonymous);
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
            
            if(target.isKind(TypeKind.Any)) {
                return true;
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
        public TypeInfo copy() {
            return new EnumTypeInfo(this.name, this.fields);
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
            
            if(target.isKind(TypeKind.Any)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class FuncTypeInfo extends GenericTypeInfo {
        public TypeInfo returnType;
        public List<ParameterDecl> parameterDecls;
        public boolean isVararg;
        
        public FuncTypeInfo(String name, 
                            TypeInfo returnType, 
                            List<ParameterDecl> parameterDecls, 
                            boolean isVararg,
                            List<GenericParam> genericParams) {
            super(TypeKind.Func, name, genericParams);
            this.returnType = returnType;
            this.parameterDecls = parameterDecls;
            this.isVararg = isVararg;
        }
        
        @Override
        public TypeInfo copy() {
            return new FuncTypeInfo(this.name, this.returnType.copy(), this.parameterDecls, this.isVararg, this.genericParams);
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
            
            if(target.isKind(TypeKind.Any)) {
                return true;
            }
            
            return false;
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
            for(ParameterDecl p : this.parameterDecls) {
                if(!isFirst) params.append(", ");
                params.append(p);
                isFirst = false;
            }
            
            if(this.isVararg) {
                if(!isFirst) {
                    params.append(",");
                }
                params.append("...");
            }
            
            return String.format("func %s%s(%s) : %s", this.name, genParams, params, this.returnType);
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
        public TypeInfo copy() {
            return new StrTypeInfo(this.str);
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
            
            if(target.isKind(TypeKind.Any)) {
                return true;
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
        
        /**
         * @return the base type of the pointer
         */
        public TypeInfo getBaseType() {
            TypeInfo base = this.ptrOf;
            while(base != null && base.isKind(TypeKind.Ptr)) {
                PtrTypeInfo subPtr = base.as();
                base = subPtr.ptrOf;
            }
                
            return base;
        }
        
        @Override
        public TypeInfo copy() {
            return new PtrTypeInfo(this.ptrOf.copy());
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
            
            if(target.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = target.as();
                return this.ptrOf.getResolvedType().canCastTo(arrayInfo.arrayOf.getResolvedType());
            }
            
            if(target.isKind(TypeKind.Null)) {
                return true;
            }
            
            if(target.isKind(TypeKind.Any)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class ArrayTypeInfo extends TypeInfo {
        public TypeInfo arrayOf;
        public int length;
        public Expr lengthExpr;

        public ArrayTypeInfo(TypeInfo arrayOf, int length, Expr lengthExpr) {
            super(TypeKind.Array, "Array");
            this.arrayOf = arrayOf;
            this.length = length;
            this.lengthExpr = lengthExpr;
        }
        
        @Override
        public TypeInfo copy() {
            return new ArrayTypeInfo(copy(this.arrayOf), this.length, this.lengthExpr);
        }
        
        /**
         * @return the base type of the array
         */
        public TypeInfo getBaseType() {
            TypeInfo base = this.arrayOf;
            while(base != null && base.isKind(TypeKind.Array)) {
                ArrayTypeInfo subArray = base.as();
                base = subArray.arrayOf;
            }
                
            return base;
        }
        
        @Override
        public String getName() {        
            return arrayOf.getName() + "[]";
        }
        
        @Override
        public String toString() {
            return arrayOf.getName() + "[]";
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Identifier)) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = (ArrayTypeInfo)target;
                if(!this.arrayOf.strictEquals(arrayInfo.arrayOf)) {
                    return false;
                }
                                
                if(this.length != arrayInfo.length) {
                    return false;
                }
                
                return true;
            }
            
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                return this.arrayOf.canCastTo(ptrInfo.ptrOf.getResolvedType());
            }
            
            if(target.isKind(TypeKind.Any)) {
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
        public TypeInfo copy() {
            return this;
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
            
            if(target.isKind(TypeKind.Any)) {
                return true;
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
        public TypeInfo copy() {
            return this;
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
            
            return false;
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
        public TypeInfo copy() {
            return this;
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
        public List<TypeInfo> genericArgs;
        public TypeInfo resolvedType;
        public String identifier;
        
        public IdentifierTypeInfo(String identifier, List<TypeInfo> genericArgs) {
            super(TypeKind.Identifier, identifier);
            this.genericArgs = genericArgs;
            this.identifier = identifier;
            this.resolvedType = null;
        }
        
        @Override
        public TypeInfo copy() {
            return new IdentifierTypeInfo(this.identifier, copy(this.genericArgs));
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
        
        public void resolve(Module module, TypeInfo resolvedTo) {
            if(resolvedTo != null) {
                resolvedTo = Generics.createFromGenericTypeInfo(module, resolvedTo, this.genericArgs);
                this.resolvedType = resolvedTo;
                //this.sym = resolvedTo.sym; overrides the correct sym for declarations
            }
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
