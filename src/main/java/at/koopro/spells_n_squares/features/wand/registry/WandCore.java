package at.koopro.spells_n_squares.features.wand.registry;

/**
 * Enum representing wand core types.
 * Each core provides unique properties and affinities.
 */
public enum WandCore {
    PHOENIX_FEATHER("phoenix_feather"),
    DRAGON_HEARTSTRING("dragon_heartstring"),
    UNICORN_HAIR("unicorn_hair");
    
    private final String id;
    
    WandCore(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public static WandCore fromId(String id) {
        for (WandCore core : values()) {
            if (core.id.equals(id)) {
                return core;
            }
        }
        return null;
    }
}

