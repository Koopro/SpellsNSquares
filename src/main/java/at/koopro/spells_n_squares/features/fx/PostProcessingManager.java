package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    // Active post-processing effects
    private static final List<PostProcessingEffect> activeEffects = new ArrayList<>();
    
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
    
    // Render pipelines for core shaders (using NeoForge custom shader resources)
    // Shader paths are relative to the shaders/ directory (e.g., "core/lumos_orb" -> "shaders/core/lumos_orb.vsh")
    private static final RenderPipeline LUMOS_ORB_PIPELINE = RenderPipeline.builder()
            .withLocation(ModIdentifierHelper.modId("pipeline/lumos_orb"))
            .withVertexShader(ModIdentifierHelper.modId("core/lumos_orb"))
            .withFragmentShader(ModIdentifierHelper.modId("core/lumos_orb"))
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .build();
    
    private static final RenderPipeline CUT_EFFECT_PIPELINE = RenderPipeline.builder()
            .withLocation(ModIdentifierHelper.modId("pipeline/cut_effect"))
            .withVertexShader(ModIdentifierHelper.modId("core/cut_effect"))
            .withFragmentShader(ModIdentifierHelper.modId("core/cut_effect"))
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .build();
    
    private static final RenderPipeline ENERGY_BALL_PIPELINE = RenderPipeline.builder()
            .withLocation(ModIdentifierHelper.modId("pipeline/energy_ball"))
            .withVertexShader(ModIdentifierHelper.modId("core/energy_ball"))
            .withFragmentShader(ModIdentifierHelper.modId("core/energy_ball"))
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .build();

    // Cache for compiled pipelines
    private static final Map<RenderPipeline, CompiledRenderPipeline> compiledPipelines = new HashMap<>();
    private static final Map<RenderPipeline, Boolean> pipelineValidity = new HashMap<>();
    private static boolean pipelinesChecked = false;

    /**
     * Represents an active post-processing effect.
     */
    private static class PostProcessingEffect {
        Identifier shaderId;
        float intensity;
        int duration; // -1 for infinite/persistent
        int age;
        
        PostProcessingEffect(Identifier shaderId, float intensity, int duration) {
            this.shaderId = shaderId;
            this.intensity = intensity;
            this.duration = duration;
            this.age = 0;
        }
        
        void tick() {
            age++;
        }
        
        boolean isExpired() {
            return duration >= 0 && age >= duration;
        }
        
        float getCurrentIntensity() {
            // For infinite duration, return full intensity
            if (duration < 0) {
                return intensity;
            }
            // Fade out over time
            float progress = (float) age / duration;
            return intensity * (1.0f - progress);
        }
    }
    
    /**
     * Gets or creates a PostChain for the given shader identifier.
     * PostChains are created lazily when first needed.
     * 
     * @param shaderId The shader identifier
     * @return The PostChain instance, or null if creation failed
     */
    private static PostChain getOrCreatePostChain(Identifier shaderId) {
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
     * Attempts to update the Time uniform in a PostChain using reflection.
     * This is a workaround since PostChain doesn't expose a direct uniform setter API.
     * 
     * @param chain The PostChain to update
     * @param time The time value to set
     */
    private static void updatePostChainTimeUniform(PostChain chain, float time) {
        if (chain == null) {
            return;
        }
        
        try {
            // Try multiple reflection approaches to update Time uniform
            // Approach 1: Try to access passes and update uniforms in each pass
            java.lang.reflect.Field passesField = chain.getClass().getDeclaredField("passes");
            passesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<Object> passes = (java.util.List<Object>) passesField.get(chain);
            
            if (passes != null) {
                for (Object pass : passes) {
                    try {
                        // Try to get shader program from pass
                        java.lang.reflect.Field programField = pass.getClass().getDeclaredField("program");
                        programField.setAccessible(true);
                        Object program = programField.get(pass);
                        
                        if (program != null) {
                            // Try to update Time uniform
                            updateShaderProgramUniform(program, "Time", time);
                        }
                    } catch (Exception e) {
                        // Try next approach
                    }
                }
            }
        } catch (Exception e) {
            // Reflection approach failed, try alternative method
            try {
                // Approach 2: Try direct method access
                java.lang.reflect.Method updateUniformMethod = chain.getClass().getDeclaredMethod("updateUniform", String.class, float.class);
                updateUniformMethod.setAccessible(true);
                updateUniformMethod.invoke(chain, "Time", time);
            } catch (Exception e2) {
                // All reflection approaches failed - shader will use Time=0.0 (static pattern)
                // This is expected and shaders have fallback position-based calculations
            }
        }
    }
    
    /**
     * Helper method to update a uniform in a shader program using reflection.
     */
    private static void updateShaderProgramUniform(Object program, String uniformName, float value) {
        if (program == null) {
            return;
        }
        
        try {
            // Try various method names for setting float uniforms
            String[] methodNames = {"setUniform", "setFloat", "setFloatUniform", "uniform1f"};
            for (String methodName : methodNames) {
                try {
                    java.lang.reflect.Method method = program.getClass().getMethod(methodName, String.class, float.class);
                    method.invoke(program, uniformName, value);
                    return; // Success
                } catch (NoSuchMethodException e) {
                    // Try next method name
                }
            }
        } catch (Exception e) {
            // Failed to update uniform
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
        if (Config.getScreenEffectIntensity() <= 0.0) {
            return;
        }
        
        float adjustedIntensity = (float) (intensity * Config.getScreenEffectIntensity());
        activeEffects.add(new PostProcessingEffect(shaderId, adjustedIntensity, duration));
    }
    
    /**
     * Adds a persistent post-processing effect (no duration, stays until manually removed).
     * 
     * @param shaderId The shader identifier
     * @param intensity The intensity (0.0 to 1.0)
     */
    public static void addPersistentEffect(Identifier shaderId, float intensity) {
        addEffect(shaderId, intensity, -1);
    }
    
    /**
     * Removes all active effects for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if any effects were removed
     */
    public static boolean removeEffect(Identifier shaderId) {
        return activeEffects.removeIf(effect -> effect.shaderId.equals(shaderId));
    }
    
    /**
     * Checks if an effect is currently active for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if the effect is active
     */
    public static boolean isEffectActive(Identifier shaderId) {
        return activeEffects.stream().anyMatch(effect -> 
            effect.shaderId.equals(shaderId) && !effect.isExpired());
    }
    
    /**
     * Checks if a persistent effect is currently active for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if a persistent effect is active
     */
    public static boolean hasPersistentEffect(Identifier shaderId) {
        return activeEffects.stream().anyMatch(effect -> 
            effect.shaderId.equals(shaderId) && effect.duration < 0 && !effect.isExpired());
    }
    
    /**
     * Registers render pipelines for custom shader resources.
     * Uses NeoForge's RegisterRenderPipelinesEvent to register custom shader pipelines.
     */
    @SubscribeEvent
    public static void registerRenderPipelines(final RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LUMOS_ORB_PIPELINE);
        event.registerPipeline(CUT_EFFECT_PIPELINE);
        event.registerPipeline(ENERGY_BALL_PIPELINE);
        LOGGER.debug("Registered render pipelines for custom shader resources");
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
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
        if (!pipelinesChecked) {
            checkPipelineValidity();
            pipelinesChecked = true;
        }
        if (activeEffects.isEmpty() || !Config.areShaderEffectsEnabled()) {
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
        for (PostProcessingEffect effect : activeEffects) {
            PostChain chain = getOrCreatePostChain(effect.shaderId);
            if (chain != null) {
                try {
                    float currentIntensity = effect.getCurrentIntensity();
                    if (currentIntensity > 0.001f) {
                        // Attempt to update Time uniform via reflection (workaround for PostChain limitation)
                        // Use animationTime for smooth frame-based animation
                        updatePostChainTimeUniform(chain, animationTime);
                        
                        chain.process(mc.getMainRenderTarget(), GraphicsResourceAllocator.UNPOOLED);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Failed to apply post-processing effect {}: {}", 
                        effect.shaderId, e.getMessage());
                }
            }
        }
    }
    
    /**
     * Ticks all active post-processing effects.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        activeEffects.removeIf(effect -> {
            effect.tick();
            return effect.isExpired();
        });
        
        // Preload post-processing shaders after resource reload to ensure ShaderManager discovers them
        if (needsShaderPreload) {
            needsShaderPreload = false;
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getShaderManager() != null) {
                LOGGER.debug("Preloading post-processing shaders after resource reload");
                // Try to load the shaders to ensure they're in ShaderManager's cache
                // This is done on the render thread after resources have reloaded
                try {
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
                } catch (Exception e) {
                    LOGGER.warn("Failed to preload post-processing shaders after resource reload: {}", e.getMessage());
                }
            }
        }
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
        activeEffects.clear();
    }
    
    /**
     * Checks the validity of registered render pipelines by precompiling them.
     * Logs results for debugging shader loading issues.
     */
    private static void checkPipelineValidity() {
        if (!Config.areShaderEffectsEnabled()) {
            return;
        }
        
        try {
            // Check lumos orb pipeline
            CompiledRenderPipeline lumosCompiled = RenderSystem.getDevice().precompilePipeline(LUMOS_ORB_PIPELINE);
            boolean lumosValid = lumosCompiled.isValid();
            compiledPipelines.put(LUMOS_ORB_PIPELINE, lumosCompiled);
            pipelineValidity.put(LUMOS_ORB_PIPELINE, lumosValid);
            
            if (lumosValid) {
                LOGGER.info("Lumos orb shader pipeline loaded and available");
            } else {
                LOGGER.warn("Lumos orb shader pipeline failed to load or compile");
            }
            
            // Check cut effect pipeline
            CompiledRenderPipeline cutCompiled = RenderSystem.getDevice().precompilePipeline(CUT_EFFECT_PIPELINE);
            boolean cutValid = cutCompiled.isValid();
            compiledPipelines.put(CUT_EFFECT_PIPELINE, cutCompiled);
            pipelineValidity.put(CUT_EFFECT_PIPELINE, cutValid);
            
            if (cutValid) {
                LOGGER.info("Cut effect shader pipeline loaded and available");
            } else {
                LOGGER.warn("Cut effect shader pipeline failed to load or compile");
            }
            
            // Check energy ball pipeline
            CompiledRenderPipeline energyBallCompiled = RenderSystem.getDevice().precompilePipeline(ENERGY_BALL_PIPELINE);
            boolean energyBallValid = energyBallCompiled.isValid();
            compiledPipelines.put(ENERGY_BALL_PIPELINE, energyBallCompiled);
            pipelineValidity.put(ENERGY_BALL_PIPELINE, energyBallValid);
            
            if (energyBallValid) {
                LOGGER.info("Energy ball shader pipeline loaded and available");
            } else {
                LOGGER.warn("Energy ball shader pipeline failed to load or compile");
            }

        } catch (Exception e) {
            LOGGER.error("Error checking pipeline validity: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Gets the compiled render pipeline for lumos orb shader.
     * 
     * @return The compiled pipeline, or null if not available
     */
    public static CompiledRenderPipeline getLumosOrbPipeline() {
        return compiledPipelines.get(LUMOS_ORB_PIPELINE);
    }
    
    /**
     * Gets the compiled render pipeline for cut effect shader.
     * 
     * @return The compiled pipeline, or null if not available
     */
    public static CompiledRenderPipeline getCutEffectPipeline() {
        return compiledPipelines.get(CUT_EFFECT_PIPELINE);
    }
    
    /**
     * Checks if a render pipeline is valid and available.
     * 
     * @param pipeline The pipeline to check
     * @return true if the pipeline is valid
     */
    public static boolean isPipelineValid(RenderPipeline pipeline) {
        Boolean valid = pipelineValidity.get(pipeline);
        return valid != null && valid;
    }
    
    /**
     * Gets the lumos orb render pipeline.
     * 
     * @return The render pipeline
     */
    public static RenderPipeline getLumosOrbRenderPipeline() {
        return LUMOS_ORB_PIPELINE;
    }
    
    /**
     * Gets the cut effect render pipeline.
     * 
     * @return The render pipeline
     */
    public static RenderPipeline getCutEffectRenderPipeline() {
        return CUT_EFFECT_PIPELINE;
    }
    
    public static RenderPipeline getEnergyBallRenderPipeline() {
        return ENERGY_BALL_PIPELINE;
    }
}













