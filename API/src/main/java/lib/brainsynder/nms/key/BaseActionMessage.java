package lib.brainsynder.nms.key;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.nms.IActionMessage;
import lib.brainsynder.particle.ParticleReflection;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;

public class BaseActionMessage extends IActionMessage {
    private Constructor packet;
    private Object value;
    private Method serializerMethod;

    public BaseActionMessage() {
        Class packetPlayOutChatClass = Reflection.getNmsClass("PacketPlayOutChat");
        try {
            if (ServerVersion.isEqualOld(ServerVersion.v1_11_R1)) {
                packet = packetPlayOutChatClass.getConstructor(Reflection.getNmsClass("IChatBaseComponent"), byte.class);
                value = (byte)2;
            }else{
                packet = packetPlayOutChatClass.getConstructor(Reflection.getNmsClass("IChatBaseComponent"), Reflection.getNmsClass("ChatMessageType"));
                value = Reflection.invoke(Reflection.getMethod(Reflection.getNmsClass("ChatMessageType"), "a", byte.class), null, (byte)2);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Class chatSerializer = Reflection.getNmsClass("IChatBaseComponent$ChatSerializer");
        serializerMethod = Reflection.getMethod(chatSerializer, "a", String.class);
    }

    @Override
    public void sendMessage(Collection<? extends Player> players, String message) {
        for (Player player : players)
            sendMessage(player, message);
    }

    @Override
    public void sendMessage(Player player, String message) {
        ParticleReflection.sendPacket(player, Reflection.initiateClass(packet, buildMessage(ChatColor.translateAlternateColorCodes('&', message)), value));
    }

    private Object buildMessage (String text) {
        return Reflection.invoke(serializerMethod, null, "{\"text\": \"" + text + "\"}");
    }
}