package at.koopro.spells_n_squares.features.misc.item;

import at.koopro.spells_n_squares.features.consumables.ConsumableEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Butterbeer item - a popular wizarding drink.
 * Provides regeneration, speed boost, and warmth (fire resistance).
 */
public class ButterbeerItem extends Item {
    
    public ButterbeerItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply Butterbeer effects
        ConsumableEffects.applyButterbeerEffects(entity);
        
        return result;
    }
}
