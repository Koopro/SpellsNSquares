package at.koopro.spells_n_squares.features.playerclass;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Handles server-side player class logic like initialization and cleanup.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class PlayerClassHandler {
    
    /**
     * Initializes player class when they join the server.
     * Syncs the player class to the client.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Sync player class to client when they join
            PlayerClassManager.syncPlayerClassToClient(serverPlayer);
        }
    }
    
    /**
     * Cleans up player class data when they disconnect.
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerClassManager.clearPlayerData(event.getEntity());
    }
}
