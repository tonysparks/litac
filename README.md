# LitaC
LitaC (pronounced Lee-ta-see) is a C like language.  In fact, its main goal is to be C with some minor syntax sugar.  It compiles to ISO-C99 - so it's fairly 
easy to use C libraries.

The syntax:

```C
import "io"

func main(len:i32, args:char**):i32 {
	io:printf("Hello World")
}
```

# Types


```C
import "io"


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

func main(len:i32, args:char**):i32 {

	// numbers
	var b : bool = true // Boolean type, is an int8_t in C, 0 = false, 1 = true
	var i : i8 = 1_i8   // 8-bit integer, is an int8_t in C
	var j : i16 = 1_i16 // 16-bit integer, is an int16_t in C
	// ..i32, i64, i128
	var k : u16 = 1_u16 // 16-bit unsigned integer, is an uint16_t in C
	// ..u32, u64, u128
	var f : f32 = 0.5_f32 // 32-bit float
	var d : f64 = 0.5_f64 // 64-bit float
	var u = 0.5 // 32-bit float
	
	
	// types can be inferred:
	var age = 32   // age is a i32
	var n   = 32_i128   // is a i128
	
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
}
```



# Control Statements 


```C
import "io"

func main(len:i32, args:char**):i32 {
	if (true) {
		io::printf("Hi")
	}
	else {
		io::printf("Bye")
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
}
```



