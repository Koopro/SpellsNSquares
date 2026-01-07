package at.koopro.spells_n_squares.features.fx.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles visual cut/slice effects when entities take damage.
 * Supports both screen-space cuts (for player) and world-space cuts.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class CutEffectHandler {
    
    // Active cuts on screen/entities
    private static final List<CutEffect> activeCuts = new ArrayList<>();
    
    /**
     * Represents an active cut effect.
     */
    private static class CutEffect {
        Vec3 startPos;      // Start of cut line (screen or world space)
        Vec3 endPos;        // End of cut line
        float intensity;    // Visual intensity (0.0 to 1.0)
        int duration;       // Duration in ticks
        int age;            // Current age in ticks
        int color;          // ARGB color (red for cuts)
        boolean screenSpace; // Whether this is a screen-space cut
        
        CutEffect(Vec3 start, Vec3 end, float intensity, int duration, int color, boolean screenSpace) {
            this.startPos = start;
            this.endPos = end;
            this.intensity = intensity;
            this.duration = duration;
            this.age = 0;
            this.color = color;
            this.screenSpace = screenSpace;
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
        
        float getCurrentAlpha() {
            return getCurrentIntensity();
        }
    }
    
    /**
     * Triggers a cut effect.
     * 
     * @param start Start position of cut line
     * @param end End position of cut line
     * @param intensity Visual intensity (0.0 to 1.0)
     * @param duration Duration in ticks
     * @param color ARGB color
     * @param screenSpace Whether this is a screen-space cut
     */
    public static void triggerCut(Vec3 start, Vec3 end, float intensity, int duration, int color, boolean screenSpace) {
        if (Config.getScreenEffectIntensity() <= 0.0) {
            return;
        }
        
        float adjustedIntensity = (float) (intensity * Config.getScreenEffectIntensity());
        activeCuts.add(new CutEffect(start, end, adjustedIntensity, duration, color, screenSpace));
    }
    
    /**
     * Triggers a cut effect with default red color.
     */
    public static void triggerCut(Vec3 start, Vec3 end, float intensity, int duration, boolean screenSpace) {
        triggerCut(start, end, intensity, duration, 0xFFFF0000, screenSpace); // Red color
    }
    
    /**
     * Hooks into damage events to trigger cut effects.
     */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        SafeEventHandler.execute(() -> {
            if (!event.getEntity().level().isClientSide()) {
                return; // Only handle on client
            }
            
            LivingEntity entity = event.getEntity();
            Minecraft mc = Minecraft.getInstance();
            
            // Only show cuts for the local player or nearby entities
            if (entity != mc.player && (mc.player == null || entity.distanceToSqr(mc.player) > 64.0)) {
                return; // Too far away
            }
            
            // Calculate cut position and direction
            Vec3 entityPos = entity.position().add(0, entity.getEyeHeight() * 0.5, 0);
            Vec3 damageSourcePos = event.getSource().getSourcePosition();
            
            // If damage source position is invalid, use entity position with random direction
            if (damageSourcePos == null || damageSourcePos.equals(Vec3.ZERO)) {
                // Use random direction for unknown source
                double angle = Math.random() * Math.PI * 2;
                damageSourcePos = entityPos.add(
                    Math.cos(angle) * 2.0,
                    (Math.random() - 0.5) * 1.0,
                    Math.sin(angle) * 2.0
                );
            }
            
            Vec3 direction = damageSourcePos.subtract(entityPos);
            double dist = direction.length();
            
            if (dist < 0.1) {
                return; // Too close, skip
            }
            
            direction = direction.normalize();
            
            // Create cut line perpendicular to damage direction
            // Use a cross product to get a perpendicular vector
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 perpendicular = direction.cross(up).normalize();
            if (perpendicular.lengthSqr() < 0.1) {
                // If direction is vertical, use different perpendicular
                perpendicular = direction.cross(new Vec3(1, 0, 0)).normalize();
            }
            
            float cutLength = 0.5f; // Length of cut line
            Vec3 start = entityPos.add(perpendicular.scale(-cutLength));
            Vec3 end = entityPos.add(perpendicular.scale(cutLength));
            
            // Calculate intensity based on damage amount
            float intensity = Math.min(event.getAmount() / 20.0f, 1.0f);
            int duration = 20 + (int)(intensity * 20); // 20-40 ticks
            
            // Determine if this should be screen-space (for player) or world-space
            boolean screenSpace = (entity == mc.player);
            
            triggerCut(start, end, intensity, duration, screenSpace);
        }, "handling living incoming damage", event.getEntity() instanceof Player player ? player : null);
    }
    
    /**
     * Ticks all active cut effects.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        SafeEventHandler.execute(() -> {
            activeCuts.removeIf(cut -> {
                cut.tick();
                return cut.isExpired();
            });
        }, "ticking cut effects");
    }
    
    /**
     * Renders cut effects.
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc != null ? mc.player : null;
        
        SafeEventHandler.execute(() -> {
            if (mc == null || mc.level == null || mc.player == null) {
                return;
            }
            
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();
            
            // Render screen-space cuts
            for (CutEffect cut : activeCuts) {
                if (cut.screenSpace) {
                    renderScreenCut(guiGraphics, width, height, cut);
                }
            }
        }, "rendering cut effects GUI", player);
    }
    
    /**
     * Renders a screen-space cut effect.
     */
    private static void renderScreenCut(GuiGraphics guiGraphics, int width, int height, CutEffect cut) {
        float alpha = cut.getCurrentAlpha();
        if (alpha <= 0.0f) {
            return;
        }
        
        // Convert world positions to screen positions (simplified)
        // For screen-space cuts, positions are already in screen coordinates
        float startX = (float) cut.startPos.x;
        float startY = (float) cut.startPos.y;
        float endX = (float) cut.endPos.x;
        float endY = (float) cut.endPos.y;
        
        // If positions are in world space, we'd need to project them
        // For now, assume they're already screen-space if screenSpace is true
        
        // Extract color components
        int red = (cut.color >> 16) & 0xFF;
        int green = (cut.color >> 8) & 0xFF;
        int blue = cut.color & 0xFF;
        int alphaInt = (int) (alpha * 255) & 0xFF;
        int argb = (alphaInt << 24) | (red << 16) | (green << 8) | blue;
        
        // Render cut as a line with glow effect
        // Simple implementation: draw a thick line
        renderCutLine(guiGraphics, startX, startY, endX, endY, argb, alpha);
    }
    
    /**
     * Renders a cut line with glow effect.
     */
    private static void renderCutLine(GuiGraphics guiGraphics, float x1, float y1, float x2, float y2, int color, float alpha) {
        // Calculate line properties
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (length < 0.1f) {
            return;
        }
        
        // Normalize direction
        dx /= length;
        dy /= length;
        
        // Draw line as a quad
        // For simplicity, draw multiple pixels to create a visible line
        int steps = (int) length;
        for (int i = 0; i < steps; i++) {
            float t = i / (float) steps;
            float x = x1 + dx * length * t;
            float y = y1 + dy * length * t;
            
            // Draw a small square at this point
            int size = 2;
            guiGraphics.fill((int)(x - size), (int)(y - size), (int)(x + size), (int)(y + size), color);
        }
    }
    
    /**
     * Gets all active cuts (for potential world-space rendering).
     */
    public static List<CutEffect> getActiveCuts() {
        return new ArrayList<>(activeCuts);
    }
}












