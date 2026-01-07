package at.koopro.spells_n_squares.features.fx.handler;

import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import net.minecraft.resources.Identifier;

/**
 * Helper class for shader effect triggering with fallback support.
 * Reduces code duplication in ShaderEffectHandler.
 */
public final class ShaderEffectHelper {
    private ShaderEffectHelper() {
    }
    
    /**
     * Triggers a post-processing shader effect with fallback to overlay.
     * 
     * @param shaderId The post-processing shader identifier
     * @param intensity The effect intensity (0.0 to 1.0)
     * @param duration The effect duration in ticks
     * @param fallbackColor The fallback overlay color (ARGB)
     * @param fallbackOpacity The fallback overlay opacity
     * @param fallbackDuration The fallback overlay duration in ticks
     * @param fallbackType The fallback overlay type
     */
    public static void triggerPostProcessingShader(
            Identifier shaderId,
            float intensity,
            int duration,
            int fallbackColor,
            float fallbackOpacity,
            int fallbackDuration,
            ScreenEffectManager.ScreenOverlay.OverlayType fallbackType) {
        
        if (ShaderEffectHandler.areShadersAvailable() && 
            PostProcessingManager.isPostProcessingShaderAvailable(shaderId)) {
            PostProcessingManager.addEffect(shaderId, intensity, duration);
            return;
        }
        
        // Fallback to overlay
        float opacity = Math.min(fallbackOpacity, intensity * (fallbackOpacity * 0.8f));
        ScreenEffectManager.triggerOverlay(fallbackColor, opacity, fallbackDuration, fallbackType);
    }
    
    /**
     * Triggers a post-processing shader effect with fallback to overlay and optional shake.
     * 
     * @param shaderId The post-processing shader identifier
     * @param intensity The effect intensity (0.0 to 1.0)
     * @param duration The effect duration in ticks
     * @param fallbackColor The fallback overlay color (ARGB)
     * @param fallbackOpacity The fallback overlay opacity
     * @param fallbackDuration The fallback overlay duration in ticks
     * @param fallbackType The fallback overlay type
     * @param shakeIntensity Optional shake intensity (0.0 to disable)
     * @param shakeDuration Optional shake duration in ticks
     */
    public static void triggerPostProcessingShaderWithShake(
            Identifier shaderId,
            float intensity,
            int duration,
            int fallbackColor,
            float fallbackOpacity,
            int fallbackDuration,
            ScreenEffectManager.ScreenOverlay.OverlayType fallbackType,
            float shakeIntensity,
            int shakeDuration) {
        
        triggerPostProcessingShader(shaderId, intensity, duration, 
            fallbackColor, fallbackOpacity, fallbackDuration, fallbackType);
        
        if (shakeIntensity > 0.0f) {
            ScreenEffectManager.triggerShake(shakeIntensity * intensity, shakeDuration);
        }
    }
}


