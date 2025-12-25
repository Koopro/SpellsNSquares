package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating biome data files.
 */
public class ModBiomeDataProvider extends DatapackBuiltinEntriesProvider {
    
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.BIOME, ModBiomeProvider::bootstrap);
    
    public ModBiomeDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(SpellsNSquares.MODID));
    }
    
    @Override
    public String getName() {
        return "Spells N Squares Biomes";
    }
}











