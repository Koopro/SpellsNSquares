package at.koopro.spells_n_squares.core.base.entity;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Base class for mod entities with common functionality.
 * Provides data synchronization, save/load helpers, and standardized patterns.
 * Uses composition with ModEntityComponent for flexibility.
 */
public abstract class BaseModEntity extends Entity {
    protected final ModEntityComponent modEntityComponent;
    
    public BaseModEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseModEntity", 
            "type=" + (entityType != null ? entityType.toString() : "null") + 
            ", level=" + (level != null ? (level.isClientSide() ? "client" : "server") : "null"));
        
        this.modEntityComponent = new ModEntityComponent(this);
        
        // Set up callbacks
        modEntityComponent.setDataCallback(builder -> defineCustomSynchedData(builder));
        modEntityComponent.setSaveLoadCallback(new ModEntityComponent.SaveLoadCallback() {
            @Override
            public void saveCustomData(ValueOutput output) {
                BaseModEntity.this.saveCustomData(output);
            }
            
            @Override
            public void loadCustomData(ValueInput input) {
                BaseModEntity.this.loadCustomData(input);
            }
        });
        modEntityComponent.setHurtCallback((serverLevel, source, amount) -> onHurt(serverLevel, source, amount));
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        modEntityComponent.defineSynchedData(builder);
    }
    
    /**
     * Defines custom synched entity data.
     * Subclasses should override to add their specific synched data.
     * 
     * @param builder The synched data builder
     */
    protected void defineCustomSynchedData(SynchedEntityData.Builder builder) {
        DevLogger.logMethodEntry(this, "defineCustomSynchedData");
        // Override in subclasses
        DevLogger.logMethodExit(this, "defineCustomSynchedData");
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        DevLogger.logDataOperation(this, "addAdditionalSaveData", "SAVE", 
            "pos=" + DevLogger.formatPos(blockPosition()));
        DevLogger.logMethodEntry(this, "addAdditionalSaveData");
        modEntityComponent.saveCustomData(output);
        DevLogger.logMethodExit(this, "addAdditionalSaveData");
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        DevLogger.logDataOperation(this, "readAdditionalSaveData", "LOAD", 
            "pos=" + DevLogger.formatPos(blockPosition()));
        DevLogger.logMethodEntry(this, "readAdditionalSaveData");
        modEntityComponent.loadCustomData(input);
        DevLogger.logMethodExit(this, "readAdditionalSaveData");
    }
    
    /**
     * Saves custom entity data.
     * Subclasses should override to save their specific data.
     * 
     * @param output The output to save to
     */
    protected void saveCustomData(ValueOutput output) {
        DevLogger.logMethodEntry(this, "saveCustomData");
        // Override in subclasses
        DevLogger.logMethodExit(this, "saveCustomData");
    }
    
    /**
     * Loads custom entity data.
     * Subclasses should override to load their specific data.
     * 
     * @param input The input to load from
     */
    protected void loadCustomData(ValueInput input) {
        DevLogger.logMethodEntry(this, "loadCustomData");
        // Override in subclasses
        DevLogger.logMethodExit(this, "loadCustomData");
    }
    
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return modEntityComponent.onHurt(level, source, amount);
    }
    
    /**
     * Called when the entity is hurt on the server.
     * Subclasses should override to implement custom damage handling.
     * 
     * @param level The server level
     * @param source The damage source
     * @param amount The damage amount
     * @return True if the entity was hurt
     */
    protected boolean onHurt(ServerLevel level, DamageSource source, float amount) {
        DevLogger.logMethodEntry(this, "onHurt", 
            "source=" + (source != null ? source.getMsgId() : "null") + 
            ", amount=" + amount);
        boolean result = false; // Default: entities are not hurt
        DevLogger.logMethodExit(this, "onHurt", result);
        return result;
    }
    
    // Note: Subclasses should define their own EntityDataAccessor fields and use builder.define() directly
    // Example:
    // private static final EntityDataAccessor<Boolean> DATA_FLAG = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    // Then in defineCustomSynchedData: builder.define(DATA_FLAG, false);
}

