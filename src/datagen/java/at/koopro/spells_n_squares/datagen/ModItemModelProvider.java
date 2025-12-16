package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModItems;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Generates item model JSON files for all mod items.
 * Skips items that already have models (like GeckoLib items).
 */
public class ModItemModelProvider implements DataProvider {
    
    private final PackOutput output;
    private final String modId;
    
    // Items that already have models and should be skipped
    private static final Set<String> EXISTING_MODELS = Set.of(
        "rubber_duck",
        "flashlight",
        "demo_wand",
        "demiguise_cloak",
        "deathly_hallow_cloak"
    );
    
    public ModItemModelProvider(PackOutput output) {
        this.output = output;
        this.modId = SpellsNSquares.MODID;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // Generate models for all items
        ModItems.ITEMS.getEntries().forEach(holder -> {
            try {
                // Get the registry name - try holder.getId() first, then fall back to getting from registry
                String itemName;
                try {
                    itemName = holder.getId().getPath();
                } catch (Exception e) {
                    // Fallback: get from the item's registry key
                    Item item = holder.get();
                    itemName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).getPath();
                }
                
                // Skip items that already have models
                if (EXISTING_MODELS.contains(itemName)) {
                    return;
                }
                
                Item item = holder.get();
                
                // Determine model type based on item class/name
                String modelType = determineModelType(itemName, item);
                
                // Always generate a model (modelType should never be null, but ensure we generate anyway)
                if (modelType != null) {
                    futures.add(generateItemModel(cache, itemName, modelType));
                } else {
                    // Fallback: generate default model if somehow modelType is null
                    futures.add(generateItemModel(cache, itemName, "generated"));
                }
            } catch (Exception e) {
                // Log error but continue with other items
                System.err.println("Failed to generate model for item: " + holder.getId() + " - " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private String determineModelType(String itemName, Item item) {
        // Handheld items (tools, wands, broomsticks)
        if (itemName.contains("wand") || 
            itemName.contains("broomstick") ||
            itemName.contains("hoe") ||
            itemName.contains("mirror") ||
            itemName.equals("deluminator") ||
            itemName.equals("time_turner") ||
            itemName.equals("sneakoscope") ||
            itemName.equals("elder_wand")) {
            return "handheld";
        }
        
        // Armor items (robes) - use generated model
        if (itemName.contains("robe")) {
            return "generated";
        }
        
        // Block items - reference block model
        if (itemName.contains("plant") || 
            itemName.contains("trunk") ||
            itemName.contains("chest") ||
            itemName.contains("cauldron") ||
            itemName.contains("furnace") ||
            itemName.contains("light") ||
            itemName.contains("farm") ||
            itemName.contains("collector") ||
            itemName.contains("composter") ||
            itemName.contains("generator") ||
            itemName.contains("enchantment_table") ||
            itemName.contains("hourglass") ||
            itemName.contains("arena") ||
            itemName.contains("post") ||
            itemName.contains("shop") ||
            itemName.contains("vault") ||
            itemName.contains("board") ||
            itemName.contains("willow")) {
            return "block";
        }
        
        // Default: generated model for most items
        return "generated";
    }
    
    private CompletableFuture<?> generateItemModel(CachedOutput cache, String itemName, String modelType) {
        JsonObject itemModel = new JsonObject();
        
        if ("handheld".equals(modelType)) {
            itemModel.addProperty("parent", "minecraft:item/handheld");
        } else if ("block".equals(modelType)) {
            // Reference block model
            itemModel.addProperty("parent", modId + ":block/" + itemName);
            return saveJson(cache, itemModel, getItemModelPath(itemName));
        } else {
            // Default: generated
            itemModel.addProperty("parent", "minecraft:item/generated");
        }
        
        // Add texture layer
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", modId + ":item/" + itemName);
        itemModel.add("textures", textures);
        
        return saveJson(cache, itemModel, getItemModelPath(itemName));
    }
    
    private Path getItemModelPath(String name) {
        return output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(modId).resolve("models").resolve("item").resolve(name + ".json");
    }
    
    private CompletableFuture<?> saveJson(CachedOutput cache, JsonObject json, Path path) {
        return DataProvider.saveStable(cache, json, path);
    }
    
    @Override
    public String getName() {
        return "Item Models - " + modId;
    }
}




