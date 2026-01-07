package at.koopro.spells_n_squares;

import at.koopro.spells_n_squares.core.base.init.ModInitialization;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * Main mod class for spells_n_squares.
 * Delegates initialization to modular initialization classes.
 */
@Mod(SpellsNSquares.MODID)
public class SpellsNSquares {
    public static final String MODID = "spells_n_squares";
    
    public SpellsNSquares(IEventBus modEventBus, ModContainer modContainer) {
        // Register all deferred registries (including addon discovery)
        ModInitialization.registerRegistries(modEventBus, modContainer);
        
        // Register event handlers and network
        ModInitialization.registerEventHandlers(modEventBus, modContainer);
    }
}
