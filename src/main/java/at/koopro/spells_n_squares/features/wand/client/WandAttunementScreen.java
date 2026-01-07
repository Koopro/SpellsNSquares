package at.koopro.spells_n_squares.features.wand.client;

import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import at.koopro.spells_n_squares.features.wand.system.WandAttunementHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * GUI screen for wand attunement ritual.
 * Shows the required spell sequence and current progress.
 */
public class WandAttunementScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = 
        Identifier.fromNamespaceAndPath("spells_n_squares", "textures/gui/wand_attunement.png");
    
    private Button startButton;
    private Button cancelButton;
    
    public WandAttunementScreen() {
        super(Component.translatable("gui.spells_n_squares.wand_attunement.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = this.height / 2 - 50;
        
        // Start attunement button
        this.startButton = Button.builder(
            Component.translatable("gui.spells_n_squares.wand_attunement.start"),
            button -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    WandAttunementHandler.startAttunement(
                        this.minecraft.player, 
                        this.minecraft.player.level()
                    );
                    this.minecraft.setScreen(null);
                }
            }
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(startButton);
        
        // Cancel button
        this.cancelButton = Button.builder(
            Component.translatable("gui.cancel"),
            button -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(null);
                }
            }
        ).bounds(centerX - 100, startY + 30, 200, 20).build();
        this.addRenderableWidget(cancelButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }
        
        int centerX = this.width / 2;
        int startY = this.height / 2;
        
        // Get wand info
        ItemStack wand = this.minecraft.player.getMainHandItem();
        if (wand.isEmpty()) {
            wand = this.minecraft.player.getOffhandItem();
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        WandWood wood = WandDataHelper.getWood(wand);
        boolean isAttuned = WandDataHelper.isAttuned(wand);
        boolean isAttuning = WandAttunementHandler.isAttuning(this.minecraft.player);
        
        // Display wand information
        if (core != null && wood != null) {
            guiGraphics.drawString(
                this.font,
                ColorUtils.coloredText("Wand: " + wood.name() + " with " + core.name(), ColorUtils.SPELL_GOLD),
                centerX - 100, startY, ColorUtils.SPELL_WHITE, false
            );
            
            if (isAttuned) {
                guiGraphics.drawString(
                    this.font,
                    ColorUtils.coloredText("Status: Attuned", ColorUtils.SPELL_GREEN),
                    centerX - 100, startY + 20, ColorUtils.SPELL_WHITE, false
                );
            } else if (isAttuning) {
                guiGraphics.drawString(
                    this.font,
                    ColorUtils.coloredText("Status: Attuning...", ColorUtils.SPELL_GOLD),
                    centerX - 100, startY + 20, ColorUtils.SPELL_WHITE, false
                );
                
                // Show required sequence
                guiGraphics.drawString(
                    this.font,
                    ColorUtils.coloredText("Cast: Lumos -> Accio -> Protego", ColorUtils.SPELL_WHITE),
                    centerX - 100, startY + 40, ColorUtils.SPELL_WHITE, false
                );
            } else {
                guiGraphics.drawString(
                    this.font,
                    ColorUtils.coloredText("Status: Not Attuned", ColorUtils.SPELL_RED),
                    centerX - 100, startY + 20, ColorUtils.SPELL_WHITE, false
                );
            }
        }
        
        // Render tooltips for buttons
        renderTooltipForButton(guiGraphics, startButton, "gui.spells_n_squares.wand_attunement.start_tooltip", mouseX, mouseY);
        renderTooltipForButton(guiGraphics, cancelButton, "gui.spells_n_squares.wand_attunement.cancel_tooltip", mouseX, mouseY);
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

