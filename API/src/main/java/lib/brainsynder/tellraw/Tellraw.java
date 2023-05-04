package lib.brainsynder.tellraw;

import com.google.common.collect.Lists;
import com.google.gson.stream.JsonWriter;
import lib.brainsynder.strings.Colorize;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Tellraw {
    private List<Part> messageParts = new ArrayList<>();
    private String jsonString = null;
    private boolean dirty = false;


    /**
     * This function returns a new Tellraw object with the text set to the text parameter.
     *
     * @param text The text to be displayed.
     * @return A new instance of Tellraw.
     */
    public static Tellraw getInstance(String text) {
        return new Tellraw().then(text);
    }

    /**
     * Takes a string of text that already contains ChatColors with the & symbol
     *
     * @param text The original text
     * @return A new Tellraw object.
     */
    public static Tellraw fromLegacy(String text) {
        return new Tellraw().fromLegacy0(text);
    }

    /**
     * Will convert the string from "&cString" to the JSON equivalent used for the tellraw command
     *
     * @param text - string used for the conversion
     */
    private Tellraw fromLegacy0(String text) {
        if ((text == null) || text.isEmpty()) throw new NullPointerException("Missing text input");
        Tellraw message = new Tellraw();
        List<Part> split = Colorize.splitMessageToParts(text);
        message.messageParts = split;
        message.jsonString = Colorize.convertParts2Json(split).toString();
        return message;
    }

    /**
     * Adds color to the latest message, It cant be in 5 different ways:
     * -   String - This will be in the hex color format (#FFFFFF)
     * -   java.awt.Color
     * -   org.bukkit.Color
     * -   net.md_5.bungee.api.ChatColor
     * -   org.bukkit.ChatColor
     *
     * @param obj The object to be converted to a color.
     * @return The Tellraw object.
     */
    public Tellraw color(Object obj) {
        latest().color = null;
        latest().customColor = null;

        if (obj instanceof String) {
            latest().customColor = Colorize.hex2Color((String) obj);
        } else if (obj instanceof java.awt.Color) {
            java.awt.Color color = (java.awt.Color) obj;
            latest().customColor = Colorize.hex2Color(Colorize.toHex(color.getRed(), color.getGreen(), color.getBlue()));

        } else if (obj instanceof Color) {
            latest().customColor = (Color) obj;

        } else if (obj instanceof net.md_5.bungee.api.ChatColor) {
            net.md_5.bungee.api.ChatColor color = (net.md_5.bungee.api.ChatColor) obj;
            try {
                Method method = obj.getClass().getMethod("getColor");
                return color(method.invoke(color));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return color(ChatColor.valueOf(color.name()));
            }

        } else if (obj instanceof ChatColor) {
            ChatColor color = (ChatColor) obj;
            if (!color.isColor()) throw new IllegalArgumentException(color.name() + " is not a color");

            latest().color = color;
        } else {
            throw new IllegalArgumentException(obj.getClass().getSimpleName() + " is not a valid input.");
        }
        this.dirty = true;
        return this;
    }

    /**
     * Sets the font of the latest message.
     * <p>
     * Example:
     * minecraft:default - Default minecraft font
     * minecraft:illageralt - Language of the Illagers
     * minecraft:alt - Enchantment language
     * minecraft:uniform - Similar to the default one
     *
     * @param font The font to use.
     * @return The Tellraw object
     */
    public Tellraw font(String font) {
        if ((font == null) || (font.isEmpty())) throw new NullPointerException("font can not be null");

        latest().font = font;
        this.dirty = true;
        return this;
    }

    /**
     * This function sets the style of the latest message.
     *
     * @return The Tellraw object
     */
    public Tellraw style(ChatColor... styles) {
        for (ChatColor style : styles) {
            if (!style.isFormat()) throw new IllegalArgumentException(style.name() + " is not a style");
        }

        latest().styles = Lists.newArrayList(styles);
        this.dirty = true;
        return this;
    }

    /**
     * When the player clicks on this text, open the file at the given path.
     *
     * @param path The path to the file to open.
     * @return The Tellraw object.
     */
    public Tellraw file(String path) {
        onClick("open_file", path);
        return this;
    }

    /**
     * Adds a click event to the tellraw message that opens the specified URL in the player's browser.
     *
     * @param url The URL to open when the player clicks the text.
     * @return The Tellraw object.
     */
    public Tellraw link(String url) {
        onClick("open_url", url);
        return this;
    }

    /**
     * When the player clicks on this text, the client will suggest the given command to the player.
     *
     * @param command The command to suggest
     * @return The Tellraw object.
     */
    public Tellraw suggest(String command) {
        onClick("suggest_command", command);
        return this;
    }

    /**
     * Adds a command to the click event of the tellraw message.
     *
     * @param command The command to run when the player clicks the text.
     * @return The Tellraw object.
     */
    public Tellraw command(String command) {
        onClick("run_command", command);
        return this;
    }

    /**
     * When the player hovers over this text, show them the achievement with the given name.
     *
     * @param name The name of the achievement.
     * @return The Tellraw object.
     */
    public Tellraw achievementTooltip(String name) {
        onHover("show_achievement", "achievement." + name);
        return this;
    }

    /**
     * Sets the hover event to show the item tooltip of the item specified by the given JSON string.
     *
     * @param itemJSON The item JSON to show in the tooltip.
     * @return The Tellraw object.
     */
    public Tellraw itemTooltip(String itemJSON) {
        onHover("show_item", itemJSON);
        return this;
    }

    /**
     * Returns a Tellraw object with the given lines as the tooltip.
     *
     * @param lines The lines of text to display in the tooltip.
     * @return A Tellraw object.
     */
    public Tellraw tooltip(List<String> lines) {
        return tooltip(lines.toArray(new String[lines.size()]));
    }

    /**
     * Adds a tooltip to the tellraw message.
     *
     * @return The Tellraw object.
     */
    public Tellraw tooltip(String... lines) {
        onHover("show_text", combineArray(0, "\n", lines));
        return this;
    }

    /**
     * If the object is a Part, add it to the messageParts list, otherwise, add a new Part with the object's toString() as
     * the text
     *
     * @param obj The object to add to the message.
     * @return The Tellraw object
     */
    public Tellraw then(Object obj) {

        if (obj instanceof Part) {
            this.messageParts.add((Part) obj);
        } else {
            this.messageParts.add(new Part(obj.toString()));
        }
        this.dirty = true;
        return this;
    }

    /**
     * It removes the last part of the message
     *
     * @return The Tellraw object.
     */
    public Tellraw removeLastPart() {
        if (messageParts.isEmpty()) return this;
        messageParts.remove((messageParts.size() - 1));
        return this;
    }

    /**
     * If the message is dirty, then it will create a new JSON string, otherwise it will return the cached JSON string
     *
     * @return A JSON string
     */
    public String toJSONString() {
        if ((!this.dirty) && (this.jsonString != null)) return this.jsonString;

        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            if (this.messageParts.size() == 1) {
                latest().writeJson(json);
            } else {
                json.beginObject().name("text").value("").name("extra").beginArray();
                for (Part part : this.messageParts) {
                    part.writeJson(json);
                }
                json.endArray().endObject();
                json.close();
            }
        } catch (IOException e) {
        }
        this.jsonString = string.toString();
        this.dirty = false;
        return this.jsonString;
    }

    /**
     * It loops through all the parts of the message, and appends the color, styles, and text to a StringBuilder
     *
     * @param sender The CommandSender to send the message to.
     */
    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            send((Player) sender);
            return;
        }

        StringBuilder builder = new StringBuilder();
        messageParts.forEach(part -> {
            if (part.color != null) builder.append(part.color);
            if (part.customColor != null) builder.append(Colorize.fetchColor(part.customColor));
            if ((part.styles != null) && !part.styles.isEmpty()) {
                for (ChatColor style : part.styles) builder.append(style);
            }
            builder.append(part.text);
        });
        sender.sendMessage(builder.toString());
    }

    /**
     * It sends a message to a player
     *
     * @param player The player to send the message to.
     */
    public void send(Player player) {
        player.spigot().sendMessage(ComponentSerializer.parse(toJSONString()));
    }

    /**
     * This function sends the message to all the players in the list.
     *
     * @param players The players to send the message to.
     */
    public void send(Iterable<Player> players) {
        for (Player player : players)
            send(player);
    }

    /**
     * Send this message to all players in the given collection.
     *
     * @param players The players to send the message to.
     */
    public void send(Collection<? extends Player> players) {
        for (Player player : players)
            send(player);
    }


    private Part latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }

    private String combineArray(int startIndex, String separator, String... stringArray) {
        return combineArray(startIndex, stringArray.length, separator, stringArray);
    }

    private String combineArray(int startIndex, int endIndex, String separator, String... stringArray) {
        if (stringArray != null && startIndex < endIndex) {
            StringBuilder builder = new StringBuilder();

            for (int i = startIndex; i < endIndex; ++i) {
                builder.append(Colorize.translateBungeeHex(stringArray[i]));
                builder.append(separator);
            }

            builder.delete(builder.length() - separator.length(), builder.length());
            return builder.toString();
        } else {
            return "";
        }
    }


    private void onClick(String name, String data) {
        Part latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }


    private void onHover(String name, String data) {
        Part latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }
}