package lib.brainsynder.anvil;

import lib.brainsynder.reflection.NMSManager;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by chasechocolate
 * <p>
 * Edited to use NMS by PhilipsNostrum
 * <p>
 * Cleaned up, Content upgrade & Edited to use in V1_14 by Gecolay
 **/

public class AnvilGUI {

    private final Plugin plugin;

    private boolean colorrename = true;
    private final Player player;
    private String title;
    private String defaultText = "";
    private Inventory inventory;
    private final HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
    private final Listener listener;
    private final IAnvilClickEvent handler;

    private Class<?> blockPosition;
    private Class<?> playOutOpenWindow;
    private Class<?> containerAnvil;
    private Class<?> chatMessage;
    private Class<?> human;
    private Class<?> containerAccess;
    private Class<?> containers;

    private Method serializerMethod;

    private boolean useNewVersion;

    private void loadClasses() {
        useNewVersion = NMSManager.useNewVersion();

        blockPosition = NMSManager.getNMSClass("BlockPosition");
        playOutOpenWindow = NMSManager.getNMSClass("PacketPlayOutOpenWindow");
        containerAnvil = NMSManager.getNMSClass("ContainerAnvil");
        chatMessage = NMSManager.getNMSClass("ChatMessage");
        human = NMSManager.getNMSClass("EntityHuman");
        Class chatSerializer = Reflection.getNmsClass("IChatBaseComponent$ChatSerializer", "network.chat");
        serializerMethod = Reflection.getMethod(chatSerializer, "a", String.class);

        if (useNewVersion) {
            containerAccess = NMSManager.getNMSClass("ContainerAccess");
            containers = NMSManager.getNMSClass("Containers");
        }
    }

    public boolean getColorRename() {
        return colorrename;
    }

    public void setColorRename(boolean value) {
        colorrename = value;
    }

    public Player getPlayer() {
        return player;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String builder) {
        this.title = builder;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public ItemStack getSlot(AnvilSlot slot) {
        return items.get(slot);
    }

    public void setSlot(AnvilSlot slot, ItemStack stack) {
        items.put(slot, stack);
    }

    public String getSlotName(AnvilSlot slot) {
        ItemStack stack = getSlot(slot);
        if (stack != null && stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            return meta.hasDisplayName() ? meta.getDisplayName() : "";

        } else return "";
    }

    public void setSlotName(AnvilSlot slot, String name) {
        ItemStack stack = getSlot(slot);
        if (stack != null) {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(name != null ? Colorize.translateBungeeHex(name) : null);
            stack.setItemMeta(meta);
            setSlot(slot, stack);
        }
    }

    public AnvilGUI(Plugin plugin, Player player, final IAnvilClickEvent handler) {
        this.plugin = plugin;
        loadClasses();

        this.player = player;
        this.handler = handler;
        listener = new Listener() {
            @EventHandler
            public void invClick(InventoryClickEvent event) {
                if (event.getInventory().equals(inventory)) {
                    event.setCancelled(true);
                    if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;

                    ItemStack stack = event.getCurrentItem();
                    int slot = event.getRawSlot();

                    String text = null;
                    if (stack != null && stack.hasItemMeta()) {
                        ItemMeta meta = stack.getItemMeta();
                        if (meta.hasDisplayName()) text = meta.getDisplayName();
                    }

                    AnvilClickEvent clickEvent = new AnvilClickEvent(event.getInventory(), AnvilSlot.bySlot(slot), text, stack);
                    AnvilGUI.this.handler.onAnvilClick(clickEvent);
                    if (clickEvent.getWillClose() || clickEvent.getWillDestroy()) event.getWhoClicked().closeInventory();
                    if (clickEvent.getWillDestroy()) HandlerList.unregisterAll(listener);
                }
            }

            @EventHandler
            public void prepare(PrepareAnvilEvent event) {
                if (event.getInventory().equals(inventory)) {
                    ItemStack stack = event.getResult();
                    if (colorrename && stack != null && stack.hasItemMeta()) {
                        ItemMeta meta = stack.getItemMeta();
                        if (meta.hasDisplayName()) meta.setDisplayName(Colorize.translateBungeeHex(meta.getDisplayName()));
                        stack.setItemMeta(meta);
                        event.setResult(stack);
                    }
                }
            }

            @EventHandler
            public void ICE(InventoryCloseEvent event) {
                if (event.getInventory().equals(inventory)) {
                    AnvilGUI.this.player.setLevel(AnvilGUI.this.player.getLevel() - 1);
                    inventory.clear();
                    HandlerList.unregisterAll(listener);
                }
            }

            @EventHandler
            public void PQE(PlayerQuitEvent event) {
                if (event.getPlayer().equals(AnvilGUI.this.player)) {
                    AnvilGUI.this.player.setLevel(AnvilGUI.this.player.getLevel() - 1);
                    HandlerList.unregisterAll(listener);
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, this.plugin);

    }

    public void open() {
        open(title);
    }

    public void open(String title) {
        player.setLevel(player.getLevel() + 1);
        Object serializer = Reflection.invoke(serializerMethod, null, Colorize.convertParts2Json(Colorize.splitMessageToParts(title)).toString());
        try {
            Object handle = NMSManager.getHandle(player);
            if (useNewVersion) {
                Method CAM = NMSManager.getMethod("at", containerAccess, NMSManager.getNMSClass("World"), blockPosition);
                Object CA = containerAnvil.getConstructor(int.class, NMSManager.getNMSClass("PlayerInventory"), containerAccess).newInstance(9, NMSManager.getPlayerField(player, "inventory"), CAM.invoke(containerAccess, NMSManager.getPlayerField(player, "world"), blockPosition.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0)));
                NMSManager.getField(NMSManager.getNMSClass("Container"), "checkReachable").set(CA, false);
                inventory = (Inventory) NMSManager.invokeMethod("getTopInventory", NMSManager.invokeMethod("getBukkitView", CA));
                for (AnvilSlot AS : items.keySet()) inventory.setItem(AS.getSlot(), items.get(AS));
                int ID = (Integer) NMSManager.invokeMethod("nextContainerCounter", handle);
                Object PC = NMSManager.getPlayerField(player, "playerConnection");
                Object PPOOW = playOutOpenWindow.getConstructor(int.class, containers, NMSManager.getNMSClass("IChatBaseComponent"))
                        .newInstance(ID, NMSManager.getField(containers, "ANVIL").get(containers), serializer);
                Method SP = NMSManager.getMethod("sendPacket", PC.getClass(), playOutOpenWindow);
                SP.invoke(PC, PPOOW);
                Field AC = NMSManager.getField(human, "activeContainer");
                if (AC != null) {
                    AC.set(handle, CA);
                    NMSManager.getField(NMSManager.getNMSClass("Container"), "windowId").set(AC.get(handle), ID);
                    NMSManager.getMethod("addSlotListener", AC.get(handle).getClass(), handle.getClass()).invoke(AC.get(handle), handle);
                }
            } else {
                Object CA = containerAnvil.getConstructor(NMSManager.getNMSClass("PlayerInventory"), NMSManager.getNMSClass("World"), blockPosition, human).newInstance(NMSManager.getPlayerField(player, "inventory"), NMSManager.getPlayerField(player, "world"), blockPosition.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0), handle);
                NMSManager.getField(NMSManager.getNMSClass("Container"), "checkReachable").set(CA, false);
                inventory = (Inventory) NMSManager.invokeMethod("getTopInventory", NMSManager.invokeMethod("getBukkitView", CA));
                for (AnvilSlot AS : items.keySet()) inventory.setItem(AS.getSlot(), items.get(AS));
                int ID = (Integer) NMSManager.invokeMethod("nextContainerCounter", handle);
                Object PC = NMSManager.getPlayerField(player, "playerConnection");
                Object PPOOW = playOutOpenWindow.getConstructor(int.class, String.class, NMSManager.getNMSClass("IChatBaseComponent"), int.class).newInstance(ID, "minecraft:anvil", serializer, 0);
                Method SP = NMSManager.getMethod("sendPacket", PC.getClass(), playOutOpenWindow);
                SP.invoke(PC, PPOOW);
                Field AC = NMSManager.getField(human, "activeContainer");
                if (AC != null) {
                    AC.set(handle, CA);
                    NMSManager.getField(NMSManager.getNMSClass("Container"), "windowId").set(AC.get(handle), ID);
                    NMSManager.getMethod("addSlotListener", AC.get(handle).getClass(), handle.getClass()).invoke(AC.get(handle), handle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}