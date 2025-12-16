package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.LightBlockManager;
import at.koopro.spells_n_squares.core.util.LightConstants;
import at.koopro.spells_n_squares.features.spell.LumosManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side handler for Lumos spell dynamic light emission.
 * Uses Light Blocks (invisible, non-collidable) to create dynamic lighting that follows the player.
 * This is the standard approach for dynamic lighting in Minecraft mods.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class LumosLightHandler {
    // Light block manager for this handler instance
    private static final LightBlockManager lightManager = new LightBlockManager();
    private static BlockPos lastPlayerBlockPos = null;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            lightManager.clearAllLights(mc);
            return;
        }
        
        Player player = mc.player;
        boolean isLumosActive = LumosManager.isLumosActive(player);
        
        BlockPos currentBlockPos = player.blockPosition();
        
        // Clear lights if lumos is inactive
        if (!isLumosActive) {
            lightManager.clearAllLights(mc);
            lastPlayerBlockPos = currentBlockPos;
            return;
        }
        
        // Update lights when player moves or every few ticks to maintain dynamic lighting
        boolean playerMoved = lastPlayerBlockPos != null && !currentBlockPos.equals(lastPlayerBlockPos);
        
        if (playerMoved || (isLumosActive && mc.level.getGameTime() % LightConstants.LUMOS_UPDATE_INTERVAL == 0)) {
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
        if (lightManager.canPlaceLight(mc, primaryLightPos)) {
            // Try to place, but also include if already exists
            if (lightManager.placeLightBlock(mc, primaryLightPos) || lightManager.hasLightAt(primaryLightPos)) {
                newLightPositions.add(primaryLightPos);
            }
        }
        
        // Add additional light sources in a sphere around the player
        // Use a more efficient spacing to reduce the number of light blocks
        for (int x = -LightConstants.LUMOS_LIGHT_RANGE; x <= LightConstants.LUMOS_LIGHT_RANGE; x += 2) {
            for (int y = -LightConstants.LUMOS_LIGHT_RANGE; y <= LightConstants.LUMOS_LIGHT_RANGE; y += 2) {
                for (int z = -LightConstants.LUMOS_LIGHT_RANGE; z <= LightConstants.LUMOS_LIGHT_RANGE; z += 2) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    // Only place lights within range and not too close
                    if (distance <= LightConstants.LUMOS_LIGHT_RANGE && distance > LightConstants.MIN_DISTANCE_FROM_PLAYER) {
                        BlockPos lightPos = playerBlockPos.offset(x, y, z);
                        if (lightManager.canPlaceLight(mc, lightPos)) {
                            // Calculate light level based on distance (fade out)
                            int lightLevel = (int) (LightConstants.MAX_LIGHT_LEVEL * (1.0 - (distance / LightConstants.LUMOS_LIGHT_RANGE)));
                            if (lightLevel > 0) {
                                // Try to place, but also include if already exists
                                if (lightManager.placeLightBlock(mc, lightPos, lightLevel) || lightManager.hasLightAt(lightPos)) {
                                    newLightPositions.add(lightPos);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Remove lights that are no longer needed
        lightManager.removeObsoleteLights(mc, newLightPositions);
    }
    
}
