package at.koopro.spells_n_squares.features.creatures.base;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Base class for mountable creature entities.
 * Extends BaseTamableCreatureEntity and provides common mounting interaction logic.
 */
public abstract class BaseMountEntity extends BaseTamableCreatureEntity {
    
    protected BaseMountEntity(EntityType<? extends BaseMountEntity> type, Level level) {
        super(type, level);
    }
    
    /**
     * Handles mounting interaction for owned creatures.
     * Checks if the creature is owned, not already a vehicle, and player is not sneaking.
     * 
     * @param player The player attempting to mount
     * @return InteractionResult.SUCCESS if mounting should occur, InteractionResult.PASS otherwise
     */
    protected InteractionResult handleMountInteraction(Player player) {
        if (hasOwner() && !this.isVehicle() && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    /**
     * Checks if the player can mount this creature.
     * Override this method for custom mounting conditions (e.g., respect requirements).
     * 
     * @param player The player attempting to mount
     * @return true if the player can mount, false otherwise
     */
    protected boolean canPlayerMount(Player player) {
        return hasOwner() && !this.isVehicle() && !player.isShiftKeyDown();
    }
}











