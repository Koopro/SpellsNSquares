package at.koopro.spells_n_squares.features.flashlight;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import at.koopro.spells_n_squares.core.registry.ModItems;

/**
 * Client-side handler for flashlight visual light overlay effect.
 * Adds a screen-space light overlay that makes it appear as if light is coming from the flashlight.
 * This works in combination with the light block system for actual world lighting.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class FlashlightShaderHandler {
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        Player player = mc.player;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        boolean hasFlashlight = false;
        boolean isOn = false;
        
        // Check main hand
        if (mainHand.is(ModItems.FLASHLIGHT.get())) {
            hasFlashlight = true;
            isOn = FlashlightItem.isOn(mainHand);
        }
        
        // Check off hand if main hand doesn't have it
        if (!hasFlashlight && offHand.is(ModItems.FLASHLIGHT.get())) {
            hasFlashlight = true;
            isOn = FlashlightItem.isOn(offHand);
        }
        
        if (!hasFlashlight || !isOn) {
            return;
        }
        
        // Disabled overlay - using light blocks for actual lighting instead
        // Uncomment below to enable visual overlay effect
        // renderFlashlightOverlay(event, player);
    }
    
    private static void renderFlashlightOverlay(RenderGuiEvent.Post event, Player player) {
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        // Calculate center of screen (crosshair position)
        float centerX = screenWidth / 2.0f;
        float centerY = screenHeight / 2.0f;
        
        // Create a more realistic flashlight beam effect
        // The beam should be brighter in the center and fade out at edges
        // Make it look like it's coming from the bottom-center (where hands would be)
        
        // Start position - slightly below center (where flashlight would be held)
        float startY = centerY + (screenHeight * 0.15f);
        float startX = centerX;
        
        // End position - center of screen (where looking)
        float endX = centerX;
        float endY = centerY;
        
        // Create a cone/beam shape from start to end
        float beamLength = Math.abs(endY - startY);
        float maxWidth = screenWidth * 0.25f; // Narrower beam
        
        // Draw the beam as a gradient cone
        int segments = 20;
        for (int i = 0; i < segments; i++) {
            float progress = i / (float) segments;
            float nextProgress = (i + 1) / (float) segments;
            
            // Current and next positions along the beam
            float currentY = startY + (endY - startY) * progress;
            float nextY = startY + (endY - startY) * nextProgress;
            
            // Width decreases as we go further (cone shape)
            float currentWidth = maxWidth * (1.0f - progress * 0.7f); // Narrower at the end
            float nextWidth = maxWidth * (1.0f - nextProgress * 0.7f);
            
            // Alpha increases towards the center (brighter in middle)
            int alpha = (int) (20 * (1.0f - Math.abs(progress - 0.5f) * 2.0f));
            if (alpha < 5) alpha = 5;
            if (alpha > 25) alpha = 25;
            
            int color = (alpha << 24) | (255 << 16) | (255 << 8) | 220; // Warm white/yellow
            
            // Draw a horizontal line segment
            int x1 = (int) (startX - currentWidth);
            int x2 = (int) (startX + currentWidth);
            int y = (int) currentY;
            int height = Math.max(1, (int) (nextY - currentY));
            
            guiGraphics.fill(x1, y, x2, y + height, color);
        }
        
        // Add a bright center spot at the end (where the beam hits)
        int centerSpotSize = (int) (maxWidth * 0.15f);
        int centerColor = (30 << 24) | (255 << 16) | (255 << 8) | 240;
        guiGraphics.fill((int) endX - centerSpotSize, (int) endY - centerSpotSize,
                        (int) endX + centerSpotSize, (int) endY + centerSpotSize,
                        centerColor);
    }
}
