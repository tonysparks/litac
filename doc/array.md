# array

## array Imports

* [assert](assert\.md)
* [libc](libc\.md)
* [mem](mem\.md)


## array Variables



## array Types

* struct [Array](#Array)


## array Functions

* func [Array\_addAll](#Array\_addAll)(a: [\*Array<T>](#Array<T>), other: [\*Array<T>](#Array<T>))
* func [Array\_add](#Array\_add)(a: [\*Array<T>](#Array<T>), element: [T](#T))
* func [Array\_clear](#Array\_clear)(a: [\*Array<T>](#Array<T>))
* func [Array\_empty](#Array\_empty)(a: [\*Array<T>](#Array<T>)) : bool
* func [Array\_first](#Array\_first)(a: [\*Array<T>](#Array<T>)) : [T](#T)
* func [Array\_forEach](#Array\_forEach)(a: [\*Array<T>](#Array<T>), fn: [func<T>\(T\) : bool](#\_))
* func [Array\_free](#Array\_free)(a: [\*Array<T>](#Array<T>))
* func [Array\_get](#Array\_get)(a: [\*Array<T>](#Array<T>), index: i32) : [T](#T)
* func [Array\_init](#Array\_init)(a: [\*Array<T>](#Array<T>), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [Array\_last](#Array\_last)(a: [\*Array<T>](#Array<T>)) : [T](#T)
* func [Array\_pop](#Array\_pop)(a: [\*Array<T>](#Array<T>)) : [T](#T)
* func [Array\_push](#Array\_push)(a: [\*Array<T>](#Array<T>), element: [T](#T))
* func [Array\_removeAt](#Array\_removeAt)(a: [\*Array<T>](#Array<T>), index: i32) : [T](#T)
* func [Array\_set](#Array\_set)(a: [\*Array<T>](#Array<T>), index: i32, element: [T](#T))
* func [Array\_size](#Array\_size)(a: [\*Array<T>](#Array<T>)) : i32



***
### Array


struct [Array](#Array)

* length: i32
* capacity: i32
* elements: [\*T](#T)
* alloc: [\*const Allocator](#Allocator)



### Array\_add


func [Array\_add](#Array\_add)(a: [\*Array<T>](#Array<T>), element: [T](#T))


### Array\_addAll


func [Array\_addAll](#Array\_addAll)(a: [\*Array<T>](#Array<T>), other: [\*Array<T>](#Array<T>))


### Array\_clear


func [Array\_clear](#Array\_clear)(a: [\*Array<T>](#Array<T>))


### Array\_empty


func [Array\_empty](#Array\_empty)(a: [\*Array<T>](#Array<T>)) : bool


### Array\_first


func [Array\_first](#Array\_first)(a: [\*Array<T>](#Array<T>)) : [T](#T)


### Array\_forEach


func [Array\_forEach](#Array\_forEach)(a: [\*Array<T>](#Array<T>), fn: [func<T>\(T\) : bool](#\_))


### Array\_free


func [Array\_free](#Array\_free)(a: [\*Array<T>](#Array<T>))


### Array\_get


func [Array\_get](#Array\_get)(a: [\*Array<T>](#Array<T>), index: i32) : [T](#T)


### Array\_init


func [Array\_init](#Array\_init)(a: [\*Array<T>](#Array<T>), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### Array\_last


func [Array\_last](#Array\_last)(a: [\*Array<T>](#Array<T>)) : [T](#T)


### Array\_pop


func [Array\_pop](#Array\_pop)(a: [\*Array<T>](#Array<T>)) : [T](#T)


### Array\_push


func [Array\_push](#Array\_push)(a: [\*Array<T>](#Array<T>), element: [T](#T))


### Array\_removeAt


func [Array\_removeAt](#Array\_removeAt)(a: [\*Array<T>](#Array<T>), index: i32) : [T](#T)


### Array\_set


func [Array\_set](#Array\_set)(a: [\*Array<T>](#Array<T>), index: i32, element: [T](#T))


### Array\_size


func [Array\_size](#Array\_size)(a: [\*Array<T>](#Array<T>)) : i32


