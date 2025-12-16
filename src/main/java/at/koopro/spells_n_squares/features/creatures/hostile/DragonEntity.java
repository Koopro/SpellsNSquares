package at.koopro.spells_n_squares.features.creatures.hostile;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Dragon - powerful boss creature with fire breath and high health.
 */
public class DragonEntity extends PathfinderMob {
    private static final int FIRE_BREATH_INTERVAL = 80; // Every 4 seconds
    private int fireBreathTimer = 0;
    
    public DragonEntity(EntityType<? extends DragonEntity> type, Level level) {
        super(type, level);
        // Size is set in EntityType.Builder
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 200.0D) // Very high health
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.ATTACK_DAMAGE, 15.0D)
            .add(Attributes.FOLLOW_RANGE, 48.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            fireBreathTimer++;
            
            // Fire breath attack
            if (fireBreathTimer >= FIRE_BREATH_INTERVAL) {
                fireBreathTimer = 0;
                performFireBreath(serverLevel);
            }
            
            // Fire particles
            if (this.tickCount % 5 == 0) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    3, 0.5, 0.5, 0.5, 0.02);
            }
        }
    }
    
    /**
     * Performs fire breath attack in a cone in front of the dragon.
     */
    private void performFireBreath(ServerLevel level) {
        // Get direction dragon is facing
        double dx = this.getLookAngle().x;
        double dz = this.getLookAngle().z;
        
        // Create fire in a cone
        for (int i = 0; i < 5; i++) {
            for (int j = -2; j <= 2; j++) {
                double distance = 3 + i * 2;
                double offsetX = dx * distance + (j * 0.5 * -dz);
                double offsetZ = dz * distance + (j * 0.5 * dx);
                
                BlockPos firePos = this.blockPosition().offset(
                    (int) offsetX,
                    1,
                    (int) offsetZ
                );
                
                // Place fire block if air
                if (level.getBlockState(firePos).isAir() && 
                    level.getBlockState(firePos.below()).isSolid()) {
                    level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
                }
                
                // Damage entities in area
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                        new net.minecraft.world.phys.AABB(firePos).inflate(1.0))) {
                    if (entity != this) {
                        entity.setRemainingFireTicks(100); // 5 seconds (20 ticks per second)
                        entity.hurt(level.damageSources().onFire(), 5.0f);
                        
                        // Visual effect
                        level.sendParticles(ParticleTypes.FLAME,
                            entity.getX(), entity.getY() + 1.0, entity.getZ(),
                            10, 0.3, 0.3, 0.3, 0.05);
                    }
                }
            }
        }
        
        level.playSound(null, this.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.0f, 0.5f);
    }
    
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        
        // TODO: Re-enable when DRAGON_SCALE item is implemented
        // Drop dragon scales
        /*
        int scaleCount = 3 + this.level().random.nextInt(5);
        this.spawnAtLocation(level, new ItemStack(
            at.koopro.spells_n_squares.core.registry.ModItems.DRAGON_SCALE.get(), scaleCount));
        */
    }
    
    @Override
    public boolean fireImmune() {
        return true; // Dragons are immune to fire
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
    }
}
