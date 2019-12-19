package lib.brainsynder.nms;

import lib.brainsynder.nms.key.BaseActionMessage;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class IActionMessage{
    private static IActionMessage actionMessage = null;

    public abstract void sendMessage (Player player, String message);
    public abstract void sendMessage(Collection<? extends Player> players, String message);

    public static IActionMessage getInstance() {
        if (actionMessage != null) return actionMessage;
        actionMessage = new BaseActionMessage();
        return actionMessage;
    }
}