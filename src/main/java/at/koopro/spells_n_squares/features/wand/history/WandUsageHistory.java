package at.koopro.spells_n_squares.features.wand.history;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks wand usage history and favorite spells for wands.
 */
public final class WandUsageHistory {
    
    private static final Map<String, WandHistoryData> WAND_HISTORY = new ConcurrentHashMap<>();
    
    private WandUsageHistory() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents wand usage history data.
     */
    public record WandHistoryData(
        String wandId,
        List<Identifier> recentSpells,
        Set<Identifier> favoriteSpells,
        Map<Identifier, Integer> spellUsageCounts,
        long lastUsedTimestamp
    ) {
        public WandHistoryData {
            if (recentSpells == null) {
                recentSpells = new ArrayList<>();
            }
            if (favoriteSpells == null) {
                favoriteSpells = new HashSet<>();
            }
            if (spellUsageCounts == null) {
                spellUsageCounts = new HashMap<>();
            }
        }
    }
    
    /**
     * Gets a unique identifier for a wand.
     * 
     * @param wand The wand item stack
     * @return Wand identifier, or null if invalid
     */
    private static String getWandId(ItemStack wand) {
        if (wand == null || wand.isEmpty()) {
            return null;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null) {
            return null;
        }
        
        return wandData.coreId() + "_" + wandData.woodId() + "_" + wandData.ownerId();
    }
    
    /**
     * Records a spell cast with a wand.
     * 
     * @param wand The wand used
     * @param spellId The spell cast
     * @param timestamp The timestamp
     */
    public static void recordSpellCast(ItemStack wand, Identifier spellId, long timestamp) {
        if (wand == null || wand.isEmpty() || spellId == null) {
            return;
        }
        
        String wandId = getWandId(wand);
        if (wandId == null) {
            return;
        }
        
        WandHistoryData history = WAND_HISTORY.computeIfAbsent(wandId, k -> 
            new WandHistoryData(wandId, new ArrayList<>(), new HashSet<>(), new HashMap<>(), 0));
        
        // Add to recent spells
        List<Identifier> recentSpells = new ArrayList<>(history.recentSpells());
        recentSpells.remove(spellId); // Remove if already present
        recentSpells.add(spellId);
        
        // Keep only last 10
        while (recentSpells.size() > 10) {
            recentSpells.remove(0);
        }
        
        // Update usage count
        Map<Identifier, Integer> usageCounts = new HashMap<>(history.spellUsageCounts());
        usageCounts.merge(spellId, 1, Integer::sum);
        
        // Update history
        WAND_HISTORY.put(wandId, new WandHistoryData(
            wandId, recentSpells, history.favoriteSpells(), usageCounts, timestamp
        ));
        
        DevLogger.logStateChange(WandUsageHistory.class, "recordSpellCast",
            "Wand: " + wandId + ", Spell: " + spellId);
    }
    
    /**
     * Toggles favorite status for a spell on a wand.
     * 
     * @param wand The wand
     * @param spellId The spell ID
     */
    public static void toggleFavorite(ItemStack wand, Identifier spellId) {
        if (wand == null || wand.isEmpty() || spellId == null) {
            return;
        }
        
        String wandId = getWandId(wand);
        if (wandId == null) {
            return;
        }
        
        WandHistoryData history = WAND_HISTORY.computeIfAbsent(wandId, k ->
            new WandHistoryData(wandId, new ArrayList<>(), new HashSet<>(), new HashMap<>(), 0));
        
        Set<Identifier> favorites = new HashSet<>(history.favoriteSpells());
        if (favorites.contains(spellId)) {
            favorites.remove(spellId);
        } else {
            favorites.add(spellId);
        }
        
        WAND_HISTORY.put(wandId, new WandHistoryData(
            wandId, history.recentSpells(), favorites, history.spellUsageCounts(), history.lastUsedTimestamp()
        ));
    }
    
    /**
     * Gets favorite spells for a wand.
     * 
     * @param wand The wand
     * @return Set of favorite spell IDs
     */
    public static Set<Identifier> getFavoriteSpells(ItemStack wand) {
        if (wand == null || wand.isEmpty()) {
            return Collections.emptySet();
        }
        
        String wandId = getWandId(wand);
        if (wandId == null) {
            return Collections.emptySet();
        }
        
        WandHistoryData history = WAND_HISTORY.get(wandId);
        return history != null ? new HashSet<>(history.favoriteSpells()) : Collections.emptySet();
    }
    
    /**
     * Gets recent spells for a wand.
     * 
     * @param wand The wand
     * @return List of recent spell IDs (most recent last)
     */
    public static List<Identifier> getRecentSpells(ItemStack wand) {
        if (wand == null || wand.isEmpty()) {
            return Collections.emptyList();
        }
        
        String wandId = getWandId(wand);
        if (wandId == null) {
            return Collections.emptyList();
        }
        
        WandHistoryData history = WAND_HISTORY.get(wandId);
        return history != null ? new ArrayList<>(history.recentSpells()) : Collections.emptyList();
    }
    
    /**
     * Gets the most used spells for a wand.
     * 
     * @param wand The wand
     * @param count Number of top spells to return
     * @return Map of spell ID to usage count
     */
    public static Map<Identifier, Integer> getMostUsedSpells(ItemStack wand, int count) {
        if (wand == null || wand.isEmpty()) {
            return Collections.emptyMap();
        }
        
        String wandId = getWandId(wand);
        if (wandId == null) {
            return Collections.emptyMap();
        }
        
        WandHistoryData history = WAND_HISTORY.get(wandId);
        if (history == null) {
            return Collections.emptyMap();
        }
        
        return history.spellUsageCounts().entrySet().stream()
            .sorted(Map.Entry.<Identifier, Integer>comparingByValue().reversed())
            .limit(count)
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
}

