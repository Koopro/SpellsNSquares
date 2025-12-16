package at.koopro.spells_n_squares.core.registry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Registry for all player data managers.
 * Provides centralized cleanup and synchronization for registered managers.
 */
public class PlayerDataManagerRegistry {
    private static final Set<PlayerDataManager> managers = new LinkedHashSet<>();
    
    /**
     * Registers a player data manager.
     * @param manager The manager to register
     */
    public static void register(PlayerDataManager manager) {
        if (manager != null) {
            managers.add(manager);
        }
    }
    
    /**
     * Clears all player data across all registered managers.
     * Called when a player disconnects.
     * @param player The player disconnecting
     */
    public static void clearAllPlayerData(Player player) {
        for (PlayerDataManager manager : managers) {
            manager.clearPlayerData(player);
        }
    }
    
    /**
     * Syncs all player data to the client across all registered managers.
     * Called when a player joins the server.
     * @param serverPlayer The server player joining
     */
    public static void syncAllToClient(ServerPlayer serverPlayer) {
        for (PlayerDataManager manager : managers) {
            manager.syncToClient(serverPlayer);
        }
    }
    
    /**
     * Gets all registered managers.
     * @return A copy of the managers list
     */
    public static List<PlayerDataManager> getManagers() {
        return new ArrayList<>(managers);
    }
}
