package at.koopro.spells_n_squares.features.quidditch;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for quidditch feature items.
 * Currently empty, ready for QuidditchBallItem when registered.
 */
public class QuidditchRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    // Quidditch items will be registered here when implemented
    // Example:
    // public static final DeferredItem<QuidditchBallItem> QUIDDITCH_BALL = ITEMS.register(...);
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}













