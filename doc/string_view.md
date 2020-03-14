# string\_view

## string\_view Imports

* [assert](assert\.md)
* [libc](libc\.md)
* [mem](mem\.md)


## string\_view Variables

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


## string\_view Types

* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [DebugAllocator](#DebugAllocator)
* struct [FILE](#FILE)
* struct [StringView](#StringView)
* struct [va\_list](#va\_list)


## string\_view Functions

* func [ABS](#ABS)(a: f32) : f32
* func [Allocator\_alloc](#Allocator\_alloc)(a: [Allocator const\*](#Allocator), size: u64) : [void\*](#void)
* func [Allocator\_calloc](#Allocator\_calloc)(a: [Allocator const\*](#Allocator), num: u64, size: u64) : [void\*](#void)
* func [Allocator\_free](#Allocator\_free)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void))
* func [Allocator\_realloc](#Allocator\_realloc)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void), oldsize: u64, newsize: u64) : [void\*](#void)
* func [Arena\_free](#Arena\_free)(arena: [Arena\*](#Arena))
* func [Arena\_init](#Arena\_init)(arena: [Arena\*](#Arena), size: u64, alloc: [Allocator const\*](#Allocator))
* func [Arena\_malloc](#Arena\_malloc)(arena: [Arena\*](#Arena), size: u64) : [void\*](#void)
* func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))
* func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))
* func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)
* func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))
* func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [StringViewInit](#StringViewInit)(str: char const*, len: i32) : [string\_view](#string\_view)::[StringView](#StringView)
* func [StringView\_clear](#StringView\_clear)(b: [string\_view](#string\_view)::[StringView](#StringView))
* func [StringView\_contains](#StringView\_contains)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool
* func [StringView\_copyTo](#StringView\_copyTo)(b: [string\_view](#string\_view)::[StringView](#StringView), buf: char*, len: i32, addZero: bool) : i32
* func [StringView\_empty](#StringView\_empty)(b: [string\_view](#string\_view)::[StringView](#StringView)) : bool
* func [StringView\_endsWith](#StringView\_endsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), suffix: char const*, len: i32) : bool
* func [StringView\_equals](#StringView\_equals)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool
* func [StringView\_forEach](#StringView\_forEach)(b: [string\_view](#string\_view)::[StringView](#StringView), fn: [func\(char\) : bool](#\_))
* func [StringView\_get](#StringView\_get)(b: [string\_view](#string\_view)::[StringView](#StringView), index: i32) : char
* func [StringView\_indexOfAt](#StringView\_indexOfAt)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32, fromIndex: i32) : i32
* func [StringView\_indexOf](#StringView\_indexOf)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : i32
* func [StringView\_size](#StringView\_size)(b: [string\_view](#string\_view)::[StringView](#StringView)) : i32
* func [StringView\_startsWith](#StringView\_startsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), prefix: char const*, len: i32, fromIndex: i32) : bool
* func [StringView\_substring](#StringView\_substring)(b: [string\_view](#string\_view)::[StringView](#StringView), start: i32, end: i32) : [string\_view](#string\_view)::[StringView](#StringView)
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



### DebugAllocator


struct [DebugAllocator](#DebugAllocator)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [Allocator const\*](#Allocator)
* allocations: [documentationGenerator](#documentationGenerator)::[Array<Allocation\*>](#Array<Allocation\*>)



### FILE


struct [FILE](#FILE)




### StringView


struct [StringView](#StringView)

* buffer: char const*
* length: i32



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


### StringViewInit


func [StringViewInit](#StringViewInit)(str: char const*, len: i32) : [string\_view](#string\_view)::[StringView](#StringView)


### StringView\_clear


func [StringView\_clear](#StringView\_clear)(b: [string\_view](#string\_view)::[StringView](#StringView))


### StringView\_contains


func [StringView\_contains](#StringView\_contains)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool


### StringView\_copyTo


func [StringView\_copyTo](#StringView\_copyTo)(b: [string\_view](#string\_view)::[StringView](#StringView), buf: char*, len: i32, addZero: bool) : i32


### StringView\_empty


func [StringView\_empty](#StringView\_empty)(b: [string\_view](#string\_view)::[StringView](#StringView)) : bool


### StringView\_endsWith


func [StringView\_endsWith](#StringView\_endsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), suffix: char const*, len: i32) : bool


### StringView\_equals


func [StringView\_equals](#StringView\_equals)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool


### StringView\_forEach


func [StringView\_forEach](#StringView\_forEach)(b: [string\_view](#string\_view)::[StringView](#StringView), fn: [func\(char\) : bool](#\_))


### StringView\_get


func [StringView\_get](#StringView\_get)(b: [string\_view](#string\_view)::[StringView](#StringView), index: i32) : char


### StringView\_indexOf


func [StringView\_indexOf](#StringView\_indexOf)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : i32


### StringView\_indexOfAt


func [StringView\_indexOfAt](#StringView\_indexOfAt)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32, fromIndex: i32) : i32


### StringView\_size


func [StringView\_size](#StringView\_size)(b: [string\_view](#string\_view)::[StringView](#StringView)) : i32


### StringView\_startsWith


func [StringView\_startsWith](#StringView\_startsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), prefix: char const*, len: i32, fromIndex: i32) : bool


### StringView\_substring


func [StringView\_substring](#StringView\_substring)(b: [string\_view](#string\_view)::[StringView](#StringView), start: i32, end: i32) : [string\_view](#string\_view)::[StringView](#StringView)


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


