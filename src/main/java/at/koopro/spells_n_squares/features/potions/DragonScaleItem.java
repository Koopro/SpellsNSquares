package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Dragon Scale - powerful ingredient from dragons.
 */
public class DragonScaleItem extends PotionIngredientItem {
    
    public DragonScaleItem(Properties properties) {
        super(properties, IngredientRarity.VERY_RARE, 1.4f);
    }
}
