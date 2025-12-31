package at.koopro.spells_n_squares.features.misc.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Gillyweed - consumable item that grants water breathing.
 */
public class GillyweedItem extends Item {
    
    public GillyweedItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Apply water breathing effect
        entity.addEffect(new MobEffectInstance(
            MobEffects.WATER_BREATHING,
            600, // 30 seconds
            0,
            false,
            true,
            true
        ));
        
        return result;
    }
}
