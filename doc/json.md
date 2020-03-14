# json

## json Imports

* [array](array)
* [assert](assert)
* [libc](libc)
* [map](map)
* [mem](mem)
* [string\_buffer](string\_buffer)


## json Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [defaultAllocator](#defaultAllocator): [Allocator const\*](#Allocator)
* const [HUGE\_VAL](#HUGE\_VAL): f64
* const [JSON\_FALSE](#JSON\_FALSE): [JsonNode\*](#JsonNode)
* const [JSON\_NULL](#JSON\_NULL): [JsonNode\*](#JsonNode)
* const [JSON\_TRUE](#JSON\_TRUE): [JsonNode\*](#JsonNode)
* const [M\_PI](#M\_PI): f64
* const [ULLONG\_MAX](#ULLONG\_MAX): i64
* const [cAllocator](#cAllocator): [Allocator const\*](#Allocator)
* const [debugAllocator](#debugAllocator): [DebugAllocator\*](#DebugAllocator)
* const [stderr](#stderr): [FILE\*](#FILE)
* const [stdout](#stdout): [FILE\*](#FILE)


## json Types

* enum [JsonType](#JsonType)
* enum [ParserStatus](#ParserStatus)
* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [Array](#Array)
* struct [DebugAllocator](#DebugAllocator)
* struct [FILE](#FILE)
* struct [JsonNode](#JsonNode)
* struct [MapEntry](#MapEntry)
* struct [MapIterator](#MapIterator)
* struct [Map](#Map)
* struct [Parser](#Parser)
* struct [StringBuffer](#StringBuffer)
* struct [va\_list](#va\_list)
* typedef [documentationGenerator](#documentationGenerator)::[Array<JsonNode\*>](#Array<JsonNode\*>) as [JsonArray](#JsonArray)
* typedef [documentationGenerator](#documentationGenerator)::[Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>) as [JsonObject](#JsonObject)
* typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)
* typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)
* union [JsonValue](#JsonValue)


## json Functions

* func [ABS](#ABS)(a: f32) : f32
* func [Allocator\_alloc](#Allocator\_alloc)(a: [Allocator const\*](#Allocator), size: u64) : [void\*](#void)
* func [Allocator\_calloc](#Allocator\_calloc)(a: [Allocator const\*](#Allocator), num: u64, size: u64) : [void\*](#void)
* func [Allocator\_free](#Allocator\_free)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void))
* func [Allocator\_realloc](#Allocator\_realloc)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void), oldsize: u64, newsize: u64) : [void\*](#void)
* func [Arena\_free](#Arena\_free)(arena: [Arena\*](#Arena))
* func [Arena\_init](#Arena\_init)(arena: [Arena\*](#Arena), size: u64, alloc: [Allocator const\*](#Allocator))
* func [Arena\_malloc](#Arena\_malloc)(arena: [Arena\*](#Arena), size: u64) : [void\*](#void)
* func [Array\_addAll](#Array\_addAll)(a: [Array<T>\*](#Array<T>), other: [Array<T>\*](#Array<T>))
* func [Array\_add](#Array\_add)(a: [Array<T>\*](#Array<T>), element: [T](#T))
* func [Array\_clear](#Array\_clear)(a: [Array<T>\*](#Array<T>))
* func [Array\_empty](#Array\_empty)(a: [Array<T>\*](#Array<T>)) : bool
* func [Array\_first](#Array\_first)(a: [Array<T>\*](#Array<T>)) : [T](#T)
* func [Array\_forEach](#Array\_forEach)(a: [Array<T>\*](#Array<T>), fn: [func<T>\(T\) : bool](#\_))
* func [Array\_free](#Array\_free)(a: [Array<T>\*](#Array<T>))
* func [Array\_get](#Array\_get)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)
* func [Array\_init](#Array\_init)(a: [Array<T>\*](#Array<T>), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [Array\_last](#Array\_last)(a: [Array<T>\*](#Array<T>)) : [T](#T)
* func [Array\_pop](#Array\_pop)(a: [Array<T>\*](#Array<T>)) : [T](#T)
* func [Array\_push](#Array\_push)(a: [Array<T>\*](#Array<T>), element: [T](#T))
* func [Array\_removeAt](#Array\_removeAt)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)
* func [Array\_set](#Array\_set)(a: [Array<T>\*](#Array<T>), index: i32, element: [T](#T))
* func [Array\_size](#Array\_size)(a: [Array<T>\*](#Array<T>)) : i32
* func [CreateJsonArray](#CreateJsonArray)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonNumber](#CreateJsonNumber)(value: f64, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonObject](#CreateJsonObject)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonStringNoDup](#CreateJsonStringNoDup)(str: char const*, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonString](#CreateJsonString)(str: char const*, len: i32, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))
* func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))
* func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)
* func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))
* func [JsonNode\_add](#JsonNode\_add)(node: [JsonNode\*](#JsonNode), n: [JsonNode\*](#JsonNode))
* func [JsonNode\_asArray](#JsonNode\_asArray)(node: [JsonNode\*](#JsonNode)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)
* func [JsonNode\_asBool](#JsonNode\_asBool)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_asFloat](#JsonNode\_asFloat)(node: [JsonNode\*](#JsonNode)) : f64
* func [JsonNode\_asInt](#JsonNode\_asInt)(node: [JsonNode\*](#JsonNode)) : i32
* func [JsonNode\_asLong](#JsonNode\_asLong)(node: [JsonNode\*](#JsonNode)) : i64
* func [JsonNode\_asNumber](#JsonNode\_asNumber)(node: [JsonNode\*](#JsonNode)) : f64
* func [JsonNode\_asObject](#JsonNode\_asObject)(node: [JsonNode\*](#JsonNode)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* func [JsonNode\_asString](#JsonNode\_asString)(node: [JsonNode\*](#JsonNode)) : char const*
* func [JsonNode\_at](#JsonNode\_at)(node: [JsonNode\*](#JsonNode), index: i32) : [JsonNode\*](#JsonNode)
* func [JsonNode\_free](#JsonNode\_free)(node: [JsonNode\*](#JsonNode))
* func [JsonNode\_getArray](#JsonNode\_getArray)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)
* func [JsonNode\_getBool](#JsonNode\_getBool)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: bool) : bool
* func [JsonNode\_getFloat](#JsonNode\_getFloat)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: f64) : f64
* func [JsonNode\_getInt](#JsonNode\_getInt)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i32) : i32
* func [JsonNode\_getLong](#JsonNode\_getLong)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i64) : i64
* func [JsonNode\_getObject](#JsonNode\_getObject)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)
* func [JsonNode\_getStrCopy](#JsonNode\_getStrCopy)(node: [JsonNode\*](#JsonNode), key: char const*, str: char*, len: i32) : char*
* func [JsonNode\_getStr](#JsonNode\_getStr)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: char const*) : char const*
* func [JsonNode\_get](#JsonNode\_get)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)
* func [JsonNode\_isArray](#JsonNode\_isArray)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isBool](#JsonNode\_isBool)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isFalse](#JsonNode\_isFalse)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isNull](#JsonNode\_isNull)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isNumber](#JsonNode\_isNumber)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isObject](#JsonNode\_isObject)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isString](#JsonNode\_isString)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isTrue](#JsonNode\_isTrue)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_print](#JsonNode\_print)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer)) : char const*
* func [JsonNode\_put](#JsonNode\_put)(node: [JsonNode\*](#JsonNode), key: char const*, n: [JsonNode\*](#JsonNode), len: i32)
* func [JsonNode\_size](#JsonNode\_size)(node: [JsonNode\*](#JsonNode)) : i32
* func [JsonTypeAsStr](#JsonTypeAsStr)(e: [json](#json)::[JsonType](#JsonType)) : char const*
* func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : bool
* func [MapIterator\_next](#MapIterator\_next)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<K,V>](#MapEntry<K,V>)
* func [Map\_contains](#Map\_contains)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : bool
* func [Map\_empty](#Map\_empty)(m: [Map<K,V>\*](#Map<K,V>)) : bool
* func [Map\_free](#Map\_free)(m: [Map<K,V>\*](#Map<K,V>))
* func [Map\_get](#Map\_get)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_init](#Map\_init)(m: [Map<K,V>\*](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_iter](#Map\_iter)(m: [Map<K,V>\*](#Map<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<K,V>](#MapIterator<K,V>)
* func [Map\_put](#Map\_put)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K), value: [V](#V))
* func [Map\_remove](#Map\_remove)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_size](#Map\_size)(m: [Map<K,V>\*](#Map<K,V>)) : i32
* func [Parser\_free](#Parser\_free)(p: [Parser\*](#Parser))
* func [Parser\_hasError](#Parser\_hasError)(p: [Parser\*](#Parser)) : bool
* func [Parser\_init](#Parser\_init)(p: [Parser\*](#Parser), alloc: [Allocator const\*](#Allocator))
* func [Parser\_parseJson](#Parser\_parseJson)(p: [Parser\*](#Parser), buffer: char const*) : [JsonNode\*](#JsonNode)
* func [PrintJson](#PrintJson)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer))
* func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool
* func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool
* func [StrHashFn](#StrHashFn)(str: char const*) : u32
* func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)
* func [StringBufferInit](#StringBufferInit)(initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)
* func [StringBuffer\_appendChar](#StringBuffer\_appendChar)(b: [StringBuffer\*](#StringBuffer), c: char) : i32
* func [StringBuffer\_appendFloat](#StringBuffer\_appendFloat)(b: [StringBuffer\*](#StringBuffer), f: f64) : i32
* func [StringBuffer\_appendI32](#StringBuffer\_appendI32)(b: [StringBuffer\*](#StringBuffer), i: i32) : i32
* func [StringBuffer\_appendI64](#StringBuffer\_appendI64)(b: [StringBuffer\*](#StringBuffer), i: i64) : i32
* func [StringBuffer\_appendStr](#StringBuffer\_appendStr)(b: [StringBuffer\*](#StringBuffer), str: char*, len: i32)
* func [StringBuffer\_appendU32](#StringBuffer\_appendU32)(b: [StringBuffer\*](#StringBuffer), i: u32) : i32
* func [StringBuffer\_appendU64](#StringBuffer\_appendU64)(b: [StringBuffer\*](#StringBuffer), i: u64) : i32
* func [StringBuffer\_append](#StringBuffer\_append)(b: [StringBuffer\*](#StringBuffer), format: char const*) : i32
* func [StringBuffer\_asStringView](#StringBuffer\_asStringView)(b: [StringBuffer\*](#StringBuffer)) : [string\_view](#string\_view)::[StringView](#StringView)
* func [StringBuffer\_cStr](#StringBuffer\_cStr)(b: [StringBuffer\*](#StringBuffer)) : char const*
* func [StringBuffer\_clear](#StringBuffer\_clear)(b: [StringBuffer\*](#StringBuffer))
* func [StringBuffer\_contains](#StringBuffer\_contains)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : bool
* func [StringBuffer\_copyTo](#StringBuffer\_copyTo)(b: [StringBuffer\*](#StringBuffer), buf: char*, len: i32, addZero: bool) : i32
* func [StringBuffer\_delete](#StringBuffer\_delete)(b: [StringBuffer\*](#StringBuffer), start: i32, end: i32)
* func [StringBuffer\_empty](#StringBuffer\_empty)(b: [StringBuffer\*](#StringBuffer)) : bool
* func [StringBuffer\_forEach](#StringBuffer\_forEach)(b: [StringBuffer\*](#StringBuffer), fn: [func\(char\) : bool](#\_))
* func [StringBuffer\_free](#StringBuffer\_free)(b: [StringBuffer\*](#StringBuffer))
* func [StringBuffer\_get](#StringBuffer\_get)(b: [StringBuffer\*](#StringBuffer), index: i32) : char
* func [StringBuffer\_indexOfAt](#StringBuffer\_indexOfAt)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32, fromIndex: i32) : i32
* func [StringBuffer\_indexOf](#StringBuffer\_indexOf)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : i32
* func [StringBuffer\_init](#StringBuffer\_init)(b: [StringBuffer\*](#StringBuffer), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [StringBuffer\_insert](#StringBuffer\_insert)(b: [StringBuffer\*](#StringBuffer), index: i32, format: char const*) : i32
* func [StringBuffer\_size](#StringBuffer\_size)(b: [StringBuffer\*](#StringBuffer)) : i32
* func [asinf](#asinf)(v: f32) : f32
* func [assert](#assert)(e: bool)
* func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)
* func [cos](#cos)(v: f64) : f64
* func [cosf](#cosf)(v: f32) : f32
* func [exit](#exit)(code: i32)
* func [fabs](#fabs)(v: f32) : f32
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fputs](#fputs)(format: char const*, f: [FILE\*](#FILE))
* func [fread](#fread)(buf: char*, size: u64, n: i64, stream: [FILE\*](#FILE)) : i64
* func [free](#free)(ptr: [void\*](#void))
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [isalnum](#isalnum)(arg: i32) : i32
* func [isdigit](#isdigit)(arg: i32) : i32
* func [isspace](#isspace)(arg: i32) : i32
* func [malloc](#malloc)(size: u64) : [void\*](#void)
* func [memcpy](#memcpy)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memduplicate](#memduplicate)(p: [void const\*](#void), len: u64, a: [Allocator const\*](#Allocator)) : [void\*](#void)
* func [memmove](#memmove)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memset](#memset)(ptr: [void\*](#void), value: i32, len: u64) : [void\*](#void)
* func [new](#new)(a: [Allocator const\*](#Allocator)) : [T\*](#T)
* func [printf](#printf)(s: char const*)
* func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)
* func [sin](#sin)(v: f64) : f64
* func [sinf](#sinf)(v: f32) : f32
* func [snprintf](#snprintf)(s: char*, n: u64, format: char const*) : i32
* func [sqrt](#sqrt)(v: f64) : f64
* func [sqrtf](#sqrtf)(v: f32) : f32
* func [strcmp](#strcmp)(a: char const*, b: char const*) : i32
* func [strcpy](#strcpy)(dest: char*, src: char const*) : char*
* func [strlen](#strlen)(str: char const*) : i32
* func [strncmp](#strncmp)(a: char const*, b: char const*, num: u64) : i32
* func [strncpy](#strncpy)(dest: char*, src: char const*, num: u64) : char*
* func [strtod](#strtod)(str: char const*, end: char**) : f64
* func [strtok](#strtok)(str: char*, delim: char const*) : char*
* func [strtol](#strtol)(str: char const*, end: char**, base: i32) : i64
* func [strtoul](#strtoul)(str: char const*, end: char**, base: i32) : u64
* func [tan](#tan)(v: f64) : f64
* func [tolower](#tolower)(arg: i32) : i32
* func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))
* func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: char const*)
* func [vfprintf](#vfprintf)(f: [FILE\*](#FILE), format: char const*, args: [libc](#libc)::[va\_list](#va\_list))
* func [vsnprintf\_s](#vsnprintf\_s)(ptr: [void\*](#void), len: u64, max: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsnprintf](#vsnprintf)(buffer: char*, len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsprintf\_s](#vsprintf\_s)(ptr: [void\*](#void), len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32



***
### HUGE\_VAL


### JSON\_FALSE


### JSON\_NULL


### JSON\_TRUE


### M\_PI


### SEEK\_CUR


### SEEK\_END


### SEEK\_SET


### ULLONG\_MAX


### cAllocator


### debugAllocator


### defaultAllocator


### stderr


### stdout


### Allocation


struct [Allocation](#Allocation)

* addr: [void\*](#void)
* size: u64
* line: u64
* filename: char[]



### Allocator


struct [Allocator](#Allocator)

* allocFn: [func\(Allocator const\*, u64\) : void\*](#\_)
* callocFn: [func\(Allocator const\*, u64, u64\) : void\*](#\_)
* reallocFn: [func\(Allocator const\*, void\*, u64, u64\) : void\*](#\_)
* freeFn: [func\(Allocator const\*, void\*\) : void](#\_)



### Arena


struct [Arena](#Arena)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [Allocator const\*](#Allocator)
* region: u8*
* size: u64
* current: u64
* next: [Arena\*](#Arena)



### Array


struct [Array](#Array)

* length: i32
* capacity: i32
* elements: [T\*](#T)
* alloc: [Allocator const\*](#Allocator)



### DebugAllocator


struct [DebugAllocator](#DebugAllocator)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [Allocator const\*](#Allocator)
* allocations: [documentationGenerator](#documentationGenerator)::[Array<Allocation\*>](#Array<Allocation\*>)



### EqualFn


typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)


### FILE


struct [FILE](#FILE)




### HashFn


typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


### JsonArray


typedef [documentationGenerator](#documentationGenerator)::[Array<JsonNode\*>](#Array<JsonNode\*>) as [JsonArray](#JsonArray)


### JsonNode


struct [JsonNode](#JsonNode)

* alloc: [Allocator const\*](#Allocator)
* type: [json](#json)::[JsonType](#JsonType)
* value: [json](#json)::[JsonValue](#JsonValue)



### JsonObject


typedef [documentationGenerator](#documentationGenerator)::[Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>) as [JsonObject](#JsonObject)


### JsonType


enum [JsonType](#JsonType)

* NULL
* BOOLEAN
* NUMBER
* STRING
* OBJECT
* ARRAY



### JsonValue


union [JsonValue](#JsonValue)

* boolValue: bool
* doubleValue: f64
* strValue: char const*
* objValue: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* arrayValue: [Array<JsonNode\*>\*](#Array<JsonNode\*>)



### Map


struct [Map](#Map)

* length: i32
* capacity: i32
* hashFn: [func\(K\) : u32](#\_)
* equalFn: [func\(K, K\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: [K\*](#K)
* values: [V\*](#V)
* emptyValue: [V](#V)



### MapEntry


struct [MapEntry](#MapEntry)

* key: [K](#K)
* value: [V](#V)



### MapIterator


struct [MapIterator](#MapIterator)

* m: [Map<K,V>\*](#Map<K,V>)
* it: i32
* count: i32



### Parser


struct [Parser](#Parser)

* alloc: [Allocator const\*](#Allocator)
* status: [json](#json)::[ParserStatus](#ParserStatus)
* errorMsg: char[]
* token: [json](#json)::[Token](#Token)
* buffer: [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)
* stream: char const*
* lineStart: char const*



### ParserStatus


enum [ParserStatus](#ParserStatus)

* OK
* WARNING
* ERROR



### StringBuffer


struct [StringBuffer](#StringBuffer)

* buffer: char*
* length: i32
* capacity: i32
* alloc: [Allocator const\*](#Allocator)



### va\_list


struct [va\_list](#va\_list)




### ABS


func [ABS](#ABS)(a: f32) : f32


### Allocator\_alloc


func [Allocator\_alloc](#Allocator\_alloc)(a: [Allocator const\*](#Allocator), size: u64) : [void\*](#void)


### Allocator\_calloc


func [Allocator\_calloc](#Allocator\_calloc)(a: [Allocator const\*](#Allocator), num: u64, size: u64) : [void\*](#void)


### Allocator\_free


func [Allocator\_free](#Allocator\_free)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void))


### Allocator\_realloc


func [Allocator\_realloc](#Allocator\_realloc)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void), oldsize: u64, newsize: u64) : [void\*](#void)


### Arena\_free


func [Arena\_free](#Arena\_free)(arena: [Arena\*](#Arena))


### Arena\_init


func [Arena\_init](#Arena\_init)(arena: [Arena\*](#Arena), size: u64, alloc: [Allocator const\*](#Allocator))


### Arena\_malloc


func [Arena\_malloc](#Arena\_malloc)(arena: [Arena\*](#Arena), size: u64) : [void\*](#void)


### Array\_add


func [Array\_add](#Array\_add)(a: [Array<T>\*](#Array<T>), element: [T](#T))


### Array\_addAll


func [Array\_addAll](#Array\_addAll)(a: [Array<T>\*](#Array<T>), other: [Array<T>\*](#Array<T>))


### Array\_clear


func [Array\_clear](#Array\_clear)(a: [Array<T>\*](#Array<T>))


### Array\_empty


func [Array\_empty](#Array\_empty)(a: [Array<T>\*](#Array<T>)) : bool


### Array\_first


func [Array\_first](#Array\_first)(a: [Array<T>\*](#Array<T>)) : [T](#T)


### Array\_forEach


func [Array\_forEach](#Array\_forEach)(a: [Array<T>\*](#Array<T>), fn: [func<T>\(T\) : bool](#\_))


### Array\_free


func [Array\_free](#Array\_free)(a: [Array<T>\*](#Array<T>))


### Array\_get


func [Array\_get](#Array\_get)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)


### Array\_init


func [Array\_init](#Array\_init)(a: [Array<T>\*](#Array<T>), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### Array\_last


func [Array\_last](#Array\_last)(a: [Array<T>\*](#Array<T>)) : [T](#T)


### Array\_pop


func [Array\_pop](#Array\_pop)(a: [Array<T>\*](#Array<T>)) : [T](#T)


### Array\_push


func [Array\_push](#Array\_push)(a: [Array<T>\*](#Array<T>), element: [T](#T))


### Array\_removeAt


func [Array\_removeAt](#Array\_removeAt)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)


### Array\_set


func [Array\_set](#Array\_set)(a: [Array<T>\*](#Array<T>), index: i32, element: [T](#T))


### Array\_size


func [Array\_size](#Array\_size)(a: [Array<T>\*](#Array<T>)) : i32


### CreateJsonArray


func [CreateJsonArray](#CreateJsonArray)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonNumber


func [CreateJsonNumber](#CreateJsonNumber)(value: f64, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonObject


func [CreateJsonObject](#CreateJsonObject)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonString


func [CreateJsonString](#CreateJsonString)(str: char const*, len: i32, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonStringNoDup


func [CreateJsonStringNoDup](#CreateJsonStringNoDup)(str: char const*, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### DebugAllocator\_free


func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))


### DebugAllocator\_init


func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))


### DebugAllocator\_malloc


func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)


### DebugAllocator\_report


func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))


### JsonNode\_add


func [JsonNode\_add](#JsonNode\_add)(node: [JsonNode\*](#JsonNode), n: [JsonNode\*](#JsonNode))


### JsonNode\_asArray


func [JsonNode\_asArray](#JsonNode\_asArray)(node: [JsonNode\*](#JsonNode)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)


### JsonNode\_asBool


func [JsonNode\_asBool](#JsonNode\_asBool)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_asFloat


func [JsonNode\_asFloat](#JsonNode\_asFloat)(node: [JsonNode\*](#JsonNode)) : f64


### JsonNode\_asInt


func [JsonNode\_asInt](#JsonNode\_asInt)(node: [JsonNode\*](#JsonNode)) : i32


### JsonNode\_asLong


func [JsonNode\_asLong](#JsonNode\_asLong)(node: [JsonNode\*](#JsonNode)) : i64


### JsonNode\_asNumber


func [JsonNode\_asNumber](#JsonNode\_asNumber)(node: [JsonNode\*](#JsonNode)) : f64


### JsonNode\_asObject


func [JsonNode\_asObject](#JsonNode\_asObject)(node: [JsonNode\*](#JsonNode)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)


### JsonNode\_asString


func [JsonNode\_asString](#JsonNode\_asString)(node: [JsonNode\*](#JsonNode)) : char const*


### JsonNode\_at


func [JsonNode\_at](#JsonNode\_at)(node: [JsonNode\*](#JsonNode), index: i32) : [JsonNode\*](#JsonNode)


### JsonNode\_free


func [JsonNode\_free](#JsonNode\_free)(node: [JsonNode\*](#JsonNode))


### JsonNode\_get


func [JsonNode\_get](#JsonNode\_get)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)


### JsonNode\_getArray


func [JsonNode\_getArray](#JsonNode\_getArray)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)


### JsonNode\_getBool


func [JsonNode\_getBool](#JsonNode\_getBool)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: bool) : bool


### JsonNode\_getFloat


func [JsonNode\_getFloat](#JsonNode\_getFloat)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: f64) : f64


### JsonNode\_getInt


func [JsonNode\_getInt](#JsonNode\_getInt)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i32) : i32


### JsonNode\_getLong


func [JsonNode\_getLong](#JsonNode\_getLong)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i64) : i64


### JsonNode\_getObject


func [JsonNode\_getObject](#JsonNode\_getObject)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)


### JsonNode\_getStr


func [JsonNode\_getStr](#JsonNode\_getStr)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: char const*) : char const*


### JsonNode\_getStrCopy


func [JsonNode\_getStrCopy](#JsonNode\_getStrCopy)(node: [JsonNode\*](#JsonNode), key: char const*, str: char*, len: i32) : char*


### JsonNode\_isArray


func [JsonNode\_isArray](#JsonNode\_isArray)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isBool


func [JsonNode\_isBool](#JsonNode\_isBool)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isFalse


func [JsonNode\_isFalse](#JsonNode\_isFalse)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isNull


func [JsonNode\_isNull](#JsonNode\_isNull)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isNumber


func [JsonNode\_isNumber](#JsonNode\_isNumber)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isObject


func [JsonNode\_isObject](#JsonNode\_isObject)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isString


func [JsonNode\_isString](#JsonNode\_isString)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isTrue


func [JsonNode\_isTrue](#JsonNode\_isTrue)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_print


func [JsonNode\_print](#JsonNode\_print)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer)) : char const*


### JsonNode\_put


func [JsonNode\_put](#JsonNode\_put)(node: [JsonNode\*](#JsonNode), key: char const*, n: [JsonNode\*](#JsonNode), len: i32)


### JsonNode\_size


func [JsonNode\_size](#JsonNode\_size)(node: [JsonNode\*](#JsonNode)) : i32


### JsonTypeAsStr


func [JsonTypeAsStr](#JsonTypeAsStr)(e: [json](#json)::[JsonType](#JsonType)) : char const*


### MAX


func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)


### MIN


func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)


### MapIterator\_hasNext


func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : bool


### MapIterator\_next


func [MapIterator\_next](#MapIterator\_next)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<K,V>](#MapEntry<K,V>)


### Map\_contains


func [Map\_contains](#Map\_contains)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : bool


### Map\_empty


func [Map\_empty](#Map\_empty)(m: [Map<K,V>\*](#Map<K,V>)) : bool


### Map\_free


func [Map\_free](#Map\_free)(m: [Map<K,V>\*](#Map<K,V>))


### Map\_get


func [Map\_get](#Map\_get)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_init


func [Map\_init](#Map\_init)(m: [Map<K,V>\*](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_iter


func [Map\_iter](#Map\_iter)(m: [Map<K,V>\*](#Map<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<K,V>](#MapIterator<K,V>)


### Map\_put


func [Map\_put](#Map\_put)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K), value: [V](#V))


### Map\_remove


func [Map\_remove](#Map\_remove)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_size


func [Map\_size](#Map\_size)(m: [Map<K,V>\*](#Map<K,V>)) : i32


### Parser\_free


func [Parser\_free](#Parser\_free)(p: [Parser\*](#Parser))


### Parser\_hasError


func [Parser\_hasError](#Parser\_hasError)(p: [Parser\*](#Parser)) : bool


### Parser\_init


func [Parser\_init](#Parser\_init)(p: [Parser\*](#Parser), alloc: [Allocator const\*](#Allocator))


### Parser\_parseJson


func [Parser\_parseJson](#Parser\_parseJson)(p: [Parser\*](#Parser), buffer: char const*) : [JsonNode\*](#JsonNode)


### PrintJson


func [PrintJson](#PrintJson)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer))


### PtrEqualFn


func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool


### StrEqualFn


func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool


### StrHashFn


func [StrHashFn](#StrHashFn)(str: char const*) : u32


### StrMap


func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)


### StringBufferInit


func [StringBufferInit](#StringBufferInit)(initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)


### StringBuffer\_append


func [StringBuffer\_append](#StringBuffer\_append)(b: [StringBuffer\*](#StringBuffer), format: char const*) : i32


### StringBuffer\_appendChar


func [StringBuffer\_appendChar](#StringBuffer\_appendChar)(b: [StringBuffer\*](#StringBuffer), c: char) : i32


### StringBuffer\_appendFloat


func [StringBuffer\_appendFloat](#StringBuffer\_appendFloat)(b: [StringBuffer\*](#StringBuffer), f: f64) : i32


### StringBuffer\_appendI32


func [StringBuffer\_appendI32](#StringBuffer\_appendI32)(b: [StringBuffer\*](#StringBuffer), i: i32) : i32


### StringBuffer\_appendI64


func [StringBuffer\_appendI64](#StringBuffer\_appendI64)(b: [StringBuffer\*](#StringBuffer), i: i64) : i32


### StringBuffer\_appendStr


func [StringBuffer\_appendStr](#StringBuffer\_appendStr)(b: [StringBuffer\*](#StringBuffer), str: char*, len: i32)


### StringBuffer\_appendU32


func [StringBuffer\_appendU32](#StringBuffer\_appendU32)(b: [StringBuffer\*](#StringBuffer), i: u32) : i32


### StringBuffer\_appendU64


func [StringBuffer\_appendU64](#StringBuffer\_appendU64)(b: [StringBuffer\*](#StringBuffer), i: u64) : i32


### StringBuffer\_asStringView


func [StringBuffer\_asStringView](#StringBuffer\_asStringView)(b: [StringBuffer\*](#StringBuffer)) : [string\_view](#string\_view)::[StringView](#StringView)


### StringBuffer\_cStr


func [StringBuffer\_cStr](#StringBuffer\_cStr)(b: [StringBuffer\*](#StringBuffer)) : char const*


### StringBuffer\_clear


func [StringBuffer\_clear](#StringBuffer\_clear)(b: [StringBuffer\*](#StringBuffer))


### StringBuffer\_contains


func [StringBuffer\_contains](#StringBuffer\_contains)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : bool


### StringBuffer\_copyTo


func [StringBuffer\_copyTo](#StringBuffer\_copyTo)(b: [StringBuffer\*](#StringBuffer), buf: char*, len: i32, addZero: bool) : i32


### StringBuffer\_delete


func [StringBuffer\_delete](#StringBuffer\_delete)(b: [StringBuffer\*](#StringBuffer), start: i32, end: i32)


### StringBuffer\_empty


func [StringBuffer\_empty](#StringBuffer\_empty)(b: [StringBuffer\*](#StringBuffer)) : bool


### StringBuffer\_forEach


func [StringBuffer\_forEach](#StringBuffer\_forEach)(b: [StringBuffer\*](#StringBuffer), fn: [func\(char\) : bool](#\_))


### StringBuffer\_free


func [StringBuffer\_free](#StringBuffer\_free)(b: [StringBuffer\*](#StringBuffer))


### StringBuffer\_get


func [StringBuffer\_get](#StringBuffer\_get)(b: [StringBuffer\*](#StringBuffer), index: i32) : char


### StringBuffer\_indexOf


func [StringBuffer\_indexOf](#StringBuffer\_indexOf)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : i32


### StringBuffer\_indexOfAt


func [StringBuffer\_indexOfAt](#StringBuffer\_indexOfAt)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32, fromIndex: i32) : i32


### StringBuffer\_init


func [StringBuffer\_init](#StringBuffer\_init)(b: [StringBuffer\*](#StringBuffer), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### StringBuffer\_insert


func [StringBuffer\_insert](#StringBuffer\_insert)(b: [StringBuffer\*](#StringBuffer), index: i32, format: char const*) : i32


### StringBuffer\_size


func [StringBuffer\_size](#StringBuffer\_size)(b: [StringBuffer\*](#StringBuffer)) : i32


### asinf


func [asinf](#asinf)(v: f32) : f32


### assert


func [assert](#assert)(e: bool)


### calloc


func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)


### cos


func [cos](#cos)(v: f64) : f64


### cosf


func [cosf](#cosf)(v: f32) : f32


### exit


func [exit](#exit)(code: i32)


### fabs


func [fabs](#fabs)(v: f32) : f32


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fputs


func [fputs](#fputs)(format: char const*, f: [FILE\*](#FILE))


### fread


func [fread](#fread)(buf: char*, size: u64, n: i64, stream: [FILE\*](#FILE)) : i64


### free


func [free](#free)(ptr: [void\*](#void))


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### isalnum


func [isalnum](#isalnum)(arg: i32) : i32


### isdigit


func [isdigit](#isdigit)(arg: i32) : i32


### isspace


func [isspace](#isspace)(arg: i32) : i32


### malloc


func [malloc](#malloc)(size: u64) : [void\*](#void)


### memcpy


func [memcpy](#memcpy)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)


### memduplicate


func [memduplicate](#memduplicate)(p: [void const\*](#void), len: u64, a: [Allocator const\*](#Allocator)) : [void\*](#void)


### memmove


func [memmove](#memmove)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)


### memset


func [memset](#memset)(ptr: [void\*](#void), value: i32, len: u64) : [void\*](#void)


### new


func [new](#new)(a: [Allocator const\*](#Allocator)) : [T\*](#T)


### printf


func [printf](#printf)(s: char const*)


### realloc


func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)


### sin


func [sin](#sin)(v: f64) : f64


### sinf


func [sinf](#sinf)(v: f32) : f32


### snprintf


func [snprintf](#snprintf)(s: char*, n: u64, format: char const*) : i32


### sqrt


func [sqrt](#sqrt)(v: f64) : f64


### sqrtf


func [sqrtf](#sqrtf)(v: f32) : f32


### strcmp


func [strcmp](#strcmp)(a: char const*, b: char const*) : i32


### strcpy


func [strcpy](#strcpy)(dest: char*, src: char const*) : char*


### strlen


func [strlen](#strlen)(str: char const*) : i32


### strncmp


func [strncmp](#strncmp)(a: char const*, b: char const*, num: u64) : i32


### strncpy


func [strncpy](#strncpy)(dest: char*, src: char const*, num: u64) : char*


### strtod


func [strtod](#strtod)(str: char const*, end: char**) : f64


### strtok


func [strtok](#strtok)(str: char*, delim: char const*) : char*


### strtol


func [strtol](#strtol)(str: char const*, end: char**, base: i32) : i64


### strtoul


func [strtoul](#strtoul)(str: char const*, end: char**, base: i32) : u64


### tan


func [tan](#tan)(v: f64) : f64


### tolower


func [tolower](#tolower)(arg: i32) : i32


### va\_end


func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))


### va\_start


func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: char const*)


### vfprintf


func [vfprintf](#vfprintf)(f: [FILE\*](#FILE), format: char const*, args: [libc](#libc)::[va\_list](#va\_list))


### vsnprintf


func [vsnprintf](#vsnprintf)(buffer: char*, len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsnprintf\_s


func [vsnprintf\_s](#vsnprintf\_s)(ptr: [void\*](#void), len: u64, max: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsprintf\_s


func [vsprintf\_s](#vsprintf\_s)(ptr: [void\*](#void), len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


