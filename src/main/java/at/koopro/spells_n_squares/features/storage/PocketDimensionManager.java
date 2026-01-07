package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages pocket dimensions - creates and tracks custom dimensions for pocket dimension items.
 */
public final class PocketDimensionManager {
    private PocketDimensionManager() {
    }
    
    // Shared pocket dimension key (we'll use one dimension with different areas per item)
    private static ResourceKey<Level> SHARED_POCKET_DIMENSION;
    
    // Map of player UUID to their entry data (dimension key, entry position, spawn position)
    private static final Map<UUID, PlayerEntryData> playerEntryMap = CollectionFactory.createMap();
    
    // Track which dimensions have had their structure loaded
    private static final Map<UUID, Boolean> structureLoadedMap = CollectionFactory.createMap();
    
    // Track last message time per player to prevent spam (cooldown: 3 seconds = 60 ticks)
    private static final Map<UUID, Long> lastMessageTime = CollectionFactory.createMap();
    private static final long MESSAGE_COOLDOWN_TICKS = 60;
    
    /**
     * Teleports a player out of the pocket dimension to the target location.
     */
    private static void teleportPlayerOut(ServerPlayer player, ServerLevel pocketLevel, 
                                         ServerLevel targetLevel, BlockPos targetPos) {
        DevLogger.logMethodEntry(PocketDimensionManager.class, "teleportPlayerOut", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", targetPos=" + DevLogger.formatPos(targetPos));
        // Visual effect at origin (pocket dimension)
        Vec3 origin = player.position();
        ParticlePool.queueParticle(
            pocketLevel,
            ParticleTypes.PORTAL,
            origin,
            30, 0.5, 0.5, 0.5, 0.1
        );
        ParticlePool.queueParticle(
            pocketLevel,
            ParticleTypes.END_ROD,
            origin,
            20, 0.3, 0.3, 0.3, 0.05
        );
        
        pocketLevel.playSound(null, origin.x, origin.y, origin.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Find safe teleport position (above the target, on solid ground)
        BlockPos safePos = targetPos;
        if (!targetLevel.getBlockState(targetPos).isAir() || 
            !targetLevel.getBlockState(targetPos.below()).canOcclude()) {
            // Find a safe spot above
            for (int y = 1; y <= 5; y++) {
                BlockPos testPos = targetPos.offset(0, y, 0);
                if (targetLevel.getBlockState(testPos).isAir() && 
                    targetLevel.getBlockState(testPos.below()).canOcclude()) {
                    safePos = testPos;
                    break;
                }
            }
        }
        
        // Teleport back
        DevLogger.logStateChange(PocketDimensionManager.class, "teleportPlayerOut", 
            "Teleporting player to safePos=" + DevLogger.formatPos(safePos));
        player.teleportTo(targetLevel, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(safePos);
        ParticlePool.queueParticle(
            targetLevel,
            ParticleTypes.PORTAL,
            dest,
            30, 0.5, 0.5, 0.5, 0.1
        );
        ParticlePool.queueParticle(
            targetLevel,
            ParticleTypes.END_ROD,
            dest,
            20, 0.3, 0.3, 0.3, 0.05
        );
        
        targetLevel.playSound(null, dest.x, dest.y, dest.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Clear entry data
        clearPlayerEntry(player);
        DevLogger.logMethodExit(PocketDimensionManager.class, "teleportPlayerOut");
    }

    private static final String ENTRY_DATA_TAG = "spells_n_squares_pocket_entry";
    private static final String ENTRY_DIMENSION_KEY = "entryDimension";
    private static final String ENTRY_POSITION_KEY = "entryPosition";
    private static final String SPAWN_POSITION_KEY = "spawnPosition";
    private static final String DIMENSION_ID_KEY = "dimensionId";
    
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
        DevLogger.logMethodEntry(PocketDimensionManager.class, "initialize");
        // Create shared pocket dimension key
        SHARED_POCKET_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ModIdentifierHelper.modId("pocket_dimension")
        );
        DevLogger.logStateChange(PocketDimensionManager.class, "initialize", 
            "Created shared pocket dimension key: " + SHARED_POCKET_DIMENSION);
        
        // Ensure the dimension exists
        ensureDimensionExists(server);
        DevLogger.logMethodExit(PocketDimensionManager.class, "initialize");
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
        int gridX = (int) ((uuidHash & 0xFFFF) % 1000) * (size + 10); // Space items out
        int gridZ = (int) ((uuidHash >> 16) & 0xFFFF) % 1000 * (size + 10);
        
        // Center the spawn area
        int centerX = gridX + size / 2;
        int centerZ = gridZ + size / 2;
        int y = 64; // Standard spawn height
        
        return new BlockPos(centerX, y, centerZ);
    }
    
    /**
     * Gets the exit platform position for Newt's Case dimension.
     * Previously this was a tall ladder tower; with the redesigned shed layout,
     * the exit is now located near the top of the shed above the spawn point.
     */
    public static BlockPos getExitPlatformPosition(BlockPos spawnPos) {
        // Exit platform is 5 blocks above spawn position (at the top of the ladder)
        // This matches the code-generated structure height
        return spawnPos.above(5);
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
        DevLogger.logMethodEntry(PocketDimensionManager.class, "getOrCreateDimension", 
            "dimensionKey=" + (dimensionKey != null ? dimensionKey.toString() : "null"));
        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) {
            // Dimension should be loaded from data pack, but if it's not available,
            // it might not be registered. Log a warning.
            com.mojang.logging.LogUtils.getLogger().warn(
                "Pocket dimension not found. Ensure dimension data pack is loaded.");
            DevLogger.logWarn(PocketDimensionManager.class, "getOrCreateDimension", 
                "Dimension not found: " + dimensionKey);
        } else {
            DevLogger.logDebug(PocketDimensionManager.class, "getOrCreateDimension", 
                "Dimension found: " + dimensionKey);
        }
        DevLogger.logMethodExit(PocketDimensionManager.class, "getOrCreateDimension", 
            level != null ? "level" : "null");
        return level;
    }
    
    /**
     * Initializes a spawn area in the pocket dimension for a specific item.
     */
    public static void initializeSpawnArea(ServerLevel level, BlockPos spawnPos, int size) {
        initializeSpawnArea(level, spawnPos, size, PocketDimensionData.DimensionType.STANDARD);
    }
    
    /**
     * Initializes a spawn area in the pocket dimension for a specific item with a type.
     */
    public static void initializeSpawnArea(ServerLevel level, BlockPos spawnPos, int size, PocketDimensionData.DimensionType type) {
        if (level == null) {
            return;
        }
        
        if (type == PocketDimensionData.DimensionType.NEWTS_CASE) {
            // For Newt's Case, use structure loading instead of simple platform
            // This will be handled by initializeNewtsCaseDimension
            return;
        }
        
        // Create a platform at spawn (for STANDARD type)
        int halfSize = size / 2;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() - 1, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.STONE.defaultBlockState(), 3);
            }
        }
        
        // Add some lighting
        for (int x = -halfSize; x <= halfSize; x += 4) {
            for (int z = -halfSize; z <= halfSize; z += 4) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY(), spawnPos.getZ() + z);
                if (level.getBlockState(mutablePos).isAir()) {
                    level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
                }
            }
        }
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
        DevLogger.logMethodEntry(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
            "dimensionId=" + dimensionId + ", upgradeLevel=" + upgradeLevel);
        
        if (level == null) {
            DevLogger.logWarn(PocketDimensionManager.class, "initializeNewtsCaseDimension", "Level is null");
            DevLogger.logMethodExit(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
                new BlockPos(0, 64, 0));
            return new BlockPos(0, 64, 0);
        }
        
        // Check if structure already loaded for this dimension
        // Note: Upgrades will require structure regeneration, so we check upgrade level too
        if (structureLoadedMap.containsKey(dimensionId) && upgradeLevel == 0) {
            // Structure already loaded at base level, return spawn position
            BlockPos spawnPos = getSpawnPosition(dimensionId, 32);
            DevLogger.logDebug(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
                "Structure already loaded, returning existing spawn position");
            DevLogger.logMethodExit(PocketDimensionManager.class, "initializeNewtsCaseDimension", spawnPos);
            return spawnPos;
        }
        
        // Get spawn position (size increases with upgrade level)
        int baseSize = 32;
        int currentSize = baseSize + (upgradeLevel * 8);
        BlockPos spawnPos = getSpawnPosition(dimensionId, currentSize);
        DevLogger.logParameter(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
            "spawnPos", DevLogger.formatPos(spawnPos));
        DevLogger.logParameter(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
            "currentSize", currentSize);
        
        // Load and place structure
        if (loadStructureSchematic(level, spawnPos)) {
            structureLoadedMap.put(dimensionId, true);
            DevLogger.logStateChange(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
                "Structure loaded from schematic, dimensionId=" + dimensionId);
        } else {
            // If structure loading failed, create a code-generated structure with ladder exit
            DevLogger.logDebug(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
                "Structure schematic not found, generating code-based structure");
            generateNewtsCaseStructure(level, spawnPos, upgradeLevel);
            structureLoadedMap.put(dimensionId, true);
            DevLogger.logStateChange(PocketDimensionManager.class, "initializeNewtsCaseDimension", 
                "Generated code-based structure, dimensionId=" + dimensionId);
        }
        
        DevLogger.logMethodExit(PocketDimensionManager.class, "initializeNewtsCaseDimension", spawnPos);
        return spawnPos;
    }
    
    /**
     * Generates a code-based structure for Newt's Case dimension.
     * Creates a room with a ladder leading up to an exit platform.
     * @param upgradeLevel The upgrade level (affects room size)
     */
    private static void generateNewtsCaseStructure(ServerLevel level, BlockPos spawnPos, int upgradeLevel) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        // Base floor size is 5x5, increases by 2 per upgrade level (max 11x11 at level 3)
        int floorSize = Math.min(5 + (upgradeLevel * 2), 11);
        int halfFloor = floorSize / 2;
        for (int x = -halfFloor; x <= halfFloor; x++) {
            for (int z = -halfFloor; z <= halfFloor; z++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() - 1, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Create walls (3 blocks high)
        int wallHeight = 3;
        for (int y = 0; y < wallHeight; y++) {
            // North wall
            for (int x = -halfFloor; x <= halfFloor; x++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() + y, spawnPos.getZ() - halfFloor);
                if (x == 0 && y == 0) {
                    // Leave opening for entrance (will be at spawn)
                    continue;
                }
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
            // South wall
            for (int x = -halfFloor; x <= halfFloor; x++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() + y, spawnPos.getZ() + halfFloor);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
            // East wall
            for (int z = -halfFloor; z <= halfFloor; z++) {
                mutablePos.set(spawnPos.getX() + halfFloor, spawnPos.getY() + y, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
            // West wall
            for (int z = -halfFloor; z <= halfFloor; z++) {
                mutablePos.set(spawnPos.getX() - halfFloor, spawnPos.getY() + y, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Create ceiling
        for (int x = -halfFloor; x <= halfFloor; x++) {
            for (int z = -halfFloor; z <= halfFloor; z++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() + wallHeight, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Add lighting - torches on walls
        mutablePos.set(spawnPos.getX() - halfFloor + 1, spawnPos.getY() + 1, spawnPos.getZ());
        level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
        mutablePos.set(spawnPos.getX() + halfFloor - 1, spawnPos.getY() + 1, spawnPos.getZ());
        level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
        mutablePos.set(spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ() - halfFloor + 1);
        level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
        mutablePos.set(spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ() + halfFloor - 1);
        level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
        
        // Add storage chests along the walls
        // Chest on west wall (left side when facing north)
        mutablePos.set(spawnPos.getX() - halfFloor + 1, spawnPos.getY(), spawnPos.getZ() - 1);
        level.setBlock(mutablePos, Blocks.CHEST.defaultBlockState()
            .setValue(net.minecraft.world.level.block.ChestBlock.FACING, net.minecraft.core.Direction.EAST), 3);
        
        // Chest on east wall (right side when facing north)
        mutablePos.set(spawnPos.getX() + halfFloor - 1, spawnPos.getY(), spawnPos.getZ() - 1);
        level.setBlock(mutablePos, Blocks.CHEST.defaultBlockState()
            .setValue(net.minecraft.world.level.block.ChestBlock.FACING, net.minecraft.core.Direction.WEST), 3);
        
        // Add crafting table in corner
        mutablePos.set(spawnPos.getX() - halfFloor + 1, spawnPos.getY(), spawnPos.getZ() + 1);
        level.setBlock(mutablePos, Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
        
        // Add furnace in corner
        mutablePos.set(spawnPos.getX() + halfFloor - 1, spawnPos.getY(), spawnPos.getZ() + 1);
        level.setBlock(mutablePos, Blocks.FURNACE.defaultBlockState()
            .setValue(net.minecraft.world.level.block.FurnaceBlock.FACING, net.minecraft.core.Direction.NORTH), 3);
        
        // Add brewing stand (if available)
        mutablePos.set(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ() + 1);
        level.setBlock(mutablePos, Blocks.BREWING_STAND.defaultBlockState(), 3);
        
        // Create ladder going up from center of room
        // Ladder goes up through the ceiling to exit platform
        int ladderHeight = 5; // Height from floor to exit platform
        BlockPos ladderBase = spawnPos.offset(0, 0, 0); // Center of room
        
        // Place ladder blocks going up
        for (int y = 0; y < ladderHeight; y++) {
            mutablePos.set(ladderBase.getX(), ladderBase.getY() + y, ladderBase.getZ());
            // Place ladder facing north (so player can climb up)
            level.setBlock(mutablePos, Blocks.LADDER.defaultBlockState()
                .setValue(net.minecraft.world.level.block.LadderBlock.FACING, net.minecraft.core.Direction.NORTH), 3);
        }
        
        // Create exit platform at the top of the ladder (3x3 platform)
        int exitPlatformSize = 3;
        int halfExit = exitPlatformSize / 2;
        BlockPos exitPlatformPos = spawnPos.offset(0, ladderHeight, 0);
        
        // Exit platform floor
        for (int x = -halfExit; x <= halfExit; x++) {
            for (int z = -halfExit; z <= halfExit; z++) {
                mutablePos.set(exitPlatformPos.getX() + x, exitPlatformPos.getY(), exitPlatformPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Add railings around exit platform (fence posts)
        for (int x = -halfExit; x <= halfExit; x++) {
            for (int z = -halfExit; z <= halfExit; z++) {
                // Only place fences on edges
                if (x == -halfExit || x == halfExit || z == -halfExit || z == halfExit) {
                    mutablePos.set(exitPlatformPos.getX() + x, exitPlatformPos.getY() + 1, exitPlatformPos.getZ() + z);
                    level.setBlock(mutablePos, Blocks.OAK_FENCE.defaultBlockState(), 3);
                }
            }
        }
        
        // Add lighting to exit platform
        mutablePos.set(exitPlatformPos.getX(), exitPlatformPos.getY() + 1, exitPlatformPos.getZ());
        level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
        
        // Open ceiling above ladder (remove ceiling blocks where ladder goes through)
        mutablePos.set(ladderBase.getX(), spawnPos.getY() + wallHeight, ladderBase.getZ());
        level.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
        // Also open the space above for ladder continuation
        for (int y = wallHeight + 1; y < ladderHeight; y++) {
            mutablePos.set(ladderBase.getX(), spawnPos.getY() + y, ladderBase.getZ());
            level.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
    
    /**
     * Loads and places the Newt's Case structure schematic at the given position.
     * The structure should be located at: data/spells_n_squares/structures/newts_case.nbt
     */
    private static boolean loadStructureSchematic(ServerLevel level, BlockPos spawnPos) {
        try {
            StructureTemplateManager structureManager = level.getServer().getStructureManager();
            // Structure location: spells_n_squares:newts_case
            // This will load from: data/spells_n_squares/structures/newts_case.nbt
            var structureLocation = ModIdentifierHelper.modId("newts_case");
            
            Optional<StructureTemplate> template = structureManager.get(structureLocation);
            if (template.isEmpty()) {
                com.mojang.logging.LogUtils.getLogger().warn(
                    "Newt's Case structure schematic not found at: {} (expected: data/spells_n_squares/structures/newts_case.nbt)", 
                    structureLocation);
                return false;
            }
            
            StructureTemplate structure = template.get();
            
            // Place the structure
            // The structure will be placed with its origin at spawnPos
            BlockPos structurePos = spawnPos.offset(
                -structure.getSize().getX() / 2,
                0,
                -structure.getSize().getZ() / 2
            );
            
            var placeSettings = new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings()
                .setIgnoreEntities(false);
            
            structure.placeInWorld(
                level,
                structurePos,
                structurePos,
                placeSettings,
                level.random,
                2 // flags
            );
            
            return true;
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error(
                "Failed to load Newt's Case structure", e);
            return false;
        }
    }
    
    /**
     * Stores player entry data when they enter a pocket dimension.
     */
    public static void storePlayerEntry(UUID playerUuid, ResourceKey<Level> entryDimension, 
                                        BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        playerEntryMap.put(playerUuid, new PlayerEntryData(entryDimension, entryPosition, spawnPosition, dimensionId));
    }

    /**
     * Stores player entry data (including persistent storage) when they enter a pocket dimension.
     */
    public static void storePlayerEntry(ServerPlayer player, ResourceKey<Level> entryDimension,
                                        BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        DevLogger.logMethodEntry(PocketDimensionManager.class, "storePlayerEntry", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", entryPos=" + DevLogger.formatPos(entryPosition) +
            ", spawnPos=" + DevLogger.formatPos(spawnPosition) +
            ", dimensionId=" + dimensionId);
        storePlayerEntry(player.getUUID(), entryDimension, entryPosition, spawnPosition, dimensionId);
        savePersistentEntry(player, entryDimension, entryPosition, spawnPosition, dimensionId);
        DevLogger.logStateChange(PocketDimensionManager.class, "storePlayerEntry", 
            "Stored player entry data for " + (player != null ? player.getName().getString() : "null"));
        DevLogger.logMethodExit(PocketDimensionManager.class, "storePlayerEntry");
    }
    
    /**
     * Removes player entry data when they exit.
     */
    public static void clearPlayerEntry(UUID playerUuid) {
        DevLogger.logMethodEntry(PocketDimensionManager.class, "clearPlayerEntry", 
            "playerUuid=" + playerUuid);
        playerEntryMap.remove(playerUuid);
        DevLogger.logStateChange(PocketDimensionManager.class, "clearPlayerEntry", 
            "Cleared entry data for player " + playerUuid);
        DevLogger.logMethodExit(PocketDimensionManager.class, "clearPlayerEntry");
    }

    /**
     * Removes player entry data and clears persistent storage.
     */
    public static void clearPlayerEntry(ServerPlayer player) {
        DevLogger.logMethodEntry(PocketDimensionManager.class, "clearPlayerEntry", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        clearPlayerEntry(player.getUUID());
        player.getPersistentData().remove(ENTRY_DATA_TAG);
        DevLogger.logStateChange(PocketDimensionManager.class, "clearPlayerEntry", 
            "Cleared entry data and persistent storage for " + (player != null ? player.getName().getString() : "null"));
        DevLogger.logMethodExit(PocketDimensionManager.class, "clearPlayerEntry");
    }

    private static void savePersistentEntry(ServerPlayer player, ResourceKey<Level> entryDimension,
                                            BlockPos entryPosition, BlockPos spawnPosition, UUID dimensionId) {
        CompoundTag tag = new CompoundTag();
        tag.store(ENTRY_DIMENSION_KEY, ResourceKey.codec(Registries.DIMENSION), entryDimension);
        tag.store(ENTRY_POSITION_KEY, BlockPos.CODEC, entryPosition);
        tag.store(SPAWN_POSITION_KEY, BlockPos.CODEC, spawnPosition);
        tag.store(DIMENSION_ID_KEY, UUIDUtil.CODEC, dimensionId);
        player.getPersistentData().put(ENTRY_DATA_TAG, tag);
    }

    private static PlayerEntryData loadEntryFromPersistent(ServerPlayer player) {
        // Only restore if the player is currently inside the shared pocket dimension
        if (!player.level().dimension().equals(SHARED_POCKET_DIMENSION)) {
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

        PlayerEntryData data = new PlayerEntryData(entryDimension.get(), entryPosition.get(), spawnPosition.get(), dimensionId.get());
        playerEntryMap.put(player.getUUID(), data);
        return data;
    }
    
    /**
     * Checks if a player is at the exit platform and teleports them back if so.
     */
    public static void checkExitPlatform(ServerPlayer player, ServerLevel level) {
        UUID playerUuid = player.getUUID();
        PlayerEntryData entryData = playerEntryMap.get(playerUuid);
        if (entryData == null) {
            entryData = loadEntryFromPersistent(player);
        }
        
        if (entryData == null) {
            return;
        }
        
        // Check if player is in the pocket dimension
        if (!level.dimension().equals(SHARED_POCKET_DIMENSION)) {
            return;
        }
        
        // Check if player is at exit platform position (within 3 blocks)
        BlockPos exitPlatformPos = getExitPlatformPosition(entryData.spawnPosition());
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
                        // Found the case at checkPos
                        caseState = checkState;
                        caseExists = true;
                        break;
                    }
                }
            }
            
            // If case doesn't exist, still allow exit but warn player
            if (!caseExists) {
                // Case block is missing - allow exit anyway to prevent trapping
                // Teleport to a safe location near the entry position
                BlockPos safePos = entryPos.above();
                if (!targetLevel.getBlockState(safePos).isAir()) {
                    // Find a safe spot nearby
                    for (int y = 1; y <= 5; y++) {
                        BlockPos testPos = entryPos.offset(0, y, 0);
                        if (targetLevel.getBlockState(testPos).isAir() && 
                            targetLevel.getBlockState(testPos.below()).canOcclude()) {
                            safePos = testPos;
                            break;
                        }
                    }
                }
                
                player.sendSystemMessage(at.koopro.spells_n_squares.core.util.rendering.ColorUtils.coloredText("Warning: Case block not found. Exiting to safe location.", at.koopro.spells_n_squares.core.util.rendering.ColorUtils.SPELL_GOLD));
                teleportPlayerOut(player, level, targetLevel, safePos);
                return;
            }
            
            // Case exists - check if it's closed
            boolean caseClosed = caseState.hasProperty(NewtsCaseBlock.OPEN) && !caseState.getValue(NewtsCaseBlock.OPEN);
            
            if (caseClosed) {
                // Case is closed - warn player but still allow exit to prevent trapping
                // Check cooldown to prevent message spam
                long currentTick = player.level().getGameTime();
                Long lastMessage = lastMessageTime.get(playerUuid);
                
                if (lastMessage == null || (currentTick - lastMessage) >= MESSAGE_COOLDOWN_TICKS) {
                    player.sendSystemMessage(at.koopro.spells_n_squares.core.util.rendering.ColorUtils.coloredText("Warning: The case is closed, but allowing exit to prevent trapping.", at.koopro.spells_n_squares.core.util.rendering.ColorUtils.SPELL_GOLD));
                    lastMessageTime.put(playerUuid, currentTick);
                }
                // Continue with exit - don't trap the player
            }
            
            // Visual effect at origin (pocket dimension)
            Vec3 origin = player.position();
            ParticlePool.queueParticle(
                level,
                ParticleTypes.PORTAL,
                origin,
                30, 0.5, 0.5, 0.5, 0.1
            );
            ParticlePool.queueParticle(
                level,
                ParticleTypes.END_ROD,
                origin,
                20, 0.3, 0.3, 0.3, 0.05
            );
            
            level.playSound(null, origin.x, origin.y, origin.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            // Teleport back
            player.teleportTo(targetLevel, entryPos.getX() + 0.5, entryPos.getY(), entryPos.getZ() + 0.5,
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);
            
            // Visual effect at destination
            Vec3 dest = Vec3.atCenterOf(entryPos);
            ParticlePool.queueParticle(
                targetLevel,
                ParticleTypes.PORTAL,
                dest,
                30, 0.5, 0.5, 0.5, 0.1
            );
            ParticlePool.queueParticle(
                targetLevel,
                ParticleTypes.END_ROD,
                dest,
                20, 0.3, 0.3, 0.3, 0.05
            );
            
            targetLevel.playSound(null, dest.x, dest.y, dest.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
            
            // Clear entry data
            clearPlayerEntry(player);
        }
    }
}
