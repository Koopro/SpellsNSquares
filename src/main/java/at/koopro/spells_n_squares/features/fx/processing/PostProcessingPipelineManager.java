package at.koopro.spells_n_squares.features.fx.processing;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages render pipelines for custom shader resources.
 * Handles pipeline registration, compilation, and validation.
 */
public final class PostProcessingPipelineManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    
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
    
    private PostProcessingPipelineManager() {}
    
    /**
     * Registers render pipelines for custom shader resources.
     * Uses NeoForge's RegisterRenderPipelinesEvent to register custom shader pipelines.
     */
    static void registerRenderPipelines(final RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LUMOS_ORB_PIPELINE);
        event.registerPipeline(CUT_EFFECT_PIPELINE);
        event.registerPipeline(ENERGY_BALL_PIPELINE);
        LOGGER.debug("Registered render pipelines for custom shader resources");
    }
    
    /**
     * Checks the validity of registered render pipelines by precompiling them.
     * Logs results for debugging shader loading issues.
     */
    static void checkPipelineValidity() {
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
    static CompiledRenderPipeline getLumosOrbPipeline() {
        return compiledPipelines.get(LUMOS_ORB_PIPELINE);
    }
    
    /**
     * Gets the compiled render pipeline for cut effect shader.
     * 
     * @return The compiled pipeline, or null if not available
     */
    static CompiledRenderPipeline getCutEffectPipeline() {
        return compiledPipelines.get(CUT_EFFECT_PIPELINE);
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
    
    /**
     * Gets the energy ball render pipeline.
     * 
     * @return The render pipeline
     */
    public static RenderPipeline getEnergyBallRenderPipeline() {
        return ENERGY_BALL_PIPELINE;
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
     * Checks if pipelines have been validated.
     * 
     * @return true if pipelines have been checked
     */
    public static boolean arePipelinesChecked() {
        return pipelinesChecked;
    }
    
    /**
     * Marks pipelines as checked.
     */
    public static void setPipelinesChecked(boolean checked) {
        pipelinesChecked = checked;
    }
}

