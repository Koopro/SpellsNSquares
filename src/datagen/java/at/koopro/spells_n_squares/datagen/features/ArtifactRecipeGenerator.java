package at.koopro.spells_n_squares.datagen.features;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.artifact.ArtifactRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Generates recipes for artifact items.
 */
public class ArtifactRecipeGenerator implements FeatureRecipeGenerator {
    
    @Override
    public void generate(RecipeOutput output, HolderLookup.Provider provider) {
        var itemLookup = provider.lookupOrThrow(Registries.ITEM);
        
        // Stage 1 - Nigredo: Prima Materia
        // Recipe: Heart of the Sea (center) + 4x Coal Blocks + 4x Wither Roses
        ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.MISC, ArtifactRegistry.PRIMA_MATERIA.get())
            .pattern("WCW")
            .pattern("CHC")
            .pattern("WCW")
            .define('W', Items.WITHER_ROSE)
            .define('C', Items.COAL_BLOCK)
            .define('H', Items.HEART_OF_THE_SEA)
            .unlockedBy("has_heart_of_the_sea", 
                net.minecraft.advancements.criterion.InventoryChangeTrigger.TriggerInstance.hasItems(Items.HEART_OF_THE_SEA))
            .save(output, recipeKey("prima_materia"));
        
        // Stage 2 - Albedo: White Stone
        // Recipe: Prima Materia in Blast Furnace (any fuel works, but Lava Bucket is recommended)
        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(ArtifactRegistry.PRIMA_MATERIA.get()),
                RecipeCategory.MISC,
                ArtifactRegistry.WHITE_STONE.get(),
                0.5f, // Experience
                100)  // Cooking time (5 seconds at 20 TPS)
            .unlockedBy("has_prima_materia",
                net.minecraft.advancements.criterion.InventoryChangeTrigger.TriggerInstance.hasItems(ArtifactRegistry.PRIMA_MATERIA.get()))
            .save(output, recipeKey("white_stone_from_blasting"));
    }
    
    private ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, path));
    }
}

