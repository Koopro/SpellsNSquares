package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.api.IFeature;
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
        if (feature != null) {
            features.add(feature);
        }
    }
    
    /**
     * Initializes all registered features.
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     */
    public static void initializeAll(IEventBus modEventBus, ModContainer modContainer) {
        for (IFeature feature : features) {
            try {
                LOGGER.debug("Initializing feature: {}", feature.getFeatureName());
                feature.initialize(modEventBus, modContainer);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize feature '{}': {}", feature.getFeatureName(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Registers all registries for all registered features.
     * @param modEventBus The mod event bus
     */
    public static void registerAllRegistries(IEventBus modEventBus) {
        for (IFeature feature : features) {
            try {
                feature.registerRegistries(modEventBus);
            } catch (Exception e) {
                LOGGER.error("Failed to register registries for feature '{}': {}", feature.getFeatureName(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Initializes all features on the client side.
     */
    public static void initializeAllClient() {
        for (IFeature feature : features) {
            try {
                feature.clientInit();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize client side for feature '{}': {}", feature.getFeatureName(), e.getMessage(), e);
            }
        }
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














