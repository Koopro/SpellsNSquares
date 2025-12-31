package at.koopro.spells_n_squares.features.fx.client;

import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectIntegrationHandler;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Handles block shader registration commands.
 */
public final class BlockShaderCommandHandler {
    
    private BlockShaderCommandHandler() {}
    
    /**
     * Builds the block shader command tree.
     */
    public static LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> buildCommand() {
        return Commands.literal("block_shader")
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
            );
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
                var registry = ShaderCommandHandler.getShaderRegistry();
                ctx.getSource().sendFailure(Component.literal("Invalid shader name. Available: " + String.join(", ", registry.keySet())));
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
        ctx.getSource().sendSuccess(
            () -> Component.literal("Block shader effects list - use /spells_n_squaresdebug fx block_shader register <block> <shader> <intensity> [look|range]"),
            false
        );
        return 1;
    }
    
    /**
     * Parses a shader name to shader identifier.
     */
    private static Identifier parseShaderId(String shaderName) {
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
}

