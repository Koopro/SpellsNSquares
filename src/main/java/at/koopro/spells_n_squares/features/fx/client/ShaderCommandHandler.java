package at.koopro.spells_n_squares.features.fx.client;

import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Handles shader-related debug commands (test, on, off).
 */
public final class ShaderCommandHandler {
    private static final Map<String, ShaderInfo> SHADER_REGISTRY = new HashMap<>();
    
    static {
        // Register all shaders
        registerShader("invert", PostProcessingManager.INVERTED_COLORS_POST_SHADER, ShaderEffectHandler::triggerInvertedColors);
        registerShader("grayscale", PostProcessingManager.GRAYSCALE_POST_SHADER, ShaderEffectHandler::triggerGrayscale);
        registerShader("chromatic", PostProcessingManager.CHROMATIC_ABERRATION_POST_SHADER, ShaderEffectHandler::triggerChromaticAberration);
        registerShader("sepia", PostProcessingManager.SEPIA_POST_SHADER, ShaderEffectHandler::triggerSepia);
        registerShader("mosaic", PostProcessingManager.MOSAIC_POST_SHADER, ShaderEffectHandler::triggerMosaic);
        registerShader("tunnel", PostProcessingManager.TUNNEL_POST_SHADER, ShaderEffectHandler::triggerTunnel);
        registerShader("fisheye", PostProcessingManager.FISHEYE_POST_SHADER, ShaderEffectHandler::triggerFisheye);
        registerShader("polaroid", PostProcessingManager.POLAROID_POST_SHADER, ShaderEffectHandler::triggerPolaroid);
        registerShader("retro", PostProcessingManager.RETRO_POST_SHADER, ShaderEffectHandler::triggerRetro);
        registerShader("black_and_white", PostProcessingManager.BLACK_AND_WHITE_POST_SHADER, ShaderEffectHandler::triggerBlackAndWhite);
        registerShader("saturated", PostProcessingManager.SATURATED_POST_SHADER, ShaderEffectHandler::triggerSaturated);
        registerShader("glitch", PostProcessingManager.GLITCH_POST_SHADER, ShaderEffectHandler::triggerGlitch);
        registerShader("kaleidoscope", PostProcessingManager.KALEIDOSCOPE_POST_SHADER, ShaderEffectHandler::triggerKaleidoscope);
        registerShader("rgb_shift", PostProcessingManager.RGB_SHIFT_POST_SHADER, ShaderEffectHandler::triggerRgbShift);
        registerShader("wave_distortion", PostProcessingManager.WAVE_DISTORTION_POST_SHADER, ShaderEffectHandler::triggerWaveDistortion);
        registerShader("bloom", PostProcessingManager.BLOOM_POST_SHADER, ShaderEffectHandler::triggerBloom);
        registerShader("edge_detection", PostProcessingManager.EDGE_DETECTION_POST_SHADER, ShaderEffectHandler::triggerEdgeDetection);
        registerShader("pixelation", PostProcessingManager.PIXELATION_POST_SHADER, ShaderEffectHandler::triggerPixelation);
        registerShader("heat_haze", PostProcessingManager.HEAT_HAZE_POST_SHADER, ShaderEffectHandler::triggerHeatHaze);
        registerShader("color_cycle", PostProcessingManager.COLOR_CYCLE_POST_SHADER, ShaderEffectHandler::triggerColorCycle);
        registerShader("mirror", PostProcessingManager.MIRROR_POST_SHADER, ShaderEffectHandler::triggerMirror);
        registerShader("noise", PostProcessingManager.NOISE_POST_SHADER, ShaderEffectHandler::triggerNoise);
        registerShader("zoom_blur", PostProcessingManager.ZOOM_BLUR_POST_SHADER, ShaderEffectHandler::triggerZoomBlur);
        registerShader("underwater", PostProcessingManager.UNDERWATER_POST_SHADER, ShaderEffectHandler::triggerUnderwater);
        registerShader("drunk", PostProcessingManager.DRUNK_POST_SHADER, ShaderEffectHandler::triggerDrunk);
        registerShader("matrix", PostProcessingManager.MATRIX_POST_SHADER, ShaderEffectHandler::triggerMatrix);
        registerShader("old_tv", PostProcessingManager.OLD_TV_POST_SHADER, ShaderEffectHandler::triggerOldTv);
        registerShader("xray", PostProcessingManager.XRAY_POST_SHADER, ShaderEffectHandler::triggerXray);
        registerShader("thermal", PostProcessingManager.THERMAL_POST_SHADER, ShaderEffectHandler::triggerThermal);
        registerShader("cartoon", PostProcessingManager.CARTOON_POST_SHADER, ShaderEffectHandler::triggerCartoon);
        registerShader("oil_painting", PostProcessingManager.OIL_PAINTING_POST_SHADER, ShaderEffectHandler::triggerOilPainting);
        registerShader("old_film", PostProcessingManager.OLD_FILM_POST_SHADER, ShaderEffectHandler::triggerOldFilm);
        registerShader("acid_trip", PostProcessingManager.ACID_TRIP_POST_SHADER, ShaderEffectHandler::triggerAcidTrip);
        registerShader("outline", PostProcessingManager.OUTLINE_POST_SHADER, ShaderEffectHandler::triggerOutline);
        registerShader("fog", PostProcessingManager.FOG_POST_SHADER, ShaderEffectHandler::triggerFog);
        registerShader("sharpen", PostProcessingManager.SHARPEN_POST_SHADER, ShaderEffectHandler::triggerSharpen);
        registerShader("motion_blur", PostProcessingManager.MOTION_BLUR_POST_SHADER, ShaderEffectHandler::triggerMotionBlur);
        registerShader("depth_of_field", PostProcessingManager.DEPTH_OF_FIELD_POST_SHADER, ShaderEffectHandler::triggerDepthOfField);
        registerShader("lens_flare", PostProcessingManager.LENS_FLARE_POST_SHADER, ShaderEffectHandler::triggerLensFlare);
        registerShader("vignette", PostProcessingManager.VIGNETTE_POST_SHADER, ShaderEffectHandler::triggerVignette);
        registerShader("contrast_boost", PostProcessingManager.CONTRAST_BOOST_POST_SHADER, ShaderEffectHandler::triggerContrastBoost);
        registerShader("pulsing_glow", PostProcessingManager.PULSING_GLOW_POST_SHADER, ShaderEffectHandler::triggerPulsingGlow);
        registerShader("scrolling_stripes", PostProcessingManager.SCROLLING_STRIPES_POST_SHADER, ShaderEffectHandler::triggerScrollingStripes);
        registerShader("warping_vortex", PostProcessingManager.WARPING_VORTEX_POST_SHADER, ShaderEffectHandler::triggerWarpingVortex);
        registerShader("particle_rain", PostProcessingManager.PARTICLE_RAIN_POST_SHADER, ShaderEffectHandler::triggerParticleRain);
        registerShader("color_wave", PostProcessingManager.COLOR_WAVE_POST_SHADER, ShaderEffectHandler::triggerColorWave);
        registerShader("ripple_effect", PostProcessingManager.RIPPLE_EFFECT_POST_SHADER, ShaderEffectHandler::triggerRippleEffect);
        registerShader("breathing_vignette", PostProcessingManager.BREATHING_VIGNETTE_POST_SHADER, ShaderEffectHandler::triggerBreathingVignette);
        registerShader("animated_noise", PostProcessingManager.ANIMATED_NOISE_POST_SHADER, ShaderEffectHandler::triggerAnimatedNoise);
        registerShader("rotating_kaleidoscope", PostProcessingManager.ROTATING_KALEIDOSCOPE_POST_SHADER, ShaderEffectHandler::triggerRotatingKaleidoscope);
        registerShader("energy_pulse", PostProcessingManager.ENERGY_PULSE_POST_SHADER, ShaderEffectHandler::triggerEnergyPulse);
    }
    
    private ShaderCommandHandler() {}
    
    private static void registerShader(String name, Identifier shaderId, Consumer<Float> triggerMethod) {
        SHADER_REGISTRY.put(name, new ShaderInfo(shaderId, triggerMethod));
    }
    
    private record ShaderInfo(Identifier shaderId, Consumer<Float> triggerMethod) {}
    
    /**
     * Builds the shader command tree.
     */
    public static LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> buildCommand() {
        return Commands.literal("shader")
            .then(Commands.argument("shader_name", StringArgumentType.string())
                .suggests((ctx, builder) -> {
                    for (String name : SHADER_REGISTRY.keySet()) {
                        builder.suggest(name);
                    }
                    return builder.buildFuture();
                })
                .then(Commands.literal("test")
                    .then(Commands.argument("intensity", FloatArgumentType.floatArg(0.0F, 2.0F))
                        .executes(ctx -> triggerShaderTest(ctx, StringArgumentType.getString(ctx, "shader_name"), FloatArgumentType.getFloat(ctx, "intensity"))))
                    .executes(ctx -> triggerShaderTest(ctx, StringArgumentType.getString(ctx, "shader_name"), 0.8F))
                )
                .then(Commands.literal("on")
                    .then(Commands.argument("intensity", FloatArgumentType.floatArg(0.0F, 2.0F))
                        .executes(ctx -> toggleShaderOn(ctx, StringArgumentType.getString(ctx, "shader_name"), FloatArgumentType.getFloat(ctx, "intensity"))))
                    .executes(ctx -> toggleShaderOn(ctx, StringArgumentType.getString(ctx, "shader_name"), 0.8F))
                )
                .then(Commands.literal("off")
                    .executes(ctx -> toggleShaderOff(ctx, StringArgumentType.getString(ctx, "shader_name")))
                )
            );
    }
    
    /**
     * Gets the shader registry for use by other handlers.
     */
    public static Map<String, ShaderInfo> getShaderRegistry() {
        return SHADER_REGISTRY;
    }
    
    /**
     * Generic handler for shader test command.
     */
    private static int triggerShaderTest(CommandContext<CommandSourceStack> ctx, String shaderName, float intensity) {
        CommandSourceStack source = ctx.getSource();
        // Use lowercase for case-insensitive lookup (registry keys are already lowercase)
        ShaderInfo info = SHADER_REGISTRY.get(shaderName.toLowerCase());
        
        if (info == null) {
            source.sendFailure(Component.literal("Unknown shader: " + shaderName + ". Available shaders: " + String.join(", ", SHADER_REGISTRY.keySet())));
            return 0;
        }
        
        float clamped = Math.max(0.0F, intensity);
        info.triggerMethod().accept(clamped);
        source.sendSuccess(
            () -> Component.literal("Triggered " + shaderName + " effect (intensity=" + clamped + ")"),
            false
        );
        return 1;
    }
    
    /**
     * Generic handler for shader on command.
     */
    private static int toggleShaderOn(CommandContext<CommandSourceStack> ctx, String shaderName, float intensity) {
        CommandSourceStack source = ctx.getSource();
        // Use lowercase for case-insensitive lookup (registry keys are already lowercase)
        ShaderInfo info = SHADER_REGISTRY.get(shaderName.toLowerCase());
        
        if (info == null) {
            source.sendFailure(Component.literal("Unknown shader: " + shaderName + ". Available shaders: " + String.join(", ", SHADER_REGISTRY.keySet())));
            return 0;
        }
        
        float clamped = Math.max(0.0F, intensity);
        PostProcessingManager.removeEffect(info.shaderId());
        PostProcessingManager.addPersistentEffect(info.shaderId(), clamped);
        
        source.sendSuccess(
            () -> Component.literal(shaderName + " effect ON (intensity=" + clamped + ")"),
            false
        );
        return 1;
    }
    
    /**
     * Generic handler for shader off command.
     */
    private static int toggleShaderOff(CommandContext<CommandSourceStack> ctx, String shaderName) {
        CommandSourceStack source = ctx.getSource();
        ShaderInfo info = SHADER_REGISTRY.get(shaderName.toLowerCase());
        
        if (info == null) {
            source.sendFailure(Component.literal("Unknown shader: " + shaderName + ". Available shaders: " + String.join(", ", SHADER_REGISTRY.keySet())));
            return 0;
        }
        
        boolean removed = PostProcessingManager.removeEffect(info.shaderId());
        
        if (removed) {
            source.sendSuccess(
                () -> Component.literal(shaderName + " effect OFF"),
                false
            );
        } else {
            source.sendSuccess(
                () -> Component.literal(shaderName + " effect was already OFF"),
                false
            );
        }
        return 1;
    }
}

