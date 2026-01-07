package at.koopro.spells_n_squares.features.spell.entity;

/**
 * Model type for dummy player entities.
 * Determines whether the dummy player uses Alex or Steve model.
 */
public enum DummyPlayerModelType {
    ALEX,
    STEVE;
    
    /**
     * Gets the model type from a string.
     * 
     * @param name The name (case-insensitive)
     * @return The model type, or STEVE as default
     */
    public static DummyPlayerModelType fromString(String name) {
        if (name == null) {
            return STEVE;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STEVE; // Default to Steve
        }
    }
    
    /**
     * Gets the string representation.
     * 
     * @return The lowercase name
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}




