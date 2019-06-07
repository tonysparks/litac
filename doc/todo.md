TODO
==

* @inline
* @volatile
* struct alignment
* @threadlocal
* @register
* multi var/const declarations: `var x = 1, y = 2`
* const/var's in function bodies read/apply annotations
* typechecker - function returns proper type (check all function return paths)
* infer arguments aggregateInitializer - infer argument type from structure/union
* rvalue vs lvalue checks
* TypeInfo exposure to stdlib (i.e., reflect:getTypeInfo(id: typeid) and operator: type_of(i32) 
* Make TypeId part of TypeInfo (enables reflection)
* builtins.lita prelude
* support `var x : const char*;`?
* support \nnn escaped characters (CharToken, StrToken) 
* Function overloading 
* Namespace enum values
* Namespace embedded defined struct/union/enum
* ~~~Typechecker -- missing Generics arguments for generic types~~~
* Configuring static/dynamic lib options
* Configuring build type option (lib, exe); need @export?
* switch statement
* `using` keyword, adds attributes to current scope:

```C
struct Vec {
    x: f32
    y: f32
}

struct Body {
    // x, y are now directly accessible from Body
    pos: using Vec
}

var b = Body{.x:0, .y:0}
b.x = 25
b.y = 35

// as well in functions
func VecAdd(a: using Vec, b: Vec): Vec {
    x += b.x
    y += b.y
    return a
}

```

* parse C header and add appropriate LitaC types (removes need for @foreign)
    * Simple #define constants
    * typedefs, unions, structs, enum, functions and extern types
    
    