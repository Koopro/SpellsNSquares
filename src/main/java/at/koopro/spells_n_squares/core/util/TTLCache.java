package at.koopro.spells_n_squares.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A simple TTL-based cache that automatically expires entries after a specified time.
 * Useful for caching parsed data, computed values, or any data that should be refreshed periodically.
 * 
 * <p><b>Thread Safety:</b> This class is not thread-safe. If used from multiple threads,
 * external synchronization is required.
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public class TTLCache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new HashMap<>();
    private final long defaultTTL;
    private long lastCleanup = System.currentTimeMillis();
    private static final long CLEANUP_INTERVAL = 60 * 1000; // Clean up every minute
    
    /**
     * Creates a new TTL cache with a default TTL.
     * @param defaultTTL The default time to live in milliseconds
     */
    public TTLCache(long defaultTTL) {
        this.defaultTTL = defaultTTL;
    }
    
    /**
     * Gets a value from the cache, or computes it if not present or expired.
     * @param key The cache key
     * @param computeFunction The function to compute the value if not cached
     * @return The cached or computed value
     */
    public V getOrCompute(K key, Function<K, V> computeFunction) {
        return getOrCompute(key, computeFunction, defaultTTL);
    }
    
    /**
     * Gets a value from the cache, or computes it if not present or expired.
     * @param key The cache key
     * @param computeFunction The function to compute the value if not cached
     * @param ttl The time to live for this entry in milliseconds
     * @return The cached or computed value
     */
    public V getOrCompute(K key, Function<K, V> computeFunction, long ttl) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        }
        
        // Compute new value
        V value = computeFunction.apply(key);
        cache.put(key, new CacheEntry<>(value, ttl));
        
        // Periodic cleanup
        cleanupIfNeeded();
        
        return value;
    }
    
    /**
     * Gets a value from the cache, or returns null if not present or expired.
     * @param key The cache key
     * @return The cached value, or null if not present or expired
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            if (entry != null) {
                cache.remove(key); // Remove expired entry
            }
            return null;
        }
        return entry.getValue();
    }
    
    /**
     * Puts a value into the cache with the default TTL.
     * @param key The cache key
     * @param value The value to cache
     */
    public void put(K key, V value) {
        put(key, value, defaultTTL);
    }
    
    /**
     * Puts a value into the cache with a specific TTL.
     * @param key The cache key
     * @param value The value to cache
     * @param ttl The time to live in milliseconds
     */
    public void put(K key, V value, long ttl) {
        cache.put(key, new CacheEntry<>(value, ttl));
        cleanupIfNeeded();
    }
    
    /**
     * Removes a value from the cache.
     * @param key The cache key
     * @return The removed value, or null if not present
     */
    public V remove(K key) {
        CacheEntry<V> entry = cache.remove(key);
        return entry != null ? entry.getValue() : null;
    }
    
    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Gets the number of entries in the cache (including expired ones).
     * @return The cache size
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Removes all expired entries from the cache.
     */
    public void cleanup() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        lastCleanup = System.currentTimeMillis();
    }
    
    /**
     * Performs cleanup if enough time has passed since the last cleanup.
     */
    private void cleanupIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastCleanup > CLEANUP_INTERVAL) {
            cleanup();
        }
    }
}


