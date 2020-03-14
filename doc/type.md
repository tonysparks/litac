# type

## type Imports



## type Variables

* const [numOfTypeInfos](#numOfTypeInfos): i64
* const [typeInfos](#typeInfos): [TypeInfo\*\*](#TypeInfo)


## type Types

* enum [Modifiers](#Modifiers)
* enum [TypeKind](#TypeKind)
* struct [EnumFieldInfo](#EnumFieldInfo)
* struct [FieldInfo](#FieldInfo)
* struct [GenericInfo](#GenericInfo)
* struct [ParamInfo](#ParamInfo)
* struct [TypeInfo](#TypeInfo)


## type Functions

* func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : char const*
* func [getTypeInfo](#getTypeInfo)(id: i64) : [TypeInfo\*](#TypeInfo)



***
### numOfTypeInfos


### typeInfos


### EnumFieldInfo


struct [EnumFieldInfo](#EnumFieldInfo)

* name: char const*
* value: i32



### FieldInfo


struct [FieldInfo](#FieldInfo)

* name: char const*
* type: i64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### GenericInfo


struct [GenericInfo](#GenericInfo)

* args: char const**
* numOfArgs: i32



### Modifiers


enum [Modifiers](#Modifiers)

* Using



### ParamInfo


struct [ParamInfo](#ParamInfo)

* genInfo: [type](#type)::[GenericInfo](#GenericInfo)
* name: char const*
* type: i64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### TypeInfo


struct [TypeInfo](#TypeInfo)

* kind: [type](#type)::[TypeKind](#TypeKind)
* name: char const*
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


func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : char const*


### getTypeInfo


func [getTypeInfo](#getTypeInfo)(id: i64) : [TypeInfo\*](#TypeInfo)


