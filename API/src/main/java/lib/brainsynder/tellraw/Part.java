package lib.brainsynder.tellraw;

import com.eclipsesource.json.JsonObject;
import lib.brainsynder.strings.Colorize;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.List;

public final class Part {
    public ChatColor color = null;
    public Color customColor = null;
    public List<ChatColor> styles = null;
    public String text = "";
    public String font = null;


    public String clickActionName = null;
    public String clickActionData = null;
    public String hoverActionName = null;
    public String hoverActionData = null;

    public Part() {
    }

    public Part(String text) {
        this.text = text;
    }

    /**
     * It converts the message into a JSON object
     *
     * @return A JsonObject
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.set("text", text);

        if (this.color != null) {
            // Uses the ChatColor variable (Default MC colors)
            json.set("color", this.color.name().toLowerCase());
        } else if ((customColor != null)) {
            // Uses the Color (Allows RGB/HEX colors) [1.16+]
            // Since 1.16 added the ability to have RGB/HEX colored messages
            json.set("color", Colorize.toHex(customColor.getRed(), customColor.getGreen(), customColor.getBlue()));
        }

        if (this.font != null) json.set("font", font.toLowerCase());

        if (this.styles != null) {
            for (ChatColor style : this.styles) json.set(style.name().toLowerCase(), true);
        }

        if ((this.clickActionName != null) && (this.clickActionData != null)) {
            JsonObject action = new JsonObject();
            action.set("action", clickActionName);
            action.set("value", clickActionData);
            json.set("clickEvent", action);
        }

        if ((this.hoverActionName != null) && (this.hoverActionData != null)) {
            JsonObject action = new JsonObject();
            action.set("action", hoverActionName);
            action.set("value", hoverActionData);
            json.set("hoverEvent", action);
        }

        return json;
    }
}