package at.koopro.spells_n_squares.core.data;

/**
 * Factory for creating DataAccessLayer instances.
 * Provides a centralized way to get the appropriate data access layer.
 * 
 * <p>Currently returns the persistent data implementation, but can be extended
 * to support different backends based on configuration or feature flags.
 */
public final class DataAccessLayerFactory {
    private static DataAccessLayer defaultLayer = PersistentDataAccessLayer.getInstance();
    
    private DataAccessLayerFactory() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the default data access layer.
     * Currently returns the persistent data implementation.
     * 
     * @return The default data access layer
     */
    public static DataAccessLayer getDefault() {
        return defaultLayer;
    }
    
    /**
     * Sets the default data access layer.
     * Mainly for testing or future feature flags.
     * 
     * @param layer The data access layer to use as default
     */
    public static void setDefault(DataAccessLayer layer) {
        if (layer != null) {
            defaultLayer = layer;
        }
    }
    
    /**
     * Resets to the default persistent data access layer.
     */
    public static void resetToDefault() {
        defaultLayer = PersistentDataAccessLayer.getInstance();
    }
}

