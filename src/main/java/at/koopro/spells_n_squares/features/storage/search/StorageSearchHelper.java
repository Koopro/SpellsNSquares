package at.koopro.spells_n_squares.features.storage.search;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;

/**
 * Helper class for storage search functionality.
 * Provides search and filtering capabilities for storage containers.
 */
public final class StorageSearchHelper {
    
    private StorageSearchHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Search result containing matching items and their slot positions.
     */
    public record SearchResult(ItemStack item, int slot, float relevance) {}
    
    /**
     * Searches a container for items matching a query.
     * 
     * @param container The container to search
     * @param query The search query
     * @return List of search results sorted by relevance
     */
    public static List<SearchResult> search(Container container, String query) {
        if (container == null || query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            
            float relevance = calculateRelevance(stack, lowerQuery);
            if (relevance > 0.0f) {
                results.add(new SearchResult(stack, i, relevance));
            }
        }
        
        // Sort by relevance (highest first)
        results.sort((a, b) -> Float.compare(b.relevance(), a.relevance()));
        
        return results;
    }
    
    /**
     * Calculates relevance score for an item stack against a query.
     * 
     * @param stack The item stack
     * @param query The search query (lowercase)
     * @return Relevance score (0.0 to 1.0)
     */
    private static float calculateRelevance(ItemStack stack, String query) {
        if (stack.isEmpty() || query.isEmpty()) {
            return 0.0f;
        }
        
        String displayName = stack.getDisplayName().getString().toLowerCase();
        String itemId = stack.getItem().toString().toLowerCase();
        
        float score = 0.0f;
        
        // Exact match in display name (highest priority)
        if (displayName.equals(query)) {
            score += 1.0f;
        } else if (displayName.startsWith(query)) {
            score += 0.8f;
        } else if (displayName.contains(query)) {
            score += 0.6f;
        }
        
        // Item ID match
        if (itemId.contains(query)) {
            score += 0.3f;
        }
        
        // Word boundary matches
        String[] words = displayName.split("\\s+");
        for (String word : words) {
            if (word.startsWith(query)) {
                score += 0.2f;
                break;
            }
        }
        
        return Math.min(1.0f, score);
    }
    
    /**
     * Filters a container using a predicate.
     * 
     * @param container The container to filter
     * @param predicate The filter predicate
     * @return List of matching items with their slots
     */
    public static List<SearchResult> filter(Container container, Predicate<ItemStack> predicate) {
        if (container == null || predicate == null) {
            return Collections.emptyList();
        }
        
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
                results.add(new SearchResult(stack, i, 1.0f));
            }
        }
        
        return results;
    }
    
    /**
     * Gets items by category.
     * 
     * @param container The container
     * @param category The category name
     * @return List of items in the category
     */
    public static List<SearchResult> getByCategory(Container container, String category) {
        if (container == null || category == null) {
            return Collections.emptyList();
        }
        
        // Simple category detection based on item type
        Predicate<ItemStack> categoryFilter = switch (category.toLowerCase()) {
            case "tools" -> stack -> {
                var item = stack.getItem();
                return item.getDefaultInstance().is(net.minecraft.tags.ItemTags.AXES) ||
                       item.getDefaultInstance().is(net.minecraft.tags.ItemTags.PICKAXES) ||
                       item.getDefaultInstance().is(net.minecraft.tags.ItemTags.SHOVELS);
            };
            case "weapons" -> stack -> {
                var item = stack.getItem();
                return item.getDefaultInstance().is(net.minecraft.tags.ItemTags.SWORDS);
            };
            case "armor" -> stack -> {
                var item = stack.getItem();
                return item.getDefaultInstance().is(net.minecraft.tags.ItemTags.ARMOR_ENCHANTABLE);
            };
            case "food" -> stack -> {
                // Simplified food check - check item tags or use a simple heuristic
                // In a full implementation, would check item tags or use registry
                return false; // Placeholder - would need proper food detection
            };
            case "blocks" -> stack -> stack.getItem() instanceof net.minecraft.world.item.BlockItem;
            default -> stack -> false;
        };
        
        return filter(container, categoryFilter);
    }
}

