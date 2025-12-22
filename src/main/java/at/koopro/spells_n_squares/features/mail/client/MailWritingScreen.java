package at.koopro.spells_n_squares.features.mail.client;

import at.koopro.spells_n_squares.features.mail.MailData;
import at.koopro.spells_n_squares.features.mail.MailItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * GUI screen for writing mail.
 */
public class MailWritingScreen extends Screen {
    private EditBox recipientField;
    private EditBox subjectField;
    private EditBox messageField;
    private Button sendButton;
    private Button cancelButton;
    private final ItemStack mailStack;
    
    public MailWritingScreen(ItemStack mailStack) {
        super(Component.translatable("gui.spells_n_squares.mail.write"));
        this.mailStack = mailStack;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 60;
        int fieldWidth = 200;
        int fieldHeight = 20;
        int spacing = 25;
        
        // Recipient field
        this.recipientField = new EditBox(this.font, centerX - fieldWidth / 2, startY, fieldWidth, fieldHeight,
            Component.translatable("gui.spells_n_squares.mail.recipient"));
        this.recipientField.setMaxLength(64);
        this.addRenderableWidget(recipientField);
        
        // Subject field
        this.subjectField = new EditBox(this.font, centerX - fieldWidth / 2, startY + spacing, fieldWidth, fieldHeight,
            Component.translatable("gui.spells_n_squares.mail.subject"));
        this.subjectField.setMaxLength(128);
        this.addRenderableWidget(subjectField);
        
        // Message field (multi-line)
        int messageHeight = 100;
        this.messageField = new EditBox(this.font, centerX - fieldWidth / 2, startY + spacing * 2, fieldWidth, messageHeight,
            Component.translatable("gui.spells_n_squares.mail.message"));
        this.messageField.setMaxLength(1000);
        this.messageField.setValue("");
        this.addRenderableWidget(messageField);
        
        // Buttons
        int buttonY = startY + spacing * 2 + messageHeight + 20;
        this.sendButton = Button.builder(
            Component.translatable("gui.spells_n_squares.mail.send"),
            button -> sendMail()
        ).bounds(centerX - 105, buttonY, 100, 20).build();
        this.addRenderableWidget(sendButton);
        
        this.cancelButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX + 5, buttonY, 100, 20).build();
        this.addRenderableWidget(cancelButton);
        
        // Set initial focus
        this.setInitialFocus(recipientField);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Draw labels
        int centerX = this.width / 2;
        int startY = 60;
        int spacing = 25;
        
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.mail.recipient"), 
            centerX - 100, startY - 10, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.mail.subject"), 
            centerX - 100, startY + spacing - 10, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.mail.message"), 
            centerX - 100, startY + spacing * 2 - 10, 0xFFFFFF);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    private void sendMail() {
        String recipient = recipientField.getValue().trim();
        String subject = subjectField.getValue().trim();
        String message = messageField.getValue().trim();
        
        if (recipient.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            // Show error - fields must be filled
            return;
        }
        
        // Write mail data to item stack
        // Note: In a full implementation, this would send a packet to the server
        // For now, we'll just close the screen
        // The actual mail writing would be handled server-side
        
        this.minecraft.setScreen(null);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}


