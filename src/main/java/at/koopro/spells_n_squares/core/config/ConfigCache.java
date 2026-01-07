package at.koopro.spells_n_squares.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Caching system for config values to reduce access overhead.
 * Automatically invalidates cache when config is reloaded.
 * 
 * <p>This is particularly useful for frequently accessed config values
 * like logging flags and effect settings that are checked many times per tick.
 * 
 * <p>Note: Config reload event handling is registered manually in ModInitialization
 * since NeoForge config events work differently.
 */
public final class ConfigCache {
    private static final Map<String, CachedValue<?>> cache = new ConcurrentHashMap<>();
    private static volatile boolean cacheEnabled = true;
    
    private ConfigCache() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Internal class for cached values.
     */
    private static class CachedValue<T> {
        private T value;
        private final Supplier<T> supplier;
        private volatile boolean valid;
        
        CachedValue(Supplier<T> supplier) {
            this.supplier = supplier;
            this.valid = false;
        }
        
        T get() {
            if (!valid || !cacheEnabled) {
                value = supplier.get();
                valid = true;
            }
            return value;
        }
        
        void invalidate() {
            valid = false;
        }
    }
    
    /**
     * Gets a cached config value, computing it if not cached or invalidated.
     * 
     * @param <T> The value type
     * @param key The cache key (should be unique per config value)
     * @param supplier The supplier that computes the value
     * @return The cached or computed value
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Supplier<T> supplier) {
        if (!cacheEnabled) {
            return supplier.get();
        }
        
        CachedValue<T> cached = (CachedValue<T>) cache.computeIfAbsent(
            key, 
            k -> new CachedValue<>(supplier)
        );
        return cached.get();
    }
    
    /**
     * Invalidates all cached values.
     * Called automatically when config is reloaded.
     */
    public static void invalidateAll() {
        for (CachedValue<?> value : cache.values()) {
            value.invalidate();
        }
    }
    
    /**
     * Clears all cached values.
     * Useful for memory management or testing.
     */
    public static void clear() {
        cache.clear();
    }
    
    /**
     * Enables or disables caching.
     * When disabled, all get() calls will compute values directly.
     * 
     * @param enabled Whether caching is enabled
     */
    public static void setEnabled(boolean enabled) {
        cacheEnabled = enabled;
        if (!enabled) {
            clear();
        }
    }
    
    /**
     * Checks if caching is enabled.
     * 
     * @return true if caching is enabled
     */
    public static boolean isEnabled() {
        return cacheEnabled;
    }
    
    /**
     * Invalidates cache when config is reloaded.
     * This should be called from ModInitialization when config reload is detected.
     */
    public static void onConfigReload() {
        invalidateAll();
    }
}

