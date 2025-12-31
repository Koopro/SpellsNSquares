package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.features.artifacts.ArtifactsRegistry;
import at.koopro.spells_n_squares.features.automation.AutomationRegistry;
import at.koopro.spells_n_squares.features.building.BuildingRegistry;
import at.koopro.spells_n_squares.features.cloak.CloakRegistry;
import at.koopro.spells_n_squares.features.combat.CombatRegistry;
import at.koopro.spells_n_squares.features.communication.CommunicationRegistry;
import at.koopro.spells_n_squares.features.economy.EconomyRegistry;
import at.koopro.spells_n_squares.features.education.EducationRegistry;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentsRegistry;
import at.koopro.spells_n_squares.features.flashlight.FlashlightRegistry;
import at.koopro.spells_n_squares.features.navigation.NavigationRegistry;
import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.quidditch.QuidditchRegistry;
import at.koopro.spells_n_squares.features.robes.RobesRegistry;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import at.koopro.spells_n_squares.features.transportation.TransportationRegistry;
import at.koopro.spells_n_squares.features.wand.WandRegistry;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates item model JSON files for all mod items.
 * Skips items that already have models (like GeckoLib items).
 */
public class ModItemModelProvider implements DataProvider {
    
    private final PackOutput output;
    private final String modId;
    
    public ModItemModelProvider(PackOutput output) {
        this.output = output;
        this.modId = SpellsNSquares.MODID;
        // Initialize the datagen config to scan for custom models
        ItemDatagenConfig.initialize();
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // Collect all item registries from feature registries
        List<DeferredRegister.Items> itemRegistries = List.of(
            ModItems.ITEMS,  // Generic items
            FlashlightRegistry.ITEMS,
            WandRegistry.ITEMS,
            CloakRegistry.ITEMS,
            ArtifactsRegistry.ITEMS,
            StorageRegistry.ITEMS,
            TransportationRegistry.ITEMS,
            CommunicationRegistry.ITEMS,
            AutomationRegistry.ITEMS,
            BuildingRegistry.ITEMS,
            NavigationRegistry.ITEMS,
            RobesRegistry.ITEMS,
            PotionsRegistry.ITEMS,
            QuidditchRegistry.ITEMS,
            EconomyRegistry.ITEMS,
            EducationRegistry.ITEMS,
            CombatRegistry.ITEMS,
            EnchantmentsRegistry.ITEMS
        );
        
        // Generate models for all items from all registries
        // Note: ModBlockModelProvider already generates item models for blocks,
        // but we still process BlockItems here to ensure they have models
        for (DeferredRegister.Items registry : itemRegistries) {
            registry.getEntries().forEach(holder -> {
                try {
                    // Get the registry name - try holder.getId() first, then fall back to getting from registry
                    String itemName;
                    try {
                        itemName = holder.getId().getPath();
                    } catch (Exception e) {
                        // Fallback: get from the item's registry key
                        Item item = holder.get();
                        itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
                    }
                    
                    Item item = holder.get();
                    
                    // Skip BlockItems - ModBlockModelProvider already generates item models for blocks
                    // This prevents duplicate generation and ensures consistency
                    if (item instanceof BlockItem) {
                        return;
                    }
                    
                    // Skip items that already have custom models (GeckoLib or manual JSON)
                    if (!ItemDatagenConfig.shouldGenerateModel(itemName)) {
                        return;
                    }
                    
                    // Determine model type based on item class/name
                    String modelType = determineModelType(itemName, item);
                    
                    // Generate model for non-block items
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
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    private String determineModelType(String itemName, Item item) {
        // Block items - check if item is a BlockItem instance first
        if (item instanceof BlockItem) {
            return "block";
        }
        
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

















