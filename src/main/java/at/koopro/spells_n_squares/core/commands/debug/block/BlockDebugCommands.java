package at.koopro.spells_n_squares.core.commands.debug.block;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Debug commands for block operations.
 */
public final class BlockDebugCommands {
    
    private BlockDebugCommands() {}
    
    /**
     * Builds the block command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> block = Commands.literal("block");
        
        // /sns debug block set <block>
        block.then(Commands.literal("set")
            .then(Commands.argument("block", StringArgumentType.string())
                .executes(BlockDebugCommands::setBlock)));
        
        return block;
    }
    
    private static int setBlock(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String blockName = StringArgumentType.getString(ctx, "block");
            
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
            
            Block block = registry.get().getValue(blockId);
            if (block == null) {
                ctx.getSource().sendFailure(Component.literal("Block not found: " + blockName));
                return 0;
            }
            
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
}


