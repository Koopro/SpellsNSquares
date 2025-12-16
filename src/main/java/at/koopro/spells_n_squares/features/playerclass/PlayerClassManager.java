package at.koopro.spells_n_squares.features.playerclass;

import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.PlayerClassChangeEvent;
import at.koopro.spells_n_squares.features.playerclass.network.PlayerClassSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages player classes for all players.
 * Handles storage, retrieval, and syncing of player class data.
 */
public class PlayerClassManager {
    // Per-player class assignments
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<Player, PlayerClass> playerClasses = new HashMap<>();
    
    /**
     * Sets the class for a player.
     * @param player The player
     * @param playerClass The class to assign
     */
    public static void setPlayerClass(Player player, PlayerClass playerClass) {
        if (playerClass == null) {
            playerClass = PlayerClass.NONE;
        }
        
        // Get old class for event
        PlayerClass oldClass = getPlayerClass(player);
        
        playerClasses.put(player, playerClass);
        
        // Fire event if class changed
        if (oldClass != playerClass) {
            PlayerClassChangeEvent event = new PlayerClassChangeEvent(player, oldClass, playerClass);
            AddonEventBus.getInstance().post(event);
        }
        
        // Sync to client if this is a server player
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            syncPlayerClassToClient(serverPlayer);
        }
    }
    
    /**
     * Gets the class for a player.
     * @param player The player
     * @return The player's class, or NONE if not set
     */
    public static PlayerClass getPlayerClass(Player player) {
        return playerClasses.getOrDefault(player, PlayerClass.NONE);
    }
    
    /**
     * Checks if a player has a specific class.
     * @param player The player
     * @param playerClass The class to check for
     * @return true if the player has the class
     */
    public static boolean hasPlayerClass(Player player, PlayerClass playerClass) {
        return getPlayerClass(player) == playerClass;
    }
    
    /**
     * Clears the class data for a player (used when player disconnects).
     * @param player The player
     */
    public static void clearPlayerData(Player player) {
        playerClasses.remove(player);
    }
    
    /**
     * Syncs player class to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncPlayerClassToClient(ServerPlayer serverPlayer) {
        PlayerClass playerClass = getPlayerClass(serverPlayer);
        PlayerClassSyncPayload payload = new PlayerClassSyncPayload(playerClass);
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }
}
