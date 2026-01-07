package at.koopro.spells_n_squares.features.wand.system;

import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * System for checking wand compatibility with spells and players.
 * Determines how well a wand works with specific spells or players.
 */
public final class WandCompatibilitySystem {
    private WandCompatibilitySystem() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Calculates compatibility between a wand and a spell.
     * Returns a value from 0.0 (incompatible) to 1.0 (perfect match).
     * 
     * @param wand The wand item stack
     * @param spellId The spell identifier
     * @return Compatibility value (0.0 to 1.0)
     */
    public static float getSpellCompatibility(ItemStack wand, Identifier spellId) {
        if (wand == null || wand.isEmpty() || spellId == null) {
            return 0.5f; // Default compatibility
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        WandWood wood = WandDataHelper.getWood(wand);
        
        if (core == null || wood == null) {
            return 0.5f; // Default if wand not fully crafted
        }
        
        // Base compatibility
        float compatibility = 0.5f;
        
        // Attuned wands have better compatibility
        if (WandDataHelper.isAttuned(wand)) {
            compatibility += 0.3f;
        }
        
        // Core-specific compatibility (future: could vary by spell)
        switch (core) {
            case PHOENIX_FEATHER:
                // Phoenix feather: good for fire and combat spells
                if (spellId.getPath().contains("incendio") || 
                    spellId.getPath().contains("confringo") ||
                    spellId.getPath().contains("stupefy")) {
                    compatibility += 0.2f;
                }
                break;
            case DRAGON_HEARTSTRING:
                // Dragon heartstring: good for powerful spells
                if (spellId.getPath().contains("avada") ||
                    spellId.getPath().contains("crucio") ||
                    spellId.getPath().contains("imperio")) {
                    compatibility += 0.2f;
                }
                break;
            case UNICORN_HAIR:
                // Unicorn hair: good for healing and utility spells
                if (spellId.getPath().contains("episkey") ||
                    spellId.getPath().contains("reparo") ||
                    spellId.getPath().contains("lumos")) {
                    compatibility += 0.2f;
                }
                break;
        }
        
        // Clamp to valid range
        return Math.max(0.0f, Math.min(1.0f, compatibility));
    }
    
    /**
     * Checks if a wand is compatible with a spell (compatibility > 0.5).
     * 
     * @param wand The wand item stack
     * @param spellId The spell identifier
     * @return true if the wand is compatible with the spell
     */
    public static boolean isCompatible(ItemStack wand, Identifier spellId) {
        return getSpellCompatibility(wand, spellId) > 0.5f;
    }
    
    /**
     * Gets a compatibility description for display.
     * 
     * @param wand The wand item stack
     * @param spellId The spell identifier
     * @return A description of the compatibility level
     */
    public static String getCompatibilityDescription(ItemStack wand, Identifier spellId) {
        float compatibility = getSpellCompatibility(wand, spellId);
        
        if (compatibility >= 0.8f) {
            return "Excellent Match";
        } else if (compatibility >= 0.6f) {
            return "Good Match";
        } else if (compatibility >= 0.4f) {
            return "Average Match";
        } else {
            return "Poor Match";
        }
    }
}

