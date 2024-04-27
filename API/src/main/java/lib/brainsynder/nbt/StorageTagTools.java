package lib.brainsynder.nbt;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import lib.brainsynder.nbt.other.IStorageList;
import lib.brainsynder.nbt.other.NBTException;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StorageTagTools {
    private static final Class<?> nbtTag;
    private static final Class<?> craftStack;
    private static final Class<?> stackClass;
//    private static final Constructor newStack;
    private static final Constructor newKey;
//    private static final Method save;
//    private static Method newItem;
//    private static final Method toString;
    private static final Method asCopy;
//    private static final Method asBukkitCopy;
    private static final Method parseString;

    static {
        Class parser = Reflection.getNmsClass("MojangsonParser", "nbt");
        parseString = Reflection.getMethod(parser, new String[]{"parse", "a"}, String.class);

        craftStack = Reflection.getCBCClass("inventory.CraftItemStack");
        Class keyClass = Reflection.getNmsClass("MinecraftKey", "resources");

        newKey = Reflection.getConstructor(keyClass, String.class);
        nbtTag = Reflection.getNmsClass("NBTTagCompound", "nbt");
        stackClass = Reflection.getNmsClass("ItemStack", "world.item"); /** {@link net.minecraft.server.v1_13_R1.ItemStack} */
//        newStack = Reflection.getConstructor(stackClass, nbtTag);
//        if (ServerVersion.isEqualNew(ServerVersion.v1_13_R1))
//            newItem = Reflection.getMethod(stackClass, "a", nbtTag);
//        asBukkitCopy = Reflection.getMethod(craftStack, "asBukkitCopy", stackClass);
//
//        save = Reflection.getMethod(stackClass, new String[]{"save", "b"}, nbtTag);
//        toString = Reflection.getMethod(nbtTag, "toString");
        asCopy = Reflection.getMethod(craftStack, "asNMSCopy", ItemStack.class);
    }

    public static ItemStack toItemStack (StorageTagCompound compound) {
        if (!compound.hasKey("id")) return new ItemStack(Material.AIR); // Checks if it is an ItemStacks NBT/STC
        return NBTItem.convertNBTtoItem(new NBTContainer(compound.toString()));
//        Object nbt = Reflection.invoke(parseString, null, compound.toString());
//        Object nmsStack;
//        if (ServerVersion.isEqualOld(ServerVersion.v1_12_R1)) {
//            nmsStack = Reflection.initiateClass(newStack, nbt); // Will make it an NMS ItemStack
//        }else{
//            nmsStack = Reflection.invoke(newItem, null, nbt); // Will make it an NMS ItemStack
//        }
//
//        return (ItemStack) Reflection.invoke(asBukkitCopy, null, nmsStack);
    }

    public static StorageTagCompound fromItemStack(ItemStack item) {
        String json = NBTItem.convertItemtoNBT(item).toString();

        // Removes the extra formatting that Spigot adds
        //if (json.contains("{\"text\":\"")) json = json.replace("'{\"text\":\"", "\"").replace("\"}'", "\"");
        StorageTagCompound compound = new StorageTagCompound ();

        try {
            compound = JsonToNBT.getTagFromJson(json);
        } catch (NBTException e) {
            e.printStackTrace();
        }

        return compound;
    }

    public PotionEffect toPotionEffect (StorageTagCompound compound) {
        return new PotionEffect(PotionEffectType.getByName(compound.getString("type", "SPEED")), compound.getInteger("duration"), compound.getInteger("amplifier"), compound.getBoolean("isAmbient"), compound.getBoolean("hasParticles"), compound.getBoolean("hasIcon"));
    }

    public static StorageTagCompound fromPotionEffect (PotionEffect effect) {
        StorageTagCompound compound = new StorageTagCompound ();
        compound.setString("type", effect.getType().getName());
        compound.setInteger("amplifier", effect.getAmplifier());
        compound.setInteger("duration", effect.getDuration());
        compound.setBoolean("isAmbient", effect.isAmbient());
        compound.setBoolean("hasParticles", effect.hasParticles());
        compound.setBoolean("hasIcon", effect.hasIcon());
        return compound;
    }

    public static JsonObject toJsonObject (StorageTagCompound compound) {
        JsonObject json = new JsonObject ();
        compound.getKeySet().forEach(key -> {
            StorageBase base = compound.getTag(key);

            if (compound.isBoolean(key)) {
                json.add (key, compound.getBoolean(key));
            }else if (base instanceof StoragePrimitive) {
                json.add(key, ((StoragePrimitive)base).getInt());
            }else if (base instanceof IStorageList) {
                JsonArray array = new JsonArray();
                Object list = ((IStorageList)base).getList();
                if (list instanceof byte[]) {
                    for (byte v : (byte[]) list) array.add(v+"b");
                }else if (list instanceof int[]) {
                    for (int v : (int[]) list) array.add(v);
                }else if (list instanceof long[]) {
                    for (long v : (long[]) list) array.add(v+"l");
                }else if (list instanceof List) {
                    ((List)list).forEach(string -> array.add(String.valueOf(string).replace("\"", "")));
                }
                json.add(key, array);
            }else if (base instanceof StorageTagCompound) {
                json.add(key, toJsonObject((StorageTagCompound)base));
            }else if (base instanceof StorageTagString) {
                json.add(key, base.getString());
            }
        });
        return json;
    }

    public static StorageTagCompound fromJsonObject (JsonObject json) {
        StorageTagCompound compound = new StorageTagCompound ();
        json.names().forEach(key -> {
            JsonValue value = json.get(key);
            if (value.isNumber()) {
                compound.setInteger(key, value.asInt());
            }else if (value.isBoolean()) {
                compound.setBoolean(key, value.asBoolean());
            }else if (value.isString()) {
                compound.setString(key, value.asString());
            }else if (value.isArray()) {
                JsonArray array = value.asArray();
                List<Byte> bytes = new ArrayList<>();
                List<Integer> ints = new ArrayList<>();
                List<Long> longs = new ArrayList<>();
                StorageTagList list = new StorageTagList();

                array.values().forEach(jsonValue -> {
                    if (jsonValue.isString()) {
                        String string = jsonValue.asString();
                        if (string.endsWith("l")) {
                            try {
                                longs.add(Long.parseLong(string.replace("l", "")));
                            }catch (NumberFormatException e) {
                                list.appendTag(new StorageTagString(string));
                            }
                        }else if (string.endsWith("b")) {
                            try {
                                bytes.add(Byte.parseByte(string.replace("b", "")));
                            }catch (NumberFormatException e) {
                                list.appendTag(new StorageTagString(string));
                            }
                        }else {
                            try {
                                ints.add(Integer.parseInt(string));
                            }catch (NumberFormatException e) {
                                list.appendTag(new StorageTagString(string));
                            }
                        }
                    }

                    if (jsonValue.isNumber()) {
                        ints.add(jsonValue.asInt());
                    }
                });

                if (!bytes.isEmpty()) {
                    compound.setTag(key, new StorageTagByteArray(bytes));
                }else if (!ints.isEmpty()) {
                    compound.setTag(key, new StorageTagIntArray(ints));
                }else if (!longs.isEmpty()) {
                    compound.setTag(key, new StorageTagLongArray(longs));
                }else{
                    compound.setTag(key, list);
                }

            }else if (value.isObject()) {
                compound.setTag(key, fromJsonObject(value.asObject()));
            }
        });

        return compound;
    }

    public static <T> T toNBTTag (StorageTagCompound compound) {
        return (T) Reflection.invoke(parseString, null, compound.toString());
    }

    private static Object asNMSCopy (ItemStack stack) {
        return Reflection.invoke(asCopy, null, stack);
    }
    private static Object newNBTTag (Class<?> nbtTag) {
        return Reflection.initiateClass(nbtTag);
    }
    private static Object newMCKey (String key) {
        return Reflection.initiateClass(newKey, key);
    }
}
