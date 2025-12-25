package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModBiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.concurrent.CompletableFuture;

/**
 * Generates biome data files for all magical biomes.
 */
public class ModBiomeProvider {
    
    public static void bootstrap(BootstrapContext<Biome> context) {
        context.register(ModBiomes.FORBIDDEN_FOREST, createForbiddenForest(context));
        context.register(ModBiomes.BLACK_LAKE, createBlackLake(context));
        context.register(ModBiomes.AZKABAN, createAzkaban(context));
        context.register(ModBiomes.MAGICAL_MEADOW, createMagicalMeadow(context));
        context.register(ModBiomes.DARK_FOREST_EDGE, createDarkForestEdge(context));
    }
    
    /**
     * Creates the Forbidden Forest biome.
     * Dark, dense forest with magical creatures.
     */
    private static Biome createForbiddenForest(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        
        // Add magical creature spawns (will be configured via biome modifiers)
        // Base spawns removed - all spawns handled via biome modifiers
        
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(
            context.lookup(Registries.PLACED_FEATURE),
            context.lookup(Registries.CONFIGURED_CARVER)
        );
        
        // Use dark forest as base, but customize
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.7f)
            .downfall(0.8f)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .waterColor(0x3F76E4)
                    .build()
            )
            .mobSpawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .build();
    }
    
    /**
     * Creates the Black Lake biome.
     * Underwater magical biome with merpeople and aquatic creatures.
     */
    private static Biome createBlackLake(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(
            context.lookup(Registries.PLACED_FEATURE),
            context.lookup(Registries.CONFIGURED_CARVER)
        );
        
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.5f)
            .downfall(1.0f)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .waterColor(0x1A1A2E) // Dark blue water
                    .build()
            )
            .mobSpawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .build();
    }
    
    /**
     * Creates the Azkaban biome.
     * Dark, cold biome with dementor spawns.
     */
    private static Biome createAzkaban(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(
            context.lookup(Registries.PLACED_FEATURE),
            context.lookup(Registries.CONFIGURED_CARVER)
        );
        
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.0f) // Very cold
            .downfall(0.9f)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .waterColor(0x2C3E50)
                    .build()
            )
            .mobSpawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .build();
    }
    
    /**
     * Creates the Magical Meadow biome.
     * Light, peaceful biome with unicorns and fairies.
     */
    private static Biome createMagicalMeadow(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(
            context.lookup(Registries.PLACED_FEATURE),
            context.lookup(Registries.CONFIGURED_CARVER)
        );
        
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.8f)
            .downfall(0.4f)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .waterColor(0x87CEEB) // Light blue
                    .build()
            )
            .mobSpawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .build();
    }
    
    /**
     * Creates the Dark Forest Edge biome.
     * Transition biome between normal forest and Forbidden Forest.
     */
    private static Biome createDarkForestEdge(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(
            context.lookup(Registries.PLACED_FEATURE),
            context.lookup(Registries.CONFIGURED_CARVER)
        );
        
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.7f)
            .downfall(0.8f)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .waterColor(0x3F76E4)
                    .build()
            )
            .mobSpawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .build();
    }
    
    /**
     * Calculates sky color based on temperature.
     */
    private static int calculateSkyColor(float temperature) {
        float f = temperature / 3.0F;
        f = Mth.clamp(f, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }
}











