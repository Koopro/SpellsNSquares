package at.koopro.spells_n_squares.features.artifact;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * MobEffect that grants immortality.
 * Prevents death and provides god mode while active.
 * When it expires, the player enters a withered state.
 * 
 * Note: The actual effect logic is handled in ImmortalityEvents.onPlayerTick()
 * and the effect expiration is handled via MobEffectEvent.Remove
 * 
 * Icon texture should be at: assets/spells_n_squares/textures/mob_effect/immortality.png
 */
public class ImmortalityEffect extends MobEffect {
    
    public ImmortalityEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700); // Gold color
    }
}

