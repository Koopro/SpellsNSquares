package at.koopro.spells_n_squares.features.fx.shader.strategies;

import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import net.minecraft.resources.Identifier;

/**
 * Base class for shader effects that use post-processing shaders with fallback to screen overlays.
 */
public abstract class BaseShaderEffectStrategy implements ShaderEffectStrategy {
    protected final Identifier postShaderId;
    protected final int defaultDuration;
    
    protected BaseShaderEffectStrategy(Identifier postShaderId, int defaultDuration) {
        this.postShaderId = postShaderId;
        this.defaultDuration = defaultDuration;
    }
    
    @Override
    public void trigger(float intensity) {
        if (areShadersAvailable()) {
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, defaultDuration);
                return;
            }
        }
        
        // Fallback to screen overlay
        triggerFallback(intensity);
    }
    
    /**
     * Checks if shaders are available.
     * @return true if shaders are available
     */
    protected boolean areShadersAvailable() {
        return at.koopro.spells_n_squares.features.fx.ShaderEffectHandler.areShadersAvailable();
    }
    
    /**
     * Triggers the fallback effect when shaders are unavailable.
     * @param intensity The intensity of the effect
     */
    protected abstract void triggerFallback(float intensity);
}

