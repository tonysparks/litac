# mem

## mem Imports

* [array](array\.md)
* [libc](libc\.md)


## mem Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [defaultAllocator](#defaultAllocator): [Allocator const\*](#Allocator)
* const [HUGE\_VAL](#HUGE\_VAL): f64
* const [M\_PI](#M\_PI): f64
* const [ULLONG\_MAX](#ULLONG\_MAX): i64
* const [cAllocator](#cAllocator): [Allocator const\*](#Allocator)
* const [debugAllocator](#debugAllocator): [DebugAllocator\*](#DebugAllocator)
* const [stderr](#stderr): [FILE\*](#FILE)
* const [stdout](#stdout): [FILE\*](#FILE)


## mem Types

* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [Array](#Array)
* struct [DebugAllocator](#DebugAllocator)
* struct [FILE](#FILE)
* struct [va\_list](#va\_list)


## mem Functions

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
* func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))
* func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))
* func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)
* func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))
* func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [asinf](#asinf)(v: f32) : f32
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



### FILE


struct [FILE](#FILE)




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


### DebugAllocator\_free


func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))


### DebugAllocator\_init


func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))


### DebugAllocator\_malloc


func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)


### DebugAllocator\_report


func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))


### MAX


func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)


### MIN


func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)


### asinf


func [asinf](#asinf)(v: f32) : f32


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


