package lib.brainsynder.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class StorageTagCompound extends StorageBase {
    private static final Logger LOGGER = LogManager.getLogger(StorageTagCompound.class);
    private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
    private Map<String, StorageBase> tagMap = Maps.newHashMap();
    private List<String> booleans = new ArrayList<>();

    private static void writeEntry(String name, StorageBase data, DataOutput output) throws IOException {
        output.writeByte(data.getId());

        if (data.getId() != 0) {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readByte();
    }

    private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readUTF();
    }

    static StorageBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        StorageBase nbtbase = StorageBase.createNewByType(id);

        try {
            nbtbase.read(input, depth, sizeTracker);
            return nbtbase;
        } catch (IOException ioexception) {
            throw new IOException(ioexception);
        }
    }

    protected static String match(String s) {
        return PATTERN.matcher(s).matches() ? s : StorageTagString.configure(s);
    }

    static void escape(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    continue;
                case '\n':
                    sb.append("\\n");
                    continue;
                case '\f':
                    sb.append("\\f");
                    continue;
                case '\r':
                    sb.append("\\r");
                    continue;
                case '"':
                    sb.append("\\\"");
                    continue;
                case '/':
                    sb.append("\\/");
                    continue;
                case '\\':
                    sb.append("\\\\");
                    continue;
            }

            if (ch >= 0 && ch <= 31 || ch >= 127 && ch <= 159 || ch >= 8192 && ch <= 8447) {
                String ss = Integer.toHexString(ch);
                sb.append("\\u");

                for (int k = 0; k < 4 - ss.length(); ++k) {
                    sb.append('0');
                }

                sb.append(ss.toUpperCase());
            } else {
                sb.append(ch);
            }
        }

    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        for (String s : this.tagMap.keySet()) {
            StorageBase nbtbase = this.tagMap.get(s);
            writeEntry(s, nbtbase, output);
        }

        output.writeByte(0);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(384L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagMap.clear();
            byte b0;

            while ((b0 = readType(input, sizeTracker)) != 0) {
                String s = readKey(input, sizeTracker);
                sizeTracker.read(224 + 16 * s.length());
                StorageBase nbtbase = readNBT(b0, s, input, depth + 1, sizeTracker);

                if (this.tagMap.put(s, nbtbase) != null) {
                    sizeTracker.read(288L);
                }
            }
        }
    }

    public Set<String> getKeySet() {
        return this.tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 10;
    }

    public int getSize() {
        return this.tagMap.size();
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public StorageTagCompound setTag(String key, StorageBase value) {
        this.tagMap.put(key, value);
        return this;
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public StorageTagCompound setByte(String key, byte value) {
        this.tagMap.put(key, new StorageTagByte(value));
        return this;
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public StorageTagCompound setShort(String key, short value) {
        this.tagMap.put(key, new StorageTagShort(value));
        return this;
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public StorageTagCompound setInteger(String key, int value) {
        this.tagMap.put(key, new StorageTagInt(value));
        return this;
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public StorageTagCompound setLong(String key, long value) {
        this.tagMap.put(key, new StorageTagLong(value));
        return this;
    }

    public StorageTagCompound setUniqueId(String key, UUID value) {
        this.setLong(key + "Most", value.getMostSignificantBits());
        this.setLong(key + "Least", value.getLeastSignificantBits());
        return this;
    }

    public UUID getUniqueId(String key) {
        return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
    }

    public boolean hasUniqueId(String key) {
        return this.hasKey(key + "Most", 99) && this.hasKey(key + "Least", 99);
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public StorageTagCompound setFloat(String key, float value) {
        this.tagMap.put(key, new StorageTagFloat(value));
        return this;
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public StorageTagCompound setDouble(String key, double value) {
        this.tagMap.put(key, new StorageTagDouble(value));
        return this;
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public StorageTagCompound setString(String key, String value) {
        this.tagMap.put(key, new StorageTagString(value));
        return this;
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public StorageTagCompound setByteArray(String key, byte[] value) {
        this.tagMap.put(key, new StorageTagByteArray(value));
        return this;
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public StorageTagCompound setIntArray(String key, int[] value) {
        this.tagMap.put(key, new StorageTagIntArray(value));
        return this;
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public StorageTagCompound setBoolean(String key, boolean value) {
        tagMap.put(key, new StorageTagByte((byte) ((value) ? 1 : 0)));
        booleans.add(key);
        return this;
    }
    public boolean isBoolean (String key) {
        return booleans.contains(key);
    }

    /**
     * gets a generic tag with the specified name
     */
    public StorageBase getTag(String key) {
        return this.tagMap.get(key);
    }

    /**
     * Gets the ID byte for the given tag key
     */
    public byte getTagId(String key) {
        StorageBase nbtbase = this.tagMap.get(key);
        return nbtbase == null ? 0 : nbtbase.getId();
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean hasKey(String key) {
        return this.tagMap.containsKey(key);
    }

    /**
     * Returns whether the given string has been previously stored as a key in this tag compound as a particular type,
     * denoted by a parameter in the form of an ordinal. If the provided ordinal is 99, this method will match tag types
     * representing numbers.
     */
    public boolean hasKey(String key, int type) {
        int i = this.getTagId(key);

        if (i == type) {
            return true;
        } else if (type != 99) {
            return false;
        } else {
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
        }
    }
    
    public StorageTagCompound setLocation (String key, Location location) {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setString("world", location.getWorld().getName());
        compound.setDouble("x", location.getX());
        compound.setDouble("y", location.getY());
        compound.setDouble("z", location.getZ());
        compound.setFloat("yaw", location.getYaw());
        compound.setFloat("pitch", location.getPitch());
        setTag(key, compound);
        return this;
    }
    public Location getLocation (String key) {
        StorageTagCompound compound = getCompoundTag(key);
        World world = Bukkit.getWorld(compound.getString("world", "world"));
        double x = compound.getDouble("x", 0);
        double y = compound.getDouble("y", 0);
        double z = compound.getDouble("z", 0);
        float yaw = compound.getFloat("yaw", 0f);
        float pitch = compound.getFloat("pitch", 0f);
        return new Location(world, x, y, z, yaw, pitch);
    }
    public Location getLocation (String key, Location fallback) {
        return (hasKey(key) ? getLocation(key) : fallback);
    }

    public StorageTagCompound setColor (String key, Color color) {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setInteger("r", color.getRed());
        compound.setInteger("g", color.getGreen());
        compound.setInteger("b", color.getBlue());
        setTag(key, compound);
        return this;
    }
    public Color getColor (String key) {
        StorageTagCompound compound = getCompoundTag(key);
        int r = compound.getInteger("r", 0);
        if (r > 255) r = 255;
        if (r < 0) r = 0;
        
        int g = compound.getInteger("g", 0);
        if (g > 255) r = 255;
        if (g < 0) r = 0;
        
        int b = compound.getInteger("b", 0);
        if (b > 255) r = 255;
        if (b < 0) r = 0;
        
        return Color.fromRGB(r, g, b);
    }
    public Color getColor (String key, Color fallback) {
        return (hasKey(key) ? getColor(key) : fallback);
    }

    public StorageTagCompound setEnum (String key, Enum anEnum) {
        setString(key, anEnum.name());
        return this;
    }
    public <E extends Enum>E getEnum (String key, Class<E> type) {
        return getEnum(key, type, null);
    }
    public <E extends Enum>E getEnum (String key, Class<E> type, E fallback) {
        if (!hasKey(key)) return fallback;
        return (E) E.valueOf(type, getString(key));
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(String key) {
        StorageBase storage = this.tagMap.get(key);
        if (storage.getId() == 1) {
            return ((StorageTagByte)storage).getByte();
        }
        return 0;
    }
    public byte getByte(String key, byte fallback) {
        return (hasKey(key) ? getByte(key) : fallback);
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(String key) {
        return Short.parseShort(getValue(key));
    }
    public short getShort(String key, short fallback) {
        return (hasKey(key) ? getShort(key) : fallback);
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(String key) {
        return Integer.parseInt(getValue(key));
    }
    public int getInteger(String key, int fallback) {
        return (hasKey(key) ? getInteger(key) : fallback);
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(String key) {
        return Long.parseLong(getValue(key));
    }
    public long getLong(String key, long fallback) {
        return (hasKey(key) ? getLong(key) : fallback);
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(String key) {
        return Float.parseFloat(getValue(key));
    }
    public float getFloat(String key, float fallback) {
        return (hasKey(key) ? getFloat(key) : fallback);
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(String key) {
        return Double.parseDouble(getValue(key));
    }
    public double getDouble(String key, double fallback) {
        return (hasKey(key) ? getDouble(key) : fallback);
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(String key) {
        return getValue(key);
    }
    public String getString(String key, String fallback) {
        return (hasKey(key) ? getValue(key) : fallback);
    }

    public String getValue(String key) {
        try {
            if (this.hasKey(key)) {
                return fetchValue(tagMap.get(key));
            }
        } catch (ClassCastException ignored) {
        }
        return "";
    }

    private String fetchValue(StorageBase base) {
        if (base instanceof StorageTagByte) {
            byte tagByte = ((StorageTagByte) base).getByte();
            if ((tagByte == 0) || (tagByte == 1))
                return String.valueOf(tagByte == 1);
            return String.valueOf(tagByte);
        }
        if (base instanceof StorageTagByteArray)
            return Arrays.toString(((StorageTagByteArray) base).getByteArray());
        if (base instanceof StorageTagDouble)
            return String.valueOf(((StorageTagDouble) base).getDouble());
        if (base instanceof StorageTagFloat)
            return String.valueOf(((StorageTagFloat) base).getFloat());
        if (base instanceof StorageTagInt)
            return String.valueOf(((StorageTagInt) base).getInt());
        if (base instanceof StorageTagIntArray)
            return Arrays.toString(((StorageTagIntArray) base).getIntArray());
        if (base instanceof StorageTagLong)
            return String.valueOf(((StorageTagLong) base).getLong());
        if (base instanceof StorageTagShort)
            return String.valueOf(((StorageTagShort) base).getShort());
        if (base instanceof StorageTagString)
            return String.valueOf(base.getString());
        if (base instanceof StorageTagList) {
            StorageTagList list = (StorageTagList) base;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.tagCount(); i++) {
                builder.append(fetchValue(list.get(i)));
            }
            return builder.toString();
        }

        return base.getString();
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(String key) {
        try {
            if (this.hasKey(key, 7)) {
                return ((IStorageList<byte[]>) this.tagMap.get(key)).getList();
            }
        } catch (ClassCastException ignored) {
        }

        return new byte[0];
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(String key) {
        try {
            if (this.hasKey(key, 11)) {
                return ((IStorageList<int[]>) this.tagMap.get(key)).getList();
            }
        } catch (ClassCastException ignored) {
        }

        return new int[0];
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public StorageTagCompound getCompoundTag(String key) {
        try {
            if (this.hasKey(key, 10)) {
                return (StorageTagCompound) this.tagMap.get(key);
            }
        } catch (ClassCastException ignored) {
        }

        return new StorageTagCompound();
    }

    /**
     * Gets the NBTTagList object with the given name.
     */
    public StorageTagList getTagList(String key, int type) {
        try {
            if (this.getTagId(key) == 9) {
                StorageTagList nbttaglist = (StorageTagList) this.tagMap.get(key);

                if (!nbttaglist.hasNoTags() && nbttaglist.getTagType() != type) {
                    return new StorageTagList();
                }

                return nbttaglist;
            }
        } catch (ClassCastException ignored) {
        }

        return new StorageTagList();
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }
    public boolean getBoolean(String key, boolean fallback) {
        return (hasKey(key) ? getBoolean(key) : fallback);
    }

    public StorageTagCompound setItemStack (String key, ItemStack item) {
        setTag(key, StorageTagTools.toStorage(item));
        return this;
    }
    public ItemStack getItemStack (String key) {
        return getItemStack(key, new ItemStack(Material.AIR));
    }
    public ItemStack getItemStack (String key, ItemStack fallback) {
        if (!hasKey(key)) return fallback;
        return StorageTagTools.toItemStack(getCompoundTag(key));
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        Collection<String> collection = this.tagMap.keySet();

        if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(this.tagMap.keySet());
            Collections.sort(list);
            collection = list;
        }

        for (String s : collection) {
            if (stringbuilder.length() != 1) {
                stringbuilder.append(',');
            }

            stringbuilder.append(match(s)).append(':').append(this.tagMap.get(s));
        }

        return stringbuilder.append('}').toString();
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagCompound copy() {
        StorageTagCompound nbttagcompound = new StorageTagCompound();

        for (String s : this.tagMap.keySet()) {
            nbttagcompound.setTag(s, this.tagMap.get(s).copy());
        }

        return nbttagcompound;
    }

    public boolean equals(Object instance) {
        return super.equals(instance) && Objects.equals(this.tagMap.entrySet(), ((StorageTagCompound) instance).tagMap.entrySet());
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    /**
     * Remove the specified tag.
     */
    public StorageTagCompound remove (String key) {
        if (hasKey(key)) tagMap.remove(key);
        booleans.remove(key);
        return this;
    }

    /**
     * Merges this NBTTagCompound with the given compound. Any sub-compounds are merged using the same methods, other
     * types of tags are overwritten from the given compound.
     */
    public StorageTagCompound merge(StorageTagCompound other) {
        for (String s : other.tagMap.keySet()) {
            StorageBase nbtbase = other.tagMap.get(s);

            if (nbtbase.getId() == 10) {
                if (this.hasKey(s, 10)) {
                    StorageTagCompound nbttagcompound = this.getCompoundTag(s);
                    nbttagcompound.merge((StorageTagCompound) nbtbase);
                } else {
                    this.setTag(s, nbtbase.copy());
                }
            } else {
                this.setTag(s, nbtbase.copy());
            }
        }
        return this;
    }
}
