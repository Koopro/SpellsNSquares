package at.koopro.spells_n_squares.block.communication;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Notice board block for server-wide announcements.
 */
public class NoticeBoardBlock extends Block {
    
    public NoticeBoardBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            // Simplified: send message for now
            // Full implementation would open a GUI for posting/reading messages
            serverPlayer.sendSystemMessage(Component.literal("Notice Board - View and post announcements"));
        }
        
        return InteractionResult.SUCCESS;
    }
}

