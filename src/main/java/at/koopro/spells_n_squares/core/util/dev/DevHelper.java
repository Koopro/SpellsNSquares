package at.koopro.spells_n_squares.core.util.dev;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Development utilities and shortcuts for testing and debugging.
 * Provides helper methods for common development tasks.
 */
public final class DevHelper {
    
    private DevHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Spawns an item with specific properties for testing.
     * 
     * @param player The server player
     * @param item The item stack to spawn
     * @return true if item was spawned
     */
    public static boolean spawnItem(ServerPlayer player, ItemStack item) {
        if (player == null || item == null || item.isEmpty()) {
            return false;
        }
        
        if (!player.getInventory().add(item)) {
            player.drop(item, false);
        }
        
        DevLogger.logDebug(DevHelper.class, "spawnItem",
            "Spawned item: " + item.getDisplayName().getString());
        
        return true;
    }
    
    /**
     * Teleports a player to specific coordinates.
     * 
     * @param player The server player
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return true if teleportation was successful
     */
    public static boolean teleportPlayer(ServerPlayer player, double x, double y, double z) {
        if (player == null) {
            return false;
        }
        
        player.teleportTo(x, y, z);
        
        DevLogger.logDebug(DevHelper.class, "teleportPlayer",
            "Teleported player to: " + x + ", " + y + ", " + z);
        
        return true;
    }
    
    /**
     * Validates a data structure and logs issues.
     * 
     * @param data The data to validate
     * @param validator Validation function
     * @return true if valid
     */
    public static <T> boolean validateData(T data, java.util.function.Predicate<T> validator) {
        if (data == null || validator == null) {
            return false;
        }
        
        boolean valid = validator.test(data);
        if (!valid) {
            DevLogger.logWarn(DevHelper.class, "validateData",
                "Data validation failed for: " + data.getClass().getSimpleName());
        }
        
        return valid;
    }
}

