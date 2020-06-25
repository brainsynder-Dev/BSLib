package lib.brainsynder;

import org.bukkit.Bukkit;

public enum ServerVersion {
    // ---- START ---- //
    UNKNOWN,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1
    ; // ---- END ---- //

    /**
     * This will fetch the servers version as an {@link Integer}
     *      EG: ServerVersion.v1_8_R3 will return 183
     */
    public int getIntVersion () {
        if (name().equals("UNKNOWN")) return -1;
        String name = name().replace("v", "").replace("R", "");
        int version = 17;
        if (!name.isEmpty()) version = Integer.parseInt(name.replace("_", ""));
        return version;
    }

    /** Will fetch the servers current {@link ServerVersion} */
    public static ServerVersion getVersion () {
        for (ServerVersion version : values()) {
            if (version.name().equals(Bukkit.getServer().getClass().getPackage().getName().substring(23))) {
                return version;
            }
        }
        return ServerVersion.UNKNOWN;
    }

    /** Will check if the servers version is equal or newer then the {@param version} */
    public static boolean isEqualNew (ServerVersion version) {
        return (getVersion().getIntVersion() >= version.getIntVersion());
    }

    /** Will check if the servers version is newer then the {@param version} */
    public static boolean isNewer (ServerVersion version) {
        return (getVersion().getIntVersion() > version.getIntVersion());
    }

    /** Will check if the servers version is equal to the {@param version} */
    public static boolean isEqual (ServerVersion version) {
        return (getVersion().getIntVersion() == version.getIntVersion());
    }

    /** Will check if the servers version is equal or older then the {@param version} */
    public static boolean isEqualOld (ServerVersion version) {
        return (getVersion().getIntVersion() <= version.getIntVersion());
    }

    /** Will check if the servers version is older then the {@param version} */
    public static boolean isOlder (ServerVersion version) {
        return (getVersion().getIntVersion() < version.getIntVersion());
    }
}
