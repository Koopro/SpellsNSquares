package at.koopro.spells_n_squares.features.wand.visual;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Client-side handler for wand visual effects when Lumos is active.
 * Reserved for future particle/glow effects implementation.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class WandGlowHandler {
    // Reserved for future wand glow/particle effects
}

