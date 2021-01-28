package lib.brainsynder.nms;

import lib.brainsynder.reflection.FieldAccessor;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.Colorize;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collection;

public class TabMessage {
    private String header = "";
    private String footer = "";
    private static TabMessage tabMessage = null;
    private final FieldAccessor headerField;
    private final FieldAccessor footerField;
    private final Method serializerMethod;

    private TabMessage() {
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

    public TabMessage setHeader(String header) {
        this.header = header;
        return this;
    }
    public TabMessage setFooter(String footer) {
        this.footer = footer;
        return this;
    }
    public TabMessage setHeaderFooter(String header, String footer) {
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


    public static TabMessage getInstance() {
        if (tabMessage != null) return tabMessage;
        tabMessage = new TabMessage();
        return tabMessage;
    }

    private Object buildMessage (String text) {
        return Reflection.invoke(serializerMethod, null, "{\"text\": \"" + Colorize.translateBungeeHex(text) + "\"}");
    }
}