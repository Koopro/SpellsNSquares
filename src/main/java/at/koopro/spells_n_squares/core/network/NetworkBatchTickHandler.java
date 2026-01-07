package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.event.EventUtils;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles flushing batched network payloads at the end of each player tick.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class NetworkBatchTickHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SafeEventHandler.execute(() -> {
                // Flush batched payloads for this player at the end of their tick
                NetworkPayloadBatcher.flushPlayer(serverPlayer);
            }, "flushing network payloads", serverPlayer);
        }
    }
}

