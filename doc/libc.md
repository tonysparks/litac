# libc

## libc Imports



## libc Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* const [HUGE\_VAL](#HUGE\_VAL): f64
* const [M\_PI](#M\_PI): f64
* const [ULLONG\_MAX](#ULLONG\_MAX): i64
* const [stderr](#stderr): [\*FILE](#FILE)
* const [stdout](#stdout): [\*FILE](#FILE)


## libc Types

* struct [FILE](#FILE)
* struct [va\_list](#va\_list)


## libc Functions

* func [ABS](#ABS)(a: f32) : f32
* func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [asinf](#asinf)(v: f32) : f32
* func [cos](#cos)(v: f64) : f64
* func [cosf](#cosf)(v: f32) : f32
* func [exit](#exit)(code: i32)
* func [fabs](#fabs)(v: f32) : f32
* func [fclose](#fclose)(stream: [\*FILE](#FILE))
* func [ferror](#ferror)(stream: [\*FILE](#FILE)) : i32
* func [fgets](#fgets)(buf: *char, n: i64, stream: [\*FILE](#FILE)) : *char
* func [fopen](#fopen)(fileName: *const char, openType: *const char) : [\*FILE](#FILE)
* func [fputs](#fputs)(format: *const char, f: [\*FILE](#FILE))
* func [fread](#fread)(buf: *char, size: u64, n: i64, stream: [\*FILE](#FILE)) : i64
* func [fseek](#fseek)(stream: [\*FILE](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [\*FILE](#FILE)) : i64
* func [isalnum](#isalnum)(arg: i32) : i32
* func [isdigit](#isdigit)(arg: i32) : i32
* func [isspace](#isspace)(arg: i32) : i32
* func [printf](#printf)(s: *const char)
* func [sin](#sin)(v: f64) : f64
* func [sinf](#sinf)(v: f32) : f32
* func [snprintf](#snprintf)(s: *char, n: u64, format: *const char) : i32
* func [sqrt](#sqrt)(v: f64) : f64
* func [sqrtf](#sqrtf)(v: f32) : f32
* func [strcmp](#strcmp)(a: *const char, b: *const char) : i32
* func [strcpy](#strcpy)(dest: *char, src: *const char) : *char
* func [strlen](#strlen)(str: *const char) : i32
* func [strncmp](#strncmp)(a: *const char, b: *const char, num: u64) : i32
* func [strncpy](#strncpy)(dest: *char, src: *const char, num: u64) : *char
* func [strnlen](#strnlen)(str: *const char, len: u64) : i32
* func [strtod](#strtod)(str: *const char, end: *char) : f64
* func [strtok](#strtok)(str: *char, delim: *const char) : *char
* func [strtol](#strtol)(str: *const char, end: *char, base: i32) : i64
* func [strtoul](#strtoul)(str: *const char, end: *char, base: i32) : u64
* func [tan](#tan)(v: f64) : f64
* func [tolower](#tolower)(arg: i32) : i32
* func [va\_copy](#va\_copy)(a: [libc](#libc)::[va\_list](#va\_list), b: [libc](#libc)::[va\_list](#va\_list))
* func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))
* func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: *const char)
* func [vfprintf](#vfprintf)(f: [\*FILE](#FILE), format: *const char, args: [libc](#libc)::[va\_list](#va\_list))
* func [vsnprintf\_s](#vsnprintf\_s)(ptr: [\*void](#void), len: u64, max: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsnprintf](#vsnprintf)(buffer: *char, len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsprintf\_s](#vsprintf\_s)(ptr: [\*void](#void), len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32



***
### HUGE\_VAL


### M\_PI


### SEEK\_CUR


### SEEK\_END


### SEEK\_SET


### ULLONG\_MAX


### stderr


### stdout


### FILE


struct [FILE](#FILE)




### va\_list


struct [va\_list](#va\_list)




### ABS


func [ABS](#ABS)(a: f32) : f32


### MAX


func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)


### MIN


func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)


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


func [fclose](#fclose)(stream: [\*FILE](#FILE))


### ferror


func [ferror](#ferror)(stream: [\*FILE](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: *char, n: i64, stream: [\*FILE](#FILE)) : *char


### fopen


func [fopen](#fopen)(fileName: *const char, openType: *const char) : [\*FILE](#FILE)


### fputs


func [fputs](#fputs)(format: *const char, f: [\*FILE](#FILE))


### fread


func [fread](#fread)(buf: *char, size: u64, n: i64, stream: [\*FILE](#FILE)) : i64


### fseek


func [fseek](#fseek)(stream: [\*FILE](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [\*FILE](#FILE)) : i64


### isalnum


func [isalnum](#isalnum)(arg: i32) : i32


### isdigit


func [isdigit](#isdigit)(arg: i32) : i32


### isspace


func [isspace](#isspace)(arg: i32) : i32


### printf


func [printf](#printf)(s: *const char)


### sin


func [sin](#sin)(v: f64) : f64


### sinf


func [sinf](#sinf)(v: f32) : f32


### snprintf


func [snprintf](#snprintf)(s: *char, n: u64, format: *const char) : i32


### sqrt


func [sqrt](#sqrt)(v: f64) : f64


### sqrtf


func [sqrtf](#sqrtf)(v: f32) : f32


### strcmp


func [strcmp](#strcmp)(a: *const char, b: *const char) : i32


### strcpy


func [strcpy](#strcpy)(dest: *char, src: *const char) : *char


### strlen


func [strlen](#strlen)(str: *const char) : i32


### strncmp


func [strncmp](#strncmp)(a: *const char, b: *const char, num: u64) : i32


### strncpy


func [strncpy](#strncpy)(dest: *char, src: *const char, num: u64) : *char


### strnlen


func [strnlen](#strnlen)(str: *const char, len: u64) : i32


### strtod


func [strtod](#strtod)(str: *const char, end: *char) : f64


### strtok


func [strtok](#strtok)(str: *char, delim: *const char) : *char


### strtol


func [strtol](#strtol)(str: *const char, end: *char, base: i32) : i64


### strtoul


func [strtoul](#strtoul)(str: *const char, end: *char, base: i32) : u64


### tan


func [tan](#tan)(v: f64) : f64


### tolower


func [tolower](#tolower)(arg: i32) : i32


### va\_copy


func [va\_copy](#va\_copy)(a: [libc](#libc)::[va\_list](#va\_list), b: [libc](#libc)::[va\_list](#va\_list))


### va\_end


func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))


### va\_start


func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: *const char)


### vfprintf


func [vfprintf](#vfprintf)(f: [\*FILE](#FILE), format: *const char, args: [libc](#libc)::[va\_list](#va\_list))


### vsnprintf


func [vsnprintf](#vsnprintf)(buffer: *char, len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsnprintf\_s


func [vsnprintf\_s](#vsnprintf\_s)(ptr: [\*void](#void), len: u64, max: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsprintf\_s


func [vsprintf\_s](#vsprintf\_s)(ptr: [\*void](#void), len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32


