package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

/**
 * Manages UI state for spell selection screen.
 */
public class SpellSelectionState {
    private int selectedSlot;
    private int scrollOffset = 0;
    private Identifier hoveredSpellId = null;
    private int keyboardSelectedIndex = -1;
    private List<Identifier> filteredSpells = CollectionFactory.createList();
    private final Map<Identifier, Integer> spellButtonPositions = CollectionFactory.createMap();
    
    /**
     * Gets the selected slot.
     */
    public int getSelectedSlot() {
        return selectedSlot;
    }
    
    /**
     * Sets the selected slot.
     */
    public void setSelectedSlot(int slot) {
        this.selectedSlot = slot;
    }
    
    /**
     * Gets the scroll offset.
     */
    public int getScrollOffset() {
        return scrollOffset;
    }
    
    /**
     * Sets the scroll offset.
     */
    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = Math.max(0, scrollOffset);
    }
    
    /**
     * Increments the scroll offset.
     */
    public void scrollDown(int maxScroll) {
        if (scrollOffset < maxScroll) {
            scrollOffset++;
        }
    }
    
    /**
     * Decrements the scroll offset.
     */
    public void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
        }
    }
    
    /**
     * Gets the hovered spell ID.
     */
    public Identifier getHoveredSpellId() {
        return hoveredSpellId;
    }
    
    /**
     * Sets the hovered spell ID.
     */
    public void setHoveredSpellId(Identifier spellId) {
        this.hoveredSpellId = spellId;
    }
    
    /**
     * Gets the keyboard selected index.
     */
    public int getKeyboardSelectedIndex() {
        return keyboardSelectedIndex;
    }
    
    /**
     * Sets the keyboard selected index.
     */
    public void setKeyboardSelectedIndex(int index) {
        this.keyboardSelectedIndex = index;
    }
    
    /**
     * Resets keyboard selection.
     */
    public void resetKeyboardSelection() {
        this.keyboardSelectedIndex = -1;
    }
    
    /**
     * Gets the filtered spells list.
     */
    public List<Identifier> getFilteredSpells() {
        return filteredSpells;
    }
    
    /**
     * Sets the filtered spells list.
     */
    public void setFilteredSpells(List<Identifier> spells) {
        this.filteredSpells = spells != null ? spells : CollectionFactory.createList();
    }
    
    /**
     * Gets the spell button positions map.
     */
    public Map<Identifier, Integer> getSpellButtonPositions() {
        return spellButtonPositions;
    }
    
    /**
     * Clears spell button positions.
     */
    public void clearSpellButtonPositions() {
        spellButtonPositions.clear();
    }
    
    /**
     * Resets scroll when filters change.
     */
    public void resetScrollOnFilterChange() {
        scrollOffset = 0;
        keyboardSelectedIndex = -1;
    }
    
    /**
     * Copies state from another SpellSelectionState.
     */
    public void copyFrom(SpellSelectionState other) {
        if (other != null) {
            this.selectedSlot = other.selectedSlot;
            this.scrollOffset = other.scrollOffset;
            this.hoveredSpellId = other.hoveredSpellId;
            this.keyboardSelectedIndex = other.keyboardSelectedIndex;
            this.filteredSpells = CollectionFactory.createListFrom(other.filteredSpells);
        }
    }
}

