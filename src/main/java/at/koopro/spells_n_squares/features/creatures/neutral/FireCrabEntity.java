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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Fire Crab - A large, turtle-like creature that shoots flames from its rear end.
 */
public class FireCrabEntity extends PathfinderMob {
    private int flameTimer = 0;
    private static final int FLAME_INTERVAL = 100; // Every 5 seconds
    
    public FireCrabEntity(EntityType<? extends FireCrabEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.15D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Shoot flames from rear periodically
        if (this.level() instanceof ServerLevel serverLevel) {
            flameTimer++;
            
            if (flameTimer >= FLAME_INTERVAL) {
                flameTimer = 0;
                
                // Flame particles from rear
                double rearX = this.getX() - Math.cos(this.getYRot() * Math.PI / 180.0) * 0.5;
                double rearZ = this.getZ() - Math.sin(this.getYRot() * Math.PI / 180.0) * 0.5;
                
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    rearX, this.getY() + 0.3, rearZ,
                    10, 0.2, 0.1, 0.2, 0.05);
            }
        }
    }
    
    // Fire Crabs don't breed
}






