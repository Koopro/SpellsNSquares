package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.features.spell.manager.SpellManager;

/**
 * Helper class for managing spell slot selection and assignment.
 */
public final class SpellSlotManager {
    private SpellSlotManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the display name for a slot.
     */
    public static String getSlotDisplayName(int slot) {
        return switch (slot) {
            case SpellManager.SLOT_TOP -> "Top (W)";
            case SpellManager.SLOT_BOTTOM -> "Bottom (S)";
            case SpellManager.SLOT_LEFT -> "Left (A)";
            case SpellManager.SLOT_RIGHT -> "Right (D)";
            default -> "Unknown";
        };
    }
    
    /**
     * Gets the button text for a slot selection button.
     */
    public static String getSlotButtonText(int slot, int selectedSlot) {
        String name = getSlotDisplayName(slot);
        return slot == selectedSlot ? "► " + name : name;
    }
    
    /**
     * Calculates the X position for slot buttons.
     */
    public static int calculateSlotButtonX(int centerX, int buttonIndex) {
        int totalWidth = SpellSelectionScreenConstants.BUTTON_WIDTH * 2 + SpellSelectionScreenConstants.BUTTON_SPACING * 2;
        int startX = centerX - totalWidth / 2;
        int buttonWidth = SpellSelectionScreenConstants.BUTTON_WIDTH / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2;
        return startX + buttonIndex * (buttonWidth + SpellSelectionScreenConstants.BUTTON_SPACING);
    }
    
    /**
     * Gets the width for slot selection buttons.
     */
    public static int getSlotButtonWidth() {
        return SpellSelectionScreenConstants.BUTTON_WIDTH / 2 - SpellSelectionScreenConstants.BUTTON_SPACING / 2;
    }
}



















