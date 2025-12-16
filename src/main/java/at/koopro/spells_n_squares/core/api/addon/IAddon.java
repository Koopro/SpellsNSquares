package at.koopro.spells_n_squares.core.api.addon;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

/**
 * Base interface for all addons.
 * Addons implement this interface to integrate with Spells_n_Squares.
 */
public interface IAddon {
    /**
     * Initializes the addon. Called during mod initialization.
     * This is where addons should register spells, items, entities, and event handlers.
     * @param context The addon context providing API access
     */
    void initialize(AddonContext context);
    
    /**
     * Registers deferred registries for this addon.
     * Called during registry registration phase.
     * @param modEventBus The mod event bus
     */
    default void registerRegistries(IEventBus modEventBus) {
        // Default implementation does nothing
    }
    
    /**
     * Client-side initialization for this addon.
     * Called only on the client side during client initialization.
     * @param context The addon context providing API access
     */
    default void clientInit(AddonContext context) {
        // Default implementation does nothing
    }
    
    /**
     * Gets the unique identifier for this addon (typically the mod ID).
     * @return The addon ID
     */
    String getAddonId();
    
    /**
     * Gets the display name of this addon.
     * @return The addon name
     */
    String getAddonName();
    
    /**
     * Gets the version of this addon.
     * @return The addon version
     */
    String getAddonVersion();
}

