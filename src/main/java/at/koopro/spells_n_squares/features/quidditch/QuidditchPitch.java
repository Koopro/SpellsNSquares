package at.koopro.spells_n_squares.features.quidditch;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Represents a Quidditch pitch area.
 * Defines the boundaries and properties of a Quidditch pitch.
 */
public record QuidditchPitch(
    String pitchName,
    ServerLevel level,
    BlockPos centerPos,
    int radius,
    BlockPos team1Spawn,
    BlockPos team2Spawn
) {
    /**
     * Checks if a position is within the pitch boundaries.
     */
    public boolean isWithinPitch(BlockPos pos) {
        int dx = pos.getX() - centerPos.getX();
        int dz = pos.getZ() - centerPos.getZ();
        return (dx * dx + dz * dz) <= (radius * radius);
    }
    
    /**
     * Gets the pitch bounds (min and max coordinates).
     */
    public Bounds getBounds() {
        return new Bounds(
            centerPos.offset(-radius, 0, -radius),
            centerPos.offset(radius, 255, radius)
        );
    }
    
    public record Bounds(BlockPos min, BlockPos max) {}
}















