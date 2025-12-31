package at.koopro.spells_n_squares.core.data;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class for data sanitization and automatic correction.
 * Provides common sanitization patterns for fixing invalid data.
 * 
 * <h2>Usage Patterns</h2>
 * 
 * <p>Sanitization automatically fixes invalid data by removing or correcting
 * problematic entries. This is typically used after validation to clean up
 * data that couldn't be automatically corrected.
 * 
 * <h3>Identifier Sanitization</h3>
 * 
 * <pre>{@code
 * // Remove invalid spell IDs from discovered spells
 * Set<Identifier> sanitized = DataSanitizationUtils.sanitizeIdentifiers(
 *     discoveredSpells,
 *     id -> SpellRegistry.get(id) != null,
 *     "discovered spells"
 * );
 * }</pre>
 * 
 * <h3>Currency Sanitization</h3>
 * 
 * <pre>{@code
 * // Ensure currency values are valid and within limits
 * int[] sanitized = DataSanitizationUtils.sanitizeCurrency(
 *     galleons, sickles, knuts,
 *     Integer.MAX_VALUE  // or a specific max limit
 * );
 * CurrencyDataComponent fixed = new CurrencyDataComponent(
 *     sanitized[0], sanitized[1], sanitized[2]
 * );
 * }</pre>
 * 
 * <h3>Map/List Sanitization</h3>
 * 
 * <pre>{@code
 * // Remove null entries from maps and lists
 * DataSanitizationUtils.sanitizeMap(assignments, "homework assignments");
 * DataSanitizationUtils.sanitizeList(spellSlots, "spell slots");
 * }</pre>
 * 
 * <h3>Combat Stats Sanitization</h3>
 * 
 * <pre>{@code
 * // Sanitize all combat stats at once
 * float[] sanitized = DataSanitizationUtils.sanitizeCombatStats(
 *     stats.accuracy(),
 *     stats.dodgeChance(),
 *     stats.criticalHitChance(),
 *     stats.spellResistance()
 * );
 * CombatStatsComponent fixed = new CombatStatsComponent(
 *     sanitized[0], sanitized[1], sanitized[2], sanitized[3],
 *     stats.duelsWon(), stats.duelsLost()
 * );
 * }</pre>
 * 
 * <h2>Best Practices</h2>
 * 
 * <ul>
 *   <li>Use sanitization after validation to fix remaining issues</li>
 *   <li>Sanitize data on load, not just on save</li>
 *   <li>Save sanitized data back to prevent re-corruption</li>
 *   <li>Log sanitization actions for debugging</li>
 * </ul>
 * 
 * <h2>When to Use</h2>
 * 
 * <ul>
 *   <li><b>Identifier sets</b>: Remove references to deleted/removed content</li>
 *   <li><b>Currency</b>: Fix negative values or overflow issues</li>
 *   <li><b>Maps/Lists</b>: Remove null entries that could cause NPEs</li>
 *   <li><b>Combat stats</b>: Ensure all percentages are in valid ranges</li>
 * </ul>
 */
public final class DataSanitizationUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private DataSanitizationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Sanitizes a set of identifiers by removing invalid ones.
     * 
     * @param identifiers The set of identifiers to sanitize
     * @param validator A predicate that checks if an identifier is valid
     * @param fieldName The name of the field (for logging)
     * @return A new set with only valid identifiers
     */
    public static Set<Identifier> sanitizeIdentifiers(Set<Identifier> identifiers, 
                                                     Predicate<Identifier> validator, 
                                                     String fieldName) {
        if (identifiers == null) {
            return new HashSet<>();
        }
        
        Set<Identifier> sanitized = new HashSet<>();
        int removed = 0;
        
        for (Identifier id : identifiers) {
            if (id != null && validator.test(id)) {
                sanitized.add(id);
            } else {
                removed++;
            }
        }
        
        if (removed > 0) {
            LOGGER.warn("Data sanitization: removed {} invalid identifiers from {}", removed, fieldName);
        }
        
        return sanitized;
    }
    
    /**
     * Sanitizes currency values to ensure they're non-negative and reasonable.
     * 
     * @param galleons The galleons value
     * @param sickles The sickles value
     * @param knuts The knuts value
     * @param maxTotal The maximum total value allowed (in knuts)
     * @return An array of sanitized values [galleons, sickles, knuts]
     */
    public static int[] sanitizeCurrency(int galleons, int sickles, int knuts, int maxTotal) {
        // Clamp to non-negative
        galleons = Math.max(0, galleons);
        sickles = Math.max(0, sickles);
        knuts = Math.max(0, knuts);
        
        // Convert to total knuts
        int totalKnuts = galleons * 493 + sickles * 29 + knuts;
        
        // Clamp to maximum if needed
        if (totalKnuts > maxTotal) {
            LOGGER.warn("Data sanitization: currency total {} exceeds maximum {}, clamping", 
                totalKnuts, maxTotal);
            totalKnuts = Math.min(totalKnuts, maxTotal);
            
            // Recalculate from total
            galleons = totalKnuts / 493;
            int remainder = totalKnuts % 493;
            sickles = remainder / 29;
            knuts = remainder % 29;
        }
        
        return new int[]{galleons, sickles, knuts};
    }
    
    /**
     * Sanitizes a map by removing entries with null keys or values.
     * 
     * @param map The map to sanitize (will be modified in place)
     * @param fieldName The name of the field (for logging)
     * @return The number of entries removed
     */
    public static <K, V> int sanitizeMap(java.util.Map<K, V> map, String fieldName) {
        if (map == null) {
            return 0;
        }
        
        int removed = 0;
        Set<K> keysToRemove = new HashSet<>();
        
        for (java.util.Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                keysToRemove.add(entry.getKey());
                removed++;
            }
        }
        
        for (K key : keysToRemove) {
            map.remove(key);
        }
        
        if (removed > 0) {
            LOGGER.warn("Data sanitization: removed {} null entries from {}", removed, fieldName);
        }
        
        return removed;
    }
    
    /**
     * Sanitizes a list by removing null entries.
     * 
     * @param list The list to sanitize (will be modified in place)
     * @param fieldName The name of the field (for logging)
     * @return The number of entries removed
     */
    public static <T> int sanitizeList(java.util.List<T> list, String fieldName) {
        if (list == null) {
            return 0;
        }
        
        int removed = 0;
        java.util.Iterator<T> iterator = list.iterator();
        
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
                removed++;
            }
        }
        
        if (removed > 0) {
            LOGGER.warn("Data sanitization: removed {} null entries from {}", removed, fieldName);
        }
        
        return removed;
    }
    
    /**
     * Sanitizes combat stats by clamping values to valid ranges.
     * 
     * @param accuracy The accuracy value (0.0 to 1.0)
     * @param dodgeChance The dodge chance (0.0 to 1.0)
     * @param criticalHitChance The critical hit chance (0.0 to 1.0)
     * @param spellResistance The spell resistance (0.0 to 0.9)
     * @return An array of sanitized values [accuracy, dodgeChance, criticalHitChance, spellResistance]
     */
    public static float[] sanitizeCombatStats(float accuracy, float dodgeChance, 
                                             float criticalHitChance, float spellResistance) {
        accuracy = DataValidationUtils.validatePercentage(accuracy, "accuracy");
        dodgeChance = DataValidationUtils.validatePercentage(dodgeChance, "dodgeChance");
        criticalHitChance = DataValidationUtils.validatePercentage(criticalHitChance, "criticalHitChance");
        spellResistance = DataValidationUtils.validateRange(spellResistance, 0.0f, 0.9f, "spellResistance");
        
        return new float[]{accuracy, dodgeChance, criticalHitChance, spellResistance};
    }
}

