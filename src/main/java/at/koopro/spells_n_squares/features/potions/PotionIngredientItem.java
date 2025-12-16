package at.koopro.spells_n_squares.features.potions;

import net.minecraft.world.item.Item;

/**
 * Base class for potion ingredient items.
 */
public class PotionIngredientItem extends Item {
    
    public enum IngredientRarity {
        COMMON,
        UNCOMMON,
        RARE,
        VERY_RARE,
        LEGENDARY
    }
    
    private final IngredientRarity rarity;
    private final float qualityModifier; // Affects potion quality (0.5 to 1.5)
    
    public PotionIngredientItem(Properties properties, IngredientRarity rarity, float qualityModifier) {
        super(properties);
        this.rarity = rarity;
        this.qualityModifier = qualityModifier;
    }
    
    public PotionIngredientItem(Properties properties, IngredientRarity rarity) {
        this(properties, rarity, 1.0f);
    }
    
    public IngredientRarity getRarity() {
        return rarity;
    }
    
    public float getQualityModifier() {
        return qualityModifier;
    }
}
