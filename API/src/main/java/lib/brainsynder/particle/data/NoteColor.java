package lib.brainsynder.particle.data;

public class NoteColor {
    private final int note;

    public NoteColor(int note) throws IllegalArgumentException {
        if (note < 0) throw new IllegalArgumentException("The note value is lower than 0");
        if (note > 24) throw new IllegalArgumentException("The note value is higher than 24");
        this.note = note;
    }

    public float getValueX() {
        return this.note / 24.0F;
    }

    public float getValueY() {
        return 0.0F;
    }

    public float getValueZ() {
        return 0.0F;
    }
}