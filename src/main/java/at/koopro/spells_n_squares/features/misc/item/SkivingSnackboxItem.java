package at.koopro.spells_n_squares.features.misc.item;

import at.koopro.spells_n_squares.features.consumables.ConsumableEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Skiving Snackbox - a box of candies that cause various "illness" effects.
 * Used by students to skip classes by appearing sick.
 */
public class SkivingSnackboxItem extends Item {
    
    public SkivingSnackboxItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply random "illness" effects
        ConsumableEffects.applySkivingSnackboxEffects(entity, level.getRandom());
        
        return result;
    }
}

