package at.koopro.spells_n_squares.features.enchantments.library;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for tracking discovered enchantments.
 * Maintains a library of enchantments that players have discovered.
 */
public final class EnchantmentLibraryHelper {
    
    private static final Map<UUID, Set<Identifier>> PLAYER_LIBRARIES = new ConcurrentHashMap<>();
    
    private EnchantmentLibraryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Records a discovered enchantment for a player.
     * 
     * @param player The server player
     * @param enchantmentId The enchantment identifier
     */
    public static void recordDiscovery(ServerPlayer player, Identifier enchantmentId) {
        if (player == null || enchantmentId == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        Set<Identifier> library = PLAYER_LIBRARIES.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet());
        
        boolean added = library.add(enchantmentId);
        if (added) {
            DevLogger.logStateChange(EnchantmentLibraryHelper.class, "recordDiscovery",
                "Player: " + player.getName().getString() + " discovered: " + enchantmentId);
        }
    }
    
    /**
     * Checks if a player has discovered an enchantment.
     * 
     * @param player The server player
     * @param enchantmentId The enchantment identifier
     * @return true if discovered
     */
    public static boolean hasDiscovered(ServerPlayer player, Identifier enchantmentId) {
        if (player == null || enchantmentId == null) {
            return false;
        }
        
        UUID playerId = player.getUUID();
        Set<Identifier> library = PLAYER_LIBRARIES.get(playerId);
        return library != null && library.contains(enchantmentId);
    }
    
    /**
     * Gets all discovered enchantments for a player.
     * 
     * @param player The server player
     * @return Set of discovered enchantment identifiers
     */
    public static Set<Identifier> getDiscoveredEnchantments(ServerPlayer player) {
        if (player == null) {
            return Collections.emptySet();
        }
        
        UUID playerId = player.getUUID();
        Set<Identifier> library = PLAYER_LIBRARIES.get(playerId);
        return library != null ? new HashSet<>(library) : Collections.emptySet();
    }
    
    /**
     * Gets discovery progress for a player.
     * 
     * @param player The server player
     * @return Discovery progress (discovered count, total count)
     */
    public static DiscoveryProgress getProgress(ServerPlayer player) {
        if (player == null) {
            return new DiscoveryProgress(0, 0);
        }
        
        Set<Identifier> discovered = getDiscoveredEnchantments(player);
        // Count total enchantments in registry - use reflection like EnchantmentHelper
        int total = 0;
        try {
            java.lang.reflect.Field enchantmentField = BuiltInRegistries.class.getDeclaredField("ENCHANTMENT");
            enchantmentField.setAccessible(true);
            Object registryObj = enchantmentField.get(null);
            if (registryObj != null) {
                java.lang.reflect.Method sizeMethod = registryObj.getClass().getMethod("size");
                total = (Integer) sizeMethod.invoke(registryObj);
            }
        } catch (Exception e) {
            // Fallback to placeholder if reflection fails
            total = 100;
        }
        
        return new DiscoveryProgress(discovered.size(), total);
    }
    
    /**
     * Represents discovery progress.
     */
    public record DiscoveryProgress(int discovered, int total) {
        public float getPercentage() {
            return total > 0 ? (float) discovered / total : 0.0f;
        }
    }
}

