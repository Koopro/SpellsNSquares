package at.koopro.spells_n_squares.features.contracts.client;

import at.koopro.spells_n_squares.features.contracts.ContractItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * GUI screen for creating and editing contracts.
 */
public class ContractCreationScreen extends Screen {
    private EditBox contractTypeField;
    private EditBox partiesField;
    private EditBox termsField;
    private EditBox conditionsField;
    private Checkbox unbreakableVowCheckbox;
    private Button createButton;
    private Button cancelButton;
    private final ItemStack contractStack;
    
    public ContractCreationScreen(ItemStack contractStack) {
        super(Component.translatable("gui.spells_n_squares.contract.create"));
        this.contractStack = contractStack;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 60;
        int fieldWidth = 300;
        int fieldHeight = 20;
        int spacing = 25;
        
        // Contract type field
        this.contractTypeField = new EditBox(this.font, centerX - fieldWidth / 2, startY, fieldWidth, fieldHeight,
            Component.translatable("gui.spells_n_squares.contract.type"));
        this.contractTypeField.setMaxLength(64);
        this.addRenderableWidget(contractTypeField);
        
        // Parties field (comma-separated)
        this.partiesField = new EditBox(this.font, centerX - fieldWidth / 2, startY + spacing, fieldWidth, fieldHeight,
            Component.translatable("gui.spells_n_squares.contract.parties"));
        this.partiesField.setMaxLength(256);
        this.addRenderableWidget(partiesField);
        
        // Terms field
        int termsHeight = 60;
        this.termsField = new EditBox(this.font, centerX - fieldWidth / 2, startY + spacing * 2, fieldWidth, termsHeight,
            Component.translatable("gui.spells_n_squares.contract.terms"));
        this.termsField.setMaxLength(500);
        this.addRenderableWidget(termsField);
        
        // Conditions field (optional)
        int conditionsHeight = 40;
        this.conditionsField = new EditBox(this.font, centerX - fieldWidth / 2, startY + spacing * 2 + termsHeight + spacing, fieldWidth, conditionsHeight,
            Component.translatable("gui.spells_n_squares.contract.conditions"));
        this.conditionsField.setMaxLength(300);
        this.addRenderableWidget(conditionsField);
        
        // Unbreakable Vow checkbox
        int checkboxY = startY + spacing * 2 + termsHeight + spacing + conditionsHeight + 10;
        this.unbreakableVowCheckbox = Checkbox.builder(
            Component.translatable("gui.spells_n_squares.contract.unbreakable_vow"),
            this.font
        ).pos(centerX - fieldWidth / 2, checkboxY)
         .selected(false)
         .build();
        this.addRenderableWidget(unbreakableVowCheckbox);
        
        // Buttons
        int buttonY = checkboxY + 30;
        this.createButton = Button.builder(
            Component.translatable("gui.spells_n_squares.contract.create"),
            button -> createContract()
        ).bounds(centerX - 105, buttonY, 100, 20).build();
        this.addRenderableWidget(createButton);
        
        this.cancelButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX + 5, buttonY, 100, 20).build();
        this.addRenderableWidget(cancelButton);
        
        // Set initial focus
        this.setInitialFocus(contractTypeField);
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
        
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.contract.type"), 
            centerX - 150, startY - 10, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.contract.parties"), 
            centerX - 150, startY + spacing - 10, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.contract.terms"), 
            centerX - 150, startY + spacing * 2 - 10, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.spells_n_squares.contract.conditions"), 
            centerX - 150, startY + spacing * 2 + 60 + spacing - 10, 0xFFFFFF);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    private void createContract() {
        String contractType = contractTypeField.getValue().trim();
        String parties = partiesField.getValue().trim();
        String terms = termsField.getValue().trim();
        boolean isUnbreakableVow = unbreakableVowCheckbox.selected();
        
        if (contractType.isEmpty() || parties.isEmpty() || terms.isEmpty()) {
            // Show error - required fields must be filled
            return;
        }
        
        // Create contract on item stack
        // Note: In a full implementation, this would send a packet to the server
        // For now, we'll just close the screen
        // The actual contract creation would be handled server-side
        
        this.minecraft.setScreen(null);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}


