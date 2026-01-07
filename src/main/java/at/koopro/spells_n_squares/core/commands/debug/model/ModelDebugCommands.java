package at.koopro.spells_n_squares.core.commands.debug.model;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.core.util.player.PlayerModelUtils;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Debug commands for player model operations.
 */
public final class ModelDebugCommands {
    
    private ModelDebugCommands() {}
    
    /**
     * Builds the model command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> model = Commands.literal("model");
        
        model.then(Commands.literal("scale")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelScale))))
        .then(Commands.literal("head")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelHead))))
        .then(Commands.literal("body")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelBody))))
        .then(Commands.literal("leftarm")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelLeftArm))))
        .then(Commands.literal("rightarm")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelRightArm))))
        .then(Commands.literal("leftleg")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelLeftLeg))))
        .then(Commands.literal("rightleg")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelRightLeg))))
        .then(Commands.literal("hitbox")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(ModelDebugCommands::modelHitbox))))
        .then(Commands.literal("reset")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ModelDebugCommands::modelReset)))
        .then(Commands.literal("info")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ModelDebugCommands::modelInfo)));
        
        return model;
    }
    
    private static int modelScale(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setPlayerScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelHead(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setHeadScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " head scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelBody(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setBodyScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " body scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelLeftArm(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setLeftArmScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " left arm scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelRightArm(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setRightArmScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " right arm scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelLeftLeg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setLeftLegScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " left leg scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelRightLeg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setRightLegScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " right leg scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelHitbox(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelUtils.setHitboxScale(player, scale);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set " + player.getName().getString() + " hitbox scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelReset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        PlayerModelUtils.resetPlayerModel(player);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Reset " + player.getName().getString() + " model to default", 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int modelInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        PlayerModelDataComponent.PlayerModelData data = PlayerModelUtils.getModelData(player);
        Component message = Component.literal(
            "Model scales for " + player.getName().getString() + ":\n" +
            "  Overall: " + data.scale() + "\n" +
            "  Head: " + data.headScale() + "\n" +
            "  Body: " + data.bodyScale() + "\n" +
            "  Left Arm: " + data.leftArmScale() + "\n" +
            "  Right Arm: " + data.rightArmScale() + "\n" +
            "  Left Leg: " + data.leftLegScale() + "\n" +
            "  Right Leg: " + data.rightLegScale() + "\n" +
            "  Hitbox: " + data.hitboxScale()
        );
        ctx.getSource().sendSuccess(() -> message, false);
        return 1;
    }
}


