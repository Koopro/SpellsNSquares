package at.koopro.spells_n_squares.features.creatures.neutral;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
 * Streeler - A giant snail that changes color hourly and leaves a poisonous trail.
 */
public class StreelerEntity extends PathfinderMob {
    private int colorTimer = 0;
    private static final int COLOR_CHANGE_INTERVAL = 1200; // Every minute (20 ticks per second)
    
    public StreelerEntity(EntityType<? extends StreelerEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.5D)); // Very slow
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.1D); // Very slow
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Change color hourly (simplified to every minute)
        colorTimer++;
        if (colorTimer >= COLOR_CHANGE_INTERVAL) {
            colorTimer = 0;
            // Color change is visual only - handled by renderer
        }
        
        // Poisonous trail - apply poison to players walking on trail
        if (this.level() instanceof ServerLevel serverLevel) {
            for (Player player : this.level().getEntitiesOfClass(Player.class, 
                    this.getBoundingBox().inflate(1.0))) {
                if (player.onGround() && player.distanceToSqr(this) < 1.0) {
                    player.addEffect(new MobEffectInstance(
                        MobEffects.POISON,
                        100,
                        0,
                        false,
                        true,
                        true
                    ));
                }
            }
        }
    }
    
    // Streelers don't breed
}



