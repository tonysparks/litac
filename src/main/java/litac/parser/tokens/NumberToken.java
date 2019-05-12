package litac.parser.tokens;

import static litac.parser.ErrorCode.*;
import static litac.parser.tokens.TokenType.*;

import java.math.BigInteger;

import litac.checker.TypeInfo;
import litac.checker.TypeInfo.TypeKind;
import litac.parser.Source;


/**
 * Number Token, parses number formats
 * 
 * @author Tony
 *
 */
public class NumberToken extends Token {
    private static final int MAX_EXPONENT = 37;
    private TypeInfo typeInfo;
    
    public NumberToken(Source source) {
        super(source);
    }
    
    /**
     * @return the typeInfo
     */
    public TypeInfo getTypeInfo() {
        return typeInfo;
    }
    
    @Override
    protected void extract() {    
        StringBuilder textBuffer = new StringBuilder(); // token's characters
        extractNumber(textBuffer);
        text = textBuffer.toString();        
    }

    /**
     * Extract a number token from the source.
     * 
     * @param textBuffer
     *            the buffer to append the token's characters.
     */
    protected void extractNumber(StringBuilder textBuffer) {
        String wholeDigits = null; // digits before the decimal point
        String fractionDigits = null; // digits after the decimal point
        String exponentDigits = null; // exponent digits
        char exponentSign = '+'; // exponent sign '+' or '-'
        boolean sawDotDot = false; // true if saw .. token
        char currentChar; // current character

        type = I32; // assume INTEGER token type for now

        // Extract the digits of the whole part of the number.
        wholeDigits = unsignedIntegerDigits(textBuffer);
        if (type == ERROR) {
            return;
        }

        // Is there a . ?
        // It could be a decimal point or the start of a .. token.
        currentChar = this.source.currentChar();
        if (currentChar == '.') {
            if (this.source.peekChar() == '.') {
                sawDotDot = true; // it's a ".." token, so don't consume it
            }
            else {
                type = F32; // decimal point, so token type is REAL
                textBuffer.append(currentChar);
                currentChar = this.source.nextChar(); // consume decimal point

                // Collect the digits of the fraction part of the number.
                if(Character.isDigit(currentChar)) {
                    fractionDigits = unsignedIntegerDigits(textBuffer);
                    if (type == ERROR) {
                        return;
                    }
                }
            }
        }

        // Is there an exponent part?
        // There cannot be an exponent if we already saw a ".." token.
        currentChar = this.source.currentChar();
        if (!sawDotDot && ((currentChar == 'E') || (currentChar == 'e'))) {
            type = F32; // exponent, so token type is REAL
            textBuffer.append(currentChar);
            currentChar = this.source.nextChar(); // consume 'E' or 'e'

            // Exponent sign?
            if ((currentChar == '+') || (currentChar == '-')) {
                textBuffer.append(currentChar);
                exponentSign = currentChar;
                currentChar = this.source.nextChar(); // consume '+' or '-'
            }

            // Extract the digits of the exponent.
            exponentDigits = unsignedIntegerDigits(textBuffer);
        }
        
        // Compute the value of an integer number token.
        if (type == I32) {
            int integerValue = computeIntegerValue(wholeDigits);

            if (type != ERROR) {
                type = NUMBER;
                value = integerValue;
                typeInfo = TypeInfo.I32_TYPE;
            }
            else if (value == RANGE_INTEGER) {                
                long longValue = computeLongValue(wholeDigits);
                
                type = NUMBER;
                value = longValue;
                typeInfo = TypeInfo.I64_TYPE;
            }
        }

        // Compute the value of a real number token.
        else if (type == F32) {
            double floatValue = computeFloatValue(wholeDigits, fractionDigits, exponentDigits, exponentSign);

            if (type != ERROR) {
                type = NUMBER;
                value = floatValue;
                if(floatValue < Float.MIN_VALUE || floatValue > Float.MAX_VALUE) {                   
                    typeInfo = TypeInfo.F64_TYPE;
                }
                else {
                    typeInfo = TypeInfo.F32_TYPE;
                }
                
            }
        }

        TypeInfo asType = readTypeInfo();
        if(asType != null) {
            typeInfo = asType;
        }
    }

    private TypeInfo readTypeInfo() {
        TypeInfo result = null;
        
        char currentChar = this.source.currentChar();
        
        if(currentChar == 'i' || currentChar == 'u') {
            if(typeInfo == TypeInfo.F32_TYPE || typeInfo == TypeInfo.F64_TYPE) {
                type = ERROR;
            }
            else {
                StringBuilder intType = new StringBuilder();
                intType.append(currentChar);
                
                currentChar = this.source.nextChar();
                while(Character.isDigit(currentChar)) {
                    intType.append(currentChar);
                    currentChar = this.source.nextChar();
                }
                
                TypeKind kind = TypeKind.fromString(intType.toString());
                if(kind == null) {
                    type = ERROR;
                }
                else {
                    switch(kind) {
                        case i8: {
                            result = TypeInfo.I8_TYPE;
                            break;
                        }
                        case u8: {
                            result = TypeInfo.U8_TYPE;
                            break;
                        }
                        case i16: {
                            result = TypeInfo.I16_TYPE;
                            break;
                        }
                        case u16: {
                            result = TypeInfo.U16_TYPE;
                            break;
                        }
                        case i32: {
                            result = TypeInfo.I32_TYPE;
                            break;
                        }
                        case u32: {
                            result = TypeInfo.U32_TYPE;
                            break;
                        }
                        case i64: {
                            result = TypeInfo.I64_TYPE;
                            break;
                        }
                        case u64: {
                            result = TypeInfo.U64_TYPE;
                            break;
                        }
                        case i128: {
                            result = TypeInfo.I128_TYPE;
                            break;
                        }
                        case u128: {
                            result = TypeInfo.U128_TYPE;
                            break;
                        }
                        default: {
                            type = ERROR;
                            break;
                        }
                    }
                }
            }
        }
        else if(currentChar == 'f') {
            StringBuilder floatType = new StringBuilder();
            floatType.append(currentChar);
            
            currentChar = this.source.nextChar();
            while(Character.isDigit(currentChar)) {
                floatType.append(currentChar);
                currentChar = this.source.nextChar();
            }
            
            TypeKind kind = TypeKind.fromString(floatType.toString());
            if(kind == null) {
                type = ERROR;
            }
            else {
                switch(kind) {
                    case f32: {
                        result = TypeInfo.F32_TYPE;
                        break;
                    }
                    case f64: {
                        result = TypeInfo.F64_TYPE;
                        break;
                    }
                    default: {
                        type = ERROR;
                        break;
                    }
                }
            }
            
        }
        
        return result;
    }
    
    /**
     * Extract and return the digits of an unsigned integer.
     * 
     * @param textBuffer
     *            the buffer to append the token's characters.
     * @return the string of digits.
     */
    private String unsignedIntegerDigits(StringBuilder textBuffer) {
        char currentChar = this.source.currentChar();

        // Must have at least one digit.
        if (!Character.isDigit(currentChar)) {
            type = ERROR;
            value = INVALID_NUMBER;
            return null;
        }

        boolean isHex = false;

        // Extract the digits.
        StringBuilder digits = new StringBuilder();
        while (Character.isDigit(currentChar) || 
               ('_' == currentChar) ||
               ('x' == currentChar && !isHex) || 
               (isHex && isHexDigit(currentChar))) {

            if ('x' == currentChar) {
                isHex = true;
            }            
            else if ('_' == currentChar) {
                currentChar = this.source.nextChar(); // consume _
                continue;
            }

            textBuffer.append(currentChar);
            digits.append(currentChar);
            currentChar = this.source.nextChar(); // consume digit
        }

        return digits.toString();
    }

    /**
     * If the character is a Hex digit
     * 
     * @param c
     * @return
     */
    private boolean isHexDigit(char c) {
        return ((c >= 48 && c <= 57) || // 0 - 9
                (c >= 65 && c <= 70) || // A - F
                (c >= 97 && c <= 102)   // a - f
        );
    }

    /**
     * Compute and return the integer value of a string of digits. Check for
     * overflow.
     * 
     * @param digits
     *            the string of digits.
     * @return the integer value.
     */
    private int computeIntegerValue(String digits) {
        // Return 0 if no digits.
        if (digits == null) {
            return 0;
        }

        /* If it's a HEX number, parse it out */
        if (digits.contains("0x")) {
            if (digits.length() > "0xFFFFFFFF".length()) {
                // Overflow: Set the integer out of range error.
                type = ERROR;
                value = RANGE_INTEGER;
                return 0;
            }

            return new BigInteger(digits.replace("0x", ""), 16).intValue();
        }

        int integerValue = 0;
        int prevValue = -1; // overflow occurred if prevValue > integerValue
        int index = 0;

        // Loop over the digits to compute the integer value
        // as long as there is no overflow.
        while ((index < digits.length()) && (integerValue >= prevValue)) {
            prevValue = integerValue;
            integerValue = 10 * integerValue + Character.getNumericValue(digits.charAt(index++));
        }

        // No overflow: Return the integer value.
        if (integerValue >= prevValue) {
            return integerValue;
        }

        // Overflow: Set the integer out of range error.
        else {
            type = ERROR;
            value = RANGE_INTEGER;
            return 0;
        }
    }

    /**
     * Compute and return the long value of a string of digits. Check for
     * overflow.
     * 
     * @param digits
     *            the string of digits.
     * @return the integer value.
     */
    private long computeLongValue(String digits) {
        // Return 0 if no digits.
        if (digits == null) {
            return 0L;
        }

        /* If it's a HEX number, parse it out */
        if (digits.contains("0x")) {
            if (digits.length() > "0xFFFFFFFFFFFFFFFF".length()) {
                // Overflow: Set the integer out of range error.
                type = ERROR;
                value = RANGE_LONG;
                return 0L;
            }

            return Long.parseLong(digits.replace("0x", ""), 16);
        }

        long longValue = 0L;
        long prevValue = -1L; // overflow occurred if prevValue > integerValue
        int index = 0;

        // Loop over the digits to compute the integer value
        // as long as there is no overflow.
        while ((index < digits.length()) && (longValue >= prevValue)) {
            prevValue = longValue;
            longValue = 10 * longValue + Character.getNumericValue(digits.charAt(index++));
        }

        // No overflow: Return the integer value.
        if (longValue >= prevValue) {
            return longValue;
        }

        // Overflow: Set the integer out of range error.
        else {
            type = ERROR;
            value = RANGE_LONG;
            return 0L;
        }
    }

    /**
     * Compute and return the float value of a real number.
     * 
     * @param wholeDigits
     *            the string of digits before the decimal point.
     * @param fractionDigits
     *            the string of digits after the decimal point.
     * @param exponentDigits
     *            the string of exponent digits.
     * @param exponentSign
     *            the exponent sign.
     * @return the float value.
     */
    private double computeFloatValue(String wholeDigits, String fractionDigits, String exponentDigits, char exponentSign) {
        double floatValue = 0.0;
        int exponentValue = computeIntegerValue(exponentDigits);
        String digits = wholeDigits; // whole and fraction digits

        // Negate the exponent if the exponent sign is '-'.
        if (exponentSign == '-') {
            exponentValue = -exponentValue;
        }

        // If there are any fraction digits, adjust the exponent value
        // and append the fraction digits.
        if (fractionDigits != null) {
            exponentValue -= fractionDigits.length();
            digits += fractionDigits;
        }

        // Check for a real number out of range error.
        if (Math.abs(exponentValue + wholeDigits.length()) > MAX_EXPONENT) {
            type = ERROR;
            value = RANGE_REAL;
            return 0.0f;
        }

        // Loop over the digits to compute the float value.
        int index = 0;
        while (index < digits.length()) {
            floatValue = 10 * floatValue + Character.getNumericValue(digits.charAt(index++));
        }

        // Adjust the float value based on the exponent value.
        if (exponentValue != 0) {
            floatValue *= Math.pow(10, exponentValue);
        }

        return floatValue;
    }
}