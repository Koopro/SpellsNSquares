package at.koopro.spells_n_squares.features.spell.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.spell.LumosManager;

/**
 * Client-side handler for Lumos spell dynamic light emission.
 * Uses Light Blocks (invisible, non-collidable) to create dynamic lighting that follows the player.
 * This is the standard approach for dynamic lighting in Minecraft mods.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class LumosLightHandler {
    // Store original blocks that we've replaced with light blocks
    private static final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    // Track which positions currently have our light blocks
    private static final Set<BlockPos> activeLightPositions = new HashSet<>();
    private static BlockPos lastPlayerBlockPos = null;
    private static final int LIGHT_RANGE = 8; // How far the light reaches from player
    private static final int LIGHT_LEVEL = 15; // Maximum light level
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            clearAllLights(mc);
            return;
        }
        
        Player player = mc.player;
        boolean isLumosActive = LumosManager.isLumosActive(player);
        
        BlockPos currentBlockPos = player.blockPosition();
        
        // Clear lights if lumos is inactive
        if (!isLumosActive) {
            clearAllLights(mc);
            lastPlayerBlockPos = currentBlockPos;
            return;
        }
        
        // Update lights when player moves or every few ticks to maintain dynamic lighting
        boolean playerMoved = lastPlayerBlockPos != null && !currentBlockPos.equals(lastPlayerBlockPos);
        
        if (playerMoved || (isLumosActive && mc.level.getGameTime() % 5 == 0)) {
            updateLights(mc, player);
        }
        
        lastPlayerBlockPos = currentBlockPos;
    }
    
    private static void updateLights(Minecraft mc, Player player) {
        if (mc.level == null) {
            return;
        }
        
        BlockPos playerBlockPos = player.blockPosition();
        
        // Calculate positions for light blocks around the player
        Set<BlockPos> newLightPositions = new HashSet<>();
        
        // Add a primary light source at the player's position (slightly above)
        BlockPos primaryLightPos = playerBlockPos.above();
        if (canPlaceLight(mc, primaryLightPos)) {
            placeLightBlock(mc, primaryLightPos);
            newLightPositions.add(primaryLightPos);
        }
        
        // Add additional light sources in a sphere around the player
        // Use a more efficient spacing to reduce the number of light blocks
        for (int x = -LIGHT_RANGE; x <= LIGHT_RANGE; x += 2) {
            for (int y = -LIGHT_RANGE; y <= LIGHT_RANGE; y += 2) {
                for (int z = -LIGHT_RANGE; z <= LIGHT_RANGE; z += 2) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    // Only place lights within range and not too close
                    if (distance <= LIGHT_RANGE && distance > 1.5) {
                        BlockPos lightPos = playerBlockPos.offset(x, y, z);
                        if (canPlaceLight(mc, lightPos)) {
                            // Calculate light level based on distance (fade out)
                            int lightLevel = (int) (LIGHT_LEVEL * (1.0 - (distance / LIGHT_RANGE)));
                            if (lightLevel > 0) {
                                placeLightBlock(mc, lightPos, lightLevel);
                                newLightPositions.add(lightPos);
                            }
                        }
                    }
                }
            }
        }
        
        // Remove lights that are no longer needed
        activeLightPositions.removeIf(pos -> {
            if (!newLightPositions.contains(pos)) {
                restoreBlock(mc, pos);
                return true;
            }
            return false;
        });
    }
    
    /**
     * Checks if a light block can be placed at the given position.
     * @param mc The Minecraft instance
     * @param pos The position to check
     * @return true if a light block can be placed
     */
    private static boolean canPlaceLight(Minecraft mc, BlockPos pos) {
        if (mc.level == null) {
            return false;
        }
        
        BlockState currentState = mc.level.getBlockState(pos);
        // Only place lights in air or replaceable blocks
        return currentState.isAir() || currentState.canBeReplaced();
    }
    
    /**
     * Places a light block at the specified position with maximum light level.
     * @param mc The Minecraft instance
     * @param pos The position to place the light
     */
    private static void placeLightBlock(Minecraft mc, BlockPos pos) {
        placeLightBlock(mc, pos, LIGHT_LEVEL);
    }
    
    /**
     * Places a light block at the specified position with the given light level.
     * @param mc The Minecraft instance
     * @param pos The position to place the light
     * @param lightLevel The light level (0-15)
     */
    private static void placeLightBlock(Minecraft mc, BlockPos pos, int lightLevel) {
        if (mc.level == null) {
            return;
        }
        
        // Check if we already have a light here
        if (activeLightPositions.contains(pos)) {
            return;
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
        }
    }
    
    /**
     * Gets a Light block state with the specified light level.
     * @param lightLevel The light level (0-15)
     * @return The block state, or null if Light blocks are not available
     */
    private static BlockState getLightBlockState(int lightLevel) {
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
     * Restores the original block at the specified position.
     * @param mc The Minecraft instance
     * @param pos The position to restore
     */
    private static void restoreBlock(Minecraft mc, BlockPos pos) {
        if (mc.level == null) {
            return;
        }
        
        BlockState originalState = originalBlocks.remove(pos);
        if (originalState != null) {
            BlockState currentState = mc.level.getBlockState(pos);
            // Only restore if it's still our light block
            boolean isOurLight = currentState.is(Blocks.LIGHT) || currentState.is(Blocks.TORCH);
            if (isOurLight) {
                mc.level.setBlock(pos, originalState, 3);
            }
        }
    }
    
    /**
     * Clears all dynamic lights.
     * @param mc The Minecraft instance
     */
    private static void clearAllLights(Minecraft mc) {
        if (mc.level == null) {
            originalBlocks.clear();
            activeLightPositions.clear();
            lastPlayerBlockPos = null;
            return;
        }
        
        // Restore all original blocks
        for (BlockPos pos : new HashSet<>(activeLightPositions)) {
            restoreBlock(mc, pos);
        }
        
        originalBlocks.clear();
        activeLightPositions.clear();
        lastPlayerBlockPos = null;
    }
}
