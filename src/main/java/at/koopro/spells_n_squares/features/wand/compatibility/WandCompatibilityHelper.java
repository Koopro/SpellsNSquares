package at.koopro.spells_n_squares.features.wand.compatibility;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Helper class for calculating wand-spell compatibility.
 * Shows compatibility between wands and spells visually.
 */
public final class WandCompatibilityHelper {
    
    private WandCompatibilityHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Compatibility levels for wand-spell combinations.
     */
    public enum CompatibilityLevel {
        INCOMPATIBLE(0.0f, "Incompatible"),
        POOR(0.3f, "Poor"),
        FAIR(0.6f, "Fair"),
        GOOD(0.8f, "Good"),
        EXCELLENT(1.0f, "Excellent"),
        PERFECT(1.2f, "Perfect");
        
        private final float multiplier;
        private final String displayName;
        
        CompatibilityLevel(float multiplier, String displayName) {
            this.multiplier = multiplier;
            this.displayName = displayName;
        }
        
        public float getMultiplier() {
            return multiplier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Calculates compatibility between a wand and a spell.
     * 
     * @param wand The wand item stack
     * @param spellId The spell ID
     * @return Compatibility level
     */
    public static CompatibilityLevel getCompatibility(ItemStack wand, Identifier spellId) {
        if (wand == null || wand.isEmpty() || spellId == null) {
            return CompatibilityLevel.INCOMPATIBLE;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null) {
            return CompatibilityLevel.INCOMPATIBLE;
        }
        
        WandCore core = wandData.getCore();
        WandWood wood = wandData.getWood();
        Spell spell = SpellRegistry.get(spellId);
        
        if (core == null || wood == null || spell == null) {
            return CompatibilityLevel.INCOMPATIBLE;
        }
        
        // Calculate compatibility based on core and wood properties
        float compatibility = calculateCompatibilityScore(core, wood, spell);
        
        if (compatibility >= 1.0f) {
            return CompatibilityLevel.PERFECT;
        } else if (compatibility >= 0.9f) {
            return CompatibilityLevel.EXCELLENT;
        } else if (compatibility >= 0.7f) {
            return CompatibilityLevel.GOOD;
        } else if (compatibility >= 0.5f) {
            return CompatibilityLevel.FAIR;
        } else if (compatibility >= 0.2f) {
            return CompatibilityLevel.POOR;
        } else {
            return CompatibilityLevel.INCOMPATIBLE;
        }
    }
    
    /**
     * Calculates a compatibility score between 0.0 and 1.2.
     * 
     * @param core The wand core
     * @param wood The wand wood
     * @param spell The spell
     * @return Compatibility score
     */
    private static float calculateCompatibilityScore(WandCore core, WandWood wood, Spell spell) {
        // Base compatibility starts at 0.5
        float score = 0.5f;
        
        // Core compatibility (simplified - in full implementation would check spell types)
        // Different cores favor different spell types
        String coreId = core.getId().toLowerCase();
        String spellName = spell.getName().toString().toLowerCase();
        
        // Basic matching logic
        if (coreId.contains("phoenix") && (spellName.contains("fire") || spellName.contains("flame"))) {
            score += 0.3f;
        } else if (coreId.contains("dragon") && (spellName.contains("fire") || spellName.contains("combat"))) {
            score += 0.3f;
        } else if (coreId.contains("unicorn") && (spellName.contains("heal") || spellName.contains("light"))) {
            score += 0.3f;
        }
        
        // Wood compatibility
        String woodId = wood.getId().toLowerCase();
        if (woodId.contains("elder") || woodId.contains("ancient")) {
            score += 0.2f; // Elder/ancient woods are more versatile
        }
        
        // Attunement bonus
        // Note: Would need to check if wand is attuned, but that requires wand data access
        // For now, assume attuned wands get a bonus
        score += 0.1f;
        
        return Math.min(1.2f, score);
    }
    
    /**
     * Gets the power multiplier for a spell when cast with a specific wand.
     * 
     * @param wand The wand
     * @param spellId The spell ID
     * @return Power multiplier
     */
    public static float getPowerMultiplier(ItemStack wand, Identifier spellId) {
        CompatibilityLevel compatibility = getCompatibility(wand, spellId);
        return compatibility.getMultiplier();
    }
    
    /**
     * Calculates compatibility as a float value between 0.0 and 1.0.
     * Useful for UI displays like progress bars.
     * 
     * @param wand The wand item stack
     * @param spellId The spell ID
     * @return Compatibility value between 0.0 and 1.0
     */
    public static float calculateCompatibility(ItemStack wand, Identifier spellId) {
        CompatibilityLevel level = getCompatibility(wand, spellId);
        if (level == null) {
            return 0.0f;
        }
        // Normalize multiplier to 0.0-1.0 range (multiplier can go up to 1.2 for PERFECT)
        return Math.min(1.0f, level.getMultiplier() / 1.2f);
    }
}

