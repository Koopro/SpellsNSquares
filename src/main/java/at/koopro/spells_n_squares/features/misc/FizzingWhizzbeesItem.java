package at.koopro.spells_n_squares.features.misc;

import at.koopro.spells_n_squares.features.consumables.ConsumableEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Fizzing Whizzbees - candy that makes you levitate.
 */
public class FizzingWhizzbeesItem extends Item {
    
    public FizzingWhizzbeesItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply Fizzing Whizzbees effects
        ConsumableEffects.applyFizzingWhizzbeesEffects(entity);
        
        return result;
    }
}

















