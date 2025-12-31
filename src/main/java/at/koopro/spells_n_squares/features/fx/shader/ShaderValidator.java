package at.koopro.spells_n_squares.features.fx.shader;

import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Validates shader availability and manages shader cache.
 * Extracted from ShaderEffectHandler to reduce file size and improve organization.
 */
public final class ShaderValidator {
    // Cache for shader availability checks (shader ID -> is available)
    private static final Map<Identifier, Boolean> shaderCache = new HashMap<>();
    
    private ShaderValidator() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a specific shader is loaded and available.
     * Uses caching to avoid repeated checks.
     * 
     * @param shaderId The shader identifier
     * @return true if the shader is available
     */
    public static boolean isShaderLoaded(Identifier shaderId) {
        if (shaderId == null) {
            return false;
        }
        
        // Check cache first
        Boolean cached = shaderCache.get(shaderId);
        if (cached != null) {
            return cached;
        }
        
        // Check availability via PostProcessingManager
        boolean available = PostProcessingManager.isPostProcessingShaderAvailable(shaderId);
        
        // Cache the result
        shaderCache.put(shaderId, available);
        return available;
    }
    
    /**
     * Clears the shader cache.
     * Call this when shaders are reloaded.
     */
    public static void clearCache() {
        shaderCache.clear();
    }
    
    /**
     * Invalidates a specific shader in the cache.
     * @param shaderId The shader identifier to invalidate
     */
    public static void invalidateCache(Identifier shaderId) {
        shaderCache.remove(shaderId);
    }
}


