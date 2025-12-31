package at.koopro.spells_n_squares.core.util;

import at.koopro.spells_n_squares.core.network.NetworkPayloadBatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

/**
 * Utility class for common player data syncing patterns.
 * Provides standardized methods for syncing player data from server to client.
 * Supports both immediate and batched sending for performance optimization.
 */
public final class PlayerDataSyncUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Default to batching for performance, but can be overridden per-call
    private static boolean useBatchingByDefault = true;
    
    private PlayerDataSyncUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Syncs a payload to a server player's client.
     * Uses batching by default for better performance.
     * Includes error handling and validation.
     * 
     * @param serverPlayer The server player to sync to
     * @param payload The payload to sync
     * @param payloadName The name of the payload (for logging)
     */
    public static void syncToClient(ServerPlayer serverPlayer, CustomPacketPayload payload, String payloadName) {
        syncToClient(serverPlayer, payload, payloadName, useBatchingByDefault);
    }
    
    /**
     * Syncs a payload to a server player's client with batching control.
     * 
     * @param serverPlayer The server player to sync to
     * @param payload The payload to sync
     * @param payloadName The name of the payload (for logging)
     * @param useBatching Whether to use batching (true) or send immediately (false)
     */
    public static void syncToClient(ServerPlayer serverPlayer, CustomPacketPayload payload, String payloadName, boolean useBatching) {
        if (serverPlayer == null) {
            LOGGER.warn("Attempted to sync {} to null player", payloadName);
            return;
        }
        
        if (payload == null) {
            LOGGER.warn("Attempted to sync null {} payload to player {}", payloadName, serverPlayer.getName().getString());
            return;
        }
        
        try {
            if (useBatching) {
                // Queue for batching (sent at end of tick)
                NetworkPayloadBatcher.queuePayload(serverPlayer, payload);
            } else {
                // Send immediately (for urgent payloads)
                NetworkPayloadBatcher.sendImmediate(serverPlayer, payload);
            }
        } catch (Exception e) {
            LOGGER.error("Error syncing {} to player {}: {}", 
                payloadName, serverPlayer.getName().getString(), e.getMessage(), e);
        }
    }
    
    /**
     * Syncs a payload to a server player's client with default payload name.
     * Uses batching by default.
     * 
     * @param serverPlayer The server player to sync to
     * @param payload The payload to sync
     */
    public static void syncToClient(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        syncToClient(serverPlayer, payload, "payload");
    }
    
    /**
     * Syncs a payload immediately without batching.
     * Use this for urgent payloads that can't wait for batching.
     * 
     * @param serverPlayer The server player to sync to
     * @param payload The payload to sync
     * @param payloadName The name of the payload (for logging)
     */
    public static void syncToClientImmediate(ServerPlayer serverPlayer, CustomPacketPayload payload, String payloadName) {
        syncToClient(serverPlayer, payload, payloadName, false);
    }
    
    /**
     * Sets whether batching should be used by default.
     * 
     * @param useBatching Whether to use batching by default
     */
    public static void setUseBatchingByDefault(boolean useBatching) {
        useBatchingByDefault = useBatching;
    }
    
    /**
     * Gets whether batching is used by default.
     * 
     * @return Whether batching is used by default
     */
    public static boolean isUseBatchingByDefault() {
        return useBatchingByDefault;
    }
}

