package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.core.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the enchanted bag inventory.
 */
public class BagMenu extends AbstractContainerMenu {
    private final Container bagContainer;
    private final ItemStack bagStack;
    private final int bagSlots;
    private final Player player;
    
    // Client-side constructor
    public BagMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.BAG_MENU.get(), containerId);
        this.bagStack = ItemStack.STREAM_CODEC.decode(buffer);
        this.player = playerInventory.player;
        
        // Get or create bag inventory
        BagInventoryData.BagInventoryComponent inventoryData = EnchantedBagItem.getBagInventory(bagStack);
        if (inventoryData == null) {
            // Fallback: create empty inventory
            inventoryData = BagInventoryData.BagInventoryComponent.createDefault(9);
        }
        
        this.bagSlots = inventoryData.maxSlots();
        
        // Create container from bag inventory data (client-side, no sync needed)
        this.bagContainer = new SimpleContainer(bagSlots);
        for (int i = 0; i < bagSlots; i++) {
            bagContainer.setItem(i, inventoryData.getItem(i));
        }
        
        // Add bag slots
        int bagRows = (bagSlots + 8) / 9; // Calculate rows (round up)
        int bagStartX = 8;
        int bagStartY = 18;
        
        for (int i = 0; i < bagSlots; i++) {
            int row = i / 9;
            int col = i % 9;
            int x = bagStartX + col * 18;
            int y = bagStartY + row * 18;
            this.addSlot(new BagSlot(bagContainer, i, x, y));
        }
        
        // Add player inventory slots (3 rows of 9)
        int playerInvStartY = bagStartY + bagRows * 18 + 13;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = bagStartX + col * 18;
                int y = playerInvStartY + row * 18;
                int slotIndex = col + row * 9 + 9; // +9 to skip hotbar
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
        
        // Add hotbar slots
        int hotbarStartY = playerInvStartY + 58;
        for (int col = 0; col < 9; col++) {
            int x = bagStartX + col * 18;
            this.addSlot(new Slot(playerInventory, col, x, hotbarStartY));
        }
    }
    
    // Server-side constructor
    public BagMenu(int containerId, Inventory playerInventory, ItemStack bagStack) {
        super(ModMenus.BAG_MENU.get(), containerId);
        this.bagStack = bagStack;
        this.player = playerInventory.player;
        
        // Get or create bag inventory
        BagInventoryData.BagInventoryComponent inventoryData = EnchantedBagItem.getBagInventory(bagStack);
        if (inventoryData == null) {
            // Fallback: create empty inventory
            inventoryData = BagInventoryData.BagInventoryComponent.createDefault(9);
        }
        
        this.bagSlots = inventoryData.maxSlots();
        
        // Create container from bag inventory data with change listener
        this.bagContainer = new SimpleContainer(bagSlots) {
            @Override
            public void setChanged() {
                super.setChanged();
                // Sync changes to data component in real-time (server-side only)
                if (!player.level().isClientSide()) {
                    syncToDataComponent();
                }
            }
        };
        for (int i = 0; i < bagSlots; i++) {
            bagContainer.setItem(i, inventoryData.getItem(i));
        }
        
        // Add bag slots
        int bagRows = (bagSlots + 8) / 9; // Calculate rows (round up)
        int bagStartX = 8;
        int bagStartY = 18;
        
        for (int i = 0; i < bagSlots; i++) {
            int row = i / 9;
            int col = i % 9;
            int x = bagStartX + col * 18;
            int y = bagStartY + row * 18;
            this.addSlot(new BagSlot(bagContainer, i, x, y));
        }
        
        // Add player inventory slots (3 rows of 9)
        int playerInvStartY = bagStartY + bagRows * 18 + 13;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = bagStartX + col * 18;
                int y = playerInvStartY + row * 18;
                int slotIndex = col + row * 9 + 9; // +9 to skip hotbar
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
        
        // Add hotbar slots
        int hotbarStartY = playerInvStartY + 58;
        for (int col = 0; col < 9; col++) {
            int x = bagStartX + col * 18;
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
            
            // If clicking from bag inventory, try to move to player inventory
            if (index < bagSlots) {
                if (!this.moveItemStackTo(slotStack, bagSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // If clicking from player inventory, try to move to bag inventory
                if (!this.moveItemStackTo(slotStack, 0, bagSlots, false)) {
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
        // Check if bag is still in player's inventory
        return player.getInventory().contains(bagStack) || 
               (player.getMainHandItem() == bagStack) || 
               (player.getOffhandItem() == bagStack);
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        
        // Final sync when menu closes (server-side only)
        if (!player.level().isClientSide()) {
            syncToDataComponent();
        }
    }
    
    /**
     * Syncs the container inventory to the bag's data component.
     */
    private void syncToDataComponent() {
        BagInventoryData.BagInventoryComponent current = EnchantedBagItem.getBagInventory(bagStack);
        if (current == null) {
            current = BagInventoryData.BagInventoryComponent.createDefault(bagSlots);
        }
        
        // Update each slot
        BagInventoryData.BagInventoryComponent updated = current;
        for (int i = 0; i < bagSlots; i++) {
            updated = updated.setItem(i, bagContainer.getItem(i));
        }
        
        EnchantedBagItem.setBagInventory(bagStack, updated);
    }
    
    /**
     * Gets the number of bag slots.
     */
    public int getBagSlots() {
        return bagSlots;
    }
    
    /**
     * Custom slot that prevents placing bags inside bags.
     */
    private static class BagSlot extends Slot {
        public BagSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            // Prevent placing bags inside bags (infinite recursion prevention)
            return !(stack.getItem() instanceof EnchantedBagItem);
        }
    }
}





