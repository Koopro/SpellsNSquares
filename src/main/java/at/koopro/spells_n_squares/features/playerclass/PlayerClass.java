package at.koopro.spells_n_squares.features.playerclass;

/**
 * Enum representing different player classes.
 * Each class can have unique abilities, bonuses, or restrictions.
 */
public enum PlayerClass {
    NONE("None", "No special class"),
    GOOD("Good", "A force for good and justice"),
    EVIL("Evil", "Embraces darkness and chaos"),
    WEREWOLF("Werewolf", "Transforms under the full moon"),
    VAMPIRE("Vampire", "Feeds on blood to survive"),
    WIZARD("Wizard", "Masters of arcane magic"),
    WARRIOR("Warrior", "Skilled in combat and defense"),
    ROGUE("Rogue", "Stealthy and agile"),
    PALADIN("Paladin", "Holy warrior of light"),
    NECROMANCER("Necromancer", "Commands the undead");
    
    private final String displayName;
    private final String description;
    
    PlayerClass(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets a PlayerClass by name (case-insensitive).
     * @param name The name to search for
     * @return The PlayerClass, or NONE if not found
     */
    public static PlayerClass fromName(String name) {
        if (name == null) {
            return NONE;
        }
        
        for (PlayerClass playerClass : values()) {
            if (playerClass.name().equalsIgnoreCase(name) || 
                playerClass.displayName.equalsIgnoreCase(name)) {
                return playerClass;
            }
        }
        
        return NONE;
    }
}
