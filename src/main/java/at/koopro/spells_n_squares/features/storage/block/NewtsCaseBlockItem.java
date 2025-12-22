package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Custom BlockItem for Newt's Case that handles dimension data transfer.
 */
public class NewtsCaseBlockItem extends BlockItem {
    
    public NewtsCaseBlockItem(NewtsCaseBlock block, Properties properties) {
        super(block, properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        
        // Check the clicked position (the block face you clicked on)
        BlockState clickedState = level.getBlockState(clickedPos);
        
        // Also check the block on the other side of the clicked face
        // When you click a face, getClickedPos() is the block the face belongs to
        // getClickedFace() is the direction of the face (UP = top face, DOWN = bottom face, etc.)
        // So the block you're actually clicking on is clickedPos.relative(getClickedFace())
        BlockPos targetBlockPos = clickedPos.relative(context.getClickedFace());
        BlockState targetBlockState = level.getBlockState(targetBlockPos);
        
        System.out.println("[BlockItem] useOn() called at " + clickedPos + ", face: " + context.getClickedFace());
        System.out.println("[BlockItem] Clicked block (at clickedPos): " + BuiltInRegistries.BLOCK.getKey(clickedState.getBlock()));
        System.out.println("[BlockItem] Target block (relative to face): " + BuiltInRegistries.BLOCK.getKey(targetBlockState.getBlock()) + " at " + targetBlockPos);
        
        // Check if either position has a Newt's Case block
        boolean clickedIsNewtsCase = clickedState.getBlock() instanceof NewtsCaseBlock || 
                                     clickedState.getBlock() == this.getBlock() ||
                                     BuiltInRegistries.BLOCK.getKey(clickedState.getBlock())
                                         .equals(BuiltInRegistries.BLOCK.getKey(this.getBlock()));
        
        boolean targetIsNewtsCase = targetBlockState.getBlock() instanceof NewtsCaseBlock || 
                                    targetBlockState.getBlock() == this.getBlock() ||
                                    BuiltInRegistries.BLOCK.getKey(targetBlockState.getBlock())
                                        .equals(BuiltInRegistries.BLOCK.getKey(this.getBlock()));
        
        if (clickedIsNewtsCase || targetIsNewtsCase) {
            System.out.println("[BlockItem] Detected NewtsCaseBlock, calling block's use() method directly");
            
            // Determine which position has the Newt's Case block
            BlockPos newtsCasePos = clickedIsNewtsCase ? clickedPos : targetBlockPos;
            BlockState newtsCaseState = level.getBlockState(newtsCasePos);
            
            // Create a BlockHitResult for the block's use() method
            // Use the clicked face direction, but if we're using targetBlockPos, we need the opposite face
            Direction hitFace = context.getClickedFace();
            if (!clickedIsNewtsCase && targetIsNewtsCase) {
                // We clicked on a different block, so the face is the opposite
                hitFace = context.getClickedFace().getOpposite();
            }
            
            Vec3 hitVec = Vec3.atCenterOf(newtsCasePos);
            BlockHitResult blockHit = new BlockHitResult(hitVec, hitFace, newtsCasePos, false);
            
            // Get the player and hand from context
            Player player = context.getPlayer();
            InteractionHand hand = context.getHand();
            
            // Call the block's use() method directly
            if (newtsCaseState.getBlock() instanceof NewtsCaseBlock newtsCaseBlock) {
                InteractionResult result = newtsCaseBlock.use(newtsCaseState, level, newtsCasePos, player, hand, blockHit);
                System.out.println("[BlockItem] Block's use() returned: " + result);
                // Return CONSUME to prevent other interactions
                if (result == InteractionResult.SUCCESS) {
                    return InteractionResult.CONSUME;
                }
                return result;
            }
            
            // Fallback: return PASS if block type check fails
            return InteractionResult.PASS;
        }
        
        // Otherwise, use default BlockItem behavior (for placing)
        System.out.println("[BlockItem] Using default BlockItem behavior");
        return super.useOn(context);
    }
    
    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        
        System.out.println("[BlockItem] placeBlock() called, placed: " + placed + ", pos: " + context.getClickedPos());
        
        if (placed && !context.getLevel().isClientSide()) {
            // Initialize BlockEntity with dimension data from ItemStack
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();
            
            System.out.println("[BlockItem] Block placed, checking BlockEntity at " + pos);
            BlockEntity be = level.getBlockEntity(pos);
            System.out.println("[BlockItem] BlockEntity type: " + (be != null ? be.getClass().getSimpleName() : "null"));
            
            if (be instanceof NewtsCaseBlockEntity blockEntity) {
                ItemStack stack = context.getItemInHand();
                PocketDimensionData.PocketDimensionComponent data = stack.get(PocketDimensionData.POCKET_DIMENSION.get());
                
                if (data != null) {
                    blockEntity.setDimensionData(data);
                    System.out.println("[BlockItem] Restored dimension data from ItemStack");
                } else {
                    // Initialize with default Newt's case data
                    blockEntity.setDimensionData(PocketDimensionData.PocketDimensionComponent.createNewtsCase(32));
                    System.out.println("[BlockItem] Created new dimension data");
                }
            } else {
                System.out.println("[BlockItem] WARNING: BlockEntity is not NewtsCaseBlockEntity!");
            }
        }
        
        return placed;
    }
}


