package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
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
     * Uses a shared dimension with unique coordinates per item UUID.
     */
    public static ResourceKey<Level> getOrCreateDimensionKey(UUID itemUuid, PocketDimensionData.DimensionType type) {
        // For now, use shared dimension - each item gets unique coordinates based on UUID
        // This avoids the complexity of creating individual dimensions per item
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
     * Exit platform is 20 blocks above spawn position.
     */
    public static BlockPos getExitPlatformPosition(BlockPos spawnPos) {
        return spawnPos.above(20);
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
            initializeNewtsCaseLayout(level, spawnPos);
        } else {
            // Create a platform at spawn
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
    }
    
    /**
     * Initializes Newt's Case with a fixed magical creature habitat layout.
     */
    public static void initializeNewtsCaseLayout(ServerLevel level, BlockPos spawnPos) {
        if (level == null) {
            return;
        }
        
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int size = 32; // Newt's case is larger - 32x32 blocks
        
        // Create base platform
        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() - 1, spawnPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            }
        }
        
        // Create multiple habitat areas
        
        // 1. Central area - flat grass platform for player spawn
        int centerSize = 8;
        for (int x = -centerSize / 2; x <= centerSize / 2; x++) {
            for (int z = -centerSize / 2; z <= centerSize / 2; z++) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY(), spawnPos.getZ() + z);
                if (level.getBlockState(mutablePos).isAir()) {
                    level.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        
        // Build ladder tower in the center for exit
        int ladderHeight = 20; // Height of the ladder tower
        BlockPos ladderBasePos = spawnPos;
        
        // Build ladder going up
        for (int y = 0; y < ladderHeight; y++) {
            mutablePos.set(ladderBasePos.getX(), ladderBasePos.getY() + y, ladderBasePos.getZ());
            level.setBlock(mutablePos, Blocks.LADDER.defaultBlockState()
                .setValue(net.minecraft.world.level.block.LadderBlock.FACING, net.minecraft.core.Direction.NORTH), 3);
        }
        
        // Create exit platform at the top of the ladder
        int exitPlatformY = spawnPos.getY() + ladderHeight;
        BlockPos exitPlatformPos = new BlockPos(spawnPos.getX(), exitPlatformY, spawnPos.getZ());
        int platformSize = 5;
        
        // Create platform blocks
        for (int x = -platformSize / 2; x <= platformSize / 2; x++) {
            for (int z = -platformSize / 2; z <= platformSize / 2; z++) {
                mutablePos.set(exitPlatformPos.getX() + x, exitPlatformPos.getY() - 1, exitPlatformPos.getZ() + z);
                level.setBlock(mutablePos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Add railing around platform
        for (int x = -platformSize / 2; x <= platformSize / 2; x++) {
            for (int z = -platformSize / 2; z <= platformSize / 2; z++) {
                if (Math.abs(x) == platformSize / 2 || Math.abs(z) == platformSize / 2) {
                    mutablePos.set(exitPlatformPos.getX() + x, exitPlatformPos.getY(), exitPlatformPos.getZ() + z);
                    level.setBlock(mutablePos, Blocks.OAK_FENCE.defaultBlockState(), 3);
                }
            }
        }
        
        // Add lighting on platform
        mutablePos.set(exitPlatformPos.getX(), exitPlatformPos.getY(), exitPlatformPos.getZ());
        level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
        
        // 2. Forest area (north) - trees and grass
        int forestOffsetX = 0;
        int forestOffsetZ = -12;
        for (int x = -6; x <= 6; x++) {
            for (int z = -6; z <= 6; z++) {
                mutablePos.set(spawnPos.getX() + forestOffsetX + x, spawnPos.getY(), spawnPos.getZ() + forestOffsetZ + z);
                if (level.getBlockState(mutablePos).isAir() && (x * x + z * z) < 25) {
                    // Place grass
                    mutablePos.set(spawnPos.getX() + forestOffsetX + x, spawnPos.getY() - 1, spawnPos.getZ() + forestOffsetZ + z);
                    level.setBlock(mutablePos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                    // Place some trees
                    if ((x * x + z * z) % 7 == 0 && (x * x + z * z) > 4) {
                        mutablePos.set(spawnPos.getX() + forestOffsetX + x, spawnPos.getY(), spawnPos.getZ() + forestOffsetZ + z);
                        level.setBlock(mutablePos, Blocks.OAK_LOG.defaultBlockState(), 3);
                        mutablePos.set(spawnPos.getX() + forestOffsetX + x, spawnPos.getY() + 1, spawnPos.getZ() + forestOffsetZ + z);
                        level.setBlock(mutablePos, Blocks.OAK_LEAVES.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        // 3. Water area (south) - water pool
        int waterOffsetX = 0;
        int waterOffsetZ = 12;
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                if (x * x + z * z < 20) {
                    mutablePos.set(spawnPos.getX() + waterOffsetX + x, spawnPos.getY() - 1, spawnPos.getZ() + waterOffsetZ + z);
                    level.setBlock(mutablePos, Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
        
        // 4. Desert area (east) - sand
        int desertOffsetX = 12;
        int desertOffsetZ = 0;
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                if (x * x + z * z < 20) {
                    mutablePos.set(spawnPos.getX() + desertOffsetX + x, spawnPos.getY() - 1, spawnPos.getZ() + desertOffsetZ + z);
                    level.setBlock(mutablePos, Blocks.SAND.defaultBlockState(), 3);
                }
            }
        }
        
        // 5. Rocky area (west) - stone and cobblestone
        int rockOffsetX = -12;
        int rockOffsetZ = 0;
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                if (x * x + z * z < 20) {
                    mutablePos.set(spawnPos.getX() + rockOffsetX + x, spawnPos.getY() - 1, spawnPos.getZ() + rockOffsetZ + z);
                    level.setBlock(mutablePos, Blocks.STONE.defaultBlockState(), 3);
                    // Add some height variation
                    if ((x * x + z * z) % 3 == 0) {
                        mutablePos.set(spawnPos.getX() + rockOffsetX + x, spawnPos.getY(), spawnPos.getZ() + rockOffsetZ + z);
                        level.setBlock(mutablePos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        // Add lighting throughout
        for (int x = -halfSize; x <= halfSize; x += 6) {
            for (int z = -halfSize; z <= halfSize; z += 6) {
                mutablePos.set(spawnPos.getX() + x, spawnPos.getY() + 1, spawnPos.getZ() + z);
                if (level.getBlockState(mutablePos).isAir()) {
                    level.setBlock(mutablePos, Blocks.TORCH.defaultBlockState(), 3);
                }
            }
        }
        
        // Add some decorative plants
        for (int i = 0; i < 10; i++) {
            int plantX = spawnPos.getX() + (int)(Math.random() * size - halfSize);
            int plantZ = spawnPos.getZ() + (int)(Math.random() * size - halfSize);
            mutablePos.set(plantX, spawnPos.getY(), plantZ);
            if (level.getBlockState(mutablePos).isAir() && 
                level.getBlockState(mutablePos.below()).is(Blocks.GRASS_BLOCK)) {
                level.setBlock(mutablePos, Blocks.SHORT_GRASS.defaultBlockState(), 3);
            }
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
     * Removes player entry data when they exit.
     */
    public static void clearPlayerEntry(UUID playerUuid) {
        playerEntryMap.remove(playerUuid);
    }
    
    /**
     * Checks if a player is at the exit platform and teleports them back if so.
     */
    public static void checkExitPlatform(ServerPlayer player, ServerLevel level) {
        UUID playerUuid = player.getUUID();
        PlayerEntryData entryData = playerEntryMap.get(playerUuid);
        
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
            
            // Teleport player back
            ServerLevel targetLevel = level.getServer().getLevel(entryData.entryDimension());
            if (targetLevel == null) {
                return;
            }
            
            BlockPos entryPos = entryData.entryPosition();
            
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
            clearPlayerEntry(playerUuid);
        }
    }
}
