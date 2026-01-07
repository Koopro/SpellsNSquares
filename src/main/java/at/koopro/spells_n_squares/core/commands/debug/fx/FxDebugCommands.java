package at.koopro.spells_n_squares.core.commands.debug.fx;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Debug commands for FX/shader operations.
 * These commands work in integrated server (single-player) context only.
 */
public final class FxDebugCommands {
    
    private FxDebugCommands() {}
    
    /**
     * Builds the fx command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> fx = Commands.literal("fx");
        
        fx.then(buildShaderCommands())
          .then(buildBlockShaderCommands())
          .then(buildPlaceEnergyBallCommand());
        
        return fx;
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> buildShaderCommands() {
        LiteralArgumentBuilder<CommandSourceStack> shader = Commands.literal("shader");
        
        java.util.Map<String, at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo> shaderRegistry = 
            getShaderRegistry();
        
        shader.then(Commands.argument("shader_name", StringArgumentType.string())
            .suggests((ctx, builder) -> {
                for (String name : shaderRegistry.keySet()) {
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
            ));
        
        return shader;
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> buildBlockShaderCommands() {
        LiteralArgumentBuilder<CommandSourceStack> blockShader = Commands.literal("block_shader");
        
        blockShader.then(Commands.literal("register")
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
            .executes(FxDebugCommands::listBlockShaders)
        );
        
        return blockShader;
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> buildPlaceEnergyBallCommand() {
        return Commands.literal("place_energy_ball")
            .executes(FxDebugCommands::placeEnergyBall);
    }
    
    private static java.util.Map<String, at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo> getShaderRegistry() {
        return at.koopro.spells_n_squares.features.fx.FxDebugCommands.SHADER_REGISTRY;
    }
    
    private static int triggerShaderTest(CommandContext<CommandSourceStack> ctx, String shaderName, float intensity) {
        CommandSourceStack source = ctx.getSource();
        java.util.Map<String, at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo> shaderRegistry = getShaderRegistry();
        at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo info = shaderRegistry.get(shaderName.toLowerCase());
        
        if (info == null) {
            source.sendFailure(Component.literal("Unknown shader: " + shaderName + ". Available shaders: " + String.join(", ", shaderRegistry.keySet())));
            return 0;
        }
        
        if (source.getServer() != null && !source.getServer().isDedicatedServer()) {
            float clamped = Math.max(0.0F, intensity);
            info.triggerMethod().accept(clamped);
            source.sendSuccess(
                () -> Component.literal("Triggered " + shaderName + " effect (intensity=" + clamped + ")"),
                false
            );
            return 1;
        } else {
            source.sendFailure(Component.literal("FX commands can only be used in single-player (integrated server)"));
            return 0;
        }
    }
    
    private static int toggleShaderOn(CommandContext<CommandSourceStack> ctx, String shaderName, float intensity) {
        CommandSourceStack source = ctx.getSource();
        java.util.Map<String, at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo> shaderRegistry = getShaderRegistry();
        at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo info = shaderRegistry.get(shaderName.toLowerCase());
        
        if (info == null) {
            source.sendFailure(Component.literal("Unknown shader: " + shaderName + ". Available shaders: " + String.join(", ", shaderRegistry.keySet())));
            return 0;
        }
        
        if (source.getServer() != null && !source.getServer().isDedicatedServer()) {
            float clamped = Math.max(0.0F, intensity);
            at.koopro.spells_n_squares.features.fx.system.PostProcessingManager.removeEffect(info.shaderId());
            at.koopro.spells_n_squares.features.fx.system.PostProcessingManager.addPersistentEffect(info.shaderId(), clamped);
            
            source.sendSuccess(
                () -> Component.literal(shaderName + " effect ON (intensity=" + clamped + ")"),
                false
            );
            return 1;
        } else {
            source.sendFailure(Component.literal("FX commands can only be used in single-player (integrated server)"));
            return 0;
        }
    }
    
    private static int toggleShaderOff(CommandContext<CommandSourceStack> ctx, String shaderName) {
        CommandSourceStack source = ctx.getSource();
        java.util.Map<String, at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo> shaderRegistry = getShaderRegistry();
        at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo info = shaderRegistry.get(shaderName.toLowerCase());
        
        if (info == null) {
            source.sendFailure(Component.literal("Unknown shader: " + shaderName + ". Available shaders: " + String.join(", ", shaderRegistry.keySet())));
            return 0;
        }
        
        if (source.getServer() != null && !source.getServer().isDedicatedServer()) {
            boolean removed = at.koopro.spells_n_squares.features.fx.system.PostProcessingManager.removeEffect(info.shaderId());
            
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
        } else {
            source.sendFailure(Component.literal("FX commands can only be used in single-player (integrated server)"));
            return 0;
        }
    }
    
    private static int registerBlockShader(CommandContext<CommandSourceStack> ctx, 
                                          String blockIdStr, String shaderIdStr,
                                          float intensity, boolean lookAtOnly, double range) {
        try {
            Identifier blockId = Identifier.parse(blockIdStr);
            Identifier shaderId = parseShaderId(shaderIdStr);
            
            if (shaderId == null) {
                java.util.Map<String, at.koopro.spells_n_squares.features.fx.FxDebugCommands.ShaderInfo> shaderRegistry = getShaderRegistry();
                ctx.getSource().sendFailure(Component.literal("Invalid shader name. Available: " + String.join(", ", shaderRegistry.keySet())));
                return 0;
            }
            
            if (!BuiltInRegistries.BLOCK.containsKey(blockId)) {
                ctx.getSource().sendFailure(Component.literal("Block not found: " + blockIdStr));
                return 0;
            }
            
            if (ctx.getSource().getServer() != null && !ctx.getSource().getServer().isDedicatedServer()) {
                at.koopro.spells_n_squares.features.fx.handler.ShaderEffectIntegrationHandler.registerBlockShaderEffect(
                    blockId, shaderId, intensity, lookAtOnly, range
                );
                
                String mode = lookAtOnly ? "when looking at" : "when within " + range + " blocks of";
                ctx.getSource().sendSuccess(
                    () -> Component.literal("Registered shader effect for block " + blockIdStr + 
                        ": " + shaderIdStr + " (intensity=" + intensity + ", " + mode + ")"),
                    false
                );
                return 1;
            } else {
                ctx.getSource().sendFailure(Component.literal("FX commands can only be used in single-player (integrated server)"));
                return 0;
            }
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int unregisterBlockShader(CommandContext<CommandSourceStack> ctx, String blockIdStr) {
        try {
            Identifier blockId = Identifier.parse(blockIdStr);
            
            if (ctx.getSource().getServer() != null && !ctx.getSource().getServer().isDedicatedServer()) {
                at.koopro.spells_n_squares.features.fx.handler.ShaderEffectIntegrationHandler.unregisterBlockShaderEffect(blockId);
                ctx.getSource().sendSuccess(
                    () -> Component.literal("Unregistered shader effect for block: " + blockIdStr),
                    false
                );
                return 1;
            } else {
                ctx.getSource().sendFailure(Component.literal("FX commands can only be used in single-player (integrated server)"));
                return 0;
            }
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int listBlockShaders(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(
            () -> Component.literal("Block shader effects list - use /sns debug fx block_shader register <block> <shader> <intensity> [look|range]"),
            false
        );
        return 1;
    }
    
    private static Identifier parseShaderId(String shaderName) {
        return at.koopro.spells_n_squares.features.fx.FxDebugCommands.parseShaderId(shaderName);
    }
    
    private static int placeEnergyBall(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            
            var hitResult = player.pick(32.0, 1.0f, false);
            if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
                ctx.getSource().sendFailure(Component.literal("No block in range to place energy ball"));
                return 0;
            }
            
            var blockHit = (net.minecraft.world.phys.BlockHitResult) hitResult;
            var targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
            
            Level level = player.level();
            if (level.isClientSide()) {
                ctx.getSource().sendFailure(Component.literal("Cannot place blocks from client"));
                return 0;
            }
            
            if (!(level instanceof ServerLevel serverLevel)) {
                ctx.getSource().sendFailure(Component.literal("Could not access server level"));
                return 0;
            }
            
            if (!serverLevel.getBlockState(targetPos).isAir()) {
                ctx.getSource().sendFailure(Component.literal("Target position is not empty"));
                return 0;
            }
            
            var block = at.koopro.spells_n_squares.features.fx.FxRegistry.ENERGY_BALL.value();
            serverLevel.setBlock(targetPos, block.defaultBlockState(), 3);
            
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

