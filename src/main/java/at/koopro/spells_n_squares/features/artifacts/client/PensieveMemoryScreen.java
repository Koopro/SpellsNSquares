package at.koopro.spells_n_squares.features.artifacts.client;

import at.koopro.spells_n_squares.features.artifacts.PensieveData;
import at.koopro.spells_n_squares.features.artifacts.PensieveItem;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * GUI screen for viewing memories stored in a Pensieve.
 */
public class PensieveMemoryScreen extends Screen {
    private final ItemStack pensieveStack;
    private final List<PensieveData.MemorySnapshot> memories;
    private int scrollOffset = 0;
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;
    private static final int START_Y = 60;
    private static final int MAX_VISIBLE = 10;
    
    /**
     * Constructor that accepts memory list directly (used for network payload).
     */
    public PensieveMemoryScreen(List<PensieveData.MemorySnapshot> memories) {
        super(Component.translatable("gui.spells_n_squares.pensieve.memories"));
        this.pensieveStack = ItemStack.EMPTY;
        this.memories = memories;
    }
    
    /**
     * Constructor that extracts memories from ItemStack (backward compatibility).
     */
    public PensieveMemoryScreen(ItemStack pensieveStack) {
        super(Component.translatable("gui.spells_n_squares.pensieve.memories"));
        this.pensieveStack = pensieveStack;
        this.memories = PensieveItem.getPensieveData(pensieveStack).memories();
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int currentY = START_Y;
        
        // Display memories
        int endIndex = Math.min(scrollOffset + MAX_VISIBLE, memories.size());
        for (int i = scrollOffset; i < endIndex; i++) {
            final PensieveData.MemorySnapshot memory = memories.get(i);
            final int buttonY = currentY;
            
            String buttonText = memory.description() + " - " + memory.location();
            if (buttonText.length() > 40) {
                buttonText = buttonText.substring(0, 37) + "...";
            }
            
            Button memoryButton = Button.builder(
                Component.literal(buttonText),
                button -> {
                    // Play memory viewing animation/effect
                    ScreenEffectManager.triggerSpellFlash();
                    ScreenEffectManager.triggerOverlay(0x7F7FFF, 0.3f, 30, at.koopro.spells_n_squares.features.fx.ScreenEffectManager.ScreenOverlay.OverlayType.FLASH);
                    
                    // Show memory details (visual effect only - message would need server-side)
                    // The visual effects provide feedback that memory is being viewed
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            
            this.addRenderableWidget(memoryButton);
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
        
        if (scrollOffset + MAX_VISIBLE < memories.size()) {
            this.addRenderableWidget(Button.builder(
                Component.literal("↓"),
                button -> {
                    scrollOffset = Math.min(memories.size() - MAX_VISIBLE, scrollOffset + MAX_VISIBLE);
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
        
        // Draw memory count
        String countText = Component.translatable("gui.spells_n_squares.pensieve.memory_count", memories.size()).getString();
        guiGraphics.drawCenteredString(this.font, countText, this.width / 2, 40, 0xCCCCCC);
        
        // Render buttons (handled by super)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}






