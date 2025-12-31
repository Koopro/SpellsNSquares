package at.koopro.spells_n_squares.features.fx.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity for the EnergyBallBlock.
 * Stores no data, just needed for rendering.
 */
public class EnergyBallBlockEntity extends BlockEntity {
    public EnergyBallBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}







