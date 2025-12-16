package at.koopro.spells_n_squares.features.fx;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side manager for screen effects (shake, overlays, distortion).
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ScreenEffectManager {
    
    // Active screen effects
    private static final List<ScreenShake> activeShakes = new ArrayList<>();
    private static final List<ScreenOverlay> activeOverlays = new ArrayList<>();
    
    /**
     * Represents an active screen shake effect.
     */
    private static class ScreenShake {
        float intensity;
        int duration;
        int age;
        
        ScreenShake(float intensity, int duration) {
            this.intensity = intensity;
            this.duration = duration;
            this.age = 0;
        }
        
        void tick() {
            age++;
        }
        
        boolean isExpired() {
            return age >= duration;
        }
        
        float getCurrentIntensity() {
            // Fade out over time
            float progress = (float) age / duration;
            return intensity * (1.0f - progress);
        }
    }
    
    /**
     * Represents an active screen overlay effect.
     */
    public static class ScreenOverlay {
        int color; // ARGB
        float opacity;
        int duration;
        int age;
        OverlayType type;
        
        public enum OverlayType {
            VIGNETTE, // Damage overlay
            FLASH,    // Brief flash effect
            GLOW      // Subtle glow effect
        }
        
        ScreenOverlay(int color, float opacity, int duration, OverlayType type) {
            this.color = color;
            this.opacity = opacity;
            this.duration = duration;
            this.age = 0;
            this.type = type;
        }
        
        void tick() {
            age++;
        }
        
        boolean isExpired() {
            return age >= duration;
        }
        
        float getCurrentOpacity() {
            float progress = (float) age / duration;
            // Fade out for most overlays
            if (type == OverlayType.FLASH) {
                return opacity * (1.0f - progress);
            }
            return opacity;
        }
    }
    
    /**
     * Triggers a screen shake effect.
     */
    public static void triggerShake(float intensity, int duration) {
        if (Config.getScreenEffectIntensity() <= 0.0) {
            return;
        }
        
        float adjustedIntensity = (float) (intensity * Config.getScreenEffectIntensity());
        activeShakes.add(new ScreenShake(adjustedIntensity, duration));
    }
    
    /**
     * Triggers a screen overlay effect.
     */
    public static void triggerOverlay(int color, float opacity, int duration, ScreenOverlay.OverlayType type) {
        if (Config.getScreenEffectIntensity() <= 0.0) {
            return;
        }
        
        float adjustedOpacity = (float) (opacity * Config.getScreenEffectIntensity());
        activeOverlays.add(new ScreenOverlay(color, adjustedOpacity, duration, type));
    }
    
    /**
     * Triggers a damage vignette overlay.
     */
    public static void triggerDamageVignette() {
        triggerOverlay(0xAA0000, 0.3f, 20, ScreenOverlay.OverlayType.VIGNETTE);
    }
    
    /**
     * Triggers a spell cast flash.
     */
    public static void triggerSpellFlash() {
        triggerOverlay(0xFFFFFF, 0.2f, 5, ScreenOverlay.OverlayType.FLASH);
    }
    
    /**
     * Ticks all active screen effects.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        activeShakes.removeIf(shake -> {
            shake.tick();
            return shake.isExpired();
        });
        
        activeOverlays.removeIf(overlay -> {
            overlay.tick();
            return overlay.isExpired();
        });
    }
    
    /**
     * Renders screen effects.
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        
        // Render overlays
        for (ScreenOverlay overlay : activeOverlays) {
            renderOverlay(guiGraphics, width, height, overlay);
        }
    }
    
    /**
     * Renders a screen overlay.
     */
    private static void renderOverlay(GuiGraphics guiGraphics, int width, int height, ScreenOverlay overlay) {
        float opacity = overlay.getCurrentOpacity();
        if (opacity <= 0.0f) {
            return;
        }
        
        // Extract ARGB components
        int alpha = (int) (opacity * 255) & 0xFF;
        int red = (overlay.color >> 16) & 0xFF;
        int green = (overlay.color >> 8) & 0xFF;
        int blue = overlay.color & 0xFF;
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
        
        switch (overlay.type) {
            case VIGNETTE:
                // Render vignette (darker at edges)
                renderVignette(guiGraphics, width, height, argb);
                break;
            case FLASH:
                // Render full-screen flash
                guiGraphics.fill(0, 0, width, height, argb);
                break;
            case GLOW:
                // Render subtle glow (could be enhanced with shaders)
                guiGraphics.fill(0, 0, width, height, argb);
                break;
        }
    }
    
    /**
     * Renders a vignette effect (darker at edges).
     * Simplified version for performance.
     */
    private static void renderVignette(GuiGraphics guiGraphics, int width, int height, int color) {
        // Simplified vignette: render corners with gradient
        int alpha = (color >> 24) & 0xFF;
        int cornerSize = Math.min(width, height) / 4;
        
        // Top-left corner
        for (int y = 0; y < cornerSize; y++) {
            for (int x = 0; x < cornerSize; x++) {
                double dist = Math.sqrt(x * x + y * y);
                double ratio = Math.min(1.0, dist / cornerSize);
                int pixelAlpha = (int) (alpha * (1.0 - ratio * 0.5));
                int pixelColor = (pixelAlpha << 24) | (color & 0x00FFFFFF);
                if (pixelAlpha > 0) {
                    guiGraphics.fill(x, y, x + 1, y + 1, pixelColor);
                }
            }
        }
        
        // Other corners similarly (simplified for performance)
        // Top-right
        for (int y = 0; y < cornerSize; y++) {
            for (int x = width - cornerSize; x < width; x++) {
                double dist = Math.sqrt((width - x) * (width - x) + y * y);
                double ratio = Math.min(1.0, dist / cornerSize);
                int pixelAlpha = (int) (alpha * (1.0 - ratio * 0.5));
                int pixelColor = (pixelAlpha << 24) | (color & 0x00FFFFFF);
                if (pixelAlpha > 0) {
                    guiGraphics.fill(x, y, x + 1, y + 1, pixelColor);
                }
            }
        }
        
        // Bottom corners (similar pattern)
        for (int y = height - cornerSize; y < height; y++) {
            for (int x = 0; x < cornerSize; x++) {
                double dist = Math.sqrt(x * x + (height - y) * (height - y));
                double ratio = Math.min(1.0, dist / cornerSize);
                int pixelAlpha = (int) (alpha * (1.0 - ratio * 0.5));
                int pixelColor = (pixelAlpha << 24) | (color & 0x00FFFFFF);
                if (pixelAlpha > 0) {
                    guiGraphics.fill(x, y, x + 1, y + 1, pixelColor);
                }
            }
            for (int x = width - cornerSize; x < width; x++) {
                double dist = Math.sqrt((width - x) * (width - x) + (height - y) * (height - y));
                double ratio = Math.min(1.0, dist / cornerSize);
                int pixelAlpha = (int) (alpha * (1.0 - ratio * 0.5));
                int pixelColor = (pixelAlpha << 24) | (color & 0x00FFFFFF);
                if (pixelAlpha > 0) {
                    guiGraphics.fill(x, y, x + 1, y + 1, pixelColor);
                }
            }
        }
    }
    
    /**
     * Gets the current screen shake offset.
     */
    public static Vec3 getShakeOffset() {
        if (activeShakes.isEmpty()) {
            return Vec3.ZERO;
        }
        
        float totalIntensity = 0.0f;
        for (ScreenShake shake : activeShakes) {
            totalIntensity += shake.getCurrentIntensity();
        }
        
        // Apply random offset based on intensity
        java.util.Random random = new java.util.Random();
        double offsetX = (random.nextDouble() - 0.5) * totalIntensity * 2.0;
        double offsetY = (random.nextDouble() - 0.5) * totalIntensity * 2.0;
        
        return new Vec3(offsetX, offsetY, 0.0);
    }
}
