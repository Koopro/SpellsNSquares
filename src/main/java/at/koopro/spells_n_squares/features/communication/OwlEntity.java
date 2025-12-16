package at.koopro.spells_n_squares.features.communication;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Owl entity for delivering items and messages.
 */
public class OwlEntity extends Mob {
    
    private Optional<UUID> targetPlayerId = Optional.empty();
    private BlockPos deliveryPos;
    private ItemStack deliveryItem = ItemStack.EMPTY;
    private Component deliveryMessage;
    private boolean hasDelivered = false;
    
    public OwlEntity(EntityType<? extends OwlEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Note: FlyGoal doesn't exist in this version, using basic navigation instead
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FLYING_SPEED, 0.6D);
    }
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }
    
    /**
     * Sets the delivery target.
     */
    public void setDeliveryTarget(UUID playerId, BlockPos pos, ItemStack item, Component message) {
        this.targetPlayerId = Optional.of(playerId);
        this.deliveryPos = pos;
        this.deliveryItem = item.copy();
        this.deliveryMessage = message;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel && !hasDelivered) {
            // Check if reached delivery position
            if (deliveryPos != null && this.blockPosition().distSqr(deliveryPos) < 4.0) {
                deliverItem(serverLevel);
                hasDelivered = true;
            }
        }
    }
    
    /**
     * Delivers the item to the target.
     */
    private void deliverItem(ServerLevel level) {
        if (targetPlayerId.isEmpty()) {
            return;
        }
        
        var targetPlayer = level.getPlayerByUUID(targetPlayerId.get());
        if (targetPlayer instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            if (!deliveryItem.isEmpty()) {
                serverPlayer.getInventory().add(deliveryItem);
            }
            if (deliveryMessage != null) {
                serverPlayer.sendSystemMessage(deliveryMessage);
            }
        }
        
        // Remove owl after delivery
        this.discard();
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        // Save delivery data
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        // Load delivery data
    }
}

