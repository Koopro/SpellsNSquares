package at.koopro.spells_n_squares.core.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Utility class for block placement, validation, and state management.
 * Provides safe block operations with validation.
 */
public final class BlockHelper {
    
    private BlockHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a block can be placed at a position.
     * 
     * @param level The level
     * @param pos The position
     * @param block The block to place
     * @return true if block can be placed
     */
    public static boolean canPlaceBlock(Level level, BlockPos pos, Block block) {
        if (level == null || pos == null || block == null) {
            return false;
        }
        
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.isAir() && !currentState.canBeReplaced()) {
            return false;
        }
        
        BlockState blockState = block.defaultBlockState();
        return blockState.canSurvive(level, pos);
    }
    
    /**
     * Safely places a block at a position.
     * 
     * @param level The level
     * @param pos The position
     * @param block The block to place
     * @param player The player placing (can be null)
     * @return true if block was placed
     */
    public static boolean placeBlock(ServerLevel level, BlockPos pos, Block block, Player player) {
        if (level == null || pos == null || block == null) {
            return false;
        }
        
        if (!canPlaceBlock(level, pos, block)) {
            return false;
        }
        
        BlockState blockState = block.defaultBlockState();
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
        
        return true;
    }
    
    /**
     * Checks if a position is safe for block placement.
     * Validates that the position and space above are clear.
     * 
     * @param level The level
     * @param pos The position
     * @return true if position is safe
     */
    public static boolean isSafePosition(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        
        BlockState state = level.getBlockState(pos);
        BlockState aboveState = level.getBlockState(pos.above());
        
        return (state.isAir() || state.getCollisionShape(level, pos).isEmpty()) &&
               (aboveState.isAir() || aboveState.getCollisionShape(level, pos.above()).isEmpty());
    }
    
    /**
     * Finds a safe position near a target position.
     * 
     * @param level The level
     * @param targetPos The target position
     * @param radius The search radius
     * @return Safe position, or null if not found
     */
    public static BlockPos findSafePosition(Level level, BlockPos targetPos, int radius) {
        if (level == null || targetPos == null || radius < 0) {
            return null;
        }
        
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = targetPos.offset(x, y, z);
                    if (isSafePosition(level, checkPos)) {
                        BlockPos groundPos = checkPos.below();
                        BlockState groundState = level.getBlockState(groundPos);
                        if (!groundState.isAir() && groundState.getCollisionShape(level, groundPos).isEmpty()) {
                            return checkPos;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets the block at a position, or null if out of bounds.
     * 
     * @param level The level
     * @param pos The position
     * @return The block, or null
     */
    public static Block getBlock(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }
        
        if (!level.isInWorldBounds(pos)) {
            return null;
        }
        
        return level.getBlockState(pos).getBlock();
    }
    
    /**
     * Checks if a block is of a specific type.
     * 
     * @param level The level
     * @param pos The position
     * @param block The block to check for
     * @return true if block matches
     */
    public static boolean isBlock(Level level, BlockPos pos, Block block) {
        if (level == null || pos == null || block == null) {
            return false;
        }
        
        return level.getBlockState(pos).is(block);
    }
    
    /**
     * Gets the distance between two block positions.
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return Distance in blocks
     */
    public static double getDistance(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null) {
            return Double.MAX_VALUE;
        }
        
        int dx = pos2.getX() - pos1.getX();
        int dy = pos2.getY() - pos1.getY();
        int dz = pos2.getZ() - pos1.getZ();
        
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Gets the horizontal distance between two block positions.
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return Horizontal distance in blocks
     */
    public static double getHorizontalDistance(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null) {
            return Double.MAX_VALUE;
        }
        
        int dx = pos2.getX() - pos1.getX();
        int dz = pos2.getZ() - pos1.getZ();
        
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    /**
     * Checks if a position is within a radius of another position.
     * 
     * @param center The center position
     * @param pos The position to check
     * @param radius The radius
     * @return true if within radius
     */
    public static boolean isWithinRadius(BlockPos center, BlockPos pos, double radius) {
        return getDistance(center, pos) <= radius;
    }
    
    /**
     * Gets all blocks in a radius around a position.
     * 
     * @param level The level
     * @param center The center position
     * @param radius The radius
     * @return List of block positions
     */
    public static java.util.List<BlockPos> getBlocksInRadius(Level level, BlockPos center, int radius) {
        if (level == null || center == null || radius < 0) {
            return java.util.Collections.emptyList();
        }
        
        java.util.List<BlockPos> positions = new java.util.ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (level.isInWorldBounds(pos)) {
                        positions.add(pos);
                    }
                }
            }
        }
        
        return positions;
    }
}

