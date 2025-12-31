package at.koopro.spells_n_squares.features.education.client;

import at.koopro.spells_n_squares.features.education.BestiaryCreatureRegistry;
import at.koopro.spells_n_squares.features.education.BestiaryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side GUI screen for the bestiary.
 * Displays a comprehensive catalog of all magical creatures from the Wizarding World.
 */
public class BestiaryScreen extends AbstractContainerScreen<BestiaryMenu> {
    // Book layout constants
    private static final int BOOK_WIDTH = 512;
    private static final int BOOK_HEIGHT = 332;
    private static final int LEFT_PAGE_X = 30;
    private static final int RIGHT_PAGE_X = 280;
    private static final int PAGE_Y = 50;
    private static final int PAGE_WIDTH = 240;
    private static final int PAGE_HEIGHT = 250;
    
    // List panel (left page)
    private static final int LIST_PANEL_WIDTH = PAGE_WIDTH - 10;
    private static final int CREATURE_BUTTON_HEIGHT = 18;
    private static final int SEARCH_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 2;
    
    // Detail panel (right page)
    private static final int DETAIL_PANEL_X = RIGHT_PAGE_X;
    private static final int DETAIL_PANEL_WIDTH = PAGE_WIDTH - 10;
    
    private BestiaryCreatureRegistry.CreatureCategory selectedCategory = null;
    private BestiaryCreatureRegistry.CreatureEntry selectedCreature = null;
    private String searchQuery = "";
    private int scrollOffset = 0;
    private EditBox searchBox;
    private List<BestiaryCreatureRegistry.CreatureEntry> filteredCreatures = new ArrayList<>();
    
    public BestiaryScreen(BestiaryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BOOK_WIDTH;
        this.imageHeight = BOOK_HEIGHT;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Ensure positions are calculated
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        int leftX = this.leftPos;
        int topY = this.topPos;
        
        // Initialize creature registry if not already done
        BestiaryCreatureRegistry.initialize();
        
        // Ensure creatures are loaded
        int creatureCount = BestiaryCreatureRegistry.getAllCreatures().size();
        if (creatureCount == 0) {
            BestiaryCreatureRegistry.initialize();
        }
        
        // Create search box (on left page)
        this.searchBox = new EditBox(this.font, leftX + LEFT_PAGE_X + 5, topY + PAGE_Y + 5, LIST_PANEL_WIDTH - 10, SEARCH_HEIGHT, 
            Component.translatable("gui.spells_n_squares.bestiary.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setValue(""); // Initialize empty
        this.searchBox.setResponder(query -> {
            String newQuery = query != null ? query : "";
            if (!newQuery.equals(this.searchQuery)) {
                this.searchQuery = newQuery;
                updateFilteredCreatures();
            }
        });
        // Don't auto-focus - let user click to focus
        this.addRenderableWidget(this.searchBox);
        
        // Create category filter buttons (on left page)
        int categoryY = topY + PAGE_Y + SEARCH_HEIGHT + 10;
        int categoryButtonWidth = (LIST_PANEL_WIDTH - 10) / 2;
        
        BestiaryCreatureRegistry.CreatureCategory[] categories = BestiaryCreatureRegistry.CreatureCategory.values();
        for (int i = 0; i < categories.length; i++) {
            BestiaryCreatureRegistry.CreatureCategory category = categories[i];
            int x = leftX + LEFT_PAGE_X + 5 + (i % 2) * categoryButtonWidth;
            int y = categoryY + (i / 2) * (BUTTON_HEIGHT + BUTTON_SPACING);
            
            final BestiaryCreatureRegistry.CreatureCategory cat = category;
            Button categoryButton = Button.builder(
                Component.literal(getCategoryName(category)),
                button -> {
                    if (selectedCategory == cat) {
                        selectedCategory = null; // Deselect
                    } else {
                        selectedCategory = cat;
                    }
                    updateFilteredCreatures();
                }
            ).bounds(x, y, categoryButtonWidth - 2, BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(categoryButton);
        }
        
        // Create scroll buttons (on left page, bottom)
        int scrollY = topY + PAGE_Y + PAGE_HEIGHT - 50;
        Button scrollUp = Button.builder(
            Component.literal("▲"),
            button -> {
                if (scrollOffset > 0) {
                    scrollOffset--;
                }
            }
        ).bounds(leftX + LEFT_PAGE_X + 5, scrollY, LIST_PANEL_WIDTH - 10, BUTTON_HEIGHT).build();
        
        Button scrollDown = Button.builder(
            Component.literal("▼"),
            button -> {
                int maxScroll = Math.max(0, filteredCreatures.size() - getVisibleCreatureCount());
                if (scrollOffset < maxScroll) {
                    scrollOffset++;
                }
            }
        ).bounds(leftX + LEFT_PAGE_X + 5, scrollY + BUTTON_HEIGHT + 2, LIST_PANEL_WIDTH - 10, BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(scrollUp);
        this.addRenderableWidget(scrollDown);
        
        // Close button (top right)
        Button closeButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(leftX + this.imageWidth - 70, topY + 15, 60, BUTTON_HEIGHT).build();
        this.addRenderableWidget(closeButton);
        
        updateFilteredCreatures();
    }
    
    private String getCategoryName(BestiaryCreatureRegistry.CreatureCategory category) {
        return switch (category) {
            case COMPANION -> "Companion";
            case MOUNT -> "Mount";
            case HOSTILE -> "Hostile";
            case NEUTRAL -> "Neutral";
            case AQUATIC -> "Aquatic";
            case SPIRITUAL -> "Spiritual";
        };
    }
    
    private void updateFilteredCreatures() {
        filteredCreatures.clear();
        
        // Ensure registry is initialized
        BestiaryCreatureRegistry.initialize();
        
        List<BestiaryCreatureRegistry.CreatureEntry> allCreatures = 
            new ArrayList<>(BestiaryCreatureRegistry.getAllCreatures());
        
        // Get current search query - always read from searchBox to ensure it's up to date
        String currentSearch = searchQuery;
        if (searchBox != null) {
            String boxValue = searchBox.getValue();
            if (boxValue != null) {
                currentSearch = boxValue;
                // Sync the stored value
                if (!currentSearch.equals(searchQuery)) {
                    searchQuery = currentSearch;
                }
            }
        }
        
        // Filter by category
        if (selectedCategory != null) {
            allCreatures.removeIf(entry -> entry.getCategory() != selectedCategory);
        }
        
        // Filter by search query
        if (currentSearch != null && !currentSearch.isEmpty()) {
            String lowerQuery = currentSearch.toLowerCase().trim();
            allCreatures.removeIf(entry -> {
                String name = entry.getName() != null ? entry.getName().toLowerCase() : "";
                String desc = entry.getDescription() != null ? entry.getDescription().toLowerCase() : "";
                boolean matches = name.contains(lowerQuery) || desc.contains(lowerQuery);
                return !matches;
            });
        }
        
        filteredCreatures.addAll(allCreatures);
        scrollOffset = Math.min(scrollOffset, Math.max(0, filteredCreatures.size() - getVisibleCreatureCount()));
        
        System.out.println("[BestiaryScreen] Final filtered count: " + filteredCreatures.size());
        
        // Auto-select first creature if none selected or if selected creature is no longer in list
        if (filteredCreatures.isEmpty()) {
            selectedCreature = null;
        } else if (selectedCreature == null || !filteredCreatures.contains(selectedCreature)) {
            selectedCreature = filteredCreatures.get(0);
        }
    }
    
    private int getVisibleCreatureCount() {
        // Calculate available height for creature list
        int categoryButtonsHeight = (BestiaryCreatureRegistry.CreatureCategory.values().length + 1) / 2 * (BUTTON_HEIGHT + BUTTON_SPACING);
        int listStartY = PAGE_Y + SEARCH_HEIGHT + 10 + categoryButtonsHeight + 10;
        int availableHeight = PAGE_HEIGHT - (listStartY - PAGE_Y) - 50; // Account for scroll buttons and debug info
        return Math.max(1, availableHeight / (CREATURE_BUTTON_HEIGHT + 2));
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int leftX = this.leftPos;
        int topY = this.topPos;
        
        // Render book background (parchment/paper color)
        int bookBgColor = 0xFFF4E6D0; // Parchment color
        guiGraphics.fill(leftX, topY, leftX + this.imageWidth, topY + this.imageHeight, bookBgColor);
        
        // Book border (decorative)
        int borderColor = 0xFF8B6F47; // Brown border
        guiGraphics.fill(leftX + 10, topY + 10, leftX + this.imageWidth - 10, topY + 12, borderColor); // Top border
        guiGraphics.fill(leftX + 10, topY + this.imageHeight - 12, leftX + this.imageWidth - 10, topY + this.imageHeight - 10, borderColor); // Bottom border
        guiGraphics.fill(leftX + 10, topY + 10, leftX + 12, topY + this.imageHeight - 10, borderColor); // Left border
        guiGraphics.fill(leftX + this.imageWidth - 12, topY + 10, leftX + this.imageWidth - 10, topY + this.imageHeight - 10, borderColor); // Right border
        
        // Book binding (center spine)
        int spineX = leftX + this.imageWidth / 2 - 2;
        guiGraphics.fill(spineX, topY + 10, spineX + 4, topY + this.imageHeight - 10, 0xFF6B4E37); // Dark brown spine
        
        // Left page background (slightly darker parchment)
        int leftPageColor = 0xFFF0E0C8;
        guiGraphics.fill(leftX + LEFT_PAGE_X, topY + PAGE_Y, leftX + LEFT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + PAGE_HEIGHT, leftPageColor);
        
        // Right page background
        int rightPageColor = 0xFFF0E0C8;
        guiGraphics.fill(leftX + RIGHT_PAGE_X, topY + PAGE_Y, leftX + RIGHT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + PAGE_HEIGHT, rightPageColor);
        
        // Page borders (subtle lines)
        int pageBorderColor = 0xFFD4C4A8;
        // Left page border
        guiGraphics.fill(leftX + LEFT_PAGE_X, topY + PAGE_Y, leftX + LEFT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + 1, pageBorderColor);
        guiGraphics.fill(leftX + LEFT_PAGE_X, topY + PAGE_Y + PAGE_HEIGHT - 1, leftX + LEFT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + PAGE_HEIGHT, pageBorderColor);
        guiGraphics.fill(leftX + LEFT_PAGE_X, topY + PAGE_Y, leftX + LEFT_PAGE_X + 1, topY + PAGE_Y + PAGE_HEIGHT, pageBorderColor);
        guiGraphics.fill(leftX + LEFT_PAGE_X + PAGE_WIDTH - 1, topY + PAGE_Y, leftX + LEFT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + PAGE_HEIGHT, pageBorderColor);
        
        // Right page border
        guiGraphics.fill(leftX + RIGHT_PAGE_X, topY + PAGE_Y, leftX + RIGHT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + 1, pageBorderColor);
        guiGraphics.fill(leftX + RIGHT_PAGE_X, topY + PAGE_Y + PAGE_HEIGHT - 1, leftX + RIGHT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + PAGE_HEIGHT, pageBorderColor);
        guiGraphics.fill(leftX + RIGHT_PAGE_X, topY + PAGE_Y, leftX + RIGHT_PAGE_X + 1, topY + PAGE_Y + PAGE_HEIGHT, pageBorderColor);
        guiGraphics.fill(leftX + RIGHT_PAGE_X + PAGE_WIDTH - 1, topY + PAGE_Y, leftX + RIGHT_PAGE_X + PAGE_WIDTH, topY + PAGE_Y + PAGE_HEIGHT, pageBorderColor);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Recalculate positions to ensure they're correct (screen might have been resized)
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Ensure filtered creatures list is populated
        if (filteredCreatures.isEmpty()) {
            System.out.println("[BestiaryScreen] render: filteredCreatures is empty, calling updateFilteredCreatures()");
            updateFilteredCreatures();
            System.out.println("[BestiaryScreen] render: After updateFilteredCreatures(), size = " + filteredCreatures.size());
        }
        
        int leftX = this.leftPos;
        int topY = this.topPos;
        
        // Debug: Log screen dimensions and positions
        System.out.println("[BestiaryScreen] render: width=" + this.width + ", height=" + this.height + 
            ", imageWidth=" + this.imageWidth + ", imageHeight=" + this.imageHeight + 
            ", leftPos=" + leftX + ", topPos=" + topY);
        
        // Draw title (centered at top of book) - use dark brown with shadow for visibility
        int titleX = leftX + this.imageWidth / 2;
        int titleY = topY + 20;
        guiGraphics.drawString(this.font, this.title, titleX - this.font.width(this.title) / 2, titleY, 0x2D1817, true);
        
        // Draw creature list (on left page)
        // Calculate list start position - after search box and category buttons
        int categoryButtonsHeight = (BestiaryCreatureRegistry.CreatureCategory.values().length + 1) / 2 * (BUTTON_HEIGHT + BUTTON_SPACING);
        int listStartY = topY + PAGE_Y + SEARCH_HEIGHT + 10 + categoryButtonsHeight + 10;
        
        // Debug: Always show debug info
        int totalCreatures = BestiaryCreatureRegistry.getAllCreatures().size();
        guiGraphics.drawString(this.font, "Total: " + totalCreatures + ", Filtered: " + filteredCreatures.size(), 
            leftX + LEFT_PAGE_X + 10, topY + PAGE_Y + PAGE_HEIGHT - 35, 0x2D1817, true);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            guiGraphics.drawString(this.font, "Search: '" + searchQuery + "'", leftX + LEFT_PAGE_X + 10, topY + PAGE_Y + PAGE_HEIGHT - 20, 0x2D1817, true);
        }
        
        int visibleCount = getVisibleCreatureCount();
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + visibleCount, filteredCreatures.size());
        
        // Debug: Check filtered creatures
        if (filteredCreatures.isEmpty()) {
            if (totalCreatures == 0) {
                guiGraphics.drawString(this.font, "No creatures loaded!", leftX + LEFT_PAGE_X + 10, listStartY, 0xFF0000, true);
            } else {
                guiGraphics.drawString(this.font, "No creatures match filter", leftX + LEFT_PAGE_X + 10, listStartY, 0xFF0000, true);
                guiGraphics.drawString(this.font, "Try clearing search/category filters", leftX + LEFT_PAGE_X + 10, listStartY + 15, 0xFF0000, true);
            }
        }
        
        // Debug: Log list state before rendering
        System.out.println("[BestiaryScreen] render: Before rendering loop, filteredCreatures.size() = " + filteredCreatures.size() + 
            ", startIndex = " + startIndex + ", endIndex = " + endIndex + ", listStartY = " + listStartY);
        
        // Render creature list
        if (!filteredCreatures.isEmpty()) {
            System.out.println("[BestiaryScreen] render: Rendering " + (endIndex - startIndex) + " creatures (indices " + startIndex + " to " + endIndex + ")");
            for (int i = startIndex; i < endIndex; i++) {
                BestiaryCreatureRegistry.CreatureEntry creature = filteredCreatures.get(i);
                int y = listStartY + (i - startIndex) * (CREATURE_BUTTON_HEIGHT + 2);
                
                // For now, all creatures are always discovered (discovery mechanism disabled)
                boolean isDiscovered = true; // Always show all creatures
                boolean isSelected = selectedCreature != null && selectedCreature.getId().equals(creature.getId());
                
                // Draw creature button background (parchment with selection highlight)
                int bgColor = isSelected ? 0xFFE8D4B0 : (isDiscovered ? 0xFFF0E0C8 : 0xFFD4C4A8);
                int borderColor = isSelected ? 0xFF8B6F47 : 0xFFC4B494;
                guiGraphics.fill(leftX + LEFT_PAGE_X + 5, y, leftX + LEFT_PAGE_X + LIST_PANEL_WIDTH - 5, y + CREATURE_BUTTON_HEIGHT, bgColor);
                guiGraphics.fill(leftX + LEFT_PAGE_X + 5, y, leftX + LEFT_PAGE_X + LIST_PANEL_WIDTH - 5, y + 1, borderColor);
                guiGraphics.fill(leftX + LEFT_PAGE_X + 5, y + CREATURE_BUTTON_HEIGHT - 1, leftX + LEFT_PAGE_X + LIST_PANEL_WIDTH - 5, y + CREATURE_BUTTON_HEIGHT, borderColor);
                
                // Draw creature name - use very dark brown text for maximum visibility on parchment
                String displayName = creature.getName() != null ? creature.getName() : "Unknown";
                // Use very dark brown (almost black) for maximum contrast on light parchment background
                int textColor = 0x2D1817; // Very dark brown
                int textX = leftX + LEFT_PAGE_X + 8;
                int textY = y + (CREATURE_BUTTON_HEIGHT - this.font.lineHeight) / 2; // Center vertically
                
                // Draw text with explicit shadow for visibility
                guiGraphics.drawString(this.font, displayName, textX, textY, textColor, true); // true = shadow
                
                // Debug: Log first creature name to verify rendering
                if (i == startIndex) {
                    System.out.println("[BestiaryScreen] Rendering creature '" + displayName + "' at (" + textX + ", " + textY + ") with color " + Integer.toHexString(textColor) + 
                        ", screen coords: leftX=" + leftX + ", topY=" + topY + ", listStartY=" + listStartY);
                }
                
                // Draw clickable area (hover highlight)
                if (mouseX >= leftX + LEFT_PAGE_X + 5 && mouseX < leftX + LEFT_PAGE_X + LIST_PANEL_WIDTH - 5 &&
                    mouseY >= y && mouseY < y + CREATURE_BUTTON_HEIGHT) {
                    guiGraphics.fill(leftX + LEFT_PAGE_X + 5, y, leftX + LEFT_PAGE_X + LIST_PANEL_WIDTH - 5, y + CREATURE_BUTTON_HEIGHT, 0x40FFFFFF);
                }
            }
        }
        
        // Draw detail panel (on right page)
        if (selectedCreature != null) {
            renderCreatureDetails(guiGraphics, leftX + DETAIL_PANEL_X + 10, topY + PAGE_Y + 10, 
                selectedCreature, mouseX, mouseY);
        } else if (!filteredCreatures.isEmpty()) {
            // Show message to select a creature
            guiGraphics.drawString(this.font, "Select a creature from", leftX + DETAIL_PANEL_X + 10, topY + PAGE_Y + 20, 0x2D1817, true);
            guiGraphics.drawString(this.font, "the list to view details", leftX + DETAIL_PANEL_X + 10, topY + PAGE_Y + 35, 0x2D1817, true);
        } else {
            guiGraphics.drawString(this.font, "No creatures available", leftX + DETAIL_PANEL_X + 10, topY + PAGE_Y + 20, 0x2D1817, true);
        }
        
        // Draw creature count (bottom of book)
        // TODO: Show discovery count when discovery mechanism is implemented
        int totalCount = BestiaryCreatureRegistry.getAllCreatures().size();
        String discoveryText = String.format("Total Creatures: %d", totalCount);
        int countX = leftX + this.imageWidth / 2;
        int countY = topY + this.imageHeight - 25;
        guiGraphics.drawString(this.font, discoveryText, countX - this.font.width(discoveryText) / 2, countY, 0x2D1817, true);
    }
    
    private void renderCreatureDetails(GuiGraphics guiGraphics, int x, int y, 
                                     BestiaryCreatureRegistry.CreatureEntry creature,
                                     int mouseX, int mouseY) {
        // Discovery mechanism is temporarily disabled - always show all creature details
        int currentY = y;
        
        // Creature name (title style) - with shadow for visibility
        String name = creature.getName() != null ? creature.getName() : "Unknown";
        guiGraphics.drawString(this.font, name, x, currentY, 0x2D1817, true); // Dark brown with shadow
        currentY += this.font.lineHeight + 8;
        
        // Decorative line under title
        guiGraphics.fill(x, currentY - 2, x + DETAIL_PANEL_WIDTH - 20, currentY - 1, 0xFF8B6F47);
        currentY += 5;
        
        // Category (with label) - with shadow for visibility
        guiGraphics.drawString(this.font, "Category:", x, currentY, 0x2D1817, true); // Dark brown with shadow
        guiGraphics.drawString(this.font, getCategoryName(creature.getCategory()), x + 70, currentY, 0x2D1817, true); // Dark brown with shadow
        currentY += this.font.lineHeight + 6;
        
        // Description - with shadow for visibility
        guiGraphics.drawString(this.font, "Description:", x, currentY, 0x2D1817, true); // Dark brown with shadow
        currentY += this.font.lineHeight + 3;
        drawWrappedText(guiGraphics, creature.getDescription(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
        currentY += getWrappedTextHeight(creature.getDescription(), DETAIL_PANEL_WIDTH - 20) + 6;
        
        // Habitat
        if (creature.getHabitat() != null && !creature.getHabitat().isEmpty()) {
            guiGraphics.drawString(this.font, "Habitat:", x, currentY, 0x2D1817, true); // Dark brown with shadow
            currentY += this.font.lineHeight + 3;
            drawWrappedText(guiGraphics, creature.getHabitat(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
            currentY += getWrappedTextHeight(creature.getHabitat(), DETAIL_PANEL_WIDTH - 20) + 6;
        }
        
        // Behavior
        if (creature.getBehavior() != null && !creature.getBehavior().isEmpty()) {
            guiGraphics.drawString(this.font, "Behavior:", x, currentY, 0x2D1817, true); // Dark brown with shadow
            currentY += this.font.lineHeight + 3;
            drawWrappedText(guiGraphics, creature.getBehavior(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
            currentY += getWrappedTextHeight(creature.getBehavior(), DETAIL_PANEL_WIDTH - 20) + 6;
        }
        
        // Abilities
        if (creature.getAbilities() != null && !creature.getAbilities().isEmpty()) {
            guiGraphics.drawString(this.font, "Abilities:", x, currentY, 0x2D1817, true); // Dark brown with shadow
            currentY += this.font.lineHeight + 3;
            drawWrappedText(guiGraphics, creature.getAbilities(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
            currentY += getWrappedTextHeight(creature.getAbilities(), DETAIL_PANEL_WIDTH - 20) + 6;
        }
        
        // Stats
        if (creature.getStats() != null && !creature.getStats().isEmpty()) {
            guiGraphics.drawString(this.font, "Stats:", x, currentY, 0x2D1817, true); // Dark brown with shadow
            currentY += this.font.lineHeight + 3;
            drawWrappedText(guiGraphics, creature.getStats(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
            currentY += getWrappedTextHeight(creature.getStats(), DETAIL_PANEL_WIDTH - 20) + 6;
        }
        
        // Taming
        if (creature.getTaming() != null && !creature.getTaming().isEmpty()) {
            guiGraphics.drawString(this.font, "Taming:", x, currentY, 0x2D1817, true); // Dark brown with shadow
            currentY += this.font.lineHeight + 3;
            drawWrappedText(guiGraphics, creature.getTaming(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
            currentY += getWrappedTextHeight(creature.getTaming(), DETAIL_PANEL_WIDTH - 20) + 6;
        }
        
        // Lore (italic style section)
        if (creature.getLore() != null && !creature.getLore().isEmpty()) {
            guiGraphics.drawString(this.font, "Lore:", x, currentY, 0x2D1817, true); // Dark brown with shadow
            currentY += this.font.lineHeight + 3;
            drawWrappedText(guiGraphics, creature.getLore(), x, currentY, DETAIL_PANEL_WIDTH - 20, 0x2D1817, true); // Dark brown with shadow
        }
        
        // Implementation status
        if (!creature.isImplemented()) {
            currentY += this.font.lineHeight + 6;
            guiGraphics.drawString(this.font, "Note: This creature is not yet implemented in the mod.", 
                x, currentY, 0xCC6600, true); // Orange with shadow
        }
    }
    
    private void drawWrappedText(GuiGraphics guiGraphics, String text, int x, int y, int maxWidth, int color, boolean shadow) {
        List<String> lines = wrapText(text, maxWidth);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(this.font, lines.get(i), x, y + i * this.font.lineHeight, color, shadow);
        }
    }
    
    private int getWrappedTextHeight(String text, int maxWidth) {
        return wrapText(text, maxWidth).size() * this.font.lineHeight;
    }
    
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            int testWidth = this.font.width(testLine);
            
            if (testWidth > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.isEmpty() ? List.of(text) : lines;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        // Scroll on left page (creature list)
        if (mouseX >= this.leftPos + LEFT_PAGE_X && mouseX < this.leftPos + LEFT_PAGE_X + LIST_PANEL_WIDTH) {
            int maxScroll = Math.max(0, filteredCreatures.size() - getVisibleCreatureCount());
            scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - deltaY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    /**
     * Checks if the search box is focused (for event handler).
     */
    public boolean isSearchBoxFocused() {
        return this.searchBox != null && this.searchBox.isFocused();
    }
    
    /**
     * Handles mouse clicks on creature list items.
     * Called from event handler.
     */
    public void handleCreatureListClick(double mouseX, double mouseY) {
        if (filteredCreatures.isEmpty()) {
            return;
        }
        
        int leftX = this.leftPos;
        int topY = this.topPos;
        // Calculate list start position - same as in render method
        int categoryButtonsHeight = (BestiaryCreatureRegistry.CreatureCategory.values().length + 1) / 2 * (BUTTON_HEIGHT + BUTTON_SPACING);
        int listStartY = topY + PAGE_Y + SEARCH_HEIGHT + 10 + categoryButtonsHeight + 10;
        
        int visibleCount = getVisibleCreatureCount();
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + visibleCount, filteredCreatures.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            BestiaryCreatureRegistry.CreatureEntry creature = filteredCreatures.get(i);
            int y = listStartY + (i - startIndex) * (CREATURE_BUTTON_HEIGHT + 2);
            
            if (mouseX >= leftX + LEFT_PAGE_X + 5 && mouseX < leftX + LEFT_PAGE_X + LIST_PANEL_WIDTH - 5 &&
                mouseY >= y && mouseY < y + CREATURE_BUTTON_HEIGHT) {
                selectedCreature = creature;
                System.out.println("[BestiaryScreen] Selected creature: " + creature.getName());
                break;
            }
        }
    }
}













