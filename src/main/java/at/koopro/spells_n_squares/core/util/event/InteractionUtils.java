package at.koopro.spells_n_squares.core.util.event;

import at.koopro.spells_n_squares.core.util.player.PlayerValidationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Utility class for common interaction patterns.
 * Provides standardized methods for server-side execution, message sending, and validation.
 */
public final class InteractionUtils {
    private InteractionUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Executes an action on the server side if the level is server-side and player is a ServerPlayer.
     * 
     * @param level The level
     * @param player The player
     * @param action The action to execute with the ServerPlayer
     * @return true if the action was executed, false otherwise
     */
    public static boolean executeOnServer(Level level, Player player, Consumer<ServerPlayer> action) {
        if (level.isClientSide()) {
            return false;
        }
        
        ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer == null) {
            return false;
        }
        
        action.accept(serverPlayer);
        return true;
    }
    
    /**
     * Executes an action on the server side with ServerLevel validation.
     * 
     * @param level The level
     * @param player The player
     * @param action The action to execute with ServerLevel and ServerPlayer
     * @return true if the action was executed, false otherwise
     */
    public static boolean executeOnServerLevel(Level level, Player player, 
                                               java.util.function.BiConsumer<ServerLevel, ServerPlayer> action) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer == null) {
            return false;
        }
        
        action.accept(serverLevel, serverPlayer);
        return true;
    }
    
    /**
     * Sends a system message to a player with null checking.
     * 
     * @param player The server player
     * @param message The message to send
     */
    public static void sendPlayerMessage(ServerPlayer player, Component message) {
        if (player != null && message != null) {
            player.sendSystemMessage(message);
        }
    }
    
    /**
     * Sends a translatable message to a player.
     * 
     * @param player The server player
     * @param translationKey The translation key
     * @param args Optional arguments for the translation
     */
    public static void sendTranslatableMessage(ServerPlayer player, String translationKey, Object... args) {
        if (player != null && translationKey != null) {
            player.sendSystemMessage(Component.translatable(translationKey, args));
        }
    }
    
    /**
     * Validates that a level is a ServerLevel.
     * 
     * @param level The level to validate
     * @return The ServerLevel if valid, null otherwise
     */
    public static ServerLevel asServerLevel(Level level) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel;
    }
}


