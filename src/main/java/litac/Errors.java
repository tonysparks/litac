/*
 * see license.txt
 */
package litac;

import litac.ast.Node.SrcPos;
import litac.parser.*;
import litac.parser.tokens.*;

/**
 * @author Tony
 *
 */
public class Errors {

    public static void compileError(SrcPos pos, String message) {
        // TODO: Rethrow the exception?? or???
        if(pos != null) {
            System.err.println(String.format("*** ERROR: %s at line: %d in '%s'", message, pos.lineNumber, pos.sourceName));
            System.err.println("\t" +pos.sourceLine);
            System.err.println();
        }
        else {
            System.err.println(String.format("*** ERROR: %s", message));            
            System.err.println();
        }
    }

    
    public static void typeCheckError(SrcPos pos, String message) {
        if(pos != null) {
            System.err.println(String.format("*** ERROR: %s at line: %d in '%s'", message, pos.lineNumber, pos.sourceName));
            System.err.println("\t" +pos.sourceLine);
            System.err.println();
        }
        else {
            System.err.println(String.format("*** ERROR: %s", message));            
            System.err.println();
        }
    }
    
    /**
     * Constructs an error message into a {@link ParseException}
     * 
     * @param token
     * @param errorCode
     * @return the {@link ParseException} to be thrown
     */
    public static ParseException parseError(Scanner scanner, Token token, ErrorCode errorCode) {
        int lineNumber = token.getLineNumber();
        int position = token.getPosition();
        String tokenText = token.getType() != TokenType.END_OF_FILE ? token.getText() : null;
        String errorMessage = errorCode.toString(); 
        
        int spaceCount = position + 1;
        String currentLine = scanner.getSourceLine(lineNumber);
        StringBuilder flagBuffer = new StringBuilder("\n");
        flagBuffer.append(currentLine != null ? currentLine : "");
        flagBuffer.append("\n");

        // Spaces up to the error position.
        for (int i = 1; i < spaceCount; ++i) {
            flagBuffer.append(' ');
        }

        // A pointer to the error followed by the error message.
        flagBuffer.append("^\n*** ").append(errorMessage);

        flagBuffer.append(" [at line: ").append(lineNumber);
        
        // Text, if any, of the bad token.
        if (tokenText != null) {
            flagBuffer.append(" '").append(tokenText).append("'");
        }
        
        flagBuffer.append(" in '").append(scanner.getSourceName()).append("'");
        
        flagBuffer.append("]");

        return new ParseException(errorCode, token, flagBuffer.toString());
    }
}
