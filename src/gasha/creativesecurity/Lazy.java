package gasha.creativesecurity;

import java.util.function.Supplier;

class Lazy<T>
implements Supplier<T> {
    private final Supplier<T> supplier;
    private boolean processed;
    private T value;

    Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.processed) {
            return this.value;
        }
        T val = this.supplier.get();
        this.value = val;
        this.processed = true;
        return val;
    }
}

