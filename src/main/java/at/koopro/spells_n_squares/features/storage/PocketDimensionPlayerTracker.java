package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles player entry tracking and persistence for pocket dimensions.
 * Manages player entry data, exit platform checking, and persistent storage.
 */
public final class PocketDimensionPlayerTracker {
    // Map of player UUID to their entry data (dimension key, entry position, spawn position)
    // Thread safety: Accessed only from main server thread
    private static final Map<UUID, at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData> playerEntryMap = new HashMap<>();
    
    // Track last message time per player to prevent spam (cooldown: 3 seconds = 60 ticks)
    // Thread safety: Accessed only from main server thread
    private static final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private static final long MESSAGE_COOLDOWN_TICKS = 60;
    
    private static final String ENTRY_DATA_TAG = "spells_n_squares_pocket_entry";
    private static final String ENTRY_DIMENSION_KEY = "entryDimension";
    private static final String ENTRY_POSITION_KEY = "entryPosition";
    private static final String SPAWN_POSITION_KEY = "spawnPosition";
    private static final String DIMENSION_ID_KEY = "dimensionId";
    
    private PocketDimensionPlayerTracker() {}
    
    /**
     * Stores player entry data when they enter a pocket dimension.
     */
    public static void storePlayerEntry(UUID playerUuid, ResourceKey<Level> entryDimension, 
                                 BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        playerEntryMap.put(playerUuid, new at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData(entryDimension, entryPosition, spawnPosition, dimensionId));
    }

    /**
     * Stores player entry data (including persistent storage) when they enter a pocket dimension.
     */
    public static void storePlayerEntry(ServerPlayer player, ResourceKey<Level> entryDimension,
                                BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        storePlayerEntry(player.getUUID(), entryDimension, entryPosition, spawnPosition, dimensionId);
        savePersistentEntry(player, entryDimension, entryPosition, spawnPosition, dimensionId);
    }
    
    /**
     * Removes player entry data when they exit.
     */
    public static void clearPlayerEntry(UUID playerUuid) {
        playerEntryMap.remove(playerUuid);
    }

    /**
     * Removes player entry data and clears persistent storage.
     */
    public static void clearPlayerEntry(ServerPlayer player) {
        if (player != null) {
            clearPlayerEntry(player.getUUID());
            player.getPersistentData().remove(ENTRY_DATA_TAG);
        }
    }
    
    /**
     * Cleans up all player-related data when a player disconnects.
     * Removes player from all tracking maps to prevent memory leaks.
     */
    public static void clearPlayerData(UUID playerUUID) {
        if (playerUUID == null) {
            return;
        }
        try {
            // Clear player entry data
            playerEntryMap.remove(playerUUID);
            
            // Clear message cooldown
            lastMessageTime.remove(playerUUID);
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error("Error cleaning up pocket dimension data for player {}: {}", 
                playerUUID, e.getMessage(), e);
        }
    }
    
    /**
     * Cleans up all player-related data when a player disconnects.
     */
    public static void clearPlayerData(ServerPlayer player) {
        if (player != null) {
            clearPlayerData(player.getUUID());
        }
    }

    /**
     * Gets player entry data for a player.
     */
    public static at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData getPlayerEntry(UUID playerUuid) {
        return playerEntryMap.get(playerUuid);
    }
    
    /**
     * Gets player entry data, loading from persistent storage if needed.
     */
    public static at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData getPlayerEntry(ServerPlayer player, ResourceKey<Level> sharedDimension) {
        at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData entryData = playerEntryMap.get(player.getUUID());
        if (entryData == null) {
            entryData = loadEntryFromPersistent(player, sharedDimension);
        }
        return entryData;
    }

    /**
     * Checks if a player is at the exit platform and teleports them back if so.
     */
    public static void checkExitPlatform(ServerPlayer player, ServerLevel level, ResourceKey<Level> sharedDimension,
                                  java.util.function.Function<BlockPos, BlockPos> exitPlatformPositionGetter,
                                  java.util.function.BiConsumer<ServerPlayer, ServerLevel> teleportOutHandler) {
        at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData entryData = getPlayerEntry(player, sharedDimension);
        
        if (entryData == null) {
            return;
        }
        
        // Check if player is in the pocket dimension
        if (!level.dimension().equals(sharedDimension)) {
            return;
        }
        
        // Check if player is at exit platform position (within 3 blocks)
        BlockPos exitPlatformPos = exitPlatformPositionGetter.apply(entryData.spawnPosition());
        BlockPos playerPos = player.blockPosition();
        
        // Check if player is at the exit platform height and within range
        if (playerPos.getY() >= exitPlatformPos.getY() - 1 && 
            playerPos.getY() <= exitPlatformPos.getY() + 2 &&
            playerPos.distSqr(exitPlatformPos) <= 9) { // Within 3 blocks
            
            // Check if the case block is open before allowing exit
            ServerLevel targetLevel = level.getServer().getLevel(entryData.entryDimension());
            if (targetLevel == null) {
                return;
            }
            
            BlockPos entryPos = entryData.entryPosition();
            
            // Check if the case block exists at entry position
            BlockState caseState = targetLevel.getBlockState(entryPos);
            boolean caseExists = caseState.getBlock() instanceof NewtsCaseBlock;
            
            if (!caseExists) {
                // Fallback: if entryPos is not the case, check positions below (legacy support)
                for (int yOffset = -1; yOffset >= -2; yOffset--) {
                    BlockPos checkPos = entryPos.offset(0, yOffset, 0);
                    BlockState checkState = targetLevel.getBlockState(checkPos);
                    if (checkState.getBlock() instanceof NewtsCaseBlock) {
                        caseState = checkState;
                        caseExists = true;
                        break;
                    }
                }
            }
            
            // If case doesn't exist, still allow exit but warn player
            if (!caseExists) {
                player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.case_not_found"));
                teleportOutHandler.accept(player, targetLevel);
                return;
            }
            
            // Case exists - check if it's closed
            boolean caseClosed = caseState.hasProperty(NewtsCaseBlock.OPEN) && !caseState.getValue(NewtsCaseBlock.OPEN);
            
            if (caseClosed) {
                // Case is closed - warn player but still allow exit to prevent trapping
                // Check cooldown to prevent message spam
                long currentTick = player.level().getGameTime();
                Long lastMessage = lastMessageTime.get(player.getUUID());
                
                if (lastMessage == null || (currentTick - lastMessage) >= MESSAGE_COOLDOWN_TICKS) {
                    player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.case_closed_warning"));
                    lastMessageTime.put(player.getUUID(), currentTick);
                }
                // Continue with exit - don't trap the player
            }
            
            // Teleport player out
            teleportOutHandler.accept(player, targetLevel);
            
            // Clear entry data
            clearPlayerEntry(player);
        }
    }
    

    /**
     * Saves player entry data to persistent storage.
     */
    private static void savePersistentEntry(ServerPlayer player, ResourceKey<Level> entryDimension,
                                           BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        CompoundTag tag = new CompoundTag();
        tag.store(ENTRY_DIMENSION_KEY, ResourceKey.codec(Registries.DIMENSION), entryDimension);
        tag.store(ENTRY_POSITION_KEY, BlockPos.CODEC, entryPosition);
        tag.store(SPAWN_POSITION_KEY, BlockPos.CODEC, spawnPosition);
        tag.store(DIMENSION_ID_KEY, UUIDUtil.CODEC, dimensionId);
        player.getPersistentData().put(ENTRY_DATA_TAG, tag);
    }

    /**
     * Loads player entry data from persistent storage.
     */
    private static at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData loadEntryFromPersistent(ServerPlayer player, ResourceKey<Level> sharedDimension) {
        // Only restore if the player is currently inside the shared pocket dimension
        if (!player.level().dimension().equals(sharedDimension)) {
            player.getPersistentData().remove(ENTRY_DATA_TAG);
            return null;
        }

        CompoundTag tag = player.getPersistentData().getCompound(ENTRY_DATA_TAG).orElse(null);
        if (tag == null || tag.isEmpty()) {
            return null;
        }

        var entryDimension = tag.read(ENTRY_DIMENSION_KEY, ResourceKey.codec(Registries.DIMENSION));
        var entryPosition = tag.read(ENTRY_POSITION_KEY, BlockPos.CODEC);
        var spawnPosition = tag.read(SPAWN_POSITION_KEY, BlockPos.CODEC);
        var dimensionId = tag.read(DIMENSION_ID_KEY, UUIDUtil.CODEC);

        if (entryDimension.isEmpty() || entryPosition.isEmpty() || spawnPosition.isEmpty() || dimensionId.isEmpty()) {
            player.getPersistentData().remove(ENTRY_DATA_TAG);
            return null;
        }

        at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData data = new at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager.PlayerEntryData(
            entryDimension.get(), entryPosition.get(), spawnPosition.get(), dimensionId.get());
        playerEntryMap.put(player.getUUID(), data);
        return data;
    }
}

