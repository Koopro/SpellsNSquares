package at.koopro.spells_n_squares.features.mail;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the mailbox GUI.
 */
public class MailboxMenu extends AbstractContainerMenu {
    private final Container mailboxContainer;
    private final BlockPos mailboxPos;
    private final Player player;
    private final int mailboxSlots;
    
    // Client-side constructor
    public MailboxMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.MAILBOX_MENU.get(), containerId);
        this.mailboxPos = buffer.readBlockPos();
        this.player = playerInventory.player;
        
        // Get mailbox data
        MailboxData.MailboxComponent mailboxData = at.koopro.spells_n_squares.features.mail.block.MailboxBlock.getMailboxInventory(
            player.level(), mailboxPos, player.getUUID());
        this.mailboxSlots = mailboxData.maxSlots();
        
        // Create container from mailbox data
        this.mailboxContainer = new SimpleContainer(mailboxSlots);
        for (int i = 0; i < mailboxSlots; i++) {
            mailboxContainer.setItem(i, mailboxData.getMail(i));
        }
        
        // Add mailbox slots (3 rows of 9)
        int mailboxStartX = 8;
        int mailboxStartY = 18;
        for (int i = 0; i < mailboxSlots; i++) {
            int row = i / 9;
            int col = i % 9;
            int x = mailboxStartX + col * 18;
            int y = mailboxStartY + row * 18;
            this.addSlot(new MailSlot(mailboxContainer, i, x, y));
        }
        
        // Add player inventory slots (3 rows of 9)
        int playerInvStartY = mailboxStartY + 3 * 18 + 13;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = mailboxStartX + col * 18;
                int y = playerInvStartY + row * 18;
                int slotIndex = col + row * 9 + 9; // +9 to skip hotbar
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
        
        // Add hotbar slots
        int hotbarStartY = playerInvStartY + 58;
        for (int col = 0; col < 9; col++) {
            int x = mailboxStartX + col * 18;
            this.addSlot(new Slot(playerInventory, col, x, hotbarStartY));
        }
    }
    
    // Server-side constructor
    public MailboxMenu(int containerId, Inventory playerInventory, BlockPos mailboxPos) {
        super(ModMenus.MAILBOX_MENU.get(), containerId);
        this.mailboxPos = mailboxPos;
        this.player = playerInventory.player;
        
        // Get mailbox data
        MailboxData.MailboxComponent mailboxData = at.koopro.spells_n_squares.features.mail.block.MailboxBlock.getMailboxInventory(
            player.level(), mailboxPos, player.getUUID());
        this.mailboxSlots = mailboxData.maxSlots();
        
        // Create container from mailbox data with change listener
        this.mailboxContainer = new SimpleContainer(mailboxSlots) {
            @Override
            public void setChanged() {
                super.setChanged();
                // Sync changes to mailbox data (server-side only)
                if (!player.level().isClientSide()) {
                    syncToMailboxData();
                }
            }
        };
        for (int i = 0; i < mailboxSlots; i++) {
            mailboxContainer.setItem(i, mailboxData.getMail(i));
        }
        
        // Add mailbox slots (3 rows of 9)
        int mailboxStartX = 8;
        int mailboxStartY = 18;
        for (int i = 0; i < mailboxSlots; i++) {
            int row = i / 9;
            int col = i % 9;
            int x = mailboxStartX + col * 18;
            int y = mailboxStartY + row * 18;
            this.addSlot(new MailSlot(mailboxContainer, i, x, y));
        }
        
        // Add player inventory slots (3 rows of 9)
        int playerInvStartY = mailboxStartY + 3 * 18 + 13;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = mailboxStartX + col * 18;
                int y = playerInvStartY + row * 18;
                int slotIndex = col + row * 9 + 9; // +9 to skip hotbar
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
        
        // Add hotbar slots
        int hotbarStartY = playerInvStartY + 58;
        for (int col = 0; col < 9; col++) {
            int x = mailboxStartX + col * 18;
            this.addSlot(new Slot(playerInventory, col, x, hotbarStartY));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            // If clicking from mailbox inventory, try to move to player inventory
            if (index < mailboxSlots) {
                if (!this.moveItemStackTo(slotStack, mailboxSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // If clicking from player inventory, try to move to mailbox inventory
                if (!this.moveItemStackTo(slotStack, 0, mailboxSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        // Check if player is still near the mailbox
        if (mailboxPos == null) {
            return false;
        }
        return player.distanceToSqr(mailboxPos.getX() + 0.5, mailboxPos.getY() + 0.5, mailboxPos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        
        // Final sync when menu closes (server-side only)
        if (!player.level().isClientSide()) {
            syncToMailboxData();
        }
    }
    
    /**
     * Syncs the container inventory to the mailbox data.
     */
    private void syncToMailboxData() {
        MailboxData.MailboxComponent current = at.koopro.spells_n_squares.features.mail.block.MailboxBlock.getMailboxInventory(
            player.level(), mailboxPos, player.getUUID());
        
        // Update each slot
        MailboxData.MailboxComponent updated = current;
        for (int i = 0; i < mailboxSlots; i++) {
            ItemStack item = mailboxContainer.getItem(i);
            updated = updated.setMail(i, item);
        }
        
        at.koopro.spells_n_squares.features.mail.block.MailboxBlock.saveMailboxInventory(player.level(), mailboxPos, player.getUUID(), updated);
    }
    
    /**
     * Custom slot that only accepts mail items.
     */
    private static class MailSlot extends Slot {
        public MailSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            // Only allow mail items
            return stack.getItem() instanceof MailItem;
        }
    }
}












