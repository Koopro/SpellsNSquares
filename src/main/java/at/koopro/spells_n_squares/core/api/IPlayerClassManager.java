package at.koopro.spells_n_squares.core.api;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Interface for player class management.
 * Defines the API contract for player class functionality.
 */
public interface IPlayerClassManager {
    /**
     * Sets the class for a player.
     * @param player The player
     * @param playerClass The class to assign
     */
    void setPlayerClass(Player player, PlayerClass playerClass);
    
    /**
     * Gets the class for a player.
     * @param player The player
     * @return The player's class, or NONE if not set
     */
    PlayerClass getPlayerClass(Player player);
    
    /**
     * Checks if a player has a specific class.
     * @param player The player
     * @param playerClass The class to check for
     * @return true if the player has the class
     */
    boolean hasPlayerClass(Player player, PlayerClass playerClass);
    
    /**
     * Clears the class data for a player (used when player disconnects).
     * @param player The player
     */
    void clearPlayerData(Player player);
    
    /**
     * Syncs player class to the client for a server player.
     * @param serverPlayer The server player
     */
    void syncPlayerClassToClient(ServerPlayer serverPlayer);
}






















