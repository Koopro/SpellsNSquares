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
import net.neoforged.neoforge.client.event.ViewportEvent;

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
        
        // Apply screen shake to GUI
        // Note: GuiGraphics.pose() returns Matrix3x2fStack which doesn't support push/pop
        // Instead, we'll apply shake by offsetting overlay rendering positions
        Vec3 shake = getShakeOffset();
        
        // Render overlays (shake will be applied per-overlay if needed)
        for (ScreenOverlay overlay : activeOverlays) {
            renderOverlay(guiGraphics, width, height, overlay, shake);
        }
    }
    
    /**
     * Renders a screen overlay.
     */
    private static void renderOverlay(GuiGraphics guiGraphics, int width, int height, ScreenOverlay overlay, Vec3 shake) {
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
        
        // Apply shake offset to rendering positions
        int shakeX = (int) Math.round(shake.x);
        int shakeY = (int) Math.round(shake.y);
        
        switch (overlay.type) {
            case VIGNETTE:
                // Render vignette (darker at edges) - shake applied via offset
                renderVignette(guiGraphics, width, height, argb, shakeX, shakeY);
                break;
            case FLASH:
                // Render full-screen flash with shake offset
                guiGraphics.fill(shakeX, shakeY, width + shakeX, height + shakeY, argb);
                break;
            case GLOW:
                // Render subtle glow (could be enhanced with shaders) with shake offset
                guiGraphics.fill(shakeX, shakeY, width + shakeX, height + shakeY, argb);
                break;
        }
    }
    
    /**
     * Renders a vignette effect (darker at edges).
     * Simplified version for performance.
     */
    private static void renderVignette(GuiGraphics guiGraphics, int width, int height, int color, int shakeX, int shakeY) {
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
                    guiGraphics.fill(x + shakeX, y + shakeY, x + 1 + shakeX, y + 1 + shakeY, pixelColor);
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
                    guiGraphics.fill(x + shakeX, y + shakeY, x + 1 + shakeX, y + 1 + shakeY, pixelColor);
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
                    guiGraphics.fill(x + shakeX, y + shakeY, x + 1 + shakeX, y + 1 + shakeY, pixelColor);
                }
            }
            for (int x = width - cornerSize; x < width; x++) {
                double dist = Math.sqrt((width - x) * (width - x) + (height - y) * (height - y));
                double ratio = Math.min(1.0, dist / cornerSize);
                int pixelAlpha = (int) (alpha * (1.0 - ratio * 0.5));
                int pixelColor = (pixelAlpha << 24) | (color & 0x00FFFFFF);
                if (pixelAlpha > 0) {
                    guiGraphics.fill(x + shakeX, y + shakeY, x + 1 + shakeX, y + 1 + shakeY, pixelColor);
                }
            }
        }
    }
    
    // Static variables for smooth shake interpolation
    private static float shakeTime = 0.0f;
    private static float previousShakeX = 0.0f;
    private static float previousShakeY = 0.0f;
    
    /**
     * Gets the current screen shake offset using smooth time-based motion.
     * Uses sine/cosine waves with interpolation for smooth, non-jittery shake.
     */
    public static Vec3 getShakeOffset() {
        if (activeShakes.isEmpty()) {
            previousShakeX = 0.0f;
            previousShakeY = 0.0f;
            return Vec3.ZERO;
        }
        
        float totalIntensity = 0.0f;
        for (ScreenShake shake : activeShakes) {
            totalIntensity += shake.getCurrentIntensity();
        }
        
        // Get game time for consistent shake motion
        Minecraft mc = Minecraft.getInstance();
        float time = mc.level != null ? (float) mc.level.getGameTime() : shakeTime;
        shakeTime = time;
        
        // Use time-based sine/cosine waves for smooth motion
        // Different frequencies for X and Y to avoid circular motion
        // Use multiple frequencies combined for more natural motion
        float timeX = time * 0.3f;
        float timeY = time * 0.5f;
        
        // Combine multiple frequencies for more natural shake
        double offsetX = (Math.sin(timeX * 1.0) * 0.6 + Math.sin(timeX * 2.3) * 0.4) * totalIntensity * 10.0;
        double offsetY = (Math.cos(timeY * 1.0) * 0.6 + Math.cos(timeY * 1.7) * 0.4) * totalIntensity * 10.0;
        
        // Interpolate with previous frame for smoother motion
        float currentX = (float) offsetX;
        float currentY = (float) offsetY;
        float interpolatedX = previousShakeX + (currentX - previousShakeX) * 0.3f;
        float interpolatedY = previousShakeY + (currentY - previousShakeY) * 0.3f;
        
        previousShakeX = currentX;
        previousShakeY = currentY;
        
        return new Vec3(interpolatedX, interpolatedY, 0.0);
    }
    
    /**
     * Applies screen shake to the camera/view.
     * This affects the world rendering, not just GUI.
     */
    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Vec3 shake = getShakeOffset();
        if (shake.lengthSqr() < 0.001) {
            return;
        }
        
        // Apply shake as camera rotation offset
        // Convert pixel offset to angle offset (small multiplier for subtle effect)
        float shakeScale = 0.1f; // Adjust this to control shake intensity
        event.setYaw(event.getYaw() + (float)shake.x * shakeScale);
        event.setPitch(event.getPitch() + (float)shake.y * shakeScale);
    }
}
