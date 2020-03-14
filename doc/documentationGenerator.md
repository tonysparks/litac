# documentationGenerator

## documentationGenerator Imports

* [array](array)
* [assert](assert)
* [builtins](builtins)
* [builtins](builtins)
* [cmdline](cmdline)
* [gl](gl)
* [glfw](glfw)
* [io](io)
* [json](json)
* [libc](libc)
* [map](map)
* [mem](mem)
* [string\_buffer](string\_buffer)
* [string\_view](string\_view)
* [string](string)
* [thread\_posix](thread\_posix)
* [type](type)


## documentationGenerator Variables

* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_CUR](#SEEK\_CUR): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_END](#SEEK\_END): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [SEEK\_SET](#SEEK\_SET): i32
* [defaultAllocator](#defaultAllocator): [Allocator const\*](#Allocator)
* const [GLAPI](#GLAPI): i32
* const [GLFW\_ACCUM\_ALPHA\_BITS](#GLFW\_ACCUM\_ALPHA\_BITS): i32
* const [GLFW\_ACCUM\_BLUE\_BITS](#GLFW\_ACCUM\_BLUE\_BITS): i32
* const [GLFW\_ACCUM\_GREEN\_BITS](#GLFW\_ACCUM\_GREEN\_BITS): i32
* const [GLFW\_ACCUM\_RED\_BITS](#GLFW\_ACCUM\_RED\_BITS): i32
* const [GLFW\_ALPHA\_BITS](#GLFW\_ALPHA\_BITS): i32
* const [GLFW\_ANY\_RELEASE\_BEHAVIOR](#GLFW\_ANY\_RELEASE\_BEHAVIOR): i32
* const [GLFW\_API\_UNAVAILABLE](#GLFW\_API\_UNAVAILABLE): i32
* const [GLFW\_ARROW\_CURSOR](#GLFW\_ARROW\_CURSOR): i32
* const [GLFW\_AUTO\_ICONIFY](#GLFW\_AUTO\_ICONIFY): i32
* const [GLFW\_AUX\_BUFFERS](#GLFW\_AUX\_BUFFERS): i32
* const [GLFW\_BLUE\_BITS](#GLFW\_BLUE\_BITS): i32
* const [GLFW\_CENTER\_CURSOR](#GLFW\_CENTER\_CURSOR): i32
* const [GLFW\_CLIENT\_API](#GLFW\_CLIENT\_API): i32
* const [GLFW\_COCOA\_CHDIR\_RESOURCES](#GLFW\_COCOA\_CHDIR\_RESOURCES): i32
* const [GLFW\_COCOA\_FRAME\_NAME](#GLFW\_COCOA\_FRAME\_NAME): i32
* const [GLFW\_COCOA\_GRAPHICS\_SWITCHING](#GLFW\_COCOA\_GRAPHICS\_SWITCHING): i32
* const [GLFW\_COCOA\_MENUBAR](#GLFW\_COCOA\_MENUBAR): i32
* const [GLFW\_COCOA\_RETINA\_FRAMEBUFFER](#GLFW\_COCOA\_RETINA\_FRAMEBUFFER): i32
* const [GLFW\_CONNECTED](#GLFW\_CONNECTED): i32
* const [GLFW\_CONTEXT\_CREATION\_API](#GLFW\_CONTEXT\_CREATION\_API): i32
* const [GLFW\_CONTEXT\_NO\_ERROR](#GLFW\_CONTEXT\_NO\_ERROR): i32
* const [GLFW\_CONTEXT\_RELEASE\_BEHAVIOR](#GLFW\_CONTEXT\_RELEASE\_BEHAVIOR): i32
* const [GLFW\_CONTEXT\_REVISION](#GLFW\_CONTEXT\_REVISION): i32
* const [GLFW\_CONTEXT\_ROBUSTNESS](#GLFW\_CONTEXT\_ROBUSTNESS): i32
* const [GLFW\_CONTEXT\_VERSION\_MAJOR](#GLFW\_CONTEXT\_VERSION\_MAJOR): i32
* const [GLFW\_CONTEXT\_VERSION\_MINOR](#GLFW\_CONTEXT\_VERSION\_MINOR): i32
* const [GLFW\_CROSSHAIR\_CURSOR](#GLFW\_CROSSHAIR\_CURSOR): i32
* const [GLFW\_CURSOR\_DISABLED](#GLFW\_CURSOR\_DISABLED): i32
* const [GLFW\_CURSOR\_HIDDEN](#GLFW\_CURSOR\_HIDDEN): i32
* const [GLFW\_CURSOR\_NORMAL](#GLFW\_CURSOR\_NORMAL): i32
* const [GLFW\_CURSOR](#GLFW\_CURSOR): i32
* const [GLFW\_DECORATED](#GLFW\_DECORATED): i32
* const [GLFW\_DEPTH\_BITS](#GLFW\_DEPTH\_BITS): i32
* const [GLFW\_DISCONNECTED](#GLFW\_DISCONNECTED): i32
* const [GLFW\_DONT\_CARE](#GLFW\_DONT\_CARE): i32
* const [GLFW\_DOUBLEBUFFER](#GLFW\_DOUBLEBUFFER): i32
* const [GLFW\_EGL\_CONTEXT\_API](#GLFW\_EGL\_CONTEXT\_API): i32
* const [GLFW\_FALSE](#GLFW\_FALSE): i32
* const [GLFW\_FLOATING](#GLFW\_FLOATING): i32
* const [GLFW\_FOCUSED](#GLFW\_FOCUSED): i32
* const [GLFW\_FOCUS\_ON\_SHOW](#GLFW\_FOCUS\_ON\_SHOW): i32
* const [GLFW\_FORMAT\_UNAVAILABLE](#GLFW\_FORMAT\_UNAVAILABLE): i32
* const [GLFW\_GAMEPAD\_AXIS\_LAST](#GLFW\_GAMEPAD\_AXIS\_LAST): i32
* const [GLFW\_GAMEPAD\_AXIS\_LEFT\_TRIGGER](#GLFW\_GAMEPAD\_AXIS\_LEFT\_TRIGGER): i32
* const [GLFW\_GAMEPAD\_AXIS\_LEFT\_X](#GLFW\_GAMEPAD\_AXIS\_LEFT\_X): i32
* const [GLFW\_GAMEPAD\_AXIS\_LEFT\_Y](#GLFW\_GAMEPAD\_AXIS\_LEFT\_Y): i32
* const [GLFW\_GAMEPAD\_AXIS\_RIGHT\_TRIGGER](#GLFW\_GAMEPAD\_AXIS\_RIGHT\_TRIGGER): i32
* const [GLFW\_GAMEPAD\_AXIS\_RIGHT\_X](#GLFW\_GAMEPAD\_AXIS\_RIGHT\_X): i32
* const [GLFW\_GAMEPAD\_AXIS\_RIGHT\_Y](#GLFW\_GAMEPAD\_AXIS\_RIGHT\_Y): i32
* const [GLFW\_GAMEPAD\_BUTTON\_A](#GLFW\_GAMEPAD\_BUTTON\_A): i32
* const [GLFW\_GAMEPAD\_BUTTON\_BACK](#GLFW\_GAMEPAD\_BUTTON\_BACK): i32
* const [GLFW\_GAMEPAD\_BUTTON\_B](#GLFW\_GAMEPAD\_BUTTON\_B): i32
* const [GLFW\_GAMEPAD\_BUTTON\_CIRCLE](#GLFW\_GAMEPAD\_BUTTON\_CIRCLE): i32
* const [GLFW\_GAMEPAD\_BUTTON\_CROSS](#GLFW\_GAMEPAD\_BUTTON\_CROSS): i32
* const [GLFW\_GAMEPAD\_BUTTON\_DPAD\_DOWN](#GLFW\_GAMEPAD\_BUTTON\_DPAD\_DOWN): i32
* const [GLFW\_GAMEPAD\_BUTTON\_DPAD\_LEFT](#GLFW\_GAMEPAD\_BUTTON\_DPAD\_LEFT): i32
* const [GLFW\_GAMEPAD\_BUTTON\_DPAD\_RIGHT](#GLFW\_GAMEPAD\_BUTTON\_DPAD\_RIGHT): i32
* const [GLFW\_GAMEPAD\_BUTTON\_DPAD\_UP](#GLFW\_GAMEPAD\_BUTTON\_DPAD\_UP): i32
* const [GLFW\_GAMEPAD\_BUTTON\_GUIDE](#GLFW\_GAMEPAD\_BUTTON\_GUIDE): i32
* const [GLFW\_GAMEPAD\_BUTTON\_LAST](#GLFW\_GAMEPAD\_BUTTON\_LAST): i32
* const [GLFW\_GAMEPAD\_BUTTON\_LEFT\_BUMPER](#GLFW\_GAMEPAD\_BUTTON\_LEFT\_BUMPER): i32
* const [GLFW\_GAMEPAD\_BUTTON\_LEFT\_THUMB](#GLFW\_GAMEPAD\_BUTTON\_LEFT\_THUMB): i32
* const [GLFW\_GAMEPAD\_BUTTON\_RIGHT\_BUMPER](#GLFW\_GAMEPAD\_BUTTON\_RIGHT\_BUMPER): i32
* const [GLFW\_GAMEPAD\_BUTTON\_RIGHT\_THUMB](#GLFW\_GAMEPAD\_BUTTON\_RIGHT\_THUMB): i32
* const [GLFW\_GAMEPAD\_BUTTON\_SQUARE](#GLFW\_GAMEPAD\_BUTTON\_SQUARE): i32
* const [GLFW\_GAMEPAD\_BUTTON\_START](#GLFW\_GAMEPAD\_BUTTON\_START): i32
* const [GLFW\_GAMEPAD\_BUTTON\_TRIANGLE](#GLFW\_GAMEPAD\_BUTTON\_TRIANGLE): i32
* const [GLFW\_GAMEPAD\_BUTTON\_X](#GLFW\_GAMEPAD\_BUTTON\_X): i32
* const [GLFW\_GAMEPAD\_BUTTON\_Y](#GLFW\_GAMEPAD\_BUTTON\_Y): i32
* const [GLFW\_GREEN\_BITS](#GLFW\_GREEN\_BITS): i32
* const [GLFW\_HAND\_CURSOR](#GLFW\_HAND\_CURSOR): i32
* const [GLFW\_HAT\_CENTERED](#GLFW\_HAT\_CENTERED): i32
* const [GLFW\_HAT\_DOWN](#GLFW\_HAT\_DOWN): i32
* const [GLFW\_HAT\_LEFT\_DOWN](#GLFW\_HAT\_LEFT\_DOWN): i32
* const [GLFW\_HAT\_LEFT\_UP](#GLFW\_HAT\_LEFT\_UP): i32
* const [GLFW\_HAT\_LEFT](#GLFW\_HAT\_LEFT): i32
* const [GLFW\_HAT\_RIGHT\_DOWN](#GLFW\_HAT\_RIGHT\_DOWN): i32
* const [GLFW\_HAT\_RIGHT\_UP](#GLFW\_HAT\_RIGHT\_UP): i32
* const [GLFW\_HAT\_RIGHT](#GLFW\_HAT\_RIGHT): i32
* const [GLFW\_HAT\_UP](#GLFW\_HAT\_UP): i32
* const [GLFW\_HOVERED](#GLFW\_HOVERED): i32
* const [GLFW\_HRESIZE\_CURSOR](#GLFW\_HRESIZE\_CURSOR): i32
* const [GLFW\_IBEAM\_CURSOR](#GLFW\_IBEAM\_CURSOR): i32
* const [GLFW\_ICONIFIED](#GLFW\_ICONIFIED): i32
* const [GLFW\_INVALID\_ENUM](#GLFW\_INVALID\_ENUM): i32
* const [GLFW\_INVALID\_VALUE](#GLFW\_INVALID\_VALUE): i32
* const [GLFW\_JOYSTICK\_10](#GLFW\_JOYSTICK\_10): i32
* const [GLFW\_JOYSTICK\_11](#GLFW\_JOYSTICK\_11): i32
* const [GLFW\_JOYSTICK\_12](#GLFW\_JOYSTICK\_12): i32
* const [GLFW\_JOYSTICK\_13](#GLFW\_JOYSTICK\_13): i32
* const [GLFW\_JOYSTICK\_14](#GLFW\_JOYSTICK\_14): i32
* const [GLFW\_JOYSTICK\_15](#GLFW\_JOYSTICK\_15): i32
* const [GLFW\_JOYSTICK\_16](#GLFW\_JOYSTICK\_16): i32
* const [GLFW\_JOYSTICK\_1](#GLFW\_JOYSTICK\_1): i32
* const [GLFW\_JOYSTICK\_2](#GLFW\_JOYSTICK\_2): i32
* const [GLFW\_JOYSTICK\_3](#GLFW\_JOYSTICK\_3): i32
* const [GLFW\_JOYSTICK\_4](#GLFW\_JOYSTICK\_4): i32
* const [GLFW\_JOYSTICK\_5](#GLFW\_JOYSTICK\_5): i32
* const [GLFW\_JOYSTICK\_6](#GLFW\_JOYSTICK\_6): i32
* const [GLFW\_JOYSTICK\_7](#GLFW\_JOYSTICK\_7): i32
* const [GLFW\_JOYSTICK\_8](#GLFW\_JOYSTICK\_8): i32
* const [GLFW\_JOYSTICK\_9](#GLFW\_JOYSTICK\_9): i32
* const [GLFW\_JOYSTICK\_HAT\_BUTTONS](#GLFW\_JOYSTICK\_HAT\_BUTTONS): i32
* const [GLFW\_JOYSTICK\_LAST](#GLFW\_JOYSTICK\_LAST): i32
* const [GLFW\_KEY\_0](#GLFW\_KEY\_0): i32
* const [GLFW\_KEY\_1](#GLFW\_KEY\_1): i32
* const [GLFW\_KEY\_2](#GLFW\_KEY\_2): i32
* const [GLFW\_KEY\_3](#GLFW\_KEY\_3): i32
* const [GLFW\_KEY\_4](#GLFW\_KEY\_4): i32
* const [GLFW\_KEY\_5](#GLFW\_KEY\_5): i32
* const [GLFW\_KEY\_6](#GLFW\_KEY\_6): i32
* const [GLFW\_KEY\_7](#GLFW\_KEY\_7): i32
* const [GLFW\_KEY\_8](#GLFW\_KEY\_8): i32
* const [GLFW\_KEY\_9](#GLFW\_KEY\_9): i32
* const [GLFW\_KEY\_APOSTROPHE](#GLFW\_KEY\_APOSTROPHE): i32
* const [GLFW\_KEY\_A](#GLFW\_KEY\_A): i32
* const [GLFW\_KEY\_BACKSLASH](#GLFW\_KEY\_BACKSLASH): i32
* const [GLFW\_KEY\_BACKSPACE](#GLFW\_KEY\_BACKSPACE): i32
* const [GLFW\_KEY\_B](#GLFW\_KEY\_B): i32
* const [GLFW\_KEY\_CAPS\_LOCK](#GLFW\_KEY\_CAPS\_LOCK): i32
* const [GLFW\_KEY\_COMMA](#GLFW\_KEY\_COMMA): i32
* const [GLFW\_KEY\_C](#GLFW\_KEY\_C): i32
* const [GLFW\_KEY\_DELETE](#GLFW\_KEY\_DELETE): i32
* const [GLFW\_KEY\_DOWN](#GLFW\_KEY\_DOWN): i32
* const [GLFW\_KEY\_D](#GLFW\_KEY\_D): i32
* const [GLFW\_KEY\_END](#GLFW\_KEY\_END): i32
* const [GLFW\_KEY\_ENTER](#GLFW\_KEY\_ENTER): i32
* const [GLFW\_KEY\_EQUAL](#GLFW\_KEY\_EQUAL): i32
* const [GLFW\_KEY\_ESCAPE](#GLFW\_KEY\_ESCAPE): i32
* const [GLFW\_KEY\_E](#GLFW\_KEY\_E): i32
* const [GLFW\_KEY\_F10](#GLFW\_KEY\_F10): i32
* const [GLFW\_KEY\_F11](#GLFW\_KEY\_F11): i32
* const [GLFW\_KEY\_F12](#GLFW\_KEY\_F12): i32
* const [GLFW\_KEY\_F13](#GLFW\_KEY\_F13): i32
* const [GLFW\_KEY\_F14](#GLFW\_KEY\_F14): i32
* const [GLFW\_KEY\_F15](#GLFW\_KEY\_F15): i32
* const [GLFW\_KEY\_F16](#GLFW\_KEY\_F16): i32
* const [GLFW\_KEY\_F17](#GLFW\_KEY\_F17): i32
* const [GLFW\_KEY\_F18](#GLFW\_KEY\_F18): i32
* const [GLFW\_KEY\_F19](#GLFW\_KEY\_F19): i32
* const [GLFW\_KEY\_F1](#GLFW\_KEY\_F1): i32
* const [GLFW\_KEY\_F20](#GLFW\_KEY\_F20): i32
* const [GLFW\_KEY\_F21](#GLFW\_KEY\_F21): i32
* const [GLFW\_KEY\_F22](#GLFW\_KEY\_F22): i32
* const [GLFW\_KEY\_F23](#GLFW\_KEY\_F23): i32
* const [GLFW\_KEY\_F24](#GLFW\_KEY\_F24): i32
* const [GLFW\_KEY\_F25](#GLFW\_KEY\_F25): i32
* const [GLFW\_KEY\_F2](#GLFW\_KEY\_F2): i32
* const [GLFW\_KEY\_F3](#GLFW\_KEY\_F3): i32
* const [GLFW\_KEY\_F4](#GLFW\_KEY\_F4): i32
* const [GLFW\_KEY\_F5](#GLFW\_KEY\_F5): i32
* const [GLFW\_KEY\_F6](#GLFW\_KEY\_F6): i32
* const [GLFW\_KEY\_F7](#GLFW\_KEY\_F7): i32
* const [GLFW\_KEY\_F8](#GLFW\_KEY\_F8): i32
* const [GLFW\_KEY\_F9](#GLFW\_KEY\_F9): i32
* const [GLFW\_KEY\_F](#GLFW\_KEY\_F): i32
* const [GLFW\_KEY\_GRAVE\_ACCENT](#GLFW\_KEY\_GRAVE\_ACCENT): i32
* const [GLFW\_KEY\_G](#GLFW\_KEY\_G): i32
* const [GLFW\_KEY\_HOME](#GLFW\_KEY\_HOME): i32
* const [GLFW\_KEY\_H](#GLFW\_KEY\_H): i32
* const [GLFW\_KEY\_INSERT](#GLFW\_KEY\_INSERT): i32
* const [GLFW\_KEY\_I](#GLFW\_KEY\_I): i32
* const [GLFW\_KEY\_J](#GLFW\_KEY\_J): i32
* const [GLFW\_KEY\_KP\_0](#GLFW\_KEY\_KP\_0): i32
* const [GLFW\_KEY\_KP\_1](#GLFW\_KEY\_KP\_1): i32
* const [GLFW\_KEY\_KP\_2](#GLFW\_KEY\_KP\_2): i32
* const [GLFW\_KEY\_KP\_3](#GLFW\_KEY\_KP\_3): i32
* const [GLFW\_KEY\_KP\_4](#GLFW\_KEY\_KP\_4): i32
* const [GLFW\_KEY\_KP\_5](#GLFW\_KEY\_KP\_5): i32
* const [GLFW\_KEY\_KP\_6](#GLFW\_KEY\_KP\_6): i32
* const [GLFW\_KEY\_KP\_7](#GLFW\_KEY\_KP\_7): i32
* const [GLFW\_KEY\_KP\_8](#GLFW\_KEY\_KP\_8): i32
* const [GLFW\_KEY\_KP\_9](#GLFW\_KEY\_KP\_9): i32
* const [GLFW\_KEY\_KP\_ADD](#GLFW\_KEY\_KP\_ADD): i32
* const [GLFW\_KEY\_KP\_DECIMAL](#GLFW\_KEY\_KP\_DECIMAL): i32
* const [GLFW\_KEY\_KP\_DIVIDE](#GLFW\_KEY\_KP\_DIVIDE): i32
* const [GLFW\_KEY\_KP\_ENTER](#GLFW\_KEY\_KP\_ENTER): i32
* const [GLFW\_KEY\_KP\_EQUAL](#GLFW\_KEY\_KP\_EQUAL): i32
* const [GLFW\_KEY\_KP\_MULTIPLY](#GLFW\_KEY\_KP\_MULTIPLY): i32
* const [GLFW\_KEY\_KP\_SUBTRACT](#GLFW\_KEY\_KP\_SUBTRACT): i32
* const [GLFW\_KEY\_K](#GLFW\_KEY\_K): i32
* const [GLFW\_KEY\_LAST](#GLFW\_KEY\_LAST): i32
* const [GLFW\_KEY\_LEFT\_ALT](#GLFW\_KEY\_LEFT\_ALT): i32
* const [GLFW\_KEY\_LEFT\_BRACKET](#GLFW\_KEY\_LEFT\_BRACKET): i32
* const [GLFW\_KEY\_LEFT\_CONTROL](#GLFW\_KEY\_LEFT\_CONTROL): i32
* const [GLFW\_KEY\_LEFT\_SHIFT](#GLFW\_KEY\_LEFT\_SHIFT): i32
* const [GLFW\_KEY\_LEFT\_SUPER](#GLFW\_KEY\_LEFT\_SUPER): i32
* const [GLFW\_KEY\_LEFT](#GLFW\_KEY\_LEFT): i32
* const [GLFW\_KEY\_L](#GLFW\_KEY\_L): i32
* const [GLFW\_KEY\_MENU](#GLFW\_KEY\_MENU): i32
* const [GLFW\_KEY\_MINUS](#GLFW\_KEY\_MINUS): i32
* const [GLFW\_KEY\_M](#GLFW\_KEY\_M): i32
* const [GLFW\_KEY\_NUM\_LOCK](#GLFW\_KEY\_NUM\_LOCK): i32
* const [GLFW\_KEY\_N](#GLFW\_KEY\_N): i32
* const [GLFW\_KEY\_O](#GLFW\_KEY\_O): i32
* const [GLFW\_KEY\_PAGE\_DOWN](#GLFW\_KEY\_PAGE\_DOWN): i32
* const [GLFW\_KEY\_PAGE\_UP](#GLFW\_KEY\_PAGE\_UP): i32
* const [GLFW\_KEY\_PAUSE](#GLFW\_KEY\_PAUSE): i32
* const [GLFW\_KEY\_PERIOD](#GLFW\_KEY\_PERIOD): i32
* const [GLFW\_KEY\_PRINT\_SCREEN](#GLFW\_KEY\_PRINT\_SCREEN): i32
* const [GLFW\_KEY\_P](#GLFW\_KEY\_P): i32
* const [GLFW\_KEY\_Q](#GLFW\_KEY\_Q): i32
* const [GLFW\_KEY\_RIGHT\_ALT](#GLFW\_KEY\_RIGHT\_ALT): i32
* const [GLFW\_KEY\_RIGHT\_BRACKET](#GLFW\_KEY\_RIGHT\_BRACKET): i32
* const [GLFW\_KEY\_RIGHT\_CONTROL](#GLFW\_KEY\_RIGHT\_CONTROL): i32
* const [GLFW\_KEY\_RIGHT\_SHIFT](#GLFW\_KEY\_RIGHT\_SHIFT): i32
* const [GLFW\_KEY\_RIGHT\_SUPER](#GLFW\_KEY\_RIGHT\_SUPER): i32
* const [GLFW\_KEY\_RIGHT](#GLFW\_KEY\_RIGHT): i32
* const [GLFW\_KEY\_R](#GLFW\_KEY\_R): i32
* const [GLFW\_KEY\_SCROLL\_LOCK](#GLFW\_KEY\_SCROLL\_LOCK): i32
* const [GLFW\_KEY\_SEMICOLON](#GLFW\_KEY\_SEMICOLON): i32
* const [GLFW\_KEY\_SLASH](#GLFW\_KEY\_SLASH): i32
* const [GLFW\_KEY\_SPACE](#GLFW\_KEY\_SPACE): i32
* const [GLFW\_KEY\_S](#GLFW\_KEY\_S): i32
* const [GLFW\_KEY\_TAB](#GLFW\_KEY\_TAB): i32
* const [GLFW\_KEY\_T](#GLFW\_KEY\_T): i32
* const [GLFW\_KEY\_UNKNOWN](#GLFW\_KEY\_UNKNOWN): i32
* const [GLFW\_KEY\_UP](#GLFW\_KEY\_UP): i32
* const [GLFW\_KEY\_U](#GLFW\_KEY\_U): i32
* const [GLFW\_KEY\_V](#GLFW\_KEY\_V): i32
* const [GLFW\_KEY\_WORLD\_1](#GLFW\_KEY\_WORLD\_1): i32
* const [GLFW\_KEY\_WORLD\_2](#GLFW\_KEY\_WORLD\_2): i32
* const [GLFW\_KEY\_W](#GLFW\_KEY\_W): i32
* const [GLFW\_KEY\_X](#GLFW\_KEY\_X): i32
* const [GLFW\_KEY\_Y](#GLFW\_KEY\_Y): i32
* const [GLFW\_KEY\_Z](#GLFW\_KEY\_Z): i32
* const [GLFW\_LOCK\_KEY\_MODS](#GLFW\_LOCK\_KEY\_MODS): i32
* const [GLFW\_LOSE\_CONTEXT\_ON\_RESET](#GLFW\_LOSE\_CONTEXT\_ON\_RESET): i32
* const [GLFW\_MAXIMIZED](#GLFW\_MAXIMIZED): i32
* const [GLFW\_MOD\_ALT](#GLFW\_MOD\_ALT): i32
* const [GLFW\_MOD\_CAPS\_LOCK](#GLFW\_MOD\_CAPS\_LOCK): i32
* const [GLFW\_MOD\_CONTROL](#GLFW\_MOD\_CONTROL): i32
* const [GLFW\_MOD\_NUM\_LOCK](#GLFW\_MOD\_NUM\_LOCK): i32
* const [GLFW\_MOD\_SHIFT](#GLFW\_MOD\_SHIFT): i32
* const [GLFW\_MOD\_SUPER](#GLFW\_MOD\_SUPER): i32
* const [GLFW\_MOUSE\_BUTTON\_1](#GLFW\_MOUSE\_BUTTON\_1): i32
* const [GLFW\_MOUSE\_BUTTON\_2](#GLFW\_MOUSE\_BUTTON\_2): i32
* const [GLFW\_MOUSE\_BUTTON\_3](#GLFW\_MOUSE\_BUTTON\_3): i32
* const [GLFW\_MOUSE\_BUTTON\_4](#GLFW\_MOUSE\_BUTTON\_4): i32
* const [GLFW\_MOUSE\_BUTTON\_5](#GLFW\_MOUSE\_BUTTON\_5): i32
* const [GLFW\_MOUSE\_BUTTON\_6](#GLFW\_MOUSE\_BUTTON\_6): i32
* const [GLFW\_MOUSE\_BUTTON\_7](#GLFW\_MOUSE\_BUTTON\_7): i32
* const [GLFW\_MOUSE\_BUTTON\_8](#GLFW\_MOUSE\_BUTTON\_8): i32
* const [GLFW\_MOUSE\_BUTTON\_LAST](#GLFW\_MOUSE\_BUTTON\_LAST): i32
* const [GLFW\_MOUSE\_BUTTON\_LEFT](#GLFW\_MOUSE\_BUTTON\_LEFT): i32
* const [GLFW\_MOUSE\_BUTTON\_MIDDLE](#GLFW\_MOUSE\_BUTTON\_MIDDLE): i32
* const [GLFW\_MOUSE\_BUTTON\_RIGHT](#GLFW\_MOUSE\_BUTTON\_RIGHT): i32
* const [GLFW\_NATIVE\_CONTEXT\_API](#GLFW\_NATIVE\_CONTEXT\_API): i32
* const [GLFW\_NOT\_INITIALIZED](#GLFW\_NOT\_INITIALIZED): i32
* const [GLFW\_NO\_API](#GLFW\_NO\_API): i32
* const [GLFW\_NO\_CURRENT\_CONTEXT](#GLFW\_NO\_CURRENT\_CONTEXT): i32
* const [GLFW\_NO\_ERROR](#GLFW\_NO\_ERROR): i32
* const [GLFW\_NO\_RESET\_NOTIFICATION](#GLFW\_NO\_RESET\_NOTIFICATION): i32
* const [GLFW\_NO\_ROBUSTNESS](#GLFW\_NO\_ROBUSTNESS): i32
* const [GLFW\_NO\_WINDOW\_CONTEXT](#GLFW\_NO\_WINDOW\_CONTEXT): i32
* const [GLFW\_OPENGL\_ANY\_PROFILE](#GLFW\_OPENGL\_ANY\_PROFILE): i32
* const [GLFW\_OPENGL\_API](#GLFW\_OPENGL\_API): i32
* const [GLFW\_OPENGL\_COMPAT\_PROFILE](#GLFW\_OPENGL\_COMPAT\_PROFILE): i32
* const [GLFW\_OPENGL\_CORE\_PROFILE](#GLFW\_OPENGL\_CORE\_PROFILE): i32
* const [GLFW\_OPENGL\_DEBUG\_CONTEXT](#GLFW\_OPENGL\_DEBUG\_CONTEXT): i32
* const [GLFW\_OPENGL\_ES\_API](#GLFW\_OPENGL\_ES\_API): i32
* const [GLFW\_OPENGL\_FORWARD\_COMPAT](#GLFW\_OPENGL\_FORWARD\_COMPAT): i32
* const [GLFW\_OPENGL\_PROFILE](#GLFW\_OPENGL\_PROFILE): i32
* const [GLFW\_OSMESA\_CONTEXT\_API](#GLFW\_OSMESA\_CONTEXT\_API): i32
* const [GLFW\_OUT\_OF\_MEMORY](#GLFW\_OUT\_OF\_MEMORY): i32
* const [GLFW\_PLATFORM\_ERROR](#GLFW\_PLATFORM\_ERROR): i32
* const [GLFW\_PRESS](#GLFW\_PRESS): i32
* const [GLFW\_RAW\_MOUSE\_MOTION](#GLFW\_RAW\_MOUSE\_MOTION): i32
* const [GLFW\_RED\_BITS](#GLFW\_RED\_BITS): i32
* const [GLFW\_REFRESH\_RATE](#GLFW\_REFRESH\_RATE): i32
* const [GLFW\_RELEASE\_BEHAVIOR\_FLUSH](#GLFW\_RELEASE\_BEHAVIOR\_FLUSH): i32
* const [GLFW\_RELEASE\_BEHAVIOR\_NONE](#GLFW\_RELEASE\_BEHAVIOR\_NONE): i32
* const [GLFW\_RELEASE](#GLFW\_RELEASE): i32
* const [GLFW\_REPEAT](#GLFW\_REPEAT): i32
* const [GLFW\_RESIZABLE](#GLFW\_RESIZABLE): i32
* const [GLFW\_SAMPLES](#GLFW\_SAMPLES): i32
* const [GLFW\_SCALE\_TO\_MONITOR](#GLFW\_SCALE\_TO\_MONITOR): i32
* const [GLFW\_SRGB\_CAPABLE](#GLFW\_SRGB\_CAPABLE): i32
* const [GLFW\_STENCIL\_BITS](#GLFW\_STENCIL\_BITS): i32
* const [GLFW\_STEREO](#GLFW\_STEREO): i32
* const [GLFW\_STICKY\_KEYS](#GLFW\_STICKY\_KEYS): i32
* const [GLFW\_STICKY\_MOUSE\_BUTTONS](#GLFW\_STICKY\_MOUSE\_BUTTONS): i32
* const [GLFW\_TRANSPARENT\_FRAMEBUFFER](#GLFW\_TRANSPARENT\_FRAMEBUFFER): i32
* const [GLFW\_TRUE](#GLFW\_TRUE): i32
* const [GLFW\_VERSION\_MAJOR](#GLFW\_VERSION\_MAJOR): i32
* const [GLFW\_VERSION\_MINOR](#GLFW\_VERSION\_MINOR): i32
* const [GLFW\_VERSION\_REVISION](#GLFW\_VERSION\_REVISION): i32
* const [GLFW\_VERSION\_UNAVAILABLE](#GLFW\_VERSION\_UNAVAILABLE): i32
* const [GLFW\_VISIBLE](#GLFW\_VISIBLE): i32
* const [GLFW\_VRESIZE\_CURSOR](#GLFW\_VRESIZE\_CURSOR): i32
* const [GLFW\_X11\_CLASS\_NAME](#GLFW\_X11\_CLASS\_NAME): i32
* const [GLFW\_X11\_INSTANCE\_NAME](#GLFW\_X11\_INSTANCE\_NAME): i32
* const [GL\_ADD](#GL\_ADD): i32
* const [GL\_ALIASED\_LINE\_WIDTH\_RANGE](#GL\_ALIASED\_LINE\_WIDTH\_RANGE): i32
* const [GL\_ALIASED\_POINT\_SIZE\_RANGE](#GL\_ALIASED\_POINT\_SIZE\_RANGE): i32
* const [GL\_ALPHA\_BITS](#GL\_ALPHA\_BITS): i32
* const [GL\_ALPHA\_TEST](#GL\_ALPHA\_TEST): i32
* const [GL\_ALPHA](#GL\_ALPHA): i32
* const [GL\_ALWAYS](#GL\_ALWAYS): i32
* const [GL\_AMBIENT\_AND\_DIFFUSE](#GL\_AMBIENT\_AND\_DIFFUSE): i32
* const [GL\_AMBIENT](#GL\_AMBIENT): i32
* const [GL\_AND\_INVERTED](#GL\_AND\_INVERTED): i32
* const [GL\_AND\_REVERSE](#GL\_AND\_REVERSE): i32
* const [GL\_AND](#GL\_AND): i32
* const [GL\_BACK](#GL\_BACK): i32
* const [GL\_BLEND](#GL\_BLEND): i32
* const [GL\_BLUE\_BITS](#GL\_BLUE\_BITS): i32
* const [GL\_BYTE](#GL\_BYTE): i32
* const [GL\_CCW](#GL\_CCW): i32
* const [GL\_CLAMP\_TO\_EDGE](#GL\_CLAMP\_TO\_EDGE): i32
* const [GL\_CLEAR](#GL\_CLEAR): i32
* const [GL\_COLOR\_ARRAY](#GL\_COLOR\_ARRAY): i32
* const [GL\_COLOR\_BUFFER\_BIT](#GL\_COLOR\_BUFFER\_BIT): i32
* const [GL\_COLOR\_LOGIC\_OP](#GL\_COLOR\_LOGIC\_OP): i32
* const [GL\_COLOR\_MATERIAL](#GL\_COLOR\_MATERIAL): i32
* const [GL\_COMPRESSED\_TEXTURE\_FORMATS](#GL\_COMPRESSED\_TEXTURE\_FORMATS): i32
* const [GL\_CONSTANT\_ATTENUATION](#GL\_CONSTANT\_ATTENUATION): i32
* const [GL\_COPY\_INVERTED](#GL\_COPY\_INVERTED): i32
* const [GL\_COPY](#GL\_COPY): i32
* const [GL\_CULL\_FACE](#GL\_CULL\_FACE): i32
* const [GL\_CW](#GL\_CW): i32
* const [GL\_DECAL](#GL\_DECAL): i32
* const [GL\_DECR](#GL\_DECR): i32
* const [GL\_DEPTH\_BITS](#GL\_DEPTH\_BITS): i32
* const [GL\_DEPTH\_BUFFER\_BIT](#GL\_DEPTH\_BUFFER\_BIT): i32
* const [GL\_DEPTH\_TEST](#GL\_DEPTH\_TEST): i32
* const [GL\_DIFFUSE](#GL\_DIFFUSE): i32
* const [GL\_DITHER](#GL\_DITHER): i32
* const [GL\_DONT\_CARE](#GL\_DONT\_CARE): i32
* const [GL\_DST\_ALPHA](#GL\_DST\_ALPHA): i32
* const [GL\_DST\_COLOR](#GL\_DST\_COLOR): i32
* const [GL\_EMISSION](#GL\_EMISSION): i32
* const [GL\_EQUAL](#GL\_EQUAL): i32
* const [GL\_EQUIV](#GL\_EQUIV): i32
* const [GL\_EXP2](#GL\_EXP2): i32
* const [GL\_EXP](#GL\_EXP): i32
* const [GL\_EXTENSIONS](#GL\_EXTENSIONS): i32
* const [GL\_FALSE](#GL\_FALSE): i32
* const [GL\_FASTEST](#GL\_FASTEST): i32
* const [GL\_FIXED](#GL\_FIXED): i32
* const [GL\_FLAT](#GL\_FLAT): i32
* const [GL\_FLOAT](#GL\_FLOAT): i32
* const [GL\_FOG\_COLOR](#GL\_FOG\_COLOR): i32
* const [GL\_FOG\_DENSITY](#GL\_FOG\_DENSITY): i32
* const [GL\_FOG\_END](#GL\_FOG\_END): i32
* const [GL\_FOG\_HINT](#GL\_FOG\_HINT): i32
* const [GL\_FOG\_MODE](#GL\_FOG\_MODE): i32
* const [GL\_FOG\_START](#GL\_FOG\_START): i32
* const [GL\_FOG](#GL\_FOG): i32
* const [GL\_FRONT\_AND\_BACK](#GL\_FRONT\_AND\_BACK): i32
* const [GL\_FRONT](#GL\_FRONT): i32
* const [GL\_GEQUAL](#GL\_GEQUAL): i32
* const [GL\_GREATER](#GL\_GREATER): i32
* const [GL\_GREEN\_BITS](#GL\_GREEN\_BITS): i32
* const [GL\_IMPLEMENTATION\_COLOR\_READ\_FORMAT\_OES](#GL\_IMPLEMENTATION\_COLOR\_READ\_FORMAT\_OES): i32
* const [GL\_IMPLEMENTATION\_COLOR\_READ\_TYPE\_OES](#GL\_IMPLEMENTATION\_COLOR\_READ\_TYPE\_OES): i32
* const [GL\_INCR](#GL\_INCR): i32
* const [GL\_INVALID\_ENUM](#GL\_INVALID\_ENUM): i32
* const [GL\_INVALID\_OPERATION](#GL\_INVALID\_OPERATION): i32
* const [GL\_INVALID\_VALUE](#GL\_INVALID\_VALUE): i32
* const [GL\_INVERT](#GL\_INVERT): i32
* const [GL\_KEEP](#GL\_KEEP): i32
* const [GL\_LEQUAL](#GL\_LEQUAL): i32
* const [GL\_LESS](#GL\_LESS): i32
* const [GL\_LIGHT0](#GL\_LIGHT0): i32
* const [GL\_LIGHT1](#GL\_LIGHT1): i32
* const [GL\_LIGHT2](#GL\_LIGHT2): i32
* const [GL\_LIGHT3](#GL\_LIGHT3): i32
* const [GL\_LIGHT4](#GL\_LIGHT4): i32
* const [GL\_LIGHT5](#GL\_LIGHT5): i32
* const [GL\_LIGHT6](#GL\_LIGHT6): i32
* const [GL\_LIGHT7](#GL\_LIGHT7): i32
* const [GL\_LIGHTING](#GL\_LIGHTING): i32
* const [GL\_LIGHT\_MODEL\_AMBIENT](#GL\_LIGHT\_MODEL\_AMBIENT): i32
* const [GL\_LIGHT\_MODEL\_TWO\_SIDE](#GL\_LIGHT\_MODEL\_TWO\_SIDE): i32
* const [GL\_LINEAR\_ATTENUATION](#GL\_LINEAR\_ATTENUATION): i32
* const [GL\_LINEAR\_MIPMAP\_LINEAR](#GL\_LINEAR\_MIPMAP\_LINEAR): i32
* const [GL\_LINEAR\_MIPMAP\_NEAREST](#GL\_LINEAR\_MIPMAP\_NEAREST): i32
* const [GL\_LINEAR](#GL\_LINEAR): i32
* const [GL\_LINES](#GL\_LINES): i32
* const [GL\_LINE\_LOOP](#GL\_LINE\_LOOP): i32
* const [GL\_LINE\_SMOOTH\_HINT](#GL\_LINE\_SMOOTH\_HINT): i32
* const [GL\_LINE\_SMOOTH](#GL\_LINE\_SMOOTH): i32
* const [GL\_LINE\_STRIP](#GL\_LINE\_STRIP): i32
* const [GL\_LUMINANCE\_ALPHA](#GL\_LUMINANCE\_ALPHA): i32
* const [GL\_LUMINANCE](#GL\_LUMINANCE): i32
* const [GL\_MAX\_ELEMENTS\_INDICES](#GL\_MAX\_ELEMENTS\_INDICES): i32
* const [GL\_MAX\_ELEMENTS\_VERTICES](#GL\_MAX\_ELEMENTS\_VERTICES): i32
* const [GL\_MAX\_LIGHTS](#GL\_MAX\_LIGHTS): i32
* const [GL\_MAX\_MODELVIEW\_STACK\_DEPTH](#GL\_MAX\_MODELVIEW\_STACK\_DEPTH): i32
* const [GL\_MAX\_PROJECTION\_STACK\_DEPTH](#GL\_MAX\_PROJECTION\_STACK\_DEPTH): i32
* const [GL\_MAX\_TEXTURE\_SIZE](#GL\_MAX\_TEXTURE\_SIZE): i32
* const [GL\_MAX\_TEXTURE\_STACK\_DEPTH](#GL\_MAX\_TEXTURE\_STACK\_DEPTH): i32
* const [GL\_MAX\_TEXTURE\_UNITS](#GL\_MAX\_TEXTURE\_UNITS): i32
* const [GL\_MAX\_VIEWPORT\_DIMS](#GL\_MAX\_VIEWPORT\_DIMS): i32
* const [GL\_MODELVIEW](#GL\_MODELVIEW): i32
* const [GL\_MODULATE](#GL\_MODULATE): i32
* const [GL\_MULTISAMPLE](#GL\_MULTISAMPLE): i32
* const [GL\_NAND](#GL\_NAND): i32
* const [GL\_NEAREST\_MIPMAP\_LINEAR](#GL\_NEAREST\_MIPMAP\_LINEAR): i32
* const [GL\_NEAREST\_MIPMAP\_NEAREST](#GL\_NEAREST\_MIPMAP\_NEAREST): i32
* const [GL\_NEAREST](#GL\_NEAREST): i32
* const [GL\_NEVER](#GL\_NEVER): i32
* const [GL\_NICEST](#GL\_NICEST): i32
* const [GL\_NOOP](#GL\_NOOP): i32
* const [GL\_NORMALIZE](#GL\_NORMALIZE): i32
* const [GL\_NORMAL\_ARRAY](#GL\_NORMAL\_ARRAY): i32
* const [GL\_NOR](#GL\_NOR): i32
* const [GL\_NOTEQUAL](#GL\_NOTEQUAL): i32
* const [GL\_NO\_ERROR](#GL\_NO\_ERROR): i32
* const [GL\_NUM\_COMPRESSED\_TEXTURE\_FORMATS](#GL\_NUM\_COMPRESSED\_TEXTURE\_FORMATS): i32
* const [GL\_OES\_VERSION\_1\_0](#GL\_OES\_VERSION\_1\_0): i32
* const [GL\_OES\_compressed\_paletted\_texture](#GL\_OES\_compressed\_paletted\_texture): i32
* const [GL\_OES\_read\_format](#GL\_OES\_read\_format): i32
* const [GL\_ONE\_MINUS\_DST\_ALPHA](#GL\_ONE\_MINUS\_DST\_ALPHA): i32
* const [GL\_ONE\_MINUS\_DST\_COLOR](#GL\_ONE\_MINUS\_DST\_COLOR): i32
* const [GL\_ONE\_MINUS\_SRC\_ALPHA](#GL\_ONE\_MINUS\_SRC\_ALPHA): i32
* const [GL\_ONE\_MINUS\_SRC\_COLOR](#GL\_ONE\_MINUS\_SRC\_COLOR): i32
* const [GL\_ONE](#GL\_ONE): i32
* const [GL\_OR\_INVERTED](#GL\_OR\_INVERTED): i32
* const [GL\_OR\_REVERSE](#GL\_OR\_REVERSE): i32
* const [GL\_OR](#GL\_OR): i32
* const [GL\_OUT\_OF\_MEMORY](#GL\_OUT\_OF\_MEMORY): i32
* const [GL\_PACK\_ALIGNMENT](#GL\_PACK\_ALIGNMENT): i32
* const [GL\_PALETTE4\_R5\_G6\_B5\_OES](#GL\_PALETTE4\_R5\_G6\_B5\_OES): i32
* const [GL\_PALETTE4\_RGB5\_A1\_OES](#GL\_PALETTE4\_RGB5\_A1\_OES): i32
* const [GL\_PALETTE4\_RGB8\_OES](#GL\_PALETTE4\_RGB8\_OES): i32
* const [GL\_PALETTE4\_RGBA4\_OES](#GL\_PALETTE4\_RGBA4\_OES): i32
* const [GL\_PALETTE4\_RGBA8\_OES](#GL\_PALETTE4\_RGBA8\_OES): i32
* const [GL\_PALETTE8\_R5\_G6\_B5\_OES](#GL\_PALETTE8\_R5\_G6\_B5\_OES): i32
* const [GL\_PALETTE8\_RGB5\_A1\_OES](#GL\_PALETTE8\_RGB5\_A1\_OES): i32
* const [GL\_PALETTE8\_RGB8\_OES](#GL\_PALETTE8\_RGB8\_OES): i32
* const [GL\_PALETTE8\_RGBA4\_OES](#GL\_PALETTE8\_RGBA4\_OES): i32
* const [GL\_PALETTE8\_RGBA8\_OES](#GL\_PALETTE8\_RGBA8\_OES): i32
* const [GL\_PERSPECTIVE\_CORRECTION\_HINT](#GL\_PERSPECTIVE\_CORRECTION\_HINT): i32
* const [GL\_POINTS](#GL\_POINTS): i32
* const [GL\_POINT\_SMOOTH\_HINT](#GL\_POINT\_SMOOTH\_HINT): i32
* const [GL\_POINT\_SMOOTH](#GL\_POINT\_SMOOTH): i32
* const [GL\_POLYGON\_OFFSET\_FILL](#GL\_POLYGON\_OFFSET\_FILL): i32
* const [GL\_POLYGON\_SMOOTH\_HINT](#GL\_POLYGON\_SMOOTH\_HINT): i32
* const [GL\_POSITION](#GL\_POSITION): i32
* const [GL\_PROJECTION](#GL\_PROJECTION): i32
* const [GL\_QUADRATIC\_ATTENUATION](#GL\_QUADRATIC\_ATTENUATION): i32
* const [GL\_RED\_BITS](#GL\_RED\_BITS): i32
* const [GL\_RENDERER](#GL\_RENDERER): i32
* const [GL\_REPEAT](#GL\_REPEAT): i32
* const [GL\_REPLACE](#GL\_REPLACE): i32
* const [GL\_RESCALE\_NORMAL](#GL\_RESCALE\_NORMAL): i32
* const [GL\_RGBA](#GL\_RGBA): i32
* const [GL\_RGB](#GL\_RGB): i32
* const [GL\_SAMPLE\_ALPHA\_TO\_COVERAGE](#GL\_SAMPLE\_ALPHA\_TO\_COVERAGE): i32
* const [GL\_SAMPLE\_ALPHA\_TO\_ONE](#GL\_SAMPLE\_ALPHA\_TO\_ONE): i32
* const [GL\_SAMPLE\_COVERAGE](#GL\_SAMPLE\_COVERAGE): i32
* const [GL\_SCISSOR\_TEST](#GL\_SCISSOR\_TEST): i32
* const [GL\_SET](#GL\_SET): i32
* const [GL\_SHININESS](#GL\_SHININESS): i32
* const [GL\_SHORT](#GL\_SHORT): i32
* const [GL\_SMOOTH\_LINE\_WIDTH\_RANGE](#GL\_SMOOTH\_LINE\_WIDTH\_RANGE): i32
* const [GL\_SMOOTH\_POINT\_SIZE\_RANGE](#GL\_SMOOTH\_POINT\_SIZE\_RANGE): i32
* const [GL\_SMOOTH](#GL\_SMOOTH): i32
* const [GL\_SPECULAR](#GL\_SPECULAR): i32
* const [GL\_SPOT\_CUTOFF](#GL\_SPOT\_CUTOFF): i32
* const [GL\_SPOT\_DIRECTION](#GL\_SPOT\_DIRECTION): i32
* const [GL\_SPOT\_EXPONENT](#GL\_SPOT\_EXPONENT): i32
* const [GL\_SRC\_ALPHA\_SATURATE](#GL\_SRC\_ALPHA\_SATURATE): i32
* const [GL\_SRC\_ALPHA](#GL\_SRC\_ALPHA): i32
* const [GL\_SRC\_COLOR](#GL\_SRC\_COLOR): i32
* const [GL\_STACK\_OVERFLOW](#GL\_STACK\_OVERFLOW): i32
* const [GL\_STACK\_UNDERFLOW](#GL\_STACK\_UNDERFLOW): i32
* const [GL\_STENCIL\_BITS](#GL\_STENCIL\_BITS): i32
* const [GL\_STENCIL\_BUFFER\_BIT](#GL\_STENCIL\_BUFFER\_BIT): i32
* const [GL\_STENCIL\_TEST](#GL\_STENCIL\_TEST): i32
* const [GL\_SUBPIXEL\_BITS](#GL\_SUBPIXEL\_BITS): i32
* const [GL\_TEXTURE0](#GL\_TEXTURE0): i32
* const [GL\_TEXTURE10](#GL\_TEXTURE10): i32
* const [GL\_TEXTURE11](#GL\_TEXTURE11): i32
* const [GL\_TEXTURE12](#GL\_TEXTURE12): i32
* const [GL\_TEXTURE13](#GL\_TEXTURE13): i32
* const [GL\_TEXTURE14](#GL\_TEXTURE14): i32
* const [GL\_TEXTURE15](#GL\_TEXTURE15): i32
* const [GL\_TEXTURE16](#GL\_TEXTURE16): i32
* const [GL\_TEXTURE17](#GL\_TEXTURE17): i32
* const [GL\_TEXTURE18](#GL\_TEXTURE18): i32
* const [GL\_TEXTURE19](#GL\_TEXTURE19): i32
* const [GL\_TEXTURE1](#GL\_TEXTURE1): i32
* const [GL\_TEXTURE20](#GL\_TEXTURE20): i32
* const [GL\_TEXTURE21](#GL\_TEXTURE21): i32
* const [GL\_TEXTURE22](#GL\_TEXTURE22): i32
* const [GL\_TEXTURE23](#GL\_TEXTURE23): i32
* const [GL\_TEXTURE24](#GL\_TEXTURE24): i32
* const [GL\_TEXTURE25](#GL\_TEXTURE25): i32
* const [GL\_TEXTURE26](#GL\_TEXTURE26): i32
* const [GL\_TEXTURE27](#GL\_TEXTURE27): i32
* const [GL\_TEXTURE28](#GL\_TEXTURE28): i32
* const [GL\_TEXTURE29](#GL\_TEXTURE29): i32
* const [GL\_TEXTURE2](#GL\_TEXTURE2): i32
* const [GL\_TEXTURE30](#GL\_TEXTURE30): i32
* const [GL\_TEXTURE31](#GL\_TEXTURE31): i32
* const [GL\_TEXTURE3](#GL\_TEXTURE3): i32
* const [GL\_TEXTURE4](#GL\_TEXTURE4): i32
* const [GL\_TEXTURE5](#GL\_TEXTURE5): i32
* const [GL\_TEXTURE6](#GL\_TEXTURE6): i32
* const [GL\_TEXTURE7](#GL\_TEXTURE7): i32
* const [GL\_TEXTURE8](#GL\_TEXTURE8): i32
* const [GL\_TEXTURE9](#GL\_TEXTURE9): i32
* const [GL\_TEXTURE\_2D](#GL\_TEXTURE\_2D): i32
* const [GL\_TEXTURE\_COORD\_ARRAY](#GL\_TEXTURE\_COORD\_ARRAY): i32
* const [GL\_TEXTURE\_ENV\_COLOR](#GL\_TEXTURE\_ENV\_COLOR): i32
* const [GL\_TEXTURE\_ENV\_MODE](#GL\_TEXTURE\_ENV\_MODE): i32
* const [GL\_TEXTURE\_ENV](#GL\_TEXTURE\_ENV): i32
* const [GL\_TEXTURE\_MAG\_FILTER](#GL\_TEXTURE\_MAG\_FILTER): i32
* const [GL\_TEXTURE\_MIN\_FILTER](#GL\_TEXTURE\_MIN\_FILTER): i32
* const [GL\_TEXTURE\_WRAP\_S](#GL\_TEXTURE\_WRAP\_S): i32
* const [GL\_TEXTURE\_WRAP\_T](#GL\_TEXTURE\_WRAP\_T): i32
* const [GL\_TEXTURE](#GL\_TEXTURE): i32
* const [GL\_TRIANGLES](#GL\_TRIANGLES): i32
* const [GL\_TRIANGLE\_FAN](#GL\_TRIANGLE\_FAN): i32
* const [GL\_TRIANGLE\_STRIP](#GL\_TRIANGLE\_STRIP): i32
* const [GL\_TRUE](#GL\_TRUE): i32
* const [GL\_UNPACK\_ALIGNMENT](#GL\_UNPACK\_ALIGNMENT): i32
* const [GL\_UNSIGNED\_BYTE](#GL\_UNSIGNED\_BYTE): i32
* const [GL\_UNSIGNED\_SHORT\_4\_4\_4\_4](#GL\_UNSIGNED\_SHORT\_4\_4\_4\_4): i32
* const [GL\_UNSIGNED\_SHORT\_5\_5\_5\_1](#GL\_UNSIGNED\_SHORT\_5\_5\_5\_1): i32
* const [GL\_UNSIGNED\_SHORT\_5\_6\_5](#GL\_UNSIGNED\_SHORT\_5\_6\_5): i32
* const [GL\_UNSIGNED\_SHORT](#GL\_UNSIGNED\_SHORT): i32
* const [GL\_VENDOR](#GL\_VENDOR): i32
* const [GL\_VERSION](#GL\_VERSION): i32
* const [GL\_VERTEX\_ARRAY](#GL\_VERTEX\_ARRAY): i32
* const [GL\_XOR](#GL\_XOR): i32
* const [GL\_ZERO](#GL\_ZERO): i32
* const [HUGE\_VAL](#HUGE\_VAL): f64
* const [JSON\_FALSE](#JSON\_FALSE): [JsonNode\*](#JsonNode)
* const [JSON\_NULL](#JSON\_NULL): [JsonNode\*](#JsonNode)
* const [JSON\_TRUE](#JSON\_TRUE): [JsonNode\*](#JsonNode)
* const [M\_PI](#M\_PI): f64
* const [ULLONG\_MAX](#ULLONG\_MAX): i64
* const [WIN32\_LEAN\_AND\_MEAN](#WIN32\_LEAN\_AND\_MEAN): i32
* const [\_\_gl\_h\_](#\_\_gl\_h\_): i32
* const [cAllocator](#cAllocator): [Allocator const\*](#Allocator)
* const [debugAllocator](#debugAllocator): [DebugAllocator\*](#DebugAllocator)
* const [numOfTypeInfos](#numOfTypeInfos): i64
* const [stderr](#stderr): [FILE\*](#FILE)
* const [stdout](#stdout): [FILE\*](#FILE)
* const [typeInfos](#typeInfos): [TypeInfo\*\*](#TypeInfo)


## documentationGenerator Types

* enum [CmdParserStatus](#CmdParserStatus)
* enum [FileStatus](#FileStatus)
* enum [JsonType](#JsonType)
* enum [Modifiers](#Modifiers)
* enum [OptionFlag](#OptionFlag)
* enum [ParserStatus](#ParserStatus)
* enum [TypeKind](#TypeKind)
* struct [Allocation](#Allocation)
* struct [Allocator](#Allocator)
* struct [Arena](#Arena)
* struct [Array<Allocation\*>](#Array<Allocation\*>)
* struct [Array<JsonNode\*>](#Array<JsonNode\*>)
* struct [Array<T>](#Array<T>)
* struct [Array](#Array)
* struct [CmdParser](#CmdParser)
* struct [DebugAllocator](#DebugAllocator)
* struct [EnumFieldInfo](#EnumFieldInfo)
* struct [FILE](#FILE)
* struct [FILE](#FILE)
* struct [FieldInfo](#FieldInfo)
* struct [GLFWcursor](#GLFWcursor)
* struct [GLFWgamepadstate](#GLFWgamepadstate)
* struct [GLFWgammaramp](#GLFWgammaramp)
* struct [GLFWimage](#GLFWimage)
* struct [GLFWmonitor](#GLFWmonitor)
* struct [GLFWvidmode](#GLFWvidmode)
* struct [GLFWwindow](#GLFWwindow)
* struct [GenericInfo](#GenericInfo)
* struct [JsonNode](#JsonNode)
* struct [Map<K,V>](#Map<K,V>)
* struct [Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>)
* struct [Map<char const\*,Option>](#Map<char\-const\*,Option>)
* struct [Map<char const\*,V>](#Map<char\-const\*,V>)
* struct [MapEntry<K,V>](#MapEntry<K,V>)
* struct [MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)
* struct [MapEntry](#MapEntry)
* struct [MapIterator<K,V>](#MapIterator<K,V>)
* struct [MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)
* struct [MapIterator](#MapIterator)
* struct [Map](#Map)
* struct [Option](#Option)
* struct [ParamInfo](#ParamInfo)
* struct [Parser](#Parser)
* struct [StringBuffer](#StringBuffer)
* struct [StringView](#StringView)
* struct [TypeInfo](#TypeInfo)
* struct [va\_list](#va\_list)
* typedef [documentationGenerator](#documentationGenerator)::[Array<JsonNode\*>](#Array<JsonNode\*>) as [JsonArray](#JsonArray)
* typedef [documentationGenerator](#documentationGenerator)::[Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>) as [JsonObject](#JsonObject)
* typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)
* typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)
* typedef [func\(K, K\) : bool](#\_) as [EqualFn<K>](#EqualFn<K>)
* typedef [func\(K\) : u32](#\_) as [HashFn<K>](#HashFn<K>)
* typedef [func\(\) : void](#\_) as [GLFWglproc](#GLFWglproc)
* typedef [func\(\) : void](#\_) as [GLFWvkproc](#GLFWvkproc)
* typedef [func\(char const\*, char const\*\) : bool](#\_) as [EqualFn<char const\*>](#EqualFn<char\-const\*>)
* typedef [func\(char const\*\) : u32](#\_) as [HashFn<char const\*>](#HashFn<char\-const\*>)
* typedef [func\(i32, char const\*\) : void](#\_) as [GLFWerrorfun](#GLFWerrorfun)
* typedef [func\(i32, i32\) : void](#\_) as [GLFWjoystickfun](#GLFWjoystickfun)
* typedef [func\(struct GLFWmonitor\*, i32\) : void](#\_) as [GLFWmonitorfun](#GLFWmonitorfun)
* typedef [func\(struct GLFWwindow\*, f32, f32\) : void](#\_) as [GLFWwindowcontentscalefun](#GLFWwindowcontentscalefun)
* typedef [func\(struct GLFWwindow\*, f64, f64\) : void](#\_) as [GLFWcursorposfun](#GLFWcursorposfun)
* typedef [func\(struct GLFWwindow\*, f64, f64\) : void](#\_) as [GLFWscrollfun](#GLFWscrollfun)
* typedef [func\(struct GLFWwindow\*, i32, \[\]char const\*\) : void](#\_) as [GLFWdropfun](#GLFWdropfun)
* typedef [func\(struct GLFWwindow\*, i32, i32, i32, i32\) : void](#\_) as [GLFWkeyfun](#GLFWkeyfun)
* typedef [func\(struct GLFWwindow\*, i32, i32, i32\) : void](#\_) as [GLFWmousebuttonfun](#GLFWmousebuttonfun)
* typedef [func\(struct GLFWwindow\*, i32, i32\) : void](#\_) as [GLFWframebuffersizefun](#GLFWframebuffersizefun)
* typedef [func\(struct GLFWwindow\*, i32, i32\) : void](#\_) as [GLFWwindowposfun](#GLFWwindowposfun)
* typedef [func\(struct GLFWwindow\*, i32, i32\) : void](#\_) as [GLFWwindowsizefun](#GLFWwindowsizefun)
* typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWcursorenterfun](#GLFWcursorenterfun)
* typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWwindowfocusfun](#GLFWwindowfocusfun)
* typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWwindowiconifyfun](#GLFWwindowiconifyfun)
* typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWwindowmaximizefun](#GLFWwindowmaximizefun)
* typedef [func\(struct GLFWwindow\*, u32, i32\) : void](#\_) as [GLFWcharmodsfun](#GLFWcharmodsfun)
* typedef [func\(struct GLFWwindow\*, u32\) : void](#\_) as [GLFWcharfun](#GLFWcharfun)
* typedef [func\(struct GLFWwindow\*\) : void](#\_) as [GLFWwindowclosefun](#GLFWwindowclosefun)
* typedef [func\(struct GLFWwindow\*\) : void](#\_) as [GLFWwindowrefreshfun](#GLFWwindowrefreshfun)
* typedef [gl](#gl)::GLclampf as [GLclampf](#GLclampf)
* typedef [gl](#gl)::GLclampx as [GLclampx](#GLclampx)
* typedef [gl](#gl)::GLfixed as [GLfixed](#GLfixed)
* typedef [gl](#gl)::GLintptrARB as [GLintptrARB](#GLintptrARB)
* typedef [gl](#gl)::GLsizeiptrARB as [GLsizeiptrARB](#GLsizeiptrARB)
* typedef [gl](#gl)::GLvoid as [GLvoid](#GLvoid)
* typedef [gl](#gl)::_GLfuncptr as [\_GLfuncptr](#\_GLfuncptr)
* typedef bool as [GLboolean](#GLboolean)
* typedef f32 as [GLfloat](#GLfloat)
* typedef i16 as [GLshort](#GLshort)
* typedef i32 as [GLbitfield](#GLbitfield)
* typedef i32 as [GLenum](#GLenum)
* typedef i32 as [GLint](#GLint)
* typedef i64 as [GLsizei](#GLsizei)
* typedef i8 as [GLbyte](#GLbyte)
* typedef u16 as [GLushort](#GLushort)
* typedef u32 as [GLuint](#GLuint)
* typedef u8 as [GLubyte](#GLubyte)
* union [JsonValue](#JsonValue)


## documentationGenerator Functions

* func [ABS](#ABS)(a: f32) : f32
* func [Allocator\_alloc](#Allocator\_alloc)(a: [Allocator const\*](#Allocator), size: u64) : [void\*](#void)
* func [Allocator\_calloc](#Allocator\_calloc)(a: [Allocator const\*](#Allocator), num: u64, size: u64) : [void\*](#void)
* func [Allocator\_free](#Allocator\_free)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void))
* func [Allocator\_realloc](#Allocator\_realloc)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void), oldsize: u64, newsize: u64) : [void\*](#void)
* func [Arena\_free](#Arena\_free)(arena: [Arena\*](#Arena))
* func [Arena\_init](#Arena\_init)(arena: [Arena\*](#Arena), size: u64, alloc: [Allocator const\*](#Allocator))
* func [Arena\_malloc](#Arena\_malloc)(arena: [Arena\*](#Arena), size: u64) : [void\*](#void)
* func [Array\_add<Allocation\*>](#Array\_add<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), element: [Allocation\*](#Allocation))
* func [Array\_add<JsonNode\*>](#Array\_add<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), element: [JsonNode\*](#JsonNode))
* func [Array\_addAll](#Array\_addAll)(a: [Array<T>\*](#Array<T>), other: [Array<T>\*](#Array<T>))
* func [Array\_add](#Array\_add)(a: [Array<T>\*](#Array<T>), element: [T](#T))
* func [Array\_clear](#Array\_clear)(a: [Array<T>\*](#Array<T>))
* func [Array\_empty](#Array\_empty)(a: [Array<T>\*](#Array<T>)) : bool
* func [Array\_first](#Array\_first)(a: [Array<T>\*](#Array<T>)) : [T](#T)
* func [Array\_forEach](#Array\_forEach)(a: [Array<T>\*](#Array<T>), fn: [func<T>\(T\) : bool](#\_))
* func [Array\_free<Allocation\*>](#Array\_free<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>))
* func [Array\_free<JsonNode\*>](#Array\_free<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>))
* func [Array\_free](#Array\_free)(a: [Array<T>\*](#Array<T>))
* func [Array\_get<Allocation\*>](#Array\_get<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)
* func [Array\_get<JsonNode\*>](#Array\_get<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), index: i32) : [JsonNode\*](#JsonNode)
* func [Array\_get](#Array\_get)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)
* func [Array\_init<Allocation\*>](#Array\_init<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [Array\_init<JsonNode\*>](#Array\_init<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [Array\_init](#Array\_init)(a: [Array<T>\*](#Array<T>), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [Array\_last](#Array\_last)(a: [Array<T>\*](#Array<T>)) : [T](#T)
* func [Array\_pop](#Array\_pop)(a: [Array<T>\*](#Array<T>)) : [T](#T)
* func [Array\_push](#Array\_push)(a: [Array<T>\*](#Array<T>), element: [T](#T))
* func [Array\_removeAt<Allocation\*>](#Array\_removeAt<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)
* func [Array\_removeAt](#Array\_removeAt)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)
* func [Array\_set](#Array\_set)(a: [Array<T>\*](#Array<T>), index: i32, element: [T](#T))
* func [Array\_size<Allocation\*>](#Array\_size<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>)) : i32
* func [Array\_size<JsonNode\*>](#Array\_size<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>)) : i32
* func [Array\_size](#Array\_size)(a: [Array<T>\*](#Array<T>)) : i32
* func [CmdParser\_addOption](#CmdParser\_addOption)(p: [CmdParser\*](#CmdParser), longName: char const*, shortName: char, description: char const*, flags: i32, defaultValue: char const*)
* func [CmdParser\_getOption](#CmdParser\_getOption)(p: [CmdParser\*](#CmdParser), longName: char const*) : [cmdline](#cmdline)::[Option](#Option)
* func [CmdParser\_init](#CmdParser\_init)(p: [CmdParser\*](#CmdParser))
* func [CmdParser\_parse](#CmdParser\_parse)(p: [CmdParser\*](#CmdParser), argc: i32, argv: char**) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)
* func [CreateJsonArray](#CreateJsonArray)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonNumber](#CreateJsonNumber)(value: f64, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonObject](#CreateJsonObject)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonStringNoDup](#CreateJsonStringNoDup)(str: char const*, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [CreateJsonString](#CreateJsonString)(str: char const*, len: i32, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))
* func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))
* func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)
* func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))
* func [FileLength](#FileLength)(fileName: char const*) : i64
* func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*
* func [JsonNode\_add](#JsonNode\_add)(node: [JsonNode\*](#JsonNode), n: [JsonNode\*](#JsonNode))
* func [JsonNode\_asArray](#JsonNode\_asArray)(node: [JsonNode\*](#JsonNode)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)
* func [JsonNode\_asBool](#JsonNode\_asBool)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_asFloat](#JsonNode\_asFloat)(node: [JsonNode\*](#JsonNode)) : f64
* func [JsonNode\_asInt](#JsonNode\_asInt)(node: [JsonNode\*](#JsonNode)) : i32
* func [JsonNode\_asLong](#JsonNode\_asLong)(node: [JsonNode\*](#JsonNode)) : i64
* func [JsonNode\_asNumber](#JsonNode\_asNumber)(node: [JsonNode\*](#JsonNode)) : f64
* func [JsonNode\_asObject](#JsonNode\_asObject)(node: [JsonNode\*](#JsonNode)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* func [JsonNode\_asString](#JsonNode\_asString)(node: [JsonNode\*](#JsonNode)) : char const*
* func [JsonNode\_at](#JsonNode\_at)(node: [JsonNode\*](#JsonNode), index: i32) : [JsonNode\*](#JsonNode)
* func [JsonNode\_free](#JsonNode\_free)(node: [JsonNode\*](#JsonNode))
* func [JsonNode\_getArray](#JsonNode\_getArray)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)
* func [JsonNode\_getBool](#JsonNode\_getBool)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: bool) : bool
* func [JsonNode\_getFloat](#JsonNode\_getFloat)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: f64) : f64
* func [JsonNode\_getInt](#JsonNode\_getInt)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i32) : i32
* func [JsonNode\_getLong](#JsonNode\_getLong)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i64) : i64
* func [JsonNode\_getObject](#JsonNode\_getObject)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)
* func [JsonNode\_getStrCopy](#JsonNode\_getStrCopy)(node: [JsonNode\*](#JsonNode), key: char const*, str: char*, len: i32) : char*
* func [JsonNode\_getStr](#JsonNode\_getStr)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: char const*) : char const*
* func [JsonNode\_get](#JsonNode\_get)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)
* func [JsonNode\_isArray](#JsonNode\_isArray)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isBool](#JsonNode\_isBool)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isFalse](#JsonNode\_isFalse)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isNull](#JsonNode\_isNull)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isNumber](#JsonNode\_isNumber)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isObject](#JsonNode\_isObject)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isString](#JsonNode\_isString)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_isTrue](#JsonNode\_isTrue)(node: [JsonNode\*](#JsonNode)) : bool
* func [JsonNode\_print](#JsonNode\_print)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer)) : char const*
* func [JsonNode\_put](#JsonNode\_put)(node: [JsonNode\*](#JsonNode), key: char const*, n: [JsonNode\*](#JsonNode), len: i32)
* func [JsonNode\_size](#JsonNode\_size)(node: [JsonNode\*](#JsonNode)) : i32
* func [JsonTypeAsStr](#JsonTypeAsStr)(e: [json](#json)::[JsonType](#JsonType)) : char const*
* func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MIN<i32>](#MIN<i32>)(a: i32, b: i32) : i32
* func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)
* func [MapIterator\_hasNext<char const\*,JsonNode\*>](#MapIterator\_hasNext<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : bool
* func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : bool
* func [MapIterator\_next<char const\*,JsonNode\*>](#MapIterator\_next<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)
* func [MapIterator\_next](#MapIterator\_next)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<K,V>](#MapEntry<K,V>)
* func [Map\_contains<char const\*,Option>](#Map\_contains<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : bool
* func [Map\_contains](#Map\_contains)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : bool
* func [Map\_empty](#Map\_empty)(m: [Map<K,V>\*](#Map<K,V>)) : bool
* func [Map\_free<char const\*,JsonNode\*>](#Map\_free<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>))
* func [Map\_free<char const\*,Option>](#Map\_free<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>))
* func [Map\_free](#Map\_free)(m: [Map<K,V>\*](#Map<K,V>))
* func [Map\_get<char const\*,JsonNode\*>](#Map\_get<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*) : [JsonNode\*](#JsonNode)
* func [Map\_get<char const\*,Option>](#Map\_get<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : [cmdline](#cmdline)::[Option](#Option)
* func [Map\_get](#Map\_get)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_init<char const\*,JsonNode\*>](#Map\_init<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), emptyValue: [JsonNode\*](#JsonNode), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_init<char const\*,Option>](#Map\_init<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), emptyValue: [cmdline](#cmdline)::[Option](#Option), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_init](#Map\_init)(m: [Map<K,V>\*](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [Allocator const\*](#Allocator))
* func [Map\_iter<char const\*,JsonNode\*>](#Map\_iter<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)
* func [Map\_iter](#Map\_iter)(m: [Map<K,V>\*](#Map<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<K,V>](#MapIterator<K,V>)
* func [Map\_put<char const\*,JsonNode\*>](#Map\_put<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*, value: [JsonNode\*](#JsonNode))
* func [Map\_put<char const\*,Option>](#Map\_put<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*, value: [cmdline](#cmdline)::[Option](#Option))
* func [Map\_put](#Map\_put)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K), value: [V](#V))
* func [Map\_remove](#Map\_remove)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)
* func [Map\_size<char const\*,JsonNode\*>](#Map\_size<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : i32
* func [Map\_size](#Map\_size)(m: [Map<K,V>\*](#Map<K,V>)) : i32
* func [Parser\_free](#Parser\_free)(p: [Parser\*](#Parser))
* func [Parser\_hasError](#Parser\_hasError)(p: [Parser\*](#Parser)) : bool
* func [Parser\_init](#Parser\_init)(p: [Parser\*](#Parser), alloc: [Allocator const\*](#Allocator))
* func [Parser\_parseJson](#Parser\_parseJson)(p: [Parser\*](#Parser), buffer: char const*) : [JsonNode\*](#JsonNode)
* func [PrintJson](#PrintJson)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer))
* func [PtrEqualFn<char const\*>](#PtrEqualFn<char\-const\*>)(a: char const*, b: char const*) : bool
* func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool
* func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)
* func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool
* func [StrHashFn](#StrHashFn)(str: char const*) : u32
* func [StrMap<Option>](#StrMap<Option>)(emptyValue: [cmdline](#cmdline)::[Option](#Option), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,Option>](#Map<char\-const\*,Option>)
* func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)
* func [StringBufferInit](#StringBufferInit)(initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)
* func [StringBuffer\_appendChar](#StringBuffer\_appendChar)(b: [StringBuffer\*](#StringBuffer), c: char) : i32
* func [StringBuffer\_appendFloat](#StringBuffer\_appendFloat)(b: [StringBuffer\*](#StringBuffer), f: f64) : i32
* func [StringBuffer\_appendI32](#StringBuffer\_appendI32)(b: [StringBuffer\*](#StringBuffer), i: i32) : i32
* func [StringBuffer\_appendI64](#StringBuffer\_appendI64)(b: [StringBuffer\*](#StringBuffer), i: i64) : i32
* func [StringBuffer\_appendStr](#StringBuffer\_appendStr)(b: [StringBuffer\*](#StringBuffer), str: char*, len: i32)
* func [StringBuffer\_appendU32](#StringBuffer\_appendU32)(b: [StringBuffer\*](#StringBuffer), i: u32) : i32
* func [StringBuffer\_appendU64](#StringBuffer\_appendU64)(b: [StringBuffer\*](#StringBuffer), i: u64) : i32
* func [StringBuffer\_append](#StringBuffer\_append)(b: [StringBuffer\*](#StringBuffer), format: char const*) : i32
* func [StringBuffer\_asStringView](#StringBuffer\_asStringView)(b: [StringBuffer\*](#StringBuffer)) : [string\_view](#string\_view)::[StringView](#StringView)
* func [StringBuffer\_cStr](#StringBuffer\_cStr)(b: [StringBuffer\*](#StringBuffer)) : char const*
* func [StringBuffer\_clear](#StringBuffer\_clear)(b: [StringBuffer\*](#StringBuffer))
* func [StringBuffer\_contains](#StringBuffer\_contains)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : bool
* func [StringBuffer\_copyTo](#StringBuffer\_copyTo)(b: [StringBuffer\*](#StringBuffer), buf: char*, len: i32, addZero: bool) : i32
* func [StringBuffer\_delete](#StringBuffer\_delete)(b: [StringBuffer\*](#StringBuffer), start: i32, end: i32)
* func [StringBuffer\_empty](#StringBuffer\_empty)(b: [StringBuffer\*](#StringBuffer)) : bool
* func [StringBuffer\_forEach](#StringBuffer\_forEach)(b: [StringBuffer\*](#StringBuffer), fn: [func\(char\) : bool](#\_))
* func [StringBuffer\_free](#StringBuffer\_free)(b: [StringBuffer\*](#StringBuffer))
* func [StringBuffer\_get](#StringBuffer\_get)(b: [StringBuffer\*](#StringBuffer), index: i32) : char
* func [StringBuffer\_indexOfAt](#StringBuffer\_indexOfAt)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32, fromIndex: i32) : i32
* func [StringBuffer\_indexOf](#StringBuffer\_indexOf)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : i32
* func [StringBuffer\_init](#StringBuffer\_init)(b: [StringBuffer\*](#StringBuffer), initialSize: i32, alloc: [Allocator const\*](#Allocator))
* func [StringBuffer\_insert](#StringBuffer\_insert)(b: [StringBuffer\*](#StringBuffer), index: i32, format: char const*) : i32
* func [StringBuffer\_size](#StringBuffer\_size)(b: [StringBuffer\*](#StringBuffer)) : i32
* func [StringViewInit](#StringViewInit)(str: char const*, len: i32) : [string\_view](#string\_view)::[StringView](#StringView)
* func [StringView\_clear](#StringView\_clear)(b: [string\_view](#string\_view)::[StringView](#StringView))
* func [StringView\_contains](#StringView\_contains)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool
* func [StringView\_copyTo](#StringView\_copyTo)(b: [string\_view](#string\_view)::[StringView](#StringView), buf: char*, len: i32, addZero: bool) : i32
* func [StringView\_empty](#StringView\_empty)(b: [string\_view](#string\_view)::[StringView](#StringView)) : bool
* func [StringView\_endsWith](#StringView\_endsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), suffix: char const*, len: i32) : bool
* func [StringView\_equals](#StringView\_equals)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool
* func [StringView\_forEach](#StringView\_forEach)(b: [string\_view](#string\_view)::[StringView](#StringView), fn: [func\(char\) : bool](#\_))
* func [StringView\_get](#StringView\_get)(b: [string\_view](#string\_view)::[StringView](#StringView), index: i32) : char
* func [StringView\_indexOfAt](#StringView\_indexOfAt)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32, fromIndex: i32) : i32
* func [StringView\_indexOf](#StringView\_indexOf)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : i32
* func [StringView\_size](#StringView\_size)(b: [string\_view](#string\_view)::[StringView](#StringView)) : i32
* func [StringView\_startsWith](#StringView\_startsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), prefix: char const*, len: i32, fromIndex: i32) : bool
* func [StringView\_substring](#StringView\_substring)(b: [string\_view](#string\_view)::[StringView](#StringView), start: i32, end: i32) : [string\_view](#string\_view)::[StringView](#StringView)
* func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : char const*
* func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)
* func [asinf](#asinf)(v: f32) : f32
* func [assert](#assert)(e: bool)
* func [c::MIN<u64>](#c::MIN<u64>)(a: u64, b: u64) : u64
* func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)
* func [cos](#cos)(v: f64) : f64
* func [cosf](#cosf)(v: f32) : f32
* func [exit](#exit)(code: i32)
* func [fabs](#fabs)(v: f32) : f32
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [fclose](#fclose)(stream: [FILE\*](#FILE))
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)
* func [fputs](#fputs)(format: char const*, f: [FILE\*](#FILE))
* func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64
* func [fread](#fread)(buf: char*, size: u64, n: i64, stream: [FILE\*](#FILE)) : i64
* func [free](#free)(ptr: [void\*](#void))
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64
* func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64
* func [getTypeInfo](#getTypeInfo)(id: i64) : [TypeInfo\*](#TypeInfo)
* func [glActiveTexture](#glActiveTexture)(_0: i32)
* func [glAlphaFunc](#glAlphaFunc)(_0: i32, _1: [gl](#gl)::GLclampf)
* func [glAlphaFuncx](#glAlphaFuncx)(_0: i32, _1: [gl](#gl)::GLclampx)
* func [glBindTexture](#glBindTexture)(_0: i32, _1: u32)
* func [glBlendFunc](#glBlendFunc)(_0: i32, _1: i32)
* func [glClearColor](#glClearColor)(_0: [gl](#gl)::GLclampf, _1: [gl](#gl)::GLclampf, _2: [gl](#gl)::GLclampf, _3: [gl](#gl)::GLclampf)
* func [glClearColorx](#glClearColorx)(_0: [gl](#gl)::GLclampx, _1: [gl](#gl)::GLclampx, _2: [gl](#gl)::GLclampx, _3: [gl](#gl)::GLclampx)
* func [glClearDepthf](#glClearDepthf)(_0: [gl](#gl)::GLclampf)
* func [glClearDepthx](#glClearDepthx)(_0: [gl](#gl)::GLclampx)
* func [glClearStencil](#glClearStencil)(_0: i32)
* func [glClear](#glClear)(_0: i32)
* func [glClientActiveTexture](#glClientActiveTexture)(_0: i32)
* func [glColor4f](#glColor4f)(_0: f32, _1: f32, _2: f32, _3: f32)
* func [glColor4x](#glColor4x)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed)
* func [glColorMask](#glColorMask)(_0: bool, _1: bool, _2: bool, _3: bool)
* func [glColorPointer](#glColorPointer)(_0: i32, _1: i32, _2: i64, _3: GLvoid const*)
* func [glCompressedTexImage2D](#glCompressedTexImage2D)(_0: i32, _1: i32, _2: i32, _3: i64, _4: i64, _5: i32, _6: i64, _7: GLvoid const*)
* func [glCompressedTexSubImage2D](#glCompressedTexSubImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i64, _5: i64, _6: i32, _7: i64, _8: GLvoid const*)
* func [glCopyTexImage2D](#glCopyTexImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i32, _5: i64, _6: i64, _7: i32)
* func [glCopyTexSubImage2D](#glCopyTexSubImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i32, _5: i32, _6: i64, _7: i64)
* func [glCullFace](#glCullFace)(_0: i32)
* func [glDeleteTextures](#glDeleteTextures)(_0: i64, _1: u32 const*)
* func [glDepthFunc](#glDepthFunc)(_0: i32)
* func [glDepthMask](#glDepthMask)(_0: bool)
* func [glDepthRangef](#glDepthRangef)(_0: [gl](#gl)::GLclampf, _1: [gl](#gl)::GLclampf)
* func [glDepthRangex](#glDepthRangex)(_0: [gl](#gl)::GLclampx, _1: [gl](#gl)::GLclampx)
* func [glDisableClientState](#glDisableClientState)(_0: i32)
* func [glDisable](#glDisable)(_0: i32)
* func [glDrawArrays](#glDrawArrays)(_0: i32, _1: i32, _2: i64)
* func [glDrawElements](#glDrawElements)(_0: i32, _1: i64, _2: i32, _3: GLvoid const*)
* func [glEnableClientState](#glEnableClientState)(_0: i32)
* func [glEnable](#glEnable)(_0: i32)
* func [glFinish](#glFinish)()
* func [glFlush](#glFlush)()
* func [glFogf](#glFogf)(_0: i32, _1: f32)
* func [glFogfv](#glFogfv)(_0: i32, _1: f32 const*)
* func [glFogx](#glFogx)(_0: i32, _1: [gl](#gl)::GLfixed)
* func [glFogxv](#glFogxv)(_0: i32, _1: GLfixed const*)
* func [glFrontFace](#glFrontFace)(_0: i32)
* func [glFrustumf](#glFrustumf)(_0: f32, _1: f32, _2: f32, _3: f32, _4: f32, _5: f32)
* func [glFrustumx](#glFrustumx)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed, _4: [gl](#gl)::GLfixed, _5: [gl](#gl)::GLfixed)
* func [glGenTextures](#glGenTextures)(_0: i64, _1: u32*)
* func [glGetError](#glGetError)() : i32
* func [glGetIntegerv](#glGetIntegerv)(_0: i32, _1: i32*)
* func [glGetString](#glGetString)(_0: i32) : u8 const*
* func [glHint](#glHint)(_0: i32, _1: i32)
* func [glLightModelf](#glLightModelf)(_0: i32, _1: f32)
* func [glLightModelfv](#glLightModelfv)(_0: i32, _1: f32 const*)
* func [glLightModelx](#glLightModelx)(_0: i32, _1: [gl](#gl)::GLfixed)
* func [glLightModelxv](#glLightModelxv)(_0: i32, _1: GLfixed const*)
* func [glLightf](#glLightf)(_0: i32, _1: i32, _2: f32)
* func [glLightfv](#glLightfv)(_0: i32, _1: i32, _2: f32 const*)
* func [glLightx](#glLightx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)
* func [glLightxv](#glLightxv)(_0: i32, _1: i32, _2: GLfixed const*)
* func [glLineWidth](#glLineWidth)(_0: f32)
* func [glLineWidthx](#glLineWidthx)(_0: [gl](#gl)::GLfixed)
* func [glLoadIdentity](#glLoadIdentity)()
* func [glLoadMatrixf](#glLoadMatrixf)(_0: f32 const*)
* func [glLoadMatrixx](#glLoadMatrixx)(_0: GLfixed const*)
* func [glLogicOp](#glLogicOp)(_0: i32)
* func [glMaterialf](#glMaterialf)(_0: i32, _1: i32, _2: f32)
* func [glMaterialfv](#glMaterialfv)(_0: i32, _1: i32, _2: f32 const*)
* func [glMaterialx](#glMaterialx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)
* func [glMaterialxv](#glMaterialxv)(_0: i32, _1: i32, _2: GLfixed const*)
* func [glMatrixMode](#glMatrixMode)(_0: i32)
* func [glMultMatrixf](#glMultMatrixf)(_0: f32 const*)
* func [glMultMatrixx](#glMultMatrixx)(_0: GLfixed const*)
* func [glMultiTexCoord4f](#glMultiTexCoord4f)(_0: i32, _1: f32, _2: f32, _3: f32, _4: f32)
* func [glMultiTexCoord4x](#glMultiTexCoord4x)(_0: i32, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed, _4: [gl](#gl)::GLfixed)
* func [glNormal3f](#glNormal3f)(_0: f32, _1: f32, _2: f32)
* func [glNormal3x](#glNormal3x)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed)
* func [glNormalPointer](#glNormalPointer)(_0: i32, _1: i64, _2: GLvoid const*)
* func [glOrthof](#glOrthof)(_0: f32, _1: f32, _2: f32, _3: f32, _4: f32, _5: f32)
* func [glOrthox](#glOrthox)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed, _4: [gl](#gl)::GLfixed, _5: [gl](#gl)::GLfixed)
* func [glPixelStorei](#glPixelStorei)(_0: i32, _1: i32)
* func [glPointSize](#glPointSize)(_0: f32)
* func [glPointSizex](#glPointSizex)(_0: [gl](#gl)::GLfixed)
* func [glPolygonOffset](#glPolygonOffset)(_0: f32, _1: f32)
* func [glPolygonOffsetx](#glPolygonOffsetx)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed)
* func [glPopMatrix](#glPopMatrix)()
* func [glPushMatrix](#glPushMatrix)()
* func [glReadPixels](#glReadPixels)(_0: i32, _1: i32, _2: i64, _3: i64, _4: i32, _5: i32, _6: GLvoid*)
* func [glRotatef](#glRotatef)(_0: f32, _1: f32, _2: f32, _3: f32)
* func [glRotatex](#glRotatex)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed)
* func [glSampleCoverage](#glSampleCoverage)(_0: [gl](#gl)::GLclampf, _1: bool)
* func [glSampleCoveragex](#glSampleCoveragex)(_0: [gl](#gl)::GLclampx, _1: bool)
* func [glScalef](#glScalef)(_0: f32, _1: f32, _2: f32)
* func [glScalex](#glScalex)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed)
* func [glScissor](#glScissor)(_0: i32, _1: i32, _2: i64, _3: i64)
* func [glShadeModel](#glShadeModel)(_0: i32)
* func [glStencilFunc](#glStencilFunc)(_0: i32, _1: i32, _2: u32)
* func [glStencilMask](#glStencilMask)(_0: u32)
* func [glStencilOp](#glStencilOp)(_0: i32, _1: i32, _2: i32)
* func [glTexCoordPointer](#glTexCoordPointer)(_0: i32, _1: i32, _2: i64, _3: GLvoid const*)
* func [glTexEnvf](#glTexEnvf)(_0: i32, _1: i32, _2: f32)
* func [glTexEnvfv](#glTexEnvfv)(_0: i32, _1: i32, _2: f32 const*)
* func [glTexEnvx](#glTexEnvx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)
* func [glTexEnvxv](#glTexEnvxv)(_0: i32, _1: i32, _2: GLfixed const*)
* func [glTexImage2D](#glTexImage2D)(_0: i32, _1: i32, _2: i32, _3: i64, _4: i64, _5: i32, _6: i32, _7: i32, _8: GLvoid const*)
* func [glTexParameterf](#glTexParameterf)(_0: i32, _1: i32, _2: f32)
* func [glTexParameterx](#glTexParameterx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)
* func [glTexSubImage2D](#glTexSubImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i64, _5: i64, _6: i32, _7: i32, _8: GLvoid const*)
* func [glTranslatef](#glTranslatef)(_0: f32, _1: f32, _2: f32)
* func [glTranslatex](#glTranslatex)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed)
* func [glVertexPointer](#glVertexPointer)(_0: i32, _1: i32, _2: i64, _3: GLvoid const*)
* func [glViewport](#glViewport)(_0: i32, _1: i32, _2: i64, _3: i64)
* func [glfwCreateCursor](#glfwCreateCursor)(image: [GLFWimage\* const](#GLFWimage), xhot: i32, yhot: i32) : [GLFWcursor\*](#GLFWcursor)
* func [glfwCreateStandardCursor](#glfwCreateStandardCursor)(shape: i32) : [GLFWcursor\*](#GLFWcursor)
* func [glfwCreateWindow](#glfwCreateWindow)(width: i32, height: i32, title: char const*, monitor: [GLFWmonitor\*](#GLFWmonitor), share: [GLFWwindow\*](#GLFWwindow)) : [GLFWwindow\*](#GLFWwindow)
* func [glfwDefaultWindowHints](#glfwDefaultWindowHints)()
* func [glfwDestroyCursor](#glfwDestroyCursor)(cursor: [GLFWcursor\*](#GLFWcursor))
* func [glfwDestroyWindow](#glfwDestroyWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwExtensionSupported](#glfwExtensionSupported)(extension: char const*) : i32
* func [glfwFocusWindow](#glfwFocusWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwGetClipboardString](#glfwGetClipboardString)(window: [GLFWwindow\*](#GLFWwindow)) : char const*
* func [glfwGetCurrentContext](#glfwGetCurrentContext)() : [GLFWwindow\*](#GLFWwindow)
* func [glfwGetCursorPos](#glfwGetCursorPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: f64*, ypos: f64*)
* func [glfwGetError](#glfwGetError)(description: char** const) : i32
* func [glfwGetFramebufferSize](#glfwGetFramebufferSize)(window: [GLFWwindow\*](#GLFWwindow), width: i32*, height: i32*)
* func [glfwGetGamepadName](#glfwGetGamepadName)(jid: i32) : char const*
* func [glfwGetGamepadState](#glfwGetGamepadState)(jid: i32, state: [GLFWgamepadstate\*](#GLFWgamepadstate)) : i32
* func [glfwGetGammaRamp](#glfwGetGammaRamp)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : [GLFWgammaramp\* const](#GLFWgammaramp)
* func [glfwGetInputMode](#glfwGetInputMode)(window: [GLFWwindow\*](#GLFWwindow), mode: i32) : i32
* func [glfwGetJoystickAxes](#glfwGetJoystickAxes)(jid: i32, count: i32*) : f32 const*
* func [glfwGetJoystickButtons](#glfwGetJoystickButtons)(jid: i32, count: i32*) : u8 const*
* func [glfwGetJoystickGUID](#glfwGetJoystickGUID)(jid: i32) : char const*
* func [glfwGetJoystickHats](#glfwGetJoystickHats)(jid: i32, count: i32*) : u8 const*
* func [glfwGetJoystickName](#glfwGetJoystickName)(jid: i32) : char const*
* func [glfwGetJoystickUserPointer](#glfwGetJoystickUserPointer)(jid: i32) : [void\*](#void)
* func [glfwGetKeyName](#glfwGetKeyName)(key: i32, scancode: i32) : char const*
* func [glfwGetKeyScancode](#glfwGetKeyScancode)(key: i32) : i32
* func [glfwGetKey](#glfwGetKey)(window: [GLFWwindow\*](#GLFWwindow), key: i32) : i32
* func [glfwGetMonitorContentScale](#glfwGetMonitorContentScale)(monitor: [GLFWmonitor\*](#GLFWmonitor), xscale: f32*, yscale: f32*)
* func [glfwGetMonitorName](#glfwGetMonitorName)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : char const*
* func [glfwGetMonitorPhysicalSize](#glfwGetMonitorPhysicalSize)(monitor: [GLFWmonitor\*](#GLFWmonitor), widthMM: i32*, heightMM: i32*)
* func [glfwGetMonitorPos](#glfwGetMonitorPos)(monitor: [GLFWmonitor\*](#GLFWmonitor), xpos: i32*, ypos: i32*)
* func [glfwGetMonitorUserPointer](#glfwGetMonitorUserPointer)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : [void\*](#void)
* func [glfwGetMonitorWorkarea](#glfwGetMonitorWorkarea)(monitor: [GLFWmonitor\*](#GLFWmonitor), xpos: i32*, ypos: i32*, width: i32*, height: i32*)
* func [glfwGetMonitors](#glfwGetMonitors)(count: i32*) : [GLFWmonitor\*\*](#GLFWmonitor)
* func [glfwGetMouseButton](#glfwGetMouseButton)(window: [GLFWwindow\*](#GLFWwindow), button: i32) : i32
* func [glfwGetPrimaryMonitor](#glfwGetPrimaryMonitor)() : [GLFWmonitor\*](#GLFWmonitor)
* func [glfwGetProcAddress](#glfwGetProcAddress)(procname: char const*) : [func\(\) : void](#\_)
* func [glfwGetTime](#glfwGetTime)() : f64
* func [glfwGetTimerFrequency](#glfwGetTimerFrequency)() : u64
* func [glfwGetTimerValue](#glfwGetTimerValue)() : u64
* func [glfwGetVersionString](#glfwGetVersionString)() : char const*
* func [glfwGetVersion](#glfwGetVersion)(major: i32*, minor: i32*, rev: i32*)
* func [glfwGetVideoMode](#glfwGetVideoMode)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : [GLFWvidmode\* const](#GLFWvidmode)
* func [glfwGetVideoModes](#glfwGetVideoModes)(monitor: [GLFWmonitor\*](#GLFWmonitor), count: i32*) : [GLFWvidmode\* const](#GLFWvidmode)
* func [glfwGetWindowAttrib](#glfwGetWindowAttrib)(window: [GLFWwindow\*](#GLFWwindow), attrib: i32) : i32
* func [glfwGetWindowContentScale](#glfwGetWindowContentScale)(window: [GLFWwindow\*](#GLFWwindow), xscale: f32*, yscale: f32*)
* func [glfwGetWindowFrameSize](#glfwGetWindowFrameSize)(window: [GLFWwindow\*](#GLFWwindow), left: i32*, top: i32*, right: i32*, bottom: i32*)
* func [glfwGetWindowMonitor](#glfwGetWindowMonitor)(window: [GLFWwindow\*](#GLFWwindow)) : [GLFWmonitor\*](#GLFWmonitor)
* func [glfwGetWindowOpacity](#glfwGetWindowOpacity)(window: [GLFWwindow\*](#GLFWwindow)) : f32
* func [glfwGetWindowPos](#glfwGetWindowPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: i32*, ypos: i32*)
* func [glfwGetWindowSize](#glfwGetWindowSize)(window: [GLFWwindow\*](#GLFWwindow), width: i32*, height: i32*)
* func [glfwGetWindowUserPointer](#glfwGetWindowUserPointer)(window: [GLFWwindow\*](#GLFWwindow)) : [void\*](#void)
* func [glfwHideWindow](#glfwHideWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwIconifyWindow](#glfwIconifyWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwInitHint](#glfwInitHint)(hint: i32, value: i32)
* func [glfwInit](#glfwInit)() : i32
* func [glfwJoystickIsGamepad](#glfwJoystickIsGamepad)(jid: i32) : i32
* func [glfwJoystickPresent](#glfwJoystickPresent)(jid: i32) : i32
* func [glfwMakeContextCurrent](#glfwMakeContextCurrent)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwMaximizeWindow](#glfwMaximizeWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwPollEvents](#glfwPollEvents)()
* func [glfwPostEmptyEvent](#glfwPostEmptyEvent)()
* func [glfwRawMouseMotionSupported](#glfwRawMouseMotionSupported)() : i32
* func [glfwRequestWindowAttention](#glfwRequestWindowAttention)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwRestoreWindow](#glfwRestoreWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwSetCharCallback](#glfwSetCharCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, u32\) : void](#\_)) : [func\(struct GLFWwindow\*, u32\) : void](#\_)
* func [glfwSetCharModsCallback](#glfwSetCharModsCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, u32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, u32, i32\) : void](#\_)
* func [glfwSetClipboardString](#glfwSetClipboardString)(window: [GLFWwindow\*](#GLFWwindow), string: char const*)
* func [glfwSetCursorEnterCallback](#glfwSetCursorEnterCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)
* func [glfwSetCursorPosCallback](#glfwSetCursorPosCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)) : [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)
* func [glfwSetCursorPos](#glfwSetCursorPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: f64, ypos: f64)
* func [glfwSetCursor](#glfwSetCursor)(window: [GLFWwindow\*](#GLFWwindow), cursor: [GLFWcursor\*](#GLFWcursor))
* func [glfwSetDropCallback](#glfwSetDropCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, \[\]char const\*\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, \[\]char const\*\) : void](#\_)
* func [glfwSetErrorCallback](#glfwSetErrorCallback)(callback: [func\(i32, char const\*\) : void](#\_)) : [func\(i32, char const\*\) : void](#\_)
* func [glfwSetFramebufferSizeCallback](#glfwSetFramebufferSizeCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)
* func [glfwSetGammaRamp](#glfwSetGammaRamp)(monitor: [GLFWmonitor\*](#GLFWmonitor), ramp: [GLFWgammaramp\* const](#GLFWgammaramp))
* func [glfwSetGamma](#glfwSetGamma)(monitor: [GLFWmonitor\*](#GLFWmonitor), gamma: f32)
* func [glfwSetInputMode](#glfwSetInputMode)(window: [GLFWwindow\*](#GLFWwindow), mode: i32, value: i32)
* func [glfwSetJoystickCallback](#glfwSetJoystickCallback)(callback: [func\(i32, i32\) : void](#\_)) : [func\(i32, i32\) : void](#\_)
* func [glfwSetJoystickUserPointer](#glfwSetJoystickUserPointer)(jid: i32, pointer: [void\*](#void))
* func [glfwSetKeyCallback](#glfwSetKeyCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32, i32, i32\) : void](#\_)
* func [glfwSetMonitorCallback](#glfwSetMonitorCallback)(callback: [func\(struct GLFWmonitor\*, i32\) : void](#\_)) : [func\(struct GLFWmonitor\*, i32\) : void](#\_)
* func [glfwSetMonitorUserPointer](#glfwSetMonitorUserPointer)(monitor: [GLFWmonitor\*](#GLFWmonitor), pointer: [void\*](#void))
* func [glfwSetMouseButtonCallback](#glfwSetMouseButtonCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32, i32\) : void](#\_)
* func [glfwSetScrollCallback](#glfwSetScrollCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)) : [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)
* func [glfwSetTime](#glfwSetTime)(time: f64)
* func [glfwSetWindowAspectRatio](#glfwSetWindowAspectRatio)(window: [GLFWwindow\*](#GLFWwindow), numer: i32, denom: i32)
* func [glfwSetWindowAttrib](#glfwSetWindowAttrib)(window: [GLFWwindow\*](#GLFWwindow), attrib: i32, value: i32)
* func [glfwSetWindowCloseCallback](#glfwSetWindowCloseCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*\) : void](#\_)) : [func\(struct GLFWwindow\*\) : void](#\_)
* func [glfwSetWindowContentScaleCallback](#glfwSetWindowContentScaleCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, f32, f32\) : void](#\_)) : [func\(struct GLFWwindow\*, f32, f32\) : void](#\_)
* func [glfwSetWindowFocusCallback](#glfwSetWindowFocusCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)
* func [glfwSetWindowIcon](#glfwSetWindowIcon)(window: [GLFWwindow\*](#GLFWwindow), count: i32, images: [GLFWimage\* const](#GLFWimage))
* func [glfwSetWindowIconifyCallback](#glfwSetWindowIconifyCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)
* func [glfwSetWindowMaximizeCallback](#glfwSetWindowMaximizeCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)
* func [glfwSetWindowMonitor](#glfwSetWindowMonitor)(window: [GLFWwindow\*](#GLFWwindow), monitor: [GLFWmonitor\*](#GLFWmonitor), xpos: i32, ypos: i32, width: i32, height: i32, refreshRate: i32)
* func [glfwSetWindowOpacity](#glfwSetWindowOpacity)(window: [GLFWwindow\*](#GLFWwindow), opacity: f32)
* func [glfwSetWindowPosCallback](#glfwSetWindowPosCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)
* func [glfwSetWindowPos](#glfwSetWindowPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: i32, ypos: i32)
* func [glfwSetWindowRefreshCallback](#glfwSetWindowRefreshCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*\) : void](#\_)) : [func\(struct GLFWwindow\*\) : void](#\_)
* func [glfwSetWindowShouldClose](#glfwSetWindowShouldClose)(window: [GLFWwindow\*](#GLFWwindow), value: i32)
* func [glfwSetWindowSizeCallback](#glfwSetWindowSizeCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)
* func [glfwSetWindowSizeLimits](#glfwSetWindowSizeLimits)(window: [GLFWwindow\*](#GLFWwindow), minwidth: i32, minheight: i32, maxwidth: i32, maxheight: i32)
* func [glfwSetWindowSize](#glfwSetWindowSize)(window: [GLFWwindow\*](#GLFWwindow), width: i32, height: i32)
* func [glfwSetWindowTitle](#glfwSetWindowTitle)(window: [GLFWwindow\*](#GLFWwindow), title: char const*)
* func [glfwSetWindowUserPointer](#glfwSetWindowUserPointer)(window: [GLFWwindow\*](#GLFWwindow), pointer: [void\*](#void))
* func [glfwShowWindow](#glfwShowWindow)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwSwapBuffers](#glfwSwapBuffers)(window: [GLFWwindow\*](#GLFWwindow))
* func [glfwSwapInterval](#glfwSwapInterval)(interval: i32)
* func [glfwTerminate](#glfwTerminate)()
* func [glfwUpdateGamepadMappings](#glfwUpdateGamepadMappings)(string: char const*) : i32
* func [glfwWaitEventsTimeout](#glfwWaitEventsTimeout)(timeout: f64)
* func [glfwWaitEvents](#glfwWaitEvents)()
* func [glfwWindowHintString](#glfwWindowHintString)(hint: i32, value: char const*)
* func [glfwWindowHint](#glfwWindowHint)(hint: i32, value: i32)
* func [glfwWindowShouldClose](#glfwWindowShouldClose)(window: [GLFWwindow\*](#GLFWwindow)) : i32
* func [isalnum](#isalnum)(arg: i32) : i32
* func [isdigit](#isdigit)(arg: i32) : i32
* func [isspace](#isspace)(arg: i32) : i32
* func [malloc](#malloc)(size: u64) : [void\*](#void)
* func [mem::new<JsonArray>](#mem::new<JsonArray>)(a: [Allocator const\*](#Allocator)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)
* func [mem::new<JsonNode>](#mem::new<JsonNode>)(a: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)
* func [mem::new<JsonObject>](#mem::new<JsonObject>)(a: [Allocator const\*](#Allocator)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* func [memcpy](#memcpy)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memduplicate](#memduplicate)(p: [void const\*](#void), len: u64, a: [Allocator const\*](#Allocator)) : [void\*](#void)
* func [memmove](#memmove)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)
* func [memset](#memset)(ptr: [void\*](#void), value: i32, len: u64) : [void\*](#void)
* func [new<Allocation>](#new<Allocation>)(a: [Allocator const\*](#Allocator)) : [Allocation\*](#Allocation)
* func [new<Arena>](#new<Arena>)(a: [Allocator const\*](#Allocator)) : [Arena\*](#Arena)
* func [new](#new)(a: [Allocator const\*](#Allocator)) : [T\*](#T)
* func [printf](#printf)(s: char const*)
* func [printf](#printf)(s: char const*)
* func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)
* func [sin](#sin)(v: f64) : f64
* func [sinf](#sinf)(v: f32) : f32
* func [snprintf](#snprintf)(s: char*, n: u64, format: char const*) : i32
* func [sqrt](#sqrt)(v: f64) : f64
* func [sqrtf](#sqrtf)(v: f32) : f32
* func [strcmp](#strcmp)(a: char const*, b: char const*) : i32
* func [strcpy](#strcpy)(dest: char*, src: char const*) : char*
* func [strlen](#strlen)(str: char const*) : i32
* func [strncmp](#strncmp)(a: char const*, b: char const*, num: u64) : i32
* func [strncpy](#strncpy)(dest: char*, src: char const*, num: u64) : char*
* func [strtod](#strtod)(str: char const*, end: char**) : f64
* func [strtok](#strtok)(str: char*, delim: char const*) : char*
* func [strtol](#strtol)(str: char const*, end: char**, base: i32) : i64
* func [strtoul](#strtoul)(str: char const*, end: char**, base: i32) : u64
* func [tan](#tan)(v: f64) : f64
* func [tolower](#tolower)(arg: i32) : i32
* func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))
* func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: char const*)
* func [vfprintf](#vfprintf)(f: [FILE\*](#FILE), format: char const*, args: [libc](#libc)::[va\_list](#va\_list))
* func [vsnprintf\_s](#vsnprintf\_s)(ptr: [void\*](#void), len: u64, max: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsnprintf](#vsnprintf)(buffer: char*, len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32
* func [vsprintf\_s](#vsprintf\_s)(ptr: [void\*](#void), len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32



***
### GLAPI


### GLFW\_ACCUM\_ALPHA\_BITS


### GLFW\_ACCUM\_BLUE\_BITS


### GLFW\_ACCUM\_GREEN\_BITS


### GLFW\_ACCUM\_RED\_BITS


### GLFW\_ALPHA\_BITS


### GLFW\_ANY\_RELEASE\_BEHAVIOR


### GLFW\_API\_UNAVAILABLE


### GLFW\_ARROW\_CURSOR


### GLFW\_AUTO\_ICONIFY


### GLFW\_AUX\_BUFFERS


### GLFW\_BLUE\_BITS


### GLFW\_CENTER\_CURSOR


### GLFW\_CLIENT\_API


### GLFW\_COCOA\_CHDIR\_RESOURCES


### GLFW\_COCOA\_FRAME\_NAME


### GLFW\_COCOA\_GRAPHICS\_SWITCHING


### GLFW\_COCOA\_MENUBAR


### GLFW\_COCOA\_RETINA\_FRAMEBUFFER


### GLFW\_CONNECTED


### GLFW\_CONTEXT\_CREATION\_API


### GLFW\_CONTEXT\_NO\_ERROR


### GLFW\_CONTEXT\_RELEASE\_BEHAVIOR


### GLFW\_CONTEXT\_REVISION


### GLFW\_CONTEXT\_ROBUSTNESS


### GLFW\_CONTEXT\_VERSION\_MAJOR


### GLFW\_CONTEXT\_VERSION\_MINOR


### GLFW\_CROSSHAIR\_CURSOR


### GLFW\_CURSOR


### GLFW\_CURSOR\_DISABLED


### GLFW\_CURSOR\_HIDDEN


### GLFW\_CURSOR\_NORMAL


### GLFW\_DECORATED


### GLFW\_DEPTH\_BITS


### GLFW\_DISCONNECTED


### GLFW\_DONT\_CARE


### GLFW\_DOUBLEBUFFER


### GLFW\_EGL\_CONTEXT\_API


### GLFW\_FALSE


### GLFW\_FLOATING


### GLFW\_FOCUSED


### GLFW\_FOCUS\_ON\_SHOW


### GLFW\_FORMAT\_UNAVAILABLE


### GLFW\_GAMEPAD\_AXIS\_LAST


### GLFW\_GAMEPAD\_AXIS\_LEFT\_TRIGGER


### GLFW\_GAMEPAD\_AXIS\_LEFT\_X


### GLFW\_GAMEPAD\_AXIS\_LEFT\_Y


### GLFW\_GAMEPAD\_AXIS\_RIGHT\_TRIGGER


### GLFW\_GAMEPAD\_AXIS\_RIGHT\_X


### GLFW\_GAMEPAD\_AXIS\_RIGHT\_Y


### GLFW\_GAMEPAD\_BUTTON\_A


### GLFW\_GAMEPAD\_BUTTON\_B


### GLFW\_GAMEPAD\_BUTTON\_BACK


### GLFW\_GAMEPAD\_BUTTON\_CIRCLE


### GLFW\_GAMEPAD\_BUTTON\_CROSS


### GLFW\_GAMEPAD\_BUTTON\_DPAD\_DOWN


### GLFW\_GAMEPAD\_BUTTON\_DPAD\_LEFT


### GLFW\_GAMEPAD\_BUTTON\_DPAD\_RIGHT


### GLFW\_GAMEPAD\_BUTTON\_DPAD\_UP


### GLFW\_GAMEPAD\_BUTTON\_GUIDE


### GLFW\_GAMEPAD\_BUTTON\_LAST


### GLFW\_GAMEPAD\_BUTTON\_LEFT\_BUMPER


### GLFW\_GAMEPAD\_BUTTON\_LEFT\_THUMB


### GLFW\_GAMEPAD\_BUTTON\_RIGHT\_BUMPER


### GLFW\_GAMEPAD\_BUTTON\_RIGHT\_THUMB


### GLFW\_GAMEPAD\_BUTTON\_SQUARE


### GLFW\_GAMEPAD\_BUTTON\_START


### GLFW\_GAMEPAD\_BUTTON\_TRIANGLE


### GLFW\_GAMEPAD\_BUTTON\_X


### GLFW\_GAMEPAD\_BUTTON\_Y


### GLFW\_GREEN\_BITS


### GLFW\_HAND\_CURSOR


### GLFW\_HAT\_CENTERED


### GLFW\_HAT\_DOWN


### GLFW\_HAT\_LEFT


### GLFW\_HAT\_LEFT\_DOWN


### GLFW\_HAT\_LEFT\_UP


### GLFW\_HAT\_RIGHT


### GLFW\_HAT\_RIGHT\_DOWN


### GLFW\_HAT\_RIGHT\_UP


### GLFW\_HAT\_UP


### GLFW\_HOVERED


### GLFW\_HRESIZE\_CURSOR


### GLFW\_IBEAM\_CURSOR


### GLFW\_ICONIFIED


### GLFW\_INVALID\_ENUM


### GLFW\_INVALID\_VALUE


### GLFW\_JOYSTICK\_1


### GLFW\_JOYSTICK\_10


### GLFW\_JOYSTICK\_11


### GLFW\_JOYSTICK\_12


### GLFW\_JOYSTICK\_13


### GLFW\_JOYSTICK\_14


### GLFW\_JOYSTICK\_15


### GLFW\_JOYSTICK\_16


### GLFW\_JOYSTICK\_2


### GLFW\_JOYSTICK\_3


### GLFW\_JOYSTICK\_4


### GLFW\_JOYSTICK\_5


### GLFW\_JOYSTICK\_6


### GLFW\_JOYSTICK\_7


### GLFW\_JOYSTICK\_8


### GLFW\_JOYSTICK\_9


### GLFW\_JOYSTICK\_HAT\_BUTTONS


### GLFW\_JOYSTICK\_LAST


### GLFW\_KEY\_0


### GLFW\_KEY\_1


### GLFW\_KEY\_2


### GLFW\_KEY\_3


### GLFW\_KEY\_4


### GLFW\_KEY\_5


### GLFW\_KEY\_6


### GLFW\_KEY\_7


### GLFW\_KEY\_8


### GLFW\_KEY\_9


### GLFW\_KEY\_A


### GLFW\_KEY\_APOSTROPHE


### GLFW\_KEY\_B


### GLFW\_KEY\_BACKSLASH


### GLFW\_KEY\_BACKSPACE


### GLFW\_KEY\_C


### GLFW\_KEY\_CAPS\_LOCK


### GLFW\_KEY\_COMMA


### GLFW\_KEY\_D


### GLFW\_KEY\_DELETE


### GLFW\_KEY\_DOWN


### GLFW\_KEY\_E


### GLFW\_KEY\_END


### GLFW\_KEY\_ENTER


### GLFW\_KEY\_EQUAL


### GLFW\_KEY\_ESCAPE


### GLFW\_KEY\_F


### GLFW\_KEY\_F1


### GLFW\_KEY\_F10


### GLFW\_KEY\_F11


### GLFW\_KEY\_F12


### GLFW\_KEY\_F13


### GLFW\_KEY\_F14


### GLFW\_KEY\_F15


### GLFW\_KEY\_F16


### GLFW\_KEY\_F17


### GLFW\_KEY\_F18


### GLFW\_KEY\_F19


### GLFW\_KEY\_F2


### GLFW\_KEY\_F20


### GLFW\_KEY\_F21


### GLFW\_KEY\_F22


### GLFW\_KEY\_F23


### GLFW\_KEY\_F24


### GLFW\_KEY\_F25


### GLFW\_KEY\_F3


### GLFW\_KEY\_F4


### GLFW\_KEY\_F5


### GLFW\_KEY\_F6


### GLFW\_KEY\_F7


### GLFW\_KEY\_F8


### GLFW\_KEY\_F9


### GLFW\_KEY\_G


### GLFW\_KEY\_GRAVE\_ACCENT


### GLFW\_KEY\_H


### GLFW\_KEY\_HOME


### GLFW\_KEY\_I


### GLFW\_KEY\_INSERT


### GLFW\_KEY\_J


### GLFW\_KEY\_K


### GLFW\_KEY\_KP\_0


### GLFW\_KEY\_KP\_1


### GLFW\_KEY\_KP\_2


### GLFW\_KEY\_KP\_3


### GLFW\_KEY\_KP\_4


### GLFW\_KEY\_KP\_5


### GLFW\_KEY\_KP\_6


### GLFW\_KEY\_KP\_7


### GLFW\_KEY\_KP\_8


### GLFW\_KEY\_KP\_9


### GLFW\_KEY\_KP\_ADD


### GLFW\_KEY\_KP\_DECIMAL


### GLFW\_KEY\_KP\_DIVIDE


### GLFW\_KEY\_KP\_ENTER


### GLFW\_KEY\_KP\_EQUAL


### GLFW\_KEY\_KP\_MULTIPLY


### GLFW\_KEY\_KP\_SUBTRACT


### GLFW\_KEY\_L


### GLFW\_KEY\_LAST


### GLFW\_KEY\_LEFT


### GLFW\_KEY\_LEFT\_ALT


### GLFW\_KEY\_LEFT\_BRACKET


### GLFW\_KEY\_LEFT\_CONTROL


### GLFW\_KEY\_LEFT\_SHIFT


### GLFW\_KEY\_LEFT\_SUPER


### GLFW\_KEY\_M


### GLFW\_KEY\_MENU


### GLFW\_KEY\_MINUS


### GLFW\_KEY\_N


### GLFW\_KEY\_NUM\_LOCK


### GLFW\_KEY\_O


### GLFW\_KEY\_P


### GLFW\_KEY\_PAGE\_DOWN


### GLFW\_KEY\_PAGE\_UP


### GLFW\_KEY\_PAUSE


### GLFW\_KEY\_PERIOD


### GLFW\_KEY\_PRINT\_SCREEN


### GLFW\_KEY\_Q


### GLFW\_KEY\_R


### GLFW\_KEY\_RIGHT


### GLFW\_KEY\_RIGHT\_ALT


### GLFW\_KEY\_RIGHT\_BRACKET


### GLFW\_KEY\_RIGHT\_CONTROL


### GLFW\_KEY\_RIGHT\_SHIFT


### GLFW\_KEY\_RIGHT\_SUPER


### GLFW\_KEY\_S


### GLFW\_KEY\_SCROLL\_LOCK


### GLFW\_KEY\_SEMICOLON


### GLFW\_KEY\_SLASH


### GLFW\_KEY\_SPACE


### GLFW\_KEY\_T


### GLFW\_KEY\_TAB


### GLFW\_KEY\_U


### GLFW\_KEY\_UNKNOWN


### GLFW\_KEY\_UP


### GLFW\_KEY\_V


### GLFW\_KEY\_W


### GLFW\_KEY\_WORLD\_1


### GLFW\_KEY\_WORLD\_2


### GLFW\_KEY\_X


### GLFW\_KEY\_Y


### GLFW\_KEY\_Z


### GLFW\_LOCK\_KEY\_MODS


### GLFW\_LOSE\_CONTEXT\_ON\_RESET


### GLFW\_MAXIMIZED


### GLFW\_MOD\_ALT


### GLFW\_MOD\_CAPS\_LOCK


### GLFW\_MOD\_CONTROL


### GLFW\_MOD\_NUM\_LOCK


### GLFW\_MOD\_SHIFT


### GLFW\_MOD\_SUPER


### GLFW\_MOUSE\_BUTTON\_1


### GLFW\_MOUSE\_BUTTON\_2


### GLFW\_MOUSE\_BUTTON\_3


### GLFW\_MOUSE\_BUTTON\_4


### GLFW\_MOUSE\_BUTTON\_5


### GLFW\_MOUSE\_BUTTON\_6


### GLFW\_MOUSE\_BUTTON\_7


### GLFW\_MOUSE\_BUTTON\_8


### GLFW\_MOUSE\_BUTTON\_LAST


### GLFW\_MOUSE\_BUTTON\_LEFT


### GLFW\_MOUSE\_BUTTON\_MIDDLE


### GLFW\_MOUSE\_BUTTON\_RIGHT


### GLFW\_NATIVE\_CONTEXT\_API


### GLFW\_NOT\_INITIALIZED


### GLFW\_NO\_API


### GLFW\_NO\_CURRENT\_CONTEXT


### GLFW\_NO\_ERROR


### GLFW\_NO\_RESET\_NOTIFICATION


### GLFW\_NO\_ROBUSTNESS


### GLFW\_NO\_WINDOW\_CONTEXT


### GLFW\_OPENGL\_ANY\_PROFILE


### GLFW\_OPENGL\_API


### GLFW\_OPENGL\_COMPAT\_PROFILE


### GLFW\_OPENGL\_CORE\_PROFILE


### GLFW\_OPENGL\_DEBUG\_CONTEXT


### GLFW\_OPENGL\_ES\_API


### GLFW\_OPENGL\_FORWARD\_COMPAT


### GLFW\_OPENGL\_PROFILE


### GLFW\_OSMESA\_CONTEXT\_API


### GLFW\_OUT\_OF\_MEMORY


### GLFW\_PLATFORM\_ERROR


### GLFW\_PRESS


### GLFW\_RAW\_MOUSE\_MOTION


### GLFW\_RED\_BITS


### GLFW\_REFRESH\_RATE


### GLFW\_RELEASE


### GLFW\_RELEASE\_BEHAVIOR\_FLUSH


### GLFW\_RELEASE\_BEHAVIOR\_NONE


### GLFW\_REPEAT


### GLFW\_RESIZABLE


### GLFW\_SAMPLES


### GLFW\_SCALE\_TO\_MONITOR


### GLFW\_SRGB\_CAPABLE


### GLFW\_STENCIL\_BITS


### GLFW\_STEREO


### GLFW\_STICKY\_KEYS


### GLFW\_STICKY\_MOUSE\_BUTTONS


### GLFW\_TRANSPARENT\_FRAMEBUFFER


### GLFW\_TRUE


### GLFW\_VERSION\_MAJOR


### GLFW\_VERSION\_MINOR


### GLFW\_VERSION\_REVISION


### GLFW\_VERSION\_UNAVAILABLE


### GLFW\_VISIBLE


### GLFW\_VRESIZE\_CURSOR


### GLFW\_X11\_CLASS\_NAME


### GLFW\_X11\_INSTANCE\_NAME


### GL\_ADD


### GL\_ALIASED\_LINE\_WIDTH\_RANGE


### GL\_ALIASED\_POINT\_SIZE\_RANGE


### GL\_ALPHA


### GL\_ALPHA\_BITS


### GL\_ALPHA\_TEST


### GL\_ALWAYS


### GL\_AMBIENT


### GL\_AMBIENT\_AND\_DIFFUSE


### GL\_AND


### GL\_AND\_INVERTED


### GL\_AND\_REVERSE


### GL\_BACK


### GL\_BLEND


### GL\_BLUE\_BITS


### GL\_BYTE


### GL\_CCW


### GL\_CLAMP\_TO\_EDGE


### GL\_CLEAR


### GL\_COLOR\_ARRAY


### GL\_COLOR\_BUFFER\_BIT


### GL\_COLOR\_LOGIC\_OP


### GL\_COLOR\_MATERIAL


### GL\_COMPRESSED\_TEXTURE\_FORMATS


### GL\_CONSTANT\_ATTENUATION


### GL\_COPY


### GL\_COPY\_INVERTED


### GL\_CULL\_FACE


### GL\_CW


### GL\_DECAL


### GL\_DECR


### GL\_DEPTH\_BITS


### GL\_DEPTH\_BUFFER\_BIT


### GL\_DEPTH\_TEST


### GL\_DIFFUSE


### GL\_DITHER


### GL\_DONT\_CARE


### GL\_DST\_ALPHA


### GL\_DST\_COLOR


### GL\_EMISSION


### GL\_EQUAL


### GL\_EQUIV


### GL\_EXP


### GL\_EXP2


### GL\_EXTENSIONS


### GL\_FALSE


### GL\_FASTEST


### GL\_FIXED


### GL\_FLAT


### GL\_FLOAT


### GL\_FOG


### GL\_FOG\_COLOR


### GL\_FOG\_DENSITY


### GL\_FOG\_END


### GL\_FOG\_HINT


### GL\_FOG\_MODE


### GL\_FOG\_START


### GL\_FRONT


### GL\_FRONT\_AND\_BACK


### GL\_GEQUAL


### GL\_GREATER


### GL\_GREEN\_BITS


### GL\_IMPLEMENTATION\_COLOR\_READ\_FORMAT\_OES


### GL\_IMPLEMENTATION\_COLOR\_READ\_TYPE\_OES


### GL\_INCR


### GL\_INVALID\_ENUM


### GL\_INVALID\_OPERATION


### GL\_INVALID\_VALUE


### GL\_INVERT


### GL\_KEEP


### GL\_LEQUAL


### GL\_LESS


### GL\_LIGHT0


### GL\_LIGHT1


### GL\_LIGHT2


### GL\_LIGHT3


### GL\_LIGHT4


### GL\_LIGHT5


### GL\_LIGHT6


### GL\_LIGHT7


### GL\_LIGHTING


### GL\_LIGHT\_MODEL\_AMBIENT


### GL\_LIGHT\_MODEL\_TWO\_SIDE


### GL\_LINEAR


### GL\_LINEAR\_ATTENUATION


### GL\_LINEAR\_MIPMAP\_LINEAR


### GL\_LINEAR\_MIPMAP\_NEAREST


### GL\_LINES


### GL\_LINE\_LOOP


### GL\_LINE\_SMOOTH


### GL\_LINE\_SMOOTH\_HINT


### GL\_LINE\_STRIP


### GL\_LUMINANCE


### GL\_LUMINANCE\_ALPHA


### GL\_MAX\_ELEMENTS\_INDICES


### GL\_MAX\_ELEMENTS\_VERTICES


### GL\_MAX\_LIGHTS


### GL\_MAX\_MODELVIEW\_STACK\_DEPTH


### GL\_MAX\_PROJECTION\_STACK\_DEPTH


### GL\_MAX\_TEXTURE\_SIZE


### GL\_MAX\_TEXTURE\_STACK\_DEPTH


### GL\_MAX\_TEXTURE\_UNITS


### GL\_MAX\_VIEWPORT\_DIMS


### GL\_MODELVIEW


### GL\_MODULATE


### GL\_MULTISAMPLE


### GL\_NAND


### GL\_NEAREST


### GL\_NEAREST\_MIPMAP\_LINEAR


### GL\_NEAREST\_MIPMAP\_NEAREST


### GL\_NEVER


### GL\_NICEST


### GL\_NOOP


### GL\_NOR


### GL\_NORMALIZE


### GL\_NORMAL\_ARRAY


### GL\_NOTEQUAL


### GL\_NO\_ERROR


### GL\_NUM\_COMPRESSED\_TEXTURE\_FORMATS


### GL\_OES\_VERSION\_1\_0


### GL\_OES\_compressed\_paletted\_texture


### GL\_OES\_read\_format


### GL\_ONE


### GL\_ONE\_MINUS\_DST\_ALPHA


### GL\_ONE\_MINUS\_DST\_COLOR


### GL\_ONE\_MINUS\_SRC\_ALPHA


### GL\_ONE\_MINUS\_SRC\_COLOR


### GL\_OR


### GL\_OR\_INVERTED


### GL\_OR\_REVERSE


### GL\_OUT\_OF\_MEMORY


### GL\_PACK\_ALIGNMENT


### GL\_PALETTE4\_R5\_G6\_B5\_OES


### GL\_PALETTE4\_RGB5\_A1\_OES


### GL\_PALETTE4\_RGB8\_OES


### GL\_PALETTE4\_RGBA4\_OES


### GL\_PALETTE4\_RGBA8\_OES


### GL\_PALETTE8\_R5\_G6\_B5\_OES


### GL\_PALETTE8\_RGB5\_A1\_OES


### GL\_PALETTE8\_RGB8\_OES


### GL\_PALETTE8\_RGBA4\_OES


### GL\_PALETTE8\_RGBA8\_OES


### GL\_PERSPECTIVE\_CORRECTION\_HINT


### GL\_POINTS


### GL\_POINT\_SMOOTH


### GL\_POINT\_SMOOTH\_HINT


### GL\_POLYGON\_OFFSET\_FILL


### GL\_POLYGON\_SMOOTH\_HINT


### GL\_POSITION


### GL\_PROJECTION


### GL\_QUADRATIC\_ATTENUATION


### GL\_RED\_BITS


### GL\_RENDERER


### GL\_REPEAT


### GL\_REPLACE


### GL\_RESCALE\_NORMAL


### GL\_RGB


### GL\_RGBA


### GL\_SAMPLE\_ALPHA\_TO\_COVERAGE


### GL\_SAMPLE\_ALPHA\_TO\_ONE


### GL\_SAMPLE\_COVERAGE


### GL\_SCISSOR\_TEST


### GL\_SET


### GL\_SHININESS


### GL\_SHORT


### GL\_SMOOTH


### GL\_SMOOTH\_LINE\_WIDTH\_RANGE


### GL\_SMOOTH\_POINT\_SIZE\_RANGE


### GL\_SPECULAR


### GL\_SPOT\_CUTOFF


### GL\_SPOT\_DIRECTION


### GL\_SPOT\_EXPONENT


### GL\_SRC\_ALPHA


### GL\_SRC\_ALPHA\_SATURATE


### GL\_SRC\_COLOR


### GL\_STACK\_OVERFLOW


### GL\_STACK\_UNDERFLOW


### GL\_STENCIL\_BITS


### GL\_STENCIL\_BUFFER\_BIT


### GL\_STENCIL\_TEST


### GL\_SUBPIXEL\_BITS


### GL\_TEXTURE


### GL\_TEXTURE0


### GL\_TEXTURE1


### GL\_TEXTURE10


### GL\_TEXTURE11


### GL\_TEXTURE12


### GL\_TEXTURE13


### GL\_TEXTURE14


### GL\_TEXTURE15


### GL\_TEXTURE16


### GL\_TEXTURE17


### GL\_TEXTURE18


### GL\_TEXTURE19


### GL\_TEXTURE2


### GL\_TEXTURE20


### GL\_TEXTURE21


### GL\_TEXTURE22


### GL\_TEXTURE23


### GL\_TEXTURE24


### GL\_TEXTURE25


### GL\_TEXTURE26


### GL\_TEXTURE27


### GL\_TEXTURE28


### GL\_TEXTURE29


### GL\_TEXTURE3


### GL\_TEXTURE30


### GL\_TEXTURE31


### GL\_TEXTURE4


### GL\_TEXTURE5


### GL\_TEXTURE6


### GL\_TEXTURE7


### GL\_TEXTURE8


### GL\_TEXTURE9


### GL\_TEXTURE\_2D


### GL\_TEXTURE\_COORD\_ARRAY


### GL\_TEXTURE\_ENV


### GL\_TEXTURE\_ENV\_COLOR


### GL\_TEXTURE\_ENV\_MODE


### GL\_TEXTURE\_MAG\_FILTER


### GL\_TEXTURE\_MIN\_FILTER


### GL\_TEXTURE\_WRAP\_S


### GL\_TEXTURE\_WRAP\_T


### GL\_TRIANGLES


### GL\_TRIANGLE\_FAN


### GL\_TRIANGLE\_STRIP


### GL\_TRUE


### GL\_UNPACK\_ALIGNMENT


### GL\_UNSIGNED\_BYTE


### GL\_UNSIGNED\_SHORT


### GL\_UNSIGNED\_SHORT\_4\_4\_4\_4


### GL\_UNSIGNED\_SHORT\_5\_5\_5\_1


### GL\_UNSIGNED\_SHORT\_5\_6\_5


### GL\_VENDOR


### GL\_VERSION


### GL\_VERTEX\_ARRAY


### GL\_XOR


### GL\_ZERO


### HUGE\_VAL


### JSON\_FALSE


### JSON\_NULL


### JSON\_TRUE


### M\_PI


### SEEK\_CUR


### SEEK\_CUR


### SEEK\_END


### SEEK\_END


### SEEK\_SET


### SEEK\_SET


### ULLONG\_MAX


### WIN32\_LEAN\_AND\_MEAN


### \_\_gl\_h\_


### cAllocator


### debugAllocator


### defaultAllocator


### numOfTypeInfos


### stderr


### stdout


### typeInfos


### Allocation


struct [Allocation](#Allocation)

* addr: [void\*](#void)
* size: u64
* line: u64
* filename: char[]



### Allocator


struct [Allocator](#Allocator)

* allocFn: [func\(Allocator const\*, u64\) : void\*](#\_)
* callocFn: [func\(Allocator const\*, u64, u64\) : void\*](#\_)
* reallocFn: [func\(Allocator const\*, void\*, u64, u64\) : void\*](#\_)
* freeFn: [func\(Allocator const\*, void\*\) : void](#\_)



### Arena


struct [Arena](#Arena)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [Allocator const\*](#Allocator)
* region: u8*
* size: u64
* current: u64
* next: [Arena\*](#Arena)



### Array


struct [Array](#Array)

* length: i32
* capacity: i32
* elements: [T\*](#T)
* alloc: [Allocator const\*](#Allocator)



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



### CmdParser


struct [CmdParser](#CmdParser)

* options: [documentationGenerator](#documentationGenerator)::[Map<char const\*,Option>](#Map<char\-const\*,Option>)
* errors: char[]
* status: [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)



### CmdParserStatus


enum [CmdParserStatus](#CmdParserStatus)

* OK
* MISSING_ARGUMENT
* MISSING_REQUIRED



### DebugAllocator


struct [DebugAllocator](#DebugAllocator)

* alloc: [mem](#mem)::[Allocator](#Allocator)
* decorated: [Allocator const\*](#Allocator)
* allocations: [documentationGenerator](#documentationGenerator)::[Array<Allocation\*>](#Array<Allocation\*>)



### EnumFieldInfo


struct [EnumFieldInfo](#EnumFieldInfo)

* name: char const*
* value: i32



### EqualFn


typedef [func<K>\(K, K\) : bool](#\_) as [EqualFn](#EqualFn)


### EqualFn<K>


typedef [func\(K, K\) : bool](#\_) as [EqualFn<K>](#EqualFn<K>)


### EqualFn<char const\*>


typedef [func\(char const\*, char const\*\) : bool](#\_) as [EqualFn<char const\*>](#EqualFn<char\-const\*>)


### FILE


struct [FILE](#FILE)




### FILE


struct [FILE](#FILE)




### FieldInfo


struct [FieldInfo](#FieldInfo)

* name: char const*
* type: i64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### FileStatus


enum [FileStatus](#FileStatus)

* Ok
* FileNotFoundError
* IOError



### GLFWcharfun


typedef [func\(struct GLFWwindow\*, u32\) : void](#\_) as [GLFWcharfun](#GLFWcharfun)


### GLFWcharmodsfun


typedef [func\(struct GLFWwindow\*, u32, i32\) : void](#\_) as [GLFWcharmodsfun](#GLFWcharmodsfun)


### GLFWcursor


struct [GLFWcursor](#GLFWcursor)




### GLFWcursorenterfun


typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWcursorenterfun](#GLFWcursorenterfun)


### GLFWcursorposfun


typedef [func\(struct GLFWwindow\*, f64, f64\) : void](#\_) as [GLFWcursorposfun](#GLFWcursorposfun)


### GLFWdropfun


typedef [func\(struct GLFWwindow\*, i32, \[\]char const\*\) : void](#\_) as [GLFWdropfun](#GLFWdropfun)


### GLFWerrorfun


typedef [func\(i32, char const\*\) : void](#\_) as [GLFWerrorfun](#GLFWerrorfun)


### GLFWframebuffersizefun


typedef [func\(struct GLFWwindow\*, i32, i32\) : void](#\_) as [GLFWframebuffersizefun](#GLFWframebuffersizefun)


### GLFWgamepadstate


struct [GLFWgamepadstate](#GLFWgamepadstate)

* buttons: u8[]
* axes: f32[]



### GLFWgammaramp


struct [GLFWgammaramp](#GLFWgammaramp)

* red: u16*
* green: u16*
* blue: u16*
* size: u32



### GLFWglproc


typedef [func\(\) : void](#\_) as [GLFWglproc](#GLFWglproc)


### GLFWimage


struct [GLFWimage](#GLFWimage)

* width: i32
* height: i32
* pixels: u8*



### GLFWjoystickfun


typedef [func\(i32, i32\) : void](#\_) as [GLFWjoystickfun](#GLFWjoystickfun)


### GLFWkeyfun


typedef [func\(struct GLFWwindow\*, i32, i32, i32, i32\) : void](#\_) as [GLFWkeyfun](#GLFWkeyfun)


### GLFWmonitor


struct [GLFWmonitor](#GLFWmonitor)




### GLFWmonitorfun


typedef [func\(struct GLFWmonitor\*, i32\) : void](#\_) as [GLFWmonitorfun](#GLFWmonitorfun)


### GLFWmousebuttonfun


typedef [func\(struct GLFWwindow\*, i32, i32, i32\) : void](#\_) as [GLFWmousebuttonfun](#GLFWmousebuttonfun)


### GLFWscrollfun


typedef [func\(struct GLFWwindow\*, f64, f64\) : void](#\_) as [GLFWscrollfun](#GLFWscrollfun)


### GLFWvidmode


struct [GLFWvidmode](#GLFWvidmode)

* width: i32
* height: i32
* redBits: i32
* greenBits: i32
* blueBits: i32
* refreshRate: i32



### GLFWvkproc


typedef [func\(\) : void](#\_) as [GLFWvkproc](#GLFWvkproc)


### GLFWwindow


struct [GLFWwindow](#GLFWwindow)




### GLFWwindowclosefun


typedef [func\(struct GLFWwindow\*\) : void](#\_) as [GLFWwindowclosefun](#GLFWwindowclosefun)


### GLFWwindowcontentscalefun


typedef [func\(struct GLFWwindow\*, f32, f32\) : void](#\_) as [GLFWwindowcontentscalefun](#GLFWwindowcontentscalefun)


### GLFWwindowfocusfun


typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWwindowfocusfun](#GLFWwindowfocusfun)


### GLFWwindowiconifyfun


typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWwindowiconifyfun](#GLFWwindowiconifyfun)


### GLFWwindowmaximizefun


typedef [func\(struct GLFWwindow\*, i32\) : void](#\_) as [GLFWwindowmaximizefun](#GLFWwindowmaximizefun)


### GLFWwindowposfun


typedef [func\(struct GLFWwindow\*, i32, i32\) : void](#\_) as [GLFWwindowposfun](#GLFWwindowposfun)


### GLFWwindowrefreshfun


typedef [func\(struct GLFWwindow\*\) : void](#\_) as [GLFWwindowrefreshfun](#GLFWwindowrefreshfun)


### GLFWwindowsizefun


typedef [func\(struct GLFWwindow\*, i32, i32\) : void](#\_) as [GLFWwindowsizefun](#GLFWwindowsizefun)


### GLbitfield


typedef i32 as [GLbitfield](#GLbitfield)


### GLboolean


typedef bool as [GLboolean](#GLboolean)


### GLbyte


typedef i8 as [GLbyte](#GLbyte)


### GLclampf


typedef [gl](#gl)::GLclampf as [GLclampf](#GLclampf)


### GLclampx


typedef [gl](#gl)::GLclampx as [GLclampx](#GLclampx)


### GLenum


typedef i32 as [GLenum](#GLenum)


### GLfixed


typedef [gl](#gl)::GLfixed as [GLfixed](#GLfixed)


### GLfloat


typedef f32 as [GLfloat](#GLfloat)


### GLint


typedef i32 as [GLint](#GLint)


### GLintptrARB


typedef [gl](#gl)::GLintptrARB as [GLintptrARB](#GLintptrARB)


### GLshort


typedef i16 as [GLshort](#GLshort)


### GLsizei


typedef i64 as [GLsizei](#GLsizei)


### GLsizeiptrARB


typedef [gl](#gl)::GLsizeiptrARB as [GLsizeiptrARB](#GLsizeiptrARB)


### GLubyte


typedef u8 as [GLubyte](#GLubyte)


### GLuint


typedef u32 as [GLuint](#GLuint)


### GLushort


typedef u16 as [GLushort](#GLushort)


### GLvoid


typedef [gl](#gl)::GLvoid as [GLvoid](#GLvoid)


### GenericInfo


struct [GenericInfo](#GenericInfo)

* args: char const**
* numOfArgs: i32



### HashFn


typedef [func<K>\(K\) : u32](#\_) as [HashFn](#HashFn)


### HashFn<K>


typedef [func\(K\) : u32](#\_) as [HashFn<K>](#HashFn<K>)


### HashFn<char const\*>


typedef [func\(char const\*\) : u32](#\_) as [HashFn<char const\*>](#HashFn<char\-const\*>)


### JsonArray


typedef [documentationGenerator](#documentationGenerator)::[Array<JsonNode\*>](#Array<JsonNode\*>) as [JsonArray](#JsonArray)


### JsonNode


struct [JsonNode](#JsonNode)

* alloc: [Allocator const\*](#Allocator)
* type: [json](#json)::[JsonType](#JsonType)
* value: [json](#json)::[JsonValue](#JsonValue)



### JsonObject


typedef [documentationGenerator](#documentationGenerator)::[Map<char const\*,JsonNode\*>](#Map<char\-const\*,JsonNode\*>) as [JsonObject](#JsonObject)


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
* strValue: char const*
* objValue: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)
* arrayValue: [Array<JsonNode\*>\*](#Array<JsonNode\*>)



### Map


struct [Map](#Map)

* length: i32
* capacity: i32
* hashFn: [func\(K\) : u32](#\_)
* equalFn: [func\(K, K\) : bool](#\_)
* alloc: [Allocator const\*](#Allocator)
* keys: [K\*](#K)
* values: [V\*](#V)
* emptyValue: [V](#V)



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
* emptyValue: [cmdline](#cmdline)::[Option](#Option)



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



### MapEntry


struct [MapEntry](#MapEntry)

* key: [K](#K)
* value: [V](#V)



### MapEntry<K,V>


struct [MapEntry<K,V>](#MapEntry<K,V>)

* key: [K](#K)
* value: [V](#V)



### MapEntry<char const\*,JsonNode\*>


struct [MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)

* key: char const*
* value: [JsonNode\*](#JsonNode)



### MapIterator


struct [MapIterator](#MapIterator)

* m: [Map<K,V>\*](#Map<K,V>)
* it: i32
* count: i32



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



### Modifiers


enum [Modifiers](#Modifiers)

* Using



### Option


struct [Option](#Option)

* name: char const*
* shortName: char
* description: char const*
* value: char const*
* defaultValue: char const*
* flags: i32



### OptionFlag


enum [OptionFlag](#OptionFlag)

* HAS_ARGUMENT
* IS_REQUIRED
* IS_USED



### ParamInfo


struct [ParamInfo](#ParamInfo)

* genInfo: [type](#type)::[GenericInfo](#GenericInfo)
* name: char const*
* type: i64
* modifiers: [type](#type)::[Modifiers](#Modifiers)



### Parser


struct [Parser](#Parser)

* alloc: [Allocator const\*](#Allocator)
* status: [json](#json)::[ParserStatus](#ParserStatus)
* errorMsg: char[]
* token: [json](#json)::[Token](#Token)
* buffer: [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)
* stream: char const*
* lineStart: char const*



### ParserStatus


enum [ParserStatus](#ParserStatus)

* OK
* WARNING
* ERROR



### StringBuffer


struct [StringBuffer](#StringBuffer)

* buffer: char*
* length: i32
* capacity: i32
* alloc: [Allocator const\*](#Allocator)



### StringView


struct [StringView](#StringView)

* buffer: char const*
* length: i32



### TypeInfo


struct [TypeInfo](#TypeInfo)

* kind: [type](#type)::[TypeKind](#TypeKind)
* name: char const*
* id: i64
* <anonymous-union-0>: [type](#type)::[<anonymous\-union\-0>](#<anonymous\-union\-0>)



### TypeKind


enum [TypeKind](#TypeKind)

* Bool
* Char
* I8
* U8
* I16
* U16
* I32
* U32
* I64
* U64
* F32
* F64
* Str
* Array
* Ptr
* Null
* FuncPtr
* Struct
* Func
* Enum
* Union
* Void
* MaxTypeKind



### \_GLfuncptr


typedef [gl](#gl)::_GLfuncptr as [\_GLfuncptr](#\_GLfuncptr)


### va\_list


struct [va\_list](#va\_list)




### ABS


func [ABS](#ABS)(a: f32) : f32


### Allocator\_alloc


func [Allocator\_alloc](#Allocator\_alloc)(a: [Allocator const\*](#Allocator), size: u64) : [void\*](#void)


### Allocator\_calloc


func [Allocator\_calloc](#Allocator\_calloc)(a: [Allocator const\*](#Allocator), num: u64, size: u64) : [void\*](#void)


### Allocator\_free


func [Allocator\_free](#Allocator\_free)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void))


### Allocator\_realloc


func [Allocator\_realloc](#Allocator\_realloc)(a: [Allocator const\*](#Allocator), ptr: [void\*](#void), oldsize: u64, newsize: u64) : [void\*](#void)


### Arena\_free


func [Arena\_free](#Arena\_free)(arena: [Arena\*](#Arena))


### Arena\_init


func [Arena\_init](#Arena\_init)(arena: [Arena\*](#Arena), size: u64, alloc: [Allocator const\*](#Allocator))


### Arena\_malloc


func [Arena\_malloc](#Arena\_malloc)(arena: [Arena\*](#Arena), size: u64) : [void\*](#void)


### Array\_add


func [Array\_add](#Array\_add)(a: [Array<T>\*](#Array<T>), element: [T](#T))


### Array\_add<Allocation\*>


func [Array\_add<Allocation\*>](#Array\_add<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), element: [Allocation\*](#Allocation))


### Array\_add<JsonNode\*>


func [Array\_add<JsonNode\*>](#Array\_add<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), element: [JsonNode\*](#JsonNode))


### Array\_addAll


func [Array\_addAll](#Array\_addAll)(a: [Array<T>\*](#Array<T>), other: [Array<T>\*](#Array<T>))


### Array\_clear


func [Array\_clear](#Array\_clear)(a: [Array<T>\*](#Array<T>))


### Array\_empty


func [Array\_empty](#Array\_empty)(a: [Array<T>\*](#Array<T>)) : bool


### Array\_first


func [Array\_first](#Array\_first)(a: [Array<T>\*](#Array<T>)) : [T](#T)


### Array\_forEach


func [Array\_forEach](#Array\_forEach)(a: [Array<T>\*](#Array<T>), fn: [func<T>\(T\) : bool](#\_))


### Array\_free


func [Array\_free](#Array\_free)(a: [Array<T>\*](#Array<T>))


### Array\_free<Allocation\*>


func [Array\_free<Allocation\*>](#Array\_free<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>))


### Array\_free<JsonNode\*>


func [Array\_free<JsonNode\*>](#Array\_free<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>))


### Array\_get


func [Array\_get](#Array\_get)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)


### Array\_get<Allocation\*>


func [Array\_get<Allocation\*>](#Array\_get<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)


### Array\_get<JsonNode\*>


func [Array\_get<JsonNode\*>](#Array\_get<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), index: i32) : [JsonNode\*](#JsonNode)


### Array\_init


func [Array\_init](#Array\_init)(a: [Array<T>\*](#Array<T>), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### Array\_init<Allocation\*>


func [Array\_init<Allocation\*>](#Array\_init<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### Array\_init<JsonNode\*>


func [Array\_init<JsonNode\*>](#Array\_init<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### Array\_last


func [Array\_last](#Array\_last)(a: [Array<T>\*](#Array<T>)) : [T](#T)


### Array\_pop


func [Array\_pop](#Array\_pop)(a: [Array<T>\*](#Array<T>)) : [T](#T)


### Array\_push


func [Array\_push](#Array\_push)(a: [Array<T>\*](#Array<T>), element: [T](#T))


### Array\_removeAt


func [Array\_removeAt](#Array\_removeAt)(a: [Array<T>\*](#Array<T>), index: i32) : [T](#T)


### Array\_removeAt<Allocation\*>


func [Array\_removeAt<Allocation\*>](#Array\_removeAt<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>), index: i32) : [Allocation\*](#Allocation)


### Array\_set


func [Array\_set](#Array\_set)(a: [Array<T>\*](#Array<T>), index: i32, element: [T](#T))


### Array\_size


func [Array\_size](#Array\_size)(a: [Array<T>\*](#Array<T>)) : i32


### Array\_size<Allocation\*>


func [Array\_size<Allocation\*>](#Array\_size<Allocation\*>)(a: [Array<Allocation\*>\*](#Array<Allocation\*>)) : i32


### Array\_size<JsonNode\*>


func [Array\_size<JsonNode\*>](#Array\_size<JsonNode\*>)(a: [Array<JsonNode\*>\*](#Array<JsonNode\*>)) : i32


### CmdParser\_addOption


func [CmdParser\_addOption](#CmdParser\_addOption)(p: [CmdParser\*](#CmdParser), longName: char const*, shortName: char, description: char const*, flags: i32, defaultValue: char const*)


### CmdParser\_getOption


func [CmdParser\_getOption](#CmdParser\_getOption)(p: [CmdParser\*](#CmdParser), longName: char const*) : [cmdline](#cmdline)::[Option](#Option)


### CmdParser\_init


func [CmdParser\_init](#CmdParser\_init)(p: [CmdParser\*](#CmdParser))


### CmdParser\_parse


func [CmdParser\_parse](#CmdParser\_parse)(p: [CmdParser\*](#CmdParser), argc: i32, argv: char**) : [cmdline](#cmdline)::[CmdParserStatus](#CmdParserStatus)


### CreateJsonArray


func [CreateJsonArray](#CreateJsonArray)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonNumber


func [CreateJsonNumber](#CreateJsonNumber)(value: f64, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonObject


func [CreateJsonObject](#CreateJsonObject)(alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonString


func [CreateJsonString](#CreateJsonString)(str: char const*, len: i32, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### CreateJsonStringNoDup


func [CreateJsonStringNoDup](#CreateJsonStringNoDup)(str: char const*, alloc: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### DebugAllocator\_free


func [DebugAllocator\_free](#DebugAllocator\_free)(d: [DebugAllocator\*](#DebugAllocator))


### DebugAllocator\_init


func [DebugAllocator\_init](#DebugAllocator\_init)(d: [DebugAllocator\*](#DebugAllocator), alloc: [Allocator const\*](#Allocator))


### DebugAllocator\_malloc


func [DebugAllocator\_malloc](#DebugAllocator\_malloc)(d: [DebugAllocator\*](#DebugAllocator), size: u64, file: char const*, line: u64) : [void\*](#void)


### DebugAllocator\_report


func [DebugAllocator\_report](#DebugAllocator\_report)(d: [DebugAllocator\*](#DebugAllocator))


### FileLength


func [FileLength](#FileLength)(fileName: char const*) : i64


### FileStatusAsStr


func [FileStatusAsStr](#FileStatusAsStr)(e: [io](#io)::[FileStatus](#FileStatus)) : char const*


### JsonNode\_add


func [JsonNode\_add](#JsonNode\_add)(node: [JsonNode\*](#JsonNode), n: [JsonNode\*](#JsonNode))


### JsonNode\_asArray


func [JsonNode\_asArray](#JsonNode\_asArray)(node: [JsonNode\*](#JsonNode)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)


### JsonNode\_asBool


func [JsonNode\_asBool](#JsonNode\_asBool)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_asFloat


func [JsonNode\_asFloat](#JsonNode\_asFloat)(node: [JsonNode\*](#JsonNode)) : f64


### JsonNode\_asInt


func [JsonNode\_asInt](#JsonNode\_asInt)(node: [JsonNode\*](#JsonNode)) : i32


### JsonNode\_asLong


func [JsonNode\_asLong](#JsonNode\_asLong)(node: [JsonNode\*](#JsonNode)) : i64


### JsonNode\_asNumber


func [JsonNode\_asNumber](#JsonNode\_asNumber)(node: [JsonNode\*](#JsonNode)) : f64


### JsonNode\_asObject


func [JsonNode\_asObject](#JsonNode\_asObject)(node: [JsonNode\*](#JsonNode)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)


### JsonNode\_asString


func [JsonNode\_asString](#JsonNode\_asString)(node: [JsonNode\*](#JsonNode)) : char const*


### JsonNode\_at


func [JsonNode\_at](#JsonNode\_at)(node: [JsonNode\*](#JsonNode), index: i32) : [JsonNode\*](#JsonNode)


### JsonNode\_free


func [JsonNode\_free](#JsonNode\_free)(node: [JsonNode\*](#JsonNode))


### JsonNode\_get


func [JsonNode\_get](#JsonNode\_get)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)


### JsonNode\_getArray


func [JsonNode\_getArray](#JsonNode\_getArray)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)


### JsonNode\_getBool


func [JsonNode\_getBool](#JsonNode\_getBool)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: bool) : bool


### JsonNode\_getFloat


func [JsonNode\_getFloat](#JsonNode\_getFloat)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: f64) : f64


### JsonNode\_getInt


func [JsonNode\_getInt](#JsonNode\_getInt)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i32) : i32


### JsonNode\_getLong


func [JsonNode\_getLong](#JsonNode\_getLong)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: i64) : i64


### JsonNode\_getObject


func [JsonNode\_getObject](#JsonNode\_getObject)(node: [JsonNode\*](#JsonNode), key: char const*) : [JsonNode\*](#JsonNode)


### JsonNode\_getStr


func [JsonNode\_getStr](#JsonNode\_getStr)(node: [JsonNode\*](#JsonNode), key: char const*, defaultValue: char const*) : char const*


### JsonNode\_getStrCopy


func [JsonNode\_getStrCopy](#JsonNode\_getStrCopy)(node: [JsonNode\*](#JsonNode), key: char const*, str: char*, len: i32) : char*


### JsonNode\_isArray


func [JsonNode\_isArray](#JsonNode\_isArray)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isBool


func [JsonNode\_isBool](#JsonNode\_isBool)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isFalse


func [JsonNode\_isFalse](#JsonNode\_isFalse)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isNull


func [JsonNode\_isNull](#JsonNode\_isNull)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isNumber


func [JsonNode\_isNumber](#JsonNode\_isNumber)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isObject


func [JsonNode\_isObject](#JsonNode\_isObject)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isString


func [JsonNode\_isString](#JsonNode\_isString)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_isTrue


func [JsonNode\_isTrue](#JsonNode\_isTrue)(node: [JsonNode\*](#JsonNode)) : bool


### JsonNode\_print


func [JsonNode\_print](#JsonNode\_print)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer)) : char const*


### JsonNode\_put


func [JsonNode\_put](#JsonNode\_put)(node: [JsonNode\*](#JsonNode), key: char const*, n: [JsonNode\*](#JsonNode), len: i32)


### JsonNode\_size


func [JsonNode\_size](#JsonNode\_size)(node: [JsonNode\*](#JsonNode)) : i32


### JsonTypeAsStr


func [JsonTypeAsStr](#JsonTypeAsStr)(e: [json](#json)::[JsonType](#JsonType)) : char const*


### MAX


func [MAX](#MAX)(a: [T](#T), b: [T](#T)) : [T](#T)


### MIN


func [MIN](#MIN)(a: [T](#T), b: [T](#T)) : [T](#T)


### MIN<i32>


func [MIN<i32>](#MIN<i32>)(a: i32, b: i32) : i32


### MapIterator\_hasNext


func [MapIterator\_hasNext](#MapIterator\_hasNext)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : bool


### MapIterator\_hasNext<char const\*,JsonNode\*>


func [MapIterator\_hasNext<char const\*,JsonNode\*>](#MapIterator\_hasNext<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : bool


### MapIterator\_next


func [MapIterator\_next](#MapIterator\_next)(iter: [MapIterator<K,V>\*](#MapIterator<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<K,V>](#MapEntry<K,V>)


### MapIterator\_next<char const\*,JsonNode\*>


func [MapIterator\_next<char const\*,JsonNode\*>](#MapIterator\_next<char\-const\*,JsonNode\*>)(iter: [MapIterator<char const\*,JsonNode\*>\*](#MapIterator<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapEntry<char const\*,JsonNode\*>](#MapEntry<char\-const\*,JsonNode\*>)


### Map\_contains


func [Map\_contains](#Map\_contains)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : bool


### Map\_contains<char const\*,Option>


func [Map\_contains<char const\*,Option>](#Map\_contains<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : bool


### Map\_empty


func [Map\_empty](#Map\_empty)(m: [Map<K,V>\*](#Map<K,V>)) : bool


### Map\_free


func [Map\_free](#Map\_free)(m: [Map<K,V>\*](#Map<K,V>))


### Map\_free<char const\*,JsonNode\*>


func [Map\_free<char const\*,JsonNode\*>](#Map\_free<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>))


### Map\_free<char const\*,Option>


func [Map\_free<char const\*,Option>](#Map\_free<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>))


### Map\_get


func [Map\_get](#Map\_get)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_get<char const\*,JsonNode\*>


func [Map\_get<char const\*,JsonNode\*>](#Map\_get<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*) : [JsonNode\*](#JsonNode)


### Map\_get<char const\*,Option>


func [Map\_get<char const\*,Option>](#Map\_get<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*) : [cmdline](#cmdline)::[Option](#Option)


### Map\_init


func [Map\_init](#Map\_init)(m: [Map<K,V>\*](#Map<K,V>), emptyValue: [V](#V), initialSize: i32, hashFn: [func\(K\) : u32](#\_), equalFn: [func\(K, K\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_init<char const\*,JsonNode\*>


func [Map\_init<char const\*,JsonNode\*>](#Map\_init<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), emptyValue: [JsonNode\*](#JsonNode), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_init<char const\*,Option>


func [Map\_init<char const\*,Option>](#Map\_init<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), emptyValue: [cmdline](#cmdline)::[Option](#Option), initialSize: i32, hashFn: [func\(char const\*\) : u32](#\_), equalFn: [func\(char const\*, char const\*\) : bool](#\_), alloc: [Allocator const\*](#Allocator))


### Map\_iter


func [Map\_iter](#Map\_iter)(m: [Map<K,V>\*](#Map<K,V>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<K,V>](#MapIterator<K,V>)


### Map\_iter<char const\*,JsonNode\*>


func [Map\_iter<char const\*,JsonNode\*>](#Map\_iter<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : [documentationGenerator](#documentationGenerator)::[MapIterator<char const\*,JsonNode\*>](#MapIterator<char\-const\*,JsonNode\*>)


### Map\_put


func [Map\_put](#Map\_put)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K), value: [V](#V))


### Map\_put<char const\*,JsonNode\*>


func [Map\_put<char const\*,JsonNode\*>](#Map\_put<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>), key: char const*, value: [JsonNode\*](#JsonNode))


### Map\_put<char const\*,Option>


func [Map\_put<char const\*,Option>](#Map\_put<char\-const\*,Option>)(m: [Map<char const\*,Option>\*](#Map<char\-const\*,Option>), key: char const*, value: [cmdline](#cmdline)::[Option](#Option))


### Map\_remove


func [Map\_remove](#Map\_remove)(m: [Map<K,V>\*](#Map<K,V>), key: [K](#K)) : [V](#V)


### Map\_size


func [Map\_size](#Map\_size)(m: [Map<K,V>\*](#Map<K,V>)) : i32


### Map\_size<char const\*,JsonNode\*>


func [Map\_size<char const\*,JsonNode\*>](#Map\_size<char\-const\*,JsonNode\*>)(m: [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)) : i32


### Parser\_free


func [Parser\_free](#Parser\_free)(p: [Parser\*](#Parser))


### Parser\_hasError


func [Parser\_hasError](#Parser\_hasError)(p: [Parser\*](#Parser)) : bool


### Parser\_init


func [Parser\_init](#Parser\_init)(p: [Parser\*](#Parser), alloc: [Allocator const\*](#Allocator))


### Parser\_parseJson


func [Parser\_parseJson](#Parser\_parseJson)(p: [Parser\*](#Parser), buffer: char const*) : [JsonNode\*](#JsonNode)


### PrintJson


func [PrintJson](#PrintJson)(node: [JsonNode\*](#JsonNode), buf: [StringBuffer\*](#StringBuffer))


### PtrEqualFn


func [PtrEqualFn](#PtrEqualFn)(a: [K](#K), b: [K](#K)) : bool


### PtrEqualFn<char const\*>


func [PtrEqualFn<char const\*>](#PtrEqualFn<char\-const\*>)(a: char const*, b: char const*) : bool


### ReadFile


func [ReadFile](#ReadFile)(fileName: char const*, data: char**, alloc: [Allocator const\*](#Allocator)) : [io](#io)::[FileStatus](#FileStatus)


### StrEqualFn


func [StrEqualFn](#StrEqualFn)(a: char const*, b: char const*) : bool


### StrHashFn


func [StrHashFn](#StrHashFn)(str: char const*) : u32


### StrMap


func [StrMap](#StrMap)(emptyValue: [V](#V), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,V>](#Map<char\-const\*,V>)


### StrMap<Option>


func [StrMap<Option>](#StrMap<Option>)(emptyValue: [cmdline](#cmdline)::[Option](#Option), initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [documentationGenerator](#documentationGenerator)::[Map<char const\*,Option>](#Map<char\-const\*,Option>)


### StringBufferInit


func [StringBufferInit](#StringBufferInit)(initialSize: i32, alloc: [Allocator const\*](#Allocator)) : [string\_buffer](#string\_buffer)::[StringBuffer](#StringBuffer)


### StringBuffer\_append


func [StringBuffer\_append](#StringBuffer\_append)(b: [StringBuffer\*](#StringBuffer), format: char const*) : i32


### StringBuffer\_appendChar


func [StringBuffer\_appendChar](#StringBuffer\_appendChar)(b: [StringBuffer\*](#StringBuffer), c: char) : i32


### StringBuffer\_appendFloat


func [StringBuffer\_appendFloat](#StringBuffer\_appendFloat)(b: [StringBuffer\*](#StringBuffer), f: f64) : i32


### StringBuffer\_appendI32


func [StringBuffer\_appendI32](#StringBuffer\_appendI32)(b: [StringBuffer\*](#StringBuffer), i: i32) : i32


### StringBuffer\_appendI64


func [StringBuffer\_appendI64](#StringBuffer\_appendI64)(b: [StringBuffer\*](#StringBuffer), i: i64) : i32


### StringBuffer\_appendStr


func [StringBuffer\_appendStr](#StringBuffer\_appendStr)(b: [StringBuffer\*](#StringBuffer), str: char*, len: i32)


### StringBuffer\_appendU32


func [StringBuffer\_appendU32](#StringBuffer\_appendU32)(b: [StringBuffer\*](#StringBuffer), i: u32) : i32


### StringBuffer\_appendU64


func [StringBuffer\_appendU64](#StringBuffer\_appendU64)(b: [StringBuffer\*](#StringBuffer), i: u64) : i32


### StringBuffer\_asStringView


func [StringBuffer\_asStringView](#StringBuffer\_asStringView)(b: [StringBuffer\*](#StringBuffer)) : [string\_view](#string\_view)::[StringView](#StringView)


### StringBuffer\_cStr


func [StringBuffer\_cStr](#StringBuffer\_cStr)(b: [StringBuffer\*](#StringBuffer)) : char const*


### StringBuffer\_clear


func [StringBuffer\_clear](#StringBuffer\_clear)(b: [StringBuffer\*](#StringBuffer))


### StringBuffer\_contains


func [StringBuffer\_contains](#StringBuffer\_contains)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : bool


### StringBuffer\_copyTo


func [StringBuffer\_copyTo](#StringBuffer\_copyTo)(b: [StringBuffer\*](#StringBuffer), buf: char*, len: i32, addZero: bool) : i32


### StringBuffer\_delete


func [StringBuffer\_delete](#StringBuffer\_delete)(b: [StringBuffer\*](#StringBuffer), start: i32, end: i32)


### StringBuffer\_empty


func [StringBuffer\_empty](#StringBuffer\_empty)(b: [StringBuffer\*](#StringBuffer)) : bool


### StringBuffer\_forEach


func [StringBuffer\_forEach](#StringBuffer\_forEach)(b: [StringBuffer\*](#StringBuffer), fn: [func\(char\) : bool](#\_))


### StringBuffer\_free


func [StringBuffer\_free](#StringBuffer\_free)(b: [StringBuffer\*](#StringBuffer))


### StringBuffer\_get


func [StringBuffer\_get](#StringBuffer\_get)(b: [StringBuffer\*](#StringBuffer), index: i32) : char


### StringBuffer\_indexOf


func [StringBuffer\_indexOf](#StringBuffer\_indexOf)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32) : i32


### StringBuffer\_indexOfAt


func [StringBuffer\_indexOfAt](#StringBuffer\_indexOfAt)(b: [StringBuffer\*](#StringBuffer), str: char const*, len: i32, fromIndex: i32) : i32


### StringBuffer\_init


func [StringBuffer\_init](#StringBuffer\_init)(b: [StringBuffer\*](#StringBuffer), initialSize: i32, alloc: [Allocator const\*](#Allocator))


### StringBuffer\_insert


func [StringBuffer\_insert](#StringBuffer\_insert)(b: [StringBuffer\*](#StringBuffer), index: i32, format: char const*) : i32


### StringBuffer\_size


func [StringBuffer\_size](#StringBuffer\_size)(b: [StringBuffer\*](#StringBuffer)) : i32


### StringViewInit


func [StringViewInit](#StringViewInit)(str: char const*, len: i32) : [string\_view](#string\_view)::[StringView](#StringView)


### StringView\_clear


func [StringView\_clear](#StringView\_clear)(b: [string\_view](#string\_view)::[StringView](#StringView))


### StringView\_contains


func [StringView\_contains](#StringView\_contains)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool


### StringView\_copyTo


func [StringView\_copyTo](#StringView\_copyTo)(b: [string\_view](#string\_view)::[StringView](#StringView), buf: char*, len: i32, addZero: bool) : i32


### StringView\_empty


func [StringView\_empty](#StringView\_empty)(b: [string\_view](#string\_view)::[StringView](#StringView)) : bool


### StringView\_endsWith


func [StringView\_endsWith](#StringView\_endsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), suffix: char const*, len: i32) : bool


### StringView\_equals


func [StringView\_equals](#StringView\_equals)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : bool


### StringView\_forEach


func [StringView\_forEach](#StringView\_forEach)(b: [string\_view](#string\_view)::[StringView](#StringView), fn: [func\(char\) : bool](#\_))


### StringView\_get


func [StringView\_get](#StringView\_get)(b: [string\_view](#string\_view)::[StringView](#StringView), index: i32) : char


### StringView\_indexOf


func [StringView\_indexOf](#StringView\_indexOf)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32) : i32


### StringView\_indexOfAt


func [StringView\_indexOfAt](#StringView\_indexOfAt)(b: [string\_view](#string\_view)::[StringView](#StringView), str: char const*, len: i32, fromIndex: i32) : i32


### StringView\_size


func [StringView\_size](#StringView\_size)(b: [string\_view](#string\_view)::[StringView](#StringView)) : i32


### StringView\_startsWith


func [StringView\_startsWith](#StringView\_startsWith)(b: [string\_view](#string\_view)::[StringView](#StringView), prefix: char const*, len: i32, fromIndex: i32) : bool


### StringView\_substring


func [StringView\_substring](#StringView\_substring)(b: [string\_view](#string\_view)::[StringView](#StringView), start: i32, end: i32) : [string\_view](#string\_view)::[StringView](#StringView)


### TypeKindAsStr


func [TypeKindAsStr](#TypeKindAsStr)(e: [type](#type)::[TypeKind](#TypeKind)) : char const*


### WriteBytes


func [WriteBytes](#WriteBytes)(fp: [FILE\*](#FILE), buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### WriteFile


func [WriteFile](#WriteFile)(fileName: char const*, buffer: char const*, len: u64) : [io](#io)::[FileStatus](#FileStatus)


### asinf


func [asinf](#asinf)(v: f32) : f32


### assert


func [assert](#assert)(e: bool)


### c::MIN<u64>


func [c::MIN<u64>](#c::MIN<u64>)(a: u64, b: u64) : u64


### calloc


func [calloc](#calloc)(num: u64, size: u64) : [void\*](#void)


### cos


func [cos](#cos)(v: f64) : f64


### cosf


func [cosf](#cosf)(v: f32) : f32


### exit


func [exit](#exit)(code: i32)


### fabs


func [fabs](#fabs)(v: f32) : f32


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### fclose


func [fclose](#fclose)(stream: [FILE\*](#FILE))


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### ferror


func [ferror](#ferror)(stream: [FILE\*](#FILE)) : i32


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fgets


func [fgets](#fgets)(buf: char*, n: i64, stream: [FILE\*](#FILE)) : char*


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fopen


func [fopen](#fopen)(fileName: char const*, openType: char const*) : [FILE\*](#FILE)


### fputs


func [fputs](#fputs)(format: char const*, f: [FILE\*](#FILE))


### fread


func [fread](#fread)(buf: char*, size: u64, n: i64, stream: [FILE\*](#FILE)) : i64


### fread


func [fread](#fread)(buf: [void\*](#void), size: u64, n: u64, stream: [FILE\*](#FILE)) : u64


### free


func [free](#free)(ptr: [void\*](#void))


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### fseek


func [fseek](#fseek)(stream: [FILE\*](#FILE), offset: i64, whence: i32) : i32


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### ftell


func [ftell](#ftell)(stream: [FILE\*](#FILE)) : i64


### fwrite


func [fwrite](#fwrite)(buf: [void const\*](#void), sizeOfElements: u64, numOfElements: u64, stream: [FILE\*](#FILE)) : u64


### getTypeInfo


func [getTypeInfo](#getTypeInfo)(id: i64) : [TypeInfo\*](#TypeInfo)


### glActiveTexture


func [glActiveTexture](#glActiveTexture)(_0: i32)


### glAlphaFunc


func [glAlphaFunc](#glAlphaFunc)(_0: i32, _1: [gl](#gl)::GLclampf)


### glAlphaFuncx


func [glAlphaFuncx](#glAlphaFuncx)(_0: i32, _1: [gl](#gl)::GLclampx)


### glBindTexture


func [glBindTexture](#glBindTexture)(_0: i32, _1: u32)


### glBlendFunc


func [glBlendFunc](#glBlendFunc)(_0: i32, _1: i32)


### glClear


func [glClear](#glClear)(_0: i32)


### glClearColor


func [glClearColor](#glClearColor)(_0: [gl](#gl)::GLclampf, _1: [gl](#gl)::GLclampf, _2: [gl](#gl)::GLclampf, _3: [gl](#gl)::GLclampf)


### glClearColorx


func [glClearColorx](#glClearColorx)(_0: [gl](#gl)::GLclampx, _1: [gl](#gl)::GLclampx, _2: [gl](#gl)::GLclampx, _3: [gl](#gl)::GLclampx)


### glClearDepthf


func [glClearDepthf](#glClearDepthf)(_0: [gl](#gl)::GLclampf)


### glClearDepthx


func [glClearDepthx](#glClearDepthx)(_0: [gl](#gl)::GLclampx)


### glClearStencil


func [glClearStencil](#glClearStencil)(_0: i32)


### glClientActiveTexture


func [glClientActiveTexture](#glClientActiveTexture)(_0: i32)


### glColor4f


func [glColor4f](#glColor4f)(_0: f32, _1: f32, _2: f32, _3: f32)


### glColor4x


func [glColor4x](#glColor4x)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed)


### glColorMask


func [glColorMask](#glColorMask)(_0: bool, _1: bool, _2: bool, _3: bool)


### glColorPointer


func [glColorPointer](#glColorPointer)(_0: i32, _1: i32, _2: i64, _3: GLvoid const*)


### glCompressedTexImage2D


func [glCompressedTexImage2D](#glCompressedTexImage2D)(_0: i32, _1: i32, _2: i32, _3: i64, _4: i64, _5: i32, _6: i64, _7: GLvoid const*)


### glCompressedTexSubImage2D


func [glCompressedTexSubImage2D](#glCompressedTexSubImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i64, _5: i64, _6: i32, _7: i64, _8: GLvoid const*)


### glCopyTexImage2D


func [glCopyTexImage2D](#glCopyTexImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i32, _5: i64, _6: i64, _7: i32)


### glCopyTexSubImage2D


func [glCopyTexSubImage2D](#glCopyTexSubImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i32, _5: i32, _6: i64, _7: i64)


### glCullFace


func [glCullFace](#glCullFace)(_0: i32)


### glDeleteTextures


func [glDeleteTextures](#glDeleteTextures)(_0: i64, _1: u32 const*)


### glDepthFunc


func [glDepthFunc](#glDepthFunc)(_0: i32)


### glDepthMask


func [glDepthMask](#glDepthMask)(_0: bool)


### glDepthRangef


func [glDepthRangef](#glDepthRangef)(_0: [gl](#gl)::GLclampf, _1: [gl](#gl)::GLclampf)


### glDepthRangex


func [glDepthRangex](#glDepthRangex)(_0: [gl](#gl)::GLclampx, _1: [gl](#gl)::GLclampx)


### glDisable


func [glDisable](#glDisable)(_0: i32)


### glDisableClientState


func [glDisableClientState](#glDisableClientState)(_0: i32)


### glDrawArrays


func [glDrawArrays](#glDrawArrays)(_0: i32, _1: i32, _2: i64)


### glDrawElements


func [glDrawElements](#glDrawElements)(_0: i32, _1: i64, _2: i32, _3: GLvoid const*)


### glEnable


func [glEnable](#glEnable)(_0: i32)


### glEnableClientState


func [glEnableClientState](#glEnableClientState)(_0: i32)


### glFinish


func [glFinish](#glFinish)()


### glFlush


func [glFlush](#glFlush)()


### glFogf


func [glFogf](#glFogf)(_0: i32, _1: f32)


### glFogfv


func [glFogfv](#glFogfv)(_0: i32, _1: f32 const*)


### glFogx


func [glFogx](#glFogx)(_0: i32, _1: [gl](#gl)::GLfixed)


### glFogxv


func [glFogxv](#glFogxv)(_0: i32, _1: GLfixed const*)


### glFrontFace


func [glFrontFace](#glFrontFace)(_0: i32)


### glFrustumf


func [glFrustumf](#glFrustumf)(_0: f32, _1: f32, _2: f32, _3: f32, _4: f32, _5: f32)


### glFrustumx


func [glFrustumx](#glFrustumx)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed, _4: [gl](#gl)::GLfixed, _5: [gl](#gl)::GLfixed)


### glGenTextures


func [glGenTextures](#glGenTextures)(_0: i64, _1: u32*)


### glGetError


func [glGetError](#glGetError)() : i32


### glGetIntegerv


func [glGetIntegerv](#glGetIntegerv)(_0: i32, _1: i32*)


### glGetString


func [glGetString](#glGetString)(_0: i32) : u8 const*


### glHint


func [glHint](#glHint)(_0: i32, _1: i32)


### glLightModelf


func [glLightModelf](#glLightModelf)(_0: i32, _1: f32)


### glLightModelfv


func [glLightModelfv](#glLightModelfv)(_0: i32, _1: f32 const*)


### glLightModelx


func [glLightModelx](#glLightModelx)(_0: i32, _1: [gl](#gl)::GLfixed)


### glLightModelxv


func [glLightModelxv](#glLightModelxv)(_0: i32, _1: GLfixed const*)


### glLightf


func [glLightf](#glLightf)(_0: i32, _1: i32, _2: f32)


### glLightfv


func [glLightfv](#glLightfv)(_0: i32, _1: i32, _2: f32 const*)


### glLightx


func [glLightx](#glLightx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)


### glLightxv


func [glLightxv](#glLightxv)(_0: i32, _1: i32, _2: GLfixed const*)


### glLineWidth


func [glLineWidth](#glLineWidth)(_0: f32)


### glLineWidthx


func [glLineWidthx](#glLineWidthx)(_0: [gl](#gl)::GLfixed)


### glLoadIdentity


func [glLoadIdentity](#glLoadIdentity)()


### glLoadMatrixf


func [glLoadMatrixf](#glLoadMatrixf)(_0: f32 const*)


### glLoadMatrixx


func [glLoadMatrixx](#glLoadMatrixx)(_0: GLfixed const*)


### glLogicOp


func [glLogicOp](#glLogicOp)(_0: i32)


### glMaterialf


func [glMaterialf](#glMaterialf)(_0: i32, _1: i32, _2: f32)


### glMaterialfv


func [glMaterialfv](#glMaterialfv)(_0: i32, _1: i32, _2: f32 const*)


### glMaterialx


func [glMaterialx](#glMaterialx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)


### glMaterialxv


func [glMaterialxv](#glMaterialxv)(_0: i32, _1: i32, _2: GLfixed const*)


### glMatrixMode


func [glMatrixMode](#glMatrixMode)(_0: i32)


### glMultMatrixf


func [glMultMatrixf](#glMultMatrixf)(_0: f32 const*)


### glMultMatrixx


func [glMultMatrixx](#glMultMatrixx)(_0: GLfixed const*)


### glMultiTexCoord4f


func [glMultiTexCoord4f](#glMultiTexCoord4f)(_0: i32, _1: f32, _2: f32, _3: f32, _4: f32)


### glMultiTexCoord4x


func [glMultiTexCoord4x](#glMultiTexCoord4x)(_0: i32, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed, _4: [gl](#gl)::GLfixed)


### glNormal3f


func [glNormal3f](#glNormal3f)(_0: f32, _1: f32, _2: f32)


### glNormal3x


func [glNormal3x](#glNormal3x)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed)


### glNormalPointer


func [glNormalPointer](#glNormalPointer)(_0: i32, _1: i64, _2: GLvoid const*)


### glOrthof


func [glOrthof](#glOrthof)(_0: f32, _1: f32, _2: f32, _3: f32, _4: f32, _5: f32)


### glOrthox


func [glOrthox](#glOrthox)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed, _4: [gl](#gl)::GLfixed, _5: [gl](#gl)::GLfixed)


### glPixelStorei


func [glPixelStorei](#glPixelStorei)(_0: i32, _1: i32)


### glPointSize


func [glPointSize](#glPointSize)(_0: f32)


### glPointSizex


func [glPointSizex](#glPointSizex)(_0: [gl](#gl)::GLfixed)


### glPolygonOffset


func [glPolygonOffset](#glPolygonOffset)(_0: f32, _1: f32)


### glPolygonOffsetx


func [glPolygonOffsetx](#glPolygonOffsetx)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed)


### glPopMatrix


func [glPopMatrix](#glPopMatrix)()


### glPushMatrix


func [glPushMatrix](#glPushMatrix)()


### glReadPixels


func [glReadPixels](#glReadPixels)(_0: i32, _1: i32, _2: i64, _3: i64, _4: i32, _5: i32, _6: GLvoid*)


### glRotatef


func [glRotatef](#glRotatef)(_0: f32, _1: f32, _2: f32, _3: f32)


### glRotatex


func [glRotatex](#glRotatex)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed, _3: [gl](#gl)::GLfixed)


### glSampleCoverage


func [glSampleCoverage](#glSampleCoverage)(_0: [gl](#gl)::GLclampf, _1: bool)


### glSampleCoveragex


func [glSampleCoveragex](#glSampleCoveragex)(_0: [gl](#gl)::GLclampx, _1: bool)


### glScalef


func [glScalef](#glScalef)(_0: f32, _1: f32, _2: f32)


### glScalex


func [glScalex](#glScalex)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed)


### glScissor


func [glScissor](#glScissor)(_0: i32, _1: i32, _2: i64, _3: i64)


### glShadeModel


func [glShadeModel](#glShadeModel)(_0: i32)


### glStencilFunc


func [glStencilFunc](#glStencilFunc)(_0: i32, _1: i32, _2: u32)


### glStencilMask


func [glStencilMask](#glStencilMask)(_0: u32)


### glStencilOp


func [glStencilOp](#glStencilOp)(_0: i32, _1: i32, _2: i32)


### glTexCoordPointer


func [glTexCoordPointer](#glTexCoordPointer)(_0: i32, _1: i32, _2: i64, _3: GLvoid const*)


### glTexEnvf


func [glTexEnvf](#glTexEnvf)(_0: i32, _1: i32, _2: f32)


### glTexEnvfv


func [glTexEnvfv](#glTexEnvfv)(_0: i32, _1: i32, _2: f32 const*)


### glTexEnvx


func [glTexEnvx](#glTexEnvx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)


### glTexEnvxv


func [glTexEnvxv](#glTexEnvxv)(_0: i32, _1: i32, _2: GLfixed const*)


### glTexImage2D


func [glTexImage2D](#glTexImage2D)(_0: i32, _1: i32, _2: i32, _3: i64, _4: i64, _5: i32, _6: i32, _7: i32, _8: GLvoid const*)


### glTexParameterf


func [glTexParameterf](#glTexParameterf)(_0: i32, _1: i32, _2: f32)


### glTexParameterx


func [glTexParameterx](#glTexParameterx)(_0: i32, _1: i32, _2: [gl](#gl)::GLfixed)


### glTexSubImage2D


func [glTexSubImage2D](#glTexSubImage2D)(_0: i32, _1: i32, _2: i32, _3: i32, _4: i64, _5: i64, _6: i32, _7: i32, _8: GLvoid const*)


### glTranslatef


func [glTranslatef](#glTranslatef)(_0: f32, _1: f32, _2: f32)


### glTranslatex


func [glTranslatex](#glTranslatex)(_0: [gl](#gl)::GLfixed, _1: [gl](#gl)::GLfixed, _2: [gl](#gl)::GLfixed)


### glVertexPointer


func [glVertexPointer](#glVertexPointer)(_0: i32, _1: i32, _2: i64, _3: GLvoid const*)


### glViewport


func [glViewport](#glViewport)(_0: i32, _1: i32, _2: i64, _3: i64)


### glfwCreateCursor


func [glfwCreateCursor](#glfwCreateCursor)(image: [GLFWimage\* const](#GLFWimage), xhot: i32, yhot: i32) : [GLFWcursor\*](#GLFWcursor)


### glfwCreateStandardCursor


func [glfwCreateStandardCursor](#glfwCreateStandardCursor)(shape: i32) : [GLFWcursor\*](#GLFWcursor)


### glfwCreateWindow


func [glfwCreateWindow](#glfwCreateWindow)(width: i32, height: i32, title: char const*, monitor: [GLFWmonitor\*](#GLFWmonitor), share: [GLFWwindow\*](#GLFWwindow)) : [GLFWwindow\*](#GLFWwindow)


### glfwDefaultWindowHints


func [glfwDefaultWindowHints](#glfwDefaultWindowHints)()


### glfwDestroyCursor


func [glfwDestroyCursor](#glfwDestroyCursor)(cursor: [GLFWcursor\*](#GLFWcursor))


### glfwDestroyWindow


func [glfwDestroyWindow](#glfwDestroyWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwExtensionSupported


func [glfwExtensionSupported](#glfwExtensionSupported)(extension: char const*) : i32


### glfwFocusWindow


func [glfwFocusWindow](#glfwFocusWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwGetClipboardString


func [glfwGetClipboardString](#glfwGetClipboardString)(window: [GLFWwindow\*](#GLFWwindow)) : char const*


### glfwGetCurrentContext


func [glfwGetCurrentContext](#glfwGetCurrentContext)() : [GLFWwindow\*](#GLFWwindow)


### glfwGetCursorPos


func [glfwGetCursorPos](#glfwGetCursorPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: f64*, ypos: f64*)


### glfwGetError


func [glfwGetError](#glfwGetError)(description: char** const) : i32


### glfwGetFramebufferSize


func [glfwGetFramebufferSize](#glfwGetFramebufferSize)(window: [GLFWwindow\*](#GLFWwindow), width: i32*, height: i32*)


### glfwGetGamepadName


func [glfwGetGamepadName](#glfwGetGamepadName)(jid: i32) : char const*


### glfwGetGamepadState


func [glfwGetGamepadState](#glfwGetGamepadState)(jid: i32, state: [GLFWgamepadstate\*](#GLFWgamepadstate)) : i32


### glfwGetGammaRamp


func [glfwGetGammaRamp](#glfwGetGammaRamp)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : [GLFWgammaramp\* const](#GLFWgammaramp)


### glfwGetInputMode


func [glfwGetInputMode](#glfwGetInputMode)(window: [GLFWwindow\*](#GLFWwindow), mode: i32) : i32


### glfwGetJoystickAxes


func [glfwGetJoystickAxes](#glfwGetJoystickAxes)(jid: i32, count: i32*) : f32 const*


### glfwGetJoystickButtons


func [glfwGetJoystickButtons](#glfwGetJoystickButtons)(jid: i32, count: i32*) : u8 const*


### glfwGetJoystickGUID


func [glfwGetJoystickGUID](#glfwGetJoystickGUID)(jid: i32) : char const*


### glfwGetJoystickHats


func [glfwGetJoystickHats](#glfwGetJoystickHats)(jid: i32, count: i32*) : u8 const*


### glfwGetJoystickName


func [glfwGetJoystickName](#glfwGetJoystickName)(jid: i32) : char const*


### glfwGetJoystickUserPointer


func [glfwGetJoystickUserPointer](#glfwGetJoystickUserPointer)(jid: i32) : [void\*](#void)


### glfwGetKey


func [glfwGetKey](#glfwGetKey)(window: [GLFWwindow\*](#GLFWwindow), key: i32) : i32


### glfwGetKeyName


func [glfwGetKeyName](#glfwGetKeyName)(key: i32, scancode: i32) : char const*


### glfwGetKeyScancode


func [glfwGetKeyScancode](#glfwGetKeyScancode)(key: i32) : i32


### glfwGetMonitorContentScale


func [glfwGetMonitorContentScale](#glfwGetMonitorContentScale)(monitor: [GLFWmonitor\*](#GLFWmonitor), xscale: f32*, yscale: f32*)


### glfwGetMonitorName


func [glfwGetMonitorName](#glfwGetMonitorName)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : char const*


### glfwGetMonitorPhysicalSize


func [glfwGetMonitorPhysicalSize](#glfwGetMonitorPhysicalSize)(monitor: [GLFWmonitor\*](#GLFWmonitor), widthMM: i32*, heightMM: i32*)


### glfwGetMonitorPos


func [glfwGetMonitorPos](#glfwGetMonitorPos)(monitor: [GLFWmonitor\*](#GLFWmonitor), xpos: i32*, ypos: i32*)


### glfwGetMonitorUserPointer


func [glfwGetMonitorUserPointer](#glfwGetMonitorUserPointer)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : [void\*](#void)


### glfwGetMonitorWorkarea


func [glfwGetMonitorWorkarea](#glfwGetMonitorWorkarea)(monitor: [GLFWmonitor\*](#GLFWmonitor), xpos: i32*, ypos: i32*, width: i32*, height: i32*)


### glfwGetMonitors


func [glfwGetMonitors](#glfwGetMonitors)(count: i32*) : [GLFWmonitor\*\*](#GLFWmonitor)


### glfwGetMouseButton


func [glfwGetMouseButton](#glfwGetMouseButton)(window: [GLFWwindow\*](#GLFWwindow), button: i32) : i32


### glfwGetPrimaryMonitor


func [glfwGetPrimaryMonitor](#glfwGetPrimaryMonitor)() : [GLFWmonitor\*](#GLFWmonitor)


### glfwGetProcAddress


func [glfwGetProcAddress](#glfwGetProcAddress)(procname: char const*) : [func\(\) : void](#\_)


### glfwGetTime


func [glfwGetTime](#glfwGetTime)() : f64


### glfwGetTimerFrequency


func [glfwGetTimerFrequency](#glfwGetTimerFrequency)() : u64


### glfwGetTimerValue


func [glfwGetTimerValue](#glfwGetTimerValue)() : u64


### glfwGetVersion


func [glfwGetVersion](#glfwGetVersion)(major: i32*, minor: i32*, rev: i32*)


### glfwGetVersionString


func [glfwGetVersionString](#glfwGetVersionString)() : char const*


### glfwGetVideoMode


func [glfwGetVideoMode](#glfwGetVideoMode)(monitor: [GLFWmonitor\*](#GLFWmonitor)) : [GLFWvidmode\* const](#GLFWvidmode)


### glfwGetVideoModes


func [glfwGetVideoModes](#glfwGetVideoModes)(monitor: [GLFWmonitor\*](#GLFWmonitor), count: i32*) : [GLFWvidmode\* const](#GLFWvidmode)


### glfwGetWindowAttrib


func [glfwGetWindowAttrib](#glfwGetWindowAttrib)(window: [GLFWwindow\*](#GLFWwindow), attrib: i32) : i32


### glfwGetWindowContentScale


func [glfwGetWindowContentScale](#glfwGetWindowContentScale)(window: [GLFWwindow\*](#GLFWwindow), xscale: f32*, yscale: f32*)


### glfwGetWindowFrameSize


func [glfwGetWindowFrameSize](#glfwGetWindowFrameSize)(window: [GLFWwindow\*](#GLFWwindow), left: i32*, top: i32*, right: i32*, bottom: i32*)


### glfwGetWindowMonitor


func [glfwGetWindowMonitor](#glfwGetWindowMonitor)(window: [GLFWwindow\*](#GLFWwindow)) : [GLFWmonitor\*](#GLFWmonitor)


### glfwGetWindowOpacity


func [glfwGetWindowOpacity](#glfwGetWindowOpacity)(window: [GLFWwindow\*](#GLFWwindow)) : f32


### glfwGetWindowPos


func [glfwGetWindowPos](#glfwGetWindowPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: i32*, ypos: i32*)


### glfwGetWindowSize


func [glfwGetWindowSize](#glfwGetWindowSize)(window: [GLFWwindow\*](#GLFWwindow), width: i32*, height: i32*)


### glfwGetWindowUserPointer


func [glfwGetWindowUserPointer](#glfwGetWindowUserPointer)(window: [GLFWwindow\*](#GLFWwindow)) : [void\*](#void)


### glfwHideWindow


func [glfwHideWindow](#glfwHideWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwIconifyWindow


func [glfwIconifyWindow](#glfwIconifyWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwInit


func [glfwInit](#glfwInit)() : i32


### glfwInitHint


func [glfwInitHint](#glfwInitHint)(hint: i32, value: i32)


### glfwJoystickIsGamepad


func [glfwJoystickIsGamepad](#glfwJoystickIsGamepad)(jid: i32) : i32


### glfwJoystickPresent


func [glfwJoystickPresent](#glfwJoystickPresent)(jid: i32) : i32


### glfwMakeContextCurrent


func [glfwMakeContextCurrent](#glfwMakeContextCurrent)(window: [GLFWwindow\*](#GLFWwindow))


### glfwMaximizeWindow


func [glfwMaximizeWindow](#glfwMaximizeWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwPollEvents


func [glfwPollEvents](#glfwPollEvents)()


### glfwPostEmptyEvent


func [glfwPostEmptyEvent](#glfwPostEmptyEvent)()


### glfwRawMouseMotionSupported


func [glfwRawMouseMotionSupported](#glfwRawMouseMotionSupported)() : i32


### glfwRequestWindowAttention


func [glfwRequestWindowAttention](#glfwRequestWindowAttention)(window: [GLFWwindow\*](#GLFWwindow))


### glfwRestoreWindow


func [glfwRestoreWindow](#glfwRestoreWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwSetCharCallback


func [glfwSetCharCallback](#glfwSetCharCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, u32\) : void](#\_)) : [func\(struct GLFWwindow\*, u32\) : void](#\_)


### glfwSetCharModsCallback


func [glfwSetCharModsCallback](#glfwSetCharModsCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, u32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, u32, i32\) : void](#\_)


### glfwSetClipboardString


func [glfwSetClipboardString](#glfwSetClipboardString)(window: [GLFWwindow\*](#GLFWwindow), string: char const*)


### glfwSetCursor


func [glfwSetCursor](#glfwSetCursor)(window: [GLFWwindow\*](#GLFWwindow), cursor: [GLFWcursor\*](#GLFWcursor))


### glfwSetCursorEnterCallback


func [glfwSetCursorEnterCallback](#glfwSetCursorEnterCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)


### glfwSetCursorPos


func [glfwSetCursorPos](#glfwSetCursorPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: f64, ypos: f64)


### glfwSetCursorPosCallback


func [glfwSetCursorPosCallback](#glfwSetCursorPosCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)) : [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)


### glfwSetDropCallback


func [glfwSetDropCallback](#glfwSetDropCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, \[\]char const\*\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, \[\]char const\*\) : void](#\_)


### glfwSetErrorCallback


func [glfwSetErrorCallback](#glfwSetErrorCallback)(callback: [func\(i32, char const\*\) : void](#\_)) : [func\(i32, char const\*\) : void](#\_)


### glfwSetFramebufferSizeCallback


func [glfwSetFramebufferSizeCallback](#glfwSetFramebufferSizeCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)


### glfwSetGamma


func [glfwSetGamma](#glfwSetGamma)(monitor: [GLFWmonitor\*](#GLFWmonitor), gamma: f32)


### glfwSetGammaRamp


func [glfwSetGammaRamp](#glfwSetGammaRamp)(monitor: [GLFWmonitor\*](#GLFWmonitor), ramp: [GLFWgammaramp\* const](#GLFWgammaramp))


### glfwSetInputMode


func [glfwSetInputMode](#glfwSetInputMode)(window: [GLFWwindow\*](#GLFWwindow), mode: i32, value: i32)


### glfwSetJoystickCallback


func [glfwSetJoystickCallback](#glfwSetJoystickCallback)(callback: [func\(i32, i32\) : void](#\_)) : [func\(i32, i32\) : void](#\_)


### glfwSetJoystickUserPointer


func [glfwSetJoystickUserPointer](#glfwSetJoystickUserPointer)(jid: i32, pointer: [void\*](#void))


### glfwSetKeyCallback


func [glfwSetKeyCallback](#glfwSetKeyCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32, i32, i32\) : void](#\_)


### glfwSetMonitorCallback


func [glfwSetMonitorCallback](#glfwSetMonitorCallback)(callback: [func\(struct GLFWmonitor\*, i32\) : void](#\_)) : [func\(struct GLFWmonitor\*, i32\) : void](#\_)


### glfwSetMonitorUserPointer


func [glfwSetMonitorUserPointer](#glfwSetMonitorUserPointer)(monitor: [GLFWmonitor\*](#GLFWmonitor), pointer: [void\*](#void))


### glfwSetMouseButtonCallback


func [glfwSetMouseButtonCallback](#glfwSetMouseButtonCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32, i32\) : void](#\_)


### glfwSetScrollCallback


func [glfwSetScrollCallback](#glfwSetScrollCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)) : [func\(struct GLFWwindow\*, f64, f64\) : void](#\_)


### glfwSetTime


func [glfwSetTime](#glfwSetTime)(time: f64)


### glfwSetWindowAspectRatio


func [glfwSetWindowAspectRatio](#glfwSetWindowAspectRatio)(window: [GLFWwindow\*](#GLFWwindow), numer: i32, denom: i32)


### glfwSetWindowAttrib


func [glfwSetWindowAttrib](#glfwSetWindowAttrib)(window: [GLFWwindow\*](#GLFWwindow), attrib: i32, value: i32)


### glfwSetWindowCloseCallback


func [glfwSetWindowCloseCallback](#glfwSetWindowCloseCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*\) : void](#\_)) : [func\(struct GLFWwindow\*\) : void](#\_)


### glfwSetWindowContentScaleCallback


func [glfwSetWindowContentScaleCallback](#glfwSetWindowContentScaleCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, f32, f32\) : void](#\_)) : [func\(struct GLFWwindow\*, f32, f32\) : void](#\_)


### glfwSetWindowFocusCallback


func [glfwSetWindowFocusCallback](#glfwSetWindowFocusCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)


### glfwSetWindowIcon


func [glfwSetWindowIcon](#glfwSetWindowIcon)(window: [GLFWwindow\*](#GLFWwindow), count: i32, images: [GLFWimage\* const](#GLFWimage))


### glfwSetWindowIconifyCallback


func [glfwSetWindowIconifyCallback](#glfwSetWindowIconifyCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)


### glfwSetWindowMaximizeCallback


func [glfwSetWindowMaximizeCallback](#glfwSetWindowMaximizeCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32\) : void](#\_)


### glfwSetWindowMonitor


func [glfwSetWindowMonitor](#glfwSetWindowMonitor)(window: [GLFWwindow\*](#GLFWwindow), monitor: [GLFWmonitor\*](#GLFWmonitor), xpos: i32, ypos: i32, width: i32, height: i32, refreshRate: i32)


### glfwSetWindowOpacity


func [glfwSetWindowOpacity](#glfwSetWindowOpacity)(window: [GLFWwindow\*](#GLFWwindow), opacity: f32)


### glfwSetWindowPos


func [glfwSetWindowPos](#glfwSetWindowPos)(window: [GLFWwindow\*](#GLFWwindow), xpos: i32, ypos: i32)


### glfwSetWindowPosCallback


func [glfwSetWindowPosCallback](#glfwSetWindowPosCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)


### glfwSetWindowRefreshCallback


func [glfwSetWindowRefreshCallback](#glfwSetWindowRefreshCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*\) : void](#\_)) : [func\(struct GLFWwindow\*\) : void](#\_)


### glfwSetWindowShouldClose


func [glfwSetWindowShouldClose](#glfwSetWindowShouldClose)(window: [GLFWwindow\*](#GLFWwindow), value: i32)


### glfwSetWindowSize


func [glfwSetWindowSize](#glfwSetWindowSize)(window: [GLFWwindow\*](#GLFWwindow), width: i32, height: i32)


### glfwSetWindowSizeCallback


func [glfwSetWindowSizeCallback](#glfwSetWindowSizeCallback)(window: [GLFWwindow\*](#GLFWwindow), callback: [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)) : [func\(struct GLFWwindow\*, i32, i32\) : void](#\_)


### glfwSetWindowSizeLimits


func [glfwSetWindowSizeLimits](#glfwSetWindowSizeLimits)(window: [GLFWwindow\*](#GLFWwindow), minwidth: i32, minheight: i32, maxwidth: i32, maxheight: i32)


### glfwSetWindowTitle


func [glfwSetWindowTitle](#glfwSetWindowTitle)(window: [GLFWwindow\*](#GLFWwindow), title: char const*)


### glfwSetWindowUserPointer


func [glfwSetWindowUserPointer](#glfwSetWindowUserPointer)(window: [GLFWwindow\*](#GLFWwindow), pointer: [void\*](#void))


### glfwShowWindow


func [glfwShowWindow](#glfwShowWindow)(window: [GLFWwindow\*](#GLFWwindow))


### glfwSwapBuffers


func [glfwSwapBuffers](#glfwSwapBuffers)(window: [GLFWwindow\*](#GLFWwindow))


### glfwSwapInterval


func [glfwSwapInterval](#glfwSwapInterval)(interval: i32)


### glfwTerminate


func [glfwTerminate](#glfwTerminate)()


### glfwUpdateGamepadMappings


func [glfwUpdateGamepadMappings](#glfwUpdateGamepadMappings)(string: char const*) : i32


### glfwWaitEvents


func [glfwWaitEvents](#glfwWaitEvents)()


### glfwWaitEventsTimeout


func [glfwWaitEventsTimeout](#glfwWaitEventsTimeout)(timeout: f64)


### glfwWindowHint


func [glfwWindowHint](#glfwWindowHint)(hint: i32, value: i32)


### glfwWindowHintString


func [glfwWindowHintString](#glfwWindowHintString)(hint: i32, value: char const*)


### glfwWindowShouldClose


func [glfwWindowShouldClose](#glfwWindowShouldClose)(window: [GLFWwindow\*](#GLFWwindow)) : i32


### isalnum


func [isalnum](#isalnum)(arg: i32) : i32


### isdigit


func [isdigit](#isdigit)(arg: i32) : i32


### isspace


func [isspace](#isspace)(arg: i32) : i32


### malloc


func [malloc](#malloc)(size: u64) : [void\*](#void)


### mem::new<JsonArray>


func [mem::new<JsonArray>](#mem::new<JsonArray>)(a: [Allocator const\*](#Allocator)) : [Array<JsonNode\*>\*](#Array<JsonNode\*>)


### mem::new<JsonNode>


func [mem::new<JsonNode>](#mem::new<JsonNode>)(a: [Allocator const\*](#Allocator)) : [JsonNode\*](#JsonNode)


### mem::new<JsonObject>


func [mem::new<JsonObject>](#mem::new<JsonObject>)(a: [Allocator const\*](#Allocator)) : [Map<char const\*,JsonNode\*>\*](#Map<char\-const\*,JsonNode\*>)


### memcpy


func [memcpy](#memcpy)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)


### memduplicate


func [memduplicate](#memduplicate)(p: [void const\*](#void), len: u64, a: [Allocator const\*](#Allocator)) : [void\*](#void)


### memmove


func [memmove](#memmove)(dest: [void\*](#void), src: [void const\*](#void), num: u64) : [void\*](#void)


### memset


func [memset](#memset)(ptr: [void\*](#void), value: i32, len: u64) : [void\*](#void)


### new


func [new](#new)(a: [Allocator const\*](#Allocator)) : [T\*](#T)


### new<Allocation>


func [new<Allocation>](#new<Allocation>)(a: [Allocator const\*](#Allocator)) : [Allocation\*](#Allocation)


### new<Arena>


func [new<Arena>](#new<Arena>)(a: [Allocator const\*](#Allocator)) : [Arena\*](#Arena)


### printf


func [printf](#printf)(s: char const*)


### printf


func [printf](#printf)(s: char const*)


### realloc


func [realloc](#realloc)(ptr: [void\*](#void), size: u64) : [void\*](#void)


### sin


func [sin](#sin)(v: f64) : f64


### sinf


func [sinf](#sinf)(v: f32) : f32


### snprintf


func [snprintf](#snprintf)(s: char*, n: u64, format: char const*) : i32


### sqrt


func [sqrt](#sqrt)(v: f64) : f64


### sqrtf


func [sqrtf](#sqrtf)(v: f32) : f32


### strcmp


func [strcmp](#strcmp)(a: char const*, b: char const*) : i32


### strcpy


func [strcpy](#strcpy)(dest: char*, src: char const*) : char*


### strlen


func [strlen](#strlen)(str: char const*) : i32


### strncmp


func [strncmp](#strncmp)(a: char const*, b: char const*, num: u64) : i32


### strncpy


func [strncpy](#strncpy)(dest: char*, src: char const*, num: u64) : char*


### strtod


func [strtod](#strtod)(str: char const*, end: char**) : f64


### strtok


func [strtok](#strtok)(str: char*, delim: char const*) : char*


### strtol


func [strtol](#strtol)(str: char const*, end: char**, base: i32) : i64


### strtoul


func [strtoul](#strtoul)(str: char const*, end: char**, base: i32) : u64


### tan


func [tan](#tan)(v: f64) : f64


### tolower


func [tolower](#tolower)(arg: i32) : i32


### va\_end


func [va\_end](#va\_end)(args: [libc](#libc)::[va\_list](#va\_list))


### va\_start


func [va\_start](#va\_start)(args: [libc](#libc)::[va\_list](#va\_list), format: char const*)


### vfprintf


func [vfprintf](#vfprintf)(f: [FILE\*](#FILE), format: char const*, args: [libc](#libc)::[va\_list](#va\_list))


### vsnprintf


func [vsnprintf](#vsnprintf)(buffer: char*, len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsnprintf\_s


func [vsnprintf\_s](#vsnprintf\_s)(ptr: [void\*](#void), len: u64, max: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


### vsprintf\_s


func [vsprintf\_s](#vsprintf\_s)(ptr: [void\*](#void), len: u64, format: char const*, args: [libc](#libc)::[va\_list](#va\_list)) : i32


