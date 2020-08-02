package lib.brainsynder.files;

import java.util.function.BiConsumer;

public interface Movable {
    void move (String oldKey, String newKey);
    default void move (String oldKey, String newKey, BiConsumer<String, String> onMove){
        move(oldKey, newKey);
        onMove.accept(oldKey, newKey);
    }
}
