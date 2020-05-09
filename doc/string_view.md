# string\_view

## string\_view Imports

* [assert](assert\.md)
* [libc](libc\.md)
* [mem](mem\.md)


## string\_view Variables



## string\_view Types

* struct [StringView](#StringView)


## string\_view Functions

* func [StringViewInit](#StringViewInit)(str: *const char, len: i32) : [string\_view](#string\_view)::[StringView](#StringView)
* func [StringView\_clear](#StringView\_clear)(b: [string\_view](#string\_view)::[StringView](#StringView))
* func [StringView\_contains](#StringView\_contains)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32) : bool
* func [StringView\_copyTo](#StringView\_copyTo)(b: [string\_view](#string\_view)::[StringView](#StringView), buf: *char, len: i32, addZero: bool) : i32
* func [StringView\_empty](#StringView\_empty)(b: [string\_view](#string\_view)::[StringView](#StringView)) : bool
* func [StringView\_endsWith](#StringView\_endsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), suffix: *const char, len: i32) : bool
* func [StringView\_equals](#StringView\_equals)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32) : bool
* func [StringView\_forEach](#StringView\_forEach)(b: [string\_view](#string\_view)::[StringView](#StringView), fn: [func\(char\) : bool](#\_))
* func [StringView\_get](#StringView\_get)(b: [string\_view](#string\_view)::[StringView](#StringView), index: i32) : char
* func [StringView\_indexOfAt](#StringView\_indexOfAt)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32, fromIndex: i32) : i32
* func [StringView\_indexOf](#StringView\_indexOf)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32) : i32
* func [StringView\_size](#StringView\_size)(b: [string\_view](#string\_view)::[StringView](#StringView)) : i32
* func [StringView\_startsWith](#StringView\_startsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), prefix: *const char, len: i32, fromIndex: i32) : bool
* func [StringView\_substring](#StringView\_substring)(b: [string\_view](#string\_view)::[StringView](#StringView), start: i32, end: i32) : [string\_view](#string\_view)::[StringView](#StringView)



***
### StringView


struct [StringView](#StringView)

* buffer: *const char
* length: i32



### StringViewInit


func [StringViewInit](#StringViewInit)(str: *const char, len: i32) : [string\_view](#string\_view)::[StringView](#StringView)


### StringView\_clear


func [StringView\_clear](#StringView\_clear)(b: [string\_view](#string\_view)::[StringView](#StringView))


### StringView\_contains


func [StringView\_contains](#StringView\_contains)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32) : bool


### StringView\_copyTo


func [StringView\_copyTo](#StringView\_copyTo)(b: [string\_view](#string\_view)::[StringView](#StringView), buf: *char, len: i32, addZero: bool) : i32


### StringView\_empty


func [StringView\_empty](#StringView\_empty)(b: [string\_view](#string\_view)::[StringView](#StringView)) : bool


### StringView\_endsWith


func [StringView\_endsWith](#StringView\_endsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), suffix: *const char, len: i32) : bool


### StringView\_equals


func [StringView\_equals](#StringView\_equals)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32) : bool


### StringView\_forEach


func [StringView\_forEach](#StringView\_forEach)(b: [string\_view](#string\_view)::[StringView](#StringView), fn: [func\(char\) : bool](#\_))


### StringView\_get


func [StringView\_get](#StringView\_get)(b: [string\_view](#string\_view)::[StringView](#StringView), index: i32) : char


### StringView\_indexOf


func [StringView\_indexOf](#StringView\_indexOf)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32) : i32


### StringView\_indexOfAt


func [StringView\_indexOfAt](#StringView\_indexOfAt)(b: [string\_view](#string\_view)::[StringView](#StringView), str: *const char, len: i32, fromIndex: i32) : i32


### StringView\_size


func [StringView\_size](#StringView\_size)(b: [string\_view](#string\_view)::[StringView](#StringView)) : i32


### StringView\_startsWith


func [StringView\_startsWith](#StringView\_startsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), prefix: *const char, len: i32, fromIndex: i32) : bool


### StringView\_substring


func [StringView\_substring](#StringView\_substring)(b: [string\_view](#string\_view)::[StringView](#StringView), start: i32, end: i32) : [string\_view](#string\_view)::[StringView](#StringView)


