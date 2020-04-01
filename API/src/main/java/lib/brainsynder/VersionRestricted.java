package lib.brainsynder;

public interface VersionRestricted {
    /**
     * Will check if the class is supported for the servers version
     *
     * @return boolean
     */
    default boolean isSupported () {
        SupportedVersion support = getSupportedVersion();
        if (support == null) return true;
        if (support.maxVersion() != ServerVersion.UNKNOWN)
            return (ServerVersion.isEqualOld(support.maxVersion()) && (ServerVersion.isEqualNew(support.version())));
        return ServerVersion.isEqualNew(support.version());
    }

    /**
     * Will return a {@link lib.brainsynder.SupportedVersion}
     *   If class does not have it will return `null`
     *
     * @return {@link lib.brainsynder.SupportedVersion}
     */
    default SupportedVersion getSupportedVersion() {
        if (!getClass().isAnnotationPresent(SupportedVersion.class)) return null;
        return getClass().getAnnotation(SupportedVersion.class);
    }
}
