package at.koopro.spells_n_squares.core.util.dev;

import at.koopro.spells_n_squares.core.config.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

/**
 * Development logging utility for verbose debugging.
 * All logging is conditional based on config settings to avoid performance impact.
 * 
 * <p>Log format: [DEV] [ClassName] [MethodName] [Action]: [Details]
 * 
 * <p>Example usage:
 * <pre>{@code
 * DevLogger.logMethodEntry(this, "handleInteraction", "player=" + player.getName() + ", pos=" + pos);
 * DevLogger.logParameter(this, "spellId", spellId);
 * DevLogger.logReturnValue(this, "castSpell", result);
 * DevLogger.logStateChange(this, "setOpen", "open=" + open + ", pos=" + pos);
 * }</pre>
 */
public final class DevLogger {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private DevLogger() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if verbose logging is enabled.
     */
    public static boolean isVerboseLoggingEnabled() {
        return Config.isVerboseLoggingEnabled();
    }
    
    /**
     * Logs method entry with parameters.
     * 
     * @param instance The object instance (for class name)
     * @param methodName The method name
     * @param details Additional details (parameters, context)
     */
    public static void logMethodEntry(Object instance, String methodName, String details) {
        if (isVerboseLoggingEnabled() && Config.isMethodEntryExitEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [ENTRY]: {}", className, methodName, details);
        }
    }
    
    /**
     * Logs method entry without details.
     */
    public static void logMethodEntry(Object instance, String methodName) {
        if (isVerboseLoggingEnabled() && Config.isMethodEntryExitEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [ENTRY]", className, methodName);
        }
    }
    
    /**
     * Logs method exit with return value.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param returnValue The return value (will be converted to string)
     */
    public static void logMethodExit(Object instance, String methodName, Object returnValue) {
        if (isVerboseLoggingEnabled() && Config.isMethodEntryExitEnabled()) {
            String className = getSimpleClassName(instance);
            if (Config.isReturnValuesEnabled()) {
                LOGGER.debug("[DEV] [{}] [{}] [EXIT]: return={}", className, methodName, returnValue);
            } else {
                LOGGER.debug("[DEV] [{}] [{}] [EXIT]", className, methodName);
            }
        }
    }
    
    /**
     * Logs method exit without return value.
     */
    public static void logMethodExit(Object instance, String methodName) {
        if (isVerboseLoggingEnabled() && Config.isMethodEntryExitEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [EXIT]", className, methodName);
        }
    }
    
    /**
     * Logs a method parameter.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param paramName The parameter name
     * @param paramValue The parameter value
     */
    public static void logParameter(Object instance, String methodName, String paramName, Object paramValue) {
        if (isVerboseLoggingEnabled() && Config.isParametersEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [PARAMETER]: {}={}", className, methodName, paramName, paramValue);
        }
    }
    
    /**
     * Logs a return value.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param returnValue The return value
     */
    public static void logReturnValue(Object instance, String methodName, Object returnValue) {
        if (isVerboseLoggingEnabled() && Config.isReturnValuesEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [RETURN]: {}", className, methodName, returnValue);
        }
    }
    
    /**
     * Logs a state change.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param details State change details
     */
    public static void logStateChange(Object instance, String methodName, String details) {
        if (isVerboseLoggingEnabled() && Config.isStateChangesEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [STATE_CHANGE]: {}", className, methodName, details);
        }
    }
    
    /**
     * Logs a block interaction.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param player The player
     * @param pos The block position
     * @param state The block state
     */
    public static void logBlockInteraction(Object instance, String methodName, Player player, BlockPos pos, BlockState state) {
        if (isVerboseLoggingEnabled() && Config.isBlockInteractionsEnabled()) {
            String className = getSimpleClassName(instance);
            String playerName = player != null ? player.getName().getString() : "null";
            String blockName = state != null ? state.getBlock().getDescriptionId() : "null";
            LOGGER.debug("[DEV] [{}] [{}] [BLOCK_INTERACTION]: player={}, pos={}, block={}", 
                className, methodName, playerName, pos, blockName);
        }
    }
    
    /**
     * Logs an item interaction.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param player The player
     * @param stack The item stack
     */
    public static void logItemInteraction(Object instance, String methodName, Player player, ItemStack stack) {
        if (isVerboseLoggingEnabled() && Config.isItemInteractionsEnabled()) {
            String className = getSimpleClassName(instance);
            String playerName = player != null ? player.getName().getString() : "null";
            String itemName = stack != null && !stack.isEmpty() ? stack.getItem().getDescriptionId() : "empty";
            LOGGER.debug("[DEV] [{}] [{}] [ITEM_INTERACTION]: player={}, item={}, count={}", 
                className, methodName, playerName, itemName, stack != null ? stack.getCount() : 0);
        }
    }
    
    /**
     * Logs an entity event.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param eventType The event type
     * @param details Additional details
     */
    public static void logEntityEvent(Object instance, String methodName, String eventType, String details) {
        if (isVerboseLoggingEnabled() && Config.isEntityEventsEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [ENTITY_EVENT]: type={}, {}", className, methodName, eventType, details);
        }
    }
    
    /**
     * Logs a network packet operation.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param packetType The packet type/class name
     * @param direction Send or receive
     * @param details Additional details
     */
    public static void logNetworkPacket(Object instance, String methodName, String packetType, String direction, String details) {
        if (isVerboseLoggingEnabled() && Config.isNetworkPacketsEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [NETWORK]: packet={}, direction={}, {}", 
                className, methodName, packetType, direction, details);
        }
    }
    
    /**
     * Logs a data operation (save/load).
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param operation The operation type (SAVE, LOAD, etc.)
     * @param details Additional details
     */
    public static void logDataOperation(Object instance, String methodName, String operation, String details) {
        if (isVerboseLoggingEnabled() && Config.isDataOperationsEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}] [DATA]: operation={}, {}", className, methodName, operation, details);
        }
    }
    
    /**
     * Logs a general debug message.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param message The message
     */
    public static void logDebug(Object instance, String methodName, String message) {
        if (isVerboseLoggingEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}]: {}", className, methodName, message);
        }
    }
    
    /**
     * Logs a general debug message with format arguments.
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param format The message format
     * @param args Format arguments
     */
    public static void logDebug(Object instance, String methodName, String format, Object... args) {
        if (isVerboseLoggingEnabled()) {
            String className = getSimpleClassName(instance);
            LOGGER.debug("[DEV] [{}] [{}]: {}", className, methodName, String.format(format, args));
        }
    }
    
    /**
     * Logs an info message (always logged, not conditional on verbose logging).
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param message The message
     */
    public static void logInfo(Object instance, String methodName, String message) {
        String className = getSimpleClassName(instance);
        LOGGER.info("[DEV] [{}] [{}]: {}", className, methodName, message);
    }
    
    /**
     * Logs a warning message (always logged).
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param message The message
     */
    public static void logWarn(Object instance, String methodName, String message) {
        String className = getSimpleClassName(instance);
        LOGGER.warn("[DEV] [{}] [{}]: {}", className, methodName, message);
    }
    
    /**
     * Logs an error message (always logged).
     * 
     * @param instance The object instance
     * @param methodName The method name
     * @param message The message
     * @param throwable The exception (if any)
     */
    public static void logError(Object instance, String methodName, String message, Throwable throwable) {
        String className = getSimpleClassName(instance);
        if (throwable != null) {
            LOGGER.error("[DEV] [{}] [{}]: {}", className, methodName, message, throwable);
        } else {
            LOGGER.error("[DEV] [{}] [{}]: {}", className, methodName, message);
        }
    }
    
    /**
     * Gets the simple class name from an object instance.
     */
    private static String getSimpleClassName(Object instance) {
        if (instance == null) {
            return "null";
        }
        if (instance instanceof Class<?> clazz) {
            return clazz.getSimpleName();
        }
        return instance.getClass().getSimpleName();
    }
    
    /**
     * Helper to format BlockPos for logging.
     */
    public static String formatPos(BlockPos pos) {
        if (pos == null) {
            return "null";
        }
        return String.format("(%d,%d,%d)", pos.getX(), pos.getY(), pos.getZ());
    }
    
    /**
     * Helper to format Identifier for logging.
     */
    public static String formatResource(Identifier resource) {
        if (resource == null) {
            return "null";
        }
        return resource.toString();
    }
    
    /**
     * Helper to format Component for logging.
     */
    public static String formatComponent(Component component) {
        if (component == null) {
            return "null";
        }
        return component.getString();
    }
}


