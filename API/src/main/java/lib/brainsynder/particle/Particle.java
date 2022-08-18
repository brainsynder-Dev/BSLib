package lib.brainsynder.particle;

import lib.brainsynder.EnumVersion;
import lib.brainsynder.ServerVersion;

import java.lang.annotation.Annotation;

// https://minecraft.fandom.com/wiki/Particles
@Deprecated
public enum Particle {
    UNKNOWN,
    // ---- START ---- //
    @EnumVersion(version = ServerVersion.v1_8_R3) BARRIER("barrier", 35),
    @EnumVersion(version = ServerVersion.v1_8_R3) BLOCK_CRACK(true, "tilecrack_", 37),
    @EnumVersion(version = ServerVersion.v1_8_R3) BLOCK_DUST(true, "blockdust_", 38),
    @EnumVersion(version = ServerVersion.v1_8_R3) CLOUD("cloud", 29),
    @EnumVersion(version = ServerVersion.v1_8_R3) CRIT("crit", 9),
    @EnumVersion(version = ServerVersion.v1_8_R3) CRIT_MAGIC("magicCrit", 10),
    @EnumVersion(version = ServerVersion.v1_8_R3) DRIP_LAVA("dripLava", 19),
    @EnumVersion(version = ServerVersion.v1_8_R3) DRIP_WATER("dripWater", 18),
    @EnumVersion(version = ServerVersion.v1_8_R3) ENCHANTMENT_TABLE("enchantmenttable", 25),
    @EnumVersion(version = ServerVersion.v1_8_R3) EXPLOSION_HUGE("hugeexplosion", 2),
    @EnumVersion(version = ServerVersion.v1_8_R3) EXPLOSION_LARGE("largeexplode", 1),
    @EnumVersion(version = ServerVersion.v1_8_R3) EXPLOSION_NORMAL("explode", 0),
    @EnumVersion(version = ServerVersion.v1_8_R3) FIREWORKS_SPARK("fireworksSpark", 3),
    @EnumVersion(version = ServerVersion.v1_8_R3) FLAME("flame", 26),
    @EnumVersion(version = ServerVersion.v1_8_R3) FOOTSTEP("footstep", 28),
    @EnumVersion(version = ServerVersion.v1_8_R3) HEART("heart", 34),
    @EnumVersion(version = ServerVersion.v1_8_R3) ITEM_CRACK(true, "iconcrack_", 36),
    @EnumVersion(version = ServerVersion.v1_8_R3) ITEM_TAKE("take", 40),
    @EnumVersion(version = ServerVersion.v1_8_R3) LAVA("lava", 27),
    @EnumVersion(version = ServerVersion.v1_8_R3) MOB_APPEARANCE("mobappearance", 41),
    @EnumVersion(version = ServerVersion.v1_8_R3) NOTE("note", 23),
    @EnumVersion(version = ServerVersion.v1_8_R3) PORTAL("portal", 24),
    @EnumVersion(version = ServerVersion.v1_8_R3) REDSTONE(true, "reddust", 30),
    @EnumVersion(version = ServerVersion.v1_8_R3) SLIME("slime", 33),
    @EnumVersion(version = ServerVersion.v1_8_R3) SMOKE_LARGE("largesmoke", 12),
    @EnumVersion(version = ServerVersion.v1_8_R3) SMOKE_NORMAL("smoke", 11),
    @EnumVersion(version = ServerVersion.v1_8_R3) SNOWBALL("snowballpoof", 31),
    @EnumVersion(version = ServerVersion.v1_8_R3) SNOW_SHOVEL("snowshovel", 32),
    @EnumVersion(version = ServerVersion.v1_8_R3) SPELL("spell", 13),
    @EnumVersion(version = ServerVersion.v1_8_R3) SPELL_INSTANT("instantSpell", 14),
    @EnumVersion(version = ServerVersion.v1_8_R3) SPELL_MOB("mobSpell", 15),
    @EnumVersion(version = ServerVersion.v1_8_R3) SPELL_MOB_AMBIENT("mobSpellAmbient", 16),
    @EnumVersion(version = ServerVersion.v1_8_R3) SPELL_WITCH("witchMagic", 17),
    @EnumVersion(version = ServerVersion.v1_8_R3) SUSPENDED("suspended", 7),
    @EnumVersion(version = ServerVersion.v1_8_R3) SUSPENDED_DEPTH("depthsuspend", 8),
    @EnumVersion(version = ServerVersion.v1_8_R3) TOWN_AURA("townaura", 22),
    @EnumVersion(version = ServerVersion.v1_8_R3) VILLAGER_ANGRY("angryVillager", 20),
    @EnumVersion(version = ServerVersion.v1_8_R3) VILLAGER_HAPPY("happyVillager", 21),
    @EnumVersion(version = ServerVersion.v1_8_R3) WATER_BUBBLE("bubble", 4),
    @EnumVersion(version = ServerVersion.v1_8_R3) WATER_DROP("droplet", 39),
    @EnumVersion(version = ServerVersion.v1_8_R3) WATER_SPLASH("splash", 5),
    @EnumVersion(version = ServerVersion.v1_8_R3) WATER_WAKE("wake", 6),


    // ---- Particles added in v1_9_R1 ---- //
    @EnumVersion(version = ServerVersion.v1_9_R1) DAMAGE_INDICATOR,
    @EnumVersion(version = ServerVersion.v1_9_R1) DRAGON_BREATH,
    @EnumVersion(version = ServerVersion.v1_9_R1) END_ROD,
    @EnumVersion(version = ServerVersion.v1_9_R1) SWEEP_ATTACK,


    // ---- Particles added in v1_11_R1 ---- //
    @EnumVersion(version = ServerVersion.v1_11_R1) FALLING_DUST(true, "fallingdust"),
    @EnumVersion(version = ServerVersion.v1_11_R1) SPIT("spit"),
    @EnumVersion(version = ServerVersion.v1_11_R1) TOTEM("totem"),


    // ---- Particles added in v1_13_R1 ---- //
    @EnumVersion(version = ServerVersion.v1_13_R1) BUBBLE_COLUMN_UP,
    @EnumVersion(version = ServerVersion.v1_13_R1) BUBBLE_POP,
    @EnumVersion(version = ServerVersion.v1_13_R1) CURRENT_DOWN,
    @EnumVersion(version = ServerVersion.v1_13_R1) DOLPHIN,
    @EnumVersion(version = ServerVersion.v1_13_R1) LEGACY_BLOCK_CRACK(true, "legacy_block_crack", -1),
    @EnumVersion(version = ServerVersion.v1_13_R1) LEGACY_BLOCK_DUST(true, "legacy_block_dust", -1),
    @EnumVersion(version = ServerVersion.v1_13_R1) LEGACY_FALLING_DUST(true, "legacy_falling_dust", -1),
    @EnumVersion(version = ServerVersion.v1_13_R1) NAUTILUS,
    @EnumVersion(version = ServerVersion.v1_13_R1) SQUID_INK,


    // ---- Particles added in v1_14_R1 ---- //
    @EnumVersion(version = ServerVersion.v1_14_R1) CAMPFIRE_COSY_SMOKE,
    @EnumVersion(version = ServerVersion.v1_14_R1) CAMPFIRE_SIGNAL_SMOKE,
    @EnumVersion(version = ServerVersion.v1_14_R1) COMPOSTER,
    @EnumVersion(version = ServerVersion.v1_14_R1) FALLING_LAVA,
    @EnumVersion(version = ServerVersion.v1_14_R1) FALLING_WATER,
    @EnumVersion(version = ServerVersion.v1_14_R1) FLASH,
    @EnumVersion(version = ServerVersion.v1_14_R1) LANDING_LAVA,
    @EnumVersion(version = ServerVersion.v1_14_R1) SNEEZE,


    // ---- Particles added in v1_15_R1 ---- //
    @EnumVersion(version = ServerVersion.v1_15_R1) DRIPPING_HONEY,
    @EnumVersion(version = ServerVersion.v1_15_R1) FALLING_HONEY,
    @EnumVersion(version = ServerVersion.v1_15_R1) FALLING_NECTAR,
    @EnumVersion(version = ServerVersion.v1_15_R1) LANDING_HONEY,


    // ---- Particles added in v1_16_R1 ---- //
    @EnumVersion(version = ServerVersion.v1_16_R1) ASH,
    @EnumVersion(version = ServerVersion.v1_16_R1) CRIMSON_SPORE,
    @EnumVersion(version = ServerVersion.v1_16_R1) DRIPPING_OBSIDIAN_TEAR,
    @EnumVersion(version = ServerVersion.v1_16_R1) FALLING_OBSIDIAN_TEAR,
    @EnumVersion(version = ServerVersion.v1_16_R1) LANDING_OBSIDIAN_TEAR,
    @EnumVersion(version = ServerVersion.v1_16_R1) REVERSE_PORTAL,
    @EnumVersion(version = ServerVersion.v1_16_R1) SOUL,
    @EnumVersion(version = ServerVersion.v1_16_R1) SOUL_FIRE_FLAME,
    @EnumVersion(version = ServerVersion.v1_16_R1) WARPED_SPORE,


    // ---- Particles added in v1_17 ---- //
    @EnumVersion(version = ServerVersion.v1_17) LIGHT,
    @EnumVersion(version = ServerVersion.v1_17) DUST_COLOR_TRANSITION(true),
    @EnumVersion(version = ServerVersion.v1_17) VIBRATION,
    @EnumVersion(version = ServerVersion.v1_17) FALLING_SPORE_BLOSSOM,
    @EnumVersion(version = ServerVersion.v1_17) SPORE_BLOSSOM_AIR,
    @EnumVersion(version = ServerVersion.v1_17) SMALL_FLAME,
    @EnumVersion(version = ServerVersion.v1_17) SNOWFLAKE,
    @EnumVersion(version = ServerVersion.v1_17) DRIPPING_DRIPSTONE_LAVA,
    @EnumVersion(version = ServerVersion.v1_17) FALLING_DRIPSTONE_LAVA,
    @EnumVersion(version = ServerVersion.v1_17) DRIPPING_DRIPSTONE_WATER,
    @EnumVersion(version = ServerVersion.v1_17) FALLING_DRIPSTONE_WATER,
    @EnumVersion(version = ServerVersion.v1_17) GLOW_SQUID_INK,
    @EnumVersion(version = ServerVersion.v1_17) GLOW,
    @EnumVersion(version = ServerVersion.v1_17) WAX_ON,
    @EnumVersion(version = ServerVersion.v1_17) WAX_OFF,
    @EnumVersion(version = ServerVersion.v1_17) ELECTRIC_SPARK,
    @EnumVersion(version = ServerVersion.v1_17) SCRAPE,


    // ---- Particles added in 1_19 ---- //
    @EnumVersion(version = ServerVersion.v1_19) WHITE_ASH,
    @EnumVersion(version = ServerVersion.v1_19) SONIC_BOOM,
    @EnumVersion(version = ServerVersion.v1_19) SCULK_SOUL,
    @EnumVersion(version = ServerVersion.v1_19) SCULK_CHARGE,
    @EnumVersion(version = ServerVersion.v1_19) SCULK_CHARGE_POP,
    @EnumVersion(version = ServerVersion.v1_19) SHRIEK,
    @EnumVersion(version = ServerVersion.v1_19) BLOCK_MARKER(true)
    ; // ---- END ---- //

    private final String name;
    private int id = -1;
    private boolean requiresData = false;

    Particle() {
        this.name = name().toLowerCase();
    }
    Particle(boolean requiresData) {
        this.name = name().toLowerCase();
        this.requiresData = requiresData;
    }
    Particle(String name) {
        this.name = ((ServerVersion.getVersion() == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
    }
    Particle(boolean requiresData, String name, int id) {
        this.name = ((ServerVersion.getVersion() == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
        this.id = id;
        this.requiresData = requiresData;
    }
    Particle(String name, int id) {
        this.name = ((ServerVersion.getVersion() == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
        this.id = id;
    }
    Particle(boolean requiresData, String name) {
        this.name = ((ServerVersion.getVersion() == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
        this.requiresData = requiresData;
    }

    @Deprecated
    public static Particle getById(int id) {
        if (id == -1) return UNKNOWN;
        for (Particle particle : values()) {
            if (particle.getId() == id)
                return particle;
        }
        return UNKNOWN;
    }

    public static Particle getByName(String name) {
        if (name.isEmpty()) return UNKNOWN;
        for (Particle particle : values()) {
            if (particle.getName().equalsIgnoreCase(name))
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
                if (annotation instanceof EnumVersion) {
                    EnumVersion support = (EnumVersion) annotation;
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

//    public boolean isCompatable() {
//        if (maxVersion == ServerVersion.UNKNOWN) return ServerVersion.isEqualNew(version);
//        return !ServerVersion.isOlder(version);
//    }

    public boolean requiresData() {
        return requiresData;
    }

    public String getName() {
        return this.name;
    }

    public String fetchName() {
        return name().toLowerCase();
    }

    @Deprecated
    public int getId() {
        return this.id;
    }

    public boolean isCompatible() {
        try {
            Annotation[] annotations = getClass().getField(name()).getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof EnumVersion) {
                    EnumVersion support = (EnumVersion) annotation;
                    if (support.maxVersion() != ServerVersion.UNKNOWN)
                        return (ServerVersion.isEqualOld(support.maxVersion()) && (ServerVersion.isEqualNew(support.version())));
                    return ServerVersion.isEqualNew(support.version());
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }
}


