package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import net.minecraft.resources.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Manages tracking of recently used spells.
 * Maintains a queue of the last N spells used.
 */
public final class RecentSpellsManager {
    private static final int MAX_RECENT_SPELLS = 10;
    private static final Queue<Identifier> recentSpells = new LinkedList<>();
    
    private RecentSpellsManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Records a spell as recently used.
     * Adds it to the front of the recent list, removing duplicates and maintaining max size.
     */
    public static void recordSpellUsed(Identifier spellId) {
        if (spellId == null) {
            return;
        }
        
        // Remove if already in list (to move to front)
        recentSpells.remove(spellId);
        
        // Add to front
        recentSpells.offer(spellId);
        
        // Maintain max size
        while (recentSpells.size() > MAX_RECENT_SPELLS) {
            recentSpells.poll(); // Remove oldest
        }
    }
    
    /**
     * Gets the list of recently used spells (newest first).
     */
    public static List<Identifier> getRecentSpells() {
        return CollectionFactory.createListFrom(recentSpells);
    }
    
    /**
     * Clears the recent spells list.
     */
    public static void clear() {
        recentSpells.clear();
    }
    
    /**
     * Gets the maximum number of recent spells to track.
     */
    public static int getMaxRecentSpells() {
        return MAX_RECENT_SPELLS;
    }
}


