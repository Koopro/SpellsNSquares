package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * Button block for custom tree types.
 */
public class ModButtonBlock extends ButtonBlock {
    
    public ModButtonBlock(BlockSetType blockSetType, int ticksToStayPressed, Properties properties) {
        super(blockSetType, ticksToStayPressed, properties);
    }
    
    /**
     * Creates a wooden button with standard 30 tick press duration.
     */
    public ModButtonBlock(BlockSetType blockSetType, Properties properties) {
        this(blockSetType, 30, properties);
    }
}








