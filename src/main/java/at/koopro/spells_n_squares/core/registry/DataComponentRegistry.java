package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import net.neoforged.bus.api.IEventBus;

/**
 * Centralized registry for all data component registrations.
 * Groups registrations by feature category for better organization.
 */
public final class DataComponentRegistry {
    private DataComponentRegistry() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Registers all data component deferred registers with the mod event bus.
     * Groups registrations by feature category for clarity.
     * 
     * @param modEventBus The mod event bus to register with
     */
    public static void registerAll(IEventBus modEventBus) {
        // Core data components
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        
        // Wand
        WandData.DATA_COMPONENTS.register(modEventBus);
        
        // Storage (Pocket Dimensions only)
        PocketDimensionData.DATA_COMPONENTS.register(modEventBus);
        
        // Player Model
        at.koopro.spells_n_squares.core.data.PlayerModelDataComponent.DATA_COMPONENTS.register(modEventBus);
        
        // Artifacts (Philosopher's Stone and Immortality)
        at.koopro.spells_n_squares.features.artifact.PhilosophersStoneData.DATA_COMPONENTS.register(modEventBus);
        at.koopro.spells_n_squares.features.artifact.ImmortalityData.DATA_COMPONENTS.register(modEventBus);
    }
}















