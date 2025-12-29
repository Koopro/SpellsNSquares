package at.koopro.spells_n_squares.features.misc;

import at.koopro.spells_n_squares.features.consumables.ConsumableEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Chocolate Frog item - a popular wizarding treat.
 * Provides regeneration and a chance to collect wizard cards.
 */
public class ChocolateFrogItem extends Item {
    
    public ChocolateFrogItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply Chocolate Frog effects
        ConsumableEffects.applyChocolateFrogEffects(entity);
        
        return result;
    }
}
