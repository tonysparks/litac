/*
 * see license.txt
 */
package litac.ast;

import java.util.List;


/**
 * @author Tony
 *
 */
public abstract class TypeInfo {
    
    public static enum TypeKind {
        bool,
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
    }
    
    public TypeKind kind;
    public String name;
    
    TypeInfo(TypeKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }
    
    @Override
    public String toString() {    
        return this.kind.name();
    }
    
    public boolean isKind(TypeKind kind) {
        return this.kind == kind;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends TypeInfo> T as() {
        return (T) this;
    }
    
    public boolean isPrimitive() {
        return false;
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
        
        return me.kind.ordinal() < them.kind.ordinal();
    }
        
    public boolean strictEquals(TypeInfo other) {
        if(other == this) {
            return true;
        }
        
        if(other.kind.equals(this.kind)) {                
            return other.name.equals(this.name);
        }
        
        return false;
    }
    
    public abstract boolean canCastTo(TypeInfo target);

    
    public static class Field {
        public TypeInfo type;
        public String name;
        
        
        /**
         * @param type
         * @param name
         */
        public Field(TypeInfo type, String name) {
            this.type = type;
            this.name = name;
        }
    }
    
    public static class EnumField {        
        public String name;
        public Expr value;
        
        
        /**
         * @param name
         * @param value
         */
        public EnumField(String name, Expr value) {            
            this.name = name;
            this.value = value;
        }
    }
    
    public static class Parameter {
        public TypeInfo type;
        public String name;
        
        /**
         * @param type
         * @param name
         */
        public Parameter(TypeInfo type, String name) {
            this.type = type;
            this.name = name;
        }
        
        @Override
        public String toString() {    
            return this.name + ": " + this.type;
        }
    }
    
    
    public static class StructTypeInfo extends TypeInfo {
        public List<Field> fields;

        /**
         * @param name
         * @param fields
         */
        public StructTypeInfo(String name, List<Field> fields) {
            super(TypeKind.Struct, name);
            this.fields = fields;
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
                
                if(this.fields.size() < targetStruct.fields.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.fields.size(); i++) {
                    Field targetField = targetStruct.fields.get(i);
                    Field thisField   = this.fields.get(i);
                    
                    // TODO
                }
            }
            return false;
        }
    }
    
    public static class UnionTypeInfo extends TypeInfo {
        public List<Field> fields;

        /**
         * @param name
         * @param fields
         */
        public UnionTypeInfo(String name, List<Field> fields) {
            super(TypeKind.Union, name);
            this.fields = fields;
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
                
                if(this.fields.size() < targetUnion.fields.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.fields.size(); i++) {
                    Field targetField = targetUnion.fields.get(i);
                    Field thisField   = this.fields.get(i);
                    
                    // TODO
                }
            }
            return false;
        }
    }
    
    public static class EnumTypeInfo extends TypeInfo {
        public List<EnumField> fields;

        /**
         * @param name
         * @param fields
         */
        public EnumTypeInfo(String name, List<EnumField> fields) {
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
        public List<Parameter> parameters;
        
        /**
         * @param name
         * @param returnType
         * @param parameters
         */
        public FuncTypeInfo(String name, TypeInfo returnType, List<Parameter> parameters) {
            super(TypeKind.Func, name);
            this.returnType = returnType;
            this.parameters = parameters;
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
                if(this.parameters.size() != funcType.parameters.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.parameters.size(); i++) {
                    if(!this.parameters.get(i).type.strictEquals(funcType.parameters.get(i).type)) {
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
            for(Parameter p : this.parameters) {
                if(!isFirst) sb.append(", ");
                sb.append(p);
                isFirst = false;
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
            
            return false;
        }
    }
    
    public static class PtrTypeInfo extends TypeInfo {
        public TypeInfo ptrOf;

        /**
         * @param name
         * @param ptrOf
         */
        public PtrTypeInfo(String name, TypeInfo ptrOf) {
            super(TypeKind.Ptr, name);
            this.ptrOf = ptrOf;
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
            
            if(target.kind == TypeKind.Identifier) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(idInfo.isResolved()) {
                    return canCastTo(idInfo.resolvedType);
                }
            }
            
            if(target.kind == TypeKind.Ptr) {
                PtrTypeInfo ptrInfo = (PtrTypeInfo)target;
                return this.ptrOf.canCastTo(ptrInfo.ptrOf);
            }
            
            return false;
        }
    }
    
    public static class ArrayTypeInfo extends TypeInfo {
        public TypeInfo arrayOf;
        public int numberOfDimensions;
        public List<Integer> dimensions;

        /**
         * @param name
         * @param arrayOf
         */
        public ArrayTypeInfo(String name, TypeInfo arrayOf, int numberOfDimensions, List<Integer> dimensions) {
            super(TypeKind.Array, name);
            this.arrayOf = arrayOf;
            this.numberOfDimensions = numberOfDimensions;
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
                
                if(this.numberOfDimensions != arrayInfo.numberOfDimensions) {
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
        
        public PrimitiveTypeInfo(String name, TypeKind kind) {
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
                return true;
            }
            
            return false;
        }
    }
    
    public static class VoidTypeInfo extends TypeInfo {
        public VoidTypeInfo() {
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
        public NullTypeInfo() {
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
            return this.identifier;
        }
        
        public boolean isResolved() {
            return this.resolvedType != null;
        }
        
        public void resolve(TypeInfo resolvedTo) {
            this.resolvedType = resolvedTo;
        }
        
        @Override
        public boolean isKind(TypeKind kind) {
            if(isResolved()) {
                return this.resolvedType.kind == kind;
            }
            
            return super.isKind(kind);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T extends TypeInfo> T as() {
            if(isResolved()) {
                return (T) this.resolvedType;
            }
            
            return (T) this;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
                        
            if(target.kind == this.kind) {
                IdentifierTypeInfo idInfo = (IdentifierTypeInfo)target;
                if(isResolved() && idInfo.isResolved()) {
                    return this.resolvedType.canCastTo(idInfo.resolvedType);
                }
            }
            else if(isResolved()) {
                return this.resolvedType.canCastTo(target);
            }
                
            return false;
        }
    }
}
