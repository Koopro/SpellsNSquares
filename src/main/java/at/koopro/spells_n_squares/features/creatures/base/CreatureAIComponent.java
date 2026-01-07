package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.performance.TickOptimizer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

/**
 * Component for creature AI functionality.
 * Provides common AI goals and behavior patterns with tick-based optimization.
 * Uses composition instead of inheritance for flexibility.
 */
public class CreatureAIComponent {
    private final PathfinderMob creature;
    private GoalsCallback goalsCallback;
    private DataCallback dataCallback;
    private final String aiOperationId;
    private static final int DEFAULT_AI_INTERVAL = 2; // Run AI every 2 ticks for optimization
    
    /**
     * Callback for registering creature-specific AI goals.
     */
    @FunctionalInterface
    public interface GoalsCallback {
        void registerCreatureGoals(PathfinderMob creature);
    }
    
    /**
     * Callback for defining creature-specific synched data.
     */
    @FunctionalInterface
    public interface DataCallback {
        void defineCreatureSynchedData(SynchedEntityData.Builder builder);
    }
    
    public CreatureAIComponent(PathfinderMob creature) {
        this.creature = creature;
        // Create unique operation ID for this creature's AI
        this.aiOperationId = "creature_ai_" + creature.getUUID().toString();
    }
    
    /**
     * Sets the callback for registering creature-specific goals.
     */
    public void setGoalsCallback(GoalsCallback callback) {
        this.goalsCallback = callback;
    }
    
    /**
     * Sets the callback for defining creature-specific synched data.
     */
    public void setDataCallback(DataCallback callback) {
        this.dataCallback = callback;
    }
    
    /**
     * Registers common AI goals for all creatures.
     */
    public void registerCommonGoals() {
        DevLogger.logMethodEntry(creature, "registerCommonGoals");
        
        // Common goals for all creatures
        creature.goalSelector.addGoal(0, new FloatGoal(creature));
        creature.goalSelector.addGoal(1, new PanicGoal(creature, 1.25));
        creature.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(creature, 1.0));
        creature.goalSelector.addGoal(7, new RandomLookAroundGoal(creature));
        
        // Call creature-specific goals callback
        if (goalsCallback != null) {
            goalsCallback.registerCreatureGoals(creature);
        }
        
        DevLogger.logMethodExit(creature, "registerCommonGoals");
    }
    
    /**
     * Checks if AI should execute this tick based on optimization settings.
     * Uses TickOptimizer to throttle AI execution for better performance.
     * 
     * @param currentTick The current game tick
     * @param intervalTicks The interval in ticks (defaults to DEFAULT_AI_INTERVAL)
     * @return true if AI should execute this tick
     */
    public boolean shouldExecuteAI(long currentTick, int intervalTicks) {
        return TickOptimizer.shouldRun(aiOperationId, intervalTicks, currentTick);
    }
    
    /**
     * Checks if AI should execute this tick using default interval.
     * 
     * @param currentTick The current game tick
     * @return true if AI should execute this tick
     */
    public boolean shouldExecuteAI(long currentTick) {
        return shouldExecuteAI(currentTick, DEFAULT_AI_INTERVAL);
    }
    
    /**
     * Resets the AI tick counter for this creature.
     * Useful when the creature's state changes significantly.
     */
    public void resetAITickCounter() {
        TickOptimizer.reset(aiOperationId);
    }
    
    /**
     * Defines creature-specific synched entity data.
     * Calls the data callback if set.
     */
    public void defineCreatureSynchedData(SynchedEntityData.Builder builder) {
        if (dataCallback != null) {
            dataCallback.defineCreatureSynchedData(builder);
        }
    }
}


