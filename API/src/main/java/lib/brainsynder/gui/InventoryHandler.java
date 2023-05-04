package lib.brainsynder.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Code provided by https://www.spigotmc.org/threads/594005/
 * With slight modifications by brainsynder
 *
 * @author 7smile7
 * @author brainsynder
 */
public interface InventoryHandler {

    /**
     * > This function is called when a player clicks on an item in the inventory
     *
     * @param event The InventoryClickEvent that was fired.
     */
    void onClick(InventoryClickEvent event);

    /**
     * This function is called when a player opens an inventory.
     *
     * @param event The event that was called.
     */
    void onOpen(InventoryOpenEvent event);

    /**
     * This function is called when a player closes an inventory.
     *
     * @param event The event that was called.
     */
    void onClose(InventoryCloseEvent event);
}