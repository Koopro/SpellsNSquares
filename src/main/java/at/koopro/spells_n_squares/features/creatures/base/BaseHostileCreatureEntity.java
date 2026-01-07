package at.koopro.spells_n_squares.features.creatures.base;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * Base class for hostile creature entities.
 * Provides attack AI goals and aggressive behavior patterns.
 * Uses composition with HostileAIComponent for flexibility.
 * 
 * <p>Subclasses should override {@link #registerCreatureGoals()} to add creature-specific goals
 * and call {@code super.registerCreatureGoals()} to include hostile behaviors.
 */
public abstract class BaseHostileCreatureEntity extends BaseCreatureEntity {
    protected final HostileAIComponent hostileAIComponent;
    
    public BaseHostileCreatureEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        DevLogger.logMethodEntry(this, "BaseHostileCreatureEntity");
        
        this.hostileAIComponent = new HostileAIComponent(this);
        hostileAIComponent.setAttackCallback(new HostileAIComponent.AttackCallback() {
            @Override
            public float getAttackDamage() {
                return BaseHostileCreatureEntity.this.getAttackDamage();
            }
            
            @Override
            public double getAttackSpeed() {
                return BaseHostileCreatureEntity.this.getAttackSpeed();
            }
        });
    }
    
    @Override
    protected void registerCreatureGoals() {
        super.registerCreatureGoals();
        hostileAIComponent.registerHostileGoals();
    }
    
    /**
     * Gets the attack damage for this creature.
     * Subclasses should override to provide creature-specific damage.
     * 
     * @return The attack damage
     */
    protected float getAttackDamage() {
        return 2.0f; // Default damage
    }
    
    /**
     * Gets the attack speed for this creature.
     * Subclasses should override to provide creature-specific speed.
     * 
     * @return The attack speed multiplier
     */
    protected double getAttackSpeed() {
        return 1.0; // Default speed
    }
}

