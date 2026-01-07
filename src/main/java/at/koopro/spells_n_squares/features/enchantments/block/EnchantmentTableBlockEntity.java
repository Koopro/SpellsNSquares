package at.koopro.spells_n_squares.features.enchantments.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity for the Enchantment Table block.
 * Stores the item to be enchanted and the selected enchantment level/type.
 */
public class EnchantmentTableBlockEntity extends BlockEntity {
    private static final int ITEM_SLOT = 1;
    private final SimpleContainer itemContainer;
    private int selectedEnchantmentLevel = 1;
    @Nullable
    private Identifier selectedEnchantmentId;
    
    public EnchantmentTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.itemContainer = new SimpleContainer(ITEM_SLOT) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentTableBlockEntity.this.setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        };
        DevLogger.logMethodEntry(this, "EnchantmentTableBlockEntity", 
            "pos=" + DevLogger.formatPos(pos));
    }
    
    /**
     * Gets the item container (single slot for item to enchant).
     * 
     * @return The item container
     */
    public SimpleContainer getItemContainer() {
        return itemContainer;
    }
    
    /**
     * Gets the item in the enchantment slot.
     * 
     * @return The item stack, or empty if no item
     */
    public ItemStack getItem() {
        return itemContainer.getItem(0);
    }
    
    /**
     * Sets the item in the enchantment slot.
     * 
     * @param stack The item stack to set
     */
    public void setItem(ItemStack stack) {
        itemContainer.setItem(0, stack);
    }
    
    /**
     * Gets the selected enchantment level.
     * 
     * @return The enchantment level (1-3)
     */
    public int getSelectedEnchantmentLevel() {
        return selectedEnchantmentLevel;
    }
    
    /**
     * Sets the selected enchantment level.
     * 
     * @param level The enchantment level (1-3)
     */
    public void setSelectedEnchantmentLevel(int level) {
        this.selectedEnchantmentLevel = Math.max(1, Math.min(3, level));
        setChanged();
    }
    
    /**
     * Gets the selected enchantment ID.
     * 
     * @return The enchantment ID, or null if none selected
     */
    @Nullable
    public Identifier getSelectedEnchantmentId() {
        return selectedEnchantmentId;
    }
    
    /**
     * Sets the selected enchantment ID.
     * 
     * @param enchantmentId The enchantment ID, or null to clear
     */
    public void setSelectedEnchantmentId(@Nullable Identifier enchantmentId) {
        this.selectedEnchantmentId = enchantmentId;
        setChanged();
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        DevLogger.logDataOperation(this, "saveAdditional", "SAVE", 
            "pos=" + DevLogger.formatPos(worldPosition));
        NonNullList<ItemStack> items = NonNullList.withSize(ITEM_SLOT, ItemStack.EMPTY);
        items.set(0, itemContainer.getItem(0));
        ContainerHelper.saveAllItems(output, items, false);
        output.putInt("enchantmentLevel", selectedEnchantmentLevel);
        if (selectedEnchantmentId != null) {
            output.putString("enchantmentId", selectedEnchantmentId.toString());
        }
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        DevLogger.logDataOperation(this, "loadAdditional", "LOAD", 
            "pos=" + DevLogger.formatPos(worldPosition));
        NonNullList<ItemStack> items = NonNullList.withSize(ITEM_SLOT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        
        // Validate and load item
        if (!items.isEmpty() && items.get(0) != null) {
            ItemStack stack = items.get(0);
            // Validate stack size
            if (stack.getCount() > stack.getMaxStackSize()) {
                DevLogger.logWarn(this, "loadAdditional", 
                    "Invalid stack size " + stack.getCount() + " for item " + stack.getItem() + ", clamping to max");
                stack.setCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
            }
            itemContainer.setItem(0, stack);
        } else {
            itemContainer.setItem(0, ItemStack.EMPTY);
        }
        
        // Validate and load enchantment level (must be 1-3)
        int loadedLevel = input.getIntOr("enchantmentLevel", 1);
        if (loadedLevel < 1 || loadedLevel > 3) {
            DevLogger.logWarn(this, "loadAdditional", 
                "Invalid enchantment level " + loadedLevel + ", clamping to valid range (1-3)");
            selectedEnchantmentLevel = Math.max(1, Math.min(3, loadedLevel));
        } else {
            selectedEnchantmentLevel = loadedLevel;
        }
        
        // Validate and load enchantment ID
        String enchantmentIdStr = input.getStringOr("enchantmentId", null);
        if (enchantmentIdStr != null && !enchantmentIdStr.trim().isEmpty()) {
            try {
                Identifier parsedId = Identifier.parse(enchantmentIdStr.trim());
                // Validate identifier format
                if (parsedId != null && parsedId.getNamespace() != null && parsedId.getPath() != null) {
                    selectedEnchantmentId = parsedId;
                } else {
                    DevLogger.logWarn(this, "loadAdditional", 
                        "Invalid enchantment ID format: " + enchantmentIdStr);
                    selectedEnchantmentId = null;
                }
            } catch (Exception e) {
                DevLogger.logWarn(this, "loadAdditional", 
                    "Failed to parse enchantment ID: " + enchantmentIdStr + ", error: " + e.getMessage());
                selectedEnchantmentId = null;
            }
        } else {
            selectedEnchantmentId = null;
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

