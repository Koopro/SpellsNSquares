package at.koopro.spells_n_squares.features.creatures;

import net.minecraft.resources.Identifier;

/**
 * Represents a type of magical creature.
 */
public class CreatureType {
    private final Identifier id;
    private final String name;
    private final String description;
    private final CreatureCategory category;
    private final boolean isTameable;
    private final boolean isHostile;
    
    public CreatureType(Identifier id, String name, String description, CreatureCategory category) {
        this(id, name, description, category, false, false);
    }
    
    public CreatureType(Identifier id, String name, String description, CreatureCategory category, 
                       boolean isTameable, boolean isHostile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.isTameable = isTameable;
        this.isHostile = isHostile;
    }
    
    public Identifier getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public CreatureCategory getCategory() {
        return category;
    }
    
    public boolean isTameable() {
        return isTameable;
    }
    
    public boolean isHostile() {
        return isHostile;
    }
    
    /**
     * Categories of magical creatures.
     */
    public enum CreatureCategory {
        COMPANION,  // Pet companions (owls, cats, toads)
        MOUNT,      // Mountable creatures (hippogriffs, thestrals)
        HOSTILE,    // Hostile creatures (dementors, boggarts, dragons)
        NEUTRAL,    // Neutral creatures
        AQUATIC,    // Water-dwelling creatures
        SPIRITUAL   // Spiritual or ethereal creatures
    }
}

