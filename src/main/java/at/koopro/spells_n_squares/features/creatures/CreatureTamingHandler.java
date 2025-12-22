package at.koopro.spells_n_squares.features.creatures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Handles creature taming mechanics.
 */
public final class CreatureTamingHandler {
    private CreatureTamingHandler() {
    }
    
    /**
     * Attempts to tame a creature.
     * 
     * <p>Note: This is a placeholder implementation. Most tamable creatures
     * handle taming directly through their mobInteract() methods. This handler
     * could be extended to provide centralized taming logic if needed.
     * 
     * <p>See TODO_TRACKING.md for implementation details.
     * 
     * @param creature The creature to tame
     * @param player The player attempting to tame
     * @return True if taming was successful
     */
    public static boolean attemptTame(LivingEntity creature, Player player) {
        if (creature.level().isClientSide()) {
            return false; // Only handle on server
        }
        
        // Check if creature is tamable
        if (creature instanceof at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity tamable) {
            // Already has owner, cannot tame
            if (tamable.hasOwner()) {
                return false;
            }
            
            // Simple taming: set owner (creature-specific logic should be in mobInteract)
            tamable.setOwner(player);
            return true;
        }
        
        // For other entities, check if they use CreatureData component
        // This could be extended in the future for non-BaseTamableCreatureEntity creatures
        // For now, return false as they should handle taming themselves
        
        return false;
    }
    
    /**
     * Checks if a creature is tamed by a player.
     * 
     * <p>Note: This is a placeholder implementation. Most tamable creatures
     * extend BaseTamableCreatureEntity which provides isOwner() method.
     * 
     * <p>See TODO_TRACKING.md for implementation details.
     * 
     * @param creature The creature
     * @param player The player
     * @return True if the creature is tamed by the player
     */
    public static boolean isTamedBy(LivingEntity creature, Player player) {
        // Check for BaseTamableCreatureEntity first
        if (creature instanceof at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity tamable) {
            return tamable.isOwner(player);
        }
        
        // For other entities, check CreatureData component if available
        // This could be extended in the future for non-BaseTamableCreatureEntity creatures
        // For now, return false as they should handle ownership themselves
        
        return false;
    }
}


