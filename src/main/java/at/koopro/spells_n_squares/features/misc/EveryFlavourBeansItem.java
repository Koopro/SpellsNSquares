package at.koopro.spells_n_squares.features.misc;

import at.koopro.spells_n_squares.features.consumables.ConsumableEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Every Flavour Beans - candy with random flavors and effects.
 * Can provide positive or negative effects depending on the flavor.
 */
public class EveryFlavourBeansItem extends Item {
    
    public EveryFlavourBeansItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply random effects based on flavor
        ConsumableEffects.applyEveryFlavourBeansEffects(entity, level.getRandom());
        
        return result;
    }
}

