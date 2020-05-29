/*
 * see license.txt
 */
package litac.checker;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.ast.Node.*;
import litac.ast.TypeSpec.*;
import litac.compiler.*;
import litac.generics.GenericParam;
import litac.util.Names;


/**
 * Information about a type.  Most of these are fairly obvious in their nature, the one interesting one
 * is the "IdentifierTypeInfo" structure which represents an unresolved type at parse time.  It is up to
 * the TypeResolver to resolve the IdentifierTypeInfo into the approtate concrete TypeInfo.
 * 
 * @author Tony
 *
 */
public abstract class TypeInfo {
    private static final AtomicLong typeIdGen = new AtomicLong(1L);
    
    public static final TypeInfo BOOL_TYPE = new PrimitiveTypeInfo("bool", TypeKind.Bool);
    public static final TypeInfo CHAR_TYPE = new PrimitiveTypeInfo("char", TypeKind.Char);
    public static final TypeInfo I8_TYPE   = new PrimitiveTypeInfo("i8", TypeKind.i8);
    public static final TypeInfo U8_TYPE   = new PrimitiveTypeInfo("u8", TypeKind.u8);
    public static final TypeInfo I16_TYPE  = new PrimitiveTypeInfo("i16", TypeKind.i16);
    public static final TypeInfo U16_TYPE  = new PrimitiveTypeInfo("u16", TypeKind.u16);
    public static final TypeInfo I32_TYPE  = new PrimitiveTypeInfo("i32", TypeKind.i32);
    public static final TypeInfo U32_TYPE  = new PrimitiveTypeInfo("u32", TypeKind.u32);
    public static final TypeInfo I64_TYPE  = new PrimitiveTypeInfo("i64", TypeKind.i64);
    public static final TypeInfo U64_TYPE  = new PrimitiveTypeInfo("u64", TypeKind.u64);
    public static final TypeInfo F32_TYPE  = new PrimitiveTypeInfo("f32", TypeKind.f32);
    public static final TypeInfo F64_TYPE  = new PrimitiveTypeInfo("f64", TypeKind.f64);
    public static final TypeInfo USIZE_TYPE  = new PrimitiveTypeInfo("usize", TypeKind.usize);
    public static final TypeInfo NULL_TYPE = new NullTypeInfo();
    public static final TypeInfo VOID_TYPE = new VoidTypeInfo();
    
    public static final int FUNC_ISVARARG_FLAG = (1<<0);
    public static final int FUNC_ISMETHOD_FLAG = (1<<1);
    
    public static TypeInfo newForeignPrimitive(String name) {
        return new PrimitiveTypeInfo(name, TypeKind.Void);
    }
    
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
            case "f32":  return F32_TYPE;
            case "f64":  return F64_TYPE;
            case "usize":  return USIZE_TYPE;
            case "null":   return NULL_TYPE;
            case "void":   return VOID_TYPE;
            default: return null;
        }
    }
    
    public static TypeInfo getBase(TypeInfo type) {
        if(type == null) {
            return null;
        }
        
        switch(type.kind) {
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();
                return getBase(arrayInfo.arrayOf);
            }            
            case Const: {
                ConstTypeInfo constInfo = type.as();
                return getBase(constInfo.constOf);
            }
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                return getBase(ptrInfo.ptrOf);
            }
            case Str: {                
                return TypeInfo.CHAR_TYPE;
            }        
            default:
                return type;
        
        }
    }
    
    public static boolean isBooleanable(TypeInfo type) {
        switch(type.getKind()) {
            case Struct:
            case Union:
                return false;
            default:
                return true;
        }
    }

    public static boolean isFloat(TypeInfo type) {
        switch(type.getKind()) {            
            case f32:
            case f64:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isUnsignedInteger(TypeInfo type) {
        switch(type.getKind()) {            
            case Bool:
            case u16:
            case u32:
            case u64:
            case u8:
            case usize:
                return true;
            default:
                return false;
        }
    }

    
    public static boolean isSignedInteger(TypeInfo type) {
        switch(type.getKind()) {            
            case Bool:
            case Char:
            case Enum:
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
            case Const: {
                ConstTypeInfo constInfo = type.as();
                return isInteger(constInfo.constOf);
            }
            case Bool:
            case Char:
            case Enum:
            case i16:
            case i32:
            case i64:
            case i8:

            case u16:
            case u32:
            case u64:
            case u8:
            case usize:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isNumber(TypeInfo type) {
        switch(type.getKind()) {            
            case Bool:
            case Char:
            case Enum:
            case i16:
            case i32:
            case i64:
            case i8:

            case u16:
            case u32:
            case u64:
            case u8:
            case usize:
                
            case f32:
            case f64:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isFunc(TypeInfo type) {        
        if(type.isKind(TypeKind.Func) || type.isKind(TypeKind.FuncPtr)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isAggregate(TypeInfo type) {        
        if(type.isKind(TypeKind.Const)) {
            ConstTypeInfo constInfo = type.as();
            return isAggregate(constInfo.constOf);
        }
        
        return type.isKind(TypeKind.Struct) || type.isKind(TypeKind.Union);
    }
    
    public static boolean isPtrAggregate(TypeInfo type) {        
        if(isPtr(type)) {
            PtrTypeInfo ptrInfo = null;
            if(type.isKind(TypeKind.Const)) {
                ConstTypeInfo constInfo = type.as();
                ptrInfo = constInfo.constOf.as();
            }
            else {
                ptrInfo = type.as();
            }
            
            return isAggregate(ptrInfo.ptrOf);
        }
        return false;
    }
    
    public static boolean isFieldAccessible(TypeInfo type) {
        if(isAggregate(type) || isPtrAggregate(type) || isEnum(type)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isEnum(TypeInfo type) {
        return type.isKind(TypeKind.Enum);
    }
    
    public static boolean isPtrLike(TypeInfo type) {
        if(type.isKind(TypeKind.Ptr) || 
           type.isKind(TypeKind.Str) || 
           type.isKind(TypeKind.Array)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isConstPtr(TypeInfo type) {                
        if(!type.isKind(TypeKind.Ptr)) {
            return false;
        }
        
        PtrTypeInfo ptrInfo = type.as();        
        if(ptrInfo.ptrOf.isKind(TypeKind.Const)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isPtr(TypeInfo type) {
        if(type.isKind(TypeKind.Ptr)) {
            return true;
        }
        
        if(type.isKind(TypeKind.Const)) {
            ConstTypeInfo constInfo = type.as();
            return isPtr(constInfo.constOf);
        }
        
        return false;
    }
    
    public static boolean isVoidPtr(TypeInfo type) {
        if(isPtr(type)) {
            PtrTypeInfo ptrInfo = type.as();
            return ptrInfo.ptrOf.isKind(TypeKind.Void);
        }
        return false;
    }
    
    public static boolean isPrimitive(TypeInfo type) {
        if(type == null ) {
            return false;
        }
        
        switch(type.kind) {
            case Bool:
            case Char:
            case Null:
            case Void:
            case f32:
            case f64:
            case i16:
            case i32:
            case i64:
            case i8:
            case u16:
            case u32:
            case u64:
            case u8:
            case usize:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isArray(TypeInfo type) {
        if(type == null) {
            return false;
        }
        
        return type.isKind(TypeKind.Array);
    }
    
    public static boolean isBool(TypeInfo type) {
        if(type == null) {
            return false;
        }
        
        return type.isKind(TypeKind.Bool);
    }
    
    public static boolean isString(TypeInfo type) {
        if(type == null) {
            return false;
        }
        
        switch(type.kind) {
            case Str:
                return true;
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                switch(ptrInfo.ptrOf.kind) {
                    case Char:
                    case u8:
                    case i8:
                        return true;
                    default:
                        return false;
                }                
            }
            case Const: {                
                ConstTypeInfo constInfo = type.as();
                return isString(constInfo.constOf);                
            }
            default:
                return false;
        }
    }
    
    public static enum TypeKind {
        Bool,
        Char,
        i8,  u8,
        i16, u16,
        i32, u32,
        i64, u64,
        f32, f64,
        usize,
        
        Str,
        Array,
        Ptr,
        Null,
        FuncPtr,
        
        Struct,
        Func,
        Enum,
        Union,
        
        Void,
        Const,
        
        GenericParam,
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
    
    protected long typeId;
    protected TypeKind kind;
    public String name;
    public Symbol sym;
    
    TypeInfo(TypeKind kind, String name) {
        switch(kind) {
            case Func:
            case Struct:
            case Union:        
            case Enum:
                this.typeId = typeIdGen.incrementAndGet();
            default: 
        }
        this.kind = kind;
        this.name = name;
    }
    
    public long getTypeId() {
        return this.typeId;
    }
    
    @Override
    public String toString() {    
        return this.kind.name().toLowerCase();
    }
    
    public boolean isAnonymous() {
        return false;
    }
    
    public boolean hasGenerics() {
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
    
    public TypeSpec asTypeSpec() {
        SrcPos pos = (this.sym != null) ? this.sym.decl.getSrcPos() : null;
        return new NameTypeSpec(pos, this.name);
    }
    
    public boolean isPrimitive() {
        return false;
    }
    
    public boolean isGreater(TypeInfo other) {
        if(this == other) {
            return false;
        }
                
        return this.kind.ordinal() > other.kind.ordinal();
    }
        
    public boolean strictEquals(TypeInfo other) {
        if(other == this) {
            return true;
        }
        
        if(other.isKind(this.getKind())) {
            return other.getName().equals(this.getName());
        }
        
        return false;
    }
    
    public TypeInfo copy() {
        TypeInfo info = doCopy();
        info.typeId = this.typeId;
        info.sym = this.sym;
        
        return info;
    }
    public abstract boolean canCastTo(TypeInfo target);
    protected abstract TypeInfo doCopy();
    
    public static class ParamInfo {
        public TypeInfo type;
        public String name;
        public Expr defaultValue;
        public Attributes attributes;
        
        public ParamInfo(TypeInfo type, String name, Expr defaultValue, Attributes attributes) {
            this.type = type;
            this.name = name;
            this.defaultValue = defaultValue;
            this.attributes = attributes;
        }
        
        @Override
        public String toString() {        
            return String.format("%s: %s", this.name, this.type);
        }
    }
    
    public static class FieldInfo {
        public TypeInfo type;
        public String name;
        public String genericArg;
        public Attributes attributes;
        
        public FieldInfo(TypeInfo type, String name, Attributes attributes, String genericArg) {
            this.type = type;
            this.name = name;
            this.attributes = attributes;
            this.genericArg = genericArg;
        }
    }
    
    public static class EnumFieldInfo {        
        public String name;
        public Expr value;
        public Attributes attributes;
        
        public EnumFieldInfo(String name, Expr value, Attributes attributes) {            
            this.name = name;
            this.value = value;
            this.attributes = attributes;
        }
    }
    
    public static abstract class GenericTypeInfo extends TypeInfo {
        public List<GenericParam> genericParams;
        
        public GenericTypeInfo(TypeKind kind, String name, List<GenericParam> genericParams) {
            super(kind, name);
            this.genericParams = genericParams;
        }
                
        @Override
        public boolean hasGenerics() {
            return !this.genericParams.isEmpty();
        }
        
        public String getBaseName() {
            String name = getName();
            int index = name.indexOf("<");
            if(index > -1) {
                return name.substring(0, index);
            }
            
            return name;
        }
        
        /**
         * @return true if the supplied name is a generic parameter
         */
        public boolean isGenericParam(String name) {
            return this.genericParams.stream().anyMatch(p -> p.name.equals(name));
        }
        
        @Override
        public boolean strictEquals(TypeInfo other) {
            if(other == this) {
                return true;
            }
            
            if(other.isKind(this.getKind())) {
                GenericTypeInfo genInfo = other.as();
                if(genInfo.hasGenerics() && this.hasGenerics()) {
                    return genInfo.getName().equals(this.getName());
                }
                
                return genInfo.getBaseName()
                         .equals(this.getBaseName());
            }
            
            return false;
        }
        
    }
    public static abstract class AggregateTypeInfo extends GenericTypeInfo {
        public static final int IS_ANONYMOUS = (1<<0);
        public static final int IS_EMBEDDED  = (1<<1);
        
        public List<FieldInfo> fieldInfos;
        public List<FieldInfo> usingInfos;
        public int flags;
        
        private Map<String, FieldPath> pathCache;
        
        AggregateTypeInfo(TypeKind kind, 
                          String name, 
                          List<GenericParam> genericParams, 
                          List<FieldInfo> fieldInfos, 
                          int flags) {
            super(kind, name, genericParams);
            
            this.fieldInfos = new ArrayList<>();
            this.flags = flags;
            this.pathCache = new HashMap<>();
            
            for(FieldInfo field : fieldInfos) {
                addField(field);
            }
        }
        
        public void addField(FieldInfo field) {
            this.fieldInfos.add(field);
            if(field.attributes.isUsing()) {
                if(this.usingInfos == null) {
                    this.usingInfos = new ArrayList<>();
                }
                
                this.usingInfos.add(field);
            }
        }
        
        public boolean hasUsingFields() {
            return this.usingInfos != null && !this.usingInfos.isEmpty();
        }
        
        @Override
        public boolean isAnonymous() {
            return (this.flags & IS_ANONYMOUS) > 0;
        }
        
        public boolean isEmbedded() {
            return (this.flags & IS_EMBEDDED) > 0;
        }
            
        public FieldPath getFieldPath(String field) {
            if(!this.pathCache.containsKey(field)) {
                this.pathCache.put(field, new FieldPath(this, field));
            }
            
            return this.pathCache.get(field);            
        }
        
        public FieldInfo getField(String field) {
            for(FieldInfo f : this.fieldInfos) {
                if(f.name.equals(field)) {
                    return f;
                }                
            }
            return null;
        }
        
        public FieldInfo getFieldByPosition(int index) {
            int fieldCount = 0;
            for(int i = 0; i < this.fieldInfos.size() && i <= fieldCount; i++) {
                FieldInfo field = this.fieldInfos.get(i);
                
                if(field.type.isAnonymous()) {                    
                    FieldInfo anonField = getFieldByPosition(index - fieldCount);
                    if(anonField != null) {
                        return anonField;
                    }
                    
                    if(TypeInfo.isAggregate(field.type)) {
                        AggregateTypeInfo anonAgg = field.type.as();
                        fieldCount += anonAgg.fieldInfos.size();
                    }
                    else {
                        fieldCount += 1;
                    }
                    
                    continue;
                }
                else if(fieldCount == index) {
                    return field;
                }
                
                fieldCount++;
            }
            
            return null;
        }
        
        public FieldInfo getFieldWithAnonymous(String field) {
            for(FieldInfo f : this.fieldInfos) {
                if(f.name.equals(field)) {
                    return f;
                }
                
                if(f.type.isAnonymous()) {
                    AggregateTypeInfo aggInfo = f.type.as();
                    FieldInfo result = aggInfo.getFieldWithAnonymous(field);
                    if(result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
        
        public boolean isUsingField(FieldInfo field) {
            if(!hasUsingFields()) {
                return false;
            }
            
            for(FieldInfo f : this.usingInfos) {
                if(f.name.equals(field.name)) {
                    return true;
                }
            }
            
            return false;
        }
        
        public boolean isUsingType(TypeInfo type) {            
            return isUsingType(this, type);
        }
        
        private boolean isUsingType(AggregateTypeInfo aggInfo, TypeInfo type) {
            if(!aggInfo.hasUsingFields()) {
                return false;
            }
            
            for(FieldInfo field : aggInfo.usingInfos) {
                if(TypeInfo.getBase(field.type).strictEquals(type)) {
                    return true;
                }
            }
            
            for(FieldInfo field : aggInfo.usingInfos) {
                if(isUsingType(TypeInfo.getBase(field.type).as(), type)) {
                    return true;
                }
            }
            
            return false;
        }
        
        public FieldInfo getFieldUsingType(TypeInfo type) {
            if(!hasUsingFields()) {
                return null;
            }
            
            for(FieldInfo field : this.usingInfos) {
                if(TypeInfo.getBase(field.type).strictEquals(type)) {
                    return field;
                }
            }
            
            return null;
        }
        
        public FieldPath getFieldPathUsingType(TypeInfo type) {
            return new FieldPath(this, type);
        }
        
        @Override
        public TypeSpec asTypeSpec() {
            SrcPos pos = (this.sym != null) ? this.sym.decl.getSrcPos() : null;
            List<TypeSpec> genArgs = new ArrayList<>();
            // TODO
            for(GenericParam p : this.genericParams) {
                genArgs.add(new NameTypeSpec(pos, p.name));
            }
            
            return new NameTypeSpec(pos, this.name, genArgs);
        }
    }
    public static class StructTypeInfo extends AggregateTypeInfo {

        public StructTypeInfo(String name, 
                              List<GenericParam> genericParams, 
                              List<FieldInfo> fieldInfos, 
                              int flags) {
            
            super(TypeKind.Struct, name, genericParams, fieldInfos, flags);
        }
        
        @Override
        public String toString() {        
            return "struct " + this.name;
        }
                
        @Override
        protected TypeInfo doCopy() {
            return new StructTypeInfo(this.name, new ArrayList<>(this.genericParams), fieldInfos, flags);
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Struct)) {
                StructTypeInfo targetStruct = target.as();
                
                if(isUsingType(target)) {
                    return true;
                }
                
                if(this.fieldInfos.size() < targetStruct.fieldInfos.size()) {
                    return false;
                }
                
                if(this.fieldInfos.size() > targetStruct.fieldInfos.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.fieldInfos.size(); i++) {
                    FieldInfo targetField = targetStruct.fieldInfos.get(i);
                    FieldInfo thisField   = this.fieldInfos.get(i);
                    
                    if(!targetField.type.strictEquals(thisField.type)) {
                        return false;
                    }
                }
                
                return true;
            }
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class UnionTypeInfo extends AggregateTypeInfo {

        public UnionTypeInfo(String name, 
                             List<GenericParam> genericParams, 
                             List<FieldInfo> fieldInfos, 
                             int flags) {
            
            super(TypeKind.Union, name, genericParams, fieldInfos, flags);
        }
        
        @Override
        public String toString() {        
            return "union " + this.name;
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new UnionTypeInfo(this.name, new ArrayList<>(this.genericParams), this.fieldInfos, this.flags);
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
                        
            if(target.isKind(TypeKind.Union)) {
                UnionTypeInfo targetUnion = target.as();
                
                if(isUsingType(target)) {
                    return true;
                }
                
                if(this.fieldInfos.size() < targetUnion.fieldInfos.size()) {
                    return false;
                }
                
                if(this.fieldInfos.size() > targetUnion.fieldInfos.size()) {
                    return false;
                }
                
                for(int i = 0; i < this.fieldInfos.size(); i++) {
                    FieldInfo targetField = targetUnion.fieldInfos.get(i);
                    FieldInfo thisField   = this.fieldInfos.get(i);
                    
                    if(!targetField.type.strictEquals(thisField.type)) {
                        return false;
                    }
                }
                
                return true;
            }
            
            if(target.isKind(TypeKind.Bool)) {
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
        
        public EnumFieldInfo getField(String fieldName) {
            return this.fields.stream()
                        .filter(f -> f.name.equals(fieldName))
                        .findFirst()
                        .orElse(null);
        }
        
        public int indexOf(String fieldName) {
            for(int index = 0; index < this.fields.size(); index++) {
                if(this.fields.get(index).name.equals(fieldName)) {
                    return index;
                }
            }
            
            return -1;
        }
        
        public TypeInfo getFieldType() {
            return TypeInfo.I32_TYPE;
        }
        
        @Override
        public String toString() {        
            return "enum " + this.name;
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new EnumTypeInfo(this.name, this.fields);
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
                        
            if(target.isKind(TypeKind.Enum)) {
                return this.name.equals(target.name);
            }
            
            if(TypeInfo.isInteger(target)) {
                return true;
            }
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class FuncTypeInfo extends GenericTypeInfo {
        public TypeInfo returnType;
        public List<ParamInfo> parameterDecls;
        public int flags;
        
        public FuncTypeInfo(String name, 
                            TypeInfo returnType, 
                            List<ParamInfo> parameterDecls, 
                            int flags,
                            List<GenericParam> genericParams) {
            super(TypeKind.Func, name, genericParams);
            this.returnType = returnType;
            this.parameterDecls = parameterDecls;
            this.flags = flags;
        }
        
        public static String getMethodName(TypeInfo recvInfo, String funcName) {            
            return Names.methodName(recvInfo, funcName);
        }
        
        public TypeInfo getReceiverType() {
            if(this.parameterDecls.isEmpty() || !isMethod()) {
                return null;
            }
            
            return this.parameterDecls.get(0).type;
        }
        
        public String getMethodName() {
            TypeInfo recvInfo = getReceiverType();
            return getMethodName(recvInfo, getName());
        }
        
        public boolean isVararg() {
            return (this.flags & FUNC_ISVARARG_FLAG) > 0;
        }
        
        public boolean isMethod() {
            return (this.flags & FUNC_ISMETHOD_FLAG) > 0;
        }
                
        public FuncPtrTypeInfo asPtr() {
            List<TypeInfo> params = this.parameterDecls.stream().map(p -> p.type).collect(Collectors.toList());
            FuncPtrTypeInfo funcPtrInfo = new FuncPtrTypeInfo(this.returnType, params, isVararg(), new ArrayList<>(this.genericParams));
            //funcPtrInfo.sym = sym;
            return funcPtrInfo;
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new FuncTypeInfo(this.name, this.returnType.copy(), this.parameterDecls, this.flags, new ArrayList<>(this.genericParams));
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
                        
            if(target.isKind(TypeKind.Func) || 
               target.isKind(TypeKind.FuncPtr)) {
                
                FuncPtrTypeInfo funcType = target.isKind(TypeKind.FuncPtr) 
                                                ? target.as() 
                                                : ((FuncTypeInfo)target.as()).asPtr();
                
                if(this.parameterDecls.size() != funcType.params.size()) {
                    return false;
                }
                
                if(this.isVararg() != funcType.isVararg) {
                    return false;
                }
                
                for(int i = 0; i < this.parameterDecls.size(); i++) {
                    if(!this.parameterDecls.get(i).type.strictEquals(funcType.params.get(i))) {
                        return false;
                    }
                }
                
                return this.returnType.strictEquals(funcType.returnType);
                       
            }
                        
            if(target.isKind(TypeKind.Bool)) {
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
            for(ParamInfo p : this.parameterDecls) {
                if(!isFirst) params.append(", ");
                params.append(p);
                isFirst = false;
            }
            
            if(this.isVararg()) {
                if(!isFirst) {
                    params.append(",");
                }
                params.append("...");
            }
            
            return String.format("func %s%s(%s) : %s", this.name, genParams, params, this.returnType);
        }
    }
    
    public static class FuncPtrTypeInfo extends GenericTypeInfo {
        public TypeInfo returnType;
        public List<TypeInfo> params;
        public boolean isVararg;
        
        public FuncPtrTypeInfo(TypeInfo returnType, 
                            List<TypeInfo> params, 
                            boolean isVararg,
                            List<GenericParam> genericParams) {
            super(TypeKind.FuncPtr, "_", genericParams);
            this.returnType = returnType;
            this.params = params;
            this.isVararg = isVararg;
        }
        
        @Override
        public TypeSpec asTypeSpec() {
            List<TypeSpec> args = new ArrayList<>();
            for(TypeInfo p : params) {
                args.add(p.asTypeSpec());
            }
            
            SrcPos pos = (this.sym != null) ? this.sym.decl.getSrcPos() : null;
            return new FuncPtrTypeSpec(pos, args, this.returnType.asTypeSpec(), this.isVararg, this.genericParams);
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new FuncPtrTypeInfo(this.returnType.copy(), copy(this.params), this.isVararg, new ArrayList<>(this.genericParams));
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Func)) {
                FuncTypeInfo funcType = target.as();
                if(this.params.size() != funcType.parameterDecls.size()) {
                    return false;
                }
                
                if(this.isVararg != funcType.isVararg()) {
                    return false;
                }
                
                for(int i = 0; i < this.params.size(); i++) {
                    if(!this.params.get(i).strictEquals(funcType.parameterDecls.get(i).type)) {
                        return false;
                    }
                }
                
                return this.returnType.strictEquals(funcType.returnType);
                       
            }
            
            if(target.isKind(TypeKind.FuncPtr)) {
                FuncPtrTypeInfo funcType = target.as();
                if(this.params.size() != funcType.params.size()) {
                    return false;
                }
                
                if(this.isVararg != funcType.isVararg) {
                    return false;
                }
                
                for(int i = 0; i < this.params.size(); i++) {
                    if(!this.params.get(i).strictEquals(funcType.params.get(i))) {
                        return false;
                    }
                }
                
                return this.returnType.strictEquals(funcType.returnType);
                       
            }
            
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                if(ptrInfo.ptrOf.isKind(TypeKind.Void)) {
                    return true;
                }
                return false;
            }
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }
            
            return false;
        }
        
        @Override
        public String getName() {
            return toString();
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
            for(TypeInfo p : this.params) {
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
            
            return String.format("func%s(%s) : %s", genParams, params, this.returnType);
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
            return "*char";
        }
        
        @Override
        public TypeSpec asTypeSpec() {
            TypeSpec baseSpec = TypeInfo.CHAR_TYPE.asTypeSpec();
            return new PtrTypeSpec(baseSpec.pos, baseSpec);
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new StrTypeInfo(this.str);
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Str)) {
                return true;
            }
            
            // Account for c style strings
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                
                TypeInfo ptrOf = ptrInfo.ptrOf;
                if(ptrInfo.ptrOf.isKind(TypeKind.Const)) {
                    ConstTypeInfo constInfo = ptrInfo.ptrOf.as();
                    ptrOf = constInfo.constOf;
                }
                
                if(ptrOf.isKind(TypeKind.Char) ||
                   ptrOf.isKind(TypeKind.u8) ||
                   ptrOf.isKind(TypeKind.i8)) {
                    return true;
                }
                
                return false;                
            }
            
            if(target.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = target.as();
                if(arrayInfo.arrayOf.isKind(TypeKind.Char)) {
                    if(arrayInfo.length > 0) {
                        // TODO length check??
                        if(arrayInfo.length < this.str.length() + 1) {
                            // error?? 
                        }
                    }
                    return true;
                }
                
                return false;
            }
            
            // constant pointers/arrays/strings
            if(target.isKind(TypeKind.Const)) {
                ConstTypeInfo constInfo = target.as();
                
                TypeInfo constOf = constInfo.constOf; 
                if(constOf.isKind(TypeKind.Ptr)) {
                    PtrTypeInfo ptrInfo = constOf.as();
                    
                    TypeInfo ptrOf = ptrInfo.ptrOf;
                    if(ptrInfo.ptrOf.isKind(TypeKind.Const)) {
                        ConstTypeInfo c = ptrInfo.ptrOf.as();
                        ptrOf = c.constOf;
                    }
                    
                    if(ptrOf.isKind(TypeKind.Char) ||
                       ptrOf.isKind(TypeKind.u8) ||
                       ptrOf.isKind(TypeKind.i8)) {
                        return true;
                    }
                    
                    return false;
                }
                else if(constOf.isKind(TypeKind.Array)) {                    
                    ArrayTypeInfo arrayInfo = constOf.as();
                    if(arrayInfo.arrayOf.isKind(TypeKind.Char)) {
                        if(arrayInfo.length > 0) {
                            // TODO length check??
                            if(arrayInfo.length < this.str.length() + 1) {
                                // error?? 
                            }
                        }
                        return true;
                    }
                    
                    return false;
                }
                
                return (constOf.isKind(TypeKind.Str));
            }
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }
            
            if(isInteger(target)) {
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
            if(base.isKind(TypeKind.Const)) {
                ConstTypeInfo constInfo = base.as();
                base = constInfo.constOf;
            }
            
            while(base != null && base.isKind(TypeKind.Ptr)) {
                PtrTypeInfo subPtr = base.as();
                base = subPtr.getBaseType();
            }
                
            return base;
        }
                
        @Override
        public TypeSpec asTypeSpec() {
            TypeSpec baseSpec = this.ptrOf.asTypeSpec();
            return new PtrTypeSpec(baseSpec.pos, baseSpec);
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new PtrTypeInfo(this.ptrOf.copy());
        }
                
        @Override
        public String getName() {
            return "*" + ptrOf.getName();
        }
        
        @Override
        public String toString() {    
            return "*" + ptrOf.toString();
        }
                
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
                 
            boolean isConstPtr = this.ptrOf.isKind(TypeKind.Const);
            
            if(target.isKind(TypeKind.Const) && !isConstPtr) {
                ConstTypeInfo constInfo = target.as();
                return canCastTo(constInfo.constOf);
            }
            
            if(target.isKind(TypeKind.Array)) {
                ArrayTypeInfo arrayInfo = target.as();
                return this.ptrOf.canCastTo(arrayInfo.arrayOf);
            }
            
            if(target.isKind(TypeKind.Null)) {
                return true;
            }
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }
            
            if(isInteger(target)) {
                return true;
            }
            
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                TypeInfo targetPtrOf = ptrInfo.ptrOf;
                boolean isTargetConst = targetPtrOf.isKind(TypeKind.Const); 
                
                // target must be a const pointer too, if this is a const ptr
                if(isConstPtr && !isTargetConst) {
                    return false;
                }
                else {
                    if(isTargetConst) {
                        // target is const, which is fine
                        ConstTypeInfo constInfo = targetPtrOf.as();
                        targetPtrOf = constInfo.constOf;
                    }
                }
                
                if(targetPtrOf.isKind(TypeKind.Void)) {
                    return true;
                }
                                
                return this.ptrOf.canCastTo(targetPtrOf);
            }
            
            if(target.isKind(TypeKind.Str)) {
                
                // target must be a const pointer too, if this is a const ptr
                if(isConstPtr) {
                    return false;    
                }
                
                if(this.ptrOf.isKind(TypeKind.Char)) {
                    return true;
                }                
            }
            
            if(target.isKind(TypeKind.FuncPtr)) {
                return this.ptrOf.isKind(TypeKind.Void);
            }
            
            return false;
        }
    }
    
    public static class ConstTypeInfo extends TypeInfo {
        public TypeInfo constOf;

        public ConstTypeInfo(TypeInfo constOf) {
            super(TypeKind.Const, "const");
            this.constOf = constOf;
        }
        
        @Override
        public TypeSpec asTypeSpec() {
            TypeSpec baseSpec = this.constOf.asTypeSpec();
            return new ConstTypeSpec(baseSpec.pos, baseSpec);
        }
                
        @Override
        protected TypeInfo doCopy() {
            return new ConstTypeInfo(this.constOf.copy());
        }
                
        @Override
        public String getName() {
            return "const " + this.constOf.getName();
        }
        
        @Override
        public String toString() {    
            return getName();
        }
        
        public TypeInfo getBaseType() {
            switch(constOf.getKind()) {
                case Ptr: {
                    PtrTypeInfo ptrInfo = constOf.as();
                    return ptrInfo.ptrOf;
                }
                case Array: {
                    ArrayTypeInfo arrayInfo = constOf.as();
                    return arrayInfo.arrayOf;
                }
                case Str: {                    
                    return TypeInfo.CHAR_TYPE;
                }
                default: {
                    return constOf;
                }
            }
        }
                
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Const)) {
                ConstTypeInfo constInfo = target.as();
                return this.constOf.canCastTo(constInfo.constOf);
            }
            
            if(this.constOf.canCastTo(target)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class ArrayTypeInfo extends TypeInfo {
        public TypeInfo arrayOf;
        public long length;
        public Expr lengthExpr;

        public ArrayTypeInfo(TypeInfo arrayOf, long length, Expr lengthExpr) {
            super(TypeKind.Array, "Array");
            this.arrayOf = arrayOf;
            this.length = length;
            this.lengthExpr = lengthExpr;
        }
        
        @Override
        protected TypeInfo doCopy() {
            return new ArrayTypeInfo(copy(this.arrayOf), this.length, (this.lengthExpr != null) ? this.lengthExpr.copy() : null);
        }
        
        @Override
        public TypeSpec asTypeSpec() {
            TypeSpec baseSpec = this.arrayOf.asTypeSpec();
            return new ArrayTypeSpec(baseSpec.pos, baseSpec, lengthExpr);
        }
        
        public PtrTypeInfo decay() {
            return new PtrTypeInfo(this.arrayOf);
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
            return "[]" + arrayOf.getName();
        }
        
        @Override
        public String toString() {
            return String.format("[%s]%s", this.length > -1 ? String.valueOf(this.length) : "", arrayOf.getName());
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
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
            
            if(isVoidPtr(target)) {
                return true;
            }
            
            if(target.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = target.as();
                return this.arrayOf.canCastTo(ptrInfo.ptrOf);
            }
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class PrimitiveTypeInfo extends TypeInfo {        
        
        private PrimitiveTypeInfo(String name, TypeKind kind) {
            super(kind, name);            
            this.typeId = typeIdGen.getAndIncrement();
        }
        
        @Override
        protected TypeInfo doCopy() {
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
            
            if(target.isKind(TypeKind.Bool)) {
                return true;
            }

            if(isInteger(this)) {
                if(target.isKind(TypeKind.Str)  ||
                   target.isKind(TypeKind.Enum) ||
                   target.isKind(TypeKind.Ptr)) {
                    return true;
                }
            }
            
            if(target.isKind(TypeKind.Const)) {
                ConstTypeInfo constInfo = target.as();
                return canCastTo(constInfo.constOf);
            }
            
            // Warnings, allow everything to be casted?
            if(target.isPrimitive()) {
                if(isFloat(this) && isInteger(target)) {
                    return false;
                }
                
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
            this.typeId = typeIdGen.getAndIncrement();
        }
        
        @Override
        public String toString() {    
            return "void";
        }
        
        @Override
        protected TypeInfo doCopy() {
            return this;
        }

        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Void)) {
                return true;
            }
            
            return false;
        }
    }
    
    public static class NullTypeInfo extends TypeInfo {
        private NullTypeInfo() {
            super(TypeKind.Null, "null");
            this.typeId = typeIdGen.getAndIncrement();
        }
        
        @Override
        public String toString() {    
            return "null";
        }
        
        @Override
        protected TypeInfo doCopy() {
            return this;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {
            if(target == this) {
                return true;
            }
            
            if(target.isKind(TypeKind.Null)) {
                return true;
            }
            
            return true;
        }
    }
    
    public static class GenericParamTypeInfo extends TypeInfo {
        public String genericName;
        
        public GenericParamTypeInfo(String genericName) {
            super(TypeKind.GenericParam, genericName);
            this.genericName = genericName;
        }
        
        @Override
        public boolean canCastTo(TypeInfo target) {        
            return true;
        }
        
        @Override
        protected TypeInfo doCopy() {        
            return this;
        }
        
        @Override
        public String toString() {        
            return this.genericName;
        }
    }
    
}
