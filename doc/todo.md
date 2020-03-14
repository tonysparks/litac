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
* parse C header and add appropriate LitaC types (removes need for @foreign)
    * Simple #define constants
    * typedefs, unions, structs, enum, functions and extern types
    
    