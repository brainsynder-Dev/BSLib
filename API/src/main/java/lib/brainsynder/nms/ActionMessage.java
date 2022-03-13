package lib.brainsynder.nms;

import lib.brainsynder.utils.Colorize;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionMessage {
    private static ActionMessage actionMessage = null;

    public void sendMessage(Collection<? extends Player> players, String message) {
        for (Player player : players)
            sendMessage(player, message);
    }

    public void sendMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ComponentSerializer.parse("{\"text\": \"" + Colorize.translateBungeeHex(message) + "\"}"));
    }

    public static ActionMessage getInstance() {
        if (actionMessage != null) return actionMessage;
        actionMessage = new ActionMessage();
        return actionMessage;
    }
}