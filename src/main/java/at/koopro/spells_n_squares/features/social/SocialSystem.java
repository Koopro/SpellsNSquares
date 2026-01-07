package at.koopro.spells_n_squares.features.social;

import at.koopro.spells_n_squares.core.data.DataComponentHelper;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Social system for managing friendships, reputation, and social interactions.
 * Provides friendship progression, reputation tracking, and social quest management.
 */
public final class SocialSystem {
    
    private static final String DATA_KEY = "spells_n_squares:social_data";
    
    private SocialSystem() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Friendship level enumeration.
     */
    public enum FriendshipLevel {
        STRANGER(0, "Stranger"),
        ACQUAINTANCE(100, "Acquaintance"),
        FRIEND(300, "Friend"),
        CLOSE_FRIEND(600, "Close Friend"),
        BEST_FRIEND(1000, "Best Friend");
        
        private final int requiredPoints;
        private final String displayName;
        
        FriendshipLevel(int requiredPoints, String displayName) {
            this.requiredPoints = requiredPoints;
            this.displayName = displayName;
        }
        
        public int getRequiredPoints() {
            return requiredPoints;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static FriendshipLevel fromPoints(int points) {
            FriendshipLevel[] levels = values();
            for (int i = levels.length - 1; i >= 0; i--) {
                if (points >= levels[i].requiredPoints) {
                    return levels[i];
                }
            }
            return STRANGER;
        }
    }
    
    /**
     * Represents friendship data between two players.
     */
    public record FriendshipData(
        UUID friendId,
        String friendName,
        int friendshipPoints,
        long lastInteractionTime
    ) {
        public static final Codec<FriendshipData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                net.minecraft.core.UUIDUtil.CODEC.fieldOf("friendId").forGetter(FriendshipData::friendId),
                Codec.STRING.fieldOf("friendName").forGetter(FriendshipData::friendName),
                Codec.INT.fieldOf("friendshipPoints").forGetter(FriendshipData::friendshipPoints),
                Codec.LONG.fieldOf("lastInteractionTime").forGetter(FriendshipData::lastInteractionTime)
            ).apply(instance, FriendshipData::new)
        );
        
        public FriendshipLevel getLevel() {
            return FriendshipLevel.fromPoints(friendshipPoints);
        }
    }
    
    /**
     * Represents reputation data for a player.
     */
    public record ReputationData(
        UUID playerId,
        String playerName,
        int reputation,
        Map<String, Integer> factionReputation
    ) {
        public static final Codec<ReputationData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                net.minecraft.core.UUIDUtil.CODEC.fieldOf("playerId").forGetter(ReputationData::playerId),
                Codec.STRING.fieldOf("playerName").forGetter(ReputationData::playerName),
                Codec.INT.fieldOf("reputation").forGetter(ReputationData::reputation),
                Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .fieldOf("factionReputation").forGetter(ReputationData::factionReputation)
            ).apply(instance, ReputationData::new)
        );
        
        public static ReputationData create(UUID playerId, String playerName) {
            return new ReputationData(playerId, playerName, 0, new HashMap<>());
        }
    }
    
    /**
     * Container for all social data for a player.
     */
    public record SocialData(
        Map<UUID, FriendshipData> friendships,
        ReputationData reputation,
        List<UUID> blockedPlayers
    ) {
        public static final Codec<SocialData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.unboundedMap(net.minecraft.core.UUIDUtil.CODEC, FriendshipData.CODEC)
                    .fieldOf("friendships").forGetter(SocialData::friendships),
                ReputationData.CODEC.fieldOf("reputation").forGetter(SocialData::reputation),
                Codec.list(net.minecraft.core.UUIDUtil.CODEC).fieldOf("blockedPlayers").forGetter(SocialData::blockedPlayers)
            ).apply(instance, SocialData::new)
        );
        
        public static SocialData empty(UUID playerId, String playerName) {
            return new SocialData(
                new HashMap<>(),
                ReputationData.create(playerId, playerName),
                new ArrayList<>()
            );
        }
    }
    
    /**
     * Gets social data for a player.
     */
    public static SocialData getSocialData(ServerPlayer player) {
        if (player == null) {
            return null;
        }
        
        return DataComponentHelper.get(
            player, DATA_KEY, SocialData.CODEC,
            () -> SocialData.empty(player.getUUID(), player.getName().getString()));
    }
    
    /**
     * Updates social data for a player.
     */
    public static void setSocialData(ServerPlayer player, SocialData data) {
        if (player == null || data == null) {
            return;
        }
        
        DataComponentHelper.set(player, DATA_KEY, SocialData.CODEC, data);
    }
    
    /**
     * Adds friendship points between two players.
     * 
     * @param player The player
     * @param friendId The friend's UUID
     * @param friendName The friend's name
     * @param points The points to add (can be negative)
     */
    public static void addFriendshipPoints(ServerPlayer player, UUID friendId, String friendName, int points) {
        if (player == null || friendId == null) {
            return;
        }
        
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            socialData = SocialData.empty(player.getUUID(), player.getName().getString());
        }
        
        Map<UUID, FriendshipData> friendships = new HashMap<>(socialData.friendships());
        FriendshipData friendship = friendships.getOrDefault(friendId,
            new FriendshipData(friendId, friendName, 0, System.currentTimeMillis()));
        
        int newPoints = Math.max(0, friendship.friendshipPoints() + points);
        FriendshipLevel oldLevel = friendship.getLevel();
        FriendshipLevel newLevel = FriendshipLevel.fromPoints(newPoints);
        
        FriendshipData updated = new FriendshipData(
            friendId,
            friendName,
            newPoints,
            System.currentTimeMillis()
        );
        
        friendships.put(friendId, updated);
        
        SocialData updatedSocialData = new SocialData(
            friendships,
            socialData.reputation(),
            socialData.blockedPlayers()
        );
        
        setSocialData(player, updatedSocialData);
        
        // Notify if level changed
        if (oldLevel != newLevel) {
            DevLogger.logStateChange(SocialSystem.class, "addFriendshipPoints",
                "Friendship level changed: " + oldLevel + " -> " + newLevel);
        }
    }
    
    /**
     * Gets friendship data for a specific friend.
     */
    public static Optional<FriendshipData> getFriendship(ServerPlayer player, UUID friendId) {
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(socialData.friendships().get(friendId));
    }
    
    /**
     * Gets all friendships for a player.
     */
    public static List<FriendshipData> getFriendships(ServerPlayer player) {
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            return Collections.emptyList();
        }
        
        return new ArrayList<>(socialData.friendships().values());
    }
    
    /**
     * Updates reputation for a player.
     * 
     * @param player The player
     * @param points The reputation points to add (can be negative)
     */
    public static void updateReputation(ServerPlayer player, int points) {
        if (player == null) {
            return;
        }
        
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            socialData = SocialData.empty(player.getUUID(), player.getName().getString());
        }
        
        ReputationData reputation = socialData.reputation();
        int newReputation = reputation.reputation() + points;
        
        ReputationData updatedReputation = new ReputationData(
            reputation.playerId(),
            reputation.playerName(),
            newReputation,
            reputation.factionReputation()
        );
        
        SocialData updatedSocialData = new SocialData(
            socialData.friendships(),
            updatedReputation,
            socialData.blockedPlayers()
        );
        
        setSocialData(player, updatedSocialData);
        
        DevLogger.logStateChange(SocialSystem.class, "updateReputation",
            "Updated reputation: " + reputation.reputation() + " -> " + newReputation);
    }
    
    /**
     * Updates faction reputation for a player.
     * 
     * @param player The player
     * @param faction The faction name
     * @param points The reputation points to add
     */
    public static void updateFactionReputation(ServerPlayer player, String faction, int points) {
        if (player == null || faction == null) {
            return;
        }
        
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            socialData = SocialData.empty(player.getUUID(), player.getName().getString());
        }
        
        ReputationData reputation = socialData.reputation();
        Map<String, Integer> factionRep = new HashMap<>(reputation.factionReputation());
        factionRep.merge(faction, points, Integer::sum);
        
        ReputationData updatedReputation = new ReputationData(
            reputation.playerId(),
            reputation.playerName(),
            reputation.reputation(),
            factionRep
        );
        
        SocialData updatedSocialData = new SocialData(
            socialData.friendships(),
            updatedReputation,
            socialData.blockedPlayers()
        );
        
        setSocialData(player, updatedSocialData);
    }
    
    /**
     * Gets reputation for a player.
     */
    public static int getReputation(ServerPlayer player) {
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            return 0;
        }
        
        return socialData.reputation().reputation();
    }
    
    /**
     * Gets faction reputation for a player.
     */
    public static int getFactionReputation(ServerPlayer player, String faction) {
        SocialData socialData = getSocialData(player);
        if (socialData == null) {
            return 0;
        }
        
        return socialData.reputation().factionReputation().getOrDefault(faction, 0);
    }
}

