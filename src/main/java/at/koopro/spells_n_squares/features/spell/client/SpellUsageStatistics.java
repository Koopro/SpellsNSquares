package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Tracks spell usage statistics (client-side).
 * Records how many times each spell has been used.
 */
public final class SpellUsageStatistics {
    private static final Map<Identifier, Integer> usageCounts = CollectionFactory.createMap();
    
    private SpellUsageStatistics() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Records that a spell was used.
     */
    public static void recordSpellUsed(Identifier spellId) {
        if (spellId == null) {
            return;
        }
        usageCounts.put(spellId, usageCounts.getOrDefault(spellId, 0) + 1);
    }
    
    /**
     * Gets the usage count for a spell.
     */
    public static int getUsageCount(Identifier spellId) {
        if (spellId == null) {
            return 0;
        }
        return usageCounts.getOrDefault(spellId, 0);
    }
    
    /**
     * Gets all usage statistics.
     */
    public static Map<Identifier, Integer> getAllUsageCounts() {
        Map<Identifier, Integer> result = CollectionFactory.createMap();
        result.putAll(usageCounts);
        return result;
    }
    
    /**
     * Clears all usage statistics.
     */
    public static void clear() {
        usageCounts.clear();
    }
}

