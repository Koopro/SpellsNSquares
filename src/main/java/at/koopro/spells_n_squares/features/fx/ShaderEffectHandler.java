package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for shader-based post-processing effects.
 * Provides fallback to particle-based effects if shaders are unavailable.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ShaderEffectHandler {
    
    private static boolean shadersSupported = false;
    
    // Shader identifiers
    public static final Identifier LUMOS_ORB_SHADER = ModIdentifierHelper.modId("shaders/core/lumos_orb");
    public static final Identifier CUT_EFFECT_SHADER = ModIdentifierHelper.modId("shaders/core/cut_effect");
    public static final Identifier INVERTED_COLORS_SHADER = ModIdentifierHelper.modId("shaders/core/inverted_colors");
    public static final Identifier GRAYSCALE_SHADER = ModIdentifierHelper.modId("shaders/core/grayscale");
    
    // Post-processing shader identifiers
    public static final Identifier INVERTED_COLORS_POST_SHADER = 
        ModIdentifierHelper.modId("shaders/post/inverted_colors");
    public static final Identifier GRAYSCALE_POST_SHADER = 
        ModIdentifierHelper.modId("shaders/post/grayscale");
    
    // Cache for shader availability
    private static final Map<Identifier, Boolean> shaderCache = new HashMap<>();
    
    /**
     * Initializes shader support check.
     */
    public static void initialize() {
        // Check if shaders are supported
        // Shader availability is now checked via compiled render pipelines
        shadersSupported = Config.areShaderEffectsEnabled();
    }
    
    /**
     * Checks if shader effects are available.
     */
    public static boolean areShadersAvailable() {
        return shadersSupported && Config.areShaderEffectsEnabled();
    }
    
    /**
     * Checks if a specific shader is loaded and available.
     * Uses compiled render pipelines to verify shader availability.
     * 
     * @param shaderId The shader identifier
     * @return true if the shader is available
     */
    public static boolean isShaderLoaded(Identifier shaderId) {
        if (!areShadersAvailable()) {
            return false;
        }
        
        // Check cache first
        Boolean cached = shaderCache.get(shaderId);
        if (cached != null) {
            return cached;
        }
        
        // Check against registered render pipelines
        boolean available = false;
        
        if (LUMOS_ORB_SHADER.equals(shaderId)) {
            RenderPipeline pipeline = PostProcessingManager.getLumosOrbRenderPipeline();
            available = pipeline != null && PostProcessingManager.isPipelineValid(pipeline);
        } else if (CUT_EFFECT_SHADER.equals(shaderId)) {
            RenderPipeline pipeline = PostProcessingManager.getCutEffectRenderPipeline();
            available = pipeline != null && PostProcessingManager.isPipelineValid(pipeline);
        } else if (INVERTED_COLORS_SHADER.equals(shaderId) || INVERTED_COLORS_POST_SHADER.equals(shaderId)) {
            // Post-processing shaders use PostChain, check via PostProcessingManager
            available = PostProcessingManager.isPostProcessingShaderAvailable(INVERTED_COLORS_POST_SHADER);
        } else if (GRAYSCALE_SHADER.equals(shaderId) || GRAYSCALE_POST_SHADER.equals(shaderId)) {
            // Post-processing shaders use PostChain, check via PostProcessingManager
            available = PostProcessingManager.isPostProcessingShaderAvailable(GRAYSCALE_POST_SHADER);
        } else {
            // For unknown shaders, assume available if shaders are enabled
            // This maintains backward compatibility
            available = true;
        }
        
        shaderCache.put(shaderId, available);
        return available;
    }
    
    /**
     * Gets the RenderType identifier for lumos orb shader.
     * Returns the shader identifier if available, otherwise returns null for fallback.
     * 
     * @return Shader identifier if available, null otherwise
     */
    public static Identifier getLumosOrbShaderId() {
        if (areShadersAvailable() && isShaderLoaded(LUMOS_ORB_SHADER)) {
            return LUMOS_ORB_SHADER;
        }
        return null;
    }
    
    /**
     * Gets a generic shader RenderType identifier.
     * 
     * @param shaderId The shader identifier
     * @return Shader identifier if available, null otherwise
     */
    public static Identifier getShaderId(Identifier shaderId) {
        if (areShadersAvailable() && isShaderLoaded(shaderId)) {
            return shaderId;
        }
        return null;
    }
    
    /**
     * Updates the Time uniform for a shader.
     * Note: This is a placeholder - actual shader uniform updates would need
     * to be done through the shader manager or render state.
     * 
     * @param shaderId The shader identifier
     * @param time The time value to set
     */
    public static void updateShaderTime(Identifier shaderId, float time) {
        if (!areShadersAvailable() || !isShaderLoaded(shaderId)) {
            return;
        }
        
        // In a full implementation, this would update the shader uniform
        // For now, this is a placeholder that can be enhanced
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            // Time would be passed via shader manager or render state
            // This is where shader uniform updates would happen
        }
    }
    
    /**
     * Gets the current game time for shader animations.
     * 
     * @return Current game time with partial tick
     */
    public static float getShaderTime() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            return (float) mc.level.getGameTime();
        }
        return 0.0f;
    }
    
    /**
     * Triggers a generic shader effect.
     * 
     * @param effectType The type of effect to trigger
     * @param intensity The intensity of the effect (0.0 to 1.0)
     */
    public static void triggerShaderEffect(String effectType, float intensity) {
        if (!areShadersAvailable()) {
            return;
        }
        
        // Route to appropriate effect handler based on type
        switch (effectType.toLowerCase()) {
            case "spell_cast":
                triggerSpellCastEffect();
                break;
            case "invisibility":
                triggerInvisibilityDistortion();
                break;
            case "time_distortion":
                triggerTimeDistortion();
                break;
            case "magical_aura":
                triggerMagicalAura();
                break;
            case "inverted":
            case "invert":
            case "negative":
                triggerInvertedColors(intensity);
                break;
            case "hue_max":
            case "saturated":
            case "vibrant":
                triggerHueMax(intensity);
                break;
            case "grayscale":
            case "black_and_white":
            case "bw":
                triggerGrayscale(intensity);
                break;
            case "chromatic":
            case "chromatic_aberration":
                triggerChromaticAberration(intensity);
                break;
            case "sepia":
                triggerSepia(intensity);
                break;
            case "night_vision":
            case "nightvision":
                triggerNightVision(intensity);
                break;
            case "acid":
            case "trippy":
                triggerAcidTrip(intensity);
                break;
            default:
                // Unknown effect type
                break;
        }
    }
    
    /**
     * Triggers a spell cast shader effect.
     * Falls back to particle effects if shaders unavailable.
     */
    public static void triggerSpellCastEffect() {
        // White flash for spell casting
        ScreenEffectManager.triggerOverlay(0xFFFFFF, 0.3f, 8, 
            ScreenEffectManager.ScreenOverlay.OverlayType.FLASH);
        // Also trigger a small shake
        ScreenEffectManager.triggerShake(0.05f, 5);
    }
    
    /**
     * Triggers an invisibility distortion effect.
     * Falls back to particle shimmer if shaders unavailable.
     */
    public static void triggerInvisibilityDistortion() {
        // Purple/blue shimmer effect
        ScreenEffectManager.triggerOverlay(0xFF8000FF, 0.15f, 15, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a time distortion effect (Time-Turner).
     * Falls back to particle effects if shaders unavailable.
     */
    public static void triggerTimeDistortion() {
        // Yellow/gold time distortion effect
        ScreenEffectManager.triggerOverlay(0xFFFF00, 0.2f, 20, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        // Add a subtle shake for time distortion
        ScreenEffectManager.triggerShake(0.03f, 10);
    }
    
    /**
     * Triggers a magical aura shader effect.
     * Falls back to particle aura if shaders unavailable.
     */
    public static void triggerMagicalAura() {
        // Cyan/blue magical aura
        ScreenEffectManager.triggerOverlay(0xFF00FFFF, 0.12f, 25, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an inverted/negative colors effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerInvertedColors(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            // Convert Identifier to the type PostProcessingManager expects
            Identifier postShaderId = PostProcessingManager.INVERTED_COLORS_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    30 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Full-screen inverted overlay
        float opacity = Math.min(1.0f, intensity * 0.8f);
        ScreenEffectManager.triggerOverlay(0xFFFFFFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.FLASH);
        // Also add a shake for disorientation
        ScreenEffectManager.triggerShake(0.02f * intensity, 15);
    }
    
    /**
     * Triggers a max hue/saturated colors effect.
     */
    public static void triggerHueMax(float intensity) {
        // Rainbow/vibrant overlay - cycle through colors
        int[] vibrantColors = {
            0xFFFF0000, // Red
            0xFFFF00FF, // Magenta
            0xFF0000FF, // Blue
            0xFF00FFFF, // Cyan
            0xFF00FF00, // Green
            0xFFFFFF00  // Yellow
        };
        int color = vibrantColors[(int)(System.currentTimeMillis() / 200) % vibrantColors.length];
        float opacity = Math.min(0.4f, intensity * 0.3f);
        ScreenEffectManager.triggerOverlay(color, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a grayscale/black and white effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerGrayscale(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            // Convert Identifier to the type PostProcessingManager expects
            Identifier postShaderId = PostProcessingManager.GRAYSCALE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    35 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Gray overlay to simulate desaturation
        float opacity = Math.min(0.5f, intensity * 0.4f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a chromatic aberration effect (color separation).
     */
    public static void triggerChromaticAberration(float intensity) {
        // Red/cyan color separation effect
        float opacity = Math.min(0.3f, intensity * 0.25f);
        // Trigger multiple overlays with slight offsets to simulate chromatic aberration
        ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 20, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        ScreenEffectManager.triggerShake(0.01f * intensity, 10);
    }
    
    /**
     * Triggers a sepia tone effect.
     */
    public static void triggerSepia(float intensity) {
        // Brown/sepia overlay
        float opacity = Math.min(0.35f, intensity * 0.3f);
        ScreenEffectManager.triggerOverlay(0xFF8B4513, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a night vision effect.
     */
    public static void triggerNightVision(float intensity) {
        // Green night vision overlay
        float opacity = Math.min(0.25f, intensity * 0.2f);
        ScreenEffectManager.triggerOverlay(0xFF00FF00, opacity, 50, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an "acid trip" trippy effect.
     */
    public static void triggerAcidTrip(float intensity) {
        // Multiple overlays with different colors for trippy effect
        float opacity = Math.min(0.3f, intensity * 0.25f);
        int[] trippyColors = {
            0xFFFF00FF, // Magenta
            0xFF00FFFF, // Cyan
            0xFFFFFF00, // Yellow
            0xFFFF0000  // Red
        };
        for (int i = 0; i < trippyColors.length; i++) {
            ScreenEffectManager.triggerOverlay(trippyColors[i], opacity, 25 + i * 5, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
        // Add shake for disorientation
        ScreenEffectManager.triggerShake(0.05f * intensity, 20);
    }
}
