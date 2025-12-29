package at.koopro.spells_n_squares.features.portraits.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.portraits.PortraitData;
import at.koopro.spells_n_squares.features.portraits.PortraitDialogueSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Block for magical portraits.
 * Portraits can hold conversations and deliver messages.
 */
public class MagicalPortraitBlock extends BaseInteractiveBlock implements EntityBlock {
    
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
        PortraitDialogueSystem.startConversation(serverPlayer, portrait, pos);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets portrait data for this block.
     * Retrieves from BlockEntity.
     */
    private PortraitData.PortraitComponent getPortraitData(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PortraitBlockEntity blockEntity) {
            return blockEntity.getPortraitData();
        }
        return null;
    }
    
    /**
     * Sets portrait data for this block.
     */
    public static void setPortraitData(Level level, BlockPos pos, PortraitData.PortraitComponent portrait) {
        if (level.getBlockEntity(pos) instanceof PortraitBlockEntity blockEntity) {
            blockEntity.setPortraitData(portrait);
        }
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Note: BlockEntityType must be registered separately
        // For now, return null - this will be set up when the BlockEntityType is registered
        return null;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null; // No ticker needed
    }
}










