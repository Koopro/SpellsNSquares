package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * BlockItem for Newt's Case with GeckoLib item rendering and dimension data preservation.
 */
public class NewtsCaseBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    public NewtsCaseBlockItem(NewtsCaseBlock block, Properties properties) {
        super(block, properties);
        GeoItem.registerSyncedAnimatable(this);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        
        // CRITICAL: If clicking on an existing Newt's Case block, call the block's use() method directly
        // instead of trying to place a new block
        BlockState clickedState = level.getBlockState(pos);
        if (clickedState.getBlock() instanceof NewtsCaseBlock block) {
            // This is an existing Newt's Case block - call the block's use() method directly
            Player player = context.getPlayer();
            InteractionHand hand = context.getHand();
            BlockHitResult hitResult = new BlockHitResult(
                context.getClickLocation(),
                context.getClickedFace(),
                context.getClickedPos(),
                context.isInside()
            );
            return block.use(clickedState, level, pos, player, hand, hitResult);
        }
        
        // Check if ItemStack has dimension data
        PocketDimensionData.PocketDimensionComponent existingData = 
            stack.get(PocketDimensionData.POCKET_DIMENSION.get());
        
        // Place the block first (only if not clicking on existing Newt's Case)
        InteractionResult result = super.useOn(context);
        
        // If placement was successful and we have dimension data, restore it to BlockEntity
        if (result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) {
            if (!level.isClientSide()) {
                BlockState placedState = level.getBlockState(pos);
                if (placedState.getBlock() instanceof NewtsCaseBlock) {
                    if (level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity) {
                        if (existingData != null) {
                            // Restore existing dimension data (same UUID = same dimension)
                            blockEntity.setDimensionData(existingData);
                        }
                        // If no existing data, BlockEntity will create new dimension data with new UUID
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private at.koopro.spells_n_squares.features.storage.block.client.NewtsCaseItemRenderer renderer;

            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new at.koopro.spells_n_squares.features.storage.block.client.NewtsCaseItemRenderer();
                }

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add idle animation when held in hand
        controllers.add(new AnimationController<>("idle", 0, state -> {
            // Play idle animation when item is held
            state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

