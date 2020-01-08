package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireworkMetaBuilder extends MetaBuilder<FireworkMeta> {
    private int power = 0;
    private List<FireworkEffect> effects = new ArrayList<>();

    public int getPower() {
        return power;
    }

    public List<FireworkEffect> getEffects() {
        return effects;
    }

    public void clearEffects () {
        effects.clear();
        modifyMeta(value -> {
            value.clearEffects();
            return value;
        });
    }

    public void addEffect (FireworkEffect effect) {
        this.effects.add(effect);
        modifyMeta(value -> {
            value.addEffect(effect);
            return value;
        });
    }
    public void addEffects (FireworkEffect... effects) {
        this.effects.addAll(Arrays.asList(effects));
        modifyMeta(value -> {
            value.addEffects(effects);
            return value;
        });
    }
    public void addEffects(Iterable<FireworkEffect> collection) {
        for (FireworkEffect effect : collection) effects.add(effect);
        modifyMeta(value -> {
            value.addEffects(collection);
            return value;
        });
    }

    public void removeEffect (int index) {
        this.effects.remove(index);
        modifyMeta(value -> {
            value.removeEffect(index);
            return value;
        });
    }

    public void setPower(int power) {
        this.power = power;
        modifyMeta(value -> {
            value.setPower(power);
            return value;
        });
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof FireworkMeta)) return;
        FireworkMeta firework = (FireworkMeta) meta;
        addEffects(firework.getEffects());
        setPower(firework.getPower());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("power")) setPower(compound.getInteger("power"));
        if (compound.hasKey("effects")) {
            List<FireworkEffect> effects = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("effects");
            list.getTagList().forEach(storageBase -> {
                effects.add(fromCompound((StorageTagCompound) storageBase));
            });
            addEffects(effects);
        }
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        if (power != 0) compound.setInteger("power", power);
        if (!effects.isEmpty()) {
            StorageTagList list = new StorageTagList();
            effects.forEach(effect -> list.appendTag(toCompound(effect)));
            compound.setTag("effects", list);
        }
        return compound;
    }

    private FireworkEffect fromCompound(StorageTagCompound compound) {
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
