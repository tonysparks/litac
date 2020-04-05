# cmdline

## cmdline Imports

* [io](io\.md)
* [libc](libc\.md)
* [map](map\.md)


## cmdline Variables



## cmdline Types

* enum [CmdParserStatus](#CmdParserStatus)
* enum [OptionFlag](#OptionFlag)
* struct [CmdParser](#CmdParser)
* struct [Option](#Option)


## cmdline Functions

* func [CmdParser\_addOption](#CmdParser\_addOption)(p: [CmdParser\*](#CmdParser), longName: char const*, shortName: char, description: char const*, flags: i32, defaultValue: char const*)
* func [CmdParser\_getOption](#CmdParser\_getOption)(p: [CmdParser\*](#CmdParser), longName: char const*) : [cmdline](#cmdline)::[Option](#Option)
* func [CmdParser\_init](#CmdParser\_init)(p: [CmdParser\*](#CmdParser))
* func [CmdParser\_parse](#CmdParser\_parse)(p: [CmdParser\*](#CmdParser), argc: i32, argv: char**) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)



***
### CmdParser


struct [CmdParser](#CmdParser)

* options: [documentationGenerator](documentationGenerator\.md)::[Map<char const\*,Option>](Map<char const\*,Option>\.md)
* errors: char[]
* status: [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)



### CmdParserStatus


enum [CmdParserStatus](#CmdParserStatus)

* OK
* MISSING_ARGUMENT
* MISSING_REQUIRED



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



### CmdParser\_addOption


func [CmdParser\_addOption](#CmdParser\_addOption)(p: [CmdParser\*](#CmdParser), longName: char const*, shortName: char, description: char const*, flags: i32, defaultValue: char const*)


### CmdParser\_getOption


func [CmdParser\_getOption](#CmdParser\_getOption)(p: [CmdParser\*](#CmdParser), longName: char const*) : [cmdline](#cmdline)::[Option](#Option)


### CmdParser\_init


func [CmdParser\_init](#CmdParser\_init)(p: [CmdParser\*](#CmdParser))


### CmdParser\_parse


func [CmdParser\_parse](#CmdParser\_parse)(p: [CmdParser\*](#CmdParser), argc: i32, argv: char**) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)


