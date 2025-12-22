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
 * Thunderbird - a large bird from Fantastic Beasts that creates storms.
 * Powerful flying mount.
 */
public class ThunderbirdEntity extends BaseTamableCreatureEntity {
    private static final int STORM_COOLDOWN = 1200; // 1 minute
    private int stormCooldownTimer = 0;
    
    public ThunderbirdEntity(EntityType<? extends ThunderbirdEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 80.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.FLYING_SPEED, 1.0D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D);
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
        
        // Try to tame with rare items
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(net.minecraft.world.item.Items.GOLDEN_APPLE) || 
                stack.is(net.minecraft.world.item.Items.ENCHANTED_GOLDEN_APPLE)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.thunderbird.tamed"));
                    
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
            stormCooldownTimer++;
            
            // Periodically create storm effects when flying
            if (stormCooldownTimer >= STORM_COOLDOWN && !this.onGround() && this.random.nextFloat() < 0.1f) {
                stormCooldownTimer = 0;
                createStormEffect(serverLevel);
            }
            
            // Visual effects when flying
            if (!this.onGround() && this.tickCount % 5 == 0) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY(), this.getZ(),
                    3, 0.3, 0.1, 0.3, 0.02);
            }
        }
    }
    
    /**
     * Creates a storm effect around the Thunderbird.
     */
    private void createStormEffect(ServerLevel level) {
        // Lightning particles
        for (int i = 0; i < 10; i++) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                this.getX() + (this.random.nextDouble() - 0.5) * 3.0,
                this.getY() + this.random.nextDouble() * 2.0,
                this.getZ() + (this.random.nextDouble() - 0.5) * 3.0,
                1, 0.1, 0.1, 0.1, 0.05);
        }
        
        // Cloud particles
        level.sendParticles(ParticleTypes.CLOUD,
            this.getX(), this.getY(), this.getZ(),
            20, 2.0, 1.0, 2.0, 0.1);
        
        level.playSound(null, this.blockPosition(),
            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.5f, 1.0f);
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.GOLDEN_APPLE) || 
               stack.is(net.minecraft.world.item.Items.ENCHANTED_GOLDEN_APPLE);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Thunderbirds don't breed
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("StormCooldown", stormCooldownTimer);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        stormCooldownTimer = input.getIntOr("StormCooldown", 0);
    }
}







