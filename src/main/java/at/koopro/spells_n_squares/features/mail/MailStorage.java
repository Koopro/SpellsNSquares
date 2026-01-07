package at.koopro.spells_n_squares.features.mail;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages mail storage and delivery to mailboxes.
 * Mail is delivered to the recipient's mailbox when they are online.
 */
public final class MailStorage {
    private static final Map<UUID, List<MailData>> PENDING_MAIL = new ConcurrentHashMap<>();
    
    private MailStorage() {
        // Utility class - prevent instantiation
    }

    /**
     * Adds mail to the pending delivery queue.
     *
     * @param mail The mail to add
     */
    public static void addMail(MailData mail) {
        DevLogger.logStateChange(MailStorage.class, "addMail",
            "Adding mail " + mail.mailId() + " from " + mail.senderName() + " to " + mail.recipientName());
        
        PENDING_MAIL.computeIfAbsent(mail.recipientId(), k -> new ArrayList<>()).add(mail);
    }

    /**
     * Delivers pending mail to a player's mailbox.
     * Should be called when a player logs in or when mail is sent.
     *
     * @param player The recipient player
     * @param level The server level
     */
    public static void deliverPendingMail(ServerPlayer player, ServerLevel level) {
        UUID playerId = player.getUUID();
        List<MailData> pending = PENDING_MAIL.getOrDefault(playerId, new ArrayList<>());
        
        if (pending.isEmpty()) {
            return;
        }
        
        DevLogger.logStateChange(MailStorage.class, "deliverPendingMail",
            "Delivering " + pending.size() + " mail items to " + player.getName().getString());
        
        // Find player's mailbox (for now, we'll search nearby mailboxes)
        // In a full implementation, players might have registered mailbox positions
        BlockPos mailboxPos = findPlayerMailbox(player, level);
        
        if (mailboxPos != null) {
            BlockEntity blockEntity = level.getBlockEntity(mailboxPos);
            if (blockEntity instanceof at.koopro.spells_n_squares.features.mail.block.MailboxBlockEntity mailbox) {
                var inventory = mailbox.getInventory();
                
                // Try to add mail items to mailbox
                for (MailData mail : pending) {
                    ItemStack mailItem = createMailItem(mail);
                    // Try to find an empty slot
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        if (inventory.getItem(i).isEmpty()) {
                            inventory.setItem(i, mailItem);
                            break;
                        }
                    }
                }
                
                // Remove delivered mail from pending
                PENDING_MAIL.remove(playerId);
                DevLogger.logStateChange(MailStorage.class, "deliverPendingMail",
                    "Delivered mail to mailbox at " + mailboxPos);
            }
        } else {
            DevLogger.logWarn(MailStorage.class, "deliverPendingMail",
                "No mailbox found for player " + player.getName().getString() + ", mail will remain pending");
        }
    }

    /**
     * Finds a mailbox near the player.
     * In a full implementation, this could use player data to store mailbox positions.
     *
     * @param player The player
     * @param level The server level
     * @return The mailbox position, or null if not found
     */
    private static BlockPos findPlayerMailbox(ServerPlayer player, ServerLevel level) {
        // Simple implementation: search in a radius around the player
        BlockPos playerPos = player.blockPosition();
        int searchRadius = 64;
        
        for (int x = -searchRadius; x <= searchRadius; x += 16) {
            for (int z = -searchRadius; z <= searchRadius; z += 16) {
                BlockPos checkPos = playerPos.offset(x, 0, z);
                if (level.getBlockEntity(checkPos) instanceof at.koopro.spells_n_squares.features.mail.block.MailboxBlockEntity) {
                    return checkPos;
                }
            }
        }
        
        return null;
    }

    /**
     * Creates an ItemStack representing a mail item.
     * For now, uses a paper item with NBT data.
     *
     * @param mail The mail data
     * @return An ItemStack representing the mail
     */
    private static ItemStack createMailItem(MailData mail) {
        ItemStack stack = new ItemStack(Items.PAPER);
        // Note: Mail data is stored in MailStorage. When a custom mail item is implemented,
        // this should be updated to use that item type and store mail data in item data components.
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, 
            net.minecraft.network.chat.Component.literal("Mail from " + mail.senderName()));
        // Store mail ID in custom data component for retrieval
        // For now, we use the item's custom name to identify it as mail
        // When a custom mail item is implemented, use a proper data component for mailId
        return stack;
    }

    /**
     * Gets pending mail count for a player.
     *
     * @param playerId The player's UUID
     * @return The number of pending mail items
     */
    public static int getPendingMailCount(UUID playerId) {
        return PENDING_MAIL.getOrDefault(playerId, new ArrayList<>()).size();
    }

    /**
     * Gets all pending mail for a player.
     *
     * @param playerId The player's UUID
     * @return List of pending mail
     */
    public static List<MailData> getPendingMail(UUID playerId) {
        return new ArrayList<>(PENDING_MAIL.getOrDefault(playerId, new ArrayList<>()));
    }
}

