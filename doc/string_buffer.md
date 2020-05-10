# string\_buffer

## string\_buffer Imports

* [assert](assert\.md)
* [io](io\.md)
* [libc](libc\.md)
* [mem](mem\.md)
* [string\_view](string\_view\.md)


## string\_buffer Variables



## string\_buffer Types

* struct [StringBuffer](#StringBuffer)


## string\_buffer Functions

* func [StringBufferInit](#StringBufferInit)(initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)
* func [StringBuffer\_appendChar](#StringBuffer\_appendChar)(b: [\*StringBuffer](#StringBuffer), c: char) : i32
* func [StringBuffer\_appendFloat](#StringBuffer\_appendFloat)(b: [\*StringBuffer](#StringBuffer), f: f64) : i32
* func [StringBuffer\_appendI32](#StringBuffer\_appendI32)(b: [\*StringBuffer](#StringBuffer), i: i32) : i32
* func [StringBuffer\_appendI64](#StringBuffer\_appendI64)(b: [\*StringBuffer](#StringBuffer), i: i64) : i32
* func [StringBuffer\_appendStr](#StringBuffer\_appendStr)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32)
* func [StringBuffer\_appendU32](#StringBuffer\_appendU32)(b: [\*StringBuffer](#StringBuffer), i: u32) : i32
* func [StringBuffer\_appendU64](#StringBuffer\_appendU64)(b: [\*StringBuffer](#StringBuffer), i: u64) : i32
* func [StringBuffer\_append](#StringBuffer\_append)(b: [\*StringBuffer](#StringBuffer), format: *const char) : i32
* func [StringBuffer\_asStringView](#StringBuffer\_asStringView)(b: [\*StringBuffer](#StringBuffer)) : [string\_view](string\_view\.md)::[StringView](StringView\.md)
* func [StringBuffer\_cStrConst](#StringBuffer\_cStrConst)(b: [\*StringBuffer](#StringBuffer)) : *const char
* func [StringBuffer\_cStr](#StringBuffer\_cStr)(b: [\*StringBuffer](#StringBuffer)) : *char
* func [StringBuffer\_clear](#StringBuffer\_clear)(b: [\*StringBuffer](#StringBuffer))
* func [StringBuffer\_contains](#StringBuffer\_contains)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32) : bool
* func [StringBuffer\_copyTo](#StringBuffer\_copyTo)(b: [\*StringBuffer](#StringBuffer), buf: *char, len: i32, addZero: bool) : i32
* func [StringBuffer\_delete](#StringBuffer\_delete)(b: [\*StringBuffer](#StringBuffer), start: i32, end: i32)
* func [StringBuffer\_empty](#StringBuffer\_empty)(b: [\*StringBuffer](#StringBuffer)) : bool
* func [StringBuffer\_forEach](#StringBuffer\_forEach)(b: [\*StringBuffer](#StringBuffer), fn: [func\(char\) : bool](#\_))
* func [StringBuffer\_free](#StringBuffer\_free)(b: [\*StringBuffer](#StringBuffer))
* func [StringBuffer\_get](#StringBuffer\_get)(b: [\*StringBuffer](#StringBuffer), index: i32) : char
* func [StringBuffer\_indexOfAt](#StringBuffer\_indexOfAt)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32, fromIndex: i32) : i32
* func [StringBuffer\_indexOf](#StringBuffer\_indexOf)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32) : i32
* func [StringBuffer\_init](#StringBuffer\_init)(b: [\*StringBuffer](#StringBuffer), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [StringBuffer\_insert](#StringBuffer\_insert)(b: [\*StringBuffer](#StringBuffer), index: i32, format: *const char) : i32
* func [StringBuffer\_size](#StringBuffer\_size)(b: [\*StringBuffer](#StringBuffer)) : i32



***
### StringBuffer


struct [StringBuffer](#StringBuffer)

* buffer: *char
* length: i32
* capacity: i32
* alloc: [\*const Allocator](#Allocator)



### StringBufferInit


func [StringBufferInit](#StringBufferInit)(initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)


### StringBuffer\_append


func [StringBuffer\_append](#StringBuffer\_append)(b: [\*StringBuffer](#StringBuffer), format: *const char) : i32


### StringBuffer\_appendChar


func [StringBuffer\_appendChar](#StringBuffer\_appendChar)(b: [\*StringBuffer](#StringBuffer), c: char) : i32


### StringBuffer\_appendFloat


func [StringBuffer\_appendFloat](#StringBuffer\_appendFloat)(b: [\*StringBuffer](#StringBuffer), f: f64) : i32


### StringBuffer\_appendI32


func [StringBuffer\_appendI32](#StringBuffer\_appendI32)(b: [\*StringBuffer](#StringBuffer), i: i32) : i32


### StringBuffer\_appendI64


func [StringBuffer\_appendI64](#StringBuffer\_appendI64)(b: [\*StringBuffer](#StringBuffer), i: i64) : i32


### StringBuffer\_appendStr


func [StringBuffer\_appendStr](#StringBuffer\_appendStr)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32)


### StringBuffer\_appendU32


func [StringBuffer\_appendU32](#StringBuffer\_appendU32)(b: [\*StringBuffer](#StringBuffer), i: u32) : i32


### StringBuffer\_appendU64


func [StringBuffer\_appendU64](#StringBuffer\_appendU64)(b: [\*StringBuffer](#StringBuffer), i: u64) : i32


### StringBuffer\_asStringView


func [StringBuffer\_asStringView](#StringBuffer\_asStringView)(b: [\*StringBuffer](#StringBuffer)) : [string\_view](string\_view\.md)::[StringView](StringView\.md)


### StringBuffer\_cStr


func [StringBuffer\_cStr](#StringBuffer\_cStr)(b: [\*StringBuffer](#StringBuffer)) : *char


### StringBuffer\_cStrConst


func [StringBuffer\_cStrConst](#StringBuffer\_cStrConst)(b: [\*StringBuffer](#StringBuffer)) : *const char


### StringBuffer\_clear


func [StringBuffer\_clear](#StringBuffer\_clear)(b: [\*StringBuffer](#StringBuffer))


### StringBuffer\_contains


func [StringBuffer\_contains](#StringBuffer\_contains)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32) : bool


### StringBuffer\_copyTo


func [StringBuffer\_copyTo](#StringBuffer\_copyTo)(b: [\*StringBuffer](#StringBuffer), buf: *char, len: i32, addZero: bool) : i32


### StringBuffer\_delete


func [StringBuffer\_delete](#StringBuffer\_delete)(b: [\*StringBuffer](#StringBuffer), start: i32, end: i32)


### StringBuffer\_empty


func [StringBuffer\_empty](#StringBuffer\_empty)(b: [\*StringBuffer](#StringBuffer)) : bool


### StringBuffer\_forEach


func [StringBuffer\_forEach](#StringBuffer\_forEach)(b: [\*StringBuffer](#StringBuffer), fn: [func\(char\) : bool](#\_))


### StringBuffer\_free


func [StringBuffer\_free](#StringBuffer\_free)(b: [\*StringBuffer](#StringBuffer))


### StringBuffer\_get


func [StringBuffer\_get](#StringBuffer\_get)(b: [\*StringBuffer](#StringBuffer), index: i32) : char


### StringBuffer\_indexOf


func [StringBuffer\_indexOf](#StringBuffer\_indexOf)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32) : i32


### StringBuffer\_indexOfAt


func [StringBuffer\_indexOfAt](#StringBuffer\_indexOfAt)(b: [\*StringBuffer](#StringBuffer), str: *const char, len: i32, fromIndex: i32) : i32


### StringBuffer\_init


func [StringBuffer\_init](#StringBuffer\_init)(b: [\*StringBuffer](#StringBuffer), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### StringBuffer\_insert


func [StringBuffer\_insert](#StringBuffer\_insert)(b: [\*StringBuffer](#StringBuffer), index: i32, format: *const char) : i32


### StringBuffer\_size


func [StringBuffer\_size](#StringBuffer\_size)(b: [\*StringBuffer](#StringBuffer)) : i32


