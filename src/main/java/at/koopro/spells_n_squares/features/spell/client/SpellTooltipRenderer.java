package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.core.util.text.StringUtils;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.base.SpellCategory;
import at.koopro.spells_n_squares.features.spell.client.SpellUsageStatistics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Dedicated class for rendering spell tooltips with improved formatting.
 */
public final class SpellTooltipRenderer {
    private SpellTooltipRenderer() {
        // Utility class - prevent instantiation
    }
    
    private static final int MAX_TOOLTIP_WIDTH = 200;
    private static final int TOOLTIP_PADDING = 4;
    private static final int TOOLTIP_OFFSET_X = 12;
    private static final int TOOLTIP_OFFSET_Y = 12;
    
    // Color constants for spell tooltips
    private static final int COLOR_SPELL_NAME = ColorUtils.SPELL_WHITE; // Bright white
    private static final int COLOR_DESCRIPTION = ColorUtils.rgb(170, 170, 170); // Gray
    private static final int COLOR_CATEGORY = ColorUtils.rgb(85, 85, 85); // Dark gray
    private static final int COLOR_COOLDOWN = ColorUtils.rgb(85, 85, 85); // Dark gray
    private static final int COLOR_ASSIGNED = ColorUtils.SPELL_GREEN; // Green
    private static final int COLOR_FAVORITE = ColorUtils.SPELL_GOLD; // Yellow/Gold
    private static final int COLOR_USAGE = ColorUtils.rgb(85, 85, 85); // Dark gray
    private static final int COLOR_HINT = ColorUtils.rgb(170, 170, 170); // Gray
    
    /**
     * Renders a tooltip for a spell with fade animation.
     * 
     * @param guiGraphics The graphics context
     * @param font The font to use
     * @param spellId The spell ID to render tooltip for
     * @param selectedSlot The currently selected slot
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param screenWidth Screen width
     * @param screenHeight Screen height
     * @param fadeAlpha Fade alpha (0.0 to 1.0)
     */
    public static void renderTooltip(GuiGraphics guiGraphics, Font font, 
                                    @Nullable Identifier spellId, int selectedSlot,
                                    int mouseX, int mouseY, int screenWidth, int screenHeight,
                                    float fadeAlpha) {
        if (spellId == null || fadeAlpha < 0.01f) {
            return;
        }
        
        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            return;
        }
        
        // Build tooltip lines
        List<Component> tooltipLines = buildTooltipLines(spell, spellId, selectedSlot);
        
        // Calculate tooltip dimensions
        int tooltipWidth = calculateTooltipWidth(font, tooltipLines);
        int tooltipHeight = tooltipLines.size() * font.lineHeight + TOOLTIP_PADDING * 2;
        
        // Calculate tooltip position
        int tooltipX = mouseX + TOOLTIP_OFFSET_X;
        int tooltipY = mouseY - TOOLTIP_OFFSET_Y;
        
        // Adjust position if tooltip would go off screen
        if (tooltipX + tooltipWidth > screenWidth) {
            tooltipX = mouseX - tooltipWidth - TOOLTIP_OFFSET_X;
        }
        if (tooltipY + tooltipHeight > screenHeight) {
            tooltipY = screenHeight - tooltipHeight - 4;
        }
        
        // Apply fade animation
        int alpha = (int) (fadeAlpha * 255);
        
        // Draw tooltip background with enhanced visual feedback and fade
        drawTooltipBackground(guiGraphics, tooltipX, tooltipY, tooltipWidth, tooltipHeight, alpha);
        
        // Draw tooltip text with improved readability and fade
        drawTooltipText(guiGraphics, font, tooltipLines, tooltipX, tooltipY, alpha);
    }
    
    /**
     * Builds the tooltip text lines for a spell using ColorUtils for consistent coloring.
     */
    private static List<Component> buildTooltipLines(Spell spell, Identifier spellId, int selectedSlot) {
        List<Component> lines = at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createList();
        
        // Spell name (bright white)
        lines.add(ColorUtils.coloredText(spell.getName(), COLOR_SPELL_NAME));
        
        // Description (gray)
        String description = spell.getDescription();
        if (StringUtils.isNotEmpty(description)) {
            lines.add(ColorUtils.coloredText(description, COLOR_DESCRIPTION));
        }
        
        // Category (dark gray)
        SpellCategory category = SpellCategory.fromSpellId(spellId);
        lines.add(ColorUtils.coloredText("Category: " + category.getDisplayName(), COLOR_CATEGORY));
        
        // Cooldown (dark gray)
        lines.add(ColorUtils.coloredText("Cooldown: " + String.format("%.1f", spell.getCooldown() / 20.0f) + "s", COLOR_COOLDOWN));
        
        // Assigned indicator (green)
        if (spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot))) {
            lines.add(ColorUtils.coloredText("Assigned to this slot", COLOR_ASSIGNED));
        }
        
        // Favorite indicator (yellow/gold)
        if (ClientSpellData.isFavorite(spellId)) {
            lines.add(ColorUtils.coloredText("★ Favorite", COLOR_FAVORITE));
        }
        
        // Usage statistics
        int usageCount = SpellUsageStatistics.getUsageCount(spellId);
        if (usageCount > 0) {
            lines.add(ColorUtils.coloredText("Used: " + usageCount + " time" + (usageCount != 1 ? "s" : ""), COLOR_USAGE));
        }
        
        // Keyboard shortcut hint (gray)
        lines.add(ColorUtils.coloredText("Press F to toggle favorite", COLOR_HINT));
        
        return lines;
    }
    
    /**
     * Calculates the width needed for the tooltip.
     */
    private static int calculateTooltipWidth(Font font, List<Component> tooltipLines) {
        int maxWidth = 0;
        for (Component line : tooltipLines) {
            int lineWidth = font.width(line);
            maxWidth = Math.max(maxWidth, lineWidth);
        }
        return Math.min(maxWidth + TOOLTIP_PADDING * 2, MAX_TOOLTIP_WIDTH);
    }
    
    /**
     * Draws the tooltip background with enhanced styling.
     */
    private static void drawTooltipBackground(GuiGraphics guiGraphics, int x, int y, 
                                             int width, int height, int alpha) {
        // Outer border (darker)
        int borderColor = (alpha << 24) | 0x000000;
        guiGraphics.fill(x, y, x + width, y + height, borderColor);
        
        // Inner background (semi-transparent dark)
        int bgColor = ((int) (alpha * 0.94f) << 24) | 0x101010;
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, bgColor);
        
        // Highlight border (subtle glow effect at top)
        int highlightColor = ((int) (alpha * 0.25f) << 24) | 0xFFFFFF;
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, highlightColor);
    }
    
    /**
     * Draws the tooltip text with proper formatting using ColorUtils colors.
     * Alpha is handled by the background rendering, so components are rendered directly.
     */
    private static void drawTooltipText(GuiGraphics guiGraphics, Font font, 
                                       List<Component> tooltipLines, int x, int y, int alpha) {
        int textY = y + TOOLTIP_PADDING;
        for (Component line : tooltipLines) {
            // Render component directly - alpha is handled by the background
            // Use white color parameter to let the Component's style handle coloring
            guiGraphics.drawString(font, line, x + TOOLTIP_PADDING, textY, 0xFFFFFF, false);
            textY += font.lineHeight;
        }
    }
}

