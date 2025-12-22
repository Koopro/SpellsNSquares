package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * BlockEntity for storing Newt's Case dimension data.
 */
public class NewtsCaseBlockEntity extends BlockEntity {
    private PocketDimensionData.PocketDimensionComponent dimensionData;
    
    public NewtsCaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public PocketDimensionData.PocketDimensionComponent getDimensionData() {
        if (dimensionData == null) {
            dimensionData = PocketDimensionData.PocketDimensionComponent.createNewtsCase(32);
        }
        return dimensionData;
    }
    
    public void setDimensionData(PocketDimensionData.PocketDimensionComponent data) {
        this.dimensionData = data;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    // Note: In 1.21.11, BlockEntity persistence is handled differently
    // We'll store dimension data in the ItemStack when breaking the block
    // and restore it when placing. For now, data is kept in memory.
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = saveWithFullMetadata(registries);
        if (dimensionData != null) {
            tag.put("dimensionData", PocketDimensionData.PocketDimensionComponent.CODEC.encodeStart(
                NbtOps.INSTANCE, dimensionData).result().orElse(new CompoundTag()));
        }
        return tag;
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.dimensionData = input.read("dimensionData", PocketDimensionData.PocketDimensionComponent.CODEC)
            .orElse(null);
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (dimensionData != null) {
            output.store("dimensionData", PocketDimensionData.PocketDimensionComponent.CODEC, dimensionData);
        }
    }
    
    /**
     * Creates an ItemStack from this BlockEntity with preserved dimension data.
     */
    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(getBlockState().getBlock().asItem());
        PocketDimensionData.PocketDimensionComponent data = getDimensionData();
        stack.set(PocketDimensionData.POCKET_DIMENSION.get(), data);
        return stack;
    }
}


