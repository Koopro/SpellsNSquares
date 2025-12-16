package at.koopro.spells_n_squares.block.portraits;

import at.koopro.spells_n_squares.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.portraits.PortraitData;
import at.koopro.spells_n_squares.features.portraits.PortraitDialogueSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Block entity for magical portraits.
 * Portraits can hold conversations and deliver messages.
 */
public class MagicalPortraitBlock extends BaseInteractiveBlock {
    
    public MagicalPortraitBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                 ServerPlayer serverPlayer, InteractionHand hand, 
                                                 BlockHitResult hit) {
        // Get portrait data from block entity or storage
        PortraitData.PortraitComponent portrait = getPortraitData(level, pos);
        
        if (portrait == null) {
            serverPlayer.sendSystemMessage(Component.translatable(
                "message.spells_n_squares.portrait.not_configured"));
            return InteractionResult.FAIL;
        }
        
        // Start conversation
        PortraitDialogueSystem.startConversation(serverPlayer, portrait);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets portrait data for this block.
     * TODO: Retrieve from BlockEntity or persistent storage
     */
    private PortraitData.PortraitComponent getPortraitData(Level level, BlockPos pos) {
        // TODO: Implement proper storage
        // For now, return null
        return null;
    }
    
    /**
     * Sets portrait data for this block.
     */
    public static void setPortraitData(Level level, BlockPos pos, PortraitData.PortraitComponent portrait) {
        // TODO: Store in BlockEntity or persistent storage
    }
}



