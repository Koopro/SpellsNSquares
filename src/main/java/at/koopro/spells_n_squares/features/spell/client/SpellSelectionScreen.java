package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.base.SpellCategory;
import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GUI screen for selecting spells to assign to slots.
 * Enhanced with search, categories, sorting, and tooltips.
 */
public class SpellSelectionScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final List<Identifier> allSpells = CollectionFactory.createList();
    
    // Lazy loading cache: only load spell data when needed
    private final Map<Identifier, Spell> spellCache = CollectionFactory.createMap();
    
    // State management
    private final SpellSelectionState uiState = new SpellSelectionState();
    private final SpellFilterState filterState = new SpellFilterState();
    
    // UI components
    private EditBox searchBox;
    private final Map<SpellCategory, Button> categoryButtons = CollectionFactory.createMap();
    private Button sortButton;
    private final List<Button> spellButtons = CollectionFactory.createList();
    
    // Animation state
    private final AnimationState animationState = new AnimationState();
    
    // Preview panel
    private final SpellPreviewPanel previewPanel = new SpellPreviewPanel();
    
    // Help overlay
    private boolean showHelpOverlay = false;
    
    // Double-click tracking
    private Identifier lastClickedSpellId = null;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME_MS = 300; // 300ms for double-click
    
    public SpellSelectionScreen() {
        this(SpellManager.SLOT_TOP);
    }
    
    public SpellSelectionScreen(int initialSlot) {
        this(initialSlot, 0);
    }
    
    public SpellSelectionScreen(int initialSlot, int initialScrollOffset) {
        super(Component.translatable("gui.spells_n_squares.spell_selection"));
        uiState.setSelectedSlot(initialSlot);
        uiState.setScrollOffset(initialScrollOffset);
        
        // Get all registered spells (with null safety)
        try {
            SpellRegistry.getAll().forEach((id, spell) -> {
                if (id != null) {
                    allSpells.add(id);
                }
            });
        } catch (Exception e) {
            LOGGER.error("Error loading spells from registry", e);
        }
        
        try {
            updateFilteredSpells();
        } catch (Exception e) {
            LOGGER.error("Error updating filtered spells in constructor", e);
        }
    }
    
    /**
     * Gets a spell with lazy loading and caching.
     */
    private Spell getSpell(Identifier spellId) {
        if (spellId == null) {
            return null;
        }
        return spellCache.computeIfAbsent(spellId, SpellRegistry::get);
    }
    
    /**
     * Updates the filtered and sorted spell list based on current filters.
     */
    private void updateFilteredSpells() {
        List<Identifier> filtered = allSpells.stream()
            .filter(spellId -> {
                // Skip null spell IDs to prevent NullPointerException
                if (spellId == null) {
                    return false;
                }
                Spell spell = getSpell(spellId);
                if (spell == null) return false;
                
                // Recent spells filter
                if (filterState.isShowRecentOnly()) {
                    java.util.List<Identifier> recentSpells = RecentSpellsManager.getRecentSpells();
                    if (!recentSpells.contains(spellId)) {
                        return false;
                    }
                }
                
                // Favorites filter
                if (filterState.isShowFavoritesOnly() && !ClientSpellData.isFavorite(spellId)) {
                    return false;
                }
                
                // Category filter
                if (filterState.getSelectedCategory() != SpellCategory.ALL) {
                    SpellCategory spellCategory = SpellCategory.fromSpellId(spellId);
                    if (spellCategory != filterState.getSelectedCategory()) {
                        return false;
                    }
                }
                
                // Search filter
                String searchText = filterState.getSearchText();
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
        
        uiState.setFilteredSpells(filtered);
        
        // Reset scroll if needed
        int maxScroll = Math.max(0, filtered.size() - getVisibleSpellCount());
        if (uiState.getScrollOffset() > maxScroll) {
            uiState.setScrollOffset(Math.max(0, maxScroll));
        }
    }
    
    /**
     * Gets the comparator for the current sort type.
     */
    private Comparator<Identifier> getComparator() {
        return switch (filterState.getSortType()) {
            case ALPHABETICAL -> Comparator.<Identifier, String>comparing(id -> {
                if (id == null) return "";
                Spell spell = getSpell(id);
                return spell != null ? spell.getName() : id.toString();
            });
            case REVERSE_ALPHABETICAL -> Comparator.<Identifier, String>comparing(id -> {
                if (id == null) return "";
                Spell spell = getSpell(id);
                return spell != null ? spell.getName() : id.toString();
            }).reversed();
            case COOLDOWN_LOW -> Comparator.comparingInt((Identifier id) -> {
                if (id == null) return Integer.MAX_VALUE;
                Spell spell = getSpell(id);
                return spell != null ? spell.getCooldown() : Integer.MAX_VALUE;
            });
            case COOLDOWN_HIGH -> Comparator.<Identifier, Integer>comparing((Identifier id) -> {
                if (id == null) return 0;
                Spell spell = getSpell(id);
                return spell != null ? spell.getCooldown() : 0;
            }).reversed();
        };
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Guard against initialization before screen is ready
        if (this.width <= 0 || this.height <= 0 || this.font == null) {
            LOGGER.warn("SpellSelectionScreen.init() called before screen is ready (width={}, height={}, font={})", 
                this.width, this.height, this.font != null);
            return;
        }
        
        // Initialize animation state
        long currentTime = System.currentTimeMillis();
        animationState.initialize(currentTime);
        
        this.clearWidgets();
        uiState.clearSpellButtonPositions();
        categoryButtons.clear();
        spellButtons.clear();
        
        int centerX = this.width / 2;
        int selectedSlot = uiState.getSelectedSlot();
        
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
                    uiState.setSelectedSlot(slot);
                    uiState.setScrollOffset(0);
                    this.minecraft.setScreen(new SpellSelectionScreen(uiState.getSelectedSlot(), uiState.getScrollOffset()));
                }
            ));
        }
        
        // Add search box
        int searchX = centerX - SpellSelectionScreenConstants.SEARCH_BAR_WIDTH / 2;
        searchBox = new EditBox(this.font, searchX, SpellSelectionScreenConstants.SEARCH_BAR_Y,
            SpellSelectionScreenConstants.SEARCH_BAR_WIDTH, SpellSelectionScreenConstants.SEARCH_BAR_HEIGHT,
            Component.translatable("gui.spells_n_squares.search_spells"));
        searchBox.setValue(filterState.getSearchText());
        searchBox.setResponder(text -> {
            filterState.setSearchText(text);
            updateFilteredSpells();
            // Only rebuild if the filtered list actually changed
            rebuildSpellButtons();
        });
        this.addRenderableWidget(searchBox);
        
        // Add sort button
        int sortX = centerX + SpellSelectionScreenConstants.SEARCH_BAR_WIDTH / 2 + 4;
        sortButton = Button.builder(
            Component.literal("Sort: " + filterState.getSortType().getDisplayName()),
            button -> {
                filterState.cycleSortType();
                updateFilteredSpells();
                rebuildSpellButtons();
            }
        ).bounds(sortX, SpellSelectionScreenConstants.SORT_BUTTON_Y,
            SpellSelectionScreenConstants.SORT_BUTTON_WIDTH, SpellSelectionScreenConstants.SORT_BUTTON_HEIGHT).build();
        this.addRenderableWidget(sortButton);
        
        // Add favorites toggle button
        Button favoritesButton = Button.builder(
            Component.literal(filterState.isShowFavoritesOnly() ? "★ Favorites" : "☆ All Spells"),
            button -> {
                filterState.toggleFavoritesOnly();
                uiState.resetScrollOnFilterChange();
                updateFilteredSpells();
                rebuildSpellButtons();
            }
        ).bounds(centerX - SpellSelectionScreenConstants.SEARCH_BAR_WIDTH / 2 - 100, SpellSelectionScreenConstants.SORT_BUTTON_Y,
            95, SpellSelectionScreenConstants.SORT_BUTTON_HEIGHT).build();
        this.addRenderableWidget(favoritesButton);
        
        // Add recent spells toggle button
        Button recentButton = Button.builder(
            Component.literal(filterState.isShowRecentOnly() ? "Recent" : "All"),
            button -> {
                filterState.toggleRecentOnly();
                uiState.resetScrollOnFilterChange();
                updateFilteredSpells();
                rebuildSpellButtons();
            }
        ).bounds(centerX - SpellSelectionScreenConstants.SEARCH_BAR_WIDTH / 2 - 200, SpellSelectionScreenConstants.SORT_BUTTON_Y,
            95, SpellSelectionScreenConstants.SORT_BUTTON_HEIGHT).build();
        this.addRenderableWidget(recentButton);
        
        // Add category tabs
        SpellCategory[] categories = SpellCategory.values();
        int tabStartX = centerX - (categories.length * (80 + SpellSelectionScreenConstants.CATEGORY_TAB_SPACING)) / 2;
        for (int i = 0; i < categories.length; i++) {
            SpellCategory category = categories[i];
            int tabX = tabStartX + i * (80 + SpellSelectionScreenConstants.CATEGORY_TAB_SPACING);
            
            Button categoryButton = Button.builder(
                Component.literal(category.getDisplayName()),
                button -> {
                    filterState.setSelectedCategory(category);
                    uiState.resetScrollOnFilterChange();
                    updateFilteredSpells();
                    rebuildSpellButtons();
                }
            ).bounds(tabX, SpellSelectionScreenConstants.CATEGORY_TAB_Y, 80, SpellSelectionScreenConstants.CATEGORY_TAB_HEIGHT).build();
            
            categoryButtons.put(category, categoryButton);
            this.addRenderableWidget(categoryButton);
        }
        
        // Calculate visible spell count
        int maxVisibleSpells = getVisibleSpellCount();
        
        // Calculate button positions dynamically based on screen height
        // Position buttons from bottom up to ensure they're always visible
        int closeButtonY = this.height - 5; // 5px from bottom
        int clearButtonY = closeButtonY - SpellSelectionScreenConstants.BUTTON_HEIGHT - SpellSelectionScreenConstants.BUTTON_SPACING;
        int scrollButtonY = clearButtonY - SpellSelectionScreenConstants.BUTTON_HEIGHT - SpellSelectionScreenConstants.BUTTON_SPACING;
        int maxSpellButtonY = scrollButtonY - SpellSelectionScreenConstants.BUTTON_SPACING;
        
        // Add scroll buttons
        int effectiveButtonWidth = getEffectiveButtonWidth();
        Button scrollUpButton = Button.builder(
            Component.literal("▲"),
            button -> {
                if (uiState.getScrollOffset() > 0) {
                    uiState.scrollUp();
                    animationState.startScrollAnimation(System.currentTimeMillis());
                    this.init();
                }
            }
        ).bounds(centerX - effectiveButtonWidth / 2, scrollButtonY,
            effectiveButtonWidth / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2,
            SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        List<Identifier> filteredSpells = uiState.getFilteredSpells();
        Button scrollDownButton = Button.builder(
            Component.literal("▼"),
            button -> {
                int maxScroll = Math.max(0, filteredSpells.size() - maxVisibleSpells);
                if (uiState.getScrollOffset() < maxScroll) {
                    uiState.scrollDown(maxScroll);
                    animationState.startScrollAnimation(System.currentTimeMillis());
                    this.init();
                }
            }
        ).bounds(centerX + SpellSelectionScreenConstants.BUTTON_SPACING / 2, scrollButtonY,
            effectiveButtonWidth / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2,
            SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(scrollUpButton);
        this.addRenderableWidget(scrollDownButton);
        
        // Add spell buttons
        int currentY = SpellSelectionScreenConstants.START_Y_WITH_FILTERS;
        int startIndex = uiState.getScrollOffset();
        int endIndex = Math.min(startIndex + maxVisibleSpells, filteredSpells.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            if (currentY + SpellSelectionScreenConstants.BUTTON_HEIGHT > maxSpellButtonY) {
                break;
            }
            
            Identifier spellId = filteredSpells.get(i);
            // Skip null spell IDs to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            Spell spell = getSpell(spellId);
            if (spell == null) {
                continue;
            }
            
            final Identifier finalSpellId = spellId;
            final int buttonY = currentY;
            
            // Record button appearance for fade-in animation
            animationState.recordSpellButtonAppear(spellId, System.currentTimeMillis());
            
            uiState.getSpellButtonPositions().put(spellId, buttonY);
            
            boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
            boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
            boolean isFavorite = ClientSpellData.isFavorite(spellId);
            boolean isKeyboardSelected = (i - startIndex) == uiState.getKeyboardSelectedIndex();
            
            // Build button text
            StringBuilder buttonTextBuilder = new StringBuilder();
            if (isFavorite) {
                buttonTextBuilder.append("★ ");
            }
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
            } else if (isKeyboardSelected) {
                buttonComponent = buttonComponent.copy().withStyle(style -> style.withColor(0xFFFFFF).withBold(true));
            }
            
            Button spellButton = Button.builder(
                buttonComponent,
                button -> {
                    // Track for double-click detection
                    trackButtonClick(finalSpellId);
                    // Single click: assign spell
                    assignSpellToSlot(finalSpellId);
                }
            ).bounds(centerX - getEffectiveButtonWidth() / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4, buttonY,
                getEffectiveButtonWidth() - SpellUIConstants.ICON_SIZE_SCREEN - 4,
                SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(spellButton);
            spellButtons.add(spellButton);
            currentY += SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        }
        
        // Add clear slot button (using dynamically calculated position)
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
                SpellSelectionScreen newScreen = new SpellSelectionScreen(selectedSlot, uiState.getScrollOffset());
                newScreen.filterState.copyFrom(this.filterState);
                newScreen.updateFilteredSpells();
                this.minecraft.setScreen(newScreen);
            }
        ).bounds(centerX - getEffectiveButtonWidth() / 2, clearButtonY,
            getEffectiveButtonWidth(), SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(clearButton);
        
        // Add close button (using dynamically calculated position)
        Button closeButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - getEffectiveButtonWidth() / 2, closeButtonY,
            getEffectiveButtonWidth(), SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(closeButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        try {
            ClientSpellData.tickCooldowns();
        
        // Update animation states
        long currentTime = System.currentTimeMillis();
        Identifier hoveredSpellId = uiState.getHoveredSpellId();
        if (hoveredSpellId != null) {
            animationState.updateHover(hoveredSpellId, true, currentTime);
            animationState.showTooltip(currentTime);
        } else {
            animationState.hideTooltip(currentTime);
        }
        
        // Update hover states for all visible spells
        java.util.Set<Identifier> visibleSpells = CollectionFactory.createSetFrom(uiState.getSpellButtonPositions().keySet());
        for (Identifier spellId : visibleSpells) {
            // Skip null keys to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            if (!spellId.equals(hoveredSpellId)) {
                animationState.updateHover(spellId, false, currentTime);
            }
        }
        
        // Update selection glow for assigned spells
        int selectedSlot = uiState.getSelectedSlot();
        Identifier assignedSpellId = ClientSpellData.getSpellInSlot(selectedSlot);
        for (Identifier spellId : visibleSpells) {
            // Skip null keys to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            boolean isSelected = spellId.equals(assignedSpellId);
            animationState.updateSelectionGlow(spellId, isSelected, currentTime);
        }
        
        // Update preview panel
        previewPanel.setSpell(hoveredSpellId);
        
        SpellSelectionRenderer.renderBackground(guiGraphics, this.width, this.height);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        String slotName = SpellSlotManager.getSlotDisplayName(selectedSlot);
        Component titleText = Component.translatable("gui.spells_n_squares.selecting_slot", slotName);
        SpellSelectionRenderer.renderTitle(guiGraphics, this.font, titleText, this.width);
        
        // Render category tab highlights with enhanced visuals
        for (Map.Entry<SpellCategory, Button> entry : categoryButtons.entrySet()) {
            Button button = entry.getValue();
            boolean isSelected = entry.getKey() == filterState.getSelectedCategory();
            int color = entry.getKey().getColor();
            
            if (isSelected) {
                // Enhanced selected state with gradient and glow
                int highlightColor = 0x60 | ((color & 0xFFFFFF) << 8);
                guiGraphics.fill(button.getX(), button.getY(),
                    button.getX() + button.getWidth(), button.getY() + button.getHeight(),
                    highlightColor);
                
                // Border highlight
                int borderColor = 0xFF | ((color & 0xFFFFFF) << 8);
                guiGraphics.fill(button.getX(), button.getY(),
                    button.getX() + button.getWidth(), button.getY() + 1, borderColor); // Top
                guiGraphics.fill(button.getX(), button.getY() + button.getHeight() - 1,
                    button.getX() + button.getWidth(), button.getY() + button.getHeight(), borderColor); // Bottom
            }
        }
        
        // Render spell icons with category colors and animations
        int centerX = this.width / 2;
        SpellSelectionRenderer.renderSpellIcons(guiGraphics, this.width, centerX, uiState.getSpellButtonPositions(), selectedSlot, animationState);
        
        // Render category color strips and keyboard selection highlight on spell buttons with enhanced visuals
        List<Identifier> filteredSpells = uiState.getFilteredSpells();
        int effectiveButtonWidth = getEffectiveButtonWidth();
        for (Map.Entry<Identifier, Integer> entry : uiState.getSpellButtonPositions().entrySet()) {
            Identifier spellId = entry.getKey();
            // Skip null keys to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            int buttonY = entry.getValue();
            SpellCategory category = SpellCategory.fromSpellId(spellId);
            int color = category.getColor();
            
            // Get hover progress for this button
            float hoverProgress = animationState.getHoverProgress(spellId);
            
            // Enhanced category color strip with hover effect
            int stripX = centerX - effectiveButtonWidth / 2;
            int baseStripColor = color;
            int hoverStripColor = AnimationHelper.lerpColorEased(
                baseStripColor,
                0xFFFFFFFF, // Brighter on hover
                hoverProgress,
                AnimationHelper.Easing.SMOOTH
            );
            guiGraphics.fill(stripX, buttonY, stripX + 3, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, hoverStripColor);
            
            // Highlight keyboard-selected spell with enhanced glow
            int visibleIndex = filteredSpells.indexOf(spellId) - uiState.getScrollOffset();
            if (visibleIndex == uiState.getKeyboardSelectedIndex() && visibleIndex >= 0) {
                int buttonX = centerX - effectiveButtonWidth / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4;
                int buttonWidth = effectiveButtonWidth - SpellUIConstants.ICON_SIZE_SCREEN - 4;
                // Enhanced glow effect
                int glowColor = SpellUIConstants.GLOW_COLOR_SELECTED;
                guiGraphics.fill(buttonX - 2, buttonY - 1, buttonX + buttonWidth + 2, buttonY, glowColor); // Top glow
                guiGraphics.fill(buttonX - 2, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, 
                    buttonX + buttonWidth + 2, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT + 1, glowColor); // Bottom glow
                guiGraphics.fill(buttonX - 2, buttonY, buttonX - 1, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, glowColor); // Left glow
                guiGraphics.fill(buttonX + buttonWidth + 1, buttonY, buttonX + buttonWidth + 2, 
                    buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, glowColor); // Right glow
                guiGraphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, 0x40FFFFFF);
            } else if (hoverProgress > 0.01f) {
                // Hover highlight
                int buttonX = centerX - effectiveButtonWidth / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4;
                int buttonWidth = effectiveButtonWidth - SpellUIConstants.ICON_SIZE_SCREEN - 4;
                int hoverColor = ((int) (hoverProgress * 0.15f * 255) << 24) | 0xFFFFFF;
                guiGraphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT, hoverColor);
            }
        }
        
        // Render tooltip for hovered spell with fade animation
        if (hoveredSpellId != null) {
            float tooltipAlpha = animationState.getTooltipFadeProgress(currentTime);
            SpellTooltipRenderer.renderTooltip(
                guiGraphics, this.font, hoveredSpellId, uiState.getSelectedSlot(),
                mouseX, mouseY, this.width, this.height, tooltipAlpha
            );
        }
        
        // Render preview panel
        previewPanel.render(guiGraphics, this.font, this.width, this.height, uiState.getSelectedSlot(), partialTick);
        
        // Render help overlay if enabled
        if (showHelpOverlay) {
            renderHelpOverlay(guiGraphics);
        }
        
        // Clean up animation state for spells no longer visible
        animationState.cleanup(visibleSpells);
        } catch (Exception e) {
            LOGGER.error("Error rendering SpellSelectionScreen", e);
            // Still render the super class to show something
            super.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    
    /**
     * Renders the keyboard shortcuts help overlay.
     */
    private void renderHelpOverlay(GuiGraphics guiGraphics) {
        java.util.List<String> helpLines = CollectionFactory.createList();
        helpLines.add("§f§lKeyboard Shortcuts");
        helpLines.add("");
        helpLines.add("§7Navigation:");
        helpLines.add("  §e↑/↓ §7- Navigate spell list");
        helpLines.add("  §eHome/End §7- Jump to first/last");
        helpLines.add("  §e1-4 §7- Switch slots");
        helpLines.add("");
        helpLines.add("§7Actions:");
        helpLines.add("  §eEnter §7- Assign selected spell");
        helpLines.add("  §eF §7- Toggle favorite");
        helpLines.add("  §eEscape §7- Close screen");
        helpLines.add("  §eF1 §7- Toggle this help");
        
        int overlayWidth = 200;
        int overlayHeight = helpLines.size() * this.font.lineHeight + 16;
        int overlayX = 10;
        int overlayY = 10;
        
        // Draw background with shadow
        int shadowColor = 0x80000000;
        guiGraphics.fill(overlayX + 2, overlayY + 2, overlayX + overlayWidth + 2, overlayY + overlayHeight + 2, shadowColor);
        
        int bgColor = 0xF0101010;
        guiGraphics.fill(overlayX, overlayY, overlayX + overlayWidth, overlayY + overlayHeight, bgColor);
        
        // Draw border
        int borderColor = SpellUIConstants.BORDER_COLOR_ENHANCED;
        guiGraphics.fill(overlayX, overlayY, overlayX + overlayWidth, overlayY + 1, borderColor); // Top
        guiGraphics.fill(overlayX, overlayY + overlayHeight - 1, overlayX + overlayWidth, overlayY + overlayHeight, borderColor); // Bottom
        guiGraphics.fill(overlayX, overlayY, overlayX + 1, overlayY + overlayHeight, borderColor); // Left
        guiGraphics.fill(overlayX + overlayWidth - 1, overlayY, overlayX + overlayWidth, overlayY + overlayHeight, borderColor); // Right
        
        // Draw text
        int textY = overlayY + 8;
        for (String line : helpLines) {
            guiGraphics.drawString(this.font, line, overlayX + 8, textY, 0xFFFFFFFF, false);
            textY += this.font.lineHeight;
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        List<Identifier> filteredSpells = uiState.getFilteredSpells();
        int maxScroll = Math.max(0, filteredSpells.size() - getVisibleSpellCount());
        int currentScroll = uiState.getScrollOffset();
        int newScrollOffset = (int) Math.max(0, Math.min(maxScroll, currentScroll - deltaY));
        if (newScrollOffset != currentScroll) {
            uiState.setScrollOffset(newScrollOffset);
            this.init();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
    
    // Track button clicks for double-click detection
    private void trackButtonClick(Identifier spellId) {
        long currentTime = System.currentTimeMillis();
        if (spellId.equals(lastClickedSpellId) && 
            (currentTime - lastClickTime) < DOUBLE_CLICK_TIME_MS) {
            // Double-click detected - assign immediately
            assignSpellToSlot(spellId);
            lastClickedSpellId = null; // Reset
        } else {
            // Single click - track for double-click detection
            lastClickedSpellId = spellId;
            lastClickTime = currentTime;
        }
    }
    
    /**
     * Gets the spell button positions map (for event handler access).
     */
    public Map<Identifier, Integer> getSpellButtonPositions() {
        return uiState.getSpellButtonPositions();
    }
    
    /**
     * Rebuilds only the spell buttons section without rebuilding the entire screen.
     * This is more efficient than calling init() for filter changes.
     */
    private void rebuildSpellButtons() {
        // Remove existing spell buttons
        for (Button button : spellButtons) {
            this.removeWidget(button);
        }
        spellButtons.clear();
        
        // Clear button positions
        uiState.clearSpellButtonPositions();
        
        // Rebuild spell buttons (reuse the logic from init())
        int maxVisibleSpells = getVisibleSpellCount();
        // Calculate bottom button positions dynamically (same as in init())
        int closeButtonY = this.height - 5;
        int clearButtonY = closeButtonY - SpellSelectionScreenConstants.BUTTON_HEIGHT - SpellSelectionScreenConstants.BUTTON_SPACING;
        int scrollButtonY = clearButtonY - SpellSelectionScreenConstants.BUTTON_HEIGHT - SpellSelectionScreenConstants.BUTTON_SPACING;
        int maxSpellButtonY = scrollButtonY - SpellSelectionScreenConstants.BUTTON_SPACING;
        int currentY = SpellSelectionScreenConstants.START_Y_WITH_FILTERS;
        int startIndex = uiState.getScrollOffset();
        List<Identifier> filteredSpells = uiState.getFilteredSpells();
        int endIndex = Math.min(startIndex + maxVisibleSpells, filteredSpells.size());
        int selectedSlot = uiState.getSelectedSlot();
        int centerX = this.width / 2;
        
        for (int i = startIndex; i < endIndex; i++) {
            if (currentY + SpellSelectionScreenConstants.BUTTON_HEIGHT > maxSpellButtonY) {
                break;
            }
            
            Identifier spellId = filteredSpells.get(i);
            // Skip null spell IDs to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            Spell spell = getSpell(spellId);
            if (spell == null) {
                continue;
            }
            
            final Identifier finalSpellId = spellId;
            final int buttonY = currentY;
            
            // Record button appearance for fade-in animation
            animationState.recordSpellButtonAppear(spellId, System.currentTimeMillis());
            
            uiState.getSpellButtonPositions().put(spellId, buttonY);
            
            boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
            boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
            boolean isFavorite = ClientSpellData.isFavorite(spellId);
            boolean isKeyboardSelected = (i - startIndex) == uiState.getKeyboardSelectedIndex();
            
            // Build button text
            StringBuilder buttonTextBuilder = new StringBuilder();
            if (isFavorite) {
                buttonTextBuilder.append("★ ");
            }
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
            } else if (isKeyboardSelected) {
                buttonComponent = buttonComponent.copy().withStyle(style -> style.withColor(0xFFFFFF).withBold(true));
            }
            
            Button spellButton = Button.builder(
                buttonComponent,
                button -> {
                    // Track for double-click detection
                    trackButtonClick(finalSpellId);
                    // Single click: assign spell
                    assignSpellToSlot(finalSpellId);
                }
            ).bounds(centerX - getEffectiveButtonWidth() / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4, buttonY,
                getEffectiveButtonWidth() - SpellUIConstants.ICON_SIZE_SCREEN - 4,
                SpellSelectionScreenConstants.BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(spellButton);
            currentY += SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        }
    }
    
    /**
     * Handles right-click on spell buttons (toggle favorite).
     */
    public void handleSpellRightClick(Identifier spellId) {
        ClientSpellData.toggleFavorite(spellId);
        updateFilteredSpells();
        this.init();
    }
    
    /**
     * Gets the spell ID at the given mouse position.
     */
    @Nullable
    private Identifier getSpellAtPosition(double mouseX, double mouseY) {
        int centerX = this.width / 2;
        int effectiveButtonWidth = getEffectiveButtonWidth();
        int buttonX = centerX - effectiveButtonWidth / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4;
        int buttonWidth = effectiveButtonWidth - SpellUIConstants.ICON_SIZE_SCREEN - 4;
        
        for (Map.Entry<Identifier, Integer> entry : uiState.getSpellButtonPositions().entrySet()) {
            Identifier spellId = entry.getKey();
            // Skip null keys to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            int buttonY = entry.getValue();
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT) {
                return spellId;
            }
        }
        return null;
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        
        // Check if mouse is over a spell button
        uiState.setHoveredSpellId(null);
        int centerX = this.width / 2;
        int effectiveButtonWidth = getEffectiveButtonWidth();
        int buttonX = centerX - effectiveButtonWidth / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4;
        int buttonWidth = effectiveButtonWidth - SpellUIConstants.ICON_SIZE_SCREEN - 4;
        
        for (Map.Entry<Identifier, Integer> entry : uiState.getSpellButtonPositions().entrySet()) {
            Identifier spellId = entry.getKey();
            // Skip null keys to prevent NullPointerException
            if (spellId == null) {
                continue;
            }
            int buttonY = entry.getValue();
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT) {
                uiState.setHoveredSpellId(spellId);
                break;
            }
        }
    }
    
    /**
     * Gets the effective button width, ensuring it fits on screen.
     */
    private int getEffectiveButtonWidth() {
        int minButtonWidth = 150; // Minimum width for usability
        int maxButtonWidth = SpellSelectionScreenConstants.BUTTON_WIDTH;
        int padding = 20; // Padding on each side
        int availableWidth = this.width - padding * 2;
        return Math.max(minButtonWidth, Math.min(maxButtonWidth, availableWidth));
    }
    
    /**
     * Calculates how many spell buttons can fit on screen.
     */
    private int getVisibleSpellCount() {
        if (this.height <= 0) {
            return 1; // Safe default
        }
        int buttonHeightWithSpacing = SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        if (buttonHeightWithSpacing <= 0) {
            return 1; // Prevent division by zero
        }
        
        // Calculate available height more accurately
        int topSpace = SpellSelectionScreenConstants.START_Y_WITH_FILTERS;
        // Reserve space for bottom buttons: scroll (20) + spacing (4) + clear (20) + spacing (4) + close (20) + padding (5)
        int bottomSpace = SpellSelectionScreenConstants.BUTTON_HEIGHT * 3 
            + SpellSelectionScreenConstants.BUTTON_SPACING * 2 
            + 5; // Small padding at bottom
        
        int availableHeight = this.height - topSpace - bottomSpace;
        
        // Ensure we have at least some space
        if (availableHeight < buttonHeightWithSpacing) {
            return 1; // At least show one button
        }
        
        return Math.max(1, availableHeight / buttonHeightWithSpacing);
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
    
    /**
     * Handles slot switching from keyboard.
     */
    public void handleSlotSwitch(int slot) {
        uiState.setSelectedSlot(slot);
        uiState.setScrollOffset(0);
        uiState.resetKeyboardSelection();
        this.init();
    }
    
    /**
     * Toggles the help overlay.
     */
    public void toggleHelpOverlay() {
        showHelpOverlay = !showHelpOverlay;
    }
    
    /**
     * Handles keyboard navigation and shortcuts.
     * Called from event handler.
     * @return true if the key was handled
     */
    public boolean handleKeyboardNavigation(int keyCode) {
        // Arrow keys: Navigate spell list
        int maxVisibleSpells = getVisibleSpellCount();
        List<Identifier> filteredSpells = uiState.getFilteredSpells();
        int startIndex = uiState.getScrollOffset();
        int endIndex = Math.min(startIndex + maxVisibleSpells, filteredSpells.size());
        int maxIndex = endIndex - startIndex - 1;
        int keyboardSelectedIndex = uiState.getKeyboardSelectedIndex();
        
        if (keyCode == GLFW.GLFW_KEY_UP) { // Up arrow
            if (keyboardSelectedIndex < 0) {
                uiState.setKeyboardSelectedIndex(0);
            } else if (keyboardSelectedIndex > 0) {
                uiState.setKeyboardSelectedIndex(keyboardSelectedIndex - 1);
            } else if (startIndex > 0) {
                uiState.scrollUp();
                this.init();
            }
            updatePreviewFromKeyboardSelection();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) { // Down arrow
            if (keyboardSelectedIndex < 0) {
                uiState.setKeyboardSelectedIndex(0);
            } else if (keyboardSelectedIndex < maxIndex) {
                uiState.setKeyboardSelectedIndex(keyboardSelectedIndex + 1);
            } else {
                int maxScroll = Math.max(0, filteredSpells.size() - maxVisibleSpells);
                if (startIndex < maxScroll) {
                    uiState.scrollDown(maxScroll);
                    uiState.setKeyboardSelectedIndex(maxIndex);
                    this.init();
                }
            }
            updatePreviewFromKeyboardSelection();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_HOME) { // Home
            uiState.setScrollOffset(0);
            uiState.setKeyboardSelectedIndex(0);
            this.init();
            updatePreviewFromKeyboardSelection();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_END) { // End
            int maxScroll = Math.max(0, filteredSpells.size() - maxVisibleSpells);
            uiState.setScrollOffset(maxScroll);
            uiState.setKeyboardSelectedIndex(Math.min(maxIndex, filteredSpells.size() - 1 - maxScroll));
            this.init();
            updatePreviewFromKeyboardSelection();
            return true;
        }
        
        // Enter: Assign selected spell
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (keyboardSelectedIndex >= 0 && keyboardSelectedIndex < endIndex - startIndex) {
                int selectedIndex = startIndex + keyboardSelectedIndex;
                if (selectedIndex < filteredSpells.size()) {
                    Identifier spellId = filteredSpells.get(selectedIndex);
                    assignSpellToSlot(spellId);
                    return true;
                }
            }
        }
        
        // F: Toggle favorite
        if (keyCode == GLFW.GLFW_KEY_F) {
            Identifier targetSpellId = getTargetSpellForAction();
            if (targetSpellId != null) {
                ClientSpellData.toggleFavorite(targetSpellId);
                updateFilteredSpells();
                this.init();
                return true;
            }
        }
        
        // Escape: Close screen
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (this.minecraft != null) {
                this.minecraft.setScreen(null);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets the target spell for keyboard actions (either keyboard-selected or hovered).
     */
    @Nullable
    private Identifier getTargetSpellForAction() {
        int keyboardSelectedIndex = uiState.getKeyboardSelectedIndex();
        if (keyboardSelectedIndex >= 0) {
            int startIndex = uiState.getScrollOffset();
            List<Identifier> filteredSpells = uiState.getFilteredSpells();
            int selectedIndex = startIndex + keyboardSelectedIndex;
            if (selectedIndex >= 0 && selectedIndex < filteredSpells.size()) {
                return filteredSpells.get(selectedIndex);
            }
        }
        return uiState.getHoveredSpellId();
    }
    
    /**
     * Updates preview panel from keyboard selection.
     */
    private void updatePreviewFromKeyboardSelection() {
        Identifier targetSpellId = getTargetSpellForAction();
        previewPanel.setSpell(targetSpellId);
    }
    
    /**
     * Assigns a spell to the current slot.
     */
    private void assignSpellToSlot(Identifier spellId) {
        try {
            int selectedSlot = uiState.getSelectedSlot();
            at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload payload =
                new at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload(
                    selectedSlot,
                    java.util.Optional.of(spellId)
                );
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
            
            ClientSpellData.setSpellInSlot(selectedSlot, spellId);
            
            if (this.minecraft != null) {
                this.minecraft.execute(() -> {
                    SpellSelectionScreen newScreen = new SpellSelectionScreen(selectedSlot, uiState.getScrollOffset());
                    // Preserve filter state
                    newScreen.filterState.copyFrom(this.filterState);
                    newScreen.updateFilteredSpells();
                    this.minecraft.setScreen(newScreen);
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error assigning spell to slot", e);
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
}

