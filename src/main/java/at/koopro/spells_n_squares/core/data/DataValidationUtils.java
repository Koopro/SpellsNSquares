package at.koopro.spells_n_squares.core.data;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.function.Predicate;

/**
 * Utility class for data validation and integrity checks.
 * Provides common validation patterns for player data components.
 * 
 * <p>Performance Notes:
 * <ul>
 *   <li>Validation methods are optimized for hot paths</li>
 *   <li>Range checks use simple comparisons (O(1))</li>
 *   <li>Identifier validation uses predicate checks (O(1) for registry lookups)</li>
 *   <li>Validation typically runs on data load, not every tick</li>
 * </ul>
 * 
 * <h2>Usage Patterns</h2>
 * 
 * <p>All validation methods automatically clamp invalid values to valid ranges
 * and log warnings when validation fails. This ensures data integrity while
 * preventing crashes from corrupted data.
 * 
 * <h3>Range Validation</h3>
 * 
 * <pre>{@code
 * // Validate and clamp percentage values
 * float accuracy = DataValidationUtils.validateRange(stats.accuracy(), 0.0f, 1.0f, "accuracy");
 * 
 * // Validate and clamp integer values
 * int level = DataValidationUtils.validateRange(playerLevel, 1, 100, "player level");
 * }</pre>
 * 
 * <h3>Non-Negative Validation</h3>
 * 
 * <pre>{@code
 * // Ensure currency values are non-negative
 * int galleons = DataValidationUtils.validateNonNegative(data.galleons(), "galleons");
 * }</pre>
 * 
 * <h3>Identifier Validation</h3>
 * 
 * <pre>{@code
 * // Validate spell IDs exist in registry
 * boolean isValid = DataValidationUtils.validateIdentifier(
 *     spellId, 
 *     id -> SpellRegistry.get(id) != null, 
 *     "spell slot"
 * );
 * }</pre>
 * 
 * <h3>Percentage Validation</h3>
 * 
 * <pre>{@code
 * // Validate combat stats are valid percentages
 * float dodgeChance = DataValidationUtils.validatePercentage(stats.dodgeChance(), "dodgeChance");
 * }</pre>
 * 
 * <h2>Parameter Validation Requirements</h2>
 * 
 * <ul>
 *   <li><b>fieldName</b>: Should be descriptive (e.g., "accuracy", "spell slot 0")</li>
 *   <li><b>min/max</b>: Must satisfy min <= max</li>
 *   <li><b>validator</b>: Must not be null for identifier validation</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * 
 * <ul>
 *   <li>Always validate data on load, not just on save</li>
 *   <li>Use descriptive field names for better error messages</li>
 *   <li>Combine validation with sanitization for complete data integrity</li>
 *   <li>Validate identifiers against actual registries, not just format</li>
 * </ul>
 */
public final class DataValidationUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private DataValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates that a value is within a specified range.
     * 
     * @param value The value to validate
     * @param min The minimum allowed value (inclusive)
     * @param max The maximum allowed value (inclusive)
     * @param fieldName The name of the field (for logging)
     * @return The clamped value if out of range, or the original value if valid
     */
    public static float validateRange(float value, float min, float max, String fieldName) {
        if (value < min || value > max) {
            LOGGER.warn("Data validation: {} value {} is out of range [{}, {}], clamping", 
                fieldName, value, min, max);
            return Math.max(min, Math.min(max, value));
        }
        return value;
    }
    
    /**
     * Validates that an integer value is within a specified range.
     * 
     * @param value The value to validate
     * @param min The minimum allowed value (inclusive)
     * @param max The maximum allowed value (inclusive)
     * @param fieldName The name of the field (for logging)
     * @return The clamped value if out of range, or the original value if valid
     */
    public static int validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            LOGGER.warn("Data validation: {} value {} is out of range [{}, {}], clamping", 
                fieldName, value, min, max);
            return Math.max(min, Math.min(max, value));
        }
        return value;
    }
    
    /**
     * Validates that a value is non-negative.
     * 
     * @param value The value to validate
     * @param fieldName The name of the field (for logging)
     * @return The clamped value (0 if negative), or the original value if valid
     */
    public static float validateNonNegative(float value, String fieldName) {
        if (value < 0) {
            LOGGER.warn("Data validation: {} value {} is negative, clamping to 0", fieldName, value);
            return 0.0f;
        }
        return value;
    }
    
    /**
     * Validates that an integer value is non-negative.
     * 
     * @param value The value to validate
     * @param fieldName The name of the field (for logging)
     * @return The clamped value (0 if negative), or the original value if valid
     */
    public static int validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            LOGGER.warn("Data validation: {} value {} is negative, clamping to 0", fieldName, value);
            return 0;
        }
        return value;
    }
    
    /**
     * Validates that an identifier exists in a registry or passes a custom check.
     * 
     * @param id The identifier to validate
     * @param validator A predicate that checks if the identifier is valid
     * @param fieldName The name of the field (for logging)
     * @return true if valid, false otherwise
     */
    public static boolean validateIdentifier(Identifier id, Predicate<Identifier> validator, String fieldName) {
        if (id == null) {
            LOGGER.warn("Data validation: {} identifier is null", fieldName);
            return false;
        }
        
        if (!validator.test(id)) {
            LOGGER.warn("Data validation: {} identifier {} is not valid", fieldName, id);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates that a string is not null or empty.
     * 
     * @param value The string to validate
     * @param fieldName The name of the field (for logging)
     * @param defaultValue The default value to return if invalid
     * @return The original value if valid, or the default value if invalid
     */
    public static String validateString(String value, String fieldName, String defaultValue) {
        if (value == null || value.isEmpty()) {
            LOGGER.warn("Data validation: {} string is null or empty, using default", fieldName);
            return defaultValue;
        }
        return value;
    }
    
    /**
     * Validates that a player is not null and is on the server side.
     * 
     * @param player The player to validate
     * @param action The action being performed (for logging)
     * @return true if valid, false otherwise
     */
    public static boolean validateServerPlayer(Player player, String action) {
        if (player == null) {
            LOGGER.warn("Data validation: player is null for action: {}", action);
            return false;
        }
        
        if (player.level().isClientSide()) {
            LOGGER.warn("Data validation: player {} is on client side for action: {}", 
                player.getName().getString(), action);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates that a percentage value (0.0 to 1.0) is within range.
     * 
     * @param value The percentage value to validate
     * @param fieldName The name of the field (for logging)
     * @return The clamped value if out of range, or the original value if valid
     */
    public static float validatePercentage(float value, String fieldName) {
        return validateRange(value, 0.0f, 1.0f, fieldName);
    }
    
    /**
     * Validates that a list size is within expected bounds.
     * 
     * @param size The size to validate
     * @param minSize The minimum allowed size (inclusive)
     * @param maxSize The maximum allowed size (inclusive)
     * @param fieldName The name of the field (for logging)
     * @return true if valid, false otherwise
     */
    public static boolean validateListSize(int size, int minSize, int maxSize, String fieldName) {
        if (size < minSize || size > maxSize) {
            LOGGER.warn("Data validation: {} list size {} is out of range [{}, {}]", 
                fieldName, size, minSize, maxSize);
            return false;
        }
        return true;
    }
}

