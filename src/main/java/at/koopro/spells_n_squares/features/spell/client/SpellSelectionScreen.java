package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.network.SpellSlotAssignPayload;
import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI screen for selecting and assigning spells to the 4 spell slots.
 */
public class SpellSelectionScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_SPACING = 8;
    private static final int AVAILABLE_SPELLS_START_Y = 100;
    private static final int AVAILABLE_SPELL_SIZE = 32;
    private static final int AVAILABLE_SPELL_SPACING = 4;
    
    // UI layout constants
    private static final int SLOTS_START_Y = 50;
    private static final int SIDE_MARGIN = 20;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_OFFSET_X = 50;
    private static final int BUTTON_OFFSET_Y = 30;
    
    // Slot positions (centered on screen)
    private final int[] slotX = new int[4];
    private final int[] slotY = new int[4];
    
    // Currently selected slot for assignment (-1 if none)
    private int selectedSlot = -1;
    
    // Cached spell list to avoid duplicate creation
    private List<Spell> cachedSpellList;
    
    // Slot constants array for iteration
    private static final int[] SLOTS = {
        SpellManager.SLOT_TOP,
        SpellManager.SLOT_BOTTOM,
        SpellManager.SLOT_LEFT,
        SpellManager.SLOT_RIGHT
    };
    
    public SpellSelectionScreen() {
        super(Component.translatable("gui.spells_n_squares.spell_selection"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Cache spell list once during initialization
        cachedSpellList = new ArrayList<>(SpellRegistry.getAll().values());
        
        int centerX = this.width / 2;
        
        // Calculate slot positions (top, bottom, left, right) using array-based approach
        slotX[SpellManager.SLOT_TOP] = centerX;
        slotY[SpellManager.SLOT_TOP] = SLOTS_START_Y;
        
        slotX[SpellManager.SLOT_BOTTOM] = centerX;
        slotY[SpellManager.SLOT_BOTTOM] = SLOTS_START_Y + SLOT_SIZE + SLOT_SPACING;
        
        slotX[SpellManager.SLOT_LEFT] = centerX - SLOT_SIZE / 2 - SLOT_SPACING / 2;
        slotY[SpellManager.SLOT_LEFT] = SLOTS_START_Y + SLOT_SIZE / 2;
        
        slotX[SpellManager.SLOT_RIGHT] = centerX + SLOT_SIZE / 2 + SLOT_SPACING / 2;
        slotY[SpellManager.SLOT_RIGHT] = SLOTS_START_Y + SLOT_SIZE / 2;
        
        // Add close button
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - BUTTON_OFFSET_X, this.height - BUTTON_OFFSET_Y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        
        // Add invisible clickable areas for slots and spells using buttons
        setupClickableAreas();
    }
    
    /**
     * Sets up clickable button widgets for slots and spells.
     */
    private void setupClickableAreas() {
        // Create invisible buttons for each slot
        for (int slot : SLOTS) {
            final int slotIndex = slot;
            int x = slotX[slot] - SLOT_SIZE / 2;
            int y = slotY[slot] - SLOT_SIZE / 2;
            
            // Create clickable button for slot (left click to select/deselect)
            Button slotButton = Button.builder(
                Component.empty(),
                button -> {
                    if (selectedSlot == slotIndex) {
                        // Click again to deselect
                        selectedSlot = -1;
                    } else {
                        selectedSlot = slotIndex;
                    }
                }
            ).bounds(x, y, SLOT_SIZE, SLOT_SIZE).build();
            this.addRenderableWidget(slotButton);
            
            // Add a small clear button in the top-right corner of each slot
            Button clearButton = Button.builder(
                Component.literal("×"),
                button -> {
                    assignSpellToSlot(slotIndex, null);
                    if (selectedSlot == slotIndex) {
                        selectedSlot = -1;
                    }
                }
            ).bounds(x + SLOT_SIZE / 2 - 12, y - SLOT_SIZE / 2 + 2, 10, 10).build();
            this.addRenderableWidget(clearButton);
        }
        
        // Create invisible buttons for each available spell
        int startX = SIDE_MARGIN;
        int currentY = AVAILABLE_SPELLS_START_Y + 5;
        int currentX = startX;
        int row = 0;
        
        for (Spell spell : cachedSpellList) {
            final int spellX = currentX;
            final int spellY = currentY + row * (AVAILABLE_SPELL_SIZE + AVAILABLE_SPELL_SPACING);
            final Identifier spellId = spell.getId();
            
            // Create invisible clickable button for spell
            Button spellButton = Button.builder(
                Component.empty(),
                button -> {
                    if (selectedSlot >= 0) {
                        assignSpellToSlot(selectedSlot, spellId);
                        selectedSlot = -1;
                    }
                }
            ).bounds(spellX, spellY, AVAILABLE_SPELL_SIZE, AVAILABLE_SPELL_SIZE + this.font.lineHeight + 4).build();
            this.addRenderableWidget(spellButton);
            
            currentX += AVAILABLE_SPELL_SIZE + AVAILABLE_SPELL_SPACING;
            if (currentX + AVAILABLE_SPELL_SIZE > this.width - SIDE_MARGIN) {
                currentX = startX;
                row++;
            }
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render a simple dark background instead of blurred background to avoid "Can only blur once per frame" error
        guiGraphics.fill(0, 0, this.width, this.height, SpellUIConstants.BG_COLOR_SCREEN);
        
        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, SpellUIConstants.TITLE_Y_OFFSET, SpellUIConstants.TEXT_COLOR_WHITE);
        
        // Draw slot labels and selection indicator
        String[] slotLabels = {"Top (W)", "Bottom (S)", "Left (A)", "Right (D)"};
        for (int slot : SLOTS) {
            int labelX = slotX[slot] - this.font.width(slotLabels[slot]) / 2;
            int labelY = slotY[slot] - SpellUIConstants.LABEL_OFFSET_Y;
            int labelColor = (selectedSlot == slot) ? SpellUIConstants.TEXT_COLOR_SELECTED : SpellUIConstants.TEXT_COLOR_WHITE;
            guiGraphics.drawString(this.font, slotLabels[slot], labelX, labelY, labelColor, true);
            
            // Draw selection indicator text
            if (selectedSlot == slot) {
                String selectText = "Selected - Click a spell below";
                int selectTextWidth = this.font.width(selectText);
                guiGraphics.drawString(this.font, selectText, 
                    this.width / 2 - selectTextWidth / 2, 
                    AVAILABLE_SPELLS_START_Y - SpellUIConstants.SELECTION_TEXT_OFFSET_Y, SpellUIConstants.TEXT_COLOR_SELECTED, true);
            }
        }
        
        // Call super.render first to render buttons and other widgets
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Draw spell slots (after widgets so icons render on top)
        Spell hoveredSlotSpell = null;
        for (int slot : SLOTS) {
            Spell slotSpell = renderSpellSlot(guiGraphics, slot, slotX[slot], slotY[slot], mouseX, mouseY);
            if (slotSpell != null) {
                hoveredSlotSpell = slotSpell;
            }
        }
        
        // Draw available spells list (after widgets so icons render on top)
        Spell hoveredAvailableSpell = renderAvailableSpells(guiGraphics, mouseX, mouseY);
        
        // Render tooltip for hovered spell (prioritize available spells over slots)
        // Tooltips should be rendered last to appear on top
        LOGGER.debug("Tooltip check: hoveredAvailableSpell={}, hoveredSlotSpell={}, mouse=({}, {}), screen size={}x{}", 
            hoveredAvailableSpell != null ? hoveredAvailableSpell.getId() : "null",
            hoveredSlotSpell != null ? hoveredSlotSpell.getId() : "null",
            mouseX, mouseY, this.width, this.height);
        
        if (hoveredAvailableSpell != null) {
            LOGGER.debug("Hovered available spell: {}", hoveredAvailableSpell.getId());
            String spellName = getSpellDisplayName(hoveredAvailableSpell);
            LOGGER.debug("Spell display name: '{}'", spellName);
            LOGGER.debug("About to call renderSimpleTooltip for available spell");
            renderSimpleTooltip(guiGraphics, spellName, mouseX, mouseY);
        } else if (hoveredSlotSpell != null) {
            LOGGER.debug("Hovered slot spell: {}", hoveredSlotSpell.getId());
            String spellName = getSpellDisplayName(hoveredSlotSpell);
            LOGGER.debug("Spell display name: '{}'", spellName);
            LOGGER.debug("About to call renderSimpleTooltip for slot spell");
            renderSimpleTooltip(guiGraphics, spellName, mouseX, mouseY);
        } else {
            LOGGER.debug("No spell hovered, not rendering tooltip");
        }
    }
    
    /**
     * Renders a spell slot.
     * @return The spell in the slot if it's being hovered, null otherwise
     */
    private Spell renderSpellSlot(GuiGraphics guiGraphics, int slot, int x, int y, int mouseX, int mouseY) {
        boolean isSelected = selectedSlot == slot;
        int slotLeft = x - SLOT_SIZE / 2;
        int slotRight = x + SLOT_SIZE / 2;
        int slotTop = y - SLOT_SIZE / 2;
        // Include text area below icon in hover detection
        int iconSize = SLOT_SIZE - 8;
        int slotBottom = y + iconSize / 2 + 2 + this.font.lineHeight;
        boolean isHovered = mouseX >= slotLeft && mouseX < slotRight &&
                           mouseY >= slotTop && mouseY < slotBottom;
        
        // Debug hover detection for first slot
        if (slot == 0) {
            LOGGER.debug("Slot 0 hover check: slot bounds x=[{}, {}], y=[{}, {}], mouse=({}, {}), isHovered={}", 
                slotLeft, slotRight, slotTop, slotBottom, mouseX, mouseY, isHovered);
        }
        
        // Draw slot background
        int borderColor = isSelected ? SpellUIConstants.BORDER_COLOR_SELECTED : 
                          (isHovered ? SpellUIConstants.BORDER_COLOR_HOVER : SpellUIConstants.BORDER_COLOR_DEFAULT);
        int borderWidth = isSelected ? 2 : 1;
        guiGraphics.fill(x - SLOT_SIZE / 2 - borderWidth, y - SLOT_SIZE / 2 - borderWidth,
                         x + SLOT_SIZE / 2 + borderWidth, y + SLOT_SIZE / 2 + borderWidth, borderColor);
        guiGraphics.fill(x - SLOT_SIZE / 2, y - SLOT_SIZE / 2,
                         x + SLOT_SIZE / 2, y + SLOT_SIZE / 2, SpellUIConstants.BG_COLOR_DARK);
        
        // Draw assigned spell icon
        Identifier spellId = ClientSpellData.getSpellInSlot(slot);
        if (spellId != null) {
            Spell spell = SpellRegistry.get(spellId);
            if (spell != null) {
                Identifier iconTexture = spell.getIcon();
                int iconX = x - iconSize / 2;
                int iconY = y - iconSize / 2;
                
                // Render spell icon texture - use same method as HUD
                guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    iconTexture,
                    iconX, iconY,
                    0.0f, 0.0f,
                    iconSize, iconSize,
                    iconSize, iconSize,
                    iconSize, iconSize,
                    SpellUIConstants.TINT_NORMAL
                );
                
                // Also show spell name as small text below icon for clarity
                String spellName = getSpellDisplayName(spell);
                int textWidth = this.font.width(spellName);
                if (textWidth <= iconSize) {
                    guiGraphics.drawString(this.font, spellName,
                        x - textWidth / 2, y + iconSize / 2 + 2, SpellUIConstants.TINT_SEMI_TRANSPARENT, true);
                }
                
                // Return spell if hovered for tooltip
                if (isHovered) {
                    LOGGER.debug("Slot spell hovered: {} at slot {} - mouse at ({}, {})", 
                        spell.getId(), slot, mouseX, mouseY);
                    return spell;
                }
                return null;
            }
        } else {
            // Empty slot - draw placeholder
            String emptyText = "Empty";
            int textWidth = this.font.width(emptyText);
            guiGraphics.drawString(this.font, emptyText,
                x - textWidth / 2, y - this.font.lineHeight / 2, SpellUIConstants.TEXT_COLOR_EMPTY_SLOT, true);
        }
        
        return null;
    }
    
    /**
     * Renders the list of available spells.
     * @return The spell being hovered, or null if none
     */
    private Spell renderAvailableSpells(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int startX = SIDE_MARGIN;
        int currentY = AVAILABLE_SPELLS_START_Y;
        
        guiGraphics.drawString(this.font, "Available Spells:", startX, currentY - 15, SpellUIConstants.TEXT_COLOR_WHITE, true);
        currentY += 5;
        
        int currentX = startX;
        int row = 0;
        Spell hoveredSpell = null;
        
        for (Spell spell : cachedSpellList) {
            int spellX = currentX;
            int spellY = currentY + row * (AVAILABLE_SPELL_SIZE + AVAILABLE_SPELL_SPACING);
            
            // Include text area below icon in hover detection
            // Match the exact rendered bounds (icon + text below)
            int spellRight = spellX + AVAILABLE_SPELL_SIZE;
            int spellBottom = spellY + AVAILABLE_SPELL_SIZE + 2 + this.font.lineHeight;
            boolean isHovered = mouseX >= spellX && mouseX < spellRight &&
                               mouseY >= spellY && mouseY < spellBottom;
            
            // Debug hover detection for all spells (can be reduced later)
            LOGGER.debug("Checking hover for {}: spell bounds x=[{}, {}], y=[{}, {}], mouse=({}, {}), isHovered={}, row={}, currentX={}", 
                spell.getId(), spellX, spellRight, spellY, spellBottom, mouseX, mouseY, isHovered, row, currentX);
            
            if (isHovered) {
                LOGGER.debug("Available spell hovered: {} at ({}, {}) - mouse at ({}, {}), setting hoveredSpell", 
                    spell.getId(), spellX, spellY, mouseX, mouseY);
                hoveredSpell = spell;
                // Don't break here - continue checking all spells to ensure proper rendering
            }
            
            // Draw spell icon background
            int borderColor = isHovered ? SpellUIConstants.TEXT_COLOR_WHITE : SpellUIConstants.BORDER_COLOR_SPELL_AVAILABLE;
            guiGraphics.fill(spellX - 1, spellY - 1,
                             spellX + AVAILABLE_SPELL_SIZE + 1, spellY + AVAILABLE_SPELL_SIZE + 1, borderColor);
            guiGraphics.fill(spellX, spellY,
                             spellX + AVAILABLE_SPELL_SIZE, spellY + AVAILABLE_SPELL_SIZE, SpellUIConstants.BG_COLOR_DARKER);
            
            // Draw spell icon texture - use same method as HUD
            Identifier iconTexture = spell.getIcon();
            guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                iconTexture,
                spellX + 2, spellY + 2,
                0.0f, 0.0f,
                AVAILABLE_SPELL_SIZE - 4, AVAILABLE_SPELL_SIZE - 4,
                AVAILABLE_SPELL_SIZE - 4, AVAILABLE_SPELL_SIZE - 4,
                AVAILABLE_SPELL_SIZE - 4, AVAILABLE_SPELL_SIZE - 4,
                SpellUIConstants.TINT_NORMAL
            );
            
            // Draw spell name below icon
            String spellName = getSpellDisplayName(spell);
            int textWidth = this.font.width(spellName);
            guiGraphics.drawString(this.font, spellName,
                spellX + (AVAILABLE_SPELL_SIZE - textWidth) / 2,
                spellY + AVAILABLE_SPELL_SIZE + 2,
                SpellUIConstants.TEXT_COLOR_WHITE, true);
            
            currentX += AVAILABLE_SPELL_SIZE + AVAILABLE_SPELL_SPACING;
            if (currentX + AVAILABLE_SPELL_SIZE > this.width - SIDE_MARGIN) {
                currentX = startX;
                row++;
            }
        }
        
        LOGGER.debug("renderAvailableSpells returning hoveredSpell: {}", 
            hoveredSpell != null ? hoveredSpell.getId() : "null");
        return hoveredSpell;
    }
    
    /**
     * Gets the display name for a spell, using translatable name with fallback to getName().
     */
    private String getSpellDisplayName(Spell spell) {
        if (spell == null) {
            LOGGER.warn("getSpellDisplayName called with null spell!");
            return "Unknown";
        }
        
        try {
            Component translatableName = spell.getTranslatableName();
            String translated = translatableName.getString();
            LOGGER.debug("Spell {}: translatableName component={}, getString()='{}'", 
                spell.getId(), translatableName, translated);
            
            // If translation returns the key itself (starts with "spell.") or is empty, use getName() as fallback
            if (translated.isEmpty() || translated.startsWith("spell.")) {
                String fallbackName = spell.getName();
                LOGGER.debug("Using fallback getName(): '{}'", fallbackName);
                return fallbackName;
            }
            LOGGER.debug("Using translated name: '{}'", translated);
            return translated;
        } catch (Exception e) {
            LOGGER.error("Error getting spell display name for {}", spell.getId(), e);
            return spell.getName();
        }
    }
    
    /**
     * Renders a simple tooltip with the spell name.
     */
    private void renderSimpleTooltip(GuiGraphics guiGraphics, String text, int mouseX, int mouseY) {
        LOGGER.debug("renderSimpleTooltip called with text='{}', mouseX={}, mouseY={}", text, mouseX, mouseY);
        
        if (text == null || text.isEmpty()) {
            LOGGER.warn("renderSimpleTooltip: text is null or empty, skipping render");
            return;
        }
        
        int textWidth = this.font.width(text);
        LOGGER.debug("Text width: {}, font lineHeight: {}", textWidth, this.font.lineHeight);
        
        int padding = 4;
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - 12;
        int tooltipHeight = this.font.lineHeight + padding * 2;
        int tooltipWidth = textWidth + padding * 2;
        
        LOGGER.debug("Tooltip dimensions: {}x{} at ({}, {})", tooltipWidth, tooltipHeight, tooltipX, tooltipY);
        
        // Ensure tooltip stays on screen
        if (tooltipX + tooltipWidth > this.width) {
            tooltipX = mouseX - tooltipWidth - 12;
        }
        if (tooltipY + tooltipHeight < 0) {
            tooltipY = mouseY + 12;
        }
        if (tooltipX < 0) {
            tooltipX = 12;
        }
        if (tooltipY < 0) {
            tooltipY = 12;
        }
        
        // Draw tooltip background - Minecraft-style dark background (more opaque)
        int bgColor = SpellUIConstants.BG_COLOR_TOOLTIP;
        guiGraphics.fill(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, bgColor);
        
        // Draw border (Minecraft tooltip style)
        int borderColor = SpellUIConstants.BORDER_COLOR_TOOLTIP;
        guiGraphics.fill(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 1, borderColor); // Top
        guiGraphics.fill(tooltipX, tooltipY + tooltipHeight - 1, tooltipX + tooltipWidth, tooltipY + tooltipHeight, borderColor); // Bottom
        guiGraphics.fill(tooltipX, tooltipY, tooltipX + 1, tooltipY + tooltipHeight, borderColor); // Left
        guiGraphics.fill(tooltipX + tooltipWidth - 1, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, borderColor); // Right
        
        // Draw tooltip text with shadow for better visibility
        int textX = tooltipX + padding;
        int textY = tooltipY + padding;
        LOGGER.debug("Drawing tooltip text '{}' at ({}, {})", text, textX, textY);
        LOGGER.debug("Tooltip bounds: x=[{}, {}], y=[{}, {}], text fits: {}", 
            tooltipX, tooltipX + tooltipWidth, tooltipY, tooltipY + tooltipHeight,
            (textX >= tooltipX && textX + textWidth <= tooltipX + tooltipWidth && 
             textY >= tooltipY && textY + this.font.lineHeight <= tooltipY + tooltipHeight));
        
        // Draw text using Component for proper rendering - use fully opaque white
        Component textComponent = Component.literal(text);
        guiGraphics.drawString(this.font, textComponent, textX, textY, SpellUIConstants.TINT_NORMAL, true);
        
        LOGGER.debug("Tooltip rendering complete - text '{}' drawn at ({}, {})", text, textX, textY);
    }
    
    /**
     * Assigns a spell to a slot and sends the assignment to the server.
     */
    private void assignSpellToSlot(int slot, Identifier spellId) {
        SpellSlotAssignPayload payload = new SpellSlotAssignPayload(slot, spellId != null ? java.util.Optional.of(spellId) : java.util.Optional.empty());
        ClientPacketDistributor.sendToServer(payload);
        
        // Update client-side immediately for visual feedback
        // The server will sync back the actual state, but this gives immediate feedback
        ClientSpellData.setSpellSlot(slot, spellId);
    }
    
    @Override
    public void tick() {
        super.tick();
        // Refresh the screen periodically to ensure spell assignments are visible
        // This helps if server sync is delayed
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game when opening this screen
    }
    
}

