package at.koopro.spells_n_squares.features.gear;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * Base class for rune items that can be socketed into gear.
 */
public class RuneItem extends Item {
    
    private final int tier;
    private final Identifier spellId;
    private final float augmentValue;
    
    public RuneItem(Properties properties, int tier, Identifier spellId, float augmentValue) {
        super(properties);
        this.tier = tier;
        this.spellId = spellId;
        this.augmentValue = augmentValue;
    }
    
    public int getTier() {
        return tier;
    }
    
    public Identifier getSpellId() {
        return spellId;
    }
    
    public float getAugmentValue() {
        return augmentValue;
    }
}
