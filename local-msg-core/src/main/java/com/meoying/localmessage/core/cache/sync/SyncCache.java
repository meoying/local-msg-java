package com.meoying.localmessage.core.cache.sync;


import com.meoying.localmessage.core.cache.Cache;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SyncCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> cache;
    private final ReentrantReadWriteLock readWriteLock;

    public SyncCache(Cache<K, V> cache) {
        this.cache = cache;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public V get(K k) {
        try {
            readWriteLock.readLock().lock();
            return cache.get(k);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void set(K k, V v) {
        try {
            readWriteLock.writeLock().lock();
            cache.set(k, v);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
