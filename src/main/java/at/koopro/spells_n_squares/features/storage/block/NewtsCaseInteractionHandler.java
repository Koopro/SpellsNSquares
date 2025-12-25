package at.koopro.spells_n_squares.features.storage.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Event handler to intercept right-clicks on Newt's Case blocks,
 * especially for empty-hand interactions that might not reach the block's use() method.
 */
@EventBusSubscriber(modid = "spells_n_squares")
public class NewtsCaseInteractionHandler {
    
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        
        // Only handle Newt's Case blocks
        if (!(state.getBlock() instanceof NewtsCaseBlock)) {
            return;
        }
        
        // Only process MAIN_HAND to avoid double processing (OFF_HAND will be ignored)
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        
        // Only process on server side
        if (level.isClientSide()) {
            return;
        }
        
        // Skip if event is already canceled
        if (event.isCanceled()) {
            return;
        }
        
        // Check if player has empty hand
        ItemStack heldItem = event.getItemStack();
        boolean isEmptyHand = heldItem.isEmpty();
        
        System.out.println("[NewtsCaseInteractionHandler] Right-click detected on NewtsCase block");
        System.out.println("[NewtsCaseInteractionHandler] Pos: " + pos);
        System.out.println("[NewtsCaseInteractionHandler] Empty hand: " + isEmptyHand);
        System.out.println("[NewtsCaseInteractionHandler] Item: " + (isEmptyHand ? "EMPTY" : heldItem.getItem()));
        
        // Only intercept if empty hand (when holding case item, let the normal flow handle it)
        if (isEmptyHand && event.getEntity() instanceof ServerPlayer) {
            // Manually call the block's interaction logic
            InteractionHand hand = event.getHand();
            BlockHitResult hitResult = event.getHitVec();
            
            System.out.println("[NewtsCaseInteractionHandler] Calling block.use() for empty hand interaction");
            NewtsCaseBlock block = (NewtsCaseBlock) state.getBlock();
            InteractionResult result = block.use(state, level, pos, event.getEntity(), hand, hitResult);
            
            if (result.consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        }
    }
}

