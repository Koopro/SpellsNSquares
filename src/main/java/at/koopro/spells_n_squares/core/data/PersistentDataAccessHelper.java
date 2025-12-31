package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.core.data.migration.DataMigrationSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * Utility class for standardized persistent data access patterns.
 * Provides common methods for loading and saving data with migration and validation built-in.
 * 
 * <p>This class consolidates the common patterns used across data systems:
 * <ul>
 *   <li>Loading data with migration support</li>
 *   <li>Saving data with version tracking</li>
 *   <li>Client-side handling (returns defaults)</li>
 *   <li>Error handling and logging</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * private static final String PERSISTENT_DATA_KEY = "spells_n_squares:my_data";
 * 
 * public static MyDataComponent getMyData(Player player) {
 *     return PersistentDataAccessHelper.load(
 *         player, 
 *         PERSISTENT_DATA_KEY, 
 *         MyDataComponent.CODEC, 
 *         MyDataComponent::createDefault,
 *         "my data"
 *     );
 * }
 * 
 * public static void setMyData(Player player, MyDataComponent data) {
 *     PersistentDataAccessHelper.save(
 *         player,
 *         PERSISTENT_DATA_KEY,
 *         MyDataComponent.CODEC,
 *         data,
 *         "my data"
 *     );
 * }
 * }</pre>
 */
public final class PersistentDataAccessHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private PersistentDataAccessHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Loads data from a player's persistent data with migration and validation support.
     * 
     * @param player The player whose data to load
     * @param dataKey The persistent data key
     * @param codec The codec for deserializing the data
     * @param defaultSupplier A supplier for default values when data is missing
     * @param dataName The name of the data (for logging)
     * @param <T> The type of data to load
     * @return The loaded data, or the default if loading fails
     */
    public static <T> T load(Player player, String dataKey, Codec<T> codec, 
                             Supplier<T> defaultSupplier, String dataName) {
        if (player == null) {
            return defaultSupplier.get();
        }
        
        if (player.level().isClientSide()) {
            // On client, return default (data syncs from server)
            return defaultSupplier.get();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(dataKey);
        
        if (tagOpt.isEmpty()) {
            return defaultSupplier.get();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return defaultSupplier.get();
        }
        
        // Migrate data if needed (before parsing)
        CompoundTag dataToParse = tag;
        if (!DataMigrationSystem.migrateIfNeeded(dataToParse, player.getName().getString())) {
            LOGGER.warn("Data migration failed for player {} ({})", 
                player.getName().getString(), dataName);
        } else if (dataToParse != tag) {
            // Migration modified the data, save it back
            persistentData.put(dataKey, dataToParse);
        }
        
        try {
            return codec.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                dataToParse
            ).result().orElseGet(defaultSupplier);
        } catch (Exception e) {
            LOGGER.warn("Failed to load {} for player {}, using default: {}", 
                dataName, player.getName().getString(), e.getMessage(), e);
            return defaultSupplier.get();
        }
    }
    
    /**
     * Saves data to a player's persistent data with version tracking.
     * 
     * @param player The player whose data to save
     * @param dataKey The persistent data key
     * @param codec The codec for serializing the data
     * @param data The data to save
     * @param dataName The name of the data (for logging)
     * @param <T> The type of data to save
     */
    public static <T> void save(Player player, String dataKey, Codec<T> codec, 
                                T data, String dataName) {
        if (player == null || data == null) {
            return;
        }
        
        if (player.level().isClientSide()) {
            // Only save on server
            return;
        }
        
        try {
            var result = codec.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                // Ensure version is set to current version
                if (tag instanceof CompoundTag compoundTag) {
                    DataMigrationSystem.setDataVersion(compoundTag, DataMigrationSystem.getCurrentDataVersion());
                    player.getPersistentData().put(dataKey, compoundTag);
                } else {
                    player.getPersistentData().put(dataKey, tag);
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to save {} for player {}: {}", 
                dataName, player.getName().getString(), e.getMessage(), e);
        }
    }
    
    /**
     * Checks if data exists for a player.
     * 
     * @param player The player to check
     * @param dataKey The persistent data key
     * @return true if data exists, false otherwise
     */
    public static boolean hasData(Player player, String dataKey) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(dataKey);
        
        if (tagOpt.isEmpty()) {
            return false;
        }
        
        var tag = tagOpt.get();
        return !tag.isEmpty();
    }
}

