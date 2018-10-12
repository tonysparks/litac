package litac.parser.tokens;

import static litac.parser.tokens.TokenType.ERROR;

import litac.parser.ErrorCode;
import litac.parser.Source;

/**
 * Leola error token
 * 
 * @author Tony
 *
 */
public class ErrorToken extends Token {
    
    /**
     * @param source
     *            the source from where to fetch subsequent characters.
     * @param errorCode
     *            the error code.
     * @param tokenText
     *            the text of the erroneous token.
     */
    public ErrorToken(Source source, ErrorCode errorCode, String tokenText) {
        super(source);

        this.text = tokenText;
        this.type = ERROR;
        this.value = errorCode;
    }

    @Override
    protected void extract() {
    }
}
