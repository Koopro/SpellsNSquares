package at.koopro.spells_n_squares.core.fx.patterns;

/**
 * High-level API for common particle effect patterns.
 * Provides builder-style methods for creating complex particle effects.
 */
public final class SpellFxPatterns {
    private SpellFxPatterns() {
    }
    
    /**
     * Creates a beam pattern builder.
     * Used for lightning beams, spell trails, wand connections.
     */
    public static BeamPattern.Builder beam() {
        return new BeamPattern.Builder();
    }
    
    /**
     * Creates a burst pattern builder.
     * Used for explosions, impacts, spell collisions.
     */
    public static BurstPattern.Builder burst() {
        return new BurstPattern.Builder();
    }
    
    /**
     * Creates an aura pattern builder.
     * Used for continuous auras around entities/items.
     */
    public static AuraPattern.Builder aura() {
        return new AuraPattern.Builder();
    }
    
    /**
     * Creates a shield pattern builder.
     * Used for Protego shield effects, impact rings.
     */
    public static ShieldPattern.Builder shield() {
        return new ShieldPattern.Builder();
    }
    
    /**
     * Creates a teleport pattern builder.
     * Used for Apparition, Portkey, portal effects.
     */
    public static TeleportPattern.Builder teleport() {
        return new TeleportPattern.Builder();
    }
    
    /**
     * Creates a clash pattern builder.
     * Used for spell clash lightning (extends BeamPattern).
     */
    public static ClashPattern.Builder clash() {
        return new ClashPattern.Builder();
    }
}

