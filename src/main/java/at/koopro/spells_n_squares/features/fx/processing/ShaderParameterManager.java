package at.koopro.spells_n_squares.features.fx.processing;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.features.fx.config.ShaderConfiguration;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages shader parameters and updates shader uniforms in real-time.
 */
public class ShaderParameterManager {
    private static final Map<Identifier, Map<String, Float>> shaderParameters = CollectionFactory.createMap();
    private static final Map<Identifier, PostChain> activeChains = CollectionFactory.createMap();
    
    private static ShaderConfiguration currentConfig;
    private static Consumer<Map<String, Float>> onParametersChanged;
    
    /**
     * Sets the current shader configuration.
     */
    public static void setCurrentConfig(ShaderConfiguration config) {
        currentConfig = config;
        if (config != null && config.getParameters() != null) {
            Identifier shaderId = config.getShaderType() == ShaderConfiguration.ShaderType.UNIFIED 
                ? PostProcessingManager.UNIFIED_POST_SHADER 
                : config.getShaderId();
            
            if (shaderId != null) {
                Map<String, Float> params = CollectionFactory.createMap();
                params.putAll(config.getParameters());
                shaderParameters.put(shaderId, params);
            }
        }
    }
    
    /**
     * Gets the current shader configuration.
     */
    public static ShaderConfiguration getCurrentConfig() {
        return currentConfig;
    }
    
    /**
     * Sets a parameter value for a shader.
     */
    public static void setParameter(Identifier shaderId, String parameterName, float value) {
        shaderParameters.computeIfAbsent(shaderId, k -> CollectionFactory.createMap()).put(parameterName, value);
        
        // Update active shader if it's currently active
        PostChain chain = activeChains.get(shaderId);
        if (chain != null) {
            updateShaderUniform(chain, parameterName, value);
        }
        
        // Notify listeners
        if (onParametersChanged != null && currentConfig != null) {
            Map<String, Float> params = shaderParameters.get(shaderId);
            if (params != null) {
                Map<String, Float> paramsCopy = CollectionFactory.createMap();
                paramsCopy.putAll(params);
                onParametersChanged.accept(paramsCopy);
            }
        }
    }
    
    /**
     * Gets a parameter value for a shader.
     */
    public static Float getParameter(Identifier shaderId, String parameterName) {
        Map<String, Float> params = shaderParameters.get(shaderId);
        return params != null ? params.get(parameterName) : null;
    }
    
    /**
     * Gets all parameters for a shader.
     */
    public static Map<String, Float> getParameters(Identifier shaderId) {
        Map<String, Float> params = shaderParameters.getOrDefault(shaderId, CollectionFactory.createMap());
        Map<String, Float> result = CollectionFactory.createMap();
        result.putAll(params);
        return result;
    }
    
    /**
     * Sets all parameters for a shader.
     */
    public static void setParameters(Identifier shaderId, Map<String, Float> parameters) {
        Map<String, Float> params = CollectionFactory.createMap();
        params.putAll(parameters);
        shaderParameters.put(shaderId, params);
        
        // Update active shader
        PostChain chain = activeChains.get(shaderId);
        if (chain != null) {
            for (Map.Entry<String, Float> entry : parameters.entrySet()) {
                updateShaderUniform(chain, entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * Activates a shader with current parameters.
     */
    public static void activateShader(Identifier shaderId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getShaderManager() == null) {
            return;
        }
        
        try {
            PostChain chain = PostProcessingManager.getOrCreatePostChain(shaderId);
            if (chain != null) {
                activeChains.put(shaderId, chain);
                
                // Apply all current parameters
                Map<String, Float> params = shaderParameters.get(shaderId);
                if (params != null) {
                    for (Map.Entry<String, Float> entry : params.entrySet()) {
                        updateShaderUniform(chain, entry.getKey(), entry.getValue());
                    }
                }
                
                // Add to post-processing manager
                PostProcessingManager.removeEffect(shaderId);
                PostProcessingManager.addPersistentEffect(shaderId, 1.0f);
            }
        } catch (Exception e) {
            // Handle error silently - shader may not be available
        }
    }
    
    /**
     * Deactivates a shader.
     */
    public static void deactivateShader(Identifier shaderId) {
        activeChains.remove(shaderId);
        PostProcessingManager.removeEffect(shaderId);
    }
    
    /**
     * Updates a shader uniform using reflection.
     */
    private static void updateShaderUniform(PostChain chain, String uniformName, float value) {
        PostProcessingReflectionHelper.updatePostChainUniform(chain, uniformName, value);
    }
    
    /**
     * Sets a callback for when parameters change.
     */
    public static void setOnParametersChanged(Consumer<Map<String, Float>> callback) {
        onParametersChanged = callback;
    }
    
    /**
     * Resets all parameters to default values.
     */
    public static void resetParameters(Identifier shaderId) {
        shaderParameters.remove(shaderId);
        if (currentConfig != null) {
            currentConfig.getParameters().clear();
        }
    }
    
    /**
     * Gets default parameter values for unified shader.
     */
    public static Map<String, Float> getUnifiedShaderDefaults() {
        Map<String, Float> defaults = CollectionFactory.createMap();
        defaults.put("Desat", 0.0f);
        defaults.put("RedTint", 0.0f);
        defaults.put("BlueTint", 0.0f);
        defaults.put("Contrast", 1.0f);
        defaults.put("Bright", 1.0f);
        defaults.put("Satur", 1.0f);
        defaults.put("Invert", 0.0f);
        defaults.put("Chrom", 0.0f);
        defaults.put("Vignette", 0.0f);
        defaults.put("RadBlur", 0.0f);
        defaults.put("Barrel", 0.0f);
        defaults.put("Edge", 0.0f);
        defaults.put("EdgeThk", 1.0f);
        defaults.put("EdgeGlow", 1.0f);
        defaults.put("Bloom", 0.0f);
        defaults.put("Blur", 0.0f);
        defaults.put("Sharp", 0.0f);
        defaults.put("ShakeX", 0.0f);
        defaults.put("ShakeY", 0.0f);
        return defaults;
    }
}

