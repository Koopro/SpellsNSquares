package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * Configuration for pocket dimension type.
 * Defines properties like fixed time, no weather, etc.
 */
public final class PocketDimensionType {
    private PocketDimensionType() {
    }
    
    /**
     * Creates a resource key for the pocket dimension type.
     */
    public static ResourceKey<DimensionType> createDimensionTypeKey() {
        return ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ModIdentifierHelper.modId("pocket_dimension_type")
        );
    }
    
    /**
     * Creates a resource key for the pocket dimension level stem.
     */
    public static ResourceKey<LevelStem> createLevelStemKey() {
        return ResourceKey.create(
            Registries.LEVEL_STEM,
            ModIdentifierHelper.modId("pocket_dimension")
        );
    }
}
