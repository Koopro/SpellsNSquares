package at.koopro.spells_n_squares.datagen;

import at.koopro.spells_n_squares.SpellsNSquares;
// import at.koopro.spells_n_squares.features.environment.block.TreeBlockSet; // TODO: Re-enable when TreeBlockSet is implemented
// import at.koopro.spells_n_squares.core.registry.ModTreeBlocks; // TODO: Re-enable when ModTreeBlocks is implemented
import at.koopro.spells_n_squares.datagen.features.FeatureRecipeGenerator;
import at.koopro.spells_n_squares.datagen.features.PotionsRecipeGenerator;
// import at.koopro.spells_n_squares.datagen.features.QuidditchRecipeGenerator; // TODO: Re-enable when QuidditchRecipeGenerator is implemented
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates recipe files for tree blocks and delegates to feature-specific recipe generators.
 * NeoForge 21.11 requires a Runner class for RecipeProvider.
 */
public class ModRecipeProvider extends RecipeProvider {
    
    private final HolderLookup.Provider provider;
    private final RecipeOutput output;
    
    protected ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
        this.provider = provider;
        this.output = output;
    }
    
    @Override
    protected void buildRecipes() {
        // Generate recipes for all tree blocks (generic, not feature-specific)
        // TODO: Re-enable when TreeBlockSet and ModTreeBlocks are implemented
        // for (TreeBlockSet set : ModTreeBlocks.getAllTreeSets()) {
        //     generateTreeRecipes(set);
        // }
        
        // Delegate to feature-specific recipe generators
        List<FeatureRecipeGenerator> generators = List.of(
            new PotionsRecipeGenerator(),
            new at.koopro.spells_n_squares.datagen.features.ArtifactRecipeGenerator()
            // Future feature recipe generators (uncomment when features are implemented):
            // new QuidditchRecipeGenerator()
        );
        
        for (FeatureRecipeGenerator generator : generators) {
            generator.generate(output, provider);
        }
    }
    
    // TODO: Re-enable when TreeBlockSet is implemented
    /*
    private void generateTreeRecipes(TreeBlockSet set) {
        String woodId = set.getWoodId();
        Block log = set.log().get();
        Block wood = set.wood().get();
        Block strippedLog = set.strippedLog().get();
        Block strippedWood = set.strippedWood().get();
        Block planks = set.planks().get();
        Block stairs = set.stairs().get();
        Block slab = set.slab().get();
        Block fence = set.fence().get();
        Block fenceGate = set.fenceGate().get();
        Block door = set.door().get();
        Block trapdoor = set.trapdoor().get();
        Block pressurePlate = set.pressurePlate().get();
        Block button = set.button().get();
        
        // Log -> 4 Planks
        shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
            .requires(log)
            .group("planks")
            .unlockedBy("has_log", has(log))
            .save(output, recipeKey(woodId + "_planks_from_log"));
        
        // Wood -> 4 Planks
        shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
            .requires(wood)
            .group("planks")
            .unlockedBy("has_wood", has(wood))
            .save(output, recipeKey(woodId + "_planks_from_wood"));
        
        // Stripped Log -> 4 Planks
        shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
            .requires(strippedLog)
            .group("planks")
            .unlockedBy("has_stripped_log", has(strippedLog))
            .save(output, recipeKey(woodId + "_planks_from_stripped_log"));
        
        // Stripped Wood -> 4 Planks
        shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
            .requires(strippedWood)
            .group("planks")
            .unlockedBy("has_stripped_wood", has(strippedWood))
            .save(output, recipeKey(woodId + "_planks_from_stripped_wood"));
        
        // 4 Logs -> 3 Wood
        shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
            .pattern("##")
            .pattern("##")
            .define('#', log)
            .group("bark")
            .unlockedBy("has_log", has(log))
            .save(output, recipeKey(woodId + "_wood"));
        
        // 4 Stripped Logs -> 3 Stripped Wood
        shaped(RecipeCategory.BUILDING_BLOCKS, strippedWood, 3)
            .pattern("##")
            .pattern("##")
            .define('#', strippedLog)
            .group("bark")
            .unlockedBy("has_stripped_log", has(strippedLog))
            .save(output, recipeKey("stripped_" + woodId + "_wood"));
        
        // Stairs
        stairBuilder(stairs, Ingredient.of(planks))
            .group("wooden_stairs")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_stairs"));
        
        // Slab
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(planks))
            .group("wooden_slab")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_slab"));
        
        // Fence
        fenceBuilder(fence, Ingredient.of(planks))
            .group("wooden_fence")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_fence"));
        
        // Fence Gate
        fenceGateBuilder(fenceGate, Ingredient.of(planks))
            .group("wooden_fence_gate")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_fence_gate"));
        
        // Door
        doorBuilder(door, Ingredient.of(planks))
            .group("wooden_door")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_door"));
        
        // Trapdoor
        trapdoorBuilder(trapdoor, Ingredient.of(planks))
            .group("wooden_trapdoor")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_trapdoor"));
        
        // Pressure Plate
        pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(planks))
            .group("wooden_pressure_plate")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_pressure_plate"));
        
        // Button
        buttonBuilder(button, Ingredient.of(planks))
            .group("wooden_button")
            .unlockedBy("has_planks", has(planks))
            .save(output, recipeKey(woodId + "_button"));
    }
    */
    
    private ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, path));
    }
    
    /**
     * Runner class required by NeoForge 21.11 for RecipeProvider.
     */
    public static class Runner extends RecipeProvider.Runner {
        
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }
        
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }
        
        @Override
        public String getName() {
            return "Spells n Squares Tree Block Recipes";
        }
    }
}





















