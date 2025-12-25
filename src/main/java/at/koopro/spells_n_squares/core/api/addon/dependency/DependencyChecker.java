package at.koopro.spells_n_squares.core.api.addon.dependency;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Validates addon dependencies and checks version compatibility.
 */
public final class DependencyChecker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String API_MOD_ID = "spells_n_squares";
    
    /**
     * Validates all dependencies for an addon.
     * @param dependencies List of dependency strings (format: "modid" or "modid@version")
     * @param addonId The addon ID for error reporting
     * @return List of validation errors, empty if all dependencies are satisfied
     */
    public static List<String> validateDependencies(List<String> dependencies, String addonId) {
        List<String> errors = new ArrayList<>();
        
        for (String dep : dependencies) {
            AddonDependency dependency = parseDependency(dep);
            if (dependency.isRequired()) {
                if (!isModLoaded(dependency.getModId())) {
                    errors.add("Required dependency '" + dependency.getModId() + "' is not loaded");
                    continue;
                }
                
                if (dependency.hasVersionRequirement()) {
                    if (!checkVersion(dependency.getModId(), dependency.getVersionRange())) {
                        errors.add("Dependency '" + dependency.getModId() + "' version requirement '" + 
                                 dependency.getVersionRange() + "' not satisfied");
                    }
                }
            } else {
                // Optional dependency - just log if not found
                if (!isModLoaded(dependency.getModId())) {
                    LOGGER.debug("Optional dependency '{}' for addon '{}' is not loaded", dependency.getModId(), addonId);
                }
            }
        }
        
        return errors;
    }
    
    /**
     * Checks if the minimum API version requirement is satisfied.
     * @param minApiVersion The minimum required API version
     * @return true if satisfied, false otherwise
     */
    public static boolean checkApiVersion(String minApiVersion) {
        if (minApiVersion == null || minApiVersion.isEmpty()) {
            return true; // No requirement
        }
        
        ModContainer container = ModList.get().getModContainerById(API_MOD_ID).orElse(null);
        if (container == null) {
            LOGGER.error("Cannot check API version - {} mod not found!", API_MOD_ID);
            return false;
        }
        
        String currentVersion = container.getModInfo().getVersion().toString();
        return compareVersions(currentVersion, minApiVersion) >= 0;
    }
    
    /**
     * Parses a dependency string into an AddonDependency object.
     * Format: "modid" or "modid@version" or "modid@version (optional)"
     * @param depString The dependency string
     * @return The parsed dependency
     */
    private static AddonDependency parseDependency(String depString) {
        if (depString == null || depString.trim().isEmpty()) {
            throw new IllegalArgumentException("Dependency string cannot be empty");
        }
        
        String trimmed = depString.trim();
        boolean optional = trimmed.endsWith(" (optional)");
        if (optional) {
            trimmed = trimmed.substring(0, trimmed.length() - " (optional)".length()).trim();
        }
        
        int atIndex = trimmed.indexOf('@');
        if (atIndex == -1) {
            return new AddonDependency(trimmed, optional);
        }
        
        String modId = trimmed.substring(0, atIndex).trim();
        String versionRange = trimmed.substring(atIndex + 1).trim();
        return new AddonDependency(modId, versionRange, !optional);
    }
    
    /**
     * Checks if a mod is loaded.
     * @param modId The mod ID to check
     * @return true if the mod is loaded
     */
    private static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
    
    /**
     * Checks if a mod version satisfies the version range requirement.
     * Simple version comparison - supports ">=version", "<=version", "==version", or just "version".
     * @param modId The mod ID
     * @param versionRange The version range requirement
     * @return true if satisfied
     */
    private static boolean checkVersion(String modId, String versionRange) {
        ModContainer container = ModList.get().getModContainerById(modId).orElse(null);
        if (container == null) {
            return false;
        }
        
        String currentVersion = container.getModInfo().getVersion().toString();
        
        // Simple version range parsing
        if (versionRange.startsWith(">=")) {
            String minVersion = versionRange.substring(2).trim();
            return compareVersions(currentVersion, minVersion) >= 0;
        } else if (versionRange.startsWith("<=")) {
            String maxVersion = versionRange.substring(2).trim();
            return compareVersions(currentVersion, maxVersion) <= 0;
        } else if (versionRange.startsWith("==")) {
            String exactVersion = versionRange.substring(2).trim();
            return currentVersion.equals(exactVersion);
        } else if (versionRange.startsWith(">")) {
            String minVersion = versionRange.substring(1).trim();
            return compareVersions(currentVersion, minVersion) > 0;
        } else if (versionRange.startsWith("<")) {
            String maxVersion = versionRange.substring(1).trim();
            return compareVersions(currentVersion, maxVersion) < 0;
        } else {
            // Exact version match
            return currentVersion.equals(versionRange);
        }
    }
    
    /**
     * Compares two version strings.
     * Simple comparison - splits by dots and compares numerically.
     * @param v1 First version
     * @param v2 Second version
     * @return Negative if v1 < v2, zero if equal, positive if v1 > v2
     */
    private static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            int part1 = i < parts1.length ? parseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? parseInt(parts2[i]) : 0;
            
            if (part1 != part2) {
                return Integer.compare(part1, part2);
            }
        }
        
        return 0;
    }
    
    private static int parseInt(String s) {
        try {
            // Remove any non-numeric suffix (e.g., "1.0.0-beta" -> "1")
            int dashIndex = s.indexOf('-');
            if (dashIndex != -1) {
                s = s.substring(0, dashIndex);
            }
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

















