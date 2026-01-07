package at.koopro.spells_n_squares.core.commands.debug.help;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Handles help commands for debug system.
 */
public final class HelpCommandHandler {
    
    private HelpCommandHandler() {}
    
    /**
     * Builds the help command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> help = Commands.literal("help");
        
        // /sns debug help - Show all categories
        help.executes(HelpCommandHandler::showHelp);
        
        // /sns debug help <category> - Show category-specific help
        help.then(Commands.argument("category", StringArgumentType.string())
            .suggests((ctx, builder) -> {
                builder.suggest("item");
                builder.suggest("block");
                builder.suggest("model");
                builder.suggest("color");
                builder.suggest("sound");
                builder.suggest("particle");
                builder.suggest("inventory");
                builder.suggest("world");
                builder.suggest("dummy_player");
                builder.suggest("fx");
                builder.suggest("identity");
                return builder.buildFuture();
            })
            .executes(ctx -> showCategoryHelp(ctx, StringArgumentType.getString(ctx, "category"))));
        
        return help;
    }
    
    private static int showHelp(CommandContext<CommandSourceStack> ctx) {
        Component message = Component.literal(
            "=== Spells_n_Squares Debug Commands ===\n" +
            "Categories: item, block, model, color, sound, particle, inventory, world, dummy_player, fx, identity\n" +
            "Use /sns debug help <category> for category-specific help"
        );
        ctx.getSource().sendSuccess(() -> message, false);
        return 1;
    }
    
    private static int showCategoryHelp(CommandContext<CommandSourceStack> ctx, String category) {
        Component message;
        switch (category.toLowerCase()) {
            case "item":
                message = Component.literal(
                    "=== Item Commands ===\n" +
                    "/sns debug item give <item> [count] - Give an item to yourself"
                );
                break;
            case "block":
                message = Component.literal(
                    "=== Block Commands ===\n" +
                    "/sns debug block set <block> - Set the block in front of you"
                );
                break;
            case "model":
                message = Component.literal(
                    "=== Model Commands ===\n" +
                    "/sns debug model scale <player> <scale> - Set player overall scale\n" +
                    "/sns debug model head <player> <scale> - Set player head scale\n" +
                    "/sns debug model body <player> <scale> - Set player body scale\n" +
                    "/sns debug model leftarm <player> <scale> - Set player left arm scale\n" +
                    "/sns debug model rightarm <player> <scale> - Set player right arm scale\n" +
                    "/sns debug model leftleg <player> <scale> - Set player left leg scale\n" +
                    "/sns debug model rightleg <player> <scale> - Set player right leg scale\n" +
                    "/sns debug model hitbox <player> <scale> - Set player hitbox scale\n" +
                    "/sns debug model reset <player> - Reset player model to default\n" +
                    "/sns debug model info <player> - Show player model info"
                );
                break;
            case "color":
                message = Component.literal(
                    "=== Color Commands ===\n" +
                    "/sns debug color rgb <r> <g> <b> <text> - Display colored text (RGB)\n" +
                    "/sns debug color hex <hexcode> <text> - Display colored text (HEX)\n" +
                    "/sns debug color house <houseName> - Show house color"
                );
                break;
            case "sound":
                message = Component.literal(
                    "=== Sound Commands ===\n" +
                    "/sns debug sound play <sound> [volume] [pitch] - Play a sound\n" +
                    "/sns debug sound magical [volume] [pitch] - Play magical sound"
                );
                break;
            case "particle":
                message = Component.literal(
                    "=== Particle Commands ===\n" +
                    "/sns debug particle circle <radius> <count> - Spawn particle circle\n" +
                    "/sns debug particle sphere <radius> <count> - Spawn particle sphere\n" +
                    "/sns debug particle burst <count> - Spawn particle burst"
                );
                break;
            case "inventory":
                message = Component.literal(
                    "=== Inventory Commands ===\n" +
                    "/sns debug inventory has <item> - Check if player has item\n" +
                    "/sns debug inventory count <item> - Count items in inventory"
                );
                break;
            case "world":
                message = Component.literal(
                    "=== World Commands ===\n" +
                    "/sns debug world biome - Show biome at your location\n" +
                    "/sns debug world light - Show light level at your location\n" +
                    "/sns debug world time - Show world time\n" +
                    "/sns debug world weather - Show weather info"
                );
                break;
            case "dummy_player":
                message = Component.literal(
                    "=== Dummy Player Commands ===\n" +
                    "/sns debug dummy_player spawn <modelType> - Spawn dummy player\n" +
                    "/sns debug dummy_player scale <entity> <scale> - Scale dummy player\n" +
                    "/sns debug dummy_player model <entity> <modelType> - Set dummy player model\n" +
                    "/sns debug dummy_player item <entity> <item> - Set dummy player item\n" +
                    "/sns debug dummy_player remove <entity> - Remove dummy player"
                );
                break;
            case "fx":
                message = Component.literal(
                    "=== FX Commands ===\n" +
                    "/sns debug fx shader test <shader_name> [intensity] - Test shader effect\n" +
                    "/sns debug fx shader on <shader_name> [intensity] - Enable shader effect\n" +
                    "/sns debug fx shader off <shader_name> - Disable shader effect\n" +
                    "/sns debug fx block_shader register <block> <shader> <intensity> [look|range] - Register block shader\n" +
                    "/sns debug fx block_shader unregister <block> - Unregister block shader\n" +
                    "/sns debug fx block_shader list - List registered block shaders\n" +
                    "/sns debug fx place_energy_ball - Place energy ball block at target"
                );
                break;
            case "identity":
                message = Component.literal(
                    "=== Identity Commands ===\n" +
                    "/sns debug identity get [player] - Get player's blood status and magical type\n" +
                    "/sns debug identity set <player> <bloodStatus> <magicalType> - Set player's identity\n" +
                    "/sns debug identity reset <player> - Reset player's identity to default\n" +
                    "/sns debug identity list - List all available blood statuses and magical types\n" +
                    "\nBlood Statuses: PURE_BLOOD, HALF_BLOOD, MUGGLE_BORN, SQUIB\n" +
                    "Magical Types: WIZARD, WITCH, SQUIB, WEREWOLF, VEELA, VAMPIRE, GOBLIN, HOUSE_ELF, GIANT, CENTAUR"
                );
                break;
            default:
                message = Component.literal("Unknown category: " + category);
                break;
        }
        ctx.getSource().sendSuccess(() -> message, false);
        return 1;
    }
}


