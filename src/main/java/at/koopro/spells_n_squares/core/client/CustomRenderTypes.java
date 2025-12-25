package at.koopro.spells_n_squares.core.client;

import at.koopro.spells_n_squares.features.fx.ShaderEffectHandler;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Custom RenderTypes that use shader programs for special effects like bloom.
 * 
 * Similar to how Minecraft's end portal block works, we can pass a shader identifier
 * directly to RenderTypes.entityTranslucentEmissive(). The shader JSON file must be
 * located at assets/<modid>/shaders/<path>.json and referenced as <modid>:<path>.
 */
public final class CustomRenderTypes {
    private CustomRenderTypes() {
        // Utility class
    }
    
    private static RenderType energyBallRenderType;
    private static boolean energyBallRenderTypeInitialized = false;
    
    /**
     * Gets the RenderType for the energy ball with bloom shader.
     * 
     * Works like the end portal block - passes the shader identifier directly to
     * RenderTypes.entityTranslucentEmissive(). The shader JSON file is at:
     * assets/spells_n_squares/shaders/core/energy_ball.json
     * 
     * The shader identifier should be: spells_n_squares:core/energy_ball
     * (shader paths are relative to the shaders/ directory)
     * 
     * @return The energy ball RenderType with shader, or texture fallback if unavailable
     */
    public static RenderType getEnergyBallRenderType() {
        if (!energyBallRenderTypeInitialized) {
            energyBallRenderTypeInitialized = true;
            
            // Check if shader is available
            if (ShaderEffectHandler.isShaderLoaded(ShaderEffectHandler.ENERGY_BALL_SHADER)) {
                // Get the shader identifier
                // The shader identifier is spells_n_squares:core/energy_ball
                // which refers to assets/spells_n_squares/shaders/core/energy_ball.json
                Identifier shaderId = ShaderEffectHandler.ENERGY_BALL_SHADER;
                
                try {
                    // Like the end portal, we can pass the shader identifier directly
                    // to RenderTypes.entityTranslucentEmissive()
                    // Minecraft will load the shader from the shaders/ directory
                    energyBallRenderType = RenderTypes.entityTranslucentEmissive(shaderId);
                } catch (Exception e) {
                    // If shader creation fails, use fallback
                    energyBallRenderType = RenderTypes.entityTranslucentEmissive(RendererConstants.GLOW_TEXTURE);
                }
            } else {
                // Shader not available, use texture fallback
                energyBallRenderType = RenderTypes.entityTranslucentEmissive(RendererConstants.GLOW_TEXTURE);
            }
        }
        
        return energyBallRenderType;
    }
}

