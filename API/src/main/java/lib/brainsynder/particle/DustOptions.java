package lib.brainsynder.particle;

import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Color;

public class DustOptions {
    private final Color color;
    private final float size;

    public DustOptions(Color color, float size) {
        this.color = color;
        this.size = size;
    }
    public DustOptions(StorageTagCompound compound) {
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