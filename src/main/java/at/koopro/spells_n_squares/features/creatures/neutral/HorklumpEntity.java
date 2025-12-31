package at.koopro.spells_n_squares.features.creatures.neutral;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Horklump - A pink, bristly creature that resembles a mushroom.
 * Fast reproduction, burrowing, stationary.
 */
public class HorklumpEntity extends PathfinderMob {
    
    public HorklumpEntity(EntityType<? extends HorklumpEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        // Stationary creature - minimal AI
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 1.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D); // Stationary
    }
    
    // Horklumps reproduce differently (fast reproduction)
}
















