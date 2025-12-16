package at.koopro.spells_n_squares.features.convenience.client;

import at.koopro.spells_n_squares.features.convenience.WaypointSystem;
import at.koopro.spells_n_squares.features.convenience.network.WaypointSelectionPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * GUI screen for selecting a waypoint to teleport to with Apparition.
 */
public class WaypointSelectionScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;
    private static final int START_Y = 60;
    private static final int SIDE_MARGIN = 20;
    
    private final List<WaypointSystem.Waypoint> waypoints;
    
    public WaypointSelectionScreen(List<WaypointSystem.Waypoint> waypoints) {
        super(Component.literal("Select Waypoint"));
        this.waypoints = waypoints;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int currentY = START_Y;
        
        // Add title
        // Title is handled by super class
        
        // Add waypoint buttons
        for (WaypointSystem.Waypoint waypoint : waypoints) {
            final String waypointName = waypoint.name();
            final int buttonY = currentY;
            
            // Create button with waypoint name and position info
            String buttonText = waypointName + " (" + 
                waypoint.position().getX() + ", " + 
                waypoint.position().getY() + ", " + 
                waypoint.position().getZ() + ")";
            
            Button waypointButton = Button.builder(
                Component.literal(buttonText),
                button -> {
                    // Send waypoint selection to server
                    WaypointSelectionPayload payload = new WaypointSelectionPayload(waypointName);
                    ClientPacketDistributor.sendToServer(payload);
                    // Close screen
                    this.minecraft.setScreen(null);
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(waypointButton);
            currentY += BUTTON_HEIGHT + BUTTON_SPACING;
        }
        
        // Add cancel button at the bottom
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
        
        // Draw instruction text
        String instruction = "Select a waypoint to Apparate to:";
        guiGraphics.drawCenteredString(this.font, instruction, this.width / 2, 40, 0xCCCCCC);
        
        // Render buttons (handled by super)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }
}








