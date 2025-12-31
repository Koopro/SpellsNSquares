package at.koopro.spells_n_squares.core.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

/**
 * Utility class for common registry lookup patterns with validation.
 * Provides standardized validation methods for registry access.
 * 
 * Note: Full registry access patterns vary by registry type and Minecraft version.
 * This utility focuses on validation and error handling.
 */
public final class RegistryHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private RegistryHelper() {
        // Utility class - prevent instantiation
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
