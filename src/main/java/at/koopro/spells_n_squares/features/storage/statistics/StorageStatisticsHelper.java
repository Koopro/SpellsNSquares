package at.koopro.spells_n_squares.features.storage.statistics;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class for storage statistics.
 * Provides methods to calculate and display storage statistics.
 */
public final class StorageStatisticsHelper {
    
    private StorageStatisticsHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents storage statistics.
     */
    public record StorageStats(
        int totalItems,
        int filledSlots,
        int emptySlots,
        int uniqueItemTypes,
        Map<Item, Integer> itemCounts,
        int totalValue  // Estimated value (simplified)
    ) {}
    
    /**
     * Calculates statistics for a container.
     * 
     * @param container The container
     * @return Storage statistics
     */
    public static StorageStats calculateStats(Container container) {
        if (container == null) {
            return new StorageStats(0, 0, 0, 0, Collections.emptyMap(), 0);
        }
        
        int totalItems = 0;
        int filledSlots = 0;
        int emptySlots = 0;
        Map<Item, Integer> itemCounts = new HashMap<>();
        
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                emptySlots++;
            } else {
                filledSlots++;
                totalItems += stack.getCount();
                itemCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        
        int uniqueItemTypes = itemCounts.size();
        int totalValue = estimateValue(itemCounts);
        
        return new StorageStats(totalItems, filledSlots, emptySlots, uniqueItemTypes, itemCounts, totalValue);
    }
    
    /**
     * Estimates the total value of items in storage.
     * Simplified implementation - in full version would use actual item values.
     * 
     * @param itemCounts Map of items to counts
     * @return Estimated total value
     */
    private static int estimateValue(Map<Item, Integer> itemCounts) {
        // Simplified value estimation
        // In a full implementation, would use item rarity, crafting cost, etc.
        return itemCounts.values().stream()
            .mapToInt(count -> count * 10) // Placeholder: 10 value per item
            .sum();
    }
    
    /**
     * Gets the most common items in storage.
     * 
     * @param container The container
     * @param count Number of top items to return
     * @return Map of item to count, sorted by frequency
     */
    public static Map<Item, Integer> getMostCommonItems(Container container, int count) {
        if (container == null) {
            return Collections.emptyMap();
        }
        
        StorageStats stats = calculateStats(container);
        
        return stats.itemCounts().entrySet().stream()
            .sorted(Map.Entry.<Item, Integer>comparingByValue().reversed())
            .limit(count)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
    
    /**
     * Gets storage utilization percentage.
     * 
     * @param container The container
     * @return Utilization percentage (0.0 to 1.0)
     */
    public static float getUtilization(Container container) {
        if (container == null) {
            return 0.0f;
        }
        
        StorageStats stats = calculateStats(container);
        int totalSlots = stats.filledSlots() + stats.emptySlots();
        
        if (totalSlots == 0) {
            return 0.0f;
        }
        
        return (float) stats.filledSlots() / totalSlots;
    }
}

