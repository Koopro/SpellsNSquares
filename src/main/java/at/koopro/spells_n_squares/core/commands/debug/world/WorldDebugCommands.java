package at.koopro.spells_n_squares.core.commands.debug.world;

import at.koopro.spells_n_squares.core.util.world.WorldUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Debug commands for world operations.
 */
public final class WorldDebugCommands {
    
    private WorldDebugCommands() {}
    
    /**
     * Builds the world command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> world = Commands.literal("world");
        
        world.then(Commands.literal("biome")
            .executes(WorldDebugCommands::worldBiome))
        .then(Commands.literal("light")
            .executes(WorldDebugCommands::worldLight))
        .then(Commands.literal("time")
            .executes(WorldDebugCommands::worldTime))
        .then(Commands.literal("weather")
            .executes(WorldDebugCommands::worldWeather));
        
        return world;
    }
    
    private static int worldBiome(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos pos = player.blockPosition();
        Level level = player.level();
        var biome = WorldUtils.getBiomeAt(level, pos);
        if (biome != null) {
            String biomeName = biome.unwrapKey()
                .map(k -> k.toString())
                .orElse("unknown");
            Component message = Component.literal("Biome: " + biomeName);
            ctx.getSource().sendSuccess(() -> message, false);
        } else {
            ctx.getSource().sendFailure(Component.literal("Could not determine biome"));
        }
        return 1;
    }
    
    private static int worldLight(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos pos = player.blockPosition();
        Level level = player.level();
        int lightLevel = WorldUtils.getLightLevel(level, pos);
        ctx.getSource().sendSuccess(() -> Component.literal("Light level: " + lightLevel), false);
        return 1;
    }
    
    private static int worldTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Level level = player.level();
        long time = WorldUtils.getTimeOfDay(level);
        boolean isDay = WorldUtils.isDay(level);
        ctx.getSource().sendSuccess(() -> Component.literal(
            "Time: " + time + " ticks (" + (time / 1000.0) + " hours)\n" +
            "Is Day: " + isDay
        ), false);
        return 1;
    }
    
    private static int worldWeather(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Level level = player.level();
        String weather = WorldUtils.getWeatherInfo(level);
        ctx.getSource().sendSuccess(() -> Component.literal("Weather: " + weather), false);
        return 1;
    }
}


