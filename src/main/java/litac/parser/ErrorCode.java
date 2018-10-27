package litac.parser;

/**
 * JSTL2 error codes
 * 
 * @author Tony
 *
 */
public enum ErrorCode {
    INVALID_ASSIGNMENT("Invalid assignment statement"),
    INVALID_CONST_EXPR("Invalid constant expression"),
    INVALID_CHARACTER("Invalid character"),
    INVALID_NUMBER("Invalid number"),
    INVALID_FIELD("Invalid field member"),
    INVALID_IMPORT_ACCESS("Invalid import access"),
    INVALID_CONTINUE("Invalid continue statement"),
    INVALID_BREAK("Invalid break statement"),    
    INVALID_OBJECT_INIT("Invalid structure initializer"),
    INVALID_MODULE_ACCESS("Invalid module access"),

    MISSING_COMMA("Missing ,"),
    MISSING_SEMICOLON("Missing ;"),
    MISSING_RIGHT_BRACE("Missing Right Brace"),
    MISSING_EQUALS("Missing ="),
    MISSING_IDENTIFIER("Missing identifier"),
    MISSING_RIGHT_BRACKET("Missing ]"),
    MISSING_RIGHT_PAREN("Missing )"),
    MISSING_LEFT_PAREN("Missing ("),
    MISSING_LEFT_BRACE("Missing {"),
    MISSING_COLON("Missing :"),
    MISSING_WHILE("Missing 'while'"),
    
    
    RANGE_INTEGER("Integer literal out of range"),
    RANGE_LONG("Long literal out of range"),
    RANGE_REAL("Real literal out of range"),

    UNEXPECTED_EOF("Unexpected end of file"),
    UNEXPECTED_TOKEN("Unexpected token"),
    UNIMPLEMENTED("Unimplemented feature"),

    UNKNOWN_ERROR("An unknown error occured"),

    // Fatal errors.
    //IO_ERROR(-101, "Object I/O error"),
    TOO_MANY_ERRORS(-102, "Too many syntax errors");

    private int status;      // exit status
    private String message;  // error message

    /**
     * Constructor.
     * @param message the error message.
     */
    ErrorCode(String message) {
        this.status = 0;
        this.message = message;
    }

    /**
     * Constructor.
     * @param status the exit status.
     * @param message the error message.
     */
    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Getter.
     * @return the exit status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the message.
     */
    @Override
    public String toString() {
        return message;
    }
}
