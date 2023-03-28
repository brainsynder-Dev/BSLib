package lib.brainsynder.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Code provided by https://www.spigotmc.org/threads/594005/
 * With slight modifications by brainsynder
 *
 * @author 7smile7
 * @author brainsynder
 */
public class GUIManager {
    private final GUIListener GUI_LISTENER;
    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();

    public GUIManager (Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(GUI_LISTENER = new GUIListener(), plugin);
    }

    /**
     * Unregister all listeners that are registered to the GUI_LISTENER object.
     */
    public void cleanup () {
        HandlerList.unregisterAll(GUI_LISTENER);
    }

    /**
     * It registers the inventory with the plugin, and then opens it for the player
     *
     * @param gui The InventoryGUI object you want to open.
     * @param player The player who will be opening the inventory.
     */
    public void openGUI(InventoryGUI gui, Player player) {
        this.registerHandledInventory(gui.getInventory(), gui);
        player.openInventory(gui.getInventory());
    }

    /**
     * It adds an inventory to the list of active inventories
     *
     * @param inventory The inventory you want to register.
     * @param handler The InventoryHandler that will handle the inventory.
     */
    public void registerHandledInventory(Inventory inventory, InventoryHandler handler) {
        this.activeInventories.put(inventory, handler);
    }

    /**
     * It removes the inventory from the list of active inventories
     *
     * @param inventory The inventory to unregister.
     */
    public void unregisterInventory(Inventory inventory) {
        this.activeInventories.remove(inventory);
    }

    /**
     * If the inventory that was clicked is one of the active inventories, call the onClick function of the inventory
     * handler
     *
     * @param event The InventoryClickEvent that was fired.
     */
    public void handleClick(InventoryClickEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onClick(event);
        }
    }

    /**
     * If the inventory is in the activeInventories map, call the onOpen function of the InventoryHandler
     *
     * @param event The event that was fired.
     */
    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onOpen(event);
        }
    }

    /**
     * If the inventory is registered, call the onClose function of the InventoryHandler and unregister the inventory
     *
     * @param event The InventoryCloseEvent that was fired.
     */
    public void handleClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHandler handler = this.activeInventories.get(inventory);
        if (handler != null) {
            handler.onClose(event);
            this.unregisterInventory(inventory);
        }
    }

    public class GUIListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            handleClick(event);
        }

        @EventHandler
        public void onOpen(InventoryOpenEvent event) {
            handleOpen(event);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            handleClose(event);
        }
    }
}