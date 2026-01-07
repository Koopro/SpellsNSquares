package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Base class for mountable creature entities.
 * Provides mounting mechanics and flight/ground movement patterns.
 * Uses composition with MountingComponent for flexibility.
 * 
 * <p>Subclasses should override {@link #registerCreatureGoals()} to add creature-specific goals
 * and call {@code super.registerCreatureGoals()} to include mount behaviors.
 */
public abstract class BaseMountCreatureEntity extends BaseTamableCreatureEntity {
    protected final MountingComponent mountingComponent;
    
    public BaseMountCreatureEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseMountCreatureEntity");
        
        this.mountingComponent = new MountingComponent(this, tamingComponent, BaseMountCreatureEntity.class);
        mountingComponent.setMountCallback(new MountingComponent.MountCallback() {
            @Override
            public double getMountSpeed() {
                return BaseMountCreatureEntity.this.getMountSpeed();
            }
            
            @Override
            public boolean canFly() {
                return BaseMountCreatureEntity.this.canFly();
            }
        });
    }
    
    @Override
    protected void defineCreatureSynchedData(SynchedEntityData.Builder builder) {
        super.defineCreatureSynchedData(builder);
        mountingComponent.defineSynchedData(builder);
    }
    
    @Override
    protected void registerCreatureGoals() {
        super.registerCreatureGoals();
        
        // Mount-specific goals - subclasses should implement custom follow owner goal
        // Example: this.goalSelector.addGoal(3, new CustomFollowOwnerGoal(this, 1.0, 10.0f, 2.0f, true));
    }
    
    /**
     * Checks if this creature is saddled.
     * 
     * @return true if saddled
     */
    public boolean isSaddled() {
        return mountingComponent.isSaddled();
    }
    
    /**
     * Sets whether this creature is saddled.
     * 
     * @param saddled true to saddle, false to unsaddle
     */
    public void setSaddled(boolean saddled) {
        mountingComponent.setSaddled(saddled);
    }
    
    /**
     * Checks if this creature can be mounted.
     * Subclasses should override to add specific requirements (e.g., must be tamed, must be saddled).
     * 
     * @param player The player attempting to mount
     * @return true if can be mounted
     */
    public boolean canBeMounted(Player player) {
        return mountingComponent.canBeMounted(player);
    }
    
    /**
     * Gets the mount speed for this creature.
     * Subclasses should override to provide creature-specific speed.
     * 
     * @return The mount speed multiplier
     */
    protected double getMountSpeed() {
        return 1.0; // Default speed
    }
    
    /**
     * Checks if this creature can fly.
     * Subclasses should override for flying mounts.
     * 
     * @return true if can fly
     */
    public boolean canFly() {
        return false; // Default: ground mount
    }
}

