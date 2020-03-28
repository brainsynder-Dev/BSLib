package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FireworkMetaHandler extends MetaHandler<FireworkMeta> {

    public FireworkMetaHandler(FireworkMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof FireworkMeta)) return;
        FireworkMeta firework = (FireworkMeta) meta;
        StorageTagCompound compound = new StorageTagCompound();
        if (firework.getPower() != 0) compound.setInteger("power", firework.getPower());
        if (!firework.getEffects().isEmpty()) {
            StorageTagList list = new StorageTagList();
            firework.getEffects().forEach(effect -> list.appendTag(toCompound(effect)));
            compound.setTag("effects", list);
        }
        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        if (compound.hasKey("power")) {
            modifyMeta(value -> {
                value.setPower(compound.getInteger("power"));
                return value;
            });
        }
        if (compound.hasKey("effects")) {
            List<FireworkEffect> effects = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("effects");
            list.getTagList().forEach(storageBase -> {
                effects.add(fromCompoundEffect((StorageTagCompound) storageBase));
            });
            modifyMeta(value -> {
                value.addEffects(effects);
                return value;
            });
        }
    }

    private FireworkEffect fromCompoundEffect(StorageTagCompound compound) {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.with(FireworkEffect.Type.valueOf(compound.getString("type", "BALL")));
        if (compound.hasKey("trail")) builder.trail(compound.getBoolean("trail"));
        if (compound.hasKey("flicker")) builder.flicker(compound.getBoolean("flicker"));
        if (compound.hasKey("colors")) {
            List<Color> colors = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("colors");
            list.getTagList().forEach(storageBase -> colors.add(((StorageTagString) storageBase).getAsColor()));
            builder.withColor(colors);
        }
        if (compound.hasKey("fade-colors")) {
            List<Color> colors = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("fade-colors");
            list.getTagList().forEach(storageBase -> colors.add(((StorageTagString) storageBase).getAsColor()));
            builder.withFade(colors);
        }


        return builder.build();
    }

    private StorageTagCompound toCompound(FireworkEffect effect) {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setString("type", effect.getType().name());
        if (effect.hasTrail()) compound.setBoolean("trail", true);
        if (effect.hasFlicker()) compound.setBoolean("flicker", true);
        if (!effect.getColors().isEmpty()) {
            StorageTagList colors = new StorageTagList();
            effect.getColors().forEach(color -> colors.appendTag(new StorageTagString(color)));
            compound.setTag("colors", colors);
        }
        if (!effect.getFadeColors().isEmpty()) {
            StorageTagList fade = new StorageTagList();
            effect.getFadeColors().forEach(color -> fade.appendTag(new StorageTagString(color)));
            compound.setTag("fade-colors", fade);
        }
        return compound;
    }
}
