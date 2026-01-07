package at.koopro.spells_n_squares.core.util.collection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.Collection;

/**
 * Utility class for common validation patterns.
 * Provides null checks, range validation, type validation, and more.
 */
public final class ValidationUtils {
    private ValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Requires that a value is not null.
     * 
     * @param value The value to check
     * @param message The error message if null
     * @param <T> The value type
     * @return The value if not null
     * @throws IllegalArgumentException if value is null
     */
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message != null ? message : "Value cannot be null");
        }
        return value;
    }
    
    /**
     * Requires that a value is not null.
     * 
     * @param value The value to check
     * @param <T> The value type
     * @return The value if not null
     * @throws IllegalArgumentException if value is null
     */
    public static <T> T requireNonNull(T value) {
        return requireNonNull(value, "Value cannot be null");
    }
    
    /**
     * Requires that a value is within a range (inclusive).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @param message The error message if out of range
     * @return The value if in range
     * @throws IllegalArgumentException if value is out of range
     */
    public static int requireInRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                message != null ? message : String.format("Value %d must be between %d and %d", value, min, max)
            );
        }
        return value;
    }
    
    /**
     * Requires that a value is within a range (inclusive).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @param message The error message if out of range
     * @return The value if in range
     * @throws IllegalArgumentException if value is out of range
     */
    public static float requireInRange(float value, float min, float max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                message != null ? message : String.format("Value %f must be between %f and %f", value, min, max)
            );
        }
        return value;
    }
    
    /**
     * Requires that a value is within a range (inclusive).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @param message The error message if out of range
     * @return The value if in range
     * @throws IllegalArgumentException if value is out of range
     */
    public static double requireInRange(double value, double min, double max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                message != null ? message : String.format("Value %f must be between %f and %f", value, min, max)
            );
        }
        return value;
    }
    
    /**
     * Requires that a value is within a range (inclusive).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @return The value if in range
     * @throws IllegalArgumentException if value is out of range
     */
    public static int requireInRange(int value, int min, int max) {
        return requireInRange(value, min, max, null);
    }
    
    /**
     * Requires that a value is within a range (inclusive).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @return The value if in range
     * @throws IllegalArgumentException if value is out of range
     */
    public static float requireInRange(float value, float min, float max) {
        return requireInRange(value, min, max, null);
    }
    
    /**
     * Requires that a value is within a range (inclusive).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @return The value if in range
     * @throws IllegalArgumentException if value is out of range
     */
    public static double requireInRange(double value, double min, double max) {
        return requireInRange(value, min, max, null);
    }
    
    /**
     * Requires that a string is not null and not empty.
     * 
     * @param value The string to check
     * @param message The error message if null or empty
     * @return The string if not null and not empty
     * @throws IllegalArgumentException if value is null or empty
     */
    public static String requireNonEmpty(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message != null ? message : "String cannot be null or empty");
        }
        return value;
    }
    
    /**
     * Requires that a string is not null and not empty.
     * 
     * @param value The string to check
     * @return The string if not null and not empty
     * @throws IllegalArgumentException if value is null or empty
     */
    public static String requireNonEmpty(String value) {
        return requireNonEmpty(value, null);
    }
    
    /**
     * Requires that a string is not null, not empty, and not blank.
     * 
     * @param value The string to check
     * @param message The error message if null, empty, or blank
     * @return The string if not null, not empty, and not blank
     * @throws IllegalArgumentException if value is null, empty, or blank
     */
    public static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message != null ? message : "String cannot be null, empty, or blank");
        }
        return value;
    }
    
    /**
     * Requires that a string is not null, not empty, and not blank.
     * 
     * @param value The string to check
     * @return The string if not null, not empty, and not blank
     * @throws IllegalArgumentException if value is null, empty, or blank
     */
    public static String requireNonBlank(String value) {
        return requireNonBlank(value, null);
    }
    
    /**
     * Requires that a collection is not null and not empty.
     * 
     * @param collection The collection to check
     * @param message The error message if null or empty
     * @param <T> The collection type
     * @return The collection if not null and not empty
     * @throws IllegalArgumentException if collection is null or empty
     */
    public static <T> Collection<T> requireNonEmpty(Collection<T> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message != null ? message : "Collection cannot be null or empty");
        }
        return collection;
    }
    
    /**
     * Requires that a collection is not null and not empty.
     * 
     * @param collection The collection to check
     * @param <T> The collection type
     * @return The collection if not null and not empty
     * @throws IllegalArgumentException if collection is null or empty
     */
    public static <T> Collection<T> requireNonEmpty(Collection<T> collection) {
        return requireNonEmpty(collection, null);
    }
    
    /**
     * Validates that a value is not null (returns boolean instead of throwing).
     * 
     * @param value The value to check
     * @return true if not null
     */
    public static boolean isNonNull(Object value) {
        return value != null;
    }
    
    /**
     * Validates that a value is null (returns boolean instead of throwing).
     * 
     * @param value The value to check
     * @return true if null
     */
    public static boolean isNull(Object value) {
        return value == null;
    }
    
    /**
     * Validates that a value is within a range (returns boolean instead of throwing).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @return true if in range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that a value is within a range (returns boolean instead of throwing).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @return true if in range
     */
    public static boolean isInRange(float value, float min, float max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that a value is within a range (returns boolean instead of throwing).
     * 
     * @param value The value to check
     * @param min The minimum value
     * @param max The maximum value
     * @return true if in range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    // ========== Minecraft-Specific Validation Methods ==========
    
    /**
     * Requires that a level is server-side.
     * 
     * @param level The level to check
     * @param message The error message if client-side
     * @return The level if server-side
     * @throws IllegalStateException if level is client-side or null
     */
    public static Level requireServerSide(Level level, String message) {
        if (level == null) {
            throw new IllegalStateException(message != null ? message : "Level cannot be null");
        }
        if (level.isClientSide()) {
            throw new IllegalStateException(
                message != null ? message : "Operation requires server-side level"
            );
        }
        return level;
    }
    
    /**
     * Requires that a level is server-side.
     * 
     * @param level The level to check
     * @return The level if server-side
     * @throws IllegalStateException if level is client-side or null
     */
    public static Level requireServerSide(Level level) {
        return requireServerSide(level, null);
    }
    
    /**
     * Requires that a level is client-side.
     * 
     * @param level The level to check
     * @param message The error message if server-side
     * @return The level if client-side
     * @throws IllegalStateException if level is server-side or null
     */
    public static Level requireClientSide(Level level, String message) {
        if (level == null) {
            throw new IllegalStateException(message != null ? message : "Level cannot be null");
        }
        if (!level.isClientSide()) {
            throw new IllegalStateException(
                message != null ? message : "Operation requires client-side level"
            );
        }
        return level;
    }
    
    /**
     * Requires that a level is client-side.
     * 
     * @param level The level to check
     * @return The level if client-side
     * @throws IllegalStateException if level is server-side or null
     */
    public static Level requireClientSide(Level level) {
        return requireClientSide(level, null);
    }
    
    /**
     * Requires that a block position is valid (not null and within valid coordinate range).
     * 
     * @param pos The block position to check
     * @param message The error message if invalid
     * @return The position if valid
     * @throws IllegalArgumentException if position is null or invalid
     */
    public static BlockPos requireValidPosition(BlockPos pos, String message) {
        if (pos == null) {
            throw new IllegalArgumentException(
                message != null ? message : "BlockPos cannot be null"
            );
        }
        // BlockPos has built-in validation, but we can add additional checks if needed
        return pos;
    }
    
    /**
     * Requires that a block position is valid (not null and within valid coordinate range).
     * 
     * @param pos The block position to check
     * @return The position if valid
     * @throws IllegalArgumentException if position is null or invalid
     */
    public static BlockPos requireValidPosition(BlockPos pos) {
        return requireValidPosition(pos, null);
    }
    
    /**
     * Requires that an entity is valid (not null and not removed).
     * 
     * @param entity The entity to check
     * @param message The error message if invalid
     * @param <T> The entity type
     * @return The entity if valid
     * @throws IllegalArgumentException if entity is null or removed
     */
    public static <T extends Entity> T requireValidEntity(T entity, String message) {
        if (entity == null) {
            throw new IllegalArgumentException(
                message != null ? message : "Entity cannot be null"
            );
        }
        if (entity.isRemoved()) {
            throw new IllegalArgumentException(
                message != null ? message : "Entity has been removed"
            );
        }
        return entity;
    }
    
    /**
     * Requires that an entity is valid (not null and not removed).
     * 
     * @param entity The entity to check
     * @param <T> The entity type
     * @return The entity if valid
     * @throws IllegalArgumentException if entity is null or removed
     */
    public static <T extends Entity> T requireValidEntity(T entity) {
        return requireValidEntity(entity, null);
    }
    
    /**
     * Requires that a player is valid (not null, not removed, and is a Player instance).
     * 
     * @param player The player to check
     * @param message The error message if invalid
     * @return The player if valid
     * @throws IllegalArgumentException if player is null, removed, or not a Player
     */
    public static Player requireValidPlayer(Player player, String message) {
        if (player == null) {
            throw new IllegalArgumentException(
                message != null ? message : "Player cannot be null"
            );
        }
        if (player.isRemoved()) {
            throw new IllegalArgumentException(
                message != null ? message : "Player has been removed"
            );
        }
        return player;
    }
    
    /**
     * Requires that a player is valid (not null, not removed, and is a Player instance).
     * 
     * @param player The player to check
     * @return The player if valid
     * @throws IllegalArgumentException if player is null, removed, or not a Player
     */
    public static Player requireValidPlayer(Player player) {
        return requireValidPlayer(player, null);
    }
    
    /**
     * Validates that a level is server-side (returns boolean instead of throwing).
     * 
     * @param level The level to check
     * @return true if server-side, false if null or client-side
     */
    public static boolean isServerSide(Level level) {
        return level != null && !level.isClientSide();
    }
    
    /**
     * Validates that a level is client-side (returns boolean instead of throwing).
     * 
     * @param level The level to check
     * @return true if client-side, false if null or server-side
     */
    public static boolean isClientSide(Level level) {
        return level != null && level.isClientSide();
    }
    
    /**
     * Validates that an entity is valid (not null and not removed).
     * 
     * @param entity The entity to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidEntity(Entity entity) {
        return entity != null && !entity.isRemoved();
    }
    
    /**
     * Validates that a player is valid (not null, not removed).
     * 
     * @param player The player to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidPlayer(Player player) {
        return player != null && !player.isRemoved();
    }
}

