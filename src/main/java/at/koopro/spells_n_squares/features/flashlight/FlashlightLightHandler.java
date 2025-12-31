package at.koopro.spells_n_squares.features.flashlight;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.ModItems;
import at.koopro.spells_n_squares.core.util.LightBlockManager;
import at.koopro.spells_n_squares.core.util.LightConstants;
import at.koopro.spells_n_squares.features.flashlight.FlashlightItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side handler for flashlight light emission.
 * Places temporary light blocks when the flashlight is held and on.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class FlashlightLightHandler {
    // Light block manager for this handler instance
    private static final LightBlockManager lightManager = new LightBlockManager();
    private static BlockPos lastPlayerBlockPos = null;
    private static float lastYaw = Float.NaN;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            lightManager.clearAllLights(mc);
            return;
        }
        
        Player player = mc.player;
        
        at.koopro.spells_n_squares.core.util.SafeEventHandler.execute(() -> {
            // Find held flashlight using utility
            var flashlightStackOpt = at.koopro.spells_n_squares.core.util.PlayerItemUtils.findHeldItem(player, FlashlightRegistry.FLASHLIGHT.get());
            boolean hasFlashlight = flashlightStackOpt.isPresent();
            boolean isOn = flashlightStackOpt.map(FlashlightItem::isOn).orElse(false);
            
            BlockPos currentBlockPos = player.blockPosition();
            float currentYaw = player.getYRot();
            
            // Clear lights if flashlight is off, not held, or player moved/turned significantly
            boolean shouldClear = !hasFlashlight || !isOn;
            boolean playerMoved = lastPlayerBlockPos != null && !currentBlockPos.equals(lastPlayerBlockPos);
            boolean playerTurned = !Float.isNaN(lastYaw) && Math.abs(currentYaw - lastYaw) > LightConstants.FLASHLIGHT_TURN_THRESHOLD;
            
            if (shouldClear || playerMoved || playerTurned) {
                if (shouldClear) {
                    lightManager.clearAllLights(mc);
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
        }, "ticking flashlight light", player);
    }
    
    private static void placeLightIfPossible(Minecraft mc, BlockPos playerPos, BlockPos lightPos, Set<BlockPos> newLightPositions) {
        // Don't place lights too close to player
        if (lightPos.distSqr(playerPos) < LightConstants.MIN_DISTANCE_SQR) {
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
            if (!lightManager.hasLightAt(targetPos)) {
                // Place a light block
                if (lightManager.placeLightBlock(mc, targetPos)) {
                    newLightPositions.add(targetPos);
                }
            } else {
                newLightPositions.add(targetPos);
            }
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
        for (int distance = 2; distance <= LightConstants.FLASHLIGHT_LIGHT_RANGE; distance += 1) {
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
        lightManager.removeObsoleteLights(mc, newLightPositions);
    }
}
