package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * Base class for all magical creatures.
 * Provides common AI goals and behavior patterns.
 * Uses composition with CreatureAIComponent for flexibility.
 * 
 * <p>Subclasses should override {@link #registerCreatureGoals()} to add creature-specific AI goals.
 */
public abstract class BaseCreatureEntity extends PathfinderMob {
    protected final CreatureAIComponent creatureAIComponent;
    
    public BaseCreatureEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseCreatureEntity", 
            "type=" + (entityType != null ? entityType.toString() : "null"));
        
        this.creatureAIComponent = new CreatureAIComponent(this);
        creatureAIComponent.setGoalsCallback(creature -> registerCreatureGoals());
        creatureAIComponent.setDataCallback(builder -> defineCreatureSynchedData(builder));
    }
    
    @Override
    protected void registerGoals() {
        DevLogger.logMethodEntry(this, "registerGoals");
        creatureAIComponent.registerCommonGoals();
        DevLogger.logMethodExit(this, "registerGoals");
    }
    
    /**
     * Registers creature-specific AI goals.
     * Subclasses should override to add their specific goals.
     */
    protected void registerCreatureGoals() {
        DevLogger.logMethodEntry(this, "registerCreatureGoals");
        // Override in subclasses
        DevLogger.logMethodExit(this, "registerCreatureGoals");
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        creatureAIComponent.defineCreatureSynchedData(builder);
    }
    
    /**
     * Defines creature-specific synched entity data.
     * Subclasses should override to add their specific data.
     * 
     * @param builder The synched data builder
     */
    protected void defineCreatureSynchedData(SynchedEntityData.Builder builder) {
        // Override in subclasses
    }
    
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        DevLogger.logEntityEvent(this, "hurtServer", "HURT", 
            "source=" + (source != null ? source.getMsgId() : "null") + 
            ", amount=" + amount);
        return super.hurtServer(level, source, amount);
    }
}

