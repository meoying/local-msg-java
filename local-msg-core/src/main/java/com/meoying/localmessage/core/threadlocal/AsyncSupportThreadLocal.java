package com.meoying.localmessage.core.threadlocal;

import java.util.HashMap;
import java.util.Map;

public class AsyncSupportThreadLocal<T> extends ThreadLocal<T> {
    private ThreadLocal<Map<AsyncSupportThreadLocal<Object>, Object>> holder;

    public AsyncSupportThreadLocal() {
        holder = new ThreadLocal<>();
        holder.set(new HashMap<>());
    }

    public Map<AsyncSupportThreadLocal<Object>, Object> to() {
        Map<AsyncSupportThreadLocal<Object>, Object> currMap = holder.get();
        return new HashMap<>(currMap);
    }

    public void from(Map<AsyncSupportThreadLocal<Object>, Object> other) {
        Map<AsyncSupportThreadLocal<Object>, Object> currMap = holder.get();
        if (other == null || other.isEmpty()) {
            holder.set(other);
            return;
        }

        other.forEach((k, v) -> {
            if (currMap.containsKey(k)) {
                currMap.put(k, v);
            }
        });
    }

    @Override
    public T get() {
        return (T) holder.get().get(this);
    }

    @Override
    public void set(T value) {
        holder.get().put((AsyncSupportThreadLocal<Object>) this, value);
    }

}
