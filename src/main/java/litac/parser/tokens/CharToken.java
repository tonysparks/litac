package litac.parser.tokens;

import static litac.parser.ErrorCode.UNEXPECTED_EOF;
import static litac.parser.tokens.TokenType.*;

import litac.parser.Source;

/**
 * The Character token
 * 
 * @author Tony
 *
 */
public class CharToken extends Token {
    public static final char CHARACTER_CHAR = '\'';
        
    /**
     * @param source the source from where to fetch the token's characters.
     */
    public CharToken(Source source) {
        super(source);
    }

    /**
     * Extract a character token from the source.
     */
    @Override
    protected void extract() {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        textBuffer.append(CHARACTER_CHAR);

        if (isEscape(currentChar)) {
            char escape = applyEscape(currentChar);
            textBuffer.append(escape);
            valueBuffer.append(escape);

            currentChar = currentChar();
        }
        else {
            // TODO: Unicode support
            textBuffer.append(currentChar);
            valueBuffer.append(currentChar);
            currentChar = nextChar();  // consume character
        }

        if (currentChar == CHARACTER_CHAR) {
            nextChar();  // consume final quote
            textBuffer.append(CHARACTER_CHAR);

            type = CHAR;
            value = valueBuffer.toString();
        }
        else {
            type = ERROR;
            value = UNEXPECTED_EOF;
        }

        text = textBuffer.toString();
    }

    /**
     * Determines if this is an escape character.
     *
     * @param currentChar
     * @return
     */
    private boolean isEscape(char currentChar) {
        boolean isEscape = false;
        if ( currentChar == '\\' ) {
            char nextChar = peekChar();
            switch(nextChar) {
            case 't':
            case 'b':
            case 'n':
            case 'r':
            case 'f':
//            case 'v':
            case '\'':
            case '\"':
            case '0':
            case '\\':
                isEscape = true;
                break;
            default:
                isEscape = false;
            }
        }

        return isEscape;
    }

    /**
     * Eat to the end of the escape
     * @param currentChar
     * @return
     */
    private char applyEscape(char currentChar) {
        char result = currentChar;
        char nextChar = nextChar();
        switch(nextChar) {
            case 't':
                result = "\t".charAt(0);
                break;
            case 'b':
                result = "\b".charAt(0);
                break;
            case 'n':
                result = "\n".charAt(0);
                break;
            case 'r':
                result = "\r".charAt(0);
                break;
            case 'f':
                result = "\f".charAt(0);
                break;
            case '\'':
                result = "\'".charAt(0);
                break;
            case '\"':
                result = "\"".charAt(0);
                break;
            case '0':
                result = '\0';
                break;
            case '\\':
                result = "\\".charAt(0);
                break;
            default:
                throw new IllegalArgumentException("Must invoke isEscape first!");
        }
        nextChar(); // eat this char

        return result;
    }
}
