package at.koopro.spells_n_squares.block.storage;

import at.koopro.spells_n_squares.features.storage.TrunkInventoryData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Magical trunk block with multi-compartment storage.
 */
public class MagicalTrunkBlock extends Block {
    
    private static final int DEFAULT_COMPARTMENTS = 3;
    private static final int DEFAULT_SLOTS_PER_COMPARTMENT = 27;
    
    public MagicalTrunkBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        // Get or create trunk inventory data
        // For blocks, we'd typically use BlockEntity, but for simplicity, using a data component approach
        // In a full implementation, this would use BlockEntity with Container
        
        if (player instanceof ServerPlayer serverPlayer) {
            // Simplified: send message for now
            // Full implementation would open a container screen
            serverPlayer.sendSystemMessage(Component.literal("Magical Trunk - Storage system"));
        }
        
        return InteractionResult.SUCCESS;
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

