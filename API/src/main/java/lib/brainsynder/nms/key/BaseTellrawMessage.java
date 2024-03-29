package lib.brainsynder.nms.key;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.google.common.collect.Lists;
import com.google.gson.stream.JsonWriter;
import lib.brainsynder.nms.Tellraw;
import lib.brainsynder.utils.Colorize;
import lib.brainsynder.utils.MessagePart;
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
import java.util.List;

public class BaseTellrawMessage extends Tellraw {
    private List<MessagePart> messageParts = new ArrayList<>();
    private String jsonString = null;
    private boolean dirty = false;

    /**
     * Will convert the string from "&cString" to the JSON equivalent used for the tellraw command
     *
     * @param text - string used for the conversion
     */
    public BaseTellrawMessage fromLegacy0 (String text) {
        if ((text == null) || text.isEmpty()) throw new NullPointerException("Missing text input");
        BaseTellrawMessage message = new BaseTellrawMessage ();
        List<MessagePart> split = splitMessageToParts(text);
        message.messageParts = split;
        message.jsonString = convertParts2Json(split).toString();
        return message;
    }

    public BaseTellrawMessage color(Object obj) {
        latest().color = null;
        latest().customColor = null;
        if (obj instanceof String) {
            latest().customColor = hex2Rgb((String) obj);
        }else if (obj instanceof java.awt.Color) {
            java.awt.Color color = (java.awt.Color) obj;
            latest().customColor = hex2Rgb(MessagePart.toHex(color.getRed(), color.getGreen(), color.getBlue()));

        }else if (obj instanceof Color) {
            latest().customColor = (Color) obj;

        }else if (obj instanceof net.md_5.bungee.api.ChatColor) {
            net.md_5.bungee.api.ChatColor color = (net.md_5.bungee.api.ChatColor) obj;
            try {
                Method method = obj.getClass().getMethod("getColor");
                return color (method.invoke(color));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return color(ChatColor.valueOf(color.name()));
            }

        }else if (obj instanceof ChatColor) {
            ChatColor color = (ChatColor) obj;
            if (!color.isColor()) throw new IllegalArgumentException(color.name() + " is not a color");

            latest().color = color;
        }else{
            throw new IllegalArgumentException(obj.getClass().getSimpleName()+" is not a valid input.");
        }
        this.dirty = true;
        return this;
    }
    private Color hex2Rgb(String hex) {
        if (hex.startsWith("#") && hex.length() == 7) {
            int rgb;
            try {
                rgb = Integer.parseInt(hex.substring(1), 16);
            } catch (NumberFormatException var7) {
                throw new IllegalArgumentException("Illegal hex string " + hex);
            }
            return Color.fromRGB(rgb);
        }
        return Color.RED;
    }

    /**
     * This feature was added to ChatComponents in 1.16
     *
     * @param font - Resource Pack path to the font
     *             Example: minecraft:default - Will result in the default minecraft texture
     */
    public BaseTellrawMessage font(String font) {
        if ((font == null) || (font.isEmpty())) {
            throw new NullPointerException("font can not be null");
        }
        latest().font = font;
        this.dirty = true;
        return this;
    }
    public BaseTellrawMessage style(ChatColor... styles) {
        for (ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }
        latest().styles = Lists.newArrayList(styles);
        this.dirty = true;
        return this;
    }
    public BaseTellrawMessage file(String path) {
        onClick("open_file", path);
        return this;
    }
    public BaseTellrawMessage link(String url) {
        onClick("open_url", url);
        return this;
    }
    public BaseTellrawMessage suggest(String command) {
        onClick("suggest_command", command);
        return this;
    }
    public BaseTellrawMessage command(String command) {
        onClick("run_command", command);
        return this;
    }
    public BaseTellrawMessage achievementTooltip(String name) {
        onHover("show_achievement", "achievement." + name);
        return this;
    }
    public BaseTellrawMessage itemTooltip(String itemJSON) {
        onHover("show_item", itemJSON);
        return this;
    }
    public BaseTellrawMessage tooltip(List<String> lines) {
        return tooltip(lines.toArray(new String[lines.size()]));
    }
    public BaseTellrawMessage tooltip(String... lines) {
        onHover("show_text", combineArray(0, "\n", lines));
        return this;
    }
    public BaseTellrawMessage then(Object obj) {
        if (obj instanceof MessagePart) {
            this.messageParts.add((MessagePart) obj);
        } else {
            this.messageParts.add(new MessagePart(obj.toString()));
        }
        this.dirty = true;
        return this;
    }

    @Override
    public BaseTellrawMessage removeLastPart() {
        if (messageParts.isEmpty()) return this;
        messageParts.remove( (messageParts.size() - 1) );
        return this;
    }

    public String toJSONString() {
        if ((!this.dirty) && (this.jsonString != null)) {
            return this.jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            if (this.messageParts.size() == 1) {
                latest().writeJson(json);
            } else {
                json.beginObject().name("text").value("").name("extra").beginArray();
                for (MessagePart part : this.messageParts) {
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

    @Override
    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            send((Player)sender);
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

    @Override
    public void send(Player player) {
        player.spigot().sendMessage(ComponentSerializer.parse(toJSONString()));
    }



    private MessagePart latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }
    private String combineArray(int startIndex, String separator, String... stringArray) {
        return combineArray(startIndex, stringArray.length, separator, stringArray);
    }
    private String combineArray(int startIndex, int endIndex, String separator, String... stringArray) {
        if(stringArray != null && startIndex < endIndex) {
            StringBuilder builder = new StringBuilder();

            for(int i = startIndex; i < endIndex; ++i) {
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
        MessagePart latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }
    private void onHover(String name, String data) {
        MessagePart latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }




    // Generates JSON: {"text":"","extra":[{"text","color"}]}
    public static JsonObject convertParts2Json (List<MessagePart> parts) {
        JsonObject json = new JsonObject();
        json.add("text", "");

        JsonArray extra = new JsonArray();
        for (MessagePart part : parts) {
            extra.add(part.toJson());
        }
        json.add("extra", extra);
        return json;
    }

    private List<MessagePart> splitMessageToParts(String value) {
        List<MessagePart> parts = new ArrayList<>();
        // String is empty
        if ((value == null) || value.isEmpty()) return parts;
        value = value.replace(org.bukkit.ChatColor.COLOR_CHAR, '&');

        if (value.contains("&")) {
            String[] args = value.split("&");

            for (String string : args) {
                if (string == null) continue;
                if (string.isEmpty()) continue;

                // Has hex color

                MessagePart part = new MessagePart();
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
                        if (color.isFormat()){
                            if (part.styles == null) part.styles = Lists.newArrayList();
                            part.styles.add(color);
                        } else {
                            part.color = color;
                        }
                    }
                }
                parts.add(part);
            }
        } else {
            // 'value' does not contain '&'
            parts.add(new MessagePart(value));
        }
        return parts;
    }

    private Color hex2Color(String hex) {
        return Color.fromRGB(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }
}