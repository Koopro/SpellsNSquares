package at.koopro.spells_n_squares.features.spell.client.components;

import at.koopro.spells_n_squares.features.spell.Spell;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.spell.client.SpellSelectionScreenConstants;
import at.koopro.spells_n_squares.features.spell.client.SpellUIConstants;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Component for managing the spell list display in the spell selection screen.
 * Handles spell button creation, scrolling, and layout.
 */
public final class SpellListComponent {
    private SpellListComponent() {
    }
    
    /**
     * Creates spell buttons for the visible spells in the list.
     * 
     * @param filteredSpells The filtered list of spells to display
     * @param scrollOffset Current scroll offset
     * @param selectedSlot The currently selected spell slot
     * @param centerX Center X coordinate of the screen
     * @param startY Starting Y coordinate for spell buttons
     * @param maxY Maximum Y coordinate (buttons won't be created below this)
     * @param buttonWidth Width of spell buttons
     * @param onSpellSelected Callback when a spell is selected
     * @param buttonConsumer Consumer to add buttons to the screen
     * @return Map of spell ID to button Y position, and max visible spells count
     */
    public static SpellListResult createSpellButtons(
            List<Identifier> filteredSpells,
            int scrollOffset,
            int selectedSlot,
            int centerX,
            int startY,
            int maxY,
            int buttonWidth,
            Consumer<Identifier> onSpellSelected,
            Consumer<Button> buttonConsumer) {
        
        Map<Identifier, Integer> spellButtonPositions = new HashMap<>();
        int currentY = startY;
        int maxVisibleSpells = calculateMaxVisibleSpells(startY, maxY);
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + maxVisibleSpells, filteredSpells.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            if (currentY + SpellSelectionScreenConstants.BUTTON_HEIGHT > maxY) {
                break;
            }
            
            Identifier spellId = filteredSpells.get(i);
            Spell spell = SpellRegistry.get(spellId);
            if (spell == null) {
                continue;
            }
            
            spellButtonPositions.put(spellId, currentY);
            
            Button spellButton = createSpellButton(
                spellId,
                spell,
                selectedSlot,
                centerX - buttonWidth / 2 + SpellUIConstants.ICON_SIZE_SCREEN + 4,
                currentY,
                buttonWidth - SpellUIConstants.ICON_SIZE_SCREEN - 4,
                onSpellSelected
            );
            
            buttonConsumer.accept(spellButton);
            currentY += SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING;
        }
        
        return new SpellListResult(spellButtonPositions, maxVisibleSpells);
    }
    
    /**
     * Creates a single spell button.
     */
    private static Button createSpellButton(
            Identifier spellId,
            Spell spell,
            int selectedSlot,
            int x,
            int y,
            int width,
            Consumer<Identifier> onSpellSelected) {
        
        boolean isAssigned = spellId.equals(ClientSpellData.getSpellInSlot(selectedSlot));
        boolean isOnCooldown = ClientSpellData.isOnCooldown(spellId);
        
        // Build button text
        StringBuilder buttonTextBuilder = new StringBuilder();
        buttonTextBuilder.append(spell.getName());
        
        if (isOnCooldown) {
            int cooldownTicks = ClientSpellData.getCooldown(spellId);
            float cooldownSeconds = cooldownTicks / 20.0f;
            String cooldownText = cooldownSeconds < 1.0f
                ? String.format(" (CD: %.1fs)", cooldownSeconds)
                : String.format(" (CD: %.0fs)", cooldownSeconds);
            buttonTextBuilder.append(cooldownText);
        } else if (spell.getCooldown() > 0) {
            float maxCooldownSeconds = spell.getCooldown() / 20.0f;
            buttonTextBuilder.append(String.format(" (CD: %.0fs)", maxCooldownSeconds));
        }
        
        Component buttonComponent = Component.literal(buttonTextBuilder.toString());
        if (isAssigned) {
            buttonComponent = buttonComponent.copy().withStyle(
                style -> style.withColor(SpellUIConstants.TEXT_COLOR_ASSIGNED)
            );
        }
        
        return Button.builder(buttonComponent, button -> onSpellSelected.accept(spellId))
            .bounds(x, y, width, SpellSelectionScreenConstants.BUTTON_HEIGHT)
            .build();
    }
    
    /**
     * Calculates the maximum number of visible spells based on available height.
     */
    public static int calculateMaxVisibleSpells(int startY, int maxY) {
        int availableHeight = maxY - startY;
        return Math.max(1, availableHeight / 
            (SpellSelectionScreenConstants.BUTTON_HEIGHT + SpellSelectionScreenConstants.BUTTON_SPACING));
    }
    
    /**
     * Result of creating spell buttons.
     */
    public static class SpellListResult {
        private final Map<Identifier, Integer> spellButtonPositions;
        private final int maxVisibleSpells;
        
        public SpellListResult(Map<Identifier, Integer> spellButtonPositions, int maxVisibleSpells) {
            this.spellButtonPositions = spellButtonPositions;
            this.maxVisibleSpells = maxVisibleSpells;
        }
        
        public Map<Identifier, Integer> getSpellButtonPositions() {
            return spellButtonPositions;
        }
        
        public int getMaxVisibleSpells() {
            return maxVisibleSpells;
        }
    }
}

