package lib.brainsynder.particle;

import org.bukkit.Color;

public class DustOptions {
    private Color color;
    private float size;

    public DustOptions(Color color, float size) {
        this.color = color;
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public float getSize() {
        return size;
    }
}