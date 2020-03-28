package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionMetaHandler extends MetaHandler<PotionMeta> {

    public PotionMetaHandler(PotionMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        PotionMeta potionMeta = (PotionMeta) meta;

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
                if (potionEffect.getColor() != null) effect.setColor("color", potionEffect.getColor());
                list.appendTag(effect);
            });
            compound.setTag("effects", list);
        }

        StorageTagCompound data = new StorageTagCompound();
        data.setString("type", potionMeta.getBasePotionData().getType().name());
        data.setBoolean("extended", potionMeta.getBasePotionData().isExtended());
        data.setBoolean("upgraded", potionMeta.getBasePotionData().isUpgraded());
        compound.setTag("base", data);

        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        modifyMeta(value -> {
            if (compound.hasKey("color")) value.setColor(compound.getColor("color"));
            if (compound.hasKey("base")) {
                PotionData data = new PotionData(PotionType.valueOf(compound.getString("type", "WATER")), compound.getBoolean("extended", false), compound.getBoolean("upgraded", false));
                value.setBasePotionData(data);
            }

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
                            effect.getColor("color", null));
                    value.addCustomEffect(potionEffect, false);
                });
            }
            return value;
        });
    }
}
