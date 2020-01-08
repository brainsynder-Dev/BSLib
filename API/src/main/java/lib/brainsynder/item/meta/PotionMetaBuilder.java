package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PotionMetaBuilder extends MetaBuilder<PotionMeta> {

    public void setColor(Color color) {
        PotionMeta meta = getMeta();
        meta.setColor(color);
        updateMeta(meta);
    }

    public Color getColor() {
        return getMeta().getColor();
    }


    public void setBasePotionData(PotionData potionData) {
        modifyMeta(value -> {
            value.setBasePotionData(potionData);
            return value;
        });
    }
    public PotionData getBasePotionData() {
        return getMeta().getBasePotionData();
    }

    public boolean hasCustomEffects() {
        return getMeta().hasCustomEffects();
    }

    public List<PotionEffect> getCustomEffects() {
        return getMeta().getCustomEffects();
    }

    public void addCustomEffect(PotionEffect var1, boolean var2) {
        modifyMeta(value -> {
            value.addCustomEffect(var1, var2);
            return value;
        });
    }

    public void removeCustomEffect(PotionEffectType var1) {
        modifyMeta(value -> {
            value.removeCustomEffect(var1);
            return value;
        });
    }

    public boolean hasCustomEffect(PotionEffectType var1) {
        return getMeta().hasCustomEffect(var1);
    }

    public void clearCustomEffects() {
        modifyMeta(value -> {
            value.clearCustomEffects();
            return value;
        });
    }

    public boolean hasColor() {
        return getMeta().hasColor();
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        PotionMeta potionMeta = (PotionMeta) meta;
        if (potionMeta.hasColor()) setColor(potionMeta.getColor());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("color")) setColor(compound.getColor("color"));
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();

        if (getMeta().hasColor()) compound.setColor("color", getMeta().getColor());
        if (!getMeta().getCustomEffects().isEmpty()) {
            StorageTagList list = new StorageTagList();
            getMeta().getCustomEffects().forEach(potionEffect -> {
                StorageTagCompound effect = new StorageTagCompound();
                effect.setString("type", potionEffect.getType().getName());
                effect.setBoolean("ambient", potionEffect.isAmbient());
                effect.setBoolean("particles", potionEffect.hasParticles());
                effect.setInteger("amplifier", potionEffect.getAmplifier());
                effect.setInteger("duration", potionEffect.getDuration());
                if (potionEffect.getColor() != null) effect.setColor("color", potionEffect.getColor());
                list.appendTag(effect);
            });
            compound.setTag("effects", list);
            compound.setColor("color", getMeta().getColor());
        }

        StorageTagCompound data = new StorageTagCompound();
        data.setString("type", getMeta().getBasePotionData().getType().name());
        data.setBoolean("extended", getMeta().getBasePotionData().isExtended());
        data.setBoolean("upgraded", getMeta().getBasePotionData().isUpgraded());
        compound.setTag("data", data);
        return compound;
    }
}
