package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Demiguise - A peaceful, ape-like creature that can turn invisible and has precognitive abilities.
 */
public class DemiguiseEntity extends BaseTamableCreatureEntity {
    private boolean isInvisible = false;
    private int invisibilityCooldown = 0;
    
    public DemiguiseEntity(EntityType<? extends DemiguiseEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Toggle invisibility when threatened
        if (!this.level().isClientSide()) {
            if (invisibilityCooldown > 0) {
                invisibilityCooldown--;
            }
            
            // Turn invisible when players approach (precognition)
            if (!isInvisible && invisibilityCooldown == 0) {
                for (Player player : this.level().getEntitiesOfClass(Player.class, 
                        this.getBoundingBox().inflate(8.0))) {
                    if (!isOwner(player)) {
                        isInvisible = true;
                        invisibilityCooldown = 200; // 10 seconds cooldown
                        break;
                    }
                }
            } else if (isInvisible && invisibilityCooldown == 0) {
                // Turn visible again after cooldown
                isInvisible = false;
            }
        }
        
        // Apply invisibility effect
        if (isInvisible) {
            this.setInvisible(true);
        } else {
            this.setInvisible(false);
        }
    }
    
    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false; // Demiguises don't eat standard food
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Demiguises don't breed
    }
}
















