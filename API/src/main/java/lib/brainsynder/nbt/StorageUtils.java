package lib.brainsynder.nbt;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import lib.brainsynder.nbt.other.IStorageList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class StorageUtils {
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
                            longs.add(Long.parseLong(string.replace("l", "")));
                        }else if (string.endsWith("b")) {
                            bytes.add(Byte.parseByte(string.replace("b", "")));
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
}
