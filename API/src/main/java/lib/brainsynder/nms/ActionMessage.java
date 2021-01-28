package lib.brainsynder.nms;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.Colorize;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;

public class ActionMessage {
    private Constructor packet;
    private Object value;
    private final Method serializerMethod;
    private static ActionMessage actionMessage = null;
    private Object uuid;

    public ActionMessage() {
        Class packetPlayOutChatClass = Reflection.getNmsClass("PacketPlayOutChat");
        try {
            if (ServerVersion.isEqualOld(ServerVersion.v1_11_R1)) {
                packet = packetPlayOutChatClass.getConstructor(Reflection.getNmsClass("IChatBaseComponent"), byte.class);
                value = (byte)2;
            }else{
                if (ServerVersion.isEqualNew(ServerVersion.v1_16_R1)) {
                    uuid = Reflection.getFieldValue(Reflection.getField(Reflection.getNmsClass("SystemUtils"), "b"), null);
                    packet = packetPlayOutChatClass.getConstructor(Reflection.getNmsClass("IChatBaseComponent"), Reflection.getNmsClass("ChatMessageType"), UUID.class);
                }else{
                    packet = packetPlayOutChatClass.getConstructor(Reflection.getNmsClass("IChatBaseComponent"), Reflection.getNmsClass("ChatMessageType"));
                }
                value = Reflection.invoke(Reflection.getMethod(Reflection.getNmsClass("ChatMessageType"), "a", byte.class), null, (byte)2);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Class chatSerializer = Reflection.getNmsClass("IChatBaseComponent$ChatSerializer");
        serializerMethod = Reflection.getMethod(chatSerializer, "a", String.class);
    }

    public void sendMessage(Collection<? extends Player> players, String message) {
        for (Player player : players)
            sendMessage(player, message);
    }

    public void sendMessage(Player player, String message) {
        if (ServerVersion.isEqualNew(ServerVersion.v1_16_R1)) {
            Reflection.sendPacket(player, Reflection.initiateClass(packet, buildMessage(Colorize.translateBungeeHex(message)), value, uuid));
        }else{
            Reflection.sendPacket(player, Reflection.initiateClass(packet, buildMessage(Colorize.translateBungeeHex(message))));
        }
    }

    private Object buildMessage (String text) {
        return Reflection.invoke(serializerMethod, null, "{\"text\": \"" + text + "\"}");
    }



    public static ActionMessage getInstance() {
        if (actionMessage != null) return actionMessage;
        actionMessage = new ActionMessage();
        return actionMessage;
    }
}