package at.koopro.spells_n_squares.core.data;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Generic helper for data component access patterns with default creation.
 * Provides common patterns for getting or creating data components on item stacks.
 * 
 * <p>This utility complements ItemDataHelper by providing higher-level patterns
 * that handle default value creation and setting.
 */
public final class DataComponentHelper {
    private DataComponentHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets a data component from an item stack, or creates and sets a default value if not present.
     * This is a common pattern where data should be initialized on first access.
     * 
     * <p>Example usage:
     * <pre>{@code
     * BagInventoryComponent data = DataComponentHelper.getOrCreateData(
     *     stack,
     *     BagInventoryData.BAG_INVENTORY.get(),
     *     () -> BagInventoryComponent.createDefault(27)
     * );
     * }</pre>
     * 
     * @param stack The item stack
     * @param type The data component type
     * @param defaultSupplier Supplier that creates the default value if not present
     * @return The data component (either existing or newly created default)
     */
    public static <T> T getOrCreateData(ItemStack stack, 
                                         DataComponentType<T> type, 
                                         Supplier<T> defaultSupplier) {
        if (stack == null || stack.isEmpty() || type == null) {
            return null;
        }
        
        return ItemDataHelper.getData(stack, type)
            .orElseGet(() -> {
                T defaultData = defaultSupplier.get();
                if (defaultData != null) {
                    ItemDataHelper.setData(stack, type, defaultData);
                }
                return defaultData;
            });
    }
    
    /**
     * Gets a data component from an item stack, or returns a default value if not present.
     * Unlike getOrCreateData, this does not set the default value on the stack.
     * 
     * <p>Use this when you want to read data but don't want to modify the stack
     * if the data is missing.
     * 
     * @param stack The item stack
     * @param type The data component type
     * @param defaultValue The default value to return if not present
     * @return The data component or default value
     */
    public static <T> T getDataOrDefault(ItemStack stack, 
                                         DataComponentType<T> type, 
                                         T defaultValue) {
        return ItemDataHelper.getDataOrDefault(stack, type, defaultValue);
    }
}


