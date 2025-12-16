package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Unicorn Hair - rare ingredient for powerful potions.
 */
public class UnicornHairItem extends PotionIngredientItem {
    
    public UnicornHairItem(Properties properties) {
        super(properties, IngredientRarity.RARE, 1.3f);
    }
}
