package at.koopro.spells_n_squares.features.enchantments.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Helper class for enchantment recipes and acquisition methods.
 * Provides information on how to obtain enchantments.
 */
public final class EnchantmentRecipeHelper {
    
    private static final Map<Identifier, EnchantmentRecipe> RECIPES = new HashMap<>();
    
    private EnchantmentRecipeHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents an enchantment recipe.
     */
    public record EnchantmentRecipe(
        Identifier enchantmentId,
        String method,  // "enchanting_table", "book", "anvil", etc.
        List<ItemStack> requiredItems,
        int levelRequirement,
        Component description
    ) {}
    
    /**
     * Gets the recipe for obtaining an enchantment.
     * 
     * @param enchantmentId The enchantment identifier
     * @return Recipe information, or null if not found
     */
    public static EnchantmentRecipe getRecipe(Identifier enchantmentId) {
        if (enchantmentId == null) {
            return null;
        }
        
        return RECIPES.get(enchantmentId);
    }
    
    /**
     * Registers a recipe for an enchantment.
     * 
     * @param recipe The recipe to register
     */
    public static void registerRecipe(EnchantmentRecipe recipe) {
        if (recipe != null && recipe.enchantmentId() != null) {
            RECIPES.put(recipe.enchantmentId(), recipe);
        }
    }
    
    /**
     * Gets all available recipes.
     * 
     * @return Map of enchantment ID to recipe
     */
    public static Map<Identifier, EnchantmentRecipe> getAllRecipes() {
        return new HashMap<>(RECIPES);
    }
    
    /**
     * Gets recipes for a specific method.
     * 
     * @param method The acquisition method
     * @return List of recipes using that method
     */
    public static List<EnchantmentRecipe> getRecipesByMethod(String method) {
        if (method == null) {
            return Collections.emptyList();
        }
        
        return RECIPES.values().stream()
            .filter(recipe -> method.equals(recipe.method()))
            .toList();
    }
}

