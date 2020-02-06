package lib.brainsynder.jcommand;

import lib.brainsynder.math.MathUtils;
import lib.brainsynder.utils.ReturnValue;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to complete tasks when you click a tellraw message
 */
public class JCommand {
    private Map<String, ReturnValue<Player>> commands = new HashMap<>();

    /**
     *
     * @param onExecute - What happens when the player runs the command
     * @return - Will return the command name {EG: /jcommand_1262378}
     */
    public String getCommand (ReturnValue<Player> onExecute) {
        return getCommand("jcommand_", onExecute);
    }

    /**
     *
     * @param prefix - The Custom Command prefix
     * @param onExecute - What happens when the player runs the command
     * @return - Will return the command name {EG: /jcommand_1262378}
     */
    public String getCommand (String prefix, ReturnValue<Player> onExecute) {
        String command = "/"+prefix+ randomID("1234567890", 5);
        commands.put(command, onExecute);
        return command;
    }

    /**
     *
     * @return - All the commands that are in use
     */
    public Map<String, ReturnValue<Player>> getCommands() {
        return commands;
    }

    /**
     * Will remove the selected command from the active commands
     *
     * @param command - the command generated by {@link JCommand#getCommand}
     */
    public void removeCommand (String command) {
        commands.remove(command);
    }

    /**
     * Will generate a random ID based on the characters supplied
     *
     * @param chars - The Characters used to generate the ID
     * @param length - The length of the ID
     * @return - A Randomized ID value to the length selected
     */
    private String randomID (String chars, int length) {
        int charLength = chars.length()-1;
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < length; i++) {
            id.append(chars.charAt(MathUtils.random(0, charLength)));
        }

        return id.toString();
    }
}