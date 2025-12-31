package at.koopro.spells_n_squares.features.potions.item;

/**
 * Phoenix Feather - very rare ingredient for legendary potions.
 */
public class PhoenixFeatherItem extends PotionIngredientItem {
    
    public PhoenixFeatherItem(Properties properties) {
        super(properties, IngredientRarity.LEGENDARY, 1.5f);
    }
}
