package lib.brainsynder.files;

import java.util.function.BiConsumer;

public interface Movable {
    boolean move (String oldKey, String newKey);
    default boolean move (String oldKey, String newKey, BiConsumer<String, String> onMove){
        boolean move = move(oldKey, newKey);
        if (move) onMove.accept(oldKey, newKey);
        return move;
    }
}
