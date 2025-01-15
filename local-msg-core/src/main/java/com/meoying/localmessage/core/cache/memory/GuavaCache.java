package com.meoying.localmessage.core.cache.memory;

import com.google.common.cache.CacheBuilder;
import com.meoying.localmessage.core.cache.Cache;

import java.util.concurrent.TimeUnit;


public class GuavaCache<K, V> implements Cache<K, V> {

    com.google.common.cache.Cache<Object, Object> cache;
    int initialCapacity = 10;
    int maximumSize = 100;
    long expireAfterWrite = 60;
    TimeUnit expireAfterWriteTimeUnit = TimeUnit.SECONDS;

    public GuavaCache() {
        cache = CacheBuilder.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, expireAfterWriteTimeUnit)
                .build();
    }

    @Override
    public V get(K k) {
        return (V) cache.getIfPresent(k);
    }

    @Override
    public void set(K k, V v) {
        cache.put(k, v);
    }

    public static class GuavaCacheBuilder<K, V> {
        private GuavaCache<K, V> guavaCache;
        private CacheBuilder<Object, Object> newBuilder;

        public GuavaCacheBuilder() {
            guavaCache = new GuavaCache<K, V>();
            newBuilder = CacheBuilder.newBuilder();
        }

        public GuavaCacheBuilder<K, V> initialCapacity(int initialCapacity) {
            newBuilder.initialCapacity(initialCapacity);
            guavaCache.initialCapacity = initialCapacity;
            return this;
        }

        public GuavaCacheBuilder<K, V> maximumSize(int maximumSize) {
            newBuilder.maximumSize(maximumSize);
            guavaCache.maximumSize = maximumSize;
            return this;
        }

        public GuavaCacheBuilder<K, V> expireAfterWrite(Long expireAfterWrite, TimeUnit timeUnit) {
            newBuilder.expireAfterWrite(expireAfterWrite, timeUnit);
            guavaCache.expireAfterWrite = expireAfterWrite;
            guavaCache.expireAfterWriteTimeUnit = timeUnit;
            return this;
        }

        public GuavaCache<K, V> build() {
            guavaCache.cache = newBuilder.build();
            return guavaCache;
        }
    }


}
