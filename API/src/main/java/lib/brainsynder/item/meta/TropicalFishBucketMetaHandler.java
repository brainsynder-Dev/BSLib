package lib.brainsynder.item.meta;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.SupportedVersion;
import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.DyeColor;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

@SupportedVersion(version = ServerVersion.v1_13_R1)
public class TropicalFishBucketMetaHandler extends MetaHandler {
    private Method getPattern, getBodyColor, getPatternColor = null,
            setPattern, setPatternColor = null, setBodyColor, valueOf;
    private Class metaClass, patternClass;

    public TropicalFishBucketMetaHandler(ItemMeta meta) {
        super(meta);
        metaClass = Reflection.getBukkitClass("inventory.meta.TropicalFishBucketMeta");
        if (metaClass == null) return;
        patternClass = Reflection.getBukkitClass("entity.TropicalFish$Pattern");
        getBodyColor = Reflection.getMethod(metaClass, "getBodyColor");
        getPatternColor = Reflection.getMethod(metaClass, "getPatternColor");
        getPattern = Reflection.getMethod(metaClass, "getPattern");
        setPatternColor = Reflection.getMethod(metaClass, "setPatternColor", DyeColor.class);
        setBodyColor = Reflection.getMethod(metaClass, "setBodyColor", DyeColor.class);
        setPattern = Reflection.getMethod(metaClass, "setPattern", patternClass);
        valueOf = Reflection.getMethod(patternClass, "valueOf", String.class);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (getPatternColor == null) return;
        StorageTagCompound compound = new StorageTagCompound();
        compound.setString("pattern-color", ((Enum)Reflection.invoke(getPatternColor, meta)).name());
        compound.setString("body-color", ((Enum)Reflection.invoke(getBodyColor, meta)).name());
        compound.setString("pattern", ((Enum)Reflection.invoke(getPattern, meta)).name());
        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        if (setPatternColor == null) return;
        modifyMeta(value -> {
            if (compound.hasKey("pattern-color")) Reflection.invoke(setPatternColor, value, DyeColor.valueOf(compound.getString("pattern-color")));
            if (compound.hasKey("body-color")) Reflection.invoke(setBodyColor, value, DyeColor.valueOf(compound.getString("body-color")));
            if (compound.hasKey("pattern")) Reflection.invoke(setPattern, value, Reflection.invoke(valueOf, null, compound.getString("pattern")));
            return value;
        });
    }
}