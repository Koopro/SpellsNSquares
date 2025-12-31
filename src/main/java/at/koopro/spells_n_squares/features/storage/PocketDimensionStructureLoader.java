package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.storage.data.PocketDimensionData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles structure loading and generation for pocket dimensions.
 * Manages structure schematics and code-generated structures.
 */
public final class PocketDimensionStructureLoader {
    // Track which dimensions have had their structure loaded
    // Thread safety: Accessed only from main server thread
    private static final Map<UUID, Boolean> structureLoadedMap = new HashMap<>();
    
    private PocketDimensionStructureLoader() {}
    
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
     * Initializes a Newt's Case dimension with a specific upgrade level.
     * Returns the spawn position for the dimension.
     */
    public static BlockPos initializeNewtsCaseDimension(ServerLevel level, UUID dimensionId, int upgradeLevel, 
                                                 java.util.function.Function<UUID, BlockPos> spawnPositionGetter) {
        if (level == null) {
            return new BlockPos(0, PocketDimensionConstants.STANDARD_SPAWN_HEIGHT, 0);
        }
        
        // Check if structure already loaded for this dimension
        // Note: Upgrades will require structure regeneration, so we check upgrade level too
        if (structureLoadedMap.containsKey(dimensionId) && upgradeLevel == 0) {
            // Structure already loaded at base level, return spawn position
            return spawnPositionGetter.apply(dimensionId);
        }
        
        // Get spawn position (size increases with upgrade level)
        BlockPos spawnPos = spawnPositionGetter.apply(dimensionId);
        
        // Load and place structure
        if (loadStructureSchematic(level, spawnPos)) {
            structureLoadedMap.put(dimensionId, true);
        } else {
            // If structure loading failed, create a code-generated structure with ladder exit
            generateNewtsCaseStructure(level, spawnPos, upgradeLevel);
            structureLoadedMap.put(dimensionId, true);
        }
        
        return spawnPos;
    }
    
    /**
     * Generates a code-based structure for Newt's Case dimension.
     * Creates a room with a ladder leading up to an exit platform.
     * @param upgradeLevel The upgrade level (affects room size)
     */
    private static void generateNewtsCaseStructure(ServerLevel level, BlockPos spawnPos, int upgradeLevel) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        // Base floor size increases with upgrade level
        int floorSize = Math.min(
            PocketDimensionConstants.BASE_FLOOR_SIZE + (upgradeLevel * PocketDimensionConstants.FLOOR_SIZE_INCREASE_PER_UPGRADE),
            PocketDimensionConstants.MAX_FLOOR_SIZE);
        int halfFloor = floorSize / 2;
        for (int x = -halfFloor; x <= halfFloor; x++) {
            for (int z = -halfFloor; z <= halfFloor; z++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() - 1, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Create walls
        int wallHeight = PocketDimensionConstants.WALL_HEIGHT;
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
     * Clears structure tracking data for a dimension.
     */
    static void clearStructureData(UUID dimensionId) {
        structureLoadedMap.remove(dimensionId);
    }
}

