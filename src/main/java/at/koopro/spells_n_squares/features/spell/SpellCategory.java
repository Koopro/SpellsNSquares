package at.koopro.spells_n_squares.features.spell;

import net.minecraft.resources.Identifier;

/**
 * Categories for organizing spells in the selection screen.
 */
public enum SpellCategory {
    ALL("All", 0xFF888888),
    CORE("Core", 0xFF4A90E2),
    UTILITY("Utility", 0xFF50C878),
    COMBAT("Combat", 0xFFE74C3C),
    DEFENSIVE("Defensive", 0xFF3498DB),
    CHARM("Charm", 0xFF9B59B6),
    HEALING("Healing", 0xFF2ECC71),
    MEMORY("Memory", 0xFFE67E22),
    TRANSFIGURATION("Transfiguration", 0xFFF39C12),
    CURSE("Curse", 0xFF8B0000),
    WEATHER("Weather", 0xFF87CEEB),
    OTHER("Other", 0xFF95A5A6);
    
    private final String displayName;
    private final int color;
    
    SpellCategory(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getColor() {
        return color;
    }
    
    /**
     * Gets the red component of the category color.
     */
    public int getRed() {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * Gets the green component of the category color.
     */
    public int getGreen() {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * Gets the blue component of the category color.
     */
    public int getBlue() {
        return color & 0xFF;
    }
    
    /**
     * Categorizes a spell based on its ID.
     * Uses spell name patterns to determine category.
     */
    public static SpellCategory fromSpellId(Identifier spellId) {
        String path = spellId.getPath().toLowerCase();
        
        // Core spells
        if (path.contains("heal") || path.contains("teleport") || path.contains("fireball") ||
            path.contains("lightning") || path.contains("protego") || path.contains("apparition") ||
            path.contains("lumos")) {
            return CORE;
        }
        
        // Utility spells
        if (path.contains("detection") || path.contains("extension") || path.contains("crafting") ||
            path.contains("recall") || path.contains("nox") || path.contains("revelio") ||
            path.contains("finite") || path.contains("aparecium") || path.contains("prior") ||
            path.contains("alohomora") || path.contains("colloportus") || path.contains("scourgify") ||
            path.contains("tergeo") || path.contains("sonorus")) {
            return UTILITY;
        }
        
        // Weather control
        if (path.contains("metelojinx") || path.contains("arania") || path.contains("ventus")) {
            return WEATHER;
        }
        
        // Defensive spells
        if (path.contains("expelliarmus") || path.contains("stupefy") || path.contains("protego")) {
            return DEFENSIVE;
        }
        
        // Combat spells
        if (path.contains("bombarda") || path.contains("confringo") || path.contains("diffindo") ||
            path.contains("depulso") || path.contains("descendo") || path.contains("flipendo") ||
            path.contains("impedimenta") || path.contains("rictusempra") || path.contains("reducto")) {
            return COMBAT;
        }
        
        // Charm spells
        if (path.contains("reparo") || path.contains("levitation") || path.contains("leviosa") ||
            path.contains("duro") || path.contains("engorgio") || path.contains("reducio") ||
            path.contains("wingardium")) {
            return CHARM;
        }
        
        // Healing spells
        if (path.contains("episkey") || path.contains("vulnera") || path.contains("anapneo") ||
            path.contains("ferula")) {
            return HEALING;
        }
        
        // Memory/Mental spells
        if (path.contains("legilimens") || path.contains("occlumency")) {
            return MEMORY;
        }
        
        // Transfiguration spells
        if (path.contains("transfiguration") || path.contains("serpensortia") || path.contains("avis")) {
            return TRANSFIGURATION;
        }
        
        // Curses (Dark Magic)
        if (path.contains("crucio") || path.contains("avada") || path.contains("kedavra") ||
            path.contains("imperio") || path.contains("sectumsempra")) {
            return CURSE;
        }
        
        // Default to OTHER for unknown spells
        return OTHER;
    }
}

