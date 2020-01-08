package lib.brainsynder.item;

import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.inventory.meta.ItemMeta;

public class MetaBuilder<T extends ItemMeta> {
    private T meta = null;

    public String getName () {
        return getClass().getSimpleName();
    }

    protected T getMeta() {
        return meta;
    }
    public void fromItemMeta (ItemMeta meta) {}
    public void loadCompound (StorageTagCompound compound) {}
    public void updateMeta (T meta){
        this.meta = meta;
    }
    public void modifyMeta (InnerReturn<T> meta) {
        this.meta = meta.run(this.meta);
    }

    public StorageTagCompound toCompound() {
        return new StorageTagCompound();
    }

    protected interface InnerReturn<T> {
        T run(T value);
    }
}
