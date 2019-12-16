package lib.brainsynder.particle;

import lib.brainsynder.ServerVersion;

public enum Particle {
    UNKNOWN(false, "", -1, ServerVersion.UNKNOWN),
    BARRIER(false, "barrier", 35, ServerVersion.v1_8_R3),
    BLOCK_CRACK(true, "tilecrack_", 37, ServerVersion.v1_8_R3),
    BLOCK_DUST(true, "blockdust_", 38, ServerVersion.v1_8_R3),
    CLOUD(false, "cloud", 29, ServerVersion.v1_8_R3),
    CRIT(false, "crit", 9, ServerVersion.v1_8_R3),
    CRIT_MAGIC(false, "magicCrit", 10, ServerVersion.v1_8_R3),
    DRIP_LAVA(false, "dripLava", 19, ServerVersion.v1_8_R3),
    DRIP_WATER(false, "dripWater", 18, ServerVersion.v1_8_R3),
    ENCHANTMENT_TABLE(false, "enchantmenttable", 25, ServerVersion.v1_8_R3),
    EXPLOSION_HUGE(false, "hugeexplosion", 2, ServerVersion.v1_8_R3),
    EXPLOSION_LARGE(false, "largeexplode", 1, ServerVersion.v1_8_R3),
    EXPLOSION_NORMAL(false, "explode", 0, ServerVersion.v1_8_R3),
    FIREWORKS_SPARK(false, "fireworksSpark", 3, ServerVersion.v1_8_R3),
    FLAME(false, "flame", 26, ServerVersion.v1_8_R3),
    @Deprecated FOOTSTEP(false, "footstep", 28, ServerVersion.v1_8_R3, ServerVersion.v1_12_R1),  /*  Was removed in 1.13 */
    HEART(false, "heart", 34, ServerVersion.v1_8_R3),
    ITEM_CRACK(true, "iconcrack_", 36, ServerVersion.v1_8_R3),
    @Deprecated ITEM_TAKE(false, "take", 40, ServerVersion.v1_8_R3, ServerVersion.v1_12_R1),  /*  Was removed in 1.13 */
    LAVA(false, "lava", 27, ServerVersion.v1_8_R3),
    MOB_APPEARANCE(false, "mobappearance", 41, ServerVersion.v1_8_R3),
    NOTE(false, "note", 23, ServerVersion.v1_8_R3),
    PORTAL(false, "portal", 24, ServerVersion.v1_8_R3),
    REDSTONE(true, "reddust", 30, ServerVersion.v1_8_R3),
    SLIME(false, "slime", 33, ServerVersion.v1_8_R3),
    SMOKE_LARGE(false, "largesmoke", 12, ServerVersion.v1_8_R3),
    SMOKE_NORMAL(false, "smoke", 11, ServerVersion.v1_8_R3),
    SNOWBALL(false, "snowballpoof", 31, ServerVersion.v1_8_R3),
    SNOW_SHOVEL(false, "snowshovel", 32, ServerVersion.v1_8_R3),
    SPELL(false, "spell", 13, ServerVersion.v1_8_R3),
    SPELL_INSTANT(false, "instantSpell", 14, ServerVersion.v1_8_R3),
    SPELL_MOB(false, "mobSpell", 15, ServerVersion.v1_8_R3),
    SPELL_MOB_AMBIENT(false, "mobSpellAmbient", 16, ServerVersion.v1_8_R3),
    SPELL_WITCH(false, "witchMagic", 17, ServerVersion.v1_8_R3),
    SUSPENDED(false, "suspended", 7, ServerVersion.v1_8_R3),
    SUSPENDED_DEPTH(false, "depthsuspend", 8, ServerVersion.v1_8_R3),
    TOWN_AURA(false, "townaura", 22, ServerVersion.v1_8_R3),
    VILLAGER_ANGRY(false, "angryVillager", 20, ServerVersion.v1_8_R3),
    VILLAGER_HAPPY(false, "happyVillager", 21, ServerVersion.v1_8_R3),
    WATER_BUBBLE(false, "bubble", 4, ServerVersion.v1_8_R3),
    WATER_DROP(false, "droplet", 39, ServerVersion.v1_8_R3),
    WATER_SPLASH(false, "splash", 5, ServerVersion.v1_8_R3),
    WATER_WAKE(false, "wake", 6, ServerVersion.v1_8_R3),


    /* Added in 1.9 */
    DAMAGE_INDICATOR(false, "damageIndicator", -1, ServerVersion.v1_9_R1),
    DRAGON_BREATH(false, "dragonbreath", -1, ServerVersion.v1_9_R1),
    END_ROD(false, "endRod", -1, ServerVersion.v1_9_R1),
    SWEEP_ATTACK(false, "sweepAttack", -1, ServerVersion.v1_9_R1),


    /* Added in 1.11 */
    FALLING_DUST(true, "fallingdust", -1, ServerVersion.v1_11_R1),
    SPIT(false, "spit", -1, ServerVersion.v1_11_R1),
    TOTEM(false, "totem", -1, ServerVersion.v1_11_R1),


    /* Added in 1.13 */
    BUBBLE_COLUMN_UP(ServerVersion.v1_13_R1),
    BUBBLE_POP(ServerVersion.v1_13_R1),
    CURRENT_DOWN(ServerVersion.v1_13_R1),
    DOLPHIN(ServerVersion.v1_13_R1),
    @Deprecated LEGACY_BLOCK_CRACK(true, "legacy_block_crack", -1, ServerVersion.v1_13_R1),
    @Deprecated LEGACY_BLOCK_DUST(true, "legacy_block_dust", -1, ServerVersion.v1_13_R1),
    @Deprecated LEGACY_FALLING_DUST(true, "legacy_falling_dust", -1, ServerVersion.v1_13_R1),
    NAUTILUS(ServerVersion.v1_13_R1),
    SQUID_INK(ServerVersion.v1_13_R1),


    /* Added in 1.14 */
    CAMPFIRE_COSY_SMOKE(ServerVersion.v1_14_R1),
    CAMPFIRE_SIGNAL_SMOKE(ServerVersion.v1_14_R1),
    COMPOSTER(ServerVersion.v1_14_R1),
    FALLING_LAVA(ServerVersion.v1_14_R1),
    FALLING_WATER(ServerVersion.v1_14_R1),
    FLASH(ServerVersion.v1_14_R1),
    LANDING_LAVA(ServerVersion.v1_14_R1),
    SNEEZE(ServerVersion.v1_14_R1),


    /* Added in 1.15 */
    DRIPPING_HONEY(ServerVersion.v1_15_R1),
    FALLING_HONEY(ServerVersion.v1_15_R1),
    LANDING_HONEY(ServerVersion.v1_15_R1),
    FALLING_NECTAR(ServerVersion.v1_15_R1);

    private String name;
    private int id = -1;
    private boolean requiresData = false;
    private ServerVersion version;
    private ServerVersion maxVersion = ServerVersion.UNKNOWN;

    Particle(ServerVersion version) {
        this.name = name().toLowerCase();
        this.version = version;
    }

    Particle(ServerVersion version, ServerVersion maxVersion) {
        this.name = name().toLowerCase();
        this.version = version;
        this.maxVersion = maxVersion;
    }

    Particle(String name, ServerVersion version, ServerVersion maxVersion) {
        this.name = ((version == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
        this.version = version;
        this.maxVersion = maxVersion;
    }

    Particle(boolean requiresData, String name, int id, ServerVersion version) {
        this.name = ((version == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
        this.id = id;
        this.requiresData = requiresData;
        this.version = version;
        this.maxVersion = ServerVersion.UNKNOWN;
    }

    Particle(boolean requiresData, String name, int id, ServerVersion version, ServerVersion maxVersion) {
        this.name = ((version == ServerVersion.v1_13_R1) ? name().toLowerCase() : name);
        this.id = id;
        this.requiresData = requiresData;
        this.version = version;
        this.maxVersion = maxVersion;
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

    public ServerVersion getSupportedVersion() {
        return version;
    }

    public ServerVersion getMaxVersion() {
        return maxVersion;
    }

    public String getAllowedVersion() {
        String versionName = version.name();
        if (maxVersion != ServerVersion.UNKNOWN)
            versionName += "-" + maxVersion.name();
        return versionName;
    }

    public boolean isCompatable() {
        if (maxVersion == ServerVersion.UNKNOWN) return ServerVersion.isEqualNew(version);
        return !ServerVersion.isOlder(version);
    }

    public boolean requiresData() {
        return requiresData;
    }

    public int getVersion() {
        return version.getIntVersion();
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
}