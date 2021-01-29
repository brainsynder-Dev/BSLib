package lib.brainsynder.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Icon {
    public ItemStack item;
    private String[] commands;
    private OptionClickEventHandler handler;

    public Icon(ItemStack item) {
        this.item = item;
    }

    public Icon withCommands(String... commands) {
        this.commands = commands;
        return this;
    }

    public Icon withHandler(OptionClickEventHandler handler) {
        this.handler = handler;
        return this;
    }

    public void activate(Player player, OptionClickEvent event) {
        if (commands != null) {
            for (String command : commands) {
                Bukkit.dispatchCommand(player, command);
            }
        }
        if (handler != null) {
            handler.onOptionClick(event);
        }
    }

    public ItemStack getItem() {
        return item;
    }
}