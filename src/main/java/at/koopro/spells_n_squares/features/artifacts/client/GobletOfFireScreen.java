package at.koopro.spells_n_squares.features.artifacts.client;

import at.koopro.spells_n_squares.features.artifacts.GobletOfFireData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GUI screen for viewing Goblet of Fire tournament status.
 */
public class GobletOfFireScreen extends Screen {
    private final List<GobletOfFireData.Participant> participants;
    private final Set<UUID> champions;
    private final boolean tournamentActive;
    private int scrollOffset = 0;
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;
    private static final int START_Y = 80;
    private static final int MAX_VISIBLE = 10;
    
    /**
     * Constructor that accepts tournament data directly (used for network payload).
     */
    public GobletOfFireScreen(
        List<GobletOfFireData.Participant> participants,
        Set<UUID> champions,
        boolean tournamentActive
    ) {
        super(Component.translatable("gui.spells_n_squares.goblet_of_fire.title"));
        this.participants = participants;
        this.champions = champions;
        this.tournamentActive = tournamentActive;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int currentY = START_Y;
        
        // Display tournament status
        String statusText = tournamentActive 
            ? Component.translatable("gui.spells_n_squares.goblet_of_fire.active").getString()
            : Component.translatable("gui.spells_n_squares.goblet_of_fire.inactive").getString();
        // Status will be drawn in render method
        
        // Display champions section
        if (!champions.isEmpty()) {
            currentY += 30;
            // Champions header will be drawn in render
        }
        
        // Display participants
        int endIndex = Math.min(scrollOffset + MAX_VISIBLE, participants.size());
        for (int i = scrollOffset; i < endIndex; i++) {
            final GobletOfFireData.Participant participant = participants.get(i);
            final int buttonY = currentY;
            final boolean isChampion = champions.contains(participant.playerId());
            
            String buttonText = participant.playerName();
            if (isChampion) {
                buttonText = "★ " + buttonText + " ★";
            }
            
            Button participantButton = Button.builder(
                Component.literal(buttonText),
                button -> {
                    // Could add more details here
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(participantButton);
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
        
        if (scrollOffset + MAX_VISIBLE < participants.size()) {
            this.addRenderableWidget(Button.builder(
                Component.literal("↓"),
                button -> {
                    scrollOffset = Math.min(participants.size() - MAX_VISIBLE, scrollOffset + MAX_VISIBLE);
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
        
        // Draw tournament status
        String statusText = tournamentActive 
            ? Component.translatable("gui.spells_n_squares.goblet_of_fire.active").getString()
            : Component.translatable("gui.spells_n_squares.goblet_of_fire.inactive").getString();
        guiGraphics.drawCenteredString(this.font, statusText, this.width / 2, 40, tournamentActive ? 0xFFFF00 : 0xCCCCCC);
        
        // Draw participant count
        String countText = Component.translatable("gui.spells_n_squares.goblet_of_fire.participants", participants.size()).getString();
        guiGraphics.drawCenteredString(this.font, countText, this.width / 2, 55, 0xCCCCCC);
        
        // Draw champions header if there are champions
        if (!champions.isEmpty()) {
            String championsText = Component.translatable("gui.spells_n_squares.goblet_of_fire.champions", champions.size()).getString();
            guiGraphics.drawCenteredString(this.font, championsText, this.width / 2, 70, 0xFFD700);
        }
        
        // Render buttons (handled by super)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}











