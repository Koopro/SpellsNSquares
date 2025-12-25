package at.koopro.spells_n_squares.features.economy.client;

import at.koopro.spells_n_squares.features.economy.CurrencyData;
import at.koopro.spells_n_squares.features.economy.CurrencySystem;
import at.koopro.spells_n_squares.features.economy.GringottsSystem;
import at.koopro.spells_n_squares.features.economy.VaultMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side GUI screen for the vault.
 * Displays currency storage and deposit/withdraw functionality.
 */
public class VaultScreen extends AbstractContainerScreen<VaultMenu> {
    private static final Identifier VAULT_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/container/vault.png");
    
    private Button depositButton;
    private Button withdrawButton;
    
    public VaultScreen(VaultMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.leftPos + this.imageWidth / 2;
        int buttonY = this.topPos + 50;
        
        // Deposit button
        this.depositButton = Button.builder(
            Component.translatable("gui.spells_n_squares.vault.deposit_all"),
            button -> depositAll()
        ).bounds(centerX - 80, buttonY, 80, 20).build();
        this.addRenderableWidget(depositButton);
        
        // Withdraw button
        this.withdrawButton = Button.builder(
            Component.translatable("gui.spells_n_squares.vault.withdraw_all"),
            button -> withdrawAll()
        ).bounds(centerX + 5, buttonY, 80, 20).build();
        this.addRenderableWidget(withdrawButton);
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Draw background (fallback to chest texture if custom texture not available)
        try {
            guiGraphics.blit(VAULT_GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        } catch (Exception e) {
            // Fallback to generic chest texture
            Identifier fallbackTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
            guiGraphics.blit(fallbackTexture, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Render title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        
        // Render currency information
        if (this.menu.getPlayer() != null) {
            // Player currency
            CurrencyData.CurrencyDataComponent playerCurrency = CurrencyData.getCurrencyData(this.menu.getPlayer());
            Component playerCurrencyText = Component.translatable("gui.spells_n_squares.vault.player_currency",
                playerCurrency.galleons(), playerCurrency.sickles(), playerCurrency.knuts());
            guiGraphics.drawString(this.font, playerCurrencyText, 8, 20, 0x404040, false);
            
            // Vault currency
            GringottsSystem.VaultData vault = GringottsSystem.getVault(this.menu.getPlayer());
            Component vaultCurrencyText = Component.translatable("gui.spells_n_squares.vault.vault_currency",
                vault.getGalleons(), vault.getSickles(), vault.getKnuts());
            guiGraphics.drawString(this.font, vaultCurrencyText, 8, 35, 0xFFD700, false); // Gold color for vault
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    private void depositAll() {
        if (this.menu.getPlayer() != null && this.minecraft != null && this.menu.getPlayer() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            CurrencyData.CurrencyDataComponent playerCurrency = CurrencyData.getCurrencyData(this.menu.getPlayer());
            // Send deposit command to server
            // For now, just show a message - would need network packet for full implementation
            serverPlayer.sendSystemMessage(Component.translatable("gui.spells_n_squares.vault.deposit_pending"));
        }
    }
    
    private void withdrawAll() {
        if (this.menu.getPlayer() != null && this.minecraft != null && this.menu.getPlayer() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            GringottsSystem.VaultData vault = GringottsSystem.getVault(this.menu.getPlayer());
            // Send withdraw command to server
            // For now, just show a message - would need network packet for full implementation
            serverPlayer.sendSystemMessage(Component.translatable("gui.spells_n_squares.vault.withdraw_pending"));
        }
    }
}







