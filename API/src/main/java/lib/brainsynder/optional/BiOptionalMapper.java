package lib.brainsynder.optional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BiOptionalMapper<T, U, R> {

    private final BiOptional<T, U> biOptional;
    private R result = null;

    BiOptionalMapper(BiOptional<T, U> biOptional) {
        this.biOptional = biOptional;
    }

    public BiOptionalMapper<T, U, R> onLeftOnlyPresent(Function<? super T, ? extends R> leftMapper) {
        if (biOptional.isLeftOnlyPresent()) setResult(leftMapper.apply(biOptional.left().get()));
        return this;
    }

    public BiOptionalMapper<T, U, R> onRightOnlyPresent(Function<? super U, ? extends R> rightMapper) {
        if (biOptional.isRightOnlyPresent()) setResult(rightMapper.apply(biOptional.right().get()));
        return this;
    }

    public BiOptionalMapper<T, U, R> onBothPresent(BiFunction<? super T, ? super U, ? extends R> bothMapper) {
        if (biOptional.areBothPresent()) setResult(bothMapper.apply(biOptional.left().get(), biOptional.right().get()));
        return this;
    }

    public BiOptionalMapper<T, U, R> onNonePresent(Supplier<? extends R> supplier) {
        if (biOptional.areNonePresent()) setResult(supplier.get());
        return this;
    }

    public BiOptionalMapper<T, U, R> onNonePresent(R other) {
        if (biOptional.areNonePresent()) setResult(other);
        return this;
    }

    public <X extends Throwable> BiOptionalMapper<T, U, R> onNonePresentThrow(Supplier<? extends X> throwableProvider) throws X {
        biOptional.ifNonePresentThrow(throwableProvider);
        return this;
    }

    public R result() {
        if (result == null) throw new IllegalStateException("Result absent");
        return result;
    }

    public Optional<R> optionalResult() {
        return Optional.ofNullable(result);
    }

    private void setResult(R result) {
        if (result == null) throw new IllegalArgumentException("Null obtained from a mapper");
        if (this.result != null) throw new IllegalStateException("Result already present: " + this.result);
        this.result = result;
    }
}