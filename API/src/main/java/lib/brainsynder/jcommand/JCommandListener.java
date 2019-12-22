package lib.brainsynder.jcommand;

import lib.brainsynder.utils.ReturnValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;

public class JCommandListener implements Listener {
    private JCommand command;

    public JCommandListener(JCommand command){
        this.command = command;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onForcedJSONCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        String cmd = command;
        for (Map.Entry<String, ReturnValue<Player>> entry : this.command.getCommands().entrySet()) {
            String name = entry.getKey();
            if(name.equalsIgnoreCase(command)) {
                // Run the command
                entry.getValue().run(event.getPlayer());
                event.setCancelled(true);
                cmd = name;
                break;
            }
        }
        // After the command is run, we remove it to prevent spamming
        this.command.removeCommand(cmd);
    }
}