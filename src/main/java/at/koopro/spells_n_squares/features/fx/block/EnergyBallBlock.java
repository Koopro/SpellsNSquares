package at.koopro.spells_n_squares.features.fx.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A block that renders as a glowing energy ball using shaders.
 * Used for testing and debugging shader effects on blocks.
 */
public class EnergyBallBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
    
    public EnergyBallBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE; // We render via BlockEntityRenderer
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return FxBlockEntities.ENERGY_BALL_BLOCK_ENTITY != null 
            ? new EnergyBallBlockEntity(FxBlockEntities.ENERGY_BALL_BLOCK_ENTITY.get(), pos, state)
            : null;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level, 
                                                                   BlockState state, 
                                                                   BlockEntityType<T> type) {
        return null; // No ticker needed
    }
}


