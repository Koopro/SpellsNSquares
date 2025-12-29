package at.koopro.spells_n_squares.features.education;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;

/**
 * Data component for storing bestiary discovery data per player.
 */
public final class BestiaryData {
    private BestiaryData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BestiaryComponent>> BESTIARY_DATA =
        DATA_COMPONENTS.register("bestiary_data", () -> DataComponentType.<BestiaryComponent>builder()
            .persistent(BestiaryComponent.CODEC)
            .build());
    
    /**
     * Component storing discovered creature IDs.
     */
    public record BestiaryComponent(Set<Identifier> discoveredCreatures) {
        public static final Codec<BestiaryComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Identifier.CODEC)
                    .xmap(l -> new HashSet<>(l), s -> new java.util.ArrayList<>(s))
                    .fieldOf("discoveredCreatures")
                    .forGetter(c -> new HashSet<>(c.discoveredCreatures))
            ).apply(instance, BestiaryComponent::new)
        );
        
        public static BestiaryComponent createDefault() {
            return new BestiaryComponent(new HashSet<>());
        }
        
        /**
         * Checks if a creature has been discovered.
         */
        public boolean isDiscovered(Identifier creatureId) {
            return discoveredCreatures.contains(creatureId);
        }
        
        /**
         * Adds a creature to the discovered set.
         */
        public BestiaryComponent discoverCreature(Identifier creatureId) {
            Set<Identifier> newSet = new HashSet<>(discoveredCreatures);
            newSet.add(creatureId);
            return new BestiaryComponent(newSet);
        }
        
        /**
         * Gets the number of discovered creatures.
         */
        public int getDiscoveredCount() {
            return discoveredCreatures.size();
        }
    }
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:bestiary_data";
    
    // Client-side cache for bestiary data (synced from server)
    private static final java.util.Map<java.util.UUID, BestiaryComponent> clientCache = new java.util.HashMap<>();
    
    /**
     * Gets bestiary data for a player from their persistent data.
     * On client, uses cached data (should be synced from server via network packets).
     */
    public static BestiaryComponent getBestiaryData(Player player) {
        if (player.level().isClientSide()) {
            // On client, use cache (in a real implementation, this would sync from server)
            // For now, try to read from persistent data if available
            var persistentData = player.getPersistentData();
            var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
            
            if (!tagOpt.isEmpty()) {
                var tag = tagOpt.get();
                if (!tag.isEmpty()) {
                    try {
                        BestiaryComponent component = BestiaryComponent.CODEC.parse(
                            net.minecraft.nbt.NbtOps.INSTANCE,
                            tag
                        ).result().orElse(BestiaryComponent.createDefault());
                        // Cache it
                        clientCache.put(player.getUUID(), component);
                        return component;
                    } catch (Exception e) {
                        // Fall through to cache/default
                    }
                }
            }
            
            // Use cache if available
            return clientCache.getOrDefault(player.getUUID(), BestiaryComponent.createDefault());
        }
        
        // Server-side: read from persistent data
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return BestiaryComponent.createDefault();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return BestiaryComponent.createDefault();
        }
        
        try {
            return BestiaryComponent.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(BestiaryComponent.createDefault());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load bestiary data for player {}, using default", player.getName().getString(), e);
            return BestiaryComponent.createDefault();
        }
    }
    
    /**
     * Updates client-side cache (called when data syncs from server).
     */
    public static void updateClientCache(java.util.UUID playerId, BestiaryComponent data) {
        clientCache.put(playerId, data);
    }
    
    /**
     * Sets bestiary data for a player in their persistent data.
     */
    public static void setBestiaryData(Player player, BestiaryComponent data) {
        if (player.level().isClientSide()) {
            return; // Only set on server
        }
        
        try {
            var result = BestiaryComponent.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save bestiary data for player {}", player.getName().getString(), e);
        }
    }
    
    /**
     * Checks if a player has discovered a creature.
     * TODO: For now, always returns true (all creatures unlocked).
     * Discovery mechanism will be implemented later.
     */
    public static boolean hasDiscovered(Player player, Identifier creatureId) {
        // TODO: Re-enable discovery check later
        // BestiaryComponent component = getBestiaryData(player);
        // return component.isDiscovered(creatureId);
        return true; // Always show all creatures for now
    }
    
    /**
     * Discovers a creature for a player.
     */
    public static void discoverCreature(Player player, Identifier creatureId) {
        BestiaryComponent component = getBestiaryData(player);
        if (!component.isDiscovered(creatureId)) {
            BestiaryComponent updated = component.discoverCreature(creatureId);
            setBestiaryData(player, updated);
        }
    }
    
    /**
     * Discovers all creatures for a player (debug/testing).
     */
    public static void discoverAllCreatures(Player player) {
        BestiaryCreatureRegistry.initialize();
        Set<Identifier> allCreatureIds = BestiaryCreatureRegistry.getAllCreatureIds();
        BestiaryComponent component = getBestiaryData(player);
        
        // Add all creatures to the discovered set
        Set<Identifier> newSet = new HashSet<>(component.discoveredCreatures());
        newSet.addAll(allCreatureIds);
        
        BestiaryComponent updated = new BestiaryComponent(newSet);
        setBestiaryData(player, updated);
    }
}













