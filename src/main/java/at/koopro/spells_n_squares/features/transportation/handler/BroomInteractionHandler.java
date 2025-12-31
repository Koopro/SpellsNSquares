package at.koopro.spells_n_squares.features.transportation.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import at.koopro.spells_n_squares.features.transportation.BroomEntity;

/**
 * Event handler for broom entity interactions.
 * Handles right-click to mount and shift+right-click to pickup.
 */
@EventBusSubscriber(modid = "spells_n_squares")
public class BroomInteractionHandler {
    
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof BroomEntity broom)) {
            return;
        }
        
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        
        // On client side, allow the interaction to proceed to server
        if (event.getLevel().isClientSide()) {
            // Don't cancel, let it go to server
            return;
        }
        
        // Server side handling
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        
        // Skip if already canceled
        if (event.isCanceled()) {
            return;
        }
        
        InteractionResult result = broom.interact(serverPlayer, hand);
        
        // Always consume the action if we handled it
        if (result.consumesAction() || result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}

