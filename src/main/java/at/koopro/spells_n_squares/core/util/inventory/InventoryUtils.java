package at.koopro.spells_n_squares.core.util.inventory;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for item and inventory management.
 * Provides item manipulation, NBT helpers, inventory searching, and enchantment utilities.
 */
public final class InventoryUtils {
    private InventoryUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Adds an item to a player's inventory.
     * Handles overflow by dropping items if inventory is full.
     * 
     * @param player The player
     * @param stack The item stack to add
     * @return true if all items were added, false if some were dropped
     */
    public static boolean addItem(Player player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return false;
        }
        
        Inventory inventory = player.getInventory();
        boolean added = inventory.add(stack);
        
        if (!stack.isEmpty()) {
            // Drop remaining items
            player.drop(stack, false);
            return false;
        }
        
        return added;
    }
    
    /**
     * Removes items from a player's inventory.
     * 
     * @param player The player
     * @param stack The item stack to match
     * @param count The number of items to remove
     * @return The number of items actually removed
     */
    public static int removeItem(Player player, ItemStack stack, int count) {
        if (player == null || stack == null || stack.isEmpty() || count <= 0) {
            return 0;
        }
        
        Inventory inventory = player.getInventory();
        int removed = 0;
        
        for (int i = 0; i < inventory.getContainerSize() && removed < count; i++) {
            ItemStack slotStack = inventory.getItem(i);
            if (ItemStack.isSameItem(slotStack, stack)) {
                int toRemove = Math.min(slotStack.getCount(), count - removed);
                slotStack.shrink(toRemove);
                removed += toRemove;
            }
        }
        
        return removed;
    }
    
    /**
     * Checks if a player has an item in their inventory.
     * 
     * @param player The player
     * @param stack The item stack to check for
     * @return true if the player has the item
     */
    public static boolean hasItem(Player player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return false;
        }
        
        return getItemCount(player, stack) > 0;
    }
    
    /**
     * Counts the number of items in a player's inventory.
     * 
     * @param player The player
     * @param stack The item stack to count
     * @return The total count of matching items
     */
    public static int getItemCount(Player player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return 0;
        }
        
        Inventory inventory = player.getInventory();
        int count = 0;
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slotStack = inventory.getItem(i);
            if (ItemStack.isSameItem(slotStack, stack)) {
                count += slotStack.getCount();
            }
        }
        
        return count;
    }
    
    /**
     * Finds items in a player's inventory by tag.
     * 
     * @param player The player
     * @param tag The item tag
     * @return List of matching item stacks
     */
    public static List<ItemStack> findItemsByTag(Player player, TagKey<Item> tag) {
        if (player == null || tag == null) {
            return List.of();
        }
        
        Inventory inventory = player.getInventory();
        List<ItemStack> result = new ArrayList<>();
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(tag)) {
                result.add(stack);
            }
        }
        
        return result;
    }
    
    /**
     * Finds items in a player's inventory by predicate.
     * 
     * @param player The player
     * @param predicate The filter predicate
     * @return List of matching item stacks
     */
    public static List<ItemStack> findItemsByPredicate(Player player, Predicate<ItemStack> predicate) {
        if (player == null || predicate == null) {
            return List.of();
        }
        
        Inventory inventory = player.getInventory();
        List<ItemStack> result = new ArrayList<>();
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
                result.add(stack);
            }
        }
        
        return result;
    }
    
    /**
     * Adds an enchantment to an item stack.
     * Note: Simplified version - may need adjustment based on actual API.
     * 
     * @param stack The item stack
     * @param enchantment The enchantment
     * @param level The enchantment level
     * @return true if the enchantment was added
     */
    public static boolean addEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        // Note: Simplified - enchantment API may need adjustment
        // For now, this is a placeholder
        return false;
    }
    
    /**
     * Removes an enchantment from an item stack.
     * Note: This is a placeholder - enchantment removal may require different API.
     * 
     * @param stack The item stack
     * @param enchantment The enchantment to remove
     * @return true if the enchantment was removed
     */
    public static boolean removeEnchantment(ItemStack stack, Enchantment enchantment) {
        // Enchantment removal is complex and may not be directly supported
        // This is a placeholder implementation
        return false;
    }
    
    /**
     * Checks if an item stack has a specific enchantment.
     * 
     * @param stack The item stack
     * @param enchantment The enchantment to check
     * @return The enchantment level, or 0 if not present
     */
    public static int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        // Note: Simplified - enchantment API may need adjustment
        // For now, returns 0
        return 0;
    }
    
    /**
     * Parses an item from an identifier string.
     * 
     * @param itemName The item name (e.g., "minecraft:diamond")
     * @return The item, or null if not found
     */
    public static Item parseItem(String itemName) {
        // Note: Simplified - returns null for now
        // Use BuiltInRegistries.ITEM.get() directly with Identifier
        return null;
    }
    
    /**
     * Parses an enchantment from an identifier string.
     * 
     * @param enchantmentName The enchantment name (e.g., "minecraft:sharpness")
     * @return The enchantment, or null if not found
     */
    public static Enchantment parseEnchantment(String enchantmentName) {
        // Note: Simplified - returns null for now
        // Use BuiltInRegistries.ENCHANTMENT.get() directly with Identifier
        return null;
    }
}


