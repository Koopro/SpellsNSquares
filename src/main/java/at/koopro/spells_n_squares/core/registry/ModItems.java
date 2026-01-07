package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for generic/shared mod items that don't belong to a specific feature.
 * Feature-specific items are registered in their respective feature registries.
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);

    // Generic items would be registered here
    // Currently empty as all items belong to specific features
}
