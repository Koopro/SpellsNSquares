package at.koopro.spells_n_squares.core.util.network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Generic caching system for expensive operations.
 * Provides configurable caching with TTL and invalidation strategies.
 */
public final class CacheHelper {
    
    private static final Map<String, CacheEntry<?>> CACHE = new ConcurrentHashMap<>();
    
    private CacheHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a cache entry with expiration time.
     */
    private static class CacheEntry<T> {
        private final T value;
        private final long expirationTime;
        
        public CacheEntry(T value, long ttlMillis) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }
        
        public T getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }
    }
    
    /**
     * Gets a value from cache, or computes it if not present or expired.
     * 
     * @param key The cache key
     * @param ttlMillis Time to live in milliseconds
     * @param computeFunction Function to compute the value if not cached
     * @return The cached or computed value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrCompute(String key, long ttlMillis, Function<String, T> computeFunction) {
        if (key == null || computeFunction == null) {
            return null;
        }
        
        CacheEntry<?> entry = CACHE.get(key);
        if (entry != null && !entry.isExpired()) {
            return (T) entry.getValue();
        }
        
        // Compute new value
        T value = computeFunction.apply(key);
        if (value != null) {
            CACHE.put(key, new CacheEntry<>(value, ttlMillis));
        }
        
        return value;
    }
    
    /**
     * Gets a value from cache, or returns null if not present or expired.
     * 
     * @param key The cache key
     * @return The cached value, or null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        if (key == null) {
            return null;
        }
        
        CacheEntry<?> entry = CACHE.get(key);
        if (entry != null && !entry.isExpired()) {
            return (T) entry.getValue();
        }
        
        // Remove expired entry
        CACHE.remove(key);
        return null;
    }
    
    /**
     * Puts a value in the cache.
     * 
     * @param key The cache key
     * @param value The value to cache
     * @param ttlMillis Time to live in milliseconds
     */
    public static <T> void put(String key, T value, long ttlMillis) {
        if (key == null) {
            return;
        }
        
        if (value != null) {
            CACHE.put(key, new CacheEntry<>(value, ttlMillis));
        } else {
            CACHE.remove(key);
        }
    }
    
    /**
     * Invalidates a cache entry.
     * 
     * @param key The cache key
     */
    public static void invalidate(String key) {
        if (key != null) {
            CACHE.remove(key);
        }
    }
    
    /**
     * Invalidates all cache entries matching a prefix.
     * 
     * @param prefix The key prefix
     */
    public static void invalidatePrefix(String prefix) {
        if (prefix == null) {
            return;
        }
        
        CACHE.keySet().removeIf(key -> key.startsWith(prefix));
    }
    
    /**
     * Clears all cache entries.
     */
    public static void clear() {
        CACHE.clear();
    }
    
    /**
     * Cleans up expired cache entries.
     * Should be called periodically to prevent memory leaks.
     */
    public static void cleanupExpired() {
        CACHE.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Gets the current cache size.
     * 
     * @return Number of entries in cache
     */
    public static int size() {
        return CACHE.size();
    }
}

