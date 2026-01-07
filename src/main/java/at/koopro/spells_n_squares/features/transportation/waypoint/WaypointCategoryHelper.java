package at.koopro.spells_n_squares.features.transportation.waypoint;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Helper class for waypoint categories and organization.
 * Allows players to organize waypoints by category.
 */
public final class WaypointCategoryHelper {
    
    private static final Map<UUID, Map<String, List<UUID>>> PLAYER_CATEGORIES = new java.util.concurrent.ConcurrentHashMap<>();
    
    private WaypointCategoryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Adds a waypoint to a category.
     * 
     * @param player The server player
     * @param waypointId The waypoint ID
     * @param category The category name
     */
    public static void addToCategory(ServerPlayer player, java.util.UUID waypointId, String category) {
        if (player == null || waypointId == null || category == null || category.trim().isEmpty()) {
            return;
        }
        
        UUID playerId = player.getUUID();
        Map<String, List<UUID>> categories = PLAYER_CATEGORIES.computeIfAbsent(playerId, k -> new java.util.concurrent.ConcurrentHashMap<>());
        List<UUID> waypoints = categories.computeIfAbsent(category.trim().toLowerCase(), k -> new ArrayList<>());
        
        if (!waypoints.contains(waypointId)) {
            waypoints.add(waypointId);
        }
    }
    
    /**
     * Removes a waypoint from a category.
     * 
     * @param player The server player
     * @param waypointId The waypoint ID
     * @param category The category name
     */
    public static void removeFromCategory(ServerPlayer player, java.util.UUID waypointId, String category) {
        if (player == null || waypointId == null || category == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        Map<String, List<UUID>> categories = PLAYER_CATEGORIES.get(playerId);
        if (categories == null) {
            return;
        }
        
        List<UUID> waypoints = categories.get(category.trim().toLowerCase());
        if (waypoints != null) {
            waypoints.remove(waypointId);
        }
    }
    
    /**
     * Gets all waypoints in a category.
     * 
     * @param player The server player
     * @param category The category name
     * @return List of waypoint IDs
     */
    public static List<java.util.UUID> getWaypointsInCategory(ServerPlayer player, String category) {
        if (player == null || category == null) {
            return Collections.emptyList();
        }
        
        UUID playerId = player.getUUID();
        Map<String, List<UUID>> categories = PLAYER_CATEGORIES.get(playerId);
        if (categories == null) {
            return Collections.emptyList();
        }
        
        List<UUID> waypoints = categories.get(category.trim().toLowerCase());
        return waypoints != null ? new ArrayList<>(waypoints) : Collections.emptyList();
    }
    
    /**
     * Gets all categories for a player.
     * 
     * @param player The server player
     * @return Set of category names
     */
    public static Set<String> getCategories(ServerPlayer player) {
        if (player == null) {
            return Collections.emptySet();
        }
        
        UUID playerId = player.getUUID();
        Map<String, List<UUID>> categories = PLAYER_CATEGORIES.get(playerId);
        return categories != null ? new HashSet<>(categories.keySet()) : Collections.emptySet();
    }
}

