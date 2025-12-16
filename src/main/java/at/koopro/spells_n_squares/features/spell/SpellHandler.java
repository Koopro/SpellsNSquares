package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.core.util.PlayerValidationUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles server-side spell logic like cooldown ticking and player cleanup.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class SpellHandler {
    // Track ticks for periodic cooldown sync (sync every 20 ticks = 1 second)
    private static final int COOLDOWN_SYNC_INTERVAL = 20;
    // Per-player tick counters for more accurate cooldown syncing
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<Player, Integer> playerTickCounters = new HashMap<>();
    
    /**
     * Ticks cooldowns for players on the server side.
     * This handles both dedicated servers and the integrated server in single-player.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // Only tick on server side (includes integrated server in single-player)
        // Post events already fire at the end of the tick, so no phase check needed
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        var player = event.getEntity();
        SpellManager.tickCooldowns(player);
        
        // Periodically sync cooldowns to client to keep them in sync
        // Use per-player tick counter for more accurate syncing
        int tickCount = playerTickCounters.getOrDefault(player, 0) + 1;
        if (tickCount >= COOLDOWN_SYNC_INTERVAL) {
            tickCount = 0;
            ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(player);
            if (serverPlayer != null) {
                SpellManager.syncCooldownsToClient(serverPlayer);
            }
        }
        playerTickCounters.put(player, tickCount);
    }
    
    /**
     * Clears the tick counter for a player when they disconnect.
     * This prevents memory leaks from accumulating tick counters.
     * @param player The player disconnecting
     */
    public static void clearPlayerTickCounter(Player player) {
        playerTickCounters.remove(player);
    }
    
}
