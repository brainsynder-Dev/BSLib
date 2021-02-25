package lib.brainsynder.utils;

import net.md_5.bungee.api.ChatColor;

public enum DyeColorWrapper {
    WHITE(0, 15, ChatColor.WHITE),
    ORANGE(1, 14, Colorize.fetchColor("#fca903", ChatColor.GOLD)),
    MAGENTA(2, 13, Colorize.fetchColor("#ff00ff", ChatColor.LIGHT_PURPLE)),
    LIGHT_BLUE(3, 12, Colorize.fetchColor("#94ccf7", ChatColor.AQUA)),
    YELLOW(4, 11, ChatColor.YELLOW),
    LIME(5, 10, ChatColor.GREEN),
    PINK(6, 9, Colorize.fetchColor("#fccfcc", ChatColor.LIGHT_PURPLE)),
    GRAY(7, 8, ChatColor.DARK_GRAY),
    LIGHT_GRAY(8, 7, ChatColor.GRAY),
    CYAN(9, 6, Colorize.fetchColor("#00ffff", ChatColor.DARK_AQUA)),
    PURPLE(10, 5, Colorize.fetchColor("#a917e8", ChatColor.DARK_PURPLE)),
    BLUE(11, 4, ChatColor.BLUE),
    BROWN(12, 3, Colorize.fetchColor("#c98a63", ChatColor.DARK_RED)),
    GREEN(13, 2, Colorize.fetchColor("#1f8f09", ChatColor.DARK_GREEN)),
    RED(14, 1, ChatColor.RED),
    BLACK(15, 0, ChatColor.BLACK);

    private final int woolData;
    private final int dyeData;
    private final ChatColor chatColor;

    DyeColorWrapper(int woolData, int dyeData, ChatColor chatChar) {
        this.woolData = woolData;
        this.dyeData = dyeData;
        this.chatColor = chatChar;
    }

    public static DyeColorWrapper getByName (String name) {
        for (DyeColorWrapper wrapper : values()) {
            if (wrapper.name().equalsIgnoreCase(name)) return wrapper;
        }
        return WHITE;
    }

    public static DyeColorWrapper getPrevious(DyeColorWrapper current) {
        int original = current.ordinal();
        if (original == 0) {
            return BLACK;
        }
        return values()[(original - 1)];
    }

    public static DyeColorWrapper getNext(DyeColorWrapper current) {
        if (current.ordinal() == 15) {
            return WHITE;
        }
        return values()[(current.ordinal() + 1)];
    }

    public static DyeColorWrapper getByWoolData(byte data) {
        for (DyeColorWrapper wrapper : values()) {
            if (wrapper.woolData == data)
                return wrapper;
        }
        return null;
    }

    public static DyeColorWrapper getByDyeData(byte data) {
        for (DyeColorWrapper wrapper : values()) {
            if (wrapper.dyeData == data)
                return wrapper;
        }
        return null;
    }

    public int getWoolData() {return this.woolData;}
    public int getDyeData() {return this.dyeData;}
    public char getChatChar() {return chatColor.toString().toCharArray()[1];}
    public ChatColor getChatColor() {return chatColor;}
}
