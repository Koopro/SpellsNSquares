package at.koopro.spells_n_squares.features.education;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    
    // Static storage for player bestiary data (UUID -> BestiaryComponent)
    private static final Map<UUID, BestiaryComponent> playerBestiaryData = new HashMap<>();
    
    /**
     * Gets bestiary data for a player.
     */
    public static BestiaryComponent getBestiaryData(Player player) {
        return playerBestiaryData.computeIfAbsent(player.getUUID(), uuid -> BestiaryComponent.createDefault());
    }
    
    /**
     * Sets bestiary data for a player.
     */
    public static void setBestiaryData(Player player, BestiaryComponent data) {
        playerBestiaryData.put(player.getUUID(), data);
    }
    
    /**
     * Checks if a player has discovered a creature.
     */
    public static boolean hasDiscovered(Player player, Identifier creatureId) {
        BestiaryComponent component = getBestiaryData(player);
        return component.isDiscovered(creatureId);
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
}











