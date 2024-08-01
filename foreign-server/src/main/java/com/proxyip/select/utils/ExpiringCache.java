package com.proxyip.select.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.utils
 * @className: ExpiringCache
 * @author: Yohann
 * @date: 2024/8/1 21:48
 */
public class ExpiringCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final ScheduledThreadPoolExecutor scheduler;

    public ExpiringCache(int poolSize) {
        scheduler = new ScheduledThreadPoolExecutor(poolSize, ThreadFactoryBuilder.create().setNamePrefix("cache-thread-").build());
        scheduler.setRemoveOnCancelPolicy(true);
    }

    public void put(K key, V value, long duration, TimeUnit unit) {
        cache.put(key, value);
        scheduler.schedule(() -> cache.remove(key), duration, unit);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
