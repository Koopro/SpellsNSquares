package at.koopro.spells_n_squares.features.creatures.neutral;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
 * Ghoul - A slimy, buck-toothed creature that often inhabits attics and barns.
 * Harmless but noisy.
 */
public class GhoulEntity extends PathfinderMob {
    private int noiseTimer = 0;
    private static final int NOISE_INTERVAL = 200; // Every 10 seconds
    
    public GhoulEntity(EntityType<? extends GhoulEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 15.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Make noise periodically
        if (!this.level().isClientSide()) {
            noiseTimer++;
            
            if (noiseTimer >= NOISE_INTERVAL) {
                noiseTimer = 0;
                this.level().playSound(null, this.blockPosition(), SoundEvents.ZOMBIE_AMBIENT, 
                    SoundSource.NEUTRAL, 0.5f, 1.2f);
            }
        }
    }
    
    // Ghouls don't breed
}











