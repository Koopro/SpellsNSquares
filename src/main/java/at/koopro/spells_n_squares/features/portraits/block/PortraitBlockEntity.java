package at.koopro.spells_n_squares.features.portraits.block;

import at.koopro.spells_n_squares.features.portraits.PortraitData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * BlockEntity for storing portrait data.
 */
public class PortraitBlockEntity extends BlockEntity {
    private PortraitData.PortraitComponent portraitData;
    
    public PortraitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public PortraitData.PortraitComponent getPortraitData() {
        return portraitData;
    }
    
    public void setPortraitData(PortraitData.PortraitComponent data) {
        this.portraitData = data;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.portraitData = input.read("PortraitData", PortraitData.PortraitComponent.CODEC)
            .orElse(null);
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (portraitData != null) {
            output.store("PortraitData", PortraitData.PortraitComponent.CODEC, portraitData);
        }
    }
}




