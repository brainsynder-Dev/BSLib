package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorMetaHandler extends MetaHandler<LeatherArmorMeta> {

    public LeatherArmorMetaHandler(LeatherArmorMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof LeatherArmorMeta)) return;
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
        updateCompound(new StorageTagCompound().setColor("color", armorMeta.getColor()));
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        modifyMeta(value -> {
            value.setColor(compound.getColor("color", Color.fromRGB(10511680)));
            return value;
        });
    }
}
