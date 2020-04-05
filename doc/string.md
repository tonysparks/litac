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

* func [StringInit](#StringInit)(str: char*, capacity: i32, len: i32) : [string](#string)::[String](#String)
* func [String\_asView](#String\_asView)(s: [string](#string)::[String](#String)) : [string\_view](string\_view\.md)::[StringView](StringView\.md)
* func [String\_copy](#String\_copy)(s: [string](#string)::[String](#String), allocator: [Allocator const\*](#Allocator)) : [string](#string)::[String](#String)
* func [String\_empty](#String\_empty)(s: [string](#string)::[String](#String)) : bool
* func [String\_format](#String\_format)(s: [String\*](#String), format: char const*) : i32
* func [String\_memset](#String\_memset)(s: [string](#string)::[String](#String), value: i32)
* func [String\_size](#String\_size)(s: [string](#string)::[String](#String)) : i32



***
### String


struct [String](#String)

* str: char*
* length: i32
* capacity: i32



### StringInit


func [StringInit](#StringInit)(str: char*, capacity: i32, len: i32) : [string](#string)::[String](#String)


### String\_asView


func [String\_asView](#String\_asView)(s: [string](#string)::[String](#String)) : [string\_view](string\_view\.md)::[StringView](StringView\.md)


### String\_copy


func [String\_copy](#String\_copy)(s: [string](#string)::[String](#String), allocator: [Allocator const\*](#Allocator)) : [string](#string)::[String](#String)


### String\_empty


func [String\_empty](#String\_empty)(s: [string](#string)::[String](#String)) : bool


### String\_format


func [String\_format](#String\_format)(s: [String\*](#String), format: char const*) : i32


### String\_memset


func [String\_memset](#String\_memset)(s: [string](#string)::[String](#String), value: i32)


### String\_size


func [String\_size](#String\_size)(s: [string](#string)::[String](#String)) : i32


