package at.koopro.spells_n_squares.block.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

/**
 * Gillyweed - grows in water, drops gillyweed item.
 */
public class GillyweedBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    
    public GillyweedBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }
    
    @Override
    public int getMaxAge() {
        return 7;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        // Only grow if in water
        if (level.getFluidState(pos.below()).is(Fluids.WATER) || 
            level.getFluidState(pos).is(Fluids.WATER)) {
            super.randomTick(state, level, pos, random);
        }
    }
}
