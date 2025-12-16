package at.koopro.spells_n_squares.block.tree;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * Stairs block for custom tree types.
 */
public class ModStairsBlock extends StairBlock {
    
    public ModStairsBlock(Supplier<BlockState> baseState, Properties properties) {
        // StairBlock constructor in 1.21 takes BlockState directly
        // Evaluate the supplier to get the actual BlockState, with fallback to stone
        super(getBaseStateSafely(baseState), properties);
    }
    
    private static BlockState getBaseStateSafely(Supplier<BlockState> baseState) {
        try {
            return baseState.get();
        } catch (Exception e) {
            // Fallback to stone if base state is not available yet (e.g., during registration)
            return Blocks.STONE.defaultBlockState();
        }
    }
}








