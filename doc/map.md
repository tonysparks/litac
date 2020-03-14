# map

## map Imports

* [assert](assert\.md)
* [io](io\.md)
* [mem](mem\.md)


## map Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [defaultAllocator](#defaultAllocator): [Allocator const\*](#Allocator)
* const [cAllocator](#cAllocator): [Allocator const\*](#Allocator)
* const [debugAllocator](#debugAllocator): [DebugAllocator\*](#DebugAllocator)


## map Types

* enum [FileStatus](#FileStatus)
* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [DebugAllocator](#DebugAllocator)
* struct [FILE](#FILE)
* struct [MapEntry](#MapEntry)
* struct [MapIterator](#MapIterator)
* struct [Map](#Map)
* typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)
* typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


## map Functions

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
* func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool
* func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
* func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool
* func [StrHashFn](#StrHashFn)(str: char const*) : u32
* func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)
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



### EqualFn


typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)


### FILE


struct [FILE](#FILE)




### FileStatus


enum [FileStatus](#FileStatus)

* Ok
* FileNotFoundError
* IOError



### HashFn


typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


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


### PtrEqualFn


func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool


### ReadFile


func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)


### StrEqualFn


func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool


### StrHashFn


func [StrHashFn](#StrHashFn)(str: char const*) : u32


### StrMap


func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)


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


