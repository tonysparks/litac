# string

## string Imports

* [assert](assert\.md)
* [io](io\.md)
* [libc](libc\.md)
* [mem](mem\.md)
* [string\_view](string\_view\.md)


## string Variables



## string Types

* struct [String](#String)


## string Functions

* func [StringCopy](#StringCopy)(original: *const char, len: i32, allocator: [\*const Allocator](#Allocator)) : *char
* func [StringInit](#StringInit)(str: *char, capacity: i32, len: i32) : [string](#string)::[String](#String)
* func [String\_adjust](#String\_adjust)(s: [\*String](#String))
* func [String\_appendChar](#String\_appendChar)(s: [\*String](#String), c: char)
* func [String\_append](#String\_append)(s: [\*String](#String), format: *const char) : i32
* func [String\_asView](#String\_asView)(s: [string](#string)::[String](#String)) : [string\_view](string\_view\.md)::[StringView](StringView\.md)
* func [String\_cStrConst](#String\_cStrConst)(s: [string](#string)::[String](#String)) : *const char
* func [String\_cStr](#String\_cStr)(s: [string](#string)::[String](#String)) : *char
* func [String\_clear](#String\_clear)(s: [\*String](#String))
* func [String\_copy](#String\_copy)(s: [string](#string)::[String](#String), allocator: [\*const Allocator](#Allocator)) : [string](#string)::[String](#String)
* func [String\_empty](#String\_empty)(s: [string](#string)::[String](#String)) : bool
* func [String\_format](#String\_format)(s: [\*String](#String), format: *const char) : i32
* func [String\_memset](#String\_memset)(s: [string](#string)::[String](#String), value: i32)
* func [String\_size](#String\_size)(s: [string](#string)::[String](#String)) : i32
* func [String\_substring](#String\_substring)(s: [string](#string)::[String](#String), start: i32, end: i32) : [string\_view](string\_view\.md)::[StringView](StringView\.md)
* func [String\_toLower](#String\_toLower)(s: [string](#string)::[String](#String))
* func [String\_toUpper](#String\_toUpper)(s: [string](#string)::[String](#String))



***
### String


struct [String](#String)

* buffer: *char
* length: i32
* capacity: i32



### StringCopy


func [StringCopy](#StringCopy)(original: *const char, len: i32, allocator: [\*const Allocator](#Allocator)) : *char


### StringInit


func [StringInit](#StringInit)(str: *char, capacity: i32, len: i32) : [string](#string)::[String](#String)


### String\_adjust


func [String\_adjust](#String\_adjust)(s: [\*String](#String))


### String\_append


func [String\_append](#String\_append)(s: [\*String](#String), format: *const char) : i32


### String\_appendChar


func [String\_appendChar](#String\_appendChar)(s: [\*String](#String), c: char)


### String\_asView


func [String\_asView](#String\_asView)(s: [string](#string)::[String](#String)) : [string\_view](string\_view\.md)::[StringView](StringView\.md)


### String\_cStr


func [String\_cStr](#String\_cStr)(s: [string](#string)::[String](#String)) : *char


### String\_cStrConst


func [String\_cStrConst](#String\_cStrConst)(s: [string](#string)::[String](#String)) : *const char


### String\_clear


func [String\_clear](#String\_clear)(s: [\*String](#String))


### String\_copy


func [String\_copy](#String\_copy)(s: [string](#string)::[String](#String), allocator: [\*const Allocator](#Allocator)) : [string](#string)::[String](#String)


### String\_empty


func [String\_empty](#String\_empty)(s: [string](#string)::[String](#String)) : bool


### String\_format


func [String\_format](#String\_format)(s: [\*String](#String), format: *const char) : i32


### String\_memset


func [String\_memset](#String\_memset)(s: [string](#string)::[String](#String), value: i32)


### String\_size


func [String\_size](#String\_size)(s: [string](#string)::[String](#String)) : i32


### String\_substring


func [String\_substring](#String\_substring)(s: [string](#string)::[String](#String), start: i32, end: i32) : [string\_view](string\_view\.md)::[StringView](StringView\.md)


### String\_toLower


func [String\_toLower](#String\_toLower)(s: [string](#string)::[String](#String))


### String\_toUpper


func [String\_toUpper](#String\_toUpper)(s: [string](#string)::[String](#String))


