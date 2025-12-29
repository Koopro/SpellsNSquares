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
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * BlockEntity for storing Newt's Case dimension data and managing animations.
 */
public class NewtsCaseBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation OPEN_ANIMATION = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation CLOSE_ANIMATION = RawAnimation.begin().thenPlayAndHold("close");

    private PocketDimensionData.PocketDimensionComponent dimensionData;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean lastOpenState = false;
    private boolean animationInitialized = false;
    private long lastOpenedGameTime = 0; // Use 0 instead of Long.MIN_VALUE to avoid overflow issues
    
    public NewtsCaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // Initialize lastOpenState based on the actual block state
        if (state.hasProperty(NewtsCaseBlock.OPEN)) {
            this.lastOpenState = state.getValue(NewtsCaseBlock.OPEN);
        } else {
            this.lastOpenState = false;
        }
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

    public void markOpened(long gameTime) {
        this.lastOpenedGameTime = gameTime;
    }

    public boolean wasJustOpened(long gameTime, long thresholdTicks) {
        // If lastOpenedGameTime is 0, it means it was never marked as opened, so it wasn't just opened
        if (lastOpenedGameTime == 0) {
            return false;
        }
        // Check if it was opened within the threshold
        return (gameTime - lastOpenedGameTime) <= thresholdTicks && (gameTime - lastOpenedGameTime) >= 0;
    }
    
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
        // Restore lastOpenState from saved data, or sync with current block state
        boolean savedState = input.getBooleanOr("lastOpenState", false);
        // Sync with current block state if available (more reliable than saved state)
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.hasProperty(NewtsCaseBlock.OPEN)) {
                this.lastOpenState = state.getValue(NewtsCaseBlock.OPEN);
            } else {
                this.lastOpenState = savedState;
            }
        } else {
            this.lastOpenState = savedState;
        }
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (dimensionData != null) {
            output.store("dimensionData", PocketDimensionData.PocketDimensionComponent.CODEC, dimensionData);
        }
        // Save lastOpenState to persist animation state across world unloads
        // Sync with current block state before saving to ensure accuracy
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.hasProperty(NewtsCaseBlock.OPEN)) {
                this.lastOpenState = state.getValue(NewtsCaseBlock.OPEN);
            }
        }
        output.putBoolean("lastOpenState", this.lastOpenState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("state", 0, state -> {
            BlockState blockState = null;
            
            // Always read from level to ensure we get the latest state (important for multiplayer)
            if (this.level != null) {
                blockState = this.level.getBlockState(this.worldPosition);
            } else if (state.hasData(DataTickets.BLOCKSTATE)) {
                blockState = state.getData(DataTickets.BLOCKSTATE);
            }

            if (blockState == null || !blockState.hasProperty(NewtsCaseBlock.OPEN)) {
                return PlayState.STOP;
            }

            boolean open = blockState.getValue(NewtsCaseBlock.OPEN);

            // Check if state changed or animation hasn't been initialized - if so, set/restart animation
            if (open != lastOpenState || !animationInitialized) {
                // State changed or first time - trigger animation
                RawAnimation animation = open ? OPEN_ANIMATION : CLOSE_ANIMATION;
                state.setAnimation(animation);
                lastOpenState = open;
                animationInitialized = true;
                return PlayState.CONTINUE;
            }
            
            // State hasn't changed - continue current animation
            // Don't set animation again to avoid interrupting the hold state
            return PlayState.CONTINUE;
        }));
    }
    
    /**
     * Called when the block state changes. Updates lastOpenState to trigger animation.
     */
    public void onBlockStateChanged(BlockState newState) {
        if (newState.hasProperty(NewtsCaseBlock.OPEN)) {
            boolean newOpenState = newState.getValue(NewtsCaseBlock.OPEN);
            if (newOpenState != lastOpenState) {
                lastOpenState = newOpenState;
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
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

