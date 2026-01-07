package at.koopro.spells_n_squares.core.commands.debug.inventory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Debug commands for inventory operations.
 */
public final class InventoryDebugCommands {
    
    private InventoryDebugCommands() {}
    
    /**
     * Builds the inventory command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> inventory = Commands.literal("inventory");
        
        inventory.then(Commands.literal("has")
            .then(Commands.argument("item", StringArgumentType.string())
                .executes(InventoryDebugCommands::inventoryHas)))
        .then(Commands.literal("count")
            .then(Commands.argument("item", StringArgumentType.string())
                .executes(InventoryDebugCommands::inventoryCount)));
        
        return inventory;
    }
    
    private static int inventoryHas(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String itemName = StringArgumentType.getString(ctx, "item");
        ctx.getSource().sendSuccess(() -> Component.literal("Has item check: " + itemName), false);
        return 1;
    }
    
    private static int inventoryCount(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String itemName = StringArgumentType.getString(ctx, "item");
        ctx.getSource().sendSuccess(() -> Component.literal("Item count: " + itemName), false);
        return 1;
    }
}


