package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages pocket dimensions - creates and tracks custom dimensions for pocket dimension items.
 */
public final class PocketDimensionManager {
    private PocketDimensionManager() {
    }
    
    // Map of item UUID to dimension key
    private static final Map<UUID, ResourceKey<Level>> dimensionRegistry = new HashMap<>();
    
    // Shared pocket dimension key (we'll use one dimension with different areas per item)
    private static ResourceKey<Level> SHARED_POCKET_DIMENSION;
    
    // Map of player UUID to their entry data (dimension key, entry position, spawn position)
    private static final Map<UUID, PlayerEntryData> playerEntryMap = new HashMap<>();
    
    // Track which dimensions have had their structure loaded
    private static final Map<UUID, Boolean> structureLoadedMap = new HashMap<>();

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
        if (level == null) {
            return new BlockPos(0, 64, 0);
        }
        
        // Check if structure already loaded for this dimension
        if (structureLoadedMap.getOrDefault(dimensionId, false)) {
            // Structure already loaded, return spawn position
            return getSpawnPosition(dimensionId, 32);
        }
        
        // Get spawn position
        BlockPos spawnPos = getSpawnPosition(dimensionId, 32);
        
        // Load and place structure
        if (loadStructureSchematic(level, spawnPos)) {
            structureLoadedMap.put(dimensionId, true);
        } else {
            // If structure loading failed, create a code-generated structure with ladder exit
            generateNewtsCaseStructure(level, spawnPos);
            structureLoadedMap.put(dimensionId, true);
        }
        
        return spawnPos;
    }
    
    /**
     * Generates a code-based structure for Newt's Case dimension.
     * Creates a room with a ladder leading up to an exit platform.
     */
    private static void generateNewtsCaseStructure(ServerLevel level, BlockPos spawnPos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        // Create floor platform (5x5 area)
        int floorSize = 5;
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
        clearPlayerEntry(player.getUUID());
        player.getPersistentData().remove(ENTRY_DATA_TAG);
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
            
            // Check if the case block at entry position is open
            // entryPos should be the case block position (stored when entering)
            BlockState caseState = targetLevel.getBlockState(entryPos);
            if (caseState.getBlock() instanceof NewtsCaseBlock) {
                // This is the case block - check if it's open
                if (caseState.hasProperty(NewtsCaseBlock.OPEN) && !caseState.getValue(NewtsCaseBlock.OPEN)) {
                    // Case is closed - prevent exit
                    player.sendSystemMessage(Component.literal("§cThe case is closed! It must be opened to exit the pocket dimension."));
                    return;
                }
            } else {
                // Fallback: if entryPos is not the case, check positions below (legacy support)
                BlockPos casePos = null;
                for (int yOffset = -1; yOffset >= -2; yOffset--) {
                    BlockPos checkPos = entryPos.offset(0, yOffset, 0);
                    BlockState checkState = targetLevel.getBlockState(checkPos);
                    if (checkState.getBlock() instanceof NewtsCaseBlock) {
                        casePos = checkPos;
                        break;
                    }
                }
                
                if (casePos != null) {
                    BlockState checkCaseState = targetLevel.getBlockState(casePos);
                    if (checkCaseState.hasProperty(NewtsCaseBlock.OPEN) && !checkCaseState.getValue(NewtsCaseBlock.OPEN)) {
                        // Case is closed - prevent exit
                        player.sendSystemMessage(Component.literal("§cThe case is closed! It must be opened to exit the pocket dimension."));
                        return;
                    }
                }
            }
            
            // Visual effect at origin (pocket dimension)
            Vec3 origin = player.position();
            level.sendParticles(ParticleTypes.PORTAL,
                origin.x, origin.y, origin.z,
                30, 0.5, 0.5, 0.5, 0.1);
            level.sendParticles(ParticleTypes.END_ROD,
                origin.x, origin.y, origin.z,
                20, 0.3, 0.3, 0.3, 0.05);
            
            level.playSound(null, origin.x, origin.y, origin.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            // Teleport back
            player.teleportTo(targetLevel, entryPos.getX() + 0.5, entryPos.getY(), entryPos.getZ() + 0.5,
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);
            
            // Visual effect at destination
            Vec3 dest = Vec3.atCenterOf(entryPos);
            targetLevel.sendParticles(ParticleTypes.PORTAL,
                dest.x, dest.y, dest.z,
                30, 0.5, 0.5, 0.5, 0.1);
            targetLevel.sendParticles(ParticleTypes.END_ROD,
                dest.x, dest.y, dest.z,
                20, 0.3, 0.3, 0.3, 0.05);
            
            targetLevel.playSound(null, dest.x, dest.y, dest.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
            
            // Clear entry data
            clearPlayerEntry(player);
        }
    }
}
