package at.koopro.spells_n_squares.features.creatures.spiritual;

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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Veela - Beautiful, semi-human magical beings who can enchant men with their dance.
 */
public class VeelaEntity extends PathfinderMob {
    private boolean isAngered = false;
    private int danceTimer = 0;
    private static final int DANCE_INTERVAL = 100; // Every 5 seconds
    
    public VeelaEntity(EntityType<? extends VeelaEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Enchantment dance
        if (!this.level().isClientSide()) {
            danceTimer++;
            
            if (danceTimer >= DANCE_INTERVAL) {
                danceTimer = 0;
                
                // Enchant nearby players with dance
                for (Player player : this.level().getEntitiesOfClass(Player.class, 
                        this.getBoundingBox().inflate(10.0))) {
                    if (!isAngered) {
                        // Enchantment effect
                        player.addEffect(new MobEffectInstance(
                            MobEffects.LEVITATION,
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
    }
    
    // Veela transform when angered - handled in tick()
    // Veela don't breed
}






