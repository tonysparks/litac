# map

## map Imports

* [assert](assert\.md)
* [io](io\.md)
* [mem](mem\.md)


## map Variables



## map Types

* struct [MapEntry](#MapEntry)
* struct [MapIterator](#MapIterator)
* struct [Map](#Map)
* typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)
* typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


## map Functions

* func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [\*MapIterator<K,V>](#MapIterator<K,V>)) : bool
* func [MapIterator\_next](#MapIterator\_next)(iter: [\*MapIterator<K,V>](#MapIterator<K,V>)) : [documentationGenerator](documentationGenerator\.md)::[MapEntry<K,V>](MapEntry<K,V>\.md)
* func [Map\_contains](#Map\_contains)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K)) : bool
* func [Map\_empty](#Map\_empty)(m: [\*Map<K,V>](#Map<K,V>)) : bool
* func [Map\_free](#Map\_free)(m: [\*Map<K,V>](#Map<K,V>))
* func [Map\_get](#Map\_get)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_init](#Map\_init)(m: [\*Map<K,V>](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [\*const Allocator](#Allocator))
* func [Map\_iter](#Map\_iter)(m: [\*Map<K,V>](#Map<K,V>)) : [documentationGenerator](documentationGenerator\.md)::[MapIterator<K,V>](MapIterator<K,V>\.md)
* func [Map\_put](#Map\_put)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K), value: [V](#V))
* func [Map\_remove](#Map\_remove)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_size](#Map\_size)(m: [\*Map<K,V>](#Map<K,V>)) : i32
* func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool
* func [StrEqualFn](#StrEqualFn)(a: *const char, b: *const char) : bool
* func [StrHashFn](#StrHashFn)(str: *const char) : u32
* func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [documentationGenerator](documentationGenerator\.md)::[Map<char const\*,V>](Map<char const\*,V>\.md)



***
### EqualFn


typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)


### HashFn


typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


### Map


struct [Map](#Map)

* length: i32
* capacity: i32
* hashFn: [func\(K\) : u32](#\_)
* equalFn: [func\(K, K\) : bool](#\_)
* alloc: [\*const Allocator](#Allocator)
* keys: [\*K](#K)
* values: [\*V](#V)
* emptyValue: [V](#V)



### MapEntry


struct [MapEntry](#MapEntry)

* key: [K](#K)
* value: [V](#V)



### MapIterator


struct [MapIterator](#MapIterator)

* m: [\*Map<K,V>](#Map<K,V>)
* it: i32
* count: i32



### MapIterator\_hasNext


func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [\*MapIterator<K,V>](#MapIterator<K,V>)) : bool


### MapIterator\_next


func [MapIterator\_next](#MapIterator\_next)(iter: [\*MapIterator<K,V>](#MapIterator<K,V>)) : [documentationGenerator](documentationGenerator\.md)::[MapEntry<K,V>](MapEntry<K,V>\.md)


### Map\_contains


func [Map\_contains](#Map\_contains)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K)) : bool


### Map\_empty


func [Map\_empty](#Map\_empty)(m: [\*Map<K,V>](#Map<K,V>)) : bool


### Map\_free


func [Map\_free](#Map\_free)(m: [\*Map<K,V>](#Map<K,V>))


### Map\_get


func [Map\_get](#Map\_get)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_init


func [Map\_init](#Map\_init)(m: [\*Map<K,V>](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [\*const Allocator](#Allocator))


### Map\_iter


func [Map\_iter](#Map\_iter)(m: [\*Map<K,V>](#Map<K,V>)) : [documentationGenerator](documentationGenerator\.md)::[MapIterator<K,V>](MapIterator<K,V>\.md)


### Map\_put


func [Map\_put](#Map\_put)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K), value: [V](#V))


### Map\_remove


func [Map\_remove](#Map\_remove)(m: [\*Map<K,V>](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_size


func [Map\_size](#Map\_size)(m: [\*Map<K,V>](#Map<K,V>)) : i32


### PtrEqualFn


func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool


### StrEqualFn


func [StrEqualFn](#StrEqualFn)(a: *const char, b: *const char) : bool


### StrHashFn


func [StrHashFn](#StrHashFn)(str: *const char) : u32


### StrMap


func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [\*const Allocator](#Allocator)) : [documentationGenerator](documentationGenerator\.md)::[Map<char const\*,V>](Map<char const\*,V>\.md)


