package at.koopro.spells_n_squares.features.storage.transfer;

import at.koopro.spells_n_squares.core.util.item.InventoryHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Helper class for quick transfer operations between storages.
 * Provides methods for moving items between containers efficiently.
 */
public final class QuickTransferHelper {
    
    private QuickTransferHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Transfers all items from source to destination.
     * 
     * @param source The source container
     * @param destination The destination container
     * @return Number of items transferred
     */
    public static int transferAll(Container source, Container destination) {
        if (source == null || destination == null) {
            return 0;
        }
        
        int transferred = 0;
        for (int i = 0; i < source.getContainerSize(); i++) {
            ItemStack stack = source.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack remaining = InventoryHelper.addItem(destination, stack);
                int moved = stack.getCount() - remaining.getCount();
                if (moved > 0) {
                    stack.shrink(moved);
                    if (stack.isEmpty()) {
                        source.setItem(i, ItemStack.EMPTY);
                    }
                    transferred += moved;
                }
            }
        }
        
        return transferred;
    }
    
    /**
     * Transfers items matching a filter from source to destination.
     * 
     * @param source The source container
     * @param destination The destination container
     * @param filter Filter predicate for items to transfer
     * @return Number of items transferred
     */
    public static int transferFiltered(Container source, Container destination, 
                                      java.util.function.Predicate<ItemStack> filter) {
        if (source == null || destination == null || filter == null) {
            return 0;
        }
        
        int transferred = 0;
        for (int i = 0; i < source.getContainerSize(); i++) {
            ItemStack stack = source.getItem(i);
            if (!stack.isEmpty() && filter.test(stack)) {
                ItemStack remaining = InventoryHelper.addItem(destination, stack);
                int moved = stack.getCount() - remaining.getCount();
                if (moved > 0) {
                    stack.shrink(moved);
                    if (stack.isEmpty()) {
                        source.setItem(i, ItemStack.EMPTY);
                    }
                    transferred += moved;
                }
            }
        }
        
        return transferred;
    }
    
    /**
     * Transfers a specific item type from source to destination.
     * 
     * @param source The source container
     * @param destination The destination container
     * @param item The item type to transfer
     * @param maxCount Maximum number to transfer (0 for all)
     * @return Number of items transferred
     */
    public static int transferItem(Container source, Container destination, 
                                   net.minecraft.world.item.Item item, int maxCount) {
        if (source == null || destination == null || item == null) {
            return 0;
        }
        
        int transferred = 0;
        int remaining = maxCount > 0 ? maxCount : Integer.MAX_VALUE;
        
        for (int i = 0; i < source.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = source.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                int toTransfer = Math.min(stack.getCount(), remaining);
                ItemStack transferStack = stack.copy();
                transferStack.setCount(toTransfer);
                
                ItemStack remainingStack = InventoryHelper.addItem(destination, transferStack);
                int actuallyTransferred = toTransfer - remainingStack.getCount();
                
                if (actuallyTransferred > 0) {
                    stack.shrink(actuallyTransferred);
                    if (stack.isEmpty()) {
                        source.setItem(i, ItemStack.EMPTY);
                    }
                    transferred += actuallyTransferred;
                    remaining -= actuallyTransferred;
                }
            }
        }
        
        return transferred;
    }
    
    /**
     * Sorts items in a container by type and count.
     * 
     * @param container The container to sort
     */
    public static void sortContainer(Container container) {
        if (container == null) {
            return;
        }
        
        // Collect all items
        List<ItemStack> items = new java.util.ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
                container.setItem(i, ItemStack.EMPTY);
            }
        }
        
        // Sort items
        items.sort((a, b) -> {
            // First by item type
            int itemCompare = a.getItem().toString().compareTo(b.getItem().toString());
            if (itemCompare != 0) {
                return itemCompare;
            }
            // Then by count (descending)
            return Integer.compare(b.getCount(), a.getCount());
        });
        
        // Place items back
        int slot = 0;
        for (ItemStack stack : items) {
            while (slot < container.getContainerSize() && !container.getItem(slot).isEmpty()) {
                slot++;
            }
            if (slot < container.getContainerSize()) {
                container.setItem(slot, stack);
                slot++;
            }
        }
    }
}

