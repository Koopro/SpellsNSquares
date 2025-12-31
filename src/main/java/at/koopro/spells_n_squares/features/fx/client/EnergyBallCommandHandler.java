package at.koopro.spells_n_squares.features.fx.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Handles energy ball placement commands.
 */
public final class EnergyBallCommandHandler {
    
    private EnergyBallCommandHandler() {}
    
    /**
     * Builds the energy ball command tree.
     */
    public static LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> buildCommand() {
        return Commands.literal("place_energy_ball")
            .executes(ctx -> placeEnergyBall(ctx));
    }
    
    /**
     * Places an energy ball block at the player's target position.
     * For client commands in single-player, accesses the integrated server.
     */
    private static int placeEnergyBall(CommandContext<CommandSourceStack> ctx) {
        try {
            // Get player from Minecraft client instance (for client commands)
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc == null || mc.player == null) {
                ctx.getSource().sendFailure(Component.literal("No player available"));
                return 0;
            }
            
            var player = mc.player;
            
            // Get block position from player's line of sight
            var hitResult = player.pick(32.0, 1.0f, false);
            if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
                ctx.getSource().sendFailure(Component.literal("No block in range to place energy ball"));
                return 0;
            }
            
            var blockHit = (net.minecraft.world.phys.BlockHitResult) hitResult;
            var targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
            
            // Get the server level (works in single-player integrated server)
            var level = player.level();
            if (level.isClientSide()) {
                // In single-player, get the integrated server's level
                if (mc.getSingleplayerServer() != null) {
                    level = mc.getSingleplayerServer().getLevel(level.dimension());
                } else {
                    ctx.getSource().sendFailure(Component.literal("Cannot place blocks in multiplayer from client command"));
                    return 0;
                }
            }
            
            if (level == null) {
                ctx.getSource().sendFailure(Component.literal("Could not access server level"));
                return 0;
            }
            
            // Check if position is valid
            if (!level.getBlockState(targetPos).isAir()) {
                ctx.getSource().sendFailure(Component.literal("Target position is not empty"));
                return 0;
            }
            
            // Place the energy ball block
            var block = at.koopro.spells_n_squares.features.fx.FxRegistry.ENERGY_BALL.value();
            level.setBlock(targetPos, block.defaultBlockState(), 3);
            
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


