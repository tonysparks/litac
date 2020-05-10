# io

## io Imports

* [libc](libc\.md)
* [mem](mem\.md)


## io Variables



## io Types

* enum [FileStatus](#FileStatus)


## io Functions

* func [FileLength](#FileLength)(fileName: *const char) : i64
* func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : *const char
* func [ReadFile](#ReadFile)(fileName: *const char, data: **char, alloc: [\*const Allocator](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteBytes](#WriteBytes)(fp: [\*FILE](#FILE), buffer: *const char, len: usize) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteFile](#WriteFile)(fileName: *const char, buffer: *const char, len: usize) : [io](#io)::[FileStatus](#FileStatus)



***
### FileStatus


enum [FileStatus](#FileStatus)

* Ok
* FileNotFoundError
* IOError



### FileLength


func [FileLength](#FileLength)(fileName: *const char) : i64


### FileStatusAsStr


func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : *const char


### ReadFile


func [ReadFile](#ReadFile)(fileName: *const char, data: **char, alloc: [\*const Allocator](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)


### WriteBytes


func [WriteBytes](#WriteBytes)(fp: [\*FILE](#FILE), buffer: *const char, len: usize) : [io](#io)::[FileStatus](#FileStatus)


### WriteFile


func [WriteFile](#WriteFile)(fileName: *const char, buffer: *const char, len: usize) : [io](#io)::[FileStatus](#FileStatus)


