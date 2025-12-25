package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Centralized configuration for item data generation.
 * Manages which items should be auto-generated vs which have custom models.
 */
public class ItemDatagenConfig {
    
    private static final String modId = SpellsNSquares.MODID;
    private static Set<String> itemsWithCustomModels;
    private static boolean initialized = false;
    
    /**
     * Items that explicitly should not have models generated.
     * These are typically GeckoLib items or items with custom JSON models.
     */
    private static final Set<String> EXPLICIT_SKIP_ITEMS = Set.of(
        "rubber_duck",
        "flashlight",
        "demo_wand",
        "demiguise_cloak",
        "deathly_hallow_cloak",
        "newts_case"
    );
    
    /**
     * Initializes the configuration by scanning for custom model files.
     * Should be called once before using the config.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        itemsWithCustomModels = new HashSet<>(EXPLICIT_SKIP_ITEMS);
        
        // Auto-detect items with custom JSON models in resources
        try {
            Path currentDir = Paths.get("").toAbsolutePath();
            Path projectRoot = currentDir.getParent(); // Go up from run/ to project root
            Path itemsDir = projectRoot.resolve("src/main/resources/assets/" + modId + "/items");
            
            if (Files.exists(itemsDir) && Files.isDirectory(itemsDir)) {
                try (Stream<Path> paths = Files.list(itemsDir)) {
                    paths.filter(Files::isRegularFile)
                         .filter(path -> path.toString().endsWith(".json"))
                         .forEach(path -> {
                             String fileName = path.getFileName().toString();
                             String itemName = fileName.substring(0, fileName.length() - 5); // Remove .json
                             itemsWithCustomModels.add(itemName);
                         });
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not scan for custom item models: " + e.getMessage());
            // Fall back to explicit list only
        }
        
        initialized = true;
    }
    
    /**
     * Checks if an item should have its model auto-generated.
     * @param itemName The registry name of the item
     * @return true if the model should be generated, false if it has a custom model
     */
    public static boolean shouldGenerateModel(String itemName) {
        if (!initialized) {
            initialize();
        }
        return !itemsWithCustomModels.contains(itemName);
    }
    
    /**
     * Checks if an item has a custom model (GeckoLib or manual JSON).
     * @param itemName The registry name of the item
     * @return true if the item has a custom model
     */
    public static boolean hasCustomModel(String itemName) {
        if (!initialized) {
            initialize();
        }
        return itemsWithCustomModels.contains(itemName);
    }
    
    /**
     * Gets all items that have custom models.
     * @return Set of item names with custom models
     */
    public static Set<String> getItemsWithCustomModels() {
        if (!initialized) {
            initialize();
        }
        return new HashSet<>(itemsWithCustomModels);
    }
    
    /**
     * Checks if an item is a GeckoLib item (implements GeoItem interface).
     * This is a helper method for determining item type.
     */
    public static boolean isGeckoLibItem(Item item) {
        return item instanceof GeoItem;
    }
}








