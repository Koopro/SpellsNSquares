package at.koopro.spells_n_squares.core.commands.debug.sound;

import at.koopro.spells_n_squares.core.util.rendering.SoundUtils;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Debug commands for sound operations.
 */
public final class SoundDebugCommands {
    
    private SoundDebugCommands() {}
    
    /**
     * Builds the sound command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> sound = Commands.literal("sound");
        
        sound.then(Commands.literal("play")
            .then(Commands.argument("sound", StringArgumentType.string())
                .executes(ctx -> soundPlay(ctx, 1.0f, 1.0f))
                .then(Commands.argument("volume", FloatArgumentType.floatArg(0.0f, 1.0f))
                    .then(Commands.argument("pitch", FloatArgumentType.floatArg(0.5f, 2.0f))
                        .executes(ctx -> soundPlay(ctx, FloatArgumentType.getFloat(ctx, "volume"), FloatArgumentType.getFloat(ctx, "pitch")))))))
        .then(Commands.literal("magical")
            .executes(ctx -> soundMagical(ctx, 1.0f, 1.0f))
            .then(Commands.argument("volume", FloatArgumentType.floatArg(0.0f, 1.0f))
                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0.5f, 2.0f))
                    .executes(ctx -> soundMagical(ctx, FloatArgumentType.getFloat(ctx, "volume"), FloatArgumentType.getFloat(ctx, "pitch"))))));
        
        return sound;
    }
    
    private static int soundPlay(CommandContext<CommandSourceStack> ctx, float volume, float pitch) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String soundName = StringArgumentType.getString(ctx, "sound");
        Level level = player.level();
        Vec3 pos = player.position();
        
        var soundEvent = SoundUtils.parseSoundEvent(soundName);
        if (soundEvent == null) {
            SoundUtils.playMagicalSound(level, pos, volume, pitch);
        }
        
        ctx.getSource().sendSuccess(() -> Component.literal("Played sound: " + soundName), true);
        return 1;
    }
    
    private static int soundMagical(CommandContext<CommandSourceStack> ctx, float volume, float pitch) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Level level = player.level();
        Vec3 pos = player.position();
        SoundUtils.playMagicalSound(level, pos, volume, pitch);
        ctx.getSource().sendSuccess(() -> Component.literal("Played magical sound"), true);
        return 1;
    }
}


