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

* func [Allocator\_alloc](#Allocator\_alloc)(a: [\*const Allocator](#Allocator), size: usize) : [\*void](#void)
* func [Allocator\_calloc](#Allocator\_calloc)(a: [\*const Allocator](#Allocator), num: usize, size: usize) : [\*void](#void)
* func [Allocator\_free](#Allocator\_free)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void))
* func [Allocator\_realloc](#Allocator\_realloc)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void), oldsize: usize, newsize: usize) : [\*void](#void)
* func [Arena\_free](#Arena\_free)(arena: [\*Arena](#Arena))
* func [Arena\_init](#Arena\_init)(arena: [\*Arena](#Arena), size: usize, alloc: [\*const Allocator](#Allocator))
* func [Arena\_malloc](#Arena\_malloc)(arena: [\*Arena](#Arena), size: usize) : [\*void](#void)
* func [DebugAllocator\_free](#DebugAllocator\_free)(d: [\*DebugAllocator](#DebugAllocator))
* func [DebugAllocator\_init](#DebugAllocator\_init)(d: [\*DebugAllocator](#DebugAllocator), alloc: [\*const Allocator](#Allocator))
* func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [\*DebugAllocator](#DebugAllocator), size: usize, file: *const char, line: u64) : [\*void](#void)
* func [DebugAllocator\_report](#DebugAllocator\_report)(d: [\*DebugAllocator](#DebugAllocator))
* func [memduplicate](#memduplicate)(p: [\*const void](#void), len: usize, a: [\*const Allocator](#Allocator)) : [\*void](#void)
* func [new](#new)(a: [\*const Allocator](#Allocator)) : [\*T](#T)



***
### cAllocator


### debugAllocator


### defaultAllocator


### Allocation


struct [Allocation](#Allocation)

* addr: [\*void](#void)
* size: usize
* line: u64
* filename: []char



### Allocator


struct [Allocator](#Allocator)

* allocFn: [func\(\*const Allocator, usize\) : \*void](#\_)
* callocFn: [func\(\*const Allocator, usize, usize\) : \*void](#\_)
* reallocFn: [func\(\*const Allocator, \*void, usize, usize\) : \*void](#\_)
* freeFn: [func\(\*const Allocator, \*void\) : void](#\_)



### Arena


struct [Arena](#Arena)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [\*const Allocator](#Allocator)
* region: *u8
* size: usize
* current: usize
* next: [\*Arena](#Arena)



### DebugAllocator


struct [DebugAllocator](#DebugAllocator)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [\*const Allocator](#Allocator)
* allocations: [documentationGenerator](documentationGenerator\.md)::[Array<Allocation\*>](Array<Allocation\*>\.md)



### Allocator\_alloc


func [Allocator\_alloc](#Allocator\_alloc)(a: [\*const Allocator](#Allocator), size: usize) : [\*void](#void)


### Allocator\_calloc


func [Allocator\_calloc](#Allocator\_calloc)(a: [\*const Allocator](#Allocator), num: usize, size: usize) : [\*void](#void)


### Allocator\_free


func [Allocator\_free](#Allocator\_free)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void))


### Allocator\_realloc


func [Allocator\_realloc](#Allocator\_realloc)(a: [\*const Allocator](#Allocator), ptr: [\*void](#void), oldsize: usize, newsize: usize) : [\*void](#void)


### Arena\_free


func [Arena\_free](#Arena\_free)(arena: [\*Arena](#Arena))


### Arena\_init


func [Arena\_init](#Arena\_init)(arena: [\*Arena](#Arena), size: usize, alloc: [\*const Allocator](#Allocator))


### Arena\_malloc


func [Arena\_malloc](#Arena\_malloc)(arena: [\*Arena](#Arena), size: usize) : [\*void](#void)


### DebugAllocator\_free


func [DebugAllocator\_free](#DebugAllocator\_free)(d: [\*DebugAllocator](#DebugAllocator))


### DebugAllocator\_init


func [DebugAllocator\_init](#DebugAllocator\_init)(d: [\*DebugAllocator](#DebugAllocator), alloc: [\*const Allocator](#Allocator))


### DebugAllocator\_malloc


func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [\*DebugAllocator](#DebugAllocator), size: usize, file: *const char, line: u64) : [\*void](#void)


### DebugAllocator\_report


func [DebugAllocator\_report](#DebugAllocator\_report)(d: [\*DebugAllocator](#DebugAllocator))


### memduplicate


func [memduplicate](#memduplicate)(p: [\*const void](#void), len: usize, a: [\*const Allocator](#Allocator)) : [\*void](#void)


### new


func [new](#new)(a: [\*const Allocator](#Allocator)) : [\*T](#T)


