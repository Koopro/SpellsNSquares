package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.slf4j.Logger;

import java.io.IOException;
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
    
    // Post-processing shader identifiers
    public static final Identifier INVERTED_COLORS_POST_SHADER = 
        ModIdentifierHelper.modId("shaders/post/inverted_colors");
    public static final Identifier GRAYSCALE_POST_SHADER = 
        ModIdentifierHelper.modId("shaders/post/grayscale");
    
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
        int duration;
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
            return age >= duration;
        }
        
        float getCurrentIntensity() {
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
        
        // Create new PostChain lazily
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getMainRenderTarget() == null || mc.getResourceManager() == null) {
            return null;
        }
        
        try {
            // PostChain has a private constructor: PostChain(List, Map, Set, CachedOrthoProjectionMatrixBuffer)
            // The API has changed - PostChain must be loaded from JSON using internal parsing
            // Try to find a static factory method or use reflection to call a method that loads from JSON
            ResourceProvider resourceProvider = mc.getResourceManager();
            PostChain chain = null;
            
            // Try to find static methods that might load PostChain from JSON
            java.lang.reflect.Method[] allMethods = PostChain.class.getDeclaredMethods();
            for (java.lang.reflect.Method method : allMethods) {
                if (java.lang.reflect.Modifier.isStatic(method.getModifiers()) && 
                    PostChain.class.isAssignableFrom(method.getReturnType())) {
                    java.lang.Class<?>[] paramTypes = method.getParameterTypes();
                    // Look for methods that take TextureManager, ResourceProvider, and Identifier/ResourceLocation
                    if (paramTypes.length >= 3) {
                        try {
                            method.setAccessible(true);
                            if (paramTypes.length == 3) {
                                chain = (PostChain) method.invoke(null, 
                                    mc.getTextureManager(), 
                                    resourceProvider, 
                                    shaderId);
                            } else if (paramTypes.length == 4 && 
                                paramTypes[3] == com.mojang.blaze3d.pipeline.RenderTarget.class) {
                                chain = (PostChain) method.invoke(null, 
                                    mc.getTextureManager(), 
                                    resourceProvider, 
                                    mc.getMainRenderTarget(),
                                    shaderId);
                            }
                            if (chain != null) {
                                LOGGER.debug("Successfully created PostChain for shader: {} using static method {}", shaderId, method.getName());
                                break;
                            }
                        } catch (Exception e) {
                            // Continue trying other methods
                        }
                    }
                }
            }
            
            // If no static method found, PostChain API has changed significantly
            // Post-processing shaders may need to use RenderPipeline instead
            if (chain == null) {
                LOGGER.warn("PostChain cannot be created for {}. The API has changed - PostChain now uses a private constructor. " +
                    "Post-processing shaders (grayscale, inverted colors) are temporarily unavailable. " +
                    "Consider using RenderPipeline for post-processing effects instead.", shaderId);
                return null;
            }
            
            if (chain != null) {
                postChains.put(shaderId, chain);
            }
            return chain;
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
     * @param duration Duration in ticks
     */
    public static void addEffect(Identifier shaderId, float intensity, int duration) {
        if (Config.getScreenEffectIntensity() <= 0.0) {
            return;
        }
        
        float adjustedIntensity = (float) (intensity * Config.getScreenEffectIntensity());
        activeEffects.add(new PostProcessingEffect(shaderId, adjustedIntensity, duration));
    }
    
    /**
     * Registers render pipelines for custom shader resources.
     * Uses NeoForge's RegisterRenderPipelinesEvent to register custom shader pipelines.
     */
    @SubscribeEvent
    public static void registerRenderPipelines(final RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LUMOS_ORB_PIPELINE);
        event.registerPipeline(CUT_EFFECT_PIPELINE);
        LOGGER.debug("Registered render pipelines for custom shader resources");
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
        
        // Apply each active effect
        for (PostProcessingEffect effect : activeEffects) {
            PostChain chain = getOrCreatePostChain(effect.shaderId);
            if (chain != null) {
                try {
                    float currentIntensity = effect.getCurrentIntensity();
                    if (currentIntensity > 0.001f) {
                        // Process the post-processing chain
                        // Note: Uniform setting would need to be done via shader manager if needed
                        // For now, using basic process() - intensity is set via JSON defaults
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
        PostChain chain = getOrCreatePostChain(shaderId);
        return chain != null;
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
}






