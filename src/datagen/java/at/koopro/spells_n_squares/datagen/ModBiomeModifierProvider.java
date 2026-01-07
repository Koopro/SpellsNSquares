package at.koopro.spells_n_squares.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

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
        // Note: Biome modifiers are now handled directly in biome data files
        // Spawns are configured in the biome's MobSpawnSettings during biome creation
        // This provider is kept for potential future use with data maps
        // For now, spawns should be added directly in ModBiomeProvider.create* methods
        // TODO: Implement biome modifier data map registration when API is available
        // TODO: Re-enable when CreatureSpawnConfig is implemented
        // for (var biomeKey : CreatureSpawnConfig.getConfiguredBiomes()) {
        //     // TODO: Implement biome modifier data map registration when API is available
        // }
    }
}
