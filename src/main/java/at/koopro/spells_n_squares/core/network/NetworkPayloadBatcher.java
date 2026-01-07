package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

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
    
    // Track queue sizes for adaptive batching (used for trend analysis)
    private static final Map<UUID, Integer> queueSizeHistory = new ConcurrentHashMap<>();
    
    // Track total payloads sent for monitoring
    private static long totalPayloadsSent = 0;
    private static long totalBatchesSent = 0;
    
    // Minimum time between sends (milliseconds) - prevents spam
    private static final long MIN_SEND_INTERVAL = 50; // 50ms = 20 sends per second max
    
    // Maximum batch size - prevents oversized packets
    private static final int MAX_BATCH_SIZE = 50;
    
    // Target batch size for optimal performance
    private static final int TARGET_BATCH_SIZE = 20;
    
    // Adaptive interval adjustment based on queue size
    private static final long ADAPTIVE_INTERVAL_MIN = 30; // 30ms minimum
    private static final long ADAPTIVE_INTERVAL_MAX = 100; // 100ms maximum
    
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
        playerQueues.computeIfAbsent(playerId, k -> CollectionFactory.createList()).add(payload);
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
     * Uses adaptive batching based on queue size and network conditions.
     * 
     * @param player The server player
     */
    public static void flushPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        List<CustomPacketPayload> queue = playerQueues.get(playerId);
        
        if (queue == null || queue.isEmpty()) {
            return;
        }
        
        // Adaptive batching: Calculate optimal send interval based on queue size
        int queueSize = queue.size();
        long adaptiveInterval = calculateAdaptiveInterval(playerId, queueSize);
        
        // Throttle: Check if we should send (respect adaptive interval)
        Long lastSend = lastSendTime.get(playerId);
        long currentTime = System.currentTimeMillis();
        if (lastSend != null && (currentTime - lastSend) < adaptiveInterval) {
            // Too soon, wait for next flush
            // But if queue is getting too large, force flush to prevent overflow
            if (queueSize >= MAX_BATCH_SIZE) {
                LOGGER.warn("Queue size {} exceeds max batch size for player {}, forcing flush", 
                    queueSize, player.getName().getString());
                // Force flush by removing from queue and sending
                playerQueues.remove(playerId);
            } else {
                return; // Re-queue for next flush
            }
        } else {
            // Remove from queue before sending
            playerQueues.remove(playerId);
        }
        
        // Split into batches if queue is too large
        List<List<CustomPacketPayload>> batches = splitIntoBatches(queue, TARGET_BATCH_SIZE);
        
        // Send all batches
        boolean allSent = true;
        try {
            for (List<CustomPacketPayload> batch : batches) {
                for (CustomPacketPayload payload : batch) {
                    try {
                        PacketDistributor.sendToPlayer(player, payload);
                    } catch (Exception e) {
                        LOGGER.error("Error sending payload to player {}: {}", 
                            player.getName().getString(), e.getMessage(), e);
                        allSent = false;
                        // Continue sending other payloads even if one fails
                    }
                }
            }
            if (allSent) {
                lastSendTime.put(playerId, currentTime);
                // Update queue size history for adaptive batching
                queueSizeHistory.put(playerId, queueSize);
                // Update statistics
                totalPayloadsSent += queue.size();
                totalBatchesSent += batches.size();
            }
        } catch (Exception e) {
            LOGGER.error("Error flushing payloads to player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
        }
    }
    
    /**
     * Calculates adaptive send interval based on queue size and history.
     * Larger queues get shorter intervals to prevent overflow.
     * 
     * @param playerId The player UUID
     * @param currentQueueSize Current queue size
     * @return Adaptive interval in milliseconds
     */
    private static long calculateAdaptiveInterval(UUID playerId, int currentQueueSize) {
        // Base interval
        long baseInterval = MIN_SEND_INTERVAL;
        
        // Adjust based on queue size
        if (currentQueueSize > TARGET_BATCH_SIZE) {
            // Queue is large, reduce interval to flush faster
            float ratio = (float) currentQueueSize / TARGET_BATCH_SIZE;
            baseInterval = (long) (MIN_SEND_INTERVAL / Math.min(ratio, 2.0f)); // Max 2x speedup
            baseInterval = Math.max(baseInterval, ADAPTIVE_INTERVAL_MIN);
        } else if (currentQueueSize < TARGET_BATCH_SIZE / 2) {
            // Queue is small, can wait longer
            baseInterval = (long) (MIN_SEND_INTERVAL * 1.5f);
            baseInterval = Math.min(baseInterval, ADAPTIVE_INTERVAL_MAX);
        }
        
        return baseInterval;
    }
    
    /**
     * Splits a queue into batches of optimal size.
     * 
     * @param queue The queue to split
     * @param batchSize Target batch size
     * @return List of batches
     */
    private static List<List<CustomPacketPayload>> splitIntoBatches(
            List<CustomPacketPayload> queue, int batchSize) {
        List<List<CustomPacketPayload>> batches = CollectionFactory.createList();
        
        for (int i = 0; i < queue.size(); i += batchSize) {
            int end = Math.min(i + batchSize, queue.size());
            batches.add(CollectionFactory.createListFrom(queue.subList(i, end)));
        }
        
        return batches;
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
    
    /**
     * Gets statistics about network batching performance.
     * 
     * @return A string with statistics (for debugging/monitoring)
     */
    public static String getStatistics() {
        return String.format("Total payloads sent: %d, Total batches: %d, Active queues: %d", 
            totalPayloadsSent, totalBatchesSent, playerQueues.size());
    }
    
    /**
     * Resets statistics counters.
     */
    public static void resetStatistics() {
        totalPayloadsSent = 0;
        totalBatchesSent = 0;
    }
}

