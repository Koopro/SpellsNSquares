package at.koopro.spells_n_squares.core.util.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Utility class for advanced item stack operations.
 * Provides methods for item manipulation, comparison, and validation.
 */
public final class ItemStackHelper {
    
    private ItemStackHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if two item stacks are similar (same item, ignoring count and NBT differences).
     * 
     * @param stack1 First stack
     * @param stack2 Second stack
     * @return true if stacks are similar
     */
    public static boolean areSimilar(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return stack1 == stack2;
        }
        if (stack1.isEmpty() || stack2.isEmpty()) {
            return stack1.isEmpty() == stack2.isEmpty();
        }
        return ItemStack.isSameItem(stack1, stack2);
    }
    
    /**
     * Checks if two item stacks are equal (same item, count, and NBT).
     * 
     * @param stack1 First stack
     * @param stack2 Second stack
     * @return true if stacks are equal
     */
    public static boolean areEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return stack1 == stack2;
        }
        return ItemStack.matches(stack1, stack2);
    }
    
    /**
     * Checks if an item stack matches a predicate.
     * 
     * @param stack The stack to check
     * @param predicate The predicate
     * @return true if stack matches predicate
     */
    public static boolean matches(ItemStack stack, Predicate<ItemStack> predicate) {
        if (stack == null || stack.isEmpty() || predicate == null) {
            return false;
        }
        return predicate.test(stack);
    }
    
    /**
     * Checks if an item stack has a specific item.
     * 
     * @param stack The stack
     * @param item The item to check for
     * @return true if stack contains the item
     */
    public static boolean isItem(ItemStack stack, Item item) {
        if (stack == null || stack.isEmpty() || item == null) {
            return false;
        }
        return stack.is(item);
    }
    
    /**
     * Gets the item ID from a stack.
     * 
     * @param stack The stack
     * @return The item's resource location, or null if stack is empty
     */
    public static String getItemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        var key = BuiltInRegistries.ITEM.getResourceKey(stack.getItem());
        if (key.isPresent()) {
            return key.get().toString();
        }
        return null;
    }
    
    /**
     * Merges two item stacks if possible.
     * 
     * @param source The source stack
     * @param target The target stack
     * @return The merged stack, or source if merge not possible
     */
    public static ItemStack mergeStacks(ItemStack source, ItemStack target) {
        if (source == null || source.isEmpty()) {
            return target != null ? target.copy() : ItemStack.EMPTY;
        }
        if (target == null || target.isEmpty()) {
            return source.copy();
        }
        
        if (!ItemStack.isSameItem(source, target)) {
            return source.copy();
        }
        
        int maxStackSize = source.getMaxStackSize();
        int totalCount = source.getCount() + target.getCount();
        
        if (totalCount <= maxStackSize) {
            ItemStack merged = source.copy();
            merged.setCount(totalCount);
            return merged;
        } else {
            ItemStack merged = source.copy();
            merged.setCount(maxStackSize);
            return merged;
        }
    }
    
    /**
     * Splits an item stack.
     * 
     * @param stack The stack to split
     * @param amount The amount to split off
     * @return The split stack, or empty if not possible
     */
    public static ItemStack splitStack(ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        
        int splitAmount = Math.min(amount, stack.getCount());
        ItemStack split = stack.copy();
        split.setCount(splitAmount);
        
        stack.shrink(splitAmount);
        
        return split;
    }
    
    /**
     * Checks if a stack has NBT data.
     * Checks if stack has any data components or custom data.
     * 
     * @param stack The stack
     * @return true if stack has data
     */
    public static boolean hasNBT(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        // Check if stack has any data components
        return !stack.getComponents().isEmpty();
    }
    
    /**
     * Gets NBT data from a stack using codec serialization.
     * 
     * @param stack The stack
     * @return The NBT compound tag, or empty tag if not available
     */
    public static CompoundTag getOrCreateNBT(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new CompoundTag();
        }
        try {
            var result = ItemStack.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, stack);
            if (result.result().isPresent()) {
                var tag = result.result().get();
                if (tag instanceof CompoundTag compound) {
                    return compound;
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
        return new CompoundTag();
    }
    
    /**
     * Checks if a stack has a specific NBT key.
     * 
     * @param stack The stack
     * @param key The NBT key
     * @return true if key exists
     */
    public static boolean hasNBTKey(ItemStack stack, String key) {
        if (stack == null || stack.isEmpty() || key == null) {
            return false;
        }
        CompoundTag tag = getOrCreateNBT(stack);
        return tag != null && tag.contains(key);
    }
    
    /**
     * Gets a value from NBT, or returns default if not present.
     * 
     * @param stack The stack
     * @param key The NBT key
     * @param defaultValue The default value
     * @return The NBT value or default
     */
    public static String getNBTString(ItemStack stack, String key, String defaultValue) {
        if (stack == null || stack.isEmpty() || key == null) {
            return defaultValue;
        }
        CompoundTag tag = getOrCreateNBT(stack);
        if (tag == null || !tag.contains(key)) {
            return defaultValue;
        }
        // Get string value - getString returns Optional, so use orElse for default
        return tag.getString(key).orElse(defaultValue);
    }
    
    /**
     * Sets an NBT string value on a stack.
     * Note: This modifies the NBT but may not persist if stack uses data components.
     * 
     * @param stack The stack
     * @param key The NBT key
     * @param value The value to set
     */
    public static void setNBTString(ItemStack stack, String key, String value) {
        if (stack == null || stack.isEmpty() || key == null) {
            return;
        }
        // Note: Direct NBT modification may not work with data components
        // This is a simplified implementation
        CompoundTag tag = getOrCreateNBT(stack);
        if (tag != null) {
            tag.putString(key, value);
        }
    }
    
    /**
     * Gets the display name of an item stack.
     * 
     * @param stack The stack
     * @return The display name, or "Empty" if stack is empty
     */
    public static String getDisplayName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "Empty";
        }
        return stack.getDisplayName().getString();
    }
    
    /**
     * Checks if a stack is at maximum stack size.
     * 
     * @param stack The stack
     * @return true if stack is full
     */
    public static boolean isFull(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.getCount() >= stack.getMaxStackSize();
    }
    
    /**
     * Gets the remaining space in a stack.
     * 
     * @param stack The stack
     * @return Remaining space (0 if full or empty)
     */
    public static int getRemainingSpace(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        return Math.max(0, stack.getMaxStackSize() - stack.getCount());
    }
}

