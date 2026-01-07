package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Event handler for mouse interactions in SpellSelectionScreen.
 * Handles right-click context menu.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class SpellSelectionScreenMouseHandler {
    
    @SubscribeEvent
    public static void onMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getScreen() instanceof SpellSelectionScreen screen) {
            // Right-click (button 1) on spell buttons toggles favorite
            if (event.getButton() == 1) {
                double mouseX = event.getMouseX();
                double mouseY = event.getMouseY();
                
                // Check if clicking on a spell button
                int centerX = screen.width / 2;
                int buttonX = centerX - SpellSelectionScreenConstants.BUTTON_WIDTH / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4;
                int buttonWidth = SpellSelectionScreenConstants.BUTTON_WIDTH - SpellUIConstants.ICON_SIZE_SCREEN - 4;
                
                for (java.util.Map.Entry<net.minecraft.resources.Identifier, Integer> entry : 
                     screen.getSpellButtonPositions().entrySet()) {
                    int buttonY = entry.getValue();
                    if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                        mouseY >= buttonY && mouseY <= buttonY + SpellSelectionScreenConstants.BUTTON_HEIGHT) {
                        screen.handleSpellRightClick(entry.getKey());
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
}


