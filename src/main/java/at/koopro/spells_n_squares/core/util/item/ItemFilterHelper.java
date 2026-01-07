package at.koopro.spells_n_squares.core.util.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for item filtering, sorting, and grouping operations.
 * Provides methods for filtering items by various criteria.
 */
public final class ItemFilterHelper {
    
    private ItemFilterHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a filter for a specific item.
     * 
     * @param item The item to filter for
     * @return Predicate that matches the item
     */
    public static Predicate<ItemStack> itemFilter(Item item) {
        return stack -> stack != null && !stack.isEmpty() && stack.is(item);
    }
    
    /**
     * Creates a filter for items by name (case-insensitive partial match).
     * 
     * @param name The name to search for
     * @return Predicate that matches items with the name
     */
    public static Predicate<ItemStack> nameFilter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return stack -> false;
        }
        
        String lowerName = name.toLowerCase().trim();
        return stack -> {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            String displayName = stack.getDisplayName().getString().toLowerCase();
            return displayName.contains(lowerName);
        };
    }
    
    /**
     * Creates a filter for items by item ID (namespace:path format).
     * 
     * @param itemId The item ID to filter for
     * @return Predicate that matches the item ID
     */
    public static Predicate<ItemStack> itemIdFilter(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            return stack -> false;
        }
        
        return stack -> {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            String stackId = ItemStackHelper.getItemId(stack);
            return itemId.equals(stackId);
        };
    }
    
    /**
     * Creates a filter for items with NBT data.
     * 
     * @return Predicate that matches items with NBT
     */
    public static Predicate<ItemStack> hasNBTFilter() {
        return stack -> ItemStackHelper.hasNBT(stack);
    }
    
    /**
     * Creates a filter for items with a specific NBT key.
     * 
     * @param key The NBT key
     * @return Predicate that matches items with the key
     */
    public static Predicate<ItemStack> nbtKeyFilter(String key) {
        if (key == null) {
            return stack -> false;
        }
        
        return stack -> ItemStackHelper.hasNBTKey(stack, key);
    }
    
    /**
     * Creates a filter for items with minimum count.
     * 
     * @param minCount Minimum count required
     * @return Predicate that matches items with at least minCount
     */
    public static Predicate<ItemStack> minCountFilter(int minCount) {
        return stack -> stack != null && !stack.isEmpty() && stack.getCount() >= minCount;
    }
    
    /**
     * Creates a filter for items with maximum count.
     * 
     * @param maxCount Maximum count allowed
     * @return Predicate that matches items with at most maxCount
     */
    public static Predicate<ItemStack> maxCountFilter(int maxCount) {
        return stack -> stack != null && !stack.isEmpty() && stack.getCount() <= maxCount;
    }
    
    /**
     * Creates a filter for full stacks (at maximum stack size).
     * 
     * @return Predicate that matches full stacks
     */
    public static Predicate<ItemStack> fullStackFilter() {
        return stack -> ItemStackHelper.isFull(stack);
    }
    
    /**
     * Creates a filter for empty stacks.
     * 
     * @return Predicate that matches empty stacks
     */
    public static Predicate<ItemStack> emptyStackFilter() {
        return stack -> stack == null || stack.isEmpty();
    }
    
    /**
     * Combines multiple filters with AND logic.
     * 
     * @param filters The filters to combine
     * @return Combined predicate
     */
    @SafeVarargs
    public static Predicate<ItemStack> and(Predicate<ItemStack>... filters) {
        if (filters == null || filters.length == 0) {
            return stack -> true;
        }
        
        Predicate<ItemStack> result = filters[0];
        for (int i = 1; i < filters.length; i++) {
            if (filters[i] != null) {
                result = result.and(filters[i]);
            }
        }
        return result;
    }
    
    /**
     * Combines multiple filters with OR logic.
     * 
     * @param filters The filters to combine
     * @return Combined predicate
     */
    @SafeVarargs
    public static Predicate<ItemStack> or(Predicate<ItemStack>... filters) {
        if (filters == null || filters.length == 0) {
            return stack -> false;
        }
        
        Predicate<ItemStack> result = filters[0];
        for (int i = 1; i < filters.length; i++) {
            if (filters[i] != null) {
                result = result.or(filters[i]);
            }
        }
        return result;
    }
    
    /**
     * Negates a filter.
     * 
     * @param filter The filter to negate
     * @return Negated predicate
     */
    public static Predicate<ItemStack> not(Predicate<ItemStack> filter) {
        if (filter == null) {
            return stack -> true;
        }
        return filter.negate();
    }
    
    /**
     * Groups items by type.
     * 
     * @param stacks List of item stacks
     * @return Map of item to list of stacks
     */
    public static Map<Item, List<ItemStack>> groupByItem(List<ItemStack> stacks) {
        if (stacks == null) {
            return Collections.emptyMap();
        }
        
        return stacks.stream()
            .filter(stack -> stack != null && !stack.isEmpty())
            .collect(Collectors.groupingBy(ItemStack::getItem));
    }
    
    /**
     * Sorts item stacks by item type, then by count (descending).
     * 
     * @param stacks List of item stacks
     * @return Sorted list
     */
    public static List<ItemStack> sortByItemAndCount(List<ItemStack> stacks) {
        if (stacks == null) {
            return Collections.emptyList();
        }
        
        return stacks.stream()
            .filter(stack -> stack != null && !stack.isEmpty())
            .sorted((a, b) -> {
                // First sort by item type
                var keyA = BuiltInRegistries.ITEM.getResourceKey(a.getItem());
                var keyB = BuiltInRegistries.ITEM.getResourceKey(b.getItem());
                
                if (keyA.isPresent() && keyB.isPresent()) {
                    int itemCompare = keyA.get().compareTo(keyB.get());
                    if (itemCompare != 0) {
                        return itemCompare;
                    }
                }
                
                // Then by count (descending)
                return Integer.compare(b.getCount(), a.getCount());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filters a list of item stacks.
     * 
     * @param stacks List of stacks
     * @param filter The filter predicate
     * @return Filtered list
     */
    public static List<ItemStack> filter(List<ItemStack> stacks, Predicate<ItemStack> filter) {
        if (stacks == null || filter == null) {
            return Collections.emptyList();
        }
        
        return stacks.stream()
            .filter(filter)
            .collect(Collectors.toList());
    }
}

