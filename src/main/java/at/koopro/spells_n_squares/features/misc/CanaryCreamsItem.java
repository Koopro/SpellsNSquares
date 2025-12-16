package at.koopro.spells_n_squares.features.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Canary Creams - A Weasleys' Wizard Wheezes product that temporarily transforms the eater into a canary.
 */
public class CanaryCreamsItem extends Item {
    
    public CanaryCreamsItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.2f)
            .alwaysEdible()
            .build()));
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            // Apply levitation effect (representing transformation)
            entity.addEffect(new MobEffectInstance(
                MobEffects.LEVITATION,
                100, // 5 seconds
                0,
                false,
                true,
                true
            ));
            
            // Apply slowness (smaller form)
            entity.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                100,
                2,
                false,
                true,
                true
            ));
        }
        
        return super.finishUsingItem(stack, level, entity);
    }
}
