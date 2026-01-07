package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Predicate;

/**
 * Helper class for storage system enhancements including auto-sorting,
 * search/filter, and storage linking functionality.
 */
public final class StorageEnhancementHelper {
    
    private StorageEnhancementHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Sorts items in a container by item type, then by stack size (largest first).
     * 
     * @param container The container to sort
     * @return true if sorting was performed
     */
    public static boolean autoSort(Container container) {
        if (container == null) {
            return false;
        }
        
        // Collect all non-empty stacks
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
                container.setItem(i, ItemStack.EMPTY);
            }
        }
        
        if (stacks.isEmpty()) {
            return false;
        }
        
        // Sort stacks: by item type, then by count (descending)
        stacks.sort((a, b) -> {
            // First sort by item type
            int itemCompare = BuiltInRegistries.ITEM.getKey(a.getItem())
                .compareTo(BuiltInRegistries.ITEM.getKey(b.getItem()));
            if (itemCompare != 0) {
                return itemCompare;
            }
            // Then by count (descending)
            return Integer.compare(b.getCount(), a.getCount());
        });
        
        // Place sorted stacks back
        int slot = 0;
        for (ItemStack stack : stacks) {
            if (slot >= container.getContainerSize()) {
                break;
            }
            container.setItem(slot, stack);
            slot++;
        }
        
        DevLogger.logStateChange(StorageEnhancementHelper.class, "autoSort",
            "Sorted " + stacks.size() + " items in container");
        return true;
    }
    
    /**
     * Filters items in a container based on a predicate.
     * Returns a list of slots containing matching items.
     * 
     * @param container The container to search
     * @param filter The filter predicate
     * @return List of slot indices containing matching items
     */
    public static List<Integer> filterSlots(Container container, Predicate<ItemStack> filter) {
        if (container == null || filter == null) {
            return Collections.emptyList();
        }
        
        List<Integer> matchingSlots = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && filter.test(stack)) {
                matchingSlots.add(i);
            }
        }
        
        return matchingSlots;
    }
    
    /**
     * Searches for items by name (case-insensitive partial match).
     * 
     * @param container The container to search
     * @param searchTerm The search term
     * @return List of slot indices containing matching items
     */
    public static List<Integer> searchByName(Container container, String searchTerm) {
        if (container == null || searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerSearch = searchTerm.toLowerCase().trim();
        
        return filterSlots(container, stack -> {
            String itemName = stack.getDisplayName().getString().toLowerCase();
            return itemName.contains(lowerSearch);
        });
    }
    
    /**
     * Searches for items by item type (exact match).
     * 
     * @param container The container to search
     * @param item The item to search for
     * @return List of slot indices containing the item
     */
    public static List<Integer> searchByItem(Container container, Item item) {
        if (container == null || item == null) {
            return Collections.emptyList();
        }
        
        return filterSlots(container, stack -> stack.getItem().equals(item));
    }
    
    /**
     * Gets a summary of items in the container grouped by type.
     * 
     * @param container The container
     * @return Map of item to total count
     */
    public static Map<Item, Integer> getItemSummary(Container container) {
        if (container == null) {
            return Collections.emptyMap();
        }
        
        Map<Item, Integer> summary = new HashMap<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                summary.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        
        return summary;
    }
    
    /**
     * Represents a linked storage connection.
     */
    public record LinkedStorage(
        net.minecraft.core.BlockPos position,
        ResourceKey<Level> dimension,
        String name
    ) {}
    
    /**
     * Storage linking manager for connecting multiple storage blocks.
     */
    public static final class StorageLinkManager {
        private static final Map<UUID, List<LinkedStorage>> PLAYER_LINKS = 
            new HashMap<>();
        
        private StorageLinkManager() {
            // Utility class
        }
        
        /**
         * Links a storage block to a player's storage network.
         * 
         * @param playerId The player's UUID
         * @param storage The storage to link
         * @return true if linked successfully
         */
        public static boolean linkStorage(UUID playerId, LinkedStorage storage) {
            if (playerId == null || storage == null) {
                return false;
            }
            
            List<LinkedStorage> links = PLAYER_LINKS.computeIfAbsent(playerId, k -> new ArrayList<>());
            
            // Check if already linked
            if (links.stream().anyMatch(s -> 
                s.position().equals(storage.position()) && 
                s.dimension().equals(storage.dimension()))) {
                return false;
            }
            
            links.add(storage);
            DevLogger.logStateChange(StorageLinkManager.class, "linkStorage",
                "Linked storage: " + storage.name() + " at " + storage.position());
            return true;
        }
        
        /**
         * Unlinks a storage block from a player's network.
         * 
         * @param playerId The player's UUID
         * @param position The storage position
         * @param dimension The storage dimension
         * @return true if unlinked
         */
        public static boolean unlinkStorage(
                UUID playerId,
                net.minecraft.core.BlockPos position,
                ResourceKey<Level> dimension) {
            
            if (playerId == null) {
                return false;
            }
            
            List<LinkedStorage> links = PLAYER_LINKS.get(playerId);
            if (links == null) {
                return false;
            }
            
            boolean removed = links.removeIf(s -> 
                s.position().equals(position) && s.dimension().equals(dimension));
            
            if (removed) {
                DevLogger.logStateChange(StorageLinkManager.class, "unlinkStorage",
                    "Unlinked storage at " + position);
            }
            
            return removed;
        }
        
        /**
         * Gets all linked storages for a player.
         * 
         * @param playerId The player's UUID
         * @return List of linked storages
         */
        public static List<LinkedStorage> getLinkedStorages(UUID playerId) {
            if (playerId == null) {
                return Collections.emptyList();
            }
            
            return new ArrayList<>(PLAYER_LINKS.getOrDefault(playerId, Collections.emptyList()));
        }
        
        /**
         * Clears all links for a player.
         * 
         * @param playerId The player's UUID
         */
        public static void clearLinks(UUID playerId) {
            if (playerId != null) {
                PLAYER_LINKS.remove(playerId);
            }
        }
    }
}

