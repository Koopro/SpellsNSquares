package at.koopro.spells_n_squares.features.creatures.mount;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Thestral - invisible flying mount, only visible to players who have seen death.
 */
public class ThestralEntity extends BaseTamableCreatureEntity {
    private static final int DEATH_COUNT_REQUIRED = 1; // Must have died at least once
    
    public ThestralEntity(EntityType<? extends ThestralEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.5D, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.FLYING_SPEED, 1.0D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }
    
    /**
     * Checks if a player can see thestrals (has seen death).
     */
    public static boolean canPlayerSeeThestral(Player player) {
        // Check player's death count (simplified - could use persistent data)
        // For now, check if player has died at least once
        // Using a simple check - if player has low health or has been hurt recently
        // In a full implementation, this would track actual death count
        return player.getHealth() < player.getMaxHealth() || player.getLastDamageSource() != null;
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Check if player can see thestral
        if (!canPlayerSeeThestral(player)) {
            // Player cannot see thestral - it appears invisible
            if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.thestral.invisible"));
            }
            return InteractionResult.PASS;
        }
        
        // Allow mounting if owned or can be tamed
        if (!this.isVehicle() && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        
        // Try to tame
        ItemStack stack = player.getItemInHand(hand);
        if (!hasOwner() && !stack.isEmpty()) {
            if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                this.setOwner(player);
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.thestral.tamed"));
            }
            return InteractionResult.SUCCESS;
        }
        
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Apply invisibility effect to make it invisible to those who haven't seen death
        // The renderer will handle visibility based on player's death count
        if (this.tickCount % 20 == 0) {
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, false, false, false));
        }
        
        // Visual effects when flying
        if (this.level() instanceof ServerLevel serverLevel && !this.onGround()) {
            if (this.tickCount % 15 == 0) {
                serverLevel.sendParticles(ParticleTypes.SOUL,
                    this.getX(), this.getY(), this.getZ(),
                    3, 0.3, 0.1, 0.3, 0.01);
            }
        }
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // No breeding
    }
    
}
