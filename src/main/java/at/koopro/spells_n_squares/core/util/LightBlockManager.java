package at.koopro.spells_n_squares.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for managing dynamic light blocks.
 * Handles placement, restoration, and tracking of temporary light blocks.
 * Each instance maintains its own state for independent light management.
 */
public final class LightBlockManager {
    // Store original blocks that we've replaced with light blocks
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    // Track which positions currently have our light blocks
    // Using HashSet - order doesn't matter, O(1) contains check needed
    private final Set<BlockPos> activeLightPositions = new HashSet<>();
    
    /**
     * Checks if a light block can be placed at the given position.
     * @param mc The Minecraft instance
     * @param pos The position to check
     * @return true if a light block can be placed
     */
    public boolean canPlaceLight(Minecraft mc, BlockPos pos) {
        if (mc.level == null) {
            return false;
        }
        
        BlockState currentState = mc.level.getBlockState(pos);
        // Only place lights in air or replaceable blocks
        return currentState.isAir() || currentState.canBeReplaced();
    }
    
    /**
     * Places a light block at the specified position with the given light level.
     * @param mc The Minecraft instance
     * @param pos The position to place the light
     * @param lightLevel The light level (0-15)
     * @return true if the light was placed successfully
     */
    public boolean placeLightBlock(Minecraft mc, BlockPos pos, int lightLevel) {
        if (mc.level == null) {
            return false;
        }
        
        // Check if we already have a light here
        if (activeLightPositions.contains(pos)) {
            return false;
        }
        
        BlockState currentState = mc.level.getBlockState(pos);
        
        // Store original block state if we haven't already
        if (!originalBlocks.containsKey(pos)) {
            originalBlocks.put(pos, currentState);
        }
        
        // Place a Light block (invisible, non-collidable)
        BlockState lightBlock = getLightBlockState(lightLevel);
        if (lightBlock != null) {
            mc.level.setBlock(pos, lightBlock, 3);
            activeLightPositions.add(pos);
            return true;
        }
        
        return false;
    }
    
    /**
     * Places a light block at the specified position with maximum light level.
     * @param mc The Minecraft instance
     * @param pos The position to place the light
     * @return true if the light was placed successfully
     */
    public boolean placeLightBlock(Minecraft mc, BlockPos pos) {
        return placeLightBlock(mc, pos, LightConstants.MAX_LIGHT_LEVEL);
    }
    
    /**
     * Restores the original block at the specified position.
     * @param mc The Minecraft instance
     * @param pos The position to restore
     * @return true if a block was restored
     */
    public boolean restoreBlock(Minecraft mc, BlockPos pos) {
        if (mc.level == null) {
            return false;
        }
        
        BlockState originalState = originalBlocks.remove(pos);
        if (originalState != null) {
            BlockState currentState = mc.level.getBlockState(pos);
            // Only restore if it's still our light block
            boolean isOurLight = currentState.is(Blocks.LIGHT) || currentState.is(Blocks.TORCH);
            if (isOurLight) {
                mc.level.setBlock(pos, originalState, 3);
                activeLightPositions.remove(pos);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Clears all dynamic lights managed by this instance.
     * @param mc The Minecraft instance
     */
    public void clearAllLights(Minecraft mc) {
        if (mc.level == null) {
            originalBlocks.clear();
            activeLightPositions.clear();
            return;
        }
        
        // Restore all original blocks
        for (BlockPos pos : new HashSet<>(activeLightPositions)) {
            restoreBlock(mc, pos);
        }
        
        originalBlocks.clear();
        activeLightPositions.clear();
    }
    
    /**
     * Gets a Light block state with the specified light level.
     * @param lightLevel The light level (0-15)
     * @return The block state, or null if Light blocks are not available
     */
    public static BlockState getLightBlockState(int lightLevel) {
        try {
            BlockState lightState = Blocks.LIGHT.defaultBlockState();
            // Set the light level property if it exists
            if (lightState.hasProperty(LightBlock.LEVEL)) {
                return lightState.setValue(LightBlock.LEVEL, Math.max(0, Math.min(15, lightLevel)));
            }
            return lightState;
        } catch (Exception e) {
            // Fallback to torch if Light block is not available
            return Blocks.TORCH.defaultBlockState();
        }
    }
    
    /**
     * Gets the set of active light positions.
     * @return A copy of the active light positions set
     */
    public Set<BlockPos> getActiveLightPositions() {
        return new HashSet<>(activeLightPositions);
    }
    
    /**
     * Checks if a position has an active light.
     * @param pos The position to check
     * @return true if there's an active light at this position
     */
    public boolean hasLightAt(BlockPos pos) {
        return activeLightPositions.contains(pos);
    }
    
    /**
     * Removes lights that are no longer in the provided set of positions.
     * @param mc The Minecraft instance
     * @param newLightPositions The set of positions that should have lights
     */
    public void removeObsoleteLights(Minecraft mc, Set<BlockPos> newLightPositions) {
        // Create a copy to avoid ConcurrentModificationException
        Set<BlockPos> positionsToRemove = new HashSet<>();
        for (BlockPos pos : activeLightPositions) {
            if (!newLightPositions.contains(pos)) {
                positionsToRemove.add(pos);
            }
        }
        
        // Restore blocks and remove from active set
        for (BlockPos pos : positionsToRemove) {
            restoreBlock(mc, pos);
        }
    }
}









