package at.koopro.spells_n_squares.core.data;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Generic helper for data component access patterns.
 * Provides type-safe methods for getting, setting, and checking data components.
 */
public final class ItemDataHelper {
    private ItemDataHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets a data component from an item stack.
     * 
     * @param stack The item stack
     * @param type The data component type
     * @return Optional containing the data component, or empty if not present
     */
    public static <T> Optional<T> getData(ItemStack stack, DataComponentType<T> type) {
        if (stack == null || stack.isEmpty() || type == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(stack.get(type));
    }
    
    /**
     * Gets a data component from an item stack, or returns a default value.
     * 
     * @param stack The item stack
     * @param type The data component type
     * @param defaultValue The default value to return if not present
     * @return The data component or default value
     */
    public static <T> T getDataOrDefault(ItemStack stack, DataComponentType<T> type, T defaultValue) {
        return getData(stack, type).orElse(defaultValue);
    }
    
    /**
     * Sets a data component on an item stack.
     * 
     * @param stack The item stack
     * @param type The data component type
     * @param value The value to set
     */
    public static <T> void setData(ItemStack stack, DataComponentType<T> type, T value) {
        if (stack == null || stack.isEmpty() || type == null) {
            return;
        }
        stack.set(type, value);
    }
    
    /**
     * Checks if an item stack has a data component.
     * 
     * @param stack The item stack
     * @param type The data component type
     * @return true if the data component is present, false otherwise
     */
    public static <T> boolean hasData(ItemStack stack, DataComponentType<T> type) {
        if (stack == null || stack.isEmpty() || type == null) {
            return false;
        }
        return stack.get(type) != null;
    }
    
    /**
     * Removes a data component from an item stack.
     * 
     * @param stack The item stack
     * @param type The data component type
     */
    public static <T> void removeData(ItemStack stack, DataComponentType<T> type) {
        if (stack == null || stack.isEmpty() || type == null) {
            return;
        }
        stack.remove(type);
    }
}


