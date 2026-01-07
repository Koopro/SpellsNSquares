package at.koopro.spells_n_squares.core.util.registry;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Utility class for common registry lookup patterns with validation and caching.
 * Provides standardized validation methods for registry access.
 * Includes caching for expensive operations like Identifier parsing.
 * 
 * Note: Full registry access patterns vary by registry type and Minecraft version.
 * This utility focuses on validation, error handling, and performance optimization.
 */
public final class RegistryHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Cache for parsed identifiers to avoid repeated parsing
    private static final Map<String, Identifier> identifierCache = new ConcurrentHashMap<>();
    
    private RegistryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Parses an identifier string with caching.
     * Cached identifiers are reused to avoid repeated parsing overhead.
     * 
     * @param idString The identifier string (e.g., "minecraft:diamond")
     * @return The parsed Identifier, or null if parsing fails
     */
    public static Identifier parseIdentifierCached(String idString) {
        if (idString == null || idString.isEmpty()) {
            return null;
        }
        
        // Check cache first
        Identifier cached = identifierCache.get(idString);
        if (cached != null) {
            return cached;
        }
        
        // Parse and cache
        try {
            Identifier id = Identifier.parse(idString);
            identifierCache.put(idString, id);
            return id;
        } catch (Exception e) {
            LOGGER.debug("Failed to parse identifier '{}': {}", idString, e.getMessage());
            return null;
        }
    }
    
    /**
     * Clears the identifier cache.
     * Useful for memory management or when identifiers might change.
     */
    public static void clearIdentifierCache() {
        identifierCache.clear();
    }
    
    /**
     * Validates that a registry and key are not null.
     * 
     * @param registry The registry to validate
     * @param key The key to validate
     * @param entryName The name of the entry (for logging)
     * @return true if valid, false otherwise
     */
    public static <T> boolean validateRegistryAccess(Registry<T> registry, ResourceKey<T> key, String entryName) {
        if (registry == null) {
            LOGGER.warn("Attempted to access {} from null registry", entryName);
            return false;
        }
        
        if (key == null) {
            LOGGER.warn("Attempted to access {} with null key", entryName);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates that a registry is not null.
     * 
     * @param registry The registry to validate
     * @param entryName The name of the entry (for logging)
     * @return true if valid, false otherwise
     */
    public static <T> boolean validateRegistry(Registry<T> registry, String entryName) {
        if (registry == null) {
            LOGGER.warn("Attempted to access {} from null registry", entryName);
            return false;
        }
        return true;
    }
    
    /**
     * Validates that a key is not null.
     * 
     * @param key The key to validate
     * @param entryName The name of the entry (for logging)
     * @return true if valid, false otherwise
     */
    public static <T> boolean validateKey(ResourceKey<T> key, String entryName) {
        if (key == null) {
            LOGGER.warn("Attempted to access {} with null key", entryName);
            return false;
        }
        return true;
    }
}


