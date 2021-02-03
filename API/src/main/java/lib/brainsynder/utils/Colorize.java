package lib.brainsynder.utils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.compnent.Part;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colorize {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");
    private static Method of;

    static {
        try {
            of = Reflection.getMethod(ChatColor.class, "of", String.class);
        } catch (Exception e) {
            of = null;
        }
    }


    /**
     * Translates the {@param text} that use the '&' symbol
     * Uses the Bungee {@link net.md_5.bungee.api.ChatColor#translateAlternateColorCodes(char, String)} method
     *
     * @param text - text to be translated
     * @return the colorized text
     */
    public static String translateBungee(String text) {
        if ((text == null) || text.isEmpty()) return text;
        return ChatColor.translateAlternateColorCodes('&', text);
    }


    /**
     * Translates the {@param text} that use the '&' symbol
     * Uses the Bukkit {@link org.bukkit.ChatColor#translateAlternateColorCodes(char, String)} method
     *
     * @param text - text to be translated
     * @return the colorized text
     */
    public static String translateBukkit(String text) {
        if ((text == null) || text.isEmpty()) return text;
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Will check if the {@param text} contains valid HEX colors '&#FFFFFF'
     *
     * @param text - text being checked
     * @return - true - contains hex
     * - false - no valid hex was found
     */
    public static boolean containsHexColors(String text) {
        if ((text == null) || text.isEmpty()) return false;
        Matcher matcher = HEX_PATTERN.matcher(text);
        return matcher.find();
    }

    /**
     * Translates the {@param text} that use the '&' symbol
     * It also allows for hex colors (Example: '&#FFFFFF' = white)
     *
     * @param text - text to be translated
     * @return the colorized text
     */
    public static String translateBungeeHex(String text) {
        if ((text == null) || text.isEmpty()) return text;
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = "";
            if (of != null) {

                // This is mostly in case someone is using a legacy version (EG below 1.16)
                try {
                    replacement = String.valueOf(Reflection.invoke(of, null, "#" + matcher.group(1)));
                } catch (Exception ignored) {
                }
            }

            matcher.appendReplacement(buffer, replacement).toString();
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    /**
     * Removes the formatting if the Hex color '&x&e&3&a&a&4&f'
     * And makes it more readable '&#e3aa4f'
     *
     * @param text - text to have hex removed from
     * @return - finalized string
     */
    public static String removeHexColor(String text) {
        // String is empty
        if ((text == null) || text.isEmpty()) return text;

        // The String does not contain any valid hex
        if (!containsHexColors(text)) return text;

        // Replaces the COLOR_CHAR('§') to '&'
        text = text.replace(ChatColor.COLOR_CHAR, '&');
        Pattern word = Pattern.compile("&x");
        Matcher matcher = word.matcher(text);

        char[] chars = text.toCharArray();
        while (matcher.find()) {
            StringBuilder builder = new StringBuilder();
            int start = matcher.start();
            int end = (start + 13);

            // If the '&x' is at the end of the string ignore it
            if (end > text.length()) continue;
            for (int i = start; i < end; i++) builder.append(chars[i]);

            String hex = builder.toString();
            hex = hex.replace("&x", "").replace("&", "");
            text = text.replace(builder.toString(), "&#" + hex);
        }

        return text;
    }

    public static JsonObject convertParts2Json (List<Part> parts) {
        JsonObject json = new JsonObject();
        json.add("text", "");

        JsonArray extra = new JsonArray();
        for (Part part : parts) {
            extra.add(part.toJson());
        }
        json.add("extra", extra);
        return json;
    }

    public static List<Part> splitMessageToParts(String value) {
        List<Part> parts = new ArrayList<>();
        // String is empty
        if ((value == null) || value.isEmpty()) return parts;
        value = value.replace(org.bukkit.ChatColor.COLOR_CHAR, '&');

        if (value.contains("&")) {
            String[] args = value.split("&");

            for (String string : args) {
                if (string == null) continue;
                if (string.isEmpty()) continue;

                // Has hex color

                Part part = new Part();
                if (string.startsWith("#")) {
                    // 0-6

                    StringBuilder HEX = new StringBuilder();
                    int end = 6;
                    // If the '&x' is at the end of the string ignore it
                    for (int i = -1; i < end; i++) HEX.append(string.charAt(i + 1));
                    part.text = string.replace(HEX.toString(), "");
                    part.customColor = hex2Color(HEX.toString());
                } else {
                    org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(string.charAt(0));
                    if (color == null) {
                        part.text = "&" + string;
                    } else {
                        part.text = string.replaceFirst(String.valueOf(string.charAt(0)), "");
                        part.color = color;
                    }
                }
                parts.add(part);
            }
        } else {
            // 'value' does not contain '&'
            parts.add(new Part(value));
        }
        return parts;
    }

    public static Color hex2Color(String hex) {
        return Color.fromRGB(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }
}
