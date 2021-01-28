package lib.brainsynder.nms;

import lib.brainsynder.apache.EnumUtils;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.Colorize;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;

@Deprecated
/**
 * This is @Deprecated because of Player#sendTitle method (and that fact that subtitles cant be sent with the code...)
 */
public class TitleMessage {
    private int fadeIn = 5, stay = 5, fadeOut = 5;
    private String header=null, subHeader=null;

    private final Constructor packet;
    private final Object TITLE, SUBTITLE;
    private final Method serializer;

    public TitleMessage () {
        Class chatSerializer = Reflection.getNmsClass("IChatBaseComponent$ChatSerializer");
        serializer = Reflection.getMethod(chatSerializer, "a", String.class);

        Class titleAction = Reflection.getNmsClass("PacketPlayOutTitle$EnumTitleAction");

        TITLE = EnumUtils.getEnum(titleAction, "TITLE");
        SUBTITLE = EnumUtils.getEnum(titleAction, "SUBTITLE");

        packet = Reflection.getConstructor(Reflection.getNmsClass("PacketPlayOutTitle"), titleAction, Reflection.getNmsClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
    }

    public TitleMessage setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }
    public TitleMessage setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }
    public TitleMessage setStay(int stay) {
        this.stay = stay;
        return this;
    }
    public TitleMessage setHeader(String header) {
        this.header = header;
        return this;
    }
    public TitleMessage setSubHeader(String subHeader) {
        this.subHeader = subHeader;
        return this;
    }

    public void sendMessage(Player player) {
        player.sendTitle((header != null) ? Colorize.translateBungeeHex(header) : "",
                (subHeader != null) ? Colorize.translateBungeeHex(subHeader) : "",
                fadeIn, stay, fadeOut);
//        //PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction, IChatBaseComponent, fadeIn, stay, fadeOut)
//        try {
//            if ((header != null) && (!header.isEmpty())) Reflection.sendPacket(player, Reflection.initiateClass(packet, TITLE, buildMessage(header), fadeIn, stay, fadeOut));
//            if ((subHeader != null) && (!subHeader.isEmpty())) Reflection.sendPacket(player, Reflection.initiateClass(packet, SUBTITLE, buildMessage(subHeader), fadeIn, stay, fadeOut));
//        }catch (Exception e) {
//            player.sendMessage (header + " " + subHeader);
//        }
    }

    public void sendMessage(Collection<? extends Player> players) {
        for (Player player : players) sendMessage(player);
    }

    private Object buildMessage (String text) {
        return Reflection.invoke(serializer, null, "{\"text\": \"" + text + "\"}");
    }
}
