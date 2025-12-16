package at.koopro.spells_n_squares.block.tree;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Wood block (bark on all sides) for custom tree types.
 * Extends RotatedPillarBlock for proper axis rotation like vanilla.
 */
public class ModWoodBlock extends RotatedPillarBlock {
    
    public ModWoodBlock(Properties properties) {
        super(properties);
    }
    
    /**
     * Creates default properties for a wood block.
     */
    public static BlockBehaviour.Properties createDefaultProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(2.0f)
            .sound(SoundType.WOOD)
            .ignitedByLava();
    }
}








