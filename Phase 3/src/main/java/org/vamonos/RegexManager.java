package main.java.org.vamonos;

// Stores regular expressions to be used throughout the application
public class RegexManager {
    // one or more of any character
    final public static String NOT_EMPTY = ".+";

    // between 0 and 15 characters (inclusive)
    final public static String MAX_15_CHARS = ".{0,15}";

    // between 0 and 25 characters (inclusive)
    final public static String MAX_25_CHARS = ".{0,25}";

    // one or more of any combination of letters, numbers, underscores, spaces, and dashes
    final public static String NO_SYMBOLS = "[\\w -]+";

    // one or more of any combination of letters, spaces, and dashes
    final public static String ONLY_LETTERS_SPACES_DASHES = "[a-zA-Z -]+";

    // (one or more digits, optionally followed by a '.' and then zero or more '0s) or (a '.' and then one or more zeros)
    final public static String IS_NON_NEGATIVE_INT = "(\\d+(\\.0*)?)|(\\.0+)";

    // identical to IS_NON_NEGATIVE_INT, but with an optional leading '-'
    final public static String IS_INT = "-?(\\d+(\\.0*)?)|(\\.0+)";

    // (zero or more leading 0's, then a single digit optionally followed by '.' and zero or more 0's) or (see above)
    final public static String IS_INT_UNDER_10 = "0*(\\d(\\.0*)?)|(\\.0+)"; // assumes known to not be negative

    /* (zero or more leading 0's, then either (1 followed by a digit 0-4) or (a single digit),
            followed optionally by '.' and zero or more 0's) or (see above)    */
    final public static String IS_INT_UNDER_15 = "0*((1[0-4])|(\\d)(\\.0*)?)|(\\.0+)"; // assumes known not to be negative

    // (one or more digits followed optionally by a '.' and then zero or more digits) or (a '.' followed by one or more digits)
    final public static String IS_NON_NEGATIVE_NUMBER = "(\\d+\\.?\\d*)|(\\.\\d+)";

    // identical to IS_NON_NEGATIVE_NUMBER, but with an optional leading '-'
    final public static String IS_NUMBER = "-?(\\d+\\.?\\d*)|(\\.\\d+)";

    // zero or more of any character, followed by a non-zero digit, followed by zero or more of any character
    final public static String IS_NOT_ZERO = ".*[1-9].*"; // assumes known to be non-negative number

    // zero or more digits, followed optionally by (a '.' followed by at most 2 of any digits, followed by zero or more 0's)
    final public static String MAX_2_DECIMALS = "\\d*(\\.\\d{0,2}0*)?"; // assumes known to be non-negative number

    // zero or more leading 0's, followed by up to 3 of any digit, followed optionally by a '.' and then zero or more digits
    final public static String IS_UNDER_1000 = "0*\\d{0,3}(\\.\\d*)?"; // assumes known to be non-negative number

    // case-insensitive matching of either 'yes' or 'no'
    final public static String YES_OR_NO = "(?i)(YES)|(NO)";

    // case-insensitive matching of one of 'aa', 'fs', 'rs', ps'
    final public static String VALID_USERTYPE = "(?i)(aa)|([frp]s)";

    // 'A' followed by exactly 7 of any digit
    final public static String VALID_ID_FORMAT = "A\\d{7}";
}