package at.koopro.spells_n_squares.features.environment.block;

import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Wolfsbane Plant - standard crop that drops wolfsbane ingredient.
 */
public class WolfsbanePlantBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    
    public WolfsbanePlantBlock(Properties properties) {
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
}
