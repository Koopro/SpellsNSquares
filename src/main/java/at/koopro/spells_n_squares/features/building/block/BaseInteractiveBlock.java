package at.koopro.spells_n_squares.features.building.block;

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
 * Base class for interactive blocks that follow a common interaction pattern.
 * Handles client-side checks and ServerPlayer casting, allowing subclasses
 * to override onServerInteract() for custom behavior.
 */
public class BaseInteractiveBlock extends Block {
    
    public BaseInteractiveBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                       InteractionHand hand, BlockHitResult hit) {
        // Debug: Log that use method was called (even on client to see if it's being called)
        if (level.isClientSide()) {
            System.out.println("[CLIENT] Block use() called for " + this.getClass().getSimpleName() + " at " + pos);
            System.out.println("[CLIENT] Player: " + (player != null ? player.getName().getString() : "null"));
            System.out.println("[CLIENT] Hand: " + hand);
            System.out.println("[CLIENT] Hit face: " + (hit != null ? hit.getDirection() : "null"));
            System.out.println("[CLIENT] Item in hand: " + (player != null && player.getItemInHand(hand) != null ? player.getItemInHand(hand).getItem() : "null"));
            // Return SUCCESS to allow server-side processing
            return InteractionResult.SUCCESS;
        }
        
        System.out.println("[SERVER] Block use() called for " + this.getClass().getSimpleName() + " at " + pos);
        System.out.println("[SERVER] Player: " + (player != null ? player.getName().getString() : "null"));
        System.out.println("[SERVER] Hand: " + hand);
        System.out.println("[SERVER] Hit face: " + (hit != null ? hit.getDirection() : "null"));
        System.out.println("[SERVER] Is shift down: " + (player != null ? player.isShiftKeyDown() : "N/A"));
        
        if (player instanceof ServerPlayer serverPlayer) {
            InteractionResult result = onServerInteract(state, level, pos, serverPlayer, hand, hit);
            System.out.println("[SERVER] onServerInteract returned: " + result);
            // Return CONSUME to prevent item interactions from taking priority
            if (result == InteractionResult.SUCCESS) {
                return InteractionResult.CONSUME;
            }
            return result;
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Called when a player interacts with this block on the server side.
     * Override this method to provide custom interaction behavior.
     * 
     * @param state The block state
     * @param level The level
     * @param pos The block position
     * @param serverPlayer The server player interacting
     * @param hand The hand used
     * @param hit The hit result
     * @return The interaction result
     */
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                  ServerPlayer serverPlayer, InteractionHand hand, 
                                                  BlockHitResult hit) {
        String messageKey = getInteractionMessageKey();
        if (messageKey != null) {
            serverPlayer.sendSystemMessage(Component.translatable(messageKey));
        }
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Returns the translation key for the interaction message.
     * Override this to provide a custom message, or return null to skip the message.
     * 
     * @return The translation key, or null to skip sending a message
     */
    protected String getInteractionMessageKey() {
        return null;
    }
}



