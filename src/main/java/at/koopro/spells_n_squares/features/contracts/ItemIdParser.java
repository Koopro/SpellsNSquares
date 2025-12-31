package at.koopro.spells_n_squares.features.contracts;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Utility class for parsing and validating item IDs for contract requirements.
 */
public final class ItemIdParser {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Cache for parsed items to avoid repeated registry lookups
    private static final java.util.Map<String, ParsedItem> itemCache = new java.util.HashMap<>();
    
    private ItemIdParser() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Parsed item data.
     */
    public record ParsedItem(Identifier itemId, Item item) {
        /**
         * Checks if the parsed item is valid.
         */
        public boolean isValid() {
            return itemId != null && item != null;
        }
    }
    
    /**
     * Parses an item ID string and validates that the item exists.
     * 
     * @param itemIdStr The item ID string to parse (format: "namespace:path")
     * @return ParsedItem with parsed identifier and item, or null if parsing/validation failed
     */
    public static ParsedItem parse(String itemIdStr) {
        if (itemIdStr == null || itemIdStr.isEmpty()) {
            return null;
        }
        
        // Check cache first
        String trimmed = itemIdStr.trim();
        ParsedItem cached = itemCache.get(trimmed);
        if (cached != null) {
            return cached;
        }
        
        try {
            Identifier itemId = Identifier.parse(trimmed);
            Optional<Item> itemOptional = BuiltInRegistries.ITEM.getOptional(itemId);
            
            if (itemOptional.isEmpty()) {
                LOGGER.warn("Item ID '{}' does not exist in registry", itemIdStr);
                return null;
            }
            
            Item item = itemOptional.get();
            ParsedItem parsed = new ParsedItem(itemId, item);
            
            if (!parsed.isValid()) {
                LOGGER.warn("Parsed item contains invalid values: {}", parsed);
                return null;
            }
            
            // Cache the parsed item
            itemCache.put(trimmed, parsed);
            return parsed;
        } catch (Exception e) {
            LOGGER.warn("Failed to parse item ID '{}': {}", itemIdStr, e.getMessage());
            return null;
        }
    }
    
    /**
     * Validates if an item ID string is in the correct format and the item exists.
     * 
     * @param itemIdStr The item ID string to validate
     * @return true if the format is valid and item exists, false otherwise
     */
    public static boolean isValid(String itemIdStr) {
        return parse(itemIdStr) != null;
    }
}

