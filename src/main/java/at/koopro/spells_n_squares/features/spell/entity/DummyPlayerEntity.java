package at.koopro.spells_n_squares.features.spell.entity;

import at.koopro.spells_n_squares.core.base.entity.BaseModEntity;
import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

/**
 * Dummy player entity for testing player model scaling, skins, weapons, etc.
 * Supports Alex/Steve model types and player model scaling system.
 */
public class DummyPlayerEntity extends BaseModEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Synched data for model type (0 = STEVE, 1 = ALEX)
    private static final EntityDataAccessor<Byte> DATA_MODEL_TYPE = 
        SynchedEntityData.defineId(DummyPlayerEntity.class, EntityDataSerializers.BYTE);
    
    // Keys for persistent data storage
    private static final String DATA_KEY_MODEL = "spells_n_squares:dummy_player_model_data";
    private static final String DATA_KEY_MAIN_HAND = "spells_n_squares:dummy_player_main_hand";
    private static final String DATA_KEY_OFF_HAND = "spells_n_squares:dummy_player_off_hand";
    
    // Cached model data (for compatibility with PlayerModelUtils)
    private PlayerModelDataComponent.PlayerModelData cachedModelData = PlayerModelDataComponent.PlayerModelData.empty();
    
    // Cached items
    private ItemStack mainHandItem = ItemStack.EMPTY;
    private ItemStack offHandItem = ItemStack.EMPTY;
    
    public DummyPlayerEntity(EntityType<? extends DummyPlayerEntity> type, Level level) {
        super(type, level);
        setModelType(DummyPlayerModelType.STEVE); // Default to Steve
    }
    
    @Override
    protected void defineCustomSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_MODEL_TYPE, (byte) DummyPlayerModelType.STEVE.ordinal());
    }
    
    /**
     * Gets the model type of this dummy player.
     * 
     * @return The model type
     */
    public DummyPlayerModelType getModelType() {
        byte value = entityData.get(DATA_MODEL_TYPE);
        if (value < 0 || value >= DummyPlayerModelType.values().length) {
            return DummyPlayerModelType.STEVE;
        }
        return DummyPlayerModelType.values()[value];
    }
    
    /**
     * Sets the model type of this dummy player.
     * 
     * @param modelType The model type
     */
    public void setModelType(DummyPlayerModelType modelType) {
        if (modelType == null) {
            modelType = DummyPlayerModelType.STEVE;
        }
        entityData.set(DATA_MODEL_TYPE, (byte) modelType.ordinal());
    }
    
    /**
     * Gets the main hand item.
     * 
     * @return The main hand item
     */
    public ItemStack getMainHandItem() {
        return mainHandItem.copy();
    }
    
    /**
     * Sets the main hand item.
     * 
     * @param item The item to set
     */
    public void setMainHandItem(ItemStack item) {
        if (item == null) {
            item = ItemStack.EMPTY;
        }
        this.mainHandItem = item.copy();
    }
    
    /**
     * Gets the off-hand item.
     * 
     * @return The off-hand item
     */
    public ItemStack getOffHandItem() {
        return offHandItem.copy();
    }
    
    /**
     * Sets the off-hand item.
     * 
     * @param item The item to set
     */
    public void setOffHandItem(ItemStack item) {
        if (item == null) {
            item = ItemStack.EMPTY;
        }
        this.offHandItem = item.copy();
    }
    
    /**
     * Gets the player model data for this dummy player.
     * Uses PlayerModelUtils for compatibility.
     * 
     * @return The player model data
     */
    public PlayerModelDataComponent.PlayerModelData getModelData() {
        // Use PlayerModelUtils if this entity can be treated as a player
        // For now, use cached data stored in persistent data
        return cachedModelData;
    }
    
    /**
     * Sets the player model data for this dummy player.
     * 
     * @param data The model data to set
     */
    public void setModelData(PlayerModelDataComponent.PlayerModelData data) {
        if (data == null) {
            data = PlayerModelDataComponent.PlayerModelData.empty();
        }
        this.cachedModelData = data;
        
        // Store in persistent data for saving
        var persistentData = getPersistentData();
        var result = PlayerModelDataComponent.PlayerModelData.CODEC.encodeStart(
            NbtOps.INSTANCE,
            data
        );
        result.result().ifPresent(tag -> {
            persistentData.put(DATA_KEY_MODEL, tag);
        });
        
        // Refresh dimensions if on server
        if (!level().isClientSide() && this instanceof DummyPlayerEntity) {
            refreshDimensions();
        }
    }
    
    @Override
    protected void saveCustomData(ValueOutput output) {
        super.saveCustomData(output);
        
        // Save model type
        output.store("modelType", com.mojang.serialization.Codec.STRING, getModelType().name());
        
        // Save complex data in persistent data (ItemStack, model data)
        var persistentData = getPersistentData();
        
        // Save model data
        var result = PlayerModelDataComponent.PlayerModelData.CODEC.encodeStart(
            NbtOps.INSTANCE,
            cachedModelData
        );
        result.result().ifPresent(tag -> {
            persistentData.put(DATA_KEY_MODEL, tag);
        });
        
        // Save items using ItemStack.CODEC
        if (!mainHandItem.isEmpty()) {
            var mainHandResult = ItemStack.CODEC.encodeStart(
                NbtOps.INSTANCE,
                mainHandItem
            );
            mainHandResult.result().ifPresent(tag -> {
                persistentData.put(DATA_KEY_MAIN_HAND, tag);
            });
        }
        if (!offHandItem.isEmpty()) {
            var offHandResult = ItemStack.CODEC.encodeStart(
                NbtOps.INSTANCE,
                offHandItem
            );
            offHandResult.result().ifPresent(tag -> {
                persistentData.put(DATA_KEY_OFF_HAND, tag);
            });
        }
    }
    
    @Override
    protected void loadCustomData(ValueInput input) {
        super.loadCustomData(input);
        
        // Load model type
        try {
            String modelTypeStr = input.read("modelType", com.mojang.serialization.Codec.STRING).orElse(null);
            if (modelTypeStr != null) {
                setModelType(DummyPlayerModelType.fromString(modelTypeStr));
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to load model type: {}", e.getMessage());
        }
        
        // Load complex data from persistent data
        var persistentData = getPersistentData();
        
        // Load model data
        try {
            var modelDataTagOpt = persistentData.getCompound(DATA_KEY_MODEL);
            if (modelDataTagOpt.isPresent()) {
                CompoundTag modelDataTag = modelDataTagOpt.get();
                if (!modelDataTag.isEmpty()) {
                    var result = PlayerModelDataComponent.PlayerModelData.CODEC.parse(
                        NbtOps.INSTANCE,
                        modelDataTag
                    ).result();
                    if (result.isPresent()) {
                        cachedModelData = result.get();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to load model data: {}", e.getMessage());
        }
        
        // Load items using ItemStack.CODEC
        try {
            var mainHandTagOpt = persistentData.getCompound(DATA_KEY_MAIN_HAND);
            if (mainHandTagOpt.isPresent()) {
                CompoundTag mainHandTag = mainHandTagOpt.get();
                if (!mainHandTag.isEmpty()) {
                    var result = ItemStack.CODEC.parse(
                        NbtOps.INSTANCE,
                        mainHandTag
                    ).result();
                    if (result.isPresent()) {
                        mainHandItem = result.get();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to load main hand item: {}", e.getMessage());
        }
        
        try {
            var offHandTagOpt = persistentData.getCompound(DATA_KEY_OFF_HAND);
            if (offHandTagOpt.isPresent()) {
                CompoundTag offHandTag = offHandTagOpt.get();
                if (!offHandTag.isEmpty()) {
                    var result = ItemStack.CODEC.parse(
                        NbtOps.INSTANCE,
                        offHandTag
                    ).result();
                    if (result.isPresent()) {
                        offHandItem = result.get();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to load off hand item: {}", e.getMessage());
        }
    }
    
    @Override
    protected boolean onHurt(ServerLevel level, DamageSource source, float amount) {
        // Dummy players don't take damage
        return false;
    }
    
    /**
     * Gets the UUID for this dummy player.
     * Returns a stable UUID based on entity ID for compatibility with PlayerModelUtils.
     * 
     * @return A stable UUID
     */
    @Override
    public java.util.UUID getUUID() {
        // Use a stable UUID based on entity ID for compatibility
        // This ensures the same dummy player always has the same UUID
        return java.util.UUID.nameUUIDFromBytes(("dummy_player_" + getId()).getBytes());
    }
    
    /**
     * Gets the name for this dummy player.
     * 
     * @return The name
     */
    @Override
    public net.minecraft.network.chat.Component getName() {
        return net.minecraft.network.chat.Component.literal("Dummy " + getModelType().toString());
    }
}
