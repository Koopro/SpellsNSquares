package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Event handler for keyboard navigation in SpellSelectionScreen.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class SpellSelectionScreenKeyHandler {
    
    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (event.getScreen() instanceof SpellSelectionScreen screen) {
            int keyCode = event.getKeyCode();
            
            // Number keys 1-4: Switch slots
            if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_4) {
                int slotIndex = keyCode - GLFW.GLFW_KEY_1;
                int[] slots = {
                    at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_TOP,
                    at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_BOTTOM,
                    at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_LEFT,
                    at.koopro.spells_n_squares.features.spell.manager.SpellManager.SLOT_RIGHT
                };
                if (slotIndex >= 0 && slotIndex < slots.length) {
                    screen.handleSlotSwitch(slots[slotIndex]);
                    event.setCanceled(true);
                    return;
                }
            }
            
            // F1: Toggle help overlay
            if (keyCode == GLFW.GLFW_KEY_F1) {
                screen.toggleHelpOverlay();
                event.setCanceled(true);
                return;
            }
            
            // Arrow keys, Enter, F, Escape, Home, End
            if (screen.handleKeyboardNavigation(keyCode)) {
                event.setCanceled(true);
            }
        }
    }
}

