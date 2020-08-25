# libc

## libc Imports



## libc Variables

* const [CHAR\_BIT](#CHAR\_BIT): i32
* const [CHAR\_MAX](#CHAR\_MAX): i32
* const [CHAR\_MIN](#CHAR\_MIN): i32
* const [HUGE\_VAL](#HUGE\_VAL): f64
* const [INT\_MAX](#INT\_MAX): i32
* const [INT\_MIN](#INT\_MIN): i32
* const [LONG\_MAX](#LONG\_MAX): i32
* const [LONG\_MIN](#LONG\_MIN): i32
* const [M\_PI](#M\_PI): f64
* const [SCHAR\_MAX](#SCHAR\_MAX): i32
* const [SCHAR\_MIN](#SCHAR\_MIN): i32
* const [SEEK\_CUR](#SEEK\_CUR): i32
* const [SEEK\_END](#SEEK\_END): i32
* const [SEEK\_SET](#SEEK\_SET): i32
* const [SHRT\_MAX](#SHRT\_MAX): i32
* const [SHRT\_MIN](#SHRT\_MIN): i32
* const [UINT\_MAX](#UINT\_MAX): i32
* const [ULLONG\_MAX](#ULLONG\_MAX): i64
* const [USHRT\_MAX](#USHRT\_MAX): i32
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
* func [calloc](#calloc)(num: usize, size: usize) : [\*void](#void)
* func [cos](#cos)(v: f64) : f64
* func [cosf](#cosf)(v: f32) : f32
* func [exit](#exit)(code: i32)
* func [fabs](#fabs)(v: f32) : f32
* func [fclose](#fclose)(stream: [\*FILE](#FILE))
* func [ferror](#ferror)(stream: [\*FILE](#FILE)) : i32
* func [fgets](#fgets)(buf: *char, n: i64, stream: [\*FILE](#FILE)) : *char
* func [fopen](#fopen)(fileName: *const char, openType: *const char) : [\*FILE](#FILE)
* func [fputs](#fputs)(format: *const char, f: [\*FILE](#FILE))
* func [fread](#fread)(buf: *char, size: usize, n: usize, stream: [\*FILE](#FILE)) : i64
* func [free](#free)(ptr: [\*void](#void))
* func [fseek](#fseek)(stream: [\*FILE](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [\*FILE](#FILE)) : i64
* func [fwrite](#fwrite)(buf: [\*const void](#void), sizeOfElements: usize, numOfElements: usize, stream: [\*FILE](#FILE)) : u64
* func [isalnum](#isalnum)(arg: i32) : i32
* func [isalpha](#isalpha)(c: char) : bool
* func [isdigit](#isdigit)(arg: i32) : i32
* func [isspace](#isspace)(arg: i32) : i32
* func [malloc](#malloc)(size: usize) : [\*void](#void)
* func [memcpy](#memcpy)(dest: [\*void](#void), src: [\*const void](#void), num: usize) : [\*void](#void)
* func [memmove](#memmove)(dest: [\*void](#void), src: [\*const void](#void), num: usize) : [\*void](#void)
* func [memset](#memset)(ptr: [\*void](#void), value: i32, num: usize) : [\*void](#void)
* func [printf](#printf)(s: *const char)
* func [realloc](#realloc)(ptr: [\*void](#void), size: usize) : [\*void](#void)
* func [sin](#sin)(v: f64) : f64
* func [sinf](#sinf)(v: f32) : f32
* func [snprintf](#snprintf)(s: *char, n: u64, format: *const char) : i32
* func [sprintf](#sprintf)(b: *char, format: *const char) : i32
* func [sqrt](#sqrt)(v: f64) : f64
* func [sqrtf](#sqrtf)(v: f32) : f32
* func [strcat](#strcat)(dest: *char, src: *const char) : *char
* func [strcmp](#strcmp)(a: *const char, b: *const char) : i32
* func [strcpy](#strcpy)(dest: *char, src: *const char) : *char
* func [strlen](#strlen)(str: *const char) : i32
* func [strncat](#strncat)(dest: *char, src: *const char, num: u64) : *char
* func [strncmp](#strncmp)(a: *const char, b: *const char, num: u64) : i32
* func [strncpy](#strncpy)(dest: *char, src: *const char, num: u64) : *char
* func [strnlen](#strnlen)(str: *const char, len: u64) : i32
* func [strtod](#strtod)(str: *const char, end: **char) : f64
* func [strtok](#strtok)(str: *char, delim: *const char) : *char
* func [strtol](#strtol)(str: *const char, end: **char, base: i32) : i64
* func [strtoul](#strtoul)(str: *const char, end: **char, base: i32) : u64
* func [system](#system)(cmd: *const char) : i32
* func [tan](#tan)(v: f64) : f64
* func [tolower](#tolower)(arg: i32) : i32
* func [va\_copy](#va\_copy)(a: [libc](#libc)::[va\_list](#va\_list), b: [libc](#libc)::[va\_list](#va\_list))
* func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))
* func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: *const char)
* func [vfprintf](#vfprintf)(f: [\*FILE](#FILE), format: *const char, args: [libc](#libc)::[va\_list](#va\_list))
* func [vprintf](#vprintf)(format: *const char, ap: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsnprintf\_s](#vsnprintf\_s)(ptr: [\*void](#void), len: u64, max: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsnprintf](#vsnprintf)(buffer: *char, len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsprintf\_s](#vsprintf\_s)(ptr: [\*void](#void), len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32



***
### CHAR\_BIT


### CHAR\_MAX


### CHAR\_MIN


### HUGE\_VAL


### INT\_MAX


### INT\_MIN


### LONG\_MAX


### LONG\_MIN


### M\_PI


### SCHAR\_MAX


### SCHAR\_MIN


### SEEK\_CUR


### SEEK\_END


### SEEK\_SET


### SHRT\_MAX


### SHRT\_MIN


### UINT\_MAX


### ULLONG\_MAX


### USHRT\_MAX


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


### calloc


func [calloc](#calloc)(num: usize, size: usize) : [\*void](#void)


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


func [fread](#fread)(buf: *char, size: usize, n: usize, stream: [\*FILE](#FILE)) : i64


### free


func [free](#free)(ptr: [\*void](#void))


### fseek


func [fseek](#fseek)(stream: [\*FILE](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [\*FILE](#FILE)) : i64


### fwrite


func [fwrite](#fwrite)(buf: [\*const void](#void), sizeOfElements: usize, numOfElements: usize, stream: [\*FILE](#FILE)) : u64


### isalnum


func [isalnum](#isalnum)(arg: i32) : i32


### isalpha


func [isalpha](#isalpha)(c: char) : bool


### isdigit


func [isdigit](#isdigit)(arg: i32) : i32


### isspace


func [isspace](#isspace)(arg: i32) : i32


### malloc


func [malloc](#malloc)(size: usize) : [\*void](#void)


### memcpy


func [memcpy](#memcpy)(dest: [\*void](#void), src: [\*const void](#void), num: usize) : [\*void](#void)


### memmove


func [memmove](#memmove)(dest: [\*void](#void), src: [\*const void](#void), num: usize) : [\*void](#void)


### memset


func [memset](#memset)(ptr: [\*void](#void), value: i32, num: usize) : [\*void](#void)


### printf


func [printf](#printf)(s: *const char)


### realloc


func [realloc](#realloc)(ptr: [\*void](#void), size: usize) : [\*void](#void)


### sin


func [sin](#sin)(v: f64) : f64


### sinf


func [sinf](#sinf)(v: f32) : f32


### snprintf


func [snprintf](#snprintf)(s: *char, n: u64, format: *const char) : i32


### sprintf


func [sprintf](#sprintf)(b: *char, format: *const char) : i32


### sqrt


func [sqrt](#sqrt)(v: f64) : f64


### sqrtf


func [sqrtf](#sqrtf)(v: f32) : f32


### strcat


func [strcat](#strcat)(dest: *char, src: *const char) : *char


### strcmp


func [strcmp](#strcmp)(a: *const char, b: *const char) : i32


### strcpy


func [strcpy](#strcpy)(dest: *char, src: *const char) : *char


### strlen


func [strlen](#strlen)(str: *const char) : i32


### strncat


func [strncat](#strncat)(dest: *char, src: *const char, num: u64) : *char


### strncmp


func [strncmp](#strncmp)(a: *const char, b: *const char, num: u64) : i32


### strncpy


func [strncpy](#strncpy)(dest: *char, src: *const char, num: u64) : *char


### strnlen


func [strnlen](#strnlen)(str: *const char, len: u64) : i32


### strtod


func [strtod](#strtod)(str: *const char, end: **char) : f64


### strtok


func [strtok](#strtok)(str: *char, delim: *const char) : *char


### strtol


func [strtol](#strtol)(str: *const char, end: **char, base: i32) : i64


### strtoul


func [strtoul](#strtoul)(str: *const char, end: **char, base: i32) : u64


### system


func [system](#system)(cmd: *const char) : i32


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


### vprintf


func [vprintf](#vprintf)(format: *const char, ap: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsnprintf


func [vsnprintf](#vsnprintf)(buffer: *char, len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsnprintf\_s


func [vsnprintf\_s](#vsnprintf\_s)(ptr: [\*void](#void), len: u64, max: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsprintf\_s


func [vsprintf\_s](#vsprintf\_s)(ptr: [\*void](#void), len: u64, format: *const char, args: [libc](#libc)::[va\_list](#va\_list)) : i32


