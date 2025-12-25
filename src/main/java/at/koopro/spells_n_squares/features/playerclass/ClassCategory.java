package at.koopro.spells_n_squares.features.playerclass;

/**
 * Categories for player classes to determine stacking and conflict rules.
 */
public enum ClassCategory {
    /**
     * Base classes that define core playstyle.
     * Can stack with other categories.
     */
    BASE,
    
    /**
     * Transformation classes that apply to any base class.
     * Can stack with base and role classes.
     */
    TRANSFORMATION,
    
    /**
     * Role classes that define profession or position.
     * Can stack with base and transformation classes.
     */
    ROLE,
    
    /**
     * Organization classes that are typically mutually exclusive.
     * Cannot have multiple organization classes simultaneously.
     */
    ORGANIZATION,
    
    /**
     * Blood status classes that are mutually exclusive.
     * Only one blood status allowed per player.
     */
    BLOOD_STATUS,
    
    /**
     * Alignment classes that may conflict with each other.
     * Some combinations may be restricted.
     */
    ALIGNMENT
}











