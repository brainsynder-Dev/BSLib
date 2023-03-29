package lib.brainsynder.nms.version;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lib.brainsynder.nbt.JsonToNBT;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.other.NBTException;
import lib.brainsynder.nms.VersionWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class Wrapper_v1_19_4 implements VersionWrapper {
    private int getRealNextContainerId(Player player) {
        return toNMS(player).nextContainerCounter();
    }

    /**
     * Turns a {@link Player} into an NMS one
     *
     * @param player The player to be converted
     * @return the NMS EntityPlayer
     */
    private ServerPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    @Override
    public ItemStack toItemStack(StorageTagCompound compound) {
        try {
            CompoundTag nbt = TagParser.parseTag(compound.toString());
            return CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.of(nbt));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return new ItemStack(Material.STONE);
    }

    @Override
    public StorageTagCompound fromItemStack(ItemStack itemStack) {
        CompoundTag nbt = new CompoundTag();
        CraftItemStack.asNMSCopy(itemStack).save(nbt);
        StorageTagCompound compound = new StorageTagCompound ();

        try {
            compound = JsonToNBT.getTagFromJson(nbt.toString());
        } catch (NBTException e) {
            e.printStackTrace();
        }

        return compound;
    }

    @Override
    public int getNextContainerId(Player player, Object container) {
        return ((AnvilContainer) container).getContainerId();
    }

    @Override
    public void handleInventoryCloseEvent(Player player) {
        CraftEventFactory.handleInventoryCloseEvent(toNMS(player));
    }

    @Override
    public void sendPacketOpenWindow(Player player, int containerId, String inventoryTitle) {
        toNMS(player).connection.send(new ClientboundOpenScreenPacket(containerId, MenuType.ANVIL, CraftChatMessage.fromString(inventoryTitle)[0]));
    }

    @Override
    public void sendPacketCloseWindow(Player player, int containerId) {
        toNMS(player).connection.send(new ClientboundContainerClosePacket(containerId));
    }

    @Override
    public void setActiveContainerDefault(Player player) {
        (toNMS(player)).containerMenu = (toNMS(player)).inventoryMenu;
    }

    @Override
    public void setActiveContainer(Player player, Object container) {
        (toNMS(player)).containerMenu = (AbstractContainerMenu) container;
    }

    @Override
    public void setActiveContainerId(Object container, int containerId) {

    }

    @Override
    public void addActiveContainerSlotListener(Object container, Player player) {
        toNMS(player).initMenu((AbstractContainerMenu) container);
    }

    @Override
    public Inventory toBukkitInventory(Object container) {
        return ((AbstractContainerMenu) container).getBukkitView().getTopInventory();
    }

    @Override
    public Object newContainerAnvil(Player player, String title) {
        return new AnvilContainer(player, getRealNextContainerId(player), title);
    }

    private static class AnvilContainer extends AnvilMenu {
        public AnvilContainer(Player player, int containerId, String guiTitle) {
            super(containerId, ((CraftPlayer) player).getHandle().getInventory(),
                    ContainerLevelAccess.create(((CraftWorld) player.getWorld()).getHandle(), new BlockPos(0, 0, 0)));
            this.checkReachable = false;
            setTitle(CraftChatMessage.fromString(guiTitle)[0]);
        }

        @Override
        public void createResult() {
            super.createResult();
            this.cost.set(0);
        }

        @Override
        public void removed(net.minecraft.world.entity.player.Player player) {}

        @Override
        protected void clearContainer(net.minecraft.world.entity.player.Player player, net.minecraft.world.Container container) {}

        public int getContainerId() {
            return this.containerId;
        }
    }
}