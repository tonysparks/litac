# LitaC
LitaC (pronounced Lee-ta-see) is a C like language.  In fact, its main goal is to be C with some minor syntax sugar.  It compiles to ISO-C99 (C11 for some features) which has the benefits of being fairly easy to use existing C libraries and code.

# Goals & Purpose
First what's not a goal: LitaC is not meant to replace any existing languages.  It isn't trying to conquer the programming world.

With that disclaimer out of the way, what I'd like LitaC to do:
* A less cumbersome version of C; C is beautiful, but it is showing its age
* Make it easier for me to write video games in a native language
* Find enjoyment in programming

# Syntax
The syntax:

```C
import "io"

func main(len:i32, args:char**):i32 {
    printf("Hello World")
}
```

# Types


```C
// imports a module, namespace it with adding "as io", otherwise the public attributes
// will be placed in this modules scope
import "io" as io


// Defines a structure
struct Vec2 {
    x: f32
    y: f32
}

// Defines a function
func Vec2Add(a:Vec2, b:Vec2, out:Vec2) : Vec2 {
    out.x = a.x + b.x
    out.y = a.y + b.y
    return out
}

// structure with union
struct X {
    union V {
        x: i32
        f: f32
    }
}

func main(len:i32, args:char**):i32 {
    // the io:: namespaces the function
    io::printf("Just because you are paranoid, don't mean their not after you\n") 
    
    // numbers
    var b : bool = true // Boolean type, is an int8_t in C, 0 = false, 1 = true
    var i : i8 = 1_i8   // 8-bit integer, is an int8_t in C
    var j : i16 = 1_i16 // 16-bit integer, is an int16_t in C
    // ..i32, i64, i128
    var k : u16 = 1_u16 // 16-bit unsigned integer, is an uint16_t in C
    // ..u32, u64, u128
    var f : f32 = 0.5_f32 // 32-bit float
    var d : f64 = 0.5_f64 // 64-bit float
    var u = 0.5 // 64-bit float
    
    
    // types can be inferred:
    var age = 32   // age is a i32
    var n   = 32_i64   // is a i64
    
    // String and Arrays
    var s   = "Hello"  // a char* null terminated string. TODO, make strings include length
    var a   = [2]i32   // an array of i32 of size 2
    var a2  = []i32 { 1, 2, 3 }
    
    var index = a2[0] // '1'
    
    // initialize a structure on the stack, can optionally use initializer syntax
    var pos = Vec2 { .x: 0_f32, 
                     .y: 0_f32 }
                     
    var vel: Vec2 = Vec2{1.5, 2.5}
    
    // call a function
    Vec2Add(pos, vel, pos)
    
    // function pointer
    var f : func(Vec2, Vec2, Vec2) : Vec2 = &Vec2Add;
    // or simply:
    var myAdd = &Vec2Add;
    
    // initialize the structure with union
    var x = X { V { 34 } }
}
```



# Control Statements 


```C
import "io" // place io public types in this scope

func main(len:i32, args:char**):i32 {
    if (true) {
        printf("Hi")
    }
    else {
        printf("Bye")
    }
    
    var i = 0
    while (i < 10) {
        if i % 2 == 0 {
            break
        }
        else {
            i += 1
            continue
        }
    }
    
    i = 0
    do {
        if i % 2 == 0 {
            break
        }
        else {
            i += 1
            continue
        }
    }
    while (i < 10)
    
    for(var j = 0; j < 10; j+=1) {
       printf("%d\n", j)
    }
    
   var x = 2
   switch(x) {
      case 0: printf("it's 0\n")
         break;
      case 1: printf("it's 1\n")
         break;
      default:
         printf("I give up!\n")
         
   }
    
   printf("The Packers are ")
   goto end;  // jump over this lie
   prinf("not ")
    
end:
   printf("the best\n");
}
```


# Generics

```C

// Defines a generic structure
struct Vec2<T> {
    x: T
    y: T
}

// Defines a generic function
func Vec2Add<T>(a: Vec2<T>, b: Vec2<T>, out: Vec2<T>) : Vec2<T> {
    out.x = a.x + b.x
    out.y = a.y + b.y
    return out
}

// You alias this type
typedef Vec2<i32> as Vec2i


func main(len:i32, args:char**):i32 {
    // 
    var origin = Vec2<f32> { 0.0, 0.0 }  // using generics syntax
    var vel    = Vec2i { 0,0 }           // using the alias
    
    Vec2Add<i32>(vel, vel, vel)
    Vec2Add<f32>(origin, origin, origin)
}

```


# Methods
There is syntax to allow functions to masquerad around like methods.

```C
struct Vec2 {
    x: f32
    y: f32
}

func (v: Vec2) Add(other: Vec2) : Vec2 {
    return Vec2 { v.x + other.x, v.y + other.y }
}

func main(len:i32, args:char**) : i32 {
    var origin = Vec2{0,0}
    
    // method syntax sugar
    origin.Add(Vec2{3,4})
    
    
    // or you can call it as a normal function
    Add(origin, Vec2{3, 4})
    
}

```  
