package at.koopro.spells_n_squares.features.wand.client;

import at.koopro.spells_n_squares.core.client.gui.BaseModScreen;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.features.wand.system.WandLatheMenu;
import at.koopro.spells_n_squares.features.wand.registry.WandRegistry;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Client-side GUI screen for the wand lathe.
 * Allows players to select wood type and core to craft a wand.
 */
public class WandLatheScreen extends BaseModScreen<WandLatheMenu> {
    private static final Identifier WAND_LATHE_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/container/wand_lathe.png");
    
    private Button woodTypeButton;
    private Button coreTypeButton;
    private Button craftButton;
    
    private WandWood selectedWood = WandWood.OAK;
    private WandCore selectedCore = WandCore.PHOENIX_FEATHER;
    
    public WandLatheScreen(WandLatheMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected Identifier getBackgroundTexture() {
        return WAND_LATHE_GUI_TEXTURE;
    }
    
    @Override
    protected void initCustomWidgets() {
        int centerX = getCenterX();
        int startY = this.topPos + 20;
        
        // Wood type selector - cycles through wood types
        this.woodTypeButton = Button.builder(
            Component.translatable("wand.wood." + selectedWood.getId()),
            button -> {
                // Cycle to next wood type
                WandWood[] woods = WandWood.values();
                int currentIndex = 0;
                for (int i = 0; i < woods.length; i++) {
                    if (woods[i] == selectedWood) {
                        currentIndex = i;
                        break;
                    }
                }
                selectedWood = woods[(currentIndex + 1) % woods.length];
                button.setMessage(Component.translatable("wand.wood." + selectedWood.getId()));
            }
        ).bounds(centerX - 80, startY, 160, 20).build();
        this.addRenderableWidget(woodTypeButton);
        
        // Core type selector - cycles through core types
        this.coreTypeButton = Button.builder(
            Component.translatable("wand.core." + selectedCore.getId()),
            button -> {
                // Cycle to next core type
                WandCore[] cores = WandCore.values();
                int currentIndex = 0;
                for (int i = 0; i < cores.length; i++) {
                    if (cores[i] == selectedCore) {
                        currentIndex = i;
                        break;
                    }
                }
                selectedCore = cores[(currentIndex + 1) % cores.length];
                button.setMessage(Component.translatable("wand.core." + selectedCore.getId()));
            }
        ).bounds(centerX - 80, startY + 30, 160, 20).build();
        this.addRenderableWidget(coreTypeButton);
        
        // Craft button
        this.craftButton = Button.builder(
            Component.translatable("gui.spells_n_squares.wand_lathe.craft"),
            button -> craftWand()
        ).bounds(centerX - 40, startY + 70, 80, 20).build();
        this.addRenderableWidget(craftButton);
    }
    
    @Override
    protected void renderStandardLabels(GuiGraphics guiGraphics) {
        super.renderStandardLabels(guiGraphics);
        
        // Render selected wand preview info with enhanced visual feedback
        int infoY = this.topPos + 50;
        int textColor = 0x404040;
        int highlightColor = 0xFFFFFF;
        
        // Wood type label with highlight
        Component woodLabel = Component.translatable("gui.spells_n_squares.wand_lathe.selected_wood", 
            Component.translatable("wand.wood." + selectedWood.getId()));
        guiGraphics.drawString(this.font, woodLabel, this.leftPos + 8, infoY, highlightColor, false);
        
        // Core type label with highlight
        Component coreLabel = Component.translatable("gui.spells_n_squares.wand_lathe.selected_core", 
            Component.translatable("wand.core." + selectedCore.getId()));
        guiGraphics.drawString(this.font, coreLabel, this.leftPos + 8, infoY + 12, highlightColor, false);
        
        // Add tooltip hint
        guiGraphics.drawString(this.font, 
            Component.translatable("gui.spells_n_squares.wand_lathe.tooltip_hint"), 
            this.leftPos + 8, infoY + 30, textColor, false);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render tooltips for buttons
        renderTooltipForButton(guiGraphics, woodTypeButton, "gui.spells_n_squares.wand_lathe.wood_tooltip", mouseX, mouseY);
        renderTooltipForButton(guiGraphics, coreTypeButton, "gui.spells_n_squares.wand_lathe.core_tooltip", mouseX, mouseY);
        renderTooltipForButton(guiGraphics, craftButton, "gui.spells_n_squares.wand_lathe.craft_tooltip", mouseX, mouseY);
    }
    
    /**
     * Renders a tooltip for a button if the mouse is over it.
     * @param guiGraphics The graphics context
     * @param button The button to check
     * @param translationKey The translation key for the tooltip
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     */
    private void renderTooltipForButton(GuiGraphics guiGraphics, Button button, String translationKey, int mouseX, int mouseY) {
        if (button != null && button.isMouseOver(mouseX, mouseY)) {
            Component tooltip = Component.translatable(translationKey);
            List<ClientTooltipComponent> tooltipComponents = List.of(ClientTooltipComponent.create(tooltip.getVisualOrderText()));
            guiGraphics.renderTooltip(this.font, tooltipComponents, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }
    
    private void craftWand() {
        DevLogger.logMethodEntry(this, "craftWand", 
            "wood=" + selectedWood.getId() + ", core=" + selectedCore.getId());
        
        if (this.minecraft == null || this.minecraft.player == null) {
            DevLogger.logWarn(this, "craftWand", "Minecraft or player is null");
            DevLogger.logMethodExit(this, "craftWand");
            return;
        }
        
        // For now, create the wand client-side (in a real implementation, this should be done server-side via network packet)
        // Create a new wand item with the selected wood and core
        ItemStack wandStack = new ItemStack(WandRegistry.DEMO_WAND.get());
        
        // Set wand data
        WandData.WandDataComponent wandData = new WandData.WandDataComponent(
            selectedCore.getId(),
            selectedWood.getId(),
            false // Not attuned yet
        );
        
        wandStack.set(WandData.WAND_DATA.get(), wandData);
        
        DevLogger.logStateChange(this, "craftWand", 
            "Created wand with wood=" + selectedWood.getId() + ", core=" + selectedCore.getId());
        
        // Add wand to player inventory or drop if full
        if (!this.minecraft.player.getInventory().add(wandStack)) {
            this.minecraft.player.drop(wandStack, false);
            DevLogger.logDebug(this, "craftWand", "Inventory full, dropped wand");
        } else {
            DevLogger.logDebug(this, "craftWand", "Added wand to inventory");
        }
        
        // Close the screen
        this.minecraft.setScreen(null);
        
        // Show message in chat (client-side)
        if (this.minecraft.gui != null) {
            this.minecraft.gui.getChat().addMessage(
                Component.translatable("gui.spells_n_squares.wand_lathe.crafted", 
                    Component.translatable("wand.wood." + selectedWood.getId()),
                    Component.translatable("wand.core." + selectedCore.getId()))
            );
        }
        
        DevLogger.logMethodExit(this, "craftWand");
    }
}

