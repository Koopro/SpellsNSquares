package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Planks block for custom tree types.
 */
public class ModPlanksBlock extends Block {
    
    public ModPlanksBlock(Properties properties) {
        super(properties);
    }
    
    /**
     * Creates default properties for a planks block.
     */
    public static BlockBehaviour.Properties createDefaultProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(2.0f, 3.0f)
            .sound(SoundType.WOOD)
            .ignitedByLava();
    }
}








