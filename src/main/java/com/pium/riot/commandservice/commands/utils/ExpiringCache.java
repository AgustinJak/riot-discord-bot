package com.pium.riot.commandservice.commands.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpiringCache<V> {
    private final Map<String, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public ExpiringCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public void put(String key, V value) {
        cleanup();
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(String key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public void remove(String key) {
        cache.remove(key);
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(e -> now - e.getValue().timestamp > ttlMillis);
    }

    private static class CacheEntry<V> {
        final V value;
        final long timestamp;

        CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
