package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Base class for tamable creature entities.
 * Provides taming mechanics, owner tracking, and pet AI goals.
 * Uses composition with TamingComponent for flexibility.
 * 
 * <p>Subclasses should override {@link #registerCreatureGoals()} to add creature-specific goals
 * and call {@code super.registerCreatureGoals()} to include pet behaviors.
 */
public abstract class BaseTamableCreatureEntity extends BaseCreatureEntity {
    protected final TamingComponent tamingComponent;
    
    public BaseTamableCreatureEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseTamableCreatureEntity");
        
        this.tamingComponent = new TamingComponent(this, BaseTamableCreatureEntity.class);
    }
    
    @Override
    protected void defineCreatureSynchedData(SynchedEntityData.Builder builder) {
        super.defineCreatureSynchedData(builder);
        tamingComponent.defineSynchedData(builder);
    }
    
    @Override
    protected void registerCreatureGoals() {
        super.registerCreatureGoals();
        
        // Pet-specific goals - subclasses should implement custom follow/sit goals
        // Example: this.goalSelector.addGoal(2, new CustomSitGoal(this));
        // Example: this.goalSelector.addGoal(4, new CustomFollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
    }
    
    /**
     * Gets the owner UUID if this creature is tamed.
     * 
     * @return The owner UUID, or empty if not tamed
     */
    public Optional<UUID> getOwnerUUID() {
        return tamingComponent.getOwnerUUID();
    }
    
    /**
     * Sets the owner UUID.
     * 
     * @param ownerUUID The owner UUID, or empty to clear
     */
    public void setOwnerUUID(Optional<UUID> ownerUUID) {
        tamingComponent.setOwnerUUID(ownerUUID);
    }
    
    /**
     * Gets the owner player if this creature is tamed and the owner is online.
     * 
     * @return The owner player, or null if not tamed or owner not found
     */
    public Player getOwner() {
        return tamingComponent.getOwner();
    }
    
    /**
     * Checks if this creature is tamed.
     * 
     * @return true if tamed
     */
    public boolean isTamed() {
        return tamingComponent.isTamed();
    }
    
    /**
     * Checks if this creature is owned by the given player.
     * 
     * @param player The player to check
     * @return true if owned by the player
     */
    public boolean isOwnedBy(Player player) {
        return tamingComponent.isOwnedBy(player);
    }
    
    /**
     * Tames this creature for the given player.
     * 
     * @param player The player to tame for
     */
    public void tame(Player player) {
        tamingComponent.tame(player);
    }
    
    /**
     * Checks if this creature is sitting.
     * 
     * @return true if sitting
     */
    public boolean isSitting() {
        return tamingComponent.isSitting();
    }
    
    /**
     * Sets whether this creature is sitting.
     * 
     * @param sitting true to sit, false to stand
     */
    public void setSitting(boolean sitting) {
        tamingComponent.setSitting(sitting);
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        tamingComponent.saveData(output);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        tamingComponent.loadData(input);
    }
}

