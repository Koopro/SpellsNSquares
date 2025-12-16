package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.worldgen.CreatureSpawnConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.data.DataMapProvider;
// Biome modifiers are handled via biome data files, not through NeoForgeBiomeModifiers
// import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeBiomeModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates biome modifier data files for creature spawning in magical biomes.
 */
public class ModBiomeModifierProvider extends DataMapProvider {
    
    public ModBiomeModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    
    @Override
    protected void gather(HolderLookup.Provider provider) {
        var biomeLookup = provider.lookupOrThrow(Registries.BIOME);
        var entityLookup = provider.lookupOrThrow(Registries.ENTITY_TYPE);
        
        // Note: Biome modifiers are now handled directly in biome data files
        // Spawns are configured in the biome's MobSpawnSettings during biome creation
        // This provider is kept for potential future use with data maps
        // For now, spawns should be added directly in ModBiomeProvider.create* methods
        // TODO: Implement biome modifier data map registration when API is available
        
        // Group spawns by biome to create one modifier per biome
        // Only process biomes that are available in the lookup (may not be available during datagen)
        for (var biomeKey : CreatureSpawnConfig.getConfiguredBiomes()) {
            var biomeHolder = biomeLookup.get(biomeKey);
            if (biomeHolder.isEmpty()) {
                // Biome not yet available in lookup during datagen - skip for now
                continue;
            }
            
            List<MobSpawnSettings.SpawnerData> spawners = new ArrayList<>();
            
            // The code below is commented out until we can properly implement biome modifiers
            /*
            for (var spawnConfig : CreatureSpawnConfig.getSpawnsForBiome(biomeKey)) {
                // Get ResourceKey from EntityType
                var entityKey = spawnConfig.entityType().builtInRegistryHolder().key();
                var entityHolder = entityLookup.get(entityKey);
                if (entityHolder.isPresent()) {
                    spawners.add(new MobSpawnSettings.SpawnerData(
                        spawnConfig.entityType(),
                        spawnConfig.weight(),
                        spawnConfig.minCount(),
                        spawnConfig.maxCount()
                    ));
                }
            }
            
            if (!spawners.isEmpty()) {
                // TODO: Implement biome modifier data map registration when API is available
            }
            */
        }
    }
}
