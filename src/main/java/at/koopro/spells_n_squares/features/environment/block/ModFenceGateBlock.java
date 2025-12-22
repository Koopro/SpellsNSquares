package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Fence gate block for custom tree types.
 */
public class ModFenceGateBlock extends FenceGateBlock {
    
    public ModFenceGateBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
    }
}








