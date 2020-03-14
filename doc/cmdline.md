# cmdline

## cmdline Imports

* [io](io\.md)
* [libc](libc\.md)
* [map](map\.md)


## cmdline Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [SEEK\_SET](#SEEK\_SET): i32
* const [HUGE\_VAL](#HUGE\_VAL): f64
* const [M\_PI](#M\_PI): f64
* const [ULLONG\_MAX](#ULLONG\_MAX): i64
* const [stderr](#stderr): [FILE\*](#FILE)
* const [stdout](#stdout): [FILE\*](#FILE)


## cmdline Types

* enum [CmdParserStatus](#CmdParserStatus)
* enum [FileStatus](#FileStatus)
* enum [OptionFlag](#OptionFlag)
* struct [CmdParser](#CmdParser)
* struct [FILE](#FILE)
* struct [FILE](#FILE)
* struct [MapEntry](#MapEntry)
* struct [MapIterator](#MapIterator)
* struct [Map](#Map)
* struct [Option](#Option)
* struct [va\_list](#va\_list)
* typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)
* typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


## cmdline Functions

* func [ABS](#ABS)(a: f32) : f32
* func [CmdParser\_addOption](#CmdParser\_addOption)(p: [CmdParser\*](#CmdParser), longName: char const*, shortName: char, description: char const*, flags: i32, defaultValue: char const*)
* func [CmdParser\_getOption](#CmdParser\_getOption)(p: [CmdParser\*](#CmdParser), longName: char const*) : [cmdline](#cmdline)::[Option](#Option)
* func [CmdParser\_init](#CmdParser\_init)(p: [CmdParser\*](#CmdParser))
* func [CmdParser\_parse](#CmdParser\_parse)(p: [CmdParser\*](#CmdParser), argc: i32, argv: char**) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)
* func [FileLength](#FileLength)(fileName: char const*) : i64
* func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*
* func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : bool
* func [MapIterator\_next](#MapIterator\_next)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<K,V>](#MapEntry<K,V>)
* func [Map\_contains](#Map\_contains)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : bool
* func [Map\_empty](#Map\_empty)(m: [Map<K,V>\*](#Map<K,V>)) : bool
* func [Map\_free](#Map\_free)(m: [Map<K,V>\*](#Map<K,V>))
* func [Map\_get](#Map\_get)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_init](#Map\_init)(m: [Map<K,V>\*](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_iter](#Map\_iter)(m: [Map<K,V>\*](#Map<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<K,V>](#MapIterator<K,V>)
* func [Map\_put](#Map\_put)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K), value: [V](#V))
* func [Map\_remove](#Map\_remove)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_size](#Map\_size)(m: [Map<K,V>\*](#Map<K,V>)) : i32
* func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool
* func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
* func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool
* func [StrHashFn](#StrHashFn)(str: char const*) : u32
* func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)
* func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [asinf](#asinf)(v: f32) : f32
* func [cos](#cos)(v: f64) : f64
* func [cosf](#cosf)(v: f32) : f32
* func [exit](#exit)(code: i32)
* func [fabs](#fabs)(v: f32) : f32
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fputs](#fputs)(format: char const*, f: [FILE\*](#FILE))
* func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64
* func [fread](#fread)(buf: char*, size: u64, n: i64, stream: [FILE\*](#FILE)) : i64
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64
* func [isalnum](#isalnum)(arg: i32) : i32
* func [isdigit](#isdigit)(arg: i32) : i32
* func [isspace](#isspace)(arg: i32) : i32
* func [printf](#printf)(s: char const*)
* func [printf](#printf)(s: char const*)
* func [sin](#sin)(v: f64) : f64
* func [sinf](#sinf)(v: f32) : f32
* func [snprintf](#snprintf)(s: char*, n: u64, format: char const*) : i32
* func [sqrt](#sqrt)(v: f64) : f64
* func [sqrtf](#sqrtf)(v: f32) : f32
* func [strcmp](#strcmp)(a: char const*, b: char const*) : i32
* func [strcpy](#strcpy)(dest: char*, src: char const*) : char*
* func [strlen](#strlen)(str: char const*) : i32
* func [strncmp](#strncmp)(a: char const*, b: char const*, num: u64) : i32
* func [strncpy](#strncpy)(dest: char*, src: char const*, num: u64) : char*
* func [strtod](#strtod)(str: char const*, end: char**) : f64
* func [strtok](#strtok)(str: char*, delim: char const*) : char*
* func [strtol](#strtol)(str: char const*, end: char**, base: i32) : i64
* func [strtoul](#strtoul)(str: char const*, end: char**, base: i32) : u64
* func [tan](#tan)(v: f64) : f64
* func [tolower](#tolower)(arg: i32) : i32
* func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))
* func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: char const*)
* func [vfprintf](#vfprintf)(f: [FILE\*](#FILE), format: char const*, args: [libc](#libc)::[va\_list](#va\_list))
* func [vsnprintf\_s](#vsnprintf\_s)(ptr: [void\*](#void), len: u64, max: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsnprintf](#vsnprintf)(buffer: char*, len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsprintf\_s](#vsprintf\_s)(ptr: [void\*](#void), len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32



***
### HUGE\_VAL


### M\_PI


### SEEK\_CUR


### SEEK\_CUR


### SEEK\_END


### SEEK\_END


### SEEK\_SET


### SEEK\_SET


### ULLONG\_MAX


### stderr


### stdout


### CmdParser


struct [CmdParser](#CmdParser)

* options: [documentationGenerator](#documentationGenerator)::[Map<char const\*,Option>](#Map<char\-const\*,Option>)
* errors: char[]
* status: [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)



### CmdParserStatus


enum [CmdParserStatus](#CmdParserStatus)

* OK
* MISSING_ARGUMENT
* MISSING_REQUIRED



### EqualFn


typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)


### FILE


struct [FILE](#FILE)




### FILE


struct [FILE](#FILE)




### FileStatus


enum [FileStatus](#FileStatus)

* Ok
* FileNotFoundError
* IOError



### HashFn


typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


### Map


struct [Map](#Map)

* length: i32
* capacity: i32
* hashFn: [func\(K\) : u32](#\_)
* equalFn: [func\(K, K\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: [K\*](#K)
* values: [V\*](#V)
* emptyValue: [V](#V)



### MapEntry


struct [MapEntry](#MapEntry)

* key: [K](#K)
* value: [V](#V)



### MapIterator


struct [MapIterator](#MapIterator)

* m: [Map<K,V>\*](#Map<K,V>)
* it: i32
* count: i32



### Option


struct [Option](#Option)

* name: char const*
* shortName: char
* description: char const*
* value: char const*
* defaultValue: char const*
* flags: i32



### OptionFlag


enum [OptionFlag](#OptionFlag)

* HAS_ARGUMENT
* IS_REQUIRED
* IS_USED



### va\_list


struct [va\_list](#va\_list)




### ABS


func [ABS](#ABS)(a: f32) : f32


### CmdParser\_addOption


func [CmdParser\_addOption](#CmdParser\_addOption)(p: [CmdParser\*](#CmdParser), longName: char const*, shortName: char, description: char const*, flags: i32, defaultValue: char const*)


### CmdParser\_getOption


func [CmdParser\_getOption](#CmdParser\_getOption)(p: [CmdParser\*](#CmdParser), longName: char const*) : [cmdline](#cmdline)::[Option](#Option)


### CmdParser\_init


func [CmdParser\_init](#CmdParser\_init)(p: [CmdParser\*](#CmdParser))


### CmdParser\_parse


func [CmdParser\_parse](#CmdParser\_parse)(p: [CmdParser\*](#CmdParser), argc: i32, argv: char**) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)


### FileLength


func [FileLength](#FileLength)(fileName: char const*) : i64


### FileStatusAsStr


func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*


### MAX


func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)


### MIN


func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)


### MapIterator\_hasNext


func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : bool


### MapIterator\_next


func [MapIterator\_next](#MapIterator\_next)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<K,V>](#MapEntry<K,V>)


### Map\_contains


func [Map\_contains](#Map\_contains)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : bool


### Map\_empty


func [Map\_empty](#Map\_empty)(m: [Map<K,V>\*](#Map<K,V>)) : bool


### Map\_free


func [Map\_free](#Map\_free)(m: [Map<K,V>\*](#Map<K,V>))


### Map\_get


func [Map\_get](#Map\_get)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_init


func [Map\_init](#Map\_init)(m: [Map<K,V>\*](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_iter


func [Map\_iter](#Map\_iter)(m: [Map<K,V>\*](#Map<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<K,V>](#MapIterator<K,V>)


### Map\_put


func [Map\_put](#Map\_put)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K), value: [V](#V))


### Map\_remove


func [Map\_remove](#Map\_remove)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_size


func [Map\_size](#Map\_size)(m: [Map<K,V>\*](#Map<K,V>)) : i32


### PtrEqualFn


func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool


### ReadFile


func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)


### StrEqualFn


func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool


### StrHashFn


func [StrHashFn](#StrHashFn)(str: char const*) : u32


### StrMap


func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)


### WriteBytes


func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### WriteFile


func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### asinf


func [asinf](#asinf)(v: f32) : f32


### cos


func [cos](#cos)(v: f64) : f64


### cosf


func [cosf](#cosf)(v: f32) : f32


### exit


func [exit](#exit)(code: i32)


### fabs


func [fabs](#fabs)(v: f32) : f32


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fputs


func [fputs](#fputs)(format: char const*, f: [FILE\*](#FILE))


### fread


func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64


### fread


func [fread](#fread)(buf: char*, size: u64, n: i64, stream: [FILE\*](#FILE)) : i64


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### fwrite


func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64


### isalnum


func [isalnum](#isalnum)(arg: i32) : i32


### isdigit


func [isdigit](#isdigit)(arg: i32) : i32


### isspace


func [isspace](#isspace)(arg: i32) : i32


### printf


func [printf](#printf)(s: char const*)


### printf


func [printf](#printf)(s: char const*)


### sin


func [sin](#sin)(v: f64) : f64


### sinf


func [sinf](#sinf)(v: f32) : f32


### snprintf


func [snprintf](#snprintf)(s: char*, n: u64, format: char const*) : i32


### sqrt


func [sqrt](#sqrt)(v: f64) : f64


### sqrtf


func [sqrtf](#sqrtf)(v: f32) : f32


### strcmp


func [strcmp](#strcmp)(a: char const*, b: char const*) : i32


### strcpy


func [strcpy](#strcpy)(dest: char*, src: char const*) : char*


### strlen


func [strlen](#strlen)(str: char const*) : i32


### strncmp


func [strncmp](#strncmp)(a: char const*, b: char const*, num: u64) : i32


### strncpy


func [strncpy](#strncpy)(dest: char*, src: char const*, num: u64) : char*


### strtod


func [strtod](#strtod)(str: char const*, end: char**) : f64


### strtok


func [strtok](#strtok)(str: char*, delim: char const*) : char*


### strtol


func [strtol](#strtol)(str: char const*, end: char**, base: i32) : i64


### strtoul


func [strtoul](#strtoul)(str: char const*, end: char**, base: i32) : u64


### tan


func [tan](#tan)(v: f64) : f64


### tolower


func [tolower](#tolower)(arg: i32) : i32


### va\_end


func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))


### va\_start


func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: char const*)


### vfprintf


func [vfprintf](#vfprintf)(f: [FILE\*](#FILE), format: char const*, args: [libc](#libc)::[va\_list](#va\_list))


### vsnprintf


func [vsnprintf](#vsnprintf)(buffer: char*, len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsnprintf\_s


func [vsnprintf\_s](#vsnprintf\_s)(ptr: [void\*](#void), len: u64, max: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsprintf\_s


func [vsprintf\_s](#vsprintf\_s)(ptr: [void\*](#void), len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


