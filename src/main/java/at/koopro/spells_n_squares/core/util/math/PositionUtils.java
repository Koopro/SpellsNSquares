package at.koopro.spells_n_squares.core.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for BlockPos and position calculations.
 * Provides position distance calculations, offset calculations, validation, and range generation.
 */
public final class PositionUtils {
    private PositionUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Calculates the distance between two block positions.
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return The distance
     */
    public static double distance(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null) {
            return 0.0;
        }
        return Math.sqrt(distanceSquared(pos1, pos2));
    }
    
    /**
     * Calculates the squared distance between two block positions (faster, no sqrt).
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return The squared distance
     */
    public static double distanceSquared(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null) {
            return 0.0;
        }
        double dx = pos2.getX() - pos1.getX();
        double dy = pos2.getY() - pos1.getY();
        double dz = pos2.getZ() - pos1.getZ();
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Calculates the distance between a block position and a Vec3 position.
     * 
     * @param blockPos The block position
     * @param vecPos The Vec3 position
     * @return The distance
     */
    public static double distance(BlockPos blockPos, Vec3 vecPos) {
        if (blockPos == null || vecPos == null) {
            return 0.0;
        }
        double dx = vecPos.x - (blockPos.getX() + 0.5);
        double dy = vecPos.y - (blockPos.getY() + 0.5);
        double dz = vecPos.z - (blockPos.getZ() + 0.5);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
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
     * Calculates an offset position by X, Y, Z offsets.
     * 
     * @param pos The starting position
     * @param x X offset
     * @param y Y offset
     * @param z Z offset
     * @return The offset position
     */
    public static BlockPos offset(BlockPos pos, int x, int y, int z) {
        if (pos == null) {
            return null;
        }
        return pos.offset(x, y, z);
    }
    
    /**
     * Validates that a block position is within world bounds.
     * 
     * @param level The level
     * @param pos The position to validate
     * @return true if valid
     */
    public static boolean isValid(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        return level.isInWorldBounds(pos);
    }
    
    /**
     * Gets all block positions within a range of a center position.
     * 
     * @param center The center position
     * @param radius The radius (in blocks)
     * @return List of block positions within range
     */
    public static List<BlockPos> getPositionsInRange(BlockPos center, int radius) {
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
     * Gets all block positions within a range of a center position (spherical).
     * 
     * @param center The center position
     * @param radius The radius (in blocks)
     * @return List of block positions within range
     */
    public static List<BlockPos> getPositionsInSphere(BlockPos center, int radius) {
        return getPositionsInRange(center, radius);
    }
    
    /**
     * Gets all block positions within a rectangular area.
     * 
     * @param minPos The minimum position (corner)
     * @param maxPos The maximum position (corner)
     * @return List of block positions in the area
     */
    public static List<BlockPos> getPositionsInArea(BlockPos minPos, BlockPos maxPos) {
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
     * Converts a BlockPos to a Vec3 (center of block).
     * 
     * @param pos The block position
     * @return The Vec3 position (center of block)
     */
    public static Vec3 toVec3(BlockPos pos) {
        if (pos == null) {
            return Vec3.ZERO;
        }
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
    
    /**
     * Converts a Vec3 to a BlockPos (floors coordinates).
     * 
     * @param vec The Vec3 position
     * @return The BlockPos
     */
    public static BlockPos toBlockPos(Vec3 vec) {
        if (vec == null) {
            return BlockPos.ZERO;
        }
        return BlockPos.containing(vec.x, vec.y, vec.z);
    }
    
    /**
     * Checks if a position is within a certain distance of another position.
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @param maxDistance The maximum distance
     * @return true if within range
     */
    public static boolean isWithinRange(BlockPos pos1, BlockPos pos2, double maxDistance) {
        if (pos1 == null || pos2 == null) {
            return false;
        }
        return distanceSquared(pos1, pos2) <= maxDistance * maxDistance;
    }
    
    /**
     * Gets the direction from one position to another.
     * 
     * @param from The starting position
     * @param to The target position
     * @return The direction, or null if positions are null
     */
    public static Direction getDirection(BlockPos from, BlockPos to) {
        if (from == null || to == null) {
            return null;
        }
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        
        int absX = Math.abs(dx);
        int absY = Math.abs(dy);
        int absZ = Math.abs(dz);
        
        if (absX >= absY && absX >= absZ) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else if (absY >= absX && absY >= absZ) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }
}


