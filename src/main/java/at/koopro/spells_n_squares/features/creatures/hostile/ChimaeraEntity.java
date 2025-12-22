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
 * Chimaera - multi-headed beast from HP canon.
 * Dangerous hybrid creature with multiple attack types.
 */
public class ChimaeraEntity extends PathfinderMob {
    private static final int SPECIAL_ATTACK_INTERVAL = 120; // Every 6 seconds
    private int specialAttackTimer = 0;
    
    public ChimaeraEntity(EntityType<? extends ChimaeraEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 150.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.ATTACK_DAMAGE, 12.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            specialAttackTimer++;
            
            // Multi-headed special attack
            if (specialAttackTimer >= SPECIAL_ATTACK_INTERVAL && this.getTarget() != null) {
                specialAttackTimer = 0;
                performMultiHeadAttack(serverLevel);
            }
        }
    }
    
    /**
     * Performs a multi-headed attack with different effects.
     */
    private void performMultiHeadAttack(ServerLevel level) {
        if (this.getTarget() instanceof LivingEntity target) {
            // Different heads apply different effects
            int attackType = this.random.nextInt(3);
            
            switch (attackType) {
                case 0: // Fire breath
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                    level.sendParticles(ParticleTypes.FLAME,
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        15, 0.5, 0.5, 0.5, 0.05);
                    break;
                case 1: // Poison breath
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                    level.sendParticles(ParticleTypes.ITEM_SLIME,
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        15, 0.5, 0.5, 0.5, 0.05);
                    break;
                case 2: // Stunning roar
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
                    level.sendParticles(ParticleTypes.EXPLOSION,
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        10, 0.5, 0.5, 0.5, 0.1);
                    break;
            }
            
            level.playSound(null, target.blockPosition(),
                SoundEvents.IRON_GOLEM_HURT, SoundSource.HOSTILE, 1.0f, 0.5f);
        }
    }
    
    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        // Chimaera attacks are powerful
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
        return hurt;
    }
}







