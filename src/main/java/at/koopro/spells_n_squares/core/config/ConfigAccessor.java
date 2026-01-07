package at.koopro.spells_n_squares.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

/**
 * Utility class for safe config value access.
 * Provides a generic way to access config values with consistent error handling.
 * 
 * <p>This reduces boilerplate in config getter methods by centralizing the
 * try-catch pattern for config loading safety.
 */
public final class ConfigAccessor {
    private ConfigAccessor() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Safely gets a boolean config value.
     * Returns the default value if config is not yet loaded.
     * 
     * @param configValue The config value to access
     * @param defaultValue The default value to return if config is not loaded
     * @return The config value, or defaultValue if config is not loaded
     */
    public static boolean getBoolean(ModConfigSpec.BooleanValue configValue, boolean defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException e) {
            // Config not loaded yet - return default as safe fallback
            return defaultValue;
        }
    }
    
    /**
     * Safely gets a boolean config value.
     * Returns false if config is not yet loaded.
     * 
     * @param configValue The config value to access
     * @return The config value, or false if config is not loaded
     */
    public static boolean getBoolean(ModConfigSpec.BooleanValue configValue) {
        return getBoolean(configValue, false);
    }
    
    /**
     * Safely gets an integer config value.
     * Returns the default value if config is not yet loaded.
     * 
     * @param configValue The config value to access
     * @param defaultValue The default value to return if config is not loaded
     * @return The config value, or defaultValue if config is not loaded
     */
    public static int getInt(ModConfigSpec.IntValue configValue, int defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException e) {
            // Config not loaded yet - return default as safe fallback
            return defaultValue;
        }
    }
    
    /**
     * Safely gets an integer config value.
     * Returns 0 if config is not yet loaded.
     * 
     * @param configValue The config value to access
     * @return The config value, or 0 if config is not loaded
     */
    public static int getInt(ModConfigSpec.IntValue configValue) {
        return getInt(configValue, 0);
    }
    
    /**
     * Safely gets a double config value.
     * Returns the default value if config is not yet loaded.
     * 
     * @param configValue The config value to access
     * @param defaultValue The default value to return if config is not loaded
     * @return The config value, or defaultValue if config is not loaded
     */
    public static double getDouble(ModConfigSpec.DoubleValue configValue, double defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException e) {
            // Config not loaded yet - return default as safe fallback
            return defaultValue;
        }
    }
    
    /**
     * Safely gets a double config value.
     * Returns 0.0 if config is not yet loaded.
     * 
     * @param configValue The config value to access
     * @return The config value, or 0.0 if config is not loaded
     */
    public static double getDouble(ModConfigSpec.DoubleValue configValue) {
        return getDouble(configValue, 0.0);
    }
    
    /**
     * Safely gets an enum config value.
     * Returns the default value if config is not yet loaded.
     * 
     * @param <T> The enum type
     * @param configValue The config value to access
     * @param defaultValue The default value to return if config is not loaded
     * @return The config value, or defaultValue if config is not loaded
     */
    public static <T extends Enum<T>> T getEnum(ModConfigSpec.EnumValue<T> configValue, T defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException e) {
            // Config not loaded yet - return default as safe fallback
            return defaultValue;
        }
    }
    
    /**
     * Safely gets a config value using a supplier.
     * Returns the default value if config is not yet loaded.
     * 
     * @param <T> The value type
     * @param configSupplier The supplier that accesses the config value
     * @param defaultValue The default value to return if config is not loaded
     * @return The config value, or defaultValue if config is not loaded
     */
    public static <T> T get(Supplier<T> configSupplier, T defaultValue) {
        try {
            return configSupplier.get();
        } catch (IllegalStateException e) {
            // Config not loaded yet - return default as safe fallback
            return defaultValue;
        }
    }
}





