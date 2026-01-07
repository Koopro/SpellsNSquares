package at.koopro.spells_n_squares.core.base.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Component for mod block functionality.
 * Provides interaction helpers, placement helpers, and standardized patterns.
 * Uses composition instead of inheritance for flexibility.
 */
public class ModBlockComponent {
    private final Block block;
    private StateCallback stateCallback;
    private PlacementCallback placementCallback;
    private InteractionCallback interactionCallback;
    
    /**
     * Callback for state definition operations.
     */
    public interface StateCallback {
        BlockState createDefaultState(BlockState baseState);
        void addStateProperties(StateDefinition.Builder<Block, BlockState> builder);
    }
    
    /**
     * Callback for placement customization.
     */
    @FunctionalInterface
    public interface PlacementCallback {
        BlockState customizePlacementState(BlockState state, BlockPlaceContext context);
    }
    
    /**
     * Callback for server-side interaction.
     */
    public interface InteractionCallback {
        InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos,
                                         ServerPlayer player, InteractionHand hand,
                                         BlockHitResult hit);
        boolean canPlayerInteract(Player player, BlockPos pos);
    }
    
    public ModBlockComponent(Block block) {
        this.block = block;
    }
    
    /**
     * Sets the callback for state definition operations.
     */
    public void setStateCallback(StateCallback callback) {
        this.stateCallback = callback;
    }
    
    /**
     * Sets the callback for placement customization.
     */
    public void setPlacementCallback(PlacementCallback callback) {
        this.placementCallback = callback;
    }
    
    /**
     * Sets the callback for server-side interaction.
     */
    public void setInteractionCallback(InteractionCallback callback) {
        this.interactionCallback = callback;
    }
    
    /**
     * Creates the default block state.
     * Calls the state callback if set, otherwise returns the base state.
     */
    public BlockState createDefaultState(BlockState baseState) {
        if (stateCallback != null) {
            return stateCallback.createDefaultState(baseState);
        }
        return baseState;
    }
    
    /**
     * Adds custom state properties to the block state definition.
     * Calls the state callback if set.
     */
    public void addStateProperties(StateDefinition.Builder<Block, BlockState> builder) {
        if (stateCallback != null) {
            stateCallback.addStateProperties(builder);
        }
    }
    
    /**
     * Customizes the block state during placement.
     * Calls the placement callback if set, otherwise returns the state unchanged.
     */
    public BlockState customizePlacementState(BlockState state, BlockPlaceContext context) {
        DevLogger.logMethodEntry(block, "customizePlacementState", 
            "pos=" + DevLogger.formatPos(context.getClickedPos()));
        BlockState result = state;
        if (placementCallback != null) {
            result = placementCallback.customizePlacementState(state, context);
        }
        DevLogger.logMethodExit(block, "customizePlacementState", result);
        return result;
    }
    
    /**
     * Handles server-side block interaction.
     * Calls the interaction callback if set, otherwise returns PASS.
     */
    public InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos,
                                             ServerPlayer player, InteractionHand hand,
                                             BlockHitResult hit) {
        DevLogger.logMethodEntry(block, "onServerInteract", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null") +
            ", hand=" + hand);
        InteractionResult result = InteractionResult.PASS;
        if (interactionCallback != null) {
            result = interactionCallback.onServerInteract(state, level, pos, player, hand, hit);
        }
        DevLogger.logMethodExit(block, "onServerInteract", result);
        return result;
    }
    
    /**
     * Checks if a player can interact with this block.
     * Calls the interaction callback if set, otherwise returns true.
     */
    public boolean canPlayerInteract(Player player, BlockPos pos) {
        if (player == null) {
            return false;
        }
        if (interactionCallback != null) {
            return interactionCallback.canPlayerInteract(player, pos);
        }
        // Default: player must be within 8 blocks (64.0 squared distance)
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
}

