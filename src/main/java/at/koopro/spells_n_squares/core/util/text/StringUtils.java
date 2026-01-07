package at.koopro.spells_n_squares.core.util.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for string manipulation and processing.
 * Provides helper methods for truncation, padding, validation, formatting, and more.
 */
public final class StringUtils {
    private StringUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Truncates a string to a maximum length, appending ellipsis if truncated.
     * 
     * @param text The text to truncate
     * @param maxLength The maximum length (including ellipsis)
     * @param ellipsis The ellipsis string to append (e.g., "...")
     * @return The truncated string
     */
    public static String truncate(String text, int maxLength, String ellipsis) {
        if (text == null) {
            return "";
        }
        if (ellipsis == null) {
            ellipsis = "...";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= ellipsis.length()) {
            return ellipsis.substring(0, maxLength);
        }
        return text.substring(0, maxLength - ellipsis.length()) + ellipsis;
    }
    
    /**
     * Truncates a string to a maximum length.
     * 
     * @param text The text to truncate
     * @param maxLength The maximum length
     * @return The truncated string
     */
    public static String truncate(String text, int maxLength) {
        return truncate(text, maxLength, "...");
    }
    
    /**
     * Pads a string to a specified length on the left.
     * 
     * @param text The text to pad
     * @param length The target length
     * @param padChar The character to use for padding
     * @return The padded string
     */
    public static String padLeft(String text, int length, char padChar) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= length) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - text.length(); i++) {
            sb.append(padChar);
        }
        sb.append(text);
        return sb.toString();
    }
    
    /**
     * Pads a string to a specified length on the right.
     * 
     * @param text The text to pad
     * @param length The target length
     * @param padChar The character to use for padding
     * @return The padded string
     */
    public static String padRight(String text, int length, char padChar) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= length) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        for (int i = text.length(); i < length; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }
    
    /**
     * Converts a string to lowercase.
     * 
     * @param text The text to convert
     * @return The lowercase string, or empty string if null
     */
    public static String toLowerCase(String text) {
        return text == null ? "" : text.toLowerCase();
    }
    
    /**
     * Converts a string to uppercase.
     * 
     * @param text The text to convert
     * @return The uppercase string, or empty string if null
     */
    public static String toUpperCase(String text) {
        return text == null ? "" : text.toUpperCase();
    }
    
    /**
     * Checks if a string is null or empty.
     * 
     * @param text The text to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
    
    /**
     * Checks if a string is null, empty, or contains only whitespace.
     * 
     * @param text The text to check
     * @return true if null, empty, or blank
     */
    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty.
     * 
     * @param text The text to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }
    
    /**
     * Checks if a string is not null, not empty, and not blank.
     * 
     * @param text The text to check
     * @return true if not null, not empty, and not blank
     */
    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }
    
    /**
     * Formats a string with placeholders.
     * Supports {0}, {1}, {2}, etc. as placeholders.
     * 
     * @param format The format string
     * @param args The arguments to substitute
     * @return The formatted string
     */
    public static String format(String format, Object... args) {
        if (format == null) {
            return "";
        }
        if (args == null || args.length == 0) {
            return format;
        }
        String result = format;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String replacement = args[i] != null ? args[i].toString() : "null";
            result = result.replace(placeholder, replacement);
        }
        return result;
    }
    
    /**
     * Joins strings with a delimiter.
     * 
     * @param delimiter The delimiter
     * @param strings The strings to join
     * @return The joined string
     */
    public static String join(String delimiter, String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            if (strings[i] != null) {
                sb.append(strings[i]);
            }
        }
        return sb.toString();
    }
    
    /**
     * Joins strings with a delimiter.
     * 
     * @param delimiter The delimiter
     * @param strings The strings to join
     * @return The joined string
     */
    public static String join(String delimiter, Iterable<String> strings) {
        if (strings == null) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String str : strings) {
            if (!first) {
                sb.append(delimiter);
            }
            if (str != null) {
                sb.append(str);
            }
            first = false;
        }
        return sb.toString();
    }
    
    /**
     * Splits a string by a delimiter.
     * 
     * @param text The text to split
     * @param delimiter The delimiter
     * @return List of split strings
     */
    public static List<String> split(String text, String delimiter) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        if (delimiter == null || delimiter.isEmpty()) {
            result.add(text);
            return result;
        }
        int start = 0;
        int index;
        while ((index = text.indexOf(delimiter, start)) != -1) {
            result.add(text.substring(start, index));
            start = index + delimiter.length();
        }
        result.add(text.substring(start));
        return result;
    }
    
    /**
     * Calculates the Levenshtein distance between two strings (string similarity).
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return The Levenshtein distance (0 = identical, higher = more different)
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null) {
            s1 = "";
        }
        if (s2 == null) {
            s2 = "";
        }
        if (s1.equals(s2)) {
            return 0;
        }
        if (s1.isEmpty()) {
            return s2.length();
        }
        if (s2.isEmpty()) {
            return s1.length();
        }
        
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Sanitizes a string by removing potentially dangerous characters.
     * 
     * @param text The text to sanitize
     * @return The sanitized string
     */
    public static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        // Remove control characters and keep only printable ASCII
        return text.replaceAll("[^\\x20-\\x7E]", "");
    }
    
    /**
     * Capitalizes the first letter of a string.
     * 
     * @param text The text to capitalize
     * @return The capitalized string
     */
    public static String capitalize(String text) {
        if (isEmpty(text)) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    /**
     * Repeats a string a specified number of times.
     * 
     * @param text The text to repeat
     * @param count The number of times to repeat
     * @return The repeated string
     */
    public static String repeat(String text, int count) {
        if (text == null || count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(text);
        }
        return sb.toString();
    }
}


