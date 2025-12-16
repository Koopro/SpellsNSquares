package at.koopro.spells_n_squares.features.robes;

/**
 * Enum representing the four Hogwarts houses.
 */
public enum House {
    GRYFFINDOR("gryffindor"),
    SLYTHERIN("slytherin"),
    HUFFLEPUFF("hufflepuff"),
    RAVENCLAW("ravenclaw");
    
    private final String id;
    
    House(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public static House fromId(String id) {
        for (House house : values()) {
            if (house.id.equals(id)) {
                return house;
            }
        }
        return null;
    }
}

