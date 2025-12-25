package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI screen for selecting spells to assign to slots.
 */
public class SpellSelectionScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private int selectedSlot = SpellManager.SLOT_TOP;
    private final List<Identifier> availableSpells = new ArrayList<>();
    private int scrollOffset = 0;
    // Map to store button positions for icon rendering: spellId -> buttonY
    private final Map<Identifier, Integer> spellButtonPositions = new HashMap<>();
    
    public SpellSelectionScreen() {
        this(SpellManager.SLOT_TOP);
    }
    
    public SpellSelectionScreen(int initialSlot) {
        this(initialSlot, 0);
    }
    
    public SpellSelectionScreen(int initialSlot, int initialScrollOffset) {
        super(Component.translatable("gui.spells_n_squares.spell_selection"));
        this.selectedSlot = initialSlot;
        this.scrollOffset = initialScrollOffset;
        
        // Get all registered spells
        SpellRegistry.getAll().forEach((id, spell) -> {
            availableSpells.add(id);
        });
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Clear all existing widgets first to avoid duplicates
        this.clearWidgets();
        spellButtonPositions.clear();
        
        int centerX = this.width / 2;
        
        // Add slot selection buttons at the top
        int[] slots = {SpellManager.SLOT_TOP, SpellManager.SLOT_BOTTOM, SpellManager.SLOT_LEFT, SpellManager.SLOT_RIGHT};
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            String buttonText = SpellSlotManager.getSlotButtonText(slot, selectedSlot);
            int buttonX = SpellSlotManager.calculateSlotButtonX(centerX, i);
            int buttonWidth = SpellSlotManager.getSlotButtonWidth();
            
            addRenderableWidget(createSlotButton(
                buttonText,
                slot == selectedSlot,
                buttonX, SpellSelectionScreenConstants.SLOT_BUTTON_Y, buttonWidth,
                () -> {
                    selectedSlot = slot;
                    scrollOffset = 0;
                    this.minecraft.setScreen(new SpellSelectionScreen(selectedSlot));
                }
            ));
        }
        
        // Calculate visible spell count based on available space
        int maxVisibleSpells = getVisibleSpellCount();
        
        // Calculate scroll button position (before spell buttons to ensure they're below)
        int scrollButtonY = this.height - SpellSelectionScreenConstants.SCROLL_BUTTON_Y_OFFSET - SpellSelectionScreenConstants.BUTTON_HEIGHT * 2;
        int clearButtonY = scrollButtonY + SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        int closeButtonY = this.height - SpellSelectionScreenConstants.CLOSE_BUTTON_Y_OFFSET;
        
        // Calculate maximum Y position for spell buttons (ensure they don't overlap with scroll/clear buttons)
        int maxSpellButtonY = scrollButtonY - SpellSelectionScreenConstants.BUTTON_SPACING;
        
        // Add scroll buttons
        Button scrollUpButton = Button.builder(
            Component.literal("▲"),
            button -> {
                if (scrollOffset > 0) {
                    scrollOffset--;
                    this.init(); // Rebuild buttons with new scroll offset
                }
            }
        ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2, scrollButtonY, 
                SpellSelectionScreenConstants.BUTTON_WIDTH / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2, 
                SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        Button scrollDownButton = Button.builder(
            Component.literal("▼"),
            button -> {
                int maxScroll = Math.max(0, availableSpells.size() - maxVisibleSpells);
                if (scrollOffset < maxScroll) {
                    scrollOffset++;
                    this.init(); // Rebuild buttons with new scroll offset
                }
            }
        ).bounds(centerX + SpellSelectionScreenConstants.BUTTON_SPACING / 2, scrollButtonY, 
                SpellSelectionScreenConstants.BUTTON_WIDTH / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2, 
                SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(scrollUpButton);
        this.addRenderableWidget(scrollDownButton);
        
        // Add spell buttons (only visible ones based on scroll offset)
        // Ensure they don't overlap with scroll/clear buttons
        int currentY = SpellSelectionScreenConstants.START_Y;
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + maxVisibleSpells, availableSpells.size());
        
        // Debug: Log if no spells available
        if (availableSpells.isEmpty()) {
            LOGGER.warn("No spells available in SpellSelectionScreen!");
        }
        
        for (int i = startIndex; i < endIndex; i++) {
            // Check if button would overlap with scroll/clear buttons
            if (currentY + SpellSelectionScreenConstants.BUTTON_HEIGHT > maxSpellButtonY) {
                break; // Stop adding buttons if they would overlap
            }
            
            Identifier spellId = availableSpells.get(i);
            Spell spell = SpellRegistry.get(spellId);
            if (spell == null) {
                LOGGER.warn("Spell {} not found in registry", spellId);
                continue;
            }
            
            final Identifier finalSpellId = spellId;
            final int buttonY = currentY;
            
            // Store button position for icon rendering
            spellButtonPositions.put(spellId, buttonY);
            
            boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
            boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
            
            // Build button text (without checkmark prefix, icon will show assignment)
            StringBuilder buttonTextBuilder = new StringBuilder();
            buttonTextBuilder.append(spell.getName());
            
            // Add cooldown information
            if (isOnCooldown) {
                int cooldownTicks = ClientSpellData.getCooldown(spellId);
                float cooldownSeconds = cooldownTicks / 20.0f;
                String cooldownText = cooldownSeconds < 1.0f 
                    ? String.format(" (CD: %.1fs)", cooldownSeconds)
                    : String.format(" (CD: %.0fs)", cooldownSeconds);
                buttonTextBuilder.append(cooldownText);
            } else if (spell.getCooldown() > 0) {
                float maxCooldownSeconds = spell.getCooldown() / 20.0f;
                buttonTextBuilder.append(String.format(" (CD: %.0fs)", maxCooldownSeconds));
            }
            
            Component buttonComponent = Component.literal(buttonTextBuilder.toString());
            if (isAssigned) {
                // Style assigned spells differently
                buttonComponent = buttonComponent.copy().withStyle(style -> style.withColor(SpellUIConstants.TEXT_COLOR_ASSIGNED));
            }
            
            Button spellButton = Button.builder(
                buttonComponent,
                button -> {
                    try {
                        // Send spell assignment to server
                        at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload payload = 
                            new at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload(
                                selectedSlot, 
                                java.util.Optional.of(finalSpellId)
                            );
                        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                        
                        // Update client-side immediately for responsiveness
                        ClientSpellData.setSpellInSlot(selectedSlot, finalSpellId);
                        
                        // Refresh screen to show updated assignment (preserve selected slot and scroll)
                        if (this.minecraft != null) {
                            this.minecraft.execute(() -> {
                                SpellSelectionScreen newScreen = new SpellSelectionScreen(selectedSlot, this.scrollOffset);
                                this.minecraft.setScreen(newScreen);
                            });
                        }
                    } catch (Exception e) {
                        // Log error but don't crash
                        LOGGER.error("Error assigning spell to slot", e);
                    }
                }
            ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4, buttonY, 
                    SpellSelectionScreenConstants.BUTTON_WIDTH - SpellUIConstants.ICON_SIZE_SCREEN - 4, 
                    SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(spellButton);
            currentY += SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        }
        
        // Add clear slot button
        Button clearButton = Button.builder(
            Component.translatable("gui.spells_n_squares.clear_slot"),
            button -> {
                // Send clear to server
                at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload payload = 
                    new at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload(
                        selectedSlot, 
                        java.util.Optional.empty()
                    );
                net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                
                // Update client-side immediately
                ClientSpellData.setSpellInSlot(selectedSlot, null);
                SpellSelectionScreen newScreen = new SpellSelectionScreen(selectedSlot, this.scrollOffset);
                this.minecraft.setScreen(newScreen);
            }
        ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2, clearButtonY, 
                SpellSelectionScreenConstants.BUTTON_WIDTH, SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(clearButton);
        
        // Add close button at the bottom
        Button closeButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2, closeButtonY, 
                SpellSelectionScreenConstants.BUTTON_WIDTH, SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(closeButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Tick client-side cooldowns for real-time updates
        ClientSpellData.tickCooldowns();
        
        // Render background and border
        SpellSelectionRenderer.renderBackground(guiGraphics, this.width, this.height);
        
        // Draw title with better styling
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Draw selected slot indicator with better visual design
        String slotName = SpellSlotManager.getSlotDisplayName(selectedSlot);
        Component titleText = Component.translatable("gui.spells_n_squares.selecting_slot", slotName);
        SpellSelectionRenderer.renderTitle(guiGraphics, this.font, titleText, this.width);
        
        // Render spell icons next to buttons
        int centerX = this.width / 2;
        SpellSelectionRenderer.renderSpellIcons(guiGraphics, this.width, centerX, spellButtonPositions, selectedSlot);
    }
    
    /**
     * Calculates how many spell buttons can be visible at once.
     */
    private int getVisibleSpellCount() {
        int availableHeight = this.height - SpellSelectionScreenConstants.START_Y - SpellSelectionScreenConstants.SCROLL_BUTTON_Y_OFFSET - SpellSelectionScreenConstants.BUTTON_HEIGHT * 3; // Account for scroll buttons, clear button, and spacing
        return Math.max(1, availableHeight / (SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING));
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        int maxScroll = Math.max(0, availableSpells.size() - getVisibleSpellCount());
        int newScrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - deltaY));
        if (newScrollOffset != scrollOffset) {
            scrollOffset = newScrollOffset;
            this.init(); // Rebuild buttons with new scroll offset
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
    
    /**
     * Creates a styled slot selection button with better visual design.
     */
    private Button createSlotButton(String text, boolean isSelected, int x, int y, int width, Runnable onClick) {
        Component buttonText = Component.literal(text);
        if (isSelected) {
            buttonText = buttonText.copy().withStyle(style -> style.withColor(SpellUIConstants.TEXT_COLOR_SELECTED));
        }
        
        return Button.builder(buttonText, button -> onClick.run())
            .bounds(x, y, width, SpellSelectionScreenConstants.BUTTON_HEIGHT)
            .build();
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}








