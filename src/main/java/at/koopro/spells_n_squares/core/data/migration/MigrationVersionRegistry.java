package at.koopro.spells_n_squares.core.data.migration;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for tracking data migration versions across different data systems.
 * Allows different data systems to have independent version numbers.
 */
public final class MigrationVersionRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, Integer> systemVersions = new HashMap<>();
    
    private MigrationVersionRegistry() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Registers the current version for a data system.
     * 
     * @param systemName The name of the data system (e.g., "currency", "combat_stats")
     * @param currentVersion The current version number for this system
     */
    public static void registerSystemVersion(String systemName, int currentVersion) {
        if (systemName == null || systemName.isEmpty()) {
            LOGGER.warn("Attempted to register version for null or empty system name");
            return;
        }
        
        if (currentVersion < 0) {
            LOGGER.warn("Attempted to register negative version {} for system {}", currentVersion, systemName);
            return;
        }
        
        systemVersions.put(systemName, currentVersion);
        LOGGER.debug("Registered version {} for data system {}", currentVersion, systemName);
    }
    
    /**
     * Gets the current version for a data system.
     * 
     * @param systemName The name of the data system
     * @return The current version, or 0 if not registered
     */
    public static int getSystemVersion(String systemName) {
        return systemVersions.getOrDefault(systemName, 0);
    }
    
    /**
     * Checks if a system is registered.
     * 
     * @param systemName The name of the data system
     * @return true if registered, false otherwise
     */
    public static boolean isSystemRegistered(String systemName) {
        return systemVersions.containsKey(systemName);
    }
    
    /**
     * Gets all registered system names.
     * 
     * @return A set of all registered system names
     */
    public static Set<String> getRegisteredSystems() {
        return Set.copyOf(systemVersions.keySet());
    }
    
    /**
     * Clears all registered system versions.
     * Mainly for testing purposes.
     */
    public static void clear() {
        systemVersions.clear();
    }
}

