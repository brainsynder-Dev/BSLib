package lib.brainsynder.item.meta;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.SupportedVersion;
import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagTools;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SupportedVersion(version = ServerVersion.v1_14_R1)
public class CrossbowMetaHandler extends MetaHandler {
    private Method setProjectiles = null, getProjectiles = null;

    public CrossbowMetaHandler(ItemMeta meta) {
        super(meta);
        Class clazz = Reflection.getBukkitClass("inventory.meta.CrossbowMeta");
        if (clazz == null) return;
        getProjectiles = Reflection.getMethod(clazz, "getChargedProjectiles");
        setProjectiles = Reflection.getMethod(clazz, "setChargedProjectiles", List.class);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (getProjectiles == null) return;
        StorageTagCompound compound = new StorageTagCompound();
        List<ItemStack> projectiles = (List<ItemStack>) Reflection.invoke(getProjectiles, meta);
        if (projectiles.isEmpty()) return;
        StorageTagList list = new StorageTagList();
        projectiles.forEach(stack -> list.appendTag(StorageTagTools.toStorage(stack)));
        compound.setTag("projectiles", list);
        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        if (setProjectiles == null) return;
        modifyMeta(value -> {
            if (compound.hasKey("projectiles")) {
                StorageTagList stored = (StorageTagList) compound.getTag("projectiles");
                List<ItemStack> list = new ArrayList<>();
                stored.getTagList().forEach(storageBase -> list.add(StorageTagTools.toItemStack((StorageTagCompound) storageBase)));
                Reflection.invoke(setProjectiles, value, list);
            }
            return value;
        });
    }
}
