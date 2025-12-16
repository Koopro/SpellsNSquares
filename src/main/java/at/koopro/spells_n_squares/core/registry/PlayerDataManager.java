package at.koopro.spells_n_squares.core.registry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Interface for managers that handle per-player data.
 * Managers implementing this interface can be registered with PlayerDataManagerRegistry
 * for automatic cleanup and synchronization.
 */
public interface PlayerDataManager {
    /**
     * Clears all data for a player when they disconnect.
     * @param player The player disconnecting
     */
    void clearPlayerData(Player player);
    
    /**
     * Syncs player data to the client when they join the server.
     * This is called automatically for registered managers.
     * @param serverPlayer The server player joining
     */
    default void syncToClient(ServerPlayer serverPlayer) {
        // Default implementation does nothing - override if sync is needed
    }
}









