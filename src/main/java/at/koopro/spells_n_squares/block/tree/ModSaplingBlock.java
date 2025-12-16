package at.koopro.spells_n_squares.block.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Sapling block for custom tree types.
 * Extends SaplingBlock for proper tree growth behavior.
 */
public class ModSaplingBlock extends SaplingBlock implements BonemealableBlock {
    
    public ModSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }
    
    /**
     * Creates default properties for a sapling block.
     */
    public static BlockBehaviour.Properties createDefaultProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .instabreak()
            .sound(SoundType.GRASS)
            .noCollision()
            .randomTicks();
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(7) == 0) {
            this.advanceTree(level, pos, state, random);
        }
    }
}

