package at.koopro.spells_n_squares.features.flashlight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import at.koopro.spells_n_squares.core.registry.ModItems;

/**
 * Client-side handler for flashlight light emission.
 * Places temporary light blocks when the flashlight is held and on.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class FlashlightLightHandler {
    // Store original blocks that we've replaced with lights
    private static final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    // Track which positions currently have our lights
    private static final Set<BlockPos> activeLightPositions = new HashSet<>();
    private static BlockPos lastPlayerBlockPos = null;
    private static float lastYaw = Float.NaN;
    private static final int LIGHT_RANGE = 10; // How far the light reaches
    private static final int LIGHT_LEVEL = 15; // Maximum light level
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            clearAllLights(mc);
            return;
        }
        
        Player player = mc.player;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        boolean hasFlashlight = false;
        boolean isOn = false;
        ItemStack flashlightStack = null;
        
        // Check main hand
        if (mainHand.is(ModItems.FLASHLIGHT.get())) {
            hasFlashlight = true;
            isOn = FlashlightItem.isOn(mainHand);
            flashlightStack = mainHand;
        }
        
        // Check off hand if main hand doesn't have it
        if (!hasFlashlight && offHand.is(ModItems.FLASHLIGHT.get())) {
            hasFlashlight = true;
            isOn = FlashlightItem.isOn(offHand);
            flashlightStack = offHand;
        }
        
        BlockPos currentBlockPos = player.blockPosition();
        float currentYaw = player.getYRot();
        
        // Clear lights if flashlight is off, not held, or player moved/turned significantly
        boolean shouldClear = !hasFlashlight || !isOn;
        boolean playerMoved = lastPlayerBlockPos != null && !currentBlockPos.equals(lastPlayerBlockPos);
        boolean playerTurned = !Float.isNaN(lastYaw) && Math.abs(currentYaw - lastYaw) > 5.0f;
        
        if (shouldClear || playerMoved || playerTurned) {
            if (shouldClear) {
                clearAllLights(mc);
            } else {
                // Update lights when player moves or turns
                updateLights(mc, player);
            }
        } else if (hasFlashlight && isOn) {
            // Update lights if nothing changed but we should maintain them
            updateLights(mc, player);
        }
        
        lastPlayerBlockPos = currentBlockPos;
        lastYaw = currentYaw;
    }
    
    private static void placeLightIfPossible(Minecraft mc, BlockPos playerPos, BlockPos lightPos, Set<BlockPos> newLightPositions) {
        // Don't place lights too close to player
        if (lightPos.distSqr(playerPos) < 2) {
            return;
        }
        
        BlockState currentState = mc.level.getBlockState(lightPos);
        BlockPos targetPos = lightPos;
        
        // If the target position is a solid block, try placing light on top of it or in the air above it
        if (!currentState.isAir() && !currentState.canBeReplaced()) {
            // Try placing one block above the solid block
            BlockPos abovePos = lightPos.above();
            BlockState aboveState = mc.level.getBlockState(abovePos);
            
            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                targetPos = abovePos;
                currentState = aboveState;
            } else {
                // If above is also solid, try placing slightly higher (for looking at floors)
                BlockPos higherPos = lightPos.above(2);
                BlockState higherState = mc.level.getBlockState(higherPos);
                if (higherState.isAir() || higherState.canBeReplaced()) {
                    targetPos = higherPos;
                    currentState = higherState;
                } else {
                    // Can't place here, skip
                    return;
                }
            }
        }
        
        // Only place lights in air or replaceable blocks
        if (currentState.isAir() || currentState.canBeReplaced()) {
            // Check if we already have a light here
            if (!activeLightPositions.contains(targetPos)) {
                // Store original block state
                originalBlocks.put(targetPos, currentState);
                // Place a light block
                BlockState lightBlock = getLightBlock(mc);
                if (lightBlock != null) {
                    mc.level.setBlock(targetPos, lightBlock, 3);
                    activeLightPositions.add(targetPos);
                }
            }
            newLightPositions.add(targetPos);
        }
    }
    
    private static void updateLights(Minecraft mc, Player player) {
        if (mc.level == null) {
            return;
        }
        
        // Calculate the direction the player is looking
        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.position();
        BlockPos playerBlockPos = player.blockPosition();
        
        // Calculate positions for light blocks in a cone in front of the player
        Set<BlockPos> newLightPositions = new HashSet<>();
        
        // Get player's pitch to handle downward/upward angles
        float pitch = player.getXRot();
        boolean lookingDown = pitch > 20.0f; // Looking down more than 20 degrees
        
        // Place lights in a cone pattern in front of the player
        // Use a more efficient approach: place lights along the look direction with a cone spread
        for (int distance = 2; distance <= LIGHT_RANGE; distance += 1) {
            Vec3 targetPos = playerPos.add(lookVec.scale(distance));
            BlockPos targetBlockPos = BlockPos.containing(targetPos);
            
            // When looking down, also check the block directly below the target
            if (lookingDown) {
                BlockPos belowPos = targetBlockPos.below();
                placeLightIfPossible(mc, playerBlockPos, belowPos, newLightPositions);
            }
            
            // Calculate beam width (wider at base, narrower at tip)
            int beamWidth = Math.max(1, (int)(2.5 - distance * 0.2));
            
            // Place lights in a cross pattern for efficiency (fewer blocks)
            // Center light
            placeLightIfPossible(mc, playerBlockPos, targetBlockPos, newLightPositions);
            
            // Place lights in a small cross pattern around the center
            for (int offset = 1; offset <= beamWidth; offset++) {
                // Horizontal spread
                placeLightIfPossible(mc, playerBlockPos, targetBlockPos.offset(offset, 0, 0), newLightPositions);
                placeLightIfPossible(mc, playerBlockPos, targetBlockPos.offset(-offset, 0, 0), newLightPositions);
                placeLightIfPossible(mc, playerBlockPos, targetBlockPos.offset(0, 0, offset), newLightPositions);
                placeLightIfPossible(mc, playerBlockPos, targetBlockPos.offset(0, 0, -offset), newLightPositions);
                
                // Vertical spread (less when looking down to avoid placing lights too high)
                if (!lookingDown || offset <= 1) {
                    placeLightIfPossible(mc, playerBlockPos, targetBlockPos.offset(0, offset, 0), newLightPositions);
                }
                placeLightIfPossible(mc, playerBlockPos, targetBlockPos.offset(0, -offset, 0), newLightPositions);
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
    
    private static BlockState getLightBlock(Minecraft mc) {
        // Try to use Light block first (available in 1.17+)
        try {
            // Light block with maximum light level (15)
            // The Light block uses a property to set the light level
            BlockState lightState = Blocks.LIGHT.defaultBlockState();
            // Try to set the light level property if it exists
            if (lightState.hasProperty(net.minecraft.world.level.block.LightBlock.LEVEL)) {
                return lightState.setValue(net.minecraft.world.level.block.LightBlock.LEVEL, LIGHT_LEVEL);
            }
            return lightState;
        } catch (Exception e) {
            // Fallback to torch if Light block is not available or has issues
            return Blocks.TORCH.defaultBlockState();
        }
    }
    
    private static void restoreBlock(Minecraft mc, BlockPos pos) {
        if (mc.level == null) {
            return;
        }
        
        BlockState originalState = originalBlocks.remove(pos);
        if (originalState != null) {
            BlockState currentState = mc.level.getBlockState(pos);
            // Only restore if it's still our light block
            // Check for Light block or Torch (our fallback)
            boolean isOurLight = currentState.is(Blocks.LIGHT) || currentState.is(Blocks.TORCH);
            if (isOurLight) {
                mc.level.setBlock(pos, originalState, 3);
            }
        }
    }
    
    private static void clearAllLights(Minecraft mc) {
        if (mc.level == null) {
            originalBlocks.clear();
            activeLightPositions.clear();
            lastPlayerBlockPos = null;
            lastYaw = Float.NaN;
            return;
        }
        
        // Restore all original blocks
        for (BlockPos pos : new HashSet<>(activeLightPositions)) {
            restoreBlock(mc, pos);
        }
        
        originalBlocks.clear();
        activeLightPositions.clear();
        lastPlayerBlockPos = null;
        lastYaw = Float.NaN;
    }
}
