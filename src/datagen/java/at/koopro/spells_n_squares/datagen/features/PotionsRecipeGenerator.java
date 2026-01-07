package at.koopro.spells_n_squares.datagen.features;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

/**
 * Recipe generator for potions feature.
 * Currently empty as potions use a custom brewing system rather than standard recipes.
 * Ready for future recipe definitions if needed.
 */
public class PotionsRecipeGenerator implements FeatureRecipeGenerator {
    
    @Override
    public void generate(RecipeOutput output, HolderLookup.Provider provider) {
        // Potions use custom brewing system (PotionBrewingManager), not standard recipes
        // Add standard crafting recipes here if needed in the future
    }
}




















