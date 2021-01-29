package lib.brainsynder.menu;

import lib.brainsynder.utils.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class IconMenu implements Listener, InventoryHolder {

    // Title of the inventory
    private final String title;
    // Size of the inventory, must be a multiple of 9
    private final int size;
    // Current page the user is on
    private int currentPage;
    // Number of pages
    private final int pageCount;

    private OptionPage[] optionPages;

    private Plugin core;
    // Stores the UUID of the player, because if we store the player itself, we start having problems
    private UUID player;
    // Stores the Inventory itself
    private Inventory inventory;

    public IconMenu(String title, int size, int pageCount, Plugin core) {
        this.title = title;
        this.size = size;
        this.core = core;
        this.player = null;
        this.inventory = null;
        this.optionPages = new OptionPage[pageCount];
        for(int i = 0; i < optionPages.length; i++){
            optionPages[i] = new OptionPage(size);
        }
        this.currentPage = 0;
        this.pageCount = pageCount;
        core.getServer().getPluginManager().registerEvents(this, core);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public IconMenu setIcon(int page, int position, Icon icon) {
        this.optionPages[page].optionIcons[position] = icon;
        return this;
    }

    public void open(Player player) {
        this.player = player.getUniqueId();
        inventory = Bukkit.createInventory(this, size, Colorize.translateBungeeHex(title));
        updateContents();
        player.openInventory(inventory);
    }

    public void openNextPage() {
        if (this.currentPage+1 >= pageCount) return;
        this.currentPage++;
        this.updatePage();
    }

    public void openPreviousPage() {
        if (this.currentPage-1 < 0) return;
        this.currentPage--;
        this.updatePage();
    }

    public void openPage(int page) {
        if (page >= pageCount || page < 0) return;
        this.currentPage = page;
        this.updatePage();
    }

    public void updatePage() {
        updateContents();
        getPlayer().updateInventory();
    }

    private void updateContents() {
        for (int i = 0; i < size; i++) {
            Icon icon = this.optionPages[currentPage].optionIcons[i];
            if (icon != null) {
                inventory.setItem(i, icon.item);
            }else {
                inventory.clear(i);
            }
        }
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        core = null;
        optionPages = null;
        player = null;
        inventory = null;
    }

    public int getPageCount() {
        return pageCount;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClosed(InventoryCloseEvent event){
        destroy();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        // Checking if the holder is an instance of IconMenu to prevent potential conflict title comparison can cause.
        if (event.getInventory().getHolder() == this) {
            // Cancel the event, stopping the player pick up the item.
            event.setCancelled(true);
            // Get the raw slot (NOT the slot)
            int slot = event.getRawSlot();
            OptionPage currentPage = this.optionPages[this.currentPage];
            // Make sure the slot is inside the custom inventory, and that the icon clicked isn't null
            if (slot >= 0 && slot < size && currentPage.optionIcons[slot] != null) {
                Icon icon = currentPage.optionIcons[slot];
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, this.currentPage, icon);
                final Player p = (Player)event.getWhoClicked();
                icon.activate(p, e);
                if (e.willClose()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.core, p::closeInventory, 1);
                }
                if (e.willDestroy()) {
                    destroy();
                }
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}