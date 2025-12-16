package at.koopro.spells_n_squares.features.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Butterbeer item - a popular wizarding drink.
 */
public class ButterbeerItem extends Item {
    
    public ButterbeerItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply positive effects
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 0));
        
        return result;
    }
}
