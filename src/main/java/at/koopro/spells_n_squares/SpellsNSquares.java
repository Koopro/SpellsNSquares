package at.koopro.spells_n_squares;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import at.koopro.spells_n_squares.init.ModInitialization;

/**
 * Main mod class for spells_n_squares.
 * Delegates initialization to modular initialization classes.
 */
@Mod(SpellsNSquares.MODID)
public class SpellsNSquares {
    public static final String MODID = "spells_n_squares";
    
    public SpellsNSquares(IEventBus modEventBus, ModContainer modContainer) {
        // Register all deferred registries
        ModInitialization.registerRegistries(modEventBus);
        
        // Register event handlers and network
        ModInitialization.registerEventHandlers(modEventBus, modContainer);
    }
}
