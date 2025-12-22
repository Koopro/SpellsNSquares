package at.koopro.spells_n_squares.core.util;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Utility class for common event handling patterns.
 * Provides helper methods to reduce repeated checks in event handlers.
 */
public final class EventUtils {
    private EventUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if the player's level is on the server side for a PlayerTickEvent.
     * @param event The player tick event
     * @return true if on server side, false if on client side
     */
    public static boolean isServerSide(PlayerTickEvent event) {
        Player player = event.getEntity();
        return player != null && !player.level().isClientSide();
    }
    
    /**
     * Checks if the player's level is on the client side for a PlayerTickEvent.
     * @param event The player tick event
     * @return true if on client side, false if on server side
     */
    public static boolean isClientSide(PlayerTickEvent event) {
        Player player = event.getEntity();
        return player != null && player.level().isClientSide();
    }
}












