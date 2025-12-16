package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Handler for shader-based post-processing effects.
 * Provides fallback to particle-based effects if shaders are unavailable.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ShaderEffectHandler {
    
    private static boolean shadersSupported = false;
    
    /**
     * Initializes shader support check.
     */
    public static void initialize() {
        // Check if shaders are supported
        // For now, simplified - can be enhanced with actual shader loading
        shadersSupported = Config.areShaderEffectsEnabled();
    }
    
    /**
     * Checks if shader effects are available.
     */
    public static boolean areShadersAvailable() {
        return shadersSupported && Config.areShaderEffectsEnabled();
    }
    
    /**
     * Triggers a spell cast shader effect.
     * Falls back to particle effects if shaders unavailable.
     */
    public static void triggerSpellCastEffect() {
        if (areShadersAvailable()) {
            // Shader-based effect would go here
            // For now, fallback to screen flash
            ScreenEffectManager.triggerSpellFlash();
        } else {
            // Fallback to screen flash
            ScreenEffectManager.triggerSpellFlash();
        }
    }
    
    /**
     * Triggers an invisibility distortion effect.
     * Falls back to particle shimmer if shaders unavailable.
     */
    public static void triggerInvisibilityDistortion() {
        if (areShadersAvailable()) {
            // Shader-based distortion would go here
            // For now, handled by CloakShimmerHandler particles
        }
    }
    
    /**
     * Triggers a time distortion effect (Time-Turner).
     * Falls back to particle effects if shaders unavailable.
     */
    public static void triggerTimeDistortion() {
        if (areShadersAvailable()) {
            // Shader-based time distortion would go here
            // For now, fallback to screen effect
            ScreenEffectManager.triggerOverlay(0xFFFF00, 0.1f, 10, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        } else {
            ScreenEffectManager.triggerOverlay(0xFFFF00, 0.1f, 10, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    /**
     * Triggers a magical aura shader effect.
     * Falls back to particle aura if shaders unavailable.
     */
    public static void triggerMagicalAura() {
        if (areShadersAvailable()) {
            // Shader-based aura would go here
            // For now, handled by EnvironmentalEffectHandler particles
        }
    }
}
