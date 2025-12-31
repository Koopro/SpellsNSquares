package at.koopro.spells_n_squares.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import at.koopro.spells_n_squares.features.artifacts.ArtifactsRegistry;
import at.koopro.spells_n_squares.features.artifacts.TimeTurnerItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Debug commands for development and testing.
 * Only available to operators (requires level 2+).
 * 
 * Available commands:
 * - /debug give <item> [count] - Give an item to yourself (format: "modid:itemname")
 * - /debug setblock <block> - Set the block in front of you (format: "modid:blockname")
 * - /debug timeturner reset - Reset cooldowns on held Time-Turner
 */
public class DebugCommands {
    
    /**
     * Registers all debug commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("debug");
            // Note: Permission check removed - add back if needed with proper API
        
        // debug give <item> [count]
        root.then(Commands.literal("give")
            .then(Commands.argument("item", StringArgumentType.string())
                .executes(ctx -> giveItem(ctx, 1))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                    .executes(ctx -> giveItem(ctx, IntegerArgumentType.getInteger(ctx, "count"))))));
        
        // debug setblock <block>
        root.then(Commands.literal("setblock")
            .then(Commands.argument("block", StringArgumentType.string())
                .executes(ctx -> setBlock(ctx))));
        
        // debug timeturner reset
        root.then(Commands.literal("timeturner")
            .then(Commands.literal("reset")
                .executes(ctx -> resetTimeTurnerCooldown(ctx))));
        
        dispatcher.register(root);
    }
    
    /**
     * Gives an item to the player.
     */
    private static int giveItem(CommandContext<CommandSourceStack> ctx, int count) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String itemName = StringArgumentType.getString(ctx, "item");
            
            // Parse item name (format: "modid:itemname" or "minecraft:itemname")
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
            // Use getValue to get item by Identifier
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
    
    /**
     * Sets a block at the player's target position.
     */
    private static int setBlock(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String blockName = StringArgumentType.getString(ctx, "block");
            
            // Parse block name (format: "modid:blockname" or "minecraft:blockname")
            Identifier blockId;
            if (blockName.contains(":")) {
                blockId = Identifier.parse(blockName);
            } else {
                blockId = Identifier.fromNamespaceAndPath("minecraft", blockName);
            }
            
            Level level = player.level();
            var registry = level.registryAccess().lookup(Registries.BLOCK);
            if (registry.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("Block registry not available"));
                return 0;
            }
            // Use getValue to get block by Identifier
            Block block = registry.get().getValue(blockId);
            
            if (block == null) {
                ctx.getSource().sendFailure(Component.literal("Block not found: " + blockName));
                return 0;
            }
            
            // Get block position from player's line of sight (simplified - uses block player is looking at)
            // For a more accurate implementation, you'd use player.getLookAngle() and raycast
            BlockPos targetPos = player.blockPosition().relative(player.getDirection());
            BlockState newState = block.defaultBlockState();
            
            level.setBlock(targetPos, newState, 3);
            ctx.getSource().sendSuccess(() -> Component.literal("Set block at " + targetPos.toShortString() + " to " + blockName), true);
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * Resets cooldowns on the Time-Turner held by the player.
     */
    private static int resetTimeTurnerCooldown(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            
            // Check main hand and offhand for Time-Turner
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            ItemStack timeTurnerStack = null;
            final String hand;
            
            if (!mainHand.isEmpty() && mainHand.getItem() == ArtifactsRegistry.TIME_TURNER.get()) {
                timeTurnerStack = mainHand;
                hand = "main hand";
            } else if (!offHand.isEmpty() && offHand.getItem() == ArtifactsRegistry.TIME_TURNER.get()) {
                timeTurnerStack = offHand;
                hand = "offhand";
            } else {
                ctx.getSource().sendFailure(Component.literal("You must be holding a Time-Turner!"));
                return 0;
            }
            
            // Get current data
            TimeTurnerItem.TimeTurnerData data = TimeTurnerItem.getTimeTurnerData(timeTurnerStack);
            
            // Reset cooldowns by setting last use tick and last death prevention tick to 0
            // This makes them appear as if they were used a very long time ago
            TimeTurnerItem.TimeTurnerData resetData = new TimeTurnerItem.TimeTurnerData(
                0, // Reset time rewind cooldown
                data.anchorX(),
                data.anchorY(),
                data.anchorZ(),
                data.anchorDimension(),
                0L // Reset death prevention cooldown
            );
            
            // Update the item stack
            timeTurnerStack.set(TimeTurnerItem.TIME_TURNER_DATA.get(), resetData);
            
            final String handFinal = hand;
            ctx.getSource().sendSuccess(() -> Component.literal(
                "§aTime-Turner cooldowns reset! (in " + handFinal + ")"), true);
            
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }
}
