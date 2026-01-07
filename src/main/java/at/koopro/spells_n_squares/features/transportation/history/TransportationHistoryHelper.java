package at.koopro.spells_n_squares.features.transportation.history;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks transportation history for players.
 * Records recent teleportations and travel patterns.
 */
public final class TransportationHistoryHelper {
    
    private static final Map<UUID, List<TransportationRecord>> PLAYER_HISTORY = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY_SIZE = 20;
    
    private TransportationHistoryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a transportation record.
     */
    public record TransportationRecord(
        ResourceKey<Level> fromDimension,
        BlockPos fromPos,
        ResourceKey<Level> toDimension,
        BlockPos toPos,
        String method,  // "apparition", "waypoint", etc.
        long timestamp
    ) {}
    
    /**
     * Records a transportation event.
     * 
     * @param player The server player
     * @param fromDimension Source dimension
     * @param fromPos Source position
     * @param toDimension Destination dimension
     * @param toPos Destination position
     * @param method Transportation method
     */
    public static void recordTransportation(ServerPlayer player, ResourceKey<Level> fromDimension, BlockPos fromPos,
                                           ResourceKey<Level> toDimension, BlockPos toPos, String method) {
        if (player == null || fromDimension == null || toDimension == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        List<TransportationRecord> history = PLAYER_HISTORY.computeIfAbsent(playerId, k -> new ArrayList<>());
        
        TransportationRecord record = new TransportationRecord(
            fromDimension, fromPos, toDimension, toPos, method, System.currentTimeMillis()
        );
        
        history.add(record);
        
        // Keep only recent records
        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        
        DevLogger.logStateChange(TransportationHistoryHelper.class, "recordTransportation",
            "Player: " + player.getName().getString() + ", Method: " + method);
    }
    
    /**
     * Gets recent transportation history for a player.
     * 
     * @param player The server player
     * @param count Number of recent records to return
     * @return List of recent transportation records
     */
    public static List<TransportationRecord> getRecentHistory(ServerPlayer player, int count) {
        if (player == null) {
            return Collections.emptyList();
        }
        
        UUID playerId = player.getUUID();
        List<TransportationRecord> history = PLAYER_HISTORY.get(playerId);
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }
        
        int startIndex = Math.max(0, history.size() - count);
        List<TransportationRecord> recent = new ArrayList<>(history.subList(startIndex, history.size()));
        Collections.reverse(recent); // Most recent first
        return recent;
    }
    
    /**
     * Gets all transportation history for a player.
     * 
     * @param player The server player
     * @return List of all transportation records
     */
    public static List<TransportationRecord> getAllHistory(ServerPlayer player) {
        if (player == null) {
            return Collections.emptyList();
        }
        
        UUID playerId = player.getUUID();
        List<TransportationRecord> history = PLAYER_HISTORY.get(playerId);
        return history != null ? new ArrayList<>(history) : Collections.emptyList();
    }
    
    /**
     * Clears transportation history for a player.
     * 
     * @param player The server player
     */
    public static void clearHistory(ServerPlayer player) {
        if (player != null) {
            PLAYER_HISTORY.remove(player.getUUID());
        }
    }
}

