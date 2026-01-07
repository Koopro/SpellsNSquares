package at.koopro.spells_n_squares.core.base.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Base class for GeckoLib block entities with common functionality.
 * Provides GeckoLib setup, animation management, data persistence, and update packet helpers.
 */
public abstract class BaseGeoBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    public BaseGeoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        DevLogger.logMethodEntry(this, "BaseGeoBlockEntity", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", type=" + (type != null ? type.toString() : "null"));
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        DevLogger.logMethodEntry(this, "registerControllers");
        setupAnimations(controllers);
        DevLogger.logMethodExit(this, "registerControllers");
    }
    
    /**
     * Sets up animations for this block entity.
     * Subclasses should override to register their animation controllers.
     * Default implementation does nothing (no animations).
     * 
     * @param controllers The controller registrar
     */
    protected void setupAnimations(AnimatableManager.ControllerRegistrar controllers) {
        DevLogger.logMethodEntry(this, "setupAnimations");
        // Override in subclasses to add animations
        DevLogger.logMethodExit(this, "setupAnimations");
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        DevLogger.logMethodEntry(this, "getUpdatePacket", "pos=" + DevLogger.formatPos(worldPosition));
        Packet<ClientGamePacketListener> result = ClientboundBlockEntityDataPacket.create(this);
        DevLogger.logMethodExit(this, "getUpdatePacket", "packet");
        return result;
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        DevLogger.logMethodEntry(this, "getUpdateTag", "pos=" + DevLogger.formatPos(worldPosition));
        CompoundTag tag = saveWithFullMetadata(registries);
        saveCustomDataToTag(tag, registries);
        DevLogger.logMethodExit(this, "getUpdateTag", "tag");
        return tag;
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        DevLogger.logDataOperation(this, "loadAdditional", "LOAD", "pos=" + DevLogger.formatPos(worldPosition));
        DevLogger.logMethodEntry(this, "loadAdditional", "pos=" + DevLogger.formatPos(worldPosition));
        super.loadAdditional(input);
        loadCustomDataFromInput(input);
        DevLogger.logMethodExit(this, "loadAdditional");
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        DevLogger.logDataOperation(this, "saveAdditional", "SAVE", "pos=" + DevLogger.formatPos(worldPosition));
        DevLogger.logMethodEntry(this, "saveAdditional", "pos=" + DevLogger.formatPos(worldPosition));
        super.saveAdditional(output);
        saveCustomDataToOutput(output);
        DevLogger.logMethodExit(this, "saveAdditional");
    }
    
    /**
     * Saves custom data to a CompoundTag.
     * Subclasses should override to save their specific data.
     * 
     * @param tag The tag to save to
     * @param registries The registry provider
     */
    protected void saveCustomDataToTag(CompoundTag tag, HolderLookup.Provider registries) {
        DevLogger.logMethodEntry(this, "saveCustomDataToTag");
        // Override in subclasses
        DevLogger.logMethodExit(this, "saveCustomDataToTag");
    }
    
    /**
     * Loads custom data from a ValueInput.
     * Subclasses should override to load their specific data.
     * 
     * @param input The input to load from
     */
    protected void loadCustomDataFromInput(ValueInput input) {
        DevLogger.logMethodEntry(this, "loadCustomDataFromInput");
        // Override in subclasses
        DevLogger.logMethodExit(this, "loadCustomDataFromInput");
    }
    
    /**
     * Saves custom data to a ValueOutput.
     * Subclasses should override to save their specific data.
     * 
     * @param output The output to save to
     */
    protected void saveCustomDataToOutput(ValueOutput output) {
        DevLogger.logMethodEntry(this, "saveCustomDataToOutput");
        // Override in subclasses
        DevLogger.logMethodExit(this, "saveCustomDataToOutput");
    }
    
    /**
     * Marks this block entity as changed and sends an update to clients.
     */
    protected void markChangedAndUpdate() {
        DevLogger.logStateChange(this, "markChangedAndUpdate", "pos=" + DevLogger.formatPos(worldPosition));
        DevLogger.logMethodEntry(this, "markChangedAndUpdate", "pos=" + DevLogger.formatPos(worldPosition));
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        DevLogger.logMethodExit(this, "markChangedAndUpdate");
    }
    
    /**
     * Gets a data component value from the block entity's item stack representation.
     * 
     * @param <T> The component type
     * @param componentType The component type
     * @return The component value, or null if not present
     */
    protected <T> T getDataComponent(net.minecraft.core.component.DataComponentType<T> componentType) {
        // This would typically be used when converting block entity to item stack
        // Override in subclasses if needed
        return null;
    }
    
    /**
     * Sets a data component value on the block entity's item stack representation.
     * 
     * @param <T> The component type
     * @param componentType The component type
     * @param value The value to set
     */
    protected <T> void setDataComponent(net.minecraft.core.component.DataComponentType<T> componentType, T value) {
        // This would typically be used when converting block entity to item stack
        // Override in subclasses if needed
    }
    
    /**
     * Creates an ItemStack from this block entity with preserved data.
     * Subclasses should override to preserve their specific data.
     * 
     * @return The ItemStack with preserved data
     */
    public net.minecraft.world.item.ItemStack getItemStack() {
        return new net.minecraft.world.item.ItemStack(getBlockState().getBlock().asItem());
    }
}


