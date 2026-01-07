package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Set;

/**
 * Helper class for rendering SpellSelectionScreen elements.
 * Optimized with caching and efficient rendering.
 */
public final class SpellSelectionRenderer {
    private SpellSelectionRenderer() {
        // Utility class - prevent instantiation
    }
    
    // Cache for validated icon textures (spells with valid icons)
    private static final Set<Identifier> validIconCache = CollectionFactory.createSet();
    private static final Set<Identifier> invalidIconCache = CollectionFactory.createSet();
    
    /**
     * Renders the screen background with gradient and enhanced border.
     * Optimized to use larger fill operations instead of per-pixel rendering.
     */
    public static void renderBackground(GuiGraphics guiGraphics, int width, int height) {
        // Render gradient background (optimized: use larger chunks)
        int gradientSteps = Math.min(height / 4, 64); // Limit steps for performance
        int stepHeight = height / gradientSteps;
        for (int i = 0; i < gradientSteps; i++) {
            int y = i * stepHeight;
            int nextY = (i + 1) * stepHeight;
            if (i == gradientSteps - 1) {
                nextY = height; // Ensure we cover the full height
            }
            float progress = (float) y / (float) height;
            int color = AnimationHelper.lerpColor(
                SpellUIConstants.BG_COLOR_GRADIENT_START,
                SpellUIConstants.BG_COLOR_GRADIENT_END,
                progress
            );
            guiGraphics.fill(0, y, width, nextY, color);
        }
        
        // Draw enhanced border with shadow effect
        int borderColor = SpellUIConstants.BORDER_COLOR_ENHANCED;
        int shadowColor = SpellUIConstants.SHADOW_COLOR;
        
        // Shadow
        guiGraphics.fill(1, 1, width - 1, 2, shadowColor); // Top shadow
        guiGraphics.fill(1, height - 2, width - 1, height - 1, shadowColor); // Bottom shadow
        guiGraphics.fill(1, 1, 2, height - 1, shadowColor); // Left shadow
        guiGraphics.fill(width - 2, 1, width - 1, height - 1, shadowColor); // Right shadow
        
        // Border
        guiGraphics.fill(0, 0, width, 1, borderColor); // Top
        guiGraphics.fill(0, height - 1, width, height, borderColor); // Bottom
        guiGraphics.fill(0, 0, 1, height, borderColor); // Left
        guiGraphics.fill(width - 1, 0, width, height, borderColor); // Right
    }
    
    /**
     * Renders the title with enhanced background and shadow.
     */
    public static void renderTitle(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, 
                                   net.minecraft.network.chat.Component titleText, int width) {
        int titleWidth = font.width(titleText);
        int titleX = width / 2 - titleWidth / 2;
        int titleY = SpellSelectionScreenConstants.TITLE_Y;
        int padding = SpellSelectionScreenConstants.TITLE_BG_PADDING;
        
        // Draw title background with shadow
        int bgX = titleX - padding;
        int bgY = titleY - 4;
        int bgWidth = titleWidth + padding * 2;
        int bgHeight = font.lineHeight + 8;
        
        // Shadow
        guiGraphics.fill(
            bgX + 2, bgY + 2,
            bgX + bgWidth + 2, bgY + bgHeight + 2,
            SpellUIConstants.SHADOW_COLOR
        );
        
        // Background with slight gradient effect
        int bgColor = SpellUIConstants.BG_COLOR_SEMI_TRANSPARENT;
        guiGraphics.fill(
            bgX, bgY,
            bgX + bgWidth, bgY + bgHeight,
            bgColor
        );
        
        // Border
        int borderColor = SpellUIConstants.BORDER_COLOR_ENHANCED;
        guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + 1, borderColor); // Top
        guiGraphics.fill(bgX, bgY + bgHeight - 1, bgX + bgWidth, bgY + bgHeight, borderColor); // Bottom
        guiGraphics.fill(bgX, bgY, bgX + 1, bgY + bgHeight, borderColor); // Left
        guiGraphics.fill(bgX + bgWidth - 1, bgY, bgX + bgWidth, bgY + bgHeight, borderColor); // Right
        
        // Draw title text with shadow
        int textColor = SpellUIConstants.TEXT_COLOR_SELECTED;
        // Shadow
        guiGraphics.drawString(font, titleText, titleX + 1, titleY + 1, 0x80000000, false);
        // Main text
        guiGraphics.drawString(font, titleText, titleX, titleY, textColor, true);
    }
    
    /**
     * Renders spell icons next to their respective buttons with animations.
     */
    public static void renderSpellIcons(GuiGraphics guiGraphics, int width, int centerX,
                                       Map<Identifier, Integer> spellButtonPositions, 
                                       int selectedSlot, AnimationState animationState) {
        int iconSize = SpellUIConstants.ICON_SIZE_SCREEN;
        int iconX = centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2;
        long currentTime = System.currentTimeMillis();
        
        for (Map.Entry<Identifier, Integer> entry : spellButtonPositions.entrySet()) {
            Identifier spellId = entry.getKey();
            int buttonY = entry.getValue();
            
            Spell spell = SpellRegistry.get(spellId);
            if (spell == null) {
                continue;
            }
            
            // Get animation values
            float fadeIn = animationState.getSpellButtonFadeIn(spellId, currentTime);
            float hoverProgress = animationState.getHoverProgress(spellId);
            float selectionGlow = animationState.getSelectionGlowProgress(spellId);
            
            Identifier iconTexture = spell.getIcon();
            boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
            boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
            int cooldownTicks = ClientSpellData.getCooldown(spellId);
            
            int iconY = buttonY + (SpellSelectionScreenConstants.BUTTON_HEIGHT - iconSize) / 2;
            
            // Apply fade-in animation
            int baseAlpha = (int) (fadeIn * 255);
            
            // Draw icon background/border with hover effect
            int bgColor = SpellUIConstants.BG_COLOR_SEMI_TRANSPARENT;
            int baseBorderColor = isAssigned ? SpellUIConstants.BORDER_COLOR_SELECTED : SpellUIConstants.BORDER_COLOR_ICON;
            // Interpolate border color on hover
            int hoverBorderColor = AnimationHelper.lerpColorEased(
                baseBorderColor, 
                0xFFFFFF88, // Yellow-white on hover
                hoverProgress,
                AnimationHelper.Easing.SMOOTH
            );
            int borderColor = (baseAlpha << 24) | (hoverBorderColor & 0xFFFFFF);
            guiGraphics.fill(iconX - 1, iconY - 1, iconX + iconSize + 1, iconY + iconSize + 1, borderColor);
            int bgAlpha = (int) (fadeIn * 0.8f * 255);
            int animatedBgColor = (bgAlpha << 24) | (bgColor & 0xFFFFFF);
            guiGraphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, animatedBgColor);
            
            // Render selection glow
            if (selectionGlow > 0.01f) {
                int glowSize = (int) (selectionGlow * 4);
                int glowColor = ((int) (selectionGlow * 0.3f * baseAlpha) << 24) | 0x88FF88;
                guiGraphics.fill(iconX - glowSize, iconY - glowSize, 
                    iconX + iconSize + glowSize, iconY + iconSize + glowSize, glowColor);
            }
            
            // Check cache before attempting to render icon
            // Skip rendering if we know the icon is invalid
            if (invalidIconCache.contains(iconTexture)) {
                return; // Skip this icon
            }
            
            // Render icon with fade-in and hover tint
            // Only attempt rendering if not in invalid cache
            boolean iconRendered = false;
            if (validIconCache.contains(iconTexture)) {
                // Icon is known to be valid, render directly
                iconRendered = renderIcon(guiGraphics, iconTexture, iconX, iconY, iconSize, 
                    baseAlpha, hoverProgress, isOnCooldown);
            } else {
                // Try rendering and cache result
                try {
                    iconRendered = renderIcon(guiGraphics, iconTexture, iconX, iconY, iconSize, 
                        baseAlpha, hoverProgress, isOnCooldown);
                    validIconCache.add(iconTexture);
                } catch (Exception e) {
                    // Icon texture not found - cache as invalid
                    invalidIconCache.add(iconTexture);
                    return; // Skip rendering for this icon
                }
            }
            
            if (iconRendered) {
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
                
                // Render assignment indicator (checkmark overlay) with fade-in
                if (isAssigned) {
                    int baseCheckColor = SpellUIConstants.TEXT_COLOR_ASSIGNED;
                    int checkColor = (baseAlpha << 24) | (baseCheckColor & 0xFFFFFF);
                    int checkX = iconX + iconSize - 4;
                    int checkY = iconY + 2;
                    guiGraphics.fill(checkX, checkY + 1, checkX + 1, checkY + 3, checkColor);
                    guiGraphics.fill(checkX + 1, checkY + 2, checkX + 2, checkY + 4, checkColor);
                    guiGraphics.fill(checkX + 2, checkY + 3, checkX + 3, checkY + 5, checkColor);
                }
            }
        }
    }
    
    /**
     * Renders a single spell icon with optimized caching.
     * 
     * @return true if the icon was successfully rendered
     */
    private static boolean renderIcon(GuiGraphics guiGraphics, Identifier iconTexture, 
                                     int iconX, int iconY, int iconSize,
                                     int baseAlpha, float hoverProgress, boolean isOnCooldown) {
        int baseTint = isOnCooldown ? SpellUIConstants.TINT_COOLDOWN : SpellUIConstants.TINT_NORMAL;
        // Brighten on hover
        int hoverTint = AnimationHelper.lerpColorEased(
            baseTint,
            0xFFFFFFFF, // Full brightness on hover
            hoverProgress,
            AnimationHelper.Easing.SMOOTH
        );
        // Apply fade-in alpha
        int tint = (baseAlpha << 24) | (hoverTint & 0xFFFFFF);
        
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
        return true;
    }
    
    /**
     * Clears the icon texture cache. Call this when spell data changes.
     */
    public static void clearIconCache() {
        validIconCache.clear();
        invalidIconCache.clear();
    }
}


















