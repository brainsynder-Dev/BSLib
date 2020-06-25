package lib.brainsynder.nms.versions.v1_16_R1;

import lib.brainsynder.anvil.AnvilClickEvent;
import lib.brainsynder.anvil.AnvilSlot;
import lib.brainsynder.anvil.IAnvilClickEvent;
import lib.brainsynder.nms.AnvilGUI;
import lib.brainsynder.reflection.FieldAccessor;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class AnvilHandler extends AnvilGUI {
    private Map<AnvilSlot, ItemStack> items = new HashMap<>();
    private Inventory inv;
    private CustomListener listener;

    public AnvilHandler (Plugin plugin, Player player, IAnvilClickEvent handler) {
        super(plugin, player, handler);
    }

    @Override
    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    @Override
    public void open() {
        EntityPlayer p = ((CraftPlayer) getPlayer()).getHandle();
        int c = p.nextContainerCounter();
        AnvilContainer container = new AnvilContainer(c, p);
        this.inv = container.getBukkitView().getTopInventory();
        for (AnvilSlot slot : this.items.keySet()) {
            this.inv.setItem(slot.getSlot(), items.get(slot));
        }
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, Containers.ANVIL, new ChatMessage("Anvil")));
        p.activeContainer = container;
        FieldAccessor<Integer> field = FieldAccessor.getField(Container.class, "windowId", Integer.TYPE);
        field.set(p.activeContainer, c);
        p.activeContainer.addSlotListener(p);
    }

    private void destroy() {
        items = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }

    private static class AnvilContainer extends ContainerAnvil {
        AnvilContainer(int id, EntityHuman entity) {
            super(id, entity.inventory, ContainerAccess.at(entity.world, new BlockPosition(0, 0, 0)));
            checkReachable = false;
        }

        public boolean a(EntityHuman entityhuman) {
            return true;
        }
        public boolean canUse(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public void e() {
            super.e();
            this.levelCost.set(0);
        }
    }

    private class CustomListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getWhoClicked() instanceof Player) {

                if (event.getInventory().equals(inv)) {
                    ItemStack item = event.getCurrentItem();
                    int slot = event.getRawSlot();
                    String name = "";

                    if (item != null) {
                        if (item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();

                            if (meta.hasDisplayName()) {
                                name = meta.getDisplayName();
                            }
                        }
                    }

                    AnvilClickEvent clickEvent = new AnvilClickEvent(event.getInventory(), AnvilSlot.bySlot(slot), name, event.getInventory().getItem(2));
                    getHandler().onAnvilClick(clickEvent);
                    event.setCancelled(clickEvent.isCanceled());
                    if (clickEvent.getWillClose()) {
                        event.getWhoClicked().closeInventory();
                    }
                    if (clickEvent.getWillDestroy()) {
                        destroy();
                    }
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.getPlayer() instanceof Player) {
                Inventory inv = event.getInventory();
                if (inv.equals(AnvilHandler.this.inv)) {
                    inv.clear();
                    destroy();
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerQuit(PlayerQuitEvent event) {
            if (event.getPlayer().equals(getPlayer())) {
                destroy();
            }
        }
    }
}
