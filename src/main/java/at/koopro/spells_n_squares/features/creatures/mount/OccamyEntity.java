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
 * Occamy - a shape-shifting serpent from Fantastic Beasts.
 * Can grow/shrink and is protective. Mountable flying creature.
 */
public class OccamyEntity extends BaseTamableCreatureEntity {
    private float currentSize = 1.0f; // Can grow/shrink
    private static final float MIN_SIZE = 0.5f;
    private static final float MAX_SIZE = 2.0f;
    private int sizeChangeTimer = 0;
    
    public OccamyEntity(EntityType<? extends OccamyEntity> type, Level level) {
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
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FLYING_SPEED, 0.7D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Allow mounting if owned
        if (hasOwner() && !this.isVehicle() && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        
        // Try to tame with eggs (protective of eggs)
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(net.minecraft.world.item.Items.EGG) || 
                stack.is(net.minecraft.world.item.Items.TURTLE_EGG)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.occamy.tamed"));
                    
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            sizeChangeTimer++;
            
            // Periodically change size (shape-shifting ability)
            if (sizeChangeTimer >= 200) { // Every 10 seconds
                sizeChangeTimer = 0;
                
                // Randomly grow or shrink
                if (this.random.nextBoolean()) {
                    currentSize = Math.min(MAX_SIZE, currentSize + 0.2f);
                } else {
                    currentSize = Math.max(MIN_SIZE, currentSize - 0.2f);
                }
                
                // Update bounding box based on size
                this.refreshDimensions();
            }
            
            // Visual effects when flying
            if (!this.onGround() && this.tickCount % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY(), this.getZ(),
                    2, 0.2, 0.1, 0.2, 0.01);
            }
        }
    }
    
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions dimensions) {
        return dimensions.height() * 0.8f * currentSize;
    }
    
    @Override
    public void refreshDimensions() {
        // Store current size before refresh
        float size = currentSize;
        super.refreshDimensions();
        // Apply size scaling after refresh
        net.minecraft.world.entity.EntityDimensions baseDimensions = this.getDimensions(this.getPose());
        this.setBoundingBox(new net.minecraft.world.phys.AABB(
            -baseDimensions.width() * size / 2.0,
            0.0,
            -baseDimensions.width() * size / 2.0,
            baseDimensions.width() * size / 2.0,
            baseDimensions.height() * size,
            baseDimensions.width() * size / 2.0
        ));
    }
    
    public float getCurrentSize() {
        return currentSize;
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.EGG) || 
               stack.is(net.minecraft.world.item.Items.TURTLE_EGG);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Occamies don't breed
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("CurrentSize", currentSize);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        currentSize = input.getFloatOr("CurrentSize", 1.0f);
        this.refreshDimensions();
    }
}

















