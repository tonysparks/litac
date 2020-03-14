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

* func [FileLength](#FileLength)(fileName: char const*) : i64
* func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*
* func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64
* func [printf](#printf)(s: char const*)



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


func [FileLength](#FileLength)(fileName: char const*) : i64


### FileStatusAsStr


func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*


### ReadFile


func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)


### WriteBytes


func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### WriteFile


func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fread


func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### fwrite


func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64


### printf


func [printf](#printf)(s: char const*)


