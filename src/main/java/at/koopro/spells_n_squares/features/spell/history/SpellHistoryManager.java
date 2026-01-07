package at.koopro.spells_n_squares.features.spell.history;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages spell history for players.
 * Tracks recently used spells for quick access and statistics.
 */
public final class SpellHistoryManager {
    
    private static final Map<UUID, List<SpellHistoryEntry>> PLAYER_HISTORY = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY_SIZE = 20;
    
    private SpellHistoryManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a spell history entry.
     */
    public record SpellHistoryEntry(
        Identifier spellId,
        long timestamp,
        boolean successful,
        String context
    ) {}
    
    /**
     * Records a spell cast in history.
     * 
     * @param playerId The player's UUID
     * @param spellId The spell ID
     * @param timestamp The timestamp (game tick)
     * @param successful Whether the cast was successful
     * @param context Optional context information
     */
    public static void recordCast(UUID playerId, Identifier spellId, long timestamp, 
                                  boolean successful, String context) {
        if (playerId == null || spellId == null) {
            return;
        }
        
        List<SpellHistoryEntry> history = PLAYER_HISTORY.computeIfAbsent(playerId, k -> new ArrayList<>());
        history.add(new SpellHistoryEntry(spellId, timestamp, successful, context != null ? context : ""));
        
        // Keep only recent entries
        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        
        DevLogger.logStateChange(SpellHistoryManager.class, "recordCast",
            "Player: " + playerId + ", Spell: " + spellId + ", History size: " + history.size());
    }
    
    /**
     * Gets recent spell history for a player.
     * 
     * @param playerId The player's UUID
     * @param count Number of recent entries to return
     * @return List of recent spell history entries (most recent first)
     */
    public static List<SpellHistoryEntry> getRecentHistory(UUID playerId, int count) {
        if (playerId == null) {
            return Collections.emptyList();
        }
        
        List<SpellHistoryEntry> history = PLAYER_HISTORY.get(playerId);
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }
        
        int startIndex = Math.max(0, history.size() - count);
        List<SpellHistoryEntry> recent = new ArrayList<>(history.subList(startIndex, history.size()));
        Collections.reverse(recent); // Most recent first
        return recent;
    }
    
    /**
     * Gets all spell history for a player.
     * 
     * @param playerId The player's UUID
     * @return List of all spell history entries
     */
    public static List<SpellHistoryEntry> getAllHistory(UUID playerId) {
        if (playerId == null) {
            return Collections.emptyList();
        }
        
        List<SpellHistoryEntry> history = PLAYER_HISTORY.get(playerId);
        return history != null ? new ArrayList<>(history) : Collections.emptyList();
    }
    
    /**
     * Gets the most frequently used spells for a player.
     * 
     * @param playerId The player's UUID
     * @param count Number of top spells to return
     * @return Map of spell ID to usage count, sorted by frequency
     */
    public static Map<Identifier, Integer> getMostUsedSpells(UUID playerId, int count) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        
        List<SpellHistoryEntry> history = PLAYER_HISTORY.get(playerId);
        if (history == null || history.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<Identifier, Integer> usageCount = new HashMap<>();
        for (SpellHistoryEntry entry : history) {
            usageCount.merge(entry.spellId(), 1, Integer::sum);
        }
        
        // Sort by frequency and return top N
        return usageCount.entrySet().stream()
            .sorted(Map.Entry.<Identifier, Integer>comparingByValue().reversed())
            .limit(count)
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
    
    /**
     * Clears history for a player.
     * 
     * @param playerId The player's UUID
     */
    public static void clearHistory(UUID playerId) {
        if (playerId != null) {
            PLAYER_HISTORY.remove(playerId);
        }
    }
    
    /**
     * Gets statistics for a player's spell usage.
     * 
     * @param playerId The player's UUID
     * @return Spell usage statistics
     */
    public static SpellUsageStats getStats(UUID playerId) {
        if (playerId == null) {
            return new SpellUsageStats(0, 0, 0, Collections.emptyMap());
        }
        
        List<SpellHistoryEntry> history = PLAYER_HISTORY.get(playerId);
        if (history == null || history.isEmpty()) {
            return new SpellUsageStats(0, 0, 0, Collections.emptyMap());
        }
        
        int totalCasts = history.size();
        int successfulCasts = (int) history.stream().filter(SpellHistoryEntry::successful).count();
        int uniqueSpells = (int) history.stream().map(SpellHistoryEntry::spellId).distinct().count();
        
        Map<Identifier, Integer> spellCounts = new HashMap<>();
        for (SpellHistoryEntry entry : history) {
            spellCounts.merge(entry.spellId(), 1, Integer::sum);
        }
        
        return new SpellUsageStats(totalCasts, successfulCasts, uniqueSpells, spellCounts);
    }
    
    /**
     * Represents spell usage statistics.
     */
    public record SpellUsageStats(
        int totalCasts,
        int successfulCasts,
        int uniqueSpells,
        Map<Identifier, Integer> spellCounts
    ) {}
}

