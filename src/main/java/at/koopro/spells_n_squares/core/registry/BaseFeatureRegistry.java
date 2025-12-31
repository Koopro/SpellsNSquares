package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;

/**
 * Base class for feature registries.
 * Provides common patterns for item/block registration and property creation.
 * 
 * <p>Subclasses should:
 * <ul>
 *   <li>Declare their DeferredRegisters as static final fields</li>
 *   <li>Register items/blocks in static initializers</li>
 *   <li>Implement register() to register all DeferredRegisters and data components</li>
 * </ul>
 * 
 * <p>This class provides common property creation methods that delegate to RegistryHelper
 * for consistency across all feature registries.
 */
public abstract class BaseFeatureRegistry {
    /**
     * The mod ID, shared across all feature registries.
     */
    protected static final String MODID = SpellsNSquares.MODID;
    
    /**
     * Creates Item.Properties with the appropriate ResourceKey for the given identifier.
     * Delegates to RegistryHelper for consistency.
     * 
     * @param id The identifier for the item
     * @return Item.Properties configured with the resource key
     */
    public static Item.Properties createItemProperties(Identifier id) {
        return RegistryHelper.createItemProperties(id);
    }
    
    /**
     * Creates BlockBehaviour.Properties with the appropriate ResourceKey for the given identifier.
     * Delegates to RegistryHelper for consistency.
     * 
     * @param id The identifier for the block
     * @return BlockBehaviour.Properties configured with the resource key
     */
    public static BlockBehaviour.Properties createBlockProperties(Identifier id) {
        return RegistryHelper.createBlockProperties(id);
    }
    
    /**
     * Registers all DeferredRegisters and data components for this feature.
     * Subclasses must implement this to register their specific items, blocks, and data components.
     * 
     * @param modEventBus The mod event bus to register with
     */
    public abstract void register(IEventBus modEventBus);
}

