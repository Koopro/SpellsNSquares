package at.koopro.spells_n_squares.features.social;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Tracks player reputation with NPCs and other players.
 * Reputation affects NPC interactions, shop prices, and quest availability.
 */
public final class ReputationSystem {
    private ReputationSystem() {
    }
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:social_data";
    
    /**
     * Gets social data for a player from their persistent data component.
     * Uses the same key as FriendshipSystem since both use SocialData.SocialComponent.
     */
    private static SocialData.SocialComponent getSocialData(Player player) {
        if (player.level().isClientSide()) {
            // On client, return default (data syncs from server)
            return new SocialData.SocialComponent();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return new SocialData.SocialComponent();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return new SocialData.SocialComponent();
        }
        
        try {
            return SocialData.SocialComponent.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(new SocialData.SocialComponent());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load social data for player {}, using default", player.getName().getString(), e);
            return new SocialData.SocialComponent();
        }
    }
    
    /**
     * Sets social data for a player in their persistent data component.
     * Uses the same key as FriendshipSystem since both use SocialData.SocialComponent.
     */
    private static void setSocialData(Player player, SocialData.SocialComponent data) {
        if (player.level().isClientSide()) {
            return; // Only set on server
        }
        
        try {
            var result = SocialData.SocialComponent.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save social data for player {}", player.getName().getString(), e);
        }
    }
    
    /**
     * Changes reputation between two players.
     * 
     * @param player The player whose reputation is being changed
     * @param otherPlayerId The other player's UUID
     * @param delta The change amount (positive for increase, negative for decrease)
     */
    public static void changePlayerReputation(Player player, UUID otherPlayerId, int delta) {
        SocialData.SocialComponent data = getSocialData(player);
        data = data.changeReputation(otherPlayerId, delta);
        setSocialData(player, data);
    }
    
    /**
     * Changes reputation with an NPC.
     * 
     * @param player The player whose reputation is being changed
     * @param npcId The NPC identifier
     * @param delta The change amount
     */
    public static void changeNpcReputation(Player player, String npcId, int delta) {
        SocialData.SocialComponent data = getSocialData(player);
        data = data.changeNpcReputation(npcId, delta);
        setSocialData(player, data);
    }
    
    /**
     * Gets reputation with another player.
     */
    public static int getPlayerReputation(Player player, UUID otherPlayerId) {
        SocialData.SocialComponent data = getSocialData(player);
        return data.getReputation(otherPlayerId);
    }
    
    /**
     * Gets reputation with an NPC.
     */
    public static int getNpcReputation(Player player, String npcId) {
        SocialData.SocialComponent data = getSocialData(player);
        return data.getNpcReputation(npcId);
    }
    
    /**
     * Gets all player reputations for a player.
     */
    public static Map<UUID, Integer> getAllPlayerReputations(Player player) {
        SocialData.SocialComponent data = getSocialData(player);
        return Map.copyOf(data.reputation());
    }
    
    /**
     * Gets all NPC reputations for a player.
     */
    public static Map<String, Integer> getAllNpcReputations(Player player) {
        SocialData.SocialComponent data = getSocialData(player);
        return Map.copyOf(data.npcReputation());
    }
    
    /**
     * Calculates a reputation modifier for shop prices.
     * Higher reputation = lower prices (better deals).
     * 
     * @param reputation The reputation value
     * @return A multiplier (0.5 to 1.5, where 1.0 is neutral)
     */
    public static float getPriceModifier(int reputation) {
        // Reputation ranges from -100 to +100
        // At -100: 1.5x price (worst)
        // At 0: 1.0x price (neutral)
        // At +100: 0.5x price (best)
        float normalized = (float) reputation / 100.0f;
        normalized = Math.max(-1.0f, Math.min(1.0f, normalized)); // Clamp to [-1, 1]
        return 1.0f - (normalized * 0.5f); // Map to [0.5, 1.5]
    }
    
    /**
     * Checks if a quest is available based on reputation.
     * 
     * @param player The player
     * @param npcId The NPC offering the quest
     * @param requiredReputation The minimum reputation required
     * @return True if the quest is available
     */
    public static boolean isQuestAvailable(Player player, String npcId, int requiredReputation) {
        int reputation = getNpcReputation(player, npcId);
        return reputation >= requiredReputation;
    }
}













