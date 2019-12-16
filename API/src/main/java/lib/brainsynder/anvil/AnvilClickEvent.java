package lib.brainsynder.anvil;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilClickEvent  {
	private AnvilSlot slot;
    private ItemStack item;
	private String name;
    private Inventory inv;

	private boolean close = true;
    private boolean destroy = true;
    private boolean cancel = true;

	public AnvilClickEvent(Inventory inv, AnvilSlot slot, String name, ItemStack item) {
		this.slot = slot;
		this.name = name;
        this.item = item;
        this.inv = inv;
	}

    public boolean isCanceled () {
        return cancel;
    }

    public void setCanceled (boolean value) {
        cancel = value;
    }

    public Inventory getAnvilInventory () {
        return this.inv;
    }

    public ItemStack getOutputItem () {
        return this.item;
    }

	public AnvilSlot getSlot() {
		return slot;
	}

	public String getName() {
		return name;
	}

	public boolean getWillClose() {
		return close;
	}

	public void setWillClose(boolean close) {
		this.close = close;
	}

	public boolean getWillDestroy() {
		return destroy;
	}

	public void setWillDestroy(boolean destroy) {
		this.destroy = destroy;
	}
}
