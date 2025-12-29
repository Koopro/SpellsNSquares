package at.koopro.spells_n_squares.features.creatures.hostile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
 * Erkling - An elf-like creature that lures children with music before eating them.
 */
public class ErklingEntity extends PathfinderMob {
    private int musicTimer = 0;
    private static final int MUSIC_INTERVAL = 100; // Every 5 seconds
    
    public ErklingEntity(EntityType<? extends ErklingEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Lure with music - apply enchantment effect
        if (!this.level().isClientSide()) {
            musicTimer++;
            
            if (musicTimer >= MUSIC_INTERVAL) {
                musicTimer = 0;
                
                // Play music and enchant nearby players
                this.level().playSound(null, this.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(), 
                    SoundSource.NEUTRAL, 1.0f, 1.2f);
                
                for (Player player : this.level().getEntitiesOfClass(Player.class, 
                        this.getBoundingBox().inflate(12.0))) {
                    // Lure effect - slow movement toward erkling
                    player.addEffect(new MobEffectInstance(
                        MobEffects.SLOWNESS,
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
    
    // Erklings don't breed
}















