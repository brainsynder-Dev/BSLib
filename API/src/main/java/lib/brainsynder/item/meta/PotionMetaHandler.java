package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionMetaHandler extends MetaHandler<PotionMeta> {

    public PotionMetaHandler(PotionMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof PotionMeta potionMeta)) return;

        StorageTagCompound compound = new StorageTagCompound();

        if (potionMeta.hasColor()) compound.setColor("color", potionMeta.getColor());

        if (!potionMeta.getCustomEffects().isEmpty()) {
            StorageTagList list = new StorageTagList();
            potionMeta.getCustomEffects().forEach(potionEffect -> {
                StorageTagCompound effect = new StorageTagCompound();
                effect.setString("type", potionEffect.getType().getName());
                effect.setBoolean("ambient", potionEffect.isAmbient());
                effect.setBoolean("particles", potionEffect.hasParticles());
                effect.setInteger("amplifier", potionEffect.getAmplifier());
                effect.setInteger("duration", potionEffect.getDuration());
                effect.setBoolean("icon", potionEffect.hasIcon());
                list.appendTag(effect);
            });
            compound.setTag("effects", list);
        }

        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        modifyMeta(value -> {
            if (compound.hasKey("color")) value.setColor(compound.getColor("color"));

            if (compound.hasKey("effects")) {
                StorageTagList list = (StorageTagList) compound.getTag("effects");
                list.getTagList().forEach(base -> {
                    StorageTagCompound effect = (StorageTagCompound) base;
                    PotionEffect potionEffect = new PotionEffect(
                            PotionEffectType.getByName(effect.getString("type", "SPEED")),
                            effect.getInteger("duration", 60),
                            effect.getInteger("amplifier", 1),
                            effect.getBoolean("ambient", true),
                            effect.getBoolean("particles", true),
                            effect.getBoolean("icon", true));
                    value.addCustomEffect(potionEffect, false);
                });
            }
            return value;
        });
    }
}
