package at.koopro.spells_n_squares.features.artifact.block.client;

import at.koopro.spells_n_squares.features.artifact.block.ElixirCauldronBlock;
import net.minecraft.client.color.block.BlockColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * Block color handler for Elixir Cauldron blocks.
 * Applies the appropriate tint color to the cauldron content texture.
 * Based on the Colored-Water mod approach.
 */
@EventBusSubscriber(modid = "spells_n_squares", value = Dist.CLIENT)
public class ElixirCauldronColorHandler {
    
    // Tint colors matching the fluid types
    private static final int ELIXIR_BASE_COLOR = 0xFFA500; // Orange/gold
    private static final int ELIXIR_OF_LIFE_COLOR = 0xFFD700; // Gold/yellow
    
    /**
     * Block color handler that tints the cauldron content texture.
     */
    private static final BlockColor CAULDRON_COLOR_HANDLER = (state, getter, pos, tintIndex) -> {
        if (getter == null || pos == null) {
            return -1; // Default color
        }
        
        if (state.getBlock() instanceof ElixirCauldronBlock cauldron) {
            // Tint index 0 is typically the content texture
            if (tintIndex == 0) {
                return switch (cauldron.getElixirType()) {
                    case BASE -> ELIXIR_BASE_COLOR | 0xFF000000; // Ensure full alpha
                    case LIFE -> ELIXIR_OF_LIFE_COLOR | 0xFF000000; // Ensure full alpha
                };
            }
        }
        
        return -1; // Default color for other tint indices
    };
    
    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        // Register color handler for Elixir Base Cauldron
        if (at.koopro.spells_n_squares.features.artifact.ArtifactRegistry.ELIXIR_BASE_CAULDRON != null) {
            event.register(CAULDRON_COLOR_HANDLER, 
                at.koopro.spells_n_squares.features.artifact.ArtifactRegistry.ELIXIR_BASE_CAULDRON.value());
        }
        
        // Register color handler for Elixir of Life Cauldron
        if (at.koopro.spells_n_squares.features.artifact.ArtifactRegistry.ELIXIR_OF_LIFE_CAULDRON != null) {
            event.register(CAULDRON_COLOR_HANDLER, 
                at.koopro.spells_n_squares.features.artifact.ArtifactRegistry.ELIXIR_OF_LIFE_CAULDRON.value());
        }
        
        // Note: Fluid blocks (LiquidBlock) are rendered using the fluid renderer,
        // which uses IClientFluidTypeExtensions directly. BlockColor handlers
        // are not used for fluid blocks, so we don't register them here.
    }
}

