package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;

/**
 * Component for neutral AI functionality.
 * Provides neutral behavior - creatures that don't attack unless provoked.
 * Uses composition instead of inheritance for flexibility.
 */
public class NeutralAIComponent {
    private final PathfinderMob creature;
    private AvoidanceCallback avoidanceCallback;
    
    /**
     * Callback for avoidance behavior.
     */
    @FunctionalInterface
    public interface AvoidanceCallback {
        boolean shouldAvoidPlayers();
    }
    
    public NeutralAIComponent(PathfinderMob creature) {
        this.creature = creature;
    }
    
    /**
     * Sets the callback for avoidance behavior.
     */
    public void setAvoidanceCallback(AvoidanceCallback callback) {
        this.avoidanceCallback = callback;
    }
    
    /**
     * Registers neutral AI goals.
     * Should be called from the creature's registerCreatureGoals method.
     */
    public void registerNeutralGoals() {
        DevLogger.logMethodEntry(creature, "registerNeutralGoals");
        
        // Check if should avoid players
        boolean shouldAvoid = shouldAvoidPlayers();
        if (shouldAvoid) {
            // Neutral creatures avoid players by default
            creature.goalSelector.addGoal(3, new AvoidEntityGoal<>(creature, Player.class, 8.0f, 1.2, 1.5));
        }
        
        DevLogger.logMethodExit(creature, "registerNeutralGoals");
    }
    
    /**
     * Checks if this creature should avoid players.
     * Calls the avoidance callback if set, otherwise returns true.
     * 
     * @return true if should avoid players
     */
    public boolean shouldAvoidPlayers() {
        if (avoidanceCallback != null) {
            return avoidanceCallback.shouldAvoidPlayers();
        }
        return true; // Default: avoid players
    }
}


