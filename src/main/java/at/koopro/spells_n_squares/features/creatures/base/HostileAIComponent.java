package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

/**
 * Component for hostile AI functionality.
 * Provides attack AI goals and aggressive behavior patterns.
 * Uses composition instead of inheritance for flexibility.
 */
public class HostileAIComponent {
    private final PathfinderMob creature;
    private AttackCallback attackCallback;
    
    /**
     * Callback for attack-specific behavior.
     */
    public interface AttackCallback {
        float getAttackDamage();
        double getAttackSpeed();
    }
    
    public HostileAIComponent(PathfinderMob creature) {
        this.creature = creature;
    }
    
    /**
     * Sets the callback for attack-specific behavior.
     */
    public void setAttackCallback(AttackCallback callback) {
        this.attackCallback = callback;
    }
    
    /**
     * Registers hostile AI goals.
     * Should be called from the creature's registerCreatureGoals method.
     */
    public void registerHostileGoals() {
        DevLogger.logMethodEntry(creature, "registerHostileGoals");
        
        // Hostile-specific goals
        creature.goalSelector.addGoal(2, new MeleeAttackGoal(creature, 1.0, false));
        creature.goalSelector.addGoal(7, new LookAtPlayerGoal(creature, Player.class, 8.0f));
        
        // Target goals
        creature.targetSelector.addGoal(1, new HurtByTargetGoal(creature));
        creature.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(creature, Player.class, true));
        
        DevLogger.logMethodExit(creature, "registerHostileGoals");
    }
    
    /**
     * Gets the attack damage for this creature.
     * Calls the attack callback if set, otherwise returns default damage.
     * 
     * @return The attack damage
     */
    public float getAttackDamage() {
        if (attackCallback != null) {
            return attackCallback.getAttackDamage();
        }
        return 2.0f; // Default damage
    }
    
    /**
     * Gets the attack speed for this creature.
     * Calls the attack callback if set, otherwise returns default speed.
     * 
     * @return The attack speed multiplier
     */
    public double getAttackSpeed() {
        if (attackCallback != null) {
            return attackCallback.getAttackSpeed();
        }
        return 1.0; // Default speed
    }
}

