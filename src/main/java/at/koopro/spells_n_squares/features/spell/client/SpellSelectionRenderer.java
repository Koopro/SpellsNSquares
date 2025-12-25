package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.Spell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Helper class for rendering SpellSelectionScreen elements.
 */
public final class SpellSelectionRenderer {
    private SpellSelectionRenderer() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Renders the screen background and border.
     */
    public static void renderBackground(GuiGraphics guiGraphics, int width, int height) {
        // Render gradient background
        guiGraphics.fill(0, 0, width, height, SpellUIConstants.BG_COLOR_SCREEN);
        
        // Draw subtle border around screen
        int borderColor = 0xFF404040;
        guiGraphics.fill(0, 0, width, 1, borderColor); // Top
        guiGraphics.fill(0, height - 1, width, height, borderColor); // Bottom
        guiGraphics.fill(0, 0, 1, height, borderColor); // Left
        guiGraphics.fill(width - 1, 0, width, height, borderColor); // Right
    }
    
    /**
     * Renders the title with background.
     */
    public static void renderTitle(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, 
                                   net.minecraft.network.chat.Component titleText, int width) {
        int titleWidth = font.width(titleText);
        int titleX = width / 2 - titleWidth / 2;
        int titleY = SpellSelectionScreenConstants.TITLE_Y;
        
        // Draw title background
        guiGraphics.fill(
            titleX - SpellSelectionScreenConstants.TITLE_BG_PADDING, titleY - 4,
            titleX + titleWidth + SpellSelectionScreenConstants.TITLE_BG_PADDING, titleY + font.lineHeight + 4,
            SpellUIConstants.BG_COLOR_SEMI_TRANSPARENT
        );
        
        // Draw title text
        guiGraphics.drawString(
            font,
            titleText,
            titleX,
            titleY,
            SpellUIConstants.TEXT_COLOR_SELECTED,
            true
        );
    }
    
    /**
     * Renders spell icons next to their respective buttons.
     */
    public static void renderSpellIcons(GuiGraphics guiGraphics, int width, int centerX,
                                       Map<Identifier, Integer> spellButtonPositions, 
                                       int selectedSlot) {
        int iconSize = SpellUIConstants.ICON_SIZE_SCREEN;
        int iconX = centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2;
        
        for (Map.Entry<Identifier, Integer> entry : spellButtonPositions.entrySet()) {
            Identifier spellId = entry.getKey();
            int buttonY = entry.getValue();
            
            Spell spell = SpellRegistry.get(spellId);
            if (spell == null) {
                continue;
            }
            
            Identifier iconTexture = spell.getIcon();
            boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
            boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
            int cooldownTicks = ClientSpellData.getCooldown(spellId);
            
            int iconY = buttonY + (SpellSelectionScreenConstants.BUTTON_HEIGHT - iconSize) / 2;
            
            // Draw icon background/border
            int bgColor = SpellUIConstants.BG_COLOR_SEMI_TRANSPARENT;
            int borderColor = isAssigned ? SpellUIConstants.BORDER_COLOR_SELECTED : SpellUIConstants.BORDER_COLOR_ICON;
            guiGraphics.fill(iconX - 1, iconY - 1, iconX + iconSize + 1, iconY + iconSize + 1, borderColor);
            guiGraphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, bgColor);
            
            // Render icon
            try {
                int tint = isOnCooldown ? SpellUIConstants.TINT_COOLDOWN : SpellUIConstants.TINT_NORMAL;
                
                guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    iconTexture,
                    iconX, iconY,
                    0.0f, 0.0f,
                    iconSize, iconSize,
                    iconSize, iconSize,
                    iconSize, iconSize,
                    tint
                );
                
                // Render cooldown overlay
                if (isOnCooldown && spell.getCooldown() > 0) {
                    float cooldownProgress = 1.0f - ((float) cooldownTicks / (float) spell.getCooldown());
                    int overlayHeight = (int) (iconSize * cooldownProgress);
                    if (overlayHeight > 0 && overlayHeight < iconSize) {
                        int overlayY = iconY + iconSize - overlayHeight;
                        int overlayColor = (int) (SpellUIConstants.COOLDOWN_OVERLAY_ALPHA * 255) << 24 | 0x000000;
                        guiGraphics.fill(iconX, overlayY, iconX + iconSize, iconY + iconSize, overlayColor);
                    }
                }
                
                // Render assignment indicator (checkmark overlay)
                if (isAssigned) {
                    int checkColor = SpellUIConstants.TEXT_COLOR_ASSIGNED;
                    int checkX = iconX + iconSize - 4;
                    int checkY = iconY + 2;
                    guiGraphics.fill(checkX, checkY + 1, checkX + 1, checkY + 3, checkColor);
                    guiGraphics.fill(checkX + 1, checkY + 2, checkX + 2, checkY + 4, checkColor);
                    guiGraphics.fill(checkX + 2, checkY + 3, checkX + 3, checkY + 5, checkColor);
                }
            } catch (Exception e) {
                // Icon texture not found - skip rendering
            }
        }
    }
}







