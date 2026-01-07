package at.koopro.spells_n_squares.features.spell.client;

/**
 * Sort types for spell list.
 */
public enum SpellSortType {
    ALPHABETICAL("A-Z"),
    REVERSE_ALPHABETICAL("Z-A"),
    COOLDOWN_LOW("CD Low"),
    COOLDOWN_HIGH("CD High");
    
    private final String displayName;
    
    SpellSortType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}


