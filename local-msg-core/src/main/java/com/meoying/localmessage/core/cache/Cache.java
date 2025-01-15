package com.meoying.localmessage.core.cache;

import com.google.common.base.Supplier;

public interface Cache<K, V> {

    V get(K k);

    void set(K k, V v);

    default V get(K k, Supplier<V> supplier) {
        V v = get(k);
        if (v == null) {
            v = supplier.get();
            set(k, v);
        }
        return v;
    }
}
