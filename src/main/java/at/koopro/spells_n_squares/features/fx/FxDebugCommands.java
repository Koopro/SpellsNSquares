package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectIntegrationHandler;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * Client-side debug hooks for FX/shader testing.
 * Adds lightweight commands to trigger post-processing shader effects
 * with configurable intensity.
 *
 * Usage (integrated client):
 *   /spells_n_squaresdebug fx invert_test [intensity] - Temporary effect (30 ticks)
 *   /spells_n_squaresdebug fx grayscale_test [intensity] - Temporary effect (35 ticks)
 *   /spells_n_squaresdebug fx chromatic_test [intensity] - Temporary effect (25 ticks)
 *   /spells_n_squaresdebug fx sepia_test [intensity] - Temporary effect (30 ticks)
 *   /spells_n_squaresdebug fx mosaic_test [intensity] - Temporary effect (30 ticks)
 *   /spells_n_squaresdebug fx invert_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx invert_off - Toggle off
 *   /spells_n_squaresdebug fx grayscale_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx grayscale_off - Toggle off
 *   /spells_n_squaresdebug fx chromatic_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx chromatic_off - Toggle off
 *   /spells_n_squaresdebug fx sepia_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx sepia_off - Toggle off
 *   /spells_n_squaresdebug fx mosaic_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx mosaic_off - Toggle off
 *   /spells_n_squaresdebug fx tunnel_test [intensity] - Temporary effect (40 ticks)
 *   /spells_n_squaresdebug fx tunnel_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx tunnel_off - Toggle off
 *   /spells_n_squaresdebug fx fisheye_test [intensity] - Temporary effect (30 ticks)
 *   /spells_n_squaresdebug fx fisheye_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx fisheye_off - Toggle off
 *   /spells_n_squaresdebug fx polaroid_test [intensity] - Temporary effect (30 ticks)
 *   /spells_n_squaresdebug fx polaroid_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx polaroid_off - Toggle off
 *   /spells_n_squaresdebug fx retro_test [intensity] - Temporary effect (35 ticks)
 *   /spells_n_squaresdebug fx retro_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx retro_off - Toggle off
 *   /spells_n_squaresdebug fx black_and_white_test [intensity] - Temporary effect (35 ticks)
 *   /spells_n_squaresdebug fx black_and_white_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx black_and_white_off - Toggle off
 *   /spells_n_squaresdebug fx saturated_test [intensity] - Temporary effect (30 ticks)
 *   /spells_n_squaresdebug fx saturated_on [intensity] - Toggle on (persistent)
 *   /spells_n_squaresdebug fx saturated_off - Toggle off
 * Default intensity: 0.8
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class FxDebugCommands {
    
    /**
     * Map of shader names to their identifiers and trigger methods.
     */
    public static final java.util.Map<String, ShaderInfo> SHADER_REGISTRY = new java.util.HashMap<>();
    
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
    
    private static void registerShader(String name, Identifier shaderId, java.util.function.Consumer<Float> triggerMethod) {
        SHADER_REGISTRY.put(name, new ShaderInfo(shaderId, triggerMethod));
    }
    
    public record ShaderInfo(Identifier shaderId, java.util.function.Consumer<Float> triggerMethod) {}

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("spells_n_squaresdebug")
            .then(Commands.literal("fx")
                .then(Commands.literal("shader")
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
                    )
                )
                .then(Commands.literal("block_shader")
                    .then(Commands.literal("register")
                        .then(Commands.argument("block", StringArgumentType.string())
                            .then(Commands.argument("shader", StringArgumentType.string())
                                .then(Commands.argument("intensity", FloatArgumentType.floatArg(0.0F, 2.0F))
                                    .then(Commands.literal("look")
                                        .executes(ctx -> registerBlockShader(ctx, 
                                            StringArgumentType.getString(ctx, "block"),
                                            StringArgumentType.getString(ctx, "shader"),
                                            FloatArgumentType.getFloat(ctx, "intensity"),
                                            true, 0.0))
                                    )
                                    .then(Commands.argument("range", FloatArgumentType.floatArg(1.0F, 32.0F))
                                        .executes(ctx -> registerBlockShader(ctx,
                                            StringArgumentType.getString(ctx, "block"),
                                            StringArgumentType.getString(ctx, "shader"),
                                            FloatArgumentType.getFloat(ctx, "intensity"),
                                            false,
                                            FloatArgumentType.getFloat(ctx, "range")))
                                    )
                                )
                            )
                        )
                    )
                    .then(Commands.literal("unregister")
                        .then(Commands.argument("block", StringArgumentType.string())
                            .executes(ctx -> unregisterBlockShader(ctx, StringArgumentType.getString(ctx, "block")))
                        )
                    )
                    .then(Commands.literal("list")
                        .executes(ctx -> listBlockShaders(ctx))
                    )
                )
                .then(Commands.literal("place_energy_ball")
                    .executes(ctx -> placeEnergyBall(ctx))
                )
            );

        dispatcher.register(root);
    }
    
    /**
     * Generic handler for shader test command.
     */
    private static int triggerShaderTest(CommandContext<CommandSourceStack> ctx, String shaderName, float intensity) {
        CommandSourceStack source = ctx.getSource();
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
    
    /**
     * Registers a shader effect for a specific block.
     */
    private static int registerBlockShader(CommandContext<CommandSourceStack> ctx, 
                                          String blockIdStr, String shaderIdStr,
                                          float intensity, boolean lookAtOnly, double range) {
        try {
            Identifier blockId = Identifier.parse(blockIdStr);
            Identifier shaderId = parseShaderId(shaderIdStr);
            
            if (shaderId == null) {
                ctx.getSource().sendFailure(Component.literal("Invalid shader name. Available: " + String.join(", ", SHADER_REGISTRY.keySet())));
                return 0;
            }
            
            // Verify block exists by trying to get it
            if (!BuiltInRegistries.BLOCK.containsKey(blockId)) {
                ctx.getSource().sendFailure(Component.literal("Block not found: " + blockIdStr));
                return 0;
            }
            
            ShaderEffectIntegrationHandler.registerBlockShaderEffect(
                blockId, shaderId, intensity, lookAtOnly, range
            );
            
            String mode = lookAtOnly ? "when looking at" : "when within " + range + " blocks of";
            ctx.getSource().sendSuccess(
                () -> Component.literal("Registered shader effect for block " + blockIdStr + 
                    ": " + shaderIdStr + " (intensity=" + intensity + ", " + mode + ")"),
                false
            );
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * Unregisters a block shader effect.
     */
    private static int unregisterBlockShader(CommandContext<CommandSourceStack> ctx, String blockIdStr) {
        try {
            Identifier blockId = Identifier.parse(blockIdStr);
            ShaderEffectIntegrationHandler.unregisterBlockShaderEffect(blockId);
            ctx.getSource().sendSuccess(
                () -> Component.literal("Unregistered shader effect for block: " + blockIdStr),
                false
            );
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * Lists all registered block shader effects.
     */
    private static int listBlockShaders(CommandContext<CommandSourceStack> ctx) {
        // Note: This would require exposing the blockShaderEffects map or adding a getter
        ctx.getSource().sendSuccess(
            () -> Component.literal("Block shader effects list - use /spells_n_squaresdebug fx block_shader register <block> <shader> <intensity> [look|range]"),
            false
        );
        return 1;
    }
    
    /**
     * Parses a shader name to shader identifier.
     */
    public static Identifier parseShaderId(String shaderName) {
        return switch (shaderName.toLowerCase()) {
            case "invert", "inverted", "inverted_colors" -> PostProcessingManager.INVERTED_COLORS_POST_SHADER;
            case "grayscale", "grey", "gray" -> PostProcessingManager.GRAYSCALE_POST_SHADER;
            case "chromatic", "chromatic_aberration" -> PostProcessingManager.CHROMATIC_ABERRATION_POST_SHADER;
            case "sepia" -> PostProcessingManager.SEPIA_POST_SHADER;
            case "mosaic" -> PostProcessingManager.MOSAIC_POST_SHADER;
            case "tunnel" -> PostProcessingManager.TUNNEL_POST_SHADER;
            case "fisheye" -> PostProcessingManager.FISHEYE_POST_SHADER;
            case "polaroid" -> PostProcessingManager.POLAROID_POST_SHADER;
            case "retro", "vintage" -> PostProcessingManager.RETRO_POST_SHADER;
            case "black_and_white", "bw", "blackandwhite" -> PostProcessingManager.BLACK_AND_WHITE_POST_SHADER;
            case "saturated", "saturation" -> PostProcessingManager.SATURATED_POST_SHADER;
            case "glitch", "corruption" -> PostProcessingManager.GLITCH_POST_SHADER;
            case "kaleidoscope", "kaleido" -> PostProcessingManager.KALEIDOSCOPE_POST_SHADER;
            case "rgb_shift", "rgb" -> PostProcessingManager.RGB_SHIFT_POST_SHADER;
            case "wave_distortion", "wave", "ripple" -> PostProcessingManager.WAVE_DISTORTION_POST_SHADER;
            case "bloom", "glow" -> PostProcessingManager.BLOOM_POST_SHADER;
            case "edge_detection", "edge" -> PostProcessingManager.EDGE_DETECTION_POST_SHADER;
            case "pixelation", "pixel" -> PostProcessingManager.PIXELATION_POST_SHADER;
            case "heat_haze", "heat", "haze" -> PostProcessingManager.HEAT_HAZE_POST_SHADER;
            case "color_cycle", "cycle", "rainbow" -> PostProcessingManager.COLOR_CYCLE_POST_SHADER;
            case "mirror", "flip" -> PostProcessingManager.MIRROR_POST_SHADER;
            case "noise", "grain", "static" -> PostProcessingManager.NOISE_POST_SHADER;
            case "zoom_blur", "zoom", "radial_blur" -> PostProcessingManager.ZOOM_BLUR_POST_SHADER;
            case "underwater", "water" -> PostProcessingManager.UNDERWATER_POST_SHADER;
            case "drunk", "wobble" -> PostProcessingManager.DRUNK_POST_SHADER;
            case "matrix", "code_rain" -> PostProcessingManager.MATRIX_POST_SHADER;
            case "old_tv", "crt", "tv" -> PostProcessingManager.OLD_TV_POST_SHADER;
            case "xray", "x_ray" -> PostProcessingManager.XRAY_POST_SHADER;
            case "thermal", "thermal_vision", "heat_map" -> PostProcessingManager.THERMAL_POST_SHADER;
            case "cartoon", "cel_shade" -> PostProcessingManager.CARTOON_POST_SHADER;
            case "oil_painting", "oil", "painting" -> PostProcessingManager.OIL_PAINTING_POST_SHADER;
            case "old_film", "vintage_film" -> PostProcessingManager.OLD_FILM_POST_SHADER;
            case "acid_trip", "acid", "psychedelic" -> PostProcessingManager.ACID_TRIP_POST_SHADER;
            case "outline", "highlight" -> PostProcessingManager.OUTLINE_POST_SHADER;
            case "fog", "atmospheric" -> PostProcessingManager.FOG_POST_SHADER;
            case "sharpen", "sharp" -> PostProcessingManager.SHARPEN_POST_SHADER;
            case "motion_blur", "motion" -> PostProcessingManager.MOTION_BLUR_POST_SHADER;
            case "depth_of_field", "dof", "focus" -> PostProcessingManager.DEPTH_OF_FIELD_POST_SHADER;
            case "lens_flare", "flare", "glare" -> PostProcessingManager.LENS_FLARE_POST_SHADER;
            case "vignette", "dark_edges" -> PostProcessingManager.VIGNETTE_POST_SHADER;
            case "contrast_boost", "contrast" -> PostProcessingManager.CONTRAST_BOOST_POST_SHADER;
            case "pulsing_glow", "pulse_glow", "glow_pulse" -> PostProcessingManager.PULSING_GLOW_POST_SHADER;
            case "scrolling_stripes", "stripes", "scrolling" -> PostProcessingManager.SCROLLING_STRIPES_POST_SHADER;
            case "warping_vortex", "vortex", "warp" -> PostProcessingManager.WARPING_VORTEX_POST_SHADER;
            case "particle_rain", "particles", "rain" -> PostProcessingManager.PARTICLE_RAIN_POST_SHADER;
            case "color_wave", "wave_color" -> PostProcessingManager.COLOR_WAVE_POST_SHADER;
            case "ripple_effect", "ripples" -> PostProcessingManager.RIPPLE_EFFECT_POST_SHADER;
            case "breathing_vignette", "breathing" -> PostProcessingManager.BREATHING_VIGNETTE_POST_SHADER;
            case "animated_noise", "moving_noise" -> PostProcessingManager.ANIMATED_NOISE_POST_SHADER;
            case "rotating_kaleidoscope", "kaleidoscope_rotate" -> PostProcessingManager.ROTATING_KALEIDOSCOPE_POST_SHADER;
            case "energy_pulse", "energy_wave" -> PostProcessingManager.ENERGY_PULSE_POST_SHADER;
            default -> null;
        };
    }
    
    /**
     * Places an energy ball block at the player's target position.
     * For client commands in single-player, accesses the integrated server.
     */
    private static int placeEnergyBall(CommandContext<CommandSourceStack> ctx) {
        try {
            // Get player from Minecraft client instance (for client commands)
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc == null || mc.player == null) {
                ctx.getSource().sendFailure(Component.literal("No player available"));
                return 0;
            }
            
            var player = mc.player;
            
            // Get block position from player's line of sight
            var hitResult = player.pick(32.0, 1.0f, false);
            if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
                ctx.getSource().sendFailure(Component.literal("No block in range to place energy ball"));
                return 0;
            }
            
            var blockHit = (net.minecraft.world.phys.BlockHitResult) hitResult;
            var targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
            
            // Get the server level (works in single-player integrated server)
            var level = player.level();
            if (level.isClientSide()) {
                // In single-player, get the integrated server's level
                if (mc.getSingleplayerServer() != null) {
                    level = mc.getSingleplayerServer().getLevel(level.dimension());
                } else {
                    ctx.getSource().sendFailure(Component.literal("Cannot place blocks in multiplayer from client command"));
                    return 0;
                }
            }
            
            if (level == null) {
                ctx.getSource().sendFailure(Component.literal("Could not access server level"));
                return 0;
            }
            
            // Check if position is valid
            if (!level.getBlockState(targetPos).isAir()) {
                ctx.getSource().sendFailure(Component.literal("Target position is not empty"));
                return 0;
            }
            
            // Place the energy ball block
            var block = at.koopro.spells_n_squares.features.fx.FxRegistry.ENERGY_BALL.value();
            level.setBlock(targetPos, block.defaultBlockState(), 3);
            
            ctx.getSource().sendSuccess(
                () -> Component.literal("Placed energy ball at " + targetPos.toShortString()),
                true
            );
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
}




