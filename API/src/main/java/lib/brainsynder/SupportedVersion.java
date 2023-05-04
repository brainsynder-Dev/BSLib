package lib.brainsynder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SupportedVersion {
    /**
     * What version does support for this Class start from
     *
     * @return {@link lib.brainsynder.ServerVersion}
     */
    ServerVersion version();

    /**
     * If the class is restricted to certain versions
     * <p>
     * Example: <code>version=v1_19_R1  maxVersion=v1_12_R1</code>
     * Will only make the class work on 1.9 -> 1.12
     *
     * @return {@link lib.brainsynder.ServerVersion}
     */
    ServerVersion maxVersion() default ServerVersion.UNKNOWN;
}