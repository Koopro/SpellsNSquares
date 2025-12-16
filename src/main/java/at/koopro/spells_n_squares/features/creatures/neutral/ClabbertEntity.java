package at.koopro.spells_n_squares.features.creatures.neutral;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Clabbert - A tree-dwelling creature resembling a cross between a monkey and a frog.
 * Warts flash when danger approaches.
 */
public class ClabbertEntity extends PathfinderMob {
    private int flashTimer = 0;
    
    public ClabbertEntity(EntityType<? extends ClabbertEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Flash warts when danger approaches
        if (this.level() instanceof ServerLevel serverLevel) {
            flashTimer++;
            
            boolean dangerNearby = false;
            for (Player player : this.level().getEntitiesOfClass(Player.class, 
                    this.getBoundingBox().inflate(12.0))) {
                dangerNearby = true;
                break;
            }
            
            if (dangerNearby && flashTimer % 10 == 0) {
                // Flash effect
                serverLevel.sendParticles(ParticleTypes.GLOW,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
    
    // Clabberts don't breed
}



