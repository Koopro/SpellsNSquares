package at.koopro.spells_n_squares.features.playerclass.client;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.network.ClassConflictResolutionPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * GUI screen for resolving class conflicts when trying to add a new class.
 * Shows conflicting classes and allows player to choose to replace them or cancel.
 */
public class ClassConflictResolutionScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;
    private static final int START_Y = 80;
    private static final int SIDE_MARGIN = 20;
    
    private final PlayerClass newClass;
    private final List<PlayerClass> conflictingClasses;
    private final String spellId; // The spell that triggered this
    
    public ClassConflictResolutionScreen(PlayerClass newClass, List<PlayerClass> conflictingClasses, String spellId) {
        super(Component.translatable("gui.spells_n_squares.class_conflict.title"));
        this.newClass = newClass;
        this.conflictingClasses = conflictingClasses;
        this.spellId = spellId;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int currentY = START_Y;
        
        // Add replace buttons for each conflicting class
        for (PlayerClass conflictingClass : conflictingClasses) {
            final PlayerClass classToRemove = conflictingClass;
            final int buttonY = currentY;
            
            String buttonText = Component.translatable(
                "gui.spells_n_squares.class_conflict.replace",
                conflictingClass.getDisplayName(),
                newClass.getDisplayName()
            ).getString();
            
            Button replaceButton = Button.builder(
                Component.literal(buttonText),
                button -> {
                    // Send resolution to server: replace conflicting class
                    ClassConflictResolutionPayload payload = new ClassConflictResolutionPayload(
                        spellId,
                        newClass,
                        classToRemove,
                        true // replace
                    );
                    ClientPacketDistributor.sendToServer(payload);
                    this.minecraft.setScreen(null);
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(replaceButton);
            currentY += BUTTON_HEIGHT + BUTTON_SPACING;
        }
        
        // Add cancel button
        Button cancelButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(cancelButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render dark background
        guiGraphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Draw conflict message
        String message = Component.translatable(
            "gui.spells_n_squares.class_conflict.message",
            newClass.getDisplayName()
        ).getString();
        guiGraphics.drawCenteredString(this.font, message, this.width / 2, 50, 0xCCCCCC);
        
        // Draw instruction
        String instruction = Component.translatable("gui.spells_n_squares.class_conflict.instruction").getString();
        guiGraphics.drawCenteredString(this.font, instruction, this.width / 2, 65, 0xAAAAAA);
        
        // Render buttons (handled by super)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }
}















