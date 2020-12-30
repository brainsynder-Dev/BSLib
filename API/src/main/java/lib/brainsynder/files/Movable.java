package lib.brainsynder.files;

import lib.brainsynder.files.options.YamlOption;

import java.util.function.BiConsumer;

public interface Movable {
    default boolean move (YamlOption option) {
        return move(option, (s, s2) -> {});
    }

    default boolean move (YamlOption option, BiConsumer<String, String> onMove) {
        if ((option.getOldPaths() == null) || (option.getOldPaths().isEmpty())) return false;
        for (String path : option.getOldPaths()) {
            if (move(path, option.getPath(), onMove)) {
                return true;
            }
        }
        return false;
    }

    boolean move (String oldKey, String newKey);
    default boolean move (String oldKey, String newKey, BiConsumer<String, String> onMove){
        boolean move = move(oldKey, newKey);
        if (move) onMove.accept(oldKey, newKey);
        return move;
    }
}
