TODO
==

Issues:
* struct alignment
* multi var/const declarations: `var x = 1, y = 2`
* rvalue vs lvalue checks
* support \nnn escaped characters (CharToken, StrToken) 
* Function overloading (might not implement) 
* Namespace embedded defined struct/union/enum
* Configuring static/dynamic lib options (is this really necessary?)
* Configuring build type option (lib, exe); need @export?
* Bug with default array declarations:
```
    var x = [4]char;
    
    produces in C:
    char* x = {} 
    // which should probably produce:
    char* x = (char[4]){
        '\0', // using default values for the type
        '\0',
        '\0',
        '\0'
    };
```
* constant folding -- major bug with trying to do things like:
```
    const x = 10 * 10
    // or
    const y = SOME_CONSTANT * 2
    
    // and using that constant in an array definition doesn't work!!!
    struct V {
        a: [x]char // doesn't work!!
    }
```
* parse C header and add appropriate LitaC types (removes need for @foreign)
    * Simple #define constants
    * typedefs, unions, structs, enum, functions and extern types
    
    