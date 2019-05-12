package litac.parser.tokens;

import static litac.parser.tokens.TokenType.IDENTIFIER;
import static litac.parser.tokens.TokenType.RESERVED_WORDS;

import litac.parser.Source;

/**
 * Word/Identifier token
 * 
 * @author Tony
 *
 */
public class WordToken extends Token {

    /**
     * Determines if the supplied character is valid inside the identifier
     * @param c
     * @return true if valid identifier character
     */
    public static final boolean isValidIdentifierCharacter(char c) {
        boolean isValid = Character.isLetterOrDigit(c);
        if ( !isValid ) {
            switch(c) {
            //case '$':
            case '_':
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    /**
     * Determines if the supplied character is a valid start character for an identifier
     *
     * @param c
     * @return true if valid
     */
    public static final boolean isValidStartIdentifierCharacter(char c) {
        boolean isValid = Character.isLetter(c);
        if ( !isValid ) {
            switch(c) {
            //case '$':
            case '_':
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    /**
     * @param source the source from where to fetch the token's characters.
     */
    public WordToken(Source source) {
        super(source);
    }

    /**
     * Extract a Leola word token from the source.
     */
    @Override
    protected void extract() {
        StringBuilder textBuffer = new StringBuilder();
        char currentChar = currentChar();

        // Get the word characters (letter or digit).  The scanner has
        // already determined that the first character is a letter.
        while (isValidIdentifierCharacter(currentChar)) {
            textBuffer.append(currentChar);
            currentChar = nextChar();            
        }

        text = textBuffer.toString();

        if(RESERVED_WORDS.contains(text)) {
            type = TokenType.valueOf(text.toUpperCase());
        }
        else {
            type = IDENTIFIER;
        }        
    }
}
