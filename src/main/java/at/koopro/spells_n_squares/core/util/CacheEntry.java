package at.koopro.spells_n_squares.core.util;

/**
 * Cache entry with timestamp for TTL (Time To Live) support.
 * Used for caching parsed data or computed values with expiration.
 * 
 * @param <T> The type of value being cached
 */
public final class CacheEntry<T> {
    private final T value;
    private final long timestamp;
    private final long ttl; // Time to live in milliseconds
    
    /**
     * Creates a new cache entry.
     * @param value The value to cache
     * @param ttl The time to live in milliseconds
     */
    public CacheEntry(T value, long ttl) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
        this.ttl = ttl;
    }
    
    /**
     * Gets the cached value.
     * @return The cached value
     */
    public T getValue() {
        return value;
    }
    
    /**
     * Gets the timestamp when this entry was created.
     * @return The creation timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Checks if this cache entry has expired.
     * @return true if the entry has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > ttl;
    }
    
    /**
     * Gets the remaining time to live in milliseconds.
     * @return The remaining TTL, or 0 if expired
     */
    public long getRemainingTTL() {
        long remaining = ttl - (System.currentTimeMillis() - timestamp);
        return Math.max(0, remaining);
    }
}


