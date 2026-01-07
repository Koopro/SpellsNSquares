package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.api.IFeature;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Registry for mod features.
 * Enables feature registration and conditional loading for better modularity.
 * <p>
 * Features are initialized in the order they are registered.
 */
public final class FeatureRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Using LinkedHashSet - insertion order matters for initialization order
    private static final Set<IFeature> features = new LinkedHashSet<>();
    
    /**
     * Registers a feature with the registry.
     * @param feature The feature to register
     */
    public static void register(IFeature feature) {
        DevLogger.logMethodEntry(FeatureRegistry.class, "register", 
            "feature=" + (feature != null ? feature.getFeatureName() : "null"));
        if (feature != null) {
            features.add(feature);
            DevLogger.logStateChange(FeatureRegistry.class, "register", 
                "Feature registered: " + feature.getFeatureName());
        } else {
            DevLogger.logWarn(FeatureRegistry.class, "register", "Attempted to register null feature");
        }
        DevLogger.logMethodExit(FeatureRegistry.class, "register");
    }
    
    /**
     * Initializes all registered features.
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     */
    public static void initializeAll(IEventBus modEventBus, ModContainer modContainer) {
        DevLogger.logMethodEntry(FeatureRegistry.class, "initializeAll", 
            "featureCount=" + features.size());
        for (IFeature feature : features) {
            try {
                LOGGER.debug("Initializing feature: {}", feature.getFeatureName());
                DevLogger.logDebug(FeatureRegistry.class, "initializeAll", 
                    "Initializing feature: " + feature.getFeatureName());
                feature.initialize(modEventBus, modContainer);
                DevLogger.logStateChange(FeatureRegistry.class, "initializeAll", 
                    "Feature initialized: " + feature.getFeatureName());
            } catch (Exception e) {
                LOGGER.error("Failed to initialize feature '{}': {}", feature.getFeatureName(), e.getMessage(), e);
                DevLogger.logError(FeatureRegistry.class, "initializeAll", 
                    "Failed to initialize feature: " + feature.getFeatureName(), e);
            }
        }
        DevLogger.logMethodExit(FeatureRegistry.class, "initializeAll");
    }
    
    /**
     * Registers all registries for all registered features.
     * @param modEventBus The mod event bus
     */
    public static void registerAllRegistries(IEventBus modEventBus) {
        DevLogger.logMethodEntry(FeatureRegistry.class, "registerAllRegistries", 
            "featureCount=" + features.size());
        for (IFeature feature : features) {
            try {
                DevLogger.logDebug(FeatureRegistry.class, "registerAllRegistries", 
                    "Registering registries for: " + feature.getFeatureName());
                feature.registerRegistries(modEventBus);
                DevLogger.logStateChange(FeatureRegistry.class, "registerAllRegistries", 
                    "Registries registered for: " + feature.getFeatureName());
            } catch (Exception e) {
                LOGGER.error("Failed to register registries for feature '{}': {}", feature.getFeatureName(), e.getMessage(), e);
                DevLogger.logError(FeatureRegistry.class, "registerAllRegistries", 
                    "Failed to register registries for feature: " + feature.getFeatureName(), e);
            }
        }
        DevLogger.logMethodExit(FeatureRegistry.class, "registerAllRegistries");
    }
    
    /**
     * Initializes all features on the client side.
     */
    public static void initializeAllClient() {
        DevLogger.logMethodEntry(FeatureRegistry.class, "initializeAllClient", 
            "featureCount=" + features.size());
        for (IFeature feature : features) {
            try {
                DevLogger.logDebug(FeatureRegistry.class, "initializeAllClient", 
                    "Initializing client for: " + feature.getFeatureName());
                feature.clientInit();
                DevLogger.logStateChange(FeatureRegistry.class, "initializeAllClient", 
                    "Client initialized for: " + feature.getFeatureName());
            } catch (Exception e) {
                LOGGER.error("Failed to initialize client side for feature '{}': {}", feature.getFeatureName(), e.getMessage(), e);
                DevLogger.logError(FeatureRegistry.class, "initializeAllClient", 
                    "Failed to initialize client for feature: " + feature.getFeatureName(), e);
            }
        }
        DevLogger.logMethodExit(FeatureRegistry.class, "initializeAllClient");
    }
    
    /**
     * Gets all registered features.
     * @return A copy of the features list
     */
    public static List<IFeature> getFeatures() {
        return new ArrayList<>(features);
    }
    
    /**
     * Clears all registered features (useful for testing).
     */
    public static void clear() {
        features.clear();
    }
}








