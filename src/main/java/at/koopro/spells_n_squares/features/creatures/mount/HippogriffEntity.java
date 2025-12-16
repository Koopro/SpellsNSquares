package at.koopro.spells_n_squares.features.creatures.mount;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Hippogriff - a mountable flying creature that requires respect (bowing) before taming.
 */
public class HippogriffEntity extends BaseTamableCreatureEntity {
    private boolean hasRespected = false; // Player must bow before mounting
    private int respectCooldown = 0;
    
    public HippogriffEntity(EntityType<? extends HippogriffEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 50.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.FLYING_SPEED, 0.8D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is bowing (sneaking)
        if (player.isShiftKeyDown() && !hasRespected && !hasOwner()) {
            // Player is showing respect
            hasRespected = true;
            respectCooldown = 100; // 5 seconds
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.hippogriff.respected"));
            }
            this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        
        // Allow mounting if respected or owned
        if ((hasRespected || hasOwner()) && !this.isVehicle() && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        
        // Try to tame if respected
        if (hasRespected && !hasOwner() && !stack.isEmpty()) {
            // Tame with any item (could be specific item in future)
            if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                this.setOwner(player);
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.hippogriff.tamed"));
            }
            return InteractionResult.SUCCESS;
        }
        
        if (!hasRespected && !hasOwner() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.hippogriff.need_respect"));
        }
        
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (respectCooldown > 0) {
            respectCooldown--;
        }
        
        // Visual effects when flying
        if (this.level() instanceof ServerLevel serverLevel && !this.onGround()) {
            if (this.tickCount % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY(), this.getZ(),
                    2, 0.2, 0.1, 0.2, 0.01);
            }
        }
    }
    
    @Override
    public void setOwner(Player player) {
        super.setOwner(player);
        this.hasRespected = true; // Once tamed, always respected
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // No breeding
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("HasRespected", hasRespected);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        hasRespected = input.getBooleanOr("HasRespected", hasOwner()); // If owned, always respected
    }
}
