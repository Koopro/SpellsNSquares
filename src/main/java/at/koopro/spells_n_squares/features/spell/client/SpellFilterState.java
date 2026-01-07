package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.features.spell.base.SpellCategory;

/**
 * Manages filter state for spell selection screen.
 */
public class SpellFilterState {
    private String searchText = "";
    private SpellCategory selectedCategory = SpellCategory.ALL;
    private SpellSortType sortType = SpellSortType.ALPHABETICAL;
    private boolean showFavoritesOnly = false;
    private boolean showRecentOnly = false;
    
    /**
     * Gets the current search text.
     */
    public String getSearchText() {
        return searchText;
    }
    
    /**
     * Sets the search text.
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText != null ? searchText : "";
    }
    
    /**
     * Gets the selected category.
     */
    public SpellCategory getSelectedCategory() {
        return selectedCategory;
    }
    
    /**
     * Sets the selected category.
     */
    public void setSelectedCategory(SpellCategory category) {
        this.selectedCategory = category != null ? category : SpellCategory.ALL;
    }
    
    /**
     * Gets the sort type.
     */
    public SpellSortType getSortType() {
        return sortType;
    }
    
    /**
     * Sets the sort type.
     */
    public void setSortType(SpellSortType sortType) {
        this.sortType = sortType != null ? sortType : SpellSortType.ALPHABETICAL;
    }
    
    /**
     * Cycles to the next sort type.
     */
    public void cycleSortType() {
        SpellSortType[] types = SpellSortType.values();
        int currentIndex = sortType.ordinal();
        int nextIndex = (currentIndex + 1) % types.length;
        setSortType(types[nextIndex]);
    }
    
    /**
     * Checks if favorites-only filter is enabled.
     */
    public boolean isShowFavoritesOnly() {
        return showFavoritesOnly;
    }
    
    /**
     * Sets the favorites-only filter.
     */
    public void setShowFavoritesOnly(boolean showFavoritesOnly) {
        this.showFavoritesOnly = showFavoritesOnly;
    }
    
    /**
     * Toggles the favorites-only filter.
     */
    public void toggleFavoritesOnly() {
        this.showFavoritesOnly = !this.showFavoritesOnly;
        if (this.showFavoritesOnly) {
            this.showRecentOnly = false; // Mutually exclusive
        }
    }
    
    /**
     * Checks if recent-only filter is enabled.
     */
    public boolean isShowRecentOnly() {
        return showRecentOnly;
    }
    
    /**
     * Sets the recent-only filter.
     */
    public void setShowRecentOnly(boolean showRecentOnly) {
        this.showRecentOnly = showRecentOnly;
        if (this.showRecentOnly) {
            this.showFavoritesOnly = false; // Mutually exclusive
        }
    }
    
    /**
     * Toggles the recent-only filter.
     */
    public void toggleRecentOnly() {
        this.showRecentOnly = !this.showRecentOnly;
        if (this.showRecentOnly) {
            this.showFavoritesOnly = false; // Mutually exclusive
        }
    }
    
    /**
     * Resets all filters to default values.
     */
    public void reset() {
        searchText = "";
        selectedCategory = SpellCategory.ALL;
        sortType = SpellSortType.ALPHABETICAL;
        showFavoritesOnly = false;
        showRecentOnly = false;
    }
    
    /**
     * Copies filter state from another SpellFilterState.
     */
    public void copyFrom(SpellFilterState other) {
        if (other != null) {
            this.searchText = other.searchText;
            this.selectedCategory = other.selectedCategory;
            this.sortType = other.sortType;
            this.showFavoritesOnly = other.showFavoritesOnly;
            this.showRecentOnly = other.showRecentOnly;
        }
    }
}

