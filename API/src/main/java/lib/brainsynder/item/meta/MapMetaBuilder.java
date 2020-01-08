package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public class MapMetaBuilder extends MetaBuilder<MapMeta> {
    private int scaling = 0;
    private String locationName = null;
    private Color color = null;


    public boolean hasColor () {
        return (color != null);
    }
    public boolean hasLocationName () {
        return (locationName != null);
    }


    public boolean isScaling() {
        return (scaling == 1);
    }
    public String getLocationName() {
        return locationName;
    }
    public Color getColor() {
        return color;
    }


    public void setColor(Color color) {
        this.color = color;
        modifyMeta(value -> {
            value.setColor(color);
            return value;
        });
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
        modifyMeta(value -> {
            value.setLocationName(locationName);
            return value;
        });
    }
    public void setScaling(boolean scaling) {
        this.scaling = (byte)(scaling ? 1 : 2);
        modifyMeta(value -> {
            value.setScaling(scaling);
            return value;
        });
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof MapMeta)) return;
        MapMeta mapMeta = (MapMeta) meta;
        if (mapMeta.hasColor()) setColor(mapMeta.getColor());
        if (mapMeta.hasLocationName()) setLocationName(mapMeta.getLocalizedName());
        if (mapMeta.isScaling()) setScaling(mapMeta.isScaling());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("color")) setColor(compound.getColor("color"));
        if (compound.hasKey("scaling")) setScaling(compound.getBoolean("scaling"));
        if (compound.hasKey("location-name")) setLocationName(compound.getString("location-name"));
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        if (hasColor()) compound.setColor("color", color);
        if (scaling != 0) compound.setBoolean("scaling", isScaling());
        if (hasLocationName()) compound.setString("location-name", locationName);
        return compound;
    }
}
