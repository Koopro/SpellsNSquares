package at.koopro.spells_n_squares.features.enchantments;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the enchantment table GUI.
 */
public class EnchantmentMenu extends AbstractContainerMenu {
    private final BlockPos tablePos;
    private final Player player;
    private final Slot itemSlot;
    
    // Client-side constructor
    public EnchantmentMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.ENCHANTMENT_MENU.get(), containerId);
        this.tablePos = buffer.readBlockPos();
        this.player = playerInventory.player;
        
        // Item slot for the item to enchant
        this.itemSlot = new Slot(new net.minecraft.world.SimpleContainer(1), 0, 80, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true; // Can place any item
            }
        };
        this.addSlot(itemSlot);
        
        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = 84 + row * 18;
                int slotIndex = col + row * 9 + 9; // +9 to skip hotbar
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
        
        // Add hotbar slots
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 142;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }
    
    // Server-side constructor
    public EnchantmentMenu(int containerId, Inventory playerInventory, BlockPos tablePos) {
        super(ModMenus.ENCHANTMENT_MENU.get(), containerId);
        this.tablePos = tablePos;
        this.player = playerInventory.player;
        
        // Item slot for the item to enchant
        this.itemSlot = new Slot(new net.minecraft.world.SimpleContainer(1), 0, 80, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true; // Can place any item
            }
        };
        this.addSlot(itemSlot);
        
        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = 84 + row * 18;
                int slotIndex = col + row * 9 + 9; // +9 to skip hotbar
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
        
        // Add hotbar slots
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 142;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            if (index == 0) {
                // From item slot to player inventory
                if (!this.moveItemStackTo(slotStack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to item slot
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
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
        // Check if player is still near the enchantment table
        if (tablePos == null) {
            return false;
        }
        return player.distanceToSqr(tablePos.getX() + 0.5, tablePos.getY() + 0.5, tablePos.getZ() + 0.5) <= 64.0;
    }
    
    public ItemStack getItemToEnchant() {
        return itemSlot.getItem();
    }
}


