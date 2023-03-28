package lib.brainsynder.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Code provided by https://www.spigotmc.org/threads/594005/
 * With slight modifications by brainsynder
 *
 * @author 7smile7
 * @author brainsynder
 */
public abstract class InventoryGUI implements InventoryHandler {

  private final Inventory inventory;
  private final Map<Integer, InventoryButton> buttonMap = new HashMap<>();

  public InventoryGUI() {
    this.inventory = this.createInventory();
  }

  /**
   * This function returns the inventory of the player.
   *
   * @return The inventory object.
   */
  public Inventory getInventory() {
    return this.inventory;
  }

  /**
   * It adds a button to the button map
   *
   * @param slot The slot in the inventory that the button will be placed in.
   * @param button The button to add to the inventory
   */
  public void addButton(int slot, InventoryButton button) {
    this.buttonMap.put(slot, button);
  }

  /**
   * For each button in the button map, set the item in the inventory to the icon of the button.
   *
   * @param player The player who's inventory is being decorated.
   */
  public void decorate(Player player) {
    this.buttonMap.forEach((slot, button) -> {
      ItemStack icon = button.getIconCreator().apply(player);
      this.inventory.setItem(slot, icon);
    });
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    event.setCancelled(true);
    int slot = event.getSlot();
    InventoryButton button = this.buttonMap.get(slot);
    if (button != null) {
      button.getEventConsumer().accept(event);
    }
  }

  @Override
  public void onOpen(InventoryOpenEvent event) {
    this.decorate((Player) event.getPlayer());
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
  }

  /**
   * Create an inventory object for the current user.
   *
   * @return An Inventory object.
   */
  protected abstract Inventory createInventory();

}