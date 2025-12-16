package at.koopro.spells_n_squares.features.playerclass;

/**
 * Enum representing different player classes from the wizarding world.
 * Each class can have unique abilities, bonuses, or restrictions.
 * Players can have multiple classes simultaneously.
 */
public enum PlayerClass {
    // Base Classes
    NONE("None", "No special class", ClassCategory.BASE),
    WIZARD("Wizard", "A practitioner of magic in the wizarding world", ClassCategory.BASE),
    
    // Role Classes
    STUDENT("Student", "A student at Hogwarts School of Witchcraft and Wizardry", ClassCategory.ROLE),
    AUROR("Auror", "A highly trained law enforcement officer specializing in dark wizard capture", ClassCategory.ROLE),
    TEACHER("Teacher", "A professor at Hogwarts or other wizarding institution", ClassCategory.ROLE),
    HEALER("Healer", "A medical professional at St. Mungo's Hospital", ClassCategory.ROLE),
    SHOPKEEPER("Shopkeeper", "A merchant in Diagon Alley or other wizarding locations", ClassCategory.ROLE),
    QUIDDITCH_PLAYER("Quidditch Player", "A professional or amateur Quidditch player", ClassCategory.ROLE),
    
    // Organization Classes
    DEATH_EATER("Death Eater", "A follower of Lord Voldemort and the Dark Arts", ClassCategory.ORGANIZATION),
    ORDER_MEMBER("Order Member", "A member of the Order of the Phoenix", ClassCategory.ORGANIZATION),
    
    // Blood Status Classes
    MUGGLE_BORN("Muggle-Born", "A witch or wizard born to non-magical parents", ClassCategory.BLOOD_STATUS),
    PURE_BLOOD("Pure-Blood", "A wizard or witch with no Muggle ancestry", ClassCategory.BLOOD_STATUS),
    HALF_BLOOD("Half-Blood", "A wizard or witch with both magical and Muggle ancestry", ClassCategory.BLOOD_STATUS),
    SQUIB("Squib", "A non-magical person born to magical parents", ClassCategory.BLOOD_STATUS),
    
    // Alignment Classes
    GOOD("Good", "A force for good and justice in the wizarding world", ClassCategory.ALIGNMENT),
    EVIL("Evil", "Embraces darkness and chaos", ClassCategory.ALIGNMENT),
    DARK_WIZARD("Dark Wizard", "A practitioner of dark magic and forbidden arts", ClassCategory.ALIGNMENT),
    
    // Transformation Classes
    WEREWOLF("Werewolf", "Transforms into a wolf under the full moon", ClassCategory.TRANSFORMATION),
    VAMPIRE("Vampire", "An immortal being that feeds on blood", ClassCategory.TRANSFORMATION);
    
    private final String displayName;
    private final String description;
    private final ClassCategory category;
    
    PlayerClass(String displayName, String description, ClassCategory category) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the category of this class.
     * @return The class category
     */
    public ClassCategory getCategory() {
        return category;
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
