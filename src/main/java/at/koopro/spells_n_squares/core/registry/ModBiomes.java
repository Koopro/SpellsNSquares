package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Registry for all magical biomes.
 * Biomes are defined via data files, but this class provides ResourceKeys for referencing them.
 */
public final class ModBiomes {
    private ModBiomes() {
    }
    
    // Biome ResourceKeys
    public static final ResourceKey<Biome> FORBIDDEN_FOREST = createKey("forbidden_forest");
    public static final ResourceKey<Biome> BLACK_LAKE = createKey("black_lake");
    public static final ResourceKey<Biome> AZKABAN = createKey("azkaban");
    public static final ResourceKey<Biome> MAGICAL_MEADOW = createKey("magical_meadow");
    public static final ResourceKey<Biome> DARK_FOREST_EDGE = createKey("dark_forest_edge");
    
    private static ResourceKey<Biome> createKey(String name) {
        return ResourceKey.create(Registries.BIOME, ModIdentifierHelper.modId(name));
    }
}



