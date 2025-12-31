package at.koopro.spells_n_squares.features.potions.item;

/**
 * Unicorn Hair - rare ingredient for powerful potions.
 */
public class UnicornHairItem extends PotionIngredientItem {
    
    public UnicornHairItem(Properties properties) {
        super(properties, IngredientRarity.RARE, 1.3f);
    }
}
