package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorMetaBuilder extends MetaBuilder<LeatherArmorMeta> {
    private Color color = Color.fromRGB(10511680);

    public void setColor(Color color) {
        this.color = color;
        modifyMeta(value -> {
            value.setColor(color);
            return value;
        });
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof LeatherArmorMeta)) return;
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
        setColor(armorMeta.getColor());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        setColor(compound.getColor("color", Color.fromRGB(10511680)));
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        compound.setColor("color", color);
        return compound;
    }
}
