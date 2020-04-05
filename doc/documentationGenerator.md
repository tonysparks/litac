# documentationGenerator

## documentationGenerator Imports

* [array](array\.md)
* [assert](assert\.md)
* [builtins](builtins\.md)
* [builtins](builtins\.md)
* [cmdline](cmdline\.md)
* [gl](gl\.md)
* [glfw](glfw\.md)
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
* struct [Array<JsonNode\*>](#Array<JsonNode\*>)
* struct [Array<T>](#Array<T>)
* struct [Map<K,V>](#Map<K,V>)
* struct [Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>)
* struct [Map<char const\*,Option>](#Map<char\-const\*,Option>)
* struct [Map<char const\*,V>](#Map<char\-const\*,V>)
* struct [MapEntry<K,V>](#MapEntry<K,V>)
* struct [MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)
* struct [MapIterator<K,V>](#MapIterator<K,V>)
* struct [MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)
* typedef [func\(K, K\) : bool](#\_) as [EqualFn<K>](#EqualFn<K>)
* typedef [func\(K\) : u32](#\_) as [HashFn<K>](#HashFn<K>)
* typedef [func\(char const\*, char const\*\) : bool](#\_) as [EqualFn<char const\*>](#EqualFn<char\-const\*>)
* typedef [func\(char const\*\) : u32](#\_) as [HashFn<char const\*>](#HashFn<char\-const\*>)


## documentationGenerator Functions

* func [Array\_add<Allocation\*>](#Array\_add<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), element: [Allocation\*](#Allocation))
* func [Array\_add<JsonNode\*>](#Array\_add<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), element: [JsonNode\*](#JsonNode))
* func [Array\_free<Allocation\*>](#Array\_free<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>))
* func [Array\_free<JsonNode\*>](#Array\_free<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>))
* func [Array\_get<Allocation\*>](#Array\_get<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)
* func [Array\_get<JsonNode\*>](#Array\_get<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), index: i32) : [JsonNode\*](#JsonNode)
* func [Array\_init<Allocation\*>](#Array\_init<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [Array\_init<JsonNode\*>](#Array\_init<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [Array\_removeAt<Allocation\*>](#Array\_removeAt<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)
* func [Array\_size<Allocation\*>](#Array\_size<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>)) : i32
* func [Array\_size<JsonNode\*>](#Array\_size<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>)) : i32
* func [MIN<i32>](#MIN<i32>)(a: i32, b: i32) : i32
* func [MapIterator\_hasNext<char const\*,JsonNode\*>](#MapIterator\_hasNext<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : bool
* func [MapIterator\_next<char const\*,JsonNode\*>](#MapIterator\_next<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)
* func [Map\_contains<char const\*,JsonNode\*>](#Map\_contains<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*) : bool
* func [Map\_contains<char const\*,Option>](#Map\_contains<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : bool
* func [Map\_free<char const\*,JsonNode\*>](#Map\_free<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>))
* func [Map\_free<char const\*,Option>](#Map\_free<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>))
* func [Map\_get<char const\*,JsonNode\*>](#Map\_get<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*) : [JsonNode\*](#JsonNode)
* func [Map\_get<char const\*,Option>](#Map\_get<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : [cmdline](cmdline\.md)::[Option](Option\.md)
* func [Map\_init<char const\*,JsonNode\*>](#Map\_init<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), emptyValue: [JsonNode\*](#JsonNode), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_init<char const\*,Option>](#Map\_init<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), emptyValue: [cmdline](cmdline\.md)::[Option](Option\.md), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_iter<char const\*,JsonNode\*>](#Map\_iter<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)
* func [Map\_put<char const\*,JsonNode\*>](#Map\_put<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*, value: [JsonNode\*](#JsonNode))
* func [Map\_put<char const\*,Option>](#Map\_put<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*, value: [cmdline](cmdline\.md)::[Option](Option\.md))
* func [Map\_size<char const\*,JsonNode\*>](#Map\_size<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : i32
* func [PtrEqualFn<char const\*>](#PtrEqualFn<char\-const\*>)(a: char const*, b: char const*) : bool
* func [StrMap<Option>](#StrMap<Option>)(emptyValue: [cmdline](cmdline\.md)::[Option](Option\.md), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,Option>](#Map<char\-const\*,Option>)
* func [c::MIN<u64>](#c::MIN<u64>)(a: u64, b: u64) : u64
* func [mem::new<JsonArray>](#mem::new<JsonArray>)(a: [Allocator const\*](#Allocator)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)
* func [mem::new<JsonNode>](#mem::new<JsonNode>)(a: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [mem::new<JsonObject>](#mem::new<JsonObject>)(a: [Allocator const\*](#Allocator)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* func [new<Allocation>](#new<Allocation>)(a: [Allocator const\*](#Allocator)) : [Allocation\*](#Allocation)
* func [new<Arena>](#new<Arena>)(a: [Allocator const\*](#Allocator)) : [Arena\*](#Arena)



***
### Array<Allocation\*>


struct [Array<Allocation\*>](#Array<Allocation\*>)

* length: i32
* capacity: i32
* elements: [Allocation\*\*](#Allocation)
* alloc: [Allocator const\*](#Allocator)



### Array<JsonNode\*>


struct [Array<JsonNode\*>](#Array<JsonNode\*>)

* length: i32
* capacity: i32
* elements: [JsonNode\*\*](#JsonNode)
* alloc: [Allocator const\*](#Allocator)



### Array<T>


struct [Array<T>](#Array<T>)

* length: i32
* capacity: i32
* elements: [T\*](#T)
* alloc: [Allocator const\*](#Allocator)



### EqualFn<K>


typedef [func\(K, K\) : bool](#\_) as [EqualFn<K>](#EqualFn<K>)


### EqualFn<char const\*>


typedef [func\(char const\*, char const\*\) : bool](#\_) as [EqualFn<char const\*>](#EqualFn<char\-const\*>)


### HashFn<K>


typedef [func\(K\) : u32](#\_) as [HashFn<K>](#HashFn<K>)


### HashFn<char const\*>


typedef [func\(char const\*\) : u32](#\_) as [HashFn<char const\*>](#HashFn<char\-const\*>)


### Map<K,V>


struct [Map<K,V>](#Map<K,V>)

* length: i32
* capacity: i32
* hashFn: [func\(K\) : u32](#\_)
* equalFn: [func\(K, K\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: [K\*](#K)
* values: [V\*](#V)
* emptyValue: [V](#V)



### Map<char const\*,JsonNode\*>


struct [Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>)

* length: i32
* capacity: i32
* hashFn: [func\(char const\*\) : u32](#\_)
* equalFn: [func\(char const\*, char const\*\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: char const**
* values: [JsonNode\*\*](#JsonNode)
* emptyValue: [JsonNode\*](#JsonNode)



### Map<char const\*,Option>


struct [Map<char const\*,Option>](#Map<char\-const\*,Option>)

* length: i32
* capacity: i32
* hashFn: [func\(char const\*\) : u32](#\_)
* equalFn: [func\(char const\*, char const\*\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: char const**
* values: [Option\*](#Option)
* emptyValue: [cmdline](cmdline\.md)::[Option](Option\.md)



### Map<char const\*,V>


struct [Map<char const\*,V>](#Map<char\-const\*,V>)

* length: i32
* capacity: i32
* hashFn: [func\(char const\*\) : u32](#\_)
* equalFn: [func\(char const\*, char const\*\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: char const**
* values: [V\*](#V)
* emptyValue: [V](#V)



### MapEntry<K,V>


struct [MapEntry<K,V>](#MapEntry<K,V>)

* key: [K](#K)
* value: [V](#V)



### MapEntry<char const\*,JsonNode\*>


struct [MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)

* key: char const*
* value: [JsonNode\*](#JsonNode)



### MapIterator<K,V>


struct [MapIterator<K,V>](#MapIterator<K,V>)

* m: [Map<K,V>\*](#Map<K,V>)
* it: i32
* count: i32



### MapIterator<char const\*,JsonNode\*>


struct [MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)

* m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* it: i32
* count: i32



### Array\_add<Allocation\*>


func [Array\_add<Allocation\*>](#Array\_add<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), element: [Allocation\*](#Allocation))


### Array\_add<JsonNode\*>


func [Array\_add<JsonNode\*>](#Array\_add<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), element: [JsonNode\*](#JsonNode))


### Array\_free<Allocation\*>


func [Array\_free<Allocation\*>](#Array\_free<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>))


### Array\_free<JsonNode\*>


func [Array\_free<JsonNode\*>](#Array\_free<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>))


### Array\_get<Allocation\*>


func [Array\_get<Allocation\*>](#Array\_get<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)


### Array\_get<JsonNode\*>


func [Array\_get<JsonNode\*>](#Array\_get<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), index: i32) : [JsonNode\*](#JsonNode)


### Array\_init<Allocation\*>


func [Array\_init<Allocation\*>](#Array\_init<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### Array\_init<JsonNode\*>


func [Array\_init<JsonNode\*>](#Array\_init<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### Array\_removeAt<Allocation\*>


func [Array\_removeAt<Allocation\*>](#Array\_removeAt<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)


### Array\_size<Allocation\*>


func [Array\_size<Allocation\*>](#Array\_size<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>)) : i32


### Array\_size<JsonNode\*>


func [Array\_size<JsonNode\*>](#Array\_size<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>)) : i32


### MIN<i32>


func [MIN<i32>](#MIN<i32>)(a: i32, b: i32) : i32


### MapIterator\_hasNext<char const\*,JsonNode\*>


func [MapIterator\_hasNext<char const\*,JsonNode\*>](#MapIterator\_hasNext<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : bool


### MapIterator\_next<char const\*,JsonNode\*>


func [MapIterator\_next<char const\*,JsonNode\*>](#MapIterator\_next<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)


### Map\_contains<char const\*,JsonNode\*>


func [Map\_contains<char const\*,JsonNode\*>](#Map\_contains<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*) : bool


### Map\_contains<char const\*,Option>


func [Map\_contains<char const\*,Option>](#Map\_contains<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : bool


### Map\_free<char const\*,JsonNode\*>


func [Map\_free<char const\*,JsonNode\*>](#Map\_free<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>))


### Map\_free<char const\*,Option>


func [Map\_free<char const\*,Option>](#Map\_free<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>))


### Map\_get<char const\*,JsonNode\*>


func [Map\_get<char const\*,JsonNode\*>](#Map\_get<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*) : [JsonNode\*](#JsonNode)


### Map\_get<char const\*,Option>


func [Map\_get<char const\*,Option>](#Map\_get<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : [cmdline](cmdline\.md)::[Option](Option\.md)


### Map\_init<char const\*,JsonNode\*>


func [Map\_init<char const\*,JsonNode\*>](#Map\_init<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), emptyValue: [JsonNode\*](#JsonNode), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_init<char const\*,Option>


func [Map\_init<char const\*,Option>](#Map\_init<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), emptyValue: [cmdline](cmdline\.md)::[Option](Option\.md), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_iter<char const\*,JsonNode\*>


func [Map\_iter<char const\*,JsonNode\*>](#Map\_iter<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)


### Map\_put<char const\*,JsonNode\*>


func [Map\_put<char const\*,JsonNode\*>](#Map\_put<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*, value: [JsonNode\*](#JsonNode))


### Map\_put<char const\*,Option>


func [Map\_put<char const\*,Option>](#Map\_put<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*, value: [cmdline](cmdline\.md)::[Option](Option\.md))


### Map\_size<char const\*,JsonNode\*>


func [Map\_size<char const\*,JsonNode\*>](#Map\_size<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : i32


### PtrEqualFn<char const\*>


func [PtrEqualFn<char const\*>](#PtrEqualFn<char\-const\*>)(a: char const*, b: char const*) : bool


### StrMap<Option>


func [StrMap<Option>](#StrMap<Option>)(emptyValue: [cmdline](cmdline\.md)::[Option](Option\.md), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,Option>](#Map<char\-const\*,Option>)


### c::MIN<u64>


func [c::MIN<u64>](#c::MIN<u64>)(a: u64, b: u64) : u64


### mem::new<JsonArray>


func [mem::new<JsonArray>](#mem::new<JsonArray>)(a: [Allocator const\*](#Allocator)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)


### mem::new<JsonNode>


func [mem::new<JsonNode>](#mem::new<JsonNode>)(a: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### mem::new<JsonObject>


func [mem::new<JsonObject>](#mem::new<JsonObject>)(a: [Allocator const\*](#Allocator)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)


### new<Allocation>


func [new<Allocation>](#new<Allocation>)(a: [Allocator const\*](#Allocator)) : [Allocation\*](#Allocation)


### new<Arena>


func [new<Arena>](#new<Arena>)(a: [Allocator const\*](#Allocator)) : [Arena\*](#Arena)


