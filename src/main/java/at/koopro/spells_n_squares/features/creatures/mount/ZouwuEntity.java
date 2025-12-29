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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Zouwu - a fast cat-like mount from Fantastic Beasts that can teleport short distances.
 */
public class ZouwuEntity extends BaseTamableCreatureEntity {
    private static final int TELEPORT_COOLDOWN = 200; // 10 seconds
    private int teleportCooldownTimer = 0;
    private static final double TELEPORT_RANGE = 8.0;
    
    public ZouwuEntity(EntityType<? extends ZouwuEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.5D, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 60.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.5D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D);
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
        
        // Try to tame with meat
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(net.minecraft.world.item.Items.BEEF) || 
                stack.is(net.minecraft.world.item.Items.PORKCHOP) ||
                stack.is(net.minecraft.world.item.Items.MUTTON)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.2f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.zouwu.tamed"));
                    
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
            teleportCooldownTimer++;
            
            // Periodically teleport when following owner or in danger
            if (teleportCooldownTimer >= TELEPORT_COOLDOWN && this.isTame() && hasOwner()) {
                UUID ownerUUID = getOwnerId().orElse(null);
                if (ownerUUID != null) {
                    Player owner = serverLevel.getPlayerByUUID(ownerUUID);
                    if (owner != null) {
                        double distance = this.distanceTo(owner);
                        if (distance > 10.0 || (this.getTarget() != null && this.random.nextFloat() < 0.3f)) {
                            attemptTeleport(serverLevel);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Attempts to teleport the Zouwu a short distance.
     */
    private void attemptTeleport(ServerLevel level) {
        Vec3 currentPos = this.position();
        Vec3 teleportPos = currentPos.add(
            (this.random.nextDouble() - 0.5) * TELEPORT_RANGE * 2,
            (this.random.nextDouble() - 0.3) * 2.0,
            (this.random.nextDouble() - 0.5) * TELEPORT_RANGE * 2
        );
        
        // Ensure valid position
        if (level.noCollision(this, this.getBoundingBox().move(teleportPos.subtract(currentPos)))) {
            // Visual effect at origin
            level.sendParticles(ParticleTypes.PORTAL,
                currentPos.x, currentPos.y, currentPos.z,
                20, 0.5, 0.5, 0.5, 0.1);
            
            // Teleport
            this.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);
            teleportCooldownTimer = 0;
            
            // Visual effect at destination
            level.sendParticles(ParticleTypes.PORTAL,
                teleportPos.x, teleportPos.y, teleportPos.z,
                20, 0.5, 0.5, 0.5, 0.1);
            
            level.playSound(null, teleportPos.x, teleportPos.y, teleportPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.5f, 1.5f);
        }
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.BEEF) || 
               stack.is(net.minecraft.world.item.Items.PORKCHOP) ||
               stack.is(net.minecraft.world.item.Items.MUTTON);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Zouwu don't breed
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("TeleportCooldown", teleportCooldownTimer);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        teleportCooldownTimer = input.getIntOr("TeleportCooldown", 0);
    }
}
















