package lib.brainsynder.optional;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BiOptional<T, U> {

    @Nullable
    private final T first;
    @Nullable
    private final U second;

    public BiOptional(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public static <T, U> BiOptional<T, U> of (T first) {
        Optional<T> firstOption = Optional.empty();
        if (first != null) firstOption = Optional.of(first);
        return from(firstOption, Optional.empty());
    }

    public static <T, U> BiOptional<T, U> of (T first, U second) {
        Optional<T> firstOption = Optional.empty();
        Optional<U> secondOption = Optional.empty();
        if (first != null) firstOption = Optional.of(first);
        if (second != null) secondOption = Optional.of(second);
        return from(firstOption, secondOption);
    }

    public static <T, U> BiOptional<T, U> empty () {
        return from(Optional.empty(), Optional.empty());
    }

    public static <T, U> BiOptional<T, U> from(Optional<T> first, Optional<U> second) {
        return new BiOptional<>(first.orElse(null), second.orElse(null));
    }

    public Optional<T> first() {
        return Optional.ofNullable(first);
    }

    public Optional<U> second() {
        return Optional.ofNullable(second);
    }

    public boolean isFirstPresent() {
        return first != null;
    }

    public boolean isSecondPresent() {
        return second != null;
    }

    public boolean isFirstOnlyPresent() {
        return isFirstPresent() && !isSecondPresent();
    }

    public boolean isSecondOnlyPresent() {
        return !isFirstPresent() && isSecondPresent();
    }

    public boolean areBothPresent() {
        return isFirstPresent() && isSecondPresent();
    }

    public boolean areNonePresent() {
        return !isFirstPresent() && !isSecondPresent();
    }

    public BiOptional<T, U> ifFirstOnlyPresent(Consumer<? super T> ifFirstOnlyPresent) {
        if (isFirstOnlyPresent()) {
            ifFirstOnlyPresent.accept(first);
        }
        return this;
    }

    public BiOptional<T, U> ifSecondOnlyPresent(Consumer<? super U> ifSecondOnlyPresent) {
        if (isSecondOnlyPresent()) {
            ifSecondOnlyPresent.accept(second);
        }
        return this;
    }

    public BiOptional<T, U> ifBothPresent(BiConsumer<? super T, ? super U> ifBothPresent) {
        if (areBothPresent()) {
            ifBothPresent.accept(first, second);
        }
        return this;
    }

    public BiOptional<T, U> ifNonePresent(Runnable ifNonePresent) {
        if (areNonePresent()) {
            ifNonePresent.run();
        }
        return this;
    }

    public <X extends Throwable> void ifNonePresentThrow(Supplier<? extends X> throwableProvider) throws X {
        if (areNonePresent()) {
            throw throwableProvider.get();
        }
    }

    public <R> BiOptionalMapper<T, U, R> mapper() {
        return new BiOptionalMapper<>(this);
    }
}