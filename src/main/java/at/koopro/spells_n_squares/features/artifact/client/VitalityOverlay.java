package at.koopro.spells_n_squares.features.artifact.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Renders the Vitality Gauge HUD overlay for immortality state.
 * Shows gold droplet when immortal, cracked grey droplet when withered.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class VitalityOverlay {
    
    private static final Identifier VITALITY_DROPLET = ModIdentifierHelper.modId("textures/gui/vitality_droplet.png");
    private static final Identifier VITALITY_DROPLET_GREY = ModIdentifierHelper.modId("textures/gui/vitality_droplet_grey.png");
    private static final Identifier VITALITY_DROPLET_CRACKS = ModIdentifierHelper.modId("textures/gui/vitality_droplet_cracks.png");
    
    private static final int DROPLET_SIZE = 16;
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int HOTBAR_OFFSET = 4;
    private static final int BOTTOM_MARGIN = 4;
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Don't render HUD when a screen is open (except inventory)
            if (mc.screen != null && !(mc.screen instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen)) {
                return;
            }
            
            // Check if player has ever drunk elixir (permanent curse) OR is currently immortal
            // This ensures the overlay shows even if persistent data isn't synced yet
            boolean hasEverDrunk = at.koopro.spells_n_squares.features.artifact.ImmortalityHelper.hasEverDrunk(mc.player);
            boolean isImmortal = at.koopro.spells_n_squares.features.artifact.ImmortalityHelper.isImmortal(mc.player);
            boolean isWithered = at.koopro.spells_n_squares.features.artifact.ImmortalityHelper.isWithered(mc.player);
            
            if (!hasEverDrunk && !isImmortal && !isWithered) {
                return;
            }
            
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();
            
            // Position: Right side of hotbar
            int hotbarX = (width - HOTBAR_WIDTH) / 2;
            int hotbarY = height - HOTBAR_HEIGHT - BOTTOM_MARGIN;
            int dropletX = hotbarX + HOTBAR_WIDTH + HOTBAR_OFFSET;
            int dropletY = hotbarY + (HOTBAR_HEIGHT - DROPLET_SIZE) / 2;
            
            if (isImmortal) {
                // Render gold droplet (draining)
                int remainingTicks = at.koopro.spells_n_squares.features.artifact.ImmortalityHelper.getRemainingTicks(mc.player);
                renderDroplet(guiGraphics, VITALITY_DROPLET, dropletX, dropletY, remainingTicks, 72000);
            } else if (isWithered) {
                // Render grey droplet with crack overlay (pulsing)
                renderCrackedDroplet(guiGraphics, dropletX, dropletY);
            }
        }, "rendering vitality overlay", mc.player);
    }
    
    /**
     * Renders the gold droplet with draining animation based on remaining time.
     */
    private static void renderDroplet(GuiGraphics guiGraphics, Identifier texture, int x, int y, int ticksRemaining, int maxTicks) {
        float progress = (float) ticksRemaining / (float) maxTicks;
        int cropHeight = (int) (DROPLET_SIZE * progress);
        
        // Render full droplet
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            x, y,
            0.0f, 0.0f,
            DROPLET_SIZE, DROPLET_SIZE,
            DROPLET_SIZE, DROPLET_SIZE,
            DROPLET_SIZE, DROPLET_SIZE,
            -1
        );
        
        // Crop from bottom to show draining effect
        if (cropHeight < DROPLET_SIZE) {
            int cropY = y + cropHeight;
            int cropHeightPixels = DROPLET_SIZE - cropHeight;
            // Draw dark overlay for drained portion
            guiGraphics.fill(x, cropY, x + DROPLET_SIZE, cropY + cropHeightPixels, 0x80000000);
        }
    }
    
    /**
     * Renders the grey droplet with crack overlay and pulsing red warning effect.
     */
    private static void renderCrackedDroplet(GuiGraphics guiGraphics, int x, int y) {
        // Calculate pulsing alpha (0.5 to 1.0)
        float time = (float) Minecraft.getInstance().level.getGameTime();
        float pulse = (Mth.sin(time * 0.2f) + 1.0f) * 0.25f + 0.5f; // 0.5 to 1.0
        int alpha = (int) (pulse * 255);
        
        // Render the grey droplet texture (should be a separate texture that matches the droplet shape)
        int color = (alpha << 24) | 0xFFFFFF; // White with alpha for pulsing
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            VITALITY_DROPLET_GREY,
            x, y,
            0.0f, 0.0f,
            DROPLET_SIZE, DROPLET_SIZE,
            DROPLET_SIZE, DROPLET_SIZE,
            DROPLET_SIZE, DROPLET_SIZE,
            color
        );
        
        // Then overlay the cracks texture
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            VITALITY_DROPLET_CRACKS,
            x, y,
            0.0f, 0.0f,
            DROPLET_SIZE, DROPLET_SIZE,
            DROPLET_SIZE, DROPLET_SIZE,
            DROPLET_SIZE, DROPLET_SIZE,
            -1
        );
        
        // Add red warning overlay (pulsing)
        int redAlpha = (int) (pulse * 0.3f * 255);
        int redColor = (redAlpha << 24) | 0xFF0000; // Red with alpha
        guiGraphics.fill(x, y, x + DROPLET_SIZE, y + DROPLET_SIZE, redColor);
    }
    
}

