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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Basilisk - giant serpent with petrifying gaze (Chamber of Secrets boss).
 * Extremely dangerous creature that can petrify players with its gaze.
 */
public class BasiliskEntity extends PathfinderMob {
    private static final int PETRIFY_INTERVAL = 100; // Every 5 seconds
    private int petrifyTimer = 0;
    private static final double PETRIFY_RANGE = 16.0;
    
    public BasiliskEntity(EntityType<? extends BasiliskEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 300.0D) // Very high health - boss level
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.ATTACK_DAMAGE, 20.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            petrifyTimer++;
            
            // Petrifying gaze attack
            if (petrifyTimer >= PETRIFY_INTERVAL && this.getTarget() != null) {
                petrifyTimer = 0;
                performPetrifyingGaze(serverLevel);
            }
            
            // Visual effect - glowing eyes
            if (this.tickCount % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    this.getX(), this.getY() + 2.0, this.getZ(),
                    5, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }
    
    /**
     * Performs the petrifying gaze attack on nearby players.
     */
    private void performPetrifyingGaze(ServerLevel level) {
        AABB gazeArea = new AABB(this.blockPosition()).inflate(PETRIFY_RANGE);
        var entities = level.getEntitiesOfClass(LivingEntity.class, gazeArea,
            entity -> entity != this && entity.isAlive() && entity instanceof Player);
        
        for (LivingEntity target : entities) {
            // Check if target is looking at the Basilisk (simplified - just check if in range)
            Vec3 toTarget = target.position().subtract(this.position());
            double distance = toTarget.length();
            
            if (distance <= PETRIFY_RANGE) {
                // Apply petrification effects
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 4)); // Extreme slowness
                target.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 200, 4)); // Mining fatigue
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2)); // Weakness
                
                // Visual effect
                level.sendParticles(ParticleTypes.ITEM_SLIME,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1);
                
                level.playSound(null, target.blockPosition(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.0f, 0.5f);
            }
        }
    }
    
    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        // Basilisk attacks are very powerful
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
        }
        return hurt;
    }
}












