package lib.brainsynder.nms;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.anvil.AnvilSlot;
import lib.brainsynder.anvil.IAnvilClickEvent;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;

public class IAnvilGUI {
    private Player player;
    private Plugin plugin;
    private IAnvilClickEvent handler;
	private static Constructor constructor = null;


	public IAnvilGUI (Plugin plugin, Player player, IAnvilClickEvent handler) {
        this.player = player;
        this.plugin = plugin;
        this.handler = handler;
	}

    public Plugin getPlugin() {
        return plugin;
    }

    public IAnvilClickEvent getHandler() {
        return handler;
    }

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item){}
	public void open(){}

	public static IAnvilGUI getInstance (Plugin plugin, Player player, IAnvilClickEvent handler) {
	    if (constructor == null) {
            try {
                Class clazz = Class.forName("lib.brainsynder.nms.versions."+ ServerVersion.getVersion().name() +".AnvilHandler");
                constructor = clazz.getConstructor(Plugin.class, Player.class, IAnvilClickEvent.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }

        return Reflection.initiateClass(constructor, plugin, player, handler);
	}
}
