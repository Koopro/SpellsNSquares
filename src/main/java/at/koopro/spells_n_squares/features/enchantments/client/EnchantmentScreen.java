package at.koopro.spells_n_squares.features.enchantments.client;

import at.koopro.spells_n_squares.core.registry.EnchantmentRegistry;
import at.koopro.spells_n_squares.features.enchantments.Enchantment;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentMenu;
import at.koopro.spells_n_squares.features.enchantments.EnchantmentSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side GUI screen for the enchantment table.
 */
public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
    private static final Identifier ENCHANTMENT_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/enchanting_table.png");
    
    private List<Button> enchantmentButtons;
    
    public EnchantmentScreen(EnchantmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 197;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Clear existing buttons
        if (enchantmentButtons != null) {
            enchantmentButtons.forEach(this::removeWidget);
        }
        enchantmentButtons = new ArrayList<>();
        
        // Get available enchantments for the item
        net.minecraft.world.item.ItemStack itemStack = menu.getItemToEnchant();
        if (!itemStack.isEmpty()) {
            int buttonY = 60;
            int buttonX = this.leftPos + 60;
            int buttonWidth = 100;
            int buttonHeight = 20;
            int spacing = 22;
            
            List<Enchantment> availableEnchantments = getAvailableEnchantments(itemStack);
            for (int i = 0; i < Math.min(availableEnchantments.size(), 3); i++) {
                Enchantment enchantment = availableEnchantments.get(i);
                final int enchantmentIndex = i;
                
                Button button = Button.builder(
                    Component.literal(enchantment.getName()),
                    btn -> applyEnchantment(enchantment)
                ).bounds(buttonX, buttonY + i * spacing, buttonWidth, buttonHeight).build();
                
                enchantmentButtons.add(button);
                this.addRenderableWidget(button);
            }
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Draw background
        guiGraphics.blit(ENCHANTMENT_GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Render title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        
        // Render item enchantment info if item is present
        net.minecraft.world.item.ItemStack itemStack = menu.getItemToEnchant();
        if (!itemStack.isEmpty()) {
            at.koopro.spells_n_squares.features.enchantments.EnchantmentSystem.EnchantmentData data = 
                EnchantmentSystem.getEnchantmentData(itemStack);
            
            if (data != null && !data.enchantments().isEmpty()) {
                int y = 20;
                guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.enchantment.current"), 
                    8, y, 0x404040, false);
                y += 10;
                
                for (var entry : data.enchantments().entrySet()) {
                    Enchantment enchantment = EnchantmentRegistry.get(entry.getKey());
                    if (enchantment != null) {
                        guiGraphics.drawString(this.font, 
                            Component.literal("- " + enchantment.getName() + " " + entry.getValue()),
                            8, y, 0x404040, false);
                        y += 10;
                    }
                }
            }
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    private net.minecraft.world.item.ItemStack lastItemStack = net.minecraft.world.item.ItemStack.EMPTY;
    
    private List<Enchantment> getAvailableEnchantments(net.minecraft.world.item.ItemStack itemStack) {
        List<Enchantment> available = new ArrayList<>();
        
        // Get all registered enchantments
        for (var entry : EnchantmentRegistry.getAll().entrySet()) {
            Enchantment enchantment = entry.getValue();
            if (EnchantmentSystem.canApplyTo(itemStack, enchantment)) {
                available.add(enchantment);
            }
        }
        
        return available;
    }
    
    private void applyEnchantment(Enchantment enchantment) {
        net.minecraft.world.item.ItemStack itemStack = menu.getItemToEnchant();
        if (!itemStack.isEmpty()) {
            // Apply enchantment (would need server-side packet in full implementation)
            // For now, just close the screen - actual application happens server-side
            // In a full implementation, this would send a packet to the server
            this.minecraft.setScreen(null);
        }
    }
}











