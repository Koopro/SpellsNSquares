package at.koopro.spells_n_squares.features.social;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Manages player friendships.
 * Handles friend requests, friend lists, and friend-related features.
 */
public final class FriendshipSystem {
    private FriendshipSystem() {
    }
    
    /**
     * Gets social data for a player.
     */
    private static SocialData.SocialComponent getSocialData(Player player) {
        // TODO: Retrieve from player data component
        // For now, return default
        return new SocialData.SocialComponent();
    }
    
    /**
     * Sets social data for a player.
     */
    private static void setSocialData(Player player, SocialData.SocialComponent data) {
        // TODO: Store in player data component
    }
    
    /**
     * Sends a friend request from one player to another.
     */
    public static boolean sendFriendRequest(ServerPlayer sender, ServerPlayer recipient) {
        if (sender.getUUID().equals(recipient.getUUID())) {
            return false; // Can't friend yourself
        }
        
        SocialData.SocialComponent senderData = getSocialData(sender);
        SocialData.SocialComponent recipientData = getSocialData(recipient);
        
        // Check if already friends
        if (senderData.friends().contains(recipient.getUUID())) {
            return false;
        }
        
        // Check if request already sent
        if (senderData.pendingSentRequests().contains(recipient.getUUID())) {
            return false;
        }
        
        // Send request
        senderData = senderData.sendFriendRequest(recipient.getUUID());
        recipientData = recipientData.receiveFriendRequest(sender.getUUID());
        
        setSocialData(sender, senderData);
        setSocialData(recipient, recipientData);
        
        // Notify recipient
        recipient.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.social.friend_request_received", sender.getName()));
        
        return true;
    }
    
    /**
     * Accepts a friend request.
     */
    public static boolean acceptFriendRequest(ServerPlayer accepter, UUID requesterId) {
        SocialData.SocialComponent accepterData = getSocialData(accepter);
        
        // Check if request exists
        if (!accepterData.pendingReceivedRequests().contains(requesterId)) {
            return false;
        }
        
        // Accept request
        accepterData = accepterData.addFriend(requesterId);
        setSocialData(accepter, accepterData);
        
        // Update requester's data
        ServerPlayer requester = accepter.level().getServer().getPlayerList().getPlayer(requesterId);
        if (requester != null) {
            SocialData.SocialComponent requesterData = getSocialData(requester);
            requesterData = requesterData.addFriend(accepter.getUUID());
            setSocialData(requester, requesterData);
            
            requester.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                "message.spells_n_squares.social.friend_request_accepted", accepter.getName()));
        }
        
        accepter.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.social.friend_added"));
        
        return true;
    }
    
    /**
     * Declines a friend request.
     */
    public static boolean declineFriendRequest(ServerPlayer decliner, UUID requesterId) {
        SocialData.SocialComponent declinerData = getSocialData(decliner);
        
        // Check if request exists
        if (!declinerData.pendingReceivedRequests().contains(requesterId)) {
            return false;
        }
        
        // Remove request
        Set<UUID> newReceived = new HashSet<>(declinerData.pendingReceivedRequests());
        newReceived.remove(requesterId);
        declinerData = new SocialData.SocialComponent(
            declinerData.friends(),
            declinerData.pendingSentRequests(),
            newReceived,
            declinerData.reputation(),
            declinerData.npcReputation()
        );
        setSocialData(decliner, declinerData);
        
        return true;
    }
    
    /**
     * Removes a friend.
     */
    public static boolean removeFriend(ServerPlayer player, UUID friendId) {
        SocialData.SocialComponent playerData = getSocialData(player);
        
        if (!playerData.friends().contains(friendId)) {
            return false;
        }
        
        playerData = playerData.removeFriend(friendId);
        setSocialData(player, playerData);
        
        // Also remove from friend's list
        ServerPlayer friend = player.level().getServer().getPlayerList().getPlayer(friendId);
        if (friend != null) {
            SocialData.SocialComponent friendData = getSocialData(friend);
            friendData = friendData.removeFriend(player.getUUID());
            setSocialData(friend, friendData);
        }
        
        return true;
    }
    
    /**
     * Gets the friend list for a player.
     */
    public static Set<UUID> getFriends(Player player) {
        SocialData.SocialComponent data = getSocialData(player);
        return new HashSet<>(data.friends());
    }
    
    /**
     * Checks if two players are friends.
     */
    public static boolean areFriends(Player player1, Player player2) {
        return getFriends(player1).contains(player2.getUUID());
    }
    
    /**
     * Gets pending friend requests received by a player.
     */
    public static Set<UUID> getPendingReceivedRequests(Player player) {
        SocialData.SocialComponent data = getSocialData(player);
        return new HashSet<>(data.pendingReceivedRequests());
    }
    
    /**
     * Gets pending friend requests sent by a player.
     */
    public static Set<UUID> getPendingSentRequests(Player player) {
        SocialData.SocialComponent data = getSocialData(player);
        return new HashSet<>(data.pendingSentRequests());
    }
    
    /**
     * Friendship levels.
     */
    public enum FriendshipLevel {
        ACQUAINTANCE(0, "Acquaintance", "You know each other"),
        FRIEND(100, "Friend", "Good friends"),
        CLOSE_FRIEND(500, "Close Friend", "Very close friends"),
        BEST_FRIEND(1000, "Best Friend", "Best friends forever");
        
        private final int requiredPoints;
        private final String displayName;
        private final String description;
        
        FriendshipLevel(int requiredPoints, String displayName, String description) {
            this.requiredPoints = requiredPoints;
            this.displayName = displayName;
            this.description = description;
        }
        
        public int getRequiredPoints() { return requiredPoints; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        public static FriendshipLevel fromPoints(int points) {
            FriendshipLevel current = ACQUAINTANCE;
            for (FriendshipLevel level : values()) {
                if (points >= level.requiredPoints) {
                    current = level;
                } else {
                    break;
                }
            }
            return current;
        }
    }
    
    // Friendship points (player1 UUID -> player2 UUID -> points)
    private static final Map<UUID, Map<UUID, Integer>> friendshipPoints = new HashMap<>();
    
    // Active friendship quests (player UUID -> quest UUID -> quest data)
    private static final Map<UUID, Map<String, FriendshipQuest>> activeQuests = new HashMap<>();
    
    /**
     * Represents a friendship quest.
     */
    public record FriendshipQuest(
        String questId,
        UUID targetPlayerId,
        String title,
        String description,
        QuestType type,
        int rewardPoints,
        long startTime,
        long expiryTime,
        boolean completed
    ) {}
    
    /**
     * Friendship quest types.
     */
    public enum QuestType {
        SPEND_TIME("Spend Time Together", "Spend time near your friend"),
        COMPLETE_TASK("Complete Task Together", "Complete a task together"),
        GIVE_GIFT("Give Gift", "Give a gift to your friend"),
        HELP_FRIEND("Help Friend", "Help your friend in need");
        
        private final String displayName;
        private final String description;
        
        QuestType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Adds friendship points between two players.
     */
    public static void addFriendshipPoints(ServerPlayer player1, ServerPlayer player2, int points) {
        if (!areFriends(player1, player2)) {
            return; // Must be friends first
        }
        
        UUID id1 = player1.getUUID();
        UUID id2 = player2.getUUID();
        
        friendshipPoints.computeIfAbsent(id1, k -> new HashMap<>()).merge(id2, points, Integer::sum);
        friendshipPoints.computeIfAbsent(id2, k -> new HashMap<>()).merge(id1, points, Integer::sum);
    }
    
    /**
     * Gets friendship points between two players.
     */
    public static int getFriendshipPoints(Player player1, Player player2) {
        Map<UUID, Integer> points = friendshipPoints.get(player1.getUUID());
        if (points == null) {
            return 0;
        }
        return points.getOrDefault(player2.getUUID(), 0);
    }
    
    /**
     * Gets friendship level between two players.
     */
    public static FriendshipLevel getFriendshipLevel(Player player1, Player player2) {
        int points = getFriendshipPoints(player1, player2);
        return FriendshipLevel.fromPoints(points);
    }
    
    /**
     * Gets friendship benefits for a friendship level.
     */
    public static Set<String> getFriendshipBenefits(FriendshipLevel level) {
        Set<String> benefits = new HashSet<>();
        
        switch (level) {
            case FRIEND:
                benefits.add("spell_sharing"); // Can share spells
                break;
            case CLOSE_FRIEND:
                benefits.add("spell_sharing");
                benefits.add("teleport_to_friend"); // Can teleport to friend
                benefits.add("shared_inventory"); // Shared inventory access
                break;
            case BEST_FRIEND:
                benefits.add("spell_sharing");
                benefits.add("teleport_to_friend");
                benefits.add("shared_inventory");
                benefits.add("resurrection_help"); // Can help resurrect friend
                benefits.add("bonus_xp"); // Bonus XP when together
                break;
        }
        
        return benefits;
    }
    
    /**
     * Creates a friendship quest.
     */
    public static FriendshipQuest createQuest(ServerPlayer player, ServerPlayer target, QuestType type, int rewardPoints) {
        if (!areFriends(player, target)) {
            return null;
        }
        
        String questId = player.getUUID().toString() + "_" + System.currentTimeMillis();
        FriendshipQuest quest = new FriendshipQuest(
            questId,
            target.getUUID(),
            type.getDisplayName(),
            type.getDescription(),
            type,
            rewardPoints,
            System.currentTimeMillis(),
            System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24 hours
            false
        );
        
        activeQuests.computeIfAbsent(player.getUUID(), k -> new HashMap<>()).put(questId, quest);
        return quest;
    }
    
    /**
     * Completes a friendship quest.
     */
    public static boolean completeQuest(ServerPlayer player, String questId) {
        Map<String, FriendshipQuest> quests = activeQuests.get(player.getUUID());
        if (quests == null) {
            return false;
        }
        
        FriendshipQuest quest = quests.get(questId);
        if (quest == null || quest.completed()) {
            return false;
        }
        
        // Mark as completed
        quests.put(questId, new FriendshipQuest(
            quest.questId(),
            quest.targetPlayerId(),
            quest.title(),
            quest.description(),
            quest.type(),
            quest.rewardPoints(),
            quest.startTime(),
            quest.expiryTime(),
            true
        ));
        
        // Award points
        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            ServerPlayer target = serverLevel.getServer().getPlayerList().getPlayer(quest.targetPlayerId());
            if (target != null) {
                addFriendshipPoints(player, target, quest.rewardPoints());
            }
        }
        
        return true;
    }
    
    /**
     * Gets active friendship quests for a player.
     */
    public static Collection<FriendshipQuest> getActiveQuests(Player player) {
        Map<String, FriendshipQuest> quests = activeQuests.get(player.getUUID());
        if (quests == null) {
            return List.of();
        }
        return quests.values().stream()
            .filter(q -> !q.completed() && System.currentTimeMillis() < q.expiryTime())
            .toList();
    }
}






