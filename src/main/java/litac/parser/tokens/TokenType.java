package litac.parser.tokens;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import litac.parser.ErrorCode;
import litac.parser.ParseException;


/**
 * Token types.
 * 
 * @author Tony
 *
 */
public enum TokenType {
    // Reserved words.    
    STRUCT, 
        VAR, CONST, FUNC, ENUM, FOR, WHILE, DO, IF, ELSE, BREAK, CONTINUE, RETURN, GOTO, 
        VOID, TRUE, FALSE, NULL, BOOL,
        I8, U8,
        I16, U16,
        I32, U32,
        I64, U64,
        I128,U128,
        F32, F64,
        STRING, ARRAY, 
        MODULE, IMPORT, AS, TYPEDEF, SIZEOF,
    UNION,
    // end Reserved words

    // Special symbols.
    PLUS("+"), 
        MINUS("-"), STAR("*"), MOD("%"), SLASH("/"), 
        DOT("."), VAR_ARGS("..."), AT("@"), QUESTION_MARK("?"), COMMA(","), SEMICOLON(";"), COLON(":"),
        
        LESS_THAN("<"), LESS_EQUALS("<="),GREATER_EQUALS(">="), GREATER_THAN(">"),
        
        EQUALS_EQUALS("=="), EQUALS("="), NOT_EQUALS("!="), 
        PLUS_EQ("+="), MINUS_EQ("-="), DIV_EQ("/="), MUL_EQ("*="),MOD_EQ("%="),
        LSHIFT_EQ("<<="), RSHIFT_EQ(">>="), BNOT_EQ("~="), XOR_EQ("^="), BAND_EQ("&="), BOR_EQ("|="),
        
        LSHIFT("<<"), RSHIFT(">>"), BNOT("~"), XOR("^"), BAND("&"), BOR("|"),
        
        LEFT_PAREN("("), RIGHT_PAREN(")"),
        LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"), NOT("!"), 
        OR("||"), AND("&&"),
    DOUBLE_QUOTE("\""),  

    // end Special symbols

    IDENTIFIER,    
    NUMBER,
//    STRING,
//    ARRAY,
    ERROR, 
    END_OF_FILE;

    private static final int FIRST_RESERVED_INDEX = STRUCT.ordinal();
    private static final int LAST_RESERVED_INDEX  = UNION.ordinal();

    private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
    private static final int LAST_SPECIAL_INDEX  = DOUBLE_QUOTE.ordinal();

    private String text;  // token text

    /**
     */
    TokenType() {
        this.text = this.toString().toLowerCase();
    }

    /**
     * @param text the token text.
     */
    TokenType(String text) {
        this.text = text;
    }

   

    /**
     * @return the token text.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the text as a number
     * @return the text as a number
     * @throws Exception
     */
    public double getTextAsNumber() throws Exception {
        try {
            return Double.parseDouble(this.text);
        }
        catch(Exception e) {
            throw new ParseException 
                (ErrorCode.INVALID_NUMBER, null, "Unable to parse: " + this.text + " as a number.", e);
        }
    }

    // Set of lower-cased Leola reserved word text strings.
    public static Set<String> RESERVED_WORDS = new HashSet<String>();
    static {
        TokenType values[] = TokenType.values();
        for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.add(values[i].getText().toLowerCase());
        }
    }

    // Hash table of Leola special symbols.  Each special symbol's text
    // is the key to its Leola token type.
    public static Map<String, TokenType> SPECIAL_SYMBOLS = new HashMap<String, TokenType>();
    static {
        TokenType values[] = TokenType.values();
        for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
            SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}
