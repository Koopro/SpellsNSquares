package at.koopro.spells_n_squares.features.spell;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import at.koopro.spells_n_squares.SpellsNSquares;

/**
 * Handles server-side spell logic like cooldown ticking and player cleanup.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class SpellHandler {
    // Track ticks for periodic cooldown sync (sync every 20 ticks = 1 second)
    private static final int COOLDOWN_SYNC_INTERVAL = 20;
    private static int tickCounter = 0;
    
    /**
     * Ticks cooldowns for players on the server side.
     * This handles both dedicated servers and the integrated server in single-player.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // Only tick on server side (includes integrated server in single-player)
        // Post events already fire at the end of the tick, so no phase check needed
        var player = event.getEntity();
        if (!player.level().isClientSide()) {
            SpellManager.tickCooldowns(player);
            
            // Periodically sync cooldowns to client to keep them in sync
            tickCounter++;
            if (tickCounter >= COOLDOWN_SYNC_INTERVAL) {
                tickCounter = 0;
                if (player instanceof ServerPlayer serverPlayer) {
                    SpellManager.syncCooldownsToClient(serverPlayer);
                }
            }
        }
    }
    
    /**
     * Cleans up player data when they disconnect.
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SpellManager.clearPlayerData(event.getEntity());
        LumosManager.clearPlayerData(event.getEntity());
    }
}
