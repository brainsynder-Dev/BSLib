package lib.brainsynder.reflection;

public class ReflectionException extends RuntimeException {
    public ReflectionException (String message) {
        super(message);
    }
    public ReflectionException (String message, Throwable throwable) {
        super(message, throwable);
    }
}
