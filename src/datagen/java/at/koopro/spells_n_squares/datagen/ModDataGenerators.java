package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point for data generation.
 * Registers all data providers for the mod.
 * 
 * NeoForge 21.11 splits GatherDataEvent into Client and Server variants.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ModDataGenerators {
    
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        
        // Custom tree block model provider - bypasses NeoForge's item validation
        // Only generates models for tree blocks, not custom GeckoLib items
        generator.addProvider(true, new TreeBlockModelProvider(output));
        
        // Generate models for non-tree blocks
        generator.addProvider(true, new ModBlockModelProvider(output));
        
        // Generate models for items
        generator.addProvider(true, new ModItemModelProvider(output));
        
        // Copy GeckoLib item JSON files from main resources to generated resources
        generator.addProvider(true, new GeckoLibItemModelProvider(output));
        
        // Generate placeholder textures for missing items and blocks
        generator.addProvider(true, new ModTextureProvider(output));
    }
    
    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        // Server-side providers (tags, loot, recipes, biomes)
        generator.addProvider(true, new ModBlockTagsProvider(output, lookupProvider));
        generator.addProvider(true, new ModBlockLootProvider(output, lookupProvider));
        generator.addProvider(true, new ModRecipeProvider.Runner(output, lookupProvider));
        generator.addProvider(true, new ModBiomeDataProvider(output, lookupProvider));
        generator.addProvider(true, new ModBiomeModifierProvider(output, lookupProvider));
    }
}















