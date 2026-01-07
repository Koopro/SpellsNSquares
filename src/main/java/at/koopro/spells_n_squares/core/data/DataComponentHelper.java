package at.koopro.spells_n_squares.core.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper utility for common data component operations.
 * Provides convenient methods for getting, setting, updating, and validating player data.
 * 
 * <p>This reduces boilerplate and ensures consistent data access patterns across the mod.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Get data with default
 * MyData data = DataComponentHelper.get(player, "my_data", MyData.CODEC, MyData::createDefault);
 * 
 * // Update data
 * MyData updated = data.withNewValue(newValue);
 * DataComponentHelper.set(player, "my_data", MyData.CODEC, updated);
 * 
 * // Update in place
 * DataComponentHelper.update(player, "my_data", MyData.CODEC, MyData::createDefault, 
 *     data -> data.withNewValue(newValue));
 * }</pre>
 */
public final class DataComponentHelper {
    private static final DataAccessLayer DATA_LAYER = DataAccessLayerFactory.getDefault();
    
    private DataComponentHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets data for a player, returning the default if not found.
     * 
     * @param <T> The data type
     * @param player The player
     * @param dataKey The data key
     * @param codec The codec for deserialization
     * @param defaultSupplier Supplier for default value
     * @return The data, or default if not found
     */
    public static <T> T get(Player player, String dataKey, Codec<T> codec, Supplier<T> defaultSupplier) {
        if (player == null) {
            return defaultSupplier.get();
        }
        return DATA_LAYER.load(player, dataKey, codec, defaultSupplier);
    }
    
    /**
     * Sets data for a player.
     * 
     * @param <T> The data type
     * @param player The player
     * @param dataKey The data key
     * @param codec The codec for serialization
     * @param data The data to save
     */
    public static <T> void set(Player player, String dataKey, Codec<T> codec, T data) {
        if (player == null) {
            return;
        }
        DATA_LAYER.save(player, dataKey, codec, data);
    }
    
    /**
     * Updates data for a player using a function.
     * Loads the current data, applies the update function, and saves the result.
     * 
     * @param <T> The data type
     * @param player The player
     * @param dataKey The data key
     * @param codec The codec for serialization/deserialization
     * @param defaultSupplier Supplier for default value if data doesn't exist
     * @param updateFunction Function to apply to the data
     */
    public static <T> void update(Player player, String dataKey, Codec<T> codec, 
                                  Supplier<T> defaultSupplier, Function<T, T> updateFunction) {
        if (player == null) {
            return;
        }
        T current = get(player, dataKey, codec, defaultSupplier);
        T updated = updateFunction.apply(current);
        set(player, dataKey, codec, updated);
    }
    
    /**
     * Checks if data exists for a player.
     * 
     * @param player The player
     * @param dataKey The data key
     * @return true if data exists, false otherwise
     */
    public static boolean hasData(Player player, String dataKey) {
        if (player == null) {
            return false;
        }
        return DATA_LAYER.hasData(player, dataKey);
    }
    
    /**
     * Removes data for a player.
     * 
     * @param player The player
     * @param dataKey The data key
     */
    public static void remove(Player player, String dataKey) {
        if (player == null) {
            return;
        }
        DATA_LAYER.removeData(player, dataKey);
    }
    
    /**
     * Validates that data exists for a player, throwing an exception if not.
     * Useful for operations that require data to exist.
     * 
     * @param player The player
     * @param dataKey The data key
     * @throws IllegalStateException if data does not exist
     */
    public static void validateExists(Player player, String dataKey) {
        if (!hasData(player, dataKey)) {
            throw new IllegalStateException("Data does not exist for key: " + dataKey + 
                " (player: " + (player != null ? player.getName().getString() : "null") + ")");
        }
    }
    
    /**
     * Gets data for a player, throwing an exception if not found.
     * 
     * @param <T> The data type
     * @param player The player
     * @param dataKey The data key
     * @param codec The codec for deserialization
     * @return The data
     * @throws IllegalStateException if data does not exist
     */
    public static <T> T getRequired(Player player, String dataKey, Codec<T> codec) {
        validateExists(player, dataKey);
        T data = DATA_LAYER.load(player, dataKey, codec, () -> null);
        if (data == null) {
            throw new IllegalStateException("Data is null for key: " + dataKey + 
                " (player: " + (player != null ? player.getName().getString() : "null") + ")");
        }
        return data;
    }
}
