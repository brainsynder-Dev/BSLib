package lib.brainsynder.files.options;

import java.util.List;

public interface YamlOption {
    /**
     * The configuration path
     *
     * Example: Config.line1
     */
    String getPath ();

    /**
     * The Default value for the option
     */
    Object getDefault();

    /**
     * If the option moved list the old path in this list and run the move method in {@link lib.brainsynder.files.Movable#move(YamlOption)}
     */
    List<String> getOldPaths();

    default String getComment () {
        return "";
    }
}
