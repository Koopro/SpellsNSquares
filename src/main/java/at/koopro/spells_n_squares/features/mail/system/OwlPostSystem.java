package at.koopro.spells_n_squares.features.mail.system;

import at.koopro.spells_n_squares.features.mail.data.MailData;
import at.koopro.spells_n_squares.features.mail.item.MailItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Core mail management system for Owl Post.
 */
public final class OwlPostSystem {
    private OwlPostSystem() {
    }
    
    // Pending mail deliveries: recipient UUID -> list of mail items
    private static final Map<UUID, List<PendingMail>> pendingDeliveries = new HashMap<>();
    
    /**
     * Pending mail delivery information.
     */
    public record PendingMail(
        ItemStack mailItem,
        UUID senderId,
        long deliveryTime,
        double distance
    ) {
    }
    
    /**
     * Schedules mail for delivery.
     * Can optionally spawn an owl for visual delivery.
     * 
     * @param mailItem The mail item to deliver
     * @param senderId The sender's UUID
     * @param recipientId The recipient's UUID
     * @param senderPos The sender's position
     * @param recipientPos The recipient's position (or null if offline)
     * @param spawnOwl Whether to spawn an owl entity for visual delivery
     */
    public static void scheduleDelivery(ItemStack mailItem, UUID senderId, UUID recipientId, 
                                       net.minecraft.world.phys.Vec3 senderPos, 
                                       net.minecraft.world.phys.Vec3 recipientPos,
                                       boolean spawnOwl) {
        if (recipientPos == null) {
            // Offline player - deliver immediately when they log in
            recipientPos = senderPos; // Use sender pos as fallback
        }
        
        double distance = senderPos.distanceTo(recipientPos);
        // Delivery time: base 100 ticks (5 seconds) + 1 tick per block distance
        long deliveryTime = 100 + (long)(distance * 0.1);
        
        // If spawning owl, create owl entity
        if (spawnOwl && mailItem.getItem() instanceof MailItem) {
            spawnOwlForDelivery(mailItem, senderId, recipientId, senderPos, recipientPos);
        } else {
            // Otherwise, use scheduled delivery
            pendingDeliveries.computeIfAbsent(recipientId, k -> new ArrayList<>())
                .add(new PendingMail(mailItem, senderId, deliveryTime, distance));
        }
    }
    
    /**
     * Spawns an owl entity to deliver mail visually.
     */
    private static void spawnOwlForDelivery(ItemStack mailItem, UUID senderId, UUID recipientId,
                                          net.minecraft.world.phys.Vec3 senderPos,
                                          net.minecraft.world.phys.Vec3 recipientPos) {
        // This would be called from a ServerLevel context
        // TODO: Implement owl spawning when called from proper context
    }
    
    /**
     * Processes pending mail deliveries for a player.
     * Should be called on server tick.
     */
    public static void processDeliveries(ServerPlayer player, ServerLevel level) {
        UUID playerId = player.getUUID();
        List<PendingMail> pending = pendingDeliveries.get(playerId);
        
        if (pending == null || pending.isEmpty()) {
            return;
        }
        
        long currentTime = level.getGameTime();
        List<PendingMail> toDeliver = new ArrayList<>();
        List<PendingMail> remaining = new ArrayList<>();
        
        for (PendingMail mail : pending) {
            if (currentTime >= mail.deliveryTime()) {
                toDeliver.add(mail);
            } else {
                remaining.add(mail);
            }
        }
        
        // Deliver ready mail
        for (PendingMail mail : toDeliver) {
            deliverMail(player, mail.mailItem(), level);
        }
        
        // Update pending list
        if (remaining.isEmpty()) {
            pendingDeliveries.remove(playerId);
        } else {
            pendingDeliveries.put(playerId, remaining);
        }
    }
    
    /**
     * Delivers mail to a player's mailbox or inventory.
     */
    private static void deliverMail(ServerPlayer player, ItemStack mailItem, ServerLevel level) {
        // Try to find player's mailbox first
        net.minecraft.core.BlockPos mailboxPos = findPlayerMailbox(level, player);
        
        if (mailboxPos != null) {
            // Try to deliver to mailbox
            if (at.koopro.spells_n_squares.features.mail.block.MailboxBlock.deliverMailToMailbox(
                    level, mailboxPos, player.getUUID(), mailItem)) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.mail.delivered_mailbox"));
                return;
            }
        }
        
        // Fallback to player inventory
        if (player.getInventory().add(mailItem)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.mail.delivered"));
        } else {
            // Inventory full - drop at player location
            player.drop(mailItem, false);
            player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.mail.delivered_ground"));
        }
    }
    
    /**
     * Finds a player's mailbox near their location.
     * TODO: Implement proper mailbox finding (could store mailbox positions per player)
     */
    private static net.minecraft.core.BlockPos findPlayerMailbox(net.minecraft.server.level.ServerLevel level, 
                                                                 net.minecraft.server.level.ServerPlayer player) {
        // Search in a 32 block radius around player
        net.minecraft.core.BlockPos playerPos = player.blockPosition();
        int searchRadius = 32;
        
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    net.minecraft.core.BlockPos checkPos = playerPos.offset(x, y, z);
                    if (level.getBlockState(checkPos).getBlock() instanceof at.koopro.spells_n_squares.features.mail.block.MailboxBlock) {
                        return checkPos;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets pending mail count for a player.
     */
    public static int getPendingMailCount(UUID playerId) {
        List<PendingMail> pending = pendingDeliveries.get(playerId);
        return pending != null ? pending.size() : 0;
    }
    
    /**
     * Creates a mail item with the specified data.
     */
    public static ItemStack createMailItem(UUID senderId, String senderName, UUID recipientId, 
                                          String recipientName, String subject, String message,
                                          List<ItemStack> attachments) {
        // TODO: Re-enable when MAIL item is registered in ModItems
        // ItemStack mailItem = new ItemStack(at.koopro.spells_n_squares.core.registry.ModItems.MAIL.get());
        ItemStack mailItem = ItemStack.EMPTY; // Placeholder until MAIL item is registered
        
        MailData.MailComponent mailData = new MailData.MailComponent(
            senderId,
            senderName,
            recipientId,
            recipientName,
            subject,
            message,
            System.currentTimeMillis(),
            false,
            attachments != null ? attachments : List.of()
        );
        
        mailItem.set(MailData.MAIL_DATA.get(), mailData);
        return mailItem;
    }
    
    /**
     * Gets mail data from an item stack.
     */
    public static MailData.MailComponent getMailData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof at.koopro.spells_n_squares.features.mail.MailItem)) {
            return null;
        }
        return stack.get(MailData.MAIL_DATA.get());
    }
}
















