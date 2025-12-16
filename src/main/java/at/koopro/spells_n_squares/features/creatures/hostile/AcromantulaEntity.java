package at.koopro.spells_n_squares.features.creatures.hostile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Acromantula - large spider enemy with web attacks and poison.
 */
public class AcromantulaEntity extends PathfinderMob {
    private static final int WEB_SPAWN_INTERVAL = 100; // Every 5 seconds
    private int webTimer = 0;
    
    public AcromantulaEntity(EntityType<? extends AcromantulaEntity> type, Level level) {
        super(type, level);
        // Size is set in EntityType.Builder
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 60.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
    
    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        
        if (hurt && target instanceof Player player) {
            // Apply poison effect
            player.addEffect(new MobEffectInstance(
                MobEffects.POISON,
                200, // 10 seconds
                1,   // Level 2
                false,
                true,
                true
            ));
            
            // Visual effect
            level.sendParticles(ParticleTypes.ITEM_SLIME,
                player.getX(), player.getY() + 1.0, player.getZ(),
                5, 0.3, 0.3, 0.3, 0.05);
        }
        
        return hurt;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            webTimer++;
            
            // Spawn web blocks periodically
            if (webTimer >= WEB_SPAWN_INTERVAL) {
                webTimer = 0;
                
                BlockPos pos = this.blockPosition();
                BlockPos webPos = pos.relative(this.getDirection(), 2);
                
                // Place web if position is air
                if (this.level().getBlockState(webPos).isAir()) {
                    BlockState webState = Blocks.COBWEB.defaultBlockState();
                    this.level().setBlock(webPos, webState, 3);
                    
                    this.level().playSound(null, webPos, SoundEvents.SPIDER_AMBIENT, SoundSource.NEUTRAL, 0.5f, 1.0f);
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
