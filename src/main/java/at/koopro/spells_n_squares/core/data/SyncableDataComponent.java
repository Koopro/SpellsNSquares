package at.koopro.spells_n_squares.core.data;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Interface for data components that can be synced from server to client.
 * Implementing this interface allows data systems to be automatically registered
 * with the sync system and ensures consistent synchronization behavior.
 * 
 * <p>Data systems implementing this interface should:
 * <ul>
 *   <li>Create a network payload type for their data</li>
 *   <li>Implement {@link #createSyncPayload(ServerPlayer)} to create the payload</li>
 *   <li>Implement {@link #applySyncPayload(CustomPacketPayload, Player)} to handle client-side updates</li>
 *   <li>Register the payload handler in {@link at.koopro.spells_n_squares.core.network.ModNetwork}</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * public class MyData implements SyncableDataComponent {
 *     public CustomPacketPayload createSyncPayload(ServerPlayer player) {
 *         MyDataComponent data = getMyData(player);
 *         return new MyDataSyncPayload(data);
 *     }
 *     
 *     public void applySyncPayload(CustomPacketPayload payload, Player player) {
 *         if (payload instanceof MyDataSyncPayload myPayload) {
 *             updateClientCache(player.getUUID(), myPayload.getData());
 *         }
 *     }
 * }
 * }</pre>
 */
public interface SyncableDataComponent {
    /**
     * Creates a network payload containing the data to sync for the given player.
     * This is called on the server when syncing data to a client.
     * 
     * @param serverPlayer The server player whose data should be synced
     * @return A network payload containing the data, or null if no sync is needed
     */
    CustomPacketPayload createSyncPayload(ServerPlayer serverPlayer);
    
    /**
     * Applies a synced payload to update client-side data.
     * This is called on the client when receiving a sync payload from the server.
     * 
     * @param payload The payload received from the server
     * @param player The client player whose data should be updated
     */
    void applySyncPayload(CustomPacketPayload payload, Player player);
    
    /**
     * Gets the name of this data component for logging purposes.
     * 
     * @return A human-readable name for this data component
     */
    default String getSyncName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Determines whether this data component should sync on player join.
     * 
     * @return true if data should sync on join, false otherwise
     */
    default boolean shouldSyncOnJoin() {
        return true;
    }
}

