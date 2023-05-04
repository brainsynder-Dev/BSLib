package lib.brainsynder.nbt;

import lib.brainsynder.nbt.other.NBTSizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StorageTagInt extends StoragePrimitive {
    /**
     * The integer value for the tag.
     */
    private int data;

    StorageTagInt() {
    }

    public StorageTagInt(int data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        output.writeInt(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(96L);
        this.data = input.readInt();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 3;
    }

    public String toString() {
        return String.valueOf(this.data);
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagInt copy() {
        return new StorageTagInt(this.data);
    }

    public boolean equals(Object instance) {
        return super.equals(instance) && this.data == ((StorageTagInt) instance).data;
    }

    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public long getLong() {
        return this.data;
    }

    public int getInt() {
        return this.data;
    }

    public short getShort() {
        return (short) (this.data & 65535);
    }

    public byte getByte() {
        return (byte) (this.data & 255);
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return (float) this.data;
    }
}