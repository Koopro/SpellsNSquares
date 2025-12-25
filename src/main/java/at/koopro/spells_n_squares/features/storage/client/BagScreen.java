package at.koopro.spells_n_squares.features.storage.client;

import at.koopro.spells_n_squares.features.storage.BagMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side GUI screen for the enchanted bag.
 */
public class BagScreen extends AbstractContainerScreen<BagMenu> {
    private static final Identifier CHEST_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
    
    private final int textureWidth;
    private final int textureHeight;
    
    public BagScreen(BagMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        
        // Calculate GUI dimensions based on bag tier
        int bagSlots = menu.getBagSlots();
        int bagRows = (bagSlots + 8) / 9; // Round up
        
        // Standard chest GUI dimensions
        // Small (9 slots, 1 row): 176x132
        // Medium (18 slots, 2 rows): 176x222  
        // Large (27 slots, 3 rows): 248x222
        // Bottomless (54 slots, 6 rows): 248x312
        
        if (bagSlots <= 9) {
            // Small bag: 1 row
            this.imageWidth = 176;
            this.imageHeight = 132;
        } else if (bagSlots <= 18) {
            // Medium bag: 2 rows
            this.imageWidth = 176;
            this.imageHeight = 222;
        } else if (bagSlots <= 27) {
            // Large bag: 3 rows
            this.imageWidth = 248;
            this.imageHeight = 222;
        } else {
            // Bottomless bag: 6 rows
            this.imageWidth = 248;
            this.imageHeight = 312;
        }
        
        this.textureWidth = 256;
        this.textureHeight = 256;
        
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Draw background
        guiGraphics.blit(CHEST_GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 
            this.textureWidth, this.textureHeight);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
}











