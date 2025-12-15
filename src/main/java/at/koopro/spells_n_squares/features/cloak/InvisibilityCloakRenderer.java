package at.koopro.spells_n_squares.features.cloak;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Client-side handler for invisibility cloak rendering.
 * Currently empty - armor and items rendering is left as-is.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class InvisibilityCloakRenderer {
    // Empty - no special rendering needed
}
