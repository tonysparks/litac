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

# Features
* no headers, no need for forward declarations
* generics for structs, unions and functions
* `using` which allows for flattening field access in structs or unions
* type inference 
* generic type inference
* struct/union "methods"
* `defer` statement  
* reflection (i.e., introspection of types, this is still a WIP)
* inline tests (enabled/run thru compiler command line argument) 
* multi-line strings

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
    // ..i32, i64, 
    var k : u16 = 1_u16 // 16-bit unsigned integer, is an uint16_t in C
    // ..u32, u64, 
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
        defer printf("there\n") // will execute at the end of this scope
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
    
    // generic types can also be infered:
    Vec2Add(vel, vel, vel) // infered as <i32>
}

```


# Methods
There is syntax to allow functions to masquerad around like methods.

```C
struct Vec2 {
    x: f32
    y: f32
}

// as a matter of convention, "methods" should be camelCase and freeform
// functions should be UpperCamelCase
func (v: Vec2) add(other: Vec2) : Vec2 {
    return Vec2 { v.x + other.x, v.y + other.y }
}

func main(len:i32, args:char**) : i32 {
    var origin = Vec2{0,0}
    
    // method syntax sugar
    origin.add(Vec2{3,4})
}

```

# Tests
Write tests right along side your application code.  When compiling for library/executable, the test code will not be included in the
final binary.

```C
func Add(a: i32, b: i32) : i32 {
    return a + b
}

// This annotation denotes a test.  
@test("test.Add My first test")
func testAdd() {
    assert(Add(4, 8) == 12)
}

@test("test.Add My second test")
func testAdd2() {
    assert(Add(4, -8) == -4)
}

```

If you include the following command line option to the litaC compiler, this will run all the tests.

```
litac -test ".*" ...
```

If you only want to run a subset of unit tests, you can define a Regular Expression to pick up the test names you want to run.  It is recommended to name your tests with a prefix standard (such as `appName.module.testName`, which would allow you to run full application tests via `appName.*` or module specific tests via `appName.module.*`).


# Building
In order to build litaC compiler you will need:
* Java 8 or above
* Maven 3 or above
* C compiler (I have only tested using Clang v6.0.1)

Once you have those dependencies installed, to build the litaC compiler, simply do:

```
git clone https://github.com/tonysparks/litac.git
cd litac
mvn clean install
```

# Using LitaC Compiler
Once you have built the `litac.jar` file (which will be located in the `/target` folder after `mvn clean install`), you can verify your jar file is valid by:

```
java -jar litac.jar -help
```

Which will print out the command line help contents.

```
usage> litac [options] [source file to compile]
OPTIONS:
  -lib <arg>           The LitaC library path
  -cPrefix <arg>       The symbol prefix to use on the generated C code output
  -run                 Runs the program after a successful compile
  -o, -output <arg>    The name of the compiled binary
  -outpuDir <arg>      The directory in which the C output files are stored
  -v, -version         Displays the LitaC version
  -h, -help            Displays this help
  -t, -types           Does not include TypeInfo for reflection
  -test <arg>          Runs functions annotated with @test.  'arg' is a regex of which tests should be run
  -buildCmd            The underlying C compiler build and compile command.  Variables will
                       be substituted if found:
                          %output%         The executable name
                          %input%          The C file(s) generated
```

Here is an example command line options:

```
set LITAC_PATH=C:\Users\antho\eclipse-workspace\litac\lib
java -jar litac.jar -run -lib "%LITAC_PATH%" -buildCmd "clang.exe -o %%output%% %%input%% -D_CRT_SECURE_NO_WARNINGS -I../include -L../lib -lraylib.lib" -outputDir "./bin" -output "mini" "./src/main.lita"
```

This example, builds an executable named `mini` from the `./src/main.lita` source file.  It will run the executable after it compiles (only if there are no errors).  It uses clang to compile the generated C code, I also set some clang compiler options such as an include directory and static libraries to use in my project.  This also specifies where the litac standard library exists (which is the `lib` folder in the litac project source.
