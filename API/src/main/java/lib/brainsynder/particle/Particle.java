package lib.brainsynder.particle;

import lib.brainsynder.EnumVersion;
import lib.brainsynder.ServerVersion;
import lib.brainsynder.particle.data.CustomColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Vibration;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.Annotation;

// https://minecraft.fandom.com/wiki/Particles
@Deprecated
public enum Particle {
    UNKNOWN("unknown"),
    POOF("poof"),
    EXPLOSION("explosion"),
    EXPLOSION_EMITTER("explosion_emitter"),
    FIREWORK("firework"),
    BUBBLE("bubble"),
    SPLASH("splash"),
    FISHING("fishing"),
    UNDERWATER("underwater"),
    CRIT("crit"),
    ENCHANTED_HIT("enchanted_hit"),
    SMOKE("smoke"),
    LARGE_SMOKE("large_smoke"),
    EFFECT("effect", CustomColor.class),
    INSTANT_EFFECT("instant_effect", CustomColor.class),
    ENTITY_EFFECT("entity_effect", Color.class),
    WITCH("witch"),
    DRIPPING_WATER("dripping_water"),
    DRIPPING_LAVA("dripping_lava"),
    ANGRY_VILLAGER("angry_villager"),
    HAPPY_VILLAGER("happy_villager"),
    MYCELIUM("mycelium"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANT("enchant"),
    FLAME("flame"),
    LAVA("lava"),
    CLOUD("cloud"),
    DUST("dust", org.bukkit.Particle.DustOptions.class),
    ITEM_SNOWBALL("item_snowball"),
    ITEM_SLIME("item_slime"),
    HEART("heart"),
    ITEM("item", ItemStack.class),
    BLOCK("block", BlockData.class),
    RAIN("rain"),
    ELDER_GUARDIAN("elder_guardian"),
    DRAGON_BREATH("dragon_breath"),
    END_ROD("end_rod"),
    DAMAGE_INDICATOR("damage_indicator"),
    SWEEP_ATTACK("sweep_attack"),
    FALLING_DUST("falling_dust", BlockData.class),
    TOTEM_OF_UNDYING("totem_of_undying"),
    SPIT("spit"),
    SQUID_INK("squid_ink"),
    BUBBLE_POP("bubble_pop"),
    CURRENT_DOWN("current_down"),
    BUBBLE_COLUMN_UP("bubble_column_up"),
    NAUTILUS("nautilus"),
    DOLPHIN("dolphin"),
    SNEEZE("sneeze"),
    CAMPFIRE_COSY_SMOKE("campfire_cosy_smoke"),
    CAMPFIRE_SIGNAL_SMOKE("campfire_signal_smoke"),
    COMPOSTER("composter"),
    FLASH("flash"),
    FALLING_LAVA("falling_lava"),
    LANDING_LAVA("landing_lava"),
    FALLING_WATER("falling_water"),
    DRIPPING_HONEY("dripping_honey"),
    FALLING_HONEY("falling_honey"),
    LANDING_HONEY("landing_honey"),
    FALLING_NECTAR("falling_nectar"),
    SOUL_FIRE_FLAME("soul_fire_flame"),
    ASH("ash"),
    CRIMSON_SPORE("crimson_spore"),
    WARPED_SPORE("warped_spore"),
    SOUL("soul"),
    DRIPPING_OBSIDIAN_TEAR("dripping_obsidian_tear"),
    FALLING_OBSIDIAN_TEAR("falling_obsidian_tear"),
    LANDING_OBSIDIAN_TEAR("landing_obsidian_tear"),
    REVERSE_PORTAL("reverse_portal"),
    WHITE_ASH("white_ash"),
    DUST_COLOR_TRANSITION("dust_color_transition", org.bukkit.Particle.DustTransition.class),
    VIBRATION("vibration", Vibration.class),
    FALLING_SPORE_BLOSSOM("falling_spore_blossom"),
    SPORE_BLOSSOM_AIR("spore_blossom_air"),
    SMALL_FLAME("small_flame"),
    SNOWFLAKE("snowflake"),
    DRIPPING_DRIPSTONE_LAVA("dripping_dripstone_lava"),
    FALLING_DRIPSTONE_LAVA("falling_dripstone_lava"),
    DRIPPING_DRIPSTONE_WATER("dripping_dripstone_water"),
    FALLING_DRIPSTONE_WATER("falling_dripstone_water"),
    GLOW_SQUID_INK("glow_squid_ink"),
    GLOW("glow"),
    WAX_ON("wax_on"),
    WAX_OFF("wax_off"),
    ELECTRIC_SPARK("electric_spark"),
    SCRAPE("scrape"),
    SONIC_BOOM("sonic_boom"),
    SCULK_SOUL("sculk_soul"),
    SCULK_CHARGE("sculk_charge", Float.class),
    SCULK_CHARGE_POP("sculk_charge_pop"),
    SHRIEK("shriek", Integer.class),
    CHERRY_LEAVES("cherry_leaves"),
    EGG_CRACK("egg_crack"),
    DUST_PLUME("dust_plume"),
    WHITE_SMOKE("white_smoke"),
    GUST("gust"),
    SMALL_GUST("small_gust"),
    GUST_EMITTER_LARGE("gust_emitter_large"),
    GUST_EMITTER_SMALL("gust_emitter_small"),
    TRIAL_SPAWNER_DETECTION("trial_spawner_detection"),
    TRIAL_SPAWNER_DETECTION_OMINOUS("trial_spawner_detection_ominous"),
    VAULT_CONNECTION("vault_connection"),
    INFESTED("infested"),
    ITEM_COBWEB("item_cobweb"),
    DUST_PILLAR("dust_pillar", BlockData.class),
    OMINOUS_SPAWNING("ominous_spawning"),
    RAID_OMEN("raid_omen"),
    TRIAL_OMEN("trial_omen"),
    /**
     * Uses {@link BlockData} as DataType
     */
    BLOCK_MARKER("block_marker", BlockData.class)
    ; // ---- END ---- //

    private final NamespacedKey key;
    private final Class<?> dataType;


    Particle(String key) {
        this(key, Void.class);
    }

    Particle(String key, Class<?> data) {
        if (key != null) {
            this.key = NamespacedKey.minecraft(key);
        } else {
            this.key = null;
        }
        dataType = data;
    }

    public static Particle getByName(String name) {
        if (name.isEmpty()) return UNKNOWN;
        for (Particle particle : values()) {
            if (particle.key.getKey().equalsIgnoreCase(name))
                return particle;
            if (particle.name().equalsIgnoreCase(name))
                return particle;
        }
        return UNKNOWN;
    }

    public String getAllowedVersion() {
        try {
            Annotation[] annotations = getClass().getField(name()).getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof EnumVersion support) {
                    String versionName = support.version().getNMS();
                    if (support.maxVersion() != ServerVersion.UNKNOWN)
                        versionName += "-" + support.maxVersion().getNMS();
                    return versionName;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public String fetchName() {
        return name().toLowerCase();
    }

    public boolean isCompatible() {
        try {
            Annotation[] annotations = getClass().getField(name()).getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof EnumVersion support) {
                    if (support.maxVersion() != ServerVersion.UNKNOWN)
                        return (ServerVersion.isEqualOld(support.maxVersion()) && (ServerVersion.isEqualNew(support.version())));
                    return ServerVersion.isEqualNew(support.version());
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return true;
    }
}


