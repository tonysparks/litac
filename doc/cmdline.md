# cmdline

## cmdline Imports

* [array](array\.md)
* [io](io\.md)
* [libc](libc\.md)
* [mem](mem\.md)
* [string\_buffer](string\_buffer\.md)


## cmdline Variables



## cmdline Types

* enum [CmdParserStatus](#CmdParserStatus)
* enum [OptionFlag](#OptionFlag)
* struct [CmdParser](#CmdParser)
* struct [Option](#Option)


## cmdline Functions

* func [CmdParserInit](#CmdParserInit)(allocator: [\*const Allocator](#Allocator)) : [cmdline](#cmdline)::[CmdParser](#CmdParser)
* func [CmdParser\_addOption](#CmdParser\_addOption)(p: [\*CmdParser](#CmdParser), longName: *const char, shortName: char, description: *const char, flags: i32, defaultValue: *const char)
* func [CmdParser\_free](#CmdParser\_free)(p: [\*CmdParser](#CmdParser))
* func [CmdParser\_getOptionShort](#CmdParser\_getOptionShort)(p: [\*CmdParser](#CmdParser), shortName: char) : [\*Option](#Option)
* func [CmdParser\_getOption](#CmdParser\_getOption)(p: [\*CmdParser](#CmdParser), longName: *const char) : [\*Option](#Option)
* func [CmdParser\_hasOptionShort](#CmdParser\_hasOptionShort)(p: [\*CmdParser](#CmdParser), shortName: char) : bool
* func [CmdParser\_hasOption](#CmdParser\_hasOption)(p: [\*CmdParser](#CmdParser), longName: *const char) : bool
* func [CmdParser\_init](#CmdParser\_init)(p: [\*CmdParser](#CmdParser), allocator: [\*const Allocator](#Allocator))
* func [CmdParser\_parse](#CmdParser\_parse)(p: [\*CmdParser](#CmdParser), argc: i32, argv: **char) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)
* func [CmdParser\_printHelp](#CmdParser\_printHelp)(p: [\*CmdParser](#CmdParser), sb: [\*StringBuffer](#StringBuffer))



***
### CmdParser


struct [CmdParser](#CmdParser)

* options: [documentationGenerator](documentationGenerator\.md)::[Array<Option>](Array<Option>\.md)
* args: [documentationGenerator](documentationGenerator\.md)::[Array<char const\*>](Array<char const\*>\.md)
* errors: []char
* status: [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)
* header: *const char



### CmdParserStatus


enum [CmdParserStatus](#CmdParserStatus)

* OK
* MISSING_ARGUMENT
* MISSING_REQUIRED



### Option


struct [Option](#Option)

* name: *const char
* shortName: char
* description: *const char
* value: *const char
* defaultValue: *const char
* flags: i32



### OptionFlag


enum [OptionFlag](#OptionFlag)

* HAS_ARGUMENT
* IS_REQUIRED
* IS_USED



### CmdParserInit


func [CmdParserInit](#CmdParserInit)(allocator: [\*const Allocator](#Allocator)) : [cmdline](#cmdline)::[CmdParser](#CmdParser)


### CmdParser\_addOption


func [CmdParser\_addOption](#CmdParser\_addOption)(p: [\*CmdParser](#CmdParser), longName: *const char, shortName: char, description: *const char, flags: i32, defaultValue: *const char)


### CmdParser\_free


func [CmdParser\_free](#CmdParser\_free)(p: [\*CmdParser](#CmdParser))


### CmdParser\_getOption


func [CmdParser\_getOption](#CmdParser\_getOption)(p: [\*CmdParser](#CmdParser), longName: *const char) : [\*Option](#Option)


### CmdParser\_getOptionShort


func [CmdParser\_getOptionShort](#CmdParser\_getOptionShort)(p: [\*CmdParser](#CmdParser), shortName: char) : [\*Option](#Option)


### CmdParser\_hasOption


func [CmdParser\_hasOption](#CmdParser\_hasOption)(p: [\*CmdParser](#CmdParser), longName: *const char) : bool


### CmdParser\_hasOptionShort


func [CmdParser\_hasOptionShort](#CmdParser\_hasOptionShort)(p: [\*CmdParser](#CmdParser), shortName: char) : bool


### CmdParser\_init


func [CmdParser\_init](#CmdParser\_init)(p: [\*CmdParser](#CmdParser), allocator: [\*const Allocator](#Allocator))


### CmdParser\_parse


func [CmdParser\_parse](#CmdParser\_parse)(p: [\*CmdParser](#CmdParser), argc: i32, argv: **char) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)


### CmdParser\_printHelp


func [CmdParser\_printHelp](#CmdParser\_printHelp)(p: [\*CmdParser](#CmdParser), sb: [\*StringBuffer](#StringBuffer))


