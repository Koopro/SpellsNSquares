package at.koopro.spells_n_squares.features.creatures.hostile;

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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Lethifold - A dangerous, black, cloak-like creature that suffocates its victims in their sleep.
 * Requires Patronus charm to repel.
 */
public class LethifoldEntity extends PathfinderMob {
    private static final int SUFFOCATION_INTERVAL = 40; // Every 2 seconds
    
    public LethifoldEntity(EntityType<? extends LethifoldEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Suffocation attack on nearby players
        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % SUFFOCATION_INTERVAL == 0) {
            for (Player player : this.level().getEntitiesOfClass(Player.class, 
                    this.getBoundingBox().inflate(3.0))) {
                // Suffocation effect
                player.addEffect(new MobEffectInstance(
                    MobEffects.WITHER,
                    100,
                    0,
                    false,
                    true,
                    true
                ));
                
                player.hurt(level().damageSources().magic(), 2.0f);
            }
        }
    }
    
    // Lethifolds don't breed
}



