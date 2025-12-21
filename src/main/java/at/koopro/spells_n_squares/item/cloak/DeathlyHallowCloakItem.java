package at.koopro.spells_n_squares.item.cloak;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The Deathly Hallow invisibility cloak.
 * Provides permanent invisibility when worn and is unbreakable.
 */
public class DeathlyHallowCloakItem extends Item {
    
    public DeathlyHallowCloakItem(Item.Properties properties) {
        super(properties);
    }
    
    @Override
    public boolean isDamageable(ItemStack stack) {
        return false; // Unbreakable
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Make it glow to show it's special
    }
}

