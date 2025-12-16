package at.koopro.spells_n_squares.features.potions;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Represents a potion brewing recipe.
 */
public record PotionRecipe(
    Identifier id,
    String potionType,
    List<IngredientRequirement> ingredients,
    int brewingTime, // Ticks required to brew
    PotionData.PotionComponent result
) {
    /**
     * Requirement for a specific ingredient in a recipe.
     */
    public record IngredientRequirement(
        Item ingredient,
        int count,
        boolean optional
    ) {
        public boolean matches(ItemStack stack) {
            return stack.is(ingredient) && stack.getCount() >= count;
        }
    }
    
    /**
     * Checks if the provided ingredients match this recipe.
     */
    public boolean matchesIngredients(List<ItemStack> providedIngredients) {
        // Count provided ingredients
        Map<Item, Integer> providedCounts = new java.util.HashMap<>();
        for (ItemStack stack : providedIngredients) {
            if (!stack.isEmpty()) {
                providedCounts.put(stack.getItem(), providedCounts.getOrDefault(stack.getItem(), 0) + stack.getCount());
            }
        }
        
        // Check required ingredients
        for (IngredientRequirement requirement : ingredients) {
            if (requirement.optional()) {
                continue; // Optional ingredients don't need to match
            }
            
            int provided = providedCounts.getOrDefault(requirement.ingredient(), 0);
            if (provided < requirement.count()) {
                return false; // Missing required ingredient
            }
        }
        
        return true;
    }
    
    /**
     * Gets the minimum quality based on ingredient quality.
     */
    public int calculateQuality(List<ItemStack> ingredients) {
        float totalQuality = 100.0f;
        int ingredientCount = 0;
        
        for (ItemStack stack : ingredients) {
            if (stack.isEmpty()) continue;
            
            if (stack.getItem() instanceof PotionIngredientItem ingredient) {
                totalQuality *= ingredient.getQualityModifier();
                ingredientCount++;
            }
        }
        
        // Average quality if multiple ingredients
        if (ingredientCount > 0) {
            totalQuality = 100.0f + (totalQuality - 100.0f) / ingredientCount;
        }
        
        return Math.max(50, Math.min(150, (int) totalQuality)); // Clamp between 50-150
    }
}
