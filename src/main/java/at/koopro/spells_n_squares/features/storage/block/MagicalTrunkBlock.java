package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.storage.TrunkInventoryData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Magical trunk block with multi-compartment storage.
 */
public class MagicalTrunkBlock extends BaseInteractiveBlock {
    
    private static final int DEFAULT_COMPARTMENTS = 3;
    private static final int DEFAULT_SLOTS_PER_COMPARTMENT = 27;
    
    public MagicalTrunkBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.trunk.description";
    }
    
    /**
     * Gets or creates trunk inventory data for a block position.
     * In a full implementation, this would be stored in BlockEntity.
     */
    public static TrunkInventoryData.TrunkInventoryComponent getTrunkInventory(Level level, BlockPos pos) {
        // Placeholder - would use BlockEntity in full implementation
        return TrunkInventoryData.TrunkInventoryComponent.createDefault(
            DEFAULT_COMPARTMENTS, DEFAULT_SLOTS_PER_COMPARTMENT);
    }
}












