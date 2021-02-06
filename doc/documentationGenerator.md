# documentationGenerator

## documentationGenerator Imports

* [array](array\.md)
* [assert](assert\.md)
* [builtins](builtins\.md)
* [builtins](builtins\.md)
* [cmdline](cmdline\.md)
* [gl](gl\.md)
* [io](io\.md)
* [json](json\.md)
* [libc](libc\.md)
* [map](map\.md)
* [mem](mem\.md)
* [string\_buffer](string\_buffer\.md)
* [string\_view](string\_view\.md)
* [string](string\.md)
* [thread\_posix](thread\_posix\.md)
* [type](type\.md)


## documentationGenerator Variables



## documentationGenerator Types

* struct [Array<Allocation\*>](#Array<Allocation\*>)
* struct [Array<Entry>](#Array<Entry>)
* struct [Array<JsonNode\*>](#Array<JsonNode\*>)
* struct [Array<Option>](#Array<Option>)
* struct [Array<T>](#Array<T>)
* struct [Array<char const\*>](#Array<char\-const\*>)
* struct [Map<K,V>](#Map<K,V>)
* struct [Map<StringView,V>](#Map<StringView,V>)
* struct [Map<char const\*,V>](#Map<char\-const\*,V>)
* struct [Map<char const\*,i32>](#Map<char\-const\*,i32>)
* struct [MapEntry<K,V>](#MapEntry<K,V>)
* struct [MapIterator<K,V>](#MapIterator<K,V>)
* typedef [func\(K, K\) : bool](#\_) as [EqualFn<K>](#EqualFn<K>)
* typedef [func\(K\) : u32](#\_) as [HashFn<K>](#HashFn<K>)
* typedef [func\(\*const char, \*const char\) : bool](#\_) as [EqualFn<char const\*>](#EqualFn<char\-const\*>)
* typedef [func\(\*const char\) : u32](#\_) as [HashFn<char const\*>](#HashFn<char\-const\*>)
* typedef [func\(struct StringView, struct StringView\) : bool](#\_) as [EqualFn<StringView>](#EqualFn<StringView>)
* typedef [func\(struct StringView\) : u32](#\_) as [HashFn<StringView>](#HashFn<StringView>)


## documentationGenerator Functions

* func [ArrayInit<Option>](#ArrayInit<Option>)(initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Array<Option>](#Array<Option>)
* func [ArrayInit<char const\*>](#ArrayInit<char\-const\*>)(initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Array<char const\*>](#Array<char\-const\*>)
* func [Array\_add<Allocation\*>](#Array\_add<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), element: [\*Allocation](#Allocation))
* func [Array\_add<Entry>](#Array\_add<Entry>)(a: [\*Array<Entry>](#Array<Entry>), element: [json](json\.md)::[Entry](Entry\.md))
* func [Array\_add<JsonNode\*>](#Array\_add<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>), element: [\*JsonNode](#JsonNode))
* func [Array\_add<Option>](#Array\_add<Option>)(a: [\*Array<Option>](#Array<Option>), element: [cmdline](cmdline\.md)::[Option](Option\.md))
* func [Array\_add<char const\*>](#Array\_add<char\-const\*>)(a: [\*Array<char const\*>](#Array<char\-const\*>), element: *const char)
* func [Array\_free<Allocation\*>](#Array\_free<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>))
* func [Array\_free<Entry>](#Array\_free<Entry>)(a: [\*Array<Entry>](#Array<Entry>))
* func [Array\_free<JsonNode\*>](#Array\_free<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>))
* func [Array\_free<Option>](#Array\_free<Option>)(a: [\*Array<Option>](#Array<Option>))
* func [Array\_get<Allocation\*>](#Array\_get<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), index: i32) : [\*Allocation](#Allocation)
* func [Array\_get<Entry>](#Array\_get<Entry>)(a: [\*Array<Entry>](#Array<Entry>), index: i32) : [json](json\.md)::[Entry](Entry\.md)
* func [Array\_get<JsonNode\*>](#Array\_get<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>), index: i32) : [\*JsonNode](#JsonNode)
* func [Array\_init<Allocation\*>](#Array\_init<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [Array\_init<Entry>](#Array\_init<Entry>)(a: [\*Array<Entry>](#Array<Entry>), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [Array\_init<JsonNode\*>](#Array\_init<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [Array\_init<Option>](#Array\_init<Option>)(a: [\*Array<Option>](#Array<Option>), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [Array\_init<char const\*>](#Array\_init<char\-const\*>)(a: [\*Array<char const\*>](#Array<char\-const\*>), initialSize: i32, alloc: [\*const Allocator](#Allocator))
* func [Array\_removeAt<Allocation\*>](#Array\_removeAt<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), index: i32) : [\*Allocation](#Allocation)
* func [Array\_size<Allocation\*>](#Array\_size<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>)) : i32
* func [Array\_size<Entry>](#Array\_size<Entry>)(a: [\*Array<Entry>](#Array<Entry>)) : i32
* func [Array\_size<JsonNode\*>](#Array\_size<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>)) : i32
* func [Array\_size<Option>](#Array\_size<Option>)(a: [\*Array<Option>](#Array<Option>)) : i32
* func [MIN<i32>](#MIN<i32>)(a: i32, b: i32) : i32
* func [Map\_contains<char const\*,i32>](#Map\_contains<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), key: *const char) : bool
* func [Map\_free<char const\*,i32>](#Map\_free<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>))
* func [Map\_get<char const\*,i32>](#Map\_get<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), key: *const char) : i32
* func [Map\_init<char const\*,i32>](#Map\_init<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), emptyValue: i32, initialSize: i32, hashFn: [func\(\*const char\) : u32](#\_), equalFn: [func\(\*const char, \*const char\) : bool](#\_), alloc: [\*const Allocator](#Allocator), emptyKey: *const char)
* func [Map\_put<char const\*,i32>](#Map\_put<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), key: *const char, value: i32)
* func [Map\_size<char const\*,i32>](#Map\_size<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>)) : i32
* func [c::MIN<usize>](#c::MIN<usize>)(a: usize, b: usize) : usize
* func [mem::new<JsonArray>](#mem::new<JsonArray>)(a: [\*const Allocator](#Allocator)) : [\*Array<JsonNode\*>](#Array<JsonNode\*>)
* func [mem::new<JsonNode>](#mem::new<JsonNode>)(a: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)
* func [mem::new<JsonObject>](#mem::new<JsonObject>)(a: [\*const Allocator](#Allocator)) : [\*JsonObject](#JsonObject)
* func [new<Allocation>](#new<Allocation>)(a: [\*const Allocator](#Allocator)) : [\*Allocation](#Allocation)
* func [new<Arena>](#new<Arena>)(a: [\*const Allocator](#Allocator)) : [\*Arena](#Arena)



***
### Array<Allocation\*>


struct [Array<Allocation\*>](#Array<Allocation\*>)

* length: i32
* capacity: i32
* elements: [\*\*Allocation](#Allocation)
* alloc: [\*const Allocator](#Allocator)



### Array<Entry>


struct [Array<Entry>](#Array<Entry>)

* length: i32
* capacity: i32
* elements: [\*Entry](#Entry)
* alloc: [\*const Allocator](#Allocator)



### Array<JsonNode\*>


struct [Array<JsonNode\*>](#Array<JsonNode\*>)

* length: i32
* capacity: i32
* elements: [\*\*JsonNode](#JsonNode)
* alloc: [\*const Allocator](#Allocator)



### Array<Option>


struct [Array<Option>](#Array<Option>)

* length: i32
* capacity: i32
* elements: [\*Option](#Option)
* alloc: [\*const Allocator](#Allocator)



### Array<T>


struct [Array<T>](#Array<T>)

* length: i32
* capacity: i32
* elements: [\*T](#T)
* alloc: [\*const Allocator](#Allocator)



### Array<char const\*>


struct [Array<char const\*>](#Array<char\-const\*>)

* length: i32
* capacity: i32
* elements: **const char
* alloc: [\*const Allocator](#Allocator)



### EqualFn<K>


typedef [func\(K, K\) : bool](#\_) as [EqualFn<K>](#EqualFn<K>)


### EqualFn<StringView>


typedef [func\(struct StringView, struct StringView\) : bool](#\_) as [EqualFn<StringView>](#EqualFn<StringView>)


### EqualFn<char const\*>


typedef [func\(\*const char, \*const char\) : bool](#\_) as [EqualFn<char const\*>](#EqualFn<char\-const\*>)


### HashFn<K>


typedef [func\(K\) : u32](#\_) as [HashFn<K>](#HashFn<K>)


### HashFn<StringView>


typedef [func\(struct StringView\) : u32](#\_) as [HashFn<StringView>](#HashFn<StringView>)


### HashFn<char const\*>


typedef [func\(\*const char\) : u32](#\_) as [HashFn<char const\*>](#HashFn<char\-const\*>)


### Map<K,V>


struct [Map<K,V>](#Map<K,V>)

* length: i32
* capacity: i32
* hashFn: [func\(K\) : u32](#\_)
* equalFn: [func\(K, K\) : bool](#\_)
* alloc: [\*const Allocator](#Allocator)
* keys: [\*K](#K)
* values: [\*V](#V)
* emptyValue: [V](#V)
* emptyKey: [K](#K)



### Map<StringView,V>


struct [Map<StringView,V>](#Map<StringView,V>)

* length: i32
* capacity: i32
* hashFn: [func\(struct StringView\) : u32](#\_)
* equalFn: [func\(struct StringView, struct StringView\) : bool](#\_)
* alloc: [\*const Allocator](#Allocator)
* keys: [\*StringView](#StringView)
* values: [\*V](#V)
* emptyValue: [V](#V)
* emptyKey: [string\_view](string\_view\.md)::[StringView](StringView\.md)



### Map<char const\*,V>


struct [Map<char const\*,V>](#Map<char\-const\*,V>)

* length: i32
* capacity: i32
* hashFn: [func\(\*const char\) : u32](#\_)
* equalFn: [func\(\*const char, \*const char\) : bool](#\_)
* alloc: [\*const Allocator](#Allocator)
* keys: **const char
* values: [\*V](#V)
* emptyValue: [V](#V)
* emptyKey: *const char



### Map<char const\*,i32>


struct [Map<char const\*,i32>](#Map<char\-const\*,i32>)

* length: i32
* capacity: i32
* hashFn: [func\(\*const char\) : u32](#\_)
* equalFn: [func\(\*const char, \*const char\) : bool](#\_)
* alloc: [\*const Allocator](#Allocator)
* keys: **const char
* values: *i32
* emptyValue: i32
* emptyKey: *const char



### MapEntry<K,V>


struct [MapEntry<K,V>](#MapEntry<K,V>)

* key: [K](#K)
* value: [V](#V)
* valuePtr: [\*V](#V)



### MapIterator<K,V>


struct [MapIterator<K,V>](#MapIterator<K,V>)

* m: [\*Map<K,V>](#Map<K,V>)
* it: i32
* count: i32



### ArrayInit<Option>


func [ArrayInit<Option>](#ArrayInit<Option>)(initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Array<Option>](#Array<Option>)


### ArrayInit<char const\*>


func [ArrayInit<char const\*>](#ArrayInit<char\-const\*>)(initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Array<char const\*>](#Array<char\-const\*>)


### Array\_add<Allocation\*>


func [Array\_add<Allocation\*>](#Array\_add<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), element: [\*Allocation](#Allocation))


### Array\_add<Entry>


func [Array\_add<Entry>](#Array\_add<Entry>)(a: [\*Array<Entry>](#Array<Entry>), element: [json](json\.md)::[Entry](Entry\.md))


### Array\_add<JsonNode\*>


func [Array\_add<JsonNode\*>](#Array\_add<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>), element: [\*JsonNode](#JsonNode))


### Array\_add<Option>


func [Array\_add<Option>](#Array\_add<Option>)(a: [\*Array<Option>](#Array<Option>), element: [cmdline](cmdline\.md)::[Option](Option\.md))


### Array\_add<char const\*>


func [Array\_add<char const\*>](#Array\_add<char\-const\*>)(a: [\*Array<char const\*>](#Array<char\-const\*>), element: *const char)


### Array\_free<Allocation\*>


func [Array\_free<Allocation\*>](#Array\_free<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>))


### Array\_free<Entry>


func [Array\_free<Entry>](#Array\_free<Entry>)(a: [\*Array<Entry>](#Array<Entry>))


### Array\_free<JsonNode\*>


func [Array\_free<JsonNode\*>](#Array\_free<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>))


### Array\_free<Option>


func [Array\_free<Option>](#Array\_free<Option>)(a: [\*Array<Option>](#Array<Option>))


### Array\_get<Allocation\*>


func [Array\_get<Allocation\*>](#Array\_get<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), index: i32) : [\*Allocation](#Allocation)


### Array\_get<Entry>


func [Array\_get<Entry>](#Array\_get<Entry>)(a: [\*Array<Entry>](#Array<Entry>), index: i32) : [json](json\.md)::[Entry](Entry\.md)


### Array\_get<JsonNode\*>


func [Array\_get<JsonNode\*>](#Array\_get<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>), index: i32) : [\*JsonNode](#JsonNode)


### Array\_init<Allocation\*>


func [Array\_init<Allocation\*>](#Array\_init<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### Array\_init<Entry>


func [Array\_init<Entry>](#Array\_init<Entry>)(a: [\*Array<Entry>](#Array<Entry>), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### Array\_init<JsonNode\*>


func [Array\_init<JsonNode\*>](#Array\_init<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### Array\_init<Option>


func [Array\_init<Option>](#Array\_init<Option>)(a: [\*Array<Option>](#Array<Option>), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### Array\_init<char const\*>


func [Array\_init<char const\*>](#Array\_init<char\-const\*>)(a: [\*Array<char const\*>](#Array<char\-const\*>), initialSize: i32, alloc: [\*const Allocator](#Allocator))


### Array\_removeAt<Allocation\*>


func [Array\_removeAt<Allocation\*>](#Array\_removeAt<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>), index: i32) : [\*Allocation](#Allocation)


### Array\_size<Allocation\*>


func [Array\_size<Allocation\*>](#Array\_size<Allocation\*>)(a: [\*Array<Allocation\*>](#Array<Allocation\*>)) : i32


### Array\_size<Entry>


func [Array\_size<Entry>](#Array\_size<Entry>)(a: [\*Array<Entry>](#Array<Entry>)) : i32


### Array\_size<JsonNode\*>


func [Array\_size<JsonNode\*>](#Array\_size<JsonNode\*>)(a: [\*Array<JsonNode\*>](#Array<JsonNode\*>)) : i32


### Array\_size<Option>


func [Array\_size<Option>](#Array\_size<Option>)(a: [\*Array<Option>](#Array<Option>)) : i32


### MIN<i32>


func [MIN<i32>](#MIN<i32>)(a: i32, b: i32) : i32


### Map\_contains<char const\*,i32>


func [Map\_contains<char const\*,i32>](#Map\_contains<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), key: *const char) : bool


### Map\_free<char const\*,i32>


func [Map\_free<char const\*,i32>](#Map\_free<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>))


### Map\_get<char const\*,i32>


func [Map\_get<char const\*,i32>](#Map\_get<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), key: *const char) : i32


### Map\_init<char const\*,i32>


func [Map\_init<char const\*,i32>](#Map\_init<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), emptyValue: i32, initialSize: i32, hashFn: [func\(\*const char\) : u32](#\_), equalFn: [func\(\*const char, \*const char\) : bool](#\_), alloc: [\*const Allocator](#Allocator), emptyKey: *const char)


### Map\_put<char const\*,i32>


func [Map\_put<char const\*,i32>](#Map\_put<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>), key: *const char, value: i32)


### Map\_size<char const\*,i32>


func [Map\_size<char const\*,i32>](#Map\_size<char\-const\*,i32>)(m: [\*Map<char const\*,i32>](#Map<char\-const\*,i32>)) : i32


### c::MIN<usize>


func [c::MIN<usize>](#c::MIN<usize>)(a: usize, b: usize) : usize


### mem::new<JsonArray>


func [mem::new<JsonArray>](#mem::new<JsonArray>)(a: [\*const Allocator](#Allocator)) : [\*Array<JsonNode\*>](#Array<JsonNode\*>)


### mem::new<JsonNode>


func [mem::new<JsonNode>](#mem::new<JsonNode>)(a: [\*const Allocator](#Allocator)) : [\*JsonNode](#JsonNode)


### mem::new<JsonObject>


func [mem::new<JsonObject>](#mem::new<JsonObject>)(a: [\*const Allocator](#Allocator)) : [\*JsonObject](#JsonObject)


### new<Allocation>


func [new<Allocation>](#new<Allocation>)(a: [\*const Allocator](#Allocator)) : [\*Allocation](#Allocation)


### new<Arena>


func [new<Arena>](#new<Arena>)(a: [\*const Allocator](#Allocator)) : [\*Arena](#Arena)


