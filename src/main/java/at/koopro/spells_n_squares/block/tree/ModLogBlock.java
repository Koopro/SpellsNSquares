package at.koopro.spells_n_squares.block.tree;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Log block for custom tree types.
 * Extends RotatedPillarBlock for proper axis rotation.
 */
public class ModLogBlock extends RotatedPillarBlock {
    
    public ModLogBlock(Properties properties) {
        super(properties);
    }
    
    /**
     * Creates default properties for a log block.
     */
    public static BlockBehaviour.Properties createDefaultProperties(MapColor topColor, MapColor sideColor) {
        return BlockBehaviour.Properties.of()
            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == net.minecraft.core.Direction.Axis.Y ? topColor : sideColor)
            .strength(2.0f)
            .sound(SoundType.WOOD)
            .ignitedByLava();
    }
    
    /**
     * Creates default properties with a single color for stripped logs.
     */
    public static BlockBehaviour.Properties createStrippedProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(2.0f)
            .sound(SoundType.WOOD)
            .ignitedByLava();
    }
}








