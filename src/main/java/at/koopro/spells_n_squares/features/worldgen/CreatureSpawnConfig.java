package at.koopro.spells_n_squares.features.worldgen;

import at.koopro.spells_n_squares.core.registry.ModBiomes;
import at.koopro.spells_n_squares.core.registry.ModEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures creature spawn rules for magical biomes.
 * Defines which creatures spawn in which biomes with what rates and conditions.
 */
public final class CreatureSpawnConfig {
    private CreatureSpawnConfig() {
    }
    
    /**
     * Spawn configuration for a creature in a biome.
     */
    public record SpawnConfig(
        ResourceKey<Biome> biome,
        EntityType<?> entityType,
        MobCategory category,
        int weight,
        int minCount,
        int maxCount
    ) {}
    
    // Map of biome to spawn configurations
    private static final Map<ResourceKey<Biome>, java.util.List<SpawnConfig>> biomeSpawns = new HashMap<>();
    
    static {
        initializeSpawnConfigs();
    }
    
    /**
     * Initializes spawn configurations for all magical biomes.
     */
    private static void initializeSpawnConfigs() {
        // Forbidden Forest spawns
        // TODO: Re-enable when creature entities are implemented
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.CENTAUR.get(), MobCategory.CREATURE, 5, 1, 3);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.UNICORN.get(), MobCategory.CREATURE, 3, 1, 2);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.ACROMANTULA.get(), MobCategory.MONSTER, 4, 1, 2);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.THESTRAL.get(), MobCategory.CREATURE, 2, 1, 2);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.BOWTRUCKLE.get(), MobCategory.CREATURE, 8, 2, 4);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.PUFFSKEIN.get(), MobCategory.CREATURE, 6, 1, 3);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.NIFFLER.get(), MobCategory.CREATURE, 4, 1, 2);
        // addSpawn(ModBiomes.FORBIDDEN_FOREST, ModEntities.KNEAZLE.get(), MobCategory.CREATURE, 3, 1, 2);
        
        // Black Lake spawns
        // TODO: Re-enable when creature entities are implemented
        // addSpawn(ModBiomes.BLACK_LAKE, ModEntities.MERPEOPLE.get(), MobCategory.WATER_CREATURE, 5, 1, 3);
        // addSpawn(ModBiomes.BLACK_LAKE, ModEntities.GRINDYLOW.get(), MobCategory.WATER_CREATURE, 4, 1, 2);
        // addSpawn(ModBiomes.BLACK_LAKE, ModEntities.KELPIE.get(), MobCategory.WATER_CREATURE, 3, 1, 2);
        // addSpawn(ModBiomes.BLACK_LAKE, ModEntities.HIPPOCAMPUS.get(), MobCategory.WATER_CREATURE, 2, 1, 2);
        // addSpawn(ModBiomes.BLACK_LAKE, ModEntities.RAMORA.get(), MobCategory.WATER_CREATURE, 6, 2, 4);
        
        // Azkaban spawns
        // TODO: Re-enable when creature entities are implemented
        // addSpawn(ModBiomes.AZKABAN, ModEntities.DEMENTOR.get(), MobCategory.MONSTER, 8, 2, 4);
        
        // Magical Meadow spawns
        // TODO: Re-enable when creature entities are implemented
        // addSpawn(ModBiomes.MAGICAL_MEADOW, ModEntities.UNICORN.get(), MobCategory.CREATURE, 6, 1, 3);
        // addSpawn(ModBiomes.MAGICAL_MEADOW, ModEntities.FAIRY.get(), MobCategory.CREATURE, 10, 3, 6);
        // addSpawn(ModBiomes.MAGICAL_MEADOW, ModEntities.PUFFSKEIN.get(), MobCategory.CREATURE, 5, 1, 3);
        // addSpawn(ModBiomes.MAGICAL_MEADOW, ModEntities.MOONCALF.get(), MobCategory.CREATURE, 4, 1, 2);
        
        // Dark Forest Edge spawns (transitional)
        // TODO: Re-enable when creature entities are implemented
        // addSpawn(ModBiomes.DARK_FOREST_EDGE, ModEntities.BOWTRUCKLE.get(), MobCategory.CREATURE, 4, 1, 2);
        // addSpawn(ModBiomes.DARK_FOREST_EDGE, ModEntities.PUFFSKEIN.get(), MobCategory.CREATURE, 3, 1, 2);
    }
    
    /**
     * Adds a spawn configuration for a biome.
     */
    private static void addSpawn(ResourceKey<Biome> biome, EntityType<?> entityType, MobCategory category, 
                                 int weight, int minCount, int maxCount) {
        biomeSpawns.computeIfAbsent(biome, k -> new java.util.ArrayList<>())
            .add(new SpawnConfig(biome, entityType, category, weight, minCount, maxCount));
    }
    
    /**
     * Gets all spawn configurations for a biome.
     */
    public static java.util.List<SpawnConfig> getSpawnsForBiome(ResourceKey<Biome> biome) {
        return biomeSpawns.getOrDefault(biome, java.util.Collections.emptyList());
    }
    
    /**
     * Gets all configured biomes.
     */
    public static java.util.Set<ResourceKey<Biome>> getConfiguredBiomes() {
        return biomeSpawns.keySet();
    }
}



