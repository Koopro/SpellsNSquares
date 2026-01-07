package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * Base class for neutral creature entities.
 * Provides neutral behavior - creatures that don't attack unless provoked.
 * Uses composition with NeutralAIComponent for flexibility.
 * 
 * <p>Subclasses should override {@link #registerCreatureGoals()} to add creature-specific goals
 * and call {@code super.registerCreatureGoals()} to include neutral behaviors.
 */
public abstract class BaseNeutralCreatureEntity extends BaseCreatureEntity {
    protected final NeutralAIComponent neutralAIComponent;
    
    public BaseNeutralCreatureEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseNeutralCreatureEntity");
        
        this.neutralAIComponent = new NeutralAIComponent(this);
        neutralAIComponent.setAvoidanceCallback(() -> shouldAvoidPlayers());
    }
    
    @Override
    protected void registerCreatureGoals() {
        super.registerCreatureGoals();
        neutralAIComponent.registerNeutralGoals();
    }
    
    /**
     * Checks if this creature should avoid players.
     * Subclasses can override to change avoidance behavior.
     * 
     * @return true if should avoid players
     */
    protected boolean shouldAvoidPlayers() {
        return true; // Default: avoid players
    }
}

