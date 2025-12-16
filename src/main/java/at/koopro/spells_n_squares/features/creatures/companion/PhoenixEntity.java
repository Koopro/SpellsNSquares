package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

/**
 * Phoenix - rare companion with resurrection and healing abilities.
 */
public class PhoenixEntity extends BaseTamableCreatureEntity {
    private static final int HEALING_AURA_RANGE = 8;
    private static final int HEALING_INTERVAL = 100; // Every 5 seconds
    private int healingTimer = 0;
    private long lastResurrectionTick = 0;
    private static final int RESURRECTION_COOLDOWN = 12000; // 10 minutes
    
    public PhoenixEntity(EntityType<? extends PhoenixEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 60.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FLYING_SPEED, 0.7D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame
        if (!hasOwner() && !stack.isEmpty()) {
            if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                this.setOwner(player);
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.phoenix.tamed"));
            }
            return InteractionResult.SUCCESS;
        }
        
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            healingTimer++;
            
            // Healing aura for nearby players
            if (healingTimer >= HEALING_INTERVAL) {
                healingTimer = 0;
                
                for (Player player : this.level().getEntitiesOfClass(Player.class,
                        this.getBoundingBox().inflate(HEALING_AURA_RANGE))) {
                    if (player.getHealth() < player.getMaxHealth()) {
                        player.addEffect(new MobEffectInstance(
                            MobEffects.REGENERATION,
                            100,
                            0,
                            false,
                            true,
                            true
                        ));
                        
                        // Visual effect
                        serverLevel.sendParticles(ParticleTypes.FLAME,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            5, 0.3, 0.3, 0.3, 0.02);
                    }
                }
            }
            
            // Check for owner death and attempt resurrection
            if (hasOwner() && this.tickCount % 20 == 0) {
                checkAndResurrectOwner(serverLevel);
            }
            
            // Fire immunity particles
            if (this.tickCount % 5 == 0) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    1, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }
    
    /**
     * Checks if owner is dead and attempts resurrection.
     */
    private void checkAndResurrectOwner(ServerLevel level) {
        if (this.tickCount - lastResurrectionTick < RESURRECTION_COOLDOWN) {
            return; // On cooldown
        }
        
        UUID ownerUUID = getOwnerId().orElse(null);
        if (ownerUUID == null) {
            return;
        }
        Player owner = level.getPlayerByUUID(ownerUUID);
        
        if (owner instanceof ServerPlayer serverOwner && serverOwner.isDeadOrDying()) {
            // Resurrect owner
            serverOwner.setHealth(serverOwner.getMaxHealth());
            serverOwner.removeAllEffects();
            serverOwner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            serverOwner.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
            
            // Visual effects
            level.sendParticles(ParticleTypes.FLAME,
                serverOwner.getX(), serverOwner.getY() + 1.0, serverOwner.getZ(),
                50, 1.0, 1.0, 1.0, 0.1);
            
            level.playSound(null, serverOwner.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
            
            serverOwner.sendSystemMessage(Component.translatable("message.spells_n_squares.phoenix.resurrected"));
            
            lastResurrectionTick = this.tickCount;
        }
    }
    
    @Override
    public boolean fireImmune() {
        return true; // Phoenix is immune to fire
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
        output.putLong("LastResurrection", lastResurrectionTick);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        lastResurrectionTick = input.getLongOr("LastResurrection", 0L);
    }
}
