package lib.brainsynder.strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class StringUtilities {
    /**
     * It takes a String, a character to pad with, the maximum padding, and the alignment of the text, and returns a padded
     * String
     *
     * @param text The text to be padded.
     * @param paddingChar The character to use for padding.
     * @param maxPadding The maximum length of the string.
     * @param alignText This is an enum that can be used to align the text to the left, right or center.
     * @return A string with the text padded to the left, right, or center.
     */
    public static String getPaddedString(String text, char paddingChar, int maxPadding, StringUtilities.AlignText alignText) {
        if (text == null) {
            throw new NullPointerException("Can not add padding in null String!");
        }

        int length = text.length();
        int padding = (maxPadding - length) / 2;//decide left and right padding
        if (padding <= 0) {
            return text;// return actual String if padding is less than or equal to 0
        }

        String empty = "", hash = "#";//hash is used as a place holder

        // extra character in case of String with even length
        int extra = (length % 2 == 0) ? 1 : 0;

        String leftPadding = "%" + padding + "s";
        String rightPadding = "%" + (padding - extra) + "s";

        // Will align the text to the selected side
        switch (alignText) {
            case LEFT:
                leftPadding = "%s";
                rightPadding = "%" + (padding+(padding - extra)) + "s";
                break;
            case RIGHT:
                rightPadding = "%s";
                leftPadding = "%" + (padding+(padding - extra)) + "s";
                break;
        }

        String strFormat = leftPadding + "%s" + rightPadding;
        String formattedString = String.format(strFormat, empty, hash, empty);

        //Replace space with * and hash with provided String
        String paddedString = formattedString.replace(' ', paddingChar).replace(hash, text);
        return paddedString;
    }

    /**
     * Replaces the last occurrence of the target string with the replacement string
     *
     * @param target the string to be replaced
     * @param replacement The string to replace the target with.
     * @param haystack The string to search in
     * @return The last occurrence of the target string is replaced with the replacement string.
     */
    public static String replaceLast(String target, String replacement, String haystack) {
        int pos = haystack.lastIndexOf(target);
        if (pos > -1) {
            return haystack.substring(0, pos)
                    + replacement
                    + haystack.substring(pos + target.length());
        } else {
            return haystack;
        }
    }

    /**
     * It returns true if the string contains the character the specified number of times
     *
     * @param string The string to search in
     * @param character The character to search for.
     * @param count The number of times the character should appear in the string. If this is -1, then the method will
     * return true if the character appears at least once.
     * @return The method returns a boolean value.
     */
    public static boolean contains (String string, char character, int count) {
        if (count == -1) return string.contains(String.valueOf(character));

        int i = 0;
        for (char c : string.toCharArray()) {
            if (c == character) i++;
        }

        return  (i == count);
    }

    /**
     * Return the part of the string after the first occurrence of the needle.
     *
     * @param needle The string to search for
     * @param haystack The string to search in
     * @return The string after the needle in the haystack.
     */
    public static String after (String needle, String haystack) {
        return haystack.substring((haystack.indexOf(needle)+needle.length()));
    }

    /**
     * Return the part of the string after the last occurrence of the needle.
     *
     * @param needle The string to search for
     * @param haystack The string to search in
     * @return The substring of haystack after the last occurrence of needle.
     */
    public static String afterLast (String needle, String haystack) {
        return haystack.substring(reversePos(needle, haystack)+needle.length());
    }

    /**
     * Return the part of the string before the first occurrence of the needle.
     *
     * @param needle The string to search for
     * @param haystack The string to search in
     * @return The string before the needle.
     */
    public static String before (String needle, String haystack) {
        return haystack.substring(0, haystack.indexOf(needle));
    }

    /**
     * Return the part of the haystack before the last occurrence of the needle.
     *
     * @param needle The string to search for
     * @param haystack The string to search in
     * @return The substring of haystack from the beginning to the position of the last occurrence of needle.
     */
    public static String beforeLast (String needle, String haystack) {
        return haystack.substring(0, reversePos(needle, haystack));
    }

    /**
     * Return the string between the first and last strings, inclusive of the first string but not the last string.
     *
     * @param first The string that comes before the string you want to extract.
     * @param last The string that will be used to find the end of the string.
     * @param haystack The string to search in
     * @return The string between the first and last strings.
     */
    public static String between (String first, String last, String haystack) {
        return before(last, after(first, haystack));
    }

    /**
     * Return the text between the last occurrence of the first string and the last occurrence of the second string.
     *
     * @param first The first string to search for.
     * @param last The last string to search for
     * @param haystack The string to search in
     * @return The string between the last two occurrences of the first and last strings.
     */
    public static String betweenLast (String first, String last, String haystack) {
        return afterLast(first, beforeLast(last, haystack));
    }


    /**
     * Returns the last position of the needle in the original haystack.
     *
     * @param needle The string to search for
     * @param haystack the string to search in
     * @return The position of the last occurrence of the needle in the haystack.
     */
    public static int reversePos(String needle, String haystack) {
        int pos = reverse(haystack).indexOf(reverse(needle));
        return haystack.length() - pos - needle.length();
    }

    /**
     * It reverses the letters in a string
     *
     * @param input a string
     * @return A string that is the reverse of the input string.
     */
    public static String reverse (String input) {
        char[] chars = input.toCharArray();
        List<Character> characters = new ArrayList<>();

        for (char c: chars)
            characters.add(c);

        Collections.reverse(characters);
        ListIterator iterator = characters.listIterator();
        StringBuilder builder = new StringBuilder();

        while (iterator.hasNext())
            builder.append(iterator.next());
        return builder.toString();
    }

    /**
     * It scrambles the words in a string.
     *
     * @param input The string to be scrambled.
     * @return A scrambled version of the input string.
     */
    public static String scramble(String input) {
        StringBuilder out = new StringBuilder();
        for (String part : input.split(" ")) {
            List<Character> characters = new ArrayList<>();
            for (char c : part.toCharArray()) {
                characters.add(c);
            }
            StringBuilder output = new StringBuilder(part.length());
            while (characters.size() != 0) {
                int rndm = (int) (Math.random() * characters.size());
                output.append(characters.remove(rndm));
            }
            out.append(output).append(' ');
        }
        return out.toString().trim();
    }

    /**
     * An enum that is used to align the text in the getPaddedString method.
     */
    public enum AlignText {
        LEFT, RIGHT, CENTER
    }



    /**
     * If the first character of the string is a letter, then return a new string with the first character capitalized and
     * the rest of the string unchanged. Otherwise, return the string unchanged
     *
     * @param str The string to capitalize, may be null
     * @return The first letter of the string is being capitalized.
     */
    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    /**
     * If the character is a delimiter, append it to the buffer and set capitalizeNext to true. If capitalizeNext is true,
     * capitalize the character and append it to the buffer. Otherwise, just append the character to the buffer
     *
     * @param str The string to capitalize
     * @param delimiters The delimiters to use to separate words. If null, then CharUtils.WHITESPACE is used.
     * @return A string with the first letter of each word capitalized.
     */
    public static String capitalize(String str, char[] delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (str != null && str.length() != 0 && delimLen != 0) {
            int strLen = str.length();
            StringBuilder buffer = new StringBuilder(strLen);
            boolean capitalizeNext = true;

            for(int i = 0; i < strLen; ++i) {
                char ch = str.charAt(i);
                if (isDelimiter(ch, delimiters)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(ch);
                }
            }

            return buffer.toString();
        } else {
            return str;
        }
    }

    /**
     * If the delimiters array is null, then return true if the character is whitespace, otherwise return true if the
     * character is in the delimiters array
     *
     * @param ch The character to check.
     * @param delimiters The characters that are considered to be delimiters.
     * @return A boolean value.
     */
    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        } else {
            int i = 0;

            for(int isize = delimiters.length; i < isize; ++i) {
                if (ch == delimiters[i]) {
                    return true;
                }
            }

            return false;
        }
    }
}