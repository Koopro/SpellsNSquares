package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Wolfsbane - specific ingredient for Wolfsbane Potion.
 */
public class WolfsbaneItem extends PotionIngredientItem {
    
    public WolfsbaneItem(Properties properties) {
        super(properties, IngredientRarity.UNCOMMON, 1.0f);
    }
}
