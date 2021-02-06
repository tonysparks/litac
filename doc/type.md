# type

## type Imports



## type Variables

* const [numOfTypeInfos](#numOfTypeInfos): usize
* const [typeInfos](#typeInfos): [\*\*TypeInfo](#TypeInfo)


## type Types

* enum [FieldInfoKind](#FieldInfoKind)
* enum [Modifiers](#Modifiers)
* enum [TypeKind](#TypeKind)
* struct [EnumFieldInfo](#EnumFieldInfo)
* struct [FieldInfo](#FieldInfo)
* struct [GenericInfo](#GenericInfo)
* struct [ParamInfo](#ParamInfo)
* struct [TypeInfo](#TypeInfo)


## type Functions

* func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : *const char
* func [getTypeInfo](#getTypeInfo)(id: u64) : [\*TypeInfo](#TypeInfo)



***
### numOfTypeInfos


### typeInfos


### EnumFieldInfo


struct [EnumFieldInfo](#EnumFieldInfo)

* name: *const char
* value: i32



### FieldInfo


struct [FieldInfo](#FieldInfo)

* kind: [type](#type)::[FieldInfoKind](#FieldInfoKind)
* name: *const char
* type: u64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### FieldInfoKind


enum [FieldInfoKind](#FieldInfoKind)

* VAR_FIELD
* ENUM_FIELD
* STRUCT_FIELD
* UNION_FIELD



### GenericInfo


struct [GenericInfo](#GenericInfo)

* args: **const char
* numOfArgs: i32



### Modifiers


enum [Modifiers](#Modifiers)

* None
* Using



### ParamInfo


struct [ParamInfo](#ParamInfo)

* genInfo: [type](#type)::[GenericInfo](#GenericInfo)
* name: *const char
* type: u64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### TypeInfo


struct [TypeInfo](#TypeInfo)

* kind: [type](#type)::[TypeKind](#TypeKind)
* name: *const char
* id: u64
* <anonymous-union-0>: [type](#type)::[<anonymous\-union\-0>](#<anonymous\-union\-0>)



### TypeKind


enum [TypeKind](#TypeKind)

* BOOL
* CHAR
* I8
* U8
* I16
* U16
* I32
* U32
* I64
* U64
* F32
* F64
* USIZE
* NULL
* VOID
* STR
* ARRAY
* PTR
* FUNC_PTR
* STRUCT
* UNION
* ENUM
* FUNC
* CONST
* GENERIC_PARAM
* POISON
* MAX_TYPE_KIND



### TypeKindAsStr


func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : *const char


### getTypeInfo


func [getTypeInfo](#getTypeInfo)(id: u64) : [\*TypeInfo](#TypeInfo)


