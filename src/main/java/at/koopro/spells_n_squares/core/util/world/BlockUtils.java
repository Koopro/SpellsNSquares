package at.koopro.spells_n_squares.core.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for common block operations.
 * Provides block type checking, position calculations, filtering, and more.
 */
public final class BlockUtils {
    private BlockUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a block at a position is air.
     * 
     * @param level The level
     * @param pos The block position
     * @return true if air, false if null or invalid
     */
    public static boolean isAir(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        return level.getBlockState(pos).isAir();
    }
    
    /**
     * Checks if a block state is air.
     * 
     * @param state The block state
     * @return true if air, false if null
     */
    public static boolean isAir(BlockState state) {
        if (state == null) {
            return false;
        }
        return state.isAir();
    }
    
    /**
     * Checks if a block at a position is a specific block.
     * 
     * @param level The level
     * @param pos The block position
     * @param block The block to check for
     * @return true if matches
     */
    public static boolean isBlock(Level level, BlockPos pos, Block block) {
        if (level == null || pos == null || block == null) {
            return false;
        }
        return level.getBlockState(pos).is(block);
    }
    
    /**
     * Gets the block at a position.
     * 
     * @param level The level
     * @param pos The block position
     * @return The block, or Blocks.AIR if null or invalid
     */
    public static Block getBlock(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return Blocks.AIR;
        }
        return level.getBlockState(pos).getBlock();
    }
    
    /**
     * Gets the block state at a position.
     * 
     * @param level The level
     * @param pos The block position
     * @return The block state, or Blocks.AIR.defaultBlockState() if null or invalid
     */
    public static BlockState getBlockState(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return Blocks.AIR.defaultBlockState();
        }
        return level.getBlockState(pos);
    }
    
    /**
     * Calculates an offset position from a starting position.
     * 
     * @param pos The starting position
     * @param direction The direction to offset
     * @param distance The distance to offset
     * @return The offset position
     */
    public static BlockPos offset(BlockPos pos, Direction direction, int distance) {
        if (pos == null || direction == null) {
            return pos;
        }
        if (distance == 0) {
            return pos;
        }
        return pos.relative(direction, distance);
    }
    
    /**
     * Calculates an offset position from a starting position (distance 1).
     * 
     * @param pos The starting position
     * @param direction The direction to offset
     * @return The offset position
     */
    public static BlockPos offset(BlockPos pos, Direction direction) {
        return offset(pos, direction, 1);
    }
    
    /**
     * Gets all block positions within a range of a center position.
     * 
     * @param center The center position
     * @param radius The radius (in blocks)
     * @return List of block positions within range
     */
    public static List<BlockPos> getBlocksInRange(BlockPos center, int radius) {
        if (center == null || radius <= 0) {
            return List.of();
        }
        List<BlockPos> positions = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        positions.add(center.offset(x, y, z));
                    }
                }
            }
        }
        return positions;
    }
    
    /**
     * Gets all block positions within a rectangular area.
     * 
     * @param minPos The minimum position (corner)
     * @param maxPos The maximum position (corner)
     * @return List of block positions in the area
     */
    public static List<BlockPos> getBlocksInArea(BlockPos minPos, BlockPos maxPos) {
        if (minPos == null || maxPos == null) {
            return List.of();
        }
        List<BlockPos> positions = new ArrayList<>();
        int minX = Math.min(minPos.getX(), maxPos.getX());
        int maxX = Math.max(minPos.getX(), maxPos.getX());
        int minY = Math.min(minPos.getY(), maxPos.getY());
        int maxY = Math.max(minPos.getY(), maxPos.getY());
        int minZ = Math.min(minPos.getZ(), maxPos.getZ());
        int maxZ = Math.max(minPos.getZ(), maxPos.getZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }
    
    /**
     * Filters block positions based on a predicate.
     * 
     * @param level The level
     * @param positions The positions to filter
     * @param predicate The filter predicate (receives BlockState)
     * @return List of matching positions
     */
    public static List<BlockPos> filter(Level level, List<BlockPos> positions, Predicate<BlockState> predicate) {
        if (level == null || positions == null || predicate == null) {
            return List.of();
        }
        return positions.stream()
            .filter(pos -> predicate.test(getBlockState(level, pos)))
            .toList();
    }
    
    /**
     * Checks if a block position is valid (within world bounds).
     * 
     * @param level The level
     * @param pos The block position
     * @return true if valid
     */
    public static boolean isValid(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        return level.isInWorldBounds(pos);
    }
    
    /**
     * Checks if a block is solid (can be stood on).
     * 
     * @param level The level
     * @param pos The block position
     * @return true if solid
     */
    public static boolean isSolid(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        // Use canOcclude() instead of deprecated isSolid()
        return state.canOcclude();
    }
    
    /**
     * Checks if a block is replaceable (can be replaced by other blocks).
     * 
     * @param level The level
     * @param pos The block position
     * @return true if replaceable
     */
    public static boolean isReplaceable(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced();
    }
}


