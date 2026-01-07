package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/**
 * Implementation of DataAccessLayer using persistent data (NBT).
 * This is the current implementation and will be the default for the mod.
 * 
 * <p>Future implementations could use entity data components when they are fully supported.
 */
public final class PersistentDataAccessLayer implements DataAccessLayer {
    private static final PersistentDataAccessLayer INSTANCE = new PersistentDataAccessLayer();
    
    private PersistentDataAccessLayer() {
        // Singleton
    }
    
    /**
     * Gets the singleton instance of the persistent data access layer.
     * 
     * @return The singleton instance
     */
    public static PersistentDataAccessLayer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public <T> T load(Player player, String dataKey, Codec<T> codec, Supplier<T> defaultSupplier) {
        DevLogger.logDataOperation(this, "load", "LOAD", 
            "player=" + (player != null ? player.getName().getString() : "null") + 
            ", dataKey=" + dataKey);
        T result = PersistentDataAccessHelper.load(player, dataKey, codec, defaultSupplier, dataKey);
        DevLogger.logMethodExit(this, "load", result != null ? "data" : "default");
        return result;
    }
    
    @Override
    public <T> void save(Player player, String dataKey, Codec<T> codec, T data) {
        DevLogger.logDataOperation(this, "save", "SAVE", 
            "player=" + (player != null ? player.getName().getString() : "null") + 
            ", dataKey=" + dataKey);
        PersistentDataAccessHelper.save(player, dataKey, codec, data, dataKey);
        DevLogger.logMethodExit(this, "save");
    }
    
    @Override
    public boolean hasData(Player player, String dataKey) {
        DevLogger.logMethodEntry(this, "hasData", 
            "player=" + (player != null ? player.getName().getString() : "null") + 
            ", dataKey=" + dataKey);
        boolean result = PersistentDataAccessHelper.hasData(player, dataKey);
        DevLogger.logReturnValue(this, "hasData", result);
        return result;
    }
    
    @Override
    public void removeData(Player player, String dataKey) {
        DevLogger.logDataOperation(this, "removeData", "REMOVE", 
            "player=" + (player != null ? player.getName().getString() : "null") + 
            ", dataKey=" + dataKey);
        if (player == null || dataKey == null || player.level().isClientSide()) {
            DevLogger.logMethodExit(this, "removeData");
            return;
        }
        
        player.getPersistentData().remove(dataKey);
        DevLogger.logStateChange(this, "removeData", "Data removed");
        DevLogger.logMethodExit(this, "removeData");
    }
    
    @Override
    public CompoundTag getRawData(Player player, String dataKey) {
        if (player == null || dataKey == null || player.level().isClientSide()) {
            return null;
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(dataKey);
        
        if (tagOpt.isEmpty()) {
            return null;
        }
        
        var tag = tagOpt.get();
        return tag.isEmpty() ? null : tag;
    }
}

