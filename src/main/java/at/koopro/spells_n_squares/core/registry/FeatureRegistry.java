package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.api.IFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Registry for mod features.
 * Enables feature registration and conditional loading for better modularity.
 */
public final class FeatureRegistry {
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
            feature.initialize(modEventBus, modContainer);
        }
    }
    
    /**
     * Registers all registries for all registered features.
     * @param modEventBus The mod event bus
     */
    public static void registerAllRegistries(IEventBus modEventBus) {
        for (IFeature feature : features) {
            feature.registerRegistries(modEventBus);
        }
    }
    
    /**
     * Initializes all features on the client side.
     */
    public static void initializeAllClient() {
        for (IFeature feature : features) {
            feature.clientInit();
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

