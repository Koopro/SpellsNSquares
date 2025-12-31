package at.koopro.spells_n_squares.features.convenience.client;

import at.koopro.spells_n_squares.features.convenience.system.WaypointSystem;
import at.koopro.spells_n_squares.features.convenience.network.WaypointManagementPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI screen for managing waypoints.
 * Allows players to create, delete, rename, and teleport to waypoints.
 */
public class WaypointManagementScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;
    private static final int START_Y = 60;
    private static final int MAX_VISIBLE_WAYPOINTS = 10;
    
    private List<WaypointSystem.Waypoint> waypoints = new ArrayList<>();
    private int scrollOffset = 0;
    private EditBox createNameBox;
    private EditBox renameNameBox;
    private String selectedWaypoint = null;
    private Button createButton;
    private Button deleteButton;
    private Button renameButton;
    private Button teleportButton;
    
    public WaypointManagementScreen() {
        super(Component.translatable("gui.spells_n_squares.waypoint_management.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int currentY = START_Y;
        
        // Request waypoint list from server
        ClientPacketDistributor.sendToServer(WaypointManagementPayload.requestList());
        
        // Create waypoint section
        int createSectionY = currentY;
        
        createNameBox = new EditBox(this.font, centerX - BUTTON_WIDTH / 2, createSectionY, BUTTON_WIDTH - 60, BUTTON_HEIGHT, 
            Component.translatable("gui.spells_n_squares.waypoint_management.name"));
        this.addRenderableWidget(createNameBox);
        
        createButton = Button.builder(
            Component.translatable("gui.spells_n_squares.waypoint_management.create_button"),
            button -> {
                String name = createNameBox.getValue().trim();
                if (!name.isEmpty() && this.minecraft != null && this.minecraft.player != null) {
                    WaypointManagementPayload payload = WaypointManagementPayload.create(
                        name,
                        this.minecraft.player.level().dimension(),
                        this.minecraft.player.blockPosition()
                    );
                    ClientPacketDistributor.sendToServer(payload);
                    createNameBox.setValue(""); // Clear input
                    // Refresh list - request will be handled by server response
                }
            }
        ).bounds(centerX + BUTTON_WIDTH / 2 - 55, createSectionY, 55, BUTTON_HEIGHT).build();
        this.addRenderableWidget(createButton);
        
        currentY += BUTTON_HEIGHT + BUTTON_SPACING + 10;
        
        // Waypoint list section
        currentY += 20;
        
        // Scroll buttons
        if (waypoints.size() > MAX_VISIBLE_WAYPOINTS) {
            Button scrollUp = Button.builder(
                Component.literal("↑"),
                button -> {
                    if (scrollOffset > 0) scrollOffset--;
                }
            ).bounds(centerX + BUTTON_WIDTH / 2 + 5, currentY, 20, BUTTON_HEIGHT).build();
            this.addRenderableWidget(scrollUp);
            
            Button scrollDown = Button.builder(
                Component.literal("↓"),
                button -> {
                    int maxScroll = Math.max(0, waypoints.size() - MAX_VISIBLE_WAYPOINTS);
                    if (scrollOffset < maxScroll) scrollOffset++;
                }
            ).bounds(centerX + BUTTON_WIDTH / 2 + 5, currentY + BUTTON_HEIGHT + 2, 20, BUTTON_HEIGHT * MAX_VISIBLE_WAYPOINTS).build();
            this.addRenderableWidget(scrollDown);
        }
        
        // Waypoint buttons
        int listStartY = currentY;
        int visibleCount = Math.min(MAX_VISIBLE_WAYPOINTS, waypoints.size() - scrollOffset);
        for (int i = 0; i < visibleCount; i++) {
            int waypointIndex = i + scrollOffset;
            if (waypointIndex >= waypoints.size()) break;
            
            WaypointSystem.Waypoint waypoint = waypoints.get(waypointIndex);
            final String waypointName = waypoint.name();
            final int buttonY = listStartY + i * (BUTTON_HEIGHT + BUTTON_SPACING);
            
            String buttonText = waypointName + " (" + 
                waypoint.position().getX() + ", " + 
                waypoint.position().getY() + ", " + 
                waypoint.position().getZ() + ")";
            
            Button waypointButton = Button.builder(
                Component.literal(buttonText),
                button -> {
                    selectedWaypoint = waypointName;
                    updateActionButtons();
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            // Highlight selected waypoint
            if (waypointName.equals(selectedWaypoint)) {
                waypointButton.setFocused(true);
            }
            
            this.addRenderableWidget(waypointButton);
        }
        
        currentY += visibleCount * (BUTTON_HEIGHT + BUTTON_SPACING) + 10;
        
        // Action buttons section
        int actionY = currentY;
        
        // Rename section
        renameNameBox = new EditBox(this.font, centerX - BUTTON_WIDTH / 2, actionY, BUTTON_WIDTH - 60, BUTTON_HEIGHT,
            Component.translatable("gui.spells_n_squares.waypoint_management.new_name"));
        this.addRenderableWidget(renameNameBox);
        
        renameButton = Button.builder(
            Component.translatable("gui.spells_n_squares.waypoint_management.rename"),
            button -> {
                if (selectedWaypoint != null && !renameNameBox.getValue().trim().isEmpty()) {
                    WaypointManagementPayload payload = WaypointManagementPayload.rename(
                        selectedWaypoint,
                        renameNameBox.getValue().trim()
                    );
                    ClientPacketDistributor.sendToServer(payload);
                    renameNameBox.setValue("");
                    selectedWaypoint = null;
                    // Refresh list - request will be handled by server response
                }
            }
        ).bounds(centerX + BUTTON_WIDTH / 2 - 55, actionY, 55, BUTTON_HEIGHT).build();
        this.addRenderableWidget(renameButton);
        
        actionY += BUTTON_HEIGHT + BUTTON_SPACING;
        
        // Delete and Teleport buttons
        deleteButton = Button.builder(
            Component.translatable("gui.spells_n_squares.waypoint_management.delete"),
            button -> {
                if (selectedWaypoint != null) {
                    WaypointManagementPayload payload = WaypointManagementPayload.delete(selectedWaypoint);
                    ClientPacketDistributor.sendToServer(payload);
                    selectedWaypoint = null;
                    // Refresh list - request will be handled by server response
                }
            }
        ).bounds(centerX - BUTTON_WIDTH / 2, actionY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT).build();
        this.addRenderableWidget(deleteButton);
        
        teleportButton = Button.builder(
            Component.translatable("gui.spells_n_squares.waypoint_management.teleport"),
            button -> {
                if (selectedWaypoint != null) {
                    WaypointManagementPayload payload = WaypointManagementPayload.teleport(selectedWaypoint);
                    ClientPacketDistributor.sendToServer(payload);
                    this.minecraft.setScreen(null); // Close screen after teleport
                }
            }
        ).bounds(centerX + 2, actionY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT).build();
        this.addRenderableWidget(teleportButton);
        
        actionY += BUTTON_HEIGHT + BUTTON_SPACING;
        
        // Close button
        Button closeButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> this.minecraft.setScreen(null)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(closeButton);
        
        updateActionButtons();
    }
    
    /**
     * Updates the waypoint list from server response.
     */
    public void updateWaypointList(List<WaypointSystem.Waypoint> waypoints) {
        this.waypoints = waypoints;
        // Reset scroll if needed
        if (scrollOffset >= waypoints.size()) {
            scrollOffset = Math.max(0, waypoints.size() - MAX_VISIBLE_WAYPOINTS);
        }
        // Clear and reinitialize widgets
        this.clearWidgets();
        this.init();
    }
    
    /**
     * Updates action buttons based on selection.
     */
    private void updateActionButtons() {
        boolean hasSelection = selectedWaypoint != null;
        deleteButton.active = hasSelection;
        renameButton.active = hasSelection && !renameNameBox.getValue().trim().isEmpty();
        teleportButton.active = hasSelection;
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render dark background
        guiGraphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Render buttons (handled by super)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public void tick() {
        super.tick();
        // Update action buttons when rename box changes
        if (renameNameBox != null && renameNameBox.isFocused()) {
            updateActionButtons();
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }
}

