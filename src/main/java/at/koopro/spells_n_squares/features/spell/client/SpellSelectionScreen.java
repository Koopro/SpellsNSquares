package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.features.spell.SpellCategory;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GUI screen for selecting spells to assign to slots.
 * Enhanced with search, categories, sorting, and tooltips.
 */
public class SpellSelectionScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private int selectedSlot = SpellManager.SLOT_TOP;
    private final List<Identifier> allSpells = new ArrayList<>();
    private List<Identifier> filteredSpells = new ArrayList<>();
    private int scrollOffset = 0;
    
    // Filter and sort state
    private String searchText = "";
    private SpellCategory selectedCategory = SpellCategory.ALL;
    private SortType sortType = SortType.ALPHABETICAL;
    private Identifier hoveredSpellId = null;
    
    // UI components
    private EditBox searchBox;
    private final Map<Identifier, Integer> spellButtonPositions = new HashMap<>();
    private final Map<SpellCategory, Button> categoryButtons = new HashMap<>();
    private Button sortButton;
    
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
            allSpells.add(id);
        });
        
        updateFilteredSpells();
    }
    
    /**
     * Updates the filtered and sorted spell list based on current filters.
     */
    private void updateFilteredSpells() {
        filteredSpells = allSpells.stream()
            .filter(spellId -> {
                Spell spell = SpellRegistry.get(spellId);
                if (spell == null) return false;
                
                // Category filter
                if (selectedCategory != SpellCategory.ALL) {
                    SpellCategory spellCategory = SpellCategory.fromSpellId(spellId);
                    if (spellCategory != selectedCategory) {
                        return false;
                    }
                }
                
                // Search filter
                if (!searchText.isEmpty()) {
                    String spellName = spell.getName().toLowerCase();
                    String search = searchText.toLowerCase();
                    if (!spellName.contains(search)) {
                        return false;
                    }
                }
                
                return true;
            })
            .sorted(getComparator())
            .collect(Collectors.toList());
        
        // Reset scroll if needed
        int maxScroll = Math.max(0, filteredSpells.size() - getVisibleSpellCount());
        if (scrollOffset > maxScroll) {
            scrollOffset = Math.max(0, maxScroll);
        }
    }
    
    /**
     * Gets the comparator for the current sort type.
     */
    private Comparator<Identifier> getComparator() {
        return switch (sortType) {
            case ALPHABETICAL -> Comparator.<Identifier, String>comparing(id -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getName() : id.toString();
            });
            case REVERSE_ALPHABETICAL -> Comparator.<Identifier, String>comparing(id -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getName() : id.toString();
            }).reversed();
            case COOLDOWN_LOW -> Comparator.comparingInt((Identifier id) -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getCooldown() : Integer.MAX_VALUE;
            });
            case COOLDOWN_HIGH -> Comparator.<Identifier, Integer>comparing((Identifier id) -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getCooldown() : 0;
            }).reversed();
        };
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.clearWidgets();
        spellButtonPositions.clear();
        categoryButtons.clear();
        
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
                    this.minecraft.setScreen(new SpellSelectionScreen(selectedSlot, scrollOffset));
                }
            ));
        }
        
        // Add search box
        int searchX = centerX - SpellSelectionScreenConstants.SEARCH_BAR_WIDTH / 2;
        searchBox = new EditBox(this.font, searchX, SpellSelectionScreenConstants.SEARCH_BAR_Y,
            SpellSelectionScreenConstants.SEARCH_BAR_WIDTH, SpellSelectionScreenConstants.SEARCH_BAR_HEIGHT,
            Component.translatable("gui.spells_n_squares.search_spells"));
        searchBox.setValue(searchText);
        searchBox.setResponder(text -> {
            searchText = text;
            updateFilteredSpells();
            this.init(); // Rebuild buttons
        });
        this.addRenderableWidget(searchBox);
        
        // Add sort button
        int sortX = centerX + SpellSelectionScreenConstants.SEARCH_BAR_WIDTH / 2 + 4;
        sortButton = Button.builder(
            Component.literal("Sort: " + sortType.getDisplayName()),
            button -> {
                // Cycle through sort types
                sortType = SortType.values()[(sortType.ordinal() + 1) % SortType.values().length];
                updateFilteredSpells();
                this.init();
            }
        ).bounds(sortX, SpellSelectionScreenConstants.SORT_BUTTON_Y,
            SpellSelectionScreenConstants.SORT_BUTTON_WIDTH, SpellSelectionScreenConstants.SORT_BUTTON_HEIGHT).build();
        this.addRenderableWidget(sortButton);
        
        // Add category tabs
        SpellCategory[] categories = SpellCategory.values();
        int tabStartX = centerX - (categories.length * (80 + SpellSelectionScreenConstants.CATEGORY_TAB_SPACING)) / 2;
        for (int i = 0; i < categories.length; i++) {
            SpellCategory category = categories[i];
            int tabX = tabStartX + i * (80 + SpellSelectionScreenConstants.CATEGORY_TAB_SPACING);
            
            Button categoryButton = Button.builder(
                Component.literal(category.getDisplayName()),
                button -> {
                    selectedCategory = category;
                    scrollOffset = 0;
                    updateFilteredSpells();
                    this.init();
                }
            ).bounds(tabX, SpellSelectionScreenConstants.CATEGORY_TAB_Y, 80, SpellSelectionScreenConstants.CATEGORY_TAB_HEIGHT).build();
            
            categoryButtons.put(category, categoryButton);
            this.addRenderableWidget(categoryButton);
        }
        
        // Calculate visible spell count
        int maxVisibleSpells = getVisibleSpellCount();
        
        // Calculate button positions
        int scrollButtonY = this.height - SpellSelectionScreenConstants.SCROLL_BUTTON_Y_OFFSET - SpellSelectionScreenConstants.BUTTON_HEIGHT * 2;
        int clearButtonY = scrollButtonY + SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        int closeButtonY = this.height - SpellSelectionScreenConstants.CLOSE_BUTTON_Y_OFFSET;
        int maxSpellButtonY = scrollButtonY - SpellSelectionScreenConstants.BUTTON_SPACING;
        
        // Add scroll buttons
        Button scrollUpButton = Button.builder(
            Component.literal("▲"),
            button -> {
                if (scrollOffset > 0) {
                    scrollOffset--;
                    this.init();
                }
            }
        ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2, scrollButtonY,
            SpellSelectionScreenConstants.BUTTON_WIDTH / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2,
            SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        Button scrollDownButton = Button.builder(
            Component.literal("▼"),
            button -> {
                int maxScroll = Math.max(0, filteredSpells.size() - maxVisibleSpells);
                if (scrollOffset < maxScroll) {
                    scrollOffset++;
                    this.init();
                }
            }
        ).bounds(centerX + SpellSelectionScreenConstants.BUTTON_SPACING / 2, scrollButtonY,
            SpellSelectionScreenConstants.BUTTON_WIDTH / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2,
            SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(scrollUpButton);
        this.addRenderableWidget(scrollDownButton);
        
        // Add spell buttons
        int currentY = SpellSelectionScreenConstants.START_Y_WITH_FILTERS;
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + maxVisibleSpells, filteredSpells.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            if (currentY + SpellSelectionScreenConstants.BUTTON_HEIGHT > maxSpellButtonY) {
                break;
            }
            
            Identifier spellId = filteredSpells.get(i);
            Spell spell = SpellRegistry.get(spellId);
            if (spell == null) {
                continue;
            }
            
            final Identifier finalSpellId = spellId;
            final int buttonY = currentY;
            
            spellButtonPositions.put(spellId, buttonY);
            
            boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
            boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
            
            // Build button text
            StringBuilder buttonTextBuilder = new StringBuilder();
            buttonTextBuilder.append(spell.getName());
            
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
                buttonComponent = buttonComponent.copy().withStyle(style -> style.withColor(SpellUIConstants.TEXT_COLOR_ASSIGNED));
            }
            
            Button spellButton = Button.builder(
                buttonComponent,
                button -> {
                    try {
                        at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload payload =
                            new at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload(
                                selectedSlot,
                                java.util.Optional.of(finalSpellId)
                            );
                        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                        
                        ClientSpellData.setSpellInSlot(selectedSlot, finalSpellId);
                        
                        if (this.minecraft != null) {
                            this.minecraft.execute(() -> {
                                SpellSelectionScreen newScreen = new SpellSelectionScreen(selectedSlot, this.scrollOffset);
                                // Preserve filter state
                                newScreen.searchText = this.searchText;
                                newScreen.selectedCategory = this.selectedCategory;
                                newScreen.sortType = this.sortType;
                                newScreen.updateFilteredSpells();
                                this.minecraft.setScreen(newScreen);
                            });
                        }
                    } catch (Exception e) {
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
                at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload payload =
                    new at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload(
                        selectedSlot,
                        java.util.Optional.empty()
                    );
                net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
                
                ClientSpellData.setSpellInSlot(selectedSlot, null);
                SpellSelectionScreen newScreen = new SpellSelectionScreen(selectedSlot, this.scrollOffset);
                newScreen.searchText = this.searchText;
                newScreen.selectedCategory = this.selectedCategory;
                newScreen.sortType = this.sortType;
                newScreen.updateFilteredSpells();
                this.minecraft.setScreen(newScreen);
            }
        ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2, clearButtonY,
            SpellSelectionScreenConstants.BUTTON_WIDTH, SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(clearButton);
        
        // Add close button
        Button closeButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2, closeButtonY,
            SpellSelectionScreenConstants.BUTTON_WIDTH, SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(closeButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ClientSpellData.tickCooldowns();
        
        SpellSelectionRenderer.renderBackground(guiGraphics, this.width, this.height);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        String slotName = SpellSlotManager.getSlotDisplayName(selectedSlot);
        Component titleText = Component.translatable("gui.spells_n_squares.selecting_slot", slotName);
        SpellSelectionRenderer.renderTitle(guiGraphics, this.font, titleText, this.width);
        
        // Render category tab highlights
        for (Map.Entry<SpellCategory, Button> entry : categoryButtons.entrySet()) {
            if (entry.getKey() == selectedCategory) {
                Button button = entry.getValue();
                int color = entry.getKey().getColor();
                guiGraphics.fill(button.getX(), button.getY(),
                    button.getX() + button.getWidth(), button.getY() + button.getHeight(),
                    0x40 | ((color & 0xFFFFFF) << 8)); // Add transparency
            }
        }
        
        // Render spell icons with category colors
        int centerX = this.width / 2;
        SpellSelectionRenderer.renderSpellIcons(guiGraphics, this.width, centerX, spellButtonPositions, selectedSlot);
        
        // Render category color strips on spell buttons
        for (Map.Entry<Identifier, Integer> entry : spellButtonPositions.entrySet()) {
            Identifier spellId = entry.getKey();
            int buttonY = entry.getValue();
            SpellCategory category = SpellCategory.fromSpellId(spellId);
            int color = category.getColor();
            
            int stripX = centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2;
            guiGraphics.fill(stripX, buttonY, stripX + 3, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, color);
        }
        
        // Render tooltip for hovered spell
        if (hoveredSpellId != null) {
            Spell spell = SpellRegistry.get(hoveredSpellId);
            if (spell != null) {
                // Build tooltip text lines
                List<String> tooltipLines = new ArrayList<>();
                tooltipLines.add("§f" + spell.getName());
                tooltipLines.add("§7" + spell.getDescription());
                tooltipLines.add("§8Category: " + SpellCategory.fromSpellId(hoveredSpellId).getDisplayName());
                tooltipLines.add("§8Cooldown: " + String.format("%.1f", spell.getCooldown() / 20.0f) + "s");
                
                if (hoveredSpellId.equals(ClientSpellData.getSpellInSlot(selectedSlot))) {
                    tooltipLines.add("§aAssigned to this slot");
                }
                
                // Render tooltip manually
                int tooltipX = mouseX + 12;
                int tooltipY = mouseY - 12;
                int maxWidth = 200;
                int padding = 4;
                
                // Calculate tooltip dimensions
                int tooltipWidth = 0;
                for (String line : tooltipLines) {
                    int lineWidth = this.font.width(line.replaceAll("§[0-9a-fk-or]", ""));
                    tooltipWidth = Math.max(tooltipWidth, lineWidth);
                }
                tooltipWidth = Math.min(tooltipWidth + padding * 2, maxWidth);
                int tooltipHeight = tooltipLines.size() * this.font.lineHeight + padding * 2;
                
                // Adjust position if tooltip would go off screen
                if (tooltipX + tooltipWidth > this.width) {
                    tooltipX = mouseX - tooltipWidth - 12;
                }
                if (tooltipY + tooltipHeight > this.height) {
                    tooltipY = this.height - tooltipHeight - 4;
                }
                
                // Draw tooltip background
                guiGraphics.fill(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, 0xE0000000);
                guiGraphics.fill(tooltipX + 1, tooltipY + 1, tooltipX + tooltipWidth - 1, tooltipY + tooltipHeight - 1, 0xF0101010);
                
                // Draw tooltip text
                int textY = tooltipY + padding;
                for (String line : tooltipLines) {
                    guiGraphics.drawString(this.font, line, tooltipX + padding, textY, 0xFFFFFF, false);
                    textY += this.font.lineHeight;
                }
            }
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        int maxScroll = Math.max(0, filteredSpells.size() - getVisibleSpellCount());
        int newScrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - deltaY));
        if (newScrollOffset != scrollOffset) {
            scrollOffset = newScrollOffset;
            this.init();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        
        // Check if mouse is over a spell button
        hoveredSpellId = null;
        int centerX = this.width / 2;
        int buttonX = centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4;
        int buttonWidth = SpellSelectionScreenConstants.BUTTON_WIDTH - SpellUIConstants.ICON_SIZE_SCREEN - 4;
        
        for (Map.Entry<Identifier, Integer> entry : spellButtonPositions.entrySet()) {
            int buttonY = entry.getValue();
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT) {
                hoveredSpellId = entry.getKey();
                break;
            }
        }
    }
    
    private int getVisibleSpellCount() {
        int availableHeight = this.height - SpellSelectionScreenConstants.START_Y_WITH_FILTERS
            - SpellSelectionScreenConstants.SCROLL_BUTTON_Y_OFFSET
            - SpellSelectionScreenConstants.BUTTON_HEIGHT * 3;
        return Math.max(1, availableHeight / (SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING));
    }
    
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
    
    /**
     * Sort types for spell list.
     */
    private enum SortType {
        ALPHABETICAL("A-Z"),
        REVERSE_ALPHABETICAL("Z-A"),
        COOLDOWN_LOW("CD Low"),
        COOLDOWN_HIGH("CD High");
        
        private final String displayName;
        
        SortType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

