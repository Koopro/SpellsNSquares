package at.koopro.spells_n_squares.features.artifacts.client;

import at.koopro.spells_n_squares.features.artifacts.MaraudersMapData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * GUI screen for viewing player locations on Marauder's Map.
 */
public class MaraudersMapScreen extends Screen {
    private final List<MaraudersMapData.PlayerLocation> playerLocations;
    private int scrollOffset = 0;
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;
    private static final int START_Y = 60;
    private static final int MAX_VISIBLE = 10;
    
    /**
     * Constructor that accepts player location list directly (used for network payload).
     */
    public MaraudersMapScreen(List<MaraudersMapData.PlayerLocation> playerLocations) {
        super(Component.translatable("gui.spells_n_squares.marauders_map.title"));
        this.playerLocations = playerLocations;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int currentY = START_Y;
        
        // Display player locations
        int endIndex = Math.min(scrollOffset + MAX_VISIBLE, playerLocations.size());
        for (int i = scrollOffset; i < endIndex; i++) {
            final MaraudersMapData.PlayerLocation location = playerLocations.get(i);
            final int buttonY = currentY;
            
            String buttonText = location.playerName() + " - " + 
                String.format("%.0f, %.0f, %.0f", location.x(), location.y(), location.z());
            if (buttonText.length() > 40) {
                buttonText = buttonText.substring(0, 37) + "...";
            }
            
            Button locationButton = Button.builder(
                Component.literal(buttonText),
                button -> {
                    // Could add teleport or highlight functionality here
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(locationButton);
            currentY += BUTTON_HEIGHT + BUTTON_SPACING;
        }
        
        // Scroll buttons
        if (scrollOffset > 0) {
            this.addRenderableWidget(Button.builder(
                Component.literal("↑"),
                button -> {
                    scrollOffset = Math.max(0, scrollOffset - MAX_VISIBLE);
                    this.init();
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, START_Y - 25, BUTTON_WIDTH / 2 - 2, 20).build());
        }
        
        if (scrollOffset + MAX_VISIBLE < playerLocations.size()) {
            this.addRenderableWidget(Button.builder(
                Component.literal("↓"),
                button -> {
                    scrollOffset = Math.min(playerLocations.size() - MAX_VISIBLE, scrollOffset + MAX_VISIBLE);
                    this.init();
                }
            ).bounds(centerX + 2, START_Y - 25, BUTTON_WIDTH / 2 - 2, 20).build());
        }
        
        // Close button
        Button closeButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(closeButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render dark background
        guiGraphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Draw player count
        String countText = Component.translatable("gui.spells_n_squares.marauders_map.player_count", playerLocations.size()).getString();
        guiGraphics.drawCenteredString(this.font, countText, this.width / 2, 40, 0xCCCCCC);
        
        // Render buttons (handled by super)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}







