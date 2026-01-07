package at.koopro.spells_n_squares.core.commands.debug.particle;

import at.koopro.spells_n_squares.core.util.rendering.ParticleUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Debug commands for particle operations.
 */
public final class ParticleDebugCommands {
    
    private ParticleDebugCommands() {}
    
    /**
     * Builds the particle command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> particle = Commands.literal("particle");
        
        particle.then(Commands.literal("circle")
            .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0.1, 10.0))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(ParticleDebugCommands::particleCircle))))
        .then(Commands.literal("sphere")
            .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0.1, 10.0))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(ParticleDebugCommands::particleSphere))))
        .then(Commands.literal("burst")
            .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                .executes(ParticleDebugCommands::particleBurst)));
        
        return particle;
    }
    
    private static int particleCircle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return 0;
        }
        double radius = DoubleArgumentType.getDouble(ctx, "radius");
        int count = IntegerArgumentType.getInteger(ctx, "count");
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        ParticleUtils.spawnParticleCircle(serverLevel, pos, radius, count, ParticleTypes.ENCHANT);
        ctx.getSource().sendSuccess(() -> Component.literal("Spawned particle circle"), true);
        return 1;
    }
    
    private static int particleSphere(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return 0;
        }
        double radius = DoubleArgumentType.getDouble(ctx, "radius");
        int count = IntegerArgumentType.getInteger(ctx, "count");
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        ParticleUtils.spawnParticleSphere(serverLevel, pos, radius, count, ParticleTypes.ENCHANT);
        ctx.getSource().sendSuccess(() -> Component.literal("Spawned particle sphere"), true);
        return 1;
    }
    
    private static int particleBurst(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return 0;
        }
        int count = IntegerArgumentType.getInteger(ctx, "count");
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        ParticleUtils.spawnParticleBurst(serverLevel, pos, count, ParticleTypes.ENCHANT);
        ctx.getSource().sendSuccess(() -> Component.literal("Spawned particle burst"), true);
        return 1;
    }
}


