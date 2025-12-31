package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.data.SyncableDataComponent;
import at.koopro.spells_n_squares.core.util.PlayerDataSyncUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Registry for data components that can be synced from server to client.
 * Provides centralized synchronization for all registered syncable data components.
 */
public final class SyncableDataRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<SyncableDataComponent> syncableComponents = new LinkedHashSet<>();
    
    private SyncableDataRegistry() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Registers a syncable data component.
     * Registered components will be automatically synced on player join if
     * {@link SyncableDataComponent#shouldSyncOnJoin()} returns true.
     * 
     * @param component The syncable data component to register
     */
    public static void register(SyncableDataComponent component) {
        if (component != null) {
            syncableComponents.add(component);
            LOGGER.debug("Registered syncable data component: {}", component.getSyncName());
        }
    }
    
    /**
     * Syncs all registered data components to a client player.
     * Only syncs components where {@link SyncableDataComponent#shouldSyncOnJoin()} returns true.
     * 
     * @param serverPlayer The server player to sync data for
     */
    public static void syncAllToClient(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            LOGGER.warn("Attempted to sync data to null player");
            return;
        }
        
        for (SyncableDataComponent component : syncableComponents) {
            if (!component.shouldSyncOnJoin()) {
                continue;
            }
            
            try {
                CustomPacketPayload payload = component.createSyncPayload(serverPlayer);
                if (payload != null) {
                    PlayerDataSyncUtils.syncToClient(serverPlayer, payload, component.getSyncName());
                }
            } catch (Exception e) {
                LOGGER.error("Error syncing {} to player {}: {}", 
                    component.getSyncName(), serverPlayer.getName().getString(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Syncs a specific data component to a client player.
     * 
     * @param serverPlayer The server player to sync data for
     * @param component The component to sync
     */
    public static void syncComponentToClient(ServerPlayer serverPlayer, SyncableDataComponent component) {
        if (serverPlayer == null || component == null) {
            return;
        }
        
        try {
            CustomPacketPayload payload = component.createSyncPayload(serverPlayer);
            if (payload != null) {
                PlayerDataSyncUtils.syncToClient(serverPlayer, payload, component.getSyncName());
            }
        } catch (Exception e) {
            LOGGER.error("Error syncing {} to player {}: {}", 
                component.getSyncName(), serverPlayer.getName().getString(), e.getMessage(), e);
        }
    }
    
    /**
     * Gets all registered syncable data components.
     * 
     * @return A copy of the registered components list
     */
    public static List<SyncableDataComponent> getRegisteredComponents() {
        return new ArrayList<>(syncableComponents);
    }
    
    /**
     * Unregisters a syncable data component.
     * 
     * @param component The component to unregister
     */
    public static void unregister(SyncableDataComponent component) {
        if (component != null) {
            syncableComponents.remove(component);
            LOGGER.debug("Unregistered syncable data component: {}", component.getSyncName());
        }
    }
}

