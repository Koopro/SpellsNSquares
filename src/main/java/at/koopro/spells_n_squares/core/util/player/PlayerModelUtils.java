package at.koopro.spells_n_squares.core.util.player;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.network.PlayerModelSyncPayload;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for player model modifications.
 * Provides methods for scaling player size, hitbox, and individual body parts.
 */
public final class PlayerModelUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private PlayerModelUtils() {
        // Utility class - prevent instantiation
    }
    
    private static final String DATA_KEY = "spells_n_squares:player_model_data";
    
    // Client-side storage for model data (synced from server)
    private static final Map<UUID, PlayerModelDataComponent.PlayerModelData> CLIENT_MODEL_DATA = new ConcurrentHashMap<>();
    
    /**
     * Gets the player model data for a player or dummy player entity.
     * Delegates to PlayerModelDataAccessor.
     * 
     * @param entity The player or dummy player entity
     * @return The player model data, or default if not found
     */
    public static PlayerModelDataComponent.PlayerModelData getModelData(net.minecraft.world.entity.Entity entity) {
        return PlayerModelDataAccessor.getModelData(entity);
    }
    
    /**
     * Gets the player model data for a player.
     * Delegates to PlayerModelDataAccessor.
     * 
     * @param player The player
     * @return The player model data, or default if not found
     */
    public static PlayerModelDataComponent.PlayerModelData getModelData(Player player) {
        return PlayerModelDataAccessor.getModelData(player);
    }
    
    /**
     * Sets the player model data for a player or dummy player entity.
     */
    public static void setModelData(net.minecraft.world.entity.Entity entity, PlayerModelDataComponent.PlayerModelData data) {
        // Support DummyPlayerEntity
        if (entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            dummyPlayer.setModelData(data);
            return;
        }
        // Support Player
        if (entity instanceof Player player) {
            setModelData(player, data);
        }
    }
    
    /**
     * Sets the player model data for a player.
     */
    public static void setModelData(Player player, PlayerModelDataComponent.PlayerModelData data) {
        if (player == null || data == null) {
            DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                "Player or data is null");
            return;
        }
        
        boolean isClient = PlayerValidationUtils.isClientSide(player);
        LOGGER.info("[PlayerModelUtils] setModelData: player={}, isClient={}, scale={}, hitboxScale={}, bodyScale={}", 
            player.getName().getString(), isClient, data.scale(), data.hitboxScale(), data.bodyScale());
        DevLogger.logMethodEntry(PlayerModelUtils.class, "setModelData", 
            "player=" + player.getName().getString() + 
            ", isClient=" + isClient + 
            ", scale=" + data.scale() + 
            ", hitboxScale=" + data.hitboxScale() + 
            ", bodyScale=" + data.bodyScale());
        
        if (isClient) {
            LOGGER.info("[PlayerModelUtils] setModelData: Client side - skipping (only set on server)");
            DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                "Client side - skipping (only set on server)");
            return; // Only set on server
        }
        
        try {
            LOGGER.info("[PlayerModelUtils] setModelData: Encoding data: scale={}, hitboxScale={}, bodyScale={}, headScale={}", 
                data.scale(), data.hitboxScale(), data.bodyScale(), data.headScale());
            DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                "Encoding data: scale=" + data.scale() +
                ", hitboxScale=" + data.hitboxScale() +
                ", bodyScale=" + data.bodyScale() +
                ", headScale=" + data.headScale() +
                ", leftArmScale=" + data.leftArmScale() +
                ", rightArmScale=" + data.rightArmScale() +
                ", leftLegScale=" + data.leftLegScale() +
                ", rightLegScale=" + data.rightLegScale() +
                ", width=" + data.width() +
                ", height=" + data.height());
            
            var result = PlayerModelDataComponent.PlayerModelData.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                LOGGER.info("[PlayerModelUtils] setModelData: Encoded data successfully, saving to persistent data for player: {}", 
                    player.getName().getString());
                DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                    "Encoded data successfully, saving to persistent data for player: " + player.getName().getString());
                player.getPersistentData().put(DATA_KEY, tag);
                boolean isEmpty = (tag instanceof CompoundTag compoundTag) ? compoundTag.isEmpty() : true;
                LOGGER.info("[PlayerModelUtils] setModelData: Data saved to persistent data, tag isEmpty: {}", isEmpty);
                DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                    "Data saved to persistent data, tag isEmpty: " + isEmpty);
                
                // Refresh dimensions to apply changes
                if (player instanceof ServerPlayer serverPlayer) {
                    LOGGER.info("[PlayerModelUtils] setModelData: Player is ServerPlayer, refreshing dimensions and sending sync packet");
                    DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                        "Player is ServerPlayer, refreshing dimensions and sending sync packet");
                    refreshPlayerDimensions(serverPlayer);
                    // Send sync packet to client
                    sendModelDataToClient(serverPlayer, data);
                } else {
                    LOGGER.info("[PlayerModelUtils] setModelData: Player is not ServerPlayer, skipping sync");
                    DevLogger.logStateChange(PlayerModelUtils.class, "setModelData", 
                        "Player is not ServerPlayer, skipping sync");
                }
            });
            
            if (result.result().isEmpty()) {
                DevLogger.logError(PlayerModelUtils.class, "setModelData", 
                    "Failed to encode data - result is empty", null);
            }
        } catch (Exception e) {
            DevLogger.logError(PlayerModelUtils.class, "setModelData", 
                "Failed to save data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Sets the overall player scale.
     * Delegates to PlayerModelScaler.
     * 
     * @param player The player
     * @param scale The scale (1.0 = normal, 0.5 = half size, 2.0 = double)
     */
    public static void setPlayerScale(Player player, float scale) {
        PlayerModelScaler.setPlayerScale(player, scale);
    }
    
    /**
     * Sets the head scale. Delegates to PlayerModelScaler.
     */
    public static void setHeadScale(Player player, float scale) {
        PlayerModelScaler.setHeadScale(player, scale);
    }
    
    /**
     * Sets the body scale. Delegates to PlayerModelScaler.
     */
    public static void setBodyScale(Player player, float scale) {
        PlayerModelScaler.setBodyScale(player, scale);
    }
    
    /**
     * Sets the left arm scale. Delegates to PlayerModelScaler.
     */
    public static void setLeftArmScale(Player player, float scale) {
        PlayerModelScaler.setLeftArmScale(player, scale);
    }
    
    /**
     * Sets the right arm scale. Delegates to PlayerModelScaler.
     */
    public static void setRightArmScale(Player player, float scale) {
        PlayerModelScaler.setRightArmScale(player, scale);
    }
    
    /**
     * Sets the left leg scale. Delegates to PlayerModelScaler.
     */
    public static void setLeftLegScale(Player player, float scale) {
        PlayerModelScaler.setLeftLegScale(player, scale);
    }
    
    /**
     * Sets the right leg scale. Delegates to PlayerModelScaler.
     */
    public static void setRightLegScale(Player player, float scale) {
        PlayerModelScaler.setRightLegScale(player, scale);
    }
    
    /**
     * Sets the scale for a specific body part. Delegates to PlayerModelScaler.
     */
    public static void setBodyPartScale(Player player, PlayerModelDataComponent.BodyPart part, float scale) {
        PlayerModelScaler.setBodyPartScale(player, part, scale);
    }
    
    /**
     * Sets the hitbox scale. Delegates to PlayerModelScaler.
     */
    public static void setHitboxScale(Player player, float scale) {
        PlayerModelScaler.setHitboxScale(player, scale);
    }
    
    /**
     * Resets all player model modifications. Delegates to PlayerModelScaler.
     */
    public static void resetPlayerModel(Player player) {
        PlayerModelScaler.resetPlayerModel(player);
    }
    
    /**
     * Resets a specific body part. Delegates to PlayerModelScaler.
     */
    public static void resetBodyPart(Player player, PlayerModelDataComponent.BodyPart part) {
        PlayerModelScaler.resetBodyPart(player, part);
    }
    
    /**
     * Gets the current overall player scale. Delegates to PlayerModelScaler.
     */
    public static float getPlayerScale(Player player) {
        return PlayerModelScaler.getPlayerScale(player);
    }
    
    /**
     * Gets the current head scale. Delegates to PlayerModelScaler.
     */
    public static float getHeadScale(Player player) {
        return PlayerModelScaler.getHeadScale(player);
    }
    
    /**
     * Gets the scale for a specific body part. Delegates to PlayerModelScaler.
     */
    public static float getBodyPartScale(Player player, PlayerModelDataComponent.BodyPart part) {
        return PlayerModelScaler.getBodyPartScale(player, part);
    }
    
    /**
     * Gets the scaled dimensions for a player or dummy player entity.
     * 
     * @param entity The player or dummy player entity
     * @param pose The pose
     * @return The scaled dimensions
     */
    public static EntityDimensions getScaledDimensions(net.minecraft.world.entity.Entity entity, Pose pose) {
        // Support DummyPlayerEntity
        if (entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer) {
            PlayerModelDataComponent.PlayerModelData data = dummyPlayer.getModelData();
            EntityDimensions baseDimensions = entity.getType().getDimensions();
            
            float overallScale = data.scale();
            float hitboxScale = data.hitboxScale();
            float width = baseDimensions.width() * overallScale * hitboxScale;
            float height = baseDimensions.height() * overallScale * hitboxScale;
            
            // Apply custom width/height if set
            if (data.width() != null) {
                width = data.width();
            }
            if (data.height() != null) {
                height = data.height();
            }
            
            return EntityDimensions.scalable(width, height);
        }
        // Support Player
        if (entity instanceof Player player) {
            return getScaledDimensions(player, pose);
        }
        return EntityDimensions.scalable(0.6f, 1.8f);
    }
    
    /**
     * Gets the scaled dimensions for a player.
     * 
     * @param player The player
     * @param pose The pose
     * @return The scaled dimensions
     */
    public static EntityDimensions getScaledDimensions(Player player, Pose pose) {
        if (player == null) {
            LOGGER.info("[PlayerModelUtils] getScaledDimensions: Player is null, returning default");
            DevLogger.logStateChange(PlayerModelUtils.class, "getScaledDimensions", "Player is null, returning default");
            return EntityDimensions.scalable(0.6f, 1.8f);
        }
        
        PlayerModelDataComponent.PlayerModelData data = getModelData(player);
        EntityDimensions baseDimensions = player.getType().getDimensions();
        
        String playerName = "Unknown";
        try {
            playerName = player.getName().getString();
        } catch (Exception e) {
            // Player name not available yet, use default
        }
        
        float overallScale = data.scale();
        float hitboxScale = data.hitboxScale();
        
        LOGGER.info("[PlayerModelUtils] getScaledDimensions: player={}, pose={}, baseDimensions={}x{}, overallScale={}, hitboxScale={}", 
            playerName, pose, baseDimensions.width(), baseDimensions.height(), overallScale, hitboxScale);
        DevLogger.logMethodEntry(PlayerModelUtils.class, "getScaledDimensions", 
            "player=" + playerName + 
            ", pose=" + pose + 
            ", baseDimensions=" + baseDimensions.width() + "x" + baseDimensions.height() +
            ", overallScale=" + overallScale +
            ", hitboxScale=" + hitboxScale);
        
        // Hitbox MUST scale with overallScale (from scale command)
        // The scale command sets overallScale, and the hitbox should always scale proportionally
        // Formula: hitbox = baseDimensions * overallScale * hitboxScale
        // - overallScale: from the scale command, ALWAYS affects hitbox (this is the main scale)
        // - hitboxScale: additional multiplier for fine-tuning (defaults to 1.0)
        // 
        // IMPORTANT: The hitbox scales with overallScale from the scale command.
        // When you use /scale <value>, it sets overallScale, and the hitbox will scale proportionally.
        float baseWidth = baseDimensions.width();
        float baseHeight = baseDimensions.height();
        float width = baseWidth * overallScale * hitboxScale;
        float height = baseHeight * overallScale * hitboxScale;
        
        LOGGER.info("[PlayerModelUtils] getScaledDimensions: Hitbox calculation - base={}x{} * overallScale={} * hitboxScale={} = {}x{}", 
            baseWidth, baseHeight, overallScale, hitboxScale, width, height);
        DevLogger.logStateChange(PlayerModelUtils.class, "getScaledDimensions", 
            "Calculated overallScale=" + overallScale + 
            ", hitboxScale=" + hitboxScale + 
            ", initial width=" + width + 
            ", initial height=" + height);
        
        // Apply custom width/height if set
        if (data.width() != null) {
            width = data.width();
            LOGGER.info("[PlayerModelUtils] getScaledDimensions: Using custom width: {}", width);
            DevLogger.logStateChange(PlayerModelUtils.class, "getScaledDimensions", 
                "Using custom width: " + width);
        }
        if (data.height() != null) {
            height = data.height();
            LOGGER.info("[PlayerModelUtils] getScaledDimensions: Using custom height: {}", height);
            DevLogger.logStateChange(PlayerModelUtils.class, "getScaledDimensions", 
                "Using custom height: " + height);
        }
        
        EntityDimensions result = EntityDimensions.scalable(width, height);
        LOGGER.info("[PlayerModelUtils] getScaledDimensions: Final dimensions: {}x{}", result.width(), result.height());
        DevLogger.logReturnValue(PlayerModelUtils.class, "getScaledDimensions", 
            "Final dimensions: " + result.width() + "x" + result.height());
        
        return result;
    }
    
    /**
     * Refreshes player dimensions to apply scale changes.
     * 
     * @param player The server player
     */
    private static void refreshPlayerDimensions(ServerPlayer player) {
        if (player == null) {
            return;
        }
        
        LOGGER.info("[PlayerModelUtils] refreshPlayerDimensions: Refreshing dimensions for player: {}", player.getName().getString());
        // Force dimension refresh by changing pose temporarily and accessing dimensions
        // This forces getDimensions to be called
        Pose currentPose = player.getPose();
        try {
            // Temporarily change pose to force dimension recalculation
            player.setPose(Pose.STANDING);
            player.refreshDimensions();
            // Force getDimensions to be called by accessing it
            EntityDimensions dims1 = player.getDimensions(Pose.STANDING);
            LOGGER.info("[PlayerModelUtils] refreshPlayerDimensions: Got dimensions for STANDING: {}x{}", dims1.width(), dims1.height());
            
            // Restore original pose
            player.setPose(currentPose);
            player.refreshDimensions();
            // Force getDimensions to be called again
            EntityDimensions dims2 = player.getDimensions(currentPose);
            LOGGER.info("[PlayerModelUtils] refreshPlayerDimensions: Got dimensions for {}: {}x{}", currentPose, dims2.width(), dims2.height());
            
            // Also trigger bounding box recalculation
            player.setBoundingBox(player.getBoundingBox());
            LOGGER.info("[PlayerModelUtils] refreshPlayerDimensions: Dimensions refreshed, current pose: {}", currentPose);
        } catch (Exception e) {
            LOGGER.error("[PlayerModelUtils] refreshPlayerDimensions: Error refreshing dimensions", e);
            // Fallback to simple refresh
            player.refreshDimensions();
        }
    }
    
    /**
     * Sends model data to the client via network.
     * 
     * @param serverPlayer The server player
     * @param data The model data to send
     */
    private static void sendModelDataToClient(ServerPlayer serverPlayer, PlayerModelDataComponent.PlayerModelData data) {
        if (serverPlayer == null || data == null) {
            LOGGER.info("[PlayerModelUtils] sendModelDataToClient: ServerPlayer or data is null, skipping send");
            DevLogger.logStateChange(PlayerModelUtils.class, "sendModelDataToClient", 
                "ServerPlayer or data is null, skipping send");
            return;
        }
        
        try {
            LOGGER.info("[PlayerModelUtils] sendModelDataToClient: player={}, UUID={}, scale={}, hitboxScale={}", 
                serverPlayer.getName().getString(), serverPlayer.getUUID(), data.scale(), data.hitboxScale());
            DevLogger.logMethodEntry(PlayerModelUtils.class, "sendModelDataToClient", 
                "player=" + serverPlayer.getName().getString() +
                ", UUID=" + serverPlayer.getUUID() +
                ", scale=" + data.scale() +
                ", hitboxScale=" + data.hitboxScale());
            
            PlayerModelSyncPayload payload = PlayerModelSyncPayload.from(data);
            LOGGER.info("[PlayerModelUtils] sendModelDataToClient: Created payload: scale={}, hitboxScale={}, sending to client...", 
                payload.scale(), payload.hitboxScale());
            DevLogger.logStateChange(PlayerModelUtils.class, "sendModelDataToClient", 
                "Created payload: scale=" + payload.scale() +
                ", hitboxScale=" + payload.hitboxScale() +
                ", sending to client...");
            
            PlayerDataSyncUtils.syncToClientImmediate(serverPlayer, payload, "PlayerModelSyncPayload");
            
            LOGGER.info("[PlayerModelUtils] sendModelDataToClient: Successfully sent PlayerModelSyncPayload to client for player: {}", 
                serverPlayer.getName().getString());
            DevLogger.logStateChange(PlayerModelUtils.class, "sendModelDataToClient", 
                "Successfully sent PlayerModelSyncPayload to client for player: " + serverPlayer.getName().getString());
        } catch (Exception e) {
            LOGGER.error("[PlayerModelUtils] sendModelDataToClient: Failed to send model data to client", e);
            DevLogger.logError(PlayerModelUtils.class, "sendModelDataToClient", 
                "Failed to send model data to client: " + e.getMessage(), e);
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
            LOGGER.info("[PlayerModelUtils] updateClientModelData: playerUUID or data is null, skipping update");
            DevLogger.logStateChange(PlayerModelUtils.class, "updateClientModelData", 
                "playerUUID or data is null, skipping update");
            return;
        }
        
        LOGGER.info("[PlayerModelUtils] updateClientModelData: UUID={}, scale={}, hitboxScale={}, bodyScale={}, headScale={}", 
            playerUUID, data.scale(), data.hitboxScale(), data.bodyScale(), data.headScale());
        DevLogger.logMethodEntry(PlayerModelUtils.class, "updateClientModelData", 
            "UUID=" + playerUUID +
            ", scale=" + data.scale() +
            ", hitboxScale=" + data.hitboxScale() +
            ", bodyScale=" + data.bodyScale() +
            ", headScale=" + data.headScale() +
            ", leftArmScale=" + data.leftArmScale() +
            ", rightArmScale=" + data.rightArmScale() +
            ", leftLegScale=" + data.leftLegScale() +
            ", rightLegScale=" + data.rightLegScale());
        
        PlayerModelDataComponent.PlayerModelData oldData = CLIENT_MODEL_DATA.get(playerUUID);
        CLIENT_MODEL_DATA.put(playerUUID, data);
        
        DevLogger.logStateChange(PlayerModelUtils.class, "updateClientModelData", 
            "Stored in CLIENT_MODEL_DATA - map size: " + CLIENT_MODEL_DATA.size() +
            ", had previous data: " + (oldData != null) +
            ", forcing dimension refresh for local player");
        
        // Force client player to refresh dimensions if it's the local player
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(playerUUID)) {
            LOGGER.info("[PlayerModelUtils] updateClientModelData: This is the local player, forcing dimension refresh");
            DevLogger.logStateChange(PlayerModelUtils.class, "updateClientModelData", 
                "This is the local player, calling refreshDimensions()");
            var localPlayer = Minecraft.getInstance().player;
            Pose currentPose = localPlayer.getPose();
            try {
                // Temporarily change pose to force dimension recalculation
                localPlayer.setPose(Pose.STANDING);
                localPlayer.refreshDimensions();
                // Force getDimensions to be called by accessing it
                EntityDimensions dims1 = localPlayer.getDimensions(Pose.STANDING);
                LOGGER.info("[PlayerModelUtils] updateClientModelData: Got dimensions for STANDING: {}x{}", dims1.width(), dims1.height());
                
                // Restore original pose
                localPlayer.setPose(currentPose);
                localPlayer.refreshDimensions();
                // Force getDimensions to be called again
                EntityDimensions dims2 = localPlayer.getDimensions(currentPose);
                LOGGER.info("[PlayerModelUtils] updateClientModelData: Got dimensions for {}: {}x{}", currentPose, dims2.width(), dims2.height());
                
                // Also trigger bounding box recalculation
                localPlayer.setBoundingBox(localPlayer.getBoundingBox());
                LOGGER.info("[PlayerModelUtils] updateClientModelData: Local player dimensions refreshed");
            } catch (Exception e) {
                LOGGER.error("[PlayerModelUtils] updateClientModelData: Error refreshing local player dimensions", e);
                localPlayer.refreshDimensions();
            }
        }
    }
    
    /**
     * Syncs model data to client (called on player login or when data changes).
     * 
     * @param serverPlayer The server player
     */
    public static void syncModelDataToClient(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            DevLogger.logStateChange(PlayerModelUtils.class, "syncModelDataToClient", 
                "ServerPlayer is null, skipping sync");
            return;
        }
        
        DevLogger.logMethodEntry(PlayerModelUtils.class, "syncModelDataToClient", 
            "player=" + serverPlayer.getName().getString() +
            ", UUID=" + serverPlayer.getUUID());
        
        PlayerModelDataComponent.PlayerModelData data = PlayerModelDataAccessor.getModelData(serverPlayer);
        
        // Always send model data to client, even if it's empty/default
        // This ensures the client knows the state and can initialize its cache
        DevLogger.logStateChange(PlayerModelUtils.class, "syncModelDataToClient", 
            "Sending model data to client: scale=" + data.scale() +
            ", hitboxScale=" + data.hitboxScale() +
            ", bodyScale=" + data.bodyScale());
        sendModelDataToClient(serverPlayer, data);
    }
}


