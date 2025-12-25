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
 * Dementor entity - a soul-sucking hostile creature that requires Patronus to defeat.
 */
public class DementorEntity extends PathfinderMob {
    private static final int SOUL_DRAIN_INTERVAL = 40; // Every 2 seconds
    private int soulDrainTimer = 0;
    
    public DementorEntity(EntityType<? extends DementorEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            soulDrainTimer++;
            
            // Drain souls from nearby players every interval
            if (soulDrainTimer >= SOUL_DRAIN_INTERVAL) {
                soulDrainTimer = 0;
                
                for (Player player : this.level().getEntitiesOfClass(Player.class, 
                        this.getBoundingBox().inflate(8.0))) {
                    // Apply negative effects
                    player.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
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
                    
                    // Deal damage
                    player.hurt(level().damageSources().magic(), 2.0f);
                    
                    // Visual effects
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        10, 0.5, 0.5, 0.5, 0.1);
                }
            }
        }
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












