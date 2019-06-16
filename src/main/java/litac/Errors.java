/*
 * see license.txt
 */
package litac;

import litac.ast.Stmt;
import litac.parser.ErrorCode;
import litac.parser.ParseException;
import litac.parser.Scanner;
import litac.parser.tokens.Token;
import litac.parser.tokens.TokenType;

/**
 * @author Tony
 *
 */
public class Errors {

    public static void typeCheckError(Stmt stmt, String message) {
        if(stmt != null) {
            System.err.println(String.format("*** ERROR: %s at line: %d in '%s'", message, stmt.getLineNumber(), stmt.getSourceFile()));
            System.err.println("\t" +stmt.getSourceLine());
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
        
        flagBuffer.append(" in '").append(scanner.getSourceFile()).append("'");
        
        flagBuffer.append("]");

        return new ParseException(errorCode, token, flagBuffer.toString());
    }
}
