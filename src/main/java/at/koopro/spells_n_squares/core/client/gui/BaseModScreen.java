package at.koopro.spells_n_squares.core.client.gui;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for mod GUI screens with common functionality.
 * Provides texture loading with fallback, standardized rendering, dynamic widget management,
 * and common patterns for container screens.
 * 
 * @param <T> The menu type for this screen
 */
public abstract class BaseModScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private final List<AbstractWidget> dynamicWidgets = CollectionFactory.createList();
    private Identifier cachedBackgroundTexture;
    private Identifier cachedFallbackTexture;
    
    public BaseModScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    /**
     * Gets the background texture identifier for this screen.
     * Subclasses should override to provide their specific texture.
     * 
     * @return The background texture identifier
     */
    protected abstract Identifier getBackgroundTexture();
    
    /**
     * Gets the fallback texture identifier if the main texture is not available.
     * Defaults to generic container texture.
     * 
     * @return The fallback texture identifier
     */
    protected Identifier getFallbackTexture() {
        if (cachedFallbackTexture == null) {
            cachedFallbackTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
        }
        return cachedFallbackTexture;
    }
    
    /**
     * Sets a custom fallback texture.
     * 
     * @param texture The fallback texture identifier
     */
    protected void setFallbackTexture(Identifier texture) {
        this.cachedFallbackTexture = texture;
    }
    
    /**
     * Gets the image width for this screen.
     * Defaults to 176 (standard container width).
     * 
     * @return The image width
     */
    protected int getImageWidth() {
        return 176;
    }
    
    /**
     * Gets the image height for this screen.
     * Defaults to 166 (standard container height).
     * 
     * @return The image height
     */
    protected int getImageHeight() {
        return 166;
    }
    
    @Override
    protected void init() {
        DevLogger.logMethodEntry(this, "init");
        super.init();
        
        // Set image dimensions
        this.imageWidth = getImageWidth();
        this.imageHeight = getImageHeight();
        this.inventoryLabelY = this.imageHeight - 94;
        
        DevLogger.logParameter(this, "init", "imageWidth", this.imageWidth);
        DevLogger.logParameter(this, "init", "imageHeight", this.imageHeight);
        
        // Initialize background texture cache
        this.cachedBackgroundTexture = getBackgroundTexture();
        DevLogger.logParameter(this, "init", "backgroundTexture", 
            this.cachedBackgroundTexture != null ? this.cachedBackgroundTexture.toString() : "null");
        
        // Initialize custom widgets
        initCustomWidgets();
        DevLogger.logMethodExit(this, "init");
    }
    
    /**
     * Called during init() to set up custom widgets.
     * Subclasses should override to add their specific widgets.
     */
    protected void initCustomWidgets() {
        DevLogger.logMethodEntry(this, "initCustomWidgets");
        // Override in subclasses
        DevLogger.logMethodExit(this, "initCustomWidgets");
    }
    
    /**
     * Adds a dynamic widget that can be added/removed at runtime.
     * 
     * @param widget The widget to add
     */
    protected void addDynamicWidget(AbstractWidget widget) {
        DevLogger.logMethodEntry(this, "addDynamicWidget", 
            "widget=" + (widget != null ? widget.getClass().getSimpleName() : "null"));
        if (widget != null) {
            dynamicWidgets.add(widget);
            this.addRenderableWidget(widget);
            DevLogger.logStateChange(this, "addDynamicWidget", "widget added, total=" + dynamicWidgets.size());
        }
        DevLogger.logMethodExit(this, "addDynamicWidget");
    }
    
    /**
     * Removes a dynamic widget.
     * 
     * @param widget The widget to remove
     */
    protected void removeDynamicWidget(AbstractWidget widget) {
        DevLogger.logMethodEntry(this, "removeDynamicWidget", 
            "widget=" + (widget != null ? widget.getClass().getSimpleName() : "null"));
        if (widget != null) {
            dynamicWidgets.remove(widget);
            this.removeWidget(widget);
            DevLogger.logStateChange(this, "removeDynamicWidget", "widget removed, total=" + dynamicWidgets.size());
        }
        DevLogger.logMethodExit(this, "removeDynamicWidget");
    }
    
    /**
     * Clears all dynamic widgets.
     */
    protected void clearDynamicWidgets() {
        DevLogger.logMethodEntry(this, "clearDynamicWidgets", "count=" + dynamicWidgets.size());
        for (AbstractWidget widget : dynamicWidgets) {
            this.removeWidget(widget);
        }
        dynamicWidgets.clear();
        DevLogger.logStateChange(this, "clearDynamicWidgets", "all widgets cleared");
        DevLogger.logMethodExit(this, "clearDynamicWidgets");
    }
    
    /**
     * Gets all dynamic widgets.
     * 
     * @return A list of dynamic widgets
     */
    protected List<AbstractWidget> getDynamicWidgets() {
        return CollectionFactory.createListFrom(dynamicWidgets);
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        DevLogger.logMethodEntry(this, "renderBg", 
            "partialTick=" + partialTick + ", mouseX=" + mouseX + ", mouseY=" + mouseY);
        renderStandardBackground(guiGraphics);
        DevLogger.logMethodExit(this, "renderBg");
    }
    
    /**
     * Renders the standard background with texture loading and fallback.
     * 
     * @param guiGraphics The graphics context
     */
    protected void renderStandardBackground(GuiGraphics guiGraphics) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Try to render custom texture, fallback to generic if not available
        try {
            Identifier texture = cachedBackgroundTexture != null ? cachedBackgroundTexture : getBackgroundTexture();
            guiGraphics.blit(texture, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        } catch (Exception e) {
            // Fallback to generic container texture
            Identifier fallback = getFallbackTexture();
            guiGraphics.blit(fallback, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        DevLogger.logMethodEntry(this, "renderLabels", "mouseX=" + mouseX + ", mouseY=" + mouseY);
        renderStandardLabels(guiGraphics);
        DevLogger.logMethodExit(this, "renderLabels");
    }
    
    /**
     * Renders standard labels (title and inventory label).
     * Subclasses can override to add custom labels.
     * 
     * @param guiGraphics The graphics context
     */
    protected void renderStandardLabels(GuiGraphics guiGraphics) {
        // Render title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        
        // Render inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
    
    /**
     * Renders a tooltip at the specified position with proper positioning logic.
     * Prevents tooltips from going off-screen and uses Minecraft's standard tooltip rendering.
     * 
     * @param guiGraphics The graphics context
     * @param tooltip The tooltip text (single line)
     * @param x The X position
     * @param y The Y position
     */
    protected void renderTooltip(GuiGraphics guiGraphics, Component tooltip, int x, int y) {
        if (tooltip == null) {
            return;
        }
        
        // Convert Component to ClientTooltipComponent and render
        List<ClientTooltipComponent> components = List.of(ClientTooltipComponent.create(tooltip.getVisualOrderText()));
        guiGraphics.renderTooltip(this.font, components, x, y, DefaultTooltipPositioner.INSTANCE, null);
    }
    
    /**
     * Renders a multi-line tooltip at the specified position.
     * 
     * @param guiGraphics The graphics context
     * @param tooltipLines List of tooltip lines
     * @param x The X position
     * @param y The Y position
     */
    protected void renderTooltip(GuiGraphics guiGraphics, List<Component> tooltipLines, int x, int y) {
        if (tooltipLines == null || tooltipLines.isEmpty()) {
            return;
        }
        
        // Convert Components to ClientTooltipComponents and render
        List<ClientTooltipComponent> components = tooltipLines.stream()
            .map(component -> ClientTooltipComponent.create(component.getVisualOrderText()))
            .collect(Collectors.toList());
        guiGraphics.renderTooltip(this.font, components, x, y, DefaultTooltipPositioner.INSTANCE, null);
    }
    
    /**
     * Renders a tooltip at the mouse position with bounds checking.
     * 
     * @param guiGraphics The graphics context
     * @param tooltip The tooltip text
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     */
    protected void renderTooltipAtMouse(GuiGraphics guiGraphics, Component tooltip, int mouseX, int mouseY) {
        if (tooltip != null && isHovering(0, 0, this.imageWidth, this.imageHeight, mouseX, mouseY)) {
            renderTooltip(guiGraphics, tooltip, mouseX, mouseY);
        }
    }
    
    /**
     * Renders a multi-line tooltip at the mouse position with bounds checking.
     * 
     * @param guiGraphics The graphics context
     * @param tooltipLines List of tooltip lines
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     */
    protected void renderTooltipAtMouse(GuiGraphics guiGraphics, List<Component> tooltipLines, int mouseX, int mouseY) {
        if (tooltipLines != null && !tooltipLines.isEmpty() && 
            isHovering(0, 0, this.imageWidth, this.imageHeight, mouseX, mouseY)) {
            renderTooltip(guiGraphics, tooltipLines, mouseX, mouseY);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        DevLogger.logMethodEntry(this, "render", 
            "mouseX=" + mouseX + ", mouseY=" + mouseY + ", partialTick=" + partialTick);
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        DevLogger.logMethodExit(this, "render");
    }
    
    /**
     * Gets the center X position of the screen.
     * 
     * @return The center X position
     */
    protected int getCenterX() {
        return this.leftPos + this.imageWidth / 2;
    }
    
    /**
     * Gets the center Y position of the screen.
     * 
     * @return The center Y position
     */
    protected int getCenterY() {
        return this.topPos + this.imageHeight / 2;
    }
    
    /**
     * Checks if a point is within the screen bounds.
     * 
     * @param x The X coordinate
     * @param y The Y coordinate
     * @return True if within bounds
     */
    protected boolean isWithinBounds(int x, int y) {
        return x >= this.leftPos && x < this.leftPos + this.imageWidth &&
               y >= this.topPos && y < this.topPos + this.imageHeight;
    }
}

