package at.koopro.spells_n_squares.features.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Data component for storing spell slot assignments persistently.
 */
public final class SpellSlotData {
    private SpellSlotData() {
    }
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:spell_slots";
    
    /**
     * Component storing spell slot assignments.
     * Stores up to MAX_SLOTS spell IDs (can be null for empty slots).
     */
    public record SpellSlotComponent(
        List<Optional<Identifier>> slots
    ) {
        public static final Codec<SpellSlotComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Identifier.CODEC.optionalFieldOf("spellId").codec()).fieldOf("slots")
                    .forGetter(SpellSlotComponent::slots)
            ).apply(instance, SpellSlotComponent::new)
        );
        
        /**
         * Creates a new component with empty slots.
         */
        public SpellSlotComponent() {
            this(new ArrayList<>(Arrays.asList(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
            )));
        }
        
        /**
         * Creates a component from an array of spell IDs.
         */
        public static SpellSlotComponent fromArray(Identifier[] spellIds) {
            List<Optional<Identifier>> slots = new ArrayList<>();
            for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
                slots.add(i < spellIds.length && spellIds[i] != null 
                    ? Optional.of(spellIds[i]) 
                    : Optional.empty());
            }
            return new SpellSlotComponent(slots);
        }
        
        /**
         * Converts to an array of spell IDs.
         */
        public Identifier[] toArray() {
            Identifier[] result = new Identifier[SpellManager.MAX_SLOTS];
            for (int i = 0; i < SpellManager.MAX_SLOTS && i < slots.size(); i++) {
                result[i] = slots.get(i).orElse(null);
            }
            return result;
        }
        
        /**
         * Sets a spell in a specific slot.
         */
        public SpellSlotComponent withSpellInSlot(int slot, Identifier spellId) {
            if (slot < 0 || slot >= SpellManager.MAX_SLOTS) {
                return this;
            }
            
            List<Optional<Identifier>> newSlots = new ArrayList<>(slots);
            while (newSlots.size() < SpellManager.MAX_SLOTS) {
                newSlots.add(Optional.empty());
            }
            
            newSlots.set(slot, spellId != null ? Optional.of(spellId) : Optional.empty());
            return new SpellSlotComponent(newSlots);
        }
        
        /**
         * Gets the spell in a specific slot.
         */
        public Optional<Identifier> getSpellInSlot(int slot) {
            if (slot < 0 || slot >= slots.size()) {
                return Optional.empty();
            }
            return slots.get(slot);
        }
    }
    
    /**
     * Gets spell slot data for a player from their persistent data.
     */
    public static SpellSlotComponent getSpellSlotData(Player player) {
        if (player.level().isClientSide()) {
            // On client, return default (data syncs from server)
            return new SpellSlotComponent();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return new SpellSlotComponent();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return new SpellSlotComponent();
        }
        
        try {
            return SpellSlotComponent.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(new SpellSlotComponent());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load spell slots for player {}, using default", player.getName().getString(), e);
            return new SpellSlotComponent();
        }
    }
    
    /**
     * Sets spell slot data for a player in their persistent data.
     */
    public static void setSpellSlotData(Player player, SpellSlotComponent data) {
        if (player.level().isClientSide()) {
            return; // Only set on server
        }
        
        try {
            var result = SpellSlotComponent.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save spell slots for player {}", player.getName().getString(), e);
        }
    }
    
    /**
     * Checks if a player has any saved spell slots.
     */
    public static boolean hasSavedSlots(Player player) {
        if (player.level().isClientSide()) {
            return false;
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return false;
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return false;
        }
        
        // Check if any slots are filled
        SpellSlotComponent data = getSpellSlotData(player);
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            if (data.getSpellInSlot(i).isPresent()) {
                return true;
            }
        }
        
        return false;
    }
}

