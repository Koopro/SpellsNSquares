package at.koopro.spells_n_squares.core.util.network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for client-server data synchronization utilities.
 * Provides automatic synchronization of data between server and client.
 */
public final class DataSyncHelper {
    
    private static final Map<UUID, Map<String, Object>> PLAYER_DATA = new ConcurrentHashMap<>();
    private static final Map<UUID, Set<String>> DIRTY_FLAGS = new ConcurrentHashMap<>();
    
    private DataSyncHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Sets data for a player that should be synced to client.
     * 
     * @param playerId The player's UUID
     * @param key The data key
     * @param value The data value
     */
    public static void setPlayerData(UUID playerId, String key, Object value) {
        if (playerId == null || key == null) {
            return;
        }
        
        PLAYER_DATA.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>()).put(key, value);
        DIRTY_FLAGS.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet()).add(key);
    }
    
    /**
     * Gets data for a player.
     * 
     * @param playerId The player's UUID
     * @param key The data key
     * @return The data value, or null if not found
     */
    public static Object getPlayerData(UUID playerId, String key) {
        if (playerId == null || key == null) {
            return null;
        }
        
        Map<String, Object> data = PLAYER_DATA.get(playerId);
        return data != null ? data.get(key) : null;
    }
    
    /**
     * Gets all dirty (unsynced) data keys for a player.
     * 
     * @param playerId The player's UUID
     * @return Set of dirty keys
     */
    public static Set<String> getDirtyKeys(UUID playerId) {
        if (playerId == null) {
            return Collections.emptySet();
        }
        
        Set<String> dirty = DIRTY_FLAGS.get(playerId);
        return dirty != null ? new HashSet<>(dirty) : Collections.emptySet();
    }
    
    /**
     * Marks data as synced (clears dirty flag).
     * 
     * @param playerId The player's UUID
     * @param key The data key
     */
    public static void markSynced(UUID playerId, String key) {
        if (playerId == null || key == null) {
            return;
        }
        
        Set<String> dirty = DIRTY_FLAGS.get(playerId);
        if (dirty != null) {
            dirty.remove(key);
        }
    }
    
    /**
     * Clears all data for a player.
     * 
     * @param playerId The player's UUID
     */
    public static void clearPlayerData(UUID playerId) {
        if (playerId != null) {
            PLAYER_DATA.remove(playerId);
            DIRTY_FLAGS.remove(playerId);
        }
    }
}

