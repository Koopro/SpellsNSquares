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
        
        // Check if this is mail delivery
        if (!deliveryItem.isEmpty() && deliveryItem.getItem() instanceof at.koopro.spells_n_squares.features.mail.MailItem) {
            deliverMail(level);
            return;
        }
        
        // Check if this is mail delivery
        if (!deliveryItem.isEmpty() && deliveryItem.getItem() instanceof at.koopro.spells_n_squares.features.mail.MailItem) {
            deliverMail(level);
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
    
    /**
     * Delivers mail to the target player's mailbox or inventory.
     */
    private void deliverMail(ServerLevel level) {
        UUID recipientId = targetPlayerId.get();
        var targetPlayer = level.getPlayerByUUID(recipientId);
        
        // Try to find mailbox first
        BlockPos mailboxPos = findMailbox(level, deliveryPos);
        
        if (mailboxPos != null) {
            // Try to deliver to mailbox
            if (at.koopro.spells_n_squares.features.mail.block.MailboxBlock.deliverMailToMailbox(
                    level, mailboxPos, recipientId, deliveryItem)) {
                if (targetPlayer instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.mail.delivered_mailbox"));
                }
                this.discard();
                return;
            }
        }
        
        // Fallback to player inventory
        if (targetPlayer instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            if (serverPlayer.getInventory().add(deliveryItem)) {
                serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.mail.delivered"));
            } else {
                // Inventory full - drop at player location
                serverPlayer.drop(deliveryItem, false);
                serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.mail.delivered_ground"));
            }
        }
        
        // Remove owl after delivery
        this.discard();
    }
    
    /**
     * Finds a mailbox near the delivery position.
     */
    private BlockPos findMailbox(ServerLevel level, BlockPos searchPos) {
        if (searchPos == null) {
            return null;
        }
        
        // Search in a 16 block radius
        int searchRadius = 16;
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = searchPos.offset(x, y, z);
                    if (level.getBlockState(checkPos).getBlock() instanceof at.koopro.spells_n_squares.features.mail.block.MailboxBlock) {
                        return checkPos;
                    }
                }
            }
        }
        
        return null;
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

