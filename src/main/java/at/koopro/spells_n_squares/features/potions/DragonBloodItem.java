package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Dragon Blood - rare potion ingredient (different from Dragon Scale).
 */
public class DragonBloodItem extends PotionIngredientItem {
    
    public DragonBloodItem(Properties properties) {
        super(properties, IngredientRarity.RARE, 1.2f);
    }
}

