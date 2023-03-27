package lib.brainsynder.optional;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BiOptional<T, U> {

    @Nullable private final T left;
    @Nullable private final U right;

    public BiOptional(@Nullable T left, @Nullable U right) {
        this.left = left;
        this.right = right;
    }

    public static <T, U> BiOptional<T, U> of (T left) {
        Optional<T> LeftOption = Optional.empty();
        if (left != null) LeftOption = Optional.of(left);
        return from(LeftOption, Optional.empty());
    }

    public static <T, U> BiOptional<T, U> of (T left, U right) {
        Optional<T> LeftOption = Optional.empty();
        Optional<U> rightOption = Optional.empty();
        if (left != null) LeftOption = Optional.of(left);
        if (right != null) rightOption = Optional.of(right);
        return from(LeftOption, rightOption);
    }

    public static <T, U> BiOptional<T, U> empty () {
        return from(Optional.empty(), Optional.empty());
    }

    public static <T, U> BiOptional<T, U> from(Optional<T> left, Optional<U> right) {
        return new BiOptional<>(left.orElse(null), right.orElse(null));
    }

    public Optional<T> left() {
        return Optional.ofNullable(left);
    }

    public Optional<U> right() {
        return Optional.ofNullable(right);
    }

    public boolean isLeftPresent() {
        return left != null;
    }

    public boolean isRightPresent() {
        return right != null;
    }

    public boolean isLeftOnlyPresent() {
        return isLeftPresent() && !isRightPresent();
    }

    public boolean isRightOnlyPresent() {
        return !isLeftPresent() && isRightPresent();
    }

    public boolean areBothPresent() {
        return isLeftPresent() && isRightPresent();
    }

    public boolean areNonePresent() {
        return !isLeftPresent() && !isRightPresent();
    }

    public BiOptional<T, U> ifLeftOnlyPresent(Consumer<? super T> ifLeftOnlyPresent) {
        if (isLeftOnlyPresent()) ifLeftOnlyPresent.accept(left);
        return this;
    }

    public BiOptional<T, U> ifRightOnlyPresent(Consumer<? super U> ifRightOnlyPresent) {
        if (isRightOnlyPresent()) ifRightOnlyPresent.accept(right);
        return this;
    }

    public BiOptional<T, U> ifBothPresent(BiConsumer<? super T, ? super U> ifBothPresent) {
        if (areBothPresent()) ifBothPresent.accept(left, right);
        return this;
    }

    public BiOptional<T, U> ifNonePresent(Runnable ifNonePresent) {
        if (areNonePresent()) ifNonePresent.run();
        return this;
    }

    public <X extends Throwable> void ifNonePresentThrow(Supplier<? extends X> throwableProvider) throws X {
        if (areNonePresent()) throw throwableProvider.get();
    }

    public <R> BiOptionalMapper<T, U, R> mapper() {
        return new BiOptionalMapper<>(this);
    }
}