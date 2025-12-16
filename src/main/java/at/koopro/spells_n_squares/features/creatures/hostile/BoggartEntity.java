package at.koopro.spells_n_squares.features.creatures.hostile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Boggart - shape-shifting fear entity that transforms into player's fear.
 * Can be defeated with Riddikulus spell (simplified as taking damage from laughter).
 */
public class BoggartEntity extends PathfinderMob {
    private static final int FEAR_EFFECT_INTERVAL = 60; // Every 3 seconds
    private int fearTimer = 0;
    private int laughterDamage = 0; // Damage from Riddikulus spell
    private static final int LAUGHTER_THRESHOLD = 20; // Damage needed to defeat
    
    public BoggartEntity(EntityType<? extends BoggartEntity> type, Level level) {
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
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }
    
    /**
     * Applies Riddikulus spell damage (laughter).
     */
    public void applyRiddikulus(int damage) {
        laughterDamage += damage;
        
        if (this.level() instanceof ServerLevel serverLevel) {
            // Visual effect
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                this.getX(), this.getY() + 1.0, this.getZ(),
                10, 0.5, 0.5, 0.5, 0.1);
            
            this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.2f);
        }
        
        // Defeat if enough laughter damage
        if (laughterDamage >= LAUGHTER_THRESHOLD) {
            this.hurt(this.level().damageSources().magic(), 100.0f); // Instant defeat
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            fearTimer++;
            
            // Apply fear effects to nearby players
            if (fearTimer >= FEAR_EFFECT_INTERVAL) {
                fearTimer = 0;
                
                for (Player player : this.level().getEntitiesOfClass(Player.class,
                        this.getBoundingBox().inflate(8.0))) {
                    // Apply fear effects (weakness, slowness, darkness)
                    player.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        100,
                        1,
                        false,
                        true,
                        true
                    ));
                    
                    player.addEffect(new MobEffectInstance(
                        MobEffects.SLOWNESS,
                        100,
                        1,
                        false,
                        true,
                        true
                    ));
                    
                    player.addEffect(new MobEffectInstance(
                        MobEffects.DARKNESS,
                        100,
                        0,
                        false,
                        true,
                        true
                    ));
                    
                    // Visual effect
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        5, 0.3, 0.3, 0.3, 0.05);
                }
            }
            
            // Shape-shifting visual effect (particles)
            if (this.tickCount % 20 == 0) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    3, 0.3, 0.3, 0.3, 0.02);
            }
        }
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("LaughterDamage", laughterDamage);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        laughterDamage = input.getIntOr("LaughterDamage", 0);
    }
}
