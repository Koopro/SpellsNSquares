package at.koopro.spells_n_squares.features.consumables;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * Centralized system for managing consumable item effects.
 * Provides consistent effect application for all consumable items.
 */
public final class ConsumableEffects {
    private ConsumableEffects() {
    }
    
    /**
     * Applies Chocolate Frog effects.
     * Provides regeneration and a chance to collect a wizard card.
     */
    public static void applyChocolateFrogEffects(LivingEntity entity) {
        // Regeneration effect (3 seconds)
        entity.addEffect(new MobEffectInstance(
            MobEffects.REGENERATION, 
            60, // 3 seconds
            0, 
            false, 
            true, 
            true
        ));
        
        // Small saturation boost
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            player.getFoodData().eat(1, 0.1f);
        }
    }
    
    /**
     * Applies Butterbeer effects.
     * Provides regeneration, speed boost, and warmth.
     */
    public static void applyButterbeerEffects(LivingEntity entity) {
        // Regeneration (5 seconds)
        entity.addEffect(new MobEffectInstance(
            MobEffects.REGENERATION, 
            100, // 5 seconds
            0, 
            false, 
            true, 
            true
        ));
        
        // Speed boost (10 seconds)
        ResourceKey<MobEffect> speedKey = ResourceKey.create(Registries.MOB_EFFECT, 
            net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "speed"));
        java.util.Optional<net.minecraft.core.Holder.Reference<MobEffect>> speedHolder = 
            BuiltInRegistries.MOB_EFFECT.get(speedKey);
        speedHolder.ifPresent(holder ->
            entity.addEffect(new MobEffectInstance(holder, 200, 0, false, true, true))
        );
        
        // Fire resistance (warmth) (5 seconds)
        entity.addEffect(new MobEffectInstance(
            MobEffects.FIRE_RESISTANCE, 
            100, // 5 seconds
            0, 
            false, 
            true, 
            true
        ));
        
        // Saturation
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            player.getFoodData().eat(2, 0.3f);
        }
    }
    
    /**
     * Applies Pumpkin Juice effects.
     * Provides health and minor regeneration.
     */
    public static void applyPumpkinJuiceEffects(LivingEntity entity) {
        // Minor regeneration (2 seconds)
        entity.addEffect(new MobEffectInstance(
            MobEffects.REGENERATION, 
            40, // 2 seconds
            0, 
            false, 
            true, 
            true
        ));
        
        // Saturation
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            player.getFoodData().eat(1, 0.2f);
        }
    }
    
    /**
     * Applies Fizzing Whizzbees effects.
     * Provides levitation for a short duration.
     */
    public static void applyFizzingWhizzbeesEffects(LivingEntity entity) {
        // Levitation (5 seconds)
        entity.addEffect(new MobEffectInstance(
            MobEffects.LEVITATION, 
            100, // 5 seconds
            0, 
            false, 
            true, 
            true
        ));
    }
    
    /**
     * Applies Every Flavour Beans random effects.
     * Can provide positive or negative effects randomly.
     */
    public static void applyEveryFlavourBeansEffects(LivingEntity entity, RandomSource random) {
        int effectType = random.nextInt(10);
        
        switch (effectType) {
            case 0, 1, 2 -> {
                // Positive: Speed boost
                ResourceKey<MobEffect> speedKey = ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "speed"));
                java.util.Optional<net.minecraft.core.Holder.Reference<MobEffect>> speedHolder = 
                    BuiltInRegistries.MOB_EFFECT.get(speedKey);
                speedHolder.ifPresent(holder ->
                    entity.addEffect(new MobEffectInstance(holder, 200, 0, false, true, true))
                );
            }
            case 3, 4 -> {
                // Positive: Regeneration
                entity.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, 
                    100, 
                    0, 
                    false, 
                    true, 
                    true
                ));
            }
            case 5 -> {
                // Positive: Strength
                ResourceKey<MobEffect> strengthKey = ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "strength"));
                java.util.Optional<net.minecraft.core.Holder.Reference<MobEffect>> strengthHolder = 
                    BuiltInRegistries.MOB_EFFECT.get(strengthKey);
                strengthHolder.ifPresent(holder ->
                    entity.addEffect(new MobEffectInstance(holder, 200, 0, false, true, true))
                );
            }
            case 6, 7 -> {
                // Negative: Nausea (bad flavor)
                entity.addEffect(new MobEffectInstance(
                    MobEffects.NAUSEA, 
                    200, 
                    0, 
                    false, 
                    true, 
                    true
                ));
            }
            case 8 -> {
                // Negative: Weakness
                entity.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS, 
                    200, 
                    0, 
                    false, 
                    true, 
                    true
                ));
            }
            case 9 -> {
                // Neutral: No effect (boring flavor)
                // Just provide food
                if (entity instanceof net.minecraft.world.entity.player.Player player) {
                    player.getFoodData().eat(1, 0.1f);
                }
            }
        }
    }
    
    /**
     * Applies Skiving Snackbox effects.
     * Provides various effects to help skip classes (speed, invisibility, etc.)
     */
    public static void applySkivingSnackboxEffects(LivingEntity entity, RandomSource random) {
        int snackType = random.nextInt(4);
        
        switch (snackType) {
            case 0 -> {
                // Fainting Fancies - causes temporary weakness
                entity.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS, 
                    400, 
                    0, 
                    false, 
                    true, 
                    true
                ));
            }
            case 1 -> {
                // Puking Pastilles - causes nausea
                entity.addEffect(new MobEffectInstance(
                    MobEffects.NAUSEA, 
                    300, 
                    0, 
                    false, 
                    true, 
                    true
                ));
            }
            case 2 -> {
                // Nosebleed Nougat - causes damage but looks like illness
                if (entity.level() != null && !entity.level().isClientSide()) {
                    entity.hurt(entity.damageSources().magic(), 1.0f);
                }
            }
            case 3 -> {
                // Fever Fudge - causes fire resistance (fever-like warmth)
                entity.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE, 
                    600, 
                    0, 
                    false, 
                    true, 
                    true
                ));
            }
        }
    }
}

