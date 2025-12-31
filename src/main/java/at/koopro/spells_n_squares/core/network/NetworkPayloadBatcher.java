package at.koopro.spells_n_squares.core.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Batches network payloads to reduce network overhead.
 * Queues payloads per player and sends them together at the end of each tick.
 */
public final class NetworkPayloadBatcher {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private NetworkPayloadBatcher() {
    }
    
    // Per-player queues of payloads to send
    private static final Map<UUID, List<CustomPacketPayload>> playerQueues = new ConcurrentHashMap<>();
    
    // Track last send time per player to throttle rapid sends
    private static final Map<UUID, Long> lastSendTime = new ConcurrentHashMap<>();
    
    // Minimum time between sends (milliseconds) - prevents spam
    private static final long MIN_SEND_INTERVAL = 50; // 50ms = 20 sends per second max
    
    /**
     * Queues a payload to be sent to a player at the end of the tick.
     * Payloads are automatically batched and sent together.
     * 
     * @param player The server player to send to
     * @param payload The payload to queue
     */
    public static void queuePayload(ServerPlayer player, CustomPacketPayload payload) {
        if (player == null || payload == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        playerQueues.computeIfAbsent(playerId, k -> new ArrayList<>()).add(payload);
    }
    
    /**
     * Immediately sends a payload without batching.
     * Use this for urgent payloads that can't wait for batching.
     * 
     * @param player The server player to send to
     * @param payload The payload to send
     */
    public static void sendImmediate(ServerPlayer player, CustomPacketPayload payload) {
        if (player == null || payload == null) {
            return;
        }
        
        try {
            PacketDistributor.sendToPlayer(player, payload);
        } catch (Exception e) {
            LOGGER.error("Error sending immediate payload to player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
        }
    }
    
    /**
     * Flushes all queued payloads for a specific player.
     * Called at the end of each tick or when needed.
     * 
     * @param player The server player
     */
    public static void flushPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        List<CustomPacketPayload> queue = playerQueues.remove(playerId);
        
        if (queue == null || queue.isEmpty()) {
            return;
        }
        
        // Throttle: Check if we should send (respect minimum interval)
        Long lastSend = lastSendTime.get(playerId);
        long currentTime = System.currentTimeMillis();
        if (lastSend != null && (currentTime - lastSend) < MIN_SEND_INTERVAL) {
            // Too soon, re-queue for next flush
            playerQueues.put(playerId, queue);
            return;
        }
        
        // Send all queued payloads in batch
        // Only update send time if all payloads were sent successfully
        boolean allSent = true;
        try {
            for (CustomPacketPayload payload : queue) {
                try {
                    PacketDistributor.sendToPlayer(player, payload);
                } catch (Exception e) {
                    LOGGER.error("Error sending payload to player {}: {}", 
                        player.getName().getString(), e.getMessage(), e);
                    allSent = false;
                    // Continue sending other payloads even if one fails
                }
            }
            if (allSent) {
                lastSendTime.put(playerId, currentTime);
            }
        } catch (Exception e) {
            LOGGER.error("Error flushing payloads to player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
        }
    }
    
    /**
     * Flushes all queued payloads for all players.
     * Should be called at the end of each server tick.
     * Note: This method requires player instances, so it's recommended to use flushPlayer() instead.
     */
    public static void flushAll() {
        // Note: This method is kept for potential future use
        // Currently, flushing is handled per-player via flushPlayer() in NetworkBatchTickHandler
    }
    
    /**
     * Clears all queued payloads for a player (e.g., on disconnect).
     * 
     * @param playerId The player UUID
     */
    public static void clearPlayer(UUID playerId) {
        if (playerId != null) {
            playerQueues.remove(playerId);
            lastSendTime.remove(playerId);
        }
    }
    
    /**
     * Clears all queued payloads (e.g., on server shutdown).
     */
    public static void clearAll() {
        playerQueues.clear();
        lastSendTime.clear();
    }
    
    /**
     * Gets the number of queued payloads for a player.
     * 
     * @param playerId The player UUID
     * @return Number of queued payloads
     */
    public static int getQueueSize(UUID playerId) {
        List<CustomPacketPayload> queue = playerQueues.get(playerId);
        return queue != null ? queue.size() : 0;
    }
}

