package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Dittany - common healing ingredient.
 */
public class DittanyItem extends PotionIngredientItem {
    
    public DittanyItem(Properties properties) {
        super(properties, IngredientRarity.COMMON, 1.0f);
    }
}
