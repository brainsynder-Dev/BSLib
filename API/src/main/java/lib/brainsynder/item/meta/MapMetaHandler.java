package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public class MapMetaHandler extends MetaHandler<MapMeta> {
    public MapMetaHandler(MapMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof MapMeta)) return;
        MapMeta mapMeta = (MapMeta) meta;
        StorageTagCompound compound = new StorageTagCompound();
        if (mapMeta.hasColor()) compound.setColor("color", mapMeta.getColor());
        if (mapMeta.isScaling()) compound.setBoolean("scaling", mapMeta.isScaling());
        if (mapMeta.hasLocationName()) compound.setString("location-name", mapMeta.getLocalizedName());
        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        modifyMeta(value -> {
            if (compound.hasKey("color")) value.setColor(compound.getColor("color"));
            if (compound.hasKey("scaling")) value.setScaling(compound.getBoolean("scaling"));
            if (compound.hasKey("location-name")) value.setLocationName(compound.getString("location-name"));
            return value;
        });
    }
}
