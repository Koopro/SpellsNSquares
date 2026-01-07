package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Component for taming functionality.
 * Provides taming mechanics, owner tracking, and pet state management.
 * Uses composition instead of inheritance for flexibility.
 */
public class TamingComponent {
    private final PathfinderMob creature;
    private final EntityDataAccessor<Boolean> DATA_SITTING;
    private UUID ownerUUID;
    
    public TamingComponent(PathfinderMob creature, Class<? extends PathfinderMob> entityClass) {
        this.creature = creature;
        this.DATA_SITTING = SynchedEntityData.defineId(entityClass, EntityDataSerializers.BOOLEAN);
    }
    
    /**
     * Gets the sitting data accessor for use in defineSynchedData.
     */
    public EntityDataAccessor<Boolean> getSittingDataAccessor() {
        return DATA_SITTING;
    }
    
    /**
     * Defines taming-related synched entity data.
     * Should be called from the creature's defineSynchedData method.
     */
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SITTING, false);
    }
    
    /**
     * Gets the owner UUID if this creature is tamed.
     * 
     * @return The owner UUID, or empty if not tamed
     */
    public Optional<UUID> getOwnerUUID() {
        return Optional.ofNullable(ownerUUID);
    }
    
    /**
     * Sets the owner UUID.
     * 
     * @param ownerUUID The owner UUID, or empty to clear
     */
    public void setOwnerUUID(Optional<UUID> ownerUUID) {
        this.ownerUUID = ownerUUID.orElse(null);
    }
    
    /**
     * Gets the owner player if this creature is tamed and the owner is online.
     * 
     * @return The owner player, or null if not tamed or owner not found
     */
    public Player getOwner() {
        Optional<UUID> ownerUUID = getOwnerUUID();
        if (ownerUUID.isEmpty() || !(creature.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getPlayerByUUID(ownerUUID.get());
    }
    
    /**
     * Checks if this creature is tamed.
     * 
     * @return true if tamed
     */
    public boolean isTamed() {
        return getOwnerUUID().isPresent();
    }
    
    /**
     * Checks if this creature is owned by the given player.
     * 
     * @param player The player to check
     * @return true if owned by the player
     */
    public boolean isOwnedBy(Player player) {
        if (player == null) {
            return false;
        }
        Optional<UUID> ownerUUID = getOwnerUUID();
        return ownerUUID.isPresent() && ownerUUID.get().equals(player.getUUID());
    }
    
    /**
     * Tames this creature for the given player.
     * 
     * @param player The player to tame for
     */
    public void tame(Player player) {
        if (player != null) {
            setOwnerUUID(Optional.of(player.getUUID()));
            DevLogger.logStateChange(creature, "tame", "Tamed by " + player.getName().getString());
        }
    }
    
    /**
     * Checks if this creature is sitting.
     * 
     * @return true if sitting
     */
    public boolean isSitting() {
        return creature.getEntityData().get(DATA_SITTING);
    }
    
    /**
     * Sets whether this creature is sitting.
     * 
     * @param sitting true to sit, false to stand
     */
    public void setSitting(boolean sitting) {
        creature.getEntityData().set(DATA_SITTING, sitting);
    }
    
    /**
     * Saves taming data.
     * Should be called from the creature's addAdditionalSaveData method.
     */
    public void saveData(ValueOutput output) {
        if (ownerUUID != null) {
            output.store("OwnerUUID", UUIDUtil.CODEC, ownerUUID);
        }
    }
    
    /**
     * Loads taming data.
     * Should be called from the creature's readAdditionalSaveData method.
     */
    public void loadData(ValueInput input) {
        ownerUUID = input.read("OwnerUUID", UUIDUtil.CODEC).orElse(null);
    }
}

