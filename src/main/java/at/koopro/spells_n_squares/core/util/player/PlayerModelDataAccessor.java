package at.koopro.spells_n_squares.core.util.player;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles reading and writing player model data from persistent storage.
 * Separated from PlayerModelUtils for better organization.
 */
public final class PlayerModelDataAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private PlayerModelDataAccessor() {
        // Utility class - prevent instantiation
    }
    
    private static final String DATA_KEY = "spells_n_squares:player_model_data";
    
    // Client-side storage for model data (synced from server)
    private static final Map<UUID, PlayerModelDataComponent.PlayerModelData> CLIENT_MODEL_DATA = new ConcurrentHashMap<>();
    
    /**
     * Gets the player model data for a player or dummy player entity.
     * 
     * @param entity The player or dummy player entity
     * @return The player model data, or default if not found
     */
    public static PlayerModelDataComponent.PlayerModelData getModelData(net.minecraft.world.entity.Entity entity) {
        // Support DummyPlayerEntity
        if (entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            return dummyPlayer.getModelData();
        }
        // Support Player
        if (entity instanceof Player player) {
            return getModelData(player);
        }
        return PlayerModelDataComponent.PlayerModelData.empty();
    }
    
    /**
     * Gets the player model data for a player.
     * 
     * @param player The player
     * @return The player model data, or default if not found
     */
    public static PlayerModelDataComponent.PlayerModelData getModelData(Player player) {
        if (player == null) {
            LOGGER.info("[PlayerModelDataAccessor] getModelData: Player is null");
            DevLogger.logStateChange(PlayerModelDataAccessor.class, "getModelData", "Player is null");
            return PlayerModelDataComponent.PlayerModelData.empty();
        }
        
        // Check if player is fully initialized (has a gameProfile)
        // During construction, gameProfile may be null, so we return empty data
        try {
            if (player.getGameProfile() == null) {
                LOGGER.info("[PlayerModelDataAccessor] getModelData: Player gameProfile is null (not fully initialized), returning empty data");
                return PlayerModelDataComponent.PlayerModelData.empty();
            }
        } catch (Exception e) {
            // If we can't access gameProfile, player is not initialized, return empty data
            LOGGER.info("[PlayerModelDataAccessor] getModelData: Player not fully initialized (exception accessing gameProfile), returning empty data");
            return PlayerModelDataComponent.PlayerModelData.empty();
        }
        
        boolean isClient = PlayerValidationUtils.isClientSide(player);
        
        if (isClient) {
            UUID playerUUID = player.getUUID();
            PlayerModelDataComponent.PlayerModelData clientData = CLIENT_MODEL_DATA.getOrDefault(playerUUID, PlayerModelDataComponent.PlayerModelData.empty());
            
            // Only log if data is non-default (to reduce log spam during rendering)
            if (clientData.scale() != 1.0f || clientData.hitboxScale() != 1.0f || 
                clientData.bodyScale() != 1.0f || clientData.headScale() != 1.0f) {
                LOGGER.debug("[PlayerModelDataAccessor] getModelData (CLIENT): player={}, scale={}, hitboxScale={}, bodyScale={}, headScale={}", 
                    player.getName().getString(), clientData.scale(), clientData.hitboxScale(), clientData.bodyScale(), clientData.headScale());
            }
            
            return clientData;
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            DevLogger.logStateChange(PlayerModelDataAccessor.class, "getModelData", 
                "No data found in persistent data, returning empty");
            return PlayerModelDataComponent.PlayerModelData.empty();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            DevLogger.logStateChange(PlayerModelDataAccessor.class, "getModelData", 
                "Tag is empty, returning empty");
            return PlayerModelDataComponent.PlayerModelData.empty();
        }
        
        try {
            var result = PlayerModelDataComponent.PlayerModelData.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(PlayerModelDataComponent.PlayerModelData.empty());
            
            // Only log if data is non-default
            if (result.scale() != 1.0f || result.hitboxScale() != 1.0f || result.bodyScale() != 1.0f) {
                LOGGER.debug("[PlayerModelDataAccessor] getModelData (SERVER): Loaded data: scale={}, hitboxScale={}, bodyScale={}", 
                    result.scale(), result.hitboxScale(), result.bodyScale());
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.error("[PlayerModelDataAccessor] getModelData: Failed to parse data", e);
            DevLogger.logError(PlayerModelDataAccessor.class, "getModelData", 
                "Failed to parse data: " + e.getMessage(), e);
            return PlayerModelDataComponent.PlayerModelData.empty();
        }
    }
    
    /**
     * Sets the player model data for a player.
     * 
     * @param player The player
     * @param data The model data to set
     */
    public static void setModelData(Player player, PlayerModelDataComponent.PlayerModelData data) {
        if (player == null || data == null) {
            DevLogger.logStateChange(PlayerModelDataAccessor.class, "setModelData", 
                "Player or data is null");
            return;
        }
        
        boolean isClient = PlayerValidationUtils.isClientSide(player);
        
        if (isClient) {
            return; // Only set on server
        }
        
        try {
            var result = PlayerModelDataComponent.PlayerModelData.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(DATA_KEY, tag);
                // Only log if data is non-default
                if (data.scale() != 1.0f || data.hitboxScale() != 1.0f || data.bodyScale() != 1.0f) {
                    LOGGER.debug("[PlayerModelDataAccessor] setModelData: Saved model data for player {}: scale={}, hitboxScale={}, bodyScale={}", 
                        player.getName().getString(), data.scale(), data.hitboxScale(), data.bodyScale());
                }
            });
            
            if (result.result().isEmpty()) {
                DevLogger.logError(PlayerModelDataAccessor.class, "setModelData", 
                    "Failed to encode data - result is empty", null);
            }
        } catch (Exception e) {
            DevLogger.logError(PlayerModelDataAccessor.class, "setModelData", 
                "Failed to save data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates model data on the client side (called from network handler).
     * 
     * @param playerUUID The UUID of the player
     * @param data The model data
     */
    public static void updateClientModelData(UUID playerUUID, PlayerModelDataComponent.PlayerModelData data) {
        if (playerUUID == null || data == null) {
            return;
        }
        
        CLIENT_MODEL_DATA.put(playerUUID, data);
        
        // Only log if data is non-default
        if (data.scale() != 1.0f || data.hitboxScale() != 1.0f || data.bodyScale() != 1.0f || data.headScale() != 1.0f) {
            LOGGER.debug("[PlayerModelDataAccessor] updateClientModelData: Updated client model data for UUID {}: scale={}, hitboxScale={}, bodyScale={}, headScale={}", 
                playerUUID, data.scale(), data.hitboxScale(), data.bodyScale(), data.headScale());
        }
    }
    
    /**
     * Gets the client-side model data map (for internal use).
     */
    static Map<UUID, PlayerModelDataComponent.PlayerModelData> getClientModelData() {
        return CLIENT_MODEL_DATA;
    }
}

