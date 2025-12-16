package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Shrivelfig - uncommon potion ingredient.
 */
public class ShrivelfigItem extends PotionIngredientItem {
    
    public ShrivelfigItem(Properties properties) {
        super(properties, IngredientRarity.UNCOMMON, 1.05f);
    }
}

