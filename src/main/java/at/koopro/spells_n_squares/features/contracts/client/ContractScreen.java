package at.koopro.spells_n_squares.features.contracts.client;

import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.contracts.network.ContractCreationPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * GUI screen for creating and viewing contracts.
 * Allows players to create magical contracts with other players.
 */
public class ContractScreen extends Screen {
    private EditBox contractText;
    private EditBox targetPlayer;
    private Button createButton;
    private Button cancelButton;
    
    public ContractScreen() {
        super(Component.translatable("gui.spells_n_squares.contract.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Contract text input
        this.contractText = new EditBox(this.font, centerX - 150, startY + 20, 300, 20, 
            Component.translatable("gui.spells_n_squares.contract.text"));
        this.contractText.setMaxLength(500);
        this.contractText.setValue("");
        this.addRenderableWidget(contractText);
        
        // Target player input
        this.targetPlayer = new EditBox(this.font, centerX - 150, startY + 50, 300, 20,
            Component.translatable("gui.spells_n_squares.contract.target"));
        this.targetPlayer.setMaxLength(16);
        this.targetPlayer.setValue("");
        this.addRenderableWidget(targetPlayer);
        
        // Create button
        this.createButton = Button.builder(
            Component.translatable("gui.spells_n_squares.contract.create"),
            button -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    String targetName = this.targetPlayer.getValue().trim();
                    String contractText = this.contractText.getValue().trim();
                    
                    // Validate input
                    if (targetName.isEmpty()) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Please enter a target player name.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate player name length (Minecraft usernames are max 16 characters)
                    if (targetName.length() > 16) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Player name cannot exceed 16 characters.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate player name contains only valid characters (letters, numbers, underscore)
                    if (!targetName.matches("^[a-zA-Z0-9_]+$")) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Player name can only contain letters, numbers, and underscores.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    if (contractText.isEmpty()) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Contract text cannot be empty.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate contract text length
                    if (contractText.length() > 500) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Contract text cannot exceed 500 characters.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Send contract creation to server
                    var payload = new ContractCreationPayload(targetName, contractText);
                    ClientPacketDistributor.sendToServer(payload);
                    
                    // Show success message
                    this.minecraft.gui.getChat().addMessage(
                        ColorUtils.coloredText("Contract created and sent to " + targetName + "!", ColorUtils.SPELL_GOLD)
                    );
                    
                    this.minecraft.setScreen(null);
                }
            }
        ).bounds(centerX - 100, startY + 80, 200, 20).build();
        this.addRenderableWidget(createButton);
        
        // Cancel button
        this.cancelButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(null);
                }
            }
        ).bounds(centerX - 100, startY + 110, 200, 20).build();
        this.addRenderableWidget(cancelButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Title
        Component title = Component.translatable("gui.spells_n_squares.contract.title")
            .withStyle(style -> style.withColor(ColorUtils.SPELL_GOLD));
        guiGraphics.drawCenteredString(this.font, title, centerX, startY, ColorUtils.SPELL_WHITE);
        
        // Labels
        guiGraphics.drawString(this.font,
            ColorUtils.coloredText("Contract Text:", ColorUtils.SPELL_WHITE),
            centerX - 150, startY + 10, ColorUtils.SPELL_WHITE, false);
        
        guiGraphics.drawString(this.font,
            ColorUtils.coloredText("Target Player:", ColorUtils.SPELL_WHITE),
            centerX - 150, startY + 40, ColorUtils.SPELL_WHITE, false);
        
        // Hint
        int hintY = startY + 120;
        Component hint = ColorUtils.coloredText("Create a magical contract with another player", ColorUtils.rgb(170, 170, 170));
        guiGraphics.drawCenteredString(this.font, hint, centerX, hintY, ColorUtils.SPELL_WHITE);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render tooltips for buttons
        renderTooltipForButton(guiGraphics, createButton, "gui.spells_n_squares.contract.create_tooltip", mouseX, mouseY);
        renderTooltipForButton(guiGraphics, cancelButton, "gui.spells_n_squares.contract.cancel_tooltip", mouseX, mouseY);
    }
    
    /**
     * Renders a tooltip for a button if the mouse is over it.
     */
    private void renderTooltipForButton(GuiGraphics guiGraphics, Button button, String translationKey, int mouseX, int mouseY) {
        if (button != null && button.isMouseOver(mouseX, mouseY)) {
            Component tooltip = Component.translatable(translationKey);
            java.util.List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent> tooltipComponents = 
                java.util.List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(tooltip.getVisualOrderText()));
            guiGraphics.renderTooltip(this.font, tooltipComponents, mouseX, mouseY, 
                net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE, null);
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

