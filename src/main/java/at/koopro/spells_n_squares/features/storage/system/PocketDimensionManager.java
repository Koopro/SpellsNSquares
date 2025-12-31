package at.koopro.spells_n_squares.features.storage.system;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import at.koopro.spells_n_squares.features.storage.PocketDimensionConstants;
import at.koopro.spells_n_squares.features.storage.PocketDimensionPlayerTracker;
import at.koopro.spells_n_squares.features.storage.PocketDimensionStructureLoader;
import at.koopro.spells_n_squares.features.storage.PocketDimensionTeleporter;
import at.koopro.spells_n_squares.features.storage.data.PocketDimensionData;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Manages pocket dimensions - creates and tracks custom dimensions for pocket dimension items.
 * Coordinates dimension management, structure loading, player tracking, and teleportation.
 * 
 * <p><b>Thread Safety:</b> All static collections are accessed only from the main server thread 
 * during game ticks. These collections are not thread-safe, but thread safety is not required as Minecraft's 
 * game logic runs on a single thread. If these collections are ever accessed from multiple threads in the future, 
 * they should be converted to thread-safe collections (e.g., {@code ConcurrentHashMap}).
 */
public final class PocketDimensionManager {
    private PocketDimensionManager() {
    }
    
    // Shared pocket dimension key (we'll use one dimension with different areas per item)
    private static ResourceKey<Level> SHARED_POCKET_DIMENSION;
    
    
    /**
     * Data class for tracking player entry points.
     */
    public static record PlayerEntryData(
        ResourceKey<Level> entryDimension,
        BlockPos entryPosition,
        BlockPos spawnPosition,
        UUID dimensionId
    ) {}
    
    /**
     * Initializes the pocket dimension system.
     */
    public static void initialize(MinecraftServer server) {
        // Create shared pocket dimension key
        SHARED_POCKET_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ModIdentifierHelper.modId("pocket_dimension")
        );
        
        // Ensure the dimension exists
        ensureDimensionExists(server);
    }
    
    /**
     * Gets or creates a dimension key for a pocket dimension item.
     * Uses a shared dimension with unique coordinates per item UUID.
     */
    public static ResourceKey<Level> getOrCreateDimensionKey(UUID itemUuid) {
        return getOrCreateDimensionKey(itemUuid, PocketDimensionData.DimensionType.STANDARD);
    }
    
    /**
     * Gets or creates a dimension key for a pocket dimension item with a specific type.
     * For NEWTS_CASE type: Creates unique dimension per UUID.
     * For STANDARD type: Uses shared dimension with unique coordinates per UUID.
     */
    public static ResourceKey<Level> getOrCreateDimensionKey(UUID itemUuid, PocketDimensionData.DimensionType type) {
        // For now, use shared pocket dimension for all types
        // Each case/item gets unique coordinates within the shared dimension
        // This avoids needing to register unique dimensions per case
        if (SHARED_POCKET_DIMENSION == null) {
            SHARED_POCKET_DIMENSION = ResourceKey.create(
                Registries.DIMENSION,
                ModIdentifierHelper.modId("pocket_dimension")
            );
        }
        return SHARED_POCKET_DIMENSION;
    }
    
    /**
     * Gets the spawn position for a pocket dimension item in the shared dimension.
     * Uses UUID to generate consistent coordinates.
     */
    public static BlockPos getSpawnPosition(UUID itemUuid, int size) {
        // Generate consistent coordinates based on UUID
        // Spread items out in a grid pattern
        long uuidHash = itemUuid.getMostSignificantBits() ^ itemUuid.getLeastSignificantBits();
        int gridX = (int) ((uuidHash & 0xFFFF) % PocketDimensionConstants.GRID_SIZE) * (size + PocketDimensionConstants.GRID_SPACING_OFFSET);
        int gridZ = (int) ((uuidHash >> 16) & 0xFFFF) % PocketDimensionConstants.GRID_SIZE * (size + PocketDimensionConstants.GRID_SPACING_OFFSET);
        
        // Center the spawn area
        int centerX = gridX + size / 2;
        int centerZ = gridZ + size / 2;
        int y = PocketDimensionConstants.STANDARD_SPAWN_HEIGHT;
        
        return new BlockPos(centerX, y, centerZ);
    }
    
    /**
     * Gets the exit platform position for Newt's Case dimension.
     * Previously this was a tall ladder tower; with the redesigned shed layout,
     * the exit is now located near the top of the shed above the spawn point.
     */
    public static BlockPos getExitPlatformPosition(BlockPos spawnPos) {
        // Exit platform is above spawn position (at the top of the ladder)
        // This matches the code-generated structure height
        return spawnPos.above(PocketDimensionConstants.EXIT_PLATFORM_HEIGHT);
    }
    
    /**
     * Ensures the pocket dimension exists and is initialized.
     */
    private static void ensureDimensionExists(MinecraftServer server) {
        ServerLevel dimension = server.getLevel(SHARED_POCKET_DIMENSION);
        if (dimension == null) {
            // Dimension doesn't exist yet - it will be created when first accessed
            // We'll handle initialization in the chunk generator
        }
    }
    
    /**
     * Gets the server level for a pocket dimension.
     * The dimension should be automatically loaded from data packs.
     */
    public static ServerLevel getOrCreateDimension(MinecraftServer server, ResourceKey<Level> dimensionKey) {
        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) {
            // Dimension should be loaded from data pack, but if it's not available,
            // it might not be registered. Log a warning.
            com.mojang.logging.LogUtils.getLogger().warn(
                "Pocket dimension not found. Ensure dimension data pack is loaded.");
        }
        return level;
    }
    
    /**
     * Initializes a spawn area in the pocket dimension for a specific item.
     */
    public static void initializeSpawnArea(ServerLevel level, BlockPos spawnPos, int size) {
        PocketDimensionStructureLoader.initializeSpawnArea(level, spawnPos, size, PocketDimensionData.DimensionType.STANDARD);
    }
    
    /**
     * Initializes a spawn area in the pocket dimension for a specific item with a type.
     */
    public static void initializeSpawnArea(ServerLevel level, BlockPos spawnPos, int size, PocketDimensionData.DimensionType type) {
        PocketDimensionStructureLoader.initializeSpawnArea(level, spawnPos, size, type);
    }
    
    /**
     * Initializes a Newt's Case dimension by loading and placing the structure schematic.
     * Only loads the structure once per dimension UUID.
     */
    public static BlockPos initializeNewtsCaseDimension(ServerLevel level, UUID dimensionId) {
        return initializeNewtsCaseDimension(level, dimensionId, 0);
    }
    
    /**
     * Initializes a Newt's Case dimension with a specific upgrade level.
     */
    public static BlockPos initializeNewtsCaseDimension(ServerLevel level, UUID dimensionId, int upgradeLevel) {
        return PocketDimensionStructureLoader.initializeNewtsCaseDimension(level, dimensionId, upgradeLevel, 
            uuid -> getSpawnPosition(uuid, PocketDimensionConstants.BASE_DIMENSION_SIZE + (upgradeLevel * PocketDimensionConstants.SIZE_INCREASE_PER_UPGRADE)));
    }
    
    /**
     * Stores player entry data when they enter a pocket dimension.
     */
    public static void storePlayerEntry(UUID playerUuid, ResourceKey<Level> entryDimension, 
                                        BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        PocketDimensionPlayerTracker.storePlayerEntry(playerUuid, entryDimension, entryPosition, spawnPosition, dimensionId);
    }

    /**
     * Stores player entry data (including persistent storage) when they enter a pocket dimension.
     */
    public static void storePlayerEntry(ServerPlayer player, ResourceKey<Level> entryDimension,
                                        BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        PocketDimensionPlayerTracker.storePlayerEntry(player, entryDimension, entryPosition, spawnPosition, dimensionId);
    }
    
    /**
     * Removes player entry data when they exit.
     */
    public static void clearPlayerEntry(UUID playerUuid) {
        PocketDimensionPlayerTracker.clearPlayerEntry(playerUuid);
    }

    /**
     * Removes player entry data and clears persistent storage.
     */
    public static void clearPlayerEntry(ServerPlayer player) {
        PocketDimensionPlayerTracker.clearPlayerEntry(player);
    }
    
    /**
     * Cleans up all player-related data when a player disconnects.
     * Removes player from all tracking maps to prevent memory leaks.
     */
    public static void clearPlayerData(UUID playerUUID) {
        PocketDimensionPlayerTracker.clearPlayerData(playerUUID);
    }
    
    /**
     * Cleans up all player-related data when a player disconnects.
     */
    public static void clearPlayerData(ServerPlayer player) {
        PocketDimensionPlayerTracker.clearPlayerData(player);
    }
    
    /**
     * Checks if a player is at the exit platform and teleports them back if so.
     */
    public static void checkExitPlatform(ServerPlayer player, ServerLevel level) {
        PocketDimensionPlayerTracker.checkExitPlatform(player, level, SHARED_POCKET_DIMENSION,
            PocketDimensionManager::getExitPlatformPosition,
            (p, targetLevel) -> {
                PocketDimensionManager.PlayerEntryData entryData = PocketDimensionPlayerTracker.getPlayerEntry(p.getUUID());
                if (entryData != null) {
                    PocketDimensionTeleporter.teleportPlayerOut(p, level, targetLevel, entryData.entryPosition());
                    p.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
                }
            });
    }
}
