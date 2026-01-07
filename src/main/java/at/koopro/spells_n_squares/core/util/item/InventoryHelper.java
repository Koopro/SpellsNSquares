package at.koopro.spells_n_squares.core.util.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;

/**
 * Utility class for inventory manipulation and management.
 * Provides methods for finding items, managing slots, and inventory operations.
 */
public final class InventoryHelper {
    
    private InventoryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Finds the first slot containing a specific item.
     * 
     * @param inventory The inventory
     * @param item The item to find
     * @return Slot index, or -1 if not found
     */
    public static int findItemSlot(Container inventory, Item item) {
        if (inventory == null || item == null) {
            return -1;
        }
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Finds all slots containing a specific item.
     * 
     * @param inventory The inventory
     * @param item The item to find
     * @return List of slot indices
     */
    public static List<Integer> findItemSlots(Container inventory, Item item) {
        if (inventory == null || item == null) {
            return Collections.emptyList();
        }
        
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                slots.add(i);
            }
        }
        
        return slots;
    }
    
    /**
     * Finds slots matching a predicate.
     * 
     * @param inventory The inventory
     * @param predicate The predicate to match
     * @return List of slot indices
     */
    public static List<Integer> findSlots(Container inventory, Predicate<ItemStack> predicate) {
        if (inventory == null || predicate == null) {
            return Collections.emptyList();
        }
        
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
                slots.add(i);
            }
        }
        
        return slots;
    }
    
    /**
     * Counts the total number of a specific item in an inventory.
     * 
     * @param inventory The inventory
     * @param item The item to count
     * @return Total count
     */
    public static int countItem(Container inventory, Item item) {
        if (inventory == null || item == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                count += stack.getCount();
            }
        }
        
        return count;
    }
    
    /**
     * Checks if an inventory contains a specific item.
     * 
     * @param inventory The inventory
     * @param item The item to check
     * @param minCount Minimum count required
     * @return true if inventory contains at least minCount items
     */
    public static boolean hasItem(Container inventory, Item item, int minCount) {
        return countItem(inventory, item) >= minCount;
    }
    
    /**
     * Checks if an inventory contains a specific item (at least 1).
     * 
     * @param inventory The inventory
     * @param item The item to check
     * @return true if inventory contains the item
     */
    public static boolean hasItem(Container inventory, Item item) {
        return hasItem(inventory, item, 1);
    }
    
    /**
     * Gets the first empty slot in an inventory.
     * 
     * @param inventory The inventory
     * @return Slot index, or -1 if no empty slot
     */
    public static int findEmptySlot(Container inventory) {
        if (inventory == null) {
            return -1;
        }
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Gets all empty slots in an inventory.
     * 
     * @param inventory The inventory
     * @return List of empty slot indices
     */
    public static List<Integer> findEmptySlots(Container inventory) {
        if (inventory == null) {
            return Collections.emptyList();
        }
        
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                slots.add(i);
            }
        }
        
        return slots;
    }
    
    /**
     * Tries to add an item stack to an inventory.
     * 
     * @param inventory The inventory
     * @param stack The stack to add
     * @return Remaining stack (empty if all added)
     */
    public static ItemStack addItem(Container inventory, ItemStack stack) {
        if (inventory == null || stack == null || stack.isEmpty()) {
            return stack != null ? stack.copy() : ItemStack.EMPTY;
        }
        
        ItemStack remaining = stack.copy();
        
        // First, try to merge with existing stacks
        for (int i = 0; i < inventory.getContainerSize() && !remaining.isEmpty(); i++) {
            ItemStack existing = inventory.getItem(i);
            if (ItemStack.isSameItem(existing, remaining)) {
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, remaining.getCount());
                    existing.grow(toAdd);
                    remaining.shrink(toAdd);
                }
            }
        }
        
        // Then, try to place in empty slots
        for (int i = 0; i < inventory.getContainerSize() && !remaining.isEmpty(); i++) {
            ItemStack existing = inventory.getItem(i);
            if (existing.isEmpty()) {
                int toAdd = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                ItemStack toPlace = remaining.copy();
                toPlace.setCount(toAdd);
                inventory.setItem(i, toPlace);
                remaining.shrink(toAdd);
            }
        }
        
        return remaining;
    }
    
    /**
     * Removes items from an inventory.
     * 
     * @param inventory The inventory
     * @param item The item to remove
     * @param count The number to remove
     * @return Number of items actually removed
     */
    public static int removeItem(Container inventory, Item item, int count) {
        if (inventory == null || item == null || count <= 0) {
            return 0;
        }
        
        int removed = 0;
        for (int i = 0; i < inventory.getContainerSize() && removed < count; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                int toRemove = Math.min(stack.getCount(), count - removed);
                stack.shrink(toRemove);
                removed += toRemove;
                
                if (stack.isEmpty()) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }
        
        return removed;
    }
    
    /**
     * Gets a summary of items in the inventory.
     * 
     * @param inventory The inventory
     * @return Map of item to total count
     */
    public static Map<Item, Integer> getItemSummary(Container inventory) {
        if (inventory == null) {
            return Collections.emptyMap();
        }
        
        Map<Item, Integer> summary = new HashMap<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                summary.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        
        return summary;
    }
    
    /**
     * Gets the total number of items in an inventory.
     * 
     * @param inventory The inventory
     * @return Total item count
     */
    public static int getTotalItemCount(Container inventory) {
        if (inventory == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                count += stack.getCount();
            }
        }
        
        return count;
    }
    
    /**
     * Gets the number of filled slots in an inventory.
     * 
     * @param inventory The inventory
     * @return Number of filled slots
     */
    public static int getFilledSlotCount(Container inventory) {
        if (inventory == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Gets the player's main inventory (excluding hotbar and offhand).
     * 
     * @param player The player
     * @return The main inventory container
     */
    public static Container getMainInventory(Player player) {
        if (player == null) {
            return null;
        }
        
        // Return a view of the main inventory (slots 9-35)
        return new Container() {
            @Override
            public int getContainerSize() {
                return 27; // 9 rows * 3 columns
            }
            
            @Override
            public boolean isEmpty() {
                Inventory inv = player.getInventory();
                for (int i = 9; i < 36; i++) {
                    if (!inv.getItem(i).isEmpty()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public ItemStack getItem(int slot) {
                Inventory inv = player.getInventory();
                int actualSlot = slot + 9; // Offset to main inventory
                if (actualSlot >= 9 && actualSlot < 36) {
                    return inv.getItem(actualSlot);
                }
                return ItemStack.EMPTY;
            }
            
            @Override
            public ItemStack removeItem(int slot, int amount) {
                Inventory inv = player.getInventory();
                int actualSlot = slot + 9;
                if (actualSlot >= 9 && actualSlot < 36) {
                    return inv.removeItem(actualSlot, amount);
                }
                return ItemStack.EMPTY;
            }
            
            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                Inventory inv = player.getInventory();
                int actualSlot = slot + 9;
                if (actualSlot >= 9 && actualSlot < 36) {
                    return inv.removeItemNoUpdate(actualSlot);
                }
                return ItemStack.EMPTY;
            }
            
            @Override
            public void setItem(int slot, ItemStack stack) {
                Inventory inv = player.getInventory();
                int actualSlot = slot + 9;
                if (actualSlot >= 9 && actualSlot < 36) {
                    inv.setItem(actualSlot, stack);
                }
            }
            
            @Override
            public void setChanged() {
                player.getInventory().setChanged();
            }
            
            @Override
            public boolean stillValid(net.minecraft.world.entity.player.Player player) {
                return !player.isRemoved();
            }
            
            @Override
            public void clearContent() {
                Inventory inv = player.getInventory();
                for (int i = 9; i < 36; i++) {
                    inv.setItem(i, ItemStack.EMPTY);
                }
            }
        };
    }
}

