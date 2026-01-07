package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for generic/shared mod blocks that don't belong to a specific feature.
 * Feature-specific blocks are registered in their respective feature registries.
 */
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    // Generic blocks would be registered here
    // Currently empty as all blocks belong to specific features
}
