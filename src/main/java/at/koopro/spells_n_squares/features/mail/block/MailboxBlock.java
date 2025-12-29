package at.koopro.spells_n_squares.features.mail.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.mail.MailboxData;
import at.koopro.spells_n_squares.features.mail.OwlPostSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Mailbox block for receiving mail via Owl Post.
 */
public class MailboxBlock extends BaseInteractiveBlock {
    
    private static final int DEFAULT_MAILBOX_SLOTS = 27;
    
    public MailboxBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                 ServerPlayer serverPlayer, InteractionHand hand, 
                                                 BlockHitResult hit) {
        ItemStack heldItem = serverPlayer.getItemInHand(hand);
        
        // If player is holding mail, try to deposit it
        if (heldItem.getItem() instanceof at.koopro.spells_n_squares.features.mail.MailItem) {
            MailboxData.MailboxComponent mailbox = getMailboxInventory(level, pos, serverPlayer.getUUID());
            int emptySlot = mailbox.getFirstEmptySlot();
            
            if (emptySlot >= 0) {
                mailbox = mailbox.setMail(emptySlot, heldItem.copy());
                saveMailboxInventory(level, pos, serverPlayer.getUUID(), mailbox);
                heldItem.shrink(1);
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.mailbox.deposited"));
                return InteractionResult.SUCCESS;
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.mailbox.full"));
                return InteractionResult.FAIL;
            }
        }
        
        // Otherwise, open mailbox GUI
        openMailboxGUI(serverPlayer, level, pos);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Opens the mailbox GUI for viewing mail.
     */
    private void openMailboxGUI(ServerPlayer player, Level level, BlockPos pos) {
        player.openMenu(new MailboxMenuProvider(pos));
    }
    
    /**
     * Menu provider for mailbox GUI.
     */
    private static class MailboxMenuProvider implements net.minecraft.world.MenuProvider {
        private final BlockPos mailboxPos;
        
        public MailboxMenuProvider(BlockPos mailboxPos) {
            this.mailboxPos = mailboxPos;
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.spells_n_squares.mailbox");
        }
        
        @org.jetbrains.annotations.Nullable
        @Override
        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory, net.minecraft.world.entity.player.Player player) {
            return new at.koopro.spells_n_squares.features.mail.MailboxMenu(containerId, playerInventory, mailboxPos);
        }
    }
    
    /**
     * Gets or creates mailbox inventory for a player at a position.
     * In a full implementation, this would be stored in BlockEntity per player.
     */
    public static MailboxData.MailboxComponent getMailboxInventory(Level level, BlockPos pos, java.util.UUID playerId) {
        // TODO: Store in BlockEntity or persistent storage
        // For now, return default empty mailbox
        return MailboxData.MailboxComponent.createDefault(DEFAULT_MAILBOX_SLOTS);
    }
    
    /**
     * Saves mailbox inventory.
     */
    public static void saveMailboxInventory(Level level, BlockPos pos, java.util.UUID playerId, 
                                           MailboxData.MailboxComponent mailbox) {
        // TODO: Save to BlockEntity or persistent storage
    }
    
    /**
     * Delivers mail to a mailbox.
     * Called by OwlPostSystem when mail arrives.
     */
    public static boolean deliverMailToMailbox(Level level, BlockPos pos, java.util.UUID recipientId, ItemStack mailItem) {
        MailboxData.MailboxComponent mailbox = getMailboxInventory(level, pos, recipientId);
        
        if (!mailbox.hasSpace()) {
            return false;
        }
        
        int emptySlot = mailbox.getFirstEmptySlot();
        if (emptySlot >= 0) {
            mailbox = mailbox.setMail(emptySlot, mailItem);
            saveMailboxInventory(level, pos, recipientId, mailbox);
            return true;
        }
        
        return false;
    }
}












