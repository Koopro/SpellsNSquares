package at.koopro.spells_n_squares.core.commands.debug.identity;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.data.PlayerIdentityData;
import at.koopro.spells_n_squares.core.data.PlayerIdentityHelper;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;


/**
 * Debug commands for player identity (blood status and magical type).
 * Commands:
 * - /sns debug identity get [player] - Get player's identity
 * - /sns debug identity set <player> <bloodStatus> <magicalType> - Set player's identity
 * - /sns debug identity reset <player> - Reset player's identity to default
 * - /sns debug identity list - List all available blood statuses and magical types
 */
public final class IdentityDebugCommands {
    
    private IdentityDebugCommands() {}
    
    /**
     * Builds the identity command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> identity = Commands.literal("identity");
        
        // /sns debug identity get [player]
        identity.then(Commands.literal("get")
            .executes(ctx -> getIdentity(ctx, null))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> getIdentity(ctx, EntityArgument.getPlayer(ctx, "player")))));
        
        // /sns debug identity set <player> <bloodStatus> <magicalType>
        identity.then(Commands.literal("set")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("bloodStatus", StringArgumentType.string())
                    .suggests((ctx, builder) -> {
                        for (PlayerIdentityData.BloodStatus status : PlayerIdentityData.BloodStatus.values()) {
                            builder.suggest(status.name());
                        }
                        return builder.buildFuture();
                    })
                    .then(Commands.argument("magicalType", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            for (PlayerIdentityData.MagicalType type : PlayerIdentityData.MagicalType.values()) {
                                builder.suggest(type.name());
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> setIdentity(ctx, EntityArgument.getPlayer(ctx, "player"),
                            StringArgumentType.getString(ctx, "bloodStatus"),
                            StringArgumentType.getString(ctx, "magicalType")))))));
        
        // /sns debug identity reset <player>
        identity.then(Commands.literal("reset")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> resetIdentity(ctx, EntityArgument.getPlayer(ctx, "player")))));
        
        // /sns debug identity list
        identity.then(Commands.literal("list")
            .executes(IdentityDebugCommands::listAll));
        
        return identity;
    }
    
    private static int getIdentity(CommandContext<CommandSourceStack> ctx, ServerPlayer targetPlayer) {
        try {
            ServerPlayer player = targetPlayer != null ? targetPlayer : ctx.getSource().getPlayerOrException();
            
            var identity = PlayerDataHelper.getIdentityData(player);
            if (identity == null) {
                ctx.getSource().sendFailure(ColorUtils.coloredText("No identity data found for player", ColorUtils.SPELL_RED));
                return 0;
            }
            
            String bloodStatus = PlayerIdentityHelper.getBloodStatusDisplayName(identity.bloodStatus());
            String magicalType = PlayerIdentityHelper.getMagicalTypeDisplayName(identity.magicalType());
            
            Component message = Component.literal("")
                .append(ColorUtils.coloredText("Player: ", ColorUtils.SPELL_GOLD))
                .append(ColorUtils.coloredText(player.getName().getString(), ColorUtils.SPELL_WHITE))
                .append(Component.literal("\n"))
                .append(ColorUtils.coloredText("Blood Status: ", ColorUtils.SPELL_GOLD))
                .append(ColorUtils.coloredText(bloodStatus, ColorUtils.SPELL_WHITE))
                .append(Component.literal("\n"))
                .append(ColorUtils.coloredText("Magical Type: ", ColorUtils.SPELL_GOLD))
                .append(ColorUtils.coloredText(magicalType, ColorUtils.SPELL_WHITE));
            
            ctx.getSource().sendSuccess(() -> message, false);
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(ColorUtils.coloredText("This command can only be used by players when no target is specified", ColorUtils.SPELL_RED));
            return 0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(ColorUtils.coloredText("Error: " + e.getMessage(), ColorUtils.SPELL_RED));
            return 0;
        }
    }
    
    private static int setIdentity(CommandContext<CommandSourceStack> ctx, ServerPlayer player,
                                   String bloodStatusStr, String magicalTypeStr) {
        try {
            PlayerIdentityData.BloodStatus bloodStatus;
            try {
                bloodStatus = PlayerIdentityData.BloodStatus.valueOf(bloodStatusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                ctx.getSource().sendFailure(ColorUtils.coloredText("Invalid blood status: " + bloodStatusStr, ColorUtils.SPELL_RED));
                return 0;
            }
            
            PlayerIdentityData.MagicalType magicalType;
            try {
                magicalType = PlayerIdentityData.MagicalType.valueOf(magicalTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                ctx.getSource().sendFailure(ColorUtils.coloredText("Invalid magical type: " + magicalTypeStr, ColorUtils.SPELL_RED));
                return 0;
            }
            
            // Validate combination
            if (!PlayerIdentityHelper.isValidCombination(bloodStatus, magicalType)) {
                ctx.getSource().sendFailure(ColorUtils.coloredText(
                    "Invalid combination: " + bloodStatus.getDisplayName() + " + " + magicalType.getDisplayName() +
                    "\nSquib type must have Squib blood status, and vice versa.", ColorUtils.SPELL_RED));
                return 0;
            }
            
            // Create and set identity
            PlayerIdentityData.IdentityData identity = new PlayerIdentityData.IdentityData(bloodStatus, magicalType);
            PlayerDataHelper.setIdentityData(player, identity);
            
            // Apply race-based size scaling
            PlayerIdentityHelper.applyRaceScaling(player);
            
            Component message = Component.literal("")
                .append(ColorUtils.coloredText("Set identity for ", ColorUtils.SPELL_GREEN))
                .append(ColorUtils.coloredText(player.getName().getString(), ColorUtils.SPELL_WHITE))
                .append(ColorUtils.coloredText(": ", ColorUtils.SPELL_GREEN))
                .append(ColorUtils.coloredText(magicalType.getDisplayName(), ColorUtils.SPELL_WHITE))
                .append(ColorUtils.coloredText(" (", ColorUtils.SPELL_GREEN))
                .append(ColorUtils.coloredText(bloodStatus.getDisplayName(), ColorUtils.SPELL_WHITE))
                .append(ColorUtils.coloredText(")", ColorUtils.SPELL_GREEN));
            
            ctx.getSource().sendSuccess(() -> message, true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(ColorUtils.coloredText("Error: " + e.getMessage(), ColorUtils.SPELL_RED));
            return 0;
        }
    }
    
    private static int resetIdentity(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        try {
            // Reset to default (Half-blood Wizard/Witch based on gender)
            // For now, default to Half-blood Wizard
            PlayerIdentityData.IdentityData defaultIdentity = PlayerIdentityData.IdentityData.empty();
            PlayerDataHelper.setIdentityData(player, defaultIdentity);
            
            Component message = Component.literal("")
                .append(ColorUtils.coloredText("Reset identity for ", ColorUtils.SPELL_GREEN))
                .append(ColorUtils.coloredText(player.getName().getString(), ColorUtils.SPELL_WHITE))
                .append(ColorUtils.coloredText(" to default: ", ColorUtils.SPELL_GREEN))
                .append(ColorUtils.coloredText(defaultIdentity.magicalType().getDisplayName(), ColorUtils.SPELL_WHITE))
                .append(ColorUtils.coloredText(" (", ColorUtils.SPELL_GREEN))
                .append(ColorUtils.coloredText(defaultIdentity.bloodStatus().getDisplayName(), ColorUtils.SPELL_WHITE))
                .append(ColorUtils.coloredText(")", ColorUtils.SPELL_GREEN));
            
            ctx.getSource().sendSuccess(() -> message, true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(ColorUtils.coloredText("Error: " + e.getMessage(), ColorUtils.SPELL_RED));
            return 0;
        }
    }
    
    private static int listAll(CommandContext<CommandSourceStack> ctx) {
        try {
            MutableComponent message = Component.literal("")
                .append(ColorUtils.coloredText("=== Blood Statuses ===\n", ColorUtils.SPELL_GOLD));
            
            for (PlayerIdentityData.BloodStatus status : PlayerIdentityData.BloodStatus.values()) {
                message.append(ColorUtils.coloredText("  - ", ColorUtils.SPELL_WHITE))
                    .append(ColorUtils.coloredText(status.getDisplayName(), ColorUtils.SPELL_WHITE))
                    .append(ColorUtils.coloredText(" (" + status.name() + ")", ColorUtils.rgb(170, 170, 170)))
                    .append(Component.literal("\n"));
            }
            
            message.append(ColorUtils.coloredText("\n=== Magical Types ===\n", ColorUtils.SPELL_GOLD));
            
            for (PlayerIdentityData.MagicalType type : PlayerIdentityData.MagicalType.values()) {
                message.append(ColorUtils.coloredText("  - ", ColorUtils.SPELL_WHITE))
                    .append(ColorUtils.coloredText(type.getDisplayName(), ColorUtils.SPELL_WHITE))
                    .append(ColorUtils.coloredText(" (" + type.name() + ")", ColorUtils.rgb(170, 170, 170)))
                    .append(Component.literal("\n"));
            }
            
            final MutableComponent finalMessage = message;
            ctx.getSource().sendSuccess(() -> finalMessage, false);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(ColorUtils.coloredText("Error: " + e.getMessage(), ColorUtils.SPELL_RED));
            return 0;
        }
    }
}

