package at.koopro.spells_n_squares.features.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Chocolate Frog item - a popular wizarding treat.
 */
public class ChocolateFrogItem extends Item {
    
    public ChocolateFrogItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply happiness effect (resistance to negative effects)
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
        
        return result;
    }
}
