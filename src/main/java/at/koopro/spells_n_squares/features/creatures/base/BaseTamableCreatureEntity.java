package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.features.creatures.util.CreatureOwnerHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Base class for tamable creature entities.
 * Provides common owner management and save/load functionality.
 */
public abstract class BaseTamableCreatureEntity extends TamableAnimal {
    private Optional<UUID> ownerId = Optional.empty();
    
    protected BaseTamableCreatureEntity(EntityType<? extends BaseTamableCreatureEntity> type, Level level) {
        super(type, level);
    }
    
    /**
     * Gets the owner UUID if present.
     * @return Optional containing the owner UUID, or empty if not owned
     */
    public Optional<UUID> getOwnerId() {
        return ownerId;
    }
    
    /**
     * Sets the owner of this creature.
     * @param player The player to set as owner
     */
    public void setOwner(Player player) {
        this.ownerId = Optional.of(player.getUUID());
    }
    
    /**
     * Checks if this creature has an owner.
     * @return true if owned, false otherwise
     */
    public boolean hasOwner() {
        return ownerId.isPresent();
    }
    
    /**
     * Checks if the given player is the owner of this creature.
     * @param player The player to check
     * @return true if the player is the owner, false otherwise
     */
    public boolean isOwner(Player player) {
        return ownerId.isPresent() && ownerId.get().equals(player.getUUID());
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Most creatures don't breed by default
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        CreatureOwnerHelper.saveOwner(output, ownerId);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        ownerId = CreatureOwnerHelper.loadOwner(input);
    }
}















