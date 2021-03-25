package lib.brainsynder.utils;

public interface Callback<T, F> {
    void success(T value);

    default void fail (F value){}
}