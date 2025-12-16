package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Mandrake Root - ingredient from mandrake plants.
 */
public class MandrakeRootItem extends PotionIngredientItem {
    
    public MandrakeRootItem(Properties properties) {
        super(properties, IngredientRarity.UNCOMMON, 1.1f);
    }
}
