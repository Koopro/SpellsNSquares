package at.koopro.spells_n_squares.features.spell.client.components;

import at.koopro.spells_n_squares.features.spell.manager.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.SpellSelectionScreenConstants;
import at.koopro.spells_n_squares.features.spell.client.SpellSlotManager;
import at.koopro.spells_n_squares.features.spell.client.SpellUIConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Component for managing spell slot selection buttons.
 * Handles creation and layout of slot selection buttons.
 */
public final class SpellSlotSelectorComponent {
    private SpellSlotSelectorComponent() {
    }
    
    /**
     * Creates slot selection buttons.
     * 
     * @param selectedSlot Currently selected slot
     * @param centerX Center X coordinate of the screen
     * @param y Y coordinate for buttons
     * @param onSlotSelected Callback when a slot is selected
     * @return List of created slot buttons
     */
    public static List<Button> createSlotButtons(
            int selectedSlot,
            int centerX,
            int y,
            Consumer<Integer> onSlotSelected) {
        
        List<Button> buttons = new ArrayList<>();
        int[] slots = {
            SpellManager.SLOT_TOP,
            SpellManager.SLOT_BOTTOM,
            SpellManager.SLOT_LEFT,
            SpellManager.SLOT_RIGHT
        };
        
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            String buttonText = SpellSlotManager.getSlotButtonText(slot, selectedSlot);
            int buttonX = SpellSlotManager.calculateSlotButtonX(centerX, i);
            int buttonWidth = SpellSlotManager.getSlotButtonWidth();
            
            Button button = createSlotButton(
                buttonText,
                slot == selectedSlot,
                buttonX,
                y,
                buttonWidth,
                () -> onSlotSelected.accept(slot)
            );
            
            buttons.add(button);
        }
        
        return buttons;
    }
    
    /**
     * Creates a single slot selection button.
     */
    private static Button createSlotButton(
            String text,
            boolean isSelected,
            int x,
            int y,
            int width,
            Runnable onClick) {
        
        Component buttonText = Component.literal(text);
        if (isSelected) {
            buttonText = buttonText.copy().withStyle(
                style -> style.withColor(SpellUIConstants.TEXT_COLOR_SELECTED)
            );
        }
        
        return Button.builder(buttonText, button -> onClick.run())
            .bounds(x, y, width, SpellSelectionScreenConstants.BUTTON_HEIGHT)
            .build();
    }
}

