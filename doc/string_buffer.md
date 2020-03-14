# string\_buffer

## string\_buffer Imports

* [assert](assert)
* [io](io)
* [mem](mem)
* [string\_view](string\_view)


## string\_buffer Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [defaultAllocator](#defaultAllocator): [Allocator const\*](#Allocator)
* const [cAllocator](#cAllocator): [Allocator const\*](#Allocator)
* const [debugAllocator](#debugAllocator): [DebugAllocator\*](#DebugAllocator)


## string\_buffer Types

* enum [FileStatus](#FileStatus)
* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [DebugAllocator](#DebugAllocator)
* struct [FILE](#FILE)
* struct [StringBuffer](#StringBuffer)
* struct [StringView](#StringView)


## string\_buffer Functions

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
* func [FileLength](#FileLength)(fileName: char const*) : i64
* func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*
* func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
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
* func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [assert](#assert)(e: bool)
* func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64
* func [free](#free)(ptr: [void\*](#void))
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64
* func [malloc](#malloc)(size: u64) : [void\*](#void)
* func [memcpy](#memcpy)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memduplicate](#memduplicate)(p: [void const\*](#void), len: u64, a: [Allocator const\*](#Allocator)) : [void\*](#void)
* func [memmove](#memmove)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memset](#memset)(ptr: [void\*](#void), value: i32, len: u64) : [void\*](#void)
* func [new](#new)(a: [Allocator const\*](#Allocator)) : [T\*](#T)
* func [printf](#printf)(s: char const*)
* func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)



***
### SEEK\_CUR


### SEEK\_END


### SEEK\_SET


### cAllocator


### debugAllocator


### defaultAllocator


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




### FileStatus


enum [FileStatus](#FileStatus)

* Ok
* FileNotFoundError
* IOError



### StringBuffer


struct [StringBuffer](#StringBuffer)

* buffer: char*
* length: i32
* capacity: i32
* alloc: [Allocator const\*](#Allocator)



### StringView


struct [StringView](#StringView)

* buffer: char const*
* length: i32



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


### FileLength


func [FileLength](#FileLength)(fileName: char const*) : i64


### FileStatusAsStr


func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*


### ReadFile


func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)


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


### WriteBytes


func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### WriteFile


func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### assert


func [assert](#assert)(e: bool)


### calloc


func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fread


func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64


### free


func [free](#free)(ptr: [void\*](#void))


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### fwrite


func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64


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


