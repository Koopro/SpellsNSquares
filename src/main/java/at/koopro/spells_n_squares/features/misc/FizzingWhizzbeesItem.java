package at.koopro.spells_n_squares.features.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
        
        // Levitation effect (makes you float)
        entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));
        
        return result;
    }
}







