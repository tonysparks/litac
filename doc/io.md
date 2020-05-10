# io

## io Imports

* [mem](mem\.md)


## io Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32


## io Types

* enum [FileStatus](#FileStatus)
* struct [FILE](#FILE)


## io Functions

* func [FileLength](#FileLength)(fileName: *const char) : i64
* func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : *const char
* func [ReadFile](#ReadFile)(fileName: *const char, data: **char, alloc: [\*const Allocator](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteBytes](#WriteBytes)(fp: [\*FILE](#FILE), buffer: *const char, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteFile](#WriteFile)(fileName: *const char, buffer: *const char, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [fclose](#fclose)(stream: [\*FILE](#FILE))
* func [ferror](#ferror)(stream: [\*FILE](#FILE)) : i32
* func [fgets](#fgets)(buf: *char, n: i64, stream: [\*FILE](#FILE)) : *char
* func [fopen](#fopen)(fileName: *const char, openType: *const char) : [\*FILE](#FILE)
* func [fread](#fread)(buf: [\*void](#void), size: u64, n: u64, stream: [\*FILE](#FILE)) : u64
* func [fseek](#fseek)(stream: [\*FILE](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [\*FILE](#FILE)) : i64
* func [fwrite](#fwrite)(buf: [\*const void](#void), sizeOfElements: u64, numOfElements: u64, stream: [\*FILE](#FILE)) : u64
* func [printf](#printf)(s: *const char)



***
### SEEK\_CUR


### SEEK\_END


### SEEK\_SET


### FILE


struct [FILE](#FILE)




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


func [WriteBytes](#WriteBytes)(fp: [\*FILE](#FILE), buffer: *const char, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### WriteFile


func [WriteFile](#WriteFile)(fileName: *const char, buffer: *const char, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### fclose


func [fclose](#fclose)(stream: [\*FILE](#FILE))


### ferror


func [ferror](#ferror)(stream: [\*FILE](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: *char, n: i64, stream: [\*FILE](#FILE)) : *char


### fopen


func [fopen](#fopen)(fileName: *const char, openType: *const char) : [\*FILE](#FILE)


### fread


func [fread](#fread)(buf: [\*void](#void), size: u64, n: u64, stream: [\*FILE](#FILE)) : u64


### fseek


func [fseek](#fseek)(stream: [\*FILE](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [\*FILE](#FILE)) : i64


### fwrite


func [fwrite](#fwrite)(buf: [\*const void](#void), sizeOfElements: u64, numOfElements: u64, stream: [\*FILE](#FILE)) : u64


### printf


func [printf](#printf)(s: *const char)


