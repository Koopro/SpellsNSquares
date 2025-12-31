package at.koopro.spells_n_squares.features.mail.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.mail.system.OwlPostSystem;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.core.util.PlayerValidationUtils;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles mail delivery processing on server ticks.
 * Optimized: Only checks every 20 ticks (1 second) to reduce overhead.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class MailDeliveryHandler {
    
    private static final int CHECK_INTERVAL = 20; // Check every second
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        
        SafeEventHandler.execute(() -> {
            // Only check periodically
            if (player.tickCount % CHECK_INTERVAL != 0) {
                return;
            }
            
            ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(player);
            if (serverPlayer != null && serverPlayer.level() instanceof ServerLevel serverLevel) {
                // Process pending mail deliveries for this player
                OwlPostSystem.processDeliveries(serverPlayer, serverLevel);
            }
        }, "processing mail delivery", player);
    }
}
















