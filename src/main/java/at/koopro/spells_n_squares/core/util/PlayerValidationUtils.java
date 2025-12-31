package at.koopro.spells_n_squares.core.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Utility class for common player validation patterns.
 * Reduces repeated instanceof checks and provides consistent validation logic.
 */
public final class PlayerValidationUtils {
    private PlayerValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a player is a ServerPlayer.
     * @param player The player to check
     * @return true if the player is a ServerPlayer, false otherwise
     */
    public static boolean isServerPlayer(Player player) {
        return player instanceof ServerPlayer;
    }
    
    /**
     * Safely casts a Player to ServerPlayer.
     * @param player The player to cast
     * @return The ServerPlayer if the player is a ServerPlayer, null otherwise
     */
    public static ServerPlayer asServerPlayer(Player player) {
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }
    
    /**
     * Checks if the player's level is on the client side.
     * @param player The player to check
     * @return true if on client side, false if on server side
     */
    public static boolean isClientSide(Player player) {
        return player != null && player.level().isClientSide();
    }
    
    /**
     * Checks if the player's level is on the server side.
     * @param player The player to check
     * @return true if on server side, false if on client side
     */
    public static boolean isServerSide(Player player) {
        return player != null && !player.level().isClientSide();
    }
}





















