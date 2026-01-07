package at.koopro.spells_n_squares.core.commands.debug.color;

import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Debug commands for color operations.
 */
public final class ColorDebugCommands {
    
    private ColorDebugCommands() {}
    
    /**
     * Builds the color command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> color = Commands.literal("color");
        
        color.then(Commands.literal("rgb")
            .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                    .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                            .executes(ColorDebugCommands::colorRgb))))))
        .then(Commands.literal("hex")
            .then(Commands.argument("hexcode", StringArgumentType.string())
                .then(Commands.argument("text", StringArgumentType.greedyString())
                    .executes(ColorDebugCommands::colorHex))))
        .then(Commands.literal("house")
            .then(Commands.argument("houseName", StringArgumentType.string())
                .executes(ColorDebugCommands::colorHouse)));
        
        return color;
    }
    
    private static int colorRgb(CommandContext<CommandSourceStack> ctx) {
        int r = IntegerArgumentType.getInteger(ctx, "r");
        int g = IntegerArgumentType.getInteger(ctx, "g");
        int b = IntegerArgumentType.getInteger(ctx, "b");
        String text = StringArgumentType.getString(ctx, "text");
        int color = ColorUtils.rgb(r, g, b);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(text, color), false);
        return 1;
    }
    
    private static int colorHex(CommandContext<CommandSourceStack> ctx) {
        String hexcode = StringArgumentType.getString(ctx, "hexcode");
        String text = StringArgumentType.getString(ctx, "text");
        int color = ColorUtils.hex(hexcode);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(text, color), false);
        return 1;
    }
    
    private static int colorHouse(CommandContext<CommandSourceStack> ctx) {
        String houseName = StringArgumentType.getString(ctx, "houseName");
        int color = ColorUtils.getHouseColor(houseName);
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "House color for " + houseName + ": " + Integer.toHexString(color), 
            color), false);
        return 1;
    }
}

