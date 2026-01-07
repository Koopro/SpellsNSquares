package at.koopro.spells_n_squares.features.economy.client;

import at.koopro.spells_n_squares.core.client.gui.BaseModScreen;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.economy.VaultMenu;
import at.koopro.spells_n_squares.features.storage.statistics.StorageStatisticsHelper;
import at.koopro.spells_n_squares.features.storage.transfer.QuickTransferHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * GUI screen for the Vault block.
 * Provides secure storage for player items and currency with search, statistics, and quick transfer.
 */
public class VaultScreen extends BaseModScreen<VaultMenu> {
    private static final Identifier VAULT_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/container/vault.png");
    
    private EditBox searchBox;
    private Button statsButton;
    private Button quickTransferButton;
    private boolean showStats = false;
    private String searchQuery = "";
    
    public VaultScreen(VaultMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        
        int buttonX = this.leftPos + 8;
        int buttonY = this.topPos + 20;
        
        // Search box
        this.searchBox = new EditBox(this.font, buttonX, buttonY, 100, 16, 
            Component.translatable("gui.spells_n_squares.vault.search"));
        this.searchBox.setValue(searchQuery);
        this.searchBox.setResponder(text -> {
            searchQuery = text;
            // Highlight matching items (would need custom rendering)
        });
        this.addRenderableWidget(searchBox);
        
        // Statistics button
        this.statsButton = Button.builder(
            Component.translatable("gui.spells_n_squares.vault.stats"),
            button -> {
                showStats = !showStats;
                updateButtonStates();
            }
        ).bounds(buttonX + 105, buttonY, 60, 20).build();
        this.addRenderableWidget(statsButton);
        
        // Quick transfer button
        this.quickTransferButton = Button.builder(
            Component.translatable("gui.spells_n_squares.vault.quick_transfer"),
            button -> {
                if (this.minecraft != null && this.minecraft.player != null && this.menu != null) {
                    // Get vault container from menu
                    net.minecraft.world.Container vaultContainer = getVaultContainer();
                    if (vaultContainer != null) {
                        // Transfer all items from player inventory to vault
                        QuickTransferHelper.transferAll(
                            this.minecraft.player.getInventory(),
                            vaultContainer
                        );
                    }
                }
            }
        ).bounds(buttonX + 170, buttonY, 80, 20).build();
        this.addRenderableWidget(quickTransferButton);
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        if (statsButton != null) {
            statsButton.active = true; // Always enabled
        }
    }
    
    @Override
    protected Identifier getBackgroundTexture() {
        return VAULT_GUI_TEXTURE;
    }
    
    @Override
    protected int getImageWidth() {
        return 176;
    }
    
    @Override
    protected int getImageHeight() {
        return 222; // Taller than standard for vault storage
    }
    
    @Override
    protected void renderStandardLabels(GuiGraphics guiGraphics) {
        super.renderStandardLabels(guiGraphics);
        
        // Render vault info
        int infoY = this.topPos + 45;
        Component info = ColorUtils.coloredText("Secure Storage", ColorUtils.SPELL_GOLD);
        guiGraphics.drawString(this.font, info, this.leftPos + 8, infoY, ColorUtils.SPELL_WHITE, false);
        
        // Render statistics if enabled
        if (showStats && this.menu != null) {
            net.minecraft.world.Container vaultContainer = getVaultContainer();
            if (vaultContainer != null) {
                StorageStatisticsHelper.StorageStats stats = StorageStatisticsHelper.calculateStats(vaultContainer);
                int statsY = infoY + 12;
                guiGraphics.drawString(this.font, 
                    ColorUtils.coloredText("Items: " + stats.totalItems(), ColorUtils.SPELL_WHITE),
                    this.leftPos + 8, statsY, ColorUtils.SPELL_WHITE, false);
                guiGraphics.drawString(this.font,
                    ColorUtils.coloredText("Types: " + stats.uniqueItemTypes(), ColorUtils.SPELL_WHITE),
                    this.leftPos + 8, statsY + 10, ColorUtils.SPELL_WHITE, false);
                guiGraphics.drawString(this.font,
                    ColorUtils.coloredText("Filled: " + stats.filledSlots() + "/" + 
                        (stats.filledSlots() + stats.emptySlots()), ColorUtils.SPELL_WHITE),
                    this.leftPos + 8, statsY + 20, ColorUtils.SPELL_WHITE, false);
            }
        }
        
        // Render hint
        int hintY = showStats ? this.topPos + 100 : infoY + 12;
        Component hint = ColorUtils.coloredText("Store your valuable items safely", ColorUtils.rgb(170, 170, 170));
        guiGraphics.drawString(this.font, hint, this.leftPos + 8, hintY, ColorUtils.SPELL_WHITE, false);
    }
    
    /**
     * Gets the vault container from the menu's block entity.
     * 
     * @return The vault container, or null if not available
     */
    private net.minecraft.world.Container getVaultContainer() {
        if (this.menu == null || this.minecraft == null || this.minecraft.level == null) {
            return null;
        }
        
        net.minecraft.core.BlockPos vaultPos = this.menu.getVaultPos();
        if (vaultPos == null) {
            return null;
        }
        
        if (this.minecraft.level.getBlockEntity(vaultPos) instanceof 
            at.koopro.spells_n_squares.features.economy.block.VaultBlockEntity blockEntity) {
            return blockEntity.getInventory();
        }
        
        return null;
    }
}

