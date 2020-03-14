# array

## array Imports

* [assert](assert\.md)
* [mem](mem\.md)


## array Variables

* [defaultAllocator](#defaultAllocator): [Allocator const\*](#Allocator)
* const [cAllocator](#cAllocator): [Allocator const\*](#Allocator)
* const [debugAllocator](#debugAllocator): [DebugAllocator\*](#DebugAllocator)


## array Types

* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [Array](#Array)
* struct [DebugAllocator](#DebugAllocator)


## array Functions

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
* func [assert](#assert)(e: bool)
* func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)
* func [free](#free)(ptr: [void\*](#void))
* func [malloc](#malloc)(size: u64) : [void\*](#void)
* func [memcpy](#memcpy)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memduplicate](#memduplicate)(p: [void const\*](#void), len: u64, a: [Allocator const\*](#Allocator)) : [void\*](#void)
* func [memmove](#memmove)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memset](#memset)(ptr: [void\*](#void), value: i32, len: u64) : [void\*](#void)
* func [new](#new)(a: [Allocator const\*](#Allocator)) : [T\*](#T)
* func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)



***
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


### assert


func [assert](#assert)(e: bool)


### calloc


func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)


### free


func [free](#free)(ptr: [void\*](#void))


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


### realloc


func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)


