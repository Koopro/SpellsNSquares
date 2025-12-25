package at.koopro.spells_n_squares.features.mail;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles mail delivery processing on server ticks.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class MailDeliveryHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                // Process pending mail deliveries for this player
                OwlPostSystem.processDeliveries(serverPlayer, serverLevel);
            }
        }
    }
}











