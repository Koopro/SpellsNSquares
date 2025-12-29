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
     * <p>Provides centralized taming logic for creatures that extend BaseTamableCreatureEntity.
     * Most tamable creatures handle taming directly through their mobInteract() methods,
     * but this handler can be used for programmatic taming or as a fallback.
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
            
            // Check if creature is already tamed by this player (shouldn't happen, but safety check)
            if (tamable.isOwner(player)) {
                return true; // Already tamed
            }
            
            // Set owner - creature-specific requirements (items, conditions) should be checked
            // in mobInteract() before calling this method
            tamable.setOwner(player);
            
            // Play taming sound
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                creature.level().playSound(null, creature.blockPosition(), 
                    net.minecraft.sounds.SoundEvents.VILLAGER_YES, 
                    net.minecraft.sounds.SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
            
            return true;
        }
        
        // For other entities, they should handle taming themselves via their own systems
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












