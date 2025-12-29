package at.koopro.spells_n_squares.datagen.features;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

/**
 * Recipe generator for quidditch feature.
 * Currently empty as quidditch items are not yet registered.
 * Ready for future recipe definitions when QuidditchBallItem and other quidditch items are added.
 */
public class QuidditchRecipeGenerator implements FeatureRecipeGenerator {
    
    @Override
    public void generate(RecipeOutput output, HolderLookup.Provider provider) {
        // Quidditch items not yet registered
        // Add recipes here when QuidditchBallItem and other quidditch items are implemented
        // Example:
        // shapeless(RecipeCategory.MISC, QuidditchRegistry.QUAFFLE.get(), 1)
        //     .requires(...)
        //     .unlockedBy("has_...", has(...))
        //     .save(output, ...);
    }
}












