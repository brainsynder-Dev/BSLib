package lib.brainsynder.utils;

public interface DualCallback<T, F> {
    void dial(T value, F value2);
}