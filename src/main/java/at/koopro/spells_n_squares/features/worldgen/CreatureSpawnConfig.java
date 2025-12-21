package at.koopro.spells_n_squares.features.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;

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
        // TODO: Re-enable when creature entities are implemented (CENTAUR, UNICORN, ACROMANTULA, THESTRAL, BOWTRUCKLE, PUFFSKEIN, NIFFLER, KNEAZLE)
        
        // Black Lake spawns
        // TODO: Re-enable when creature entities are implemented (MERPEOPLE, GRINDYLOW, KELPIE, HIPPOCAMPUS, RAMORA)
        
        // Azkaban spawns
        // TODO: Re-enable when creature entities are implemented (DEMENTOR)
        
        // Magical Meadow spawns
        // TODO: Re-enable when creature entities are implemented (UNICORN, FAIRY, PUFFSKEIN, MOONCALF)
        
        // Dark Forest Edge spawns (transitional)
        // TODO: Re-enable when creature entities are implemented (BOWTRUCKLE, PUFFSKEIN)
    }
    
    /**
     * Adds a spawn configuration for a biome.
     * Note: Currently unused as all spawn configurations are disabled.
     * This method will be used when creature entities are implemented.
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
