package at.koopro.spells_n_squares.features.social.friends;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for managing friend lists and friend status.
 * Provides methods for adding, removing, and querying friends.
 */
public final class FriendListHelper {
    
    private static final Map<UUID, Set<UUID>> FRIEND_LISTS = new ConcurrentHashMap<>();
    private static final Map<UUID, FriendStatus> FRIEND_STATUSES = new ConcurrentHashMap<>();
    
    private FriendListHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents friend status information.
     */
    public record FriendStatus(
        boolean online,
        String dimension,
        long lastSeen,
        String displayName
    ) {}
    
    /**
     * Adds a friend to a player's friend list.
     * 
     * @param playerId The player's UUID
     * @param friendId The friend's UUID
     * @return true if friend was added
     */
    public static boolean addFriend(UUID playerId, UUID friendId) {
        if (playerId == null || friendId == null || playerId.equals(friendId)) {
            return false;
        }
        
        Set<UUID> friends = FRIEND_LISTS.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet());
        boolean added = friends.add(friendId);
        
        if (added) {
            DevLogger.logStateChange(FriendListHelper.class, "addFriend",
                "Player: " + playerId + " added friend: " + friendId);
        }
        
        return added;
    }
    
    /**
     * Removes a friend from a player's friend list.
     * 
     * @param playerId The player's UUID
     * @param friendId The friend's UUID
     * @return true if friend was removed
     */
    public static boolean removeFriend(UUID playerId, UUID friendId) {
        if (playerId == null || friendId == null) {
            return false;
        }
        
        Set<UUID> friends = FRIEND_LISTS.get(playerId);
        if (friends == null) {
            return false;
        }
        
        boolean removed = friends.remove(friendId);
        if (removed) {
            DevLogger.logStateChange(FriendListHelper.class, "removeFriend",
                "Player: " + playerId + " removed friend: " + friendId);
        }
        
        return removed;
    }
    
    /**
     * Checks if two players are friends.
     * 
     * @param playerId The first player's UUID
     * @param friendId The second player's UUID
     * @return true if they are friends
     */
    public static boolean areFriends(UUID playerId, UUID friendId) {
        if (playerId == null || friendId == null) {
            return false;
        }
        
        Set<UUID> friends = FRIEND_LISTS.get(playerId);
        return friends != null && friends.contains(friendId);
    }
    
    /**
     * Gets the friend list for a player.
     * 
     * @param playerId The player's UUID
     * @return Set of friend UUIDs
     */
    public static Set<UUID> getFriends(UUID playerId) {
        if (playerId == null) {
            return Collections.emptySet();
        }
        
        Set<UUID> friends = FRIEND_LISTS.get(playerId);
        return friends != null ? new HashSet<>(friends) : Collections.emptySet();
    }
    
    /**
     * Updates friend status for a player.
     * 
     * @param player The server player
     */
    public static void updateFriendStatus(ServerPlayer player) {
        if (player == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        FriendStatus status = new FriendStatus(
            true,
            player.level().dimension().toString(),
            System.currentTimeMillis(),
            player.getName().getString()
        );
        
        FRIEND_STATUSES.put(playerId, status);
    }
    
    /**
     * Gets friend status for a player.
     * 
     * @param friendId The friend's UUID
     * @return Friend status, or null if not found
     */
    public static FriendStatus getFriendStatus(UUID friendId) {
        if (friendId == null) {
            return null;
        }
        
        return FRIEND_STATUSES.get(friendId);
    }
    
    /**
     * Gets all friends with their statuses.
     * 
     * @param playerId The player's UUID
     * @return Map of friend UUID to status
     */
    public static Map<UUID, FriendStatus> getFriendsWithStatus(UUID playerId) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        
        Set<UUID> friends = getFriends(playerId);
        Map<UUID, FriendStatus> result = new HashMap<>();
        
        for (UUID friendId : friends) {
            FriendStatus status = FRIEND_STATUSES.get(friendId);
            if (status != null) {
                result.put(friendId, status);
            } else {
                // Offline friend
                result.put(friendId, new FriendStatus(false, "", 0, "Unknown"));
            }
        }
        
        return result;
    }
}

