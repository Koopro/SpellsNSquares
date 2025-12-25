package at.koopro.spells_n_squares.core.api;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

/**
 * Interface for mod features that can self-register and manage their own lifecycle.
 * Features implementing this interface can be registered with FeatureRegistry for automatic initialization.
 */
public interface IFeature {
    /**
     * Initializes the feature. Called during mod initialization.
     * @param modEventBus The mod event bus
     * @param modContainer The mod container
     */
    default void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Default implementation does nothing
    }
    
    /**
     * Registers deferred registries for this feature.
     * Called during registry registration phase.
     * @param modEventBus The mod event bus
     */
    default void registerRegistries(IEventBus modEventBus) {
        // Default implementation does nothing
    }
    
    /**
     * Client-side initialization for this feature.
     * Called only on the client side during client initialization.
     */
    default void clientInit() {
        // Default implementation does nothing
    }
    
    /**
     * Gets the feature name for logging and identification.
     * @return The feature name
     */
    default String getFeatureName() {
        return getClass().getSimpleName();
    }
}

















