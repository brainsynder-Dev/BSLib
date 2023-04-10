package lib.brainsynder.utils;

public interface FailCallback<T, Error extends Throwable> {
    void dial(T value);

    default void fail (Error value){}
}
