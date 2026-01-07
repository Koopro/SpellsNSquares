package at.koopro.spells_n_squares.features.mail.client;

import at.koopro.spells_n_squares.core.client.gui.BaseModScreen;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.mail.MailboxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * GUI screen for the Mailbox block.
 * Allows players to view and retrieve received mail with inbox/outbox organization and filtering.
 */
public class MailboxScreen extends BaseModScreen<MailboxMenu> {
    private static final Identifier MAILBOX_GUI_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/container/mailbox.png");
    
    private Button inboxButton;
    private Button outboxButton;
    private boolean showingInbox = true;
    
    public MailboxScreen(MailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        
        int buttonX = this.leftPos + 8;
        int buttonY = this.topPos + 20;
        
        // Inbox/Outbox toggle buttons
        this.inboxButton = Button.builder(
            Component.translatable("gui.spells_n_squares.mailbox.inbox"),
            button -> {
                showingInbox = true;
                updateButtonStates();
            }
        ).bounds(buttonX, buttonY, 80, 20).build();
        this.addRenderableWidget(inboxButton);
        
        this.outboxButton = Button.builder(
            Component.translatable("gui.spells_n_squares.mailbox.outbox"),
            button -> {
                showingInbox = false;
                updateButtonStates();
            }
        ).bounds(buttonX + 85, buttonY, 80, 20).build();
        this.addRenderableWidget(outboxButton);
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        if (inboxButton != null && outboxButton != null) {
            inboxButton.active = !showingInbox;
            outboxButton.active = showingInbox;
        }
    }
    
    @Override
    protected Identifier getBackgroundTexture() {
        return MAILBOX_GUI_TEXTURE;
    }
    
    @Override
    protected int getImageWidth() {
        return 176;
    }
    
    @Override
    protected int getImageHeight() {
        return 166;
    }
    
    @Override
    protected void renderStandardLabels(GuiGraphics guiGraphics) {
        super.renderStandardLabels(guiGraphics);
        
        // Render mailbox info
        int infoY = this.topPos + 45;
        Component info = ColorUtils.coloredText(
            showingInbox ? "Inbox" : "Outbox", 
            ColorUtils.SPELL_GOLD
        );
        guiGraphics.drawString(this.font, info, this.leftPos + 8, infoY, ColorUtils.SPELL_WHITE, false);
        
        // Render hint
        int hintY = infoY + 12;
        Component hint = ColorUtils.coloredText(
            showingInbox 
                ? "Mail received from other players" 
                : "Mail you have sent",
            ColorUtils.rgb(170, 170, 170)
        );
        guiGraphics.drawString(this.font, hint, this.leftPos + 8, hintY, ColorUtils.SPELL_WHITE, false);
    }
}

