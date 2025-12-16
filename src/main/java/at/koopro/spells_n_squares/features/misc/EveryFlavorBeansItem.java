package at.koopro.spells_n_squares.features.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Every Flavor Beans - candy with random flavors (some good, some bad).
 */
public class EveryFlavorBeansItem extends Item {
    
    private static final Random RANDOM = new Random();
    
    public EveryFlavorBeansItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Random chance for good or bad effects
        int roll = RANDOM.nextInt(10);
        
        if (roll < 6) {
            // Good flavor - positive effect
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
        } else if (roll < 8) {
            // Neutral flavor - no effect
        } else {
            // Bad flavor - negative effect
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 0));
        }
        
        return result;
    }
}




