package at.koopro.spells_n_squares.features.artifact.block;

import at.koopro.spells_n_squares.features.artifact.ArtifactRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Handles interactions with Elixir Cauldron blocks (bottles, bowls, etc.)
 */
public class ElixirCauldronInteraction {
    
    /**
     * Handles right-click interaction with an elixir cauldron.
     * Allows collecting elixir with bottles or bowls.
     */
    public static InteractionResult interact(BlockState state, Level level, BlockPos pos, 
                                            Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        ItemStack heldItem = player.getItemInHand(hand);
        int levelValue = state.getValue(ElixirCauldronBlock.LEVEL);
        
        if (levelValue <= 0) {
            return InteractionResult.PASS;
        }
        
        // Check if player is holding a bottle or bowl
        if (heldItem.is(Items.GLASS_BOTTLE)) {
            return handleBottleCollection(state, level, pos, player, hand, heldItem);
        } else if (heldItem.is(Items.BOWL)) {
            return handleBowlCollection(state, level, pos, player, hand, heldItem);
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * Handles collecting elixir with a glass bottle.
     */
    private static InteractionResult handleBottleCollection(BlockState state, Level level, BlockPos pos,
                                                           Player player, InteractionHand hand, ItemStack bottle) {
        ElixirCauldronBlock.ElixirType type = ((ElixirCauldronBlock) state.getBlock()).getElixirType();
        int levelValue = state.getValue(ElixirCauldronBlock.LEVEL);
        
        // Only Elixir of Life can be collected in bottles
        if (type != ElixirCauldronBlock.ElixirType.LIFE) {
            return InteractionResult.PASS;
        }
        
        // Consume bottle
        if (!player.getAbilities().instabuild) {
            bottle.shrink(1);
        }
        
        // Give elixir item
        ItemStack elixir = new ItemStack(ArtifactRegistry.ELIXIR_OF_LIFE.get(), 1);
        if (bottle.isEmpty()) {
            player.setItemInHand(hand, elixir);
        } else {
            if (!player.getInventory().add(elixir)) {
                player.drop(elixir, false);
            }
        }
        
        // Reduce cauldron level
        int newLevel = levelValue - 1;
        if (newLevel <= 0) {
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.CAULDRON.defaultBlockState(), 3);
        } else {
            level.setBlock(pos, state.setValue(ElixirCauldronBlock.LEVEL, newLevel), 3);
        }
        
        // Sound effect
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles collecting elixir with a bowl (for drinking directly).
     */
    private static InteractionResult handleBowlCollection(BlockState state, Level level, BlockPos pos,
                                                         Player player, InteractionHand hand, ItemStack bowl) {
        ElixirCauldronBlock.ElixirType type = ((ElixirCauldronBlock) state.getBlock()).getElixirType();
        int levelValue = state.getValue(ElixirCauldronBlock.LEVEL);
        
        // Both types can be consumed from bowls
        if (type == ElixirCauldronBlock.ElixirType.BASE) {
            // Elixir Base - not consumable, just for collection
            return InteractionResult.PASS;
        }
        
        // Elixir of Life - consume directly
        // Give player the elixir effect
        if (level instanceof net.minecraft.server.level.ServerLevel) {
            // Grant immortality (same as drinking elixir item)
            at.koopro.spells_n_squares.features.artifact.ImmortalityHelper.grantImmortality(player);
        }
        
        // Reduce cauldron level
        int newLevel = levelValue - 1;
        if (newLevel <= 0) {
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.CAULDRON.defaultBlockState(), 3);
        } else {
            level.setBlock(pos, state.setValue(ElixirCauldronBlock.LEVEL, newLevel), 3);
        }
        
        // Sound effect
        level.playSound(null, pos, SoundEvents.GENERIC_DRINK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        
        return InteractionResult.SUCCESS;
    }
}

