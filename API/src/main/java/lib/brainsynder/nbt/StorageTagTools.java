package lib.brainsynder.nbt;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.reflection.FieldAccessor;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class StorageTagTools {
    private static Method parseString;
    private static Object registry = null;
    private static Class<?> nbtTag, craftStack, stackClass;
    private static Constructor newStack, newKey;
    private static Method save, load, toString, asCopy, asBukkitCopy, getItem;

    static {
        Class parser = Reflection.getNmsClass("MojangsonParser");
        parseString = Reflection.getMethod(parser, "parse", String.class);

        craftStack = Reflection.getCBCClass("inventory.CraftItemStack");
        Class keyClass = Reflection.getNmsClass("MinecraftKey");

        newKey = Reflection.getConstructor(keyClass, String.class);
        nbtTag = Reflection.getNmsClass("NBTTagCompound");
        stackClass = Reflection.getNmsClass("ItemStack");
        newStack = Reflection.getConstructor(stackClass, Reflection.getNmsClass("Item"));

        if (ServerVersion.isEqualNew(ServerVersion.v1_14_R1)) { // TODO: Find the correct version this changed in
            FieldAccessor accessor = FieldAccessor.getField(Reflection.getNmsClass("IRegistry"), "ITEM", Object.class);
            registry = accessor.get(null);
            getItem = Reflection.getMethod(registry.getClass(), "get", keyClass);
        }else{
            getItem = Reflection.getMethod(Reflection.getNmsClass("Items"), "get", String.class);
        }

        load = Reflection.getMethod(stackClass, "load", nbtTag);
        asBukkitCopy = Reflection.getMethod(craftStack, "asBukkitCopy", stackClass);

        save = Reflection.getMethod(stackClass, "save", nbtTag);
        toString = Reflection.getMethod(nbtTag, "toString");
        asCopy = Reflection.getMethod(craftStack, "asNMSCopy", ItemStack.class);
    }

    public static ItemStack toItemStack (StorageTagCompound compound) {
        if (!compound.hasKey("id")) return new ItemStack(Material.AIR); // Checks if it is an ItemStacks NBT/STC

        String material = compound.getString("id");
        Object item;
        if (registry != null) {
            item = Reflection.invoke(getItem, registry, newMCKey(material)); // Finds the material, Returns { Item }
        }else{
            item = Reflection.invoke(getItem, null, material); // Finds the material, Returns { Item }
        }

        Object nmsStack = Reflection.initiateClass(newStack, item); // Will make it an NMS ItemStack

        Reflection.invoke(load, nmsStack, toNBTTag(compound));

        return (ItemStack) Reflection.invoke(asBukkitCopy, null, nmsStack);
    }

    public static StorageTagCompound toStorage (ItemStack item) {
        Object nbt = Reflection.invoke(save, asNMSCopy(item), newNBTTag(nbtTag));
        String json = (String) Reflection.invoke(toString, nbt);

        // Removes the extra formatting that Spigot adds
        if (json.contains("{\"text\":\"")) json = json.replace("'{\"text\":\"", "\"").replace("\"}'", "\"");
        StorageTagCompound compound = new StorageTagCompound ();

        try {
            compound = JsonToNBT.getTagFromJson(json);
        } catch (NBTException e) {
            e.printStackTrace();
        }

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
