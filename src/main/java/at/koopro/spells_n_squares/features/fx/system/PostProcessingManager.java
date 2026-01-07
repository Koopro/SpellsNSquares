package at.koopro.spells_n_squares.features.fx.system;

import at.koopro.spells_n_squares.features.fx.processing.PostProcessingReflectionHelper;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages post-processing shader effects applied to the entire rendered frame.
 * Uses NeoForge's PostChain system for screen-wide effects like inverted colors and grayscale.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class PostProcessingManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Cached PostChain instances for each shader
    private static final Map<Identifier, PostChain> postChains = new HashMap<>();
    
    // Flag to track if we need to preload shaders after resource reload
    private static boolean needsShaderPreload = false;
    
    // Frame counter for animation (increments each render frame)
    private static int animationFrameCounter = 0;
    
    // Post-processing shader identifiers
    // PostChain identifiers should point to just the name; ShaderManager
    // will look them up under post_effect/<name>.json.
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
    public static final Identifier UNIFIED_POST_SHADER =
        ModIdentifierHelper.modId("unified");
    
    
    /**
     * Gets or creates a PostChain for the given shader identifier.
     * PostChains are created lazily when first needed.
     * 
     * @param shaderId The shader identifier
     * @return The PostChain instance, or null if creation failed
     */
    public static PostChain getOrCreatePostChain(Identifier shaderId) {
        // Check cache first
        PostChain cached = postChains.get(shaderId);
        if (cached != null) {
            return cached;
        }
        
        // Create new PostChain lazily using the shader manager
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getMainRenderTarget() == null || mc.getShaderManager() == null) {
            return null;
        }

        // Make sure the backing json exists before attempting to load; avoids noisy
        // exceptions when a resource is missing (e.g., dev workspace not refreshed).
        // ShaderManager expects post-effect JSON files in post_effect/ directory
        Identifier shaderJson = Identifier.fromNamespaceAndPath(
                shaderId.getNamespace(),
                "post_effect/" + shaderId.getPath() + ".json"
        );
        var jsonResource = mc.getResourceManager().getResource(shaderJson);
        if (jsonResource.isEmpty()) {
            LOGGER.warn("Post-processing shader resource missing: {} (expected at: {})", shaderJson, shaderJson);
            return null;
        }
        LOGGER.debug("Found post-processing shader JSON: {}", shaderJson);

        // Check that the fragment shader exists (we use minecraft:core/screenquad for vertex shader)
        // Fragment shaders are referenced as "spells_n_squares:post/{name}" in the JSON
        Identifier fragmentShader = Identifier.fromNamespaceAndPath(
                shaderId.getNamespace(),
                "shaders/post/" + shaderId.getPath() + ".fsh"
        );
        if (mc.getResourceManager().getResource(fragmentShader).isEmpty()) {
            LOGGER.warn("Post-processing fragment shader missing: {}", fragmentShader);
            return null;
        }
        LOGGER.debug("Found post-processing fragment shader: {}", fragmentShader);

        try {
            LOGGER.debug("Attempting to load PostChain with identifier: {} (expects JSON at: {})", shaderId, shaderJson);
            // Note: ShaderManager.getPostChain() requires the shader to be discovered during resource reload.
            // If it's not in ShaderManager's internal cache, this will throw CompilationException.
            // The shader files exist, but ShaderManager's cache may not have been populated.
            PostChain chain = mc.getShaderManager().getPostChain(shaderId, LevelTargetBundle.MAIN_TARGETS);
            if (chain != null) {
                postChains.put(shaderId, chain);
                LOGGER.debug("Successfully loaded post-processing shader: {}", shaderId);
            } else {
                LOGGER.warn("PostChain returned null for shader: {} (ShaderManager may not have discovered it during resource reload)", shaderId);
            }
            return chain;
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Failed to parse post-processing shader {}: {}", shaderId, e.getMessage());
            return null;
        } catch (RuntimeException e) {
            // ShaderManager.CompilationException is a RuntimeException
            if (e.getClass().getSimpleName().contains("CompilationException")) {
                LOGGER.warn("Failed to compile post-processing shader {}: {}", shaderId, e.getMessage());
            } else {
                LOGGER.warn("Runtime error creating PostChain for {}: {}", shaderId, e.getMessage());
            }
            return null;
        } catch (Exception e) {
            LOGGER.warn("Unexpected error creating PostChain for {}: {}", shaderId, e.getMessage(), e);
            return null;
        }
    }
    
    
    /**
     * Adds a new post-processing effect.
     * 
     * @param shaderId The shader identifier
     * @param intensity The intensity of the effect (0.0 to 1.0)
     * @param duration Duration in ticks (-1 for infinite/persistent)
     */
    public static void addEffect(Identifier shaderId, float intensity, int duration) {
        PostProcessingEffectManager.addEffect(shaderId, intensity, duration);
    }
    
    /**
     * Adds a persistent post-processing effect (no duration, stays until manually removed).
     * 
     * @param shaderId The shader identifier
     * @param intensity The intensity (0.0 to 1.0)
     */
    public static void addPersistentEffect(Identifier shaderId, float intensity) {
        PostProcessingEffectManager.addPersistentEffect(shaderId, intensity);
    }
    
    /**
     * Removes all active effects for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if any effects were removed
     */
    public static boolean removeEffect(Identifier shaderId) {
        return PostProcessingEffectManager.removeEffect(shaderId);
    }
    
    /**
     * Checks if an effect is currently active for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if the effect is active
     */
    public static boolean isEffectActive(Identifier shaderId) {
        return PostProcessingEffectManager.isEffectActive(shaderId);
    }
    
    /**
     * Checks if a persistent effect is currently active for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if a persistent effect is active
     */
    public static boolean hasPersistentEffect(Identifier shaderId) {
        return PostProcessingEffectManager.hasPersistentEffect(shaderId);
    }
    
    /**
     * Registers render pipelines for custom shader resources.
     * Uses NeoForge's RegisterRenderPipelinesEvent to register custom shader pipelines.
     */
    @SubscribeEvent
    public static void registerRenderPipelines(final RegisterRenderPipelinesEvent event) {
        PostProcessingPipelineManager.registerRenderPipelines(event);
    }
    
    /**
     * Clears PostChain cache on resource reload so ShaderManager can rediscover shaders.
     */
    @SubscribeEvent
    public static void onAddClientReloadListeners(AddClientReloadListenersEvent event) {
        // Clear our PostChain cache when resources reload
        // This allows ShaderManager to rediscover shaders that may have been added/modified
        postChains.clear();
        needsShaderPreload = true;
        LOGGER.debug("Cleared PostChain cache for resource reload, will preload shaders on next tick");
    }
    
    /**
     * Precompiles and validates registered render pipelines.
     * Called after level rendering to check if shaders loaded successfully.
     */
    @SubscribeEvent
    // Note: RenderLevelStageEvent.AfterLevel may be deprecated, but it's the correct event for post-processing
    // No alternative exists in current NeoForge API
    @SuppressWarnings("deprecation")
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
        SafeEventHandler.execute(() -> {
            if (!PostProcessingPipelineManager.arePipelinesChecked()) {
                PostProcessingPipelineManager.checkPipelineValidity();
                PostProcessingPipelineManager.setPipelinesChecked(true);
            }
            if (!PostProcessingEffectManager.hasActiveEffects() || !Config.areShaderEffectsEnabled()) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.getMainRenderTarget() == null) {
                return;
            }

            // Increment frame counter for animation
            animationFrameCounter++;
            // Use frame counter as time source (more reliable than game time for smooth animation)
            // Convert to seconds: frame counter * (1/60) assuming ~60 FPS
            float animationTime = animationFrameCounter * 0.016f;
            
            // Apply each active effect using PostChain (when available)
            for (PostProcessingEffectManager.PostProcessingEffect effect : PostProcessingEffectManager.getActiveEffects()) {
                PostChain chain = getOrCreatePostChain(effect.shaderId);
                if (chain != null) {
                    float currentIntensity = effect.getCurrentIntensity();
                    if (currentIntensity > 0.001f) {
                        // Attempt to update Time uniform via reflection (workaround for PostChain limitation)
                        // Use animationTime for smooth frame-based animation
                        PostProcessingReflectionHelper.updatePostChainTimeUniform(chain, animationTime);
                        
                        // Note: process() is deprecated but no alternative exists yet
                        chain.process(mc.getMainRenderTarget(), GraphicsResourceAllocator.UNPOOLED);
                    }
                }
            }
        }, "rendering post-processing level stage");
    }
    
    /**
     * Ticks all active post-processing effects.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        SafeEventHandler.execute(() -> {
            PostProcessingEffectManager.tickEffects();
            
            // Preload post-processing shaders after resource reload to ensure ShaderManager discovers them
            if (needsShaderPreload) {
                needsShaderPreload = false;
                Minecraft mc = Minecraft.getInstance();
                if (mc != null && mc.getShaderManager() != null) {
                    LOGGER.debug("Preloading post-processing shaders after resource reload");
                    // Try to load the shaders to ensure they're in ShaderManager's cache
                    // This is done on the render thread after resources have reloaded
                    getOrCreatePostChain(INVERTED_COLORS_POST_SHADER);
                    getOrCreatePostChain(GRAYSCALE_POST_SHADER);
                    getOrCreatePostChain(CHROMATIC_ABERRATION_POST_SHADER);
                    getOrCreatePostChain(SEPIA_POST_SHADER);
                    getOrCreatePostChain(MOSAIC_POST_SHADER);
                    getOrCreatePostChain(TUNNEL_POST_SHADER);
                    getOrCreatePostChain(FISHEYE_POST_SHADER);
                    getOrCreatePostChain(POLAROID_POST_SHADER);
                    getOrCreatePostChain(RETRO_POST_SHADER);
                    getOrCreatePostChain(BLACK_AND_WHITE_POST_SHADER);
                    getOrCreatePostChain(SATURATED_POST_SHADER);
                    getOrCreatePostChain(GLITCH_POST_SHADER);
                    getOrCreatePostChain(KALEIDOSCOPE_POST_SHADER);
                    getOrCreatePostChain(RGB_SHIFT_POST_SHADER);
                    getOrCreatePostChain(WAVE_DISTORTION_POST_SHADER);
                    getOrCreatePostChain(BLOOM_POST_SHADER);
                    getOrCreatePostChain(EDGE_DETECTION_POST_SHADER);
                    getOrCreatePostChain(PIXELATION_POST_SHADER);
                    getOrCreatePostChain(HEAT_HAZE_POST_SHADER);
                    getOrCreatePostChain(COLOR_CYCLE_POST_SHADER);
                    getOrCreatePostChain(MIRROR_POST_SHADER);
                    getOrCreatePostChain(NOISE_POST_SHADER);
                    getOrCreatePostChain(ZOOM_BLUR_POST_SHADER);
                    getOrCreatePostChain(UNDERWATER_POST_SHADER);
                    getOrCreatePostChain(DRUNK_POST_SHADER);
                    getOrCreatePostChain(MATRIX_POST_SHADER);
                    getOrCreatePostChain(OLD_TV_POST_SHADER);
                    getOrCreatePostChain(XRAY_POST_SHADER);
                    getOrCreatePostChain(THERMAL_POST_SHADER);
                    getOrCreatePostChain(CARTOON_POST_SHADER);
                    getOrCreatePostChain(OIL_PAINTING_POST_SHADER);
                    getOrCreatePostChain(OLD_FILM_POST_SHADER);
                    getOrCreatePostChain(ACID_TRIP_POST_SHADER);
                    getOrCreatePostChain(OUTLINE_POST_SHADER);
                    getOrCreatePostChain(FOG_POST_SHADER);
                    getOrCreatePostChain(SHARPEN_POST_SHADER);
                    getOrCreatePostChain(MOTION_BLUR_POST_SHADER);
                    getOrCreatePostChain(DEPTH_OF_FIELD_POST_SHADER);
                    getOrCreatePostChain(LENS_FLARE_POST_SHADER);
                    getOrCreatePostChain(VIGNETTE_POST_SHADER);
                    getOrCreatePostChain(CONTRAST_BOOST_POST_SHADER);
                    getOrCreatePostChain(PULSING_GLOW_POST_SHADER);
                    getOrCreatePostChain(SCROLLING_STRIPES_POST_SHADER);
                    getOrCreatePostChain(WARPING_VORTEX_POST_SHADER);
                    getOrCreatePostChain(PARTICLE_RAIN_POST_SHADER);
                    getOrCreatePostChain(COLOR_WAVE_POST_SHADER);
                    getOrCreatePostChain(RIPPLE_EFFECT_POST_SHADER);
                    getOrCreatePostChain(BREATHING_VIGNETTE_POST_SHADER);
                    getOrCreatePostChain(ANIMATED_NOISE_POST_SHADER);
                    getOrCreatePostChain(ROTATING_KALEIDOSCOPE_POST_SHADER);
                    getOrCreatePostChain(ENERGY_PULSE_POST_SHADER);
                }
            }
        }, "ticking post-processing effects");
    }
    
    /**
     * Checks if a post-processing shader is available.
     * Attempts to create the PostChain if not already cached.
     * 
     * @param shaderId The shader identifier
     * @return true if the PostChain can be created and is available
     */
    public static boolean isPostProcessingShaderAvailable(Identifier shaderId) {
        if (!Config.areShaderEffectsEnabled()) {
            return false;
        }
        try {
            PostChain chain = getOrCreatePostChain(shaderId);
            return chain != null;
        } catch (Exception e) {
            LOGGER.warn("Post-processing shader {} unavailable: {}", shaderId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Clears all active post-processing effects.
     */
    public static void clearAllEffects() {
        PostProcessingEffectManager.clearAllEffects();
    }
    
}













