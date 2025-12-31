package at.koopro.spells_n_squares.features.fx.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Handler for shader-based post-processing effects.
 * Provides fallback to particle-based effects if shaders are unavailable.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ShaderEffectHandler {
    
    private static boolean shadersSupported = false;
    
    // Shader identifiers
    // Note: Shader paths in identifiers are relative to the shaders/ directory
    // So "core/energy_ball" refers to assets/spells_n_squares/shaders/core/energy_ball.json
    public static final Identifier LUMOS_ORB_SHADER = ModIdentifierHelper.modId("core/lumos_orb");
    public static final Identifier CUT_EFFECT_SHADER = ModIdentifierHelper.modId("core/cut_effect");
    public static final Identifier INVERTED_COLORS_SHADER = ModIdentifierHelper.modId("core/inverted_colors");
    public static final Identifier GRAYSCALE_SHADER = ModIdentifierHelper.modId("core/grayscale");
    public static final Identifier ENERGY_BALL_SHADER = ModIdentifierHelper.modId("core/energy_ball");
    
    // Post-processing shader identifiers
    public static final Identifier INVERTED_COLORS_POST_SHADER =
        ModIdentifierHelper.modId("inverted_colors");
    public static final Identifier GRAYSCALE_POST_SHADER =
        ModIdentifierHelper.modId("grayscale");
    public static final Identifier CHROMATIC_ABERRATION_POST_SHADER =
        ModIdentifierHelper.modId("chromatic_aberration");
    public static final Identifier SEPIA_POST_SHADER =
        ModIdentifierHelper.modId("sepia");
    public static final Identifier MOSAIC_POST_SHADER =
        ModIdentifierHelper.modId("mosaic");
    public static final Identifier TUNNEL_POST_SHADER =
        ModIdentifierHelper.modId("tunnel");
    public static final Identifier FISHEYE_POST_SHADER =
        ModIdentifierHelper.modId("fisheye");
    public static final Identifier POLAROID_POST_SHADER =
        ModIdentifierHelper.modId("polaroid");
    public static final Identifier RETRO_POST_SHADER =
        ModIdentifierHelper.modId("retro");
    public static final Identifier BLACK_AND_WHITE_POST_SHADER =
        ModIdentifierHelper.modId("black_and_white");
    public static final Identifier SATURATED_POST_SHADER =
        ModIdentifierHelper.modId("saturated");
    public static final Identifier GLITCH_POST_SHADER =
        ModIdentifierHelper.modId("glitch");
    public static final Identifier KALEIDOSCOPE_POST_SHADER =
        ModIdentifierHelper.modId("kaleidoscope");
    public static final Identifier RGB_SHIFT_POST_SHADER =
        ModIdentifierHelper.modId("rgb_shift");
    public static final Identifier WAVE_DISTORTION_POST_SHADER =
        ModIdentifierHelper.modId("wave_distortion");
    public static final Identifier BLOOM_POST_SHADER =
        ModIdentifierHelper.modId("bloom");
    public static final Identifier EDGE_DETECTION_POST_SHADER =
        ModIdentifierHelper.modId("edge_detection");
    public static final Identifier PIXELATION_POST_SHADER =
        ModIdentifierHelper.modId("pixelation");
    public static final Identifier HEAT_HAZE_POST_SHADER =
        ModIdentifierHelper.modId("heat_haze");
    public static final Identifier COLOR_CYCLE_POST_SHADER =
        ModIdentifierHelper.modId("color_cycle");
    public static final Identifier MIRROR_POST_SHADER =
        ModIdentifierHelper.modId("mirror");
    public static final Identifier NOISE_POST_SHADER =
        ModIdentifierHelper.modId("noise");
    public static final Identifier ZOOM_BLUR_POST_SHADER =
        ModIdentifierHelper.modId("zoom_blur");
    public static final Identifier UNDERWATER_POST_SHADER =
        ModIdentifierHelper.modId("underwater");
    public static final Identifier DRUNK_POST_SHADER =
        ModIdentifierHelper.modId("drunk");
    public static final Identifier MATRIX_POST_SHADER =
        ModIdentifierHelper.modId("matrix");
    public static final Identifier OLD_TV_POST_SHADER =
        ModIdentifierHelper.modId("old_tv");
    public static final Identifier XRAY_POST_SHADER =
        ModIdentifierHelper.modId("xray");
    public static final Identifier THERMAL_POST_SHADER =
        ModIdentifierHelper.modId("thermal");
    public static final Identifier CARTOON_POST_SHADER =
        ModIdentifierHelper.modId("cartoon");
    public static final Identifier OIL_PAINTING_POST_SHADER =
        ModIdentifierHelper.modId("oil_painting");
    public static final Identifier OLD_FILM_POST_SHADER =
        ModIdentifierHelper.modId("old_film");
    public static final Identifier ACID_TRIP_POST_SHADER =
        ModIdentifierHelper.modId("acid_trip");
    public static final Identifier OUTLINE_POST_SHADER =
        ModIdentifierHelper.modId("outline");
    public static final Identifier FOG_POST_SHADER =
        ModIdentifierHelper.modId("fog");
    public static final Identifier SHARPEN_POST_SHADER =
        ModIdentifierHelper.modId("sharpen");
    public static final Identifier MOTION_BLUR_POST_SHADER =
        ModIdentifierHelper.modId("motion_blur");
    public static final Identifier DEPTH_OF_FIELD_POST_SHADER =
        ModIdentifierHelper.modId("depth_of_field");
    public static final Identifier LENS_FLARE_POST_SHADER =
        ModIdentifierHelper.modId("lens_flare");
    public static final Identifier VIGNETTE_POST_SHADER =
        ModIdentifierHelper.modId("vignette");
    public static final Identifier CONTRAST_BOOST_POST_SHADER =
        ModIdentifierHelper.modId("contrast_boost");
    public static final Identifier PULSING_GLOW_POST_SHADER =
        ModIdentifierHelper.modId("pulsing_glow");
    public static final Identifier SCROLLING_STRIPES_POST_SHADER =
        ModIdentifierHelper.modId("scrolling_stripes");
    public static final Identifier WARPING_VORTEX_POST_SHADER =
        ModIdentifierHelper.modId("warping_vortex");
    public static final Identifier PARTICLE_RAIN_POST_SHADER =
        ModIdentifierHelper.modId("particle_rain");
    public static final Identifier COLOR_WAVE_POST_SHADER =
        ModIdentifierHelper.modId("color_wave");
    public static final Identifier RIPPLE_EFFECT_POST_SHADER =
        ModIdentifierHelper.modId("ripple_effect");
    public static final Identifier BREATHING_VIGNETTE_POST_SHADER =
        ModIdentifierHelper.modId("breathing_vignette");
    public static final Identifier ANIMATED_NOISE_POST_SHADER =
        ModIdentifierHelper.modId("animated_noise");
    public static final Identifier ROTATING_KALEIDOSCOPE_POST_SHADER =
        ModIdentifierHelper.modId("rotating_kaleidoscope");
    public static final Identifier ENERGY_PULSE_POST_SHADER =
        ModIdentifierHelper.modId("energy_pulse");
    
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
     * Delegates to ShaderValidator for validation logic.
     * 
     * @param shaderId The shader identifier
     * @return true if the shader is available
     */
    public static boolean isShaderLoaded(Identifier shaderId) {
        if (!areShadersAvailable()) {
            return false;
        }
        
        // Use ShaderValidator for validation
        return at.koopro.spells_n_squares.features.fx.shader.ShaderValidator.isShaderLoaded(shaderId);
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
     * Uses strategy pattern for dispatch instead of large switch statement.
     * 
     * @param effectType The type of effect to trigger
     * @param intensity The intensity of the effect (0.0 to 1.0)
     */
    public static void triggerShaderEffect(String effectType, float intensity) {
        if (!areShadersAvailable()) {
            return;
        }
        
        // Use strategy pattern for dispatch
        at.koopro.spells_n_squares.features.fx.shader.strategies.ShaderEffectStrategy strategy = 
            at.koopro.spells_n_squares.features.fx.shader.strategies.ShaderEffectStrategyRegistry.getStrategy(effectType);
        
        if (strategy != null) {
            strategy.trigger(intensity);
        }
        // Unknown effect type - silently ignored (was default case in switch)
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
     * Convenience trigger for testing the inverted colors post effect.
     * Uses a reasonable default intensity and duration and falls back automatically.
     */
    public static void triggerInvertedColorsTest() {
        triggerInvertedColors(0.8f);
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
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerChromaticAberration(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.CHROMATIC_ABERRATION_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    25 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Red/cyan color separation effect
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 20, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        ScreenEffectManager.triggerShake(0.01f * intensity, 10);
    }
    
    /**
     * Triggers a sepia tone effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerSepia(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.SEPIA_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    30 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Brown/sepia overlay
        float opacity = Math.min(0.35f, intensity * 0.3f);
        ScreenEffectManager.triggerOverlay(0xFF8B4513, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a mosaic/pixelation effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerMosaic(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.MOSAIC_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    30 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Simple overlay (mosaic effect is hard to simulate with just overlays)
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
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
     * Triggers a tunnel/abstract 3D effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerTunnel(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.TUNNEL_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    40 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Trippy overlay effect
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        ScreenEffectManager.triggerShake(0.03f * intensity, 20);
    }
    
    /**
     * Triggers a fisheye lens distortion effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerFisheye(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.FISHEYE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    30 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Simple overlay (fisheye is hard to simulate without shader)
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an "acid trip" trippy effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerAcidTrip(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.ACID_TRIP_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        // Fallback: Multiple overlays with different colors for trippy effect
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
    
    /**
     * Triggers a polaroid/vintage photo effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerPolaroid(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.POLAROID_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    30 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Warm sepia-like overlay
        float opacity = Math.min(0.4f, intensity * 0.35f);
        ScreenEffectManager.triggerOverlay(0xFFD4A574, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a retro/vintage effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerRetro(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.RETRO_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    35 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Color-shifted overlay with slight shake
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFFFFCC99, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        ScreenEffectManager.triggerShake(0.01f * intensity, 10);
    }
    
    /**
     * Triggers a black and white effect (high contrast).
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerBlackAndWhite(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.BLACK_AND_WHITE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    35 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: High contrast grayscale overlay
        float opacity = Math.min(0.6f, intensity * 0.5f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a saturated/vibrant colors effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerSaturated(float intensity) {
        // Try post-processing shader first
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.SATURATED_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(
                    postShaderId,
                    intensity,
                    30 // Duration in ticks
                );
                return;
            }
        }
        
        // Fallback: Vibrant color overlay
        float opacity = Math.min(0.25f, intensity * 0.2f);
        int[] vibrantColors = {
            0xFFFF0000, // Red
            0xFF00FF00, // Green
            0xFF0000FF  // Blue
        };
        int color = vibrantColors[(int)(System.currentTimeMillis() / 300) % vibrantColors.length];
        ScreenEffectManager.triggerOverlay(color, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a glitch/digital corruption effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerGlitch(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.GLITCH_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 25);
                return;
            }
        }
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 20, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        ScreenEffectManager.triggerShake(0.02f * intensity, 10);
    }
    
    /**
     * Triggers a kaleidoscope effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerKaleidoscope(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.KALEIDOSCOPE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.25f, intensity * 0.2f);
        ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an RGB shift effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerRgbShift(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.RGB_SHIFT_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 25, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a wave distortion effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerWaveDistortion(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.WAVE_DISTORTION_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a bloom/glow effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerBloom(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.BLOOM_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFFFFFFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an edge detection effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerEdgeDetection(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.EDGE_DETECTION_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.4f, intensity * 0.35f);
        ScreenEffectManager.triggerOverlay(0xFF000000, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a pixelation effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerPixelation(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.PIXELATION_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a heat haze effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerHeatHaze(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.HEAT_HAZE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFF8800, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a color cycle/rainbow effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerColorCycle(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.COLOR_CYCLE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 40);
                return;
            }
        }
        float opacity = Math.min(0.25f, intensity * 0.2f);
        int[] cycleColors = {
            0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00
        };
        int color = cycleColors[(int)(System.currentTimeMillis() / 200) % cycleColors.length];
        ScreenEffectManager.triggerOverlay(color, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a mirror/flip effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerMirror(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.MIRROR_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a noise/grain effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerNoise(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.NOISE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.1f, intensity * 0.08f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a zoom blur effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerZoomBlur(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.ZOOM_BLUR_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an underwater effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerUnderwater(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.UNDERWATER_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 40);
                return;
            }
        }
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFF0066FF, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a drunk effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerDrunk(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.DRUNK_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.25f, intensity * 0.2f);
        ScreenEffectManager.triggerOverlay(0xFFFFCC99, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        ScreenEffectManager.triggerShake(0.03f * intensity, 15);
    }
    
    /**
     * Triggers a matrix effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerMatrix(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.MATRIX_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 50);
                return;
            }
        }
        float opacity = Math.min(0.4f, intensity * 0.35f);
        ScreenEffectManager.triggerOverlay(0xFF00FF00, opacity, 50, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an old TV/CRT effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerOldTv(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.OLD_TV_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an X-ray effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerXray(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.XRAY_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFF00FF80, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a thermal vision effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerThermal(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.THERMAL_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 40);
                return;
            }
        }
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a cartoon/cel-shading effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerCartoon(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.CARTOON_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.25f, intensity * 0.2f);
        ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an oil painting effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerOilPainting(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.OIL_PAINTING_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFFCC99, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an old film effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerOldFilm(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.OLD_FILM_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 40);
                return;
            }
        }
        float opacity = Math.min(0.25f, intensity * 0.2f);
        ScreenEffectManager.triggerOverlay(0xFF8B4513, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an outline/highlight effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerOutline(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.OUTLINE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a fog effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerFog(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.FOG_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 40);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFCCCCCC, opacity, 40, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a sharpen effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerSharpen(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.SHARPEN_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.1f, intensity * 0.08f);
        ScreenEffectManager.triggerOverlay(0xFFFFFFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a motion blur effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerMotionBlur(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.MOTION_BLUR_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a depth of field effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerDepthOfField(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.DEPTH_OF_FIELD_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 35);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a lens flare effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerLensFlare(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.LENS_FLARE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFFFF00, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a vignette effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerVignette(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.VIGNETTE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF000000, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a contrast boost effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerContrastBoost(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.CONTRAST_BOOST_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.1f, intensity * 0.08f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a pulsing glow effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerPulsingGlow(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.PULSING_GLOW_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a scrolling stripes effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerScrollingStripes(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.SCROLLING_STRIPES_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a warping vortex effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerWarpingVortex(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.WARPING_VORTEX_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF8000FF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a particle rain effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerParticleRain(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.PARTICLE_RAIN_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a color wave effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerColorWave(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.COLOR_WAVE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a ripple effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerRippleEffect(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.RIPPLE_EFFECT_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.15f, intensity * 0.12f);
        ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a breathing vignette effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerBreathingVignette(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.BREATHING_VIGNETTE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.3f, intensity * 0.25f);
        ScreenEffectManager.triggerOverlay(0xFF000000, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an animated noise effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerAnimatedNoise(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.ANIMATED_NOISE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.1f, intensity * 0.08f);
        ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers a rotating kaleidoscope effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerRotatingKaleidoscope(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.ROTATING_KALEIDOSCOPE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
    
    /**
     * Triggers an energy pulse effect.
     * Uses proper post-processing shader if available, otherwise falls back to overlay.
     */
    public static void triggerEnergyPulse(float intensity) {
        if (areShadersAvailable()) {
            Identifier postShaderId = PostProcessingManager.ENERGY_PULSE_POST_SHADER;
            if (PostProcessingManager.isPostProcessingShaderAvailable(postShaderId)) {
                PostProcessingManager.addEffect(postShaderId, intensity, 30);
                return;
            }
        }
        float opacity = Math.min(0.2f, intensity * 0.15f);
        ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 30, 
            ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
    }
}
