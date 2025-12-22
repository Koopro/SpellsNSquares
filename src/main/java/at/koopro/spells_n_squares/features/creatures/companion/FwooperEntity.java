package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
 * Fwooper - A brightly colored bird whose song can drive listeners insane.
 * Must be silenced with charms.
 */
public class FwooperEntity extends BaseTamableCreatureEntity {
    private int songTimer = 0;
    private static final int SONG_INTERVAL = 200; // Every 10 seconds
    
    public FwooperEntity(EntityType<? extends FwooperEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 6.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Sing periodically - causes confusion to nearby players
        if (this.level() instanceof ServerLevel serverLevel && !this.isOrderedToSit()) {
            songTimer++;
            
            if (songTimer >= SONG_INTERVAL) {
                songTimer = 0;
                
                // Apply confusion effect to nearby players (madness-inducing song)
                for (Player player : this.level().getEntitiesOfClass(Player.class, 
                        this.getBoundingBox().inflate(8.0))) {
                    if (!isOwner(player)) {
                        player.addEffect(new MobEffectInstance(
                            MobEffects.NAUSEA,
                            200, // 10 seconds
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
    
    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false; // Fwoopers don't eat standard food
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Fwoopers don't breed
    }
}






