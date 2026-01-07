package at.koopro.spells_n_squares.features.economy.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity for the Vault block.
 * Provides secure storage with 54 slots (6 rows x 9 columns).
 */
public class VaultBlockEntity extends BlockEntity {
    private static final int VAULT_SLOTS = 54;
    private final SimpleContainer inventory;
    
    public VaultBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new SimpleContainer(VAULT_SLOTS) {
            @Override
            public void setChanged() {
                super.setChanged();
                VaultBlockEntity.this.setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        };
        DevLogger.logMethodEntry(this, "VaultBlockEntity", 
            "pos=" + DevLogger.formatPos(pos));
    }
    
    /**
     * Gets the vault inventory.
     * 
     * @return The inventory container
     */
    public SimpleContainer getInventory() {
        return inventory;
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        DevLogger.logDataOperation(this, "saveAdditional", "SAVE", 
            "pos=" + DevLogger.formatPos(worldPosition));
        NonNullList<ItemStack> items = NonNullList.withSize(VAULT_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < VAULT_SLOTS; i++) {
            items.set(i, inventory.getItem(i));
        }
        ContainerHelper.saveAllItems(output, items, false);
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        DevLogger.logDataOperation(this, "loadAdditional", "LOAD", 
            "pos=" + DevLogger.formatPos(worldPosition));
        NonNullList<ItemStack> items = NonNullList.withSize(VAULT_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        
        // Validate and load items
        for (int i = 0; i < VAULT_SLOTS && i < items.size(); i++) {
            ItemStack stack = items.get(i);
            // Validate item stack is not null
            if (stack != null && !stack.isEmpty()) {
                // Validate stack size is reasonable (prevent overflow exploits)
                if (stack.getCount() > stack.getMaxStackSize()) {
                    DevLogger.logWarn(this, "loadAdditional", 
                        "Invalid stack size " + stack.getCount() + " for item " + stack.getItem() + " at slot " + i + ", clamping to max");
                    stack.setCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
                }
                inventory.setItem(i, stack);
            } else {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        
        // Ensure all slots are initialized (in case loaded data had fewer slots)
        for (int i = items.size(); i < VAULT_SLOTS; i++) {
            inventory.setItem(i, ItemStack.EMPTY);
        }
    }
    
    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }
}

