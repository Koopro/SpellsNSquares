package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Bezoar - antidote ingredient that can cure poison.
 */
public class BezoarItem extends PotionIngredientItem {
    
    public BezoarItem(Properties properties) {
        super(properties, IngredientRarity.RARE, 1.2f);
    }
}
