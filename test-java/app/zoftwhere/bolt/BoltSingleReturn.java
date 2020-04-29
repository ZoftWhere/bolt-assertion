package app.zoftwhere.bolt;

import java.util.function.Supplier;

/**
 * <p>Bolt single return class.
 * </p>
 * <p>This is a test-only class for building lambda-based code blocks.
 * </p>
 *
 * @param <T> The code block return type.
 * @since 6.0.0
 */
public class BoltSingleReturn<T> {

    private final BoltPlaceHolder<T> boltPlaceHolder = new BoltPlaceHolder<>(null);

    public void block(Supplier<T> code) {
        if (boltPlaceHolder.get() == null) {
            boltPlaceHolder.set(code.get());
        }
    }

    public T end() {
        return boltPlaceHolder.get();
    }

}
