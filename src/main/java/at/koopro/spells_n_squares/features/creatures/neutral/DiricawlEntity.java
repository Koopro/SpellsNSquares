package at.koopro.spells_n_squares.features.creatures.neutral;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Diricawl - A plump, flightless bird capable of vanishing and reappearing elsewhere.
 * Known to Muggles as the dodo.
 */
public class DiricawlEntity extends PathfinderMob {
    private int vanishCooldown = 0;
    
    public DiricawlEntity(EntityType<? extends DiricawlEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 6.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Vanishing ability when threatened
        if (!this.level().isClientSide() && vanishCooldown == 0) {
            for (Player player : this.level().getEntitiesOfClass(Player.class, 
                    this.getBoundingBox().inflate(8.0))) {
                // Teleport away (vanishing)
                if (this.level() instanceof ServerLevel serverLevel) {
                    double newX = this.getX() + (serverLevel.random.nextDouble() - 0.5) * 16.0;
                    double newY = this.getY();
                    double newZ = this.getZ() + (serverLevel.random.nextDouble() - 0.5) * 16.0;
                    this.teleportTo(newX, newY, newZ);
                    vanishCooldown = 200; // 10 second cooldown
                    break;
                }
            }
        }
        
        if (vanishCooldown > 0) {
            vanishCooldown--;
        }
    }
    
    // Diricawls don't breed
}



