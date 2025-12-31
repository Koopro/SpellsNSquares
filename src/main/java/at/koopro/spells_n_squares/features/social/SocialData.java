package at.koopro.spells_n_squares.features.social;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Data component for storing social information (friendships and reputation).
 */
public final class SocialData {
    private SocialData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SocialComponent>> SOCIAL_DATA =
        DATA_COMPONENTS.register(
            "social_data",
            () -> DataComponentType.<SocialComponent>builder()
                .persistent(SocialComponent.CODEC)
                .build()
        );
    
    /**
     * Friendship status.
     */
    public enum FriendshipStatus {
        NONE,
        PENDING_REQUEST_SENT,
        PENDING_REQUEST_RECEIVED,
        FRIENDS
    }
    
    /**
     * Component storing social information.
     */
    public record SocialComponent(
        Set<UUID> friends,
        Set<UUID> pendingSentRequests,
        Set<UUID> pendingReceivedRequests,
        Map<UUID, Integer> reputation,
        Map<String, Integer> npcReputation
    ) {
        public static final Codec<SocialComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(UUIDUtil.CODEC).xmap(l -> new HashSet<>(l), s -> new ArrayList<>(s)).fieldOf("friends").forGetter(c -> new HashSet<>(c.friends())),
                Codec.list(UUIDUtil.CODEC).xmap(l -> new HashSet<>(l), s -> new ArrayList<>(s)).optionalFieldOf("pendingSentRequests", new HashSet<>()).forGetter(c -> new HashSet<>(c.pendingSentRequests())),
                Codec.list(UUIDUtil.CODEC).xmap(l -> new HashSet<>(l), s -> new ArrayList<>(s)).optionalFieldOf("pendingReceivedRequests", new HashSet<>()).forGetter(c -> new HashSet<>(c.pendingReceivedRequests())),
                Codec.unboundedMap(UUIDUtil.CODEC, Codec.INT).optionalFieldOf("reputation", Map.of()).forGetter(SocialComponent::reputation),
                Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("npcReputation", Map.of()).forGetter(SocialComponent::npcReputation)
            ).apply(instance, SocialComponent::new)
        );
        
        public SocialComponent() {
            this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(), new HashMap<>());
        }
        
        public FriendshipStatus getFriendshipStatus(UUID playerId) {
            if (friends.contains(playerId)) {
                return FriendshipStatus.FRIENDS;
            }
            if (pendingSentRequests.contains(playerId)) {
                return FriendshipStatus.PENDING_REQUEST_SENT;
            }
            if (pendingReceivedRequests.contains(playerId)) {
                return FriendshipStatus.PENDING_REQUEST_RECEIVED;
            }
            return FriendshipStatus.NONE;
        }
        
        public SocialComponent addFriend(UUID playerId) {
            Set<UUID> newFriends = new HashSet<>(friends);
            newFriends.add(playerId);
            Set<UUID> newSent = new HashSet<>(pendingSentRequests);
            newSent.remove(playerId);
            Set<UUID> newReceived = new HashSet<>(pendingReceivedRequests);
            newReceived.remove(playerId);
            return new SocialComponent(newFriends, newSent, newReceived, reputation, npcReputation);
        }
        
        public SocialComponent sendFriendRequest(UUID playerId) {
            Set<UUID> newSent = new HashSet<>(pendingSentRequests);
            newSent.add(playerId);
            return new SocialComponent(friends, newSent, pendingReceivedRequests, reputation, npcReputation);
        }
        
        public SocialComponent receiveFriendRequest(UUID playerId) {
            Set<UUID> newReceived = new HashSet<>(pendingReceivedRequests);
            newReceived.add(playerId);
            return new SocialComponent(friends, pendingSentRequests, newReceived, reputation, npcReputation);
        }
        
        public SocialComponent removeFriend(UUID playerId) {
            Set<UUID> newFriends = new HashSet<>(friends);
            newFriends.remove(playerId);
            return new SocialComponent(newFriends, pendingSentRequests, pendingReceivedRequests, reputation, npcReputation);
        }
        
        public SocialComponent changeReputation(UUID playerId, int delta) {
            Map<UUID, Integer> newReputation = new HashMap<>(reputation);
            newReputation.put(playerId, newReputation.getOrDefault(playerId, 0) + delta);
            return new SocialComponent(friends, pendingSentRequests, pendingReceivedRequests, newReputation, npcReputation);
        }
        
        public SocialComponent changeNpcReputation(String npcId, int delta) {
            Map<String, Integer> newNpcReputation = new HashMap<>(npcReputation);
            newNpcReputation.put(npcId, newNpcReputation.getOrDefault(npcId, 0) + delta);
            return new SocialComponent(friends, pendingSentRequests, pendingReceivedRequests, reputation, newNpcReputation);
        }
        
        public int getReputation(UUID playerId) {
            return reputation.getOrDefault(playerId, 0);
        }
        
        public int getNpcReputation(String npcId) {
            return npcReputation.getOrDefault(npcId, 0);
        }
    }
}
















