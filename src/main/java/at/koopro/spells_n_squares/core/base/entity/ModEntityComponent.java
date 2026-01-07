package at.koopro.spells_n_squares.core.base.entity;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Component for mod entity functionality.
 * Provides data synchronization, save/load helpers, and damage handling.
 * Uses composition instead of inheritance for flexibility.
 */
public class ModEntityComponent {
    private final Entity entity;
    private DataCallback dataCallback;
    private SaveLoadCallback saveLoadCallback;
    private HurtCallback hurtCallback;
    
    /**
     * Callback for defining custom synched entity data.
     */
    @FunctionalInterface
    public interface DataCallback {
        void defineCustomSynchedData(SynchedEntityData.Builder builder);
    }
    
    /**
     * Callback for save/load operations.
     */
    public interface SaveLoadCallback {
        void saveCustomData(ValueOutput output);
        void loadCustomData(ValueInput input);
    }
    
    /**
     * Callback for damage handling.
     */
    @FunctionalInterface
    public interface HurtCallback {
        boolean onHurt(ServerLevel level, DamageSource source, float amount);
    }
    
    public ModEntityComponent(Entity entity) {
        this.entity = entity;
    }
    
    /**
     * Sets the callback for defining custom synched data.
     */
    public void setDataCallback(DataCallback callback) {
        this.dataCallback = callback;
    }
    
    /**
     * Sets the callback for save/load operations.
     */
    public void setSaveLoadCallback(SaveLoadCallback callback) {
        this.saveLoadCallback = callback;
    }
    
    /**
     * Sets the callback for damage handling.
     */
    public void setHurtCallback(HurtCallback callback) {
        this.hurtCallback = callback;
    }
    
    /**
     * Defines synched entity data.
     * Calls the data callback if set.
     */
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        DevLogger.logMethodEntry(entity, "defineSynchedData");
        if (dataCallback != null) {
            dataCallback.defineCustomSynchedData(builder);
        }
        DevLogger.logMethodExit(entity, "defineSynchedData");
    }
    
    /**
     * Saves custom entity data.
     * Calls the save callback if set.
     */
    public void saveCustomData(ValueOutput output) {
        DevLogger.logMethodEntry(entity, "saveCustomData");
        if (saveLoadCallback != null) {
            saveLoadCallback.saveCustomData(output);
        }
        DevLogger.logMethodExit(entity, "saveCustomData");
    }
    
    /**
     * Loads custom entity data.
     * Calls the load callback if set.
     */
    public void loadCustomData(ValueInput input) {
        DevLogger.logMethodEntry(entity, "loadCustomData");
        if (saveLoadCallback != null) {
            saveLoadCallback.loadCustomData(input);
        }
        DevLogger.logMethodExit(entity, "loadCustomData");
    }
    
    /**
     * Handles entity being hurt on the server.
     * Calls the hurt callback if set, otherwise returns false.
     */
    public boolean onHurt(ServerLevel level, DamageSource source, float amount) {
        DevLogger.logEntityEvent(entity, "hurtServer", "HURT", 
            "source=" + (source != null ? source.getMsgId() : "null") + 
            ", amount=" + amount + 
            ", pos=" + DevLogger.formatPos(entity.blockPosition()));
        DevLogger.logMethodEntry(entity, "hurtServer", 
            "source=" + (source != null ? source.getMsgId() : "null") + 
            ", amount=" + amount);
        
        boolean result = false;
        if (hurtCallback != null) {
            result = hurtCallback.onHurt(level, source, amount);
        }
        
        DevLogger.logReturnValue(entity, "hurtServer", result);
        return result;
    }
}

