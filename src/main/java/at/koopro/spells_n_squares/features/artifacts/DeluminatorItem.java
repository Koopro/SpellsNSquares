package at.koopro.spells_n_squares.features.artifacts;

import net.minecraft.world.item.Item;

/**
 * Deluminator artifact that can capture and release light.
 * TODO: Implement light capture/release mechanics.
 */
public class DeluminatorItem extends Item {
    
    public DeluminatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }
}
