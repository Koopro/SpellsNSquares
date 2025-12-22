package at.koopro.spells_n_squares.datagen.features;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

/**
 * Interface for feature-specific recipe generators.
 * Each feature that has recipes should implement this interface.
 */
public interface FeatureRecipeGenerator {
    /**
     * Generates recipes for this feature.
     * @param output The recipe output to write recipes to
     * @param provider The holder lookup provider for accessing registries
     */
    void generate(RecipeOutput output, HolderLookup.Provider provider);
}



