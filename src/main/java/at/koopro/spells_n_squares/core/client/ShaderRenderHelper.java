package at.koopro.spells_n_squares.core.client;

import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Small helper for working with shader-backed render types.
 *
 * This class wraps the existing ShaderEffectHandler + RenderTypes APIs
 * so that entity/geometry renderers can:
 * - Prefer a shader-based render type when a shader is available
 * - Cleanly fall back to the existing texture-based render type
 *
 * It deliberately avoids depending on the concrete RenderType class from the
 * Minecraft mappings by using generic return types. Callers let the compiler
 * infer the correct type based on method usage.
 */
public final class ShaderRenderHelper {
    /**
     * Lightweight hook for time-based uniforms. Delegates to ShaderEffectHandler
     * so renderers can keep uniform logic in one place.
     */
    public static void updateTimeUniform(Identifier shaderId) {
        ShaderEffectHandler.updateShaderTime(shaderId, ShaderEffectHandler.getShaderTime());
    }

    private ShaderRenderHelper() {
        // Utility class
    }

    /**
     * Gets the render type for the lumos orb effect.
     *
     * - If the lumos orb shader is available, returns an emissive shader-based render type.
     * - Otherwise, falls back to the existing glow texture render type.
     * 
     * Note: Unchecked cast is necessary due to generic type erasure when returning different RenderType implementations.
     * This is safe as callers know the expected type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getLumosOrbRenderType() {
        Identifier shaderId = ShaderEffectHandler.getLumosOrbShaderId();
        if (shaderId != null) {
            // Use emissive shader-backed render type when available
            return (T) RenderTypes.entityTranslucentEmissive(shaderId);
        }

        // Fallback: original texture-based glow
        return (T) RenderTypes.entityTranslucent(RendererConstants.GLOW_TEXTURE);
    }

    /**
     * Generic helper for "shader or texture" translucent emissive rendering.
     *
     * @param preferredShaderId Shader identifier to prefer when available
     * @param fallbackTexture   Texture identifier to use when shader is unavailable
     * @return A render type that uses the shader if possible, otherwise the texture
     * 
     * Note: Unchecked cast is necessary due to generic type erasure when returning different RenderType implementations.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getTranslucentEmissiveOrTexture(Identifier preferredShaderId, Identifier fallbackTexture) {
        Identifier shaderId = ShaderEffectHandler.getShaderId(preferredShaderId);
        if (shaderId != null) {
            return (T) RenderTypes.entityTranslucentEmissive(shaderId);
        }

        return (T) RenderTypes.entityTranslucent(fallbackTexture);
    }
    
    /**
     * Gets the render type for the energy ball effect with bloom.
     *
     * Uses CustomRenderTypes to create a RenderType that uses the energy ball shader.
     * The shader provides bloom effects with animated pulse and color gradients.
     *
     * - If the energy ball shader is available, returns a custom shader-based render type.
     * - Otherwise, falls back to the lumos orb render type or texture-based glow.
     * 
     * Note: Unchecked cast is necessary due to generic type erasure when returning different RenderType implementations.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getEnergyBallRenderType() {
        // Use the custom RenderType that uses the shader program
        return (T) CustomRenderTypes.getEnergyBallRenderType();
    }
}






