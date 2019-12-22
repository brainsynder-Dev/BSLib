package lib.brainsynder.nms;

import lib.brainsynder.reflection.FieldAccessor;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collection;

public class ITabMessage {
    private String header = "";
    private String footer = "";
    private static ITabMessage tabMessage = null;
    private FieldAccessor headerField, footerField;
    private Method serializerMethod;

    private ITabMessage() {
        Class chatSerializer = Reflection.getNmsClass("IChatBaseComponent$ChatSerializer");
        serializerMethod = Reflection.getMethod(chatSerializer, "a", String.class);

        Class packet = Reflection.getNmsClass("PacketPlayOutPlayerListHeaderFooter");
        headerField = FieldAccessor.getField(packet, "a", Reflection.getNmsClass("IChatBaseComponent"));
        footerField = FieldAccessor.getField(packet, "b", Reflection.getNmsClass("IChatBaseComponent"));
    }

    public String getHeader () {
        return header;
    }
    public String getFooter() {
        return footer;
    }

    public ITabMessage setHeader(String header) {
        this.header = header;
        return this;
    }
    public ITabMessage setFooter(String footer) {
        this.footer = footer;
        return this;
    }
    public ITabMessage setHeaderFooter(String header, String footer) {
        this.header = header;
        this.footer = footer;
        return this;
    }

    public void send(Player player) {
        try {
            Object packet = Reflection.getNmsClass("PacketPlayOutPlayerListHeaderFooter").newInstance();
            if ((header != null) && (!header.isEmpty())) headerField.set(packet, buildMessage(header));
            if ((footer != null) && (!footer.isEmpty())) footerField.set(packet, buildMessage(footer));
            Reflection.sendPacket(player, packet);
        } catch (InstantiationException | IllegalAccessException ignored) {}
    }
    public void send(Iterable<Player> players) {
        for (Player player : players)
            send (player);
    }
    public void send(Collection<? extends Player> players) {
        for (Player player : players)
            send (player);
    }


    public static ITabMessage getInstance() {
        if (tabMessage != null) return tabMessage;
        tabMessage = new ITabMessage();
        return tabMessage;
    }

    private Object buildMessage (String text) {
        return Reflection.invoke(serializerMethod, null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}");
    }
}