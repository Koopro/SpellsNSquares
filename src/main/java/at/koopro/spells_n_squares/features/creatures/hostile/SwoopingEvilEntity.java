package at.koopro.spells_n_squares.features.creatures.hostile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Swooping Evil - a venomous flying creature from Fantastic Beasts.
 * Used for combat, can poison enemies.
 */
public class SwoopingEvilEntity extends Monster {
    
    public SwoopingEvilEntity(EntityType<? extends SwoopingEvilEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.FLYING_SPEED, 1.2D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Swooping Evil flies
        if (!this.onGround() && this.level() instanceof ServerLevel serverLevel) {
            // Visual effect when flying
            if (this.tickCount % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    2, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }
    
    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        // Swooping Evil attacks apply poison
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            
            // Visual effect
            level.sendParticles(ParticleTypes.ITEM_SLIME,
                target.getX(), target.getY() + 1.0, target.getZ(),
                10, 0.3, 0.3, 0.3, 0.05);
            
            level.playSound(null, target.blockPosition(),
                SoundEvents.SPIDER_AMBIENT, SoundSource.HOSTILE, 0.8f, 1.5f);
        }
        return hurt;
    }
    
    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        return new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level);
    }
}












