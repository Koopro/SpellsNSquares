package at.koopro.spells_n_squares.core.config;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for configuration presets.
 * Allows saving and loading configuration presets.
 */
public final class ConfigPresetHelper {
    
    private static final Map<String, Map<String, Object>> PRESETS = new ConcurrentHashMap<>();
    
    private ConfigPresetHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Saves current config as a preset.
     * 
     * @param presetName The preset name
     * @param configValues Map of config keys to values
     * @return true if preset was saved
     */
    public static boolean savePreset(String presetName, Map<String, Object> configValues) {
        if (presetName == null || presetName.trim().isEmpty() || configValues == null) {
            return false;
        }
        
        PRESETS.put(presetName.trim().toLowerCase(), new HashMap<>(configValues));
        
        DevLogger.logStateChange(ConfigPresetHelper.class, "savePreset",
            "Saved preset: " + presetName);
        
        return true;
    }
    
    /**
     * Loads a config preset.
     * 
     * @param presetName The preset name
     * @return Map of config keys to values, or null if preset not found
     */
    public static Map<String, Object> loadPreset(String presetName) {
        if (presetName == null || presetName.trim().isEmpty()) {
            return null;
        }
        
        Map<String, Object> preset = PRESETS.get(presetName.trim().toLowerCase());
        return preset != null ? new HashMap<>(preset) : null;
    }
    
    /**
     * Gets all available preset names.
     * 
     * @return Set of preset names
     */
    public static Set<String> getPresetNames() {
        return new HashSet<>(PRESETS.keySet());
    }
    
    /**
     * Deletes a preset.
     * 
     * @param presetName The preset name
     * @return true if preset was deleted
     */
    public static boolean deletePreset(String presetName) {
        if (presetName == null || presetName.trim().isEmpty()) {
            return false;
        }
        
        Map<String, Object> removed = PRESETS.remove(presetName.trim().toLowerCase());
        return removed != null;
    }
    
    /**
     * Creates default presets.
     */
    public static void createDefaultPresets() {
        // Performance preset
        Map<String, Object> performance = new HashMap<>();
        performance.put("performance.optimize", true);
        performance.put("performance.reduceParticles", true);
        savePreset("performance", performance);
        
        // Quality preset
        Map<String, Object> quality = new HashMap<>();
        quality.put("quality.high", true);
        quality.put("quality.maxParticles", true);
        savePreset("quality", quality);
        
        // Balanced preset
        Map<String, Object> balanced = new HashMap<>();
        balanced.put("performance.optimize", false);
        balanced.put("quality.high", false);
        savePreset("balanced", balanced);
    }
}

