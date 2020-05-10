# mem

## mem Imports

* [array](array\.md)
* [libc](libc\.md)


## mem Variables

* [defaultAllocator](#defaultAllocator): [\*const Allocator](#Allocator)
* const [cAllocator](#cAllocator): [\*const Allocator](#Allocator)
* const [debugAllocator](#debugAllocator): [\*DebugAllocator](#DebugAllocator)


## mem Types

* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [DebugAllocator](#DebugAllocator)


## mem Functions

* func [Allocator\_alloc](#Allocator\_alloc)(a: [\*const Allocator](#Allocator), size: u64) : [\*void](#void)
* func [Allocator\_calloc](#Allocator\_calloc)(a: [\*const Allocator](#Allocator), num: u64, size: u64) : [\*void](#void)
* func [Allocator\_free](#Allocator\_free)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void))
* func [Allocator\_realloc](#Allocator\_realloc)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void), oldsize: u64, newsize: u64) : [\*void](#void)
* func [Arena\_free](#Arena\_free)(arena: [\*Arena](#Arena))
* func [Arena\_init](#Arena\_init)(arena: [\*Arena](#Arena), size: u64, alloc: [\*const Allocator](#Allocator))
* func [Arena\_malloc](#Arena\_malloc)(arena: [\*Arena](#Arena), size: u64) : [\*void](#void)
* func [DebugAllocator\_free](#DebugAllocator\_free)(d: [\*DebugAllocator](#DebugAllocator))
* func [DebugAllocator\_init](#DebugAllocator\_init)(d: [\*DebugAllocator](#DebugAllocator), alloc: [\*const Allocator](#Allocator))
* func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [\*DebugAllocator](#DebugAllocator), size: u64, file: *const char, line: u64) : [\*void](#void)
* func [DebugAllocator\_report](#DebugAllocator\_report)(d: [\*DebugAllocator](#DebugAllocator))
* func [calloc](#calloc)(num: u64, size: u64) : [\*void](#void)
* func [free](#free)(ptr: [\*void](#void))
* func [malloc](#malloc)(size: u64) : [\*void](#void)
* func [memcpy](#memcpy)(dest: [\*void](#void), src: [\*const void](#void), num: u64) : [\*void](#void)
* func [memduplicate](#memduplicate)(p: [\*const void](#void), len: u64, a: [\*const Allocator](#Allocator)) : [\*void](#void)
* func [memmove](#memmove)(dest: [\*void](#void), src: [\*const void](#void), num: u64) : [\*void](#void)
* func [memset](#memset)(ptr: [\*void](#void), value: i32, len: u64) : [\*void](#void)
* func [new](#new)(a: [\*const Allocator](#Allocator)) : [\*T](#T)
* func [realloc](#realloc)(ptr: [\*void](#void), size: u64) : [\*void](#void)



***
### cAllocator


### debugAllocator


### defaultAllocator


### Allocation


struct [Allocation](#Allocation)

* addr: [\*void](#void)
* size: u64
* line: u64
* filename: []char



### Allocator


struct [Allocator](#Allocator)

* allocFn: [func\(\*const Allocator, u64\) : \*void](#\_)
* callocFn: [func\(\*const Allocator, u64, u64\) : \*void](#\_)
* reallocFn: [func\(\*const Allocator, \*void, u64, u64\) : \*void](#\_)
* freeFn: [func\(\*const Allocator, \*void\) : void](#\_)



### Arena


struct [Arena](#Arena)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [\*const Allocator](#Allocator)
* region: *u8
* size: u64
* current: u64
* next: [\*Arena](#Arena)



### DebugAllocator


struct [DebugAllocator](#DebugAllocator)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [\*const Allocator](#Allocator)
* allocations: [documentationGenerator](documentationGenerator\.md)::[Array<Allocation\*>](Array<Allocation\*>\.md)



### Allocator\_alloc


func [Allocator\_alloc](#Allocator\_alloc)(a: [\*const Allocator](#Allocator), size: u64) : [\*void](#void)


### Allocator\_calloc


func [Allocator\_calloc](#Allocator\_calloc)(a: [\*const Allocator](#Allocator), num: u64, size: u64) : [\*void](#void)


### Allocator\_free


func [Allocator\_free](#Allocator\_free)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void))


### Allocator\_realloc


func [Allocator\_realloc](#Allocator\_realloc)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void), oldsize: u64, newsize: u64) : [\*void](#void)


### Arena\_free


func [Arena\_free](#Arena\_free)(arena: [\*Arena](#Arena))


### Arena\_init


func [Arena\_init](#Arena\_init)(arena: [\*Arena](#Arena), size: u64, alloc: [\*const Allocator](#Allocator))


### Arena\_malloc


func [Arena\_malloc](#Arena\_malloc)(arena: [\*Arena](#Arena), size: u64) : [\*void](#void)


### DebugAllocator\_free


func [DebugAllocator\_free](#DebugAllocator\_free)(d: [\*DebugAllocator](#DebugAllocator))


### DebugAllocator\_init


func [DebugAllocator\_init](#DebugAllocator\_init)(d: [\*DebugAllocator](#DebugAllocator), alloc: [\*const Allocator](#Allocator))


### DebugAllocator\_malloc


func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [\*DebugAllocator](#DebugAllocator), size: u64, file: *const char, line: u64) : [\*void](#void)


### DebugAllocator\_report


func [DebugAllocator\_report](#DebugAllocator\_report)(d: [\*DebugAllocator](#DebugAllocator))


### calloc


func [calloc](#calloc)(num: u64, size: u64) : [\*void](#void)


### free


func [free](#free)(ptr: [\*void](#void))


### malloc


func [malloc](#malloc)(size: u64) : [\*void](#void)


### memcpy


func [memcpy](#memcpy)(dest: [\*void](#void), src: [\*const void](#void), num: u64) : [\*void](#void)


### memduplicate


func [memduplicate](#memduplicate)(p: [\*const void](#void), len: u64, a: [\*const Allocator](#Allocator)) : [\*void](#void)


### memmove


func [memmove](#memmove)(dest: [\*void](#void), src: [\*const void](#void), num: u64) : [\*void](#void)


### memset


func [memset](#memset)(ptr: [\*void](#void), value: i32, len: u64) : [\*void](#void)


### new


func [new](#new)(a: [\*const Allocator](#Allocator)) : [\*T](#T)


### realloc


func [realloc](#realloc)(ptr: [\*void](#void), size: u64) : [\*void](#void)


