package at.koopro.spells_n_squares.core.util.network;

import at.koopro.spells_n_squares.core.network.NetworkPayloadBatcher;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Helper class for simplified network payload creation and sending.
 * Provides convenience methods for common network operations.
 */
public final class NetworkHelper {
    
    private NetworkHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Sends a payload to a player immediately.
     * 
     * @param player The server player
     * @param payload The payload to send
     */
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (player == null || payload == null) {
            return;
        }
        
        PacketDistributor.sendToPlayer(player, payload);
    }
    
    /**
     * Sends a payload to all players.
     * 
     * @param payload The payload to send
     */
    public static void sendToAll(CustomPacketPayload payload) {
        if (payload == null) {
            return;
        }
        
        PacketDistributor.sendToAllPlayers(payload);
    }
    
    /**
     * Queues a payload to be sent to a player (batched).
     * 
     * @param player The server player
     * @param payload The payload to queue
     */
    public static void queueToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (player == null || payload == null) {
            return;
        }
        
        NetworkPayloadBatcher.queuePayload(player, payload);
    }
    
    /**
     * Sends a payload to all players in a dimension.
     * Note: Simplified implementation - requires server level access for full functionality.
     * 
     * @param dimension The dimension key
     * @param payload The payload to send
     */
    public static void sendToDimension(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension, 
                                       CustomPacketPayload payload) {
        if (dimension == null || payload == null) {
            return;
        }
        
        // Send to all players - dimension filtering would require server level access
        sendToAll(payload);
    }
    
    /**
     * Sends a payload to all players near a position.
     * Note: Simplified implementation - requires server level access for full functionality.
     * 
     * @param pos The position
     * @param radius The radius
     * @param payload The payload to send
     */
    public static void sendToNearby(net.minecraft.core.BlockPos pos, double radius, 
                                    CustomPacketPayload payload) {
        if (pos == null || payload == null) {
            return;
        }
        
        // Send to all players - position filtering would require server level access
        sendToAll(payload);
    }
}

