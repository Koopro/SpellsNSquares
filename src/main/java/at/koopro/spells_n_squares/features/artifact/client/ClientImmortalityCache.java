package at.koopro.spells_n_squares.features.artifact.client;

import at.koopro.spells_n_squares.features.artifact.ImmortalityData;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Client-side cache for immortality data.
 * Updated via network sync from server.
 */
public final class ClientImmortalityCache {
    private static final Map<UUID, ImmortalityData.ImmortalityComponent> cache = new HashMap<>();
    
    private ClientImmortalityCache() {
    }
    
    /**
     * Updates the cache for a player.
     */
    public static void update(Player player, ImmortalityData.ImmortalityComponent data) {
        if (player != null) {
            cache.put(player.getUUID(), data);
        }
    }
    
    /**
     * Gets cached data for a player, or default if not cached.
     */
    public static ImmortalityData.ImmortalityComponent get(Player player) {
        if (player == null) {
            return ImmortalityData.ImmortalityComponent.createDefault();
        }
        return cache.getOrDefault(player.getUUID(), ImmortalityData.ImmortalityComponent.createDefault());
    }
    
    /**
     * Clears the cache for a player (e.g., on disconnect).
     */
    public static void clear(Player player) {
        if (player != null) {
            cache.remove(player.getUUID());
        }
    }
}


