package at.koopro.spells_n_squares.modules.spell.internal;

import at.koopro.spells_n_squares.features.spell.SpellSlotData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Complete spell data for a player.
 * Includes slots, learned spells, cooldowns, active hold spell, mastery, and combo history.
 */
public record SpellData(
    SpellSlotData.SpellSlotComponent slots,
    Set<Identifier> learnedSpells,
    Map<Identifier, Integer> cooldowns,  // spell ID -> remaining ticks
    Optional<Identifier> activeHoldSpell,
    Map<Identifier, Integer> masteryUses,  // spell ID -> number of times cast
    List<Identifier> recentCasts  // Recent spell casts for combo detection (max 5)
) {
    public static final Codec<SpellData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            SpellSlotData.SpellSlotComponent.CODEC
                .optionalFieldOf("slots", new SpellSlotData.SpellSlotComponent())
                .forGetter(SpellData::slots),
            Codec.list(Identifier.CODEC)
                .xmap(Set::copyOf, ArrayList::new)
                .optionalFieldOf("learnedSpells", Set.of())
                .forGetter(SpellData::learnedSpells),
            Codec.unboundedMap(Identifier.CODEC, Codec.INT)
                .optionalFieldOf("cooldowns", Map.of())
                .forGetter(SpellData::cooldowns),
            Identifier.CODEC.optionalFieldOf("activeHoldSpell")
                .forGetter(SpellData::activeHoldSpell),
            Codec.unboundedMap(Identifier.CODEC, Codec.INT)
                .optionalFieldOf("masteryUses", Map.of())
                .forGetter(SpellData::masteryUses),
            Codec.list(Identifier.CODEC)
                .optionalFieldOf("recentCasts", List.of())
                .forGetter(SpellData::recentCasts)
        ).apply(instance, SpellData::new)
    );
    
    /**
     * Creates default empty spell data.
     */
    public static SpellData empty() {
        return new SpellData(
            new SpellSlotData.SpellSlotComponent(),
            Set.of(),
            Map.of(),
            Optional.empty(),
            Map.of(),
            List.of()
        );
    }
    
    /**
     * Updates the spell slots.
     */
    public SpellData withSlots(SpellSlotData.SpellSlotComponent slots) {
        return new SpellData(slots, learnedSpells, cooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Adds a learned spell.
     */
    public SpellData withLearnedSpell(Identifier spellId) {
        Set<Identifier> newLearned = new HashSet<>(learnedSpells);
        newLearned.add(spellId);
        return new SpellData(slots, newLearned, cooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Removes a learned spell.
     */
    public SpellData withoutLearnedSpell(Identifier spellId) {
        Set<Identifier> newLearned = new HashSet<>(learnedSpells);
        newLearned.remove(spellId);
        return new SpellData(slots, newLearned, cooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Updates cooldowns.
     */
    public SpellData withCooldowns(Map<Identifier, Integer> cooldowns) {
        return new SpellData(slots, learnedSpells, cooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Updates a single cooldown.
     */
    public SpellData withCooldown(Identifier spellId, int ticks) {
        Map<Identifier, Integer> newCooldowns = new HashMap<>(cooldowns);
        if (ticks > 0) {
            newCooldowns.put(spellId, ticks);
        } else {
            newCooldowns.remove(spellId);
        }
        return new SpellData(slots, learnedSpells, newCooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Updates a spell in a specific slot.
     */
    public SpellData withSpellInSlot(int slot, Identifier spellId) {
        SpellSlotData.SpellSlotComponent newSlots = slots.withSpellInSlot(slot, spellId);
        return new SpellData(newSlots, learnedSpells, cooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Sets the active hold spell.
     */
    public SpellData withActiveHoldSpell(Identifier spellId) {
        return new SpellData(slots, learnedSpells, cooldowns, Optional.of(spellId), masteryUses, recentCasts);
    }
    
    /**
     * Clears the active hold spell.
     */
    public SpellData withoutActiveHoldSpell() {
        return new SpellData(slots, learnedSpells, cooldowns, Optional.empty(), masteryUses, recentCasts);
    }
    
    /**
     * Updates mastery uses.
     */
    public SpellData withMasteryUses(Map<Identifier, Integer> masteryUses) {
        return new SpellData(slots, learnedSpells, cooldowns, activeHoldSpell, masteryUses, recentCasts);
    }
    
    /**
     * Adds a spell to the recent casts history for combo detection.
     * Maintains a maximum of 5 recent casts.
     */
    public SpellData withRecentCast(Identifier spellId) {
        List<Identifier> newRecentCasts = new ArrayList<>(recentCasts);
        newRecentCasts.add(spellId);
        // Keep only the last 5 casts
        if (newRecentCasts.size() > 5) {
            newRecentCasts.remove(0);
        }
        return new SpellData(slots, learnedSpells, cooldowns, activeHoldSpell, masteryUses, newRecentCasts);
    }
    
    /**
     * Clears the recent casts history.
     */
    public SpellData withoutRecentCasts() {
        return new SpellData(slots, learnedSpells, cooldowns, activeHoldSpell, masteryUses, List.of());
    }
}

