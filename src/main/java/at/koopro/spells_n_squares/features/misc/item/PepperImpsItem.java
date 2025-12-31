package at.koopro.spells_n_squares.features.misc.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Pepper Imps - candy that makes you breathe fire (fire resistance).
 */
public class PepperImpsItem extends Item {
    
    public PepperImpsItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Fire resistance effect
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0));
        
        return result;
    }
}










