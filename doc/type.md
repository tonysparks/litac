# type

## type Imports



## type Variables

* const [numOfTypeInfos](#numOfTypeInfos): usize
* const [typeInfos](#typeInfos): [\*\*TypeInfo](#TypeInfo)


## type Types

* enum [Modifiers](#Modifiers)
* enum [TypeKind](#TypeKind)
* struct [EnumFieldInfo](#EnumFieldInfo)
* struct [FieldInfo](#FieldInfo)
* struct [GenericInfo](#GenericInfo)
* struct [ParamInfo](#ParamInfo)
* struct [TypeInfo](#TypeInfo)


## type Functions

* func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : *const char
* func [getTypeInfo](#getTypeInfo)(id: i64) : [\*TypeInfo](#TypeInfo)



***
### numOfTypeInfos


### typeInfos


### EnumFieldInfo


struct [EnumFieldInfo](#EnumFieldInfo)

* name: *const char
* value: i32



### FieldInfo


struct [FieldInfo](#FieldInfo)

* name: *const char
* type: i64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### GenericInfo


struct [GenericInfo](#GenericInfo)

* args: **const char
* numOfArgs: i32



### Modifiers


enum [Modifiers](#Modifiers)

* Using



### ParamInfo


struct [ParamInfo](#ParamInfo)

* genInfo: [type](#type)::[GenericInfo](#GenericInfo)
* name: *const char
* type: i64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### TypeInfo


struct [TypeInfo](#TypeInfo)

* kind: [type](#type)::[TypeKind](#TypeKind)
* name: *const char
* id: i64
* <anonymous-union-0>: [type](#type)::[<anonymous\-union\-0>](#<anonymous\-union\-0>)



### TypeKind


enum [TypeKind](#TypeKind)

* Bool
* Char
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
* Str
* Array
* Ptr
* Null
* FuncPtr
* Struct
* Func
* Enum
* Union
* Void
* MaxTypeKind



### TypeKindAsStr


func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : *const char


### getTypeInfo


func [getTypeInfo](#getTypeInfo)(id: i64) : [\*TypeInfo](#TypeInfo)


