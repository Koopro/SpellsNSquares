package at.koopro.spells_n_squares.features.misc.item;

import at.koopro.spells_n_squares.features.consumables.ConsumableEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Pumpkin Juice - a popular wizarding drink.
 * Provides minor regeneration and hydration.
 */
public class PumpkinJuiceItem extends Item {
    
    public PumpkinJuiceItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply Pumpkin Juice effects
        ConsumableEffects.applyPumpkinJuiceEffects(entity);
        
        return result;
    }
}




