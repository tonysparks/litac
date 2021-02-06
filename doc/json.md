# json

## json Imports

* [array](array\.md)
* [assert](assert\.md)
* [libc](libc\.md)
* [map](map\.md)
* [mem](mem\.md)
* [string\_buffer](string\_buffer\.md)


## json Variables

* const [JSON\_FALSE](#JSON\_FALSE): [\*JsonNode](#JsonNode)
* const [JSON\_NULL](#JSON\_NULL): [\*JsonNode](#JsonNode)
* const [JSON\_TRUE](#JSON\_TRUE): [\*JsonNode](#JsonNode)


## json Types

* enum [JsonParserStatus](#JsonParserStatus)
* enum [JsonType](#JsonType)
* struct [JsonNode](#JsonNode)
* struct [JsonObject](#JsonObject)
* struct [JsonParser](#JsonParser)
* typedef [documentationGenerator](documentationGenerator\.md)::[Array<JsonNode\*>](Array<JsonNode\*>\.md) as [JsonArray](#JsonArray)
* union [JsonValue](#JsonValue)


## json Functions

* func [CreateJsonArray](#CreateJsonArray)(alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)
* func [CreateJsonNumber](#CreateJsonNumber)(value: f64, alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)
* func [CreateJsonObject](#CreateJsonObject)(alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)
* func [CreateJsonStringNoDup](#CreateJsonStringNoDup)(str: *const char, alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)
* func [CreateJsonString](#CreateJsonString)(str: *const char, len: i32, alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)
* func [JsonNode\_add](#JsonNode\_add)(node: [\*JsonNode](#JsonNode), n: [\*JsonNode](#JsonNode))
* func [JsonNode\_asArray](#JsonNode\_asArray)(node: [\*JsonNode](#JsonNode)) : [\*Array<JsonNode\*>](#Array<JsonNode\*>)
* func [JsonNode\_asBool](#JsonNode\_asBool)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_asFloat](#JsonNode\_asFloat)(node: [\*JsonNode](#JsonNode)) : f64
* func [JsonNode\_asInt](#JsonNode\_asInt)(node: [\*JsonNode](#JsonNode)) : i32
* func [JsonNode\_asLong](#JsonNode\_asLong)(node: [\*JsonNode](#JsonNode)) : i64
* func [JsonNode\_asNumber](#JsonNode\_asNumber)(node: [\*JsonNode](#JsonNode)) : f64
* func [JsonNode\_asObject](#JsonNode\_asObject)(node: [\*JsonNode](#JsonNode)) : [\*JsonObject](#JsonObject)
* func [JsonNode\_asString](#JsonNode\_asString)(node: [\*JsonNode](#JsonNode)) : *const char
* func [JsonNode\_at](#JsonNode\_at)(node: [\*JsonNode](#JsonNode), index: i32) : [\*JsonNode](#JsonNode)
* func [JsonNode\_contains](#JsonNode\_contains)(node: [\*JsonNode](#JsonNode), key: *const char) : bool
* func [JsonNode\_equals](#JsonNode\_equals)(node: [\*JsonNode](#JsonNode), other: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_free](#JsonNode\_free)(node: [\*JsonNode](#JsonNode))
* func [JsonNode\_getArray](#JsonNode\_getArray)(node: [\*JsonNode](#JsonNode), key: *const char) : [\*JsonNode](#JsonNode)
* func [JsonNode\_getBool](#JsonNode\_getBool)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: bool) : bool
* func [JsonNode\_getFloat](#JsonNode\_getFloat)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: f64) : f64
* func [JsonNode\_getInt](#JsonNode\_getInt)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: i32) : i32
* func [JsonNode\_getLong](#JsonNode\_getLong)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: i64) : i64
* func [JsonNode\_getObject](#JsonNode\_getObject)(node: [\*JsonNode](#JsonNode), key: *const char) : [\*JsonNode](#JsonNode)
* func [JsonNode\_getStrCopy](#JsonNode\_getStrCopy)(node: [\*JsonNode](#JsonNode), key: *const char, str: *char, len: i32) : *char
* func [JsonNode\_getStr](#JsonNode\_getStr)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: *const char) : *const char
* func [JsonNode\_get](#JsonNode\_get)(node: [\*JsonNode](#JsonNode), key: *const char) : [\*JsonNode](#JsonNode)
* func [JsonNode\_isArray](#JsonNode\_isArray)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isBool](#JsonNode\_isBool)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isFalse](#JsonNode\_isFalse)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isNull](#JsonNode\_isNull)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isNumber](#JsonNode\_isNumber)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isObject](#JsonNode\_isObject)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isString](#JsonNode\_isString)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_isTrue](#JsonNode\_isTrue)(node: [\*JsonNode](#JsonNode)) : bool
* func [JsonNode\_print](#JsonNode\_print)(node: [\*JsonNode](#JsonNode), buf: [\*StringBuffer](#StringBuffer)) : *const char
* func [JsonNode\_putBool](#JsonNode\_putBool)(node: [\*JsonNode](#JsonNode), key: *const char, b: bool, len: i32)
* func [JsonNode\_putNumber](#JsonNode\_putNumber)(node: [\*JsonNode](#JsonNode), key: *const char, number: f64, len: i32)
* func [JsonNode\_putStr](#JsonNode\_putStr)(node: [\*JsonNode](#JsonNode), key: *const char, str: *const char, len: i32)
* func [JsonNode\_put](#JsonNode\_put)(node: [\*JsonNode](#JsonNode), key: *const char, n: [\*JsonNode](#JsonNode), len: i32)
* func [JsonNode\_size](#JsonNode\_size)(node: [\*JsonNode](#JsonNode)) : i32
* func [JsonParserInit](#JsonParserInit)(alloc: [\*const Allocator](#Allocator)) : [json](#json)::[JsonParser](#JsonParser)
* func [JsonParser\_free](#JsonParser\_free)(p: [\*JsonParser](#JsonParser))
* func [JsonParser\_hasError](#JsonParser\_hasError)(p: [\*JsonParser](#JsonParser)) : bool
* func [JsonParser\_init](#JsonParser\_init)(p: [\*JsonParser](#JsonParser), alloc: [\*const Allocator](#Allocator))
* func [JsonParser\_parseJson](#JsonParser\_parseJson)(p: [\*JsonParser](#JsonParser), buffer: *const char) : [\*JsonNode](#JsonNode)
* func [JsonTypeAsStr](#JsonTypeAsStr)(e: [json](#json)::[JsonType](#JsonType)) : *const char
* func [PrintJson](#PrintJson)(node: [\*JsonNode](#JsonNode), buf: [\*StringBuffer](#StringBuffer))



***
### JSON\_FALSE


### JSON\_NULL


### JSON\_TRUE


### JsonArray


typedef [documentationGenerator](documentationGenerator\.md)::[Array<JsonNode\*>](Array<JsonNode\*>\.md) as [JsonArray](#JsonArray)


### JsonNode


struct [JsonNode](#JsonNode)

* alloc: [\*const Allocator](#Allocator)
* type: [json](#json)::[JsonType](#JsonType)
* value: [json](#json)::[JsonValue](#JsonValue)



### JsonObject


struct [JsonObject](#JsonObject)

* indexes: [documentationGenerator](documentationGenerator\.md)::[Map<char const\*,i32>](Map<char const\*,i32>\.md)
* values: [documentationGenerator](documentationGenerator\.md)::[Array<Entry>](Array<Entry>\.md)



### JsonParser


struct [JsonParser](#JsonParser)

* alloc: [\*const Allocator](#Allocator)
* status: [json](#json)::[JsonParserStatus](#JsonParserStatus)
* errorMsg: []char
* token: [json](#json)::[Token](#Token)
* buffer: [string\_buffer](string\_buffer\.md)::[StringBuffer](StringBuffer\.md)
* stream: *const char
* lineStart: *const char



### JsonParserStatus


enum [JsonParserStatus](#JsonParserStatus)

* OK
* WARNING
* ERROR



### JsonType


enum [JsonType](#JsonType)

* NULL
* BOOLEAN
* NUMBER
* STRING
* OBJECT
* ARRAY



### JsonValue


union [JsonValue](#JsonValue)

* boolValue: bool
* doubleValue: f64
* strValue: *const char
* objValue: [\*JsonObject](#JsonObject)
* arrayValue: [\*Array<JsonNode\*>](#Array<JsonNode\*>)



### CreateJsonArray


func [CreateJsonArray](#CreateJsonArray)(alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)


### CreateJsonNumber


func [CreateJsonNumber](#CreateJsonNumber)(value: f64, alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)


### CreateJsonObject


func [CreateJsonObject](#CreateJsonObject)(alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)


### CreateJsonString


func [CreateJsonString](#CreateJsonString)(str: *const char, len: i32, alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)


### CreateJsonStringNoDup


func [CreateJsonStringNoDup](#CreateJsonStringNoDup)(str: *const char, alloc: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)


### JsonNode\_add


func [JsonNode\_add](#JsonNode\_add)(node: [\*JsonNode](#JsonNode), n: [\*JsonNode](#JsonNode))


### JsonNode\_asArray


func [JsonNode\_asArray](#JsonNode\_asArray)(node: [\*JsonNode](#JsonNode)) : [\*Array<JsonNode\*>](#Array<JsonNode\*>)


### JsonNode\_asBool


func [JsonNode\_asBool](#JsonNode\_asBool)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_asFloat


func [JsonNode\_asFloat](#JsonNode\_asFloat)(node: [\*JsonNode](#JsonNode)) : f64


### JsonNode\_asInt


func [JsonNode\_asInt](#JsonNode\_asInt)(node: [\*JsonNode](#JsonNode)) : i32


### JsonNode\_asLong


func [JsonNode\_asLong](#JsonNode\_asLong)(node: [\*JsonNode](#JsonNode)) : i64


### JsonNode\_asNumber


func [JsonNode\_asNumber](#JsonNode\_asNumber)(node: [\*JsonNode](#JsonNode)) : f64


### JsonNode\_asObject


func [JsonNode\_asObject](#JsonNode\_asObject)(node: [\*JsonNode](#JsonNode)) : [\*JsonObject](#JsonObject)


### JsonNode\_asString


func [JsonNode\_asString](#JsonNode\_asString)(node: [\*JsonNode](#JsonNode)) : *const char


### JsonNode\_at


func [JsonNode\_at](#JsonNode\_at)(node: [\*JsonNode](#JsonNode), index: i32) : [\*JsonNode](#JsonNode)


### JsonNode\_contains


func [JsonNode\_contains](#JsonNode\_contains)(node: [\*JsonNode](#JsonNode), key: *const char) : bool


### JsonNode\_equals


func [JsonNode\_equals](#JsonNode\_equals)(node: [\*JsonNode](#JsonNode), other: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_free


func [JsonNode\_free](#JsonNode\_free)(node: [\*JsonNode](#JsonNode))


### JsonNode\_get


func [JsonNode\_get](#JsonNode\_get)(node: [\*JsonNode](#JsonNode), key: *const char) : [\*JsonNode](#JsonNode)


### JsonNode\_getArray


func [JsonNode\_getArray](#JsonNode\_getArray)(node: [\*JsonNode](#JsonNode), key: *const char) : [\*JsonNode](#JsonNode)


### JsonNode\_getBool


func [JsonNode\_getBool](#JsonNode\_getBool)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: bool) : bool


### JsonNode\_getFloat


func [JsonNode\_getFloat](#JsonNode\_getFloat)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: f64) : f64


### JsonNode\_getInt


func [JsonNode\_getInt](#JsonNode\_getInt)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: i32) : i32


### JsonNode\_getLong


func [JsonNode\_getLong](#JsonNode\_getLong)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: i64) : i64


### JsonNode\_getObject


func [JsonNode\_getObject](#JsonNode\_getObject)(node: [\*JsonNode](#JsonNode), key: *const char) : [\*JsonNode](#JsonNode)


### JsonNode\_getStr


func [JsonNode\_getStr](#JsonNode\_getStr)(node: [\*JsonNode](#JsonNode), key: *const char, defaultValue: *const char) : *const char


### JsonNode\_getStrCopy


func [JsonNode\_getStrCopy](#JsonNode\_getStrCopy)(node: [\*JsonNode](#JsonNode), key: *const char, str: *char, len: i32) : *char


### JsonNode\_isArray


func [JsonNode\_isArray](#JsonNode\_isArray)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isBool


func [JsonNode\_isBool](#JsonNode\_isBool)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isFalse


func [JsonNode\_isFalse](#JsonNode\_isFalse)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isNull


func [JsonNode\_isNull](#JsonNode\_isNull)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isNumber


func [JsonNode\_isNumber](#JsonNode\_isNumber)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isObject


func [JsonNode\_isObject](#JsonNode\_isObject)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isString


func [JsonNode\_isString](#JsonNode\_isString)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_isTrue


func [JsonNode\_isTrue](#JsonNode\_isTrue)(node: [\*JsonNode](#JsonNode)) : bool


### JsonNode\_print


func [JsonNode\_print](#JsonNode\_print)(node: [\*JsonNode](#JsonNode), buf: [\*StringBuffer](#StringBuffer)) : *const char


### JsonNode\_put


func [JsonNode\_put](#JsonNode\_put)(node: [\*JsonNode](#JsonNode), key: *const char, n: [\*JsonNode](#JsonNode), len: i32)


### JsonNode\_putBool


func [JsonNode\_putBool](#JsonNode\_putBool)(node: [\*JsonNode](#JsonNode), key: *const char, b: bool, len: i32)


### JsonNode\_putNumber


func [JsonNode\_putNumber](#JsonNode\_putNumber)(node: [\*JsonNode](#JsonNode), key: *const char, number: f64, len: i32)


### JsonNode\_putStr


func [JsonNode\_putStr](#JsonNode\_putStr)(node: [\*JsonNode](#JsonNode), key: *const char, str: *const char, len: i32)


### JsonNode\_size


func [JsonNode\_size](#JsonNode\_size)(node: [\*JsonNode](#JsonNode)) : i32


### JsonParserInit


func [JsonParserInit](#JsonParserInit)(alloc: [\*const Allocator](#Allocator)) : [json](#json)::[JsonParser](#JsonParser)


### JsonParser\_free


func [JsonParser\_free](#JsonParser\_free)(p: [\*JsonParser](#JsonParser))


### JsonParser\_hasError


func [JsonParser\_hasError](#JsonParser\_hasError)(p: [\*JsonParser](#JsonParser)) : bool


### JsonParser\_init


func [JsonParser\_init](#JsonParser\_init)(p: [\*JsonParser](#JsonParser), alloc: [\*const Allocator](#Allocator))


### JsonParser\_parseJson


func [JsonParser\_parseJson](#JsonParser\_parseJson)(p: [\*JsonParser](#JsonParser), buffer: *const char) : [\*JsonNode](#JsonNode)


### JsonTypeAsStr


func [JsonTypeAsStr](#JsonTypeAsStr)(e: [json](#json)::[JsonType](#JsonType)) : *const char


### PrintJson


func [PrintJson](#PrintJson)(node: [\*JsonNode](#JsonNode), buf: [\*StringBuffer](#StringBuffer))


