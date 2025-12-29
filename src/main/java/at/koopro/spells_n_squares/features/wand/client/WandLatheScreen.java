package at.koopro.spells_n_squares.features.wand.client;

import at.koopro.spells_n_squares.features.wand.WandCore;
import at.koopro.spells_n_squares.features.wand.WandData;
import at.koopro.spells_n_squares.features.wand.WandLatheMenu;
import at.koopro.spells_n_squares.features.wand.WandRegistry;
import at.koopro.spells_n_squares.features.wand.WandWood;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Client-side GUI screen for the wand lathe.
 * Allows players to select wood type and core to craft a wand.
 */
public class WandLatheScreen extends AbstractContainerScreen<WandLatheMenu> {
    private static final Identifier WAND_LATHE_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/container/wand_lathe.png");
    
    private Button woodTypeButton;
    private Button coreTypeButton;
    private Button craftButton;
    
    private WandWood selectedWood = WandWood.OAK;
    private WandCore selectedCore = WandCore.PHOENIX_FEATHER;
    
    public WandLatheScreen(WandLatheMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.leftPos + this.imageWidth / 2;
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Draw background (fallback to generic container texture if custom texture not available)
        try {
            guiGraphics.blit(WAND_LATHE_GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        } catch (Exception e) {
            // Fallback to generic container texture
            Identifier fallbackTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
            guiGraphics.blit(fallbackTexture, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Render title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        
        // Render selected wand preview info
        int infoY = 50;
        guiGraphics.drawString(this.font, 
            Component.translatable("gui.spells_n_squares.wand_lathe.selected_wood", 
                Component.translatable("wand.wood." + selectedWood.getId())), 
            8, infoY, 0x404040, false);
        
        guiGraphics.drawString(this.font, 
            Component.translatable("gui.spells_n_squares.wand_lathe.selected_core", 
                Component.translatable("wand.core." + selectedCore.getId())), 
            8, infoY + 12, 0x404040, false);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    private void craftWand() {
        if (this.minecraft == null || this.minecraft.player == null) {
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
        
        // Add wand to player inventory or drop if full
        if (!this.minecraft.player.getInventory().add(wandStack)) {
            this.minecraft.player.drop(wandStack, false);
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
    }
}

