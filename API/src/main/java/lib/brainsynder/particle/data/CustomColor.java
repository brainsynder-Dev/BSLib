package lib.brainsynder.particle.data;

import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Color;

public class CustomColor {
    private final Color color;
    private final float size;

    public CustomColor(Color color, float size) {
        this.color = color;
        this.size = size;
    }
    public CustomColor(StorageTagCompound compound) {
        this.color = compound.getColor("color", Color.RED);
        this.size = compound.getFloat("size", 1.0f);
    }

    public Color getColor() {
        return color;
    }

    public float getSize() {
        return size;
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound ();
        compound.setColor("color", color);
        compound.setFloat("size", size);
        return compound;
    }
}