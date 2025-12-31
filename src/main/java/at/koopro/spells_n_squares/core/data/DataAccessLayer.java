package at.koopro.spells_n_squares.core.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/**
 * Abstraction layer for data access that allows future migration to entity data components.
 * Currently uses persistent data (NBT) but can be extended to support other backends.
 * 
 * <p>This interface provides a clean abstraction for data storage, allowing the implementation
 * to be swapped without changing the data classes that use it.
 * 
 * <p>Example usage:
 * <pre>{@code
 * DataAccessLayer dataLayer = DataAccessLayerFactory.getDefault();
 * MyData data = dataLayer.load(player, "my_data_key", MyData.CODEC, MyData::createDefault);
 * dataLayer.save(player, "my_data_key", MyData.CODEC, updatedData);
 * }</pre>
 */
public interface DataAccessLayer {
    /**
     * Loads data for a player from the storage backend.
     * 
     * @param player The player whose data to load
     * @param dataKey The key identifying the data
     * @param codec The codec for deserializing the data
     * @param defaultSupplier A supplier for default values when data is missing
     * @param <T> The type of data to load
     * @return The loaded data, or the default if loading fails
     */
    <T> T load(Player player, String dataKey, Codec<T> codec, Supplier<T> defaultSupplier);
    
    /**
     * Saves data for a player to the storage backend.
     * 
     * @param player The player whose data to save
     * @param dataKey The key identifying the data
     * @param codec The codec for serializing the data
     * @param data The data to save
     * @param <T> The type of data to save
     */
    <T> void save(Player player, String dataKey, Codec<T> codec, T data);
    
    /**
     * Checks if data exists for a player.
     * 
     * @param player The player to check
     * @param dataKey The key identifying the data
     * @return true if data exists, false otherwise
     */
    boolean hasData(Player player, String dataKey);
    
    /**
     * Removes data for a player.
     * 
     * @param player The player whose data to remove
     * @param dataKey The key identifying the data
     */
    void removeData(Player player, String dataKey);
    
    /**
     * Gets the raw NBT data for a player (for advanced use cases).
     * 
     * @param player The player
     * @param dataKey The key identifying the data
     * @return The raw NBT data, or null if not found
     */
    CompoundTag getRawData(Player player, String dataKey);
}

