package at.koopro.spells_n_squares.core.commands.debug.item;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Debug commands for item operations.
 */
public final class ItemDebugCommands {
    
    private ItemDebugCommands() {}
    
    /**
     * Builds the item command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> item = Commands.literal("item");
        
        // /sns debug item give <item> [count]
        item.then(Commands.literal("give")
            .then(Commands.argument("item", StringArgumentType.string())
                .executes(ctx -> giveItem(ctx, 1))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                    .executes(ctx -> giveItem(ctx, IntegerArgumentType.getInteger(ctx, "count"))))));
        
        return item;
    }
    
    private static int giveItem(CommandContext<CommandSourceStack> ctx, int count) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String itemName = StringArgumentType.getString(ctx, "item");
            
            Identifier itemId;
            if (itemName.contains(":")) {
                itemId = Identifier.parse(itemName);
            } else {
                itemId = Identifier.fromNamespaceAndPath("minecraft", itemName);
            }
            
            Level level = player.level();
            var registry = level.registryAccess().lookup(Registries.ITEM);
            if (registry.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("Item registry not available"));
                return 0;
            }
            
            Item item = registry.get().getValue(itemId);
            if (item == null) {
                ctx.getSource().sendFailure(Component.literal("Item not found: " + itemName));
                return 0;
            }
            
            ItemStack itemStack = new ItemStack(item, count);
            if (player.getInventory().add(itemStack)) {
                ctx.getSource().sendSuccess(() -> Component.literal("Gave " + count + "x " + itemStack.getDisplayName().getString()), true);
                return 1;
            } else {
                ctx.getSource().sendFailure(Component.literal("Inventory full!"));
                return 0;
            }
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
}


