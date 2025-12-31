package at.koopro.spells_n_squares.core.util;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * Utility class for safe event handling with consistent error handling.
 * Provides standardized error handling patterns for event handlers across the mod.
 * 
 * <p>All error messages follow a consistent format:
 * "Error [action] [context]: [error message]"
 * 
 * <p>Example usage:
 * <pre>{@code
 * @SubscribeEvent
 * public static void onPlayerTick(PlayerTickEvent.Post event) {
 *     SafeEventHandler.execute(() -> {
 *         // Handler logic here
 *     }, "ticking player", event.getEntity());
 * }
 * }</pre>
 */
public final class SafeEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private SafeEventHandler() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Wraps an event handler with try-catch-error-logging.
     * 
     * @param handler The event handler to wrap
     * @param eventName The name of the event (for logging)
     * @return A safe wrapper that handles exceptions
     */
    public static <T> Consumer<T> wrap(Consumer<T> handler, String eventName) {
        return event -> {
            try {
                handler.accept(event);
            } catch (Exception e) {
                LOGGER.error("Error handling {} event: {}", eventName, e.getMessage(), e);
            }
        };
    }
    
    /**
     * Executes a runnable with error handling.
     * 
     * @param runnable The runnable to execute
     * @param actionName The name of the action (for logging)
     */
    public static void execute(Runnable runnable, String actionName) {
        try {
            runnable.run();
        } catch (Exception e) {
            LOGGER.error("Error {}: {}", actionName, e.getMessage(), e);
        }
    }
    
    /**
     * Executes a runnable with error handling and context information.
     * 
     * @param runnable The runnable to execute
     * @param actionName The name of the action (for logging)
     * @param context Additional context information (e.g., player name, position)
     */
    public static void execute(Runnable runnable, String actionName, String context) {
        try {
            runnable.run();
        } catch (Exception e) {
            LOGGER.error("Error {} (context: {}): {}", actionName, context, e.getMessage(), e);
        }
    }
    
    /**
     * Executes a runnable with error handling and player context.
     * Automatically extracts player name for context.
     * 
     * @param runnable The runnable to execute
     * @param actionName The name of the action (for logging)
     * @param player The player (for context)
     */
    public static void execute(Runnable runnable, String actionName, Player player) {
        String context = player != null ? "player " + player.getName().getString() : "unknown player";
        execute(runnable, actionName, context);
    }
    
    /**
     * Executes a runnable with error handling, player context, and additional context.
     * 
     * @param runnable The runnable to execute
     * @param actionName The name of the action (for logging)
     * @param player The player (for context)
     * @param additionalContext Additional context information
     */
    public static void execute(Runnable runnable, String actionName, Player player, String additionalContext) {
        String context = player != null ? "player " + player.getName().getString() : "unknown player";
        if (additionalContext != null && !additionalContext.isEmpty()) {
            context += ", " + additionalContext;
        }
        execute(runnable, actionName, context);
    }
    
    /**
     * Executes a runnable with error handling and position context.
     * 
     * @param runnable The runnable to execute
     * @param actionName The name of the action (for logging)
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public static void execute(Runnable runnable, String actionName, double x, double y, double z) {
        String context = String.format("position (%.1f, %.1f, %.1f)", x, y, z);
        execute(runnable, actionName, context);
    }
    
    /**
     * Executes a runnable with error handling, catching only specific exception types.
     * Useful when you want to handle some exceptions differently.
     * 
     * @param runnable The runnable to execute
     * @param actionName The name of the action (for logging)
     * @param exceptionType The exception type to catch and log
     * @param <E> The exception type
     * @throws E If the exception is not of the specified type
     */
    public static <E extends Exception> void executeCatching(
            Runnable runnable, String actionName, Class<E> exceptionType) throws E {
        try {
            runnable.run();
        } catch (Exception e) {
            if (exceptionType.isInstance(e)) {
                LOGGER.error("Error {}: {}", actionName, e.getMessage(), e);
            } else {
                // Re-throw if not the expected type
                throw e;
            }
        }
    }
}

