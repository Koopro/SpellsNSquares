package at.koopro.spells_n_squares.core.menu;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for mod container menus with common functionality.
 * Provides standardized validation, slot management, and quick move helpers.
 * 
 * @param <T> The menu type (usually the same as the subclass)
 */
public abstract class BaseModMenu extends AbstractContainerMenu {
    protected final Player player;
    
    /**
     * Creates a menu instance.
     * 
     * @param menuType The menu type
     * @param containerId The container ID
     * @param playerInventory The player inventory
     */
    public BaseModMenu(MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId);
        this.player = playerInventory.player;
        DevLogger.logMethodEntry(this, "BaseModMenu", 
            "containerId=" + containerId + 
            ", player=" + (this.player != null ? this.player.getName().getString() : "null"));
    }
    
    /**
     * Creates a menu instance with a player reference.
     * 
     * @param menuType The menu type
     * @param containerId The container ID
     * @param player The player
     */
    public BaseModMenu(MenuType<?> menuType, int containerId, Player player) {
        super(menuType, containerId);
        this.player = player;
        DevLogger.logMethodEntry(this, "BaseModMenu", 
            "containerId=" + containerId + 
            ", player=" + (player != null ? player.getName().getString() : "null"));
    }
    
    /**
     * Checks if the menu is still valid for the player.
     * Subclasses should override to provide specific validation logic.
     * 
     * @param player The player to validate
     * @return True if the menu is still valid
     */
    protected abstract boolean isValid(Player player);
    
    @Override
    public final boolean stillValid(Player player) {
        DevLogger.logMethodEntry(this, "stillValid", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        boolean result = isValid(player);
        DevLogger.logReturnValue(this, "stillValid", result);
        return result;
    }
    
    /**
     * Adds standard player inventory slots.
     * 
     * @param inventory The player inventory
     * @param startX The starting X position
     * @param startY The starting Y position
     */
    protected void addPlayerSlots(Inventory inventory, int startX, int startY) {
        // Player inventory (9x3 grid)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }
        
        // Player hotbar (9 slots)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, startX + col * 18, startY + 58));
        }
    }
    
    /**
     * Called during menu construction to add custom slots.
     * Subclasses should override to add their specific slots.
     */
    protected void addCustomSlots() {
        // Override in subclasses
    }
    
    /**
     * Moves an item stack from container to player inventory.
     * 
     * @param stack The item stack to move
     * @return The remaining stack after moving
     */
    protected ItemStack quickMoveToPlayer(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        int playerStartSlot = getPlayerInventoryStartSlot();
        int playerEndSlot = getPlayerInventoryEndSlot();
        
        if (!this.moveItemStackTo(stack, playerStartSlot, playerEndSlot, false)) {
            return ItemStack.EMPTY;
        }
        
        return stack;
    }
    
    /**
     * Moves an item stack from player inventory to container.
     * 
     * @param stack The item stack to move
     * @return The remaining stack after moving
     */
    protected ItemStack quickMoveFromPlayer(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        int containerStartSlot = getContainerStartSlot();
        int containerEndSlot = getContainerEndSlot();
        
        if (!this.moveItemStackTo(stack, containerStartSlot, containerEndSlot, false)) {
            return ItemStack.EMPTY;
        }
        
        return stack;
    }
    
    /**
     * Gets the starting slot index for the player inventory.
     * Defaults to 0 (first player inventory slot).
     * 
     * @return The starting slot index
     */
    protected int getPlayerInventoryStartSlot() {
        return 0;
    }
    
    /**
     * Gets the ending slot index for the player inventory.
     * Defaults to 36 (all player inventory slots: 27 main + 9 hotbar).
     * 
     * @return The ending slot index
     */
    protected int getPlayerInventoryEndSlot() {
        return 36;
    }
    
    /**
     * Gets the starting slot index for the container inventory.
     * Defaults to 36 (after player inventory).
     * 
     * @return The starting slot index
     */
    protected int getContainerStartSlot() {
        return 36;
    }
    
    /**
     * Gets the ending slot index for the container inventory.
     * Defaults to slots.size() (all container slots).
     * 
     * @return The ending slot index
     */
    protected int getContainerEndSlot() {
        return this.slots.size();
    }
    
    /**
     * Gets the player associated with this menu.
     * 
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        DevLogger.logMethodEntry(this, "quickMoveStack", 
            "player=" + (player != null ? player.getName().getString() : "null") + 
            ", index=" + index);
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            DevLogger.logParameter(this, "quickMoveStack", "slotStack", 
                slotStack.getItem().getDescriptionId() + " x" + slotStack.getCount());
            
            // Determine if moving from container to player or vice versa
            if (index < getContainerStartSlot()) {
                // Moving from player inventory to container
                DevLogger.logDebug(this, "quickMoveStack", "Moving from player to container");
                if (!this.moveItemStackTo(slotStack, getContainerStartSlot(), getContainerEndSlot(), false)) {
                    DevLogger.logMethodExit(this, "quickMoveStack", ItemStack.EMPTY);
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from container to player inventory
                DevLogger.logDebug(this, "quickMoveStack", "Moving from container to player");
                if (!this.moveItemStackTo(slotStack, getPlayerInventoryStartSlot(), getPlayerInventoryEndSlot(), false)) {
                    DevLogger.logMethodExit(this, "quickMoveStack", ItemStack.EMPTY);
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == itemstack.getCount()) {
                DevLogger.logMethodExit(this, "quickMoveStack", ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, slotStack);
        }
        
        DevLogger.logMethodExit(this, "quickMoveStack", itemstack);
        return itemstack;
    }
}

