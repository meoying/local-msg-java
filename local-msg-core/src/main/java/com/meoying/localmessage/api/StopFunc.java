package com.meoying.localmessage.api;

import java.util.concurrent.atomic.AtomicBoolean;

public class StopFunc {

    private final AtomicBoolean flag = new AtomicBoolean(false);

    public boolean isDone() {
        return flag.get();
    }

    public void done() {
        if (isDone()) {
            return;
        }
        flag.compareAndSet(false, true);
    }
}
