package at.koopro.spells_n_squares.features.spell.client.components;

import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.base.SpellCategory;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.resources.Identifier;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages filtering and sorting of spells for the spell selection screen.
 * Separated from SpellSelectionScreen for better code organization.
 */
public final class SpellFilterManager {
    private SpellFilterManager() {
    }
    
    /**
     * Sort types for spell list.
     */
    public enum SortType {
        ALPHABETICAL("A-Z"),
        REVERSE_ALPHABETICAL("Z-A"),
        COOLDOWN_LOW("CD Low"),
        COOLDOWN_HIGH("CD High");
        
        private final String displayName;
        
        SortType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Filter state for spell selection.
     */
    public static class FilterState {
        private String searchText = "";
        private SpellCategory selectedCategory = SpellCategory.ALL;
        private SortType sortType = SortType.ALPHABETICAL;
        
        public String getSearchText() {
            return searchText;
        }
        
        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }
        
        public SpellCategory getSelectedCategory() {
            return selectedCategory;
        }
        
        public void setSelectedCategory(SpellCategory selectedCategory) {
            this.selectedCategory = selectedCategory;
        }
        
        public SortType getSortType() {
            return sortType;
        }
        
        public void setSortType(SortType sortType) {
            this.sortType = sortType;
        }
        
        public void cycleSortType() {
            this.sortType = SortType.values()[(sortType.ordinal() + 1) % SortType.values().length];
        }
    }
    
    /**
     * Filters and sorts a list of spells based on the filter state.
     * 
     * @param allSpells The complete list of spell IDs
     * @param filterState The current filter state
     * @return Filtered and sorted list of spell IDs
     */
    public static List<Identifier> filterAndSort(List<Identifier> allSpells, FilterState filterState) {
        return allSpells.stream()
            .filter(spellId -> matchesFilter(spellId, filterState))
            .sorted(getComparator(filterState.getSortType()))
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if a spell matches the current filter criteria.
     */
    private static boolean matchesFilter(Identifier spellId, FilterState filterState) {
        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            return false;
        }
        
        // Category filter
        if (filterState.getSelectedCategory() != SpellCategory.ALL) {
            SpellCategory spellCategory = SpellCategory.fromSpellId(spellId);
            if (spellCategory != filterState.getSelectedCategory()) {
                return false;
            }
        }
        
        // Search filter
        String searchText = filterState.getSearchText();
        if (!searchText.isEmpty()) {
            String lowerSearch = searchText.toLowerCase();
            String lowerName = spell.getName() != null ? spell.getName().toLowerCase() : "";
            if (!lowerName.contains(lowerSearch)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the comparator for the specified sort type.
     */
    private static Comparator<Identifier> getComparator(SortType sortType) {
        return switch (sortType) {
            case ALPHABETICAL -> Comparator.<Identifier, String>comparing(id -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getName() : id.toString();
            });
            case REVERSE_ALPHABETICAL -> Comparator.<Identifier, String>comparing(id -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getName() : id.toString();
            }).reversed();
            case COOLDOWN_LOW -> Comparator.comparingInt((Identifier id) -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getCooldown() : Integer.MAX_VALUE;
            });
            case COOLDOWN_HIGH -> Comparator.<Identifier, Integer>comparing((Identifier id) -> {
                Spell spell = SpellRegistry.get(id);
                return spell != null ? spell.getCooldown() : 0;
            }).reversed();
        };
    }
}

