package at.koopro.spells_n_squares.features.mail.client;

import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.mail.network.MailSendPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * GUI screen for writing and sending mail.
 * Allows players to send messages via owl post.
 */
public class MailScreen extends Screen {
    private EditBox recipientBox;
    private EditBox subjectBox;
    private EditBox messageBox;
    private Button sendButton;
    private Button cancelButton;
    
    public MailScreen() {
        super(Component.translatable("gui.spells_n_squares.mail.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Recipient input
        this.recipientBox = new EditBox(this.font, centerX - 150, startY + 20, 300, 20,
            Component.translatable("gui.spells_n_squares.mail.recipient"));
        this.recipientBox.setMaxLength(16);
        this.recipientBox.setValue("");
        this.addRenderableWidget(recipientBox);
        
        // Subject input
        this.subjectBox = new EditBox(this.font, centerX - 150, startY + 50, 300, 20,
            Component.translatable("gui.spells_n_squares.mail.subject"));
        this.subjectBox.setMaxLength(100);
        this.subjectBox.setValue("");
        this.addRenderableWidget(subjectBox);
        
        // Message input (multi-line)
        this.messageBox = new EditBox(this.font, centerX - 150, startY + 80, 300, 100,
            Component.translatable("gui.spells_n_squares.mail.message"));
        this.messageBox.setMaxLength(1000);
        this.messageBox.setValue("");
        this.messageBox.setEditable(true);
        this.addRenderableWidget(messageBox);
        
        // Send button
        this.sendButton = Button.builder(
            Component.translatable("gui.spells_n_squares.mail.send"),
            button -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    String recipientName = this.recipientBox.getValue().trim();
                    String subject = this.subjectBox.getValue().trim();
                    String message = this.messageBox.getValue().trim();
                    
                    // Validate input
                    if (recipientName.isEmpty()) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Please enter a recipient name.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate player name length (Minecraft usernames are max 16 characters)
                    if (recipientName.length() > 16) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Player name cannot exceed 16 characters.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate player name contains only valid characters
                    if (!recipientName.matches("^[a-zA-Z0-9_]+$")) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Player name can only contain letters, numbers, and underscores.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    if (message.isEmpty()) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Mail message cannot be empty.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate message length
                    if (message.length() > 1000) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Mail message cannot exceed 1000 characters.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Validate subject length (if provided)
                    if (subject.length() > 100) {
                        this.minecraft.gui.getChat().addMessage(
                            ColorUtils.coloredText("Error: Subject cannot exceed 100 characters.", ColorUtils.SPELL_RED)
                        );
                        return;
                    }
                    
                    // Send mail to server
                    var payload = new MailSendPayload(recipientName, subject, message);
                    ClientPacketDistributor.sendToServer(payload);
                    
                    // Show success message
                    this.minecraft.gui.getChat().addMessage(
                        ColorUtils.coloredText("Mail sent to " + recipientName + "!", ColorUtils.SPELL_GOLD)
                    );
                    
                    this.minecraft.setScreen(null);
                }
            }
        ).bounds(centerX - 100, startY + 190, 200, 20).build();
        this.addRenderableWidget(sendButton);
        
        // Cancel button
        this.cancelButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(null);
                }
            }
        ).bounds(centerX - 100, startY + 190, 200, 20).build();
        this.addRenderableWidget(cancelButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Title
        Component title = Component.translatable("gui.spells_n_squares.mail.title")
            .withStyle(style -> style.withColor(ColorUtils.SPELL_GOLD));
        guiGraphics.drawCenteredString(this.font, title, centerX, startY, ColorUtils.SPELL_WHITE);
        
        // Labels
        guiGraphics.drawString(this.font,
            ColorUtils.coloredText("Recipient:", ColorUtils.SPELL_WHITE),
            centerX - 150, startY + 10, ColorUtils.SPELL_WHITE, false);
        
        guiGraphics.drawString(this.font,
            ColorUtils.coloredText("Subject:", ColorUtils.SPELL_WHITE),
            centerX - 150, startY + 40, ColorUtils.SPELL_WHITE, false);
        
        guiGraphics.drawString(this.font,
            ColorUtils.coloredText("Message:", ColorUtils.SPELL_WHITE),
            centerX - 150, startY + 70, ColorUtils.SPELL_WHITE, false);
        
        // Hint
        int hintY = startY + 220;
        Component hint = ColorUtils.coloredText("Send a message via owl post", ColorUtils.rgb(170, 170, 170));
        guiGraphics.drawCenteredString(this.font, hint, centerX, hintY, ColorUtils.SPELL_WHITE);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render tooltips for buttons
        renderTooltipForButton(guiGraphics, sendButton, "gui.spells_n_squares.mail.send_tooltip", mouseX, mouseY);
        renderTooltipForButton(guiGraphics, cancelButton, "gui.spells_n_squares.mail.cancel_tooltip", mouseX, mouseY);
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

