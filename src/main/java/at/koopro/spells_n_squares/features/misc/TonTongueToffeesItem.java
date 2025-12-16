package at.koopro.spells_n_squares.features.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Ton-Tongue Toffees - A Weasleys' Wizard Wheezes product that makes the tongue grow.
 */
public class TonTongueToffeesItem extends Item {
    
    public TonTongueToffeesItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.1f)
            .alwaysEdible()
            .build()));
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            // Apply slowness effect (representing difficulty speaking)
            entity.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                200, // 10 seconds
                1,
                false,
                true,
                true
            ));
        }
        
        return super.finishUsingItem(stack, level, entity);
    }
}
