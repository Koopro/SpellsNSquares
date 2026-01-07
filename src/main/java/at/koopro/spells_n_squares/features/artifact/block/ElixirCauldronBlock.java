package at.koopro.spells_n_squares.features.artifact.block;

import at.koopro.spells_n_squares.features.artifact.fluid.ElixirFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Custom cauldron block that can hold Elixir Base or Elixir of Life fluids.
 * Implements cauldron behavior manually since LayeredCauldronBlock doesn't support custom fluids easily.
 */
public class ElixirCauldronBlock extends Block {
    
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 3);
    
    private final ElixirType elixirType;
    
    public ElixirCauldronBlock(BlockBehaviour.Properties properties, ElixirType type) {
        super(properties);
        this.elixirType = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }
    
    public ElixirType getElixirType() {
        return elixirType;
    }
    
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, 
                                         Player player, InteractionHand hand, BlockHitResult hit) {
        int levelValue = state.getValue(LEVEL);
        
        // Handle bucket interactions
        if (stack.is(Items.BUCKET)) {
            // Empty cauldron with bucket
            if (levelValue == 3 && !level.isClientSide()) {
                ItemStack bucket = getBucketForType();
                if (stack.getCount() == 1) {
                    player.setItemInHand(hand, bucket);
                } else {
                    stack.shrink(1);
                    if (!player.getInventory().add(bucket)) {
                        player.drop(bucket, false);
                    }
                }
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        } else if (stack.getItem() == getBucketForType().getItem()) {
            // Fill cauldron with bucket
            if (levelValue < 3 && !level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    } else {
                        player.getInventory().add(new ItemStack(Items.BUCKET));
                    }
                }
                level.setBlock(pos, state.setValue(LEVEL, 3), 3);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        
        // Handle bottle/bowl interactions (delegate to interaction handler)
        return ElixirCauldronInteraction.interact(state, level, pos, player, hand);
    }
    
    /**
     * Gets the bucket item for this cauldron's fluid type.
     */
    private ItemStack getBucketForType() {
        return switch (elixirType) {
            case BASE -> new ItemStack(ElixirFluids.ELIXIR_BASE_BUCKET.get());
            case LIFE -> new ItemStack(ElixirFluids.ELIXIR_OF_LIFE_BUCKET.get());
        };
    }
    
    public enum ElixirType {
        BASE,  // Elixir Base (from Gold Block + Magma Cream)
        LIFE   // Elixir of Life (from Philosopher's Stone + Base)
    }
}
