package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.core.base.item.BaseGeoBlockItem;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * BlockItem for Newt's Case with GeckoLib item rendering and dimension data preservation.
 */
public class NewtsCaseBlockItem extends BaseGeoBlockItem {
    
    public NewtsCaseBlockItem(NewtsCaseBlock block, Properties properties) {
        super(block, properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        DevLogger.logItemInteraction(this, "useOn", context.getPlayer(), context.getItemInHand());
        DevLogger.logMethodEntry(this, "useOn", 
            "pos=" + DevLogger.formatPos(context.getClickedPos()) + 
            ", player=" + (context.getPlayer() != null ? context.getPlayer().getName().getString() : "null"));
        
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        
        // CRITICAL: If clicking on an existing Newt's Case block, call the block's interaction method directly
        // instead of trying to place a new block
        BlockState clickedState = level.getBlockState(pos);
        if (clickedState.getBlock() instanceof NewtsCaseBlock block) {
            // This is an existing Newt's Case block - call the block's interaction method directly
            DevLogger.logDebug(this, "useOn", "Clicking on existing Newt's Case block");
            Player player = context.getPlayer();
            BlockHitResult hitResult = new BlockHitResult(
                context.getClickLocation(),
                context.getClickedFace(),
                context.getClickedPos(),
                context.isInside()
            );
            InteractionResult result = block.handleInteraction(clickedState, level, pos, player, context.getHand(), hitResult);
            DevLogger.logMethodExit(this, "useOn", result);
            return result;
        }
        
        // Check if ItemStack has dimension data
        PocketDimensionData.PocketDimensionComponent existingData = 
            stack.get(PocketDimensionData.POCKET_DIMENSION.get());
        
        DevLogger.logParameter(this, "useOn", "hasDimensionData", existingData != null);
        if (existingData != null) {
            DevLogger.logStateChange(this, "useOn", 
                "Preserving dimension data, dimensionId=" + existingData.dimensionId());
        }
        
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
                            DevLogger.logStateChange(this, "useOn", 
                                "Restoring dimension data to BlockEntity, pos=" + DevLogger.formatPos(pos));
                            blockEntity.setDimensionData(existingData);
                        } else {
                            DevLogger.logDebug(this, "useOn", "No existing dimension data, BlockEntity will create new");
                        }
                        // If no existing data, BlockEntity will create new dimension data with new UUID
                    }
                }
            }
        }
        
        DevLogger.logMethodExit(this, "useOn", result);
        return result;
    }
    
    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new at.koopro.spells_n_squares.features.storage.block.client.NewtsCaseItemRenderer();
    }

    @Override
    protected void setupAnimations(AnimatableManager.ControllerRegistrar controllers) {
        // Add idle animation when held in hand
        controllers.add(new AnimationController<>("idle", 0, state -> {
            // Play idle animation when item is held
            state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }
}

