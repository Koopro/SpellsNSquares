package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.base.SpellCategory;
import at.koopro.spells_n_squares.features.spell.client.SpellUsageStatistics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Panel component for displaying detailed spell information.
 * Shows spell description, statistics, and quick actions.
 */
public class SpellPreviewPanel {
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_PADDING = 8;
    private static final int LINE_SPACING = 4;
    private static final int SECTION_SPACING = 12;
    
    @Nullable
    private Identifier displayedSpellId = null;
    private float fadeProgress = 0.0f;
    private long lastUpdateTime = 0;
    private static final long FADE_DURATION = 200; // milliseconds
    
    /**
     * Sets the spell to display in the preview panel.
     */
    public void setSpell(@Nullable Identifier spellId) {
        if (spellId != displayedSpellId) {
            long currentTime = System.currentTimeMillis();
            if (displayedSpellId != null) {
                // Start fade out
                lastUpdateTime = currentTime;
            }
            displayedSpellId = spellId;
            if (spellId != null) {
                // Start fade in
                lastUpdateTime = currentTime;
            }
        }
    }
    
    /**
     * Gets the current fade progress for the panel.
     */
    public float getFadeProgress() {
        if (displayedSpellId == null) {
            // Fade out
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastUpdateTime;
            fadeProgress = Math.max(0.0f, 1.0f - ((float) elapsed / FADE_DURATION));
        } else {
            // Fade in
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastUpdateTime;
            fadeProgress = Math.min(1.0f, (float) elapsed / FADE_DURATION);
        }
        return AnimationHelper.applyEasing(fadeProgress, AnimationHelper.Easing.SMOOTH);
    }
    
    /**
     * Renders the preview panel.
     */
    public void render(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight, 
                      int selectedSlot, float partialTick) {
        if (displayedSpellId == null && fadeProgress < 0.01f) {
            return; // Not visible
        }
        
        float alpha = getFadeProgress();
        if (alpha < 0.01f) {
            return;
        }
        
        Spell spell = SpellRegistry.get(displayedSpellId);
        if (spell == null && displayedSpellId != null) {
            return;
        }
        
        // Panel position (right side of screen)
        int panelX = screenWidth - PANEL_WIDTH - 10;
        int panelY = 50;
        int panelHeight = screenHeight - panelY - 50;
        
        // Calculate content height
        int contentHeight = calculateContentHeight(font, spell, selectedSlot);
        panelHeight = Math.min(panelHeight, contentHeight + PANEL_PADDING * 2);
        
        int alphaInt = (int) (alpha * 255);
        
        // Draw panel background with shadow
        int shadowColor = (alphaInt / 2 << 24) | 0x000000;
        guiGraphics.fill(panelX + 2, panelY + 2, panelX + PANEL_WIDTH + 2, panelY + panelHeight + 2, shadowColor);
        
        int bgColor = ((int) (alpha * 0.95f * 255) << 24) | 0x1A1A1A;
        guiGraphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + panelHeight, bgColor);
        
        // Draw border
        int borderColor = (alphaInt << 24) | (SpellUIConstants.BORDER_COLOR_ENHANCED & 0xFFFFFF);
        guiGraphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 1, borderColor); // Top
        guiGraphics.fill(panelX, panelY + panelHeight - 1, panelX + PANEL_WIDTH, panelY + panelHeight, borderColor); // Bottom
        guiGraphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor); // Left
        guiGraphics.fill(panelX + PANEL_WIDTH - 1, panelY, panelX + PANEL_WIDTH, panelY + panelHeight, borderColor); // Right
        
        if (spell == null) {
            return;
        }
        
        // Render content
        int currentY = panelY + PANEL_PADDING;
        int textX = panelX + PANEL_PADDING;
        int textColor = (alphaInt << 24) | 0xFFFFFF;
        int secondaryColor = (alphaInt << 24) | 0xCCCCCC;
        int accentColor = (alphaInt << 24) | (SpellUIConstants.TEXT_COLOR_SELECTED & 0xFFFFFF);
        
        // Spell name
        Component nameComponent = Component.literal(spell.getName());
        guiGraphics.drawString(font, nameComponent, textX, currentY, accentColor, false);
        currentY += font.lineHeight + LINE_SPACING;
        
        // Category
        SpellCategory category = SpellCategory.fromSpellId(displayedSpellId);
        int categoryColor = (alphaInt << 24) | (category.getColor() & 0xFFFFFF);
        Component categoryComponent = Component.literal("Category: " + category.getDisplayName());
        guiGraphics.drawString(font, categoryComponent, textX, currentY, categoryColor, false);
        currentY += font.lineHeight + SECTION_SPACING;
        
        // Description
        String description = spell.getDescription();
        if (description != null && !description.isEmpty()) {
            Component descComponent = Component.literal("Description:");
            guiGraphics.drawString(font, descComponent, textX, currentY, textColor, false);
            currentY += font.lineHeight + LINE_SPACING;
            
            // Wrap description text
            int maxWidth = PANEL_WIDTH - PANEL_PADDING * 2;
            java.util.List<net.minecraft.util.FormattedCharSequence> wrapped = font.split(
                Component.literal(description), maxWidth
            );
            for (net.minecraft.util.FormattedCharSequence line : wrapped) {
                guiGraphics.drawString(font, line, textX, currentY, secondaryColor, false);
                currentY += font.lineHeight + LINE_SPACING;
            }
            currentY += SECTION_SPACING - LINE_SPACING;
        }
        
        // Statistics
        Component statsLabel = Component.literal("Statistics:");
        guiGraphics.drawString(font, statsLabel, textX, currentY, textColor, false);
        currentY += font.lineHeight + LINE_SPACING;
        
        // Cooldown
        float cooldownSeconds = spell.getCooldown() / 20.0f;
        Component cooldownText = Component.literal(String.format("Cooldown: %.1fs", cooldownSeconds));
        guiGraphics.drawString(font, cooldownText, textX, currentY, secondaryColor, false);
        currentY += font.lineHeight + LINE_SPACING;
        
        // Check if assigned
        boolean isAssigned = displayedSpellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
        if (isAssigned) {
            Component assignedText = Component.literal("✓ Assigned to this slot");
            int assignedColor = (alphaInt << 24) | (SpellUIConstants.TEXT_COLOR_ASSIGNED & 0xFFFFFF);
            guiGraphics.drawString(font, assignedText, textX, currentY, assignedColor, false);
            currentY += font.lineHeight + LINE_SPACING;
        }
        
        // Check if favorite
        boolean isFavorite = ClientSpellData.isFavorite(displayedSpellId);
        if (isFavorite) {
            Component favoriteText = Component.literal("★ Favorite");
            int favoriteColor = (alphaInt << 24) | 0xFFD700; // Gold
            guiGraphics.drawString(font, favoriteText, textX, currentY, favoriteColor, false);
            currentY += font.lineHeight + LINE_SPACING;
        }
        
        // Usage statistics
        int usageCount = SpellUsageStatistics.getUsageCount(displayedSpellId);
        if (usageCount > 0) {
            Component usageText = Component.literal("Used: " + usageCount + " time" + (usageCount != 1 ? "s" : ""));
            guiGraphics.drawString(font, usageText, textX, currentY, secondaryColor, false);
        }
    }
    
    /**
     * Calculates the height needed for the content.
     */
    private int calculateContentHeight(Font font, Spell spell, int selectedSlot) {
        if (spell == null) {
            return 0;
        }
        
        int height = PANEL_PADDING * 2;
        height += font.lineHeight; // Name
        height += LINE_SPACING;
        height += font.lineHeight; // Category
        height += SECTION_SPACING;
        
        // Description
        String description = spell.getDescription();
        if (description != null && !description.isEmpty()) {
            height += font.lineHeight; // "Description:" label
            height += LINE_SPACING;
            int maxWidth = PANEL_WIDTH - PANEL_PADDING * 2;
            java.util.List<net.minecraft.util.FormattedCharSequence> wrapped = font.split(
                Component.literal(description), maxWidth
            );
            height += wrapped.size() * (font.lineHeight + LINE_SPACING);
            height += SECTION_SPACING - LINE_SPACING;
        }
        
        // Statistics
        height += font.lineHeight; // "Statistics:" label
        height += LINE_SPACING;
        height += font.lineHeight; // Cooldown
        
        if (displayedSpellId != null && displayedSpellId.equals(ClientSpellData.getSpellInSlot(selectedSlot))) {
            height += font.lineHeight + LINE_SPACING; // Assigned text
        }
        
        if (displayedSpellId != null && ClientSpellData.isFavorite(displayedSpellId)) {
            height += font.lineHeight + LINE_SPACING; // Favorite text
        }
        
        return height;
    }
    
    /**
     * Gets the currently displayed spell ID.
     */
    @Nullable
    public Identifier getDisplayedSpellId() {
        return displayedSpellId;
    }
}

