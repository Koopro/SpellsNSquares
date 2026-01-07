package at.koopro.spells_n_squares.features.creatures.base;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

/**
 * Component for mounting functionality.
 * Provides mounting mechanics, saddle state, and mount validation.
 * Uses composition instead of inheritance for flexibility.
 */
public class MountingComponent {
    private final PathfinderMob creature;
    private final TamingComponent tamingComponent;
    private final EntityDataAccessor<Boolean> DATA_SADDLED;
    private MountCallback mountCallback;
    
    /**
     * Callback for mount-specific behavior.
     */
    public interface MountCallback {
        double getMountSpeed();
        boolean canFly();
    }
    
    public MountingComponent(PathfinderMob creature, TamingComponent tamingComponent, Class<? extends PathfinderMob> entityClass) {
        this.creature = creature;
        this.tamingComponent = tamingComponent;
        this.DATA_SADDLED = SynchedEntityData.defineId(entityClass, EntityDataSerializers.BOOLEAN);
    }
    
    /**
     * Sets the callback for mount-specific behavior.
     */
    public void setMountCallback(MountCallback callback) {
        this.mountCallback = callback;
    }
    
    /**
     * Gets the saddled data accessor for use in defineSynchedData.
     */
    public EntityDataAccessor<Boolean> getSaddledDataAccessor() {
        return DATA_SADDLED;
    }
    
    /**
     * Defines mounting-related synched entity data.
     * Should be called from the creature's defineSynchedData method.
     */
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SADDLED, false);
    }
    
    /**
     * Checks if this creature is saddled.
     * 
     * @return true if saddled
     */
    public boolean isSaddled() {
        return creature.getEntityData().get(DATA_SADDLED);
    }
    
    /**
     * Sets whether this creature is saddled.
     * 
     * @param saddled true to saddle, false to unsaddle
     */
    public void setSaddled(boolean saddled) {
        creature.getEntityData().set(DATA_SADDLED, saddled);
    }
    
    /**
     * Checks if this creature can be mounted.
     * Requires the creature to be tamed, owned by the player, and saddled.
     * 
     * @param player The player attempting to mount
     * @return true if can be mounted
     */
    public boolean canBeMounted(Player player) {
        return tamingComponent.isTamed() && tamingComponent.isOwnedBy(player) && isSaddled();
    }
    
    /**
     * Gets the mount speed for this creature.
     * Calls the mount callback if set, otherwise returns default speed.
     * 
     * @return The mount speed multiplier
     */
    public double getMountSpeed() {
        if (mountCallback != null) {
            return mountCallback.getMountSpeed();
        }
        return 1.0; // Default speed
    }
    
    /**
     * Checks if this creature can fly.
     * Calls the mount callback if set, otherwise returns false.
     * 
     * @return true if can fly
     */
    public boolean canFly() {
        if (mountCallback != null) {
            return mountCallback.canFly();
        }
        return false; // Default: ground mount
    }
}

