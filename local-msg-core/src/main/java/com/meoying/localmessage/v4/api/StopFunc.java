package com.meoying.localmessage.v4.api;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class StopFunc {

    private final AtomicBoolean flag = new AtomicBoolean(false);

    private final Supplier<Void> supplier;

    public StopFunc(Supplier<Void> supplier) {
        this.supplier = supplier;
    }

    public StopFunc() {
        this.supplier = () -> null;
    }

    public boolean isDone() {
        return flag.get();
    }

    public void done() {
        if (isDone()) {
            return;
        }
        if (flag.compareAndSet(false, true)) {
            supplier.get();
        }
    }
}
