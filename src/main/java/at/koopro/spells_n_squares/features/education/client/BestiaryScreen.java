package at.koopro.spells_n_squares.features.education.client;

import at.koopro.spells_n_squares.features.education.BestiaryCreatureRegistry;
import at.koopro.spells_n_squares.features.education.BestiaryData;
import at.koopro.spells_n_squares.features.education.BestiaryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side GUI screen for the bestiary.
 * Displays a comprehensive catalog of all magical creatures from the Wizarding World.
 */
public class BestiaryScreen extends AbstractContainerScreen<BestiaryMenu> {
    private static final int LIST_PANEL_WIDTH = 200;
    private static final int DETAIL_PANEL_X = LIST_PANEL_WIDTH + 10;
    private static final int PANEL_Y = 30;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 2;
    private static final int CREATURE_BUTTON_HEIGHT = 16;
    private static final int SEARCH_HEIGHT = 20;
    
    private BestiaryCreatureRegistry.CreatureCategory selectedCategory = null;
    private BestiaryCreatureRegistry.CreatureEntry selectedCreature = null;
    private String searchQuery = "";
    private int scrollOffset = 0;
    private EditBox searchBox;
    private List<BestiaryCreatureRegistry.CreatureEntry> filteredCreatures = new ArrayList<>();
    
    public BestiaryScreen(BestiaryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 400;
        this.imageHeight = 250;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int leftX = this.leftPos;
        int topY = this.topPos;
        
        // Initialize creature registry if not already done
        BestiaryCreatureRegistry.initialize();
        
        // Create search box
        this.searchBox = new EditBox(this.font, leftX + 5, topY + 5, LIST_PANEL_WIDTH - 10, SEARCH_HEIGHT, 
            Component.translatable("gui.spells_n_squares.bestiary.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setResponder(query -> {
            this.searchQuery = query;
            updateFilteredCreatures();
        });
        this.addRenderableWidget(this.searchBox);
        
        // Create category filter buttons
        int categoryY = topY + SEARCH_HEIGHT + 10;
        int categoryButtonWidth = (LIST_PANEL_WIDTH - 10) / 2;
        
        BestiaryCreatureRegistry.CreatureCategory[] categories = BestiaryCreatureRegistry.CreatureCategory.values();
        for (int i = 0; i < categories.length; i++) {
            BestiaryCreatureRegistry.CreatureCategory category = categories[i];
            int x = leftX + 5 + (i % 2) * categoryButtonWidth;
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
        
        // Create scroll buttons
        int scrollY = topY + this.imageHeight - 40;
        Button scrollUp = Button.builder(
            Component.literal("▲"),
            button -> {
                if (scrollOffset > 0) {
                    scrollOffset--;
                }
            }
        ).bounds(leftX + 5, scrollY, LIST_PANEL_WIDTH - 10, BUTTON_HEIGHT).build();
        
        Button scrollDown = Button.builder(
            Component.literal("▼"),
            button -> {
                int maxScroll = Math.max(0, filteredCreatures.size() - getVisibleCreatureCount());
                if (scrollOffset < maxScroll) {
                    scrollOffset++;
                }
            }
        ).bounds(leftX + 5, scrollY + BUTTON_HEIGHT + 2, LIST_PANEL_WIDTH - 10, BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(scrollUp);
        this.addRenderableWidget(scrollDown);
        
        // Close button
        Button closeButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(leftX + this.imageWidth - 60, topY + 5, 55, BUTTON_HEIGHT).build();
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
        
        List<BestiaryCreatureRegistry.CreatureEntry> allCreatures = 
            new ArrayList<>(BestiaryCreatureRegistry.getAllCreatures());
        
        // Filter by category
        if (selectedCategory != null) {
            allCreatures.removeIf(entry -> entry.getCategory() != selectedCategory);
        }
        
        // Filter by search query
        if (!searchQuery.isEmpty()) {
            String lowerQuery = searchQuery.toLowerCase();
            allCreatures.removeIf(entry -> 
                !entry.getName().toLowerCase().contains(lowerQuery) &&
                !entry.getDescription().toLowerCase().contains(lowerQuery)
            );
        }
        
        filteredCreatures.addAll(allCreatures);
        scrollOffset = Math.min(scrollOffset, Math.max(0, filteredCreatures.size() - getVisibleCreatureCount()));
        
        // Auto-select first creature if none selected
        if (selectedCreature == null && !filteredCreatures.isEmpty()) {
            selectedCreature = filteredCreatures.get(0);
        }
    }
    
    private int getVisibleCreatureCount() {
        int availableHeight = this.imageHeight - PANEL_Y - 60; // Account for buttons and spacing
        return Math.max(1, availableHeight / (CREATURE_BUTTON_HEIGHT + 2));
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Render background
        int leftX = this.leftPos;
        int topY = this.topPos;
        
        // Dark background
        guiGraphics.fill(leftX, topY, leftX + this.imageWidth, topY + this.imageHeight, 0xC0101010);
        
        // List panel background
        guiGraphics.fill(leftX, topY + PANEL_Y, leftX + LIST_PANEL_WIDTH, topY + this.imageHeight - 10, 0xFF1A1A1A);
        guiGraphics.fill(leftX, topY + PANEL_Y, leftX + LIST_PANEL_WIDTH, topY + PANEL_Y + 1, 0xFF404040);
        
        // Detail panel background
        guiGraphics.fill(leftX + DETAIL_PANEL_X, topY + PANEL_Y, leftX + this.imageWidth - 5, topY + this.imageHeight - 10, 0xFF1A1A1A);
        guiGraphics.fill(leftX + DETAIL_PANEL_X, topY + PANEL_Y, leftX + DETAIL_PANEL_X + 1, topY + this.imageHeight - 10, 0xFF404040);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        int leftX = this.leftPos;
        int topY = this.topPos;
        
        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, leftX + this.imageWidth / 2, topY + 10, 0xFFFFFF);
        
        // Draw creature list
        int listStartY = topY + PANEL_Y + 5;
        int visibleCount = getVisibleCreatureCount();
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + visibleCount, filteredCreatures.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            BestiaryCreatureRegistry.CreatureEntry creature = filteredCreatures.get(i);
            int y = listStartY + (i - startIndex) * (CREATURE_BUTTON_HEIGHT + 2);
            
            // Check if discovered
            boolean isDiscovered = BestiaryData.hasDiscovered(this.menu.getPlayer(), creature.getId());
            boolean isSelected = selectedCreature != null && selectedCreature.getId().equals(creature.getId());
            
            // Draw creature button background
            int bgColor = isSelected ? 0xFF404040 : (isDiscovered ? 0xFF2A2A2A : 0xFF1A1A1A);
            guiGraphics.fill(leftX + 5, y, leftX + LIST_PANEL_WIDTH - 5, y + CREATURE_BUTTON_HEIGHT, bgColor);
            
            // Draw creature name
            String displayName = isDiscovered ? creature.getName() : "???";
            int textColor = isDiscovered ? (isSelected ? 0xFFFFFF : 0xCCCCCC) : 0x666666;
            guiGraphics.drawString(this.font, displayName, leftX + 8, y + 4, textColor, false);
            
            // Draw clickable area (invisible button)
            if (mouseX >= leftX + 5 && mouseX < leftX + LIST_PANEL_WIDTH - 5 &&
                mouseY >= y && mouseY < y + CREATURE_BUTTON_HEIGHT) {
                guiGraphics.fill(leftX + 5, y, leftX + LIST_PANEL_WIDTH - 5, y + CREATURE_BUTTON_HEIGHT, 0x40FFFFFF);
                
                // Handle click
                if (this.minecraft.mouseHandler.isLeftPressed()) {
                    selectedCreature = creature;
                }
            }
        }
        
        // Draw detail panel
        if (selectedCreature != null) {
            renderCreatureDetails(guiGraphics, leftX + DETAIL_PANEL_X + 10, topY + PANEL_Y + 10, 
                selectedCreature, mouseX, mouseY);
        }
        
        // Draw discovery count
        int discoveredCount = BestiaryData.getBestiaryData(this.menu.getPlayer()).getDiscoveredCount();
        int totalCount = BestiaryCreatureRegistry.getAllCreatures().size();
        String discoveryText = String.format("Discovered: %d/%d", discoveredCount, totalCount);
        guiGraphics.drawString(this.font, discoveryText, leftX + 5, topY + this.imageHeight - 20, 0xCCCCCC, false);
    }
    
    private void renderCreatureDetails(GuiGraphics guiGraphics, int x, int y, 
                                     BestiaryCreatureRegistry.CreatureEntry creature,
                                     int mouseX, int mouseY) {
        boolean isDiscovered = BestiaryData.hasDiscovered(this.menu.getPlayer(), creature.getId());
        
        int currentY = y;
        
        // Creature name
        String name = isDiscovered ? creature.getName() : "???";
        guiGraphics.drawString(this.font, name, x, currentY, 0xFFFFFF, false);
        currentY += this.font.lineHeight + 5;
        
        if (!isDiscovered) {
            guiGraphics.drawString(this.font, "Not yet encountered", x, currentY, 0x999999, false);
            return;
        }
        
        // Category
        guiGraphics.drawString(this.font, "Category: " + getCategoryName(creature.getCategory()), x, currentY, 0xCCCCCC, false);
        currentY += this.font.lineHeight + 3;
        
        // Description
        guiGraphics.drawString(this.font, "Description:", x, currentY, 0xCCCCCC, false);
        currentY += this.font.lineHeight + 2;
        drawWrappedText(guiGraphics, creature.getDescription(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
        currentY += getWrappedTextHeight(creature.getDescription(), this.imageWidth - DETAIL_PANEL_X - 30) + 5;
        
        // Habitat
        if (creature.getHabitat() != null && !creature.getHabitat().isEmpty()) {
            guiGraphics.drawString(this.font, "Habitat:", x, currentY, 0xCCCCCC, false);
            currentY += this.font.lineHeight + 2;
            drawWrappedText(guiGraphics, creature.getHabitat(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
            currentY += getWrappedTextHeight(creature.getHabitat(), this.imageWidth - DETAIL_PANEL_X - 30) + 5;
        }
        
        // Behavior
        if (creature.getBehavior() != null && !creature.getBehavior().isEmpty()) {
            guiGraphics.drawString(this.font, "Behavior:", x, currentY, 0xCCCCCC, false);
            currentY += this.font.lineHeight + 2;
            drawWrappedText(guiGraphics, creature.getBehavior(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
            currentY += getWrappedTextHeight(creature.getBehavior(), this.imageWidth - DETAIL_PANEL_X - 30) + 5;
        }
        
        // Abilities
        if (creature.getAbilities() != null && !creature.getAbilities().isEmpty()) {
            guiGraphics.drawString(this.font, "Abilities:", x, currentY, 0xCCCCCC, false);
            currentY += this.font.lineHeight + 2;
            drawWrappedText(guiGraphics, creature.getAbilities(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
            currentY += getWrappedTextHeight(creature.getAbilities(), this.imageWidth - DETAIL_PANEL_X - 30) + 5;
        }
        
        // Stats
        if (creature.getStats() != null && !creature.getStats().isEmpty()) {
            guiGraphics.drawString(this.font, "Stats:", x, currentY, 0xCCCCCC, false);
            currentY += this.font.lineHeight + 2;
            drawWrappedText(guiGraphics, creature.getStats(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
            currentY += getWrappedTextHeight(creature.getStats(), this.imageWidth - DETAIL_PANEL_X - 30) + 5;
        }
        
        // Taming
        if (creature.getTaming() != null && !creature.getTaming().isEmpty()) {
            guiGraphics.drawString(this.font, "Taming:", x, currentY, 0xCCCCCC, false);
            currentY += this.font.lineHeight + 2;
            drawWrappedText(guiGraphics, creature.getTaming(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
            currentY += getWrappedTextHeight(creature.getTaming(), this.imageWidth - DETAIL_PANEL_X - 30) + 5;
        }
        
        // Lore
        if (creature.getLore() != null && !creature.getLore().isEmpty()) {
            guiGraphics.drawString(this.font, "Lore:", x, currentY, 0xCCCCCC, false);
            currentY += this.font.lineHeight + 2;
            drawWrappedText(guiGraphics, creature.getLore(), x, currentY, this.imageWidth - DETAIL_PANEL_X - 30, 0xAAAAAA);
        }
        
        // Implementation status
        if (!creature.isImplemented()) {
            currentY += this.font.lineHeight + 5;
            guiGraphics.drawString(this.font, "Note: This creature is not yet implemented in the mod.", 
                x, currentY, 0xFFAA00, false);
        }
    }
    
    private void drawWrappedText(GuiGraphics guiGraphics, String text, int x, int y, int maxWidth, int color) {
        List<String> lines = wrapText(text, maxWidth);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(this.font, lines.get(i), x, y + i * this.font.lineHeight, color, false);
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
        if (mouseX >= this.leftPos && mouseX < this.leftPos + LIST_PANEL_WIDTH) {
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
}











